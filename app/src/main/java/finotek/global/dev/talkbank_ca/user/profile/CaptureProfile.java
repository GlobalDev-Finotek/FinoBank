package finotek.global.dev.talkbank_ca.user.profile;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.base.mvp.BasePresenter;

/**
 * Created by kwm on 2017. 3. 6..
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CaptureProfile extends BasePresenter<CaptureProfileView> implements TextureView.SurfaceTextureListener {

  static final String[] CAM_PERMISSIONS = {
      Manifest.permission.CAMERA
  };

  private static final int SENSOR_ORIENTATION_DEFAULT_DEGREES = 90;
  private static final int SENSOR_ORIENTATION_INVERSE_DEGREES = 270;
  private static final SparseIntArray DEFAULT_ORIENTATIONS = new SparseIntArray();
  private static final SparseIntArray INVERSE_ORIENTATIONS = new SparseIntArray();

  private static SparseIntArray ORIENTATIONS = new SparseIntArray();

  static {
    ORIENTATIONS.append(Surface.ROTATION_0, 0);
    ORIENTATIONS.append(Surface.ROTATION_90, 90);
    ORIENTATIONS.append(Surface.ROTATION_180, 180);
    ORIENTATIONS.append(Surface.ROTATION_270, 270);
  }

  static {
    DEFAULT_ORIENTATIONS.append(Surface.ROTATION_0, 90);
    DEFAULT_ORIENTATIONS.append(Surface.ROTATION_90, 0);
    DEFAULT_ORIENTATIONS.append(Surface.ROTATION_180, 270);
    DEFAULT_ORIENTATIONS.append(Surface.ROTATION_270, 180);
  }

  static {
    INVERSE_ORIENTATIONS.append(Surface.ROTATION_0, 270);
    INVERSE_ORIENTATIONS.append(Surface.ROTATION_90, 180);
    INVERSE_ORIENTATIONS.append(Surface.ROTATION_180, 90);
    INVERSE_ORIENTATIONS.append(Surface.ROTATION_270, 0);
  }


  @NonNull
  private Activity context;

  private boolean isFront = true;
  private boolean isFlashOn = false;
  private MediaRecorder mediaRecorder;
  private boolean isRecording;
  private String videoAbsolutePath;

  private float aspectRatio = 0.0f;

  private Integer sensorOrientation;

  private CameraDevice cameraDevice;

  private SurfaceTexture preview;
  private CaptureRequest.Builder previewBuilder;
  private Surface recorderSurface;

  /**
   * The {@link Size} of camera preview.
   */
  private Size previewSize;

  /**
   * The {@link Size} of video recording.
   */
  private Size videoSize;

  /**
   * A reference to the current {@link CameraCaptureSession} for
   * preview.
   */
  private CameraCaptureSession previewSession;

  /**
   * A {@link Semaphore} to prevent the app from exiting before closing the camera.
   */
  private Semaphore cameraOpenCloseLock = new Semaphore(1);

  /**
   * {@link CameraDevice.StateCallback} is called when {@link CameraDevice} changes its status.
   */
  private CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {

    public CameraDevice cameraDevice;

    @Override
    public void onOpened(CameraDevice cameraDevice) {
      this.cameraDevice = cameraDevice;
      startPreview();
      cameraOpenCloseLock.release();
    }

    @Override
    public void onDisconnected(CameraDevice cameraDevice) {
      cameraOpenCloseLock.release();
    }

    @Override
    public void onError(CameraDevice cameraDevice, int error) {
      cameraOpenCloseLock.release();
    }
  };
  private int previewWidth = 0;
  private int previewHeight = 0;

  CaptureProfile(@NonNull Activity context) {
    this.context = context;
    isFront = true;

    WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    DisplayMetrics metrics = new DisplayMetrics();
    wm.getDefaultDisplay().getRealMetrics(metrics);
    aspectRatio = ((float) metrics.heightPixels / (float) metrics.widthPixels);
  }

  private static int sensorToDeviceRotation(CameraCharacteristics cameraCharacteristics, int deviceOrientation) {
    int sensorOrientation = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
    deviceOrientation = ORIENTATIONS.get(deviceOrientation);
    return (sensorOrientation + deviceOrientation + 360) % 360;
  }

  /**
   * Given {@code choices} of {@code Size}s supported by a camera, chooses the smallest one whose
   * width and height are at least as large as the respective requested values, and whose aspect
   * ratio matches with the specified value.
   *
   * @param choices     The list of sizes that the camera supports for the intended output class
   * @param width       The minimum desired width
   * @param height      The minimum desired height
   * @param aspectRatio The aspect ratio
   * @return The optimal {@code Size}, or an arbitrary one if none were big enough
   */
  private static Size chooseOptimalSize2(Size[] choices, int width, int height) {
    List<Size> bigEnough = new ArrayList<>();
    for (Size option : choices) {
      if (option.getHeight() == option.getWidth() * height / width &&
          option.getWidth() >= width && option.getHeight() >= height) {
        bigEnough.add(option);
      }
    }
    if (bigEnough.size() > 0) {
      return Collections.min(bigEnough, new CompareSizeByArea());
    } else {
      return choices[0];
    }
  }

  void releaseMediaRecorder() {
  }

  void releaseCamera() {

    try {
      cameraOpenCloseLock.acquire();
      closePreviewSession();
      if (null != cameraDevice) {
        cameraDevice.close();
        cameraDevice = null;
      }
      if (null != mediaRecorder) {
        mediaRecorder.release();
        mediaRecorder = null;
      }
    } catch (InterruptedException e) {
      throw new RuntimeException("Interrupted while trying to lock camera closing.");
    } finally {
      cameraOpenCloseLock.release();
    }
  }

  void toggleCamera() throws IOException {
    int currentCameraId;
    isFront = !isFront;
    if (isFront) {
      currentCameraId = CameraCharacteristics.LENS_FACING_FRONT;
    } else {
      currentCameraId = CameraCharacteristics.LENS_FACING_BACK;
    }

    closeCamera();
    closePreviewSession();

    try {
      openCamera(previewWidth, previewHeight, currentCameraId);
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (CameraAccessException e) {
      e.printStackTrace();
    }
  }

  void toggleFlashMode() throws IllegalStateException {
    if (isFlashOn) {
      isFlashOn = false;
      previewBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_OFF);
    } else {
      isFlashOn = true;
      previewBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_TORCH);
    }

    try {
      previewSession.setRepeatingRequest(previewBuilder.build(), null, null);
    } catch (CameraAccessException e) {
      e.printStackTrace();
    }

    getMvpView().setFlashButtonIcon(isFlashOn);
  }

  void startRecording() {

    try {
      startRecordingVideo();

    } catch (IOException | IllegalStateException | CameraAccessException e) {
      // TODO : 에러 상황에 대한 View 처리를 반드시 구현한다.
      e.printStackTrace();
    }
  }

  void stopRecording() {

  }

  @Override
  public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {

    previewWidth = width;
    previewHeight = height;
    preview = surfaceTexture;

    try {
      openCamera(width, height, CameraCharacteristics.LENS_FACING_FRONT);
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (CameraAccessException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {

  }

  @Override
  public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
    closePreviewSession();
    closeCamera();
    preview = null;
    return true;
  }

  @Override
  public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
  }

  private boolean hasPermissionsGranted(String[] permissions) {
    for (String permission : permissions) {
      if (ActivityCompat.checkSelfPermission(context, permission)
          != PackageManager.PERMISSION_GRANTED) {
        return false;
      }
    }
    return true;
  }

  private void configureTransform(int viewWidth, int viewHeight) {

    if (previewSize == null) {
      return;
    }
    int rotation = context.getWindowManager().getDefaultDisplay().getRotation();
    Matrix matrix = new Matrix();
    RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
    RectF bufferRect = new RectF(0, 0, previewSize.getHeight(), previewSize.getWidth());
    float centerX = viewRect.centerX();
    float centerY = viewRect.centerY();
    if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
      bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
      matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
      float scale = Math.max(
          (float) viewHeight / previewSize.getHeight(),
          (float) viewWidth / previewSize.getWidth());
      matrix.postScale(scale, scale, centerX, centerY);
      matrix.postRotate(90 * (rotation - 2), centerX, centerY);
    }
    getMvpView().setTransform(matrix);
  }

  /**
   * Tries to open a {@link CameraDevice}. The result is listened by `mStateCallback`.
   */
  private void openCamera(int width, int height, int camId) throws InterruptedException, CameraAccessException {

    if (!hasPermissionsGranted(CAM_PERMISSIONS)) {
      getMvpView().requestVideoPermissions();
    } else {

      CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);

      if (!cameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
        throw new RuntimeException("Time out waiting to lock camera opening.");
      }

      ArrayList<String> camList = new ArrayList<>(Arrays.asList(manager.getCameraIdList()));
      String cameraId = camList.get(camId);

      CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
      StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

      int deviceOrientation = context.getWindowManager().getDefaultDisplay().getRotation();
      int mTotalRotation = sensorToDeviceRotation(characteristics, deviceOrientation);
      boolean swapRotation = mTotalRotation == 90 || mTotalRotation == 270;
      int rotatedWidth = width;
      int rotatedHeight = height;
      if (swapRotation) {
        rotatedWidth = height;
        rotatedHeight = width;
      }

      sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
      videoSize = chooseOptimalSize2(map.getOutputSizes(MediaRecorder.class), rotatedWidth, rotatedHeight);
      previewSize = videoSize;

      int orientation = context.getResources().getConfiguration().orientation;
      if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
        // mTextureView.setAspectRatio(mPreviewSize.getWidth(), mPreviewSize.getHeight());
      } else {
        // mTextureView.setAspectRatio(mPreviewSize.getHeight(), mPreviewSize.getWidth());
      }

      configureTransform(width, height);
      mediaRecorder = new MediaRecorder();
      if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
        return;
      }
      manager.openCamera(cameraId, stateCallback, null);
    }
  }

  private void closeCamera() {
    try {
      cameraOpenCloseLock.acquire();
      closePreviewSession();
      if (null != cameraDevice) {
        cameraDevice.close();
        cameraDevice = null;
      }
      if (null != mediaRecorder) {
        mediaRecorder.release();
        mediaRecorder = null;
      }
    } catch (InterruptedException e) {
      throw new RuntimeException("Interrupted while trying to lock camera closing.");
    } finally {
      cameraOpenCloseLock.release();
    }
  }

  private void closePreviewSession() {
    if (previewSession != null) {
      previewSession.close();
      previewSession = null;
    }
  }

  /**
   * Start the camera preview.
   */
  private void startPreview() {
//    if (null == cameraDevice || !mTextureView.isAvailable() || null == mPreviewSize) {
    if (null == cameraDevice || null == previewSize) {
      return;
    }
    try {
      closePreviewSession();
//      SurfaceTexture texture = mTextureView.getSurfaceTexture();
      assert preview != null;
      preview.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
      previewBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

      Surface previewSurface = new Surface(preview);
      previewBuilder.addTarget(previewSurface);

      cameraDevice.createCaptureSession(Collections.singletonList(previewSurface), new CameraCaptureSession.StateCallback() {

        @Override
        public void onConfigured(CameraCaptureSession cameraCaptureSession) {
          previewSession = cameraCaptureSession;
          updatePreview();
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
          if (null != context) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
          }
        }
      }, null);
    } catch (CameraAccessException e) {
      e.printStackTrace();
    }
  }

  private void updatePreview() {

    if (null == cameraDevice) {
      return;
    }
    try {
      setUpCaptureRequestBuilder(previewBuilder);
      HandlerThread thread = new HandlerThread("CameraPreview");
      thread.start();
      previewSession.setRepeatingRequest(previewBuilder.build(), null, null);
    } catch (CameraAccessException e) {
      e.printStackTrace();
    }
  }

  private void setUpCaptureRequestBuilder(CaptureRequest.Builder builder) {
    builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
  }

  private void startRecordingVideo() throws IOException, CameraAccessException {
    if (cameraDevice == null || previewSize == null) {
      return;
    }

    videoAbsolutePath = getVideoFilePath(context);
    closePreviewSession();
    prepareRecorder(videoAbsolutePath);

    previewBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
    List<Surface> surfaces = new ArrayList<>();

    // Set up Surface for the camera preview
    Surface previewSurface = getMvpView().getRecordingSurface(previewSize.getWidth(), previewSize.getHeight());
    surfaces.add(previewSurface);
    previewBuilder.addTarget(previewSurface);

    // Set up Surface for the MediaRecorder
    recorderSurface = mediaRecorder.getSurface();
    surfaces.add(recorderSurface);
    previewBuilder.addTarget(recorderSurface);

    // Start a capture session
    // Once the session starts, we can update the UI and start recording
    cameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {

      @Override
      public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
        previewSession = cameraCaptureSession;
        updatePreview();
        context.runOnUiThread(() -> {

          // UI
          isRecording = true;

          if (mediaRecorder != null) {
            // Start recording
            mediaRecorder.start();
          }
        });
      }

      @Override
      public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
        Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
      }
    }, null);
  }

  private void prepareRecorder(String videoFilePath) throws IOException {
    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
    mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
    mediaRecorder.setOutputFile(videoFilePath);
    mediaRecorder.setVideoEncodingBitRate(4000000);
    mediaRecorder.setVideoFrameRate(25);
    mediaRecorder.setVideoSize(videoSize.getWidth(), videoSize.getHeight());
    mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
    int rotation = context.getWindowManager().getDefaultDisplay().getRotation();
    switch (sensorOrientation) {
      case SENSOR_ORIENTATION_DEFAULT_DEGREES:
        mediaRecorder.setOrientationHint(DEFAULT_ORIENTATIONS.get(rotation));
        break;
      case SENSOR_ORIENTATION_INVERSE_DEGREES:
        mediaRecorder.setOrientationHint(INVERSE_ORIENTATIONS.get(rotation));
        break;
    }
    mediaRecorder.prepare();
  }

  private String getVideoFilePath(Context context) {
    return context.getExternalFilesDir(null).getAbsolutePath() + "/"
        + System.currentTimeMillis() + ".mp4";
  }

  private static class CompareSizeByArea implements Comparator<Size> {

    @Override
    public int compare(Size lhs, Size rhs) {
      return Long.signum((long) (lhs.getWidth() * lhs.getHeight()) -
          (long) (rhs.getWidth() * rhs.getHeight()));
    }
  }
}

package finotek.global.dev.talkbank_ca.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;

import finotek.global.dev.talkbank_ca.R;

/**
 * Created by magyeong-ug on 21/03/2017.
 */

public class DrawingCanvas extends View {

	private Path drawPath;
	private Paint drawPaint, canvasPaint;
	private Canvas drawCanvas;
	private Bitmap canvasBitmap;
	private Context context;
	private OnCanvasTouchListener onCanvasTouchListener;

	public DrawingCanvas(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	private void init() {
		drawPath = new Path();
		drawPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		this.setDrawingCacheEnabled(true);

		drawPaint.setColor(ContextCompat.getColor(context, R.color.black));
		drawPaint.setAntiAlias(true);
		drawPaint.setStrokeWidth(10);
		drawPaint.setStyle(Paint.Style.STROKE);
		drawPaint.setStrokeJoin(Paint.Join.ROUND);
		drawPaint.setStrokeCap(Paint.Cap.ROUND);
		drawPaint.setPathEffect(new CornerPathEffect(40));   // set the path effect when they join.
		drawPaint.setDither(true);

		canvasPaint = new Paint(Paint.DITHER_FLAG);
	}

	public void setOnCanvasTouchListener(OnCanvasTouchListener onCanvasTouchListener) {
		this.onCanvasTouchListener = onCanvasTouchListener;
	}

	public void save() {
		String path = context.getExternalFilesDir(null) + "/mySign.png";
		Log.d("BNP-APP", "signature is saved.");

//		this.buildDrawingCache();
//		Bitmap b = Bitmap.createBitmap(this.getDrawingCache());
//		this.setDrawingCacheEnabled(false);

		Log.d("BNP-APP", "Measured Width: " + this.getMeasuredWidth() + ", Measured Height: " + getMeasuredHeight());

		Bitmap b = Bitmap.createBitmap(this.getMeasuredWidth(), this.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(b);
		this.draw(canvas);

		if (b != null) {
			try {
				File f = new File(path);
				FileOutputStream fos = new FileOutputStream(f);

				b.compress(Bitmap.CompressFormat.PNG, 100, fos);
				fos.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		drawCanvas = new Canvas(canvasBitmap);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float touchX = event.getX();
		float touchY = event.getY();

		if (onCanvasTouchListener == null) {
			throw new NullPointerException("canvas touch listener null");
		}

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				onCanvasTouchListener.onTouchStart();
				drawPath.moveTo(touchX, touchY);
				break;
			case MotionEvent.ACTION_MOVE:
				drawPath.lineTo(touchX, touchY);
				break;
			case MotionEvent.ACTION_UP:
				drawCanvas.drawPath(drawPath, drawPaint);
				drawPath.reset();
				break;
			default:
				return false;
		}

		invalidate();
		return true;
	}

	public void clear() {
		drawCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
		canvas.drawPath(drawPath, drawPaint);
	}

	public interface OnCanvasTouchListener {
		void onTouchStart();
	}
}

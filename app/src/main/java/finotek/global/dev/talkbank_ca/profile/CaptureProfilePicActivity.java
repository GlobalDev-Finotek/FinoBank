package finotek.global.dev.talkbank_ca.profile;

import android.app.FragmentTransaction;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;

import com.jakewharton.rxbinding.view.RxView;

import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.databinding.ActivityCaptureProfilePicBinding;

public class CaptureProfilePicActivity extends AppCompatActivity {

  private ActivityCaptureProfilePicBinding binding;
  private CaptureProfile presenter;
  private static final int REQUEST_VIDEO_PERMISSIONS = 1;

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.activity_capture_profile_pic);

    binding.tvInstruction.setText("");

    CapturePicFragment capturePicFragment = new CapturePicFragment();

    FragmentTransaction transaction = getFragmentManager().beginTransaction();
    transaction.add(R.id.fl_pic, capturePicFragment);
    transaction.commit();

    RxView.clicks(binding.btnTakePic)
        .throttleFirst(300, TimeUnit.MILLISECONDS)
        .subscribe(aVoid -> {

        });

  }


}

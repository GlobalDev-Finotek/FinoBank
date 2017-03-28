package finotek.global.dev.talkbank_ca.user.profile;

import android.app.FragmentTransaction;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import com.jakewharton.rxbinding.view.RxView;

import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.databinding.ActivityCaptureProfilePicBinding;
import finotek.global.dev.talkbank_ca.user.CapturePicFragment;

public class CaptureProfilePicActivity extends AppCompatActivity {

	private static final int REQUEST_VIDEO_PERMISSIONS = 1;
	private ActivityCaptureProfilePicBinding binding;
	private CaptureProfile presenter;
	private boolean isCaptureDone;

	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityCaptureProfilePicBinding binding = DataBindingUtil.setContentView(this,
				R.layout.activity_capture_profile_pic);

		CapturePicFragment capturePicFragment = new CapturePicFragment();

		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.add(R.id.fl_cam, capturePicFragment);
		transaction.commit();

	}

}

package finotek.global.dev.talkbank_ca.user.profile;

import android.app.FragmentTransaction;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.databinding.ActivityCaptureProfilePicBinding;
import finotek.global.dev.talkbank_ca.user.CapturePicFragment;

public class CaptureProfilePicActivity extends AppCompatActivity {

	private ActivityCaptureProfilePicBinding binding;
	private CapturePicFragment capturePicFragment = new CapturePicFragment();
	private boolean isCaptureDone;

	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this,
				R.layout.activity_capture_profile_pic);
		setSupportActionBar(binding.toolbar);
		getSupportActionBar().setTitle("");
		// binding.toolbar.setTitle(getString(R.string.registration_string_profile_register));
		binding.appbar.setOutlineProvider(null);
		binding.ibBack.setOnClickListener(v -> onBackPressed());

		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.add(R.id.fl_cam, capturePicFragment);
		transaction.commit();


//		capturePicFragment.setOnSizeChangeListener(new CapturePicFragment.OnSizeChangeListener() {
//			@Override
//			public void onSizeFull() {
//				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//						LinearLayout.LayoutParams.MATCH_PARENT);
//				binding.flCam.setLayoutParams(lp);
//				binding.appbar.setVisibility(View.GONE);
//			}
//
//			@Override
//			public void onSizeMinimize() {
//				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//						Converter.dpToPx(350));
//				binding.flCam.setLayoutParams(lp);
//				binding.appbar.setVisibility(View.VISIBLE);
//			}
//
//		});

		capturePicFragment.takePicture(path -> {

			if (isCaptureDone) {
				finish();
			} else {
				binding.tvInst.setText(getString(R.string.registration_string_picture_check));
			}

			isCaptureDone = !isCaptureDone;
		});

	}

}

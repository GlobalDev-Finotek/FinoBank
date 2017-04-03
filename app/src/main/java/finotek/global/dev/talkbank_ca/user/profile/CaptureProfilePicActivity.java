package finotek.global.dev.talkbank_ca.user.profile;

import android.app.FragmentTransaction;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.databinding.ActivityCaptureProfilePicBinding;
import finotek.global.dev.talkbank_ca.user.CapturePicFragment;
import finotek.global.dev.talkbank_ca.util.Converter;

public class CaptureProfilePicActivity extends AppCompatActivity {

	private ActivityCaptureProfilePicBinding binding;

	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityCaptureProfilePicBinding binding = DataBindingUtil.setContentView(this,
				R.layout.activity_capture_profile_pic);
		setSupportActionBar(binding.toolbar);
		getSupportActionBar().setTitle("");
		binding.toolbar.setTitle("인증 사진");
		binding.appbar.setOutlineProvider(null);
		binding.ibBack.setOnClickListener(v -> onBackPressed());

		CapturePicFragment capturePicFragment = new CapturePicFragment();

		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.add(R.id.fl_cam, capturePicFragment);
		transaction.commit();

		capturePicFragment.setOnSizeChangeListener(new CapturePicFragment.OnSizeChangeListener() {
			@Override
			public void onSizeFull() {
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.MATCH_PARENT);
				binding.flCam.setLayoutParams(lp);
			}

			@Override
			public void onSizeMinimize() {
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
						Converter.dpToPx(350));
				binding.flCam.setLayoutParams(lp);
			}

		});

	}

}

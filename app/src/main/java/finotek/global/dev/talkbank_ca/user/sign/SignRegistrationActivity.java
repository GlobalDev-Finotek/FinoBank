package finotek.global.dev.talkbank_ca.user.sign;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.ChatActivity;
import finotek.global.dev.talkbank_ca.databinding.ActivitySignRegistartionBinding;


public class SignRegistrationActivity extends AppCompatActivity {

  private ActivitySignRegistartionBinding binding;


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_registartion);
	  setSupportActionBar(binding.toolbar);
	  getSupportActionBar().setTitle("");
	  binding.toolbarTitle.setText("자필 서명");
	  binding.appbar.setOutlineProvider(null);
	  binding.ibBack.setOnClickListener(v -> onBackPressed());

	  SignRegistFragment signRegistFragment = new SignRegistFragment();
	  FragmentTransaction transaction = getFragmentManager().beginTransaction();
	  transaction.add(R.id.fl_content, signRegistFragment);
	  transaction.commit();

	  signRegistFragment.setOnSizeControlClick(size -> {
		  if (size == SignRegistFragment.CanvasSize.LARGE) {
			  LinearLayout.LayoutParams fl = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
					  LinearLayout.LayoutParams.MATCH_PARENT);
			  binding.flContent.setLayoutParams(fl);
			  binding.llInstWrapper.setVisibility(View.GONE);
			  binding.appbar.setVisibility(View.GONE);
		  } else if (size == SignRegistFragment.CanvasSize.SMALL) {
			  LinearLayout.LayoutParams fl = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
					  (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 350, getResources().getDisplayMetrics()));
				binding.llInstWrapper.setVisibility(View.VISIBLE);
			  binding.flContent.setLayoutParams(fl);
			  binding.appbar.setVisibility(View.VISIBLE);
		  }
    });

	  signRegistFragment.setOnSaveListener(() -> {
		  Intent intent = new Intent(SignRegistrationActivity.this, ChatActivity.class);
		  startActivity(intent);
		  finish();
	  });
  }

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		int orientation = newConfig.orientation;

		switch(orientation) {

			case Configuration.ORIENTATION_LANDSCAPE:
				binding.appbar.setVisibility(View.GONE);
				break;

			case Configuration.ORIENTATION_PORTRAIT:
				binding.appbar.setVisibility(View.VISIBLE);
				break;

		}
	}
}
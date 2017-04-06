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
import finotek.global.dev.talkbank_ca.databinding.ActivitySignRegistartionBinding;
import finotek.global.dev.talkbank_ca.user.dialogs.SucceededDialog;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;


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

	  OneStepSignRegisterFragment signRegistFragment = new OneStepSignRegisterFragment();
	  FragmentTransaction transaction = getFragmentManager().beginTransaction();
	  transaction.add(R.id.fl_content, signRegistFragment);
	  transaction.commit();

	  signRegistFragment.setOnSizeControlClick(size -> {
		  if (size == BaseSignRegisterFragment.CanvasSize.LARGE) {
			  LinearLayout.LayoutParams fl = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
					  LinearLayout.LayoutParams.MATCH_PARENT);
			  binding.flContent.setLayoutParams(fl);
			  binding.llInstWrapper.setVisibility(View.GONE);
			  binding.appbar.setVisibility(View.GONE);
		  } else if (size == BaseSignRegisterFragment.CanvasSize.SMALL) {
			  LinearLayout.LayoutParams fl = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
					  (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 350, getResources().getDisplayMetrics()));
				binding.llInstWrapper.setVisibility(View.VISIBLE);
			  binding.flContent.setLayoutParams(fl);
			  binding.appbar.setVisibility(View.VISIBLE);
		  }
    });

	  Intent intent = getIntent();

	  try {
		  Class nextClass = (Class) intent.getExtras().get("nextClass");
		  signRegistFragment.setOnSaveListener(() -> {

			  SucceededDialog dialog = new SucceededDialog(SignRegistrationActivity.this);

			  dialog.setTitle(getString(R.string.setting_string_signature_verified));
			  dialog.setDescription(getString(R.string.setting_string_authentication_complete));
			  dialog.setButtonText(getString(R.string.setting_string_yes));
			  dialog.setDoneListener(() -> {
				  Intent intent2 = new Intent(SignRegistrationActivity.this, nextClass);
				  intent2.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
				  intent2.putExtras(intent.getExtras());
				  startActivity(intent2);
				  finish();
			  });

			  dialog.show();
		  });
	  } catch (NullPointerException e) {
		  e.printStackTrace();
	  }
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
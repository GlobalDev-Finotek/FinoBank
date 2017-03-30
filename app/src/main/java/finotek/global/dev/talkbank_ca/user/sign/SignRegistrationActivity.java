package finotek.global.dev.talkbank_ca.user.sign;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

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
	  binding.toolbarTitle.setText("자필 서명");

	  SignRegistFragment signRegistFragment = new SignRegistFragment();
	  FragmentTransaction transaction = getFragmentManager().beginTransaction();
	  transaction.add(R.id.fl_content, signRegistFragment);
	  transaction.commit();

	  signRegistFragment.setOnSizeControlClick(size -> {
		  if (size == SignRegistFragment.CanvasSize.LARGE) {
			  RelativeLayout.LayoutParams fl = new RelativeLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
					  RelativeLayout.LayoutParams.MATCH_PARENT);
			  binding.flContent.setLayoutParams(fl);
		  } else if (size == SignRegistFragment.CanvasSize.SMALL) {
			  RelativeLayout.LayoutParams fl = new RelativeLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
					  (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 350, getResources().getDisplayMetrics()));

			  binding.flContent.setLayoutParams(fl);
		  }
    });

	  signRegistFragment.setOnSaveListener(new SignRegistFragment.OnSignSaveListener() {
		  @Override
		  public void onSave() {
			  Intent intent = new Intent(SignRegistrationActivity.this, ChatActivity.class);
			  startActivity(intent);
			  finish();
		  }
	  });
  }


}
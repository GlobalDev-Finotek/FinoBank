package finotek.global.dev.talkbank_ca.user.sign;

import android.app.FragmentTransaction;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Menu;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.databinding.ActivitySignRegistartionBinding;


public class SignRegistrationActivity extends AppCompatActivity {

  private ActivitySignRegistartionBinding binding;


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_registartion);

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
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.user_regi_menu, menu);
    return true;
  }

}
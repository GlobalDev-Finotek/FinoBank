package finotek.global.dev.talkbank_ca;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.jakewharton.rxbinding.view.RxView;

import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.chat.ChatActivity;
import finotek.global.dev.talkbank_ca.databinding.ActivitySplashBinding;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashActivity extends AppCompatActivity {
  private ActivitySplashBinding binding;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
    binding.tvIntro.setText(R.string.fintek_intro);


    if (true) {
      binding.btnNext.setText(R.string.string_user_regi);
    } else {
      binding.btnNext.setText(R.string.string_login);
    }


    RxView.clicks(binding.btnNext)
        .throttleFirst(200, TimeUnit.MILLISECONDS)
        .subscribe(aVoid -> {

          Intent intent;

          if (true) {
            intent = new Intent(SplashActivity.this, ChatActivity.class);
          } else {
            intent = new Intent(SplashActivity.this, ChatActivity.class);
          }

          startActivity(intent);
        });
  }
}

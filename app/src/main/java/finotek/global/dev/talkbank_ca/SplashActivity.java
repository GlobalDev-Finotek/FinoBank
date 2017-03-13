package finotek.global.dev.talkbank_ca;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.jakewharton.rxbinding.view.RxView;

import java.util.concurrent.TimeUnit;

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

    ValueAnimator colorAnimation = getColorAnimator(
        ContextCompat.getColor(this, R.color.colorPrimary),
        ContextCompat.getColor(this, R.color.color_foreground),
        valueAnimator ->    binding.wrapperSplash
            .setBackgroundColor((int) valueAnimator.getAnimatedValue()));
    colorAnimation.addListener(new Animator.AnimatorListener() {
      @Override
      public void onAnimationStart(Animator animation) {

      }

      @Override
      public void onAnimationEnd(Animator animation) {
        binding.btnNext.setVisibility(View.VISIBLE);
      }

      @Override
      public void onAnimationCancel(Animator animation) {

      }

      @Override
      public void onAnimationRepeat(Animator animation) {

      }
    });
    colorAnimation.start();

    ValueAnimator textColorAnim = getColorAnimator(
        ContextCompat.getColor(this, R.color.color_foreground),
        ContextCompat.getColor(this, R.color.black),
    valueAnimator -> binding.tvIntro
        .setTextColor((Integer) valueAnimator.getAnimatedValue()));

    textColorAnim.start();


    Animation fadeOutAnim = AnimationUtils.loadAnimation(this, R.anim.fadout);
    fadeOutAnim.setAnimationListener(new Animation.AnimationListener() {
      @Override
      public void onAnimationStart(Animation animation) {}

      @Override
      public void onAnimationEnd(Animation animation) {
        Animation fadeInAnim = AnimationUtils.loadAnimation(SplashActivity.this,
            R.anim.fadein);
        fadeInAnim.setAnimationListener(new Animation.AnimationListener() {
          @Override
          public void onAnimationStart(Animation animation) {
            binding.ivFinotekLogo.setImageResource(R.drawable.ic_finotek_logo2);
          }
          @Override
          public void onAnimationEnd(Animation animation) {}
          @Override
          public void onAnimationRepeat(Animation animation) {}
        });
        binding.ivFinotekLogo.startAnimation(fadeInAnim);
      }

      @Override
      public void onAnimationRepeat(Animation animation) {}
    });

    binding.ivFinotekLogo.startAnimation(fadeOutAnim);


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
            intent = new Intent(SplashActivity.this, UserRegistrationActivity.class);
          } else {
            intent = new Intent(SplashActivity.this, UserRegistrationActivity.class);
          }

          startActivity(intent);
        });
  }

  private ValueAnimator getColorAnimator(int fromColor, int toColor, ValueAnimator.AnimatorUpdateListener updateListener) {

    ValueAnimator textColorAnim = ValueAnimator.ofObject(new ArgbEvaluator(), fromColor, toColor);
    textColorAnim.setDuration(1500); // milliseconds
    textColorAnim.addUpdateListener(updateListener);

    return textColorAnim;
  }
}

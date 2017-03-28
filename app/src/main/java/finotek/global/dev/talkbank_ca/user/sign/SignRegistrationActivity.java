package finotek.global.dev.talkbank_ca.user.sign;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.databinding.ActivitySignRegistartionBinding;
import finotek.global.dev.talkbank_ca.widget.DrawingCanvas;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;


public class SignRegistrationActivity extends AppCompatActivity {

  private ActivitySignRegistartionBinding binding;
  private int stepCount;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_registartion);

    Observable.just(stepCount)
        .subscribe(new Action1<Integer>() {
          @Override
          public void call(Integer stepCount) {

            switch (stepCount) {

            }

          }
        });

    binding.drawingCanvas.setOnCanvasTouchListener(new DrawingCanvas.OnCanvasTouchListener() {
      @Override
      public void onTouchStart() {

      }

      @Override
      public void onTouchEnd() {

      }
    });




  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.user_regi_menu, menu);
    return true;
  }

}
package finotek.global.dev.talkbank_ca;

import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;

import finotek.global.dev.talkbank_ca.databinding.ActivitySignRegistartionBinding;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.PublishSubject;


public class SignRegistrationActivity extends AppCompatActivity {

  private ActivitySignRegistartionBinding binding;
  private int touchCount;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_registartion);

    RxView.clicks(binding.btnRegistration)
        .subscribe(integer -> binding.btnRegistration.setText(R.string.string_next));


  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.user_regi_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.action_settings) {
      Toast.makeText(getApplicationContext(), "Settings", Toast.LENGTH_SHORT).show();
      return true;
    } else if (id == R.id.action_search) {
      Toast.makeText(getApplicationContext(), "Search", Toast.LENGTH_SHORT).show();
      return true;
    } else if (id == R.id.action_edit) {
      Toast.makeText(getApplicationContext(), "Edit", Toast.LENGTH_SHORT).show();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }




}
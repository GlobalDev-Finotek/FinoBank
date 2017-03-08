package finotek.global.dev.talkbank_ca;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class SignRegistrationActivity extends AppCompatActivity {



  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

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
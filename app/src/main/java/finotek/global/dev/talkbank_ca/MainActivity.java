package finotek.global.dev.talkbank_ca;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jakewharton.rxbinding.view.RxView;

import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.chat.ChatActivity;
import finotek.global.dev.talkbank_ca.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        RxView.clicks(binding.mainButton)
            .throttleFirst(200, TimeUnit.MILLISECONDS)
            .subscribe(aVoid -> {
                Intent intent;
                intent = new Intent(MainActivity.this, ChatActivity.class);
                startActivity(intent);
            });
    }
}

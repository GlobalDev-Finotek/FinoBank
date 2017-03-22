package finotek.global.dev.talkbank_ca.user;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.databinding.ActivityPinRegistrationBinding;
import finotek.global.dev.talkbank_ca.model.Pin;
import finotek.global.dev.talkbank_ca.setting.SettingsActivity;
import finotek.global.dev.talkbank_ca.widget.SecureKeyboardAdapter;

public class PinRegistrationActivity extends AppCompatActivity {

	private final int PINCODE_LENGTH = 6;
	ActivityPinRegistrationBinding binding;
	private TextView[] tvPwd = new TextView[PINCODE_LENGTH];


	private int ptrTvPwd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_pin_registration);
		setSupportActionBar(binding.toolbar);
		getSupportActionBar().setTitle("PIN 코드");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		for (Pin.Color c : Pin.Color.values()) {
			setPinBackgroundCircle(c);
			setTextColorCircle(c);
		}

		binding.glPinCode.setUseDefaultMargins(false);
		binding.glPinCode.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
		binding.glPinCode.setRowOrderPreserved(false);

		for (int i = 0; i < PINCODE_LENGTH; ++i) {

			TextView tv = new TextView(this);
			tv.setPadding(60, 20, 60, 20);
			tv.setTextSize(40f);
			tv.setText(" ");
			tv.setBackground(ContextCompat.getDrawable(this, R.drawable.border_black_blank));
			tv.setOnClickListener(v ->  {
				Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
						R.anim.slide_up);

				binding.llSecureKeyboard.setVisibility(View.VISIBLE);
				// Start animation
				binding.llSecureKeyboard.startAnimation(slide_up);

			});

			tvPwd[i] = tv;

			binding.glPinCode.addView(tv);
		}

		SecureKeyboardAdapter secureKeyboardAdapter = new SecureKeyboardAdapter(this, getCompleteRandomizedSeq());
		binding.gvKeypad.setAdapter(secureKeyboardAdapter);
		binding.gvKeypad.setOnItemClickListener((parent, view, position, id) -> {
			String key = (String) secureKeyboardAdapter.getItem(position);

			if (ptrTvPwd < PINCODE_LENGTH && !TextUtils.isEmpty(key.trim())) {
				tvPwd[ptrTvPwd++].setText("*");
			}

			if (position == secureKeyboardAdapter.getCount() - 1) {
				startActivity(new Intent(PinRegistrationActivity.this, SettingsActivity.class));
			}

		});

		secureKeyboardAdapter.setOnBackPressListener(() -> {
			if (ptrTvPwd > 0) {
				tvPwd[--ptrTvPwd].setText(" ");
			}
		});


	}

	private void setTextColorCircle(Pin.Color c) {
		ImageView iv2 = new ImageView(this);
		iv2.setTag(ContextCompat.getColor(this, c.getColor()));
		iv2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.circle));
		DrawableCompat.setTint(iv2.getDrawable(), ContextCompat.getColor(this, c.getColor()));
		binding.glPinTextColor.addView(iv2);

		iv2.setOnClickListener(v -> {
			for (TextView tv : tvPwd) {
				tv.setTextColor((int) iv2.getTag());
			}
		});
	}

	private void setPinBackgroundCircle(Pin.Color c) {
		ImageView iv = new ImageView(this);
		iv.setTag(ContextCompat.getColor(this, c.getColor()));
		iv.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.circle));
		DrawableCompat.setTint(iv.getDrawable(), ContextCompat.getColor(this, c.getColor()));
		binding.glPinBackground.addView(iv);
		iv.setOnClickListener(v -> binding.glPinCode.setBackgroundColor((int) iv.getTag()));
	}

	private List<String> getCompleteRandomizedSeq() {
		ArrayList<String> completeSets = new ArrayList<>();

		for (int i = 0; i < 10; ++i) {
			completeSets.add(String.valueOf(i));
		}

		Collections.shuffle(completeSets);

		completeSets.add(7, " ");
		completeSets.add(" ");

		return completeSets;
	}

	private View crateColoredView(Pin.Color color) {
		TextView coloredWidget = new TextView(this);
		coloredWidget.setBackgroundColor(ContextCompat.getColor(this, color.getColor()));
		coloredWidget.setText("111");
		coloredWidget.setTextSize(15f);
		coloredWidget.setTag(color.getColor());
		coloredWidget.setTextColor(ContextCompat.getColor(this, color.getColor()));

		return coloredWidget;
	}

}

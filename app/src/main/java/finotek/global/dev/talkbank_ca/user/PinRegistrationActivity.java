package finotek.global.dev.talkbank_ca.user;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
		getSupportActionBar().setTitle("");
		binding.toolbarTitle.setText("PIN 코드");
		binding.appbar.setOutlineProvider(null);
		binding.ibBack.setOnClickListener(v -> onBackPressed());

		for (Pin.Color c : Pin.Color.values()) {
			setPinBackgroundCircle(c);
			setTextColorCircle(c);
		}

		Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.slide_up);

		binding.llSecureKeyboard.setVisibility(View.VISIBLE);
		// Start animation
		binding.llSecureKeyboard.startAnimation(slide_up);


		binding.llPincodeWrapper.setOnClickListener(v -> {
	

			binding.llSecureKeyboard.setVisibility(View.VISIBLE);
			// Start animation
			binding.llSecureKeyboard.startAnimation(slide_up);

		});
		for (int i = 0; i < PINCODE_LENGTH; ++i) {
			TextView tv = generatePwdTextView();
			tvPwd[i] = tv;
			binding.llPincodeWrapper.addView(tv);
		}

		SecureKeyboardAdapter secureKeyboardAdapter = new SecureKeyboardAdapter(this, getCompleteRandomizedSeq());
		binding.gvKeypad.setAdapter(secureKeyboardAdapter);
		binding.gvKeypad.setOnItemClickListener((parent, view, position, id) -> {
			String key = (String) secureKeyboardAdapter.getItem(position);

			if (ptrTvPwd < PINCODE_LENGTH && !TextUtils.isEmpty(key.trim())) {
				tvPwd[ptrTvPwd++].setText("*");
			}
		});

		secureKeyboardAdapter.setOnBackPressListener(() -> {
			if (ptrTvPwd > 0) {
				tvPwd[--ptrTvPwd].setText("");
			}
		});

		secureKeyboardAdapter.onCompletePressed(() -> {
			startActivity(new Intent(PinRegistrationActivity.this, SettingsActivity.class));
		});


	}

	@NonNull
	private TextView generatePwdTextView() {
		TextView tv = new TextView(this);
		tv.setBackground(ContextCompat.getDrawable(this, R.drawable.border_black_blank));
		tv.setMinEms(3);
		tv.setPadding(0, 40, 0, 40);
		tv.setGravity(Gravity.CENTER);
		tv.setTextSize(15.8f);
		return tv;
	}

	private void setTextColorCircle(Pin.Color c) {

		ImageView iv2 = generateBackgroundColorCircle(c);
		binding.glPinTextColor.addView(iv2);

		iv2.setOnClickListener(new View.OnClickListener() {

			private boolean state = false;

			@Override
			public void onClick(View v) {

				/* 비밀번호 문자열 변경 */
				for (TextView tv : tvPwd) {
					tv.setTextColor((int) iv2.getTag());
				}

				if (state) {
					iv2.setImageDrawable(ContextCompat.getDrawable(PinRegistrationActivity.this, R.drawable.vector_drawable_icon_check));

					/* 흰색은 검은 체크 표시로 처리 */
					if(Pin.Color.WHITE == iv2.getTag()) {
						iv2.getDrawable().setTint(ContextCompat.getColor(PinRegistrationActivity.this, R.color.black));
					}

					iv2.setBackgroundColor((Integer) iv2.getTag());
				} else {
					iv2.setImageDrawable(ContextCompat.getDrawable(PinRegistrationActivity.this, R.drawable.circle));
					DrawableCompat.setTint(iv2.getDrawable(), ContextCompat.getColor(PinRegistrationActivity.this, c.getColor()));
				}

				state = !state;
			}
		});
	}

	private void setPinBackgroundCircle(Pin.Color c) {
		ImageView iv = generateBackgroundColorCircle(c);
		binding.glPinBackground.addView(iv);
		iv.setOnClickListener(v -> binding.llPincodeWrapper.setBackgroundColor((int) iv.getTag()));
	}

	@NonNull
	private ImageView generateBackgroundColorCircle(Pin.Color c) {
		ImageView iv = new ImageView(this);
		iv.setTag(ContextCompat.getColor(this, c.getColor()));
		iv.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.circle));
		DrawableCompat.setTint(iv.getDrawable(), ContextCompat.getColor(this, c.getColor()));
		return iv;
	}

	/* 키보드 문자열 생성 */
	private List<String> getCompleteRandomizedSeq() {
		ArrayList<String> completeSets = new ArrayList<>();

		for (int i = 0; i < 10; ++i) {
			completeSets.add(String.valueOf(i));
		}

		Collections.shuffle(completeSets);

		completeSets.add(7, " ");
		completeSets.add("등록");

		return completeSets;
	}

}

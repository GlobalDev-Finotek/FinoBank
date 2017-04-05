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

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;

public class PinRegistrationActivity extends AppCompatActivity {

	private final int PINCODE_LENGTH = 6;
	ActivityPinRegistrationBinding binding;
	private TextView[] tvPwd = new TextView[PINCODE_LENGTH];

	private ArrayList<ImageView> bgColorCircles = new ArrayList<>();
	private ArrayList<ImageView> textColorCircles = new ArrayList<>();
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


		try {
			Intent intent = getIntent();
			Class nextClass = (Class) intent.getExtras().get("nextClass");

			secureKeyboardAdapter.onCompletePressed(() -> {
				Intent intent2 = new Intent(PinRegistrationActivity.this, nextClass);
				intent2.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent2);
				finish();
			});

		} catch (NullPointerException e) {
			e.printStackTrace();
		}

	}

	@NonNull
	private TextView generatePwdTextView() {
		TextView tv = new TextView(this);
		tv.setBackground(ContextCompat.getDrawable(this, R.drawable.border_black_blank));
		tv.setMinEms(3);
		tv.setPadding(0, 30, 0, 30);
		tv.setGravity(Gravity.CENTER);
		tv.setTextSize(15.5f);
		return tv;
	}

	private void setTextColorCircle(Pin.Color c) {

		ImageView iv2 = generateBackgroundColorCircle(c);
		textColorCircles.add(iv2);
		binding.glPinTextColor.addView(iv2);

		iv2.setOnClickListener(new View.OnClickListener() {


			@Override
			public void onClick(View v) {

				/* 비밀번호 문자열 변경 */
				for (TextView tv : tvPwd) {
					tv.setTextColor((int) iv2.getTag());
				}

				iv2.setImageDrawable(ContextCompat.getDrawable(PinRegistrationActivity.this, R.drawable.vector_drawable_icon_check));

				/* 흰색은 검은 체크 표시로 처리 */
				if (ContextCompat.getColor(PinRegistrationActivity.this, Pin.Color.WHITE.getColor()) == (int)iv2.getTag()) {
					DrawableCompat.setTint(iv2.getDrawable(),
							ContextCompat.getColor(PinRegistrationActivity.this, R.color.black));
				} else {
					iv2.setBackgroundColor((Integer) iv2.getTag());
					DrawableCompat.setTint(iv2.getDrawable(),
							ContextCompat.getColor(PinRegistrationActivity.this, R.color.white));
				}

				ArrayList<ImageView> tmp = new ArrayList<>(textColorCircles);
				tmp.remove(iv2);
				for (ImageView iv : tmp) {
					iv.setImageDrawable(ContextCompat.getDrawable(PinRegistrationActivity.this, R.drawable.circle));
					DrawableCompat.setTint(iv.getDrawable(), (Integer) iv.getTag());
				}
			}
		});
	}

	private void setPinBackgroundCircle(Pin.Color c) {
		ImageView iv = generateBackgroundColorCircle(c);
		bgColorCircles.add(iv);
		binding.glPinBackground.addView(iv);

		iv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				binding.llPincodeWrapper.setBackgroundColor((int) iv.getTag());
				iv.setImageDrawable(ContextCompat.getDrawable(PinRegistrationActivity.this, R.drawable.vector_drawable_icon_check));

				/* 흰색은 검은 체크 표시로 처리 */
				if (ContextCompat.getColor(PinRegistrationActivity.this, Pin.Color.WHITE.getColor()) == (int)iv.getTag()) {
					DrawableCompat.setTint(iv.getDrawable(),
							ContextCompat.getColor(PinRegistrationActivity.this, R.color.black));
				} else {
					iv.setBackgroundColor((Integer) iv.getTag());
					DrawableCompat.setTint(iv.getDrawable(),
							ContextCompat.getColor(PinRegistrationActivity.this, R.color.white));
				}

				ArrayList<ImageView> tmp = new ArrayList<>(bgColorCircles);
				tmp.remove(iv);
				for (ImageView iv : tmp) {
					iv.setImageDrawable(ContextCompat.getDrawable(PinRegistrationActivity.this, R.drawable.circle));
					DrawableCompat.setTint(iv.getDrawable(), (Integer) iv.getTag());
				}
			}
		});

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

package finotek.global.dev.talkbank_ca.user;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.databinding.ActivityPinRegistrationBinding;
import finotek.global.dev.talkbank_ca.model.Pin;
import finotek.global.dev.talkbank_ca.util.Converter;
import finotek.global.dev.talkbank_ca.widget.SecureKeyboardAdapter;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;

public class PinRegistrationActivity extends AppCompatActivity {

	private final int PINCODE_LENGTH = 6;
	ActivityPinRegistrationBinding binding;
	private TextView[] tvPwd = new TextView[PINCODE_LENGTH];

	private ArrayList<View> bgColorCircles = new ArrayList<>();
	private ArrayList<View> textColorCircles = new ArrayList<>();
	private int ptrTvPwd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_pin_registration);
		setSupportActionBar(binding.toolbar);
		getSupportActionBar().setTitle("");

		binding.toolbarTitle.setText(getString(R.string.setting_string_pin_code));
		binding.appbar.setOutlineProvider(null);
		binding.ibBack.setOnClickListener(v -> onBackPressed());

		for (Pin.Color c : Pin.Color.values()) {
			setPinBackgroundCircle(c);
			setTextColorCircle(c);
		}

		Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
		binding.llSecureKeyboard.setVisibility(View.VISIBLE);
		// Start animation
		binding.llSecureKeyboard.startAnimation(slide_up);
		binding.llPincodeWrapper.setOnClickListener(v -> {
			binding.llSecureKeyboard.setVisibility(View.VISIBLE);
			binding.llSecureKeyboard.startAnimation(slide_up);
		});

		for (int i = 0; i < PINCODE_LENGTH; i++) {
			TextView tv = generatePwdTextView();
			tvPwd[i] = tv;

			if (i == 0) {
				tv.setBackground(getApplicationContext().getDrawable(R.drawable.pin_item_left));
			} else if (i == PINCODE_LENGTH - 1) {
				tv.setBackground(getApplicationContext().getDrawable(R.drawable.pin_item_right));
			}

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
				if (nextClass == null) {
					Intent intent2 = new Intent(PinRegistrationActivity.this, nextClass);
					intent2.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
					startActivity(intent2);
					finish();
				} else {
					finish();
				}
			});
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	@NonNull
	private TextView generatePwdTextView() {
		DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
		float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
		int width = (int) (dpWidth - Converter.dpToPx(32)) / 6;

		TextView tv = new TextView(this);
		tv.setBackground(ContextCompat.getDrawable(this, R.drawable.pin_item_middle));
		tv.setGravity(Gravity.CENTER);
		tv.setTextSize(15.5f);
		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
				Converter.dpToPx(width), Converter.dpToPx(44));
		param.weight = 1;
		tv.setLayoutParams(param);
		return tv;
	}

	private void setTextColorCircle(Pin.Color c) {
		View iv2 = generateBackgroundColorCircle(c);
		textColorCircles.add(iv2);
		binding.glPinTextColor.addView(iv2);

		DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
		float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
		int margin = (int) (dpWidth - Converter.dpToPx(62)) / 7;

		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) iv2.getLayoutParams();
		params.setMarginEnd(Converter.dpToPx(margin));
		iv2.setLayoutParams(params);

		iv2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/* 비밀번호 문자열 변경 */
				for (TextView tv : tvPwd) {
					tv.setTextColor((int) iv2.getTag());
				}

				ImageView icon = (ImageView) iv2.findViewById(R.id.icon);
				icon.setImageDrawable(ContextCompat.getDrawable(PinRegistrationActivity.this, R.drawable.select_color));

				/* 흰색은 검은 체크 표시로 처리 */
				if (ContextCompat.getColor(PinRegistrationActivity.this, Pin.Color.WHITE.getColor()) == (int) iv2.getTag()) {
					DrawableCompat.setTint(icon.getDrawable(), ContextCompat.getColor(PinRegistrationActivity.this, R.color.black));
				} else {
					DrawableCompat.setTint(icon.getDrawable(), ContextCompat.getColor(PinRegistrationActivity.this, R.color.white));
				}

				ArrayList<View> tmp = new ArrayList<>(textColorCircles);
				tmp.remove(iv2);
				for (View iv : tmp) {
					((ImageView) iv.findViewById(R.id.icon)).setImageDrawable(null);

					iv.setBackground(ContextCompat.getDrawable(PinRegistrationActivity.this, R.drawable.circle));
					DrawableCompat.setTint(iv.getBackground(), (Integer) iv.getTag());
				}
			}
		});
	}

	private void setPinBackgroundCircle(Pin.Color c) {
		View iv = generateBackgroundColorCircle(c);
		bgColorCircles.add(iv);
		binding.glPinBackground.addView(iv);

		DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
		float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
		int margin = (int) (dpWidth - Converter.dpToPx(62)) / 7;

		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) iv.getLayoutParams();
		params.setMarginEnd(Converter.dpToPx(margin));
		iv.setLayoutParams(params);

		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DrawableCompat.setTint(binding.llPincodeWrapper.getBackground(), (int) iv.getTag());

				ImageView icon = (ImageView) iv.findViewById(R.id.icon);
				icon.setImageDrawable(ContextCompat.getDrawable(PinRegistrationActivity.this, R.drawable.select_color));

				/* 흰색은 검은 체크 표시로 처리 */
				if (ContextCompat.getColor(PinRegistrationActivity.this, Pin.Color.WHITE.getColor()) == (int) iv.getTag()) {
					DrawableCompat.setTint(icon.getDrawable(), ContextCompat.getColor(PinRegistrationActivity.this, R.color.black));
				} else {
					DrawableCompat.setTint(icon.getDrawable(), ContextCompat.getColor(PinRegistrationActivity.this, R.color.white));
				}

				ArrayList<View> tmp = new ArrayList<>(bgColorCircles);
				tmp.remove(iv);

				for (View iv : tmp) {
					((ImageView) iv.findViewById(R.id.icon)).setImageDrawable(null);

					iv.setBackground(ContextCompat.getDrawable(PinRegistrationActivity.this, R.drawable.circle));
					DrawableCompat.setTint(iv.getBackground(), (Integer) iv.getTag());
				}
			}
		});
	}

	@NonNull
	private View generateBackgroundColorCircle(Pin.Color c) {
		View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_pin_color, (ViewGroup) binding.getRoot(), false);
		view.setBackground(ContextCompat.getDrawable(this, R.drawable.circle));
		view.setTag(ContextCompat.getColor(this, c.getColor()));
		DrawableCompat.setTint(view.getBackground(), ContextCompat.getColor(this, c.getColor()));
		return view;
	}

	/* 키보드 문자열 생성 */
	private List<String> getCompleteRandomizedSeq() {
		ArrayList<String> completeSets = new ArrayList<>();

		for (int i = 0; i < 10; ++i) {
			completeSets.add(String.valueOf(i));
		}

		Collections.shuffle(completeSets);

		completeSets.add(7, " ");
		completeSets.add(getString(R.string.setting_button_save));

		return completeSets;
	}
}
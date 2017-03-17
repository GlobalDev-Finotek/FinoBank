package finotek.global.dev.talkbank_ca.user;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.databinding.ActivityPinRegistrationBinding;
import finotek.global.dev.talkbank_ca.model.Pin;
import finotek.global.dev.talkbank_ca.widget.SecureKeyboardAdapter;
import finotek.global.dev.talkbank_ca.widget.SecureKeyboardBottomSheetDialog;

public class PinRegistrationActivity extends AppCompatActivity {

	ActivityPinRegistrationBinding binding;
	private final int PINCODE_LENGTH = 6;
	private TextView[] tvPwd = new TextView[PINCODE_LENGTH];
	private int ptrTvPwd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_pin_registration);

		for(Pin.Color c : Pin.Color.values()) {
			TextView iv = (TextView) crateColoredView(c);
			binding.glPinBackground.addView(iv);
			iv.setOnClickListener(v -> binding.glPinCode.setBackgroundColor(iv.getCurrentTextColor()));

			TextView iv2 = (TextView) crateColoredView(c);
			binding.glPinTextColor.addView(iv2);

			iv2.setOnClickListener(v -> {
				for(TextView tv : tvPwd) {
					tv.setTextColor(iv2.getCurrentTextColor());
				}
			});
		}

		binding.glPinCode.setUseDefaultMargins(false);
		binding.glPinCode.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
		binding.glPinCode.setRowOrderPreserved(false);

		for (int i = 0; i < PINCODE_LENGTH; ++i) {
			TextView tv = new TextView(this);
			tv.setPadding(50, 20, 50, 20);
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

			if (ptrTvPwd < tvPwd.length && !TextUtils.isEmpty(key.trim())) {
				tvPwd[ptrTvPwd++].setText("*");
			}

		});

		binding.btnRegistration.setOnClickListener(v -> finish());

	}

	private List<String> getCompleteRandomizedSeq() {
		ArrayList<String> completeSets = new ArrayList<>();

		for (int i = 0; i < 10; ++i) {
			completeSets.add(String.valueOf(i));
		}

		Collections.shuffle(completeSets);

		completeSets.add(new Random().nextInt(completeSets.size() - 1), " ");
		completeSets.add(new Random().nextInt(completeSets.size() - 1), " ");

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

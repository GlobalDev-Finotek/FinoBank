package finotek.global.dev.talkbank_ca;

import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.GridLayout.Spec;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.HashMap;

import finotek.global.dev.talkbank_ca.databinding.ActivityPinRegistrationBinding;
import finotek.global.dev.talkbank_ca.model.Pin;

import static android.widget.GridLayout.ALIGN_BOUNDS;
import static android.widget.GridLayout.spec;

public class PinRegistrationActivity extends AppCompatActivity {

	ActivityPinRegistrationBinding binding;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_pin_registration);

		for(Pin.Color c : Pin.Color.values()) {
			TextView iv = (TextView) crateColoredView(c);
			binding.glPinBackground.addView(iv);
			iv.setOnClickListener(v -> binding.llPinCode.setBackgroundColor(iv.getCurrentTextColor()));


			TextView iv2 = (TextView) crateColoredView(c);
			binding.glPinTextColor.addView(iv2);
			iv2.setOnClickListener(v -> binding.tvPin.setTextColor(iv2.getCurrentTextColor()));
		}

		binding.tvPin.setText("*    *     *     *     *");



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

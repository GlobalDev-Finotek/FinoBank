package finotek.global.dev.talkbank_ca.model;

import finotek.global.dev.talkbank_ca.R;

/**
 * Created by magyeong-ug on 13/03/2017.
 */

public class Pin {
	public enum Color {
		RED(R.color.pin_red),
		ORANGE(R.color.pin_orange),
		YELLOW(R.color.pin_yellow),
		// LIGHT_GREEN (R.color.pin_light_green),
		GREEN(R.color.pin_green),
		// LIGHT_BLUE (R.color.pin_light_blue),
		BLUE(R.color.pin_blue),
		PURPLE(R.color.pin_purple),
		WHITE(R.color.pin_white),
		BLACK(R.color.pin_black);

		private int colorId;

		Color(int value) {
			this.colorId = value;
		}

		public int getColor() {
			return this.colorId;
		}
	}
}

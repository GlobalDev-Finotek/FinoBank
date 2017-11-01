package finotek.global.dev.brazil.model;

import finotek.global.dev.brazil.R;

public class Pin {
	public enum Color {
		RED(R.color.pin_red),
		ORANGE(R.color.pin_orange),
		GREEN(R.color.pin_green),
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

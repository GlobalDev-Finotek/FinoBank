package finotek.global.dev.talkbank_ca.util;

import android.content.res.Resources;
import android.util.DisplayMetrics;

public class Converter {
	public static int pxToDp(int px) {
		DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
		int dp = (int) (px / (metrics.densityDpi / 160f));
		return Math.round(dp);
	}

	public static int dpToPx(int dp) {
		DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
		return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
	}
}

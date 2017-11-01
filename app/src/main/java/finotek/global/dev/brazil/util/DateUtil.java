package finotek.global.dev.brazil.util;

import android.content.Context;

import java.util.Calendar;

import finotek.global.dev.brazil.R;

public class DateUtil {
	public static String currentDate(Context context) {

		Calendar cal = Calendar.getInstance();
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);

		String strWeek = "";
		int nWeek = cal.get(Calendar.DAY_OF_WEEK);
		if (nWeek == 1) {
			strWeek = context.getString(R.string.sunday);
		} else if (nWeek == 2) {
			strWeek = context.getString(R.string.monday);
		} else if (nWeek == 3) {
			strWeek = context.getString(R.string.tuesday);
		} else if (nWeek == 4) {
			strWeek = context.getString(R.string.wednesday);
		} else if (nWeek == 5) {
			strWeek = context.getString(R.string.thursday);
		} else if (nWeek == 6) {
			strWeek = context.getString(R.string.friday);
		} else if (nWeek == 7) {
			strWeek = context.getString(R.string.saturday);
		}

		return String.format("%02d.%02d %s", month, day, strWeek);
	}
}

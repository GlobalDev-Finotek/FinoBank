package finotek.global.dev.talkbank_ca.util;

import java.util.Calendar;
import java.util.Locale;

public class DateUtil {
    public static String currentDate() {

        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        String strWeek = "";
        int nWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (nWeek == 1) {
            strWeek = (Locale.getDefault().getDisplayLanguage().equals("ko")) ? "일요일" : "Sun";
        } else if (nWeek == 2) {
            strWeek = (Locale.getDefault().getDisplayLanguage().equals(" ko")) ? "월요일" : "Mon";
        } else if (nWeek == 3) {
            strWeek = (Locale.getDefault().getDisplayLanguage().equals("ko")) ? "화요일" : "Tue";
        } else if (nWeek == 4) {
            strWeek = (Locale.getDefault().getDisplayLanguage().equals("ko")) ? "수요일" : "Wed";
        } else if (nWeek == 5) {
            strWeek = (Locale.getDefault().getDisplayLanguage().equals("ko")) ? "목요일" : "Thus";
        } else if (nWeek == 6) {
            strWeek = (Locale.getDefault().getDisplayLanguage().equals("ko")) ? "금요일" : "Fri";
        } else if (nWeek == 7) {
            strWeek = (Locale.getDefault().getDisplayLanguage().equals("ko")) ? "토요일" : "Sat";
        }

        return String.format("%02d.%02d %s", month, day, strWeek);
    }

}

package finotek.global.dev.talkbank_ca.util;

import android.content.res.Resources;
import android.util.DisplayMetrics;

public class Converter {
    public static float convertPixelsToDp(float px){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return Math.round(dp);
    }
}

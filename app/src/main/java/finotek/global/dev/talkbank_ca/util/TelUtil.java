package finotek.global.dev.talkbank_ca.util;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * Created by magyeong-ug on 2017. 3. 30..
 */

public class TelUtil {
  public static String getMyPhoneNumber(Context context) {
    TelephonyManager tMgr =(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
    String phoneNumber = tMgr.getLine1Number();

    if (TextUtils.isEmpty(phoneNumber)) {
      return "0";
    } else {
      return phoneNumber;
    }
  }

  public static String getDeviceName() {
    String manufacturer = Build.MANUFACTURER;
    String model = Build.MODEL;
    if (model.startsWith(manufacturer)) {
      return (model);
    }
    return (manufacturer) + " " + model;
  }
}

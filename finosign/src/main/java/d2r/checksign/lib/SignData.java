package d2r.checksign.lib;

import android.view.MotionEvent;

/**
 * Created by magyeong-ug on 2017. 10. 11..
 */

public class SignData {
	public int x;
	public int y;
	public int time;
	public int act;

	@Override
	public String toString() {
//		if (act == MotionEvent.ACTION_UP)
//			return "\n";
//		else
        return String.valueOf(x) + "," + String.valueOf(y) + "," + String.valueOf(time) + ",";
	}

	public byte[] toByte() {
		return toString().getBytes();
	}

}

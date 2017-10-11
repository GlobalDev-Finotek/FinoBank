package globaldev.finotek.com.finosign;

/**
 * Created by magyeong-ug on 2017. 10. 11..
 */

public class SignData {
	public float x;
	public float y;
	public float time;

	@Override
	public String toString() {
		return String.valueOf(x) + "," + String.valueOf(y) + "," + String.valueOf(time) + ",";
	}

	public byte[] toByte() {
		return toString().getBytes();
	}

}

package d2r.checksign.lib;

/**
 * Created by magyeong-ug on 2017. 10. 11..
 */

public class SignData {
	public int x;
	public int y;
	public int time;

	@Override
	public String toString() {
		return String.valueOf(x) + "," + String.valueOf(y) + "," + String.valueOf(time) + ",";
	}

	public byte[] toByte() {
		return toString().getBytes();
	}

}

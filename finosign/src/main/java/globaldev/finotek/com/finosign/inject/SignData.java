package globaldev.finotek.com.finosign.inject;

/**
 * Created by magyeong-ug on 2017. 11. 6..
 */
public final class SignData {
	String data;
	int type;

	SignData(int type, String data) {
		this.data = data;
		this.type = type;
	}
}

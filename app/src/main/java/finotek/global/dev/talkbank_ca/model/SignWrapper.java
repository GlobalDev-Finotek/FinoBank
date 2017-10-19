package finotek.global.dev.talkbank_ca.model;

/**
 * Created by magyeong-ug on 2017. 10. 19..
 */

public class SignWrapper {
	public StringBuilder firstDatas = new StringBuilder();
	public StringBuilder secondDatas = new StringBuilder();

	public void clear() {
		firstDatas.setLength(0);
		secondDatas.setLength(0);
	}
}

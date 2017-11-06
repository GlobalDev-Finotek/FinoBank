package globaldev.finotek.com.finosign;

import io.reactivex.Flowable;

/**
 * Created by magyeong-ug on 2017. 11. 3..
 */

public interface FinoSignService {
	Flowable<String> register(String data, int signType, String userKey);

	Flowable<Boolean> verify(String masterSignData, String hiddenSignData, String userKey);
}

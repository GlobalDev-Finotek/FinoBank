import junit.framework.TestCase;

import globaldev.finotek.com.finosign.SignType;
import globaldev.finotek.com.finosign.service.FinoSign;
import io.reactivex.functions.Consumer;

/**
 * Created by magyeong-ug on 2017. 11. 8..
 */

public class FinoSignTestCase extends TestCase {
	@Override
	public void setUp() throws Exception {
		super.setUp();
	}

	public void testRegister() throws Exception {

		String data = "12342";
		int signType = SignType.HIDDEN;
		String userKey = "userKey1234";

		FinoSign.getInstance()
				.register(data, signType, userKey)
				.subscribe(new Consumer<String>() {
					@Override
					public void accept(String s) throws Exception {
						assertEquals(true, s != null);
						assertEquals(true, s.length() > 0);
					}
				});
	}

	public void testVerify() throws Exception {
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
	}
}

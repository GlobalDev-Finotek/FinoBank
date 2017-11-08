package globaldev.finotek.com.finosign.service;

import globaldev.finotek.com.finosign.FinoSignService;

/**
 * Created by magyeong-ug on 2017. 11. 6..
 */

public class FinoSign {
	private static final FinoSignService finoSignService = new FinoSignServiceImpl();

	public static FinoSignService getInstance() {
		return finoSignService;
	}
}

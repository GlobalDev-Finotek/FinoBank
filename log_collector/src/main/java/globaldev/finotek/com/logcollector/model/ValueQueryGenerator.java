package globaldev.finotek.com.logcollector.model;

import java.util.HashMap;

/**
 * Created by magyeong-ug on 2017. 8. 9..
 */

public interface ValueQueryGenerator {
	HashMap<String, String> generate();

	int getLogType();
}

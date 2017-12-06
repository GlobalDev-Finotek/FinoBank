package globaldev.finotek.com.finosign.inject.request;

import java.util.ArrayList;
import java.util.List;

import globaldev.finotek.com.finosign.inject.SignData;

/**
 * Created by magyeong-ug on 2017. 12. 6..
 */

public class SignRegisterRequest {
	public List<SignData> signs = new ArrayList<>();
  public String userKey;
	public int type;
}

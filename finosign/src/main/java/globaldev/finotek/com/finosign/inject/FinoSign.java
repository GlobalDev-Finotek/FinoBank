package globaldev.finotek.com.finosign.inject;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;

import globaldev.finotek.com.finosign.BuildConfig;
import globaldev.finotek.com.finosign.FinoSignService;
import globaldev.finotek.com.finosign.R;
import globaldev.finotek.com.finosign.inject.request.UserJoinRequest;
import globaldev.finotek.com.finosign.inject.response.BaseResponse;
import globaldev.finotek.com.finosign.inject.response.UserJoinResponse;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by magyeong-ug on 2017. 11. 6..
 */

public class FinoSign {

	static FinoSignService finoSignService;

	public static void init(Application application) {
		init((Context) application);
	}

	public static void init(Context context) {
		finoSignService = new FinoSignServiceImpl(context);
	}

	public static FinoSignService getInstance() {
		return finoSignService;
	}



}

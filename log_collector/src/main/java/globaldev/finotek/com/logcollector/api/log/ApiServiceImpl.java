package globaldev.finotek.com.logcollector.api.log;

import com.google.gson.Gson;

import java.util.List;

import javax.inject.Inject;

import globaldev.finotek.com.logcollector.api.message.BaseRequest;
import globaldev.finotek.com.logcollector.api.message.BaseResponse;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by magyeong-ug on 27/04/2017.
 */

public class ApiServiceImpl<T> {

	@Inject
	LogApi logService;


	@Inject
	public ApiServiceImpl(LogApi logService) {
		this.logService = logService;
	}

	public Flowable<BaseResponse> upload(String userKey, int actionType, List<T> logs, boolean isGetAllData) {
		UploadLogParam<T> uploadLogParam = new UploadLogParam<>();
		uploadLogParam.userKey = userKey;
		uploadLogParam.logs = logs;
		uploadLogParam.type = actionType;

		BaseRequest<UploadLogParam<T>> updateLogRequest =
				new BaseRequest.Builder<UploadLogParam<T>>()
						.setParam(uploadLogParam)
						.build();


		String mode = "";

		if (isGetAllData) {
			mode = "init";
		} else {
			mode = "running";
		}

		String json = new Gson().toJson(updateLogRequest);

		return logService.update(updateLogRequest)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread());
	}

	class UploadLogParam<T> {
		int type;
		String userKey;
		List<T> logs;
	}
}

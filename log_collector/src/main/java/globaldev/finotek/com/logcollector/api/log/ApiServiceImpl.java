package globaldev.finotek.com.logcollector.api.log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import globaldev.finotek.com.logcollector.api.message.BaseRequest;
import globaldev.finotek.com.logcollector.api.message.BaseResponse;
import globaldev.finotek.com.logcollector.model.ValueQueryGenerator;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by magyeong-ug on 27/04/2017.
 */

public class ApiServiceImpl<T> {

	LogApi logService;

	public ApiServiceImpl(LogApi logService) {
		this.logService = logService;
	}

	static class RecentLogParam {
		public String userKey;
		public List<LogParamDatas> logs = new ArrayList<>();

		static class LogParamDatas {
			public int type;
			public HashMap<String, String> queryMap;
		}
	}


	public Flowable<BaseResponse> getRecentLogs(String userKey, List<ValueQueryGenerator> logs) {

		RecentLogParam recentLogParam = new RecentLogParam();
		recentLogParam.userKey = userKey;

		for (ValueQueryGenerator vgq : logs) {
			RecentLogParam.LogParamDatas params = new RecentLogParam.LogParamDatas();
			params.type = vgq.getLogType();
			params.queryMap = vgq.generate();
			recentLogParam.logs.add(params);
		}

		BaseRequest<RecentLogParam> getRecentLogRequest =
				new BaseRequest.Builder<RecentLogParam>()
						.setParam(recentLogParam)
						.build();


		return logService.getRecentLogs(getRecentLogRequest)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread());

	}

	public Flowable<BaseResponse> upload(String userKey, int actionType, List<T> logs) {
		UploadLogParam<T> uploadLogParam = new UploadLogParam<>();
		uploadLogParam.userKey = userKey;
		uploadLogParam.logs = logs;
		uploadLogParam.type = actionType;

		BaseRequest<UploadLogParam<T>> updateLogRequest =
				new BaseRequest.Builder<UploadLogParam<T>>()
						.setParam(uploadLogParam)
						.build();

		return logService.update(updateLogRequest)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread());
	}

	private class UploadLogParam<Type> {
		int type;
		String userKey;
		List<Type> logs;
	}
}

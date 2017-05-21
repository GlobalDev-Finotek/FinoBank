package globaldev.finotek.com.logcollector.api.user;


import org.reactivestreams.Publisher;

import javax.inject.Inject;

import globaldev.finotek.com.logcollector.api.message.BaseRequest;
import globaldev.finotek.com.logcollector.api.message.BaseResponse;
import globaldev.finotek.com.logcollector.api.message.Header;
import globaldev.finotek.com.logcollector.model.User;
import globaldev.finotek.com.logcollector.model.UserDevice;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by magyeong-ug on 28/04/2017.
 */

public class UserServiceImpl {

	@Inject
	UserService userService;

	@Inject
	public UserServiceImpl(UserService userService) {
		this.userService = userService;
	}

	public Flowable<UserInitResponse> init(User user, UserDevice device) {
		UserInitParam userInitParam = new UserInitParam(user, device);
		BaseRequest<UserInitParam> request = new BaseRequest.Builder<UserInitParam>()
				.setHeader(new Header())
				.setParam(userInitParam)
				.build();

		return userService.init(request)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeOn(Schedulers.io())
				.flatMap(new Function<BaseResponse<UserInitResponse>, Publisher<UserInitResponse>>() {
					@Override
					public Publisher<UserInitResponse> apply(BaseResponse<UserInitResponse> userInitResponseBaseResponse) throws Exception {
						return BaseResponse.body(userInitResponseBaseResponse);
					}
				});
	}
}

package globaldev.finotek.com.logcollector.api.file;

import java.io.File;

import javax.inject.Inject;

import globaldev.finotek.com.logcollector.api.message.BaseResponse;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by magyeong-ug on 16/05/2017.
 */

public class FileServiceImpl {

	@Inject
	FileApi fileApi;

	@Inject
	public FileServiceImpl(FileApi fileApi) {
		this.fileApi = fileApi;
	}

	public Flowable<BaseResponse> uploadFile(File file) {
		return fileApi.upload(file)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread());
	}

}

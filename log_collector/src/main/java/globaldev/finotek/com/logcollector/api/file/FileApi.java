package globaldev.finotek.com.logcollector.api.file;

import java.io.File;

import globaldev.finotek.com.logcollector.api.message.BaseResponse;
import io.reactivex.Flowable;
import retrofit2.http.Multipart;
import retrofit2.http.PUT;
import retrofit2.http.Part;

/**
 * Created by magyeong-ug on 16/05/2017.
 */

public interface FileApi {

	// TODO 파라메터 정의
	@Multipart
	@PUT("file")
	Flowable<BaseResponse>
	upload(@Part File file);
}

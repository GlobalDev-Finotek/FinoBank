package finotek.global.dev.talkbank_ca.inject.module;

import android.app.Activity;
import android.support.annotation.NonNull;

import dagger.Module;
import dagger.Provides;
import finotek.global.dev.talkbank_ca.inject.scope.PerActivity;

/**
 * Created by magyeong-ug on 21/03/2017.
 */

@Module
public class ActivityModule {
	@NonNull
	private final Activity activity;

	public ActivityModule(@NonNull Activity activity) {
		this.activity = activity;
	}

	@Provides
	@PerActivity
	Activity activity() {
		return this.activity;
	}
}

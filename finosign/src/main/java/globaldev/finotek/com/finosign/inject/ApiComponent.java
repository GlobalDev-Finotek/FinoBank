package globaldev.finotek.com.finosign.inject;

import javax.inject.Singleton;

import dagger.Component;


/**
 * Created by magyeong-ug on 2017. 11. 3..
 */
@Singleton
@Component(modules = ApiModule.class)
public interface ApiComponent {
	void inject(FinoSignServiceImpl finoSign);
}

package finotek.global.dev.talkbank_ca.inject.scope;

import java.lang.annotation.Retention;

import javax.inject.Scope;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by magyeong-ug on 21/03/2017.
 */

@Scope
@Retention(RUNTIME)
public @interface PerActivity {

}

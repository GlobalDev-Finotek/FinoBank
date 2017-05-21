package globaldev.finotek.com.logcollector.log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by magyeong-ug on 26/04/2017.
 */

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface UserScope {
}

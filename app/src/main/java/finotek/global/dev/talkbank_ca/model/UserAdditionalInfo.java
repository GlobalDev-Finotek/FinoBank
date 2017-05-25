package finotek.global.dev.talkbank_ca.model;

import io.realm.RealmObject;
import lombok.Data;

/**
 * Created by kwm on 2017. 3. 6..
 */

@Data
public class UserAdditionalInfo extends RealmObject {
	String emergencyPhoneNumber;
	String email;
	String creditPicPath;
	String profilePicPath;

	public UserAdditionalInfo() {

	}

}

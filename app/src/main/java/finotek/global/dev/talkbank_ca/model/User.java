package finotek.global.dev.talkbank_ca.model;

import io.realm.RealmObject;
import lombok.Data;

/**
 * Created by kwm on 2017. 3. 6..
 */


public
@Data
class User extends RealmObject {
  String phoneNumber;
  String name;
  String signaturePath;

  UserAdditionalInfo additionalInfo;
}

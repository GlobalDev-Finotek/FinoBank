package finotek.global.dev.talkbank_ca.model;

import lombok.Data;

/**
 * Created by kwm on 2017. 3. 6..
 */

@Data
public class User {
  String phoneNumber;
  String name;
  String signaturePath;

  UserAdditionalInfo additionalInfo;
}
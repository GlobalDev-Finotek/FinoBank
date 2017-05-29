package globaldev.finotek.com.logcollector.model;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by magyeong-ug on 11/05/2017.
 */

public class IDCard extends RealmObject {
	String userKey;
	int cardType;
	String frontImageUrl;
	String backImageUrl;
	String sequenceNumber;
	String name;
	String address;
	Date issuedDate;
	Date expiredDate;
	String extraText;
}

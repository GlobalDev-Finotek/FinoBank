package finotek.global.dev.talkbank_ca.model;

import java.util.Date;

import lombok.Data;

/**
 * Created by magyeong-ug on 15/03/2017.
 */
@Data
public class CreditCard {
	public String number;
	public String company;
	public Date expirationDate;
	public String name;

	public static CreditCard getMockData() {
		CreditCard creditCard = new CreditCard();
		creditCard.company = "BC";
		creditCard.number = "5555-****-****-2222";
		creditCard.expirationDate = new Date();
		creditCard.name = "SEON JI SIM";
		return creditCard;
	}
}

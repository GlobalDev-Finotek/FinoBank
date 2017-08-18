package globaldev.finotek.com.logcollector.model;

/**
 * Created by magyeong-ug on 27/04/2017.
 */

public class User {
	String phoneNumber;
	String name;
	String email;
	String gender;

	public User(String phoneNumber, String name, String email) {
		this.phoneNumber = phoneNumber;
		this.name = name;
		this.email = email;
		this.gender = " ";
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}
}

package finotek.global.dev.talkbank_ca.chat.messages.ui;

import android.graphics.Bitmap;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IDCardInfo {
	String type;
	String name;
	String jumin;
	String issueDate;
	Bitmap image;
}

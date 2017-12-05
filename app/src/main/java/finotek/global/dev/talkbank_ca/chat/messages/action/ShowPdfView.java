package finotek.global.dev.talkbank_ca.chat.messages.action;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
public class ShowPdfView {
	String title;
	String pdfAsset;
	int step;

	public ShowPdfView(String title, String pdfAsset) {
		this.title = title;
		this.pdfAsset = pdfAsset;
	}
}

package finotek.global.dev.brazil.chat.messages.control;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class DonateRequest {
	@NonNull
	String title;
	@NonNull
	String description;
	@NonNull
	List<DonateItem> menus;

	Runnable doAfterEvent;
	boolean enabled = true;

	public void addMenu(int image, String menu, Runnable listener) {
		if (menus == null)
			menus = new ArrayList<>();

		menus.add(new DonateItem(image, menu, listener));
	}
}

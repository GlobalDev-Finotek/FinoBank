package finotek.global.dev.brazil.chat.messages.control;

import finotek.global.dev.brazil.widget.RoundButton;
import io.reactivex.annotations.NonNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ConfirmControlEvent {
	@NonNull
	RoundButton.ButtonType buttonType;
	@NonNull
	String name;
	@NonNull
	Runnable listener;
	@NonNull
	boolean isDisappearAfter;
	int icon = 0;
}

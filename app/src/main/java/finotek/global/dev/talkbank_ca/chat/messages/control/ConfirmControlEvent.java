package finotek.global.dev.talkbank_ca.chat.messages.control;

import finotek.global.dev.talkbank_ca.widget.RoundButton;
import io.reactivex.annotations.NonNull;
import io.realm.annotations.Required;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ConfirmControlEvent {
    @NonNull RoundButton.ButtonType buttonType;
    @NonNull String name;
    @NonNull Runnable listener;
    @NonNull boolean isDisappearAfter;
    int icon = 0;
}

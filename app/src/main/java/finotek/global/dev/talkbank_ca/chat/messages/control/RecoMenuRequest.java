package finotek.global.dev.talkbank_ca.chat.messages.control;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.MessageBox;
import finotek.global.dev.talkbank_ca.chat.messages.SendMessage;
import lombok.Data;

@Data
public class RecoMenuRequest {
    String title;
    String description;
    List<RecoMenu> menus;

    Runnable doAfterEvent;
    boolean enabled = true;

    public void addMenu(int icon, String name, Runnable listener){
        if(menus == null)
            menus = new ArrayList<>();


        if(listener == null)
            listener = () -> {
                MessageBox.INSTANCE.add(new SendMessage(name));
            };

        menus.add(new RecoMenu(icon, name, listener));
    }

    public static RecoMenuRequest buildYesOrNo(Context context, String description){
        RecoMenuRequest req = new RecoMenuRequest();
        req.setDescription(description);

        String yes = context.getResources().getString(R.string.dialog_button_yes);
        String no = context.getResources().getString(R.string.dialog_button_no);

        req.addMenu(R.drawable.icon_haha, yes, () -> {
            MessageBox.INSTANCE.add(new SendMessage(yes));
        });
        req.addMenu(R.drawable.icon_sad, no, () -> {
            MessageBox.INSTANCE.add(new SendMessage(no));
        });

        return req;
    }
}

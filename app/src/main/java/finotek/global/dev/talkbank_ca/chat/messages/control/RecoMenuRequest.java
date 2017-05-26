package finotek.global.dev.talkbank_ca.chat.messages.control;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class RecoMenuRequest {
    String title;
    String description;
    List<RecoMenu> menus;

    Runnable doAfterEvent;

    public void addMenu(int icon, String name, Runnable listener){
        if(menus == null)
            menus = new ArrayList<>();

        menus.add(new RecoMenu(icon, name, listener));
    }
}

package finotek.global.dev.talkbank_ca.chat.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import finotek.global.dev.talkbank_ca.R;

public class ExtendedControlBuilder {
    public static View build(Context context, ViewGroup parent){
        return LayoutInflater.from(context).inflate(R.layout.chat_extended_control, parent, false);
    }
}

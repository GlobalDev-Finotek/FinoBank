package finotek.global.dev.talkbank_ca.chat.messages.action;

import android.text.InputType;

import lombok.Data;

@Data
public class RequestKeyboardInput {
    private int inputType;

    public RequestKeyboardInput() {
        inputType = InputType.TYPE_CLASS_TEXT;
    }

    public RequestKeyboardInput(int inputType){
        this.inputType = inputType;
    }
}

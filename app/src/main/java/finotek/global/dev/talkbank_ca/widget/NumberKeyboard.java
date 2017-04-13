package finotek.global.dev.talkbank_ca.widget;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.GridView;

import com.jakewharton.rxbinding2.view.RxView;

import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class NumberKeyboard extends GridView {
    private SecureKeyboardAdapter keyboardAdapter;
    private Runnable onCompleteListener = null;
    private EditText currentFocus = null;

    public NumberKeyboard(Context context) {
        super(context);
    }

    public NumberKeyboard(Context context, AttributeSet attrs) {
        super(context, attrs);

        keyboardAdapter = new SecureKeyboardAdapter(context, transferKeyboardSet());
        this.setAdapter(keyboardAdapter);
        this.setOnItemClickListener((parent, view, position, id) -> {
            String key = (String) keyboardAdapter.getItem(position);

            if (position != keyboardAdapter.getCount() - 1 && currentFocus != null) {
                String text = currentFocus.getText().toString();
                if(text.length() < 12) {
                    text = text + key;
                    currentFocus.setText(numberFormat(text));
                }
            }
        });

        keyboardAdapter.setOnBackPressListener(() -> {
            String text = currentFocus.getText().toString();
            if(!text.isEmpty() && currentFocus != null)
                currentFocus.setText(numberFormat(text.substring(0, text.length()-1)));
        });

        keyboardAdapter.onCompletePressed(() -> {
            if(this.onCompleteListener != null)
                onCompleteListener.run();
        });
    }

    public void addManagableTextField(final EditText editText){
        disableKeyboard(editText);

        RxView.focusChanges(editText)
                .subscribe(hasFocus -> {
                    if(hasFocus)
                        currentFocus = editText;
                });
    }

    public void onComplete(Runnable onCompleteListener){
        this.onCompleteListener = onCompleteListener;
    }

    private String numberFormat(String text) {
        text = text.replaceAll(",", "");
        if(!text.isEmpty())
            return NumberFormat.getNumberInstance().format(Long.parseLong(text));
        else
            return "";
    }

    private void disableKeyboard(EditText editText){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            editText.setShowSoftInputOnFocus(false);
        } else {
            try {
                final Method method = EditText.class.getMethod(
                        "setShowSoftInputOnFocus"
                        , new Class[]{boolean.class});
                method.setAccessible(true);
                method.invoke(editText, false);
            } catch (Exception e) {

            }
        }
    }

    private List<String> transferKeyboardSet() {
        List<String> set = new ArrayList<>();
        set.add("1");
        set.add("2");
        set.add("3");
        set.add("4");
        set.add("5");
        set.add("6");
        set.add("7");
        set.add("-");
        set.add("8");
        set.add("9");
        set.add("0");
        set.add("이체");
        return set;
    }
}

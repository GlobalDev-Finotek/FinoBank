package finotek.global.dev.talkbank_ca.user.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.databinding.DialogSuccessBinding;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class SucceededDialog extends Dialog {
    private DialogSuccessBinding binding;
    private Runnable listener;

    public SucceededDialog(@NonNull Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_success, null, false);
        setContentView(binding.getRoot());

        RxView.clicks(binding.doneBtn)
            .observeOn(AndroidSchedulers.mainThread())
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .subscribe(aVoid -> {
                    if (listener != null) {
                        dismiss();
                        listener.run();
                    }
                });
    }

    public void setTitle(String title){
        binding.title.setText(title);
    }

    public void setDescription(String description){
        binding.description.setText(description);
    }

    public void setButtonText(String text){
        binding.doneBtn.setVisibility(View.VISIBLE);
        binding.doneBtn.setText(text);
    }

    public void setDoneListener(Runnable listener){
        this.listener = listener;
    }

    @Override
    public void show() {
        this.setCancelable(false);
        this.setCanceledOnTouchOutside(false);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = (int) ((int)displaymetrics.widthPixels * 0.85);

        getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        super.show();
    }
}

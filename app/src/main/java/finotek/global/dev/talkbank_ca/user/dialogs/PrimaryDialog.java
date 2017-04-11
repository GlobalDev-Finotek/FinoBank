package finotek.global.dev.talkbank_ca.user.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.databinding.DialogPrimaryBinding;

public class PrimaryDialog extends Dialog {
    private DialogPrimaryBinding binding;
    private Runnable listener;

    public PrimaryDialog(@NonNull Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_primary, null, false);
        setContentView(binding.getRoot());

        RxView.clicks(binding.doneBtn)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .subscribe(aVoid -> {
                    if(listener != null)
                        listener.run();
                });

        RotateAnimation anim = new RotateAnimation(0, -360,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(700);

        binding.icon.startAnimation(anim);


    }

    @Override
    public void dismiss() {
        super.dismiss();
        binding.icon.setAnimation(null);
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
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        super.show();
    }
}

package finotek.global.dev.talkbank_ca.user.cardif;

import android.app.Fragment;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.databinding.FragmentContractBinding;

public class ContractFragment extends Fragment {
    private FragmentContractBinding binding;
    private Runnable confirmListener;
    private Runnable cancelListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_contract, container, false);

        RxView.clicks(binding.confirmButton)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .subscribe(aVoid -> {
                    binding.contractSignedImage.save();

                    if(confirmListener != null)
                        confirmListener.run();
                });

        RxView.clicks(binding.cancelButton)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .subscribe(aVoid -> {
                    if(cancelListener != null)
                        cancelListener.run();
                });

        return binding.getRoot();
    }

    public void setConfirmListener(Runnable confirmListener) {
        this.confirmListener = confirmListener;
    }

    public void setCancelListener(Runnable cancelListener) {
        this.cancelListener = cancelListener;
    }
}

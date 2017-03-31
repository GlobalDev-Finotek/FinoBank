package finotek.global.dev.talkbank_ca.widget;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.databinding.WidgetSecureKeyboardBinding;

/**
 * Created by magyeong-ug on 16/03/2017.
 */

public class SecureKeyboardBottomSheetDialog extends BottomSheetDialogFragment {

	private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

		@Override
		public void onStateChanged(@NonNull View bottomSheet, int newState) {
			if (newState == BottomSheetBehavior.STATE_HIDDEN) {
				dismiss();
			}

		}

		@Override
		public void onSlide(@NonNull View bottomSheet, float slideOffset) {
		}
	};

	private SecureKeyboardAdapter secureKeyboardAdapter;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		WidgetSecureKeyboardBinding binding = WidgetSecureKeyboardBinding.inflate(inflater, container, false);

		secureKeyboardAdapter = new SecureKeyboardAdapter(getContext(),
				getCompleteRandomizedSeq());

		binding.gvKeypad.setAdapter(secureKeyboardAdapter);

		return binding.getRoot();
	}

	private List<String> getCompleteRandomizedSeq() {
		ArrayList<String> completeSets = new ArrayList<>();

		for (int i = 0; i < 10; ++i) {
			completeSets.add(String.valueOf(i));
		}

		Collections.shuffle(completeSets);
		return completeSets;
	}

}


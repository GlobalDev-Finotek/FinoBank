package finotek.global.dev.talkbank_ca.setting.context_aware;

import android.app.TimePickerDialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Date;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.databinding.FragmentContextAwareBinding;

/**
 * Created by magyeong-ug on 21/03/2017.
 */

public class ContextAwareAuthFragment extends android.app.Fragment {

	public static ContextAwareAuthFragment newInstance(String title) {
		ContextAwareAuthFragment fragment = new ContextAwareAuthFragment();
		Bundle args = new Bundle();
		args.putString("title", title);
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		FragmentContextAwareBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_context_aware, container, false);

		SimpleDateFormat sdfDate = new SimpleDateFormat("hh:mm");
		Date now = new Date();
		String strDate = sdfDate.format(now);

		binding.tvStart.setText(strDate);
		binding.tvEnd.setText(strDate);

		binding.tvStart.setOnClickListener(v -> {
			TimePickerDialog dialog = new TimePickerDialog(getActivity(),
					(view, hourOfDay, minute) -> {
						String hr = hourOfDay > 10 ? String.valueOf(hourOfDay) : "0" + String.valueOf(hourOfDay);
						String min = minute > 10 ? String.valueOf(minute) : "0" + String.valueOf(minute);
						binding.tvStart.setText(hr + ":" + min);
					},
					now.getHours(), now.getMinutes(), false);
			dialog.show();
		});

		binding.tvEnd.setOnClickListener(v -> {
			TimePickerDialog dialog = new TimePickerDialog(getActivity(),
					(view, hourOfDay, minute) -> {
						String hr = hourOfDay > 10 ? String.valueOf(hourOfDay) : "0" + String.valueOf(hourOfDay);
						String min = minute > 10 ? String.valueOf(minute) : "0" + String.valueOf(minute);
						binding.tvEnd.setText(hr + ":" + min);
					},
					now.getHours(), now.getMinutes(), false);
			dialog.show();
		});

		binding.btnSave.setOnClickListener(v -> getActivity().onBackPressed());

		return binding.getRoot();
	}
}

package finotek.global.dev.talkbank_ca.widget;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.databinding.RowKeypadBinding;

/**
 * Created by magyeong-ug on 13/03/2017.
 */

public class SecureKeyboardAdapter extends BaseAdapter {


	private Context context;
	private List<String> keySeq;
	private OnBackPressListener onBackPressListener;


	public SecureKeyboardAdapter(Context context, List<String> keySeq) {
		this.context = context;
		this.keySeq = keySeq;
	}

	@Override
	public int getCount() {
		return keySeq.size();
	}

	@Override
	public Object getItem(int position) {
		return keySeq.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setOnBackPressListener(OnBackPressListener onBackPressListener) {
		this.onBackPressListener = onBackPressListener;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		RowKeypadBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context),
				R.layout.row_keypad, parent, false);
		if (position == keySeq.size() - 1) {
			binding.llWrapper.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
			binding.tvKey.setTextColor(ContextCompat.getColor(context, R.color.white));
			binding.tvKey.setTypeface(Typeface.DEFAULT);

			binding.setKey("등록");
		} else if (position == 7) {
			binding.tvKey.setVisibility(View.INVISIBLE);
			binding.ibBack.setVisibility(View.VISIBLE);
			binding.ibBack.setOnClickListener(v -> onBackPressListener.onBackPress());
		} else {
			String key = (String) getItem(position);
			binding.setKey(key);
		}

		return binding.getRoot();
	}

	public interface OnBackPressListener {
		void onBackPress();
	}
}

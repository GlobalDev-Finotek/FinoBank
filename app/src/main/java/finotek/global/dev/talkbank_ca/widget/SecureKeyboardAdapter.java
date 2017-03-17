package finotek.global.dev.talkbank_ca.widget;

import android.content.Context;
import android.databinding.DataBindingUtil;
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		RowKeypadBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context),
				R.layout.row_keypad, parent, false);

		if (position == keySeq.size()- 1) {

			binding.tvKey.setBackgroundResource(R.drawable.ic_backspace_black_24dp);

		} else {
			String key = (String) getItem(position);
			binding.setKey(key);
		}


		return binding.getRoot();
	}
}

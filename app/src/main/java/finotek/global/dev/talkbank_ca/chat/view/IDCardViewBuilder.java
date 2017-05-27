package finotek.global.dev.talkbank_ca.chat.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.media.ExifInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import java.io.IOException;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.messages.ui.IDCardInfo;
import finotek.global.dev.talkbank_ca.databinding.ChatIdCardBinding;
import finotek.global.dev.talkbank_ca.util.Converter;
import finotek.global.dev.talkbank_ca.util.MyTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class IDCardViewBuilder implements ChatView.ViewBuilder<IDCardInfo> {
	private final Context context;

	public IDCardViewBuilder(Context context) {
		this.context = context;
	}

	@Override
	public RecyclerView.ViewHolder build(ViewGroup parent) {
		return new IDCardViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_id_card, parent, false));
	}

	@Override
	public void bind(RecyclerView.ViewHolder viewHolder, IDCardInfo data) {
		IDCardViewHolder holder = (IDCardViewHolder) viewHolder;

		try {

			String imgPath = data.getImgPath();
			ExifInterface exifInterface = new ExifInterface(imgPath);
			int orientation = Integer.parseInt(exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION));
			holder.binding.idCardInfo.setVisibility(View.GONE);

			if (!TextUtils.isEmpty(imgPath)) {
				holder.binding.idCardInfo.setVisibility(View.GONE);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						Converter.dpToPx(200), Converter.dpToPx(240));
				holder.binding.idCardImg.setLayoutParams(lp);
				holder.binding.idCardImg.setScaleType(ImageView.ScaleType.FIT_START);

				Glide.with(context)
						.load(imgPath)
						.bitmapTransform(new RoundedCornersTransformation(context, 40, 0, RoundedCornersTransformation.CornerType.ALL))
						.into(holder.binding.idCardImg);

			} else {

				holder.binding.idCardInfo.setVisibility(View.VISIBLE);
				holder.binding.cardType.setText(data.getType());
				holder.binding.name.setText(data.getName());
				holder.binding.jumin.setText(data.getJumin());
				holder.binding.issueDate.setText(data.getIssueDate());


				holder.binding.idCardImg
						.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.id_card));
			}


		} catch (IOException e) {
			e.printStackTrace();
		}



	}

	@Override
	public void onDelete() {

	}

	private class IDCardViewHolder extends RecyclerView.ViewHolder {
		ChatIdCardBinding binding;

		public IDCardViewHolder(View itemView) {
			super(itemView);
			binding = DataBindingUtil.bind(itemView);
		}
	}
}

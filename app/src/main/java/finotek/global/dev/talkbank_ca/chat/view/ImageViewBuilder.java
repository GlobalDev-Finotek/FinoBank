package finotek.global.dev.talkbank_ca.chat.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.messages.ImageMessage;
import finotek.global.dev.talkbank_ca.databinding.ChatImageBinding;
import finotek.global.dev.talkbank_ca.setting.PageType;

/**
 * Created by magyeong-ug on 04/07/2017.
 */

public class ImageViewBuilder implements ChatView.ViewBuilder<ImageMessage> {
	private Context context;

	public ImageViewBuilder(Context context) {
		this.context = context;
	}

	@Override
	public RecyclerView.ViewHolder build(ViewGroup parent) {
		return new ImageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_image, parent, false));
	}

	@Override
	public void bind(RecyclerView.ViewHolder viewHolder, ImageMessage data) {
		ImageViewHolder holder = (ImageViewHolder) viewHolder;

		Integer imgPath = data.getImgPath();

		if (imgPath == null) {
			if(data.getStringImagePath() != null && !data.getStringImagePath().isEmpty()) {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inPreferredConfig = Bitmap.Config.ARGB_8888;

				String path = data.getStringImagePath();
				Bitmap signedContract = BitmapFactory.decodeFile(path, options).copy(Bitmap.Config.ARGB_8888, true);

				holder.binding.chatIvImg.setImageBitmap(signedContract);
			} else {
				holder.binding.chatIvImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.id_card));
			}
		} else {
			holder.binding.chatIvImg.setImageDrawable(ContextCompat.getDrawable(context, imgPath));
		}
	}

	@Override
	public void onDelete() {

	}

	private class ImageViewHolder extends RecyclerView.ViewHolder {
		ChatImageBinding binding;

		public ImageViewHolder(View itemView) {
			super(itemView);
			binding = DataBindingUtil.bind(itemView);
		}
	}
}

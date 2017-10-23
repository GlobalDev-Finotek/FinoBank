package finotek.global.dev.talkbank_ca.chat.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.media.ExifInterface;
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
import finotek.global.dev.talkbank_ca.chat.messages.ImageMessage;
import finotek.global.dev.talkbank_ca.databinding.ChatImageViewBinding;
import finotek.global.dev.talkbank_ca.util.Converter;
import io.realm.Realm;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ImageViewBuilder implements ChatView.ViewBuilder<ImageMessage> {
    private final Context context;

    public ImageViewBuilder(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder build(ViewGroup parent) {
        return new ImageViewBuilder.ImageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_image_view, parent, false));
    }

    @Override
    public void bind(RecyclerView.ViewHolder viewHolder, ImageMessage data) {
        ImageViewBuilder.ImageViewHolder holder = (ImageViewBuilder.ImageViewHolder) viewHolder;
        Realm realm = Realm.getDefaultInstance();

        try {
            String imgPath = data.getPath();

            if (!TextUtils.isEmpty(imgPath)) {
                ExifInterface exifInterface = new ExifInterface(imgPath);
                int orientation = Integer.parseInt(exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION));

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        Converter.dpToPx(200), Converter.dpToPx(240));
                holder.binding.imageView.setLayoutParams(lp);
                holder.binding.imageView.setScaleType(ImageView.ScaleType.FIT_XY);

                Glide.with(context)
                        .load(imgPath)
                        .bitmapTransform(new RoundedCornersTransformation(context, 40, 0, RoundedCornersTransformation.CornerType.ALL))
                        .fitCenter()
                        .into(holder.binding.imageView);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDelete() {

    }

    private class ImageViewHolder extends RecyclerView.ViewHolder {
        ChatImageViewBinding binding;

        public ImageViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
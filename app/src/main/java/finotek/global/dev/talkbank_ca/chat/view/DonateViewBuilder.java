package finotek.global.dev.talkbank_ca.chat.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.messages.control.DonateItem;
import finotek.global.dev.talkbank_ca.chat.messages.control.DonateRequest;
import finotek.global.dev.talkbank_ca.databinding.ChatDonateMenuBinding;
import finotek.global.dev.talkbank_ca.databinding.ChatRecommendedDescBinding;
import finotek.global.dev.talkbank_ca.databinding.ChatDonateViewBinding;

public class DonateViewBuilder implements ChatView.ViewBuilder<DonateRequest> {
    private Context context;

    public DonateViewBuilder(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder build(ViewGroup parent) {
        return new DonateViewBuilder.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_donate_view, parent, false));
    }

    @Override
    public void bind(RecyclerView.ViewHolder viewHolder, DonateRequest data) {
        addMenus((DonateViewBuilder.ViewHolder) viewHolder, data);
    }

    @Override
    public void onDelete() {

    }

    private void setDescription(DonateViewBuilder.ViewHolder holder, DonateRequest data) {
        ChatRecommendedDescBinding desc = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.chat_recommended_desc, holder.binding.menu, false);
        if (data.getTitle() == null || data.getTitle().isEmpty()) {
            desc.title.setVisibility(View.GONE);
        } else {
            desc.title.setText(data.getTitle());
        }

        desc.description.setText(data.getDescription());
        holder.binding.menu.addView(desc.getRoot());
    }

    private void addMenus(DonateViewBuilder.ViewHolder holder, DonateRequest data) {
        List<DonateItem> menus = data.getMenus();
        holder.binding.menu.removeAllViews();

        setDescription(holder, data);

        if (menus != null) {
            for (int i = 0; i < menus.size(); i++) {
                addMenu(holder, menus.get(i), i == menus.size() - 1, data);
            }
        }
    }

    private void addMenu(DonateViewBuilder.ViewHolder holder, DonateItem menu, boolean isLast, DonateRequest data) {
        ChatDonateMenuBinding btn = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.chat_donate_menu, holder.binding.menu, false);
        btn.image.setImageResource(menu.getImage());
        btn.textView.setText(menu.getMenu());

        if (isLast) {
            btn.main.setBackground(context.getDrawable(R.drawable.chat_reco_menu_bottom));
        }

        if (!data.isEnabled()) {
            btn.main.setClickable(false);
        } else {
            if (menu.getListener() != null) {
                RxView.clicks(btn.main)
                        .throttleFirst(300, TimeUnit.MILLISECONDS)
                        .subscribe(aVoid -> {
                            menu.getListener().run();

                            data.setEnabled(false);
                            addMenus(holder, data);
                        });
            }
        }

        holder.binding.menu.addView(btn.getRoot());
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        ChatDonateViewBinding binding;

        ViewHolder(View itemView) {
            super(itemView);
            binding = ChatDonateViewBinding.bind(itemView);
        }
    }
}
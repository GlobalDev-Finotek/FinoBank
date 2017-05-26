package finotek.global.dev.talkbank_ca.chat.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.messages.control.RecoMenu;
import finotek.global.dev.talkbank_ca.chat.messages.control.RecoMenuRequest;
import finotek.global.dev.talkbank_ca.databinding.ChatRecommendedButtonBinding;
import finotek.global.dev.talkbank_ca.databinding.ChatRecommendedDescBinding;
import finotek.global.dev.talkbank_ca.databinding.ChatRecommendedMenuBinding;

public class RecoMenuViewBuilder implements ChatView.ViewBuilder<RecoMenuRequest> {
    private Context context;

    public RecoMenuViewBuilder(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder build(ViewGroup parent) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_recommended_menu, parent, false));
    }

    @Override
    public void bind(RecyclerView.ViewHolder viewHolder, RecoMenuRequest data) {
        List<RecoMenu> menus = data.getMenus();
        ViewHolder holder = (ViewHolder) viewHolder;
        holder.binding.menu.removeAllViews();

        setDescription(viewHolder, data);

        for(int i = 0; i < menus.size(); i++) {
            addMenu(holder, menus.get(i), i == menus.size()-1);
        }
    }

    @Override
    public void onDelete() {

    }

    private void setDescription(RecyclerView.ViewHolder viewHolder, RecoMenuRequest data){
        ViewHolder holder = (ViewHolder) viewHolder;
        ChatRecommendedDescBinding desc = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.chat_recommended_desc, holder.binding.menu, false);
        if(data.getTitle().isEmpty()) {
            desc.title.setVisibility(View.GONE);
        } else {
            desc.title.setText(data.getTitle());
        }

        desc.description.setText(data.getDescription());
        holder.binding.menu.addView(desc.getRoot());
    }

    private void addMenu(ViewHolder holder, RecoMenu menu, boolean isLast){
        ChatRecommendedButtonBinding btn = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.chat_recommended_button, holder.binding.menu, false);
        btn.icon.setImageResource(menu.getIcon());
        btn.textView.setText(menu.getMenuName());

        if(isLast) {
            btn.main.setBackground(context.getDrawable(R.drawable.chat_reco_menu_bottom));
        }

        if(menu.getListener() != null){
            RxView.clicks(btn.main)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .subscribe(aVoid -> {
                    menu.getListener().run();
                });
        }

        holder.binding.menu.addView(btn.getRoot());
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        ChatRecommendedMenuBinding binding;

        ViewHolder(View itemView) {
            super(itemView);
            binding = ChatRecommendedMenuBinding.bind(itemView);
        }
    }
}

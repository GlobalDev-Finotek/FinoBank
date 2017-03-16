package finotek.global.dev.talkbank_ca.chat.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ViewGroup;

import finotek.global.dev.talkbank_ca.chat.adapter.ChatAdapter;
import finotek.global.dev.talkbank_ca.chat.adapter.ChatItem;
import finotek.global.dev.talkbank_ca.chat.adapter.ChatItemWithType;

public class ChatView extends RecyclerView {
    private ChatAdapter adapter;

    public interface ViewBuilder<T> {
        RecyclerView.ViewHolder build(ViewGroup parent);
        void bind(RecyclerView.ViewHolder viewHolder, T data);
    }

    public ChatView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.adapter = new ChatAdapter();
        setAdapter(adapter);
    }

    public void addChatViewBuilder(int viewType, ViewBuilder builder) {
        adapter.addChatViewBuilder(viewType, builder);
    }

    public void addMessage(int viewType, ChatItem item) {
        adapter.addChatItem(new ChatItemWithType(viewType, item));
    }
}
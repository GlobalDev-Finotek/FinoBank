package finotek.global.dev.talkbank_ca.chat.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ViewGroup;

import finotek.global.dev.talkbank_ca.chat.adapter.ChatAdapter;
import finotek.global.dev.talkbank_ca.chat.adapter.DataWithType;
import finotek.global.dev.talkbank_ca.chat.adapter.ChatMessage;
import finotek.global.dev.talkbank_ca.chat.adapter.ChatSelectButtonEvent;

/**
 * @author david lee at finotek.
 * */
public class ChatView extends RecyclerView {
    private ChatAdapter adapter;
    enum ViewType {
        Send, Receive, Divider, Status, Confirm
    }

    public interface ViewBuilder<T> {
        RecyclerView.ViewHolder build(ViewGroup parent);
        void bind(RecyclerView.ViewHolder viewHolder, T data);
    }

    public ChatView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.adapter = new ChatAdapter();
        setAdapter(adapter);

        this.addChatViewBuilder(ViewType.Send.ordinal(), new SendViewBuilder());
        this.addChatViewBuilder(ViewType.Receive.ordinal(), new ReceiveViewBuilder());
        this.addChatViewBuilder(ViewType.Status.ordinal(), new StatusViewBuilder());
        this.addChatViewBuilder(ViewType.Divider.ordinal(), new DividerViewBuilder());
        this.addChatViewBuilder(ViewType.Confirm.ordinal(), new ConfirmViewBuilder());
    }

    public void sendMessage(String msg) {
        addMessage(ViewType.Send.ordinal(), new ChatMessage(msg));
    }

    public void receiveMessage(String msg) {
        addMessage(ViewType.Receive.ordinal(), new ChatMessage(msg));
    }

    public void statusMessage(String msg) {
        addMessage(ViewType.Status.ordinal(), new ChatMessage(msg));
    }

    public void dividerMessage(String msg) {
        addMessage(ViewType.Divider.ordinal(), new ChatMessage(msg));
    }

    public void confirm(ChatSelectButtonEvent ev){
        addMessage(ViewType.Confirm.ordinal(), ev);
    }

    private void addChatViewBuilder(int viewType, ViewBuilder builder) {
        adapter.addChatViewBuilder(viewType, builder);
    }

    private void addMessage(int viewType, Object item) {
        adapter.addChatItem(new DataWithType(viewType, item));
        scrollToBottom();
    }

    public void removeAt(int pos) {
        adapter.removeItem(pos);
        scrollToBottom();
    }

    public void removeLast() {
        adapter.removeItem(adapter.getItemCount() - 1);
    }

    public void scrollToBottom() {
        getLayoutManager().scrollToPosition(adapter.getItemCount() - 1);
    }
}
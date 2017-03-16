package finotek.global.dev.talkbank_ca.chat.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.ViewGroup;
import java.util.ArrayList;
import finotek.global.dev.talkbank_ca.chat.view.ChatView;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private SparseArray<ChatView.ViewBuilder> builders;
    private ArrayList<ChatItemWithType> items;

    public ChatAdapter() {
        builders = new SparseArray<>();
        items = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ChatView.ViewBuilder builder = builders.get(viewType);
        if(builder != null)
            return builder.build(parent);

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatView.ViewBuilder builder = builders.get(getItemViewType(position));
        if(builder != null) {
            builder.bind(holder, items.get(position).getItem());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getViewType();
    }

    public void addChatViewBuilder(int viewType, ChatView.ViewBuilder builder) {
        builders.put(viewType, builder);
    }

    public void addChatItem(ChatItemWithType item){
        items.add(item);
    }
}
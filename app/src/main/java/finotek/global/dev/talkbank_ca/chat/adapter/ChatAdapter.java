package finotek.global.dev.talkbank_ca.chat.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.model.ChatElement;
import finotek.global.dev.talkbank_ca.model.ChatElementDivider;
import finotek.global.dev.talkbank_ca.model.ChatElementReceive;
import finotek.global.dev.talkbank_ca.model.ChatElementSend;
import finotek.global.dev.talkbank_ca.model.ChatElementStatus;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int ELEMENT_DIVIDER   = 0;
    private final int ELEMENT_STATUS    = 1;
    private final int ELEMENT_SEND      = 2;
    private final int ELEMENT_RECEIVE   = 3;
    private ChatElement[] chatElements;

    public ChatAdapter(ChatElement[] chatElements) {
        this.chatElements = chatElements;
    }

    @Override
    public int getItemCount() {
        return chatElements.length;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == ELEMENT_SEND) {
            RelativeLayout view = (RelativeLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_send, parent, false);
            return new SenderViewHolder(view);
        }

        if(viewType == ELEMENT_RECEIVE) {
            RelativeLayout view = (RelativeLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_receive, parent, false);
            return new ReceiverViewHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof SenderViewHolder)
            bindSend((SenderViewHolder) holder, (ChatElementSend) chatElements[position], position);

        if(holder instanceof ReceiverViewHolder)
            bindReceive((ReceiverViewHolder) holder, (ChatElementReceive) chatElements[position], position);
    }

    private void bindSend(SenderViewHolder holder, ChatElementSend element, int position){
        holder.setName(element.getName());
        holder.setMessage(element.getMessage());
    }

    private void bindReceive(ReceiverViewHolder holder, ChatElementReceive element, int position) {
        holder.setName(element.getName());
        holder.setMessage(element.getMessage());
    }

    @Override
    public int getItemViewType(int position) {
        ChatElement element = chatElements[position];

        if(element instanceof ChatElementDivider)
            return ELEMENT_DIVIDER;

        if(element instanceof ChatElementReceive)
            return ELEMENT_RECEIVE;

        if(element instanceof ChatElementSend)
            return ELEMENT_SEND;

        if(element instanceof ChatElementStatus)
            return ELEMENT_STATUS;

        return -1;
    }

    public static class SenderViewHolder extends RecyclerView.ViewHolder {
        private TextView nameText;
        private TextView messageText;

        public SenderViewHolder(RelativeLayout view) {
            super(view);
            nameText = (TextView) view.findViewById(R.id.name);
            messageText = (TextView) view.findViewById(R.id.message);
        }

        public void setName(String name){
            nameText.setText(name);
        }

        public void setMessage(String message){
            messageText.setText(message);
        }
    }

    public static class ReceiverViewHolder extends RecyclerView.ViewHolder {
        private TextView nameText;
        private TextView messageText;

        public ReceiverViewHolder(RelativeLayout view) {
            super(view);
            nameText = (TextView) view.findViewById(R.id.name);
            messageText = (TextView) view.findViewById(R.id.message);
        }

        public void setName(String name){
            nameText.setText(name);
        }

        public void setMessage(String message){
            messageText.setText(message);
        }
    }
}
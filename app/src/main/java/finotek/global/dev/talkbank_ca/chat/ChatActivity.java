package finotek.global.dev.talkbank_ca.chat;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.view.ReceiveViewBuilder;
import finotek.global.dev.talkbank_ca.chat.view.SendViewBuilder;
import finotek.global.dev.talkbank_ca.databinding.ActivityChatBinding;
import finotek.global.dev.talkbank_ca.model.ChatItemReceive;
import finotek.global.dev.talkbank_ca.model.ChatItemSend;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding binding;
    private enum ViewType {
        Send, Receive, Divider, Status
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setStackFromEnd(true);
        binding.chatView.setLayoutManager(manager);

        // set chat view builders
        binding.chatView.addChatViewBuilder(ViewType.Send.ordinal(), new SendViewBuilder());
        binding.chatView.addChatViewBuilder(ViewType.Receive.ordinal(), new ReceiveViewBuilder());

        // add default items
        binding.chatView.addMessage(ViewType.Receive.ordinal(), new ChatItemReceive("Hi."));
        binding.chatView.addMessage(ViewType.Receive.ordinal(), new ChatItemReceive("David"));
        binding.chatView.addMessage(ViewType.Send.ordinal(), new ChatItemSend("Good"));
    }
}
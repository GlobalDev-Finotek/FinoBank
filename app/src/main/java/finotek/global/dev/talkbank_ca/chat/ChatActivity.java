package finotek.global.dev.talkbank_ca.chat;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.adapter.ChatAdapter;
import finotek.global.dev.talkbank_ca.databinding.ActivityChatBinding;
import finotek.global.dev.talkbank_ca.model.ChatElement;
import finotek.global.dev.talkbank_ca.model.ChatElementReceive;
import finotek.global.dev.talkbank_ca.model.ChatElementSend;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setStackFromEnd(true);
        binding.chatList.setLayoutManager(manager);

        ChatElement[] elements = new ChatElement[]{
            new ChatElementReceive("System", "Hi david."),
            new ChatElementReceive("System", "It's me"),
            new ChatElementSend("Me", "good")
        };

        ChatAdapter adapter = new ChatAdapter(elements);
        binding.chatList.setAdapter(adapter);
    }
}
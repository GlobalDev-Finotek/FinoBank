package finotek.global.dev.talkbank_ca.chat;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.view.ReceiveViewBuilder;
import finotek.global.dev.talkbank_ca.chat.view.SendViewBuilder;
import finotek.global.dev.talkbank_ca.chat.view.StatusViewBuilder;
import finotek.global.dev.talkbank_ca.databinding.ActivityChatBinding;
import finotek.global.dev.talkbank_ca.model.ChatMessage;

public class ChatActivity extends AppCompatActivity {
    private boolean isSendEnabled = false;
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
        binding.chatView.addChatViewBuilder(ViewType.Status.ordinal(), new StatusViewBuilder());

        // add default items
        binding.chatView.addMessage(ViewType.Status.ordinal(), new ChatMessage("맥락 데이터 분석 결과 87% 확률로 인증되었습니다."));
        binding.chatView.addMessage(ViewType.Receive.ordinal(), new ChatMessage("홍길동님 안녕하세요. 무엇을 도와드릴까요?"));
    }
}
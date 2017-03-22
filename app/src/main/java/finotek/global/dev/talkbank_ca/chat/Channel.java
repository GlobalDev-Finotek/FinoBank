package finotek.global.dev.talkbank_ca.chat;

import finotek.global.dev.talkbank_ca.chat.view.ChatView;
import finotek.global.dev.talkbank_ca.chat.view.DividerViewBuilder;
import finotek.global.dev.talkbank_ca.chat.view.ReceiveViewBuilder;
import finotek.global.dev.talkbank_ca.chat.view.SendViewBuilder;
import finotek.global.dev.talkbank_ca.chat.view.StatusViewBuilder;
import finotek.global.dev.talkbank_ca.model.ChatMessage;
import finotek.global.dev.talkbank_ca.util.DateUtil;

class Channel {
    private ChatView chatView;
    private enum ViewType {
        Send, Receive, Divider, Status
    }

    Channel(ChatView chatView) {
        this.chatView = chatView;

        chatView.addChatViewBuilder(ViewType.Send.ordinal(), new SendViewBuilder());
        chatView.addChatViewBuilder(ViewType.Receive.ordinal(), new ReceiveViewBuilder());
        chatView.addChatViewBuilder(ViewType.Status.ordinal(), new StatusViewBuilder());
        chatView.addChatViewBuilder(ViewType.Divider.ordinal(), new DividerViewBuilder());

        chatView.addMessage(ViewType.Divider.ordinal(), new ChatMessage(DateUtil.currentDate()));
        chatView.addMessage(ViewType.Status.ordinal(), new ChatMessage("맥락 데이터 분석 결과 87% 확률로 인증되었습니다."));
    }

    void sendMessage(String msg) {
        chatView.addMessage(ViewType.Send.ordinal(), new ChatMessage(msg));

        switch (msg) {
            case "계좌 개설":
                createAccount();
                break;
            default:
                receiveMessage("무슨 말씀인지 잘 모르겠어요.");
                break;
        }
    }

    void sendDividerMessage(String msg) {
        chatView.addMessage(ViewType.Divider.ordinal(), new ChatMessage(msg));
    }

    void sendStatusMessage(String msg) {
        chatView.addMessage(ViewType.Status.ordinal(), new ChatMessage(msg));
    }

    void receiveMessage(String msg) {
        chatView.addMessage(ViewType.Receive.ordinal(), new ChatMessage(msg));
    }

    private void createAccount() {
        receiveMessage("홍길동님께는 `스마트 계좌`를 추천드립니다\n계좌 개설을 진행하시겠습니까?");
    }
}
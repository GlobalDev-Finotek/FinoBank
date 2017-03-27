package finotek.global.dev.talkbank_ca.chat;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

import finotek.global.dev.talkbank_ca.chat.messages.AgreementResult;
import finotek.global.dev.talkbank_ca.chat.messages.ConfirmRequest;
import finotek.global.dev.talkbank_ca.chat.messages.DividerMessage;
import finotek.global.dev.talkbank_ca.chat.messages.IDCardInfo;
import finotek.global.dev.talkbank_ca.chat.messages.ReceiveMessage;
import finotek.global.dev.talkbank_ca.chat.messages.RequestTakeIDCard;
import finotek.global.dev.talkbank_ca.chat.messages.SendMessage;
import finotek.global.dev.talkbank_ca.chat.messages.StatusMessage;
import finotek.global.dev.talkbank_ca.chat.view.ChatView;
import finotek.global.dev.talkbank_ca.chat.adapter.ChatSelectButtonEvent;
import finotek.global.dev.talkbank_ca.util.DateUtil;

class Scenario {
    private Context context;
    private ChatView chatView;
    private final MessageBox messageBox;
    private int step = 0;
    private int lastRequestIndex = -1;

    Scenario(Context context, ChatView chatView, MessageBox messageBox) {
        this.context = context;
        this.chatView = chatView;
        this.messageBox = messageBox;
        messageBox.getObservable().subscribe(this::onNewMessage);

        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setStackFromEnd(true);
        chatView.setLayoutManager(manager);

        messageBox.add(new DividerMessage(DateUtil.currentDate()));
        messageBox.add(new StatusMessage("맥락 데이터 분석 결과 87% 확률로 인증되었습니다."));
        messageBox.add(new ReceiveMessage("홍길동님 안녕하세요. 무엇을 도와드릴까요?"));
    }

    private void onNewMessage(Object msg){
        if(msg instanceof SendMessage) {
            SendMessage recv = (SendMessage) msg;
            chatView.sendMessage(recv.getMessage());
            this.respondTo(recv.getMessage());
        }

        if(msg instanceof ReceiveMessage) {
            ReceiveMessage recv = (ReceiveMessage) msg;
            chatView.receiveMessage(recv.getMessage());
        }

        if(msg instanceof StatusMessage) {
            StatusMessage recv = (StatusMessage) msg;
            chatView.statusMessage(recv.getMessage());
        }

        if(msg instanceof DividerMessage) {
            DividerMessage recv = (DividerMessage) msg;
            chatView.dividerMessage(recv.getMessage());
        }

        if(msg instanceof ConfirmRequest) {
            lastRequestIndex = messageBox.size() -1;
            ChatSelectButtonEvent ev = new ChatSelectButtonEvent();
            ev.setConfirmAction(aVoid -> {
                messageBox.add(new SendMessage("예"));
            });
            ev.setCancelAction(aVoid -> {
                messageBox.add(new SendMessage("아니오"));
            });
            chatView.confirm(ev);
        }

        if(msg instanceof IDCardInfo) {
            chatView.showIdCardInfo((IDCardInfo) msg);
        }

        if(msg instanceof AgreementResult) {
            chatView.agreementResult();
            messageBox.add(new ReceiveMessage("대출 신청이 정상적으로 처리되어\n입금 완료 되었습니다.\n더 필요한 사항이 있으세요?"));
        }
    }

    private void respondTo(String msg) {
        if(lastRequestIndex != -1) {
            chatView.removeAt(lastRequestIndex);
            lastRequestIndex = -1;
        }

        switch (msg) {
            case "계좌 개설":
                createAccount();
                step = 1;
                break;
            case "네":
            case "예":
                if(step == 1) {
                    messageBox.add(new ReceiveMessage("계좌 개설 시 본인 확인 용도로 주민등록증이나\n운전면허증이 필요합니다.\n 준비가 되셨으면 신분증 촬영을 진행해 주세요."));
                    messageBox.add(new RequestTakeIDCard());
                }
                break;
            case "아니오":
                if(step == 1) {
                    messageBox.add(new ReceiveMessage("계좌 개설 진행을 취소했습니다."));
                    step = 0;
                }
                break;
            case "신분증":
                messageBox.add(new IDCardInfo("주민등록증", "홍길동", "931203-1155123", "2012.11.11"));
                break;
            case "약관" :
                break;
            case "약관확인" :
                messageBox.add(new AgreementResult());
                break;
            case "최근거래내역" :
                break;
            case "계좌이체" :
                break;
            default:
                messageBox.add(new ReceiveMessage("무슨 말씀인지 잘 모르겠어요."));
                break;
        }
    }

    private void createAccount() {
        messageBox.add(new ReceiveMessage("홍길동님께는 `스마트 계좌`를 추천드립니다\n계좌 개설을 진행하시겠습니까?"));
        messageBox.add(new ConfirmRequest());
    }
}
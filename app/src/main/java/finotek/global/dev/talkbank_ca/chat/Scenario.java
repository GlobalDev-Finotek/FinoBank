package finotek.global.dev.talkbank_ca.chat;

import android.util.Log;

import finotek.global.dev.talkbank_ca.chat.view.ChatView;
import finotek.global.dev.talkbank_ca.chat.adapter.ChatSelectButtonEvent;
import finotek.global.dev.talkbank_ca.util.DateUtil;
import rx.functions.Action1;

class Scenario {
    private ChatView chatView;
    private int step = 0;

    Scenario(ChatView chatView) {
        this.chatView = chatView;

        chatView.dividerMessage(DateUtil.currentDate());
        chatView.statusMessage("맥락 데이터 분석 결과 87% 확률로 인증되었습니다.");
        chatView.receiveMessage("홍길동님 안녕하세요. 무엇을 도와드릴까요?");
    }

    void sendMessage(String msg) {
        chatView.sendMessage(msg);

        switch (msg) {
            case "계좌 개설":
                createAccount();
                step = 1;
                break;
            case "네":
                if(step == 1) {
                    chatView.receiveMessage("계좌 개설 시 본인 확인 용도로 주민등록증이나\n운전면허증이 필요합니다.\n 준비가 되셨으면 신분증 촬영을 진행해 주세요.");
                }
                break;
            default:
                chatView.receiveMessage("무슨 말씀인지 잘 모르겠어요.");
                break;
        }
    }

    private void createAccount() {
        chatView.receiveMessage("홍길동님께는 `스마트 계좌`를 추천드립니다\n계좌 개설을 진행하시겠습니까?");

        ChatSelectButtonEvent ev = new ChatSelectButtonEvent();
        ev.setConfirmAction(new Action1<Void>(){
            @Override
            public void call(Void aVoid) {
                Log.d("FINOTEK", "Test");
            }
        });
        ev.setCancelAction(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                Log.d("FINOTEK", "Test1");
            }
        });
        chatView.confirm(ev);
    }
}
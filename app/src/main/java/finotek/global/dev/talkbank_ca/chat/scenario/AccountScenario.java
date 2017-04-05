package finotek.global.dev.talkbank_ca.chat.scenario;

import finotek.global.dev.talkbank_ca.chat.MessageBox;
import finotek.global.dev.talkbank_ca.chat.messages.control.ConfirmRequest;
import finotek.global.dev.talkbank_ca.chat.messages.Done;
import finotek.global.dev.talkbank_ca.chat.messages.ReceiveMessage;
import finotek.global.dev.talkbank_ca.chat.messages.RequestSignature;
import finotek.global.dev.talkbank_ca.chat.messages.RequestTakeIDCard;

public class AccountScenario implements Scenario {
    private enum Step {
        Initial, CheckIDCard, TakeSign, Last
    }
    private Step step = Step.Initial;

    @Override
    public boolean decideOn(String msg) {
        return msg.equals("계좌개설") || msg.equals("계좌 개설");
    }

    @Override
    public void onReceive(Object msg) {
        if(msg instanceof Done) {
            if(step == Step.Last) {
                MessageBox.INSTANCE.add(new ReceiveMessage("계좌개설이 완료 되었습니다.\n더 필요한 사항이 있으세요?"));
            }

            step = Step.Initial;
        }
    }

    @Override
    public void onUserSend(String msg) {
        switch (step) {
            case Initial:
                MessageBox.INSTANCE.add(new ReceiveMessage("홍길동님께는 `스마트 계좌`를 추천드립니다\n계좌 개설을 진행하시겠습니까?"));
                MessageBox.INSTANCE.add(ConfirmRequest.buildYesOrNo()); // 네, 아니오
                step = Step.CheckIDCard;
                break;
            case CheckIDCard:
                if(msg.equals("네")) {
                    MessageBox.INSTANCE.add(new ReceiveMessage("계좌 개설 시 본인 확인 용도로 주민등록증이나\n운전면허증이 필요합니다.\n 준비가 되셨으면 신분증 촬영을 진행해 주세요."));
                    MessageBox.INSTANCE.add(new RequestTakeIDCard());
                    step = Step.TakeSign;
                } else if(msg.equals("아니오")) {
                    MessageBox.INSTANCE.add(new ReceiveMessage("계좌 개설 진행을 취소했습니다."));
                    MessageBox.INSTANCE.add(new Done());
                } else {
                    MessageBox.INSTANCE.add(new ReceiveMessage("무슨 말씀인지 잘 모르겠습니다."));
                }
                break;
            // 본인이 맞으세요?
            case TakeSign:
                if(msg.equals("네")) {
                    MessageBox.INSTANCE.add(new ReceiveMessage("마지막으로 사용자 등록 시 입력한 자필 서명을\n표시된 영역 안에 손톱이 아닌 손가락 끝을 사용하여\n서명해 주세요."));
                    MessageBox.INSTANCE.add(new RequestSignature());
                    step = Step.Last;
                } else if(msg.equals("아니오")){
                    MessageBox.INSTANCE.add(new ReceiveMessage("계좌 개설을 취소합니다."));
                    MessageBox.INSTANCE.add(new Done());
                } else {
                    MessageBox.INSTANCE.add(new ReceiveMessage("무슨 말씀인지 잘 모르겠습니다."));
                }
                break;
        }
    }
}

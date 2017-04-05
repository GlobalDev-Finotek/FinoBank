package finotek.global.dev.talkbank_ca.chat.scenario;

import java.util.ArrayList;
import java.util.List;

import finotek.global.dev.talkbank_ca.chat.MessageBox;
import finotek.global.dev.talkbank_ca.chat.messages.Agreement;
import finotek.global.dev.talkbank_ca.chat.messages.AgreementRequest;
import finotek.global.dev.talkbank_ca.chat.messages.AgreementResult;
import finotek.global.dev.talkbank_ca.chat.messages.ConfirmRequest;
import finotek.global.dev.talkbank_ca.chat.messages.Done;
import finotek.global.dev.talkbank_ca.chat.messages.ReceiveMessage;

public class LoanScenario implements Scenario {
    private enum Step {
        Initial, InputAddress, InputMoney, Agreement, Last
    }

    private Step step = Step.Initial;

    @Override
    public boolean decideOn(String msg) {
        switch(msg) {
            case "집을 담보로 대출 받고 싶어":
            case "소액담보대출":
            case "소액 담보 대출":
                return true;
            default:
                return false;
        }
    }

    // SendMessage를 제외한 모든 메시지를 받음
    @Override
    public void onReceive(Object msg) {
        if(msg instanceof Done) {
            if(step == Step.Last) {
                MessageBox.INSTANCE.add(new AgreementResult());
                MessageBox.INSTANCE.add(new ReceiveMessage("대출 신청이 정상적으로 처리되어\n입금 완료 되었습니다.\n\n더 필요한 사항이 있으세요?"));
            }

            step = Step.Initial;
        }
    }

    // 사용자가 톡뱅에게 메시지를 보낼 때 호출되는 이벤트
    @Override
    public void onUserSend(String msg) {
        switch(step){
            case Initial:
                MessageBox.INSTANCE.add(new ReceiveMessage("거래 내역 확인 결과, “원데이 대출”을 추천합니다.\n최대 5천만 원 이내, 최저 금리 2.0%입니다.\n\n대출을 신청하시겠습니까?"));
                MessageBox.INSTANCE.add(new ConfirmRequest());
                step = Step.InputAddress;
                break;
            case InputAddress:
                if(msg.equals("네")) {
                    MessageBox.INSTANCE.add(new ReceiveMessage("집 주소를 입력해 주세요."));
                    step = Step.InputMoney;
                } else if(msg.equals("아니오")){
                    MessageBox.INSTANCE.add(new ReceiveMessage("소액담보대출 진행을 취소했습니다."));
                    MessageBox.INSTANCE.add(new Done());
                } else {
                    MessageBox.INSTANCE.add(new ReceiveMessage("무슨 의미 인가요?"));
                }
                break;
            case InputMoney:
                MessageBox.INSTANCE.add(new ReceiveMessage("입력하신 주소로 담보 설정 시 최대 5천만 원까지\n2.0%의 금리로 대출 가능합니다.\n\n대출 신청 금액을 입력해 주세요."));
                step = Step.Agreement;
                break;
            case Agreement:
                MessageBox.INSTANCE.add(new ReceiveMessage("약관 확인 및 동의를 진행해주세요."));

                List<Agreement> agreements = new ArrayList<>();
                Agreement required = new Agreement(100, "필수 항목 전체 동의");
                required.addChild(new Agreement(101, "대출 서비스 이용 약관"));
                required.addChild(new Agreement(102, "개인(신용)정보 조회 및 이용 제공 동의"));
                required.addChild(new Agreement(103, "대출거래 약정서"));
                required.addChild(new Agreement(104, "계약 안내사항 및 유의사항"));

                Agreement optional = new Agreement(200, "선택항목 전체 동의");
                optional.addChild(new Agreement(201, "고객 정보 활용 동의"));

                agreements.add(required);
                agreements.add(optional);

                MessageBox.INSTANCE.add(new AgreementRequest(agreements));
                step = Step.Last;
                break;
            default:
                MessageBox.INSTANCE.add(new ReceiveMessage("무슨 의미 인가요?"));
                break;
        }
    }
}

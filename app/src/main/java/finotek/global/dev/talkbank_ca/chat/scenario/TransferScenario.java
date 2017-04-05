package finotek.global.dev.talkbank_ca.chat.scenario;

import java.util.ArrayList;
import java.util.List;

import finotek.global.dev.talkbank_ca.chat.MessageBox;
import finotek.global.dev.talkbank_ca.chat.messages.Account;
import finotek.global.dev.talkbank_ca.chat.messages.AccountList;
import finotek.global.dev.talkbank_ca.chat.messages.ReceiveMessage;
import finotek.global.dev.talkbank_ca.chat.messages.Done;
import finotek.global.dev.talkbank_ca.chat.messages.transfer.RequestTransfer;
import finotek.global.dev.talkbank_ca.chat.messages.transfer.TransferConfirmRequest;

public class TransferScenario implements Scenario {
    private enum Step {
        Initial, Analyzing, SelectAccount
    }

    private Step step = Step.Initial;

    @Override
    public boolean decideOn(String msg) {
        return msg.equals("계좌이체") || msg.equals("계좌 이체");
    }

    @Override
    public void onReceive(Object msg) {
        if(msg instanceof Done) {
            if(step == Step.SelectAccount) {
                MessageBox.INSTANCE.add(new ReceiveMessage("어머니(010-5678-1234)님에게 100,000원을\n이체하였습니다.\n현재 계좌 잔액은 3,270,000원입니다.\n\n더 필요한 사항이 있으세요?"));
            }

            step = Step.Initial;
        }
    }

    @Override
    public void onUserSend(String msg) {
        switch (step) {
            case Initial:
                MessageBox.INSTANCE.add(new ReceiveMessage("월별 입출금 거래 내역 확인 결과,\n김가람(010-5678-1234)님에게 100,000원을\n이체하시겠어요?"));
                MessageBox.INSTANCE.add(new TransferConfirmRequest());
                step = Step.Analyzing;
                break;
            case Analyzing:
                if(msg.equals("네") || msg.equals("예")) {
                    MessageBox.INSTANCE.add(new ReceiveMessage("김가람(010-5678-1234)님에게 100,000원을\n이체하였습니다.\n현재 계좌 잔액은 3,270,000원입니다.\n\n더 필요한 사항이 있으세요?"));
                    MessageBox.INSTANCE.add(new Done());
                } else if(msg.equals("아니오")) {
                    MessageBox.INSTANCE.add(new ReceiveMessage("계좌이체를 취소했습니다."));
                    MessageBox.INSTANCE.add(new Done());
                } else if(msg.equals("다른 사람에게 이체")) {
                    List<Account> accounts = new ArrayList<>();
                    accounts.add(new Account("어머니", "2017년 01월 25일", "200,000원 이체", true));
                    accounts.add(new Account("박예린", "2017년 01월 11일", "100,000원 이체", false));
                    accounts.add(new Account("김가람", "2017년 01월 11일", "36,200원 이체", false));
                    accounts.add(new Account("김이솔", "2017년 01월 10일", "100,000원 입금", false));

                    MessageBox.INSTANCE.add(new ReceiveMessage("이체하실 분을 선택해 주세요."));
                    MessageBox.INSTANCE.add(new AccountList(accounts));
                    MessageBox.INSTANCE.add(new RequestTransfer());

                    step = Step.SelectAccount;
                }
                break;
        }
    }
}

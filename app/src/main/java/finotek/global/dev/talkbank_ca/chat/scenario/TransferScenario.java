package finotek.global.dev.talkbank_ca.chat.scenario;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.app.MyApplication;
import finotek.global.dev.talkbank_ca.chat.MessageBox;
import finotek.global.dev.talkbank_ca.chat.messages.Account;
import finotek.global.dev.talkbank_ca.chat.messages.AccountList;
import finotek.global.dev.talkbank_ca.chat.messages.ReceiveMessage;
import finotek.global.dev.talkbank_ca.chat.messages.RecentTransaction;
import finotek.global.dev.talkbank_ca.chat.messages.action.Done;
import finotek.global.dev.talkbank_ca.chat.messages.action.SignatureVerified;
import finotek.global.dev.talkbank_ca.chat.messages.transfer.TransferButtonPressed;
import finotek.global.dev.talkbank_ca.chat.messages.ui.RequestRemoveControls;
import finotek.global.dev.talkbank_ca.chat.messages.ui.RequestSignature;
import finotek.global.dev.talkbank_ca.chat.messages.SendMessage;
import finotek.global.dev.talkbank_ca.chat.messages.control.ConfirmRequest;
import finotek.global.dev.talkbank_ca.chat.messages.transfer.RequestTransfer;
import finotek.global.dev.talkbank_ca.chat.storage.TransactionDB;

public class TransferScenario implements Scenario {
    private Context context = MyApplication.getContext();

    private enum Step {
        Initial, Analyzing, SelectAccount, TransferDone
    }

    private Step step = Step.Initial;

    @Override
    public boolean decideOn(String msg) {
        return msg.equals("계좌이체") || msg.equals(context.getResources().getString(R.string.dialog_button_transfer));
    }

    @Override
    public void onReceive(Object msg) {
        if(msg instanceof SignatureVerified) {
            if(step == Step.SelectAccount) {
                MessageBox.INSTANCE.add(new ReceiveMessage("어머니(010-5678-1234)님에게 100,000원을\n이체하였습니다.\n현재 계좌 잔액은 3,270,000원입니다.\n\n더 필요한 사항이 있으세요?"));
            } else if(step == Step.Analyzing){
                MessageBox.INSTANCE.add(new ReceiveMessage("김가람(010-5678-1234)님에게 100,000원을\n이체하였습니다.\n현재 계좌 잔액은 3,270,000원입니다.\n\n더 필요한 사항이 있으세요?"));
            }

            ConfirmRequest request = new ConfirmRequest();
            request.addInfoEvent(context.getResources().getString(R.string.dialog_button_recent_transaction), () -> {
                MessageBox.INSTANCE.add(new SendMessage(context.getResources().getString(R.string.dialog_button_recent_transaction)));
            });
            request.addPrimaryEvent(context.getResources().getString(R.string.dialog_button_transfer_add), () -> {
                MessageBox.INSTANCE.add(new SendMessage(context.getResources().getString(R.string.dialog_button_transfer_add)));
            });
            MessageBox.INSTANCE.add(request);

            step = Step.TransferDone;
        }

        if(msg instanceof TransferButtonPressed) {
            MessageBox.INSTANCE.add(new RequestRemoveControls());

            MessageBox.INSTANCE.add(new SendMessage("어머니(010-9876-5432)님에게\n200,000원을 이체"));
            MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_finger_tip_sign)));
            MessageBox.INSTANCE.add(new RequestSignature());
        }

        if(msg instanceof Done) {
            step = Step.Initial;
        }
    }

    @Override
    public void onUserSend(String msg) {
        switch (step) {
            case Initial:
                MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_transaction)));

                ConfirmRequest request = ConfirmRequest.buildYesOrNo();
                request.addInfoEvent(context.getResources().getString(R.string.dialog_button_transfer_other), () -> {
                    MessageBox.INSTANCE.add(new SendMessage(context.getResources().getString(R.string.dialog_button_transfer_other)));
                });
                MessageBox.INSTANCE.add(request);

                step = Step.Analyzing;
                break;
            case Analyzing:
                if(msg.equals(context.getResources().getString(R.string.dialog_button_yes))) {
                    MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_finger_tip_sign)));
                    MessageBox.INSTANCE.add(new RequestSignature());
                } else if(msg.equals(context.getResources().getString(R.string.dialog_button_no))) {
                    MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_transfer_cancel)));
                    MessageBox.INSTANCE.add(new Done());
                } else if(msg.equals(context.getResources().getString(R.string.dialog_button_transfer_other))) {
                    selectAccounts();
                    step = Step.SelectAccount;
                }
                break;
            case TransferDone:
                if(msg.equals(context.getResources().getString(R.string.dialog_button_recent_transaction))) {
                    MessageBox.INSTANCE.add(new ReceiveMessage("홍길동님의 최근 거래내역입니다."));
                    RecentTransaction rt = new RecentTransaction(TransactionDB.INSTANCE.getTx());
                    MessageBox.INSTANCE.add(rt);
                    MessageBox.INSTANCE.add(new Done());
                } else if(msg.equals(context.getResources().getString(R.string.dialog_button_transfer_add))) {
                    selectAccounts();
                    step = Step.SelectAccount;
                } else {
                    MessageBox.INSTANCE.add(new Done());
                }
                break;
        }
    }

    private void selectAccounts(){
        List<Account> accounts = new ArrayList<>();
        accounts.add(new Account("어머니", "2017년 01월 25일", "200,000원 이체", true));
        accounts.add(new Account("박예린", "2017년 01월 11일", "100,000원 이체", false));
        accounts.add(new Account("김가람", "2017년 01월 11일", "36,200원 이체", false));
        accounts.add(new Account("김이솔", "2017년 01월 10일", "100,000원 입금", false));

        MessageBox.INSTANCE.add(new ReceiveMessage("이체하실 분을 선택해 주세요."));
        MessageBox.INSTANCE.add(new AccountList(accounts));
        MessageBox.INSTANCE.add(new RequestTransfer());
    }
}

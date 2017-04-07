package finotek.global.dev.talkbank_ca.chat.scenario;

import android.content.Context;

import org.joda.time.DateTime;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.MessageBox;
import finotek.global.dev.talkbank_ca.chat.messages.Account;
import finotek.global.dev.talkbank_ca.chat.messages.AccountList;
import finotek.global.dev.talkbank_ca.chat.messages.ReceiveMessage;
import finotek.global.dev.talkbank_ca.chat.messages.RecentTransaction;
import finotek.global.dev.talkbank_ca.chat.messages.SendMessage;
import finotek.global.dev.talkbank_ca.chat.messages.Transaction;
import finotek.global.dev.talkbank_ca.chat.messages.action.Done;
import finotek.global.dev.talkbank_ca.chat.messages.action.SignatureVerified;
import finotek.global.dev.talkbank_ca.chat.messages.control.ConfirmRequest;
import finotek.global.dev.talkbank_ca.chat.messages.transfer.RequestTransfer;
import finotek.global.dev.talkbank_ca.chat.messages.transfer.TransferButtonPressed;
import finotek.global.dev.talkbank_ca.chat.messages.transfer.TransferToSomeone;
import finotek.global.dev.talkbank_ca.chat.messages.ui.RequestRemoveControls;
import finotek.global.dev.talkbank_ca.chat.messages.ui.RequestSignature;
import finotek.global.dev.talkbank_ca.chat.storage.TransactionDB;

public class TransferScenario implements Scenario {
    private Context context;
    private Step step = Step.Initial;

    public TransferScenario(Context context) {
        this.context = context;
    }

    @Override
    public boolean decideOn(String msg) {
        return msg.equals("계좌이체") || msg.equals(context.getResources().getString(R.string.dialog_button_transfer));
    }

    @Override
    public void onReceive(Object msg) {
        if(msg instanceof TransferToSomeone) {
            TransferToSomeone action = (TransferToSomeone) msg;
            String name = action.getName();
            String money = NumberFormat.getNumberInstance().format(action.getMoney());
            String message = context.getResources().getString(R.string.dialog_chat_transaction, money, name);
            MessageBox.INSTANCE.add(new ReceiveMessage(message));

            TransactionDB.INSTANCE.setTxName(name);
            TransactionDB.INSTANCE.setTxMoney(money);

            ConfirmRequest request = ConfirmRequest.buildYesOrNo(context);
            request.addInfoEvent(context.getResources().getString(R.string.dialog_button_transfer_other), () -> {
                MessageBox.INSTANCE.add(new SendMessage(context.getResources().getString(R.string.dialog_button_transfer_other)));
            });
            MessageBox.INSTANCE.add(request);

            step = Step.Analyzing;
        }

        if(msg instanceof SignatureVerified) {
            if(step == Step.SelectAccount) {
                String name = TransactionDB.INSTANCE.getTxName();
                String moneyAsString = TransactionDB.INSTANCE.getTxMoney();
                int money = Integer.valueOf(moneyAsString.replaceAll(",", ""));
                int balance = TransactionDB.INSTANCE.getBalance();
                String balanceAsString = NumberFormat.getNumberInstance().format(balance);

                MessageBox.INSTANCE.add(new ReceiveMessage(name + "(010-5678-1234)님에게 " + moneyAsString + "원을\n이체하였습니다.\n현재 계좌 잔액은 " + balanceAsString + "원입니다.\n\n더 필요한 사항이 있으세요?"));
                TransactionDB.INSTANCE.addTx(new Transaction(name, 0, money, balance, new DateTime()));
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

            String name = TransactionDB.INSTANCE.getTxName();
            String money = TransactionDB.INSTANCE.getTxMoney();

            MessageBox.INSTANCE.add(new SendMessage(name + "(010-9876-5432)님에게\n" + money + "원을 이체"));
            MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_finger_tip_sign)));
            MessageBox.INSTANCE.add(new RequestSignature());
        }

        if(msg instanceof Done) {
            TransactionDB.INSTANCE.setTxName("");
            TransactionDB.INSTANCE.setTxMoney("");

            step = Step.Initial;
        }
    }

    @Override
    public void onUserSend(String msg) {
        switch (step) {
            case Initial:
                MessageBox.INSTANCE.add(new TransferToSomeone("김가람", 100000));
                break;
            case Analyzing:
                if (msg.equals(context.getString(R.string.dialog_button_yes))) {
                    MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_finger_tip_sign)));
                    MessageBox.INSTANCE.add(new RequestSignature());
                    step = Step.SelectAccount;
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

    @Override
    public void clear() {
        step = Step.Initial;
        TransactionDB.INSTANCE.setTxMoney("");
        TransactionDB.INSTANCE.setTxName("");
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

    private enum Step {
        Initial, Analyzing, SelectAccount, TransferDone
    }
}

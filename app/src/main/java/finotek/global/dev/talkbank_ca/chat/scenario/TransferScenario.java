package finotek.global.dev.talkbank_ca.chat.scenario;

import android.Manifest;
import android.content.Context;
import android.support.v4.app.ActivityCompat;

import org.joda.time.DateTime;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.MessageBox;
import finotek.global.dev.talkbank_ca.chat.messages.Account;
import finotek.global.dev.talkbank_ca.chat.messages.AccountList;
import finotek.global.dev.talkbank_ca.chat.messages.ReceiveMessage;
import finotek.global.dev.talkbank_ca.chat.messages.RequestContactPermission;
import finotek.global.dev.talkbank_ca.chat.messages.SendMessage;
import finotek.global.dev.talkbank_ca.chat.messages.Transaction;
import finotek.global.dev.talkbank_ca.chat.messages.action.Done;
import finotek.global.dev.talkbank_ca.chat.messages.action.MoneyTransferred;
import finotek.global.dev.talkbank_ca.chat.messages.action.SignatureVerified;
import finotek.global.dev.talkbank_ca.chat.messages.contact.RequestSelectContact;
import finotek.global.dev.talkbank_ca.chat.messages.control.ConfirmRequest;
import finotek.global.dev.talkbank_ca.chat.messages.control.RecoMenuRequest;
import finotek.global.dev.talkbank_ca.chat.messages.control.RecommendScenarioMenuRequest;
import finotek.global.dev.talkbank_ca.chat.messages.transfer.RequestTransferUI;
import finotek.global.dev.talkbank_ca.chat.messages.transfer.RequestTransferUI_v1;
import finotek.global.dev.talkbank_ca.chat.messages.transfer.RequestTransferUI_v2;
import finotek.global.dev.talkbank_ca.chat.messages.transfer.RequestTransferUI_v3;
import finotek.global.dev.talkbank_ca.chat.messages.transfer.TransferButtonPressed;
import finotek.global.dev.talkbank_ca.chat.messages.transfer.alterOne;
import finotek.global.dev.talkbank_ca.chat.messages.transfer.alterThree;
import finotek.global.dev.talkbank_ca.chat.messages.transfer.alterTwo;
import finotek.global.dev.talkbank_ca.chat.messages.ui.RequestRemoveControls;
import finotek.global.dev.talkbank_ca.chat.messages.ui.RequestSignature;
import finotek.global.dev.talkbank_ca.chat.storage.TransactionDB;
import finotek.global.dev.talkbank_ca.model.DBHelper;

public class TransferScenario implements Scenario {
    private DBHelper dbHelper;
    private Context context;
    private Step step = Step.BankAsk;
    private boolean isProceeding = true;
    private int navigateNum = 0;


    public TransferScenario(Context context, DBHelper dbHelper) {
        this.context = context;
        this.dbHelper = dbHelper;
    }

    @Override
    public String getName() {
        return context.getString(R.string.scenario_transfer);
    }

    @Override
    public boolean decideOn(String msg) {
        return msg.equals("계좌이체") || msg.equals(context.getResources().getString(R.string.dialog_button_transfer))
                || msg.equals("이체") || msg.equals("송금");
    }

    @Override
    public void onReceive(Object msg) {
        if (msg instanceof TransferButtonPressed) {
            MessageBox.INSTANCE.add(new RequestRemoveControls());

            String name = TransactionDB.INSTANCE.getTxName();
            String moneyAsString = TransactionDB.INSTANCE.getTxMoney();
            int money = TransactionDB.INSTANCE.getMoneyAsInt();
            MessageBox.INSTANCE.add(new SendMessage(context.getString(R.string.dialog_chat_send_transfer, name, moneyAsString)));

            if (money >= 1000000) {
                MessageBox.INSTANCE.addAndWait(
                        new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_finger_tip_sign)),
                        new RequestSignature()
                );
            } else {
                if (navigateNum == 1) {
                    MessageBox.INSTANCE.add(new MoneyTransferred());
                }
                 if (navigateNum == 2) {
                    MessageBox.INSTANCE.add(new alterOne());
                }
                 if (navigateNum == 3){
                    MessageBox.INSTANCE.add(new alterTwo());
                }
                 if (navigateNum == 4){
                    MessageBox.INSTANCE.add(new alterThree());
                }
            }
        }

        if (msg instanceof SignatureVerified) {
            if (navigateNum == 1) {
                MessageBox.INSTANCE.add(new MoneyTransferred());
            }
             if (navigateNum == 2) {
                MessageBox.INSTANCE.add(new alterOne());
            }
             if (navigateNum == 3){
                MessageBox.INSTANCE.add(new alterTwo());
            }
             if (navigateNum == 4){
                MessageBox.INSTANCE.add(new alterThree());
            }
        }

        if (msg instanceof MoneyTransferred) {
            isProceeding = false;
            String name = TransactionDB.INSTANCE.getTxName();
            String moneyAsString = TransactionDB.INSTANCE.getTxMoney();
            int money = TransactionDB.INSTANCE.getMoneyAsInt();

            TransactionDB.INSTANCE.transferMoney(money);
            int balance = TransactionDB.INSTANCE.getMainBalance();
            String balanceAsString = NumberFormat.getNumberInstance().format(balance);
            TransactionDB.INSTANCE.addTx(new Transaction(name, 0, money, balance, new DateTime()));

            RecommendScenarioMenuRequest request = new RecommendScenarioMenuRequest(context);
            request.setTitle("");
            request.setDescription(context.getString(R.string.dialog_chat_after_transfer, name, moneyAsString, balanceAsString));

            MessageBox.INSTANCE.addAndWait(request, new Done());
            step = Step.TransferDone;
        }
        if (msg instanceof alterOne) {
            isProceeding = false;
            String name = TransactionDB.INSTANCE.getTxName();
            String moneyAsString = TransactionDB.INSTANCE.getTxMoney();
            int money = TransactionDB.INSTANCE.getMoneyAsInt();

            TransactionDB.INSTANCE.transferMoneyV1(money);
            int balance = TransactionDB.INSTANCE.getFirstAlternativeBalance();

            String balanceAsString = NumberFormat.getNumberInstance().format(balance);
            TransactionDB.INSTANCE.addTx(new Transaction(name, 0, money, balance, new DateTime()));

            RecommendScenarioMenuRequest request = new RecommendScenarioMenuRequest(context);
            request.setTitle("");
            request.setDescription(context.getString(R.string.dialog_chat_after_transfer, name, moneyAsString, balanceAsString));

            MessageBox.INSTANCE.addAndWait(request, new Done());
            step = Step.TransferDone;
        }
        if (msg instanceof alterTwo) {
            isProceeding = false;
            String name = TransactionDB.INSTANCE.getTxName();
            String moneyAsString = TransactionDB.INSTANCE.getTxMoney();
            int money = TransactionDB.INSTANCE.getMoneyAsInt();

            TransactionDB.INSTANCE.transferMoneyV2(money);
            int balance = TransactionDB.INSTANCE.getSecondAlternativeBalance();

            String balanceAsString = NumberFormat.getNumberInstance().format(balance);
            TransactionDB.INSTANCE.addTx(new Transaction(name, 0, money, balance, new DateTime()));

            RecommendScenarioMenuRequest request = new RecommendScenarioMenuRequest(context);
            request.setTitle("");
            request.setDescription(context.getString(R.string.dialog_chat_after_transfer, name, moneyAsString, balanceAsString));

            MessageBox.INSTANCE.addAndWait(request, new Done());
            step = Step.TransferDone;
        }

        if (msg instanceof alterThree) {
            isProceeding = false;
            String name = TransactionDB.INSTANCE.getTxName();
            String moneyAsString = TransactionDB.INSTANCE.getTxMoney();
            int money = TransactionDB.INSTANCE.getMoneyAsInt();

            TransactionDB.INSTANCE.transferMoneyV3(money);
            int balance = TransactionDB.INSTANCE.getThirdAlternativeBalance();

            String balanceAsString = NumberFormat.getNumberInstance().format(balance);
            TransactionDB.INSTANCE.addTx(new Transaction(name, 0, money, balance, new DateTime()));

            RecommendScenarioMenuRequest request = new RecommendScenarioMenuRequest(context);
            request.setTitle("");
            request.setDescription(context.getString(R.string.dialog_chat_after_transfer, name, moneyAsString, balanceAsString));

            MessageBox.INSTANCE.addAndWait(request, new Done());
            step = Step.TransferDone;
        }
        if (msg instanceof Done) {
            this.clear();
        }


    }


    @Override
    public void onUserSend(String msg) {
        RecoMenuRequest req = new RecoMenuRequest();
        req.setDescription(context.getResources().getString(R.string.dialog_chat_before_transfer));
        req.addMenu(R.drawable.icon_haha, context.getResources().getString(R.string.dialog_chat_select_bank_yes), null);
        req.addMenu(R.drawable.icon_sad, context.getResources().getString(R.string.dialog_chat_select_bank_no), null);
        switch (step) {
            case BankAsk:

                MessageBox.INSTANCE.addAndWait(
                        req
                );
                step = Step.BankAnswer;
                break;

            case BankAnswer:
                if (msg.equals(context.getResources().getString(R.string.dialog_chat_select_bank_yes))) {
                    selectAccounts();
                } else if ((msg.equals(context.getResources().getString(R.string.dialog_chat_select_bank_no)))) {
                    RecoMenuRequest selectbank = new RecoMenuRequest();
                    selectbank.setDescription(context.getResources().getString(R.string.dialog_chat_bank_select));
                    selectbank.addMenu(R.drawable.icon_love, context.getResources().getString(R.string.dialog_chat_bank_select_A1), null);
                    selectbank.addMenu(R.drawable.icon_mike, context.getResources().getString(R.string.dialog_chat_bank_select_A2), null);
                    selectbank.addMenu(R.drawable.icon_haha, context.getResources().getString(R.string.dialog_chat_bank_select_A3), null);
                    selectbank.addMenu(R.drawable.icon_sad, context.getResources().getString(R.string.dialog_chat_bank_select_cancel), null);

                    MessageBox.INSTANCE.addAndWait(
                            selectbank
                    );
                    step = Step.Question;
                    break;
                }
            case Question:

                if (msg.equals(context.getResources().getString(R.string.dialog_chat_bank_select_A1))) {
                    int balance = TransactionDB.INSTANCE.getFirstAlternativeBalance();
                    RecoMenuRequest a1 = new RecoMenuRequest();
                    a1.setDescription(context.getResources().getString(R.string.dialog_chat_bank_balance, balance));
                    a1.addMenu(R.drawable.icon_haha, context.getResources().getString(R.string.dialog_chat_select_bank_yes), null);
                    a1.addMenu(R.drawable.icon_sad, context.getResources().getString(R.string.dialog_chat_select_bank_no), null);
                    MessageBox.INSTANCE.addAndWait(
                            a1
                    );
                    step = Step.FirstBank;

                    }
                else if (msg.equals(context.getResources().getString(R.string.dialog_chat_bank_select_A2))) {
                    int balance = TransactionDB.INSTANCE.getSecondAlternativeBalance();
                    RecoMenuRequest a2 = new RecoMenuRequest();
                    a2.setDescription(context.getResources().getString(R.string.dialog_chat_bank_balance, balance));
                    a2.addMenu(R.drawable.icon_haha, context.getResources().getString(R.string.dialog_chat_select_bank_yes), null);
                    a2.addMenu(R.drawable.icon_sad, context.getResources().getString(R.string.dialog_chat_select_bank_no), null);
                    MessageBox.INSTANCE.addAndWait(
                            a2
                    );
                    step = Step.SecondBank;

                } else if (msg.equals(context.getResources().getString(R.string.dialog_chat_bank_select_A3))) {
                    int balance = TransactionDB.INSTANCE.getThirdAlternativeBalance();
                    RecoMenuRequest a3 = new RecoMenuRequest();
                    a3.setDescription(context.getResources().getString(R.string.dialog_chat_bank_balance, balance));
                    a3.addMenu(R.drawable.icon_haha, context.getResources().getString(R.string.dialog_chat_select_bank_yes), null);
                    a3.addMenu(R.drawable.icon_sad, context.getResources().getString(R.string.dialog_chat_select_bank_no), null);
                    MessageBox.INSTANCE.addAndWait(
                            a3
                    );
                    step = Step.ThirdBank;

                } else {
                    MessageBox.INSTANCE.addAndWait(
                            new ReceiveMessage(context.getString(R.string.dialog_chat_recognize_error))
                    );                }
                break;

            case FirstBank:
                if (msg.equals(context.getResources().getString(R.string.dialog_chat_select_bank_yes))) {
                    firstBank();
                }
                else if (msg.equals(context.getResources().getString(R.string.dialog_chat_select_bank_no))) {
                    RecoMenuRequest selectbank = new RecoMenuRequest();
                    selectbank.setDescription(context.getResources().getString(R.string.dialog_chat_bank_select));
                    selectbank.addMenu(R.drawable.icon_love, context.getResources().getString(R.string.dialog_chat_bank_select_A1), null);
                    selectbank.addMenu(R.drawable.icon_mike, context.getResources().getString(R.string.dialog_chat_bank_select_A2), null);
                    selectbank.addMenu(R.drawable.icon_haha, context.getResources().getString(R.string.dialog_chat_bank_select_A3), null);
                    selectbank.addMenu(R.drawable.icon_sad, context.getResources().getString(R.string.dialog_chat_bank_select_cancel), null);

                    MessageBox.INSTANCE.addAndWait(
                            selectbank
                    );                }
                break;

            case SecondBank:
                if (msg.equals(context.getResources().getString(R.string.dialog_chat_select_bank_yes))) {
                    secondBank();
                }
                else if (msg.equals(context.getResources().getString(R.string.dialog_chat_select_bank_no))) {
                    RecoMenuRequest selectbank = new RecoMenuRequest();
                    selectbank.setDescription(context.getResources().getString(R.string.dialog_chat_bank_select));
                    selectbank.addMenu(R.drawable.icon_love, context.getResources().getString(R.string.dialog_chat_bank_select_A1), null);
                    selectbank.addMenu(R.drawable.icon_mike, context.getResources().getString(R.string.dialog_chat_bank_select_A2), null);
                    selectbank.addMenu(R.drawable.icon_haha, context.getResources().getString(R.string.dialog_chat_bank_select_A3), null);
                    selectbank.addMenu(R.drawable.icon_sad, context.getResources().getString(R.string.dialog_chat_bank_select_cancel), null);

                    MessageBox.INSTANCE.addAndWait(
                            selectbank
                    );
                }
                break;
            case ThirdBank:
                if (msg.equals(context.getResources().getString(R.string.dialog_chat_select_bank_yes))) {
                    thirdBank();
                }
                else if (msg.equals(context.getResources().getString(R.string.dialog_chat_select_bank_no))) {
                    RecoMenuRequest selectbank = new RecoMenuRequest();
                    selectbank.setDescription(context.getResources().getString(R.string.dialog_chat_bank_select));
                    selectbank.addMenu(R.drawable.icon_love, context.getResources().getString(R.string.dialog_chat_bank_select_A1), null);
                    selectbank.addMenu(R.drawable.icon_mike, context.getResources().getString(R.string.dialog_chat_bank_select_A2), null);
                    selectbank.addMenu(R.drawable.icon_haha, context.getResources().getString(R.string.dialog_chat_bank_select_A3), null);
                    selectbank.addMenu(R.drawable.icon_sad, context.getResources().getString(R.string.dialog_chat_bank_select_cancel), null);

                    MessageBox.INSTANCE.addAndWait(
                            selectbank
                    );
                    step = Step.Question;                }
                break;
        }
    }

    @Override
    public void clear() {
        isProceeding = true;
        step = Step.BankAsk;
        TransactionDB.INSTANCE.setTxMoney("");
        TransactionDB.INSTANCE.setTxName("");
    }

    @Override
    public boolean isProceeding() {
        return isProceeding;
    }

    private void selectAccounts() {
        navigateNum = 1;
        List<Account> accounts = new ArrayList<>();
        accounts.add(new Account("어머니", "2017/01/25", "200,000 원 " + context.getString(R.string.string_transfer).toLowerCase(), true));
        accounts.add(new Account("박예린", "2017/01/11", "100,000 원 " + context.getString(R.string.string_transfer).toLowerCase(), false));
        accounts.add(new Account("김가람", "2017/01/11", "36,200 원 " + context.getString(R.string.string_transfer).toLowerCase(), false));
        accounts.add(new Account("김이솔", "2017/01/10", "100,000 원 " + context.getString(R.string.dialog_string_deposit).toLowerCase(), false));

        ConfirmRequest confirmRequest = new ConfirmRequest();
        confirmRequest.addInfoEvent(context.getString(R.string.dialog_contact), () -> {
            MessageBox.INSTANCE.add(new RequestSelectContact());
        }, false);

        MessageBox.INSTANCE.addAndWait(
                new ReceiveMessage(context.getString(R.string.dialog_string_select_receiver)),
                new AccountList(accounts),
                confirmRequest,
                new RequestTransferUI(),
                new RequestContactPermission()
        );
    }
    private void firstBank() {
        navigateNum = 2;
        List<Account> accounts = new ArrayList<>();
        accounts.add(new Account("어머니", "2017/01/25", "200,000 원 " + context.getString(R.string.string_transfer).toLowerCase(), true));
        accounts.add(new Account("박예린", "2017/01/11", "100,000 원 " + context.getString(R.string.string_transfer).toLowerCase(), false));
        accounts.add(new Account("김가람", "2017/01/11", "36,200 원 " + context.getString(R.string.string_transfer).toLowerCase(), false));
        accounts.add(new Account("김이솔", "2017/01/10", "100,000 원 " + context.getString(R.string.dialog_string_deposit).toLowerCase(), false));

        ConfirmRequest confirmRequest = new ConfirmRequest();
        confirmRequest.addInfoEvent(context.getString(R.string.dialog_contact), () -> {
            MessageBox.INSTANCE.add(new RequestSelectContact());
        }, false);

        MessageBox.INSTANCE.addAndWait(
                new ReceiveMessage(context.getString(R.string.dialog_string_select_receiver)),
                new AccountList(accounts),
                confirmRequest,
                new RequestTransferUI_v1(),
                new RequestContactPermission()
        );

    }

    private void secondBank() {
        navigateNum = 3;
        List<Account> accounts = new ArrayList<>();
        accounts.add(new Account("어머니", "2017/01/25", "200,000 원 " + context.getString(R.string.string_transfer).toLowerCase(), true));
        accounts.add(new Account("박예린", "2017/01/11", "100,000 원 " + context.getString(R.string.string_transfer).toLowerCase(), false));
        accounts.add(new Account("김가람", "2017/01/11", "36,200 원 " + context.getString(R.string.string_transfer).toLowerCase(), false));
        accounts.add(new Account("김이솔", "2017/01/10", "100,000 원 " + context.getString(R.string.dialog_string_deposit).toLowerCase(), false));

        ConfirmRequest confirmRequest = new ConfirmRequest();
        confirmRequest.addInfoEvent(context.getString(R.string.dialog_contact), () -> {
            MessageBox.INSTANCE.add(new RequestSelectContact());
        }, false);

        MessageBox.INSTANCE.addAndWait(
                new ReceiveMessage(context.getString(R.string.dialog_string_select_receiver)),
                new AccountList(accounts),
                confirmRequest,
                new RequestTransferUI_v2(),
                new RequestContactPermission()
        );
    }

        private void thirdBank() {
            navigateNum = 4;
            List<Account> accounts = new ArrayList<>();
            accounts.add(new Account("어머니", "2017/01/25", "200,000 원 " + context.getString(R.string.string_transfer).toLowerCase(), true));
            accounts.add(new Account("박예린", "2017/01/11", "100,000 원 " + context.getString(R.string.string_transfer).toLowerCase(), false));
            accounts.add(new Account("김가람", "2017/01/11", "36,200 원 " + context.getString(R.string.string_transfer).toLowerCase(), false));
            accounts.add(new Account("김이솔", "2017/01/10", "100,000 원 " + context.getString(R.string.dialog_string_deposit).toLowerCase(), false));

            ConfirmRequest confirmRequest = new ConfirmRequest();
            confirmRequest.addInfoEvent(context.getString(R.string.dialog_contact), () -> {
                MessageBox.INSTANCE.add(new RequestSelectContact());
            }, false);

            MessageBox.INSTANCE.addAndWait(
                    new ReceiveMessage(context.getString(R.string.dialog_string_select_receiver)),
                    new AccountList(accounts),
                    confirmRequest,
                    new RequestTransferUI_v3(),
                    new RequestContactPermission()
            );

        }

    private enum Step {
        BankAsk, BankAnswer, Question, FirstBank, SecondBank, ThirdBank, Initial, TransferToSomeone, TransferByAI, TransferDone
    }
}
package finotek.global.dev.talkbank_ca.chat.storage;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

import finotek.global.dev.talkbank_ca.chat.messages.Transaction;

public enum TransactionDB {
    INSTANCE;

    private List<Transaction> tx;
    private int balance;

    private String txName;
    private String txMoney;

    TransactionDB(){
        DateTimeFormatter f = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

        tx = new ArrayList<>();
        tx.add(new Transaction("어머니", 0, 200000, 3033800, f.parseDateTime("2017-04-02 21:12:00")));
        tx.add(new Transaction("박예린", 0, 100000, 3233800, f.parseDateTime("2017-03-31 14:43:00")));
        tx.add(new Transaction("김가람", 0, 36200, 3333800, f.parseDateTime("2017-02-22 13:11:00")));
        tx.add(new Transaction("어머니", 1, 400000, 3733800, f.parseDateTime("2017-04-02 21:12:00")));

        balance = 3033800;
    }

    public List<Transaction> getTx() {
        return tx;
    }

    public void addTx(Transaction transaction){
        tx.add(0, transaction);
    }

    public int getBalance() {
        return balance;
    }

    public void transferMoney(int money){
        this.balance -= money;
    }

    public String getTxName() {
        return txName;
    }

    public void setTxName(String txName) {
        this.txName = txName;
    }

    public String getTxMoney() {
        return txMoney;
    }

    public void setTxMoney(String txMoney) {
        this.txMoney = txMoney;
    }
}
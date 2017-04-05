package finotek.global.dev.talkbank_ca.chat.storage;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import finotek.global.dev.talkbank_ca.chat.messages.Transaction;

public enum TransactionDB {
    INSTANCE;

    private List<Transaction> tx;

    TransactionDB(){
        tx = new ArrayList<>();
        tx.add(new Transaction("어머니", 0, 200000, 3033800, new DateTime()));
        tx.add(new Transaction("박예린", 0, 100000, 3233800, new DateTime()));
        tx.add(new Transaction("김가람", 0, 36200, 3333800, new DateTime()));
        tx.add(new Transaction("김이솔", 1, 100000, 3370000, new DateTime()));
        tx.add(new Transaction("김가람", 0, 15500, 3270000, new DateTime()));
    }

    public List<Transaction> getTx() {
        return tx;
    }
}
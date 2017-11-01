package finotek.global.dev.brazil.chat.storage;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

import finotek.global.dev.brazil.chat.messages.Transaction;

public enum TransactionDB {
	INSTANCE;

	private List<Transaction> tx;
	private int mainBalance;
	private int firstAlternativeBalance;
	private int secondAlternativeBalance;
	private int thirdAlternativeBalance;

	private String txName;
	private String txMoney;

	private boolean isTransfer;

	TransactionDB() {
		DateTimeFormatter f = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

		tx = new ArrayList<>();
		tx.add(new Transaction("어머니", 0, 200000, 3033800, f.parseDateTime("2017-04-02 21:12:00")));
		tx.add(new Transaction("박예린", 0, 100000, 3233800, f.parseDateTime("2017-03-31 14:43:00")));
		tx.add(new Transaction("김가람", 0, 36200, 3333800, f.parseDateTime("2017-02-22 13:11:00")));
		tx.add(new Transaction("어머니", 1, 400000, 3733800, f.parseDateTime("2017-04-02 21:12:00")));

		mainBalance = 3033800;
		firstAlternativeBalance = 2015800;
		secondAlternativeBalance = 11520800;
		thirdAlternativeBalance = 20000;
		isTransfer = false;
	}

	public List<Transaction> getTx() {
		return tx;
	}

	public void addTx(Transaction transaction) {
		tx.add(0, transaction);
	}

	public int getMainBalance() {
		return mainBalance;
	}

	public int getFirstAlternativeBalance() {
		return firstAlternativeBalance;
	}

	public int getSecondAlternativeBalance() {
		return secondAlternativeBalance;
	}

	public int getThirdAlternativeBalance() {
		return thirdAlternativeBalance;
	}

	public void transferMoney(int money) {
		this.mainBalance -= money;
	}

	public void transferMoneyV1(int money) {
		this.firstAlternativeBalance -= money;
	}

	public void transferMoneyV2(int money) {
		this.secondAlternativeBalance -= money;
	}

	public void transferMoneyV3(int money) {
		this.thirdAlternativeBalance -= money;
	}

	public void deposit(int money) {
		this.mainBalance += money;
	}

	public void depositV1(int money) {
		this.firstAlternativeBalance += money;
	}

	public void depositV2(int money) {
		this.secondAlternativeBalance += money;
	}

	public void depositV3(int money) {
		this.thirdAlternativeBalance += money;
	}

	public boolean isTransfer() {
		return isTransfer;
	}

	public void setTransfer(boolean transfer) {
		isTransfer = transfer;
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

	public int getMoneyAsInt() {
		int money = 0;
		try {
			money = Integer.valueOf(txMoney.replaceAll(",", ""));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return money;
	}
}
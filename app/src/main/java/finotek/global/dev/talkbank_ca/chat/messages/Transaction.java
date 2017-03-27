package finotek.global.dev.talkbank_ca.chat.messages;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.NumberFormat;

public class Transaction {
    private String name;            // 이름
    private int type;               // 0: 출금, 1: 입금
    private int price;              //
    private int balance;            // 잔액
    private DateTime date;          // 날짜
    private String priceAsString;
    private String balanceAsString;
    private String dateAsString;

    public Transaction(String name, int type, int price, int balance, DateTime date) {
        this.name = name;
        this.type = type;
        this.price = price;
        this.balance = balance;
        this.date = date;

        this.priceAsString = NumberFormat.getInstance().format(price);
        this.balanceAsString = NumberFormat.getInstance().format(balance);

        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy.MM.dd HH:mm");
        this.dateAsString = formatter.print(date);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public String getPriceAsString() {
        return priceAsString;
    }

    public void setPriceAsString(String priceAsString) {
        this.priceAsString = priceAsString;
    }

    public String getBalanceAsString() {
        return balanceAsString;
    }

    public void setBalanceAsString(String balanceAsString) {
        this.balanceAsString = balanceAsString;
    }

    public String getDateAsString() {
        return dateAsString;
    }

    public void setDateAsString(String dateAsString) {
        this.dateAsString = dateAsString;
    }
}

package finotek.global.dev.talkbank_ca.chat.messages;

import lombok.AllArgsConstructor;
import lombok.Data;

public class Account {
    String name;
    String date;
    String history;
    boolean isStar;

    public Account(String name, String date, String history, boolean isStar) {
        this.name = name;
        this.date = date;
        this.history = history;
        this.isStar = isStar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public boolean isStar() {
        return isStar;
    }

    public void setStar(boolean star) {
        isStar = star;
    }
}

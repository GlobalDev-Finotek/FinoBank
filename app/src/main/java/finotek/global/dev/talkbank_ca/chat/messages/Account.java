package finotek.global.dev.talkbank_ca.chat.messages;

import lombok.AllArgsConstructor;
import lombok.Data;

public class Account {
    private String name;
    private String date;
    private String history;
    private boolean isStar;
    private boolean isFromContact;

    public Account(String name, String date, String history, boolean isStar) {
        this.name = name;
        this.date = date;
        this.history = history;
        this.isStar = isStar;
        this.isFromContact = false;
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

    public boolean isFromContact() {
        return isFromContact;
    }

    public void setFromContact(boolean fromContact) {
        isFromContact = fromContact;
    }
}

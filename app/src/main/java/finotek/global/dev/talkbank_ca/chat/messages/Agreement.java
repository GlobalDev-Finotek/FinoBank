package finotek.global.dev.talkbank_ca.chat.messages;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public class Agreement {
    private int id;
    private String name;
    private List<Agreement> child = null;
    private boolean isNewCheck = false;

    public Agreement(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isNewCheck() {
        return isNewCheck;
    }

    public boolean isParent(){
        return !isEmptyChild();
    }

    public void setNewCheck(boolean newCheck) {
        isNewCheck = newCheck;
    }

    public List<Agreement> getChild() {
        return child;
    }

    public boolean isEmptyChild(){
        return child == null || child.isEmpty();
    }

    public void addChild(Agreement childAgr){
        if(this.child == null)
            this.child = new ArrayList<>();

        this.child.add(childAgr);
    }
}
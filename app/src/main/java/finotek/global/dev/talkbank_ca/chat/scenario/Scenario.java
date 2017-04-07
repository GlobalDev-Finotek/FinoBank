package finotek.global.dev.talkbank_ca.chat.scenario;

public interface Scenario {
    boolean decideOn(String msg);
    void onReceive(Object msg);
    void onUserSend(String msg);
    void clear();
}
package finotek.global.dev.brazil.chat.scenario;

public interface Scenario {
	boolean decideOn(String msg);

	void onReceive(Object msg);

	void onUserSend(String msg);

	String getName();

	void clear();

	boolean isProceeding();
}
package finotek.global.dev.talkbank_ca.chat.scenario;

import android.content.Context;
import android.util.Log;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.MessageBox;
import finotek.global.dev.talkbank_ca.chat.messages.ReceiveMessage;
import finotek.global.dev.talkbank_ca.chat.messages.action.Done;
import finotek.global.dev.talkbank_ca.chat.messages.context.CurrentAddressReceived;
import finotek.global.dev.talkbank_ca.chat.messages.context.RequestCurrentAddress;
import finotek.global.dev.talkbank_ca.chat.messages.control.RecommendScenarioMenuRequest;

public class FakeContextSearch implements Scenario {
<<<<<<< HEAD
    private Step step = Step.initial;
    private Context context;
    private int counter = 0;
    private long lastTime = 0;

    private final int[] scores = {65, 35, 88, 22, 34};
    private final int[] offset = {2, 14, 5, 20, 10};

    public FakeContextSearch(Context context) {
        this.context = context;
    }

    @Override
    public boolean decideOn(String msg) {
        return msg.equals("ㅁㄹ") || msg.equals(context.getResources().getString(R.string.dialog_chat_fake_contextlog_search));
    }

    @Override
    public void onReceive(Object msg) {
        if (msg instanceof Done) {
            step = Step.initial;
            MessageBox.INSTANCE.addAndWait(new RecommendScenarioMenuRequest(context));
        }

        if (msg instanceof CurrentAddressReceived) {
            String address = ((CurrentAddressReceived) msg).getAddress();

            String message = "";
            message = buildScoreMessages(address);
            MessageBox.INSTANCE.addAndWait(new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_contextlog_result_message, message)), new Done());
            Log.d("FINOTEK", "search message ended");
        }
    }

    @Override
    public void onUserSend(String msg) {
        switch (step) {
            case initial:
                MessageBox.INSTANCE.add(new RequestCurrentAddress());
                break;
        }
    }

    @Override
    public String getName() {
        return context.getResources().getString(R.string.dialog_chat_contextlog_search);
    }

    @Override
    public void clear() {

    }

    @Override
    public boolean isProceeding() {
        return false;
    }

    private String buildScoreMessages(String address) {
        String[] addresses = address.split(" ");
        String result = addresses[1] + " " + addresses[2] + " git " + addresses[3];

        if (counter == 0) {
            lastTime = System.currentTimeMillis();
        }

        int minutes = (int) ((System.currentTimeMillis() - lastTime) / (1000 * 60));

        String messages = "";
        messages += "1: 당신은 " + (minutes + offset[0]) + "분전 아내(친밀도: 2위)과(와) 5분간 통화한이 확인되었으므로 " + getScore(scores[0], minutes) + "점을 얻었습니다.\n\n";
        messages += "2: 당신은 " + (minutes + offset[1]) + "분전 어머니(친밀도: 7위)과(와) 3분간 통화한 것이 확인되었으므로 " + getScore(scores[1], minutes) + " 점을 얻었습니다.\n\n";
        messages += "3: 당신은 " + (minutes + offset[2]) + " 분전 카카오톡 앱(친밀도: 1위)을(를) 사용한 것이 확인되었으므로 " + getScore(scores[2], minutes) + " 점을 얻었습니다.\n\n";
        messages += "4: 당신은 " + (minutes + offset[3]) + " 분전 프로젝트웨어 앱(친밀도: 7위)을(를) 사용한 것이 확인되었으므로 " + getScore(scores[3], minutes) + " 점을 얻었습니다.\n\n";
        messages += "5: 당신은 현재 " + result + "(친밀도: 12위)에 있는 것이 확인되었으므로 73점을 얻었습니다.\n\n";
        messages += "6: 당신은 " + (minutes + offset[4]) + " 분전 이도현(친밀도: 15위)과(와) 메시지를 주고받은 확인되었으므로 " + getScore(scores[4], minutes) + " 점을 얻었습니다.";
        counter += 1;

        return messages;
    }

    private int getScore(int score, int minute) {
        return score;
    }

    private enum Step {
        initial, ask
    }
=======
	private final int[] scores = {65, 35, 88, 22, 34};
	private final int[] offset = {2, 14, 5, 20, 10};
	private Step step = Step.initial;
	private Context context;
	private int counter = 0;
	private long lastTime = 0;

	public FakeContextSearch(Context context) {
		this.context = context;
	}

	@Override
	public boolean decideOn(String msg) {
		return msg.equals("ㅁㄹ") || msg.equals(context.getResources().getString(R.string.dialog_chat_fake_contextlog_search));
	}

	@Override
	public void onReceive(Object msg) {
		if (msg instanceof Done) {
			step = Step.initial;
			MessageBox.INSTANCE.addAndWait(new RecommendScenarioMenuRequest(context));
		}

		if (msg instanceof CurrentAddressReceived) {
			String address = ((CurrentAddressReceived) msg).getAddress();

			String message = "";
			message = buildScoreMessages(address);
			MessageBox.INSTANCE.addAndWait(new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_contextlog_result_message, message)), new Done());
			Log.d("FINOTEK", "search message ended");
		}
	}

	@Override
	public void onUserSend(String msg) {
		switch (step) {
			case initial:
				MessageBox.INSTANCE.add(new RequestCurrentAddress());
				break;
		}
	}

	@Override
	public String getName() {
		return context.getResources().getString(R.string.dialog_chat_contextlog_search);
	}

	@Override
	public void clear() {

	}

	@Override
	public boolean isProceeding() {
		return false;
	}

	private String buildScoreMessages(String address) {
		String[] addresses = address.split(" ");
		String result = addresses[1] + " " + addresses[2] + " git " + addresses[3];

		if (counter == 0) {
			lastTime = System.currentTimeMillis();
		}

		int minutes = (int) ((System.currentTimeMillis() - lastTime) / (1000 * 60));

		String messages = "";
		messages += "1: 당신은 " + (minutes + offset[0]) + "분전 아내(친밀도: 2위)과(와) 5분간 통화한이 확인되었으므로 " + getScore(scores[0], minutes) + "점을 얻었습니다.\n\n";
		messages += "2: 당신은 " + (minutes + offset[1]) + "분전 어머니(친밀도: 7위)과(와) 3분간 통화한 것이 확인되었으므로 " + getScore(scores[1], minutes) + " 점을 얻었습니다.\n\n";
		messages += "3: 당신은 " + (minutes + offset[2]) + " 분전 카카오톡 앱(친밀도: 1위)을(를) 사용한 것이 확인되었으므로 " + getScore(scores[2], minutes) + " 점을 얻었습니다.\n\n";
		messages += "4: 당신은 " + (minutes + offset[3]) + " 분전 프로젝트웨어 앱(친밀도: 7위)을(를) 사용한 것이 확인되었으므로 " + getScore(scores[3], minutes) + " 점을 얻었습니다.\n\n";
		messages += "5: 당신은 현재 " + result + "(친밀도: 12위)에 있는 것이 확인되었으므로 73점을 얻었습니다.\n\n";
		messages += "6: 당신은 " + (minutes + offset[4]) + " 분전 이도현(친밀도: 15위)과(와) 메시지를 주고받은 확인되었으므로 " + getScore(scores[4], minutes) + " 점을 얻었습니다.";
		counter += 1;

		return messages;
	}

	private int getScore(int score, int minute) {
		return score;
	}

	private enum Step {
		initial, ask
	}
>>>>>>> master
}
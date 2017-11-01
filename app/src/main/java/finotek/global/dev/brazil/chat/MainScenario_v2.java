package finotek.global.dev.brazil.chat;

import android.content.Context;
import android.text.format.DateFormat;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import finotek.global.dev.brazil.R;
import finotek.global.dev.brazil.chat.messages.AccountConfirm;
import finotek.global.dev.brazil.chat.messages.AccountList;
import finotek.global.dev.brazil.chat.messages.AgreementRequest;
import finotek.global.dev.brazil.chat.messages.AgreementResult;
import finotek.global.dev.brazil.chat.messages.ApplyScenario;
import finotek.global.dev.brazil.chat.messages.DividerMessage;
import finotek.global.dev.brazil.chat.messages.ImageMessage;
import finotek.global.dev.brazil.chat.messages.MessageEmitted;
import finotek.global.dev.brazil.chat.messages.ReceiveMessage;
import finotek.global.dev.brazil.chat.messages.RecentTransaction;
import finotek.global.dev.brazil.chat.messages.SendMessage;
import finotek.global.dev.brazil.chat.messages.StatusMessage;
import finotek.global.dev.brazil.chat.messages.WaitDone;
import finotek.global.dev.brazil.chat.messages.WaitResult;
import finotek.global.dev.brazil.chat.messages.action.Done;
import finotek.global.dev.brazil.chat.messages.control.ConfirmRequest;
import finotek.global.dev.brazil.chat.messages.control.DonateRequest;
import finotek.global.dev.brazil.chat.messages.control.RecoMenuRequest;
import finotek.global.dev.brazil.chat.messages.transfer.TransferButtonPressed;
import finotek.global.dev.brazil.chat.messages.ui.IDCardInfo;
import finotek.global.dev.brazil.chat.messages.ui.RequestRemoveControls;
import finotek.global.dev.brazil.chat.scenario.LeftScenario;
import finotek.global.dev.brazil.chat.scenario.Scenario;
import finotek.global.dev.brazil.chat.scenario.ShowUserScenario;
import finotek.global.dev.brazil.chat.scenario.BrazilSignScenario;
import finotek.global.dev.brazil.chat.view.ChatView;
import finotek.global.dev.brazil.model.User;
import finotek.global.dev.brazil.util.DateUtil;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.realm.Realm;

public class MainScenario_v2 {
	private final Disposable disposable;
	private Context context;
	private ChatView chatView;
	private Scenario currentScenario = null;
	private Map<String, Scenario> scenarioPool;
	private User user;

	public MainScenario_v2(Context context, ChatView chatView) {
		this.context = context;
		this.chatView = chatView;
		Realm realm = Realm.getDefaultInstance();
		user = realm.where(User.class).findAll().last();

		//Recommend scenario setup
		if (!LeftScenario.scenarioList.contains("E"))
			LeftScenario.scenarioList.add("E");
		if (!LeftScenario.scenarioList.contains("P"))
			LeftScenario.scenarioList.add("P");
		if (!LeftScenario.scenarioList.contains("T"))
			LeftScenario.scenarioList.add("T");
		if (!LeftScenario.scenarioList.contains("H"))
			LeftScenario.scenarioList.add("H");
		if (!LeftScenario.scenarioList.contains("D"))
			LeftScenario.scenarioList.add("D");

		// 메시지 박스 설정
		disposable = MessageBox.INSTANCE.observable
				.flatMap(msg -> {
					if (isImmediateMessage(msg)) {
						return Observable.just(msg);
					} else {
						return Observable.just(msg)
								.delay(1, TimeUnit.SECONDS)
								.observeOn(AndroidSchedulers.mainThread());
					}
				})
				.doOnNext(msg -> {
					if (!isImmediateMessage(msg)) {
						MessageBox.INSTANCE.add(new MessageEmitted());
					}
				})
				.doOnError(e -> {
					chatView.statusMessage(context.getResources().getString(R.string.dialog_message_error));
				})
				.subscribe(msg -> {
					updateUIOn(msg);
					onRequest(msg);
				}, throwable -> {

				});

		// 초기 시나리오 진행
		this.firstScenario();

		// 시나리오 저장
		scenarioPool = new HashMap<>();
		scenarioPool.put("trySignScenario", new BrazilSignScenario(context));
		scenarioPool.put("showId", new ShowUserScenario(context));

		currentScenario = null;
	}

	private String getGreetings() {
		long temptime = System.currentTimeMillis();
		String time = DateFormat.format("HH", temptime).toString();
		int hour = Integer.parseInt(time);

		String greetingString = "";

		User user = Realm.getDefaultInstance().where(User.class).findAll().last();
		if (user != null) {
			greetingString = context.getResources().getString(R.string.brazil_scenario_greeting);
		}


		return greetingString;
	}

	private void firstScenario() {
		MessageBox.INSTANCE.add(new DividerMessage(DateUtil.currentDate(context)));

		MessageBox.INSTANCE.addAndWait(new ReceiveMessage(getGreetings()));
	}

	private void onRequest(Object msg) {
		if (msg instanceof SendMessage) {
			SendMessage recv = (SendMessage) msg;

			if (!recv.isOnlyDisplay()) {
				Iterator<String> keySet = scenarioPool.keySet().iterator();

				while (keySet.hasNext()) {
					String key = keySet.next();
					Scenario scenario = scenarioPool.get(key);

					if (scenario.decideOn(recv.getMessage())) {
						MessageBox.INSTANCE.add(new RequestRemoveControls());

						if (currentScenario != null && currentScenario.isProceeding()) {
							MessageBox.INSTANCE.add(new ReceiveMessage(context.getString(R.string.dialog_chat_scenario_is_cancelled, currentScenario.getName(), scenario.getName())));

							currentScenario.clear();
							scenario.clear();
							currentScenario = scenario;
							break;
						} else {
							currentScenario = scenario;
							currentScenario.clear();
						}
					}
				}

				if (currentScenario == null) {
					this.respondToSendMessage(recv.getMessage());
				} else {
					currentScenario.onUserSend(recv.getMessage());
				}
			}
		} else {
			if (currentScenario != null)
				currentScenario.onReceive(msg);
		}

		if (msg instanceof Done) {
			currentScenario = null;
		}
	}

	private void updateUIOn(Object msg) {

		chatView.scrollToBottom();

		// 보낸 메시지
		if (msg instanceof SendMessage) {
			SendMessage recv = (SendMessage) msg;
			if (recv.getIcon() == -1) {
				chatView.sendMessage(recv.getMessage());
			} else {
				chatView.sendMessage(recv.getMessage(), recv.getIcon());
			}
		}

		// 받은 메시지
		if (msg instanceof ReceiveMessage) {
			ReceiveMessage recv = (ReceiveMessage) msg;
			chatView.receiveMessage(recv.getMessage());
		}

		// 상태 메시지
		if (msg instanceof StatusMessage) {
			StatusMessage recv = (StatusMessage) msg;
			chatView.statusMessage(recv.getMessage());
		}

		// 구분선
		if (msg instanceof DividerMessage) {
			DividerMessage recv = (DividerMessage) msg;
			chatView.dividerMessage(recv.getMessage());
		}

		// 예, 아니오 선택 요청
		if (msg instanceof ConfirmRequest) {
			ConfirmRequest request = (ConfirmRequest) msg;
			request.setDoAfterEvent(() -> {
				chatView.removeOf(ChatView.ViewType.Confirm);
			});
			chatView.confirm(request);
		}

		if (msg instanceof ImageMessage) {
			chatView.showImage((ImageMessage) msg);
		}

		// 추천 메뉴 요청
		if (msg instanceof RecoMenuRequest) {
			chatView.recoMenu((RecoMenuRequest) msg);
		}

		if (msg instanceof DonateRequest) {
			chatView.addDonateView((DonateRequest) msg);
		}

		// 신분증 스캔 결과
		if (msg instanceof IDCardInfo) {
			chatView.showIdCardInfo((IDCardInfo) msg);
		}

		// 약관 동의 화면
		if (msg instanceof AgreementRequest) {
			chatView.agreement((AgreementRequest) msg);
		}

		// 약관 결과
		if (msg instanceof AgreementResult) {
			chatView.agreementResult();
		}

		if (msg instanceof AccountConfirm) {
			chatView.accountConfirm();
		}

		// 최근 거래 내역
		if (msg instanceof RecentTransaction) {
			chatView.transactionList((RecentTransaction) msg);
		}

		// 계좌 리스트
		if (msg instanceof AccountList) {
			chatView.accountList((AccountList) msg);
		}

		if (msg instanceof ApplyScenario) {
			ApplyScenario result = (ApplyScenario) msg;
			String key = result.getName();

			if (scenarioPool.containsKey(key)) {
				currentScenario = scenarioPool.get(key);
				currentScenario.clear();
			} else {
				throw new RuntimeException("NoSuchScenarioError Exception: for key: " + key);
			}
		}

		if (msg instanceof RequestRemoveControls) {
			chatView.removeOf(ChatView.ViewType.AccountList);
			chatView.removeOf(ChatView.ViewType.Confirm);
			chatView.removeOf(ChatView.ViewType.Agreement);
		}

		if (msg instanceof WaitResult) {
			chatView.waiting();
		}

		if (msg instanceof WaitDone) {
			chatView.waitingDone();
		}
	}

	private void respondToSendMessage(String msg) {
		String s = msg.trim();
		MessageBox.INSTANCE.add(new ReceiveMessage(context.getString(R.string.dialog_chat_recognize_error)));
	}

	private boolean isImmediateMessage(Object msg) {
		return msg instanceof SendMessage || msg instanceof RequestRemoveControls ||
				msg instanceof TransferButtonPressed || msg instanceof DividerMessage ||
				msg instanceof MessageEmitted;
	}

	public void release() {
		disposable.dispose();
	}
}
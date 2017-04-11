package finotek.global.dev.talkbank_ca.chat;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.base.mvp.event.RxEventBus;
import finotek.global.dev.talkbank_ca.chat.messages.AccountList;
import finotek.global.dev.talkbank_ca.chat.messages.AgreementRequest;
import finotek.global.dev.talkbank_ca.chat.messages.AgreementResult;
import finotek.global.dev.talkbank_ca.chat.messages.DividerMessage;
import finotek.global.dev.talkbank_ca.chat.messages.ReceiveMessage;
import finotek.global.dev.talkbank_ca.chat.messages.RecentTransaction;
import finotek.global.dev.talkbank_ca.chat.messages.SendMessage;
import finotek.global.dev.talkbank_ca.chat.messages.StatusMessage;
import finotek.global.dev.talkbank_ca.chat.messages.action.Done;
import finotek.global.dev.talkbank_ca.chat.messages.action.SignatureVerified;
import finotek.global.dev.talkbank_ca.chat.messages.control.ConfirmRequest;
import finotek.global.dev.talkbank_ca.chat.messages.transfer.TransferButtonPressed;
import finotek.global.dev.talkbank_ca.chat.messages.ui.IDCardInfo;
import finotek.global.dev.talkbank_ca.chat.messages.ui.RequestRemoveControls;
import finotek.global.dev.talkbank_ca.chat.scenario.AccountScenario;
import finotek.global.dev.talkbank_ca.chat.scenario.LoanScenario;
import finotek.global.dev.talkbank_ca.chat.scenario.Scenario;
import finotek.global.dev.talkbank_ca.chat.scenario.SendMailScenario;
import finotek.global.dev.talkbank_ca.chat.scenario.TransferScenario;
import finotek.global.dev.talkbank_ca.chat.storage.TransactionDB;
import finotek.global.dev.talkbank_ca.chat.view.ChatView;
import finotek.global.dev.talkbank_ca.model.DBHelper;
import finotek.global.dev.talkbank_ca.model.User;
import finotek.global.dev.talkbank_ca.util.DateUtil;
import kr.co.finotek.finopass.finopassvalidator.CallLogVerifier;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public enum ScenarioChannel {
	INSTANCE;

	DBHelper userDBHelper;
	RxEventBus eventBus;
	private ChatView chatView;
	private Scenario currentScenario = null;
	private Map<String, Scenario> scenarioPool;
	private Context context;

	ScenarioChannel() {
	}

	public void init(Context context, ChatView chatView, RxEventBus eventBus, DBHelper dbHelper) {
		this.context = context;
		this.chatView = chatView;
		this.userDBHelper = dbHelper;
		this.eventBus = eventBus;

		MessageBox.INSTANCE.observable
				.flatMap(msg -> {
					if (msg instanceof SendMessage || msg instanceof RequestRemoveControls ||
							msg instanceof TransferButtonPressed || msg instanceof DividerMessage) {
						return Observable.just(msg);
					} else {
						return Observable.just(msg)
								.delay(2000, TimeUnit.MILLISECONDS)
								.observeOn(AndroidSchedulers.mainThread());
					}
				})
				.subscribe(msg -> {
					updateUIOn(msg);
					onRequest(msg);
				});

		// 채팅 화면 설정
		LinearLayoutManager manager = new LinearLayoutManager(context);
		manager.setStackFromEnd(true);
		chatView.setLayoutManager(manager);

		// 초기 시나리오 진행
		this.firstScenario();

		// 시나리오 저장
		scenarioPool = new HashMap<>();
		scenarioPool.put("transfer", new TransferScenario(context, dbHelper));
		scenarioPool.put("loan", new LoanScenario(context));
		scenarioPool.put("account", new AccountScenario(context));
		scenarioPool.put("sendMail", new SendMailScenario(context));

		currentScenario = null;
	}

	public void applyScenario(String key) {
		if (scenarioPool.containsKey(key)) {
			currentScenario = scenarioPool.get(key);
			currentScenario.clear();
		} else {
			throw new RuntimeException("NoSuchScenarioError Exception: for key: " + key);
		}
	}

	private void firstScenario() {
		MessageBox.INSTANCE.add(new DividerMessage(DateUtil.currentDate()));

		double accuracy = CallLogVerifier.getCallLogPassRate(context);
		MessageBox.INSTANCE.add(new StatusMessage("맥락 데이터 분석 결과 " + String.valueOf((int) (accuracy * 100))
				+ "% 확률로 인증되었습니다."));

		userDBHelper.get(User.class)
				.subscribe(
						users -> MessageBox.INSTANCE.add(new ReceiveMessage(
								users.last().getName() + " 님 안녕하세요. 무엇을 도와드릴까요?")),
						throwable -> {
						});

	}

	private void onRequest(Object msg) {
		if (msg instanceof SendMessage) {
			SendMessage recv = (SendMessage) msg;

			if (currentScenario == null) {
				Iterator<String> keySet = scenarioPool.keySet().iterator();

				while (keySet.hasNext()) {
					String key = keySet.next();
					Scenario scenario = scenarioPool.get(key);

					if (scenario.decideOn(recv.getMessage())) {
						currentScenario = scenario;
						currentScenario.clear();
						break;
					}
				}
			}

			if (currentScenario == null) {
				this.respondToSendMessage(recv.getMessage());
			} else {
				currentScenario.onUserSend(recv.getMessage());
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

		// 최근 거래 내역
		if (msg instanceof RecentTransaction) {
			chatView.transactionList((RecentTransaction) msg);
		}

		// 계좌 리스트
		if (msg instanceof AccountList) {
			chatView.accountList((AccountList) msg);
		}

		if (msg instanceof SignatureVerified) {

		}

		if (msg instanceof RequestRemoveControls) {
			chatView.removeOf(ChatView.ViewType.AccountList);
			chatView.removeOf(ChatView.ViewType.Confirm);
		}
	}

	private String convertStrIdToString(int resId) {
		return context.getString(resId);
	}

	private void respondToSendMessage(String msg) {

		String s = msg.trim();
		if (s.equals("계좌조회") || s.equals("계좌 조회") || s.equals("최근거래내역") ||
				s.equals("최근 거래 내역") ||
				s.equals(convertStrIdToString(R.string.dialog_chat_someone_recent_transaction)) ||
				s.equals(convertStrIdToString(R.string.main_string_view_account_details))) {

			userDBHelper.get(User.class)
					.subscribe(
							users -> {
								MessageBox.INSTANCE.add(new ReceiveMessage(users.last().getName() + " 님의 최근 거래내역입니다."));
								RecentTransaction rt = new RecentTransaction(TransactionDB.INSTANCE.getTx());
								MessageBox.INSTANCE.add(rt);
							},
							throwable -> {
							});


		} else {
			MessageBox.INSTANCE.add(new ReceiveMessage("무슨 말씀인지 잘 모르겠어요."));

		}
	}
}
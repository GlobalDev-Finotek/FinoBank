package finotek.global.dev.talkbank_ca.chat;

import android.content.Context;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.base.mvp.event.RxEventBus;
import finotek.global.dev.talkbank_ca.chat.messages.AccountConfirm;
import finotek.global.dev.talkbank_ca.chat.messages.AccountList;
import finotek.global.dev.talkbank_ca.chat.messages.AgreementRequest;
import finotek.global.dev.talkbank_ca.chat.messages.AgreementResult;
import finotek.global.dev.talkbank_ca.chat.messages.ApplyScenario;
import finotek.global.dev.talkbank_ca.chat.messages.DividerMessage;
import finotek.global.dev.talkbank_ca.chat.messages.MessageEmitted;
import finotek.global.dev.talkbank_ca.chat.messages.ReceiveMessage;
import finotek.global.dev.talkbank_ca.chat.messages.RecentTransaction;
import finotek.global.dev.talkbank_ca.chat.messages.SendMessage;
import finotek.global.dev.talkbank_ca.chat.messages.StatusMessage;
import finotek.global.dev.talkbank_ca.chat.messages.WaitDone;
import finotek.global.dev.talkbank_ca.chat.messages.WaitResult;
import finotek.global.dev.talkbank_ca.chat.messages.action.Done;
import finotek.global.dev.talkbank_ca.chat.messages.control.ConfirmRequest;
import finotek.global.dev.talkbank_ca.chat.messages.control.RecoMenuRequest;
import finotek.global.dev.talkbank_ca.chat.messages.transfer.TransferButtonPressed;
import finotek.global.dev.talkbank_ca.chat.messages.ui.IDCardInfo;
import finotek.global.dev.talkbank_ca.chat.messages.ui.RequestRemoveControls;
import finotek.global.dev.talkbank_ca.chat.scenario.ContextSearch;
import finotek.global.dev.talkbank_ca.chat.scenario.LoanScenario;
import finotek.global.dev.talkbank_ca.chat.scenario.RecentTransactionScenario;
import finotek.global.dev.talkbank_ca.chat.scenario.Scenario;
import finotek.global.dev.talkbank_ca.chat.scenario.SendMailScenario;
import finotek.global.dev.talkbank_ca.chat.scenario.TransferScenario;
import finotek.global.dev.talkbank_ca.chat.view.ChatView;
import finotek.global.dev.talkbank_ca.model.DBHelper;
import finotek.global.dev.talkbank_ca.model.User;
import finotek.global.dev.talkbank_ca.util.DateUtil;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.realm.Realm;

public class MainScenario {
	private final Disposable disposable;
	private Context context;
	private RxEventBus eventBus;
	private ChatView chatView;
	private Scenario currentScenario = null;
	private Map<String, Scenario> scenarioPool;
	private DBHelper dbHelper;

	public MainScenario(Context context, ChatView chatView, RxEventBus eventBus, DBHelper dbHelper, boolean isSigned) {
		this.context = context;
		this.chatView = chatView;
		this.eventBus = eventBus;
		this.dbHelper = dbHelper;

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
		scenarioPool.put("recentTransaction", new RecentTransactionScenario(context));
		scenarioPool.put("transfer", new TransferScenario(context));
		scenarioPool.put("loan", new LoanScenario(context));
		scenarioPool.put("sendMail", new SendMailScenario(context));
		scenarioPool.put("contextSearch", new ContextSearch(context));

		currentScenario = null;
	}

	private void firstScenario() {
		Realm realm = Realm.getDefaultInstance();
		User user = realm.where(User.class).findAll().last();
		MessageBox.INSTANCE.add(new DividerMessage(DateUtil.currentDate(context)));

		ReceiveMessage intro = new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_ask_help, user.getName()));
		RecoMenuRequest req = new RecoMenuRequest();
		req.setTitle("추천메뉴");
		req.setDescription("제가 추천하는 사항은 아래 세가지입니다.\n원하시는 메뉴를 눌러주세요.");

		req.addMenu(R.drawable.icon_like, "전기료 이체", null);
		req.addMenu(R.drawable.icon_love, "여행 적금 가입", null);
		req.addMenu(R.drawable.icon_love, "자동차 대출", null);
		req.addMenu(R.drawable.icon_wow, "다음에 알려주세요.", null);
		MessageBox.INSTANCE.addAndWait(intro, req);
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

		// 추천 메뉴 요청
		if (msg instanceof RecoMenuRequest) {
			chatView.recoMenu((RecoMenuRequest) msg);
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
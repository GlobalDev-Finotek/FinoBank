package finotek.global.dev.talkbank_ca.chat;

import android.content.Context;
import android.text.format.DateFormat;

import java.text.NumberFormat;
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
import finotek.global.dev.talkbank_ca.chat.messages.control.DonateRequest;
import finotek.global.dev.talkbank_ca.chat.messages.control.RecoMenuRequest;
import finotek.global.dev.talkbank_ca.chat.messages.control.RecommendScenarioMenuRequest;
import finotek.global.dev.talkbank_ca.chat.messages.transfer.TransferButtonPressed;
import finotek.global.dev.talkbank_ca.chat.messages.ui.IDCardInfo;
import finotek.global.dev.talkbank_ca.chat.messages.ui.RequestRemoveControls;
import finotek.global.dev.talkbank_ca.chat.scenario.AccountScenario_v2;
import finotek.global.dev.talkbank_ca.chat.scenario.ContextSearch;
import finotek.global.dev.talkbank_ca.chat.scenario.DonateScenario;
import finotek.global.dev.talkbank_ca.chat.scenario.ElectricityCharge;
import finotek.global.dev.talkbank_ca.chat.scenario.HouseLoan;
import finotek.global.dev.talkbank_ca.chat.scenario.LeftScenario;
import finotek.global.dev.talkbank_ca.chat.scenario.LoanScenario;
import finotek.global.dev.talkbank_ca.chat.scenario.PocketMoney;
import finotek.global.dev.talkbank_ca.chat.scenario.RecentTransactionScenario;
import finotek.global.dev.talkbank_ca.chat.scenario.Scenario;
import finotek.global.dev.talkbank_ca.chat.scenario.SendMailScenario;
import finotek.global.dev.talkbank_ca.chat.scenario.TransferScenario;
import finotek.global.dev.talkbank_ca.chat.scenario.TravelSaving;
import finotek.global.dev.talkbank_ca.chat.storage.TransactionDB;
import finotek.global.dev.talkbank_ca.chat.view.ChatView;
import finotek.global.dev.talkbank_ca.model.DBHelper;
import finotek.global.dev.talkbank_ca.model.User;
import finotek.global.dev.talkbank_ca.user.util.AccountImageBuilder;
import finotek.global.dev.talkbank_ca.util.DateUtil;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.realm.Realm;

public class MainScenario_v2 {
	private final Disposable disposable;
	private Context context;
	private RxEventBus eventBus;
	private ChatView chatView;
	private Scenario currentScenario = null;
	private Map<String, Scenario> scenarioPool;
	private DBHelper dbHelper;
	private User user;

	public MainScenario_v2(Context context, ChatView chatView, RxEventBus eventBus, DBHelper dbHelper, boolean isSigned) {
		this.context = context;
		this.chatView = chatView;
		this.eventBus = eventBus;
		this.dbHelper = dbHelper;
		Realm realm = Realm.getDefaultInstance();
		user = realm.where(User.class).findAll().last();

        //Recommend scenario setup
		if(!LeftScenario.scenarioList.contains("E"))
        	LeftScenario.scenarioList.add("E");
		if(!LeftScenario.scenarioList.contains("P"))
			LeftScenario.scenarioList.add("P");
		if(!LeftScenario.scenarioList.contains("T"))
        	LeftScenario.scenarioList.add("T");
		if(!LeftScenario.scenarioList.contains("H"))
        	LeftScenario.scenarioList.add("H");
		if(!LeftScenario.scenarioList.contains("D"))
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
		scenarioPool.put("recentTransaction", new RecentTransactionScenario(context, dbHelper));
		scenarioPool.put("transfer", new TransferScenario(context, dbHelper));
		scenarioPool.put("loan", new LoanScenario(context));
		scenarioPool.put("account", new AccountScenario_v2(context));
		scenarioPool.put("sendMail", new SendMailScenario(context));
		scenarioPool.put("electricityCharge", new ElectricityCharge(context));
		scenarioPool.put("houseLoan", new HouseLoan(context));
		scenarioPool.put("travelSaving", new TravelSaving(context));
		scenarioPool.put("pocketMoney", new PocketMoney(context));
		scenarioPool.put("contextSearch", new ContextSearch(context));
		scenarioPool.put("donate", new DonateScenario(context));

		currentScenario = null;
	}

	private String getGreetings(){
		long temptime = System.currentTimeMillis();
		String time = DateFormat.format("HH", temptime).toString();
		int hour = Integer.parseInt(time);

		String greetingString = "";

		User user = Realm.getDefaultInstance().where(User.class).findAll().last();
		if (user != null) {
			greetingString = context.getResources().getString(R.string.main_string_v2_login_hello, user.getName());
		}

		if (hour >=6 && 13 > hour){
			greetingString = greetingString + context.getResources().getString(R.string.main_string_v2_login_hello_M)+"\n";

		}else if (hour >= 12 && 19 > hour ){
			greetingString = greetingString + context.getResources().getString(R.string.main_string_v2_login_hello_L)+"\n";

		}else if (hour >= 18) {
			greetingString = greetingString + context.getResources().getString(R.string.main_string_v2_login_hello_E)+"\n";
		}
		else if (hour >= 0  && 7 > hour ) {
			greetingString = greetingString + context.getResources().getString(R.string.main_string_v2_login_hello_N)+"\n";
		}

		greetingString = greetingString + context.getResources().getString(R.string.main_string_v2_login_notify_balance, NumberFormat.getInstance().format(TransactionDB.INSTANCE.getMainBalance()));

		return greetingString;
	}

	private void firstScenario() {
		MessageBox.INSTANCE.add(new DividerMessage(DateUtil.currentDate(context)));

        RecommendScenarioMenuRequest req = new RecommendScenarioMenuRequest(context);
		/*RecoMenuRequest req = new RecoMenuRequest();
		//req.setTitle("추천메뉴");
		req.setDescription(context.getResources().getString(R.string.main_string_v2_login_recommend_task, user.getName()));

		req.addMenu(R.drawable.icon_like, context.getResources().getString(R.string.main_string_v2_login_pay_electricity), null);
		req.addMenu(R.drawable.icon_love, context.getResources().getString(R.string.main_string_v2_login_open_saving_account), null);
		req.addMenu(R.drawable.icon_love, context.getResources().getString(R.string.main_string_v2_login_house_loan), null);
		req.addMenu(R.drawable.icon_wow, context.getResources().getString(R.string.main_string_v2_login_notify_again), null);
        */
		MessageBox.INSTANCE.addAndWait(
				new StatusMessage(context.getResources().getString(R.string.main_string_v2_result_context, user.getName())),
				new ReceiveMessage(getGreetings()),
				req
		);





		/*eventBus.getObservable()
				.subscribe(iEvent -> {
					Log.d("FINO-TB", iEvent.getClass().getName());

					if (iEvent instanceof AccuracyMeasureEvent) {

						double accuracy = ((AccuracyMeasureEvent) iEvent).getAccuracy();
						if (isSigned) {
							MessageBox.INSTANCE.add(new StatusMessage(context.getResources().getString(R.string.main_string_v2_login_hello, (int) (accuracy * 100))));
						} else {
							MessageBox.INSTANCE.add(new StatusMessage(context.getResources().getString(R.string.main_string_v2_result_context, (int) (accuracy * 100))));
						}
					}
					Realm realm = Realm.getDefaultInstance();
					User user = realm.where(User.class).findAll().last();
					MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.main_string_v2_login_hello, user.getName())));

                    long temptime = System.currentTimeMillis();
                    String time = DateFormat.format("HH", temptime).toString();
                    int hour = Integer.parseInt(time);

                    if (hour >=6 && 13 > hour){
                        MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.main_string_v2_login_hello_M)));

                    }else if (hour >= 12 && 19 > hour ){
                        MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.main_string_v2_login_hello_L)));

                    }else if (hour >= 18) {
                        MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.main_string_v2_login_hello_E)));
                    }
                    else if (hour >= 0  && 7 > hour ) {
                        MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.main_string_v2_login_hello_N)));
                    }

                    String [] proposal = {context.getResources().getString(R.string.main_string_v2_login_recommend),
                            context.getResources().getString(R.string.main_string_v2_login_recommend_list), context.getResources().getString(R.string.main_string_v2_login_recommend_task)
                    };
                    String proposalRandom =(proposal[new Random().nextInt(proposal.length)]);

                    MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.main_string_v2_login_notify_balance)));
                    MessageBox.INSTANCE.add(new ReceiveMessage(proposalRandom));


                    MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.main_string_v2_login_ask_step)));
                    MessageBox.INSTANCE.add(context.getResources().getString(R.string.main_string_v2_login_pay_electricity));
                    MessageBox.INSTANCE.add(context.getResources().getString(R.string.main_string_v2_login_open_saving_account));
                    MessageBox.INSTANCE.add(context.getResources().getString(R.string.main_string_v2_login_loan_car));
                    MessageBox.INSTANCE.add(context.getResources().getString(R.string.main_string_v2_login_notify_again));


                });*/

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

		if (msg instanceof AccountConfirm){
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

		if(msg instanceof WaitResult) {
			chatView.waiting();
		}

		if(msg instanceof WaitDone) {
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
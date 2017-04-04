package finotek.global.dev.talkbank_ca.chat;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import finotek.global.dev.talkbank_ca.base.mvp.event.AccuracyMeasureEvent;
import finotek.global.dev.talkbank_ca.base.mvp.event.RxEventBus;
import finotek.global.dev.talkbank_ca.chat.adapter.ChatSelectButtonEvent;
import finotek.global.dev.talkbank_ca.chat.messages.Account;
import finotek.global.dev.talkbank_ca.chat.messages.AccountList;
import finotek.global.dev.talkbank_ca.chat.messages.Agreement;
import finotek.global.dev.talkbank_ca.chat.messages.AgreementRequest;
import finotek.global.dev.talkbank_ca.chat.messages.AgreementResult;
import finotek.global.dev.talkbank_ca.chat.messages.ConfirmRequest;
import finotek.global.dev.talkbank_ca.chat.messages.DividerMessage;
import finotek.global.dev.talkbank_ca.chat.messages.IDCardInfo;
import finotek.global.dev.talkbank_ca.chat.messages.ReceiveMessage;
import finotek.global.dev.talkbank_ca.chat.messages.RecentTransaction;
import finotek.global.dev.talkbank_ca.chat.messages.Succeeded;
import finotek.global.dev.talkbank_ca.chat.messages.RequestSignature;
import finotek.global.dev.talkbank_ca.chat.messages.RequestTakeIDCard;
import finotek.global.dev.talkbank_ca.chat.messages.RequestTransfer;
import finotek.global.dev.talkbank_ca.chat.messages.SendMessage;
import finotek.global.dev.talkbank_ca.chat.messages.SignaturePassed;
import finotek.global.dev.talkbank_ca.chat.messages.StatusMessage;
import finotek.global.dev.talkbank_ca.chat.messages.Transaction;
import finotek.global.dev.talkbank_ca.chat.view.ChatView;
import finotek.global.dev.talkbank_ca.util.DateUtil;

class Scenario {
    private enum Step {
        // 아무것도 아님
        None,

        // 계좌 개설
        Account, AccountCheckIDCard, AccountTakeSign, AccountSucceeded,

        // 소액담보대출
        Loan, LoanInputMoney, LoanInputAddress, LoanSucceeded,

        // 계좌 이체
        Transfer
    }

    private final MessageBox messageBox;
    private Context context;
    private ChatView chatView;

    private Step step = Step.None;
    private boolean isConfirmAppear = false;

    Scenario(Context context, ChatView chatView, MessageBox messageBox) {
        this.context = context;
        this.chatView = chatView;
        this.messageBox = messageBox;

        // 메시지 박스 설정
        messageBox.getObservable().subscribe(this::onNewMessage);

        // 채팅 설정
        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setStackFromEnd(true);
        chatView.setLayoutManager(manager);

        // 초기 메시지 출력
        messageBox.add(new DividerMessage(DateUtil.currentDate()));
        RxEventBus.getInstance().getObservable()
            .subscribe(iEvent -> {
                if (iEvent instanceof AccuracyMeasureEvent) {
                    double accuracy = ((AccuracyMeasureEvent) iEvent).getAccuracy();
                    messageBox.add(new StatusMessage("맥락 데이터 분석 결과 " + String.valueOf((int) (accuracy * 100))
                        + "% 확률로 인증되었습니다."));
                }
            });

        messageBox.add(new ReceiveMessage("홍길동님 안녕하세요. 무엇을 도와드릴까요?"));
    }

    private void onNewMessage(Object msg){
        // 보낸 메시지
        if(msg instanceof SendMessage) {
            SendMessage recv = (SendMessage) msg;
            if(recv.getIcon() == -1) {
                chatView.sendMessage(recv.getMessage());
            } else {
                chatView.sendMessage(recv.getMessage(), recv.getIcon());
            }
            this.respondToSendMessage(recv.getMessage());
        }

        // 받은 메시지
        if(msg instanceof ReceiveMessage) {
            ReceiveMessage recv = (ReceiveMessage) msg;
            chatView.receiveMessage(recv.getMessage());
        }

        // 상태 메시지
        if(msg instanceof StatusMessage) {
            StatusMessage recv = (StatusMessage) msg;
            chatView.statusMessage(recv.getMessage());
        }

        // 구분선
        if(msg instanceof DividerMessage) {
            DividerMessage recv = (DividerMessage) msg;
            chatView.dividerMessage(recv.getMessage());
        }

        // 예, 아니오 선택 요청
        if(msg instanceof ConfirmRequest) {
            isConfirmAppear = true;

            ChatSelectButtonEvent ev = new ChatSelectButtonEvent();
            ev.setConfirmAction(aVoid -> {
                chatView.removeOf(ChatView.ViewType.Confirm);
                isConfirmAppear = false;
                messageBox.add(new SendMessage("예"));
            });
            ev.setCancelAction(aVoid -> {
                chatView.removeOf(ChatView.ViewType.Confirm);
                isConfirmAppear = false;
                messageBox.add(new SendMessage("아니오"));
            });
            chatView.confirm(ev);
        }

        // 신분증 스캔 결과
        if(msg instanceof IDCardInfo) {
            chatView.showIdCardInfo((IDCardInfo) msg);
            messageBox.add(new ReceiveMessage("위 내용이 맞으세요?"));
            messageBox.add(new ConfirmRequest());
        }

        // 약관 동의 화면
        if(msg instanceof AgreementRequest) {
            messageBox.add(new ReceiveMessage("약관 확인 및 동의를 진행해 주세요."));
            chatView.agreement((AgreementRequest) msg);
        }

        // 약관 결과
        if(msg instanceof AgreementResult) {
            chatView.agreementResult();
            messageBox.add(new ReceiveMessage("대출 신청이 정상적으로 처리되어\n입금 완료 되었습니다.\n더 필요한 사항이 있으세요?"));
        }

        // 최근 거래 내역
        if(msg instanceof RecentTransaction) {
            messageBox.add(new ReceiveMessage("홍길동님의 최근 거래내역입니다."));
            chatView.transactionList((RecentTransaction) msg);
        }

        // 계좌 리스트
        if(msg instanceof AccountList) {
            chatView.accountList((AccountList) msg);
        }

        if(msg instanceof Succeeded) {
            switch (step) {
                case Transfer:
                    chatView.removeOf(ChatView.ViewType.AccountList);
                    chatView.receiveMessage("어머니(010-5678-1234)님에게 100,000원을\n이체하였습니다.\n현재 계좌 잔액은 3,270,000원입니다.\n\n더 필요한 사항이 있으세요?");
                    step = Step.None;
                break;
                case AccountSucceeded:
                    new ReceiveMessage("계좌개설이 완료 되었습니다.\n더 필요한 사항이 있으세요?\n");
                break;
                case LoanSucceeded:
                    messageBox.add(new AgreementResult());
                    messageBox.add(new ReceiveMessage("대출 신청이 정상적으로 처리되어\n입금 완료 되었습니다.\n\n더 필요한 사항이 있으세요?"));
                break;
            }
        }
    }

    private void respondToSendMessage(String msg) {
        switch (msg.trim()) {
            case "계좌 개설":
            case "계좌개설":
                createAccount();
                step = Step.Account;
                break;
            case "네":
            case "예":
                if(isConfirmAppear)
                    chatView.removeOf(ChatView.ViewType.Confirm);

                switch (step) {
                    case Account:
                        messageBox.add(new ReceiveMessage("계좌 개설 시 본인 확인 용도로 주민등록증이나\n운전면허증이 필요합니다.\n 준비가 되셨으면 신분증 촬영을 진행해 주세요."));
                        messageBox.add(new RequestTakeIDCard());
                        step = Step.AccountCheckIDCard;
                        break;
                    case Loan:
                        messageBox.add(new ReceiveMessage("집 주소를 입력해 주세요."));
                        step = Step.LoanInputAddress;
                        break;
                    default:
                        messageBox.add(new ReceiveMessage("무슨 의미인가요? 현재 진행중인 내용이 없습니다."));
                        break;
                }
                break;
            case "아니오":
                if(isConfirmAppear)
                    chatView.removeOf(ChatView.ViewType.Confirm);

                switch (step) {
                    case Account:
                        messageBox.add(new ReceiveMessage("계좌 개설 진행을 취소했습니다."));
                        break;
                    case Loan:
                        messageBox.add(new ReceiveMessage("소액담보대출 진행을 취소했습니다."));
                        break;
                }

                step = Step.None;
                break;
            case "최근거래내역" :
                ArrayList<Transaction> tx = new ArrayList<>();
                tx.add(new Transaction("어머니", 0, 200000, 3033800, new DateTime()));
                tx.add(new Transaction("박예린", 0, 100000, 3233800, new DateTime()));
                tx.add(new Transaction("김가람", 0, 36200, 3333800, new DateTime()));
                tx.add(new Transaction("김이솔", 1, 100000, 3370000, new DateTime()));
                tx.add(new Transaction("김가람", 0, 15500, 3270000, new DateTime()));

                RecentTransaction rt = new RecentTransaction();
                rt.setTransactions(tx);
                messageBox.add(rt);
                break;
            case "계좌이체" :
            case "계좌 이체" :
                List<Account> accounts = new ArrayList<>();
                accounts.add(new Account("어머니", "2017년 01월 25일", "200,000원 이체", true));
                accounts.add(new Account("박예린", "2017년 01월 11일", "100,000원 이체", false));
                accounts.add(new Account("김가람", "2017년 01월 11일", "36,200원 이체", false));
                accounts.add(new Account("김이솔", "2017년 01월 10일", "100,000원 입금", false));

                messageBox.add(new ReceiveMessage("이체하실 분을 선택해 주세요."));
                messageBox.add(new AccountList(accounts));
                messageBox.add(new RequestTransfer());

                step = Step.Transfer;
                break;
            case "집을 담보로 대출 받고 싶어":
            case "소액담보대출":
            case "소액 담보 대출":
                messageBox.add(new ReceiveMessage("거래 내역 확인 결과, “원데이 대출”을 추천합니다.\n최대 5천만 원 이내, 최저 금리 2.0%입니다.\n\n대출을 신청하시겠습니까?"));
                messageBox.add(new ConfirmRequest());
                step = Step.Loan;
                break;
            default:
                switch (step) {
                    case LoanInputAddress:
                        messageBox.add(new ReceiveMessage("입력하신 주소로 담보 설정 시 최대 5천만 원까지\n2.0%의 금리로 대출 가능합니다.\n\n대출 신청 금액을 입력해 주세요."));
                        step = Step.LoanInputMoney;
                        break;
                    case LoanInputMoney:
                        messageBox.add(new ReceiveMessage("약관 확인 및 동의를 진행해주세요."));

                        List<Agreement> agreements = new ArrayList<>();
                        Agreement required = new Agreement(100, "필수 항목 전체 동의");
                        required.addChild(new Agreement(101, "대출 서비스 이용 약관"));
                        required.addChild(new Agreement(102, "개인(신용)정보 조회 및 이용 제공 동의"));
                        required.addChild(new Agreement(103, "대출거래 약정서"));
                        required.addChild(new Agreement(104, "계약 안내사항 및 유의사항"));

                        Agreement optional = new Agreement(200, "선택항목 전체 동의");
                        optional.addChild(new Agreement(201, "고객 정보 활용 동의"));

                        agreements.add(required);
                        agreements.add(optional);

                        messageBox.add(new AgreementRequest(agreements));
                        step = Step.LoanSucceeded;
                        break;
                    case AccountCheckIDCard:
                        messageBox.add(new ReceiveMessage("마지막으로 사용자 등록 시 입력한 자필 서명을\n표시된 영역 안에 손톱이 아닌 손가락 끝을 사용하여\n서명해 주세요."));
                        messageBox.add(new RequestSignature());
                        break;
                    default:
                        messageBox.add(new ReceiveMessage("무슨 말씀인지 잘 모르겠어요."));
                        break;
                }
                break;
        }
    }

    private void createAccount() {
        messageBox.add(new ReceiveMessage("홍길동님께는 `스마트 계좌`를 추천드립니다\n계좌 개설을 진행하시겠습니까?"));
        messageBox.add(new ConfirmRequest());
    }
}
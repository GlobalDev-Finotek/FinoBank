package finotek.global.dev.talkbank_ca.chat.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import finotek.global.dev.talkbank_ca.chat.adapter.ChatAdapter;
import finotek.global.dev.talkbank_ca.chat.adapter.DataWithType;
import finotek.global.dev.talkbank_ca.chat.messages.AccountList;
import finotek.global.dev.talkbank_ca.chat.messages.AgreementRequest;
import finotek.global.dev.talkbank_ca.chat.messages.DividerMessage;
import finotek.global.dev.talkbank_ca.chat.messages.ReceiveMessage;
import finotek.global.dev.talkbank_ca.chat.messages.RecentTransaction;
import finotek.global.dev.talkbank_ca.chat.messages.SendMessage;
import finotek.global.dev.talkbank_ca.chat.messages.StatusMessage;
import finotek.global.dev.talkbank_ca.chat.messages.control.ConfirmRequest;
import finotek.global.dev.talkbank_ca.chat.messages.control.RecoMenuRequest;
import finotek.global.dev.talkbank_ca.chat.messages.ui.IDCardInfo;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;

/**
 * @author david lee at finotek.
 */
public class ChatView extends RecyclerView {
	private ChatAdapter adapter;

	public ChatView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);

		this.adapter = new ChatAdapter();
        setAdapter(adapter);

		this.addChatViewBuilder(ViewType.Send.ordinal(), new SendViewBuilder());
		this.addChatViewBuilder(ViewType.IconicSend.ordinal(), new IconicSendViewBuilder(context));
		this.addChatViewBuilder(ViewType.Receive.ordinal(), new ReceiveViewBuilder());
		this.addChatViewBuilder(ViewType.Status.ordinal(), new StatusViewBuilder());
		this.addChatViewBuilder(ViewType.Divider.ordinal(), new DividerViewBuilder());
		this.addChatViewBuilder(ViewType.Confirm.ordinal(), new ConfirmViewBuilder());
        this.addChatViewBuilder(ViewType.RecoMenu.ordinal(), new RecoMenuViewBuilder(context));
		this.addChatViewBuilder(ViewType.IDCard.ordinal(), new IDCardViewBuilder());

		this.addChatViewBuilder(ViewType.AccountList.ordinal(), new AccountListViewBuilder(context));
		this.addChatViewBuilder(ViewType.Agreement.ordinal(), new AgreementBuilder(context));
		this.addChatViewBuilder(ViewType.AgreementResult.ordinal(), new AgreementResultBuilder());
		this.addChatViewBuilder(ViewType.RecentTransaction.ordinal(), new TransactionViewBuilder(context));

        this.addChatViewBuilder(ViewType.Wait.ordinal(), new WaitViewBuilder());
	}

	public void showIdCardInfo(IDCardInfo info) {
		addMessage(ViewType.IDCard.ordinal(), info);
	}

	public void sendMessage(String msg) {
		addMessage(ViewType.Send.ordinal(), new SendMessage(msg));
	}

	public void sendMessage(String msg, int icon) {
		addMessage(ViewType.IconicSend.ordinal(), new SendMessage(msg, icon));
	}

	public void receiveMessage(String msg) {
		addMessage(ViewType.Receive.ordinal(), new ReceiveMessage(msg));
	}

	public void statusMessage(String msg) {
		addMessage(ViewType.Status.ordinal(), new StatusMessage(msg));
	}

	public void dividerMessage(String msg) {
		addMessage(ViewType.Divider.ordinal(), new DividerMessage(msg));
	}

	public void agreement(AgreementRequest msg) {
		addMessage(ViewType.Agreement.ordinal(), msg);
	}

	public void agreementResult() {
		addMessage(ViewType.AgreementResult.ordinal(), null);
	}

	public void accountList(AccountList accountList) {
		addMessage(ViewType.AccountList.ordinal(), accountList);
	}

    public void waiting() {
        addMessage(ViewType.Wait.ordinal(), null);
    }

    public void waitingDone() {
        removeOf(ViewType.Wait);
    }

	public void
	transactionList(RecentTransaction data) {
		addMessage(ViewType.RecentTransaction.ordinal(), data);
	}

	public void confirm(ConfirmRequest ev) {
		addMessage(ViewType.Confirm.ordinal(), ev);
	}

	public void recoMenu(RecoMenuRequest req) {
        addMessage(ViewType.RecoMenu.ordinal(), req);
    }

	private void addChatViewBuilder(int viewType, ViewBuilder builder) {
		adapter.addChatViewBuilder(viewType, builder);
	}

	private void addMessage(int viewType, Object item) {
		adapter.addChatItem(new DataWithType(viewType, item));
	}

	public void removeOf(ViewType viewType) {
		adapter.removeChatItem(viewType.ordinal());
	}

	public void scrollToBottom() {
		getLayoutManager().scrollToPosition(0);
	}

	public enum ViewType {
		Send, IconicSend, Receive, Divider, Status,
		Confirm, RecoMenu,
		IDCard, RecentTransaction, AccountList,
		Agreement, AgreementResult,
        Wait
	}

	public interface ViewBuilder<T> {
		RecyclerView.ViewHolder build(ViewGroup parent);

		void bind(RecyclerView.ViewHolder viewHolder, T data);

		void onDelete();
	}
}
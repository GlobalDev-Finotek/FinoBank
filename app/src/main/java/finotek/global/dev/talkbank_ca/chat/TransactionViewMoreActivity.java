package finotek.global.dev.talkbank_ca.chat;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.messages.Transaction;
import finotek.global.dev.talkbank_ca.chat.storage.TransactionDB;
import finotek.global.dev.talkbank_ca.databinding.ActivityTransactionViewMoreBinding;
import finotek.global.dev.talkbank_ca.databinding.ChatItemTransactionBinding;

public class TransactionViewMoreActivity extends AppCompatActivity {

	private ActivityTransactionViewMoreBinding rootBinding;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		rootBinding = DataBindingUtil.setContentView(this, R.layout.activity_transaction_view_more);
		ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
				.findViewById(android.R.id.content)).getChildAt(0);

		for (Transaction tx : TransactionDB.INSTANCE.getTx()) {
			View view = LayoutInflater.from(this).inflate(R.layout.chat_item_transaction, viewGroup, false);
			ChatItemTransactionBinding binding = ChatItemTransactionBinding.bind(view);
			binding.setItem(tx);

			binding.transferBtn.setVisibility(View.GONE);
			rootBinding.list.addView(view);
		}
	}
}

package finotek.global.dev.talkbank_ca.chat.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.MessageBox;
import finotek.global.dev.talkbank_ca.chat.messages.Agreement;
import finotek.global.dev.talkbank_ca.chat.messages.AgreementRequest;
import finotek.global.dev.talkbank_ca.chat.messages.ReceiveMessage;
import finotek.global.dev.talkbank_ca.chat.messages.action.ShowPdfView;
import finotek.global.dev.talkbank_ca.chat.messages.ui.RequestSignature;
import finotek.global.dev.talkbank_ca.databinding.ChatAgreementBinding;
import finotek.global.dev.talkbank_ca.databinding.ChatItemAgreementBinding;

public class AgreementBuilder implements ChatView.ViewBuilder<AgreementRequest> {
    private Context context;
    private TreeMap<Agreement, List<ChatItemAgreementBinding>> agreementViewTree = new TreeMap<>();

    public AgreementBuilder(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder build(ViewGroup parent) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_agreement, parent, false));
    }

    @Override
    public void bind(RecyclerView.ViewHolder viewHolder, AgreementRequest data) {
        ViewHolder holder = (ViewHolder) viewHolder;
        holder.binding.agreements.removeAllViews();

        for (int i = 0; i < data.getAgreements().size(); i++) {
            boolean isNewCheck = i == data.getAgreements().size() - 1;
            // parent
            Agreement agr = data.getAgreements().get(i);
            agr.setNewCheck(isNewCheck);
            addAgreement(holder.binding.agreements, agr);

            // child
            if (!agr.isEmptyChild()) {
                addChildAgreement(holder.binding.agreements, agr);
            }

        }

    }

    @Override
    public void onDelete() {

    }

    private void addChildAgreement(LinearLayout holder, Agreement parentAgreement) {

        ArrayList<ChatItemAgreementBinding> childAgreementViewBindings = new ArrayList<>();

        for (Agreement childAgreement : parentAgreement.getChild()) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_agreement, holder, false);
            ChatItemAgreementBinding binding = ChatItemAgreementBinding.bind(view);
            binding.setItem(childAgreement);
            holder.addView(view);
            childAgreementViewBindings.add(binding);

            binding.textView.setClickable(true);
            Runnable r = new Runnable(){
                @Override
                public void run() {
                    MessageBox.INSTANCE.add(new ShowPdfView(childAgreement.getName(), childAgreement.getPdfAsset()));
                }
            };

            RxView.clicks(binding.textView)
                    .throttleFirst(2000, TimeUnit.MILLISECONDS)
                    .subscribe(aVoid -> r.run());

            RxView.clicks(binding.arrow)
                    .throttleFirst(2000, TimeUnit.MILLISECONDS)
                    .subscribe(aVoid -> r.run());
        }

        agreementViewTree.put(parentAgreement, childAgreementViewBindings);

    }

    private void addAgreement(LinearLayout holder, Agreement agreement) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_item_agreement, holder, false);
        ChatItemAgreementBinding binding = ChatItemAgreementBinding.bind(view);
        binding.setItem(agreement);

        binding.textView.setFontType(context, 1);
        binding.arrow.setVisibility(View.INVISIBLE);

        binding.checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            List<ChatItemAgreementBinding> bindings = agreementViewTree.get(agreement);
            if (isChecked) {
                for (ChatItemAgreementBinding b : bindings) {
                    b.checkbox.setChecked(true);
                    b.checkbox.setSelected(true);
                }
            } else {
                for (ChatItemAgreementBinding b : bindings) {
                    b.checkbox.setChecked(false);
                    b.checkbox.setSelected(false);
                }
            }
        });


        holder.addView(view);
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        ChatAgreementBinding binding;

        ViewHolder(View itemView) {
            super(itemView);
            binding = ChatAgreementBinding.bind(itemView);
            RxView.clicks(binding.signButton)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .subscribe(aVoid -> {
                    MessageBox.INSTANCE.add(new ReceiveMessage("사용자 등록 시 입력한 자필 서명을 표시된 영역 안에\n손톱이 아닌 손가락 끝을 사용하여 서명해 주세요."));
                    MessageBox.INSTANCE.add(new RequestSignature());
                });
        }
    }
}
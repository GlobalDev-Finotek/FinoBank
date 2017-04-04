package finotek.global.dev.talkbank_ca.chat.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.messages.Agreement;
import finotek.global.dev.talkbank_ca.chat.messages.AgreementRequest;
import finotek.global.dev.talkbank_ca.databinding.ChatAgreementBinding;
import finotek.global.dev.talkbank_ca.databinding.ChatItemAgreementBinding;
import finotek.global.dev.talkbank_ca.util.Converter;

public class AgreementBuilder implements ChatView.ViewBuilder<AgreementRequest> {
    private Context context;

    private class ViewHolder extends RecyclerView.ViewHolder {
        ChatAgreementBinding binding;

        ViewHolder(View itemView) {
            super(itemView);
            binding = ChatAgreementBinding.bind(itemView);
        }
    }

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

        for(int i = 0; i < data.getAgreements().size(); i++){
            boolean isNewCheck = i == data.getAgreements().size() - 1;

            // parent
            Agreement agr = data.getAgreements().get(i);
            agr.setNewCheck(isNewCheck);
            addAgreement(holder.binding.agreements, agr);

            // child
            if(!agr.isEmptyChild())
                for(Agreement child : agr.getChild()) {
                    addAgreement(holder.binding.agreements, child);
                }
        }
    }

    @Override
    public void onDelete() {

    }

    private void addAgreement(LinearLayout holder, Agreement agreement){
        View view = LayoutInflater.from(context).inflate(R.layout.chat_item_agreement, holder, false);
        ChatItemAgreementBinding binding = ChatItemAgreementBinding.bind(view);
        binding.setItem(agreement);

        if(agreement.isParent()) {
            binding.textView.setFontType(context, 1);
            binding.arrow.setVisibility(View.INVISIBLE);
        }

        holder.addView(view);
    }
}
package finotek.global.dev.talkbank_ca.chat.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import finotek.global.dev.talkbank_ca.chat.view.ChatView;

/**
 * @author david lee at finotek.
 * ViewBuilder와 데이터 영역의 책임을 외부로 내보냄으로서 채팅 엘리먼트 요소가 계속 추가될 수 있게끔 구성되어있다
* */
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private SparseArray<ChatView.ViewBuilder> builders;
    private ArrayList<ChatItemWithType> items;

    public ChatAdapter() {
        builders = new SparseArray<>();
        items = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ChatView.ViewBuilder builder = builders.get(viewType);
        if(builder != null)
            return builder.build(parent);
        else
            throw new RuntimeException("No such View Builder Exception \n you should implement a class with the ChatView.ViewHolder and set it into ChatView");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatView.ViewBuilder builder = builders.get(getItemViewType(position));
        if(builder != null) {
            if(position == 0) {
                RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
                DisplayMetrics dm = holder.itemView.getResources().getDisplayMetrics();
                int top = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, dm);
                int bottom = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, dm);
                lp.setMargins(0, top, 0, bottom);
                holder.itemView.setLayoutParams(lp);
            }

            builder.bind(holder, items.get(position).getItem());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getViewType();
    }

    /**
     * @param viewType RecylcerView의 메서드들에서 쓰일 viewType을 정의한다, SparseArray에 데이터를 넣기 때문에 이 값은 중복되어서는 안된다
     * @param builder ViewHolder를 생성하는 build메서드와 뷰와 데이터를 연결하는 bind메서드를 구현한 객체
     *
     * ChatView.addMessage로 메시지를 넣기 전에 ViewBuilder가 반드시 등록되어야 한다
     * */
    public void addChatViewBuilder(int viewType, ChatView.ViewBuilder builder) {
        builders.put(viewType, builder);
    }

    public void addChatItem(ChatItemWithType item){
        items.add(item);
    }

    public void removeItem(int position) {
        items.remove(position);
    }
}
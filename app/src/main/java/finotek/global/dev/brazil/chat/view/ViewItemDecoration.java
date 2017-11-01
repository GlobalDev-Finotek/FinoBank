package finotek.global.dev.brazil.chat.view;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by magyeong-ug on 27/05/2017.
 */

public class ViewItemDecoration extends RecyclerView.ItemDecoration {

	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

		RecyclerView.ViewHolder vh = parent.getChildViewHolder(view);

		if (vh instanceof ReceiveViewBuilder.ReceiveViewHolder) {
			outRect.set(0, 2, 0, 2);
		} else if (vh instanceof SendViewBuilder.SendViewHolder) {
			outRect.set(0, 4, 0, 4);
		}

	}
}

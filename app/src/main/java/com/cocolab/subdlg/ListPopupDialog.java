package com.cocolab.subdlg;

import java.util.List;

import com.cocolap.talkbank.MainApplication;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class ListPopupDialog extends Dialog {
	private Context CTX;
	private MainApplication mainApp;
	private int displayWidth, displayHeight;
	private String popupTitle;
	private List<String> popupArrayList;
	private LinearLayout mainLayout;
	private int backColor1 = Color.rgb(245, 245, 245);
	private int backColor2 = Color.rgb(235, 235, 235);
	private View.OnClickListener selectClickListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
		displayWidth = getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? Math.max(metrics.widthPixels, metrics.heightPixels) : Math.min(metrics.widthPixels, metrics.heightPixels);
		displayHeight = getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? Math.min(metrics.widthPixels, metrics.heightPixels) : Math.max(metrics.widthPixels, metrics.heightPixels);

		mainLayout = new LinearLayout(CTX);
		LayoutParams linLayoutParam = new LayoutParams((int)(displayWidth*0.8), (int)(displayHeight/3));
		mainLayout.setLayoutParams(linLayoutParam);
		mainLayout.setOrientation(LinearLayout.VERTICAL);
		mainLayout.setBackgroundColor(Color.LTGRAY);

		TextView popupTItle = new TextView(CTX);
		LinearLayout.LayoutParams lp_popupTItle = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, mainApp.h(80));
		popupTItle.setLayoutParams(lp_popupTItle);
		popupTItle.setText(popupTitle);
		popupTItle.setTextColor(Color.WHITE);
		popupTItle.setTextSize(TypedValue.COMPLEX_UNIT_PX, mainApp.w(32));
		popupTItle.setBackgroundColor(Color.DKGRAY);
		popupTItle.setGravity(Gravity.CENTER);
		popupTItle.setPadding(mainApp.w(10), 0, mainApp.w(10), 0);

		ScrollView scroll = new ScrollView(CTX);
		scroll.setLayoutParams(new LayoutParams((int)(displayWidth*0.1), (int)(displayHeight)));
		
		LinearLayout crlvLayout = new LinearLayout(CTX);
		LayoutParams lp_crlvLayout = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		crlvLayout.setLayoutParams(lp_crlvLayout);
		crlvLayout.setOrientation(LinearLayout.VERTICAL);
		if (popupArrayList != null || popupArrayList.size() > 0) {
			int i;
			for (i=0; i<popupArrayList.size(); i++){
				TextView popupText = new TextView(CTX);
				LinearLayout.LayoutParams lp_popupText = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				popupText.setLayoutParams(lp_popupText);
				popupText.setText(popupArrayList.get(i)+"%");
				popupText.setTextColor(Color.DKGRAY);
				popupText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mainApp.w(32));
				popupText.setBackgroundColor(i%2==0?backColor1:backColor2);
				popupText.setGravity(Gravity.CENTER);
				popupText.setPadding(mainApp.w(10), mainApp.h(20), mainApp.w(10), mainApp.h(20));
				popupText.setTag(popupArrayList.get(i));
				popupText.setOnClickListener(selectClickListener);
				crlvLayout.addView(popupText);
			}
		}
		scroll.addView(crlvLayout);
		
		if (popupTitle != null) mainLayout.addView(popupTItle);
		mainLayout.addView(scroll);
		setContentView(mainLayout);
	}

	public ListPopupDialog(Context context, String title, List<String> popupList, View.OnClickListener selectListener) {
		super(context);
		CTX = context;
		mainApp = (MainApplication) context.getApplicationContext();
		this.popupTitle = title;
		this.popupArrayList = popupList;
		this.selectClickListener = selectListener;
	}
}
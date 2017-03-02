package com.cocolab.subdlg;

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
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cocolap.talkbank.MainApplication;
import com.cocolap.talkbank.R;

public class notice_Popup_Dialog extends Dialog {
	private Context CTX;
	private MainApplication mainApp;
	private int displayWidth, displayHeight, bgColor;
	private String popupTitle, messageText;
	private LinearLayout mainLayout;
	private View.OnClickListener confirmClickListener;
	private String ButtonName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

		DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
		displayWidth = getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? Math.max(metrics.widthPixels, metrics.heightPixels) : Math.min(metrics.widthPixels, metrics.heightPixels);
		displayHeight = getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? Math.min(metrics.widthPixels, metrics.heightPixels) : Math.max(metrics.widthPixels, metrics.heightPixels);

		mainLayout = new LinearLayout(CTX);
		LayoutParams linLayoutParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mainLayout.setLayoutParams(linLayoutParam);
		mainLayout.setOrientation(LinearLayout.VERTICAL);
		mainLayout.setBackgroundColor(bgColor);
//		mainLayout.setBackgroundResource(R.drawable.border_dkgray_blank);

		TextView popupTV = new TextView(CTX);
		LinearLayout.LayoutParams lp_popupTItle = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, mainApp.h(90));
		popupTV.setLayoutParams(lp_popupTItle);
		popupTV.setText(popupTitle);
		popupTV.setTextColor(Color.BLACK);
		popupTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, mainApp.w(40));
		popupTV.setGravity(Gravity.CENTER);
		popupTV.setPadding(mainApp.w(10), 0, mainApp.w(10), 0);

		TextView messageTV = new TextView(CTX);
		LinearLayout.LayoutParams lp_messageText = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, mainApp.h(140));
		messageTV.setLayoutParams(lp_messageText);
		messageTV.setText(messageText);
		messageTV.setTextColor(Color.BLACK);
		messageTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, mainApp.w(32));
		messageTV.setGravity(Gravity.CENTER);
		messageTV.setPadding(mainApp.w(10), 0, mainApp.w(10), 0);

		LinearLayout imageLayout = new LinearLayout(CTX);
		LayoutParams lp_imageLayout = new LayoutParams(LayoutParams.MATCH_PARENT, mainApp.h(5));
		imageLayout.setLayoutParams(lp_imageLayout);
		imageLayout.setGravity(Gravity.CENTER);
		
		ImageView divLine = new ImageView(CTX);
		LinearLayout.LayoutParams lp_divLine = new LinearLayout.LayoutParams(mainApp.w(640), LayoutParams.MATCH_PARENT);
		lp_divLine.leftMargin = mainApp.w(20);
		lp_divLine.rightMargin = mainApp.w(20);
		divLine.setLayoutParams(lp_divLine);
		divLine.setImageResource(R.drawable.list_div_line);
		divLine.setScaleType(ScaleType.FIT_CENTER);
		imageLayout.addView(divLine);
		
		TextView buttonTV = new TextView(CTX);
		LinearLayout.LayoutParams lp_buttonTV = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, mainApp.h(100));
		buttonTV.setLayoutParams(lp_buttonTV);
		buttonTV.setText(ButtonName);
		buttonTV.setTextColor(Color.rgb(33, 150, 243));
		buttonTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, mainApp.w(38));
		buttonTV.setGravity(Gravity.CENTER);
		buttonTV.setOnClickListener(confirmClickListener);

		mainLayout.addView(popupTV);
		mainLayout.addView(messageTV);
		mainLayout.addView(imageLayout);
		mainLayout.addView(buttonTV);
		
		setContentView(mainLayout);
	}

	public notice_Popup_Dialog(Context context, String title, String message, int bgColor, View.OnClickListener confirmListener, String ButtonName) {
		super(context);
		CTX = context;
		mainApp = (MainApplication) context.getApplicationContext();
		this.popupTitle = title;
		this.messageText = message;
		this.bgColor = bgColor;
		this.confirmClickListener = confirmListener;
		this.ButtonName = ButtonName;
}
}
package com.cocolab.subdlg;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cocolap.talkbank.MainApplication;
import com.cocolap.talkbank.R;

public class progress_Popup_Dialog extends Dialog {
	private Context CTX;
	private MainApplication mainApp;
	private int displayWidth, displayHeight;
	private String popupTitle, messageText1, messageText2;
	private LinearLayout mainLayout;
	private ProgressBar progressBar;

	@Override
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
		displayWidth = getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? Math.max(metrics.widthPixels, metrics.heightPixels) : Math.min(metrics.widthPixels, metrics.heightPixels);
		displayHeight = getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? Math.min(metrics.widthPixels, metrics.heightPixels) : Math.max(metrics.widthPixels, metrics.heightPixels);

		mainLayout = new LinearLayout(CTX);
		LayoutParams linLayoutParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mainLayout.setLayoutParams(linLayoutParam);
		mainLayout.setOrientation(LinearLayout.VERTICAL);
		mainLayout.setBackgroundColor(Color.rgb(255, 255, 255));

		TextView popupTV = new TextView(CTX);
		LinearLayout.LayoutParams lp_popupTItle = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, mainApp.h(100));
		popupTV.setLayoutParams(lp_popupTItle);
		popupTV.setText(popupTitle);
		popupTV.setTextColor(Color.BLACK);
		popupTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, mainApp.w(40));
		popupTV.setGravity(Gravity.CENTER);
		popupTV.setPadding(mainApp.w(10), 0, mainApp.w(10), 0);
		mainLayout.addView(popupTV);

		TextView messageTV1 = new TextView(CTX);
		LinearLayout.LayoutParams lp_messageText1 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, mainApp.h(100));
		messageTV1.setLayoutParams(lp_messageText1);
		messageTV1.setText(messageText1);
		messageTV1.setTextColor(Color.BLACK);
		messageTV1.setTextSize(TypedValue.COMPLEX_UNIT_PX, mainApp.w(32));
		messageTV1.setGravity(Gravity.CENTER);
		messageTV1.setPadding(mainApp.w(10), 0, mainApp.w(10), 0);
		mainLayout.addView(messageTV1);

		TextView messageTV2 = new TextView(CTX);
		LinearLayout.LayoutParams lp_messageText2 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, mainApp.h(100));
		messageTV2.setLayoutParams(lp_messageText2);
		messageTV2.setText(messageText2);
		messageTV2.setTextColor(Color.BLACK);
		messageTV2.setTextSize(TypedValue.COMPLEX_UNIT_PX, mainApp.w(32));
		messageTV2.setGravity(Gravity.CENTER);
		messageTV2.setPadding(mainApp.w(10), 0, mainApp.w(10), 0);
		mainLayout.addView(messageTV2);

		LinearLayout progLayout = new LinearLayout(CTX);
		LinearLayout.LayoutParams lp_progressLayout = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, mainApp.h(100));
		progLayout.setLayoutParams(lp_progressLayout);
		progLayout.setGravity(Gravity.CENTER);
		
		progressBar = new ProgressBar(CTX, null, android.R.attr.progressBarStyleHorizontal);
		LinearLayout.LayoutParams lp_progressBar = new LinearLayout.LayoutParams(mainApp.w(640), LayoutParams.MATCH_PARENT);
		lp_progressBar.topMargin = mainApp.h(40);
		lp_progressBar.leftMargin = mainApp.w(50);
		lp_progressBar.bottomMargin = mainApp.h(50);
		lp_progressBar.rightMargin = mainApp.w(50);
		progressBar.setLayoutParams(lp_progressBar);
		progressBar.setIndeterminate(false);
		progressBar.setVisibility(View.VISIBLE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			progressBar.setProgressDrawable(CTX.getResources().getDrawable(R.drawable.custom_progress, CTX.getTheme()));
		   } else {
			progressBar.setProgressDrawable(CTX.getResources().getDrawable(R.drawable.custom_progress));
		}
		//progressBar.setBackgroundDrawable(CTX.getResources().getDrawable(R.drawable.custom_progress));; //setBackgroundDrawable(android.R.drawable.progress_horizontal);
		progressBar.setMax(100);
		//progressBar.getProgressDrawable().setColorFilter(0xFFFFFF00,android.graphics.PorterDuff.Mode.MULTIPLY);
		//progressBar.setPadding(mainApp.w(50), mainApp.h(40), mainApp.w(50), mainApp.h(50));
		progLayout.addView(progressBar);
		mainLayout.addView(progLayout);
		
//		LinearLayout imageLayout = new LinearLayout(CTX);
//		LayoutParams lp_imageLayout = new LayoutParams(LayoutParams.MATCH_PARENT, mainApp.h(100));
//		imageLayout.setLayoutParams(lp_imageLayout);
//		imageLayout.setGravity(Gravity.CENTER);
//		
//		ImageView divLine = new ImageView(CTX);
//		LinearLayout.LayoutParams lp_divLine = new LinearLayout.LayoutParams(mainApp.w(640), LayoutParams.MATCH_PARENT);
//		lp_divLine.topMargin = mainApp.h(30);
//		lp_divLine.bottomMargin = mainApp.h(50);
//		divLine.setLayoutParams(lp_divLine);
//		divLine.setImageResource(R.drawable.progress_dot);
//		divLine.setScaleType(ScaleType.FIT_CENTER);
//		imageLayout.addView(divLine);
//		mainLayout.addView(imageLayout);
		
		setContentView(mainLayout);
	}

	public progress_Popup_Dialog(Context context, String title, String message1, String message2) {
		super(context);
		CTX = context;
		mainApp = (MainApplication) context.getApplicationContext();
		this.popupTitle = title;
		this.messageText1 = message1;
		this.messageText2 = message2;
	}

	public void setProgress(int prog){
		progressBar.setProgress(prog);
		progressBar.invalidate();
	}
}
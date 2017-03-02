package com.cocolap.talkbank;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


public class ActivityStart extends Activity {
	
	private ImageView selfImage;
	private MainApplication mainApp;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_start);
		mainApp = (MainApplication) getApplication();
		//mainApp.setLocale("us");
		
		selfImage = (ImageView) findViewById(R.id.show_image);
		selfImage.setBackgroundResource(R.drawable.talkbank_login_bg);
		
        LinearLayout buttonsLayout = (LinearLayout) findViewById(R.id.buttons_line);
        RelativeLayout.LayoutParams buttonsPL = (RelativeLayout.LayoutParams) buttonsLayout.getLayoutParams();
		buttonsPL.bottomMargin = mainApp.h(65);
		buttonsPL.height = mainApp.h(110);
		buttonsLayout.setLayoutParams(buttonsPL);
		
		Button userRegiBtn = (Button) findViewById(R.id.user_regi_btn);
		LinearLayout.LayoutParams userRegiBtnPL = (LinearLayout.LayoutParams) userRegiBtn.getLayoutParams();
		userRegiBtnPL.leftMargin = mainApp.w(20);
		userRegiBtnPL.rightMargin = mainApp.w(10);
		userRegiBtn.setLayoutParams(userRegiBtnPL);
		userRegiBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
		
		userRegiBtn.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(ActivityStart.this, tbUserRegiActivity.class);
				startActivity(intent);				
			}
		});
		
		Button loginBtn = (Button) findViewById(R.id.login_btn);
		LinearLayout.LayoutParams loginBtnPL = (LinearLayout.LayoutParams) loginBtn.getLayoutParams();
		loginBtnPL.leftMargin = mainApp.w(10);
		loginBtnPL.rightMargin = mainApp.w(20);
		loginBtn.setLayoutParams(loginBtnPL);
		loginBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);

		loginBtn.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(ActivityStart.this, tbSignInputActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
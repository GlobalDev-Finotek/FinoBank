package com.cocolap.talkbank;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;


public class UserRegiActivity extends AppCompatActivity {
	
	private MainApplication mainApp;
	private final int editMargin = 50;
	private final int SIGN_RESULT = 701;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int displayWidth = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? Math.max(metrics.widthPixels, metrics.heightPixels) : Math.min(metrics.widthPixels, metrics.heightPixels);
		int displayHeight = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? Math.min(metrics.widthPixels, metrics.heightPixels) : Math.max(metrics.widthPixels, metrics.heightPixels);

		setContentView(R.layout.tb_user_regi_layout);
		mainApp = (MainApplication) getApplication();

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
//		actionBar.setTitle(getString(R.string.string_user_regi));
		actionBar.setTitle(Html.fromHtml("<big>" + getString(R.string.string_user_regi) + "</big>"));
		//actionBar.setBackgroundDrawable(new ColorDrawable(0xFFFFFFFF));
		//mainApp.setLocale("us");

		LinearLayout buttonsLayout = (LinearLayout) findViewById(R.id.buttons_line);
		RelativeLayout.LayoutParams buttonsPL = (RelativeLayout.LayoutParams) buttonsLayout.getLayoutParams();
		buttonsPL.height = mainApp.h(110);
		buttonsLayout.setLayoutParams(buttonsPL);

		Button cancelBtn = (Button) findViewById(R.id.cancel_btn);
		cancelBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
		cancelBtn.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		Button signBtn = (Button) findViewById(R.id.sign_btn);
		signBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
		signBtn.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(UserRegiActivity.this, SignRegiActivity.class);
				startActivityForResult(intent, SIGN_RESULT);
			}
		});

		EditText inputPhone = (EditText) findViewById(R.id.input_phone);
		EditText inputName = (EditText) findViewById(R.id.input_name);

		TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String pnum = mainApp.get_phoneNum(mTelephonyMgr.getLine1Number());
		inputPhone.setText(PhoneNumberUtils.formatNumber(pnum));

		LinearLayout.LayoutParams inputPhoneLP = (LinearLayout.LayoutParams) inputPhone.getLayoutParams();
		inputPhoneLP.width = mainApp.w(720);
		inputPhoneLP.height = mainApp.h(100);
		inputPhoneLP.leftMargin = mainApp.w(editMargin);
		inputPhoneLP.rightMargin = mainApp.w(editMargin);
		inputPhoneLP.topMargin = mainApp.h(230);
		inputPhone.setLayoutParams(inputPhoneLP);
		inputPhone.setTextSize(TypedValue.COMPLEX_UNIT_PX, mainApp.w(42));

		LinearLayout.LayoutParams inputNameLP = (LinearLayout.LayoutParams) inputName.getLayoutParams();
		inputNameLP.width = mainApp.w(720);
		inputNameLP.height = mainApp.h(100);
		inputNameLP.leftMargin = mainApp.w(editMargin);
		inputNameLP.rightMargin = mainApp.w(editMargin);
		inputNameLP.topMargin = mainApp.h(120);
		inputName.setLayoutParams(inputNameLP);
		inputName.setTextSize(TypedValue.COMPLEX_UNIT_PX, mainApp.w(42));
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_regi_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
			Toast.makeText(getApplicationContext(), "Settings", Toast.LENGTH_SHORT).show();
			return true;
        } else if (id == R.id.action_search) {
			Toast.makeText(getApplicationContext(), "Search", Toast.LENGTH_SHORT).show();
			return true;
		} else if (id == R.id.action_edit) {
			Toast.makeText(getApplicationContext(), "Edit", Toast.LENGTH_SHORT).show();
			return true;
		}
        return super.onOptionsItemSelected(item);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case SIGN_RESULT:
				if (resultCode == Activity.RESULT_OK) {
					finish();
				}
				break;
		}
	}
}
package com.cocolap.talkbank;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cocolab.client.message.prefValue;
import com.cocolab.drawing.ConCanvas;
import com.cocolab.subdlg.DiagramType;

import java.util.ArrayList;


public class tbSignInputActivity extends AppCompatActivity  implements ConCanvas.OnCanvasListener {
	
	private MainApplication mainApp;
	private ImageView signPanel, panelBack;
	private ConCanvas ccCanvas;
	private int signColor = Color.rgb(51, 51, 51);
	private int penWidth = 5, actionHeight = 0;
	private ActionBar actionBar;
	private boolean simpleSign = false;
	ArrayList<xyClass> xyArray = new ArrayList<xyClass>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int displayWidth = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? Math.max(metrics.widthPixels, metrics.heightPixels) : Math.min(metrics.widthPixels, metrics.heightPixels);
		int displayHeight = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? Math.min(metrics.widthPixels, metrics.heightPixels) : Math.max(metrics.widthPixels, metrics.heightPixels);

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			simpleSign = bundle.getBoolean(prefValue.SimpleSign, false);
		}

		setContentView(R.layout.tb_sign_input_layout);
		signPanel = (ImageView) findViewById(R.id.signPanel);
		panelBack = (ImageView) findViewById(R.id.panelBack);

		mainApp = (MainApplication) getApplication();

		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		TypedValue tv = new TypedValue();
		if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)){
			actionHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
		}

		//actionBar.setTitle(getString(R.string.string_comm_sign_input));
		actionBar.setTitle(Html.fromHtml("<big>" + getString(R.string.string_comm_sign_input) + "</big>"));
		actionBar.setBackgroundDrawable(new ColorDrawable(0xFFFFFFFF));
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

		Button nextBtn = (Button) findViewById(R.id.login_btn);
		nextBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
		nextBtn.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if (simpleSign){
					setResult(RESULT_OK);
					finish();
				} else {
					Intent intent = new Intent(tbSignInputActivity.this, tbChatActivity.class);
					startActivity(intent);
				}
			}
		});

		ccCanvas = new ConCanvas(this, this, displayWidth, displayHeight, 1);
		signPanel.setImageBitmap(ccCanvas.foreBitmap);
		signPanel.setBackgroundColor(Color.TRANSPARENT);
		signPanel.setScaleType(ImageView.ScaleType.MATRIX);

		penWidth = mainApp.w(16);
		ccCanvas.setTextMode(false);
		ccCanvas.setColor(signColor);
		ccCanvas.setDocHeight(displayHeight);
		ccCanvas.setDocWidth(displayWidth);
		ccCanvas.setDiagram(DiagramType.SCRIBBLE);

		ccCanvas.setPage(0);
		ccCanvas.setForeZoom(1.f);
		ccCanvas.setPenWidth(penWidth);
		ccCanvas.setPageWidth(displayWidth);
		ccCanvas.setPageHeight(displayHeight);
		ccCanvas.setForeStartX(0);
		ccCanvas.setForeStartY(0);
		signPanel.setVisibility(View.VISIBLE);
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
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY() - actionHeight;
		xyClass bmClass = new xyClass((int) x, (int) y, event.getAction());
		xyArray.add(bmClass);
		ccCanvas.DrawEvent(x, y, event.getAction());
		// backDrawing.setVisibility(View.INVISIBLE);
		signPanel.setVisibility(View.VISIBLE);
		signPanel.invalidate();

		return true;
	}

	@Override
	public void updateCanvas() {
		signPanel.invalidate();
		panelBack.invalidate();
	}

	@Override
	public void sendLine(int type, float x, float y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendShpae(int type, float x1, float y2, float x2, float y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendText(String text, float x, float y) {
		// TODO Auto-generated method stub

	}

	class xyClass {
		public long time;
		public Integer x;
		public Integer y;
		public Integer tAct;

		public xyClass(Integer _x, Integer _y, Integer _tAct) {
			this.time = System.currentTimeMillis();
			this.x = _x;
			this.y = _y;
			this.tAct = _tAct;
		}

		public String getX() {
			return x.toString();
		}

		public String getY() {
			return y.toString();
		}

		public int getAct() {
			return tAct;
		}

		public long getTime() {
			return time;
		}
	}
}
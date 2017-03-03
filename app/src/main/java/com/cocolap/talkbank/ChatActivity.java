package com.cocolap.talkbank;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cocolab.client.message.PrefValue;
import com.cocolap.talkbank.EmoticonsGridAdapter.KeyClickListener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.TimerTask;

public class ChatActivity extends AppCompatActivity implements KeyClickListener {
	
	private MainApplication mainApp;
	private final int editMargin = 50;
	private final int SIGN_RESULT = 701;
	ListView chatListView;
	ChatListAdapter chatListAdapter;
	ArrayList<MessageArray> chatArrayList = new ArrayList<MessageArray>();
	private LinearLayout emoticonsCover;
	private View popUpView;
	private PopupWindow popupWindow;
	private int keyboardHeight;
	private LinearLayout parentLayout;
	private boolean isKeyBoardVisible;
	private Bitmap[] emoticons;
	private static final int NO_OF_EMOTICONS = 6;
	private static final int REQ_SIGN_INPUT = 601;
	private EditText inputMessage;
	private int delayTime = 3000;
	private int displayWidth, displayHeight;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		DisplayMetrics metrics = getResources().getDisplayMetrics();
		displayWidth = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? Math.max(metrics.widthPixels, metrics.heightPixels) : Math.min(metrics.widthPixels, metrics.heightPixels);
		displayHeight = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? Math.min(metrics.widthPixels, metrics.heightPixels) : Math.max(metrics.widthPixels, metrics.heightPixels);

		setContentView(R.layout.tb_chat_activity);
		mainApp = (MainApplication) getApplication();

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(getString(R.string.string_talk_bank));
		//actionBar.setBackgroundDrawable(new ColorDrawable(0xFFFFFFFF));
		//mainApp.setLocale("us");

		LinearLayout inputLayout = (LinearLayout) findViewById(R.id.message_input);
		LinearLayout.LayoutParams inputLP = (LinearLayout.LayoutParams) inputLayout.getLayoutParams();
		inputLP.height = mainApp.h(100);
		inputLayout.setLayoutParams(inputLP);

		chatListView = (ListView)findViewById(R.id.msg_list);
		chatListView.setDivider(null);
		chatListView.setDividerHeight(20);
		chatListAdapter = new ChatListAdapter(this, R.layout.tb_chatdata_msgrecv, chatArrayList);
		chatListView.setAdapter(chatListAdapter);

		parentLayout = (LinearLayout) findViewById(R.id.list_parent);
		emoticonsCover = (LinearLayout) findViewById(R.id.footer_for_emoticons);
		popUpView = getLayoutInflater().inflate(R.layout.tb_emoticons_popup, null);

		final float popUpheight = getResources().getDimension(R.dimen.keyboard_height);
		changeKeyboardHeight((int) popUpheight);

		ImageView emoticonsButton = (ImageView) findViewById(R.id.emoticons_button);
		emoticonsButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!popupWindow.isShowing()) {
					popupWindow.setHeight((int) (keyboardHeight));
					if (isKeyBoardVisible) {
						emoticonsCover.setVisibility(LinearLayout.GONE);
					} else {
						emoticonsCover.setVisibility(LinearLayout.VISIBLE);
					}
					popupWindow.showAtLocation(parentLayout, Gravity.BOTTOM, 0, 0);
				} else {
					popupWindow.dismiss();
				}
			}
		});

		readEmoticons();
		enablePopUpView();
		checkKeyboardHeight(parentLayout);
		enableFooterView();

		Handler dateHandler = new Handler();
		dateHandler.postDelayed(dateTask, 1000);
	}

	private void readEmoticons () {

		emoticons = new Bitmap[NO_OF_EMOTICONS];
		for (short i = 0; i < NO_OF_EMOTICONS; i++) {
			emoticons[i] = getImage((i+1) + ".png", 2.2f*util.fw(1, displayWidth));
		}
	}

	private void enableFooterView() {

		inputMessage = (EditText) findViewById(R.id.input_message);
		inputMessage.setTextSize(TypedValue.COMPLEX_UNIT_PX, mainApp.w(42));
		inputMessage.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (popupWindow.isShowing()) {
					popupWindow.dismiss();
				}
			}
		});

		inputMessage.requestFocus();
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

		final Button postButton = (Button) findViewById(R.id.message_send);
		postButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (inputMessage.getText().toString().length() > 0) {
					int msgType = 2;
					if (inputMessage.getId() == -1) msgType = 3;
					MessageArray aMessage = new MessageArray(msgType, inputMessage.getText(), util.getTime(), -1);
					chatArrayList.add(aMessage);
					((BaseAdapter) chatListView.getAdapter()).notifyDataSetChanged();
					inputMessage.setText("");
					inputMessage.setId(0);
				}
			}
		});
	}

	/**
	 * Overriding onKeyDown for dismissing keyboard on key down
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (popupWindow.isShowing()) {
			popupWindow.dismiss();
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	/**
	 * Checking keyboard height and keyboard visibility
	 */
	int previousHeightDiffrence = 0;
	private void checkKeyboardHeight(final View parentLayout) {

		parentLayout.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						Rect r = new Rect();
						parentLayout.getWindowVisibleDisplayFrame(r);

						int screenHeight = parentLayout.getRootView().getHeight();
						int heightDifference = screenHeight - (r.bottom);

						if (previousHeightDiffrence - heightDifference > 50) {
							popupWindow.dismiss();
						}

						previousHeightDiffrence = heightDifference;
						if (heightDifference > 100) {
							isKeyBoardVisible = true;
							changeKeyboardHeight(heightDifference);
						} else {
							isKeyBoardVisible = false;
						}
					}
				});

	}

	/**
	 * change height of emoticons keyboard according to height of actual
	 * keyboard
	 *
	 * @param height
	 *            minimum height by which we can make sure actual keyboard is
	 *            open or not
	 */
	private void changeKeyboardHeight(int height) {

		if (height > 100) {
			keyboardHeight = height;
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, keyboardHeight);
			emoticonsCover.setLayoutParams(params);
		}

	}

	/**
	 * Defining all components of emoticons keyboard
	 */
	private void enablePopUpView() {

		ViewPager pager = (ViewPager) popUpView.findViewById(R.id.emoticons_pager);
		pager.setOffscreenPageLimit(3);

		ArrayList<String> paths = new ArrayList<String>();

		for (short i = 1; i <= NO_OF_EMOTICONS; i++) {
			paths.add(i + ".png");
		}

		EmoticonsPagerAdapter adapter = new EmoticonsPagerAdapter(ChatActivity.this, paths, this);
		pager.setAdapter(adapter);

		// Creating a pop window for emoticons keyboard
		popupWindow = new PopupWindow(popUpView, LinearLayout.LayoutParams.MATCH_PARENT,
				(int) keyboardHeight, false);

		TextView backSpace = (TextView) popUpView.findViewById(R.id.back);
		backSpace.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
				inputMessage.dispatchKeyEvent(event);
			}
		});

		popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				emoticonsCover.setVisibility(LinearLayout.GONE);
			}
		});
	}

	/**
	 * For loading smileys from assets
	 */
	private Bitmap getImage(String path, float times) {
		AssetManager mngr = getAssets();
		InputStream in = null;
		try {
			in = mngr.open("emoticons/" + path);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Bitmap temp = BitmapFactory.decodeStream(in, null, null);
		float h = temp.getHeight();
		float w= temp.getWidth();
		temp = Bitmap.createScaledBitmap(temp, (int) (w * times), (int) (h * times), true);
		return temp;
	}

	@Override
	public void keyClickedIndex(final String index) {

		ImageGetter imageGetter = new ImageGetter() {
			public Drawable getDrawable(String source) {
				StringTokenizer st = new StringTokenizer(index, ".");
				Drawable d = new BitmapDrawable(getResources(),emoticons[Integer.parseInt(st.nextToken()) - 1]);
				d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
				return d;
			}
		};

		Spanned cs = Html.fromHtml("<img src ='" + index + "'/>", imageGetter, null);

		int cursorPosition = inputMessage.getSelectionStart();
		inputMessage.getText().insert(cursorPosition, cs);
		inputMessage.setId(-1);

		MessageArray aMessage = new MessageArray(3, inputMessage.getText(), util.getTime(), -1);
		chatArrayList.add(aMessage);
		((BaseAdapter) chatListView.getAdapter()).notifyDataSetChanged();
		inputMessage.setText("");
		inputMessage.setId(0);

		if (index.contains("1.png")) {
			Handler remmitanceHandler = new Handler();
			remmitanceHandler.postDelayed(remmitanceTask, delayTime);
		}
	}

	private class ChatListAdapter extends ArrayAdapter<MessageArray> {
		private ArrayList<MessageArray> items;
		public ChatListAdapter(Context context, int textViewResourceId, ArrayList<MessageArray> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View conv = convertView;

			LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			MessageArray p = items.get(position);
			switch (p.getType()){
				case 0:
					conv = vi.inflate(R.layout.tb_chatdata_date, null);
					TextView dateText = (TextView) conv.findViewById(R.id.message);
					dateText.setText(p.getMessage().toString());
					break;
				case 1:
					conv = vi.inflate(R.layout.tb_chatdata_msgrecv, null);
					ImageView iconImage = (ImageView) conv.findViewById(R.id.show_icon);
					LinearLayout.LayoutParams imgParams = (LinearLayout.LayoutParams)iconImage.getLayoutParams();
					imgParams.width = mainApp.DPtoPx(60);
					imgParams.height = mainApp.DPtoPx(60);
					iconImage.setLayoutParams(imgParams);

					TextView msgText = (TextView) conv.findViewById(R.id.message);
					msgText.setText(p.getMessage());
					msgText.setTag(p.getIndex());
					msgText.setOnClickListener(new Button.OnClickListener() {
						public void onClick(View v) {
							int ttt = Integer.parseInt(v.getTag().toString());
							switch (ttt) {
								case 2:
									Intent intent1 = new Intent(ChatActivity.this, SignInputActivity.class);
									intent1.putExtra(PrefValue.SimpleSign, true);
									startActivityForResult(intent1, REQ_SIGN_INPUT);
									break;
								case 3:
									Intent intent2 = getApplicationContext().getPackageManager().getLaunchIntentForPackage("fi.harism.curl");
									startActivity(intent2);
									break;
							}
						}
					});


					TextView msgTime = (TextView) conv.findViewById(R.id.time);
					msgTime.setText(p.getTime());
					break;
				case 2:
					conv = vi.inflate(R.layout.tb_chatdata_msgsend, null);
					TextView sendMsgText = (TextView) conv.findViewById(R.id.message);
					sendMsgText.setText(p.getMessage().toString());

					TextView sendMsgTime = (TextView) conv.findViewById(R.id.time);
					sendMsgTime.setText(p.getTime());
					break;
				case 3:
					conv = vi.inflate(R.layout.tb_chatdata_imtsend, null);
					TextView sendImtText = (TextView) conv.findViewById(R.id.message);
					sendImtText.setText(p.getMessage());

					TextView sendImtTime = (TextView) conv.findViewById(R.id.time);
					sendImtTime.setText(p.getTime());
					break;
			}
			return conv;
		}
	}

	final int SIX = 5;
	final int SEVEN = 6;
	final int EIGHT = 7;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tb_chat_menu, menu);

		SubMenu subMenu = menu.addSubMenu("글씨체 설정");

		subMenu.add(1, SIX, Menu.NONE, "굴림체");
		subMenu.add(1, SEVEN, Menu.NONE, "이탤릭체");
		subMenu.add(1, EIGHT, Menu.NONE, "맑은고딕");
//
//		SearchView searchView = new SearchView(getSupportActionBar().getThemedContext());
//		SubMenu subMenu = menu.addSubMenu(0, 0, 2, null);
//
//		subMenu.add(0, 4, 4, "Search Message")
//				.setIcon(R.drawable.app_icon)
//				.setActionView(searchView)
//				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
//
//
//		subMenu.add(0, 5, 5, "Settings")
//				.setIcon(R.drawable.ic_settings);
//
//		subMenu.add(0, 6, 6, "About")
//				.setIcon(R.drawable.ic_about);
//
//		MenuItem subMenuItem = subMenu.getItem();
//		subMenuItem.setIcon(R.drawable.ic_action_overflow);
//		subMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
//
		return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

		inputMessage.requestFocus();
		InputMethodManager immhide = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		immhide.hideSoftInputFromWindow(inputMessage.getWindowToken(), 0);

		if (id == R.id.action_search) {
			MessageArray aMessage = new MessageArray(1, Html.fromHtml("안녕하세요, 톡뱅크입니다.<br>홍길동 고객님!<br>무엇을 도와드릴까요?"), util.getTime(), 1);
			chatArrayList.add(aMessage);
			((BaseAdapter) chatListView.getAdapter()).notifyDataSetChanged();
			chatListView.setSelection(chatArrayList.size());

			//Toast.makeText(getApplicationContext(), utilString.pad("123","0",7,-1) ,Toast.LENGTH_SHORT).show();
			return true;
        }
//		if (id == R.id.action_hi) {
//			MessageArray aMessage = new MessageArray(1, Html.fromHtml("안녕하세요, 톡뱅크입니다.<br>홍길동 고객님!<br>무엇을 도와드릴까요?"), util.getTime(), 1);
//			chatArrayList.add(aMessage);
//			((BaseAdapter) chatListView.getAdapter()).notifyDataSetChanged();
//			chatListView.setSelection(chatArrayList.size());
//			return true;
//        } else if (id == R.id.action_remittanc) {
//			MessageArray aMessage = new MessageArray(1, Html.fromHtml("△△△에게<br>100,000원을 우리은행,<br>1001-101-100101로<br>송금하시겠습니까?<br><u>전자서명하러 가기 ></u>"), util.getTime(), 2);
//			chatArrayList.add(aMessage);
//			((BaseAdapter) chatListView.getAdapter()).notifyDataSetChanged();
//			chatListView.setSelection(chatArrayList.size());
//			return true;
//		} else if (id == R.id.action_verify) {
//			MessageArray aMessage = new MessageArray(1, Html.fromHtml("△△△에게 100,000원을<br>송금하였습니다.<br>최종잔액은 51,345원입니다.<br><u>내통장에서 확인하기 ></u>"), util.getTime(), 3);
//			chatArrayList.add(aMessage);
//			((BaseAdapter) chatListView.getAdapter()).notifyDataSetChanged();
//			chatListView.setSelection(chatArrayList.size());
//			return true;
//		}
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

	class MessageArray {
		private int Type;
		private Spanned Message;
		private String Time;
		private int Index;
		public MessageArray(int _type, Spanned _message, String _time, int _index){
			this.Type = _type;
			this.Message = _message;
			this.Time = _time;
			this.Index = _index;
		}
		public int getType() {
			return Type;
		}
		public Spanned getMessage() {
			return Message;
		}
		public String getTime() {
			return Time;
		}
		public int getIndex() {
			return Index;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case REQ_SIGN_INPUT:
				if (resultCode  == RESULT_OK) {
					Handler verifyHandler = new Handler();
					verifyHandler.postDelayed(verifyTask, delayTime);
				}
				break;
		}
	}

	TimerTask dateTask = new TimerTask() {
		public void run() {
			inputMessage.requestFocus();
			InputMethodManager immhide = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
			immhide.hideSoftInputFromWindow(inputMessage.getWindowToken(), 0);

			MessageArray aMessage = new MessageArray(0, Html.fromHtml(util.getYMDWeek()), util.getTime(), -1);
			chatArrayList.add(aMessage);
			((BaseAdapter) chatListView.getAdapter()).notifyDataSetChanged();
			chatListView.setSelection(chatArrayList.size());

			Handler greetHandler = new Handler();
			greetHandler.postDelayed(greetTask, delayTime);
		}
	};

	TimerTask greetTask = new TimerTask() {
		public void run() {
			MessageArray aMessage = new MessageArray(1, Html.fromHtml("안녕하세요, 톡뱅크입니다.<br>홍길동 고객님!<br>무엇을 도와드릴까요?"), util.getTime(), 1);
			chatArrayList.add(aMessage);
			((BaseAdapter) chatListView.getAdapter()).notifyDataSetChanged();
			chatListView.setSelection(chatArrayList.size());
		}
	};

	TimerTask remmitanceTask = new TimerTask() {
		public void run() {
			MessageArray aMessage2 = new MessageArray(1, Html.fromHtml("△△△에게<br>100,000원을 우리은행,<br>1001-101-100101로<br>송금하시겠습니까?<br><br><u>전자서명하러 가기 ></u>"), util.getTime(), 2);
			chatArrayList.add(aMessage2);
			((BaseAdapter) chatListView.getAdapter()).notifyDataSetChanged();
			chatListView.setSelection(chatArrayList.size());
		}
	};

	TimerTask verifyTask = new TimerTask() {
		public void run() {
			MessageArray aMessage = new MessageArray(1, Html.fromHtml("△△△에게 100,000원을<br>송금하였습니다.<br>최종잔액은 51,345원입니다.<br><br><u>내통장에서 확인하기 ></u>"), util.getTime(), 3);
			chatArrayList.add(aMessage);
			((BaseAdapter) chatListView.getAdapter()).notifyDataSetChanged();
			chatListView.setSelection(chatArrayList.size());
		}
	};
}
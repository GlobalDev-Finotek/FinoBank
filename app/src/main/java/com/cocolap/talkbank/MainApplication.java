package com.cocolap.talkbank;

import java.util.Locale;

import android.app.Application;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;


public class MainApplication extends Application {
	public int displayWidth, displayHeight, screenType = 0, baseWidth = 800, baseHeight = 1280;
	public final float baseInches = 5.287476f; // Note 1 : 1280 X 800
	private double myInches;

	@Override
	public void onCreate() {
		super.onCreate();

		DisplayMetrics metrics = getResources().getDisplayMetrics();
		displayWidth = Math.min(metrics.widthPixels, metrics.heightPixels);
		displayHeight = Math.max(metrics.widthPixels, metrics.heightPixels);
		double x = Math.pow(metrics.widthPixels / metrics.xdpi, 2);
		double y = Math.pow(metrics.heightPixels / metrics.ydpi, 2);
		myInches = Math.sqrt(x + y);
		
		String text_color1 = util.intTohexaDecimal(47, 46, 125);  // 2f2e7d
		String text_color2 = util.intTohexaDecimal(40, 62, 85);  // 283e55
		Log.d("D2RCSD","color");
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	public int w(int size) {
		return (int) ((float) (size * (float) displayWidth) / (float) baseWidth);
	}

	public int h(int size) {
		float ttt = (int) ((float) (size * (float) displayHeight) / (float) baseHeight);
		return (int) ((float) (size * (float) displayHeight) / (float) baseHeight);
	}

	public int DPtoPx(int _Dp) {
		float density = getResources().getDisplayMetrics().density;
		return (int) (_Dp * density + 0.5f);
	}

	public int scrSizeTune(int size) {
		return (int) (size * Math.sqrt((float) (myInches / baseInches)));
	}

	Handler asyncHandler;
	public void AsyncProc(Handler handler) {
		asyncHandler = handler;
		class asyncExe extends AsyncTask<String, String, String> {
			@Override
			protected String doInBackground(String... params) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return "";
			}

			protected void onPostExecute(String result) {
				Message msg = new Message();
				msg.obj = result;
				asyncHandler.sendMessage(msg);
			}
		}
		new asyncExe().execute();
	}

	public void progAsyncProc(Handler handler) {
		asyncHandler = handler;
		class asyncExe extends AsyncTask<String, String, String> {
			@Override
			protected String doInBackground(String... params) {
				for (int i=0; i<99; i++){
					try {
						Thread.sleep(10);
						Message msg = new Message();
						msg.arg1 = i+1;
						asyncHandler.sendMessage(msg);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				return "";
			}

			protected void onPostExecute(String result) {
				Message msg = new Message();
				msg.obj = result;
				msg.arg1 = 100;
				asyncHandler.sendMessage(msg);
			}
		}
		new asyncExe().execute();
	}

	public String get_phoneNum(String _pnum) {
		if (_pnum == null || _pnum.equals("")) return "";
		final String First_num = _pnum.substring(0, 3);
		final int len = _pnum.length();
		if (First_num.indexOf("82") >= 0) {
			return "0" + _pnum.substring(len - 10, len);
		} else
			return _pnum;
	}

	public void setLocale(String charicter) {
    	Locale locale = new Locale(charicter); 
    	Locale.setDefault(locale);
    	Configuration config = new Configuration();
    	config.locale = locale;
    	getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

	public String getLocale() {
		Locale systemLocale = getResources().getConfiguration().locale;
		String strLanguage = systemLocale.getLanguage();
		return strLanguage;
    }
}

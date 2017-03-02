package com.cocolap.talkbank;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

public class util {
	public static String readFile(String path) {
		FileReader reader = null;
		BufferedReader bufferReader = null;
		String data = "";
		String read_data = "";
		String[] split_data;

		if (new File(path).exists()) {
			try {
				reader = new FileReader(path);
				bufferReader = new BufferedReader(reader);
				while ((data = bufferReader.readLine()) != null) {
					read_data += data;
				}
			} catch (Exception e) {
				;
			}
		}
		return read_data;
	}

	public static float fw(int size, int displayWidth){
		return ((float) (size * (float) displayWidth) / (float) 800);

	}
	public static int w(int size, int displayWidth){
		return (int) ((float) (size * (float) displayWidth) / (float) 800);

	}
	public static int h(int size, int displayHeight){
		return (int) ((float) (size * (float) displayHeight) / (float) 1280);

	}
	public static void catchTopActivity(Context context) {
		try {
			ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningTaskInfo> taskInfo = am.getRunningTasks(1);
			ComponentName topActivity = taskInfo.get(0).topActivity;

			String str = topActivity.getPackageName();
			Log.d("CallPrompt", "catchtop:" + str);
		} catch (Exception e) {
			;
		}
	}

	public static boolean checkRunning(Context context, String PackageClassName) {
		ActivityManager activityManager = (ActivityManager) context.getApplicationContext().getSystemService(context.getApplicationContext().ACTIVITY_SERVICE);
		List<RunningTaskInfo> info;
		info = activityManager.getRunningTasks(7);
		for (Iterator iterator = info.iterator(); iterator.hasNext();) {
			RunningTaskInfo runningTaskInfo = (RunningTaskInfo) iterator.next();
			if (runningTaskInfo.topActivity.getClassName().equals(PackageClassName)) {
				Log.d("CallPrompt", PackageClassName + " is running");
				return true;
			}
		}
		Log.d("D2RCSD", PackageClassName + " is not running");
		return false;
	}

	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}

	public static void deleteFiles(String path) {

		File file = new File(path);

		if (file.exists()) {
			String deleteCmd = "rm -r " + path;
			Runtime runtime = Runtime.getRuntime();
			try {
				runtime.exec(deleteCmd);
			} catch (IOException e) {
			}
		}
	}

	public static String makeStringComma(String str) {
		if (str.length() == 0)
			return "";
		long value = Long.parseLong(str);
		DecimalFormat format = new DecimalFormat("###,###");
		return format.format(value);
	}

	public static String replaceComma(String data) {
		int convert = Integer.parseInt(data);
		DecimalFormat df = new DecimalFormat("#,###");

		String formatNum = (String) df.format(convert);
		return formatNum;
	}

	public static String colorToHexColor(int color) {
		return String.format("#%06X", (0xFFFFFF & color));
	}

	public static String decodeBase64(String content) {
		byte[] dec = Base64.decode(content, Base64.NO_WRAP);
		return new String(dec, 0, dec.length);
	}

	public static void urlImageGet(Handler handler, String Url) {
		// 이유없이 통신이 안되면 퍼미션 체크하라
		final Handler httpHandler = handler;
		final String url = Url;
		class httpConnect extends AsyncTask<String, String, Bitmap> {
			@Override
			protected Bitmap doInBackground(String... params) {
				String respnoseEntity = null;
				HttpURLConnection connection = null;
				String postURL = params[0];
				Bitmap bm = null;
				try {
					URL url = new URL(postURL);
					connection = (HttpURLConnection) url.openConnection();
					URLConnection conn = url.openConnection();
					BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
					bm = BitmapFactory.decodeStream(bis);
					bis.close();
				} catch (Exception e) {
					respnoseEntity = "ERROR";
				} finally {
				}
				return bm;
			}

			protected void onProgressUpdate(Integer... progress) {
			}

			protected void onPostExecute(Bitmap bm) {
				Message msg = new Message();
				msg.obj = bm;
				httpHandler.sendMessage(msg);
			}
		}
		new httpConnect().execute(Url);
	}

	public static double angle(Point Cen, Point First, Point Second) {
		// CClientDC dc(this);
		double lega1, lega2, legb1, legb2;
		double norm, norm1, norm2, angle, prod, curl;
		int x1, y1, x2, y2, x3, y3;

		x2 = Cen.x;
		y2 = Cen.y;
		x1 = First.x;
		y1 = First.y;
		x3 = Second.x;
		y3 = Second.y;

		lega1 = x1 - x2;
		legb1 = y1 - y2;
		lega2 = x3 - x2;
		legb2 = y3 - y2;

		norm1 = Math.sqrt(lega1 * lega1 + legb1 * legb1);// 두 벡터의 크기
		norm2 = Math.sqrt(lega2 * lega2 + legb2 * legb2);// 두 벡터의 크기
		norm = norm1 * norm2;
		prod = (lega1 * lega2) + (legb1 * legb2);// 두 벡터의 내적
		angle = Math.acos(prod / norm);

		curl = (lega1 * legb2) - (legb1 * lega2);// 두 벡터의 외적

		if (curl <= 0)
			return angle / 3.141592654 * 180;

		else
			return (360 - angle / 3.141592654 * 180);
	}

	public static double distance(Point p1, Point p2) {
		double dist;
		dist = Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
		return dist;
	}

	public static String getRealPathFromURI(Context context, Uri contentUri){
		Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
	    cursor.moveToFirst();
	         
	    String path = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
	         
	    return path;
	}

	public static String getFileName(String fullPath) {
		if (fullPath == null)
			return "";
		final String[] file_name = fullPath.split("\\/");
		return file_name[file_name.length - 1];
	}
	
	public static String intTohexaDecimal(int i1, int i2, int i3){
		return Integer.toHexString(i1) + Integer.toHexString(i2) + Integer.toHexString(i3);

	}

	public static String getYMDWeek(){
		Calendar cal= Calendar.getInstance ();
//		cal.set(Calendar.YEAR, 2015);
//		cal.set(Calendar.MONTH, Calendar.DECEMBER);
//		cal.set(Calendar.DATE, 24);
		String return_String = cal.get(Calendar.YEAR)+"년 "+cal.get(Calendar.MONTH)+"월"+cal.get(Calendar.DATE)+"일 ";
		switch (cal.get(Calendar.DAY_OF_WEEK)){
			case 1:
				return_String += "일요일";
				break;
			case 2:
				return_String += "월요일";
				break;
			case 3:
				return_String += "화요일";
				break;
			case 4:
				return_String += "수요일";
				break;
			case 5:
				return_String += "목요일";
				break;
			case 6:
				return_String += "금요일";
				break;
			case 7:
				return_String += "토요일";
				break;
		}
		return return_String;
	}

	public static String getTime(){
		Calendar now = Calendar.getInstance();
		int isAMorPM = now.get(Calendar.AM_PM);
		switch (isAMorPM) {
			case Calendar.AM:
				return "오전 "+now.get(Calendar.HOUR)+":"+String.format("%02d", now.get(Calendar.MINUTE));
			case Calendar.PM:
				return "오후 "+now.get(Calendar.HOUR)+":"+String.format("%02d", now.get(Calendar.MINUTE));
		}
		return "";
	}
}

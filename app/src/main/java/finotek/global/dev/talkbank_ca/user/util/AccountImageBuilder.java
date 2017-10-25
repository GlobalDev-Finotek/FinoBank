package finotek.global.dev.talkbank_ca.user.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

import finotek.global.dev.talkbank_ca.R;

// 계좌 이미지를 가상으로 생성함
public class AccountImageBuilder {
	private static final String accountFile = "my_account.png";

	public static void saveAccount(Context context, String name, String date, String lang) {
		float scale = context.getResources().getDisplayMetrics().density;
		Bitmap account;
		if (lang.equals("ko"))
			account = BitmapFactory.decodeResource(context.getResources(), R.drawable.finobank_eng);
		else
			account = BitmapFactory.decodeResource(context.getResources(), R.drawable.finobank_eng);

		if (account != null) {
			try {
				Bitmap accountCopy = account.copy(Bitmap.Config.ARGB_8888, true);
				Canvas canvas = new Canvas(accountCopy);

				Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
				paint.setColor(Color.rgb(0, 0, 0));
				paint.setTextSize((int) (14 * scale));
				canvas.drawText(name, 50, 50, paint);

				paint.setTextSize((int) (8 * scale));
				canvas.drawText(date, 245, 160, paint);
				canvas.drawText(date, 245, 200, paint);

				FileOutputStream fos = context.openFileOutput(accountFile, Context.MODE_PRIVATE);
				accountCopy.compress(Bitmap.CompressFormat.PNG, 10, fos);
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static Bitmap getAccount(Context context) {
		File file;
		file = new File(context.getFilesDir(), accountFile);
		String path = file.getAbsolutePath();

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		return BitmapFactory.decodeFile(path, options);
	}
}

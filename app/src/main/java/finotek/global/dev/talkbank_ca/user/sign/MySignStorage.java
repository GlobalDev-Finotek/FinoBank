package finotek.global.dev.talkbank_ca.user.sign;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import org.joda.time.LocalDateTime;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by idohyeon on 2017. 10. 10..
 */

public class MySignStorage {
	private static final String signFileName = "my_sign.png";

	public static void saveSign(Context context, View view) {
		view.setDrawingCacheEnabled(true);

		Bitmap bitmap = view.getDrawingCache();
		try {
			FileOutputStream fos = context.openFileOutput(signFileName, Context.MODE_PRIVATE);
			bitmap.compress(Bitmap.CompressFormat.PNG, 10, fos);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Bitmap getSign(Context context) {
		File file;
		file = new File(context.getFilesDir(), signFileName);
		String path = file.getAbsolutePath();

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		return BitmapFactory.decodeFile(path, options);
	}
}

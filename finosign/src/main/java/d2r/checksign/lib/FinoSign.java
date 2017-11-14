package d2r.checksign.lib;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by magyeong-ug on 2017. 10. 11..
 */

public class FinoSign {

	static {
		System.loadLibrary("AndroidLibFinoSign");
	}

	public static native int checkSign(byte[] p1, byte[] p2);

	public static void saveSign(Context context, String fileName, String datas) {


		try {
			context.deleteFile(fileName);

			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));
			outputStreamWriter.write(datas);
			outputStreamWriter.close();
		} catch (IOException e) {
			Log.e("Exception", "File write failed: " + e.toString());
		}
	}

	public static int validate(String sign1Data, String sign2Data) {

		int similarity = -1;

		byte[] sign1 = sign1Data.getBytes();
		byte[] sign2 = sign2Data.getBytes();

		similarity = checkSign(sign1, sign2);

		return similarity;
	}

	public static String loadSign(Context context, String fileName) {

		String ret = "";

		try {
			InputStreamReader inputStreamReader = new InputStreamReader(context.openFileInput(fileName));
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String receiveString;
			StringBuilder stringBuilder = new StringBuilder();

			while ((receiveString = bufferedReader.readLine()) != null) {
				stringBuilder.append(receiveString);
                stringBuilder.append("\n");
			}

			ret = stringBuilder.toString();

		} catch (IOException e) {
			Log.e("Exception", "File write failed: " + e.toString());
		}

		return ret;
	}
}

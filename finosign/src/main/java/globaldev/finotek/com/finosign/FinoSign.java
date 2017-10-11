package globaldev.finotek.com.finosign;

import android.content.Context;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

/**
 * Created by magyeong-ug on 2017. 10. 11..
 */

public class FinoSign {

	static {
		System.loadLibrary("libAndroidLibFinoSign");
	}

	public static native int checkSign(byte[] p1, byte[] p2);

	public static void saveSign(Context context, String fileName, List<SignData> datas) {
		try {
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_PRIVATE));

			for(SignData data : datas) {
				outputStreamWriter.write(data.toString() + "\n");
			}

			outputStreamWriter.close();
		}
		catch (IOException e) {
			Log.e("Exception", "File write failed: " + e.toString());
		}
	}

	public static int validate(List<SignData> sign1Data, List<SignData> sign2Data) {

		int similarity = -1;

		byte[] sign1 = convertDatasToBytes(sign1Data);
		byte[] sign2 = convertDatasToBytes(sign2Data);

		similarity = checkSign(sign1, sign2);

		return similarity;
	}

	private static byte[] convertDatasToBytes(List<SignData> datas) {

		int dataSize = datas.size();
		byte[] sign = new byte[dataSize];
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		for(SignData data : datas) {
			try {
				bos.write(data.toByte());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sign;
	}


}

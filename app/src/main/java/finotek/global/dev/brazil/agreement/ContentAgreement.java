package finotek.global.dev.brazil.agreement;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

import finotek.global.dev.brazil.R;

public class ContentAgreement extends AppCompatActivity {
	private String assetTxt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_content_agreement);
		int num = getIntent().getExtras().getInt("num");
		switch (num) {
			case 1:
				first();
				break;
			case 2:
				second();
				break;
			case 3:
				third();
				break;
			case 4:
				fourth();
				break;
			case 5:
				fifth();
				break;
		}
	}

	private void first() {
		TextView text = (TextView) findViewById(R.id.tv);
		try {
			assetTxt = readTxt("test.pdf");
		} catch (Exception e) {
			e.printStackTrace();
		}
		text.setText(assetTxt);

	}

	private void second() {
		TextView text = (TextView) findViewById(R.id.tv);
		try {
			assetTxt = readTxt("view2.txt");
		} catch (Exception e) {
			e.printStackTrace();
		}
		text.setText(assetTxt);

	}

	private void third() {
		TextView text = (TextView) findViewById(R.id.tv);
		try {
			assetTxt = readTxt("view3.txt");
		} catch (Exception e) {
			e.printStackTrace();
		}
		text.setText(assetTxt);

	}

	private void fourth() {
		TextView text = (TextView) findViewById(R.id.tv);
		try {
			assetTxt = readTxt("view4.txt");
		} catch (Exception e) {
			e.printStackTrace();
		}
		text.setText(assetTxt);

	}

	private void fifth() {
		TextView text = (TextView) findViewById(R.id.tv);
		try {
			assetTxt = readTxt("view5.PNG");
		} catch (Exception e) {
			e.printStackTrace();
		}
		text.setText(assetTxt);

	}


	private String readTxt(String file) throws IOException {
		InputStream is = getAssets().open(file);

		int size = is.available();
		byte[] buffer = new byte[size];
		is.read(buffer);
		is.close();

		String text = new String(buffer);

		return text;


	}


}
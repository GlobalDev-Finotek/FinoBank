package finotek.global.dev.talkbank_ca.user.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;

import com.jakewharton.rxbinding2.view.RxView;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.databinding.DialogPdfViewBinding;

public class PdfViewDialog extends Dialog {
	private DialogPdfViewBinding binding;
	private Context context;

	public PdfViewDialog(@NonNull Context context) {
		super(context);
		this.context = context;
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_pdf_view, null, false);
		setContentView(binding.getRoot());

		// addContentView(signView, lp);


		RxView.clicks(binding.closeBtn)
				.throttleFirst(200, TimeUnit.MILLISECONDS)
				.subscribe(aVoid -> {
					this.dismiss();
				});
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		dismiss();
	}

	public void setTitle(String title) {
		binding.titleText.setText(title);
	}

	public void setPdfAssets(String filePath) {

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;

		try {
			/* pdf 비트맵 로드 */
			InputStream is = context.getAssets().open(filePath);
			Bitmap bitmap = BitmapFactory.decodeStream(new BufferedInputStream(is))
					.copy(Bitmap.Config.ARGB_8888, true);

			/* sign 비트맵 로드 */
			String signFilePath = context.getExternalFilesDir(null) + "/mySign.png";
			Bitmap overlaySignBM = BitmapFactory.decodeFile(signFilePath, options)
					.copy(Bitmap.Config.ARGB_8888, true);

			Canvas canvas = new Canvas(bitmap);
			Paint p = new Paint(Paint.FILTER_BITMAP_FLAG);

			int pdfWidth = bitmap.getWidth();
			int pdfHeight = bitmap.getHeight();
			canvas.drawBitmap(overlaySignBM, pdfWidth - 1200, pdfHeight - 1200, p);

			binding.pdfView.setImageDrawable(new BitmapDrawable(context.getResources(), bitmap));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public void show() {
		this.setCancelable(false);
		this.setCanceledOnTouchOutside(false);

		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		super.show();
	}
}

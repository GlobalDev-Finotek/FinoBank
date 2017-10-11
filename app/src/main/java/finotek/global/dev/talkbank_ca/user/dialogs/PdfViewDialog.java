package finotek.global.dev.talkbank_ca.user.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;

import com.github.barteksc.pdfviewer.listener.OnDrawListener;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.databinding.DialogPdfViewBinding;
import finotek.global.dev.talkbank_ca.user.sign.MySignStorage;

public class PdfViewDialog extends Dialog {
	protected DialogPdfViewBinding view;

	public PdfViewDialog(@NonNull Context context) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		view = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_pdf_view, null, false);
		setContentView(view.getRoot());

		RxView.clicks(view.closeBtn)
				.throttleFirst(200, TimeUnit.MILLISECONDS)
				.subscribe(aVoid -> {
					this.dismiss();
				});
	}

	public void setTitle(String title) {
		view.titleText.setText(title);
	}

	public void setPdfAssets(String asset) {
		view.pdfView.fromAsset(asset)
				.enableSwipe(true)
				.enableAntialiasing(true)
				.load();

		view.pdfView.setMinZoom(5);
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

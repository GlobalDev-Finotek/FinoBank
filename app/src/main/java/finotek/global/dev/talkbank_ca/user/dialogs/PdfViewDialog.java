package finotek.global.dev.talkbank_ca.user.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.databinding.DialogPdfViewBinding;

public class PdfViewDialog extends Dialog {
	private DialogPdfViewBinding binding;

	public PdfViewDialog(@NonNull Context context) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_pdf_view, null, false);
		setContentView(binding.getRoot());

		RxView.clicks(binding.closeBtn)
				.throttleFirst(200, TimeUnit.MILLISECONDS)
				.subscribe(aVoid -> {
					this.dismiss();
				});
	}

	public void setTitle(String title) {
		binding.titleText.setText(title);
	}

	public void setPdfAssets(String asset) {
		binding.pdfView.fromAsset(asset)
				.enableSwipe(true)
				.enableAntialiasing(true)
				.load();

		binding.pdfView.setMinZoom(5);
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

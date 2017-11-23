package finotek.global.dev.talkbank_ca.user.dialogs;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import finotek.global.dev.talkbank_ca.user.sign.MySignStorage;

/**
 * Created by idohyeon on 2017. 10. 11..
 */

public class ContractPdfViewDialog extends PdfViewDialog {
    public ContractPdfViewDialog(Context context) {
        super(context);
    }

    @Override
    public void setPdfAssets(String asset) {
        view.pdfView.fromAsset(asset)
                .enableSwipe(true)
                .enableAntialiasing(true)
                .onDraw((canvas, pageWidth, pageHeight, displayedPage) -> {
                    Bitmap signImage = MySignStorage.getSign(getContext());

                    if (signImage != null) {
                        signImage = Bitmap.createScaledBitmap(signImage, 250, 160, true);

                        float x = pageWidth * 0.49f;
                        float y = pageHeight * 0.13f;
                        canvas.drawBitmap(signImage, x, y, null);
                    }
                })
                .load();

        view.pdfView.setMinZoom(5);
    }
}

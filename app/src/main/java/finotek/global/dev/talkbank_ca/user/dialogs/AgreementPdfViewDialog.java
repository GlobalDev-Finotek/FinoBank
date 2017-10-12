package finotek.global.dev.talkbank_ca.user.dialogs;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import finotek.global.dev.talkbank_ca.user.sign.MySignStorage;

/**
 * Created by idohyeon on 2017. 10. 11..
 */

public class AgreementPdfViewDialog extends PdfViewDialog {
    public AgreementPdfViewDialog(Context context) {
        super(context);
    }

    @Override
    public void setPdfAssets(String asset) {
        view.pdfView.fromAsset(asset)
                .enableSwipe(true)
                .enableAntialiasing(true)
                .onDraw((canvas, pageWidth, pageHeight, displayedPage) -> {
                    Bitmap signImage = Bitmap.createScaledBitmap(MySignStorage.getSign(getContext()), 250, 160, true);
//                    Bitmap signImage = MySignStorage.getSign(getContext());
                    if(signImage == null) {
                        Log.d("FINOPASS", "the sign image was not saved");
                    } else {
                        // 한글 파일일 경우
                        if (asset.equals("view.pdf") && displayedPage == 2) {
                            // 대출 서비스 이용 약관
                            float x = pageWidth * 0.49f;
                            float y = pageHeight * 0.13f;
                            canvas.drawBitmap(signImage, x, y, null);
                        } else if (asset.equals("view2.pdf") && displayedPage == 1) {
                            // 개인 신용정보 조회 및 이용 제공 동의
                            float x = pageWidth * 0.49f;
                            float y = pageHeight * 0.29f;
                            canvas.drawBitmap(signImage, x, y, null);
                        } else if (asset.equals("view3.pdf") && displayedPage == 3) {
                            // 대출 거래 약정서
                            float x = pageWidth * 0.49f;
                            float y = pageHeight * 0.65f;
                            canvas.drawBitmap(signImage, x, y, null);
                        } else if (asset.equals("view4.pdf")) {
                            // 계약 안내사항 및 유의사항
                            float x = pageWidth * 0.49f;
                            float y = pageHeight * 0.35f;
                            canvas.drawBitmap(signImage, x, y, null);
                        }

                        // 영어 파일일 경우
                        if (asset.equals("view_eng.pdf") && displayedPage == 2) {
                            // 대출 서비스 이용 약관
                            float x = pageWidth * 0.15f;
                            float y = pageHeight * 0.35f;
                            canvas.drawBitmap(signImage, x, y, null);
                        } else if (asset.equals("view2_eng.pdf") && displayedPage == 1) {
                            // 개인 신용정보 조회 및 이용 제공 동의
                            float x = pageWidth * 0.52f;
                            float y = pageHeight * 0.58f;
                            canvas.drawBitmap(signImage, x, y, null);
                        } else if (asset.equals("view3_eng.pdf") && displayedPage == 3) {
                            // 대출 거래 약정서
                            float x = pageWidth * 0.51f;
                            float y = pageHeight * 0.23f;
                            canvas.drawBitmap(signImage, x, y, null);
                        } else if (asset.equals("view4_eng.pdf")) {
                            // 계약 안내사항 및 유의사항
                            float x = pageWidth * 0.55f;
                            float y = pageHeight * 0.43f;
                            canvas.drawBitmap(signImage, x, y, null);
                        }
                    }
                })
                .load();

        view.pdfView.setMinZoom(5);
    }
}

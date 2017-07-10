package finotek.global.dev.talkbank_ca.user.cardif;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class ContractSigned extends AppCompatImageView {
    private Context context;

    public ContractSigned(Context context) {
        super(context);
    }

    public ContractSigned(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        try {
			/* pdf 비트맵 로드 */
            InputStream is = context.getAssets().open("contract_signed.png");
            Bitmap bitmap = BitmapFactory.decodeStream(new BufferedInputStream(is))
                    .copy(Bitmap.Config.ARGB_8888, true);

			/* sign 비트맵 로드 */
            String signFilePath = context.getExternalFilesDir(null) + "/mySign.png";
            Bitmap overlaySignBM = BitmapFactory.decodeFile(signFilePath, options)
                    .copy(Bitmap.Config.ARGB_8888, true);
            overlaySignBM = Bitmap.createScaledBitmap(overlaySignBM, 213, 120, false);

            Log.d("BNP-APP", "Contract signed, width:" + overlaySignBM.getWidth() + ", height: " + overlaySignBM.getHeight());

            Canvas canvas = new Canvas(bitmap);
            Paint p = new Paint(Paint.FILTER_BITMAP_FLAG);

            int pdfWidth = bitmap.getWidth();
            int pdfHeight = bitmap.getHeight();
            canvas.drawBitmap(overlaySignBM, pdfWidth - 450, pdfHeight - 130, p);

            setImageDrawable(new BitmapDrawable(context.getResources(), bitmap));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ContractSigned(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void save() {
        String path = context.getExternalFilesDir(null) + "/mySignedContract.png";
        Log.d("BNP-APP", "signatured contract is saved.");

        Log.d("BNP-APP", "Measured Width: " + this.getMeasuredWidth() + ", Measured Height: " + getMeasuredHeight());

        Bitmap b = Bitmap.createBitmap(this.getMeasuredWidth(), this.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(b);
        this.draw(canvas);

        if (b != null) {
            try {
                File f = new File(path);
                FileOutputStream fos = new FileOutputStream(f);

                b.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

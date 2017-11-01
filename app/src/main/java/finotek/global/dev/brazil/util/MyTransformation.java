package finotek.global.dev.brazil.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ExifInterface;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;

/**
 * Created by magyeong-ug on 26/05/2017.
 */

public class MyTransformation extends BitmapTransformation {

	private int mOrientation;

	public MyTransformation(Context context, int orientation) {
		super(context);
		mOrientation = orientation;
	}

	@Override
	protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
		int exifOrientationDegrees = getExifOrientationDegrees(mOrientation);
		return TransformationUtils.rotateImageExif(toTransform, pool, exifOrientationDegrees);
	}

	private int getExifOrientationDegrees(int orientation) {
		int exifInt;
		switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				exifInt = ExifInterface.ORIENTATION_NORMAL;
				break;
			//more cases
			case ExifInterface.ORIENTATION_NORMAL:
				exifInt = ExifInterface.ORIENTATION_NORMAL;
				break;

			default:
				exifInt = ExifInterface.ORIENTATION_NORMAL;
				break;
		}
		return exifInt;
	}

	@Override
	public String getId() {
		return this.getClass().getCanonicalName();
	}
}

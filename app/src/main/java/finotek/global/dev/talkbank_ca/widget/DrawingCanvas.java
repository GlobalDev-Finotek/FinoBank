package finotek.global.dev.talkbank_ca.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.Date;

import javax.inject.Inject;

import d2r.checksign.lib.SignData;
import finotek.global.dev.talkbank_ca.R;

/**
 * Created by magyeong-ug on 21/03/2017.
 */

public class DrawingCanvas extends View {

	private Path drawPath;
	private Paint drawPaint, canvasPaint;
	private Canvas drawCanvas;
	private Bitmap canvasBitmap;
	private Context context;
	private OnCanvasTouchListener onCanvasTouchListener;
	private OnDrawListener onDrawListener;

	public DrawingCanvas(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	private void init() {
		drawPath = new Path();
		drawPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

		drawPaint.setColor(ContextCompat.getColor(context, R.color.black));
		drawPaint.setAntiAlias(true);
		drawPaint.setStrokeWidth(10);
		drawPaint.setStyle(Paint.Style.STROKE);
		drawPaint.setStrokeJoin(Paint.Join.ROUND);
		drawPaint.setStrokeCap(Paint.Cap.ROUND);
		drawPaint.setPathEffect(new CornerPathEffect(40));   // set the path effect when they join.
		drawPaint.setDither(true);

		canvasPaint = new Paint(Paint.DITHER_FLAG);
	}

	public void setOnCanvasTouchListener(OnCanvasTouchListener onCanvasTouchListener) {
		this.onCanvasTouchListener = onCanvasTouchListener;
	}

	public void setOnDrawListener(OnDrawListener onDrawListener) {
		this.onDrawListener = onDrawListener;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		drawCanvas = new Canvas(canvasBitmap);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float touchX = event.getX();
		float touchY = event.getY();


		int x = (int) touchX;
		int y = (int) touchY;
		int times = Integer.parseInt(
				String.valueOf(System.currentTimeMillis())
						.substring(3, 12));

		SignData signData = new SignData();
		signData.x = x;
		signData.y = y;
		signData.time = times;
		signData.act = event.getAction();

		onDrawListener.onDraw(signData.toString());

		if (onCanvasTouchListener == null) {
			throw new NullPointerException("canvas touch listener null");
		}

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				onCanvasTouchListener.onTouchStart();
				drawPath.moveTo(touchX, touchY);
				break;
			case MotionEvent.ACTION_MOVE:
				drawPath.lineTo(touchX, touchY);
				break;
			case MotionEvent.ACTION_UP:
				drawCanvas.drawPath(drawPath, drawPaint);
				drawPath.reset();
				onDrawListener.onDraw("\n");
				break;
			default:
				return false;
		}

		invalidate();
		return true;
	}

	public void clear() {
		drawCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
		canvas.drawPath(drawPath, drawPaint);
	}

	public interface OnCanvasTouchListener {
		void onTouchStart();
	}

	public interface OnDrawListener {
		void onDraw(String strData);
	}
}

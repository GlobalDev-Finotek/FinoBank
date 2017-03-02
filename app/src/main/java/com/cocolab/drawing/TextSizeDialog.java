package com.cocolab.drawing;

import android.os.Bundle;
import android.app.Dialog;
import android.content.Context;
import android.graphics.*;
import android.graphics.Paint.Align;
import android.view.MotionEvent;
import android.view.View;

public class TextSizeDialog extends Dialog {
	public interface OnSizeChangedListener {
		void FontChanged(int size, int color, int alpha, boolean isBold, boolean isItalic, boolean isUnderline);
	}

	private OnSizeChangedListener mListener;
	private int mTextsize;
	private int mColor;
	private int mAlpha;
	private boolean mBold;
	private boolean mItalic;
	private boolean mUnderline;
	private static String confirmButton;

	private static class TextSizeView extends View {
		private Paint mPaint;
		private OnSizeChangedListener mListener;

		private final int SPACE_WIDTH = 10;
		private final int SPACE_HEIGHT = 10;
		private final int TITLE_HEIGHT = 40;
		private final int BOX_WIDTH = 50;
		private final int BOX_HEIGHT = 50;
		private final int COLOR_PICKER_WIDTH = 256;
		private final int BUTTON_WIDTH = 80;
		private final int BUTTON_HEIGHT = 40;
		
		private final int DIALOG_WIDTH =  SPACE_WIDTH
										+ BOX_WIDTH*6
										+ SPACE_WIDTH;
		
		private final int DIALOG_HEIGHT = SPACE_HEIGHT
										+ TITLE_HEIGHT
										+ BOX_HEIGHT
										+ TITLE_HEIGHT
										+ BOX_HEIGHT
										+ TITLE_HEIGHT
										+ BOX_HEIGHT
										+ TITLE_HEIGHT
										+ BUTTON_HEIGHT
										+ SPACE_HEIGHT;
		private final int SIZE_START  = SPACE_HEIGHT + TITLE_HEIGHT;
		private final int SIZE_END    = SIZE_START + BOX_HEIGHT;
		private final int COLOR_START = SIZE_END + TITLE_HEIGHT;
		private final int COLOR_END   = COLOR_START + BOX_HEIGHT;
		private final int ALPHA_START = COLOR_END + TITLE_HEIGHT;
		private final int ALPHA_END   = ALPHA_START + BOX_HEIGHT;
		private final int BUTTON_START = ALPHA_END + TITLE_HEIGHT;
		private final int BUTTON_END   = BUTTON_START + BUTTON_HEIGHT;
		
		private final int TITLE_SIZE = 24;
		private final int[] LINE_WIDTH ={1,2,3,5,7,10};
		private final int[] TextSize ={10,14,18,22,26,30};
		private final int[] ColorTable = {Color.rgb(255, 0, 0), Color.rgb(255, 0, 255), Color.rgb(0, 0, 255), 
										  Color.rgb(0, 255, 255), Color.rgb(0, 255, 0), Color.rgb(255, 255, 0)};
		private final int[] AlphaTable = {40, 80, 120, 165, 210, 255};
		
		private int mColor, mTextsize, mAlpha;
		private boolean mBold, mItalic, mUnderline;

		TextSizeView(Context c, OnSizeChangedListener l, int size, int color, int alpha, boolean isBold, boolean isItalic, boolean isUnderline) {
			super(c);
			mListener = l;
			mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			mPaint.setTypeface(Typeface.DEFAULT_BOLD);
			mPaint.setTextAlign(Align.LEFT);
			mPaint.setColor(Color.WHITE);
			
			mTextsize = size;
			mColor = color;
			mAlpha = alpha;
			mBold = isBold;
			mItalic = isItalic;
			mUnderline = isUnderline;
			if(mColor == 0) mColor = ColorTable[0];
		}

		@Override
		protected void onDraw(Canvas canvas) {
			// draw text size title
			mPaint.setColor(Color.WHITE);
			mPaint.setTextSize(TITLE_SIZE);
			mPaint.setTextAlign(Paint.Align.LEFT);
			canvas.drawText("Text Size", SPACE_WIDTH, SPACE_HEIGHT + TITLE_HEIGHT, mPaint);
			
			// draw text "A" for each size		
			for(int i = 0; i < TextSize.length; i++){
				mPaint.setTextSize(TextSize[i]);
				if(mTextsize == TextSize[i]){
					mPaint.setStyle(Paint.Style.FILL); 
					mPaint.setColor(Color.argb(255,100,100,100));
					canvas.drawRect(new Rect(SPACE_WIDTH + (i*(BOX_WIDTH)), SIZE_START, 
											 SPACE_WIDTH + (i*(BOX_WIDTH)) + BOX_WIDTH, SIZE_END),mPaint);
				}
				mPaint.setColor(mColor);
				canvas.drawText("A", SPACE_WIDTH + (i*(BOX_WIDTH)), SIZE_END, mPaint);
			}
			
			// draw color box title
			mPaint.setColor(Color.WHITE);
			mPaint.setTextSize(TITLE_SIZE);
			canvas.drawText("Select Color", SPACE_WIDTH, COLOR_START, mPaint);
			
			// draw color box
			for(int i = 0; i < ColorTable.length; i++){
				mPaint.setColor(ColorTable[i]);
				canvas.drawRect(new Rect(SPACE_WIDTH + (i*(BOX_WIDTH)), COLOR_START,
										 SPACE_WIDTH + (i*(BOX_WIDTH)) + BOX_WIDTH, COLOR_END), mPaint);
			}
			
			// draw transparent title
			mPaint.setColor(Color.WHITE);
			mPaint.setTextSize(TITLE_SIZE);
			canvas.drawText("Transparent Setting", SPACE_WIDTH, ALPHA_START, mPaint);
			
			// draw transparent
			for(int i = 0; i < ColorTable.length; i++){
				mPaint.setColor(mColor);
				mPaint.setAlpha(AlphaTable[i]);
				canvas.drawRect(new Rect(SPACE_WIDTH + (i*(BOX_WIDTH)), ALPHA_START, SPACE_WIDTH + (i*(BOX_WIDTH)) + BOX_WIDTH, ALPHA_END), mPaint);
			}
			
			// draw button
			mPaint.setColor(Color.argb(255,100,100,100));			
			canvas.drawRoundRect(new RectF(DIALOG_WIDTH/2 - BUTTON_WIDTH/2, BUTTON_START, DIALOG_WIDTH/2 + BUTTON_WIDTH/2, BUTTON_END), 10.f, 10.f, mPaint);
			mPaint.setColor(Color.BLACK);
			mPaint.setTextSize(TITLE_SIZE);
			mPaint.setTextAlign(Paint.Align.CENTER);
			mPaint.setColor(Color.WHITE);
			canvas.drawText(confirmButton, (DIALOG_WIDTH)/2, (BUTTON_START+BUTTON_END+TITLE_SIZE)/2, mPaint);
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			setMeasuredDimension(DIALOG_WIDTH, DIALOG_HEIGHT);
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				float x = event.getX();
				float y = event.getY();
				
				if(getIndex(x) < 0) return false;
				
				if(y >= SIZE_START && y < SIZE_END){
					mTextsize = TextSize[getIndex(x)];
				}
				else if(y >= COLOR_START && y < COLOR_END){
					mColor = ColorTable[getIndex(x)];
				}
				else if(y >= ALPHA_START && y < ALPHA_END){
					mAlpha = AlphaTable[getIndex(x)];
				}
				else if(y >= BUTTON_START && y < BUTTON_END){
					if(isButtonX(x)) 
						mListener.FontChanged(mTextsize, mColor, mAlpha, mBold, mItalic, mUnderline);
				}
					
				invalidate();
			}
			return true;
		}
		
		public int getIndex(float x){
			int ret = 0;
			for(int i = 0; i < 6; i++){
				if(x >= (SPACE_WIDTH + i*BOX_WIDTH) && x < (SPACE_WIDTH + (i+1)*BOX_WIDTH)){
					ret = i;
					break;
				}
				else ret = -1;
			}
			return ret;
		}
		public boolean isButtonX(float x){
			if(x >= DIALOG_WIDTH/2 - BUTTON_WIDTH/2 && x < DIALOG_WIDTH/2 + BUTTON_WIDTH/2)
				return true;
			else return false;
		}
		
	}

	public TextSizeDialog(Context context, OnSizeChangedListener listener, int size, int color, int alpha, boolean isBold, boolean isItalic, boolean isUnderline, String confirmStr) {
		super(context);
		mListener = listener;
		mTextsize = size;
		mColor = color;
		mAlpha = alpha;
		mBold = isBold;
		mItalic = isItalic;
		mUnderline = isUnderline;
		confirmButton = confirmStr;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		OnSizeChangedListener l = new OnSizeChangedListener() {
			@Override
			public void FontChanged(int size, int color, int alpha, boolean isBold, boolean isItalic, boolean isUnderline) {
				mListener.FontChanged(size, color, alpha, isBold, isItalic, isUnderline);
				dismiss();
			}
		};
		mTextsize = 30;
		mColor = Color.rgb(255, 0, 0);
		mAlpha = 255;
		mBold = false;
		mItalic = false;
		mUnderline = false;
		setContentView(new TextSizeView(getContext(), l, mTextsize, mColor, mAlpha, mBold, mItalic, mUnderline));
		setTitle("Font");
	}
}
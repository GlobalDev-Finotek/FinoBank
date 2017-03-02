package com.cocolab.subdlg;

import java.io.IOException;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Bundle;
import android.os.Debug;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class DiagramPickerDialog extends Dialog {
    public interface OnColorChangedListener {
        void diagramDrawChanged(int color, int strokeWidth, int diagramType);
    }

    private OnColorChangedListener Listener;
    private static String title_diagram, title_stroke_width, title_stroke_color, button_confirm;

	private static int 
			mStrokeWidth=0,
			mCurrentColor, 
			mCurrentDiagram;

	private static class ColorPickerView extends View {
		private Paint mPaint;
		private OnColorChangedListener vListener;
		private Bitmap bgBitmap;
		private Canvas backCanvas;
		private final int[] mHueBarColors = new int[258];
		private final int[] LINE_WIDTH ={1,2,3,5,7,10};
		private final int[] DIAGRAM_TYPE ={DiagramType.LINE,DiagramType.RECTANGLE,DiagramType.ELLIPSE,DiagramType.TRYANGLE,0};

		private final int 	
				DIALOG_WIDTH = 800, 
				DIALOG_HEIGHT = 1000,
				DIALOG_OUTLINE_WIDTH = 4,
				DIALOG_LEFT_MARGIN = 50,
				DIALOG_RIGHT_MARGIN = 50;
		
		private final int 
				DIAGRAM_TOP_MARGIN = 120,
				DIAGRAM_LEFT_MARGIN = 20,
				DIAGRAM_BETWEENS = 40,
				STROKE_TOP_MARGIN = 400,
				COLOR_ARRANGE_HEIGHT = 130,
				COLOR_ARRANGE_TOP_MARGIN = 650;
		
		private final int 	
				CONFIRM_WIDTH = 200, 
				CONFIRM_HEIGHT = 110,
				CONFIRM_TOP_MARGIN = 840;
		
		private final int 	POPUP_BUTTON_GAP = 19;
		private final int 	roundRect = 10, textSize = 50, textMargine = -40;
		private final int 	SELECT_NONE = -1, SELECT_CONFIRM = 0;
		private int			confirmSelect = -1;
		
		private RectF 		DIALOG_RECT_OUT, 
							DIALOG_RECT_IN, 
							COLOR_ARRANGE_RECT,
							CONFIRM_RECT_OUT, 
							CONFIRM_RECT_IN;

		private RectF[] 	
				STROKE_WIDTH_RECT = new RectF[6],
				DIAGRAM_RECT = new RectF[5]; 

		private final int START_SPACE_WIDTH = 10;
		private final int START_SPACE_HEIGHT = 10;
		private final int STROKE_TITLE_HEIGHT = 40;
		private final int STROKE_BOX_WIDTH = 50;
		private final int STROKE_BOX_HEIGHT = 50;
		private final int COLOR_TITLE_HEIGHT = 50;
		private final int COLOR_BOX_WIDTH = 40;
		private final int COLOR_BOX_HEIGHT = 40;
		private final int COLOR_PICKER_WIDTH = 256;
		private final int BUTTON_WIDTH = 80;
		private final int BUTTON_HEIGHT = 40;
		private final int TITLE_SIZE = 24;

		ColorPickerView(Context c, OnColorChangedListener listener) {
			super(c);
			vListener = listener;

			DisplayMetrics metrics = getResources().getDisplayMetrics();
			int dispW = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? Math.max(metrics.widthPixels, metrics.heightPixels) : Math.min(metrics.widthPixels, metrics.heightPixels);
			int dispH = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? Math.min(metrics.widthPixels, metrics.heightPixels) : Math.max(metrics.widthPixels, metrics.heightPixels);

			displayWidth = dispH > dispW ? dispW : dispH ;
			displayHeight  = dispH > dispW ? dispH : dispW ;

			//this.setBackgroundColor(Color.rgb(200, 200, 200));
			DIALOG_RECT_OUT	= new RectF(0, 
					0, 
					w(DIALOG_WIDTH), 
					h(DIALOG_HEIGHT));
			DIALOG_RECT_IN = new RectF(w(DIALOG_OUTLINE_WIDTH), 
					w(DIALOG_OUTLINE_WIDTH), 
					w(DIALOG_WIDTH)-w(DIALOG_OUTLINE_WIDTH), 
					h(DIALOG_HEIGHT)-w(DIALOG_OUTLINE_WIDTH));
			COLOR_ARRANGE_RECT = new RectF(w(DIALOG_LEFT_MARGIN), 
					h(COLOR_ARRANGE_TOP_MARGIN), 
					w(DIALOG_WIDTH)-w(DIALOG_RIGHT_MARGIN), 
					h(COLOR_ARRANGE_TOP_MARGIN)+h(COLOR_ARRANGE_HEIGHT));
			
			CONFIRM_RECT_OUT = new RectF(w((DIALOG_WIDTH - CONFIRM_WIDTH)/2), 
					h(CONFIRM_TOP_MARGIN), 
					w((DIALOG_WIDTH - CONFIRM_WIDTH)/2 + CONFIRM_WIDTH), 
					h(CONFIRM_TOP_MARGIN+CONFIRM_HEIGHT));
			CONFIRM_RECT_IN = new RectF(w(DIALOG_WIDTH - CONFIRM_WIDTH)/2 + w(DIALOG_OUTLINE_WIDTH), 
					h(CONFIRM_TOP_MARGIN)+w(DIALOG_OUTLINE_WIDTH), 
					w((DIALOG_WIDTH - CONFIRM_WIDTH)/2 + CONFIRM_WIDTH)-w(DIALOG_OUTLINE_WIDTH), 
					h(CONFIRM_TOP_MARGIN+CONFIRM_HEIGHT)-w(DIALOG_OUTLINE_WIDTH));
			
			int innerWidth = (DIALOG_WIDTH - DIALOG_LEFT_MARGIN * 2);
			for (int j=0; j<STROKE_WIDTH_RECT.length; j++){
				STROKE_WIDTH_RECT[j] = new RectF(w(DIALOG_LEFT_MARGIN+j*innerWidth/STROKE_WIDTH_RECT.length), 
						h(STROKE_TOP_MARGIN), 
						w(DIALOG_LEFT_MARGIN+(j+1)*innerWidth/STROKE_WIDTH_RECT.length), 
						h(STROKE_TOP_MARGIN)+h(innerWidth/STROKE_WIDTH_RECT.length));
			}
			for (int j=0; j<DIAGRAM_RECT.length; j++){
				DIAGRAM_RECT[j] = new RectF(w(DIALOG_LEFT_MARGIN+DIAGRAM_LEFT_MARGIN+j*innerWidth/DIAGRAM_RECT.length)+j*w(DIAGRAM_BETWEENS), 
						h(DIAGRAM_TOP_MARGIN), 
						w(DIALOG_LEFT_MARGIN+DIAGRAM_LEFT_MARGIN+(j+1)*innerWidth/DIAGRAM_RECT.length)+j*w(DIAGRAM_BETWEENS), 
						h(DIAGRAM_TOP_MARGIN)+h(innerWidth/DIAGRAM_RECT.length));
			}
			
			int index = 0;
			for (float i=0; i<256; i += 256/42) // Red (#f00) to pink (#f0f)
			{
				mHueBarColors[index] = Color.rgb(255, 0, (int) i);
				index++;
			}
			for (float i=0; i<256; i += 256/42) // Pink (#f0f) to blue (#00f)
			{
				mHueBarColors[index] = Color.rgb(255-(int) i, 0, 255);
				index++;
			}
			for (float i=0; i<256; i += 256/42) // Blue (#00f) to light blue (#0ff)
			{
				mHueBarColors[index] = Color.rgb(0, (int) i, 255);
				index++;
			}
			for (float i=0; i<256; i += 256/42) // Light blue (#0ff) to green (#0f0)
			{
				mHueBarColors[index] = Color.rgb(0, 255, 255-(int) i);
				index++;
			}
			for (float i=0; i<256; i += 256/42) // Green (#0f0) to yellow (#ff0)
			{
				mHueBarColors[index] = Color.rgb((int) i, 255, 0);
				index++;
			}
			for (float i=0; i<256; i += 256/42) // Yellow (#ff0) to red (#f00)
			{
				mHueBarColors[index] = Color.rgb(255, 255-(int) i, 0);
				index++;
			}
			
			initBackground();
		}

		private int 		displayWidth, 
							displayHeight, 
							baseWidth = 1080, 
							baseHeight = 1920;

		private int w(int size) {
			return (size * displayWidth) / baseWidth;
		}

		private int h(int size) {
			return (size * displayHeight) / baseHeight;
		}


	    private void initBackground(){
			mPaint = new Paint(Paint.DITHER_FLAG);
			mPaint.setAntiAlias(true); // 경계면을 부드럽게
			mPaint.setDither(true);
			mPaint.setStrokeJoin(Paint.Join.ROUND); // 끝모양을 둥글게
			mPaint.setStrokeCap(Paint.Cap.ROUND); // 모서리 둥글게

			bgBitmap = Bitmap.createBitmap(w(DIALOG_WIDTH), h(DIALOG_HEIGHT), Bitmap.Config.ARGB_8888);
			backCanvas = new Canvas(bgBitmap);
			
			mPaint.setColor(Color.rgb(111, 113, 116));
			mPaint.setStyle(Paint.Style.FILL);
			backCanvas.drawRoundRect(DIALOG_RECT_OUT, w(roundRect), h(roundRect), mPaint);
			
			mPaint.setColor(Color.rgb(31, 36, 41));
			mPaint.setStyle(Paint.Style.FILL);
			backCanvas.drawRoundRect(DIALOG_RECT_IN, w(roundRect), h(roundRect), mPaint);
			
			for (int i = (int)COLOR_ARRANGE_RECT.left; i<=(int)COLOR_ARRANGE_RECT.right; i++){
	    		mPaint.setColor(mHueBarColors[getArrangeColor(i)]);
				backCanvas.drawLine(i,h(COLOR_ARRANGE_TOP_MARGIN),i,h(COLOR_ARRANGE_TOP_MARGIN+COLOR_ARRANGE_HEIGHT), mPaint);
			}
			mPaint.setColor(Color.rgb(255, 240, 0));
			mPaint.setStyle(Paint.Style.FILL);
			backCanvas.drawRoundRect(CONFIRM_RECT_OUT, w(roundRect), h(roundRect), mPaint);
			mPaint.setColor(Color.rgb(72, 78, 84));
			mPaint.setStyle(Paint.Style.FILL);
			backCanvas.drawRoundRect(CONFIRM_RECT_IN, w(roundRect), h(roundRect), mPaint);
			mPaint.setColor(Color.rgb(255, 240, 0));
			mPaint.setTextSize(w(textSize));
			mPaint.setTextAlign(Paint.Align.CENTER);
			mPaint.setStrokeWidth(2);
			backCanvas.drawText(button_confirm, w(DIALOG_WIDTH / 2), h(CONFIRM_TOP_MARGIN+CONFIRM_HEIGHT+textMargine), mPaint);

			mPaint.setTextAlign(Paint.Align.LEFT);
			backCanvas.drawText(title_diagram, w(DIALOG_LEFT_MARGIN), h(DIAGRAM_TOP_MARGIN)-h(30), mPaint);
			backCanvas.drawText(title_stroke_width, w(DIALOG_LEFT_MARGIN), h(STROKE_TOP_MARGIN)-h(30), mPaint);
			backCanvas.drawText(title_stroke_color, w(DIALOG_LEFT_MARGIN), h(COLOR_ARRANGE_TOP_MARGIN-h(100)), mPaint);
	    }
	    
	    private int getArrangeColor(int x){
	    	int color = -1;
	    	if (x >= (int)COLOR_ARRANGE_RECT.left && x <= (int)COLOR_ARRANGE_RECT.right){
	    		color = (int)(((float)x - COLOR_ARRANGE_RECT.left) / (COLOR_ARRANGE_RECT.right - COLOR_ARRANGE_RECT.left) * 255);
	    	}		
	    	return color;
	    }
	    
		@Override
		protected void onDraw(Canvas canvas) {
			if (bgBitmap != null) canvas.drawBitmap(bgBitmap, 0, 0, mPaint);

			drawWidthCircle(canvas);
			drawScribble(canvas);
		}
		
		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			setMeasuredDimension(w(DIALOG_WIDTH), h(DIALOG_HEIGHT));
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			float x = event.getX();
			float y = event.getY();
			
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				for (int k=0; k<STROKE_WIDTH_RECT.length; k++){
					if (x > STROKE_WIDTH_RECT[k].left && x < STROKE_WIDTH_RECT[k].right && y > STROKE_WIDTH_RECT[k].top && y < STROKE_WIDTH_RECT[k].bottom){
						mStrokeWidth = LINE_WIDTH[k];
						mPaint.setStrokeWidth(mStrokeWidth);
						invalidate();
					}	
				}
				
				for (int k=0; k<DIAGRAM_RECT.length; k++){
					if (x > DIAGRAM_RECT[k].left && x < DIAGRAM_RECT[k].right && y > DIAGRAM_RECT[k].top && y < DIAGRAM_RECT[k].bottom){
						mCurrentDiagram = DIAGRAM_TYPE[k];
						invalidate();
					}	
				}
				
				if (x > COLOR_ARRANGE_RECT.left && x < COLOR_ARRANGE_RECT.right && y > COLOR_ARRANGE_RECT.top && y < COLOR_ARRANGE_RECT.bottom){
					mCurrentColor = mHueBarColors[getArrangeColor((int)x)];
					invalidate();
				}

				if ((int) event.getX() >= CONFIRM_RECT_IN.left && (int) event.getX() <= CONFIRM_RECT_IN.right && (int) event.getY() >= CONFIRM_RECT_IN.top && (int) event.getY() <= CONFIRM_RECT_IN.bottom){
					confirmSelect = SELECT_CONFIRM;
				}
//				if ((int) event.getX() >= EXIT_RECT_IN.left && (int) event.getX() <= EXIT_RECT_IN.right && (int) event.getY() >= EXIT_RECT_IN.top && (int) event.getY() <= EXIT_RECT_IN.bottom){
//					confirmSelect = SELECT_EXIT;
//				}
				break;
			case MotionEvent.ACTION_MOVE:
				if (x > COLOR_ARRANGE_RECT.left && x < COLOR_ARRANGE_RECT.right && y > COLOR_ARRANGE_RECT.top && y < COLOR_ARRANGE_RECT.bottom){
					mCurrentColor = mHueBarColors[getArrangeColor((int)x)];
					invalidate();
				}
				break;
			case MotionEvent.ACTION_UP:
				if ((int) event.getX() >= CONFIRM_RECT_IN.left && (int) event.getX() <= CONFIRM_RECT_IN.right && (int) event.getY() >= CONFIRM_RECT_IN.top && (int) event.getY() <= CONFIRM_RECT_IN.bottom){
					if (confirmSelect == SELECT_CONFIRM) 
						vListener.diagramDrawChanged(mCurrentColor, mStrokeWidth, mCurrentDiagram);
				}
				break;
			default:
				break;
			}
			return true;
		}
		
		private void drawWidthCircle(Canvas canvas){
			for (int k=0; k<STROKE_WIDTH_RECT.length; k++){
				if (LINE_WIDTH[k] == mStrokeWidth){
					mPaint.setStyle(Paint.Style.FILL); 
					mPaint.setColor(Color.argb(255,100,100,100));
					canvas.drawRect(STROKE_WIDTH_RECT[k],mPaint);
				}
				mPaint.setStyle(Paint.Style.FILL);
				mPaint.setColor(mCurrentColor);
				canvas.drawCircle((STROKE_WIDTH_RECT[k].left+STROKE_WIDTH_RECT[k].right)/2, (STROKE_WIDTH_RECT[k].top+STROKE_WIDTH_RECT[k].bottom)/2, LINE_WIDTH[k], mPaint);
			}
		}
		
		private void drawScribble(Canvas canvas){
			for (int k=0; k<DIAGRAM_RECT.length; k++){
				if (mCurrentDiagram == DIAGRAM_TYPE[k]){
					mPaint.setStyle(Paint.Style.FILL); 
					mPaint.setColor(Color.argb(255,100,100,100));
					canvas.drawRect(DIAGRAM_RECT[k],mPaint);
				}
				mPaint.setStyle(Paint.Style.STROKE);
				mPaint.setColor(mCurrentColor);
				mPaint.setStrokeWidth(mStrokeWidth);
				
//				if (DIAGRAM_TYPE[k] == DiagramType.SCRIBBLE){
//					Path path = new Path();
//					path.reset();
//					path.moveTo(DIAGRAM_RECT[k].left+w(20),DIAGRAM_RECT[k].bottom-h(20));
//					path.lineTo(DIAGRAM_RECT[k].left+w(90),DIAGRAM_RECT[k].top+h(30));
//					path.lineTo(DIAGRAM_RECT[k].left+w(70),DIAGRAM_RECT[k].top+h(100));
//					path.lineTo(DIAGRAM_RECT[k].left+w(110),DIAGRAM_RECT[k].top+h(20));
//					//path.rLineTo(DIAGRAM_RECT[k].left-w(10),DIAGRAM_RECT[k].top+h(20));
//					path.quadTo(DIAGRAM_RECT[k].left+w(90),DIAGRAM_RECT[k].top+h(20), DIAGRAM_RECT[k].right-w(20),DIAGRAM_RECT[k].top+h(100));
//					path.lineTo(DIAGRAM_RECT[k].left+w(20),DIAGRAM_RECT[k].top+h(20));
//					canvas.drawPath(path, mPaint);
//				}
				
				if (DIAGRAM_TYPE[k] == DiagramType.LINE){
					Path path = new Path();
					path.reset();
					path.moveTo(DIAGRAM_RECT[k].left+w(30),DIAGRAM_RECT[k].bottom-h(30));
					path.lineTo(DIAGRAM_RECT[k].left+w(110),DIAGRAM_RECT[k].top+h(30));
					canvas.drawPath(path, mPaint);
				}
				if (DIAGRAM_TYPE[k] == DiagramType.RECTANGLE){
					int length = (int)((DIAGRAM_RECT[k].right-DIAGRAM_RECT[k].left)/5);
//					canvas.drawRect(DIAGRAM_RECT[k], mPaint);
					canvas.drawRect(DIAGRAM_RECT[k].left+length, DIAGRAM_RECT[k].top+length, DIAGRAM_RECT[k].right-length, DIAGRAM_RECT[k].bottom-length, mPaint);
				}
				if (DIAGRAM_TYPE[k] == DiagramType.ELLIPSE){
					canvas.drawCircle((DIAGRAM_RECT[k].left+DIAGRAM_RECT[k].right)/2, (DIAGRAM_RECT[k].top+DIAGRAM_RECT[k].bottom)/2, (DIAGRAM_RECT[k].right-DIAGRAM_RECT[k].left)/3, mPaint);
				}
				if (DIAGRAM_TYPE[k] == DiagramType.TRYANGLE){
					Path path = new Path();
					path.reset();
					path.moveTo(DIAGRAM_RECT[k].left+w(30),DIAGRAM_RECT[k].bottom-h(30));
					path.lineTo(DIAGRAM_RECT[k].left+w(110),DIAGRAM_RECT[k].bottom-h(30));
					path.lineTo(DIAGRAM_RECT[k].left+w(70),DIAGRAM_RECT[k].top+w(30));
					path.lineTo(DIAGRAM_RECT[k].left+w(30),DIAGRAM_RECT[k].bottom-h(30));
					canvas.drawPath(path, mPaint);
				}
			}
			
		}
	}

    public DiagramPickerDialog(Context context, OnColorChangedListener listener, int currentColor, int strokeWidth, int diagramType, String _title_diagram, String _title_stroke_width, String _title_stroke_color, String _button_confirm) {
        super(context);

        Listener = listener;
        mStrokeWidth = strokeWidth;
		mCurrentColor = currentColor;
		mCurrentDiagram = diagramType;
		
	    title_diagram = _title_diagram;
	    title_stroke_width = _title_stroke_width;
	    title_stroke_color = _title_stroke_color;
	    button_confirm = _button_confirm;

   }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		
		OnColorChangedListener listener = new OnColorChangedListener() {
			public void diagramDrawChanged(int color, int strokeWidth, int diagram) {
                Listener.diagramDrawChanged(color, strokeWidth, diagram);
                dismiss();
			}
        };

        
        setContentView(new ColorPickerView(getContext(), listener));
        //setTitle(Html.fromHtml("<p align='center'><B>Pick a Color</B></p>"));
    }
    
}

//나인패치 Canvas에 그리기.
//Bitmap mBubbleBitmap = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.scrollbar_handle_horizontal);  
//NinePatch sc = new NinePatch(mBubbleBitmap, mBubbleBitmap.getNinePatchChunk(), "");
//sc.draw(canvas, new Rect(0,0,100,100), mPaint);

//Bitmap tmpBitmap;
//try {
//	tmpBitmap = BitmapFactory.decodeStream(getResources().getAssets().open("score_bg_img.png"));
//	scoreImage = Bitmap.createScaledBitmap(tmpBitmap, w(tmpBitmap.getWidth()), h(tmpBitmap.getHeight()), true);

//	tmpBitmap = BitmapFactory.decodeResource(c.getResources(), R.drawable.popup_bg);
	//tmpBitmap = BitmapFactory.decodeStream(getResources().getAssets().open("popup_bg.9.png"));
	//scoreImage = Bitmap.createScaledBitmap(tmpBitmap, w(tmpBitmap.getWidth()), h(tmpBitmap.getHeight()), true);
	//NinePatch sc = new NinePatch(tmpBitmap, tmpBitmap.getNinePatchChunk(), "");
//	byte[] chunk = tmpBitmap.getNinePatchChunk();
//    np_drawable = new NinePatchDrawable(scoreImage, chunk, new Rect(), null);
//    np_drawable.setBounds(0, 0, 100, 200);
	
//	popupBgNine = new NinePatch(tmpBitmap, tmpBitmap.getNinePatchChunk(), "");
//	Log.d("D2RCSD","ok");
//} catch (IOException e) {
//	e.printStackTrace();
//}


package com.cocolab.drawing;

import java.util.ArrayList;

import com.cocolab.client.message.lineType;
import com.cocolab.subdlg.DiagramType;
import com.cocolab.viewer.SerializablePath;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path.Direction;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.EditText;

public class ConCanvas { 
	public interface OnCanvasListener {
        void updateCanvas();
        void sendLine(int type, float x, float y);
        void sendText(String text, float x, float y);
		void sendShpae(int type, float x1, float y2, float x2, float y1);
    }
	private OnCanvasListener canvasListener;
	
	private float sx;
	private float sy;
	private float mX, mY;
	private float scale;
	private int penWidth;
	private int textSize;
	private int color, textColor, textAlpha, diagram;
	private boolean isBold, isItalic, isUnderline;
	private final float TOUCH_TOLERANCE = 4;
	
	private int page, pageNum;
	private float pageW, pageH;
	private float startX, startY, zoom;
	public static float foreStartX, foreStartY, foreZoom;
	
	int docWidth, docHeight;
	
	private ObjType currentType;
	public static Bitmap foreBitmap, backBitmap;
    private Canvas foreCanvas, backCanvas;
	private Paint paint;
	private SerializablePath path;
	
	private boolean wait = false;
	
    //ArrayList<ConObj> objList[];
	ConObjManager conObjMan;
    
    Context mContext;
    @SuppressWarnings("unchecked")
    
    private static ConCanvas instance;
    
    public static Display getDisplayInfo()
    {
    	Display display = ((WindowManager) instance.mContext
				.getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
    	
    	return display;
    }
    
    public static int getRotateInfo()
    {
    	return instance.mContext.getResources().getConfiguration().orientation;
    }
    
	public ConCanvas(Context context, OnCanvasListener canvaslistener, int w, int h, int pagecnt)
    {
		instance = this;
		
    	mContext = context;
    	this.canvasListener = canvaslistener;
    	
    	this.docWidth = w;
    	this.docHeight = h;
    	
    	penWidth = 2;
    	scale = (float) 1;
    	color = 0xFFFF0000;
    	currentType = ObjType.Curve;
		diagram = DiagramType.SCRIBBLE;
    	textSize = 30;
    	textColor = Color.rgb(255, 0, 0);
    	page = 0;
    	pageNum = pagecnt;
    	zoom = 1;
    	pageW = 0;
    	pageH = 0;
    	startX = 0;
    	startY = 0;
    	
    	foreBitmap = Bitmap.createBitmap(this.getDisplayInfo().getWidth(), this.getDisplayInfo().getHeight(), Bitmap.Config.ARGB_8888);
    	foreCanvas = new Canvas(foreBitmap);
    	backBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
    	backCanvas = new Canvas(backBitmap);
        
        paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
 		paint.setColor(color);
 		paint.setStyle(Paint.Style.STROKE);
 		paint.setStrokeJoin(Paint.Join.ROUND);
 		paint.setStrokeCap(Paint.Cap.ROUND);
 		paint.setStrokeWidth((float)(penWidth*foreZoom/2));
    
 		path = new SerializablePath();

 		Display display = ((WindowManager) this.mContext
				.getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
 		
 		conObjMan = new ConObjManager(pageNum, display.getHeight(), display.getWidth());
 		 		
// 		objList = new ArrayList[pageNum];
// 		for(int i = 0; i < pageNum; i++)
// 			objList[i] = new ArrayList<ConObj>();
 		
 
    }
	
	public void resizeBackBitmap(float _width, float _height){
		if (_width > 0 && _height > 0 && (backBitmap.getWidth() != _width || backBitmap.getHeight() != _height))
			backBitmap = Bitmap.createScaledBitmap(backBitmap, (int) _width, (int) _height, true);
	}
	public RectF resizeForeBitmap(float _width, float _height){
		if (_width > 0 && _height > 0 && (foreBitmap.getWidth() != (int)_width || foreBitmap.getHeight() != (int)_height)){
//			foreBitmap = Bitmap.createBitmap((int)_width, (int)_height, Bitmap.Config.ARGB_4444);
			foreBitmap = Bitmap.createScaledBitmap(foreBitmap, (int) _width, (int) _height, true);
//			foreBitmap = Bitmap.createBitmap(this.getDisplayInfo().getWidth(), this.getDisplayInfo().getHeight(), Bitmap.Config.ARGB_4444);
			foreCanvas = new Canvas(foreBitmap);
			return new RectF(0, 0, foreBitmap.getWidth(), foreBitmap.getHeight());
		}
		return new RectF(0, 0, foreBitmap.getWidth(), foreBitmap.getHeight());
//		return null;
	}
	
	public int getDocWidth() {
		return docWidth;
	}

	public int getDocHeight() {
		return docHeight;
	}

	public void setDocWidth(int docWidth) {
		this.docWidth = docWidth;
	}

	public void setDocHeight(int docHeight) {
		this.docHeight = docHeight;
	}

	public void setDisplaySize(){
		Display display = ((WindowManager) this.mContext
				.getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
        conObjMan.setDeviceHeigth(display.getHeight());
        conObjMan.setDeviceWidth(display.getWidth());
	}
	
	public boolean rotateCanvas(int w, int h)
	{
		if(getDocWidth() == w)
			return false;
		
		this.setDocHeight(h);
		this.setDocWidth(w);
		
		//canvas = null;
		//canvasBack = null;
		
		//closeCanvas();
		int imsi1 = this.getDocWidth();
		int imsi2 = this.getDocHeight();
		
//		foreBitmap = Bitmap.createBitmap(this.getDisplayInfo().getWidth(), this.getDisplayInfo().getHeight(), Bitmap.Config.ARGB_4444);
//		foreCanvas = new Canvas(foreBitmap);
		backBitmap = Bitmap.createBitmap(this.getDocWidth(), this.getDocHeight(), Bitmap.Config.ARGB_8888);
		backCanvas = new Canvas(backBitmap);
        
        Display display = ((WindowManager) this.mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        
//        conObjMan.setDeviceHeigth(display.getWidth());
//        conObjMan.setDeviceWidth(display.getHeight());
        conObjMan.reLoadSubData();
//        Redraw();
        conObjMan.setDeviceHeigth(display.getHeight());
        conObjMan.setDeviceWidth(display.getWidth());
//        conObjMan.setDeviceHeigth(h);
//        conObjMan.setDeviceWidth(w);
//        
        
        return true;
	}
    
//    public Bitmap getBitmap()
//    {
//    	return bitmap;
//    }
    
    public boolean EraseAll()
    {
    	//objList[page].clear();
    	boolean result = conObjMan.eraseObjOnPage(page);
    	Redraw();
    	return result;
    }
    public boolean EraseLastItem()
    {
//    	int index = objList[page].size() - 1;
//    	if(index < 0) return;
//
//    	objList[page].remove(index);
    	boolean result = conObjMan.eraseLastObjOnPage(page);
    	Redraw();
    	return result;
    }
    public void Redraw()
    {
    	if(foreCanvas == null)
    		return;
    	
    	foreCanvas.drawColor(0, PorterDuff.Mode.CLEAR); 
    	backCanvas.drawColor(0, PorterDuff.Mode.CLEAR); 
    	
    	if(this.conObjMan.getObject(page) == null)
    		return;
    	
    	for(ConObj eachObj : this.conObjMan.getObject(page))// objList[page])
    	{
    		eachObj.draw(foreCanvas, foreStartX < 0 ? foreStartX : 0, foreStartY < 0 ? foreStartY : 0, foreZoom);
    		eachObj.draw(backCanvas, 0, 0, 1);
    	}
    	
    	canvasListener.updateCanvas();
    }

	public void StartColorPicker() {

	}

	public void SetCurrentType() {
		diagram = getDiagram();
		if(currentType == ObjType.Text) return;
		if (diagram == DiagramType.SCRIBBLE)
			currentType = ObjType.Curve;
		else if (diagram == DiagramType.RECTANGLE)
			currentType = ObjType.Rectangle;
		else if (diagram == DiagramType.ELLIPSE)
			currentType = ObjType.Circle;
		else if (diagram == DiagramType.LINE)
			currentType = ObjType.Line;
		else if (diagram == DiagramType.TRYANGLE)
			currentType = ObjType.Triangle;
		else
			currentType = ObjType.Curve;

	}

	public void TouchEvent(MotionEvent event) {
		if (foreCanvas == null)
			return;

		float x = event.getX();
		float y = event.getY();
		// set diagram type
		SetCurrentType();
		switch (currentType) {
		case Line:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				sx = x;
				sy = y;
				//path.reset();
				//path.moveTo(x, y);
				path.addPathPoints(new float[] { x, y});
				mX = x;
				mY = y;
				//canvasListener.sendLine(lineType.DOWN, x, y);
			}
			if (event.getAction() == MotionEvent.ACTION_MOVE) {
				float dx = Math.abs(x - mX);
				float dy = Math.abs(y - mY);
				if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
					Redraw();
//					canvas.drawPath(path, paint);
					foreCanvas.drawLine(sx, sy, x, y, paint);
					//canvasListener.sendLine(lineType.UP, x, y);
					// paint.set
					mX = x;
					mY = y;					
					// canvasListener.updateCanvas();
				}
			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
				
				path.addPathPoints(new float[] { sx - startX, sy - startY, mX - startX, mY - startY});
				Matrix matrix = new Matrix();
				matrix.postTranslate(-startX, -startY);
				path.transform(matrix);
				path.setStartX(startX);
				path.setStartY(startY);
				Line newObj = new Line(sx - startX, sy - startY,
						new SerializablePath(path), penWidth * zoom, color, zoom);
				newObj.draw(backCanvas, 0, 0, 1);
				conObjMan.insertObj(page, newObj);
				canvasListener.sendShpae(DiagramType.LINE, sx, sy, mX, mY);
				path.reset();
				path.clearPathPoints();
			}
			//canvasListener.updateCanvas();
			break;
		case Curve:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				sx = x;
				sy = y;
				path.moveTo(x, y);
				path.addPathPoints(new float[] { x, y });
				mX = x;
				mY = y;
				canvasListener.sendLine(lineType.DOWN, x, y);
			}
			if (event.getAction() == MotionEvent.ACTION_MOVE) {
				float dx = Math.abs(x - mX);
				float dy = Math.abs(y - mY);
				if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
					path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
					path.addPathPoints(new float[] { mX, mY, (x + mX) / 2,
							(y + mY) / 2 });
					foreCanvas.drawPath(path, paint);
					canvasListener.sendLine(lineType.MOVE, x, y);
					mX = x;
					mY = y;
					// canvasListener.updateCanvas();
				}
			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
				path.lineTo(mX, mY);
				path.addPathPoints(new float[] { mX, mY });
				foreCanvas.drawPath(path, paint);
				canvasListener.sendLine(lineType.UP, x, y);

				Matrix matrix = new Matrix();
				matrix.postTranslate(-startX, -startY);
				path.transform(matrix);
				path.setStartX(startX);
				path.setStartY(startY);
				Curve newObj = new Curve(sx - startX, sy - startY,
						new SerializablePath(path), penWidth * zoom, color, zoom);
				newObj.draw(backCanvas, 0, 0, 1);
				conObjMan.insertObj(page, newObj);
				path.reset();
				path.clearPathPoints();
			}
			//canvasListener.updateCanvas();
			break;

		case Rectangle:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				sx = x;
				sy = y;
				path.moveTo(x, y);
				path.addPathPoints(new float[] { x, y });
				mX = x;
				mY = y;
				
			}

			if (event.getAction() == MotionEvent.ACTION_MOVE) {
				float dx = Math.abs(x - mX);
				float dy = Math.abs(y - mY);
				if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {

					if (!path.isEmpty()) {

						Paint delPaint = new Paint();
						delPaint.setColor(0x00000000);
						delPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
						delPaint.setAlpha(0x00);
						delPaint.setAntiAlias(true);
						delPaint.setDither(true);
						delPaint.setStyle(Paint.Style.STROKE);
						delPaint.setStrokeJoin(Paint.Join.ROUND);
						delPaint.setStrokeCap(Paint.Cap.ROUND);
						delPaint.setStrokeWidth((float)(penWidth * zoom/2));
						
						foreCanvas.drawPath(path, delPaint);
						Redraw();
						path.rewind();
					}
					RectF rf = new RectF(x, mY, mX, y);
					path.addRect(rf, Direction.CW);

					// paint.set
					foreCanvas.drawPath(path, paint);
					
				}
			}

			if (event.getAction() == MotionEvent.ACTION_UP) {
				
				path.addPathPoints(new float[] { x - startX, mY - startY, mX - startX, y - startY });
				Matrix matrix = new Matrix();
				matrix.postTranslate(-startX, -startY);
				path.transform(matrix);
				path.setStartX(startX);
				path.setStartY(startY);
				Rectangle newObj = new Rectangle(sx - startX, sy - startY,
						new SerializablePath(path), penWidth * zoom, color, zoom);
				newObj.draw(backCanvas, 0, 0, 1);
				conObjMan.insertObj(page, newObj);
				path.reset();
				path.clearPathPoints();
				canvasListener.sendShpae(DiagramType.RECTANGLE, x, mY, mX, y);
			}
			break;
		case Circle:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				sx = x;
				sy = y;
				path.moveTo(x, y);
				path.addPathPoints(new float[] { x, y });
				mX = x;
				mY = y;
			} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
				float dx = Math.abs(x - mX);
				float dy = Math.abs(y - mY);
				if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
					if (!path.isEmpty()) {

						Paint delPaint = new Paint();
						delPaint.setColor(0x00000000);
						delPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
						delPaint.setAlpha(0x00);
						delPaint.setAntiAlias(true);
						delPaint.setDither(true);
						delPaint.setStyle(Paint.Style.STROKE);
						delPaint.setStrokeJoin(Paint.Join.ROUND);
						delPaint.setStrokeCap(Paint.Cap.ROUND);
						delPaint.setStrokeWidth((float)(penWidth * zoom/2));

						foreCanvas.drawPath(path, delPaint);
						Redraw();
						path.rewind();
					}
					RectF rf = new RectF(x, mY, mX, y);
					path.addOval(rf, Direction.CCW);
					foreCanvas.drawPath(path, paint);
					// canvasListener.sendLine(lineType.MOVE, x, y);
				}
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				path.addPathPoints(new float[] { x - startX, mY - startY, mX - startX, y - startY});
				Matrix matrix = new Matrix();
				matrix.postTranslate(-startX, -startY);
				path.transform(matrix);
				path.setStartX(startX);
				path.setStartY(startY);
				Circle newObj = new Circle(sx - startX, sy - startY,
						new SerializablePath(path), penWidth * zoom, color, zoom);

				newObj.draw(backCanvas, 0, 0, 1);
				conObjMan.insertObj(page, newObj);
				canvasListener.sendShpae(DiagramType.ELLIPSE, x, mY, mX, y);
				path.reset();
				path.clearPathPoints();

			}
			break;
		case Triangle:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				sx = x;
				sy = y;
				path.addPathPoints(new float[] { x, y });
				mX = x;
				mY = y;
			} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
				float dx = Math.abs(x - mX);
				float dy = Math.abs(y - mY);
				if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
					Redraw();					
					foreCanvas.drawLine((sx+x)/2, sy, sx, y, paint);
					foreCanvas.drawLine(sx, y, x, y, paint);
					foreCanvas.drawLine((sx+x)/2, sy, x, y, paint);
					// canvasListener.sendLine(lineType.MOVE, x, y);
					
				}
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				path.addPathPoints(new float[] { sx - startX, sy  - startY, x  - startX, y  - startY});
				Matrix matrix = new Matrix();	
				matrix.postTranslate(-startX, -startY);
				path.transform(matrix);
				path.setStartX(startX);
				path.setStartY(startY);
				Triangle newObj = new Triangle(sx - startX, sy - startY,
						new SerializablePath(path), penWidth * zoom, color, zoom);

				newObj.draw(backCanvas, 0, 0, 1);
				conObjMan.insertObj(page, newObj);
				canvasListener.sendShpae(DiagramType.TRYANGLE, sx, sy, x, y);
				path.reset();
				path.clearPathPoints();

			}
			break;
		case Text:
			if (wait)
				return;
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				wait = true;
				inputText(x, y);
			}
			break;
		default:
			break;
		}
	}

	public void drawShape(int diagramType, float x1, float y2, float x2, float y1)
	{
		//1. sanitycheck
		if(foreCanvas == null)
			return;
		
//		sx = x1;
//		sy = y1;

		x1 = x1*foreZoom;
		x2 = x2*foreZoom;
		y1 = y1*foreZoom;
		y2 = y2*foreZoom;
		
		sx = x1;
		sy = y1;
		
		//2. draw Rectangle
		if(diagramType == DiagramType.RECTANGLE)
		{
			path.addPathPoints(new float[] { x1, y1 });
			path.addPathPoints(new float[] { x1 - startX, y2 - startY, x2 - startX, y1 - startY});
			
			RectF rf = new RectF(x1, y2, x2, y1);
			path.addRect(rf, Direction.CW);
			path.offset(foreStartX < 0 ? foreStartX : 0, foreStartY < 0 ? foreStartY : 0);
			foreCanvas.drawPath(path, paint);
			path.offset(foreStartX < 0 ? -foreStartX : 0, foreStartY < 0 ? -foreStartY : 0);
//			Matrix matrix = new Matrix();
//			matrix.postTranslate(-startX, -startY);
//			path.transform(matrix);
//			path.setStartX(startX);
//			path.setStartY(startY);
			Rectangle newObj = new Rectangle(sx - startX, sy - startY,
					new SerializablePath(path), penWidth * foreZoom, color, foreZoom);
			newObj.draw(backCanvas, 0, 0, 1);
			conObjMan.insertObj(page, newObj);
			canvasListener.updateCanvas();
			Redraw();
			path.reset();
			path.clearPathPoints();
			
		}
		
		//3. draw circle
		if(diagramType == DiagramType.ELLIPSE)
		{
			path.addPathPoints(new float[] { x1, y1 });
			path.addPathPoints(new float[] { x1 - startX, y2 - startY, x2 - startX, y1 - startY });
			
			RectF rf = new RectF(x1, y2, x2, y1);
			path.addOval(rf, Direction.CW);
			path.offset(foreStartX < 0 ? foreStartX : 0, foreStartY < 0 ? foreStartY : 0);
			foreCanvas.drawPath(path, paint);
			path.offset(foreStartX < 0 ? -foreStartX : 0, foreStartY < 0 ? -foreStartY : 0);
//			Matrix matrix = new Matrix();
//			matrix.postTranslate(-startX, -startY);
//			path.transform(matrix);
//			path.setStartX(startX);
//			path.setStartY(startY);
			Circle newObj = new Circle(sx - startX, sy - startY,
					new SerializablePath(path), penWidth * foreZoom, color, foreZoom);

			newObj.draw(backCanvas, 0, 0, 1);
			conObjMan.insertObj(page, newObj);
			Redraw();
			canvasListener.updateCanvas();
			path.reset();
			path.clearPathPoints();
					
		}
		//3. draw triangle
		if(diagramType == DiagramType.TRYANGLE)
		{
			path.addPathPoints(new float[] { x1, y1 });
			path.addPathPoints(new float[] { x1 - startX, y2 - startY, x2 - startX, y1 - startY });
											
			path.moveTo((x1+x2)/ (float) 2, y2);
			path.lineTo(x1, y1);
			path.lineTo(x2, y1);
			path.lineTo((x1+x2)/ (float) 2, y2);
			path.offset(foreStartX < 0 ? foreStartX : 0, foreStartY < 0 ? foreStartY : 0);
			foreCanvas.drawPath(path, paint);
			path.offset(foreStartX < 0 ? -foreStartX : 0, foreStartY < 0 ? -foreStartY : 0);
			
//			Matrix matrix = new Matrix();
//			matrix.postTranslate(-startX, -startY);
//			path.transform(matrix);
//			path.setStartX(startX);
//			path.setStartY(startY);
			
			Triangle newObj = new Triangle(sx - startX, sy - startY,
					new SerializablePath(path), penWidth * foreZoom, color, foreZoom);

			newObj.draw(backCanvas, 0, 0, 1);
			conObjMan.insertObj(page, newObj);
			Redraw();
			canvasListener.updateCanvas();
			path.reset();
			path.clearPathPoints();
					
		}
		
		//4. draw Line
		if(diagramType == DiagramType.LINE)
		{
			path.addPathPoints(new float[] { x1, y2 });
			path.addPathPoints(new float[] { x1 - startX, y2 - startY, x2 - startX, y1 - startY });

			path.moveTo(x1, y2);
			path.lineTo(x2, y1);
			path.offset(foreStartX < 0 ? foreStartX : 0, foreStartY < 0 ? foreStartY : 0);
			foreCanvas.drawPath(path, paint);
			path.offset(foreStartX < 0 ? -foreStartX : 0, foreStartY < 0 ? -foreStartY : 0);
//			Matrix matrix = new Matrix();
//			matrix.postTranslate(-startX, -startY);
//			path.transform(matrix);
//			path.setStartX(startX);
//			path.setStartY(startY);
			
			Line newObj = new Line(sx - startX, sy - startY,
					new SerializablePath(path), penWidth * foreZoom, color, foreZoom);

			newObj.draw(backCanvas, 0, 0, 1);
			conObjMan.insertObj(page, newObj);
			Redraw();
			canvasListener.updateCanvas();
			path.reset();
			path.clearPathPoints();
					
		}
	}
	
	
    public void drawLine(int linetype, int _x, int _y){
    	if(foreCanvas == null)
    		return;

    	float x = _x;
    	float y = _y;
    
    	if(linetype == lineType.DOWN){
    		sx = x*foreZoom;
			sy = y*foreZoom;
			path.moveTo((x-startX)*foreZoom, (y-startY)*foreZoom);
			path.addPathPoints(new float[] { x*foreZoom, y*foreZoom });
			mX = x*foreZoom;
			mY = y*foreZoom;
    	}
    	else if(linetype == lineType.MOVE){
    		float dx = Math.abs(x*foreZoom - mX);
			float dy = Math.abs(y*foreZoom - mY);
			if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
				path.quadTo(mX, mY, (x*foreZoom + mX) / 2, (y*foreZoom + mY) / 2);
				path.addPathPoints(new float[] { mX, mY, (x*foreZoom + mX) / 2, (y*foreZoom + mY) / 2 });
				path.offset(foreStartX < 0 ? foreStartX : 0, foreStartY < 0 ? foreStartY : 0);
				foreCanvas.drawPath(path, paint);
				path.offset(foreStartX < 0 ? -foreStartX : 0, foreStartY < 0 ? -foreStartY : 0);
				canvasListener.sendLine(lineType.MOVE, x, y);
				mX = x*foreZoom;
				mY = y*foreZoom;
				//canvasListener.updateCanvas();
			}
 			
 			canvasListener.updateCanvas();
    	}
    	else if(linetype == lineType.UP){
    		path.lineTo(mX, mY);
			path.addPathPoints(new float[] { mX, mY });
			path.offset(foreStartX < 0 ? foreStartX : 0, foreStartY < 0 ? foreStartY : 0);
			foreCanvas.drawPath(path, paint);
			path.offset(foreStartX < 0 ? -foreStartX : 0, foreStartY < 0 ? -foreStartY : 0);
			Curve newObj = new Curve(sx - startX, sy - startY,
					new SerializablePath(path), penWidth * foreZoom, color, foreZoom);
			newObj.draw(backCanvas, 0, 0, 1);
			conObjMan.insertObj(page, newObj);
			Redraw();
			path.reset();
			path.clearPathPoints();
    		
    		
//			path.lineTo(mX, mY);
//			path.addPathPoints(new float[] { mX, mY });
//			foreCanvas.translate(foreStartX < 0 ? foreStartX : 0, foreStartY < 0 ? foreStartY : 0);
// 			foreCanvas.drawPath(path, paint);
//			foreCanvas.translate(foreStartX < 0 ? -foreStartX : 0, foreStartY < 0 ? -foreStartY : 0);
////			Matrix matrix = new Matrix();
////			matrix.postTranslate(-startX, -startY);
////			path.transform(matrix);
////			path.setStartX(startX);
////			path.setStartY(startY);
//			Curve newObj = new Curve(sx - startX, sy - startY, new SerializablePath(path), penWidth*foreZoom, color, foreZoom);
//			newObj.draw(backCanvas, 0, 0, 1);
//			//objList[page].add(newObj);
//			conObjMan.insertObj(page, newObj);
//			Redraw();
//			path.reset();
//			path.clearPathPoints();
//			
			canvasListener.updateCanvas();
    	}
    }
    
    public void drawText(String text, int _x, int _y){
    	if(foreCanvas == null)
    		return;
    	
    	sx = _x;
    	sy = _y;
//    	Text newObj = new Text(sx - startX, sy - startY, new SerializablePath(path), text, textSize * foreZoom, textColor, foreZoom);
//    	conObjMan.insertObj(page, newObj);
//		newObj.draw(foreCanvas, foreStartX < 0 ? foreStartX : 0, foreStartY < 0 ? foreStartY : 0, foreZoom);
////		newObj.draw(foreCanvas, startX, startY, zoom);
//		newObj.draw(backCanvas, 0, 0, 1);
//		canvasListener.updateCanvas();
//		
		
		path.addPathPoints(new float[] { sx * foreZoom, sy * foreZoom });
//		path.offset(foreStartX < 0 ? foreStartX : 0, foreStartY < 0 ? foreStartY : 0);
//		foreCanvas.drawPath(path, paint);
//		path.offset(foreStartX < 0 ? -foreStartX : 0, foreStartY < 0 ? -foreStartY : 0);
		
		Text newObj = new Text(sx - startX, sy - startY,
				new SerializablePath(path), text, textSize * foreZoom, textColor, foreZoom);
		newObj.draw(foreCanvas, foreStartX < 0 ? foreStartX : 0, foreStartY < 0 ? foreStartY : 0, foreZoom);
		newObj.draw(backCanvas, 0, 0, 1);
		canvasListener.updateCanvas();
		
		conObjMan.insertObj(page, newObj);
		path.reset();
		path.clearPathPoints();
    }
    
    public void drawLine(int recvpage, int linetype, int _x, int _y){
    	float x = _x;
    	float y = _y;
    
    	if(linetype == lineType.DOWN){
			sx = x;
			sy = y;
			path.moveTo(x, y);
			path.addPathPoints(new float[] { x, y });
			mX = x;
 			mY = y;
    	}
    	else if(linetype == lineType.MOVE){
			float dx = Math.abs(x - mX);
			float dy = Math.abs(y - mY);
 			if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
 				path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
 				path.addPathPoints(new float[] { mX, mY, (x + mX) / 2, (y + mY) / 2 });
 				//canvas.drawPath(path, paint);
 				mX = x;
 				mY = y;
 			}
 			
 			//canvasListener.updateCanvas();
    	}
    	else if(linetype == lineType.UP){
			path.lineTo(mX, mY);
			path.addPathPoints(new float[] { mX, mY });
			//canvas.drawPath(path, paint);
			Matrix matrix = new Matrix();
			matrix.postTranslate(-startX, -startY);
			path.transform(matrix);
			path.setStartX(startX);
			path.setStartY(startY);
			Curve newObj = new Curve(sx - startX, sy - startY, new SerializablePath(path), penWidth*foreZoom, color, foreZoom);
			newObj.draw(backCanvas, 0, 0, 1);
			//objList[recvpage].add(newObj);
			conObjMan.insertObj(recvpage, newObj);
			path.reset();
			path.clearPathPoints();
			
			//canvasListener.updateCanvas();
    	}
    }
    
    public void EraseAll(int recvpage)
    {
    	//objList[recvpage].clear();
    	conObjMan.eraseObjOnPage(recvpage);
    }
    public void EraseLastItem(int recvpage)
    {
    	conObjMan.eraseLastObjOnPage(recvpage);
//    	int index = objList[recvpage].size() - 1;
//    	if(index < 0) return;
//
//    	objList[recvpage].remove(index);
    }
    
//    public void drawText(int recvpage, String text, int _x, int _y){
//    	sx = _x * foreZoom;
//    	sy = _y * foreZoom;
//    	Text newObj = new Text(sx - startX, sy - startY, text, textSize * foreZoom, textColor, foreZoom);
//		//objList[recvpage].add(newObj);
//    	conObjMan.insertObj(recvpage, newObj);
//		//newObj.draw(canvas, startX, startY, zoom);
//		//newObj.draw(canvasBack, 0, 0, 1);
//		//canvasListener.updateCanvas();
//    }
//    public void runTextSizeDialog(){
//    	(new TextSizeDialog(mContext, (OnSizeChangedListener) mContext)).show();
//    }
    private void inputText(float _x, float _y){
//    	sx = _x;
//    	sy = _y;
		sx = _x;
		sy = _y;
    	
    	AlertDialog.Builder alert = new AlertDialog.Builder(mContext);;
    	final EditText input = new EditText(mContext);
    	alert.setTitle("텍스트 입력");
 		//alert.setMessage("Message");
 		// Set an EditText view to get user input
 		alert.setView(input);
   		alert.setPositiveButton("Ok", new OnClickListener() {
   			@SuppressLint("NewApi")
			@Override
   			public void onClick(DialogInterface dialog, int whichButton) {
   				if(foreCanvas == null)
   		    		return;
   				
 				String text = input.getText().toString();
 				wait = false;
				if(text.isEmpty()) return;
//				canvas.drawText(text, sx, sy, paint);
				
				path.addPathPoints(new float[] { sx * foreZoom, sy * foreZoom });
//				path.offset(foreStartX < 0 ? foreStartX : 0, foreStartY < 0 ? foreStartY : 0);
//				foreCanvas.drawPath(path, paint);
//				path.offset(foreStartX < 0 ? -foreStartX : 0, foreStartY < 0 ? -foreStartY : 0);
				
				Text newObj = new Text(sx - startX, sy - startY,
						new SerializablePath(path), text, textSize * foreZoom, textColor, foreZoom);
				newObj.draw(foreCanvas, foreStartX < 0 ? foreStartX : 0, foreStartY < 0 ? foreStartY : 0, foreZoom);
				newObj.draw(backCanvas, 0, 0, 1);
				canvasListener.sendText(text, sx, sy);
				canvasListener.updateCanvas();
				
				conObjMan.insertObj(page, newObj);
				path.reset();
				path.clearPathPoints();

				
				
/*				
 				Text newObj = new Text(sx*foreZoom - startX, sy*foreZoom - startY, text, textSize*foreZoom, textColor, foreZoom);
				//objList[page].add(newObj);
 				conObjMan.insertObj(page, newObj);
 				
				newObj.draw(foreCanvas, foreStartX < 0 ? foreStartX : 0, foreStartY < 0 ? foreStartY : 0, foreZoom);
				newObj.draw(backCanvas, 0, 0, 1);
				canvasListener.sendText(text, sx, sy);
				canvasListener.updateCanvas();
*/				
			}
 		});
   		alert.setNegativeButton("Cancel",
   		new DialogInterface.OnClickListener() {
       		public void onClick(DialogInterface dialog, int whichButton) {
       		// Canceled.
       			wait = false;
     		}
   		});
//   		alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    	alert.show(); 
    }
    
    private void setPaintMode(){
    	if(currentType == ObjType.Text){
			paint.setTextSize(textSize*foreZoom);
			paint.setColor(textColor);
			paint.setAlpha(textAlpha);
			if(this.isBold)
				paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
			if(this.isItalic)
				paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
			if(this.isUnderline)
				paint.setUnderlineText(true);
			
			paint.setStrokeWidth(1);
			paint.setStyle(Paint.Style.FILL_AND_STROKE);
    	}
    	else{
    		paint.setColor(color);
    		paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
    		paint.setStrokeWidth((float)(penWidth*foreZoom/2));
    		paint.setStyle(Paint.Style.STROKE);
    	}
    }
    
    public void setTextMode(boolean isTextMode){
    	if(isTextMode){
    		currentType = ObjType.Text;
    	}
    	else{
    		currentType = ObjType.Curve;
    	}
    	setPaintMode();
    }
    public boolean isTextMode()
    {
    	if(currentType == ObjType.Text)
    		return true;
    	else return false;
    }
    public void setScale(float s)
    {
    	this.scale = s;
    }
    public void setPenWidth(int w)
    {
    	this.penWidth = w;
    	if(currentType == ObjType.Text){
    		paint.setTextSize(textSize*foreZoom);
    		paint.setStrokeWidth(1);
    	}
    	else{
    		paint.setStrokeWidth((float)(penWidth*foreZoom/2));
    	}
    }
    public void setColor(int c)
    {
    	this.color = c;
    	paint.setColor(color);
    }

	public void setDiagram(int d) {
		this.diagram = d;
	}

	public int getColor() {
		return color;
	}

	public int getWidth() {
		return penWidth;
	}

	public int getDiagram() {
		return diagram;
	}

	public void setPage(int p) {
		this.page = p;
	}
	public void setZoom(float z)
	{
		this.zoom = z;
	}
	public float getZoom(){
		return this.zoom;
	}
	public void setForeZoom(float z)
	{
		this.foreZoom = z;
	}
	public float getForeZoom(){
		return this.foreZoom;
	}
	public void setPageWidth(float w)
	{
		this.pageW = w;
	}
//	public float getPageWidth(){
//		return this.pageW;
//	}
	public void setPageHeight(float h)
	{
		this.pageH = h;
	}
//	public float getPageHeight(){
//		return this.pageH;
//	}
//	public void setStartX(float x)
//	{
//		this.startX = x;
//	}
//	public float getStartX(){
//		return this.startX;
//	}
//	public void setStartY(float y)
//	{
//		this.startY = y;
//	}
//	public float getStartY(){
//		return this.startY;
//	}
//
	public void setForeStartX(float x)
	{
		this.foreStartX = x;
	}
	public float getForeStartX(){
		return this.foreStartX;
	}
	public void setForeStartY(float y)
	{
		this.foreStartY = y;
	}
	public float getForeStartY(){
		return this.foreStartY;
	}

	public void setTextSize(int size) {
		this.textSize = size;
		paint.setTextSize(textSize*zoom);
	}

	public int getTextSize() {
		return this.textSize;
	}
	public void setTextColor(int color) {
		this.textColor = color;
	}

	public int getTextColor() {
		return this.textColor;
	}
	public void setTextAlpha(int alpha) {
		this.textAlpha = alpha;
	}

	public int getTextAlpha() {
		return this.textAlpha;
	}
	public void setTextBold(boolean b) {
		this.isBold = b;
	}

	public boolean getTextBold() {
		return this.isBold;
	}
	public void setTextItalic(boolean i) {
		this.isItalic = i;
	}
	public boolean getTextItalic() {
		return this.isItalic;
	}
	public void setTextUnderline(boolean u) {
		this.isUnderline = u;
	}
	public boolean getTextUnderline() {
		return this.isUnderline;
	}
	public void updatePaint(){
    	setPaintMode();
	}
	
	public void closeCanvas(){
		foreBitmap.recycle();
		foreBitmap = null;
		backBitmap.recycle();
		backBitmap = null;
	}
	
	public byte[] getDrawingData()
	{
		return this.conObjMan.conObjSave();
	}
	
	public boolean setDrawingData(byte[] drawingData){
		return this.conObjMan.conObjLoad(drawingData);
	}


	public void DrawEvent(float _x, float _y, int _mAction) {
		if (foreCanvas == null)
			return;

		float x = _x;
		float y = _y;
		// set diagram type
		SetCurrentType();
		switch (currentType) {
		case Line:
			if (_mAction == MotionEvent.ACTION_DOWN) {
				sx = x*foreZoom;
				sy = y*foreZoom;
				path.addPathPoints(new float[] { x*foreZoom, y*foreZoom});
				mX = x;
				mY = y;
			}
			if (_mAction == MotionEvent.ACTION_MOVE) {
				float dx = Math.abs(x*foreZoom - mX*foreZoom);
				float dy = Math.abs(y*foreZoom - mY*foreZoom);
				if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
					Redraw();
					foreCanvas.translate(foreStartX < 0 ? foreStartX : 0, foreStartY < 0 ? foreStartY : 0);
					foreCanvas.drawLine(sx, sy, x*foreZoom, y*foreZoom, paint);
					foreCanvas.translate(foreStartX < 0 ? -foreStartX : 0, foreStartY < 0 ? -foreStartY : 0);
					//canvasListener.sendLine(lineType.UP, x, y);
					// paint.set
					mX = x;
					mY = y;
					// canvasListener.updateCanvas();
				}
			}
			if (_mAction == MotionEvent.ACTION_UP) {
				path.addPathPoints(new float[] { sx - startX, sy - startY, mX*foreZoom - startX, mY*foreZoom - startY});
				Line newObj = new Line(sx - startX, sy - startY,
						new SerializablePath(path), penWidth * foreZoom, color, foreZoom);
				newObj.draw(backCanvas, 0, 0, 1);
				conObjMan.insertObj(page, newObj);
				canvasListener.sendShpae(DiagramType.LINE, sx, sy, mX, mY);
				path.reset();
				path.clearPathPoints();
			}
			//canvasListener.updateCanvas();
			break;
		case Curve:
			if (_mAction == MotionEvent.ACTION_DOWN) {
//				foreZoom = 1.0f;
//				startX = 0;
//				startY = 0;
				sx = x*foreZoom;
				sy = y*foreZoom;
				path.moveTo((x-startX)*foreZoom, (y-startY)*foreZoom);
				path.addPathPoints(new float[] { x*foreZoom, y*foreZoom });
				mX = x*foreZoom;
				mY = y*foreZoom;
				canvasListener.sendLine(lineType.DOWN, x, y);
			}
			if (_mAction == MotionEvent.ACTION_MOVE) {
				float dx = Math.abs(x*foreZoom - mX);
				float dy = Math.abs(y*foreZoom - mY);
				if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
					path.quadTo(mX, mY, (x*foreZoom + mX) / 2, (y*foreZoom + mY) / 2);
					path.addPathPoints(new float[] { mX, mY, (x*foreZoom + mX) / 2, (y*foreZoom + mY) / 2 });
					path.offset(foreStartX < 0 ? foreStartX : 0, foreStartY < 0 ? foreStartY : 0);
					foreCanvas.drawPath(path, paint);
					path.offset(foreStartX < 0 ? -foreStartX : 0, foreStartY < 0 ? -foreStartY : 0);
					canvasListener.sendLine(lineType.MOVE, x, y);
					mX = x*foreZoom;
					mY = y*foreZoom;
					//canvasListener.updateCanvas();
				}
			}
			if (_mAction == MotionEvent.ACTION_UP) {
				path.lineTo(mX, mY);
				path.addPathPoints(new float[] { mX, mY });
				path.offset(foreStartX < 0 ? foreStartX : 0, foreStartY < 0 ? foreStartY : 0);
				foreCanvas.drawPath(path, paint);
				path.offset(foreStartX < 0 ? -foreStartX : 0, foreStartY < 0 ? -foreStartY : 0);
				canvasListener.sendLine(lineType.UP, x, y);

//				Matrix matrix = new Matrix();
//				matrix.postTranslate(-startX, -startY);
//				path.transform(matrix);
//				path.setStartX(startX);
//				path.setStartY(startY);
				Curve newObj = new Curve(sx - startX, sy - startY,
						new SerializablePath(path), penWidth * foreZoom, color, foreZoom);
				newObj.draw(backCanvas, 0, 0, 1);
				conObjMan.insertObj(page, newObj);
				path.reset();
				path.clearPathPoints();
			}
//			canvasListener.updateCanvas();
			break;

		case Rectangle:
			if (_mAction == MotionEvent.ACTION_DOWN) {
				sx = x*foreZoom;
				sy = y*foreZoom;
				path.moveTo((x-startX)*foreZoom, (y-startY)*foreZoom);
				path.addPathPoints(new float[] { x*foreZoom, y*foreZoom });
				mX = x;
				mY = y;
//				canvasListener.sendLine(lineType.DOWN, x, y);
//				sx = x;
//				sy = y;
//				path.moveTo(x, y);
//				path.addPathPoints(new float[] { x, y });
//				mX = x;
//				mY = y;
//				
			}

			if (_mAction == MotionEvent.ACTION_MOVE) {
				float dx = Math.abs(x*foreZoom - mX*foreZoom);
				float dy = Math.abs(y*foreZoom - mY*foreZoom);
//				float dx = Math.abs(x - mX);
//				float dy = Math.abs(y - mY);
				if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {

					if (!path.isEmpty()) {

						Paint delPaint = new Paint();
						delPaint.setColor(0x00000000);
						delPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
						delPaint.setAlpha(0x00);
						delPaint.setAntiAlias(true);
						delPaint.setDither(true);
						delPaint.setStyle(Paint.Style.STROKE);
						delPaint.setStrokeJoin(Paint.Join.ROUND);
						delPaint.setStrokeCap(Paint.Cap.ROUND);
						delPaint.setStrokeWidth((float)(penWidth * foreZoom/2));
						
						path.offset(foreStartX < 0 ? foreStartX : 0, foreStartY < 0 ? foreStartY : 0);
						foreCanvas.drawPath(path, delPaint);
						path.offset(foreStartX < 0 ? -foreStartX : 0, foreStartY < 0 ? -foreStartY : 0);
						Redraw();
						path.rewind();
					}
					RectF rf = new RectF(x*foreZoom, mY*foreZoom, mX*foreZoom, y*foreZoom);
					path.addRect(rf, Direction.CW);

					// paint.set
					path.offset(foreStartX < 0 ? foreStartX : 0, foreStartY < 0 ? foreStartY : 0);
					foreCanvas.drawPath(path, paint);
					path.offset(foreStartX < 0 ? -foreStartX : 0, foreStartY < 0 ? -foreStartY : 0);
				}
			}

			if (_mAction == MotionEvent.ACTION_UP) {
				
				path.addPathPoints(new float[] { x*foreZoom - startX, mY*foreZoom - startY, mX*foreZoom - startX, y*foreZoom - startY });
//				Matrix matrix = new Matrix();
//				matrix.postTranslate(-startX, -startY);
//				path.transform(matrix);
//				path.setStartX(startX);
//				path.setStartY(startY);
				Rectangle newObj = new Rectangle(sx - startX, sy - startY,
						new SerializablePath(path), penWidth * foreZoom, color, foreZoom);
				newObj.draw(backCanvas, 0, 0, 1);
				conObjMan.insertObj(page, newObj);
				path.reset();
				path.clearPathPoints();
				canvasListener.sendShpae(DiagramType.RECTANGLE, x, mY, mX, y);
			}
			break;
		case Circle:
			if (_mAction == MotionEvent.ACTION_DOWN) {
				sx = x*foreZoom;
				sy = y*foreZoom;
				path.moveTo((x-startX)*foreZoom, (y-startY)*foreZoom);
				path.addPathPoints(new float[] { x*foreZoom, y*foreZoom });
				mX = x;
				mY = y;
//				sx = x;
//				sy = y;
//				path.moveTo(x, y);
//				path.addPathPoints(new float[] { x, y });
//				mX = x;
//				mY = y;
			} else if (_mAction == MotionEvent.ACTION_MOVE) {
				float dx = Math.abs(x*foreZoom - mX*foreZoom);
				float dy = Math.abs(y*foreZoom - mY*foreZoom);
//				float dx = Math.abs(x - mX);
//				float dy = Math.abs(y - mY);
				if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
					if (!path.isEmpty()) {

						Paint delPaint = new Paint();
						delPaint.setColor(0x00000000);
						delPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
						delPaint.setAlpha(0x00);
						delPaint.setAntiAlias(true);
						delPaint.setDither(true);
						delPaint.setStyle(Paint.Style.STROKE);
						delPaint.setStrokeJoin(Paint.Join.ROUND);
						delPaint.setStrokeCap(Paint.Cap.ROUND);
						delPaint.setStrokeWidth((float)(penWidth * foreZoom/2));

						path.offset(foreStartX < 0 ? foreStartX : 0, foreStartY < 0 ? foreStartY : 0);
						foreCanvas.drawPath(path, delPaint);
						path.offset(foreStartX < 0 ? -foreStartX : 0, foreStartY < 0 ? -foreStartY : 0);
						Redraw();
						path.rewind();
					}
					RectF rf = new RectF(x*foreZoom, mY*foreZoom, mX*foreZoom, y*foreZoom);
					path.addOval(rf, Direction.CCW);
					path.offset(foreStartX < 0 ? foreStartX : 0, foreStartY < 0 ? foreStartY : 0);
					foreCanvas.drawPath(path, paint);
					path.offset(foreStartX < 0 ? -foreStartX : 0, -foreStartY < 0 ? foreStartY : 0);
					// canvasListener.sendLine(lineType.MOVE, x, y);
				}
			} else if (_mAction == MotionEvent.ACTION_UP) {
				path.addPathPoints(new float[] { x*foreZoom - startX, mY*foreZoom - startY, mX*foreZoom - startX, y*foreZoom - startY});
//				Matrix matrix = new Matrix();
//				matrix.postTranslate(-startX, -startY);
//				path.transform(matrix);
//				path.setStartX(startX);
//				path.setStartY(startY);
				Circle newObj = new Circle(sx - startX, sy - startY,
						new SerializablePath(path), penWidth * foreZoom, color, foreZoom);

				newObj.draw(backCanvas, 0, 0, 1);
				conObjMan.insertObj(page, newObj);
				canvasListener.sendShpae(DiagramType.ELLIPSE, x, mY, mX, y);
				path.reset();
				path.clearPathPoints();

			}
			break;
		case Triangle:
			if (_mAction == MotionEvent.ACTION_DOWN) {
				sx = x*foreZoom;
				sy = y*foreZoom;
				path.moveTo((x-startX)*foreZoom, (y-startY)*foreZoom);
				path.addPathPoints(new float[] { x*foreZoom, y*foreZoom });
				mX = x;
				mY = y;
//				sx = x;
//				sy = y;
//				path.addPathPoints(new float[] { x, y });
//				mX = x;
//				mY = y;
			} else if (_mAction == MotionEvent.ACTION_MOVE) {
				float dx = Math.abs(x*foreZoom - mX*foreZoom);
				float dy = Math.abs(y*foreZoom - mY*foreZoom);
//				float dx = Math.abs(x - mX);
//				float dy = Math.abs(y - mY);
				if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
					Redraw();					
					foreCanvas.translate(foreStartX < 0 ? foreStartX : 0, foreStartY < 0 ? foreStartY : 0);
					foreCanvas.drawLine((sx+x*foreZoom)/2, sy, sx, y*foreZoom, paint);
					foreCanvas.drawLine(sx, y*foreZoom, x*foreZoom, y*foreZoom, paint);
					foreCanvas.drawLine((sx+x*foreZoom)/2, sy, x*foreZoom, y*foreZoom, paint);
					foreCanvas.translate(foreStartX < 0 ? -foreStartX : 0, foreStartY < 0 ? -foreStartY : 0);
					// canvasListener.sendLine(lineType.MOVE, x, y);
					
				}
			} else if (_mAction == MotionEvent.ACTION_UP) {
				path.addPathPoints(new float[] { sx - startX, sy  - startY, x*foreZoom  - startX, y*foreZoom  - startY});
//				Matrix matrix = new Matrix();	
//				matrix.postTranslate(-startX, -startY);
//				path.transform(matrix);
//				path.setStartX(startX);
//				path.setStartY(startY);
				Triangle newObj = new Triangle(sx - startX, sy - startY,
						new SerializablePath(path), penWidth * foreZoom, color, foreZoom);

				newObj.draw(backCanvas, 0, 0, 1);
				conObjMan.insertObj(page, newObj);
				canvasListener.sendShpae(DiagramType.TRYANGLE, mX, mY, x, y);
				path.reset();
				path.clearPathPoints();

			}
			break;
		case Text:
			if (wait)
				return;
			if (_mAction == MotionEvent.ACTION_DOWN) {
				wait = true;
				inputText(x, y);
			}
			break;
		default:
			break;
		}
	}


//	public void changeBackground(int w, int h){
//		bitmapBack = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
//		canvasBack = new Canvas(bitmapBack);
//	}
	public byte[] getSignData()
	{
		return this.conObjMan.conSignSave();
	}
}

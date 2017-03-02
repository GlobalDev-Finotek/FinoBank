package com.cocolab.drawing;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import com.cocolab.viewer.SerializablePath;

import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.Display;

public class Text extends ConObj{

	String text;
	private SerializablePath path;
	
	public Text(float x, float y, SerializablePath p, String s, float w, int c, float z) {
		super(x, y, w, c, z, ObjType.Text);
		this.text = s;
		this.path = p;
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
	}
	
	public void setPath(SerializablePath p) {
		this.path = p;
	}

	public Path getPath() {
		return this.path;
	}

	public String getText()
	{
		return this.text;
	}
	public void draw(Canvas canvas, float startX, float startY, float z){
		if(canvas == null || path == null) return;
		
		ArrayList<float[]> temp = path.GetPathPoints();
		float[] TextPosition   = temp.get(0);

    	Display display = ConCanvas.getDisplayInfo();
    	
    	int currentDeviceWidth = display.getWidth();
    	ConObjManager.getInstance().getDeviceWidth();
     	float rate = (float)currentDeviceWidth / (ConCanvas.getRotateInfo() == Configuration.ORIENTATION_PORTRAIT ? ConObjManager.getInstance().getDeviceWidth() : ConObjManager.getInstance().getDeviceHeigth());
		
		float rZoom = z/zoom;
		paint.setTextSize(width*rZoom*rate);
		paint.setColor(this.color);
		paint.setStrokeWidth(1);
		//paint.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC),Typeface.ITALIC);
		//paint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.ITALIC));
		canvas.translate(startX, startY);
		canvas.drawText(text, TextPosition[0]*rZoom, TextPosition[1]*rZoom, paint);
		canvas.translate(-startX, -startY);
		
		// 아래는 텍스트 좌표 데이터를 설정해서, 저장시에 저장될 수 있게 한다.(이걸 안하면 확대시 위치가 틀어짐)
		// ConObj unpackingToLoad의 TEXT에서 이 좌표값을 로딩해서 제대로 된 위치를 가져온다.
		this.x = TextPosition[0] + startX;
		this.y = TextPosition[1] + startY;
	}
	
	public byte[] getSubData()
	{
		try {
			return text.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void setSubData(byte[] buf)
	{
		try {
			text = new String(buf, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void reLoadSubData()
	{
		if(path == null) return;
		this.path.loadPathPointsAsQuadTo();
	}
}

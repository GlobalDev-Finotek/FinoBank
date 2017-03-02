package com.cocolab.drawing;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.Display;

import com.cocolab.viewer.SerializablePath;

public class ConObj {
	int page;
	protected float x, y;
	protected float zoom;
	protected int color;
	protected float width;
	protected ObjType type;
	protected Paint paint;
	
	Paint.Style paintStype = Paint.Style.STROKE;
	Paint.Join paintJoin = Paint.Join.ROUND;
	Paint.Cap paintCap = Paint.Cap.ROUND;
	boolean AntiAlias = true;
	boolean Dither = true;
	
	public ConObj(){};
	public ConObj(float x, float y, float w, int c, float z,  ObjType t){
		this.x = x;
		this.y = y;
		this.width = w;
		this.color = c;
		this.zoom = z;
		this.type = t;
		this.paint = new Paint();
		paint.setAntiAlias(AntiAlias);
		paint.setDither(Dither);
 		paint.setColor(color);
 		paint.setStyle(paintStype);
 		paint.setStrokeJoin(paintJoin);
 		paint.setStrokeCap(paintCap);
 		paint.setStrokeWidth(width);
	}
	
	public void setX(float x){
		this.x = x;
	}
	public float getX(){
		return this.x;
	}

	public void setY(float y){
		this.y = y;
	}
	public float getY(){
		return this.y;
	}
	
	public void setColor(int color){
		this.color = color;
		this.paint.setColor(this.color);
	}
	public int getColor(){
		return this.color;
	}
	
	public void setWidth(float w){
		this.width = w;
		this.paint.setStrokeWidth(this.width*zoom);
	}
	public float getWidth(){
		return this.width;
	}
	
	public void setObjType(ObjType type){
		this.type = type;
	}
	public ObjType getObjType(){
		return this.type;
	}
	
	
	public Paint.Style getPaintStype() {
		return paintStype;
	}
	public Paint.Join getPaintJoin() {
		return paintJoin;
	}
	public Paint.Cap getPaintCap() {
		return paintCap;
	}
	public boolean isAntiAlias() {
		return AntiAlias;
	}
	public boolean isDither() {
		return Dither;
	}
	public void setPaintStype(Paint.Style paintStype) {
		this.paintStype = paintStype;
	}
	public void setPaintJoin(Paint.Join paintJoin) {
		this.paintJoin = paintJoin;
	}
	public void setPaintCap(Paint.Cap paintCap) {
		this.paintCap = paintCap;
	}
	public void setAntiAlias(boolean antiAlias) {
		AntiAlias = antiAlias;
	}
	public void setDither(boolean dither) {
		Dither = dither;
	}
	
	
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	
	public void draw(Canvas canvas, float sx, float sy, float z){
		
	}

	public byte[] getSubData()
	{
		return null;
	}
	
	public void setSubData(byte[] buf)
	{
		
	}
	
	public void reLoadSubData()
	{
		
	}
	
	static int headerLength = 47;
	public byte[] packingToSave(int page) // packing with page
	{
		int sindex = 0;
		byte[] subData = getSubData(); 
		
		int totalLength = headerLength + ((subData != null && subData.length > 0) ? subData.length : 0);
		byte[] data = new byte[totalLength];
		
		
		// 0. length of packing with length size
		GTools.Int32ToByteArray(totalLength, sindex, data);
		
		int length = GTools.byteArrayToInt32(sindex, data);
		sindex += 4;
		
		// 1. page (size 4 bytes)
		GTools.Int32ToByteArray(page, sindex, data);
		sindex += 4;
		
		// 2. Type (size 1 byte)
		data[sindex] = (byte)this.type.ordinal();
		sindex++;
		
		// Paint information
		// 3. AntiAlias (size 1byte)
		data[sindex] = 1;
		sindex++;
		
		// 4. Dither (Size 1byte)
		data[sindex] = 1;
		sindex++;
		
		// 5. color(Size 4bytes)
		GTools.Int32ToByteArray(this.color, sindex, data);
		sindex += 4;
		
		// 6. style(size 4bytes)
		GTools.Int32ToByteArray(Paint.Style.STROKE.ordinal(), sindex, data);
		sindex += 4;
		
		// 7. StrokeJoin(size 4bytes)
		GTools.Int32ToByteArray(Paint.Join.ROUND.ordinal(), sindex, data);
		sindex += 4;
		
		// 8. StrokeCap(size 4bytes)
		GTools.Int32ToByteArray(Paint.Cap.ROUND.ordinal(), sindex, data);
		sindex += 4;
		
		// 9. StrokeColor(size 8bytes)
		GTools.Int32ToByteArray(color, sindex, data);
		
		float decode = GTools.byteArrayToInt32(sindex, data);
		sindex += 4;
				
		// 10. StrokeWidth(size 8bytes)
		GTools.floatToByteArray(width, sindex, data);
		sindex += 4;
		
		// 11. zoom (size 8bytes)
		GTools.floatToByteArray(zoom, sindex, data);
		sindex += 4;
		
		// 12. x (size 8bytes)
		GTools.floatToByteArray(x, sindex, data);
		sindex += 4;
		
		// 13. y (size 8bytes)
		GTools.floatToByteArray(y, sindex, data);
		sindex += 4;
		
		// 14. subdata
		if(subData != null && subData.length > 0)
		{
			System.arraycopy(subData, 0, data, sindex, subData.length);
		}
		return data;
	}
	
	public static ConObj unpackingToLoad(byte[] data)
	{
		if(data.length >= (headerLength))
		{
			int subDataLength = data.length - (headerLength);
			
			int sindex = 0;
			
			int length = GTools.byteArrayToInt32(sindex, data);
			sindex += 4;
			
			if(data.length != length)
				return null;
			
			// 1. page (size 4 bytes)
			int page = GTools.byteArrayToInt32(sindex, data);
			sindex += 4;
			
			ObjType type = toObjType(data[sindex]);
			sindex++;
			
			// Skip Paint information
			// 3. AntiAlias (size 1byte)
			sindex++;
			
			// 4. Dither (Size 1byte)
			sindex++;
			
			// 5. color(Size 4bytes)
			sindex += 4;
			
			// 6. style(size 4bytes)
			sindex += 4;
			
			// 7. StrokeJoin(size 4bytes)
			sindex += 4;
			
			// 8. StrokeCap(size 4bytes)
			sindex += 4;
			
			// 9. StrokeColor(size 8bytes)
			int color = GTools.byteArrayToInt32(sindex, data);
			sindex += 4;
			
			// 10. StrokeWidth(size 8bytes)
			float width = GTools.byteArrayToFloat(sindex, data);
			sindex += 4;
			
			// 11. zoom (size 8bytes)
			float zoom = GTools.byteArrayToFloat(sindex, data);
			sindex += 4;
			
			// 12. x (size 8bytes)
			float x = GTools.byteArrayToFloat(sindex, data);
			sindex += 4;
			
			// 13. y (size 8bytes)
			float y = GTools.byteArrayToFloat(sindex, data);
			sindex += 4;
			
			byte[] subData = null;
			
			// 14. subdata
			if(subDataLength > 0)
			{
				subData = new byte[subDataLength];
				System.arraycopy(data, sindex, subData, 0, subDataLength);
			}
			if(type == ObjType.Line)
			{
				Line obj = new Line(x, y, null, (int)width, color, zoom);
				obj.setPage(page);
				if(subData != null)
					obj.setSubData(subData);
				
				return obj;
			}
			else if(type == ObjType.Curve)
			{
				Curve obj = new Curve(x, y, null, (int)width, color, zoom);
				obj.setPage(page);
				if(subData != null)
					obj.setSubData(subData);
				
				return obj;
			}
			else if(type == ObjType.Text)
			{
				Display display = ConCanvas.getDisplayInfo();
		    	float currentDeviceHeight = display.getHeight();
		    	float currentDeviceWidth = display.getWidth();
		    	float virtualDeviceHeight = ConObjManager.getInstance().getDeviceHeigth();
		    	float virtualDeviceWidth = ConObjManager.getInstance().getDeviceWidth();
		    	float rate = currentDeviceWidth / virtualDeviceWidth;
		    	
				SerializablePath path = new SerializablePath();
				path.addPathPoints(new float[] { x*rate, y*rate });
				Text obj = new Text(x*rate, y*rate, path, null, (int)width, color, zoom);
				obj.setPage(page);
				if(subData != null)
					obj.setSubData(subData);
				
				return obj;
			}
			else if(type == ObjType.Circle)
			{
				Circle obj = new Circle(x, y, null, (int)width, color, zoom);
				obj.setPage(page);
				if(subData != null)
					obj.setSubData(subData);
				return obj;
			}
			else if(type == ObjType.Rectangle)
			{
				Rectangle obj = new Rectangle(x, y, null, (int)width, color, zoom);
				obj.setPage(page);
				if(subData != null)
					obj.setSubData(subData);
				return obj;
			}
			else if(type == ObjType.Triangle)
			{
				Triangle obj = new Triangle(x, y, null, (int)width, color, zoom);
				obj.setPage(page);
				if(subData != null)
					obj.setSubData(subData);
				return obj;
			}
		}
		
		return null;
	}
	
	static ObjType toObjType(int bType)
	{
		switch(bType)
		{
		case 0:
			return ObjType.Line;
		case 1:
			return ObjType.Curve;
		case 2:
			return ObjType.Text;
		case 3:
			return ObjType.Circle;
		case 4:
			return ObjType.Rectangle;
		case 5:
			return ObjType.Triangle;
		default:
			return ObjType.UnKnown;
		}
	}

	public byte[] signPackingToSave(int page) // packing with page
	{
		int sindex = 0;
		byte[] subData = getSubData(); 
		
		int totalLength = 4 + ((subData != null && subData.length > 0) ? subData.length : 0);
		byte[] data = new byte[totalLength];
		
		
		// length of packing with length size
		GTools.Int32ToByteArray(totalLength, sindex, data);
		sindex += 4;
		
		// subdata
		if(subData != null && subData.length > 0)
		{
			System.arraycopy(subData, 0, data, sindex, subData.length);
		}
		return data;
	}
}

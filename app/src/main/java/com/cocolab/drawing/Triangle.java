package com.cocolab.drawing;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

import com.cocolab.viewer.SerializablePath;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Path.Direction;

public class Triangle extends ConObj {

	private SerializablePath path;

	public Triangle() {
		super();
	}

	public Triangle(float x, float y, SerializablePath p, float w, int c, float z) {
 		super(x, y, w, c, z, ObjType.Triangle);
		this.path = p;
	}

	public void setPath(SerializablePath p) {
		this.path = p;
	}

	public Path getPath() {
		return this.path;
	}

	public void draw(Canvas canvas, float startX, float startY, float z) {
		if (canvas == null || path == null)
			return; // make null pointer exception

		
		ArrayList<float[]> temp = path.GetPathPoints();
		float[] TrianglePoint = temp.get(1);
/*		
		RectF rf = new RectF(TrianglePoint[0], TrianglePoint[1], TrianglePoint[2], TrianglePoint[3]);
								x1					y1                x2                   y2
			path.moveTo((x1+x2)/2, y1);
			path.lineTo(x1, y2);
			path.lineTo(x2, y2);
			path.lineTo((x1+x2)/2, y1);
		*/
		Path virtualPath = new Path();
		virtualPath.moveTo((TrianglePoint[0]+TrianglePoint[2])/  (float) 2, TrianglePoint[1]);
		virtualPath.lineTo(TrianglePoint[0], TrianglePoint[3]);
		virtualPath.lineTo(TrianglePoint[2], TrianglePoint[3]);
		virtualPath.lineTo((TrianglePoint[0]+TrianglePoint[2])/ (float) 2, TrianglePoint[1]);
		
		
		Matrix matrix = new Matrix();
		matrix.postScale(z / this.zoom, z / this.zoom);
		matrix.postTranslate(startX, startY);

		paint.setStrokeWidth((float)((width * z / zoom)/2.f));
		// paint.setStrokeWidth(width);
		virtualPath.transform(matrix);


		// virtualPath.offset(startX, startY);
		canvas.drawPath(virtualPath, paint);

	}

	byte[] pathToByteArray() throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(
				outputStream);
		objectOutputStream.writeObject(path);
		byte[] array = outputStream.toByteArray();

		return array;
	}

	SerializablePath ByteArrayToPath(byte[] buf) throws StreamCorruptedException, IOException, ClassNotFoundException {
		ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(
				buf));
		SerializablePath object = (SerializablePath) in.readObject();
		object.loadPathPointsAsQuadTo();
		in.close();
		return object;
	}
	
	public byte[] getSubData()
	{
		try {
			return pathToByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void setSubData(byte[] buf)
	{
		try {
			path = this.ByteArrayToPath(buf);
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void reLoadSubData()
	{
		this.path.loadPathPointsAsQuadTo();
	}
}

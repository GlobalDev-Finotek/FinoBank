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

public class Rectangle extends ConObj {

	private SerializablePath path;

	public Rectangle() {
		super();
	}

	public Rectangle(float x, float y, SerializablePath p, float w, int c, float z) {
 		super(x, y, w, c, z, ObjType.Rectangle);
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
		float[] RectPoint = temp.get(1);
		
		RectF rf = new RectF(RectPoint[0], RectPoint[1], RectPoint[2], RectPoint[3]);
		
		Path virtualPath = new Path();
		virtualPath.addRect(rf, Direction.CW);
		
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

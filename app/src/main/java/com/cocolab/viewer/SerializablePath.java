package com.cocolab.viewer;

import java.util.ArrayList;

import java.io.Serializable;

import com.cocolab.drawing.ConCanvas;
import com.cocolab.drawing.ConObjManager;

import android.graphics.Matrix;
import android.graphics.Path;
import android.util.Log;
import android.view.Display;

public class SerializablePath extends Path implements Serializable {

    private ArrayList<float[]> pathPoints;
    private float startX, startY;

    public SerializablePath() {
        super();
        pathPoints = new ArrayList<float[]>();
    }

    public SerializablePath(SerializablePath p) {
        super(p);
        pathPoints = new ArrayList<float[]>();
        pathPoints.addAll(p.pathPoints);
        
        this.setStartX(p.getStartX());
        this.setStartY(p.getStartY());
    }
    public ArrayList<float[]> GetPathPoints()
    {
    	if(pathPoints != null)
    		return pathPoints;
    	return null;
    }
    public void addPathPoints(float[] points) {
    		this.pathPoints.add(points);
    }

    public void loadPathPointsAsQuadTo() {
    	if(pathPoints.size() <= 0)
    		return;
    	
    	float toDeviceHeight, toDeviceWidth;
    	float currentDeviceHeight, currentDeviceWidth;
    	
    	Display display = ConCanvas.getDisplayInfo();
    	
    	toDeviceHeight = display.getHeight();
    	toDeviceWidth = display.getWidth();
    	
    	currentDeviceHeight = ConObjManager.getInstance().getDeviceHeigth();
    	currentDeviceWidth = ConObjManager.getInstance().getDeviceWidth();
    	Log.d("GCM", "deviceHeigth/deviceHeight:"+currentDeviceHeight+"/"+currentDeviceWidth);
    	Log.d("GCM", "currentHeigth/currentHeight:"+toDeviceHeight+"/"+toDeviceWidth);
    	float rate = toDeviceWidth / currentDeviceWidth;
    	
    	this.reset();
    	
    	ArrayList<float[]>  newPathPoints = new ArrayList<float[]>();
    	
        float[] initPoints = pathPoints.get(0);
        this.moveTo(initPoints[0] * rate, initPoints[1] * rate);
        
        newPathPoints.add(new float[] {initPoints[0] * rate, initPoints[1] * rate});
        for (int i = 1; i < pathPoints.size() ; i++) 
        {
        	float[] pointSet = pathPoints.get(i);
        	if(pointSet.length == 4)
        	{
        		this.quadTo(pointSet[0] * rate, pointSet[1] * rate, pointSet[2] * rate, pointSet[3] * rate);
        		newPathPoints.add(new float[] {pointSet[0] * rate, pointSet[1] * rate, pointSet[2] * rate, pointSet[3] * rate});
        	}
        	else if(pointSet.length ==2)
        	{
        		this.lineTo(pointSet[0] * rate, pointSet[1] * rate);
        		newPathPoints.add(new float[] {pointSet[0] * rate, pointSet[1] * rate});
        	}
        } 
        
        pathPoints.clear();
        pathPoints.addAll(newPathPoints);
        
        startX = startX * rate;
        startY = startY * rate;
        
        Matrix matrix = new Matrix();
		matrix.postTranslate(-startX, -startY);
		transform(matrix);
    }
    
    public void clearPathPoints()
    {
    	this.pathPoints.clear();
    }

	public float getStartX() {
		return startX;
	}

	public float getStartY() {
		return startY;
	}

	public void setStartX(float startX) {
		this.startX = startX;
	}

	public void setStartY(float startY) {
		this.startY = startY;
	}
}

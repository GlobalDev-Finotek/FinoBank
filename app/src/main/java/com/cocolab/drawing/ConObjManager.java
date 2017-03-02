package com.cocolab.drawing;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ConObjManager {
	float version = 1.0f;
	
	int totalPage;
	
	float deviceWidth, deviceHeigth;
	
	ArrayList<ConObj> objList[];
	
	private static ConObjManager instance; 
	
	public static ConObjManager getInstance()
	{
		return instance;
	}
	
	public ConObjManager(int totalPage, float deviceHeigth, float deviceWidth){
		instance = this;
		
		this.deviceHeigth = deviceHeigth;
		this.deviceWidth = deviceWidth;
		
		this.totalPage = totalPage;
		objList = new ArrayList[totalPage];
 		for(int i = 0; i < totalPage; i++)
 			objList[i] = new ArrayList<ConObj>();
	}
	
	public void insertObj(int recvPage, ConObj obj){
		objList[recvPage].add(obj);
	}
	
	public void eraseAllObj()
	{
		for(int i = 0; i < totalPage; i++)
		{
			eraseObjOnPage(i);
		}
	}
	
	public boolean eraseObjOnPage(int page)
	{
		int index = objList[page].size() - 1;
    	if(index < 0) return false;
    	
		objList[page].clear();
		return true;
	}
	
	public boolean eraseLastObjOnPage(int page)
	{
		int index = objList[page].size() - 1;
    	if(index < 0) return false;

    	objList[page].remove(index);
    	return true;
	}
	
	public ArrayList<ConObj> getObject(int page)
	{
		int index = objList[page].size() - 1;
    	if(index < 0) return null;
    	
    	return objList[page];
	}
	
	public void reLoadSubData()
	{
		for(int page = 0; page < totalPage; page++)
		{
			int index = objList[page].size() - 1;
	    	if(index < 0) continue;
	    	
	    	for(ConObj eachObj : this.objList[page])// objList[page])
	    	{
	    		eachObj.reLoadSubData();
	    	}
		}
	}
	
	public byte[] conObjSave()
	{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			boolean bNeedSave = false;
			
			for(int i = 0; i < totalPage; i++)
			{
				if(getObject(i) == null)
					continue;
				
				for(ConObj eachObj : getObject(i))
				{
					if(!bNeedSave)
					{
						bNeedSave = true;
						int sindex = 0;
						byte[] data = new byte[52];
						// 1. version
						GTools.floatToByteArray(version, sindex, data);
						sindex += 4;
						
						// 2. device height
						GTools.floatToByteArray(this.deviceHeigth, sindex, data);
						sindex += 4;
						
						// 3. device width
						GTools.floatToByteArray(this.deviceWidth, sindex, data);
						sindex += 4;
						
						// reserve data
						sindex += 4 * 10;
						
						outputStream.write(data);
					}
					outputStream.write(eachObj.packingToSave(i));
				}
			}
			
			byte[] array = outputStream.toByteArray(); 
			return array;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean conObjLoad(byte[] data)
	{
		int readOffset = 0;
		
		this.eraseAllObj();
	
		if(data == null || data.length <= 0)
			return false;
		
		boolean bResult = false;
		
		if(data.length < 52)
			return bResult;
		
		// read version
		this.version = GTools.byteArrayToFloat(readOffset, data);
		readOffset += 4;
		
		// only support version 1.0f
		if(this.version != 1.0)
			return bResult;
		
		this.deviceHeigth = GTools.byteArrayToFloat(readOffset, data);
		readOffset += 4;
		
		this.deviceWidth = GTools.byteArrayToFloat(readOffset, data);
		readOffset += 4;
		
		readOffset += 4 * 10;
					
		while(readOffset < data.length)
		{	
			int packedLength = GTools.byteArrayToInt32(readOffset, data);
			String test = data.toString();
			if(packedLength > 0)// && packedLength < Integer.SIZE )
			{
				byte[] packedData = new byte[packedLength];
				
				System.arraycopy(data, readOffset, packedData, 0, packedLength);
				
				ConObj newObj = ConObj.unpackingToLoad(packedData);
				
				if(newObj != null && newObj.getPage() >= 0)
					
				{
					this.insertObj(newObj.getPage(), newObj);
					bResult = true;
				}
			}
			
			if(packedLength < 0)
				break;
			
			readOffset += packedLength;
		}
		
		return bResult;
	}

	public float getDeviceWidth() {
		return deviceWidth;
	}

	public float getDeviceHeigth() {
		return deviceHeigth;
	}

	public void setDeviceWidth(float deviceWidth) {
		this.deviceWidth = deviceWidth;
	}

	public void setDeviceHeigth(float deviceHeigth) {
		this.deviceHeigth = deviceHeigth;
	}
	
	
	public byte[] conSignSave()
	{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			boolean bNeedSave = false;
			
			for(int i = 0; i < totalPage; i++)
			{
				if(getObject(i) == null)
					continue;
				
				for(ConObj eachObj : getObject(i))
				{
					outputStream.write(eachObj.signPackingToSave(i));
				}
			}
			
			byte[] array = outputStream.toByteArray(); 
			return array;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}

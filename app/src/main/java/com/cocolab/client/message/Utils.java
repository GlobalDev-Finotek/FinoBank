package com.cocolab.client.message;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
	public static String AddDbQutation(String param)
	{
		String retParam = param;
		
		if(!param.startsWith("\""))
			retParam = "\"" + param;
		
		if(!param.endsWith("\""))
			retParam += "\"";
		
		return retParam;
	}
	
	public static boolean isExpired(int expireSeconds, Date startDate){
		long diff = System.currentTimeMillis() - startDate.getTime();
		
		// convert total seconds
		diff = diff / 1000;
		
		if(diff > expireSeconds)
			return true;
		
		return false;
	}
	
	public static String toStringExpireDate(int expireMinutes) {
		long curr = System.currentTimeMillis();
		curr += expireMinutes * 60 * 1000;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date(curr));
	}
	
	public static String toStringfromDateAdd(int addSeconds) {
		long curr = System.currentTimeMillis()+addSeconds;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date(curr));
	}
	
//	public static String toStringfromDateSecond(int second) {
//		long curr = System.currentTimeMillis();
//		curr -= second * 1000;
//
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		return sdf.format(new Date(curr));
//	}
//	
	public static String toStringDate() {
		long curr = System.currentTimeMillis();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date(curr));
	}
	
	public static String toStringDate(int second) {
		long curr = System.currentTimeMillis()+second;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date(curr));
	}
	
	public static Date strToDate(String textDate) throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.parse(textDate);
	}

	public static long ServerTimeGap(String ClientDate) throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return System.currentTimeMillis() - format.parse(ClientDate).getTime();
	}
}

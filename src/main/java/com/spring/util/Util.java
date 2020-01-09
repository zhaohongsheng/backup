package com.spring.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @author zhujf
 * 
 */
public class Util {
	public static boolean isNullOrEmpty(String inStr) {
		return (inStr == null || inStr.trim().length() == 0);
	}
	
	public static String getCurrTime(){
		return new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
	}
	
	public static String getDate(){
		return new SimpleDateFormat("yyyyMMdd").format(new Date());
	}
}

package com.spring.util;

import java.io.IOException;
import java.io.InputStream;

public class MD5Util {
	
	
	public static String getMd5(InputStream iss){
		try {
			if(iss == null){
				return null;
			}
			String putDataFileMd5 = org.springframework.util.DigestUtils.md5DigestAsHex(iss);
			return putDataFileMd5;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}

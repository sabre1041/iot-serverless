package com.redhat.iot.util;

import java.nio.charset.Charset;
import java.util.Base64;

import org.springframework.stereotype.Component;

@Component
public class Utils {
	
	public static String shortTriggerName(String triggerName) {
		
		if(triggerName != null) {
			return triggerName.split("/")[-1];
		}
		
		return triggerName;
	}
	
	public static String base64Encoded(String text) {
	    byte[] encodedText = Base64.getEncoder().encode(
	      text.getBytes(Charset.forName("US-ASCII")));
	    return new String(encodedText);
	  }


}

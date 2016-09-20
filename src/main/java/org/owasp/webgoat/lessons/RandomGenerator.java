package org.owasp.webgoat.lessons;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class RandomGenerator {
	
	public int generateNumber(int x) {
		//hasil random: 0 sampai x-1
		Random rand = new Random(); 
		int value = rand.nextInt(x);

		return value;
	}
	
    public static String hash(String text) 
    {
 	   	try 
 	   	{
 	   		MessageDigest md = MessageDigest.getInstance("MD5");
 	   		byte[] array = md.digest(text.getBytes());
 	   		StringBuffer sb = new StringBuffer();
 	   		for (int i = 0; i < array.length; ++i)
 	   		{
 	   			sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
 	   		}
	        
 	   		return sb.toString();
 	   	} 
 	   	catch (NoSuchAlgorithmException e) 
 	    {
 	    }
 	    return null;
    }
    
	public String generateFlag(String text) {
		int angka = generateNumber(10000);
		String flag = text + angka;
		System.out.println("Raw = " + flag);
		flag = hash(flag);
		System.out.println("Flag{" + flag + "}");
		
		return flag;
	}
}
package org.owasp.webgoat.lessons;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import org.apache.ecs.*;
import org.apache.ecs.html.*;
import org.owasp.webgoat.session.ECSFactory;
import org.owasp.webgoat.session.WebSession;
import java.util.Base64;
import java.util.UUID;
import java.io.UnsupportedEncodingException;

public class A06 extends LessonAdapter
{	
    private final static String INPUT_MD5 = "MD5";

    private final static String INPUT_SHA1 = "SHA1";
    
    private final static String INPUT_BASE64 = "BASE64";
    
    static String md5hash;

	static String sha1hash;
	
	static String base64encode;
	
	private static int acak = generateNumber();
	
    static String kata = wordTarget();
    
    private static String wordTarget() {
		String target = null;
    	if(acak == 0) {
			target = "kalilinux";
		} else if(acak == 1) {
			target = "ubuntu";
		} else if(acak == 2) {
			target = "mandriva";
		}
    	
    	return target;
    }
	
	private static int generateNumber() {
		int x;
		RandomGenerator rn = new RandomGenerator();
		x = rn.generateNumber(3);
		
		return x;
	}
	
	private String getFlag() {
		String raw = "EncodingBasics";
		RandomGenerator rn = new RandomGenerator();
		raw = rn.generateFlag(raw);
		
		FlagList fl = new FlagList("A06", raw, 0, 0);
		fl.changeKey("A06", raw);
				
		return raw;
	}
    
	protected Element createContent(WebSession s)
	{
		String md5Input = null;
		String sha1Input = null;
		String base64Input = null;
		
        ElementContainer ec = new ElementContainer();
        try
        {
        	ec.addElement(new H2("String: " + kata));

            md5Input = s.getParser().getRawParameter(INPUT_MD5, "");
            sha1Input = s.getParser().getStringParameter(INPUT_SHA1, "");
            base64Input = s.getParser().getStringParameter(INPUT_BASE64, "");

            Table table = new Table();
            TR tr = new TR();

            tr.addElement(new TD("Enter MD5 hash: "));
            Input md = new Input(Input.TEXT, INPUT_MD5, md5Input);
            tr.addElement(new TD().addElement(md));
            table.addElement(tr);
            tr = new TR();

            tr.addElement(new TD("Enter SHA-1 hash: "));
            Input sha = new Input(Input.TEXT, INPUT_SHA1, sha1Input);
            tr.addElement(new TD().addElement(sha));
            table.addElement(tr);
            tr = new TR();
            
            tr.addElement(new TD("Enter BASE64 encode: "));
            Input base = new Input(Input.TEXT, INPUT_BASE64, base64Input);
            tr.addElement(new TD().addElement(base));
            table.addElement(tr);
            tr = new TR();

            Element b = ECSFactory.makeButton("Go!");
            tr.addElement(new TD().setAlign("right").setColSpan(2).addElement(b));
            table.addElement(tr);

            ec.addElement(table);

            ec.addElement(new P());
        }        
        catch (Exception e)
        {
            s.setMessage("Error generating " + this.getClass().getName());
            e.printStackTrace();
        }
    	
        md5hash = hashMD5(kata);
    	sha1hash = hashSHA1(kata);
    	base64encode = base64(kata);
    	System.out.println("A6 MD5: " + md5hash);
    	System.out.println("A6 SHA1: " + sha1hash);
    	System.out.println("A6 BASE64: " + base64encode);
    	
        if (md5Input.equalsIgnoreCase(md5hash) && sha1Input.equalsIgnoreCase(sha1hash) && base64Input.equals(base64encode))
        {
        	makeSuccess(s);
            String flag = getFlag();
        	s.setMessage("Flag{" + flag + "}");
        }
          
        return (ec);
	}

    public static String hashMD5(String text) 
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
 	   		e.printStackTrace();
 	    }
 	    return null;
    }
    
    private static String hashSHA1(String word)
	{
	    String sha1 = "";
	    try
	    {
	        MessageDigest crypt = MessageDigest.getInstance("SHA-1");
	        crypt.reset();
	        crypt.update(word.getBytes("UTF-8"));
	        sha1 = byteToHex(crypt.digest());
	    }
	    catch(NoSuchAlgorithmException e)
	    {
	        e.printStackTrace();
	    }
	    catch(UnsupportedEncodingException e)
	    {
	        e.printStackTrace();
	    }
	    return sha1;
	}

	private static String byteToHex(final byte[] hash)
	{
	    Formatter formatter = new Formatter();
	    for (byte b : hash)
	    {
	        formatter.format("%02x", b);
	    }
	    String result = formatter.toString();
	    formatter.close();
	    return result;
	}
	
	private static String base64(String raw)
	{
		String base64encoded = "";
		try 
		{		
	         // Encode using basic encoder
	         base64encoded = Base64.getEncoder().encodeToString(raw.getBytes("utf-8"));         
	     }
		catch(UnsupportedEncodingException e)
		{
	         System.out.println("Error :" + e.getMessage());
	    }
		return base64encoded;
	}
//------------------------------------------------------------------
	public Category getDefaultCategory()
	{
		return Category.SKRIPSI;
	}
	
    
//------------------------------------------------------------------
	public List<String> getHints(WebSession s)
	{
		List<String> hints = new ArrayList<String>();
		hints.add("Masukkan hasil konversi dari kata " + kata);
		hints.add("Gunakan tool enkripsi yang ada di OWASP ZAP");
		
		return hints;
		
	}

//------------------------------------------------------------------
    public Integer getRanking() 
    {
        return new Integer(6);
    }
    
//------------------------------------------------------------------
	public String getTitle()
	{
		return("A6 - Sensitive Data Exposure");
	}

//------------------------------------------------------------------	
    public String getInstructions(WebSession s)
    {
    String instructions = "Untuk mengamankan data sensitif seperti password, digunakan enkripsi data. "
        + "<br><b>Objective</b>: Konversikan kata di bawah menjadi MD5 hash, SHA-1 hash dan BASE64 encode.";

    return (instructions);
    }
}
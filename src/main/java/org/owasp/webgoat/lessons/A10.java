package org.owasp.webgoat.lessons;

import java.util.*;
import org.apache.ecs.*;
import org.apache.ecs.html.*;
import org.owasp.webgoat.session.ECSFactory;
import org.owasp.webgoat.session.WebSession;
import org.owasp.webgoat.util.HtmlEncoder;


// Add copyright text - use text from another lesson 

public class A10 extends LessonAdapter
{		
    private static final String PARAMETER = "param";

    private final static String MESSAGE = "message";
    
    private String message;

	private String getFlag() {
		String raw = "UnvalidatedRedirects";
		RandomGenerator rn = new RandomGenerator();
		raw = rn.generateFlag(raw);
		
		FlagList fl = new FlagList("A10", raw, 0, 0);
		fl.changeKey("A10", raw);
				
		return raw;
	}
	
    @Override
    protected Element createContent(WebSession s)
    {
        ElementContainer ec = new ElementContainer();
        
        if (isTransferFunds(s)){
            ec.addElement(destination(s));
        } else {
        	ec.addElement(new HR());
            ec.addElement(makeInput(s));
            ec.addElement(new HR());
            ec.addElement(makeCurrent(s));
            ec.addElement(new HR());
        }
        return ec;
    }

    protected Element destination(WebSession s) {
        String paramKey = HtmlEncoder.encode(s.getParser().getRawParameter(PARAMETER, ""));
        ElementContainer ec = new ElementContainer();
        
        if (paramKey.length() != 0){
            
            //transfer is confirmed
            ec.addElement(new H1("This is the secret page of A10 Redirect"));
            ec.addElement(new StringElement("<a href=http://localhost:9090/WebGoat/start.mvc>Go back</a>"));
            makeSuccess(s);
            String flag = getFlag();
        	s.setMessage("Flag{" + flag + "}");
        } 
        return ec;
    }

    protected boolean isTransferFunds(WebSession s) {
        return s.getRequest().getParameterMap().containsKey(PARAMETER);
    }

    protected Element makeInput(WebSession s)
    {
        Table t = new Table(0).setCellSpacing(0).setCellPadding(0).setBorder(0);
        TR row1 = new TR();
        TR row2 = new TR();
        
        TD item1 = new TD();
        item1.setVAlign("TOP");
        item1.addElement(new StringElement("Tulis kode HTML di sini: "));
        row1.addElement(item1);

        TD item2 = new TD();
        TextArea ta = new TextArea(MESSAGE, 0, 60);
        ta.addAttribute("wrap", "soft");
        item2.addElement(ta);
        row2.addElement(item2);
        t.addElement(row1);
        t.addElement(row2);
        message = s.getParser().getRawParameter(MESSAGE, "");
        
        Element b = ECSFactory.makeButton("Submit");
        ElementContainer ec = new ElementContainer();
        ec.addElement(t);
        ec.addElement(new P().addElement(b));

        return (ec);
    }

    protected Element makeCurrent(WebSession s)
    {
        ElementContainer ec = new ElementContainer();

        try
        {
            Table t = new Table(0).setCellSpacing(0).setCellPadding(0).setBorder(0);

            String messageData = message;
            TR row1 = new TR(new TD(new B(new StringElement("Test link redirect: <br>"))));
            row1.addElement(new TD(new StringElement(messageData)));
            t.addElement(row1);

            ec.addElement(t);

        } catch (Exception e)
        {
            s.setMessage("Error generating " + this.getClass().getName());
            e.printStackTrace();
        }

        return (ec);
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
		hints.add("Tambahkan alamat target pada fitur redirect /A10redirect?url=");
		hints.add("Tulis kode link HTML tersebut pada kolom dan ketik submit.");
		
		return hints;
		
	}

//------------------------------------------------------------------
    public Integer getRanking() 
    {
        return new Integer(10);
    }
    
//------------------------------------------------------------------
	public String getTitle()
	{
		return("A10 - Unvalidated Redirects and Forwards");
	}
	
//------------------------------------------------------------------	
    public String getInstructions(WebSession s)
    {
    String instructions = "Terdapat fitur redirect yang dapat digunakan secara bebas. "
    	+ "<br>Halaman tujuan redirect tidak divalidasi dan dapat digunakan dengan tujuan apapun. "
    	+ "<br>Contohnya adalah: http://localhost:9090/WebGoat/A10redirect?url=http://google.com/"
    	+ "<br>Alamat tersebut ketika diakses akan mengarah ke google. "
        + "<br><b>Objective</b>: Buat sebuah link (tautan) menggunakan kode HTML yang berisi redirect A10 seperti contoh "
        + "dengan halaman tujuan adalah http://localhost:9090/WebGoat/attack?Screen=27"
        + "<br>Flag akan muncul pada halaman tujuan";

    return (instructions);
    }
}
package org.owasp.webgoat.lessons;

import java.util.ArrayList;
import java.util.List;
import org.apache.ecs.Element;
import org.apache.ecs.ElementContainer;
import org.apache.ecs.StringElement;
import org.owasp.webgoat.session.ECSFactory;
import org.owasp.webgoat.session.WebSession;
import org.apache.ecs.*;
import org.apache.ecs.html.*;
import org.owasp.webgoat.session.WebSession;

// Add copyright text - use text from another lesson 

public class A05 extends LessonAdapter
{	
    private final static String SUCCEEDED = "succeeded";

    /**
     * Description of the Method
     * 
     * @param s
     *            Description of the Parameter
     * @return Description of the Return Value
     */
    private String getFlag() {
		String raw = "ForcedBrowsing";
		RandomGenerator rn = new RandomGenerator();
		raw = rn.generateFlag(raw);
		
		FlagList fl = new FlagList("A05", raw, 0, 0);
		fl.changeKey("A05", raw);
				
		return raw;
	}    
    
    protected Element createContent(WebSession s)
    {
        ElementContainer ec = new ElementContainer();
        String success = new String(s.getParser().getStringParameter(SUCCEEDED, ""));

        if (success.length() != 0 && success.equals("yes"))
        {
            ec.addElement(new BR().addElement(new H1().addElement("Welcome to WebGoat Configuration Page")));
            ec.addElement(new BR());
            Table t1 = new Table().setCellSpacing(0).setCellPadding(0).setBorder(0).setWidth("90%").setAlign("center");

            TR tr = new TR();
            tr.addElement(new TD(new StringElement("Set Admin Privileges for: ")));

            Input input1 = new Input(Input.TEXT, "", "");
            tr.addElement(new TD(input1));
            t1.addElement(tr);

            tr = new TR();
            tr.addElement(new TD(new StringElement("Set Admin Password:")));

            input1 = new Input(Input.PASSWORD, "", "");
            tr.addElement(new TD(input1));
            t1.addElement(tr);

            tr.addElement(new TD(new StringElement("<a href=http://localhost:9090/WebGoat/start.mvc>Go back</a>")));

            ec.addElement(t1);

            makeSuccess(s);
            String flag = getFlag();
        	s.setMessage("Flag{" + flag + "}");
        }
        else
        {
            ec.addElement("Terdapat halaman config yang hanya dapat diakses oleh admin website. "
            		+ "<br><b>Objective</b>: Akses halaman config dengan menebak url yang digunakan. "
            		+ "<br>Klik tombol kembali ketika sudah berhasil mengakses halaman config");
        }
        return ec;
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
        hints.add("Halaman config admin dapat ditebak dengan mudah");
        hints.add("Gunakan beberapa kemungkinan kata untuk mengakses config");
        hints.add("http://localhost/WebGoat/...");
		
		return hints;		
	}

//------------------------------------------------------------------
    public Integer getRanking() 
    {
        return new Integer(5);
    }
    
//------------------------------------------------------------------
	public String getTitle()
	{
		return("A5 - Security Misconfiguration");
	}
}
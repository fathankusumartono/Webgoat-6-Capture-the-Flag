package org.owasp.webgoat.lessons;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import org.apache.ecs.*;
import org.apache.ecs.html.*;
import org.owasp.webgoat.session.DatabaseUtilities;
import org.owasp.webgoat.session.ECSFactory;
import org.owasp.webgoat.session.WebSession;
import org.owasp.webgoat.util.HtmlEncoder;

// Add copyright text - use text from another lesson 

public class A08 extends LessonAdapter
{	
    private static final String TRANSFER_FUNDS_PARAMETER = "transferFunds";
    
    private static final String TRANSFER_FUNDS_PAGE = "main";

    private final static String MESSAGE = "message";

    private final static String TITLE = "title";
    
    private String message;
    private String title;

	private String getFlag() {
		String raw = "CrossSiteRequestForgery";
		RandomGenerator rn = new RandomGenerator();
		raw = rn.generateFlag(raw);
		
		FlagList fl = new FlagList("A08", raw, 0, 0);
		fl.changeKey("A08", raw);
				
		return raw;
	}
	
    @Override
    protected Element createContent(WebSession s)
    {
        ElementContainer ec = new ElementContainer();
        
        if (isTransferFunds(s)){
            ec.addElement(doTransfer(s));
        } else {
        	ec.addElement(new HR());
            ec.addElement(makeInput(s));
            ec.addElement(new HR());
            ec.addElement(makeCurrent(s));
            ec.addElement(new HR());
        }
        return ec;
    }

    /**
     * if TRANSFER_FUND_PARAMETER is a parameter, then doTransfer is invoked.  doTranser presents the 
     * web content to display the electronic transfer of funds.  An request
     * should have a dollar amount specified.  When this page is accessed it will mark the lesson complete  
     * 
     * @param s
     * @return Element will appropriate web content for a transfer of funds.
     */
    protected Element doTransfer(WebSession s) {
        String transferFunds = HtmlEncoder.encode(s.getParser().getRawParameter(TRANSFER_FUNDS_PARAMETER, ""));
        ElementContainer ec = new ElementContainer();
        
        if (transferFunds.equalsIgnoreCase(TRANSFER_FUNDS_PAGE)){
            
            //transfer form
            ec.addElement(new H1("Electronic Transfer:"));
            String action = getLink();
            Form form = new Form(action, Form.POST);
            form.addElement( new Input(Input.text, TRANSFER_FUNDS_PARAMETER, "0"));
            //if this token is present we won't mark the lesson as completed
            form.addElement( new Input(Input.submit));
            ec.addElement(form);
            //present transfer funds form
        } else if (transferFunds.length() != 0){
            
            //transfer is confirmed
            ec.addElement(new H1("Electronic Transfer Complete"));
            ec.addElement(new StringElement("Amount Transfered: "+transferFunds));
            ec.addElement(new HR());
            ec.addElement(new StringElement("<a href=http://localhost:9090/WebGoat/start.mvc>Go back</a>"));
            makeSuccess(s);
            String flag = getFlag();
        	s.setMessage("Flag{" + flag + "}");
        } 
        return ec;
    }

    /**
     * @param s current web session
     * @return true if the page should be rendered as a Transfer of funds page or false for the normal message posting page.
     */
    protected boolean isTransferFunds(WebSession s) {
        return s.getRequest().getParameterMap().containsKey(TRANSFER_FUNDS_PARAMETER);
    }

    /**
     * Description of the Method
     * 
     * @param s
     *            Description of the Parameter
     * @return Description of the Return Value
     */
    protected Element makeInput(WebSession s)
    {
        Table t = new Table(0).setCellSpacing(0).setCellPadding(0).setBorder(0);
        TR row1 = new TR();
        TR row2 = new TR();
        row1.addElement(new TD(new StringElement("Title: ")));

        Input inputTitle = new Input(Input.TEXT, TITLE, "");
        row1.addElement(new TD(inputTitle));
        title = HtmlEncoder.encode(s.getParser().getRawParameter(TITLE, ""));
        
        TD item1 = new TD();
        item1.setVAlign("TOP");
        item1.addElement(new StringElement("Message: "));
        row2.addElement(item1);

        TD item2 = new TD();
        TextArea ta = new TextArea(MESSAGE, 12, 60);
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
            ec.addElement(new H1("You got a Message!"));
            Table t = new Table(0).setCellSpacing(0).setCellPadding(0).setBorder(0);
            TR row1 = new TR(new TD(new B(new StringElement("Title: "))));
            row1.addElement(new TD(new StringElement(title)));
            t.addElement(row1);

            String messageData = message;
            TR row2 = new TR(new TD(new B(new StringElement("Message: "))));
            row2.addElement(new TD(new StringElement(messageData)));
            t.addElement(row2);

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
		hints.add("Isi dari Email bebas");
        hints.add("Salah satu sumber gambar yang dapat digunakan: <pre>http://localhost:9090/WebGoat/images/icon.jpg</pre>");
		
		return hints;
		
	}

//------------------------------------------------------------------
    public Integer getRanking() 
    {
        return new Integer(8);
    }
    
//------------------------------------------------------------------
	public String getTitle()
	{
		return("A8 - Cross Site Request Forgery");
	}

//------------------------------------------------------------------	
    public String getInstructions(WebSession s)
    {
    String instructions = "Halaman ini merupakan simulasi dari pengiriman email. "
    	+ "<br>Hasil dari Email yang telah ditulis akan ditampilkan pada bagian bawah halaman. "
        + "<br><b>Objective</b>: Buat sebuah Email yang berisi gambar disertai link ke halaman transaksi. "
        + "<br>Halaman tujuan transaksi adalah /WebGoat/attack?Screen=14&menu=3005&transferFunds=5000";

    return (instructions);
    }
}
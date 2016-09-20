package org.owasp.webgoat.lessons;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.ecs.Element;
import org.apache.ecs.ElementContainer;
import org.apache.ecs.StringElement;
import org.apache.ecs.html.BR;
import org.apache.ecs.html.Center;
import org.apache.ecs.html.H1;
import org.apache.ecs.html.HR;
import org.apache.ecs.html.Input;
import org.apache.ecs.html.TD;
import org.apache.ecs.html.TH;
import org.apache.ecs.html.TR;
import org.apache.ecs.html.Table;
import org.owasp.webgoat.session.ECSFactory;
import org.owasp.webgoat.session.WebSession;
import org.owasp.webgoat.util.HtmlEncoder;

// Add copyright text - use text from another lesson 

public class A03 extends LessonAdapter
{	
	private String getFlag() {
		String raw = "ReflectedXSS";
		RandomGenerator rn = new RandomGenerator();
		raw = rn.generateFlag(raw);
		
		FlagList fl = new FlagList("A03", raw, 0, 0);
		fl.changeKey("A03", raw);
				
		return raw;
	}
	
	protected Element createContent(WebSession s)
    {

        ElementContainer ec = new ElementContainer();
        String regex1 = "^[0-9]{3}$";// any three digits
        Pattern pattern1 = Pattern.compile(regex1);

        try
        {
            String param1 = s.getParser().getRawParameter("field1", "111");
            String param2 = HtmlEncoder.encode(s.getParser().getRawParameter("field2", "4128 3214 0002 1999"));
            float quantity = 1.0f;
            float total = 0.0f;
            float runningTotal = 0.0f;

            DecimalFormat money = new DecimalFormat("$0.00");

            // test input field1
            if (!pattern1.matcher(param1).matches())
            {
                if (param1.toLowerCase().indexOf("script") != -1)
                {
                	makeSuccess(s);
                    String flag = getFlag();
                	s.setMessage("Flag{" + flag + "}");
                }

                s.setMessage(getLabelManager().get("ReflectedXSSWhoops1")+ param1 + getLabelManager().get("ReflectedXSSWhoops2"));
            }

            // FIXME: encode output of field2, then s.setMessage( field2 );

            ec.addElement(new HR().setWidth("90%"));
            ec.addElement(new Center().addElement(new H1().addElement(getLabelManager().get("ShoppingCart"))));
            Table t = new Table().setCellSpacing(0).setCellPadding(2).setBorder(1).setWidth("90%").setAlign("center");

            if (s.isColor())
            {
                t.setBorder(1);
            }

            TR tr = new TR();
            tr.addElement(new TH().addElement(getLabelManager().get("ShoppingCartItems")).setWidth("80%"));
            tr.addElement(new TH().addElement(getLabelManager().get("Price")).setWidth("10%"));
            tr.addElement(new TH().addElement(getLabelManager().get("Quantity")).setWidth("3%"));
            tr.addElement(new TH().addElement(getLabelManager().get("Total")).setWidth("7%"));
            t.addElement(tr);

            tr = new TR();
            tr.addElement(new TD().addElement("Studio RTA - Laptop/Reading Cart with Tilting Surface - Cherry "));
            tr.addElement(new TD().addElement("69.99").setAlign("right"));
            tr.addElement(new TD().addElement(
                                                new Input(Input.TEXT, "QTY1", s.getParser().getStringParameter("QTY1",
                                                                                                                "1"))
                                                        .setSize(6)).setAlign("right"));
            quantity = s.getParser().getFloatParameter("QTY1", 0.0f);
            total = quantity * 69.99f;
            runningTotal += total;
            tr.addElement(new TD().addElement(money.format(total)));
            t.addElement(tr);
            tr = new TR();
            tr.addElement(new TD().addElement("Dynex - Traditional Notebook Case"));
            tr.addElement(new TD().addElement("27.99").setAlign("right"));
            tr.addElement(new TD().addElement(
                                                new Input(Input.TEXT, "QTY2", s.getParser().getStringParameter("QTY2",
                                                                                                                "1"))
                                                        .setSize(6)).setAlign("right"));
            quantity = s.getParser().getFloatParameter("QTY2", 0.0f);
            total = quantity * 27.99f;
            runningTotal += total;
            tr.addElement(new TD().addElement(money.format(total)));
            t.addElement(tr);
            tr = new TR();
            tr.addElement(new TD().addElement("Hewlett-Packard - Pavilion Notebook with Intel Centrino"));
            tr.addElement(new TD().addElement("1599.99").setAlign("right"));
            tr.addElement(new TD().addElement(
                                                new Input(Input.TEXT, "QTY3", s.getParser().getStringParameter("QTY3",
                                                                                                                "1"))
                                                        .setSize(6)).setAlign("right"));
            quantity = s.getParser().getFloatParameter("QTY3", 0.0f);
            total = quantity * 1599.99f;
            runningTotal += total;
            tr.addElement(new TD().addElement(money.format(total)));
            t.addElement(tr);
            tr = new TR();
            tr.addElement(new TD().addElement("3 - Year Performance Service Plan $1000 and Over "));
            tr.addElement(new TD().addElement("299.99").setAlign("right"));

            tr.addElement(new TD().addElement(
                                                new Input(Input.TEXT, "QTY4", s.getParser().getStringParameter("QTY4",
                                                                                                                "1"))
                                                        .setSize(6)).setAlign("right"));
            quantity = s.getParser().getFloatParameter("QTY4", 0.0f);
            total = quantity * 299.99f;
            runningTotal += total;
            tr.addElement(new TD().addElement(money.format(total)));
            t.addElement(tr);

            ec.addElement(t);

            t = new Table().setCellSpacing(0).setCellPadding(2).setBorder(0).setWidth("90%").setAlign("center");

            if (s.isColor())
            {
                t.setBorder(1);
            }

            ec.addElement(new BR());

            tr = new TR();
            tr.addElement(new TD().addElement(getLabelManager().get("TotalChargedCreditCard")+":"));
            tr.addElement(new TD().addElement(money.format(runningTotal)));
            tr.addElement(new TD().addElement(ECSFactory.makeButton(getLabelManager().get("UpdateCart"))));
            t.addElement(tr);
            tr = new TR();
            tr.addElement(new TD().addElement("&nbsp;").setColSpan(2));
            t.addElement(tr);
            tr = new TR();
            tr.addElement(new TD().addElement(getLabelManager().get("EnterCreditCard")+":"));
            tr.addElement(new TD().addElement(new Input(Input.TEXT, "field2", param2)));
            t.addElement(tr);
            tr = new TR();
            tr.addElement(new TD().addElement(getLabelManager().get("Enter3DigitCode")+":"));
            tr.addElement(new TD().addElement("<input name='field1' type='TEXT' value='" + param1 + "'>"));
            // tr.addElement(new TD().addElement(new Input(Input.TEXT, "field1",param1)));
            t.addElement(tr);

            Element b = ECSFactory.makeButton(getLabelManager().get("Purchase"));
            tr = new TR();
            tr.addElement(new TD().addElement(b).setColSpan(2).setAlign("center"));
            t.addElement(tr);

            ec.addElement(t);
            ec.addElement(new BR());
            ec.addElement(new HR().setWidth("90%"));
        } catch (Exception e)
        {
            s.setMessage(getLabelManager().get("ErrorGenerating") + this.getClass().getName());
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
		hints.add("Terdapat kolom yang dapat diinject dengan script");
		
		return hints;
	}

//------------------------------------------------------------------
    public Integer getRanking() 
    {
        return new Integer(3);
    }
    
//------------------------------------------------------------------
	public String getTitle()
	{
		return("A3 - Cross Site Scripting");
	}
//------------------------------------------------------------------	
    public String getInstructions(WebSession s)
    {
    String instructions = "Salah satu kolom input di bawah memiliki celah reflected XSS attack."
        + "<br><b>Objective</b>: Buat sebuah script sederhana untuk menampilkan pop up window";

    return (instructions);
    }
}
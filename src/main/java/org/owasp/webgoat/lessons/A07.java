package org.owasp.webgoat.lessons;

import java.util.ArrayList;
import java.util.List;
import org.apache.ecs.Element;
import org.apache.ecs.ElementContainer;
import org.apache.ecs.html.A;
import org.apache.ecs.html.IMG;
import org.apache.ecs.html.TD;
import org.apache.ecs.html.TR;
import org.apache.ecs.html.Table;
import org.owasp.webgoat.session.ECSFactory;
import org.owasp.webgoat.session.WebSession;
import org.apache.ecs.*;
import org.apache.ecs.html.*;
import org.owasp.webgoat.session.WebSession;

public class A07 extends LessonAdapter
{	
    private final static String RESOURCE = "Resource";

    private final static String USER = "User";

    private final static String[] resources = { "Public Share", "Time Card Entry", "Performance Review",
            "Time Card Approval", "Site Manager", "Account Manager" };

    private final static String[] roles = { "Public", "User", "Manager", "Admin" };

    private final static String[] users = { "Moe", "Larry", "Curly", "Shemp" };
    
    private static int acak = generateNumber();
    
	private static int generateNumber() {
		int x;
		RandomGenerator rn = new RandomGenerator();
		x = rn.generateNumber(3);
		
		return x;
	}
    
	private String getFlag() {
		String raw = "AccessControlFlaws";
		RandomGenerator rn = new RandomGenerator();
		raw = rn.generateFlag(raw);
		
		FlagList fl = new FlagList("A07", raw, 0, 0);
		fl.changeKey("A07", raw);
				
		return raw;
	}
    
    protected Element createContent(WebSession s)
    {
        ElementContainer ec = new ElementContainer();

        try
        {
        	System.out.println("A7 acak: " + acak);
            String user = s.getParser().getRawParameter(USER, users[0]);
            String resource = s.getParser().getRawParameter(RESOURCE, resources[0]);
            String credentials = getRoles(user).toString();

            Table t = new Table().setCellSpacing(0).setCellPadding(2).setBorder(0).setWidth("90%").setAlign("center");

            if (s.isColor())
            {
                t.setBorder(1);
            }

            TR tr = new TR();
            tr.addElement(new TD().addElement("Change user:"));
            tr.addElement(new TD().addElement(ECSFactory.makePulldown(USER, users, user, 1)));
            t.addElement(tr);

            // These two lines would allow the user to select the resource from a list
            // Didn't seem right to me so I made them type it in.
            // ec.addElement( new P().addElement( "Choose a resource:" ) );
            // ec.addElement( ECSFactory.makePulldown( RESOURCE, resources, resource, 1 ) );
            tr = new TR();
            tr.addElement(new TD().addElement("Select resource: "));
            tr.addElement(new TD().addElement(ECSFactory.makePulldown(RESOURCE, resources, resource, 1)));
            t.addElement(tr);

            tr = new TR();
            tr.addElement(new TD("&nbsp;").setColSpan(2).setAlign("center"));
            t.addElement(tr);

            tr = new TR();
            tr.addElement(new TD(ECSFactory.makeButton("Check Access")).setColSpan(2).setAlign("center"));
            t.addElement(tr);
            ec.addElement(t);

            if (isAllowed(user, resource))
            {
                if (!getRoles(user).contains("Admin") && resource.equals("Account Manager"))
                {
                    makeSuccess(s);
                    String flag = getFlag();
                	s.setMessage("Flag{" + flag + "}");
                }
                s.setMessage("User " + user + " " + credentials + " was allowed to access resource " + resource);
            }
            else
            {
                s.setMessage("User " + user + " " + credentials + " did not have privilege to access resource "
                        + resource);
            }
        } catch (Exception e)
        {
            s.setMessage("Error generating " + this.getClass().getName());
            e.printStackTrace();
        }

        return (ec);
    }
    
    private List getResources(List rl)
    {
        // return the resources allowed for these roles
        ArrayList<String> list = new ArrayList<String>();

        if (rl.contains(roles[0]))
        {
            list.add(resources[0]);
        }

        if (rl.contains(roles[1]))
        {
            list.add(resources[1]);
            list.add(resources[5]);
        }

        if (rl.contains(roles[2]))
        {
            list.add(resources[2]);
            list.add(resources[3]);
        }

        if (rl.contains(roles[3]))
        {
            list.add(resources[4]);
            list.add(resources[5]);
        }

        return list;
    }

    private List getRoles(String user)
    {
        ArrayList<String> list = new ArrayList<String>();

        if (user.equals(users[0]))//Moe
        {
            list.add(roles[0]);
        }
        else if (user.equals(users[1]))//Larry
        {
            //list.add(roles[1]);
            list.add(roles[2]);
        }
        else if (user.equals(users[2]))//Curly
        {
            list.add(roles[0]);
            list.add(roles[2]);
        }
        else if (user.equals(users[3]))//Shemp
        {
            list.add(roles[3]);
        }
        
        if (user.equals(users[acak]))
        {
        	list.add(roles[1]);
        }

        return list;
    }
	
    private boolean isAllowed(String user, String resource)
    {
        List roles = getRoles(user);
        List resources = getResources(roles);
        return (resources.contains(resource));
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
		hints.add("User selain Admin adalah Moe, Larry dan Curly");
		hints.add("Gunakan kombinasi user dan resource untuk menemukan celah keamanan");
		
		return hints;
		
	}

//------------------------------------------------------------------
    public Integer getRanking() 
    {
        return new Integer(7);
    }
    
//------------------------------------------------------------------
	public String getTitle()
	{
		return("A7 - Missing Function Level Access Control");
	}
	
//------------------------------------------------------------------	
    public String getInstructions(WebSession s)
    {
    String instructions = "Skema role-based access control terdiri dari dua bagian: role permission management "
    	+ "dan role assignment. Terdapat kesalahan pemberian hak akses pada kofigurasi di bawah. "
    	+ "<br>Seharusnya, hanya Shemp [Admin] yang dapat mengakses resource <b>Site Manager dan Account Manager</b>. "
    	+ "<br>Tetapi terdapat satu akun lainnya yang bukan merupakan Admin dapat mengakses salah satu resource tersebut. "
        + "<br><b>Objective</b>: Carilah akun lain yang bukan merupakan Admin tetapi dapat mengakses resource Admin.";

    return (instructions);
    }
}
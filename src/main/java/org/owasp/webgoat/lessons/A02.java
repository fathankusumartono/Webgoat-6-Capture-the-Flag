package org.owasp.webgoat.lessons;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.Cookie;
import org.apache.ecs.Element;
import org.apache.ecs.ElementContainer;
import org.apache.ecs.StringElement;
import org.apache.ecs.html.A;
import org.apache.ecs.html.B;
import org.apache.ecs.html.H1;
import org.apache.ecs.html.IMG;
import org.apache.ecs.html.Input;
import org.apache.ecs.html.P;
import org.apache.ecs.html.TD;
import org.apache.ecs.html.TH;
import org.apache.ecs.html.TR;
import org.apache.ecs.html.Table;
import org.owasp.webgoat.session.*;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import org.apache.ecs.*;
import org.apache.ecs.html.*;
import org.owasp.webgoat.session.ECSFactory;
import org.owasp.webgoat.session.WebSession;

public class A02 extends LessonAdapter
{
	protected final static String AUTHCOOKIE = "AuthCookie";

    protected final static String LOGOUT = "WACLogout";

    protected final static String PASSWORD = "Password";

    protected final static String USERNAME = "Username";

    private static int acak = generateNumber();
    
    private String TARGET_USER = userTarget();
        
    static String key;
    
    private static String userTarget() {
		String target = null;
    	if(acak == 0) {
			target = "alice";
			key = "65432fdjmb";
		} else if(acak == 1) {
			target = "alana";
			key = "65432bobmb";
		} else if(acak == 2) {
			target = "ellie";
			key = "65432fjmmf";
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
		String raw = "WeakAuthenticationCookie";
		RandomGenerator rn = new RandomGenerator();
		raw = rn.generateFlag(raw);
		
		FlagList fl = new FlagList("A02", raw, 0, 0);
		fl.changeKey("A02", raw);
				
		return raw;
	}
	
    protected String checkCookie(WebSession s) throws Exception
    {
        String cookie = getCookie(s);

        if (cookie != null)
        {
            if (cookie.equals(encode("webgoat12345"))) { return ("webgoat"); }

            if (cookie.equals(encode("aspect12345"))) { return ("aspect"); }

            if (cookie.equals(encode(TARGET_USER + "12345")))
            {
                makeSuccess(s);
                String flag = getFlag();
            	s.setMessage("Flag{" + flag + "}");
                return (TARGET_USER);
            }           
            else
            {
                s.setMessage(getLabelManager().get("InvalidCookie"));
                s.eatCookies();
            }
        }

        return (null);
    }

    protected String checkParams(WebSession s) throws Exception
    {
        String username = s.getParser().getStringParameter(USERNAME, "");
        String password = s.getParser().getStringParameter(PASSWORD, "");

        if ((username.length() > 0) && (password.length() > 0))
        {
            String loginID = "";

            if (username.equals("webgoat") && password.equals("webgoat"))
            {
                loginID = encode("webgoat12345");
            }
            else if (username.equals("aspect") && password.equals("aspect"))
            {
                loginID = encode("aspect12345");
            }

            if (loginID != "")
            {
                Cookie newCookie = new Cookie(AUTHCOOKIE, loginID);
                s.setMessage(getLabelManager().get("IdentityRemembered"));
                s.getResponse().addCookie(newCookie);

                return (username);
            }
            else
            {
                s.setMessage(getLabelManager().get("InvalidUsernameAndPassword"));
            }
        }

        return (null);
    }

    protected Element createContent(WebSession s)
    {
        boolean logout = s.getParser().getBooleanParameter(LOGOUT, false);

        if (logout)
        {
            s.setMessage(getLabelManager().get("PasswordForgotten"));
            s.eatCookies();

            return (makeLogin(s));
        }

        try
        {
            String user = checkCookie(s);

            if ((user != null) && (user.length() > 0)) { return (makeUser(s, user, "COOKIE")); }

            user = checkParams(s);

            if ((user != null) && (user.length() > 0)) { return (makeUser(s, user, "PARAMETERS")); }
        } catch (Exception e)
        {
            s.setMessage(getLabelManager().get("ErrorGenerating") + this.getClass().getName());
            e.printStackTrace();
        }

        return (makeLogin(s));
    }

    private String encode(String value)
    {
        // <START_OMIT_SOURCE>
        StringBuffer encoded = new StringBuffer();

        for (int i = 0; i < value.length(); i++)
        {
            encoded.append(String.valueOf((char) (value.charAt(i) + 1)));
        }

        return encoded.reverse().toString();
        // <END_OMIT_SOURCE>
    }

    protected String getCookie(WebSession s)
    {
        Cookie[] cookies = s.getRequest().getCookies();

        for (int i = 0; i < cookies.length; i++)
        {
            if (cookies[i].getName().equalsIgnoreCase(AUTHCOOKIE)) { return (cookies[i].getValue()); }
        }

        return (null);
    }
    
    protected Element makeLogin(WebSession s)
    {
        ElementContainer ec = new ElementContainer();

        ec.addElement(new H1().addElement(getLabelManager().get("SignIn")));
        Table t = new Table().setCellSpacing(0).setCellPadding(2).setBorder(0).setWidth("90%").setAlign("center");

        if (s.isColor())
        {
            t.setBorder(1);
        }

        TR tr = new TR();
        tr.addElement(new TH()
                .addElement(getLabelManager().get("WeakAuthenticationCookiePleaseSignIn"))
                .setColSpan(2).setAlign("left"));
        t.addElement(tr);

        tr = new TR();
        tr.addElement(new TD().addElement("*"+getLabelManager().get("RequiredFields")).setWidth("30%"));
        t.addElement(tr);

        tr = new TR();
        tr.addElement(new TD().addElement("&nbsp;").setColSpan(2));
        t.addElement(tr);

        TR row1 = new TR();
        TR row2 = new TR();
        row1.addElement(new TD(new B(new StringElement("*"+getLabelManager().get("UserName")))));
        row2.addElement(new TD(new B(new StringElement("*"+getLabelManager().get("Password")))));

        Input input1 = new Input(Input.TEXT, USERNAME, "");
        Input input2 = new Input(Input.PASSWORD, PASSWORD, "");
        row1.addElement(new TD(input1));
        row2.addElement(new TD(input2));
        t.addElement(row1);
        t.addElement(row2);

        Element b = ECSFactory.makeButton(getLabelManager().get("Login"));
        t.addElement(new TR(new TD(b)));
        ec.addElement(t);

        return (ec);
    }

    protected Element makeUser(WebSession s, String user, String method) throws Exception
    {
        ElementContainer ec = new ElementContainer();
        ec.addElement(new P().addElement(getLabelManager().get("WelcomeUser") + user));
        ec.addElement(new P().addElement(getLabelManager().get("YouHaveBeenAuthenticatedWith") + method));
        ec.addElement(new P().addElement(ECSFactory.makeLink(getLabelManager().get("Logout"), LOGOUT, true)));
        ec.addElement(new P().addElement(ECSFactory.makeLink(getLabelManager().get("Refresh"), "", "")));

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
		hints.add("Lewati autentikasi password dengan sisipkan AuthCookie");
		hints.add("Gunakan aplikasi OWASP ZAP untuk menangkap request dan manambahkan Cookie");
		return hints;
	}

//------------------------------------------------------------------
    public Integer getRanking() 
    {
        return new Integer(2);
    }
    
//------------------------------------------------------------------
	public String getTitle()
	{
		return("A2 - Broken Authentication and Session Management");
	}
//------------------------------------------------------------------	
    public String getInstructions(WebSession s)
    {
    String instructions = "Autentikasi halaman login di bawah ini bisa dilewati dengan menyisipkan Cookie. "
        + "<br><b>Objective</b>: Lewati autentikasi dengan user <b>" + TARGET_USER + "</b> dan AuthCookie=" + key;

    return (instructions);
    }
}
package org.owasp.webgoat.lessons;

import java.util.*;
import org.apache.ecs.*;
import org.apache.ecs.html.*;
import org.owasp.webgoat.session.ECSFactory;
import org.owasp.webgoat.session.LessonTracker;
import org.owasp.webgoat.session.Screen;
import org.owasp.webgoat.session.UserTracker;
import org.owasp.webgoat.session.WebSession;

// Add copyright text - use text from another lesson 

public class Scoring extends LessonAdapter
{	
	protected final static String USERNAME = "Username";
	
	private final static String FLAG = "flag";
		
	private String lessonid = "?";
	
	private String flagid;
	
	private int poin = 0;
	
	private int valid = 0; 
		
	protected Element createContent(WebSession s)
	{
		ElementContainer ec = new ElementContainer();
		try
		{
			String user = cariUser(s);
			ec.addElement(tampilUser(s, user));
			ec.addElement(new HR());
			
			ec.addElement(tampilInput(s));
			ec.addElement(new HR());
			
			FlagList fl = new FlagList(lessonid, flagid, poin, valid);
			fl.getFlagList(flagid);
			System.out.println("Scoring flag: " + flagid);
			
			int validIndex = fl.getValidIndex();
			valid = cekValidIndex(s, user, validIndex);
			System.out.println("Scoring valid: " + valid);
			
			poin = fl.getScore();
			System.out.println("Scoring score: " + poin);
			
			lessonid = fl.getLesson();
			System.out.println("Scoring lesson: " + lessonid);
			
			if (valid == 1) {
				tulisFinalScore(s, user, poin);						
				s.setMessage("Congratulations! You got the Flag for lesson " + lessonid + "!");
				s.setMessage("+" + poin);
			} 
			else if (valid == -1) {
				s.setMessage("You already submitted the Flag for lesson " + lessonid + "!");
			} 
			else if (valid == -2) {
				s.setMessage("Reset Valid");
			} 
			
			ec.addElement(tampilFinalScore(s, user));
			
			//reset score, poin, valid, validindex
			poin = fl.resetScore();
			valid = fl.resetValidIndex();
		}
		catch (Exception e)
		{
			s.setMessage("Error generating " + this.getClass().getName());
			e.printStackTrace();
		}
		return (ec);
	}

	protected int bacaFinalScore(WebSession s, String user)
	{
        Screen screen = null;
        for (Iterator lessonIter = s.getCourse().getLessons(s, AbstractLesson.USER_ROLE).iterator(); lessonIter
                .hasNext();)
        {
            screen = (Screen) lessonIter.next();          
        }
		LessonTracker lessonTracker = UserTracker.instance().getLessonTracker(s, user, screen);

		return lessonTracker.getFinalScore();
	}
	
	protected void tulisFinalScore(WebSession s, String user, int score)
	{
        Screen screen = null;
        for (Iterator lessonIter = s.getCourse().getLessons(s, AbstractLesson.USER_ROLE).iterator(); lessonIter
                .hasNext();)
        {
            screen = (Screen) lessonIter.next();          
        }
		LessonTracker lessonTracker = UserTracker.instance().getLessonTracker(s, user, screen);
		
		if (score == 12345)
		{
			lessonTracker.resetFinalScore();
		} else
		{
			lessonTracker.tambahFinalScore(score);
		}
	}
	
	protected int cekValidIndex(WebSession s, String user, int validIndex)
	{
        Screen screen = null;
        for (Iterator lessonIter = s.getCourse().getLessons(s, AbstractLesson.USER_ROLE).iterator(); lessonIter
                .hasNext();)
        {
            screen = (Screen) lessonIter.next();          
        }
		LessonTracker lessonTracker = UserTracker.instance().getLessonTracker(s, user, screen);

		int cek = lessonTracker.getValid(validIndex);
		
		return cek;
	}	
	
	protected Element tampilFinalScore(WebSession s, String user)
	{
		ElementContainer ec = new ElementContainer();
		
        int SCORE = bacaFinalScore(s, user);
        ec.addElement(new H3("Your Score: " + SCORE));
        /*
        for (int i=1; i<11; i++) {
        	ec.addElement(new StringElement("A" + i));
        	ec.addElement(new BR());
        }
        */
		return ec;
	}
	
	protected Element tampilInput(WebSession s)
	{
        ElementContainer ec = new ElementContainer();
        
        ec.addElement(new StringElement("Enter flag here: "));
        flagid = s.getParser().getRawParameter(FLAG, "flag");

        Input input = new Input(Input.TEXT, FLAG, flagid.toString());
        ec.addElement(input);
        
        Element b = ECSFactory.makeButton(getLabelManager().get("Go!"));
        ec.addElement(b);
        
        return ec;
	}
	
    protected String cariUser(WebSession s)
    {
		String user = null;
        try
        {
            if (s.getRequest().isUserInRole(WebSession.WEBGOAT_ADMIN))
            {
                user = s.getParser().getRawParameter(USERNAME);
            }
            else
            {
                user = s.getUserName();
            }
        } catch (Exception e)
        {
        }
        if (user == null)
        {
            user = s.getUserName();
        }
                
        return user;
    }
    
    protected Element tampilUser(WebSession s, String user)
    {
    	H2 h2 = new H2();
        String type = "";
        h2.addElement(new StringElement("Results for: " + user + type));
                
        return h2;
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
		hints.add("Kerjakan salah satu lesson yang disediakan");
		hints.add("Jika berhasil, anda akan mendapat flag{...}");
		hints.add("Masukkan kata acak dalam bracket {...} untuk mendapat poin");
		hints.add("Setiap lesson hanya memberikan flag valid satu kali");
		hints.add("Masukkan semua flag untuk mendapat poin penuh");
		
		return hints;	
	}

//------------------------------------------------------------------
    public Integer getRanking() 
    {
        return new Integer(11);
    }
    
//------------------------------------------------------------------
	public String getTitle()
	{
		return("Scoring System");
	}
}
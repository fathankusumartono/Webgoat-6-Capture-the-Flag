package org.owasp.webgoat.lessons;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.owasp.webgoat.session.LessonTracker;
import org.owasp.webgoat.session.Screen;
import org.owasp.webgoat.session.UserTracker;
import org.owasp.webgoat.session.WebSession;

public class FlagList implements Comparable {
	
	public static FlagList RESET_FINALSCORE = new FlagList("Reset", "reset", new Integer(12345), new Integer(999));
	
	public static FlagList A1 = new FlagList("A01", "0ca2fac7ff6426c46c2c76216e5745a6", new Integer(120), new Integer(101));
	
	public static FlagList A2 = new FlagList("A02", "e622b1d63551ae58609d436a3301f8d4", new Integer(100), new Integer(102));
	
	public static FlagList A3 = new FlagList("A03", "f98b6586b2ed64f40350720c40ae527b", new Integer(100), new Integer(103));
	
	public static FlagList A4 = new FlagList("A04", "e279339ff73976f0a24214cdfbbef41c", new Integer(120), new Integer(104));
	
	public static FlagList A5 = new FlagList("A05", "00f6798f21a6045f48a0eea3be5c9694", new Integer(70), new Integer(105));
	
	public static FlagList A6 = new FlagList("A06", "d15e428e2a1e4264f75a97cc34cfe328", new Integer(50), new Integer(106));

	public static FlagList A7 = new FlagList("A07", "b9d7a13dc0de9d59a1ea39ee5f972393", new Integer(50), new Integer(107));
	
	public static FlagList A8 = new FlagList("A08", "941ae2cab6e95d0cc26c021dd02aa4b8", new Integer(150), new Integer(108));
	
	public static FlagList A9 = new FlagList("A09", "f5a7c44963d3c1af5cfda2aad3c6f565", new Integer(150), new Integer(109));
	
	public static FlagList A10 = new FlagList("A10", "5a4f6378ff0894578d662ee1a8357ae1", new Integer(110), new Integer(110));
	
    private static List<FlagList> FlagLists = new ArrayList<FlagList>();
    
    private String lesson;
    
    private String flag;
    
    private Integer score = 0;
    
    private Integer validIndex = 0;
    
    static {
    	FlagLists.add(RESET_FINALSCORE);
    	FlagLists.add(A1);
    	FlagLists.add(A2);
    	FlagLists.add(A3);
    	FlagLists.add(A4);
    	FlagLists.add(A5);
    	FlagLists.add(A6);
    	FlagLists.add(A7);
    	FlagLists.add(A8);
    	FlagLists.add(A9);
    	FlagLists.add(A10);
    }
    
    public FlagList getFlagList(String name) {
        Iterator<FlagList> it = FlagLists.iterator();
        while (it.hasNext()) {
        	FlagList c = it.next();
            if (c.getKey().equals(name)) {
            	validIndex = c.getValidIndex();
            	score = c.getScore();
            	lesson = c.getLesson();
                return c;
            }
        }
        return null;
    }
    
    //Ubah flag dari RandomGenerator
    public FlagList changeKey(String lessonID, String newKey) {
        Iterator<FlagList> it = FlagLists.iterator();
        while (it.hasNext()) {
        	FlagList c = it.next();
            if (c.getLesson().equals(lessonID)) {
            	c.flag = newKey;
                return c;
            }
        }
        return null;
    }
    
    public FlagList(String lesson, String flag, Integer score, Integer validIndex) {
    	this.lesson = lesson;
        this.flag = flag;
        this.score =  score;
        this.validIndex = validIndex;
    }
    
    public String getLesson() {
    	return lesson;
    }
    
    public String getKey() {
        return flag;
    }
            
    public Integer getScore() {
        return score;
    }
    
    public Integer resetScore() {
    	score = 0;
        return score;
    }
    
    public Integer getValidIndex() {
        return validIndex;
    }
    
    public Integer resetValidIndex() {
    	validIndex = 0;
        return validIndex;
    }
    
	@Override
	public int compareTo(Object obj) {
        int value = 1;

        if (obj instanceof FlagList) {
            value = this.getScore().compareTo(((FlagList) obj).getScore());
        }

        return value;
	}
}
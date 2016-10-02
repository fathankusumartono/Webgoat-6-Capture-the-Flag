package org.owasp.webgoat.lessons;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import javax.servlet.http.Cookie;

import org.apache.ecs.*;
import org.apache.ecs.html.*;
import org.owasp.webgoat.lessons.SQLInjection.SQLInjection;
import org.owasp.webgoat.session.DatabaseUtilities;
import org.owasp.webgoat.session.ECSFactory;
import org.owasp.webgoat.session.ParameterNotFoundException;
import org.owasp.webgoat.session.WebSession;


public class A01 extends SequentialLessonAdapter
{	
	private final static String USERID = "userid";

    private String TARGET_USERID = UserTarget();
    
    private int TARGET_VALUE = ValueTarget();

    private String userid;
    
    private static int ValueTarget() {
		int y;
		RandomGenerator rn = new RandomGenerator();
		y = rn.generateNumber(99999);
		y += 20000;
		
		return y;
    }
    
    private static int acak = generateNumber();
        
    private static String UserTarget() {
		String target = null;
    	if(acak == 0) {
			target = "jsmith";
		} else if(acak == 1) {
			target = "wgoat";
		} else if(acak == 2) {
			target = "rjones";
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
		String raw = "SqlModifyData";
		RandomGenerator rn = new RandomGenerator();
		raw = rn.generateFlag(raw);
		
		FlagList fl = new FlagList("A01", raw, 0, 0);
		fl.changeKey("A01", raw);
				
		return raw;
	}
	
    protected Element createContent(WebSession s)
    {
        ElementContainer ec = new ElementContainer();

        try
        {
            Connection connection = DatabaseUtilities.getConnection(s);

            ec.addElement(new HR());
            ec.addElement(makeAccountLine(s));

            String query = "SELECT * FROM salaries WHERE userid = '" + userid + "'";
            //ec.addElement(new PRE(query));

            try
            {
                // check target data
                Statement target_statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_READ_ONLY);
                ResultSet target_results = target_statement.executeQuery("SELECT salary from salaries where userid='"+TARGET_USERID+"'");
                target_results.first();
                String before_salary_target_salary = target_results.getString(1);
                
                System.out.println("Before running query, salary for target userid " + TARGET_USERID + " = " + before_salary_target_salary );
                     
                // execute query
                Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                                                                    ResultSet.CONCUR_READ_ONLY);
                //
                statement.execute(query);
                
                ResultSet results = statement.getResultSet();

                if ((results != null) && (results.first() == true))
                {
                    ResultSetMetaData resultsMetaData = results.getMetaData();
                    ec.addElement(DatabaseUtilities.writeTable(results, resultsMetaData));
                    results.last();
                }
                else
                {
                    ec.addElement(getLabelManager().get("NoResultsMatched"));
                }
                
                // see if target data was modified
                target_results = target_statement.executeQuery("SELECT salary from salaries where userid='"+TARGET_USERID+"'");
                target_results.first();
                String after_salary_target_salary = target_results.getString(1);
                
                System.out.println("After running query, salary for target userid " + TARGET_USERID + " = " + before_salary_target_salary );

                //if(!after_salary_target_salary.equals(before_salary_target_salary)) {
                if(TARGET_VALUE == Integer.parseInt(after_salary_target_salary)) {
                	makeSuccess(s);
                    String flag = getFlag();
                    s.setMessage("Flag{" + flag + "}");
                }
        		
            } catch (SQLException sqle)
            {
                ec.addElement(new P().addElement(sqle.getMessage()));
                sqle.printStackTrace();
            }
        } catch (Exception e)
        {
            s.setMessage(getLabelManager().get("ErrorGenerating") + this.getClass().getName());
            e.printStackTrace();
        }

        return (ec);
    }

    protected Element makeAccountLine(WebSession s)
    {
        ElementContainer ec = new ElementContainer();
        ec.addElement(new P().addElement(getLabelManager().get("EnterUserid")));

        userid = s.getParser().getRawParameter(USERID, "jsmith");
        Input input = new Input(Input.TEXT, USERID, userid.toString());
        ec.addElement(input);

        Element b = ECSFactory.makeButton(getLabelManager().get("Go!"));
        ec.addElement(b);

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
		hints.add("Ubah salary pada USERID " + TARGET_USERID);
				
		return hints;
	}

//------------------------------------------------------------------
    public Integer getRanking() 
    {
        return new Integer(1);
    }

//------------------------------------------------------------------
	public String getTitle()
	{
		return("A1 - Injection");
	}
//------------------------------------------------------------------	
    public String getInstructions(WebSession s)
    {
    String instructions = "Form di bawah ini digunakan untuk menampilkan salary dengan memasukkan userid. "
        + "Terdapat celah keamanan berupa String SQL Injection di kolom input tersebut."
        + "Nama tabel yang digunakan adalah <b>salaries</b>. "
        + "<br><b>Objective</b>: Gunakan SQL injection untuk mengubah salary pada userid <b>" + TARGET_USERID + "</b> "
        		+ "menjadi " + TARGET_VALUE;

    return (instructions);
    }
}
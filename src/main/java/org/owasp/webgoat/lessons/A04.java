package org.owasp.webgoat.lessons;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.ecs.Element;
import org.apache.ecs.ElementContainer;
import org.apache.ecs.StringElement;
import org.apache.ecs.html.BR;
import org.apache.ecs.html.HR;
import org.apache.ecs.html.TD;
import org.apache.ecs.html.TR;
import org.apache.ecs.html.Table;
import org.owasp.webgoat.session.ECSFactory;
import org.owasp.webgoat.session.WebSession;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import org.apache.ecs.*;
import org.apache.ecs.html.*;
import org.owasp.webgoat.session.ECSFactory;
import org.owasp.webgoat.session.WebSession;

public class A04 extends LessonAdapter
{	
	private final static String FILE = "File";
    
    private static int acak = generateNumber();
    
    private String TARGET_FILE = "../../" + fileTarget();

    private static String target = null;
    /**
     * Description of the Method
     * 
     * @param s
     *            Description of the Parameter
     * @return Description of the Return Value
     */
    private static String fileTarget() {		
    	if(acak == 0) {
			target = "main.jsp";
		} else if(acak == 1) {
			target = "index.jsp";
		} else if(acak == 2) {
			target = "webgoat.jsp";
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
		String raw = "PathBasedAccessControl";
		RandomGenerator rn = new RandomGenerator();
		raw = rn.generateFlag(raw);
		
		FlagList fl = new FlagList("A04", raw, 0, 0);
		fl.changeKey("A04", raw);
				
		return raw;
	}
    
    protected Element createContent(WebSession s)
    {
        ElementContainer ec = new ElementContainer();

        try
        {
            String dir = s.getContext().getRealPath("/lesson_plans/en");
            File d = new File(dir);

            ec.addElement(new HR());
            Table t = new Table().setCellSpacing(0).setCellPadding(2).setWidth("90%").setAlign("center");

            if (s.isColor())
            {
                t.setBorder(1);
            }

            String[] list = d.list();
            String listing = " <p><B>"+getLabelManager().get("CurrentDirectory")+"</B> " + Encoding.urlDecode(dir)
                    + "<br><br>"+getLabelManager().get("ChooseFileToView")+"</p>";

            TR tr = new TR();
            tr.addElement(new TD().setColSpan(2).addElement(new StringElement(listing)));
            t.addElement(tr);

            tr = new TR();
            tr.addElement(new TD().setWidth("35%").addElement(ECSFactory.makePulldown(FILE, list, "", 15)));
            tr.addElement(new TD().addElement(ECSFactory.makeButton(getLabelManager().get("ViewFile"))));
            t.addElement(tr);

            ec.addElement(t);

            // FIXME: would be cool to allow encodings here -- hex, percent,
            // url, etc...
            String file = s.getParser().getRawParameter(FILE, "");

            // defuse file searching
            boolean illegalCommand = getWebgoatContext().isDefuseOSCommands();
            if (getWebgoatContext().isDefuseOSCommands())
            {
                // allow them to look at any file in the webgoat hierachy. Don't
                // allow them
                // to look about the webgoat root, except to see the LICENSE
                // file
                if (upDirCount(file) == 3 && !file.endsWith("LICENSE"))
                {
                    s.setMessage(getLabelManager().get("AccessDenied"));
                    s.setMessage(getLabelManager().get("ItAppears1"));
                }
                else if (upDirCount(file) > 3)
                {
                    s.setMessage(getLabelManager().get("AccessDenied"));
                    s.setMessage(getLabelManager().get("ItAppears2"));
                }
                else
                {
                    illegalCommand = false;
                }
            }

            // Using the URI supports encoding of the data.
            // We could force the user to use encoded '/'s == %2f to make the lesson more difficult.
            // We url Encode our dir name to avoid problems with special characters in our own path.
            // File f = new File( new URI("file:///" +
            // Encoding.urlEncode(dir).replaceAll("\\\\","/") + "/" +
            // file.replaceAll("\\\\","/")) );
            File f = new File((dir + "\\" + file).replaceAll("\\\\", "/"));

            if (s.isDebug())
            {

                s.setMessage(getLabelManager().get("File") + file);
                s.setMessage(getLabelManager().get("Dir")+ dir);
                // s.setMessage("File URI: " + "file:///" +
                // (Encoding.urlEncode(dir) + "\\" +
                // Encoding.urlEncode(file)).replaceAll("\\\\","/"));
                s.setMessage(getLabelManager().get("IsFile")+ f.isFile());
                s.setMessage(getLabelManager().get("Exists") + f.exists());
            }
            if (!illegalCommand)
            {
                if (f.isFile() && f.exists())
                {
                    // Don't set completion if they are listing files in the
                    // directory listing we gave them.
                	System.out.println("target: " + TARGET_FILE);
                    if (upDirCount(file) >= 1 && TARGET_FILE.equals(file))
                	{
                        s.setMessage(getLabelManager().get("CongratsAccessToFileAllowed"));
                        s.setMessage(" ==> " + Encoding.urlDecode(f.getCanonicalPath()));
                        makeSuccess(s);
                        String flag = getFlag();
                    	s.setMessage("Flag{" + flag + "}");
                    }
                    else
                    {
                        s.setMessage(getLabelManager().get("FileInAllowedDirectory"));
                        s.setMessage(" ==> " + Encoding.urlDecode(f.getCanonicalPath()));
                    }
                }
                else if (file != null && file.length() != 0)
                {
                    s
                            .setMessage(getLabelManager().get("AccessToFileDenied1") + Encoding.urlDecode(f.getCanonicalPath())
                                    +  getLabelManager().get("AccessToFileDenied2"));
                }
                else
                {
                    // do nothing, probably entry screen
                }

                try
                {
                    // Show them the file
                    // Strip out some of the extra html from the "help" file
                    ec.addElement(new BR());
                    ec.addElement(new BR());
                    ec.addElement(new HR().setWidth("100%"));
                    ec.addElement(getLabelManager().get("ViewingFile")+ f.getCanonicalPath());
                    ec.addElement(new HR().setWidth("100%"));
                    if (f.length() > 80000) { throw new Exception(getLabelManager().get("FileTooLarge")); }
                    String fileData = getFileText(new BufferedReader(new FileReader(f)), false);
                    if (fileData.indexOf(0x00) != -1) { throw new Exception(getLabelManager().get("FileBinary")); }
                    ec.addElement(new StringElement(fileData.replaceAll(System.getProperty("line.separator"), "<br>")
                            .replaceAll("(?s)<!DOCTYPE.*/head>", "").replaceAll("<br><br>", "<br>")
                            .replaceAll("<br>\\s<br>", "<br>").replaceAll("<\\?", "&lt;").replaceAll("<(r|u|t)",
                                                                                                        "&lt;$1")));
                } catch (Exception e)
                {
                    ec.addElement(new BR());
                    ec.addElement(getLabelManager().get("TheFollowingError"));
                    ec.addElement(e.getMessage());
                }
            }
        } catch (Exception e)
        {
            s.setMessage(getLabelManager().get("ErrorGenerating")+ this.getClass().getName());
            e.printStackTrace();
        }

        return (ec);
    }

    private int upDirCount(String fileName)
    {
        int count = 0;
        int startIndex = fileName.indexOf("..");
        while (startIndex != -1)
        {
            count++;
            startIndex = fileName.indexOf("..", startIndex + 1);
        }
        return count;
    }
	
//------------------------------------------------------------------
	public Category getDefaultCategory()
	{
		return Category.SKRIPSI;
	}
	
    
//------------------------------------------------------------------
	public List<String> getHints(WebSession s)
	{
		System.out.println("rand = " + acak);
		List<String> hints = new ArrayList<String>();
		hints.add("Untuk mengakses parent directory gunakan (..) sebelum nama file");
		hints.add("Gunakan OWASP ZAP untuk mengubah letak file yang akan diakses");
		
		return hints;
	}

//------------------------------------------------------------------
    public Integer getRanking() 
    {
        return new Integer(4);
    }
    
//------------------------------------------------------------------
	public String getTitle()
	{
		return("A4 - Insecure Direct Object References");
	}

//------------------------------------------------------------------	
    public String getInstructions(WebSession s)
    {
    String instructions = "Halaman ini digunakan untuk melihat isi file dengan memilih nama file pada list. "
    	+ "Terdapat celah keamanan yaitu pengguna dapat mengakses file di luar list yang telah disediakan."
        + "<br><b>Objective</b>: Modifikasi request akses file di bawah untuk mengakses file <b>" + target
        + "</b> yang berada di dua parent directory saat ini.";

    return (instructions);
    }
}
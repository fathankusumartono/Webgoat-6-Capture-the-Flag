package org.owasp.webgoat.lessons;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.File;

import org.apache.ecs.Element;
import org.apache.ecs.ElementContainer;
import org.apache.ecs.html.Form;
import org.apache.ecs.html.H1;
import org.apache.ecs.html.Input;
import org.apache.ecs.html.P;
import org.apache.ecs.html.A;
import org.apache.ecs.html.IMG;
import org.owasp.webgoat.session.DatabaseUtilities;
import org.owasp.webgoat.session.ECSFactory;
import org.owasp.webgoat.session.WebSession;

import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.disk.*;
import org.apache.commons.fileupload.servlet.*;
import org.apache.ecs.*;
import org.apache.ecs.html.*;
import org.owasp.webgoat.session.WebSession;

public class A09 extends LessonAdapter
{	   
    private String uploads_and_target_parent_directory = null; 
    
    private final static String UPLOADS_RELATIVE_PATH = "uploads";

    private String TARGET_RELATIVE_PATH = DirectoryTarget(); 
    
    private static int acak = generateNumber();
    
    private static String DirectoryTarget() {
		String target = null;
    	if(acak == 0) {
			target = "mfe_target";
		} else if(acak == 1) {
			target = "mfe_destination";
		} else if(acak == 2) {
			target = "mfe_folder";
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
		String raw = "MaliciousFileExecution";
		RandomGenerator rn = new RandomGenerator();
		raw = rn.generateFlag(raw);
		
		FlagList fl = new FlagList("A09", raw, 0, 0);
		fl.changeKey("A09", raw);
				
		return raw;
	}

    private void fill_uploads_and_target_parent_directory(WebSession s) {
        uploads_and_target_parent_directory = s.getContext().getRealPath("/");

        if(!uploads_and_target_parent_directory.endsWith(File.separator)) {
            uploads_and_target_parent_directory = uploads_and_target_parent_directory + 
                File.separator;
        }
        System.out.println("uploads_and_target_parent_directory set to = " 
                + uploads_and_target_parent_directory);
        
        // make sure the directories exist
        File uploads_dir = new File(uploads_and_target_parent_directory
                + UPLOADS_RELATIVE_PATH);
        uploads_dir.mkdir();
        
        File target_dir = new File(uploads_and_target_parent_directory 
                + TARGET_RELATIVE_PATH);
        target_dir.mkdir();
        
        // delete the user's target file if it is already there since we must
            // have restarted webgoat
        File userfile = new File(uploads_and_target_parent_directory 
                + TARGET_RELATIVE_PATH + java.io.File.separator 
                + s.getUserName() + ".txt");
        
        userfile.delete();
    }

    protected Element createContent(WebSession s)
    {
        
    if(uploads_and_target_parent_directory == null) {
        fill_uploads_and_target_parent_directory(s);
    }
    
    
    ElementContainer ec = new ElementContainer();

    try
    {      
        // check for success - see if the target file exists yet       
        File userfile = new File(uploads_and_target_parent_directory 
                + TARGET_RELATIVE_PATH + java.io.File.separator 
                + s.getUserName() + ".txt");
        
        if(userfile.exists()) {
            makeSuccess(s);
            String flag = getFlag();
            s.setMessage("Flag{" + flag + "}");
        }
        
        Connection connection = DatabaseUtilities.getConnection(s);
        
        ec.addElement(new H1().addElement("WebGoat Image Storage"));
        
        // show the current image
        ec.addElement(new P().addElement("Your current image:"));
        
        String image_query = "SELECT image_relative_url FROM mfe_images WHERE user_name = '" 
            + s.getUserName() + "'";

        Statement image_statement = connection.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet image_results = image_statement.executeQuery(image_query);
        
        if(image_results.next() == false) {
            // result set was empty
            ec.addElement(new P().addElement("No image uploaded"));
            System.out.println("No image uploaded");
        } else {

            String image_url = image_results.getString(1);
            
            ec.addElement(new IMG(image_url).setBorder(0).setHspace(0).setVspace(0));
            
            System.out.println("Found image named: " + image_url);

        }
        
        ec.addElement(new P().addElement("Upload a new image:"));

        Input input = new Input(Input.FILE, "myfile", "");
        ec.addElement(input);
        
        Element b = ECSFactory.makeButton("Start Upload");
        ec.addElement(b);
        
    }
    catch (Exception e)
    {
        s.setMessage("Error generating " + this.getClass().getName());
        e.printStackTrace();
    }

    return (ec);
    }
	
    public void restartLesson(WebSession s)
    {
        if(uploads_and_target_parent_directory == null) {
            fill_uploads_and_target_parent_directory(s);
        }
        
        System.out.println("Restarting Malicious File Execution lesson for user " + s.getUserName());
        
        // delete the user's target file
        File userfile = new File(uploads_and_target_parent_directory 
                + TARGET_RELATIVE_PATH 
                + java.io.File.separator 
                + s.getUserName() + ".txt");
        
        userfile.delete();
        
        // remove the row from the mfe table
        // add url to database table
        
        try {
            Connection connection = DatabaseUtilities.getConnection(s);
            
            Statement statement = connection.createStatement();
            
            String deleteuserrow = "DELETE from mfe_images WHERE user_name = '"
                + s.getUserName() + "';";

            statement.executeUpdate(deleteuserrow);
            
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void handleRequest(WebSession s)
    {
        if(uploads_and_target_parent_directory == null) {
            fill_uploads_and_target_parent_directory(s);
        }          
    try
    {
        if(ServletFileUpload.isMultipartContent(s.getRequest())) {
 
            DiskFileItemFactory factory = new DiskFileItemFactory();
            factory.setSizeThreshold(500000); 
            ServletFileUpload upload = new ServletFileUpload(factory);
            
//           Parse the request
            List /* FileItem */ items = upload.parseRequest(s.getRequest());
            
//           Process the uploaded items
            java.util.Iterator iter = items.iterator();
            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();

                if (item.isFormField()) {
                    
                    // ignore regular form fields
                    
                } else {
                    
                    // not a form field, must be a file upload
                    if(item.getName().contains("/") || item.getName().contains("\\")) {
                        System.out.println("Uploaded file contains a / or \\ (i.e. attempted directory traversal).  Not storing file.");
                        // TODO - is there a way to show an error to the user here?
                        
                        s.setMessage("Directory traversal not allowed.  Nice try though.");
                        
                    } else {
                    
                        // write file to disk with original name in uploads directory
                        String uploaded_file_path = uploads_and_target_parent_directory 
                            + UPLOADS_RELATIVE_PATH 
                            + java.io.File.separator
                            + item.getName();
                        File uploadedFile = new File(uploaded_file_path);
                        item.write(uploadedFile);
                        System.out.println("Stored file:\n" + uploaded_file_path );
                        
                        // add url to database table
                        Connection connection = DatabaseUtilities.getConnection(s);
                        
                        Statement statement = connection.createStatement();
                        
                        // attempt an update
                        String updateData1 = "UPDATE mfe_images SET image_relative_url='" + UPLOADS_RELATIVE_PATH + "/"
                            + item.getName() + "' WHERE user_name = '"
                            + s.getUserName() + "';";
                        
                        System.out.println("Updating row:\n" + updateData1 );
                        if(statement.executeUpdate(updateData1) == 0) {
                        
                            // update failed, we need to add a row
                            String insertData1 = "INSERT INTO mfe_images VALUES ('" +
                                s.getUserName() + "','" + UPLOADS_RELATIVE_PATH + "/" + 
                                item.getName() + "')";
                            
                            System.out.println("Inserting row:\n" + insertData1 );
                            statement.executeUpdate(insertData1);
                        }
                    }
                    
                }
            }
            
        } 

            Form form = new Form(getFormAction(), Form.POST).setName("form")
                .setEncType("multipart/form-data");

            form.addElement(createContent(s));

            setContent(form);
    }
    catch (Exception e)
    {
        System.out.println("Exception caught: " + e);
        e.printStackTrace(System.out);
    }
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
		hints.add("Pada J2EE server, file yang dapat dieksekusi adalah tipe .jsp");
		hints.add("Pastikan file dibuat pada direktori yang tepat");
		
		return hints;
		
	}

//------------------------------------------------------------------
    public Integer getRanking() 
    {
        return new Integer(9);
    }
    
//------------------------------------------------------------------
	public String getTitle()
	{
		return("A9 - Using Components with Known Vulnerabilities");
	}
    public String getInstructions(WebSession s)
    {
        if(uploads_and_target_parent_directory == null) {
            fill_uploads_and_target_parent_directory(s);
        }
        
//------------------------------------------------------------------        
    String instructions = "Halaman di bawah ini digunakan untuk mengunggah gambar dan menampilkannya langsung. " 
        + "Fitur seperti ini sering sekali ditemui pada berbagai web. Tetapi masalahnya, terdapat celah keamanan yaitu " 
        + "file tertentu yang diunggah dapat diekseskusi oleh sistem untuk menjalankan suatu perintah."
        + "<br><b>Objective</b>: Unggah sebuah file yang dapat menjalankan perintah untuk membuat file pada direktori berikut:<br>"
        + uploads_and_target_parent_directory
        + TARGET_RELATIVE_PATH 
        + java.io.File.separator 
        + s.getUserName() + ".txt"
        + "<br><br>Jika file berhasil dibuat, restart lesson untuk menampilkan Flag.";

    return (instructions);
    }
}
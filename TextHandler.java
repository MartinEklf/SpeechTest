package ciGUI;

import java.io.*;
import java.util.Calendar;
import java.text.SimpleDateFormat;

/**
 * Writes data to text file.
 * 
 * @author mrel
 *
 */

public class TextHandler {// implements Runnable{
	private String fileName = "";//"c:\\temp\\"; 
//	private DataOutputStream out;
	private PrintWriter out1;
	private PrintWriter out2;
//	private boolean stopped = false;
	private File theFile = null;
	private File theFile2 = null;
	private String suffix = null;
	private int lineNr = 0;
	
	//Constructor
	public TextHandler() {

		// Create filename suffix
		Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT_NOW);
	    String theDate = sdf.format(cal.getTime());
	    fileName = fileName + theDate + ".txt";
	    
	    suffix = theDate + "; ";
	    
//	    if (arg.length > 0) theFile = new File(arg[0] + fileName);
		if (theFile == null) {
			theFile = new File("BMLD_" + fileName);
			theFile2 = new File("BMLD.csv");
		}
		
		try {
			if (!theFile.exists()) theFile.createNewFile();
			if (!theFile2.exists()) theFile2.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		System.out.println("Texthandler");
//		
//		try {
//			System.out.println(new File(".").getCanonicalPath());
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//	    try {
//	    	theFile = new File(fileName);
//	    	theFile.createNewFile();
//	    } catch (IOException e){
//	    	// TODO Catcher
//	    	e.printStackTrace();
//	    }

//	    try {
//			out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}	
		
		try {
			out1 = new PrintWriter(new BufferedWriter(new FileWriter(theFile)));
		} catch (IOException e){
			// TODO Catcher
			e.printStackTrace();
		}
		
		try {
			out2 = new PrintWriter(new BufferedWriter(new FileWriter(theFile2, true)));
		} catch (IOException e){
			e.printStackTrace();
		}
			
//		while (!stopped) {
//			try {
//				Thread.sleep(200);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		
	}
	
	public void done() {
			if (out1 != null){
					out1.flush();
					out1.close();
			}
			if (out2 != null){
				out2.flush();
				out2.close();
		}			
	}
	
	public void addSuffix(String newSuffix){
		suffix = suffix + newSuffix + "; ";
		out1.println(newSuffix);
	}
	
	public void writeString(String data){
		out1.println(data);
		out2.println(suffix + data);
		lineNr++;
	}
	
	public void writeIntData(int data){
		out1.println(data);
//		try {
//			out.writeInt(data);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
//	public void done(){
//		stopped = true;
//	}
	
	public void flushnow(){
		out1.flush();
		out2.flush();
	}
	
}



package ciGUI;

import java.io.*;

public class Test {
	public Test(){
		File file = new File("test.txt");
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (pw != null) { 
		pw.println("Hello World!");
		pw.close();
		}
	}
}

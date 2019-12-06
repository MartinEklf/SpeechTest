package ciGUI;

import java.io.*;

	/**
	 * The WordWindow class displays a window with a scrollpane upon creation.
	 * The file containing the sentences is read and stored in the object.
	 * The class contains methods for showing next sentence and previous sentence.
	 * 
	 * @author Martin Ekl�f
	 *
	 */
public class WordWindow {
	
	private int currentSentence = -1;
	private String[] sentences;
        private int rows = 0;
	
	public static void main(String[] args){
		
	}
	
	/**
	 * The constructor opens and reads the file 
	 * @param fName is the name of the file that contains the list
	 * @param rows is the number of sentences in that list
	 */
	public WordWindow(String fName){//, int rows){
		System.out.println("WordWindow");
		File file = new File(Constants.THE_PATH + fName);
		if (!file.exists()){
			System.out.println("Meningslistan finns inte!!! " + Constants.THE_PATH + fName);
			return;
		}
		
		BufferedReader bufRead;
		try {
			bufRead = new BufferedReader(new FileReader(Constants.THE_PATH + fName));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		
		
		sentences = new String[200];
                int i = 0;
//		for (int i = 0; i < rows; i++){
			try{
                            sentences[0] = bufRead.readLine();
                            while(sentences[i]!=null){
                                i++;
                                sentences[i] = bufRead.readLine();
                            }
			bufRead.close();
			}
			catch (IOException e){
				// TODO Error handling
				e.printStackTrace();
				//textArea.append("Error reading file :" + fName);
				return;
			}
                rows = i;
                System.out.println(rows);

//          }
//		try {
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

        public int getNrOfSentences(){
            return rows;
        }
	
	/**
	 * Method to get the next sentence to the window. If the number of sentences are
	 * reached the message "End of list" is returned.
	 */
	public String nextSentence(){
		//if (currentSentence != -1) newLine(); 
		if (currentSentence < sentences.length - 1){
			currentSentence++;
			return sentences[currentSentence];
		} else {
			return "End of list";
		}
	}
}

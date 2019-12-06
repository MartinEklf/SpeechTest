package ciGUI;
import javax.swing.*;
import javax.swing.text.MaskFormatter;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.*;
import java.io.FileFilter;
import java.util.Observable;
import java.util.Observer;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;



public class MainFrame extends JFrame implements ActionListener, Observer{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton correct;
	private JButton wrong;
	private JButton startButton;
	private JButton stopButton;
	private JButton pauseButton;
	private JComboBox listDropDown;
	private JComboBox bmld;
	private JComboBox test;
	private JComboBox side;
	private JTextField subjectName;
	private JTextField subjectNr;
	private GridBagConstraints c;
	private SoundTest theTest;
	private Calibrator calibrator;
	private WordWindow ww;
	private UpDownMethod upDown;
	private JTextArea textBox;
	private JScrollPane scrollPane;
	private JPanel textPane;

	
	//Groups of objects
	private JPanel settingsPanel; 
	private JPanel controlPanel;
	private JPanel startPanel;
	
	//Labels for settings panel
	private JLabel lblBmld;
	private JLabel lblList;
	private JLabel lblSubjectName;
	private JLabel lblSubjectNr;
	private JLabel lblTest;
	private JLabel lblSide;
	
	//Menu bar
	private JMenuBar menuBar;
	private JMenu menu;
	private JCheckBoxMenuItem cbMenuItem;
	
	//Additional variables
	private File sFile;
	private File cFile;
        private FileInputStream cValueFile;
	
	private File files[];
	private String[] fileList;
	private int[] nrOfLists;
        private int nrOfSentences;
	private File filesPath;

	
	private float prevNoiseVal = 1000.0f;
	private float prevSNLevel = 1000.0f;
	private boolean waitForResponse = true;
	private boolean finished = true;
	private boolean fileError = false;
	private int sentenceNr = 0;
        private float signalGain = Constants.INIT_SIGNAL_GAIN;
	
//	private TextHandler printer;
	
	public MainFrame() {
		// constructor
		super("Hearing Test GUI");
		setBounds(0,0,1200,100);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

        populateAndCreateGUI();
		setVisible(true);
	}
	
	public void populateAndCreateGUI(){
		//Huvudpanelen...
		JPanel p = new JPanel(new GridBagLayout());
		
		c = new GridBagConstraints();
//		c.fill = GridBagConstraints.NONE;
//		c.weighty = 0; //Do not request additional vertical space
//		c.gridx = 0;
//		c.gridy = 0;
//		c.gridwidth = 3;
//		JLabel lblLogo = new JLabel(new ImageIcon("img\\logo.GIF"), JLabel.CENTER);
//		p.add(lblLogo,c);
//		//myPanel.add(lblLogo);
		
		//Settingspanelen
		this.createObjectsInSettingsPanel();
		settingsPanel = new JPanel(new GridLayout(0,1,5,8));
		
//		settingsPanel.add(lblSubjectName);
//		settingsPanel.add(subjectName);
//
//		settingsPanel.add(lblSubjectNr);
//		settingsPanel.add(subjectNr);
//
//		settingsPanel.add(lblTest);
		settingsPanel.add(test);
		
//		settingsPanel.add(lblMethod);
//		settingsPanel.add(method);
		
//		settingsPanel.add(lblSide);
//		settingsPanel.add(side);
//
//		settingsPanel.add(lblBmld);
//		settingsPanel.add(bmld);
//
////		settingsPanel.add(lblSn);
////		settingsPanel.add(snDropDown);
		
//		settingsPanel.add(lblList);
		settingsPanel.add(listDropDown);
		
//		settingsPanel.add(lblWaitForResponseDropDown);
//		settingsPanel.add(waitForResponseDropDown);
		
//		settingsPanel.setBorder(BorderFactory.createTitledBorder("Test settings"));
		c.weightx = 1; //request additional space
		c.weighty = 0; //do not Request additional space
		c.gridx = 0;
		c.gridy = 0;
//		c.gridwidth = 2;
//		c.anchor = GridBagConstraints.FIRST_LINE_START;
		p.add(settingsPanel,c);
		
		//Starta-test panelen
		this.createPauseButton();
		this.createStartButton();
		this.createStopButton();
		startPanel = new JPanel(new GridLayout(0,5,5,8));
		startPanel.add(startButton);
		startPanel.add(pauseButton);
		startPanel.add(stopButton);
//		settingsPanel.setBorder(BorderFactory.createTitledBorder("Start test"));
//		c.weightx = 0;
//		c.gridx = 0;
//		c.gridy = 2;
//		c.gridwidth = 1;
//		c.fill = GridBagConstraints.BOTH;
//		p.add(startPanel,c);
		
		//Control-panelen (ratt och felknappar)
		this.createControlButtons();
//		controlPanel = new JPanel(new GridLayout(2,1,5,8));
		startPanel.add(correct);
		startPanel.add(wrong);
//		controlPanel.setBorder(BorderFactory.createTitledBorder("Control test"));
		c.weightx = 0;
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 1;
//		c.fill = GridBagConstraints.BOTH;
		p.add(startPanel,c);
		
		//Panel med meningarna
		this.createTextBox();		
//		textPane.setBorder(BorderFactory.createTitledBorder("Text"));
		c.weightx = 1;
		c.gridx = 3;
		c.gridy = 0;
		c.gridwidth = 1;
//		c.gridheight = 2;
		c.fill = GridBagConstraints.BOTH;
		p.add(textPane,c);
		
//		p.add(lblLogo, BorderLayout.NORTH);
//		p.add(myPanel, BorderLayout.CENTER);
//		p.add(textPane, BorderLayout.SOUTH);
		
//              p.add(settingsPanel,c);
                this.add(p);
		stopButton.setEnabled(false);
		pauseButton.setEnabled(false);
		correct.setEnabled(false);
		wrong.setEnabled(false);
		//this.addButtons();
		
		//this.addPauseButton();
		//this.addStopButton();
		
//		//Menus
//		menuBar = new JMenuBar();
//
//		//Build first menu
//		menu = new JMenu("Tools");
//		menu.setMnemonic(KeyEvent.VK_T);
//		menu.getAccessibleContext().setAccessibleDescription(
//				"Extra functions needed to adjust the test environment");
//		menuBar.add(menu);
//
//		//First JMenuItem
//		cbMenuItem = new JCheckBoxMenuItem("Start/Stop calibration tone");
//		cbMenuItem.setMnemonic(KeyEvent.VK_C);
//		cbMenuItem.setSelected(false);
//		cbMenuItem.addActionListener(this);
//
//		menu.add(cbMenuItem);
//
//		this.setJMenuBar(menuBar);
		
		//Shows the window with the sentence lists
//		WordWindow ww = new WordWindow(288);
		
	}

    public void actionPerformed(ActionEvent e) {
    	fileError = false;
    	//Testforrattaren vill starta testet
    	if(e.getSource().equals((Object)startButton)){
            if(stopButton.isEnabled()){
                theTest.getSignalPlayer().unPause();
                pauseButton.setEnabled(true);
                startButton.setEnabled(false);
            }
            else
            {
    		System.out.println("forsoker starta test");
    		//...sa skapar vi ett nytt "test"
    		String strTest = test.getSelectedItem().toString();
//    		String strMethod = method.getSelectedItem().toString();
//    		String strSide = side.getSelectedItem().toString();
//    		int intMethod = Constants.ConstantSignalToNoiseRatio;//method.getSelectedIndex();
//    		String strBmldMode = bmld.getSelectedItem().toString();
    		String listChoice = listDropDown.getSelectedItem().toString();
    		listChoice = listChoice.substring(listChoice.indexOf(" ") + 1,listChoice.length());

                try {
////                        cValueFile = new FileInputStream(Constants.THE_PATH + "calibrate.txt");
//                        if(strBmldMode.equals(Constants.spin0)){
//	    			sFile = new File(Constants.THE_PATH + strTest + "\\Spi_" + listChoice + ".wav");
////	    			nFile = new File(Constants.THE_PATH + strTest + "\\N0_" + listChoice + ".wav");
//	    			System.out.println(sFile);
//	    			}
//	    		else {
	    			sFile = new File(Constants.THE_PATH + strTest + "\\S0_" + listChoice + ".wav");
//	    			nFile = new File(Constants.THE_PATH + strTest + "\\N0_" + listChoice + ".wav");
	    			System.out.println(sFile);
//    		}
    		} catch (Exception ex)
	    		{
	    			fileError = true;
	        		System.out.println(fileError);
	    			JOptionPane.showMessageDialog(null, "Filer hittades inte!");
    		}
                if (!sFile.isFile()) {
                    fileError = true;
                    cleanTextBox();
                    printResult("Denna lista saknas!");
                    return;
                }
                signalGain = Constants.INIT_SIGNAL_GAIN + getCalibGain(Constants.THE_PATH);
//                try {
//                        cValueFile = new FileInputStream (argv[0]);
//                    DataInputStream in = new DataInputStream (cValueFile);
                
//                } catch (IOException x) {
//                   System.err.println(x);
//                } finally {
 //                   if (in != null) in.close();
//                }

                
    		System.out.println(fileError);
    		if (fileError != true) {
    		theTest = new SoundTest(sFile);//, sFile);   // These are the files we'll be playing);
    		theTest.getSignalPlayer().addObserver(this);
    		theTest.getSignalPlayer().setObserved(true);
    		theTest.getSignalPlayer().setInitGain(signalGain);
    		
//    		if (strBmldMode.equals(Constants.sln0)) theTest.getSignalPlayer().setMode(Constants.sL);
//    		if (strBmldMode.equals(Constants.srn0)) theTest.getSignalPlayer().setMode(Constants.sR);
//    		if (strBmldMode.equals(Constants.srnr)) {
//    			theTest.getSignalPlayer().setMode(Constants.sR);
//    			theTest.getNoisePlayer().setMode(Constants.sR);
//    		}
//    		if (strBmldMode.equals(Constants.slnl)) {
//    			theTest.getSignalPlayer().setMode(Constants.sL);
//    			theTest.getNoisePlayer().setMode(Constants.sL);
//    		}
    		
    		
//    		float f = signalGain - Constants.MAX_SN_LEVEL + Float.parseFloat(String.valueOf(snDropDown.getSelectedIndex()));

//    		float f = signalGain - Float.parseFloat(snDropDown.getSelectedItem().toString());
//    		theTest.getNoisePlayer().setInitGain(f);

    		
//    		theTest.getNoisePlayer().setLoop(true); //brus med loop
//    		if (waitForResponseDropDown.getSelectedItem().toString().equals("Yes")) {
//    			waitForResponse = true;
//    			theTest.getSignalPlayer().setAutoPause(true);
//    		} else {
//    			waitForResponse = false;
//    			theTest.getSignalPlayer().setAutoPause(false);
//    		}

    		finished = false;
    		sentenceNr = 0;
    		
//    		printer = new TextHandler();
//    		printer.addSuffix(subjectName.getText());
//    		printer.addSuffix(strTest);
//    		printer.addSuffix(strMethod);
//    		printer.addSuffix(strBmldMode);
//    		printer.addSuffix(strSide);
//    		printer.addSuffix(listChoice);
    		
    		enableButtons(false);
    		
    		ww = new WordWindow(strTest + "\\list" + listChoice + ".txt");//, Constants.NR_OF_SENTENCES);
    		nrOfSentences = ww.getNrOfSentences();
                cleanTextBox();
    		nextSentence();
    		upDown = new UpDownMethod(Constants.ConstantSignalToNoiseRatio, nrOfSentences);
                try {
                    //set volume:
                    System.out.println("Ska köra batchfilen volume.bat");
                    Process p = new ProcessBuilder(new String[]{"cmd.exe", "/c" ,"volume.bat"}).start();
                } catch (IOException ex) {
                    System.out.println("Hittade inte volume.bat");                    
                    JOptionPane.showMessageDialog(null, "Volume.bat hittades inte!");
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
    		}
            }
        }
    	//Correct is pressed
    	else if(e.getSource().equals((Object)correct)){	
    		responsePressed(true);
    	}
    	//Wrong is pressed
    	else if(e.getSource().equals((Object)wrong)){
    		responsePressed(false);
    	}
    	//Stop is pressed
    	else if(e.getSource().equals((Object)stopButton)){
//    		printer.writeString("Test avbrutet!");
    		String strSoundCard = null;
    		strSoundCard = theTest.getSignalPlayer().getMinLevel() + ", " + theTest.getSignalPlayer().getMaxLevel();
//    		printer.writeString(strSoundCard);
//    		cleanTextBox();
    		stoppPressed();	
        	}
    	//Pause is pressed
    	else if(e.getSource().equals((Object)pauseButton)){
    		if (theTest.getSignalPlayer().isPaused()){
    			theTest.getSignalPlayer().unPause();
                        pauseButton.setEnabled(true);
                        startButton.setEnabled(false);
    		}else{
    			theTest.getSignalPlayer().pause();
                        pauseButton.setEnabled(false);
                        startButton.setEnabled(true);
    		}
    	}
//    	//BMLD test is chosen
//    	else if(e.getSource().equals((Object)bmld)){
//    	}
    	//Test is chosen
    	else if(e.getSource().equals((Object)test)){
    		int intTest = test.getSelectedIndex();
    		setNrOfLists(nrOfLists[intTest]);
    	}
    	else if (e.getSource().equals((Object)cbMenuItem)){
    		if (cbMenuItem.isSelected()){
    			if (finished){
    			//Calibrate
    			printResult("Startar kalibrering...");
    			cFile = new File(Constants.THE_PATH + "Calibration.wav");
    			calibrator = new Calibrator(cFile);
    			calibrator.getCalibPlayer().setInitGain(signalGain);
    			startButton.setEnabled(false);
    			} else {
    				cbMenuItem.setSelected(false);
    			}
    		} else {
    			calibrator.getCalibPlayer().done();
    			startButton.setEnabled(true);
    		}
    	}
    }

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new MainFrame();
	}
	
	public void update(Observable t, Object o){
		switch(theTest.getSignalPlayer().getState()) {
			case Constants.PLAYING_SENTENCE:
				theTest.getSignalPlayer().setLevel(signalGain);
				startButton.setEnabled(false);
				stopButton.setEnabled(true);
//				wrong.setEnabled(false);
//				correct.setEnabled(false);
//				if (!waitForResponse) {
//					if(!theTest.getSignalPlayer().hasRecievedResponse()) {
//						responsePressed(false);
//					}
//				}
				finished = false;
				break;
			case Constants.PLAYING_SILENCE_WAIT_RESPONSE:
//				wrong.setEnabled(true);
//				correct.setEnabled(true);
				finished = false;
				break;
			case Constants.PLAYING_SILENCE_NOT_WAITING:
//				wrong.setEnabled(false);
//				correct.setEnabled(false);
				finished = false;
				break;
			case Constants.AUTOPAUSED:
//				wrong.setEnabled(true);
//				correct.setEnabled(true);
				finished = false;
				break;
			case Constants.FINISHED:
				finished = true;
//				if (theTest.getSignalPlayer().hasRecievedResponse()){
//					stoppPressed();
//				} else {
//					wrong.setEnabled(true);
//					correct.setEnabled(true);
//					if (!waitForResponse) {
//						responsePressed(false);
//					}
//				}
				break;
			case Constants.START_ERROR:
    			JOptionPane.showMessageDialog(this, "Hittade inga ljudfiler!","Error",JOptionPane.ERROR_MESSAGE);
//    			printer.writeString("Could not initiate file!");
    			stoppPressed();
		}
	}
	
	private void responsePressed(boolean correct){
		theTest.getSignalPlayer().setLevel(signalGain);
//		prevNoiseVal = theTest.getNoisePlayer().getCurrentLevel();
		prevSNLevel = signalGain - prevNoiseVal;
                if(sentenceNr < nrOfSentences){
		sentenceNr++;
		String response = correct ? "Correct" : "Wrong";
		printResponse(response);// + ": " + Math.round(prevSNLevel) + " dB S/N");
		nextSentence();
//		printer.writeString(sentenceNr + "; " + response);// + "; " + Math.round(prevSNLevel) + "; " + prevNoiseVal + "; " + theTest.getSignalPlayer().getCurrentLevel());
//		printer.flushnow();
  		upDown.setNewResponse(correct, prevSNLevel);
                }
//		int i = snDropDown.getSelectedIndex(); //startv�rde, eller senaste v�rde p� sn
//		if (correct){ //Correct pressed
//			if (prevNoiseVal + upDown.getStep() <= signalGain -
//					Constants.MIN_SN_LEVEL) {
//				snDropDown.setSelectedIndex(i+(int)upDown.getStep());
//				prevNoiseVal = prevNoiseVal + upDown.getStep();
//				System.out.println("Nu h�jer vi :" + prevNoiseVal);
//			} else {  // Out of range
//				snDropDown.setSelectedIndex(snDropDown.getItemCount() - 1);
//				prevNoiseVal = signalGain - Constants.MIN_SN_LEVEL;
//			}
//		} else { //Wrong pressed
//			if (prevNoiseVal - upDown.getStep() >= signalGain -
//					Constants.MAX_SN_LEVEL) {
//				prevNoiseVal = prevNoiseVal - upDown.getStep();
//				snDropDown.setSelectedIndex(i-(int) upDown.getStep());
//				System.out.println("Nu s�nker vi :" + prevNoiseVal);
//			} else { // Out of range
//				snDropDown.setSelectedIndex(0);
//				prevNoiseVal = signalGain - Constants.MAX_SN_LEVEL;
//			}
//		}
//		theTest.getNoisePlayer().setLevel(prevNoiseVal); //max = 6.0206 f�r mitt ljudkort?
		theTest.getSignalPlayer().setResponded(true);
		
  		if (upDown.shouldBreak() || finished){
  			stoppPressed();
  		}
	}
		
	private void stoppPressed() {
		theTest.getSignalPlayer().done();
//		theTest.getNoisePlayer().done();
		theTest.getSignalPlayer().unPause();
		
		
//                snDropDown.setSelectedIndex(Constants.START_SN_INDEX);

		enableButtons(true);
		String result = upDown.getResult();
//		String result = ";;;;;Number of turningpoints/% correct: " + upDown.getNrOfTurningPoints() + 
//		", Average level: " + upDown.getAverage() +	", Deviation: " + upDown.getDeviation());
//		String comment = JOptionPane.showInputDialog(this, "Add comment to measurement:","Comment dialog",JOptionPane.OK_CANCEL_OPTION);
//		if (comment != null) result = result + "; " + comment;
//		printer.writeString(";;;;;" + result);
		printResult(result);
		
//		printer.done();
//		printer = null;
		ww = null;

		finished = true;
	}
	
	
	private void enableButtons(boolean enable){
		startButton.setEnabled(enable);
		stopButton.setEnabled(!enable);
		pauseButton.setEnabled(!enable);
//		bmld.setEnabled(enable);
//		test.setEnabled(enable);
//		side.setEnabled(enable);
//   		snDropDown.setEnabled(enable);
		listDropDown.setEnabled(enable);
//		waitForResponseDropDown.setEnabled(enable);
//		subjectName.setEnabled(enable);
//		subjectNr.setEnabled(enable);
		correct.setEnabled(!enable);
		wrong.setEnabled(!enable);
	}

	/**
	 * Method to print the next sentence to the window. If the number of sentences are
	 * reached the message "End of list" is displayed.
	 */
	public void nextSentence(){
		if (sentenceNr < 9) textBox.append(" ");
		textBox.append((sentenceNr + 1) + " ");
		textBox.append(ww.nextSentence());
	}
	
	/**
	 * Method to start a new line in the text area. 
	 */
	private void newLine(){
		textBox.append("\n");
	}
	
	/**
	 * Method to insert a tab on the current line in the text area.
	 */
	private void newTab(){
		textBox.append("\t");
	}
	
	/**
	 * Method to erase everything in the textbox
	 */
	private void cleanTextBox(){
		textBox.setText(null);
	}
	
	/**
	 * Method to display text at the end of the current line in the text area.
	 * A tab is inserted before the text.
	 * 
	 * @param result is the string to be displayed in the text area.
	 */
	public void printResponse(String result){
		newTab();
		textBox.append(result);
		newLine();
	}

	/**
	 * Method to display text at the end of the current line in the text area.
	 * A tab is inserted before the text.
	 * 
	 * @param result is the string to be displayed in the text area.
	 */
	public void printResult(String result){
		newLine();
		textBox.append("Result: " + result);
		newLine();
	}
	
	public void createControlButtons(){
		ImageIcon icoCorrect = new ImageIcon("img\\correct.GIF");
		ImageIcon icoWrong = new ImageIcon("img\\wrong.GIF");
		correct = new JButton("R", icoCorrect);
		correct.setMnemonic(KeyEvent.VK_R);
                correct.addActionListener(this);
		wrong = new JButton("F", icoWrong);
		wrong.setMnemonic(KeyEvent.VK_F);
		wrong.addActionListener(this);
	}

	protected MaskFormatter createFormatter(String s) {
	    MaskFormatter formatter = null;
	    try {
	        formatter = new MaskFormatter(s);
	        formatter.setPlaceholderCharacter('_');
	    } catch (java.text.ParseException exc) {
	        System.err.println("formatter is bad: " + exc.getMessage());
	        System.exit(-1);
	    }
	    return formatter;
	}

	public void createObjectsInSettingsPanel(){
		
//		snDropDown = new JComboBox();
//
//		waitForResponseDropDown = new JComboBox();
//		//Populate ComboBox with Yes No
//		waitForResponseDropDown.addItem("Yes");
//		waitForResponseDropDown.addItem("No");
//		waitForResponseDropDown.setSelectedIndex(1);
//
//		waitForResponseDropDown.addActionListener(this);
//
//		subjectName = new JTextField(20);
		
//		subjectNr = new JFormattedTextField(createFormatter("####-##-##"));
		
		listDropDown = new JComboBox();
		
//		bmld = new JComboBox();
		
//		// populate Combobox array with integers
//		for (int i = Constants.MAX_SN_LEVEL; i >= Constants.MIN_SN_LEVEL; i--)
//			snDropDown.addItem(String.valueOf(i));
//
//		snDropDown.setSelectedIndex(Constants.START_SN_INDEX);
////		snDropDown.setSelectedIndex((int) Math.floor((Constants.MAX_SN_LEVEL - Constants.MIN_SN_LEVEL)/2));
//
//		bmld.addItem(Constants.s0n0);
//		bmld.addItem(Constants.slnl);
//		bmld.addItem(Constants.sln0);
//		bmld.addItem(Constants.srnr);
//		bmld.addItem(Constants.srn0);
//		bmld.addItem(Constants.spin0);
		
		test = new JComboBox();
		
		String[] fileL = dirList(Constants.THE_PATH);
		
		for (int i=0; i < fileL.length; i++){
                    test.addItem(fileL[i]);
		}
                setNrOfLists(nrOfLists[0]);
		
		test.addActionListener(this);
		
//		method = new JComboBox();
//		method.addItem(Constants.ADAPTIVE1);
//		method.addItem(Constants.ADAPTIVE2);
//		method.addItem(Constants.ADAPTIVE3);
//		method.addItem(Constants.CONSTANT);
		
		//labels
		lblBmld = new JLabel("Mode:");
		lblList = new JLabel("List:");
//		lblSn = new JLabel("s/n-ratio:");
//		lblWaitForResponseDropDown = new JLabel("Wait for response:");
		lblSubjectName = new JLabel("Name:");
		lblSubjectNr = new JLabel("Birth date YYYY-MM-DD:");
		lblTest = new JLabel("Test:");
//		lblMethod = new JLabel("Method:");
		lblSide = new JLabel("Aided side (if freefield):");
		
		side = new JComboBox();
		side.addItem("Headphones");
		side.addItem("Right");
		side.addItem("Left");
		side.addItem("Bilateral");
		
	}
	
	public void setNrOfLists(int theNrOfLists){
		listDropDown.removeAllItems();
		for (int j = 1; j < theNrOfLists + 1; j++){
			listDropDown.addItem("lista " + j);
		}
	}
	
	public void createStartButton(){
		ImageIcon play = new ImageIcon("img\\play.GIF");
		startButton = new JButton("", play);
		startButton.addActionListener(this);
	}
	
	public void createStopButton(){
		ImageIcon stop = new ImageIcon("img\\stop.GIF");
		stopButton = new JButton("", stop);
		stopButton.addActionListener(this);
	}
	
	public void createPauseButton(){
		ImageIcon pause = new ImageIcon("img\\pause.GIF");
		pauseButton = new JButton("", pause);
		pauseButton.addActionListener(this);
	}
	
	public void createTextBox(){
		textBox = new JTextArea(10,20);
		textBox.setTabSize(20);
		textBox.setEditable(false);
		scrollPane = new JScrollPane(textBox);
		scrollPane.setMinimumSize(new Dimension(50,10));
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setWheelScrollingEnabled(true);
		textPane = new JPanel(new BorderLayout());
		textPane.add(scrollPane, BorderLayout.CENTER);//scrollPane, BorderLayout.CENTER);
		textPane.setPreferredSize(new Dimension(200, 30));
	}

        private float getCalibGain(String path){
            float f = 0f;
            File cVfile = new File(Constants.THE_PATH + "calib.txt");
            String fS = String.valueOf(f);
            if (!cVfile.exists()){
                    printResult("VARNING! Kalibreringsfilen finns inte!!! " + Constants.THE_PATH + "calib.txt");
                    return f;
            }

            BufferedReader bufRead;
            try {
                    bufRead = new BufferedReader(new FileReader(Constants.THE_PATH + "calib.txt"));
            } catch (FileNotFoundException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                    return f;
            }


            try{
                    fS = bufRead.readLine();
            }
            catch (IOException e){
                    // TODO Error handling
                    e.printStackTrace();
                    //textArea.append("Error reading file :" + fName);
                    return f;
            }

            try {
                    bufRead.close();
            } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
            }
            f = Float.parseFloat(fS);
            return f;
        }
	
	public String[] dirList(String path){
		filesPath = new File(path);
		if (!filesPath.exists()) {
                    String[] tempString = {"Unvalid path"};
                    nrOfLists = new int[1];
                    nrOfLists[0] = 0;
                    return tempString;
		}
		files = filesPath.listFiles();
		Arrays.sort(files);
		int j=0;
		for (int i=0;i<files.length;i++){
			if (files[i].isDirectory()){
				j++;
			}
		}
		fileList = new String[j];
		nrOfLists = new int[j];
		j = 0;
		for (int i=0;i<files.length;i++){
			if (files[i].isDirectory()){
				fileList[j] = files[i].getName();
				nrOfLists[j] = files[i].listFiles(txtFilter).length;
				j++;
			}
		}
		return fileList;
	}
	
	// Defining a file filter and the condition that will satisfy the file filter
	FileFilter txtFilter = new FileFilter()
	{
	        public boolean accept(File file)
	        {
	                String sFilePath = file.getName();
	                if( sFilePath.endsWith(".txt") )
	                {
	                        return true;
	                }
	                else
	                {
	                        return false;
	                }
	        }                      
	};
	

}

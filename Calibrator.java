package ciGUI;

import java.io.File;

public class Calibrator {
	private CISoundPlayer calibPlayer;
	private Thread calib;
	
	public Calibrator(File calibFile) {
		// Create the signal from a file
		
		try{	
			//skapa en "player" som spelar upp vald fil
			calibPlayer = new CISoundPlayer(calibFile);
			calib = new Thread(calibPlayer);
			calib.start();
			calibPlayer.setAutoPause(false);
			calibPlayer.setLoop(true);
		}catch(Exception e){System.out.println(e.toString());}
	}
	
	public CISoundPlayer getCalibPlayer(){
		return calibPlayer;
	}
}

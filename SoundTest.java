package ciGUI;

import java.io.File;

public class SoundTest {
	private CISoundPlayer signalP;
//	private CISoundPlayer noiseP;
//	private Thread noise;
	private Thread signal;
	
	//konstruktor
	public SoundTest(File sFile){//nFile, File sFile){
		
		// Create the signal from a file
		
//		try{
//
//			//skapa en "player" som spelar upp vald fil
//			noiseP = new CISoundPlayer(nFile);
//			noise = new Thread(noiseP);
//			noise.start();
//			noiseP.setAutoPause(false);
//		}catch(Exception e){System.out.println(e.toString());}
//
		// Create the noise from a file
		try{
			
			//skapa en "player" som spelar upp vald fil
			signalP = new CISoundPlayer(sFile);
			signal = new Thread(signalP);
			signal.start();
		}catch(Exception e){System.out.println(e.toString());}
	}
	
	public CISoundPlayer getSignalPlayer(){
		return signalP;
	}
	
//	public CISoundPlayer getNoisePlayer(){
//		return noiseP;
//	}
}

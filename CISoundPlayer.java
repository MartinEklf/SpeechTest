package ciGUI;

import java.io.*;
import javax.sound.sampled.*;
import java.util.Observable;

/**
 * This class is a Swing component that can load and play a sound clip,
 * displaying progress and controls.  The main( ) method is a test program.
 * This component can play sampled audio or MIDI files, but handles them 
 * differently. For sampled audio, time is reported in microseconds, tracked in
 * milliseconds and displayed in seconds and tenths of seconds. For midi
 * files time is reported, tracked, and displayed in MIDI "ticks".
 * This program does no transcoding, so it can only play sound files that use
 * the PCM encoding.
 */
public class CISoundPlayer extends Observable implements Runnable {
	
	    Clip clip;               // Contents of a sampled audio file
	    Clip clip2;
	    boolean playing = false; // whether the sound is currently playing
		private boolean isThreadStopped = false; // whether the sound is stopped
		private boolean isThreadPaused = false; // whether the sound is stopped
		private boolean loopContinuously = false; // whether the file should loop
		private boolean isAutoPaused = false; // whether the file is paused in long silence.
		private boolean silent = true; // whether the file is silent at the moment.
		private boolean willAutoPause = true; // whether the file should pause at long silence.
		private boolean skipNextAutoPause = true; // whether next pause due to end of long silence should be skipped 
		private boolean isResponded = true; // whether last sentence has been responded.
		private boolean finished = false; // whether file is finished or not.
		private boolean isError = false; // Whether the file is loaded and playing as expected.
		private boolean isObserved = false;
		
		private float initGain;
		private float oldGain;
		private byte[] abData;
		
	    // Length and position of the sound are measured in milliseconds for 
	    // sampled sounds and MIDI "ticks" for MIDI sounds
	    int audioLength;         // Length of the sound.  
	    int audioPosition = 0;   // Current position within the sound
	    Mixer mixer;
	    DataLine.Info info;
	    FloatControl gainControl;
	    FloatControl balanceControl;
	    FloatControl volume;
	    Float maxLevelForSoundCard;
	    Float minLevelForSoundCard;
	    Float resolutionLevelForSoundCard;
	    private File soundFile;
	    private boolean bigEndian = true;
	    private int thisMode = Constants.s0;


	    // Create a CISoundPlayer component for the specified file.
	    /**
	     * Class constructor specifying the sound file to load and play.
	     *  @param f  the wave sound file to be loaded and played.
	     *   
	     *  @author Martin Ekl�f and Filip Asp
	     *  
	     */
	    public CISoundPlayer(File f)
//	        throws IOException,
//	               UnsupportedAudioFileException,
//	               LineUnavailableException,
//	               MidiUnavailableException,
//	               InvalidMidiDataException
	    {
	    	
	    	soundFile = f;
//	            // Getting a Clip object for a file of sampled audio data is kind
//	            // of cumbersome.  The following lines do what we need.
//	            AudioInputStream ain = AudioSystem.getAudioInputStream(f);
//	            try {
//	            	info = new DataLine.Info(Clip.class,ain.getFormat( ));
//	                clip = (Clip) AudioSystem.getLine(info);
//	                clip.open(ain);
//	               
//	            }
//	            finally { // We're done with the input stream.
//	                ain.close( );
//	            }
//	            
//	            // Get the clip length in microseconds
//	            audioLength = (int)(clip.getMicrosecondLength( ));
//	            
//	            
//	            gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN); //Master gain �r i decibel!!!
//	            
//	        
	    }
	    /**
	     * This method stops the playing of the sound file causing auline
	     * to close.
	     */
	    
	    public void done(){
	    	isThreadStopped = true;
	    	loopContinuously = false;
	    }
	    
	    /**
	     * Turns the current playing file into playing mode if paused
	     */
	    public void unPause(){
	    	isThreadPaused = false;
	    }
	    
	    /**
	     * Pauses the current played file if not already paused.
	     */
	    public void pause(){  //boolean sentenceEnded){
	    	isThreadPaused = true;
	    }
	    
	    /**
	     * Decides whether the sound file should play continuously or not.
	     * 
	     * @param b	should be true if the file should repeat and false otherwise
	     */
	    public void setLoop(boolean b){
	    	loopContinuously = b;
	    }
	    
	    /**
	     * Decides whether the sound file should stop automatically when a silent
	     * part of the file if followed by sound.
	     *  
	     * @param b should be true if the file should stop automatically
	     */
	    public void setAutoPause(boolean b){
	    	willAutoPause = b;
	    }
	    
	    /**
	     * Decides whether the sound file should skip the next automatic stop
	     * at the end of a silent part or not. This has no effect if the sound
	     * player is not configured to stop automatically. 
	     * 
	     * @param b should be true if the file should go on at next transition
	     *          from silent to sound and not stop automatically.
	     */
	    public void setNextAutoPause(boolean b){
	    	skipNextAutoPause = b;
	    }
	    
	    /**
	     * Sets whether the sound player has received a response or not. If the 
	     * sound player is configured to stop automatically on transition from 
	     * silent to sound and is currently automatically paused, the player will
	     * continue playing. If it is not automatically paused the player is set 
	     * not to pause automatically at next transition from silent to sound.
	     * Observers are notified.
	     * 
	     * @param b should be set true if the sound player has received a response.
	     * 
	     */
	    public void setResponded (boolean b){
	    	isResponded = b;
	    	if (willAutoPause && isResponded) {
	    		if (isAutoPaused) {
	    			isAutoPaused = false;
	    		}	
	    		else {
	    			skipNextAutoPause = true;
	    		}
	    	}
	    	setChanged();
	    	notifyObservers();
	    }
	    
	    /**
	     * Sets the parameter controlling the way the sound should be presented.
	     * Signal presented to both ears correlated (s0), only one ear (sL, sR) or
	     * phase shifted (sPi).
	     * 
	     *  @param mode is 0 for s0, 1 for sL, 2 for sR, and 3 for sPi. 
	     */
	    public void setMode(int mode){
	    	thisMode = mode;
	    }
	    
	    /*
	     * State:
//		PLAYING_SENTENCE = 0;
//		PLAYING_SILENCE_WAIT_RESPONSE = 1;
//		PLAYING_SILENCE_NOT_WAITING = 2;
//		AUTOPAUSED = 3;
//		PAUSED = 4;
//		STOPPED = 5;
//		FINISHED = 6;
//	    START_ERROR = 7;

	    /**
	     * Returns the state of the sound player
	     * 0 = Playing, not waiting for response (Sentence)
	     * 1 = Playing, waiting for response (Silence)
	     * 2 = Playing, not waiting for response (Silence)
	     * 3 = Autopaused, waiting for response
	     * 4 = Paused by user
	     * 5 = Stopped, closing soundplayer
	     * 6 = Finished, signal file is ended
	     * 7 = Start error, the signal file is not playing or stopped or paused in any way.
	     * 
	     * @return state an integer from 0 to 7
	     */
	    public int getState(){
            if (finished){
                return Constants.FINISHED;
            } else {
                return Constants.PLAYING_SENTENCE;
            }
                //	    	int state = 0;
//	    	if (isError) state = Constants.START_ERROR;
//	    	else if (isThreadStopped) state = Constants.STOPPED;
//	    	else if (finished) state = Constants.FINISHED;
//	    	else if (isThreadPaused) state = Constants.PAUSED;
//	    	else if (isAutoPaused) state = Constants.AUTOPAUSED;
//	    	else if (silent) {
//	    		if (isResponded) {
//	    			state = Constants.PLAYING_SILENCE_NOT_WAITING;
//	    		}else {
//	    			state = Constants.PLAYING_SILENCE_WAIT_RESPONSE;
//	    		}
//	    	} else {
//	    		state = Constants.PLAYING_SENTENCE;
//	    	}
//	    	return state;
	    }
	    
	    /**
	     * The default thread invoked upon start. Plays the sound file until finished
	     * or stopped in any way. 
	     */
	    public void run() {
	    	
			oldGain = 9999;

			// Repeat if loop continuously
//	    	do {
				if (!soundFile.exists()) {
					System.err.println("Wave file not found: " + soundFile);
					isError = true;
					setChanged();
					notifyObservers();
					return;
				}
		 
				AudioInputStream audioInputStream = null;
				try {
					audioInputStream = AudioSystem.getAudioInputStream(soundFile);
				} catch (UnsupportedAudioFileException e1) {
					e1.printStackTrace();
					isError = true;
					setChanged();
					notifyObservers();
					return;
				} catch (IOException e1) {
					e1.printStackTrace();
					isError = true;
					setChanged();
					notifyObservers();
					return;
				}
		 
				AudioFormat format = audioInputStream.getFormat();
				SourceDataLine auline = null;
				DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
				
				bigEndian = format.isBigEndian();
				
				System.out.println("Format: " + format.toString());
				
				try {
					auline = (SourceDataLine) AudioSystem.getLine(info);
					auline.open(format);
				} catch (LineUnavailableException e) {
					e.printStackTrace();
					isError = true;
					setChanged();
					notifyObservers();
					return;
				} catch (Exception e) {
					e.printStackTrace();
					isError = true;
					setChanged();
					notifyObservers();
					return;
				}
		 
				if (auline.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
					gainControl = (FloatControl) auline
							.getControl(FloatControl.Type.MASTER_GAIN);
				} else{
					System.out.println("Det finns ingen master_gain p� det h�r ljudkortet. Vi kan inte s�tta volymen");
					isError = true;
					setChanged();
					notifyObservers();
					return;
				}
				if (auline.isControlSupported(FloatControl.Type.BALANCE)) {
					balanceControl = (FloatControl) auline
							.getControl(FloatControl.Type.BALANCE);
				} else{
					System.out.println("Det finns ingen balance p� det h�r ljudkortet. Vi kan inte �ndra balans");
					isError = true;
					setChanged();
					notifyObservers();
					return;
				}

				if (oldGain == 9999) {
					setLevel(initGain);
					oldGain = getCurrentLevel();
					setBalance();
				} else {
					setLevel(oldGain);
				}

				playing = true;
				auline.start();
				int nBytesRead = 0;
				abData = new byte[Constants.EXTERNAL_BUFFER_SIZE];
				
				try {
					// Runs until file is empty and player not stopped 
					while (nBytesRead != -1 && !isThreadStopped) {
						// Gets stuck in this loop if paused
						while (isThreadPaused){
							playing = false;
							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						playing = true;
						nBytesRead = audioInputStream.read(abData, 0, abData.length);
//						if (isObserved) {
//							if (silent){
//								int i = 0;
//								//Check if bytes to play are still silent
//								while (getSampleMax(abData, abData.length - i - 4) == 0.0f && i<Constants.BYTES_TO_CHECK_FOR_SILENT) {
//									i++;
//								}
//								//Ta bort denna check sedan:
////								if (willAutoPause) System.out.println("Silent; " + i);
////								System.out.println("Tyst: " + getSampleMax(abData, abData.length - i - 4));
//
//								if (i<Constants.BYTES_TO_CHECK_FOR_SILENT - 1){
//									silent = false;
//									System.out.println("Tyst? " + silent);
//									if(!skipNextAutoPause && willAutoPause) {
//										isAutoPaused = true;
//									}
//									skipNextAutoPause = false;
//									setChanged();
//									notifyObservers();
//								}
//
//							} else {
//								int i = 0;
//								//Check if bytes to play are silent
//								while (getSampleMax(abData, i) == 0.0f && i<Constants.BYTES_TO_CHECK_FOR_SILENT) {
////									System.out.print(getSampleMax(abData, i));
//									i++;
////									System.out.print(abData[i] + ", ");
//								}
////								if (willAutoPause) System.out.println("Sentence: " + i);
//								System.out.println("Tal: " + getSampleMax(abData,abData.length - i - 4));
//								if (i>Constants.BYTES_TO_CHECK_FOR_SILENT - 1){ //Silent
//									silent = true;
//									isResponded = false;
//									setChanged();
//									notifyObservers();
////									System.out.println("Tyst? " + silent);
//
//								}
//
//							}
//							// Gets stuck in this loop if autopaused
//							while (isAutoPaused){
//								playing = false;
//								try {
//									Thread.sleep(500);
//								} catch (InterruptedException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
//							}
//
//
//							playing = true;
//						}
////						if (thisMode != Constants.s0) alterStereo(nBytesRead);
						if (nBytesRead >= 0)
							auline.write(abData, 0, nBytesRead);
					}
				} catch (IOException e) {
					e.printStackTrace();
					isError = true;
					setChanged();
					notifyObservers();
					return;
				} finally {
					oldGain = getCurrentLevel();
					auline.drain();
					auline.close();
				}
//			}while(loopContinuously);
			
	    	finished = true;
			setChanged();
			notifyObservers();
			
	    }
	
	    private float getSampleMax(byte[] buffer, int offset){
	    	if (bigEndian){
		    	float sampleLeft =
			    	(  (buffer[offset + 0] << 8)
			    	 | (buffer[offset + 1] & 0xFF)  )
			    	 / 32768.0F;
			    	float sampleRight = 
			    	(  (buffer[offset + 2] << 8)
			    	 | (buffer[offset + 3] & 0xFF)  )
			    	 / 32768.0F;
			    	return Math.max(Math.abs(sampleLeft), Math.abs(sampleRight));
	    	} else {
		    	float sampleLeft =
		    	(  (buffer[offset + 0] & 0xFF)
		    	 | (buffer[offset + 1] << 8)  )
		    	 / 32768.0F;
		    	float sampleRight = 
		    	(  (buffer[offset + 2] & 0xFF)
		    	 | (buffer[offset + 3] << 8)  )
		    	 / 32768.0F;
		    	return Math.max(Math.abs(sampleLeft), Math.abs(sampleRight));
	    	}
	    }
	    
	    /**
	     * Alters the balance between left and right channel.
	     */
	    private void setBalance(){
	    	switch (thisMode){
	    	case Constants.s0:
	    		balanceControl.setValue(0.0f);
	    		break;
	    	case Constants.sR:
	    		balanceControl.setValue(1.0f);
	    		break;
	    	case Constants.sL:
	    		balanceControl.setValue(-1.0f);
	    		break;
	    	}
	    }
	    
	    
	    /**
	     * Alters the buffer containing the sound to be written to the sound card.
	     * 
	     * @param nBytesRead is the amount of bytes that has been read into the 
	     * buffer.
	     */
	    private void alterStereo(int nBytesRead){
	    	int offset;
	    	offset = bigEndian ? 0 : 2;
	    	
	    	switch (thisMode){
	    	case Constants.s0:
	    		break;
	    		// ToDo change sign 
	    	case Constants.sPi:
		    	for (int i = 0; i < nBytesRead - 4; i = i + 4){
		    	abData[i + offset] = abData[i + offset];
		    	}
	    		break;
	    	case Constants.sL:
	    		for (int i = 0; i < nBytesRead - 4; i = i + 4){
	    			abData[i + 2] = 0;
	    			abData[i + 3] = 0;
	    		}
	    		break;
	    	case Constants.sR:
	    		for (int i = 0; i < nBytesRead - 4; i = i + 4){
	    			abData[i] = 0;
	    			abData[i + 1] = 0;
	    		}
	    		break;
	    	}	
	    }
	    

	    //-------------------------GAIN--------------------------------	    
	    /**
	     * Returns the gain of the sound player.
	     * 
	     * @return level is the current gain
	     */
	    public float getCurrentLevel() {
	    	return gainControl.getValue();
	    }

	    /**
	     * Sets the gain of the sound player. If level exceeds the minimum or
	     * maximum level the gain is not altered.
	     * 
	     * @param f is a float probably between -80 and 6 dB
	     */
	    public void setLevel(float f) {
	    	if (f <= gainControl.getMaximum() && f >= gainControl.getMinimum())
	    	gainControl.setValue(f);
	    }
	    
	    /**
	     * Sets the initial gain of the sound player.
	     * 
	     * @param f is a float probably between -80 and 6 dB
	     */
	    public void setInitGain(float f) {
	    	initGain = f;
	    }
	    
	    public float getResolutionLevel(){
	    	return gainControl.getPrecision();
	    }
	    public String getTheUnit(){
	    	return gainControl.getUnits();
	    }
	    public int getUpdateperiodMicroSec(){
	    	return gainControl.getUpdatePeriod();
	    }
	    //Ska vara private sen?
	    public float getMinLevel(){
	    	return gainControl.getMinimum();
	    }
	    public float getMaxLevel(){
	    	return gainControl.getMaximum();
	    }
	    
	    public boolean isPlaying(){
	    	return playing;
	    }

	    public boolean isPaused(){
	    	return isThreadPaused;
	    }
	    
	    public boolean isLoopContinuously(){
	    	return loopContinuously;
	    }
	    
	    public boolean isSilence(){
	    	return silent;
	    }
	    
	    public boolean hasRecievedResponse(){
	    	return isResponded;
	    }
	    
	    public void setObserved(boolean b){
	    	isObserved = b;
	    }
}

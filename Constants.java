package ciGUI;

public class Constants {
	public static final int MIN_SN_LEVEL = -15;
	public static final int MAX_SN_LEVEL = 20;
	public static final float INIT_SIGNAL_GAIN = -18.0f; //Initial gain of signal
	public static final int EXTERNAL_BUFFER_SIZE = 8192;
	public static final int BYTES_TO_CHECK_FOR_SILENT = 400;
	public static final int START_SN_INDEX = 14;
	public static final String DATE_FORMAT_NOW = "yyyy-MM-dd_HH-mm-ss";
	public static final String sln0 = "SLN0";
	public static final String srn0 = "SRN0";
	public static final String spin0 = "S700N0";
	public static final String s0n0 = "Binaural";//S0N0";
	public static final String slnl = "Left"; //SLNL";
	public static final String srnr = "Right"; //SRNR";
	public static final int s0 = 0;
	public static final int sL = 1;
	public static final int sR = 2;
	public static final int sPi = 3;
	
	public static final int OneUpOneDown2dB = 0;
	public static final int OneUpOneDown2dBbreak = 1; // Adaptive method 3 with break criteria
	public static final int OneUpOneDown5dB3dB1dB = 2; // Adaptive method 1.
	public static final int ConstantSignalToNoiseRatio = 3; //Constant metod.
	public static final String ADAPTIVE1 = "Adaptive 1-up-1-down 2dB 20 sent.";
	public static final String ADAPTIVE2 = "Adaptive 1-up-1-down 2dB break";
	public static final String ADAPTIVE3 = "Adaptive 1-up-1-down 5-3-1dB";
	public static final String CONSTANT = "Constant S/N";
	public static final int NR_OF_LISTS = 15; //
	public static final int NR_OF_SENTENCES = 20; // in each list
	
	public static final String THE_PATH = "Sound\\"; // Path to sounds and text files
	
	/*
     * State:
     * 0 = Playing, not waiting for response (Sentence)
     * 1 = Playing, waiting for response (Silence)
     * 2 = Playing, not waiting for response (Silence)
     * 3 = Autopaused, waiting for response
     * 4 = Paused by user
     * 5 = Stopped, closing soundplayer
     * 6 = Finished, signal file is ended
     * 7 = Failed to start file
     */
	public static final int PLAYING_SENTENCE = 0;
	public static final int PLAYING_SILENCE_WAIT_RESPONSE = 1;
	public static final int PLAYING_SILENCE_NOT_WAITING = 2;
	public static final int AUTOPAUSED = 3;
	public static final int PAUSED = 4;
	public static final int STOPPED = 5;
	public static final int FINISHED = 6;
	public static final int START_ERROR = 7;
}

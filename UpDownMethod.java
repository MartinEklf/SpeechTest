package ciGUI;

import java.text.DecimalFormat;


public class UpDownMethod {
	private int adaptiveMethod = 0;
	private int testNumber = -1;
        private int maxNrOfTests = 0;
	private int nrOfTurningPoints = 0;
	private int nrOfLastCorrectsInRow = 1;
	private int nrOfLastIncorrectsInRow = 1;
	private int nrOfCorrect = 0;
	private float stepSize = 0;
	private boolean[] responses;
	private float[] levels;
	private float[] turningPointLevels;
	private float average = 0;
	private float deviation = 0;
	private boolean step = false;
	
	public UpDownMethod(int arg, int maxNrOfResponses){
		adaptiveMethod = arg;
		switch (adaptiveMethod){
			case Constants.OneUpOneDown5dB3dB1dB:
				stepSize = 5;
				step = true;
				break;
			case Constants.OneUpOneDown2dB:
				stepSize = 2;
				step = true;
				break;
			case Constants.OneUpOneDown2dBbreak:
				stepSize = 2;
				step = true;
				break;
			case Constants.ConstantSignalToNoiseRatio:
				stepSize = 0;
				step = false;
				break;
		}
                maxNrOfTests = maxNrOfResponses;
		responses = new boolean[maxNrOfResponses];
		levels = new float[maxNrOfResponses];
		turningPointLevels = new float[maxNrOfResponses];
	}
	
	public void setNewResponse(boolean correct, float level){
		if (testNumber <= maxNrOfTests){
                testNumber++;
		responses[testNumber] = correct;
		levels[testNumber] = level;
		calculate();
                }
	}
	
	public float getStep(){
		return stepSize;
	}
	
	public float getAverage(){
		return average;
	}
	
	public float getDeviation(){
		return deviation;
	}
	
	public int getNrOfTurningPoints(){
		return nrOfTurningPoints;
	}
	
	public int getTestNumber(){
		return testNumber + 1;
	}
	
	public boolean shouldBreak(){
		if (adaptiveMethod == Constants.OneUpOneDown2dBbreak){
			if (testNumber >= 13 && deviation <= 1.5){
				return true;
			} else {
				return false;
			}
		}
		return false;
	}
	
	
	public String getResult(){
		String returnString = null;
		switch (adaptiveMethod){
		case (Constants.OneUpOneDown5dB3dB1dB):
			returnString = "Average S/N: " + dec1(average) + "dB, Deviation: " 
			+ dec1(deviation);
			break;
		case (Constants.OneUpOneDown2dB):
			returnString = "Average S/N: " + dec1(average) + "dB, TurningPoints: " 
			+ nrOfTurningPoints +  ", Deviation: " + dec1(deviation);
			break;
		case (Constants.OneUpOneDown2dBbreak):
			returnString = "Average S/N: " + dec1(average) + "dB, TurningPoints: " 
			+ nrOfTurningPoints +  ", Deviation: " + dec1(deviation);
		break;			
		case (Constants.ConstantSignalToNoiseRatio):
			returnString = "Percent correct: " + nrOfTurningPoints + "%";//, S/N: "
//			+ average + "dB";
			break;
		}
		return returnString;
	}
	
	private String dec1(float f){
		DecimalFormat df = new DecimalFormat("##.#");
		return df.format(f);
	}
	
	public void calculate(){
		
		if (adaptiveMethod == Constants.OneUpOneDown5dB3dB1dB){
			step = true;
			if (responses[testNumber]){ //Correct response
				if (nrOfLastCorrectsInRow == 0){ // Turning point
					if (stepSize > 1) stepSize = stepSize - 2;
					turningPointLevels[nrOfTurningPoints] = levels[testNumber];
					nrOfTurningPoints++;
				}
				nrOfLastCorrectsInRow++;
				nrOfLastIncorrectsInRow = 0;
			} else { // InCorrect response
				if (nrOfLastIncorrectsInRow == 0){ // Turning point
					if (stepSize > 1) stepSize = stepSize - 2;
					turningPointLevels[nrOfTurningPoints] = levels[testNumber];
					nrOfTurningPoints++;
				}
				nrOfLastIncorrectsInRow++;
				nrOfLastCorrectsInRow = 0;
			}
			if (nrOfTurningPoints > 0){
				average = 0;
				for (int i = 0; i < nrOfTurningPoints; i++){
					average = average + turningPointLevels[i];
				}
				average = average / nrOfTurningPoints;
				for (int i = 0; i < nrOfTurningPoints; i++){
					deviation = deviation + Math.abs(turningPointLevels[i] - average);
				}
				deviation = deviation / nrOfTurningPoints;
			}
		}
		if (adaptiveMethod == Constants.OneUpOneDown2dB || 
				adaptiveMethod == Constants.OneUpOneDown2dBbreak){
		step = true;
		float nextLevel = 0;
		if (responses[testNumber]){ //Correct response
			if (nrOfLastCorrectsInRow == 0 && testNumber >= 4){ // Turning point
				turningPointLevels[nrOfTurningPoints] = levels[testNumber];
				nrOfTurningPoints++;
			}
			nextLevel = levels[testNumber] - stepSize;
			nrOfLastCorrectsInRow++;
			nrOfLastIncorrectsInRow = 0;
		} else { // InCorrect response
			if (nrOfLastIncorrectsInRow == 0 && testNumber >= 4){ // Turning point
				turningPointLevels[nrOfTurningPoints] = levels[testNumber];
				nrOfTurningPoints++;
			}
			nextLevel = levels[testNumber] + stepSize;
			nrOfLastIncorrectsInRow++;
			nrOfLastCorrectsInRow = 0;
		}
		if (testNumber >= 4){
			int nr = 0;
			average = 0;
			for (int i = 4; i <= testNumber; i++){
				average = average + levels[i];
				nr++;
			}
			
			average = average + nextLevel;
			nr++;
			average = average / nr;
			
			nr = 0;
			for (int i = 4; i <= testNumber; i++){
				deviation = deviation + Math.abs(levels[i] - average);
				nr++;
			}
			deviation = deviation + Math.abs(nextLevel - average);
			nr++;
			deviation = deviation / nr;
		}
		}
			
		if (adaptiveMethod == Constants.ConstantSignalToNoiseRatio){
			if (responses[testNumber]){ //Correct response
				nrOfCorrect++;
                        }
			nrOfTurningPoints = Math.round((nrOfCorrect * 100) / (testNumber + 1));
			average = levels[0];		
		}
	}
}
package util;

import java.util.HashMap;
import java.util.Map;

public class GlobalAttributes {

	public static Map<Character, Integer> idMap = new HashMap<Character, Integer>();
	public static int instructionNumber = 0;
	public static boolean braceEncounteredPreviously = false;

	public static void incrementInstructionNumberBy(int k) {
		instructionNumber += k;
	}
	
	public static int getInstructionNumber() {
		return instructionNumber;
	}
	
	public static Map<Character, Integer> getIdMap() {
		return idMap;
	}
}
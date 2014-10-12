package fpl;

import java.util.HashMap;
import java.util.Map;

public class GlobalAttributes {

	public static Map<Character, Integer> idMap = new HashMap<Character, Integer>();
	public static int instructionNumber = 0;

	public static void incrementInstructionNumberBy(int k) {
		instructionNumber += k;
	}
	
	public static int getInstructionNumber() {
		return instructionNumber;
	}
	
	public static Map<Character, Integer> getIdMap() {
		return idMap;
	}
	

	public static boolean isRelationalOperator(int token) {
		return (token == Token.LESSER_OP || token == Token.GREATER_OP || token == Token.NOT_EQ || token == Token.ASSIGN_OP);
	}
}

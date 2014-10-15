import java.util.HashMap;
import java.util.Map;

import stacks.Stacks;
import util.GlobalAttributes;

class Code {

	static int counter = 0;
	static Map<Integer, String> codeList = new HashMap<Integer, String>();

	public static String generateCodeForLiteral(int literal) {
		String codeString = "";
		if (literal < 6) {
			codeString = GlobalAttributes.getInstructionNumber() + ": iconst_" + literal;
			GlobalAttributes.incrementInstructionNumberBy(1);
		} else if (literal >= 6 && literal < 128) {
			codeString = GlobalAttributes.getInstructionNumber() + ": bipush " + literal;
			GlobalAttributes.incrementInstructionNumberBy(2);
		} else {
			codeString = GlobalAttributes.getInstructionNumber() + ": sipush " + literal;
			GlobalAttributes.incrementInstructionNumberBy(3);
		}
		return codeString;
	}

	public static String generateCodeForOperator(int opToken) {
		String codeString = "";
		if (opToken == Token.ADD_OP) {
			codeString = GlobalAttributes.getInstructionNumber() + ": iadd";
		} else if (opToken == Token.SUB_OP) {
			codeString = GlobalAttributes.getInstructionNumber() + ": isub";
		} else if (opToken == Token.MULT_OP) {
			codeString = GlobalAttributes.getInstructionNumber() + ": imul";
		} else if (opToken == Token.DIV_OP) {
			codeString = GlobalAttributes.getInstructionNumber() + ": idiv";
		}
		GlobalAttributes.incrementInstructionNumberBy(1);
		return codeString;
	}

	public static String generateCodeForLoading(int id) {
		String code = GlobalAttributes.getInstructionNumber() + ": iload_" + id;
		GlobalAttributes.incrementInstructionNumberBy(1);
		return code;
	}

	public static String generateCodeForStoring(Integer id) {
		String code = GlobalAttributes.getInstructionNumber() + ": istore_" + id;
		GlobalAttributes.incrementInstructionNumberBy(1);
		return code;
	}

	public static void addToCodeList(String codeString) {
		codeList.put(counter++, new String(codeString));
	}
	
	public static void addAllToCodeList(Map<Integer, String> newCodeList) {
		for (int i: newCodeList.keySet()) {
			codeList.put(counter++, newCodeList.get(i));
		}
	}

	public static String output() {
		String outputString = "";
		for (int i=0; i < codeList.size(); i++) {
			String codeString = codeList.get(i);
			outputString += codeString + "\n";
		}
		return outputString;
	}

	public static Map<Integer, String> generateCodeFromByteCodeStack() {
		int counter=0;
		Map<Integer, String> newCodeList = new HashMap<Integer, String>();
		for (int i = 0; i <= Stacks.byteCodeStack.getStackPointerPosition(); i++) {
			String term = (String) Stacks.byteCodeStack.getElementAtPosition(i);
			if (term != null && term.length() > 0) {
				if (Character.isDigit(term.charAt(0))) {
					newCodeList.put(counter++, (generateCodeForLiteral(Integer.parseInt(String.valueOf(term)))));
				} else if (Character.isAlphabetic(term.charAt(0))) {
					int id = GlobalAttributes.getIdMap().get(term.charAt(0));
					newCodeList.put(counter++, generateCodeForLoading(id));
				} else if (term.charAt(0) == '+' || term.charAt(0) == '-' || term.charAt(0) == '*' || term.charAt(0) == '/') {
					int opCode = 0;
					switch (term.charAt(0)) {
					case '+':
						opCode = Token.ADD_OP;
						break;
					case '-':
						opCode = Token.SUB_OP;
						break;
					case '*':
						opCode = Token.MULT_OP;
						break;
					case '/':
						opCode = Token.DIV_OP;
						break;
					}
					newCodeList.put(counter++, generateCodeForOperator(opCode));
				} else {
					System.err.println("Unexpected token in bytecodestack: " + term);
				}
			}
		}
		return newCodeList;
	}

	public static String generateCodeForEnd() {
		return GlobalAttributes.getInstructionNumber() + ": return";
	}

	public static String generateCodeForRelationalOp(char nextChar) {
		String code = null;
		if (nextChar == Token.toString(Token.LESSER_OP).charAt(0)) {
			code = GlobalAttributes.getInstructionNumber() + ": if_icmpge";
		} else if (nextChar == Token.toString(Token.NOT_EQ).charAt(0)) {
			code = GlobalAttributes.getInstructionNumber() + ": if_icmpeq";
		} else if (nextChar == Token.toString(Token.GREATER_OP).charAt(0)) {
			code = GlobalAttributes.getInstructionNumber() + ": if_icmple";
		} else if (nextChar == Token.toString(Token.ASSIGN_OP).charAt(0)) {
			code = GlobalAttributes.getInstructionNumber() + ": if_icmpne";
		}
		GlobalAttributes.incrementInstructionNumberBy(3);
		return code;
	}

	public static String generateCodeForGoto(int gotoInstructionNumber) {
		String code = GlobalAttributes.getInstructionNumber() + ": goto";
		if (gotoInstructionNumber != -1) {
			code += " " + gotoInstructionNumber;
		}
		GlobalAttributes.incrementInstructionNumberBy(3);
		return code;
	}
	
	public static String generateCodeForGoto() {
		return generateCodeForGoto(-1);
	}
}
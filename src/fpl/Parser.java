package fpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stacks.ByteCodeStack;
import stacks.OperatorStack;
import stacks.Stack;
import stacks.TermStack;
import dao.CompoundExpression;

public class Parser {
	public static void main(String[] args) {
		System.out.println("Enter program and terminate with 'end'!\n");
		Lexer.lex();

		new Program();

		System.out.println("\nByte code:");
		Code.output();

		System.out.println("\nStack:");
		while (!Stacks.byteCodeStack.isEmpty()) {
			System.out.println(Stacks.byteCodeStack.pop());
		}

	}
}

class IdMap {
	public static Map<Character, Integer> idMap = new HashMap<Character, Integer>();

	public static Map<Character, Integer> getIdMap() {
		return idMap;
	}
}

class Stacks {
	public static Stack termStack = new TermStack();
	public static Stack operatorStack = new OperatorStack();
	public static Stack byteCodeStack = new ByteCodeStack();
}

class Program {
	public Program() {
		new Decls();
		new Stmts();
	}
}

class Decls {

	public Decls() {
		new Idlist();
	}

}

class Idlist {

	Map<Character, Integer> idMap = IdMap.getIdMap();

	public Idlist() {

		int lexResult = 0;
		int count = 0;
		idMap = new HashMap<Character, Integer>();
		do {
			lexResult = Lexer.lex();
			if (lexResult == Token.ID) {
				idMap.put(Lexer.ident, count++);
			}
		} while (lexResult != Token.SEMICOLON);
	}
}

class Stmts {
	public Stmts() {
		int lexResult = 0;
		while (true) {
			lexResult = Lexer.lex();
			if (lexResult != Token.KEY_END && lexResult != Token.RIGHT_PAREN) {
				new Stmt(lexResult);
			} else {
				break;
			}
		}
	}
}

class Stmt {
	public Stmt(int lexResult) {
		switch (lexResult) {
		case Token.KEY_WHILE:
			new Loop();
			break;
		case Token.KEY_IF:
			new Cond();
			break;
		case Token.ID:
			new Assign();
			break;
		case Token.SEMICOLON:
			break;
		default:
			System.err.println("UNKNOWN CASE? Lex ID: " + lexResult);
		}
	}
}

/* x = 12 + 3 * y; */
class Assign {
	public Assign() {
		Map<Character, Integer> idMap = IdMap.getIdMap();
		char assignmentDestination = Lexer.ident;

		/* Skip over the = symbol */
		Lexer.lex();
		Lexer.lex();

		new Expr();

		/* Now store */
		if (idMap.containsKey(assignmentDestination)) {
			Code.generateCodeForStoring(idMap.get(assignmentDestination));
		}
	}
}

class Expr {

	/* expr -> term [ (+ | -) expr ] */
	public Expr() {

		boolean rightParenthesisEncountered = false, semicolonEncountered = false;
		
		if (Lexer.nextToken == Token.LEFT_PAREN) {
			/* If the very first character is an opening parenthesis '(' */
			Stacks.termStack.push(Lexer.nextChar);
			Lexer.lex();
		}
		
		/* Check for exit condition */
		if (Lexer.nextToken != Token.RIGHT_PAREN && Lexer.nextToken != Token.SEMICOLON) {
			/* No end condition met. Check for add or sub symbols */
			new Term();

			if (Lexer.nextToken == Token.ADD_OP || Lexer.nextToken == Token.SUB_OP) {
				Stacks.operatorStack.push(Lexer.nextChar);

				/* Move to the next term and call Expr */
				Lexer.lex();
				new Expr();
			} else if (Lexer.nextToken == Token.SEMICOLON) {
				semicolonEncountered = true;
			} else if (Lexer.nextToken == Token.RIGHT_PAREN) {
				rightParenthesisEncountered = true;
			} else {
				System.err.println("This shouldn't happen.");				
			}
		} else if (Lexer.nextToken == Token.RIGHT_PAREN) {
			rightParenthesisEncountered = true;
			System.err.println("I thought it'll never come here for a right parenthesis. But it's ok.");
		} else if (Lexer.nextToken == Token.SEMICOLON) {
			semicolonEncountered = true;
			System.err.println("I thought it'll never come here for a semicolon. But it's ok.");
		}
		
		if (rightParenthesisEncountered) {
			/* Exit condition. Pop stuff until you reach the left parenthesis */
			CompoundExpression rightOperand = (CompoundExpression) Stacks.termStack.pop();

			/* Next pop should be the left parenthesis '(' */
			Stacks.termStack.pop();
			/*- And the next should be the operand t1 in the expression t1 + (t2) */
			Object leftOperand = Stacks.termStack.pop();

			/* Pop the operator */
			char operator = (Character) Stacks.operatorStack.pop();

			/*- Make a new node t3 that holds t1 + (t2) as a compound expression */
			CompoundExpression newNode = new CompoundExpression();
			newNode.setLeftOperand(leftOperand);
			newNode.setRightOperand(rightOperand);
			newNode.setOperator(operator);
			Stacks.termStack.push(newNode);

			/* Push onto byte code stack */
			if (!(leftOperand instanceof CompoundExpression))
				Stacks.byteCodeStack.push(leftOperand);
			Stacks.byteCodeStack.push(operator);
		} else if (semicolonEncountered) {
			/* Exit condition.
			 * If there's any operators left in the stack, we need to deal with them */
			if (!Stacks.operatorStack.isEmpty()) {
				char operator = (Character) Stacks.operatorStack.pop();
				Object rightOperand = Stacks.termStack.pop();
				Object leftOperand = Stacks.termStack.pop();
				
				/*- Make a new node t3 that holds t1 + (t2) as a compound expression */
				CompoundExpression newNode = new CompoundExpression();
				newNode.setLeftOperand(leftOperand);
				newNode.setRightOperand(rightOperand);
				newNode.setOperator(operator);
				Stacks.termStack.push(newNode);
				
				/* Push onto byte code stack */
				if (!(rightOperand instanceof CompoundExpression))
					Stacks.byteCodeStack.push(rightOperand);
				if (!(leftOperand instanceof CompoundExpression))
					Stacks.byteCodeStack.push(leftOperand);
				Stacks.byteCodeStack.push(operator);
			}
		}
	}
}

class Term {

	/* term -> factor [ (* | /) term ] */
	public Term() {
		new Factor();

		Lexer.lex();

		if (Lexer.nextToken != Token.ADD_OP && Lexer.nextToken != Token.SUB_OP) {
			/* No end condition met. Check for add or sub symbols */

			if (Lexer.nextToken == Token.MULT_OP || Lexer.nextToken == Token.DIV_OP) {
				/* Push into the op-stack */
				Stacks.operatorStack.push(Lexer.nextChar);

				/* Move forward */
				Lexer.lex();

				/* Call Term */
				new Term();
			}
		} else {
			/* Exit condition */
		}
	}
}

class Factor {

	/* factor -> int_lit | id | ‘(‘ expr ‘)’ */
	public Factor() {

		if (Lexer.nextToken == Token.INT_LIT || Lexer.nextToken == Token.ID) {

			/*- What's the current object on top of the term stack?
			 *  We need to check if it is an opening parenthesis '(' */
			Object lastTermStackTerm = Stacks.termStack.getTop();
			char lastStackToken = ' ';
			if (lastTermStackTerm instanceof Character) {
				lastStackToken = (Character) lastTermStackTerm;
			}
			
			/* Push the value found */
			char valueToPush = ' ';
			if (Lexer.nextToken == Token.INT_LIT) {
				valueToPush = Integer.toString(Lexer.intValue).charAt(0);
			} else if (Lexer.nextToken == Token.ID) {
				valueToPush = Lexer.ident;
			} else {
				System.err.println("Error: Shouldn't be happening");
			}
			Stacks.termStack.push(valueToPush);


			if (!Stacks.operatorStack.isEmpty() && lastStackToken != Token.toString(Token.LEFT_PAREN).charAt(0)) {
				/* Pop, and then push as a compound expression */
				Object rightOperand = Stacks.termStack.pop();
				char operator = (Character) Stacks.operatorStack.pop();
				Object leftOperand = Stacks.termStack.pop();

				CompoundExpression comp = new CompoundExpression();
				comp.setLeftOperand(leftOperand);
				comp.setRightOperand(rightOperand);
				comp.setOperator(operator);
				Stacks.termStack.push(comp);

				/* Put into the bytecode stack as well */
				if (!(leftOperand instanceof CompoundExpression))
					Stacks.byteCodeStack.push(leftOperand);
				Stacks.byteCodeStack.push(valueToPush);
				Stacks.byteCodeStack.push(operator);
			}

		} else if (Lexer.nextToken == Token.LEFT_PAREN) {
			new Expr();
		}
	}
}

class Cond {

}

class Loop {

}

class Cmpdstmt {

}

class Rexpr {

}

class Code {

	static List<String> codeList = new ArrayList<String>();

	public static String generateCodeForLiteral(int literal) {
		String codeString = "";
		if (literal < 6) {
			codeString = "iconst" + literal;
		} else if (literal >= 6 && literal < 128) {
			codeString = "bipush" + literal;
		} else {
			codeString = "sipush" + literal;
		}
		return codeString;
	}

	public static String generateCodeForStoring(Integer id) {
		return "istore_" + id;
	}

	public static String generateCodeForOperator(int opToken) {
		String codeString = "";
		if (opToken == Token.ADD_OP) {
			codeString = "iadd";
		} else if (opToken == Token.SUB_OP) {
			codeString = "isub";
		} else if (opToken == Token.MULT_OP) {
			codeString = "imul";
		} else if (opToken == Token.DIV_OP) {
			codeString = "idiv";
		}
		return codeString;
	}

	public static String generateCodeForLoading(int id) {
		return "iload_" + id;
	}

	public static void addToCodeList(String codeString) {
		codeList.add(new String(codeString));
	}

	public static void output() {
		for (String codeString : codeList) {
			System.out.println(codeString);
		}

	}
}
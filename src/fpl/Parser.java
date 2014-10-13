package fpl;

import java.util.HashMap;
import java.util.Map;

import stacks.ByteCodeStack;
import stacks.CompoundStatementStack;
import stacks.OperatorStack;
import stacks.Stack;
import stacks.TermStack;

public class Parser {
	public static void main(String[] args) {
		System.out.println("Enter program and terminate with 'end'!\n");
		Lexer.lex();

		new Program();

		System.out.println("\nByte code:");
		Code.output();

		// System.out.println("\nStack:");
		// while (!Stacks.byteCodeStack.isEmpty()) {
		// System.out.println(Stacks.byteCodeStack.pop());
		// }

	}
}

class Stacks {
	public static Stack termStack = new TermStack();
	public static Stack operatorStack = new OperatorStack();
	public static Stack byteCodeStack = new ByteCodeStack();
	public static Stack cmpdStmtStack = new CompoundStatementStack();

	public static void clearStacks() {
		termStack = new TermStack();
		operatorStack = new OperatorStack();
		byteCodeStack = new ByteCodeStack();
	}
}

class Program {
	public Program() {
		new Decls();
		new Stmts();
		Code.addToCodeList(Code.generateCodeForEnd());
	}
}

class Decls {

	public Decls() {
		new Idlist();
	}
}

class Idlist {

	public Idlist() {

		int lexResult = 0;
		int count = 0;
		GlobalAttributes.idMap = new HashMap<Character, Integer>();
		do {
			lexResult = Lexer.lex();
			if (lexResult == Token.ID) {
				GlobalAttributes.idMap.put(Lexer.ident, count++);
			}
		} while (lexResult != Token.SEMICOLON);
	}
}

class Stmts {
	public Stmts() {
		int lexResult = 0;
		while (true) {
			/* The program may have already reach it's end. */
			if (Lexer.nextToken == Token.KEY_END || GlobalAttributes.braceEncounteredPreviously) {
				lexResult = Lexer.nextToken;
				GlobalAttributes.braceEncounteredPreviously = false;
			} else {
				lexResult = Lexer.lex();
			}
			
			if (lexResult != Token.KEY_END && lexResult != Token.RIGHT_BRACE) {
				new Stmt(lexResult);
			} else if (lexResult == Token.RIGHT_BRACE) {
				break;
			} else if (lexResult == Token.KEY_END) {
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
		case Token.LEFT_BRACE:
			break;
		default:
			System.err.println("UNKNOWN CASE. Lex: " + Token.toString(lexResult));
		}
	}
}

/* x = 12 + 3 * y; */
class Assign {
	public Assign() {
		Map<Character, Integer> idMap = GlobalAttributes.getIdMap();
		char assignmentDestination = Lexer.ident;

		/* Skip over the = symbol */
		Lexer.lex();
		Lexer.lex();

		new Expr();
		Code.addAllToCodeList(Code.generateCodeFromByteCodeStack());
		Stacks.clearStacks();

		/* Now store */
		if (idMap.containsKey(assignmentDestination)) {
			Code.addToCodeList(Code.generateCodeForStoring(idMap.get(assignmentDestination)));
		}
	}
}

class Expr {

	/* expr -> term [ (+ | -) expr ] */
	public Expr() {

		boolean semicolonEncountered = false, relationalOpEncountered = false;

		if (Lexer.nextToken == Token.LEFT_PAREN) {
			/* If the very first character is an opening parenthesis '(' */
			Stacks.termStack.push(Lexer.nextChar);
			Lexer.lex();
		}

		/* Check for exit condition */
		if (Lexer.nextToken != Token.RIGHT_PAREN && Lexer.nextToken != Token.SEMICOLON && !GlobalAttributes.isRelationalOperator(Lexer.nextToken)) {
			/* No end condition met. Check for add or sub symbols */
			new Term();

			if (Lexer.nextToken == Token.ADD_OP || Lexer.nextToken == Token.SUB_OP) {
				Stacks.operatorStack.push(Lexer.nextChar);

				/* Move to the next term and call Expr */
				Lexer.lex();
				new Expr();
			} else if (Lexer.nextToken == Token.RIGHT_PAREN) {
				relationalOpEncountered = rightParenthesisEncountered();

				/* Move to the next term and call Expr */
				if (!relationalOpEncountered) {
					Lexer.lex();
					new Expr();
				}
			} else if (Lexer.nextToken == Token.SEMICOLON) {
				semicolonEncountered = true;
			} else if (GlobalAttributes.isRelationalOperator(Lexer.nextToken)) {
				relationalOpEncountered = true;
			} else {
				System.err.println("This shouldn't be coming after Term(): " + Token.toString(Lexer.nextToken));
			}
		} else if (Lexer.nextToken == Token.RIGHT_PAREN) {
			relationalOpEncountered = rightParenthesisEncountered();

			/* Move to the next term and call Expr */
			if (!relationalOpEncountered) {
				Lexer.lex();
				new Expr();
			}
		} else if (Lexer.nextToken == Token.SEMICOLON) {
			semicolonEncountered = true;
			System.err.println("I thought it'll never come here for a semicolon. But it's ok.");
		} else if (GlobalAttributes.isRelationalOperator(Lexer.nextToken)) {
			relationalOpEncountered = true;
			System.err.println("I thought it'll never come here for a relational operator. But it's ok.");
		} else {
			System.err.println("This shouldn't be coming after Term(): " + Lexer.nextToken);
		}

		if (semicolonEncountered || relationalOpEncountered) {
			/*
			 * Exit condition. If there's any operators left in the stack, we
			 * need to deal with them
			 */
			if (!Stacks.operatorStack.isEmpty()) {
				char operator = (Character) Stacks.operatorStack.pop();
				Object rightOperand = Stacks.termStack.pop();
				Object leftOperand = Stacks.termStack.pop();

				/*- Make a new node t3 that holds t1 + (t2) as a compound expression */
				CompoundExpression newNode = new CompoundExpression();
				newNode.setLeftOperand(leftOperand);
				newNode.setRightOperand(rightOperand);
				newNode.setOperator(String.valueOf(operator));
				Stacks.termStack.push(newNode);

				/* Push onto byte code stack */
				if (!(rightOperand instanceof CompoundExpression))
					Stacks.byteCodeStack.push(rightOperand);
				if (!(leftOperand instanceof CompoundExpression))
					Stacks.byteCodeStack.push(leftOperand);
				Stacks.byteCodeStack.push(operator);
			} else {
				/*- Operator stack is empty. But are there any operators on the term stack? */
				if (!Stacks.termStack.isEmpty()) {
					Object poppedTerm = Stacks.termStack.pop();
					if (!(poppedTerm instanceof CompoundExpression))
						Stacks.byteCodeStack.push(poppedTerm);
				}
			}

			if (relationalOpEncountered && Lexer.nextToken == Token.RIGHT_PAREN)
				Lexer.lex();
		}
	}

	public boolean rightParenthesisEncountered() {
		/* Exit condition. Pop stuff until you reach the left parenthesis */
		boolean relationalOpEncountered = false;

		/*- Next pop should be the left parenthesis '('. If it's not,
		 *  then this is the end of an IF or a WHILE condition */
		Object popped = Stacks.termStack.getElementAtPosition(Stacks.termStack.getStackPointerPosition() - 1);
		if (popped == null) {
			relationalOpEncountered = true;
		} else if (popped instanceof Character && !(((Character) popped) == Token.toString(Token.LEFT_PAREN).charAt(0))) {
			relationalOpEncountered = true;
		} else {
			Object rightOperand = Stacks.termStack.pop();
			/* Next pop WILL be the left parenthesis '(' */
			Stacks.termStack.pop();

			/*- Make a new node t3 that holds t1 + (t2) as a compound expression */
			CompoundExpression newNode = new CompoundExpression();

			/*- For handling expressions like t1 + (t2)
			 *  Next should be the operand t1 in the expression */
			Object leftOperand = Stacks.termStack.pop();
			if (leftOperand != null) {

				/*- Pop the operator */
				Object poppedOp = Stacks.operatorStack.pop();
				if (poppedOp != null) {
					char operator = (Character) poppedOp;
					newNode.setOperator(String.valueOf(operator));
					Stacks.byteCodeStack.push(operator);
				}
				/* Push onto byte code stack */
				newNode.setLeftOperand(leftOperand);
				newNode.setRightOperand(rightOperand);
				if (!(leftOperand instanceof CompoundExpression))
					Stacks.byteCodeStack.push(leftOperand);

			} else {
				newNode.setLeftOperand("(");
				newNode.setRightOperand(")");
				newNode.setOperator(String.valueOf(rightOperand));
			}

			Stacks.termStack.push(newNode);
		}
		return relationalOpEncountered;
	}
}

class Term {

	/* term -> factor [ (* | /) term ] */
	public Term() {
		new Factor();

		if (!GlobalAttributes.isArithmeticOperator(Lexer.nextToken))
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
			Object lastTermStackTerm = Stacks.termStack.getElementAtPosition(Stacks.termStack.getStackPointerPosition());
			char lastStackToken = ' ';
			if (lastTermStackTerm instanceof Character) {
				lastStackToken = (Character) lastTermStackTerm;
			}

			/* Push the value found */
			String valueToPush = null;
			if (Lexer.nextToken == Token.INT_LIT) {
				valueToPush = Integer.toString(Lexer.intValue);
			} else if (Lexer.nextToken == Token.ID) {
				valueToPush = String.valueOf(Lexer.ident);
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
				comp.setOperator(String.valueOf(operator));
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
	public Cond() {

		/* Skip over the opening parenthesis '(' */
		Lexer.lex();
		Lexer.lex();

		/* Process the condition */
		new Rexpr();

		/* The body */
		new Cmpdstmt();

		/* Skip over '}' */
		if (Lexer.nextToken == Token.RIGHT_BRACE) {
			Lexer.lex();
			GlobalAttributes.braceEncounteredPreviously = true;
			if (Lexer.nextToken == Token.KEY_ELSE) {
				Cmpdstmt.appendLineNumbers(true);

				/* Keep track of the line number of the goto */
				Stacks.cmpdStmtStack.push(GlobalAttributes.getInstructionNumber());
				Code.addToCodeList(Code.generateCodeForGoto());

				new Cmpdstmt();
				Cmpdstmt.appendLineNumbers(false);
			} else if (Lexer.nextToken == Token.RIGHT_BRACE) {
				Cmpdstmt.appendLineNumbers(false);
			} else {
				Cmpdstmt.appendLineNumbers(false);
			}
		} else {
			System.err.println("Why is this not a right brace after the IF/ELSE?");
		}
	}
}

class Loop {
	public Loop() {
		/* Skip over the opening parenthesis '(' */
		Lexer.lex();

		/* Keep track of the instruction number at the beginning of the loop */
		int loopHeadInstructionNumber = GlobalAttributes.getInstructionNumber();

		/* Process the condition */
		new Rexpr();

		/* The body */
		new Cmpdstmt();

		/* Skip over '}' */
		if (Lexer.nextToken == Token.RIGHT_BRACE || Lexer.nextToken == Token.KEY_END) {
			Cmpdstmt.appendLineNumbers(true);

			/* Keep track of the line number of the goto */
			Code.addToCodeList(Code.generateCodeForGoto(loopHeadInstructionNumber));
		} else {
			System.err.println("Why is this not a right brace after the LOOP?");
		}
	}
}

class Cmpdstmt {

	public Cmpdstmt() {

		/*- Skip over the opening braces '{' (if you need to) */
		if (Lexer.nextToken != Token.LEFT_BRACE) {
			Lexer.lex();
		}
		if (Lexer.nextToken == Token.LEFT_BRACE) {

			new Stmts();

			/* Skip over the opening braces '{' */
			if (Lexer.nextToken == Token.RIGHT_BRACE || Lexer.nextToken == Token.KEY_END) {
				/* Exit condition. Do nothing */

			} else {
				System.err.println("Problem in Cmpdstmt");
			}

		} else {
			System.err.println("Problem in Cmpdstmt");
		}
	}

	public static void appendLineNumbers(boolean followedByGoto) {
		/*- Now that the compound statment is over,
		    write the line number for the corresponding IF (or WHILE or GOTO?) */
		int currentLineNumber = GlobalAttributes.getInstructionNumber();
		if (followedByGoto)
			currentLineNumber += 3;

		Object popped = Stacks.cmpdStmtStack.pop();
		if (popped != null) {
			int lineNumOfTarget = (Integer) popped;
			/* Find the code corresponding to the line number */
			for (int i = 0; i < Code.codeList.size(); i++) {
				String code = Code.codeList.get(i);
				if (code.startsWith(Integer.toString(lineNumOfTarget) + ":")) {
					Code.codeList.put(i, code + " " + currentLineNumber);
					break;
				}
			}
		}
	}

	public static void appendLineNumbers() {
		appendLineNumbers(false);
	}
}

class Rexpr {

	public Rexpr() {

		/*--- Left-hand side of the relational-exp ---*/
		new Expr();
		/* Store the byte-code generated */
		Map<Integer, String> leftHandSide = Code.generateCodeFromByteCodeStack();
		Stacks.clearStacks();

		/*--- Relational operator ---*/
		char relationalOperator = ' ';
		if (GlobalAttributes.isRelationalOperator(Lexer.nextToken)) {
			relationalOperator = Lexer.nextChar;
			Lexer.lex();
		} else {
			System.err.println("Something went wrong with rexpr");
		}

		/*--- Right-hand side of the relational-exp ---*/
		new Expr();
		/* Store the byte-code generated */
		Map<Integer, String> rightHandSide = Code.generateCodeFromByteCodeStack();
		Stacks.clearStacks();

		/* Write down the byte code */
		Code.addAllToCodeList(leftHandSide);
		Code.addAllToCodeList(rightHandSide);

		/* Keep track of the line number of the if_cmp */
		Stacks.cmpdStmtStack.push(GlobalAttributes.getInstructionNumber());
		Code.addToCodeList(Code.generateCodeForRelationalOp(relationalOperator));
	}
}
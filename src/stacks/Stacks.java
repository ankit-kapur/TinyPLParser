package stacks;

import stacks.ByteCodeStack;
import stacks.CompoundStatementStack;
import stacks.OperatorStack;
import stacks.Stack;
import stacks.TermStack;

public class Stacks {
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

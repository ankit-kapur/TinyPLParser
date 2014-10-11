package stacks;

import java.util.HashMap;
import java.util.Map;

public class OperatorStack implements Stack {

	public Map<Integer, Character> opStack;
	public int stackPointer;

	public OperatorStack() {
		opStack = new HashMap<Integer, Character>();
		stackPointer = -1;
	}

	@Override
	public void push(Object s) {
		if (opStack != null) {
			opStack.put(++stackPointer, (Character) s);
		}
	}

	@Override
	public Object pop() {
		if (!isEmpty()) {
			return opStack.get(stackPointer--);
		} else {
			return null;
		}
	}

	@Override
	public boolean isEmpty() {
		return (stackPointer < 0 ? true : false);
	}

	@Override
	public Object getTop() {
		if (!isEmpty()) {
			return opStack.get(stackPointer);
		} else {
			return null;
		}
	}
}

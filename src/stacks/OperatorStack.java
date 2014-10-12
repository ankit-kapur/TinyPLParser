package stacks;

import java.util.HashMap;
import java.util.Map;

public class OperatorStack implements Stack {

	public Map<Integer, Character> stack;
	public int stackPointer;

	public OperatorStack() {
		stack = new HashMap<Integer, Character>();
		stackPointer = -1;
	}
	
	@Override
	public int getStackPointerPosition() {
		return stackPointer;
	}

	@Override
	public void push(Object s) {
		if (stack != null) {
			stack.put(++stackPointer, (Character) s);
		}
	}

	@Override
	public Object pop() {
		if (!isEmpty()) {
			return stack.get(stackPointer--);
		} else {
			return null;
		}
	}

	@Override
	public boolean isEmpty() {
		return (stackPointer < 0 ? true : false);
	}

	@Override
	public Object getElementAtPosition(int k) {
		if (!isEmpty() && k >= 0 && k <= stackPointer) {
			return stack.get(k);
		} else {
			return null;
		}
	}
}
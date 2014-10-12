package stacks;

import java.util.HashMap;
import java.util.Map;

public class CompoundStatementStack implements Stack {

	public Map<Integer, Integer> stack;
	public int stackPointer;

	public CompoundStatementStack() {
		stack = new HashMap<Integer, Integer>();
		stackPointer = -1;
	}
	
	@Override
	public int getStackPointerPosition() {
		return stackPointer;
	}

	@Override
	public void push(Object s) {
		if (stack != null) {
			stack.put(++stackPointer, (Integer) s);
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
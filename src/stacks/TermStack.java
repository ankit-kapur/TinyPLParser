package stacks;

import java.util.HashMap;
import java.util.Map;

public class TermStack implements Stack {
	public Map<Integer, Object> stack;
	public int stackPointer;

	public TermStack() {
		stack = new HashMap<Integer, Object>();
		stackPointer = -1;
	}

	@Override
	public int getStackPointerPosition() {
		return stackPointer;
	}
	
	@Override
	public void push(Object object) {
		if (stack != null) {
			stack.put(++stackPointer, object);
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
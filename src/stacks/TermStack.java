package stacks;

import java.util.HashMap;
import java.util.Map;

public class TermStack implements Stack {
	public Map<Integer, Object> opStack;
	public int stackPointer;

	public TermStack() {
		opStack = new HashMap<Integer, Object>();
		stackPointer = -1;
	}

	@Override
	public void push(Object object) {
		if (opStack != null) {
			opStack.put(++stackPointer, object);
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

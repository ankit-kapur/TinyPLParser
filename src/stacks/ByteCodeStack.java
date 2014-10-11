package stacks;

import java.util.HashMap;
import java.util.Map;

public class ByteCodeStack implements Stack {
	public Map<Integer, Character> codeStack;
	public int stackPointer;
	
	public ByteCodeStack() {
		codeStack = new HashMap<Integer, Character>();
		stackPointer = -1;
	}

	@Override
	public void push(Object object) {
		if (codeStack != null && object instanceof Character) {
			codeStack.put(++stackPointer, (Character) object);
		} else {
			System.err.println("Something's wrong in ByteCodeStack");
		}
	}

	@Override
	public Object pop() {
		if (!isEmpty()) {
			return codeStack.get(stackPointer--);
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
			return codeStack.get(stackPointer);
		} else {
			return null;
		}
	}
}

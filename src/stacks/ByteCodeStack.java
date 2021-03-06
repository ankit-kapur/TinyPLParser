package stacks;

import java.util.HashMap;
import java.util.Map;

public class ByteCodeStack implements Stack {
	public Map<Integer, String> stack;
	public int stackPointer;
	
	public ByteCodeStack() {
		stack = new HashMap<Integer, String>();
		stackPointer = -1;
	}
	
	@Override
	public int getStackPointerPosition() {
		return stackPointer;
	}

	@Override
	public void push(Object object) {
		String value = null;
		if (object instanceof Character) {
			value = String.valueOf(object);
		} else if (object instanceof String) {
			value = (String) object;
		} else {
			System.err.println("Something's wrong in ByteCodeStack");
		}
		
		if (stack != null) {
			stack.put(++stackPointer, value);
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

package stacks;


public interface Stack {

	public void push(Object s);
	public Object pop();
	public boolean isEmpty();
	public Object getElementAtPosition(int k);
	int getStackPointerPosition();
}
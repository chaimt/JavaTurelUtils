package com.turel.utils.random;

import java.util.Stack;

/**
 * This class will contain the stack of a single cycle and the number of time the cycle has been run
 * @author Chaimt
 *
 */
public class CycleStack<T> {
	private Stack<Stack<T>> stacks = new Stack<>();
	
	public CycleStack(){
		stacks.push(new Stack<T>());
		
	}

	private Stack<T> top(){
		return stacks.peek();
	}
	
	public Stack<T> getStack() {
		return top();
	}

	public int getCycleDepth() {
		return stacks.size();
	}

	public void push(T type) {
		top().push(type);
	}

	public void pop() {
		top().pop();
		if ((top().size() == 0) && (stacks.size()>1)) {
			stacks.pop();
		}
	}

	public T peek() {
		return top().peek();
	}

	public int indexOf(T type) {
		return top().indexOf(type);
	}

	public int size() {
		return top().size();
	}

	public Stack<T> newStack() {
		Stack<T> stack = new Stack<T>();
		stacks.push(stack);
		return stack;
	}

	
}

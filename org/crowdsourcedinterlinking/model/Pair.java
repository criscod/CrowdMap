package org.crowdsourcedinterlinking.model;

public class Pair {

	private Object left;
	private Object right;

	public Pair(Object left, Object right) {
		this.left = left;
		this.right = right;
	}

	public Object getLeft() {
		return left;
	}

	public Object getRight() {
		return right;
	}

	@Override
	public int hashCode() {
		return left.hashCode() * right.hashCode();
	}

}

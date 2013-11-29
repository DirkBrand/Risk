package risk.aiplayers.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BinaryTree {
	private class Node {
		Node left;
		Node right;
		int depth;
		int n;
		int m;

		public Node(int depth, int n, int m) {
			this.depth = depth;
			this.n = n;
			this.m = m;
		}
	}

	Node root;
	private List<boolean[]> allPerm = new ArrayList<boolean[]>();
	boolean[] baseArray;

	public BinaryTree(int n, int m) {
		root = new Node(0, n, m);
		baseArray = new boolean[n + m];
		generateTree(root);
		generatePermutations(root);
	}

	public void generateTree(Node r) {
		if (r.n > 0) {
			r.left = new Node(r.depth + 1, r.n - 1, r.m);
			generateTree(r.left);
		}

		if (r.m > 0) {
			r.right = new Node(r.depth + 1, r.n, r.m - 1);
			generateTree(r.right);
		}
	}

	public void generatePermutations(Node current) {
		if (current.left == null && current.right == null) {
			allPerm.add(Arrays.copyOf(baseArray, baseArray.length));
		}
		
		if (current.left != null) {
			baseArray[current.depth] = true;
			generatePermutations(current.left);
		} 
		
		if (current.right != null) {
			baseArray[current.depth] = false;
			generatePermutations(current.right);
		}
	}
	
	public List<boolean []> getPermutations() {
		return allPerm;
	}
	
}

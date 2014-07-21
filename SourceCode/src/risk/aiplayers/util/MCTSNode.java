package risk.aiplayers.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import risk.commonObjects.Territory;

public class MCTSNode extends GameTreeNode implements Cloneable {

	// For MCTS
	private List<MCTSNode> children;
	private MCTSNode parent;
	private int visitCount;
	private int winCount;

	private double randomNodeExpectedWinRate;

	public int depth;

	public int maxChildren;

	// Recruiting
	private Territory recruitedTer;
	public int  whereRecruitedId = -1;
	public double ave; // average ?
	public int numberOfRecruitBranches = 0;
	public ArrayList<MCTSNode> recruitChildren;

	// Attacking
	public boolean noAttackAdded = false;
	public ArrayList<MCTSNode> attackChildren;
	public int numberOfAttackBranches = 0;
	private int moveAfterAttackCount = -1;

	// Manoeuvering
	public boolean noManAdded = false;
	private Territory manSource;
	private Territory manDest;
	public int numberOfManoeuvreBranches = 0;
	public ArrayList<MCTSNode> manChildren;
	public ArrayList<Integer> manTroopBins; //Each element in this represent a possible manoeuvre. A to B with alpha troops.
	private LinkedList<LinkedList<Territory>> connComponentBuckets;

	public MCTSNode() {
		super();
	}

	// Clone method
	@Override
	public MCTSNode clone() {
		MCTSNode copy = (MCTSNode) super.clone();

		if (recruitedTer != null) {
			Territory temp = recruitedTer.clone();
			copy.setRecruitedTer(temp);
		}

		if (manSource != null) {
			Territory tempManS = manSource.clone();
			copy.setManSource(tempManS);
		}
		if (manDest != null) {
			Territory tempManD = manDest.clone();
			copy.setManDest(tempManD);
		}
		copy.whereRecruitedId = whereRecruitedId;
		copy.numberOfAttackBranches = 0;
		copy.numberOfManoeuvreBranches = 0;
		copy.maxChildren = 0;
		copy.numberOfRecruitBranches = 0;
		copy.recruitChildren = null;
		copy.attackChildren = null;
		copy.manChildren = null;
		copy.manTroopBins = null;
		copy.noManAdded = false;
		copy.noAttackAdded = false;

		return copy;
	}

	
	public MCTSNode getParent() {
		return parent;
	}

	public void setParent(MCTSNode parent) {
		this.parent = parent;
	}

	public List<MCTSNode> getChildren() {
		return children;
	}

	public void setChildren(List<MCTSNode> children) {
		this.children = children;
	}

	public int getNumberOfChildren() {
		if (children == null) {
			return 0;
		}
		return children.size();
	}

	public void addChild(MCTSNode child) {
		if (children == null) {
			children = new ArrayList<MCTSNode>();
		}
		children.add(child);
	}

	public void removeChildAt(int index) throws IndexOutOfBoundsException {
		children.remove(index);
	}

	public int getVisitCount() {
		return visitCount;
	}

	public void setVisitCount(int visitCount) {
		this.visitCount = visitCount;
	}

	public int getWinCount() {
		return winCount;
	}

	public void setWinCount(int winCount) {
		this.winCount = winCount;
	}

	public Territory getRecruitedTer() {
		return recruitedTer;
	}

	public void setRecruitedTer(Territory recruitedTer) {
		this.recruitedTer = recruitedTer;
	}

	public Territory getManSource() {
		return manSource;
	}

	public void setManSource(Territory manSource) {
		this.manSource = manSource;
	}

	public Territory getManDest() {
		return manDest;
	}

	public void setManDest(Territory manDest) {
		this.manDest = manDest;
	}

	public int arity() {
		return children == null ? 0 : children.size();
	}

	public double getRandomNodeExpectedWinRate() {
		return randomNodeExpectedWinRate;
	}

	public void setRandomNodeExpectedWinRate(double randomNodeExpectedWinRate) {
		this.randomNodeExpectedWinRate = randomNodeExpectedWinRate;
	}

	public LinkedList<LinkedList<Territory>> getConnComponentBuckets() {
		return connComponentBuckets;
	}

	public void setConnComponentBuckets(
			LinkedList<LinkedList<Territory>> connComponentBuckets) {
		this.connComponentBuckets = connComponentBuckets;
	}

	public int getMoveAfterAttackCount() {
		return moveAfterAttackCount;
	}

	public void setMoveAfterAttackCount(int moveAfterAttackCount) {
		this.moveAfterAttackCount = moveAfterAttackCount;
	}
	
	@Override
	public String toString() {
		return getManSource() + " - " + getManDest() + "";
	}
	}

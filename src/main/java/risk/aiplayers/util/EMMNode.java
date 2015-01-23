package risk.aiplayers.util;

import java.util.Comparator;

import risk.aiplayers.ExpectiminimaxPlayer;
import risk.commonObjects.Territory;

public class EMMNode extends GameTreeNode implements Cloneable {
	private Territory recruitedTer;
	private double value;

	// RSK20150115: Suspect this is not used because we only use AIParameter's static fields and methods:
	// private static AIParameter params = new AIParameter();

	public static Comparator<EMMNode> EMMNodeComparator = new Comparator<EMMNode>() {
		@Override
		public int compare(EMMNode o1, EMMNode o2) {
			double val1 = o1.getValue();
			double val2 = o2.getValue();
			if (val1 < val2) {
				return -1;
			} else if (val1 > val2) {
				return 1;
			} else {
				return 0;
			}
		}
	};

	public EMMNode() {
		super();
	}

	@Override
	public EMMNode clone() {
		EMMNode copy = (EMMNode) super.clone();
		if (recruitedTer != null)
			copy.setRecruitedTer(recruitedTer.clone());
		copy.setValue(0);
		return copy;
	}
	
	public EMMNode makeAttackChildNode(Territory src, Territory dest, boolean setval, ExpectiminimaxPlayer p) {
		EMMNode tempNode = (EMMNode) super.makeAttackChildNode(src, dest);
		if (setval) {
			tempNode.setValue(p.getWeightedEval(tempNode));
		}
		return tempNode;
	}
	
	public EMMNode makeNoAttackChildNode(boolean setval, ExpectiminimaxPlayer p) {
		EMMNode noAttackNode = (EMMNode) super.makeNoAttackChildNode(false);
		if (setval) {
			noAttackNode.setValue(p.getValue(noAttackNode));
		}
		return noAttackNode;
	}
	
	@Override
	public String toString() {
		return getValue() + "";
	}

	public Territory getRecruitedTer() {
		return recruitedTer;
	}

	public void setRecruitedTer(Territory recruitedTer) {
		this.recruitedTer = recruitedTer;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
	
}

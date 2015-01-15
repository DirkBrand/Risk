package risk.aiplayers.util;

import java.util.Comparator;

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

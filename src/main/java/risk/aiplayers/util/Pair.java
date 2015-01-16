package risk.aiplayers.util;

/**
 * Elements in the Hashmap
 * Double : Node values.
 * Boolean : presence as a child node.
 * @author glebris
 *
 */
public class Pair {
	Double value = null;
	boolean presence = false;

	public Pair(Double value, Boolean presence) {
		this.value = value;
		this.presence = presence;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public Boolean isPresent() {
		return presence;
	}

	public void setPresence() {
		this.presence = true;
	}

}

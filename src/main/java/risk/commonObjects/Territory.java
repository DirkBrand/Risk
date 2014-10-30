package risk.commonObjects;

import java.lang.management.MemoryUsage;
import java.util.Arrays;

public class Territory implements Cloneable, Comparable<Territory> {
	private String name;
	private String continent;
	private int id;
	private int continentID;

	private Territory[] neighbours;

	private int x;
	private int y;

	private int nrTroops;

	public int connectedRegion;
		
	public int depth;

	public Territory(int id, String name, int contID, String cont, int x,
			int y, int nrTroops) {
		this.setId(id);
		this.setName(name);
		this.setContinent(cont);
		this.setCoordinates(x, y);
		this.setContinentID(contID);
		this.nrTroops = nrTroops;
	}
	
	
	@Override 
	public boolean equals(Object ter) {
		if (this == null && ter == null) return true;
		if (this == null || ter == null) return false;
		
		Territory t = (Territory) ter;
		if (this.nrTroops == t.getNrTroops() && this.getName().equals(t.getName()) && Arrays.deepEquals(this.getNeighbours(), t.getNeighbours())) {
			return true;
		}
		return false;
	}
	
	public Territory (String name) {
		this.setName(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContinent() {
		return continent;
	}

	public void setContinent(String continent) {
		this.continent = continent;
	}

	public void setCoordinates(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getXCoordinate() {
		return x;
	}

	public int getYCoordinate() {
		return y;
	}

	public int getContinentNum() {
		return continentID;
	}

	public void setContinentID(int continentNum) {
		this.continentID = continentNum;
	}

	public Territory[] getNeighbours() {
		return neighbours;
	}

	public void setNeighbours(Territory[] neighbours) {
		this.neighbours = Arrays.copyOf(neighbours, neighbours.length);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getNrTroops() {
		return nrTroops;
	}

	public void setNrTroops(int nrTroops) {
		this.nrTroops = nrTroops;
	}

	public void decrementTroops() {
		this.nrTroops--;
	}

	public void incrementTroops() {
		this.nrTroops++;
	}

	@Override
	public Territory clone() {
		try {
			Territory copy = (Territory) super.clone();
			copy.setNeighbours(Arrays.copyOf(neighbours, neighbours.length));
			return copy;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int compareTo(Territory o) {
		return this.getName().compareTo(o.getName());
	}
	
	@Override
	public String toString() {
		return getName() + " - "  + getNrTroops();
	}
}

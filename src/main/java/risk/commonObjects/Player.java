package risk.commonObjects;

import java.util.HashMap;
import java.util.Iterator;

public class Player implements Cloneable {

	private int id;
	private String name;
	
	private String ipAddress;
	private int port;
	
	private HashMap<String, Territory> territories = new HashMap<String,Territory>();
	
	public Player (int id, String name) {
		this.setId(id);
		this.setName(name);
	}
	
	public Player (int id, String name, HashMap<String,Territory> ters) {
		this.setId(id);
		this.setName(name);
		this.territories = ters;
	}
	
	public Player (int id, String name, String ip, int port) {
		this.setId(id);
		this.setName(name);
		this.setIpAddress(ip);
		this.setPort(port);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public HashMap<String,Territory> getTerritories() {
		return territories;
	}

	public void setTerritories(HashMap<String,Territory> territories) {
		this.territories = territories;
	}

	public Territory getTerritoryByID(int id) {
		Iterator<Territory> it =  getTerritories().values().iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			if (t.getId() == id) return t;
		}
		return null;
	}

	public Territory getTerritoryByName(String name) {
		return territories.get(name);
	}
	
	@Override
	public Player clone() {
		Player copy;
		try {
			copy = (Player) super.clone();
			HashMap<String,Territory> copyT = new HashMap<String,Territory>();
			Iterator<Territory> it =  territories.values().iterator();
			while (it.hasNext()) {
				Territory t = it.next();
				copyT.put(t.getName(),t.clone());
			}
			copy.setTerritories(copyT);
			return copy;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}

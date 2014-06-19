package risk.commonObjects;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import risk.aiplayers.AIPlayer;

public class GameState extends AbstractGameState implements Cloneable {

	public final static int SETUP = 0;
	public final static int RECRUIT = 1;
	public final static int BATTLE = 2;
	public final static int MANOEUVRE = 3;
	
	
	public GameState() {
		super();
	}
	

	public GameState(String mapLoc, LinkedList<Player> players,
			int phase, HashMap<String,Territory> play1Ter,
			HashMap<String,Territory> play2Ter, int current) {
		this.setMapLocation(mapLoc);
		this.setMapName(mapName);

		this.players = players;

		this.setPhase(phase);
		this.players.get(0).setTerritories(play1Ter);
		this.players.get(1).setTerritories(play2Ter);
		this.setCurrentPlayer(current);
	}
	
	public GameState(String mapLoc, LinkedList<Player> players,
			int phase, int current) {
		this.setMapLocation(mapLoc);
		this.setMapName(mapName);

		this.players = players;

		this.setPhase(phase);
		this.setCurrentPlayer(current);
	}
	
	@Override
	public GameState clone() {		
		try {
			GameState copy = (GameState) super.clone();
			LinkedList<Player> tempP = new LinkedList<Player>();
			for (Player p : players) {
				tempP.add(p.clone());
			}
			copy.setPlayers(tempP);
			
			copy.setAllContinents(allContinents); 		
			
			return copy;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;	
		}	
	}
	
	public static long getHash(GameState game, long [][] ZobristArray, long [] ZobristPlayerFactor) {
		long key = 0;
		int i = 0;
		Iterator<Territory> It = game.getPlayers().get(0).getTerritories().values().iterator();
		while (It.hasNext()) {
			Territory t = It.next();
			int troopNumber = Math.min(t.getNrTroops(), 49);
			long za = ZobristArray[i++][troopNumber];
			long zpf = ZobristPlayerFactor[game.getCurrentPlayerID()];
			key = key ^ za ^ zpf;
		}
		It = game.getPlayers().get(1).getTerritories().values().iterator();
		while (It.hasNext()) {
			Territory t = It.next();
			int troopNumber = Math.min(t.getNrTroops(), 49);
			long za = ZobristArray[i++][troopNumber];
			long zpf = ZobristPlayerFactor[game.getCurrentPlayerID()];
			key = key ^ za ^ zpf;
		}
		return key;		
	}
	
	@Override
	public int hashCode() {
			long key = 0;
			int i = 0;
			Iterator<Territory> It = getPlayers().get(0).getTerritories().values().iterator();
			while (It.hasNext()) {
				Territory t = It.next();
				key = key ^ AIPlayer.ZobristArray[i++][t.getNrTroops()] ^ AIPlayer.ZobristPlayerFactor[getCurrentPlayerID()];
			}
			It = getPlayers().get(1).getTerritories().values().iterator();
			while (It.hasNext()) {
				Territory t = It.next();
				key = key ^ AIPlayer.ZobristArray[i++][t.getNrTroops()];
			}
			key = key ^ AIPlayer.ZobristPlayerFactor[getCurrentPlayerID()];
			return (int)key;		
	}
	
	@Override
	public boolean equals(Object node) {
		if (this == null || node == null) return false;
		
		GameState myNode = (GameState) node;
		
		Iterator<Territory> It1 = getPlayers().get(0).getTerritories().values().iterator();
		Iterator<Territory> It2 = myNode.getPlayers().get(0).getTerritories().values().iterator();
		while (It1.hasNext() && It2.hasNext()) {
			Territory t1 = It1.next();
			Territory t2 = It2.next();
			if (!t1.equals(t2)) return false;
		}
		 It1 = getPlayers().get(1).getTerritories().values().iterator();
		 It2 = myNode.getPlayers().get(1).getTerritories().values().iterator();
		while (It1.hasNext() && It2.hasNext()) {
			Territory t1 = It1.next();
			Territory t2 = It2.next();
			if (!t1.equals(t2)) return false;
		}
		
		return true;
	}
	
}

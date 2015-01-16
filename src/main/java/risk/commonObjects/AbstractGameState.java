package risk.commonObjects;

import java.util.LinkedList;

public abstract class AbstractGameState {
	protected String mapName;
	protected String mapFileLocation;
	protected String mapImgLocation;

	protected LinkedList<Player> players;
	protected int gamePhase;

	protected int currentPlayer;

	protected Continent [] allContinents;

	public AbstractGameState() {

	}

	// GETTERS AND SETTERS
		public LinkedList<Player> getPlayers() {
			return players;
		}
		
		public void setPlayers(LinkedList<Player> players) {
			this.players = players;
		}

		public Player getCurrentPlayer() {
			for (Player p : players) {
				if (p.getId() == getCurrentPlayerID()) return p;
			}
			return null;
		}
		
		public Player getOtherPlayer() {
			for (Player p : players) {
				if (p.getId() != getCurrentPlayerID()) return p;
			}
			return null;
		}
		
		public int getOtherPlayerID() {
			return getOtherPlayer().getId();
		}

		
		public int getCurrentPlayerID() {
			return currentPlayer;  
		}

		public void changeCurrentPlayer() {
			this.currentPlayer = (this.currentPlayer + 1) % 2;
		}

		public void setCurrentPlayer(int current) {
			this.currentPlayer = current;
		}


		public String getMapLocation() {
			return mapFileLocation;
		}

		public void setMapLocation(String mapLocation) {
			this.mapFileLocation = mapLocation;
		}
		
		public String getImgLocation() {
			return mapImgLocation;
		}

		public void setImgLocation(String mapLocation) {
			this.mapImgLocation = mapLocation;
		}

		public String getMapName() {
			return mapName;
		}

		public void setMapName(String mapName) {
			this.mapName = mapName;
		}

		public int getPhase() {
			return gamePhase;
		}

		public void setPhase(int phase) {
			this.gamePhase = phase;
		}

		public Continent [] getAllContinents() {
			return allContinents;
		}

		public void setAllContinents(Continent [] allContinents) {
			this.allContinents = allContinents;
		}

}

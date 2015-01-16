package risk.humanEngine;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;

import risk.commonObjects.GameState;
import risk.commonObjects.Player;
import risk.commonObjects.Territory;
import risk.gui.clientGui.ClientGUI;
import risk.gui.gameSetup.ControlGUI;

public class EngineLogic {
	public GameState game;
	public EngineProtocolManager emp;

	public Territory[] territories;
	public int[] contBonus;

	public ControlGUI controlGui;
	public ClientGUI clientGui;

	public Color playerColour = Color.RED;
	public Color oppColour = Color.BLACK;

	public int lastMessageID = 0;

	public String username;
	public String ip;

	public int moveTroopsMin = 0;
	public int moveTroopsMax = 0;

	public Territory lastAttackSource;
	public Territory lastAttackDestination;

	public boolean otherPlayerHasSetUp = false;
	public boolean mePlayerHasSetUp = false;
	public boolean moveAfterAttackRequired = false;

	public Comparator<Territory> territoryComparator = new Comparator<Territory>() {
		@Override
		public int compare(Territory o1, Territory o2) {
			return o1.getName().compareTo(o2.getName());
		}
	};

	public static void main(String[] args) {
		System.setProperty("java.net.useSystemProxies", "true");
		new EngineLogic(true);
	}

	public void initialize() {
		otherPlayerHasSetUp = false;
		mePlayerHasSetUp = false;
		moveAfterAttackRequired = false;
		game = null;
		controlGui = null;
		clientGui = null;
	}

	public EngineLogic(boolean firstLaunch) {
		initialize();

		controlGui = new ControlGUI(this, firstLaunch);
	}

	public void establishFacilitatorConnection(String address) {
		Socket controller = null;
		try {
			InetAddress host = InetAddress.getByName(address);
			controller = new Socket(host, 3000);
		} catch (IOException e) {
			System.err.println("The server cannot be connected to.");
			System.exit(0);
		}

		emp = new EngineProtocolManager(controller, this);
	}

	public void establishControllerConnection(String address, int controlPort) {
		Socket controller = null;
		try {
			InetAddress host = InetAddress.getByName(address);
			controller = new Socket(host, controlPort);

		} catch (IOException e) {
			System.err.println("The server cannot be connected to.");
			System.exit(0);
		}

		emp = new EngineProtocolManager(controller, this);
	}

	public void startGame(String player1, String player2, String mapName) {
		LinkedList<Player> players = new LinkedList<Player>();
		players.add(new Player(0, player1));
		players.add(new Player(1, player2));
		game = new GameState(getMapLocation(mapName), players, GameState.SETUP,
				0);

		loadMap(getMapLocation(mapName));

		controlGui.close();
		clientGui = new ClientGUI(game, this);
		clientGui.setColours(playerColour, oppColour);

	}

	public void setUsername(String name) {
		this.username = name;
	}

	public String getUsername() {
		return username;
	}

	public void setIP(String ip) {
		this.ip = ip;
	}

	public String getIP() {
		return ip;
	}

	public void setupRecruit(int number) {
		clientGui.setRecruitedNumber(number);
		clientGui.changePhase();
		if (getUsername().equalsIgnoreCase(getCurrentPlayer().getName())) {
			yourTurn();
		} else {
			notYourTurn();
		}
	}

	public void setPhase(int phase) {
		game.setPhase(phase);
	}

	public int getPhase() {
		return game.getPhase();
	}

	public void loadMap(String mapPath) {
		String[] continents = null;
		int numTer = 0;

		try {
			FileInputStream fstream = new FileInputStream(mapPath);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					fstream));
			StringTokenizer tk;
			String line;
			while ((line = br.readLine()) != null) {
				// Skip blank lines
				while (line.length() == 0 && line != null)
					line = br.readLine();

				tk = new StringTokenizer(line, " ");
				String mod = tk.nextToken();

				if (mod.equalsIgnoreCase("name")) { // Get name

					String mapName = "";
					while (tk.hasMoreTokens())
						mapName += tk.nextToken() + " ";

					game.setMapName(mapName.trim());

				} else if (mod.equalsIgnoreCase("pic")) { // Get mapLocation

					String imgLoc = tk.nextToken();

					game.setImgLocation(imgLoc.trim());

				} else if (mod.equalsIgnoreCase("continents")) {
					int numCont = Integer.parseInt(tk.nextToken());
					continents = new String[numCont];
					contBonus = new int[numCont];
					for (int i = 0; i < numCont; i++) {
						line = br.readLine();
						tk = new StringTokenizer(line, " ");
						String continentStr = tk.nextToken();
						int bonus = Integer.parseInt(tk.nextToken());

						continents[i] = continentStr.replaceAll("_", " ");
						contBonus[i] = bonus;
					}
				} else if (mod.equalsIgnoreCase("territories")) {
					numTer = Integer.parseInt(tk.nextToken());
					territories = new Territory[numTer];
					for (int i = 0; i < numTer; i++) {
						line = br.readLine();
						tk = new StringTokenizer(line, " ");

						int terId = Integer.parseInt(tk.nextToken());
						String terName = tk.nextToken();

						int terContId = Integer.parseInt(tk.nextToken());
						int xCor = Integer.parseInt(tk.nextToken());
						int yCor = Integer.parseInt(tk.nextToken());

						territories[i] = new Territory(terId, terName.replace(
								'_', ' '), terContId - 1,
								continents[terContId - 1], xCor, yCor, 0);
					}
				} else if (mod.equalsIgnoreCase("borders")) {
					for (int i = 0; i < numTer; i++) {
						line = br.readLine();
						tk = new StringTokenizer(line, " ");

						Territory[] neighbours = new Territory[tk.countTokens() - 1];

						int terId = Integer.parseInt(tk.nextToken());

						int ind = 0;
						while (tk.hasMoreTokens()) {
							int borId = Integer.parseInt(tk.nextToken());
							neighbours[ind++] = territories[borId - 1];
						}

						territories[terId - 1].setNeighbours(neighbours);
					}
				}

			}

			br.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	public void troopPlaced(int territoryId, int number) {
		for (Player p : game.getPlayers()) {
			Iterator<Territory> it = p.getTerritories().values().iterator();
			while (it.hasNext()) {
				Territory t = it.next();
				if (t.getId() == territoryId) {
					t.setNrTroops(number);

				}
			}
		}
		clientGui.updateMap();
	}

	public void recruitment(int number) {
		clientGui.setRecruitedNumber(number);
		clientGui.changePhase();

	}

	public void getAttackSourceDestination() {
		clientGui.changePhase();
	}

	public void getManSourceDestination() {
		if (getPhase() == GameState.BATTLE) {
			if (moveTroopsMax != moveTroopsMin) {
				clientGui.moveBattle(moveTroopsMin, moveTroopsMax);
			} else {
				int choice = moveTroopsMax;
				LinkedList<String> reply = new LinkedList<String>();
				reply.add(getLastAttackSource().getId() + "");
				reply.add(getLastAttackDestination().getId() + "");
				reply.add(choice + "");
				resolveManoeuvre(getLastAttackSource().getId(),
						getLastAttackDestination().getId(), choice);
				sendManoeuvreReply(reply);
				clientGui.updateSourceDestinationComboBoxes();
			}

		} else {
			clientGui.changePhase();
			showToaster(
					"It is the Manoeuvre phase. Pick a source and Destination for your manoeuvre.",
					2500);
		}
	}

	public String resolveAttack(int a1, int a2, int a3, int d1, int d2,
			int sID, int dID) {
		moveAfterAttackRequired = false;
		Territory s = game.getCurrentPlayer().getTerritoryByID(sID);
		Territory d = game.getOtherPlayer().getTerritoryByID(dID);

		String attackResult = "";

		if (d.getNrTroops() == 1 && a3 > d2) { // defender defeated
			d.decrementTroops();

			moveAfterAttackRequired = true;

			int count = 0;
			if (a1 > 0)
				count++;
			if (a2 > 0)
				count++;
			if (a3 > 0)
				count++;

			moveTroopsMin = count;
			moveTroopsMax = s.getNrTroops() - 1;
			lastAttackSource = s;
			lastAttackDestination = d;

			transferTerritoryControl(d.getId(), game.getCurrentPlayer(),
					game.getOtherPlayer());

			attackResult = d.getName() + " was defeated.";

			// Determine if whole continent conquered
			boolean found = false;
			Iterator<Territory> it = game.getOtherPlayer().getTerritories()
					.values().iterator();
			while (it.hasNext()) {
				Territory T = it.next();
				if (T.getContinent().equalsIgnoreCase(d.getContinent())) {
					found = true;
					break;
				}
			}

			if (!found) {
				attackResult += "\n" + game.getCurrentPlayer().getName()
						+ " now owns the entire " + d.getContinent();
			}

		} else {
			int sCount = 0;
			int dCount = 0;

			if (a3 <= d2) {
				s.decrementTroops();
				sCount++;
			} else {
				d.decrementTroops();
				dCount++;
			}
			if (d.getNrTroops() != 1) {
				if (a2 <= d1) {
					if (s.getNrTroops() != 1) {
						s.decrementTroops();
						sCount++;
					}
				} else {
					if (d.getNrTroops() != 1) {
						d.decrementTroops();
						dCount++;
					}
				}
			}
			if (dCount > 0)
				attackResult += d.getName() + " lost " + dCount + " troop"
						+ ((dCount > 1) ? "s" : "");

			if (dCount > 0 && sCount > 0)
				attackResult += " and ";
			else if (dCount > 0)
				attackResult += ".";

			if (sCount > 0)
				attackResult += s.getName() + " lost " + sCount + " troop"
						+ ((sCount > 1) ? "s" : "");

			if (s.getNrTroops() == 1)
				clientGui.updateSourceDestinationComboBoxes();

			clientGui.updateMap();
		}

		return attackResult;
	}

	public void postBattleResult(String message) {
		clientGui.postBattleResult(message);

	}

	public int getMoveTroopsMin() {
		return moveTroopsMin;
	}

	public int getMoveTroopsMax() {
		return moveTroopsMax;
	}

	public Territory getLastAttackSource() {
		return lastAttackSource;
	}

	public Territory getLastAttackDestination() {
		return lastAttackDestination;
	}

	private void transferTerritoryControl(int territoryId, Player player1,
			Player player2) {
		Territory mover = player2.getTerritoryByID(territoryId);
		player1.getTerritories().put(mover.getName(), mover);
		player2.getTerritories().remove(mover);

		clientGui.updateSourceDestinationComboBoxes();
	}

	public void setInitialTerritories(LinkedList<String> terrs) {
		HashMap<String, Territory> ownTer = new HashMap<String, Territory>();
		HashMap<String, Territory> oppTer = new HashMap<String, Territory>();
		for (Territory t : territories) {
			boolean found = false;
			for (String s : terrs) {
				if (s.equalsIgnoreCase(t.getId() + "")) {
					ownTer.put(t.getName(), t);
					found = true;
					break;
				}
			}
			if (!found)
				oppTer.put(t.getName(), t);
		}
		for (Player p : game.getPlayers()) {
			if (p.getName().equalsIgnoreCase(username)) {
				p.setTerritories(ownTer);
			} else {
				p.setTerritories(oppTer);
			}
		}
	}

	public void setMessageID(int id) {
		lastMessageID = id;
	}

	private String getMapLocation(String mapName) {
		String mapLoc = "./MapFiles/";

		File folder = new File("./MapFiles");
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			String temp = "";
			if (listOfFiles[i].isFile()) {
				temp = listOfFiles[i].getName();
				if (temp.startsWith(mapName)) {
					mapLoc += temp;
					break;
				}
			}
		}

		return mapLoc;
	}

	public int getTerritoryIDByName(String tName) {
		Iterator<Territory> it = game.getPlayers().get(0).getTerritories()
				.values().iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			if (t.getName().equalsIgnoreCase(tName)) {
				return t.getId();
			}
		}

		it = game.getPlayers().get(1).getTerritories().values().iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			if (t.getName().equalsIgnoreCase(tName)) {
				return t.getId();
			}
		}
		return -1;
	}

	public void updateRecruitedTroops(LinkedList<String> reply) {
		for (int i = 0; i < reply.size() / 2; i++) {
			String ter = reply.get(2 * i);
			String num = reply.get(2 * i + 1);

			Iterator<Territory> it = getMePlayer().getTerritories().values()
					.iterator();
			while (it.hasNext()) {
				Territory t = it.next();
				if (t.getId() == Integer.parseInt(ter)) {
					t.setNrTroops(Integer.parseInt(num));
				}
			}
		}

	}

	public void resolveManoeuvre(int sourceID, int destID, int choice) {
		Territory t1 = game.getCurrentPlayer().getTerritoryByID(sourceID);
		t1.setNrTroops(t1.getNrTroops() - choice);

		Territory t2 = game.getCurrentPlayer().getTerritoryByID(destID);
		t2.setNrTroops(t2.getNrTroops() + choice);

		clientGui.updateMap();
	}

	public void endOfGame(String message) {
		clientGui.endGame(message);
	}

	public void endTurn() {
		game.changeCurrentPlayer();
		game.setPhase(GameState.RECRUIT);
		if (game.getCurrentPlayer().getName().equalsIgnoreCase(username)) {
			clientGui.yourTurn(true);
		} else {
			clientGui.yourTurn(false);
		}
		flipGlassPane();
	}

	public void notYourTurn() {
		clientGui.yourTurn(false);
	}

	public void yourTurn() {
		clientGui.yourTurn(true);
	}

	public Player getCurrentPlayer() {
		return game.getCurrentPlayer();
	}

	public Player getOtherPlayer() {
		return game.getOtherPlayer();
	}

	public Player getMePlayer() {
		for (Player p : game.getPlayers()) {
			if (p.getName().equalsIgnoreCase(username)) {
				return p;
			}
		}
		return null;
	}

	public void flipGlassPane() {
		clientGui.flipGlassPane();
	}

	// GUI interaction methods
	public void setOpponents(LinkedList<String> args) {
		controlGui.setOpponents(args);
	}

	public void setMaps(LinkedList<String> args) {
		controlGui.setMaps(args);
	}

	public void setOtherPlayerHasSetUp() {
		otherPlayerHasSetUp = true;
	}

	public void determineAttackAgain() {
		clientGui.attackAgain();
	}

	public void updateMap() {
		clientGui.updateMap();
	}

	public void setColours(Color playColor, Color oppColor) {
		playerColour = playColor;
		oppColour = oppColor;
	}

	public void showToaster(String message, int delayTime) {
		clientGui.showToaster(message, delayTime);

	}

	// Send methods
	public void sendInitialChoices(Player human, Player opponent, String mapName) {
		LinkedList<String> args = new LinkedList<String>();
		args.add(human.getName());
		args.add(opponent.getName());
		args.add(mapName);

		emp.sendSuccess(lastMessageID, "start_choices", args);
	}

	public void sendPlacementReply(LinkedList<String> reply) {
		mePlayerHasSetUp = true;
		if (game.getPhase() == GameState.SETUP && otherPlayerHasSetUp) {
			setPhase(GameState.RECRUIT);
			clientGui.flipGlassPane();
		}

		emp.sendSuccess(lastMessageID, "place_troops", reply);
	}

	public void sendAttackReply(LinkedList<String> reply) {
		emp.sendSuccess(lastMessageID, "attack ", reply);
	}

	public void sendManoeuvreReply(LinkedList<String> reply) {
		emp.sendSuccess(lastMessageID, "manoeuvre", reply);
	}

}

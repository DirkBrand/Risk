package risk.server.controller;

import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.File;
import java.io.FileInputStream;
//import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.StringTokenizer;

import risk.commonObjects.ConnectedPlayer;
import risk.commonObjects.GameState;
import risk.commonObjects.Logger;
import risk.commonObjects.Player;
import risk.commonObjects.Territory;

public class ControllerLogic {

	int startPort = 4000;

	GameState game;

	ControllerProtocolManager CPM;

	Logger log;

	Random rand = new Random();

	Territory[] territories;
	String[] continents;
	int[] contBonus;

	int[] contBucketTotals;

	public int playerRec;

	public boolean moveRequired;
	public boolean firstConnected = false;

	public LinkedList<String> attackResult;
	public LinkedList<String> gameResult;
	public LinkedList<String> placed1;
	public LinkedList<String> placed2;

	public Thread connectionThread;

	public ServerSocket server1;
	public ServerSocket server2;

	public ConnectedPlayer cp2;

	Comparator<Territory> territoryComparator = new Comparator<Territory>() {
		@Override
		public int compare(Territory o1, Territory o2) {
			return o1.getName().compareTo(o2.getName());
		}
	};

	public ControllerLogic(ConnectedPlayer cp1, ConnectedPlayer cp2,
			String mapLoc, Logger log, int id, int port) {
		this.log = log;
		this.startPort = port;
		CPM = new ControllerProtocolManager(this.log, this);
		CPM.setMesID(id);

		LinkedList<Player> players = new LinkedList<Player>();
		// Create gamestate
		players.add(new Player(0, cp1.getName(), cp1.getInetAddress()
				.getHostAddress(), cp1.getPortNR()));
		players.add(new Player(1, cp2.getName(), cp2.getInetAddress()
				.getHostAddress(), cp2.getPortNR()));

		game = new GameState(mapLoc, players, GameState.SETUP, players.get(0)
				.getId());

		loadMap(mapLoc);
		setupContinents();

		this.cp2 = cp2;

		connectionThread = new Thread(new startControlServer(startPort, 1));
		connectionThread.start();

		connectionThread = new Thread(new startControlServer(startPort + 1, 2));
		connectionThread.start();

		handShake(cp1, startPort);
	}

	private void setupContinents() {
		contBucketTotals = new int[continents.length];
		Arrays.fill(contBucketTotals, 0);
		for (Territory t : territories) {
			for (int i = 0; i < continents.length; i++) {
				if (t.getContinent().equalsIgnoreCase(continents[i])) {
					contBucketTotals[i]++;
				}
			}
		}
	}

	public void startGame() {
		game.setPhase(GameState.SETUP);
		randomAllocateTerritories();
	}

	public void starterAllocation(int playerID) {
		LinkedList<String> placed = new LinkedList<String>();
		playerRec = territories.length - 2;

		Iterator<Territory> it = game.getPlayers().get(playerID)
				.getTerritories().values().iterator();
		while (it.hasNext()) {
			Territory T = it.next();
			T.incrementTroops();
			playerRec--;
			placed.add(T.getId() + "");
			placed.add("1");
		}
		placed1 = placed;

		try { // informs first engine of placement
			CPM.sendMessage(game.getPlayers().get(0).getId(), CPM.getMesID(),
					CPM.getCommand(6), placed);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void randomAllocateTerritories() {
		HashMap<String, Territory> player1Territories = new HashMap<String, Territory>();
		LinkedList<String> p1Str = new LinkedList<String>();
		HashMap<String, Territory> player2Territories = new HashMap<String, Territory>();

		LinkedList<Territory> allTerritories = new LinkedList<Territory>();
		for (Territory t : territories) {
			allTerritories.add(t);
		}

		int count = 0;
		while (count < territories.length) {
			int i = rand.nextInt(allTerritories.size());
			Territory temp = allTerritories.remove(i);
			if (count % 2 == 0) {
				player1Territories.put(temp.getName(), temp);
				p1Str.add(temp.getId() + "");
			} else {
				player2Territories.put(temp.getName(), temp);
			}

			count++;
		}

		game.getPlayers().get(0).setTerritories(player1Territories);
		game.getPlayers().get(1).setTerritories(player2Territories);

		try { // informs the first engine of initial territory allocations
			CPM.sendMessage(game.getPlayers().get(0).getId(), CPM.getMesID(),
					CPM.getCommand(4), p1Str);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void loadMap(String mapPath) {

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

	public void resolveAttack(String sourceID, String destID) {
		boolean endOfGame = false;

		int sID = Integer.parseInt(sourceID);
		int dID = Integer.parseInt(destID);

		Territory s = game.getCurrentPlayer().getTerritoryByID(sID);
		Territory d = game.getOtherPlayer().getTerritoryByID(dID);

		int sourceTroops = s.getNrTroops();
		int destTroops = d.getNrTroops();

		LinkedList<String> result = new LinkedList<String>();

		int attackD[] = new int[] { 0, 0, 0 };
		int defendD[] = new int[] { 0, 0 };

		// Attacker
		if (sourceTroops == 2) {
			attackD[0] = genRoll();
		} else if (sourceTroops == 3) {
			attackD[0] = genRoll();
			attackD[1] = genRoll();
		} else if (sourceTroops > 3) {
			attackD[0] = genRoll();
			attackD[1] = genRoll();
			attackD[2] = genRoll();
		}

		if (destTroops == 2 || destTroops == 1) {
			defendD[0] = genRoll();
		} else if (destTroops > 2) {
			defendD[0] = genRoll();
			defendD[1] = genRoll();
		}

		Arrays.sort(attackD);
		Arrays.sort(defendD);

		// Resolving the Attack
		moveRequired = false;

		if (d.getNrTroops() == 1 && attackD[2] > defendD[1]) { // defender
																// defeated
			moveRequired = true;
			d.decrementTroops();

			transferTerritoryControl(d.getId(), game.getCurrentPlayer(),
					game.getOtherPlayer());

			// The end of the game and the AI's have played
			if (game.getOtherPlayer().getTerritories().size() == 0) {
				endOfGame = true;
				if (CPM.clients.get(0).isAI() && CPM.clients.get(1).isAI()) {
					// writeEndOfGame(game.getCurrentPlayer().getName());
				}
			}
		} else {
			if (attackD[2] <= defendD[1]) {
				s.decrementTroops();
			} else {
				d.decrementTroops();
			}

			if (d.getNrTroops() != 1) {
				if (attackD[1] <= defendD[0]) {
					if (s.getNrTroops() != 1 && attackD[1] != 0) {
						s.decrementTroops();
					}
				} else {
					if (d.getNrTroops() != 1 && defendD[0] != 0) {
						d.decrementTroops();
					}
				}
			}
		}

		result.add(attackD[0] + "");
		result.add(attackD[1] + "");
		result.add(attackD[2] + "");
		result.add(defendD[0] + "");
		result.add(defendD[1] + "");
		result.add(sourceID);
		result.add(destID);

		if (endOfGame) {
			LinkedList<String> tempList = new LinkedList<String>();
			String endMessage = game.getCurrentPlayer().getName();
			tempList.add(endMessage);

			gameResult = tempList;
			try {
				CPM.sendMessage(game.getOtherPlayerID(), CPM.getMesID(),
						CPM.getCommand(11), tempList);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			attackResult = result;
			try {
				CPM.sendMessage(game.getOtherPlayerID(), CPM.getMesID(),
						CPM.getCommand(8), result);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/* private void writeEndOfGame(String winner) {
		File log = new File("aiResults.txt");
		try {
			FileWriter fileWriter = new FileWriter(log, true);

			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

			bufferedWriter.write(winner + "\n");
			bufferedWriter.close();

		} catch (IOException e) {
			System.out.println("COULD NOT LOG RESULTS!!");
		}
	} */

	private void transferTerritoryControl(int territoryId, Player toTer,
			Player fromTer) {
		Territory mover = fromTer.getTerritoryByID(territoryId);
		toTer.getTerritories().put(mover.getName(), mover);
		fromTer.getTerritories().remove(mover.getName());
	}

	private int genRoll() {
		return rand.nextInt(6) + 1;
	}

	public int getPhase() {
		return game.getPhase();
	}

	public void setPhase(int phase) {
		game.setPhase(phase);
	}

	public void resolveManoeuvre(int sourceID, int destID, int number) {
		Territory t1 = game.getCurrentPlayer().getTerritoryByID(sourceID);
		t1.setNrTroops(t1.getNrTroops() - number);

		Territory t2 = game.getCurrentPlayer().getTerritoryByID(destID);
		t2.setNrTroops(t2.getNrTroops() + number);
	}

	public void placeTroopsReply(int id, LinkedList<String> arguments) {
		int n = arguments.size() / 2;

		Player tempP = null;
		for (Player p : game.getPlayers()) {
			if (p.getId() == id) {
				tempP = p;
			}
		}

		for (int i = 0; i < n; i++) {
			String tID = arguments.get(i * 2);
			String num = arguments.get(i * 2 + 1);
			int numToSet = Integer.parseInt(num);

			Territory T = tempP.getTerritoryByID(Integer.parseInt(tID));
			T.setNrTroops(numToSet);
		}
	}

	public Player getCurrentPlayer() {
		return game.getCurrentPlayer();
	}

	public Player getOtherPlayer() {
		return game.getOtherPlayer();
	}

	public GameState getGameState() {
		return game;
	}

	public int calculatedRecruitedTroops() {
		int n = 0;

		// Territory Bonus
		n += (int) (game.getCurrentPlayer().getTerritories().size() / 3);

		// Continent Bonus
		int[] contBuckets = new int[continents.length];
		Arrays.fill(contBuckets, 0);

		Iterator<Territory> it = game.getCurrentPlayer().getTerritories()
				.values().iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			for (int i = 0; i < continents.length; i++) {
				String contTemp = t.getContinent();
				if (contTemp.equalsIgnoreCase(continents[i])) {
					contBuckets[i]++;
				}
			}
		}

		for (int i = 0; i < contBuckets.length; i++) {
			if (contBuckets[i] == contBucketTotals[i]) {
				n += contBonus[i];
			}
		}

		// Minimum is 3
		if (n < 3)
			n = 3;

		return n;
	}

	public int getCurrentPlayerID() {
		return game.getCurrentPlayerID();
	}

	public int getOtherPlayerID() {
		return game.getOtherPlayerID();
	}

	public void nextTurn() {
		
		game.setPhase(GameState.RECRUIT);
		game.changeCurrentPlayer();

		int recruitedNumber = calculatedRecruitedTroops();
		LinkedList<String> args = new LinkedList<String>();
		args.add(recruitedNumber + "");
		CPM.sendMessage(getCurrentPlayerID(), CPM.getMesID(),
				CPM.getCommand(5), args);
	}

	public void connectPlayer(int id, Socket client) {
		CPM.connectPlayer(new ConnectedPlayer(client, id, client.getPort(),
				client.getInetAddress()));

	}

	public void handShake(ConnectedPlayer cp, int port) {

		try {
			String message = "."
					+ port
					+ "."
					+ InetAddress.getLocalHost().getHostAddress()
							.replaceAll("\\.", ",") + ".";

			cp.getOutput().println(message);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void closeServers() {
		try {
			server1.close();
			server2.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// Thread to wait for new clients to connect
	public class startControlServer implements Runnable {

		int port;
		int numberOfThread;

		public startControlServer(int port, int i) {
			this.port = port;
			this.numberOfThread = i;
		}

		@Override
		public void run() {
			try {
				if (numberOfThread == 1) {

					server1 = new ServerSocket(port);
					Socket client = server1.accept();
					connectPlayer(0, client);

					handShake(cp2, startPort + 1);

				} else {

					server2 = new ServerSocket(port);
					Socket client = server2.accept();
					connectPlayer(1, client);

				}

			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Controller read thread closed.");
			}

		}
	}

}

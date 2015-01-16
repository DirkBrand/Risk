package risk.aiplayers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.StringTokenizer;

import risk.aiplayers.util.AIProtocolManager;
import risk.commonObjects.Continent;
import risk.commonObjects.GameState;
import risk.commonObjects.Player;
import risk.commonObjects.Territory;

public abstract class AIPlayer {
	protected GameState game;
	protected int id;
	protected String name;

	protected String opponentName;
	protected String map;

	protected int type;

	Territory[] territories;

	protected AIProtocolManager APM;

	protected Random rand = new Random();

	Socket controller;

	public boolean moveAfterAttackRequired;

	protected Territory lastAttackSource;
	protected Territory lastAttackDestination;
	
	public boolean stillRunning = false;

	public int numberOfMovesTaken = 0;
	public int maxRecruitable = Integer.MAX_VALUE;
	
	public static final int NUMBER_OF_PLAYERS = 2;
	public static final int NUMBER_OF_PHASES = 5;
	public static final int MAX_TROOPS = 49;
	
	public static long ZobristArray [/*Territory ID*/][/*Number of troops*/][/*Number of players*/]; // Number of troops goes from 0 to 49.
	public static long ZobristPlayerFactor[/*Number of players*/];
	public static long ZobristPhaseFactor[/* RECRUIT - ATTACK - RANDOMEVENT - MOVEAFTERATTACK - MANOEUVRE */];
	public static long ZobristAttackSource[/* Territory ID */];
	public static long ZobristAttackDestination[/* Territory ID */];

	Comparator<Territory> territoryComparator = new Comparator<Territory>() {
		@Override
		public int compare(Territory o1, Territory o2) {
			return o1.getName().compareTo(o2.getName());
		}
	};



	public final static int SUBMISSIVE_AI = 0;
	public final static int BASELINE_AI = 1;
	public final static int EMM_AI = 2;
	public final static int MCTS_AI = 3;
	public final static int SIMULATION_AI = 4;

	public AIPlayer(int type, String name, String opponent, String map, int id) {
		stillRunning = true;
		setType(type);
		setId(id);
		setName(name);
		setOpponentName(opponent);
		setMap(map);
		connectToController("localhost", 3000);
	}
	
	
	public void recruit(int number) {
		Player me = game.getPlayers().get(id);

		if (game.getPhase() == GameState.SETUP) {
			setupRecruit(me.getTerritories().values(), number);
		} else if (game.getPhase() == GameState.RECRUIT) {
			recruitPhase(me.getTerritories().values(), number);
		}
	}
	
	/**
	 * Procedure to place troops in the recruitment phase, with the BASELINE
	 * strategy. Must be overridden for other players.
	 * 
	 * @param myTerritories
	 *            Collection of the territories owned by the current player.
	 * @param numberOfTroops
	 *            The number of troops to place.
	 */
	protected abstract void recruitPhase(Collection<Territory> myTerritories,
			int numberOfTroops);

	/**
	 * Procedure to place troops in the setup phase.
	 * 
	 * @param myTerritories
	 *            Collection of the territories owned by the current player.
	 * @param numberOfTroops
	 *            The number of troops to place.
	 */
	protected void setupRecruit(Collection<Territory> myTerritories,
			int numberOfTroops) {
		LinkedList<String> reply = new LinkedList<String>();

		Iterator<Territory> it = myTerritories.iterator();
		while (numberOfTroops > 0) {
			Territory t = it.next();
			t.incrementTroops();
			numberOfTroops--;
		}

		it = myTerritories.iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			reply.add(t.getId() + "");
			reply.add(t.getNrTroops() + "");
		}

		APM.sendSuccess(APM.getMesID(), "place_troops", reply);
	}
	
	public abstract LinkedList<String> getAttackSourceDestination();
	
	public abstract LinkedList<String> getMoveAfterAttack();
	
	public abstract LinkedList<String> getManSourceDestination();
	
	public abstract boolean attackAgain();

	private void setupContinents() {
		for (Territory t : territories) {
			for (int i = 0; i < game.getAllContinents().length; i++) {
				if (t.getContinent().equalsIgnoreCase(game.getAllContinents()[i].getName())) {
					game.getAllContinents()[i].setNumberOfTerritories(game.getAllContinents()[i].getNumberOfTerritories() + 1);
				}
			}
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOpponentName() {
		return opponentName;
	}

	public void setOpponentName(String opponent) {
		this.opponentName = opponent;
	}

	public String getMap() {
		return map;
	}

	public void setMap(String map) {
		this.map = map;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void connectToController(String adress, int port) {

		if (controller != null)
			try {
				controller.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		try {
			controller = new Socket(InetAddress.getByName(adress), port);
		} catch (IOException e) {
			System.err.println("The server cannot be connected to.");
		}

		APM = new AIProtocolManager(controller, this);
	}

	public void startGame(String player1, String player2, String mapName) {
		LinkedList<Player> players = new LinkedList<Player>();
		players.add(new Player(0, player1));
		players.add(new Player(1, player2));
		game = new GameState(getMapLocation(mapName), players, GameState.SETUP,
				0);

		if (player1.equalsIgnoreCase(name))
			id = 0;
		if (player2.equalsIgnoreCase(name))
			id = 1;

		loadMap(getMapLocation(mapName));
		setupContinents();
		setMaxRecruitable();
		
		
		initializeZobrist();
	}

	public void setMaxRecruitable() {
		int n = 0;

		// Total possible Territory Bonus
		n += (int) (territories.length / 3);

		// Continent Bonus
		int[] contBuckets = new int[game.getAllContinents().length];
		Arrays.fill(contBuckets, 0);

		for (int i = 0; i < contBuckets.length; i++) {
			n += game.getAllContinents()[i].getBonus();
		}

		maxRecruitable = n;
	}
	
	/**
	 * Generates a 3D-matrix containing unique id for each possible state
	 * of the game. 
	 */
	private void initializeZobrist() {
		Random r = new Random();
		ZobristArray = new long [territories.length][MAX_TROOPS+1][NUMBER_OF_PLAYERS];
		ZobristPlayerFactor = new long [NUMBER_OF_PLAYERS];
		ZobristPhaseFactor = new long [NUMBER_OF_PHASES];
		ZobristAttackDestination = new long [territories.length];
		ZobristAttackSource = new long [territories.length];
		for (int i = 0; i < territories.length; i++) {
			ZobristAttackSource[i]=r.nextLong();
			ZobristAttackDestination[i]=r.nextLong();
			for (int j = 0; j <MAX_TROOPS+1; j++) {
				ZobristArray[i][j][0] = r.nextLong();
				ZobristArray[i][j][1] = r.nextLong();
			}
		}
		ZobristPlayerFactor[0]=r.nextLong();
		ZobristPlayerFactor[1]=r.nextLong();
		for(int k = 0; k<NUMBER_OF_PHASES; k++) {
			ZobristPhaseFactor[k]=r.nextLong();
		}
	}	

	private String getMapLocation(String mapName) {
		java.io.File file = new java.io.File("");   //Dummy file
	    String  abspath=file.getAbsolutePath();
	    
		String mapLoc = abspath + "/MapFiles/";
		
		File folder = new File(mapLoc);
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

	public void loadMap(String mapPath) {
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
					game.setAllContinents(new Continent[numCont]);
					for (int i = 0; i < numCont; i++) {
						line = br.readLine();
						tk = new StringTokenizer(line, " ");
						String continentStr = tk.nextToken();
						int borders = Integer.parseInt(tk.nextToken());
						int bonus = Integer.parseInt(tk.nextToken());

						String name = continentStr
								.replaceAll("_", " ");
						game.getAllContinents()[i] = new Continent(name, 0, bonus, borders);
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
								game.getAllContinents()[terContId - 1].getName(), xCor,
								yCor, 0);
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

	public GameState getGameState() {
		return game;
	}

	public void setInitialTerritories(LinkedList<String> terrs) {
		HashMap<String,Territory> ownTer = new HashMap<String,Territory>();
		HashMap<String,Territory> oppTer = new HashMap<String,Territory>();
		for (Territory t : territories) {
			boolean found = false;
			for (String s : terrs) {
				if (s.equalsIgnoreCase(t.getId() + "")) {
					ownTer.put(t.getName(),t);
					found = true;
					break;
				}
			}
			if (!found)
				oppTer.put(t.getName(),t);
		}

		for (Player p : game.getPlayers()) {
			if (p.getName().equalsIgnoreCase(name)) {
				p.setTerritories(ownTer);
			} else {
				p.setTerritories(oppTer);
			}
		}
	}

	public void troopPlaced(int territoryID, int number) {
		for (Player p : game.getPlayers()) {
			Iterator<Territory> it = p.getTerritories().values().iterator();
			while (it.hasNext()) {
				Territory t = it.next();
				if (t.getId() == territoryID)
					t.setNrTroops(number);
			}
		}
	}

	public void resolveAttack(int a1, int a2, int a3, int d1, int d2, int sID,
			int dID) {
		moveAfterAttackRequired = false;

		Territory s = game.getCurrentPlayer().getTerritoryByID(sID);
		Territory d = game.getOtherPlayer().getTerritoryByID(dID);
		
		if (d.getNrTroops() == 1 && a3 > d2) { // defender defeated
			moveAfterAttackRequired = true;
			lastAttackSource = s;
			lastAttackDestination = d;
			d.decrementTroops();

			transferTerritoryControl(d.getId(), game.getCurrentPlayer(),
					game.getOtherPlayer());
			updateConnectedRegions();
		} else {
			if (a3 <= d2) {
				s.decrementTroops();
			} else {
				d.decrementTroops();
			}
			if (d.getNrTroops() != 1) {
				if (a2 <= d1) {
					if (s.getNrTroops() != 1 && a2 != 0) {
						s.decrementTroops();
					}
				} else {
					if (d.getNrTroops() != 1 && d1 != 0) {
						d.decrementTroops();
					}
				}
			}

		}
	}

	private void transferTerritoryControl(int territoryId, Player toPlayer,
			Player fromPlayer) {
		Territory mover = fromPlayer.getTerritoryByID(territoryId);
		toPlayer.getTerritories().put(mover.getName(), mover);
		fromPlayer.getTerritories().remove(mover.getName());
	}

	public void resolveManoeuvre(int sourceID, int destID, int number) {
		Territory t1 = game.getCurrentPlayer().getTerritoryByID(sourceID);
		t1.setNrTroops(t1.getNrTroops() - number);

		Territory t2 = game.getCurrentPlayer().getTerritoryByID(destID);
		t2.setNrTroops(t2.getNrTroops() + number);
	}

	// DFS for updating regions
	public void updateConnectedRegions() {
		Iterator<Territory> it = game.getCurrentPlayer().getTerritories().values()
				.iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			t.connectedRegion = -1;
		}
		int regionCounter = 0;
		it = game.getCurrentPlayer().getTerritories().values().iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			if (t.connectedRegion == -1) {
				visit(t, regionCounter);
				regionCounter++;
			}
		}

	}

	private void visit(Territory t, int counter) {
		t.connectedRegion = counter;

		for (Territory n : t.getNeighbours()) {
			if (n.connectedRegion == -1) {
				visit(n, counter);
			}
		}
	}

	public int getPhase() {
		return game.getPhase();
	}

	public void setPhase(int phase) {
		game.setPhase(phase);
	}

	
	

}

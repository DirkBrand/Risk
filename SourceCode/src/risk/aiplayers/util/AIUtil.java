package risk.aiplayers.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import risk.commonObjects.Continent;
import risk.commonObjects.GameState;
import risk.commonObjects.Player;
import risk.commonObjects.Territory;

public class AIUtil {
	static Random r = new Random();

	public static int calculateRecruitedTroops(GameTreeNode node) {
		int n = 0;

		// Territory Bonus
		n += (int) (node.getGame().getCurrentPlayer().getTerritories().size() / 3);

		// Continent Bonus
		int[] contBuckets = new int[node.getGame().getAllContinents().length];
		Arrays.fill(contBuckets, 0);

		Iterator<Territory> it = node.getGame().getCurrentPlayer()
				.getTerritories().values().iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			for (int i = 0; i < node.getGame().getAllContinents().length; i++) {
				String contTemp = t.getContinent();
				if (contTemp
						.equalsIgnoreCase(node.getGame().getAllContinents()[i]
								.getName())) {
					contBuckets[i]++;
				}
			}
		}

		for (int i = 0; i < contBuckets.length; i++) {
			if (contBuckets[i] == node.getGame().getAllContinents()[i]
					.getNumberOfTerritories()) {
				n += node.getGame().getAllContinents()[i].getBonus();
			}
		}

		if (n < 3)
			n = 3;

		return n;
	}

	public static boolean isHinterland(GameTreeNode node, Territory t) {
		for (Territory n : t.getNeighbours()) {
			if (node.getGame().getOtherPlayer().getTerritoryByName(n.getName()) != null) {
				return false;
			}
		}
		return true;
	}

	public static boolean isTerminalNode(GameTreeNode node) {
		boolean aDead = node.getGame().getPlayers().get(0).getTerritories()
				.size() == 0;
		boolean bDead = node.getGame().getPlayers().get(1).getTerritories()
				.size() == 0;
		if (aDead || bDead)
			return true;
		else
			return false;
	}

	// According to http://www.plainsboro.com/~lemke/risk/
	public static double getProb(GameTreeNode child) {

		int[] diceRolls = child.getDiceRolls();

		// Defender has 1 die
		if (diceRolls[3] == 0) {
			// Attacker has 1 die
			if (diceRolls[1] == 0) {
				// Attacker Wins
				if (diceRolls[2] > diceRolls[4]) {
					return 0.4167;
				} else {
					return 0.5833;
				}
			}
			// Attacker has 2 dice
			else if (diceRolls[0] == 0) {
				// Attacker Wins
				if (diceRolls[2] > diceRolls[4]) {
					return 0.5787;
				} else {
					return 0.4213;
				}
			}
			// Attacker has 3 dice
			else {
				// Attacker Wins
				if (diceRolls[2] > diceRolls[4]) {
					return 0.6597;
				} else {
					return 0.3403;
				}
			}
		}
		// Defender has 2 dice
		else {
			// Attacker has 1 die
			if (diceRolls[1] == 0) {
				// Attacker Wins
				if (diceRolls[2] > diceRolls[4]) {
					return 0.2546;
				} else {
					return 0.7454;
				}
			}
			// Attacker has 2 dice
			else if (diceRolls[0] == 0) {
				// Attacker Wins
				if (diceRolls[2] > diceRolls[4] && diceRolls[1] > diceRolls[3]) {
					return 0.2276;
				}
				// Attacker loses 1, defende loses 1
				else if ((diceRolls[2] <= diceRolls[4] && diceRolls[1] > diceRolls[3])
						|| (diceRolls[2] > diceRolls[4] && diceRolls[1] <= diceRolls[3])) {
					return 0.3241;
				}
				// Defender wins
				else {
					return 0.4483;
				}
			}
			// Attacker has 3 dice
			else {
				// Attacker Wins
				if (diceRolls[2] > diceRolls[4] && diceRolls[1] > diceRolls[3]) {
					return 0.3717;
				}
				// Attacker loses 1, defende loses 1
				else if ((diceRolls[2] <= diceRolls[4] && diceRolls[1] > diceRolls[3])
						|| (diceRolls[2] > diceRolls[4] && diceRolls[1] <= diceRolls[3])) {
					return 0.3358;
				}
				// Defender wins
				else {
					return 0.2926;
				}

			}
		}
	}

	public static void resolveRecruit(GameTreeNode node, Territory t) {
		t.setNrTroops(t.getNrTroops() + node.getRecruitedNumber());
	}

	public static void resolveAttackAction(GameTreeNode node) {
		int[] diceRolls = node.getDiceRolls();

		Territory d = node.getGame().getOtherPlayer()
				.getTerritoryByName(node.getAttackDest());
		Territory s = node.getGame().getCurrentPlayer()
				.getTerritoryByName(node.getAttackSource());

		node.setMoveReq(false);

		if (d.getNrTroops() <= 1 && diceRolls[2] > diceRolls[4]) { // defender
																	// defeated
			node.setMoveReq(true);

			d.setNrTroops(0);

			// transfering territory
			node.getGame().getCurrentPlayer().getTerritories()
					.put(d.getName(), d);
			node.getGame().getOtherPlayer().getTerritories()
					.remove(d.getName());

			updateRegions(node.getGame());
		} else {
			if (diceRolls[2] <= diceRolls[4]) {
				s.decrementTroops();
			} else {
				d.decrementTroops();
			}
			if (d.getNrTroops() != 1) {
				if (diceRolls[1] <= diceRolls[3]) {
					if (s.getNrTroops() != 1 && diceRolls[1] != 0) {
						s.decrementTroops();
					}
				} else {
					if (d.getNrTroops() != 1 && diceRolls[3] != 0) {
						d.decrementTroops();
					}
				}
			}

		}
	}

	public static void resolveMoveAction(Territory source, Territory dest,
			int number) {
		if (source.getNrTroops() - number < 1) {
			System.out.println("WTF WTF");
			for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
				System.out.println(ste);
			}
		}
		if (dest == null) {
			return;
		}
		if (!source.getName().equalsIgnoreCase(dest.getName())) {

			source.setNrTroops(source.getNrTroops() - number);

			dest.setNrTroops(dest.getNrTroops() + number);
		}
	}

	// Returns the number of regions
	public static int updateRegions(GameState game) {
		// Updating territory regions
		Iterator<Territory> it = game.getCurrentPlayer().getTerritories()
				.values().iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			t.connectedRegion = -1;
		}
		int regionCounter = 0;
		it = game.getCurrentPlayer().getTerritories().values().iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			if (t.connectedRegion == -1) {
				visit(game.getCurrentPlayer(), t, regionCounter);
				regionCounter++;
			}
		}
		return regionCounter;
	}

	private static void visit(Player p, Territory t, int counter) {
		t.connectedRegion = counter;

		for (Territory n : t.getNeighbours()) {
			Territory temp = p.getTerritoryByName(n.getName());
			if (temp != null && temp.connectedRegion == -1) {
				visit(p, temp, counter);
			}
		}
	}

	// Sums weighted features of the board state
	// (Linear evaluation function)
	public static double eval(GameTreeNode node, double[] weights, int maxRecruitable) {

		if (isTerminalNode(node)) {
			if (node.isMaxPlayer())
				return Double.POSITIVE_INFINITY - 1e-6;
			else
				return Double.NEGATIVE_INFINITY + 1e-6;
		}

		/*
		 * if (node.getTreePhase() == GameTreeNode.ATTACK && node.getGame()
		 * .getCurrentPlayer() .getTerritoryByName( node.getAttackSource()) !=
		 * null && node.getGame() .getCurrentPlayer() .getTerritoryByName(
		 * node.getAttackDest()) != null && AIParameter .getProbOfWin(
		 * node.getGame() .getCurrentPlayer() .getTerritoryByName(
		 * node.getAttackSource()) .getNrTroops(), node.getGame()
		 * .getCurrentPlayer() .getTerritoryByName( node.getAttackDest())
		 * .getNrTroops()) > 0.95) { return Double.MAX_VALUE; }
		 */

		boolean usage[] = new boolean[13];

		usage[0] = true;
		usage[1] = true;
		usage[2] = true;
		usage[3] = true;
		usage[4] = true;
		usage[5] = true;
		usage[6] = true;
		usage[7] = true;
		usage[8] = true;
		usage[9] = true;
		usage[10] = true;
		usage[11] = true;
		usage[12] = true;

		double attackFeature = 0, // 0
		bestEnemyFeature = 0, // 1
		moreThanOneTroopFeature = 0, // 2
		hinterlandFeature = 0, // 3
		distanceToFrontierFeature = 0, // 4
		continentSafetyFeature = 0, // 5
		continentThreatFeature = 0, // 6
		enemyRecruitFeature = 0, // 7
		enemyOccupiedContinentsFeature = 0, // 8
		maximumThreatFeature = 0, // 9
		occupiedTerritoryFeature = 0, // 10
		ownRecruitFeature = 0, // 11
		ownOccupiedContinentsFeature = 0; // 12

		// System.out.println("FEATURES");
		// Features
		if (usage[0] && weights[0] != 0)
			// 0-1
			attackFeature = AIFeatures.armyStrength(node);
		// System.out.println(1);

		if (usage[1] && weights[1] != 0)
			// 0-1
			bestEnemyFeature = AIFeatures.enemyStrength(node);
		// System.out.println(2);

		if (usage[2] && weights[2] != 0)
			// 0-1
			moreThanOneTroopFeature = AIFeatures.fortifiedTerritories(node);
		// System.out.println(3);

		if (usage[3] && weights[3] != 0)
			// 0-1
			hinterlandFeature = AIFeatures.hinterlandStrength(node);
		// System.out.println(4);

		if (usage[4] && weights[4] != 0)
			// 0-1
			distanceToFrontierFeature = AIFeatures.distanceToFrontier(node);
		// System.out.println(5);

		if (usage[5] && weights[5] != 0)
			continentSafetyFeature = AIFeatures.continentSafetyFeature(node);
		// System.out.println(6);

		if (usage[6] && weights[6] != 0)
			continentThreatFeature = AIFeatures.continentThreatFeature(node);
		// System.out.println(7);

		if (usage[7] && weights[7] != 0)
			// 1->
			enemyRecruitFeature = AIFeatures.enemyRecruitFeature(node);
		// System.out.println(8);

		if (usage[8] && weights[8] != 0)
			enemyOccupiedContinentsFeature = AIFeatures
					.enemyOccupiedContinentsFeature(node);
		// System.out.println(9);

		if (usage[9] && weights[9] != 0)
			// 0-1
			maximumThreatFeature = AIFeatures.maximumThreatFeature(node);
		// System.out.println(10);

		if (usage[10] && weights[10] != 0)
			// 0-1
			occupiedTerritoryFeature = AIFeatures
					.occupiedTerritoryFeature(node);
		// System.out.println(11);

		if (usage[11] && weights[11] != 0)
			ownRecruitFeature = AIFeatures.ownRecruitFeature(node);
		// System.out.println(12);

		if (usage[12] && weights[12] != 0)
			ownOccupiedContinentsFeature = AIFeatures
					.ownOccupiedContinentsFeature(node);
		// System.out.println(13);

		// NORMALIZATION
		AIParameter params = new AIParameter();
		enemyRecruitFeature/=maxRecruitable;
		enemyOccupiedContinentsFeature/=node.getGame().getAllContinents().length;
		ownRecruitFeature/=maxRecruitable;
		ownOccupiedContinentsFeature/=node.getGame().getAllContinents().length;
		
		
		// Add up features
		double sum = weights[0] * attackFeature // 1
				+ weights[1] * bestEnemyFeature // 2
				+ weights[2] * moreThanOneTroopFeature // 3
				+ weights[3] * hinterlandFeature // 4
				+ weights[4] * distanceToFrontierFeature // 5
				+ weights[5] * continentSafetyFeature // 6
				+ weights[6] * continentThreatFeature // 7
				+ weights[7] * enemyRecruitFeature // 8
				+ weights[8] * enemyOccupiedContinentsFeature // 9
				+ weights[9] * maximumThreatFeature // 10
				+ weights[10] * occupiedTerritoryFeature // 11
				+ weights[11] * ownRecruitFeature // 12
				+ weights[12] * ownOccupiedContinentsFeature; // 13

		return sum + r.nextDouble() * 0.00001; // To break Ties

	}


	public static int genRoll() {
		Random rand = new Random();
		return rand.nextInt(6) + 1;
	}

	// MCTS specific
	public static double ucb(MCTSNode node, AIParameter params) {
		double wr = 0;
		double ucb = 0;

		wr = (double) node.getWinCount()
				/ ((double) node.getVisitCount() + params.epsilon);

		ucb = params.c * Math.sqrt(getVar(node, params));

		/*
		 * if (node.isMaxPlayer()) { return wr + ucb; } else { return
		 * Math.abs(wr + ucb); }
		 */

		return wr + ucb + params.r.nextDouble() * params.epsilon;
	}

	public static double getVar(MCTSNode node, AIParameter params) {
		return 2 * Math.log((double) node.getParent().getVisitCount() + 1)
				/ ((double) node.getVisitCount() + params.epsilon);
	}

	public static double getUrgencyScore(MCTSNode node, AIParameter params) {
		double maxUCB = 0;

		maxUCB = Double.NEGATIVE_INFINITY;
		for (MCTSNode child : node.getChildren()) {
			double val = ucb(child, params);
			if (val > maxUCB) {
				maxUCB = val;
			}
		}
		return maxUCB;

	}

	public static int nCk(int n, int k) {
		int[][] arr = new int[n + 1][k + 1];

		for (int i = 0; i <= n; i++) {
			arr[i][0] = 1;
		}
		for (int i = 1; i <= k; i++) {
			arr[0][i] = 0;
		}
		for (int i = 1; i <= n; i++) {
			for (int j = 1; j <= k; j++) {
				arr[i][j] = arr[i - 1][j - 1] + arr[i - 1][j];
			}
		}
		return arr[n][k];
	}

	// Implementing Fisher-Yates shuffle
	public static void shuffleArray(Boolean[] ar) {
		Random rnd = new Random();
		for (int i = ar.length - 1; i >= 0; i--) {
			int index = rnd.nextInt(i + 1);
			// Simple swap
			boolean a = ar[index];
			ar[index] = ar[i];
			ar[i] = a;
		}
	}

	// Find distance from territory to closest enemy territory
	public static int distance(GameTreeNode node, Territory t) {
		LinkedList<String> alreadySearched = new LinkedList<String>();

		Queue<Territory> q = new LinkedList<Territory>();
		t.depth = 0;
		q.add(t);
		alreadySearched.add(t.getName());

		int minDepth = Integer.MAX_VALUE;
		while (!q.isEmpty()) {
			Territory n = q.remove();
			if (node.getGame().getOtherPlayer().getTerritoryByName(n.getName()) != null) {
				minDepth = n.depth;
				break;
			}
			for (Territory adj : n.getNeighbours()) {
				boolean found = false;
				for (String ter : alreadySearched) {
					if (ter.equalsIgnoreCase(adj.getName())) {
						found = true;
						break;
					}
				}
				if (!found) {
					adj.depth = n.depth + 1;
					if (!q.add(adj)) {
						break;
					}
					alreadySearched.add(adj.getName());
				}
			}
		}

		return minDepth;
	}

	// Average victory probability of neighbouring enemy territories against
	// Territory t
	public static double threat(GameTreeNode node, Territory t, int playerIndex) {
		double threat = 0;
		AIParameter params = new AIParameter();
		for (Territory n : t.getNeighbours()) {
			Territory tempT = node.getGame().getPlayers().get(playerIndex)
					.getTerritoryByName(n.getName());
			// Enemy Territory
			if (tempT != null && tempT.getNrTroops() > 0 && t.getNrTroops() > 0) {
				threat = Math.max(
						threat,
						params.getProbOfWin(tempT.getNrTroops(),
								t.getNrTroops()));
			}
		}

		return threat;
	}

	public static double continentRating(GameTreeNode node, int index) {
		Continent myContinent = node.getGame().getAllContinents()[index];
		double rating = (15 + (double) myContinent.getBonus() - 4 * (double) myContinent
				.getNumberOfBorderingTerritories())
				/ (double) myContinent.getNumberOfTerritories();

		return rating;
	}

}

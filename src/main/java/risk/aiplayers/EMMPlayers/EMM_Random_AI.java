package risk.aiplayers.EMMPlayers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import risk.aiplayers.ExpectiminimaxPlayer;
import risk.aiplayers.util.AIFeatures;
import risk.aiplayers.util.AIParameter;
import risk.aiplayers.util.AIUtil;
import risk.aiplayers.util.EMMNode;
import risk.aiplayers.util.GameTreeNode;
import risk.commonObjects.GameState;
import risk.commonObjects.Territory;

public class EMM_Random_AI extends ExpectiminimaxPlayer {

	public static void main(String[] args) {
		String tempName = args[0];
		int depth = 0;
		if (args.length > 1) {
			depth = Integer.parseInt(args[1]);
		}
		new EMM_Random_AI(tempName, null, null, 2, depth);
	}

	public EMM_Random_AI(String name, String opp, String map, int id, int depth) {
		super(name, opp, map, id, depth, AIParameter.evalWeights);
	}

	@Override
	protected LinkedList<EMMNode> getMoveAfterAttackActions(EMMNode node) {
		LinkedList<EMMNode> actions = new LinkedList<EMMNode>();

		Territory source = node.getGame().getCurrentPlayer()
				.getTerritoryByName(node.getAttackSource());
		Territory dest = node.getGame().getCurrentPlayer()
				.getTerritoryByName(node.getAttackDest());

		if (source.getNrTroops() == 1) {
			System.out.println("WTF");
			System.out.println();
		}

		int troops = rand.nextInt(source.getNrTroops() - 1) + 1;

		EMMNode temp = node.clone();
		temp.setTreePhase(GameTreeNode.ATTACK);
		AIUtil.resolveMoveAction(temp.getGame().getCurrentPlayer()
				.getTerritoryByName(source.getName()), temp.getGame()
				.getCurrentPlayer().getTerritoryByName(dest.getName()), troops);
		actions.add(temp);

		return actions;
	}

	@Override
	protected LinkedList<EMMNode> getRecruitActions(EMMNode node, int depth) {
		LinkedList<EMMNode> actions = new LinkedList<EMMNode>();

		int n = AIUtil.calculateRecruitedTroops(node);

		int m = node.getGame().getCurrentPlayer().getTerritories().size();

		Boolean[] perm = new Boolean[n + m - 1];
		int d = 0;
		for (int i = 0; i < n; i++)
			perm[d++] = true;

		for (int i = 0; i < m - 1; i++)
			perm[d++] = false;

		for (int j = 0; j < params.EMMRecruitBranchingLimit; j++) {
			AIUtil.shuffleArray(perm);

			EMMNode temp = node.clone();
			temp.setTreePhase(GameTreeNode.ATTACK);

			Iterator<Territory> it = temp.getGame().getCurrentPlayer()
					.getTerritories().values().iterator();
			Territory current = it.next();
			// Place troops according to permutation
			for (int p = 0; p < perm.length; p++) {
				if (perm[p]) {
					current.incrementTroops();
				} else {
					current = it.next();
				}
			}

			actions.add(temp);
		}

		return actions;
	}

	@Override
	protected LinkedList<EMMNode> getAttackActions(EMMNode node) {
		LinkedList<EMMNode> actions = new LinkedList<EMMNode>();

		LinkedList<EMMNode> attackTerCombos = new LinkedList<EMMNode>();

		// No attack is added as an option by default
		EMMNode noAttackNode = node.clone();
		noAttackNode.setTreePhase(GameTreeNode.MANOEUVRE);
		noAttackNode.setAttackSource("");
		noAttackNode.setAttackDest("");
		noAttackNode.setMoveReq(false);
		attackTerCombos.add(noAttackNode);

		// Get list of possible territory combos
		Iterator<Territory> it = node.getGame().getCurrentPlayer()
				.getTerritories().values().iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			if (t.getNrTroops() > 1) {
				for (Territory n : t.getNeighbours()) {
					Territory dest = node.getGame().getOtherPlayer()
							.getTerritoryByName(n.getName());
					if (dest != null) {
						EMMNode tempNode = node.clone();
						tempNode.setTreePhase(GameTreeNode.RANDOMEVENT);
						tempNode.setAttackSource(t.getName());
						tempNode.setAttackDest(dest.getName());
						attackTerCombos.add(tempNode);
					}
				}
			}
		}

		// Split on the top k (k = branchingfactor)
		int k = Math
				.min(params.EMMAttackBranchingLimit, attackTerCombos.size());
		for (int i = 0; i < k; i++) {
			int index = rand.nextInt(attackTerCombos.size());
			actions.add(attackTerCombos.remove(index));
		}

		return actions;
	}

	@Override
	protected LinkedList<EMMNode> getMoveActions(EMMNode node) {
		LinkedList<EMMNode> actions = new LinkedList<EMMNode>();
		int size = AIUtil.updateRegions(node.getGame());
		// Create list of connected components
		LinkedList<LinkedList<Territory>> connComponentBuckets = new LinkedList<LinkedList<Territory>>();
		for (int i = 0; i < size; i++)
			connComponentBuckets.add(new LinkedList<Territory>());

		Iterator<Territory> it = node.getGame().getCurrentPlayer()
				.getTerritories().values().iterator();

		int count = 0;
		while (it.hasNext()) {
			Territory t = it.next();
			connComponentBuckets.get(t.connectedRegion).add(t);
		}

		for (LinkedList<Territory> bucket : connComponentBuckets) {
			if (bucket.size() > 1) {
				for (Territory src : bucket) {
					if (src.getNrTroops() > 1) {
						for (Territory dest : bucket) {
							if (!src.getName().equals(dest.getName())) {
								// Unique source-dest combo
								count += (src.getNrTroops() - 1);
							}
						}
					}
				}
			}
		}

		// No territories with more than 1 troop. Thus only option is not
		// maneuvering
		if (count == 0) {
			EMMNode temp = node.clone();
			temp.setTreePhase(GameTreeNode.RECRUIT);
			temp.switchMaxPlayer();
			temp.getGame().changeCurrentPlayer();

			actions.add(temp);
			return actions;
		}

		Random r = new Random();
		boolean noManAdded = false;
		// Repeat for the number of allowed branches
		for (int j = 0; j < params.EMMManBranchingLimit; j++) {
			if (!noManAdded) {
				// Not maneuvering
				actions.add(node.clone());
				noManAdded = true;
				continue;
			}

			LinkedList<Territory> connComponent = null;

			// Confirm connComponent contains fortified territories
			boolean found = false;
			while (!found) {
				int cIndex = r.nextInt(size);
				connComponent = connComponentBuckets.get(cIndex);
				for (Territory t : connComponent) {
					if (t.getNrTroops() > 1) {
						found = true;
						break;
					}
				}
			}

			int sIndex = r.nextInt(connComponent.size());
			Territory source = connComponent.get(sIndex);
			// confirm source is fortified
			while (source.getNrTroops() <= 1) {
				sIndex = r.nextInt(connComponent.size());
				source = connComponent.get(sIndex);
			}

			int dIndex = r.nextInt(connComponent.size());
			while (dIndex == sIndex && connComponent.size() != 1)
				dIndex = r.nextInt(connComponent.size());

			Territory dest = connComponent.get(dIndex);

			int troopNumber = Math.round(r.nextInt(source.getNrTroops() - 1)) + 1;

			EMMNode temp = node.clone();
			AIUtil.resolveMoveAction(temp.getGame().getCurrentPlayer()
					.getTerritoryByName(source.getName()), temp.getGame()
					.getCurrentPlayer().getTerritoryByName(dest.getName()),
					troopNumber);

			temp.setTreePhase(GameTreeNode.RECRUIT);
			temp.switchMaxPlayer();
			temp.getGame().changeCurrentPlayer();

			actions.add(temp);
		}

		return actions;
	}

	// Immature recruiting scheme where all troops are placed on a single
	// territory if that territory has neighboring enemy territories

	@Override
	public void recruitPhase(Collection<Territory> meTerritories, int number) {
		if (NodeValues.size() > 1000000) {
			NodeValues = null;
			NodeValues = new HashMap<Long, Double>();
		}

		LinkedList<String> reply = new LinkedList<String>();

		GameState bestGame = game;
		double maximum = Double.NEGATIVE_INFINITY;

		int n = number;

		int m = game.getCurrentPlayer().getTerritories().size();

		Boolean[] perm = new Boolean[n + m - 1];
		int d = 0;
		for (int i = 0; i < n; i++)
			perm[d++] = true;

		for (int i = 0; i < m - 1; i++)
			perm[d++] = false;

		for (int j = 0; j < params.EMMRecruitBranchingLimit; j++) {
			AIUtil.shuffleArray(perm);

			EMMNode node = new EMMNode();
			node.setGame(game.clone());
			node.setTreePhase(GameTreeNode.ATTACK);
			node.setMaxPlayer(true);

			Iterator<Territory> it = node.getGame().getCurrentPlayer()
					.getTerritories().values().iterator();
			Territory current = it.next();
			// Place troops according to permutation
			for (int p = 0; p < perm.length; p++) {
				if (perm[p]) {
					current.incrementTroops();
				} else {
					current = it.next();
				}
			}

			nodeCount = 0;
			trimCount = 0;

			/* System.out.println("RECRUITING"); */
			long startTime = System.nanoTime();
			double rating = EMM_AB(node, maxDepth, Double.NEGATIVE_INFINITY,
					Double.POSITIVE_INFINITY);
			double time = (System.nanoTime() - startTime) / 1000000.0;
			/*
			 * System.out.println("Running time: " + (int)time + "ms");
			 * System.out.println("Rating:" + rating);
			 * System.out.println("Number of nodes: " + nodeCount);
			 * System.out.println("Number of prunings: " + trimCount);
			 * System.out.println("HashMap ratio - " + (double) foundIt /
			 * (double) (foundIt + missedIt) * 100 + "%");
			 * System.out.println("Nodes / Second: " + (int)(nodeCount / (time /
			 * 1000.0))); System.out.println();
			 */

			if (rating > maximum) {
				bestGame = node.getGame();
				maximum = rating;
				reply = new LinkedList<String>();
				it = node.getGame().getCurrentPlayer().getTerritories()
						.values().iterator();
				while (it.hasNext()) {
					Territory T = it.next();
					reply.add(T.getId() + "");
					reply.add(T.getNrTroops() + "");
				}
			}
		}
		game = bestGame;

		APM.sendSuccess(APM.getMesID(), "place_troops", reply);
	}

	@Override
	public LinkedList<String> getAttackSourceDestination() {

		// Get list of possible territory combos
		LinkedList<EMMNode> attackTerCombos = new LinkedList<EMMNode>();

		Iterator<Territory> it = game.getCurrentPlayer().getTerritories()
				.values().iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			if (t.getNrTroops() > 1) {
				for (Territory n : t.getNeighbours()) {
					Territory temp = game.getOtherPlayer().getTerritoryByName(
							n.getName());
					if (temp != null) {
						EMMNode node = new EMMNode();
						node.setGame(game.clone());
						node.setTreePhase(GameTreeNode.RANDOMEVENT);
						node.setMaxPlayer(true);

						node.setAttackSource(t.getName());
						node.setAttackDest(temp.getName());

						attackTerCombos.add(node);
					}
				}
			}
		}

		// Play with no attack as an option
		EMMNode noAttackNode = new EMMNode();
		noAttackNode.setGame(game.clone());
		noAttackNode.setTreePhase(GameTreeNode.MANOEUVRE);
		noAttackNode.setMaxPlayer(true);
		noAttackNode.setAttackDest("");
		noAttackNode.setAttackSource("");
		double maximum = Double.NEGATIVE_INFINITY;

		if (AIFeatures.occupiedTerritoryFeature(noAttackNode) < params.leadWinRate
				&& AIFeatures.armyStrength(noAttackNode) < params.leadWinRate) {

			maximum = EMM_AB(noAttackNode, maxDepth, Double.NEGATIVE_INFINITY,
					Double.POSITIVE_INFINITY);

		}

		LinkedList<String> reply = new LinkedList<String>();

		// Split on the top k (k = branchingfactor)
		for (int i = 0; i < Math.min(params.EMMAttackBranchingLimit,
				attackTerCombos.size()); i++) {
			int index = rand.nextInt(attackTerCombos.size());
			EMMNode child = attackTerCombos.remove(index);

			nodeCount = 0;
			trimCount = 0;

			/* System.out.println("ATTACKING"); */

			long startTime = System.nanoTime();
			double rating = EMM_AB(child, maxDepth, Double.NEGATIVE_INFINITY,
					Double.POSITIVE_INFINITY);
			double time = (System.nanoTime() - startTime) / 1000000.0;
			/*
			 * System.out.println("Running time: " + (int)time + "ms");
			 * System.out.println("Rating:" + rating);
			 * System.out.println("Number of nodes: " + nodeCount);
			 * System.out.println("Number of prunings: " + trimCount);
			 * System.out.println("HashMap ratio - " + (double) foundIt /
			 * (double) (foundIt + missedIt) * 100 + "%");
			 * System.out.println("Nodes / Second: " + (int)(nodeCount / (time /
			 * 1000.0))); System.out.println();
			 */

			if (rating >= maximum) {
				maximum = rating;
				reply = new LinkedList<String>();
				String source = child.getAttackSource();
				String dest = child.getAttackDest();
				if (source.length() > 0 && dest.length() > 0) {
					reply.add(child.getGame().getCurrentPlayer()
							.getTerritoryByName(source).getId()
							+ "");
					reply.add(child.getGame().getOtherPlayer()
							.getTerritoryByName(dest).getId()
							+ "");
				}
			}

		}

		return reply;

	}

	@Override
	public LinkedList<String> getMoveAfterAttack() {
		LinkedList<String> reply = new LinkedList<String>();
		reply.add(lastAttackSource.getId() + "");
		reply.add(lastAttackDestination.getId() + "");

		int troops = rand.nextInt(lastAttackSource.getNrTroops() - 1) + 1;

		reply.add(troops + "");

		return reply;
	}

	@Override
	// Manoeuvre
	public LinkedList<String> getManSourceDestination() {

		if (NodeValues.size() > 1000000) {
			NodeValues = null;
			NodeValues = new HashMap<Long, Double>();
		}

		LinkedList<String> reply = new LinkedList<String>();
		GameState bestGame = null;
		int current = game.getCurrentPlayerID();
		double maximum = Double.NEGATIVE_INFINITY;

		int size = AIUtil.updateRegions(game);
		// Create list of connected components
		LinkedList<LinkedList<Territory>> connComponentBuckets = new LinkedList<LinkedList<Territory>>();
		for (int i = 0; i < size; i++)
			connComponentBuckets.add(new LinkedList<Territory>());

		Iterator<Territory> it = game.getCurrentPlayer().getTerritories()
				.values().iterator();
		int count = 0;
		while (it.hasNext()) {
			Territory t = it.next();
			connComponentBuckets.get(t.connectedRegion).add(t);
		}

		for (LinkedList<Territory> bucket : connComponentBuckets) {
			if (bucket.size() > 1) {
				for (Territory src : bucket) {
					if (src.getNrTroops() > 1) {
						for (Territory dest : bucket) {
							if (!src.getName().equals(dest.getName())) {
								// Unique source-dest combo
								count += (src.getNrTroops() - 1);
							}
						}
					}
				}
			}
		}

		// No territories with more than 1 troop. Thus only option is not
		// maneuvering
		if (count <= 0) {
			return reply;
		}

		Random r = new Random();
		boolean noManAdded = false;
		// Repeat for the number of allowed branches
		for (int j = 0; j < Math.min(params.EMMManBranchingLimit, count); j++) {

			if (!noManAdded) {
				// Not maneuvering
				EMMNode temp = new EMMNode();
				temp.setGame(game.clone());
				temp.setTreePhase(GameTreeNode.RECRUIT);
				temp.setMaxPlayer(false);
				maximum = EMM_AB(temp, maxDepth, Double.NEGATIVE_INFINITY,
						Double.POSITIVE_INFINITY);

				noManAdded = true;

				continue;
			}

			LinkedList<Territory> connComponent = null;

			// Confirm connComponent contains fortified territories
			boolean found = false;
			while (!found) {
				int cIndex = r.nextInt(size);
				connComponent = connComponentBuckets.get(cIndex);
				for (Territory t : connComponent) {
					if (t.getNrTroops() > 1) {
						found = true;
						break;
					}
				}
			}

			int sIndex = r.nextInt(connComponent.size());
			Territory source = connComponent.get(sIndex);
			// confirm source is fortified
			while (source.getNrTroops() <= 1) {
				sIndex = r.nextInt(connComponent.size());
				source = connComponent.get(sIndex);
			}

			int dIndex = r.nextInt(connComponent.size());
			while (dIndex == sIndex && connComponent.size() != 1)
				dIndex = r.nextInt(connComponent.size());

			Territory dest = connComponent.get(dIndex);

			int troopNumber = Math.round(r.nextInt(source.getNrTroops() - 1)) + 1;

			EMMNode temp = new EMMNode();
			temp.setGame(game.clone());
			temp.setManSourceID(source.getId() + "");
			temp.setManDestID(dest.getId() + "");
			temp.setManTroopCount(troopNumber + "");

			AIUtil.resolveMoveAction(temp.getGame().getCurrentPlayer()
					.getTerritoryByName(source.getName()), temp.getGame()
					.getCurrentPlayer().getTerritoryByName(dest.getName()),
					troopNumber);

			temp.setTreePhase(GameTreeNode.RECRUIT);
			temp.setMaxPlayer(false);
			temp.getGame().changeCurrentPlayer();

			nodeCount = 0;
			trimCount = 0;
			/*
			 * System.out.println("MANOEUVRE");
			 */

			double rating = EMM_AB(temp, maxDepth - 1,
					Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

			/*
			 * System.out.println("Rating:" + rating);
			 * System.out.println("Number of nodes: " + nodeCount);
			 * System.out.println("Number of prunings: " + trimCount);
			 * System.out.println("HashMap ratio - " + (double) foundIt /
			 * (double) (foundIt + missedIt) * 100 + "%");
			 * 
			 * System.out.println();
			 */

			if (rating >= maximum) {
				bestGame = temp.getGame();
				maximum = rating;
				reply = new LinkedList<String>();
				if (temp.getManSourceID() != null
						&& temp.getManDestID() != null) {
					reply.add(temp.getManSourceID());
					reply.add(temp.getManDestID());
					reply.add(temp.getManTroopCount());
				}
			}
		}

		if (bestGame != null) {
			game = bestGame;
			game.setCurrentPlayer(current);
		}

		return reply;
	}

}

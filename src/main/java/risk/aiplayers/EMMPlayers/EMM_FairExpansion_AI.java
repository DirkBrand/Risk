package risk.aiplayers.EMMPlayers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import risk.aiplayers.ExpectiminimaxPlayer;
import risk.aiplayers.util.AIFeatures;
import risk.aiplayers.util.AIParameter;
import risk.aiplayers.util.AIUtil;
import risk.aiplayers.util.EMMNode;
import risk.aiplayers.util.NodeType;
import risk.commonObjects.GameState;
import risk.commonObjects.Territory;

public class EMM_FairExpansion_AI extends ExpectiminimaxPlayer {


	public static void main(String[] args) {
		String tempName = args[0];
		int depth = 0;
		if (args.length > 1) {
			depth = Integer.parseInt(args[1]);
		}
		new EMM_FairExpansion_AI(tempName, null, null, 2, depth);
	}

	public EMM_FairExpansion_AI(String name, String opp, String map, int id,
			int depth) {
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
		int k = Math.min(params.EMMMoveAfterAttackBranchingLimit,
				source.getNrTroops() - 1);

		for (int i = 1; i <= k; i++) {
			EMMNode temp = node.clone();
			temp.setTreePhase(NodeType.ATTACK);
			AIUtil.resolveMoveAction(temp.getGame().getCurrentPlayer()
					.getTerritoryByName(source.getName()), temp.getGame()
					.getCurrentPlayer().getTerritoryByName(dest.getName()), i);
			actions.add(temp);
		}

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
			EMMNode maxChild = null;
			double maxRating = Double.NEGATIVE_INFINITY;

			double perc = params.EMMRecruitBranchQualityFactor * 100;
			int length = (int) (100 / perc);
			for (int i = 0; i < length; i++) {
				AIUtil.shuffleArray(perm);

				EMMNode temp = node.clone();
				temp.setTreePhase(NodeType.ATTACK);

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
				double value = getValue(temp);
				if (value >= maxRating) {
					maxRating = value;
					maxChild = temp;
				}
			}

			actions.add(maxChild);
		}

		return actions;
	}

	@Override
	protected LinkedList<EMMNode> getAttackActions(EMMNode node) {
		LinkedList<EMMNode> actions = new LinkedList<EMMNode>();

		LinkedList<EMMNode> attackTerCombos = new LinkedList<EMMNode>();

		// No attack is added as an option by default
		EMMNode noAttackNode = node.clone();
		noAttackNode.setTreePhase(NodeType.MANOEUVRE);
		noAttackNode.setAttackSource("");
		noAttackNode.setAttackDest("");
		noAttackNode.setMoveReq(false);
		noAttackNode.setValue(getValue(noAttackNode));
		attackTerCombos.add(noAttackNode);

		// Get list of possible territory combos
		Iterator<Territory> it = node.getGame().getCurrentPlayer()
				.getTerritories().values().iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			if (t.getNrTroops() > 1) {
				for (Territory n : t.getNeighbours()) {
					Territory temp = node.getGame().getOtherPlayer()
							.getTerritoryByName(n.getName());
					if (temp != null) {
						EMMNode tempNode = node.clone();
						tempNode.setTreePhase(NodeType.RANDOMEVENT);
						tempNode.setAttackSource(t.getName());
						tempNode.setAttackDest(temp.getName());
						tempNode.setValue(getWeightedEval(tempNode));
						attackTerCombos.add(tempNode);
					}
				}
			}
		}

		Collections.sort(attackTerCombos,
				Collections.reverseOrder(EMMNode.EMMNodeComparator));

		// Split on the top k (k = branchingfactor)
		int k = Math
				.min(params.EMMAttackBranchingLimit, attackTerCombos.size());
		for (int i = 0; i < k; i++) {
			actions.add(attackTerCombos.get(i));
		}

		return actions;
	}

	@Override
	protected LinkedList<EMMNode> getMoveActions(EMMNode node) {
		LinkedList<EMMNode> actions = new LinkedList<EMMNode>();

		ArrayList<Integer> manTroopBins = new ArrayList<Integer>();
		ArrayList<EMMNode> manChildren = new ArrayList<EMMNode>();

		int size = AIUtil.updateRegions(node.getGame());
		// Create list of connected components
		LinkedList<LinkedList<Territory>> connComponentBuckets = new LinkedList<LinkedList<Territory>>();
		for (int i = 0; i < size; i++)
			connComponentBuckets.add(new LinkedList<Territory>());

		Iterator<Territory> it = node.getGame().getCurrentPlayer()
				.getTerritories().values().iterator();

		int count = 1;
		while (it.hasNext()) {
			Territory t = it.next();
			connComponentBuckets.get(t.connectedRegion).add(t);
		}

		EMMNode noManChild = node.clone();
		// Add option to not manoeuvre
		manChildren.add(noManChild);
		manTroopBins.add(0);

		for (LinkedList<Territory> bucket : connComponentBuckets) {
			if (bucket.size() > 1) {
				for (Territory src : bucket) {
					if (src.getNrTroops() > 1) {
						for (Territory dest : bucket) {
							if (!src.getName().equals(dest.getName())) {
								EMMNode child = node.clone();
								child.setManSourceID("" + src.getId());
								child.setManDestID("" + dest.getId());
								manTroopBins.add(manTroopBins.get(manTroopBins
										.size() - 1) + src.getNrTroops() - 1);
								manChildren.add(child);

								count += (src.getNrTroops() - 1);
							}
						}
					}
				}
			}
		}

		Random r = new Random();
		// Repeat for the number of allowed branches
		for (int j = 0; j < params.EMMManBranchingLimit; j++) {
			EMMNode maxChild = null;
			double maxRating = Double.NEGATIVE_INFINITY;

			double perc = params.EMMManBranchQualityFactor * 100;
			int length = (int) (100 / perc);
			for (int i = 0; i < length; i++) {
				int index = r.nextInt(count);

				int first, last, middle = -1, nrTroops = -1;
				EMMNode temp = null;

				if (index > 0) {
					first = 0;
					last = manTroopBins.size() - 1;
					middle = (first + last) / 2;

					while (first <= last) {
						int value = manTroopBins.get(middle);

						if (value < index
								&& index < manTroopBins.get(middle + 1)) {
							nrTroops = index - value;
							middle++;
							break;
						} else if (value == index) {
							nrTroops = value - manTroopBins.get(middle - 1);
							break;
						} else if (value < index) {
							first = middle + 1;
						} else {
							last = middle - 1;
						}
						middle = (first + last) / 2;

					}
					temp = manChildren.get(middle).clone();

					temp.setManTroopCount(nrTroops + "");
					AIUtil.resolveMoveAction(
							temp.getGame()
									.getCurrentPlayer()
									.getTerritoryByID(
											Integer.parseInt(temp
													.getManSourceID())),
							temp.getGame()
									.getCurrentPlayer()
									.getTerritoryByID(
											Integer.parseInt(temp
													.getManDestID())), nrTroops);
				} else {
					temp = manChildren.get(0);
				}

				double value = getValue(temp);

				if (value >= maxRating) {
					maxRating = value;
					maxChild = temp;
				}
			}

			maxChild.setTreePhase(NodeType.RECRUIT);
			maxChild.switchMaxPlayer();
			maxChild.getGame().changeCurrentPlayer();

			actions.add(maxChild);
		}

		return actions;
	}

	@Override
	public void recruitPhase(Collection<Territory> myTerritories,
			int numberOfTroops) {
		if (NodeValues.size() > 1000000) {
			NodeValues = null;
			NodeValues = new HashMap<Long, Double>();
		}

		LinkedList<String> reply = new LinkedList<String>();

		GameState bestGame = game;
		double maximum = Double.NEGATIVE_INFINITY;

		int n = numberOfTroops;

		int m = game.getCurrentPlayer().getTerritories().size();

		Boolean[] perm = new Boolean[n + m - 1];
		int d = 0;
		for (int i = 0; i < n; i++)
			perm[d++] = true;

		for (int i = 0; i < m - 1; i++)
			perm[d++] = false;

		for (int j = 0; j < params.EMMRecruitBranchingLimit; j++) {
			EMMNode maxChild = null;
			double maxRating = Double.NEGATIVE_INFINITY;

			double perc = params.EMMRecruitBranchQualityFactor * 100;
			int length = (int) (100 / perc);
			for (int i = 0; i < length; i++) {
				AIUtil.shuffleArray(perm);

				EMMNode node = new EMMNode();
				node.setGame(game.clone());
				node.setTreePhase(NodeType.ATTACK);
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
				double value = getValue(node);
				if (value >= maxRating) {
					maxRating = value;
					maxChild = node;
				}
			}

			nodeCount = 0;
			trimCount = 0;

			/* System.out.println("RECRUITING"); */
			double rating = EMM_AB(maxChild, maxDepth,
					Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
			/*
			 * long startTime = System.nanoTime();
			 * double time = (System.nanoTime() - startTime) / 1000000.0;
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
				bestGame = maxChild.getGame();
				maximum = rating;
				reply = new LinkedList<String>();
				Iterator<Territory> it = maxChild.getGame().getCurrentPlayer()
						.getTerritories().values().iterator();
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
						node.setTreePhase(NodeType.RANDOMEVENT);
						node.setMaxPlayer(true);

						node.setAttackSource(t.getName());
						node.setAttackDest(temp.getName());
						node.setValue(getWeightedEval(node));

						attackTerCombos.add(node);
					}
				}
			}
		}
		Collections.sort(attackTerCombos,
				Collections.reverseOrder(EMMNode.EMMNodeComparator));

		// Play with no attack as an option
		EMMNode noAttackNode = new EMMNode();
		noAttackNode.setGame(game.clone());
		noAttackNode.setTreePhase(NodeType.MANOEUVRE);
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
			EMMNode child = attackTerCombos.get(i);

			nodeCount = 0;
			trimCount = 0;

			/* System.out.println("ATTACKING"); */

			double rating = EMM_AB(child, maxDepth, Double.NEGATIVE_INFINITY,
					Double.POSITIVE_INFINITY);
			/*
			 * long startTime = System.nanoTime();
			 * double time = (System.nanoTime() - startTime) / 1000000.0;
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

		int troops = -1;
		double maxValue = Double.NEGATIVE_INFINITY;

		for (int i = 1; i < lastAttackSource.getNrTroops(); i++) {
			EMMNode temp = new EMMNode();
			temp.setGame(game.clone());
			AIUtil.resolveMoveAction(
					temp.getGame().getCurrentPlayer()
							.getTerritoryByName(lastAttackSource.getName()),
					temp.getGame()
							.getCurrentPlayer()
							.getTerritoryByName(lastAttackDestination.getName()),
					i);
			double value = getValue(temp);
			if (value >= maxValue) {
				troops = i;
				maxValue = value;
			}
		}

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

		ArrayList<Integer> manTroopBins = new ArrayList<Integer>();
		ArrayList<EMMNode> manChildren = new ArrayList<EMMNode>();

		EMMNode noManChild = new EMMNode();
		noManChild.setGame(game.clone());
		// Add option to not manoeuvre
		manChildren.add(noManChild);
		manTroopBins.add(0);

		for (LinkedList<Territory> bucket : connComponentBuckets) {
			if (bucket.size() > 1) {
				for (Territory src : bucket) {
					if (src.getNrTroops() > 1) {
						for (Territory dest : bucket) {
							if (!src.getName().equals(dest.getName())) {
								// Unique source-dest combo
								EMMNode child = new EMMNode();
								child.setGame(game.clone());
								child.setManSourceID("" + src.getId());
								child.setManDestID("" + dest.getId());
								manTroopBins.add(manTroopBins.get(manTroopBins
										.size() - 1) + src.getNrTroops() - 1);
								manChildren.add(child);

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

		// Repeat for the number of allowed branches
		for (int j = 0; j < Math.min(params.EMMManBranchingLimit, count); j++) {
			EMMNode maxChild = null;

			double maxRating = Double.NEGATIVE_INFINITY;

			double perc = params.EMMManBranchQualityFactor * 100;
			int length = (int) (100 / perc);
			for (int i = 0; i < length; i++) {
				int index = r.nextInt(count);

				int first, last, middle = -1, nrTroops = -1;
				EMMNode temp = null;

				if (index > 0) {
					first = 0;
					last = manTroopBins.size() - 1;
					middle = (first + last) / 2;

					while (first <= last) {
						int value = manTroopBins.get(middle);

						if (value < index
								&& index < manTroopBins.get(middle + 1)) {
							nrTroops = index - value;
							middle++;
							break;
						} else if (value == index) {
							nrTroops = value - manTroopBins.get(middle - 1);
							break;
						} else if (value < index) {
							first = middle + 1;
						} else {
							last = middle - 1;
						}
						middle = (first + last) / 2;

					}
					temp = manChildren.get(middle).clone();

					temp.setManTroopCount(nrTroops + "");
					AIUtil.resolveMoveAction(
							temp.getGame()
									.getCurrentPlayer()
									.getTerritoryByID(
											Integer.parseInt(temp
													.getManSourceID())),
							temp.getGame()
									.getCurrentPlayer()
									.getTerritoryByID(
											Integer.parseInt(temp
													.getManDestID())), nrTroops);
				} else {
					temp = manChildren.get(0);
				}

				double value = getValue(temp);

				if (value >= maxRating) {
					maxRating = value;
					maxChild = temp;
				}
			}

			maxChild.setTreePhase(NodeType.RECRUIT);
			maxChild.switchMaxPlayer();
			maxChild.getGame().changeCurrentPlayer();

			nodeCount = 0;
			trimCount = 0;
			/*
			 * System.out.println("MANOEUVRE");
			 */

			double rating = EMM_AB(maxChild, maxDepth - 1,
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
				bestGame = maxChild.getGame();
				maximum = rating;
				reply = new LinkedList<String>();
				if (maxChild.getManSourceID() != null
						&& maxChild.getManDestID() != null) {
					reply.add(maxChild.getManSourceID());
					reply.add(maxChild.getManDestID());
					reply.add(maxChild.getManTroopCount());
				}
			}
		}
		game = bestGame;
		game.setCurrentPlayer(current);

		return reply;
	}

}

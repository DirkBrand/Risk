package risk.aiplayers.EMMPlayers;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import risk.aiplayers.ExpectiminimaxPlayer;
import risk.aiplayers.util.AIUtil;
import risk.aiplayers.util.EMMNode;
import risk.aiplayers.util.GameTreeNode;
import risk.commonObjects.GameState;
import risk.commonObjects.Territory;

public class EMMGreedy_AI extends ExpectiminimaxPlayer {


	public static void main(String[] args) {
		String tempName = args[0];
		/* TODO: Incorporate support for depth specification: 
		int depth = 0;
		if (args.length > 1) {
			depth = Integer.parseInt(args[1]);
		} */
		new EMMGreedy_AI(tempName, null, null, 2, new double[] { 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1 });
	}

	public EMMGreedy_AI(String name, String opp, String map, int id,
			double[] weights) {
		super(name, opp, map, id, 1, weights);

	}

	@Override
	public void recruitPhase(Collection<Territory> myTerritories,
			int numberOfTroops) {

		LinkedList<String> reply = new LinkedList<String>();

		GameState bestGame = null;
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
				double value = AIUtil.eval(node, weights, maxRecruitable);
				if (value > maxRating) {
					maxRating = value;
					maxChild = node;
				}
			}

			double rating = AIUtil.eval(maxChild, weights, maxRecruitable);

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

		// Play with no attack as an option
		EMMNode noAttackNode = new EMMNode();
		noAttackNode.setGame(game.clone());
		noAttackNode.setTreePhase(GameTreeNode.MANOEUVRE);
		noAttackNode.setValue(AIUtil.eval(noAttackNode, weights, maxRecruitable));
		noAttackNode.setAttackSource("");
		noAttackNode.setAttackDest("");
		attackTerCombos.add(noAttackNode);

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

		LinkedList<String> reply = new LinkedList<String>();

		// Split on the top k (k = branchingfactor)
		EMMNode child = attackTerCombos.get(0);

		reply = new LinkedList<String>();
		if (child.getAttackSource() == "" || child.getAttackDest() == "") {
			return reply;
		}
		reply.add(child.getGame().getCurrentPlayer()
				.getTerritoryByName(child.getAttackSource()).getId()
				+ "");
		reply.add(child.getGame().getOtherPlayer()
				.getTerritoryByName(child.getAttackDest()).getId()
				+ "");

		return reply;

	}

	@Override
	public LinkedList<String> getMoveAfterAttack() {
		LinkedList<String> reply = new LinkedList<String>();
		reply.add(lastAttackSource.getId() + "");
		reply.add(lastAttackDestination.getId() + "");
		reply.add((lastAttackSource.getNrTroops() - 1) + "");

		return reply;
	}

	@Override
	// Manoeuvre
	public LinkedList<String> getManSourceDestination() {
		String minID = "";
		int min = Integer.MAX_VALUE;
		String maxID = "";
		int max = Integer.MIN_VALUE;

		Iterator<Territory> it = game.getCurrentPlayer().getTerritories()
				.values().iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			if (t.getNrTroops() > max) {
				max = t.getNrTroops();
				maxID = t.getName();
			}
		}
		Territory source = game.getCurrentPlayer().getTerritoryByName(maxID);

		it = game.getCurrentPlayer().getTerritories().values().iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			if (t.getNrTroops() < min
					&& t.connectedRegion == source.connectedRegion) {
				min = t.getNrTroops();
				minID = t.getName();
			}
		}

		if (minID.length() == 0) {
			LinkedList<String> reply = new LinkedList<String>();
			return reply;
		}
		Territory dest = game.getCurrentPlayer().getTerritoryByName(minID);

		LinkedList<String> reply = new LinkedList<String>();
		if (source.getId() != dest.getId()) {
			int total = rand.nextInt(source.getNrTroops());
			source.setNrTroops(source.getNrTroops() - total);
			dest.setNrTroops(dest.getNrTroops() + total);

			reply.add(source.getId() + "");
			reply.add(dest.getId() + "");
			reply.add(total + "");
		}
		return reply;
	}

	@Override
	protected LinkedList<EMMNode> getMoveAfterAttackActions(EMMNode node) {
		return null;
	}

	@Override
	protected LinkedList<EMMNode> getRecruitActions(EMMNode node, int depth) {
		return null;
	}

	@Override
	protected LinkedList<EMMNode> getAttackActions(EMMNode node) {
		return null;
	}

	@Override
	protected LinkedList<EMMNode> getMoveActions(EMMNode node) {
		return null;
	}

}

package risk.aiplayers.EMMPlayers;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import risk.aiplayers.ExpectiminimaxPlayer;
import risk.aiplayers.util.AIFeatures;
import risk.aiplayers.util.AIParameter;
import risk.aiplayers.util.AIUtil;
import risk.aiplayers.util.EMMNode;
import risk.aiplayers.util.GameTreeNode;
import risk.commonObjects.GameState;
import risk.commonObjects.Territory;

public class EMMBaselineAI extends ExpectiminimaxPlayer {

	Territory lastAttackSource;
	Territory lastAttackDestination;

	String line = "";
	int ind = 0;

	int maxDepth = -1;

	AIParameter params;

	int nodeCount = 0;

	public static void main(String[] args) {
		String tempName = args[0];
		int depth = 0;
		if (args.length > 1) {
			depth = Integer.parseInt(args[1]);
		}
		new EMMBaselineAI(tempName, null, null, 2, depth);
	}

	public EMMBaselineAI(String name, String opp, String map, int id, int depth) {
		super(name, opp, map, id, depth, AIParameter.evalWeights);

	}

	@Override
	protected LinkedList<EMMNode> getRecruitActions(EMMNode node, int number) {
		LinkedList<EMMNode> actions = new LinkedList<EMMNode>();

		double totalTroops = 0;

		Iterator<Territory> it = node.getGame().getCurrentPlayer()
				.getTerritories().values().iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			totalTroops += t.getNrTroops();
		}
		double average = totalTroops
				/ (double) node.getGame().getCurrentPlayer().getTerritories()
						.size();

		// Split on recruit location choices
		/**********************************************/
		it = node.getGame().getCurrentPlayer().getTerritories().values()
				.iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			// Places on frontier territories (ie. on the border)
			if (t.getNrTroops() <= average && !AIUtil.isHinterland(node, t)) {
				EMMNode temp = node.clone();
				temp.setMoveReq(false);
				temp.setTreePhase(EMMNode.ATTACK);

				Territory recT = temp.getGame().getCurrentPlayer()
						.getTerritoryByName(t.getName());
				recT.setNrTroops(recT.getNrTroops() + number);

				actions.add(temp);
			}

		}
		return actions;
	}

	@Override
	protected LinkedList<EMMNode> getAttackActions(EMMNode node) {
		LinkedList<EMMNode> actions = new LinkedList<EMMNode>();

		// Stop attacking if the army strength is low (only attack when in the
		// lead);
		if (AIFeatures.armyStrength(node) < params.EMMAttackThreshold) {
			EMMNode noAttackNode = node.clone();
			noAttackNode.setTreePhase(EMMNode.MANOEUVRE);
			noAttackNode.setAttackSource("");
			noAttackNode.setAttackDest("");
			noAttackNode.setMoveReq(false);
			actions.add(noAttackNode);
			return actions;
		}

		// Split on source destination choices
		/**********************************************/
		Iterator<Territory> it = node.getGame().getCurrentPlayer()
				.getTerritories().values().iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			// Only consider fortified territories
			if (t.getNrTroops() > 1 && !AIUtil.isHinterland(node, t)) {
				EMMNode temp = node.clone();
				temp.setTreePhase(EMMNode.RANDOMEVENT);

				Territory source = temp.getGame().getCurrentPlayer()
						.getTerritoryByName(t.getName());

				for (Territory n : source.getNeighbours()) {
					Territory tempT = temp.getGame().getOtherPlayer()
							.getTerritoryByName(n.getName());
					if (tempT != null
							&& AIParameter.getProbOfWin(t.getNrTroops(),
									tempT.getNrTroops()) >= params.EMMAttackThreshold) {

						temp.setAttackDest(tempT.getName());
						temp.setAttackSource(source.getName());

						actions.add(temp);
					}
				}

			}
		}
		return actions;
	}

	@Override
	protected LinkedList<EMMNode> getMoveActions(EMMNode node) {
		LinkedList<EMMNode> actions = new LinkedList<EMMNode>();
		// Split on source destination choices
		/**********************************************/
		Iterator<Territory> it = node.getGame().getCurrentPlayer()
				.getTerritories().values().iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			// Only consider fortified territories
			if (t.getNrTroops() > 1) {
				EMMNode temp = node.clone();
				temp.setMoveReq(false);

				int min = Integer.MAX_VALUE;
				Territory source = t;
				Territory dest = null;

				it = node.getGame().getCurrentPlayer().getTerritories()
						.values().iterator();
				while (it.hasNext()) {
					Territory d = it.next();
					if (d.getNrTroops() < min
							&& d.connectedRegion == t.connectedRegion) {
						min = d.getNrTroops();
						dest = d;
					}
				}

				AIUtil.resolveMoveAction(source, dest, 1);

				temp.setTreePhase(EMMNode.RECRUIT);
				temp.switchMaxPlayer();
				temp.getGame().changeCurrentPlayer();

				actions.add(temp);

				// Only one action for now
				// break;
			}
		}
		return actions;
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
			temp.setTreePhase(GameTreeNode.ATTACK);
			AIUtil.resolveMoveAction(temp.getGame().getCurrentPlayer()
					.getTerritoryByName(source.getName()), temp.getGame()
					.getCurrentPlayer().getTerritoryByName(dest.getName()), i);
			actions.add(temp);
		}

		return actions;
	}
	
	@Override
	public void recruitPhase(Collection<Territory> myTerritories,
			int numberOfTroops) {

		double maxRating = Double.NEGATIVE_INFINITY;
		LinkedList<String> reply = new LinkedList<String>();

		GameState bestGame = null;
		Iterator<Territory> it = game.getCurrentPlayer().getTerritories()
				.values().iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			// If the territory has neighboring enemy territories
			boolean hinterland = true;
			for (Territory n : t.getNeighbours()) {
				if (game.getOtherPlayer().getTerritoryByName(n.getName()) != null) {
					hinterland = false;
					break;
				}
			}
			if (!hinterland) {
				EMMNode node = new EMMNode();
				node.setGame(game.clone());
				node.setTreePhase(EMMNode.ATTACK);
				node.setMaxPlayer(true);

				node.setRecruitedTer(node.getGame().getCurrentPlayer()
						.getTerritoryByName(t.getName()));
				node.getRecruitedTer().setNrTroops(
						node.getRecruitedTer().getNrTroops() + numberOfTroops);

				nodeCount = 0;

				double rating = EMM_AB(node, maxDepth, Double.MIN_VALUE,
						Double.MAX_VALUE);
				/*
				 * System.out.println("Rating:" + rating);
				 * System.out.println("Number of nodes: " + nodeCount);
				 */

				if (rating > maxRating) {
					maxRating = rating;
					reply = new LinkedList<String>();

					bestGame = node.getGame();

					reply.add(node.getRecruitedTer().getId() + "");
					reply.add(node.getRecruitedTer().getNrTroops() + "");

				}
			}

		}
		game = bestGame;

		APM.sendSuccess(APM.getMesID(), "place_troops", reply);
	}

	@Override
	public LinkedList<String> getAttackSourceDestination() {

		double maxRating = Double.NEGATIVE_INFINITY;
		LinkedList<String> reply = new LinkedList<String>();

		// Split on available territories
		Iterator<Territory> it = game.getCurrentPlayer().getTerritories()
				.values().iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			if (t.getNrTroops() <= 1)
				continue;

			EMMNode node = new EMMNode();
			node.setGame(game.clone());
			node.setTreePhase(EMMNode.RANDOMEVENT);
			node.setMaxPlayer(true);

			Territory source = node.getGame().getCurrentPlayer()
					.getTerritoryByName(t.getName());
			node.setAttackSource(source.getName());

			// Split on neighbours

			for (Territory n : source.getNeighbours()) {
				Territory tempT = node.getGame().getOtherPlayer()
						.getTerritoryByName(n.getName());
				if (tempT != null
						&& AIParameter.getProbOfWin(t.getNrTroops(),
								tempT.getNrTroops()) >= 
								params.EMMAttackThreshold) {
					EMMNode tempNode = node.clone();
					tempNode.setAttackDest(tempT.getName());
					/*
					 * System.out .println(
					 * "*********************************************************************"
					 * );
					 */
					nodeCount = 0;

					double rating = EMM_AB(tempNode, maxDepth,
							Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

					/*
					 * System.out.println("Rating:" + rating);
					 * System.out.println("Number of nodes: " + nodeCount);
					 * 
					 * System.out .println(
					 * "*********************************************************************"
					 * );
					 */

					if (rating > maxRating) {
						maxRating = rating;
						reply = new LinkedList<String>();
						reply.add(tempNode.getGame().getCurrentPlayer()
								.getTerritoryByName(tempNode.getAttackSource())
								.getId()
								+ "");
						reply.add(tempNode.getGame().getOtherPlayer()
								.getTerritoryByName(tempNode.getAttackDest())
								.getId()
								+ "");
					}
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

		int sourceBefore = source.getNrTroops();

		LinkedList<String> reply = new LinkedList<String>();
		if (source.getId() != dest.getId()) {
			int total = source.getNrTroops() + dest.getNrTroops();
			source.setNrTroops((int) (total / 2.0));
			dest.setNrTroops(total - (int) (total / 2.0));

			reply.add(source.getId() + "");
			reply.add(dest.getId() + "");
			reply.add(Math.abs(sourceBefore - source.getNrTroops()) + "");
		}
		return reply;
	}

}
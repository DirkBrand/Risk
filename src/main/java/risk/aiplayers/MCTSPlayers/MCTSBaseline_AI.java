package risk.aiplayers.MCTSPlayers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import risk.aiplayers.MonteCarloTreeSearchPlayer;
import risk.aiplayers.util.AIParameter;
import risk.aiplayers.util.AIUtil;
import risk.aiplayers.util.GameTreeNode;
import risk.aiplayers.util.MCTSNode;
import risk.commonObjects.Territory;

public class MCTSBaseline_AI extends MonteCarloTreeSearchPlayer {

	public static void main(String[] args) {
		String tempName = args[0];
		long time = Long.parseLong(args[1]);
			new MCTSBaseline_AI(tempName, null, null, 2, time);
	}

	public MCTSBaseline_AI(String name, String opp, String map, int id, long time) {
		super(name, opp, map, id, time, new AIParameter());
	}

	@Override
	protected void recruitPhase(Collection<Territory> myTerritories,
			int numberOfTroops) {
		LinkedList<String> reply = new LinkedList<String>();

		int maxID = 0;
		double max = Double.MIN_VALUE;

		Iterator<Territory> it = myTerritories.iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			double sum = 0.0;
			for (Territory n : t.getNeighbours()) {
				if (game.getOtherPlayer().getTerritoryByID(n.getId()) != null) {
					sum += n.getNrTroops();
				}
			}
			if (sum == 0.0)
				continue;
			double ratio = t.getNrTroops() / sum;
			if (ratio > max) {
				max = ratio;
				maxID = t.getId();
			}
		}

		Territory source = game.getCurrentPlayer().getTerritoryByID(maxID);
		source.setNrTroops(source.getNrTroops() + numberOfTroops);
		reply.add(source.getId() + "");
		reply.add(source.getNrTroops() + "");

		APM.sendSuccess(APM.getMesID(), "place_troops", reply);

	}

	protected void calculateMaxChildren(MCTSNode lastNode) {
		int count = 0;
		switch (lastNode.getTreePhase()) {
		case GameTreeNode.RECRUIT: {
			double totalTroops = 0;

			Iterator<Territory> it = lastNode.getGame().getCurrentPlayer()
					.getTerritories().values().iterator();
			while (it.hasNext()) {
				Territory t = it.next();
				totalTroops += t.getNrTroops();
			}
			double average = totalTroops
					/ lastNode.getGame().getCurrentPlayer().getTerritories()
							.size();

			boolean atLeastOne = false;
			while (!atLeastOne) {
				it = lastNode.getGame().getCurrentPlayer().getTerritories()
						.values().iterator();
				while (it.hasNext()) {
					Territory t = it.next();
					if (t.getNrTroops() <= average
							&& !AIUtil.isHinterland(lastNode, t)) {
						count++;
						atLeastOne = true;
					}
				}

				average += 1;
			}
			lastNode.ave = average;
			break;
		}

		case GameTreeNode.ATTACK: {
			Iterator<Territory> it = lastNode.getGame().getCurrentPlayer()
					.getTerritories().values().iterator();
			while (it.hasNext()) {
				Territory t = it.next();
				// Only consider fortified frontier territories
				if (t.getNrTroops() > 1 && !AIUtil.isHinterland(lastNode, t)) {
					for (Territory n : t.getNeighbours()) {

						Territory tempT = lastNode.getGame().getOtherPlayer()
								.getTerritoryByName(n.getName());
						// Only consider targets that have less troops than the
						// source
						if (tempT != null
								&& AIParameter.getProbOfWin(t.getNrTroops(),
										tempT.getNrTroops()) >= params.MCTSAttackThreshold) {
							count++;
						}
					}
				}
			}
			if (count == 0)
				count = 1;
			break;
		}
		case GameTreeNode.RANDOMEVENT: {
			int sourceTroops = lastNode.getGame().getCurrentPlayer()
					.getTerritoryByName(lastNode.getAttackSource())
					.getNrTroops();
			int destTroops = lastNode.getGame().getOtherPlayer()
					.getTerritoryByName(lastNode.getAttackDest()).getNrTroops();

			if (sourceTroops == 2) {
				count = 2;
			} else if (sourceTroops == 3) {
				if (destTroops == 2 || destTroops == 1) {
					count = 2;
				} else {
					count = 3;
				}

			} else {
				if (destTroops == 2 || destTroops == 1) {
					count = 2;
				} else {
					count = 3;
				}
			}
			break;
		}
		case GameTreeNode.MANOEUVRE: {
			Iterator<Territory> it = lastNode.getGame().getCurrentPlayer()
					.getTerritories().values().iterator();
			while (it.hasNext()) {
				Territory t = it.next();
				// Only consider fortified territories
				if (t.getNrTroops() > 1) {
					it = lastNode.getGame().getCurrentPlayer().getTerritories()
							.values().iterator();
					while (it.hasNext()) {
						Territory d = it.next();
						if (d.connectedRegion == t.connectedRegion) {
							count++;
						}
					}
				}
			}
			break;
		}
		}

		lastNode.setMaxChildren(count);
	}

	// EXPANSION (One child at a time)
	@Override
	protected MCTSNode Expand(MCTSNode lastNode) {

		switch (lastNode.getTreePhase()) {
		case GameTreeNode.RECRUIT: {
			int troops = AIUtil.calculateRecruitedTroops(lastNode);

			double average = lastNode.ave;

			while (true) {
				Iterator<Territory> it = lastNode.getGame().getCurrentPlayer()
						.getTerritories().values().iterator();
				while (it.hasNext()) {
					Territory t = it.next();
					if (t.getNrTroops() <= average
							&& !AIUtil.isHinterland(lastNode, t)) {

						// Checks if child already exists
						boolean unique = true;
						for (MCTSNode child : lastNode.getChildren()) {
							if (child.getRecruitedTer().getName()
									.equalsIgnoreCase(t.getName())) {
								unique = false;
								break;
							}
						}

						// Enforces adding unique children
						if (unique) {
							MCTSNode newChild = lastNode.clone();
							newChild.setVisitCount(0);
							newChild.setWinCount(0);
							newChild.setParent(lastNode);
							newChild.setChildren(new ArrayList<MCTSNode>());

							newChild.setRecruitedNumber(troops);
							newChild.setRecruitedTer(newChild.getGame()
									.getCurrentPlayer()
									.getTerritoryByName(t.getName()));
							newChild.setTreePhase(GameTreeNode.ATTACK);

							AIUtil.resolveRecruit(newChild,
									newChild.getRecruitedTer());

							newChild.setAttackSource(newChild.getRecruitedTer()
									.getName());
							newChild.setMoveReq(false);

							calculateMaxChildren(newChild);

							newChild.depth = lastNode.depth + 1;
							if (newChild.depth > maxTreeDepth) {
								maxTreeDepth = newChild.depth;
							}

							lastNode.addChild(newChild);
							return newChild;
						}
					}
				}
				average += 1;
			}
		}
		case GameTreeNode.ATTACK: {
			// Move after attack is resolved by moving all troops across.
			if (lastNode.moveRequired()) {
				AIUtil.resolveMoveAction(lastNode.getGame().getCurrentPlayer()
						.getTerritoryByName(lastNode.getAttackSource()),
						lastNode.getGame().getCurrentPlayer()
								.getTerritoryByName(lastNode.getAttackDest()),
						1);

				lastNode.setAttackSource(lastNode.getAttackDest());

				lastNode.setMoveReq(false);
			}

			boolean atLeastOne = false;

			Iterator<Territory> it = lastNode.getGame().getCurrentPlayer()
					.getTerritories().values().iterator();
			while (it.hasNext()) {
				Territory t = it.next();
				// Only consider fortified territories
				if (t.getNrTroops() > 1 && !AIUtil.isHinterland(lastNode, t)) {
					for (Territory n : t.getNeighbours()) {

						Territory tempT = lastNode.getGame().getOtherPlayer()
								.getTerritoryByName(n.getName());
						// Only consider targets that have less troops than the
						// source
						if (tempT != null
								&& AIParameter.getProbOfWin(t.getNrTroops(),
										tempT.getNrTroops()) >= params.MCTSAttackThreshold) {

							// Checks if child already exists
							boolean unique = true;
							for (MCTSNode child : lastNode.getChildren()) {
								if (child.getAttackSource().equalsIgnoreCase(
										t.getName())
										&& child.getAttackDest()
												.equalsIgnoreCase(n.getName())) {
									unique = false;
									break;
								}
							}

							// Add unique child to existing children
							if (unique) {
								atLeastOne = true;

								MCTSNode newChild = lastNode.clone();
								newChild.setVisitCount(0);
								newChild.setWinCount(0);
								newChild.setParent(lastNode);
								newChild.setChildren(new ArrayList<MCTSNode>());

								newChild.setTreePhase(GameTreeNode.RANDOMEVENT);

								newChild.setAttackSource(t.getName());
								newChild.setAttackDest(n.getName());

								calculateMaxChildren(newChild);

								newChild.depth = lastNode.depth + 1;
								if (newChild.depth > maxTreeDepth) {
									maxTreeDepth = newChild.depth;
								}

								lastNode.addChild(newChild);
								return newChild;
							}

						}
					}
				}
			}
			// No attack (Done attacking)
			if (!atLeastOne) {
				MCTSNode newChild = lastNode.clone();
				newChild.setVisitCount(0);
				newChild.setWinCount(0);
				newChild.setParent(lastNode);
				newChild.setChildren(new ArrayList<MCTSNode>());

				newChild.setAttackSource("");
				newChild.setAttackDest("");
				newChild.setTreePhase(GameTreeNode.MANOEUVRE);

				calculateMaxChildren(newChild);

				newChild.depth = lastNode.depth + 1;
				if (newChild.depth > maxTreeDepth) {
					maxTreeDepth = newChild.depth;
				}

				lastNode.addChild(newChild);
				return newChild;
			}
			break;
		}
		case GameTreeNode.RANDOMEVENT: {

			int sourceTroops = lastNode.getGame().getCurrentPlayer()
					.getTerritoryByName(lastNode.getAttackSource())
					.getNrTroops();
			int destTroops = lastNode.getGame().getOtherPlayer()
					.getTerritoryByName(lastNode.getAttackDest()).getNrTroops();

			MCTSNode newChild = lastNode.clone();
			newChild.setVisitCount(0);
			newChild.setWinCount(0);
			newChild.setParent(lastNode);
			newChild.setChildren(new ArrayList<MCTSNode>());

			newChild.setTreePhase(GameTreeNode.ATTACK);

			// Attacker
			if (sourceTroops == 2) {
				if (destTroops == 2 || destTroops == 1) {
					// Defender Wins
					if (lastNode.getChildren().size() == 0) {
						newChild.setDiceRolls(0, 0, 1, 0, 6);
					}
					// Attacker Wins
					else {
						newChild.setDiceRolls(0, 0, 6, 0, 1);
					}
				} else {
					// Defender Wins
					if (lastNode.getChildren().size() == 0) {
						newChild.setDiceRolls(0, 0, 1, 5, 6);
					}
					// Attacker Wins
					else {
						newChild.setDiceRolls(0, 0, 6, 1, 2);
					}
				}
			} else if (sourceTroops == 3) {
				if (destTroops == 2 || destTroops == 1) {
					// Attacker Wins
					if (lastNode.getChildren().size() == 0) {
						newChild.setDiceRolls(0, 5, 6, 0, 1);
					}
					// Defender Wins
					else {
						newChild.setDiceRolls(0, 1, 2, 0, 6);
					}

				} else {
					// Defender Wins
					if (lastNode.getChildren().size() == 0) {
						newChild.setDiceRolls(0, 1, 2, 5, 6);
					}
					// Draw
					else if (lastNode.getChildren().size() == 1) {
						newChild.setDiceRolls(0, 1, 6, 2, 5);
					}
					// Attacker Wins
					else {
						newChild.setDiceRolls(0, 5, 6, 1, 2);
					}
				}

			} else {
				if (destTroops == 2 || destTroops == 1) {
					// Attacker Wins
					if (lastNode.getChildren().size() == 0) {
						newChild.setDiceRolls(4, 5, 6, 0, 1);
					}
					// Defender Wins
					else {
						newChild.setDiceRolls(1, 2, 3, 0, 6);
					}

				} else {
					// Attacker Wins
					if (lastNode.getChildren().size() == 0) {
						newChild.setDiceRolls(4, 5, 6, 1, 2);
					}
					// Draw
					else if (lastNode.getChildren().size() == 1) {
						newChild.setDiceRolls(1, 2, 6, 3, 5);
					}
					// Defender Wins
					else {
						newChild.setDiceRolls(1, 2, 3, 5, 6);
					}
				}
			}

			AIUtil.resolveAttackAction(newChild);
			calculateMaxChildren(newChild);

			newChild.depth = lastNode.depth + 1;
			if (newChild.depth > maxTreeDepth) {
				maxTreeDepth = newChild.depth;
			}

			lastNode.addChild(newChild);

			return newChild;
		} // Splits on possible manoeuvres to the minimum troop territory
		case GameTreeNode.MANOEUVRE: {
			boolean atLeastOne = false;
			Iterator<Territory> it = lastNode.getGame().getCurrentPlayer()
					.getTerritories().values().iterator();
			while (it.hasNext()) {
				Territory t = it.next();

				// Only consider fortified territories
				if (t.getNrTroops() > 1) {

					int min = Integer.MAX_VALUE;
					Territory dest = null;

					it = lastNode.getGame().getCurrentPlayer().getTerritories()
							.values().iterator();
					while (it.hasNext()) {
						Territory d = it.next();
						if (d.getNrTroops() < min
								&& d.connectedRegion == t.connectedRegion) {
							min = d.getNrTroops();
							dest = d;
						}
					}

					// Checks if child already exists (only sources are unique)
					boolean unique = true;
					for (MCTSNode child : lastNode.getChildren()) {
						if (child.getManSource().getName()
								.equalsIgnoreCase(t.getName())) {
							unique = false;
							break;
						}
					}

					// Add unique child to existing children
					if (unique) {
						atLeastOne = true;

						MCTSNode newChild = lastNode.clone();
						newChild.setVisitCount(0);
						newChild.setWinCount(0);
						newChild.setParent(lastNode);
						newChild.setChildren(new ArrayList<MCTSNode>());

						newChild.setTreePhase(GameTreeNode.RECRUIT);

						newChild.setManSource(newChild.getGame()
								.getCurrentPlayer()
								.getTerritoryByName(t.getName()));

						newChild.setManDest(newChild.getGame()
								.getCurrentPlayer()
								.getTerritoryByName(dest.getName()));

						AIUtil.resolveMoveAction(newChild.getManSource(),
								newChild.getManDest(), 1);

						newChild.switchMaxPlayer();
						newChild.getGame().changeCurrentPlayer();

						calculateMaxChildren(newChild);

						newChild.depth = lastNode.depth + 1;
						if (newChild.depth > maxTreeDepth) {
							maxTreeDepth = newChild.depth;
						}

						lastNode.addChild(newChild);
						return newChild;
					}
				}
			}
			if (!atLeastOne) {
				MCTSNode newChild = lastNode.clone();
				newChild.setVisitCount(0);
				newChild.setWinCount(0);
				newChild.setParent(lastNode);
				newChild.setChildren(new ArrayList<MCTSNode>());

				newChild.setTreePhase(GameTreeNode.RECRUIT);

				newChild.switchMaxPlayer();
				newChild.getGame().changeCurrentPlayer();

				calculateMaxChildren(newChild);

				newChild.depth = lastNode.depth + 1;
				if (newChild.depth > maxTreeDepth) {
					maxTreeDepth = newChild.depth;
				}

				lastNode.addChild(newChild);
				return newChild;
			}
			break;
		}
		}

		return null;
	}

	/****************************************************************/

	@Override
	public LinkedList<String> getAttackSourceDestination() {
		LinkedList<String> reply = new LinkedList<String>();

		MCTSNode root = new MCTSNode();
		root.setTreePhase(GameTreeNode.ATTACK);
		root.setGame(game.clone());
		root.setMaxPlayer(true);
		root.setVisitCount(0);
		root.setWinCount(0);
		root.depth = 1;
		root.setChildren(new ArrayList<MCTSNode>());

		calculateMaxChildren(root);
		if (root.maxChildren() == 0) {
			return reply;
		} else if (root.maxChildren() == 1) {
			boolean atLeastOne = false;
			Iterator<Territory> it = root.getGame().getCurrentPlayer()
					.getTerritories().values().iterator();
			while (it.hasNext()) {
				Territory t = it.next();
				// Only consider fortified territories
				if (t.getNrTroops() > 1 && !AIUtil.isHinterland(root, t)) {
					for (Territory n : t.getNeighbours()) {

						Territory tempT = root.getGame().getOtherPlayer()
								.getTerritoryByName(n.getName());
						// Only consider targets that have less troops than the
						// source
						if (tempT != null
								&& AIParameter.getProbOfWin(t.getNrTroops(),
										tempT.getNrTroops()) >= params.MCTSAttackThreshold) {

							reply.add(t.getId() + "");
							reply.add(n.getId() + "");
							return reply;
						}

					}
				}
			}

			// No attack (Done attacking)
			if (!atLeastOne) {
				System.out.println("NAIVE: " + "No Attack");
				return reply;
			}
		}

		treeDepth = 0;
		maxTreeDepth = Integer.MIN_VALUE;
		treeNodeCount = 0;

		MCTSNode action = MCTSSearch(root);

		/*
		 * double time = (System.nanoTime() - startTime) / 1000000.0;
		 * 
		 * System.out.println("Ended MCTS in " + time + " ms");
		 * 
		 * System.out.println("Depth : " + maxTreeDepth);
		 * System.out.println("Node Count: " + treeNodeCount);
		 * System.out.println("Root playouts : " + root.getVisitCount());
		 */

		if (action.getAttackSource() == null || action.getAttackDest() == null) {
			System.out.println("NAIVE: " + "No Attack");
			return reply;
		}

		Territory Source = action.getGame().getCurrentPlayer()
				.getTerritoryByName(action.getAttackSource());
		Territory Dest = action.getGame().getOtherPlayer()
				.getTerritoryByName(action.getAttackDest());

		if (Source == null || Dest == null) {
			System.out.println("NAIVE: " + "No Attack");
			return reply;
		}
		reply.add(Source.getId() + "");
		reply.add(Dest.getId() + "");

		System.out.println("NAIVE: " + "Attacking from " + Source.toString()
				+ "  to  " + Dest.toString());

		return reply;

	}

	@Override
	public LinkedList<String> getMoveAfterAttack() {
		LinkedList<String> reply = new LinkedList<String>();
		reply.add(lastAttackSource.getId() + "");
		reply.add(lastAttackDestination.getId() + "");
		reply.add((lastAttackSource.getNrTroops() - 1) + ""); //Baseline Strategy. But did it follow it ?

		return reply;
	}

	@Override
	public LinkedList<String> getManSourceDestination() {
		int minID = -1;
		int min = Integer.MAX_VALUE;
		int maxID = -1;
		int max = Integer.MIN_VALUE;

		Iterator<Territory> it = game.getCurrentPlayer().getTerritories()
				.values().iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			if (t.getNrTroops() > max) {
				max = t.getNrTroops();
				maxID = t.getId();
			}
		}
		Territory source = game.getCurrentPlayer().getTerritoryByID(maxID);

		it = game.getCurrentPlayer().getTerritories().values().iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			if (t.getNrTroops() < min
					&& t.connectedRegion == source.connectedRegion) {
				min = t.getNrTroops();
				minID = t.getId();
			}
		}

		if (minID == -1) {
			LinkedList<String> reply = new LinkedList<String>();
			System.out.println("NAIVE: " + "No Manoeuvre");
			return reply;
		}
		Territory dest = game.getCurrentPlayer().getTerritoryByID(minID);

		int sourceBefore = source.getNrTroops();

		LinkedList<String> reply = new LinkedList<String>();
		if (source.getId() != dest.getId()) {
			int total = source.getNrTroops() + dest.getNrTroops();
			source.setNrTroops((int) (total / 2.0));
			dest.setNrTroops(total - (int) (total / 2.0));

			reply.add(source.getId() + "");
			reply.add(dest.getId() + "");
			reply.add(Math.abs(sourceBefore - source.getNrTroops()) + "");
			System.out.println("NAIVE: " + "Manoeuvre from "
					+ source.toString() + " to " + dest.toString() + " with "
					+ reply.get(2));
		} else {
			System.out.println("NAIVE: " + "No Manoeuvre");
		}

		return reply;
	}

}

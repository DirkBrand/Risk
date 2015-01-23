package risk.aiplayers.MCTSPlayers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import risk.aiplayers.MonteCarloTreeSearchPlayer;
import risk.aiplayers.util.AIParameter;
import risk.aiplayers.util.AIUtil;
import risk.aiplayers.util.MCTSNode;
import risk.aiplayers.util.NodeType;
import risk.commonObjects.Territory;

public class MCTSRandom_AI extends MonteCarloTreeSearchPlayer {

	boolean myTurn = false;

	public static void main(String[] args) {
		String tempName = args[0];
		long time = Long.parseLong(args[1]);
		new MCTSRandom_AI(tempName, null, null, 2, time);
	}

	public MCTSRandom_AI(String name, String opp, String map, int id, long time) {
		super(name, opp, map, id, time, new AIParameter());

	}

	protected void calculateMaxChildren(MCTSNode lastNode) {
		int count = 0;
		switch (lastNode.getTreePhase()) {
		case RECRUIT: {
			int troops = AIUtil.calculateRecruitedTroops(lastNode);

			int n = troops;
			int m = lastNode.getGame().getCurrentPlayer().getTerritories()
					.size();
			count = (int) (AIUtil.nCk(n + m - 1, n));

			break;
		}
		case ATTACK: {
			count = 1;
			lastNode.noAttackAdded = false;
			Iterator<Territory> it = lastNode.getGame().getCurrentPlayer()
					.getTerritories().values().iterator();
			while (it.hasNext()) {
				Territory t = it.next();
				// Only consider fortified territories
				if (t.getNrTroops() > 1) {
					for (Territory n : t.getNeighbours()) {
						Territory tempT = lastNode.getGame().getOtherPlayer()
								.getTerritoryByName(n.getName());
						// Only consider targets that have less troops than the
						// source
						if (tempT != null) {
							count++;
						}
					}
				}
			}

			break;
		}
		case MOVEAFTERATTACK: {
			count = lastNode.getGame().getCurrentPlayer()
					.getTerritoryByName(lastNode.getParent().getAttackSource())
					.getNrTroops() - 1;
			if (count == 0) {
				System.out.println("WTF");
			}
			break;
		}
		case RANDOMEVENT: {
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
		case MANOEUVRE: {
			int size = AIUtil.updateRegions(lastNode.getGame());
			// Create list of connected components
			lastNode.setConnComponentBuckets(new LinkedList<LinkedList<Territory>>());
			for (int i = 0; i < size; i++) {
				lastNode.getConnComponentBuckets().add(
						new LinkedList<Territory>());
			}

			// Since not maneuvering is an option
			count = 1;

			Iterator<Territory> it = lastNode.getGame().getCurrentPlayer()
					.getTerritories().values().iterator();
			while (it.hasNext()) {
				Territory t = it.next();
				lastNode.getConnComponentBuckets().get(t.connectedRegion)
						.add(t);
			}

			for (LinkedList<Territory> bucket : lastNode
					.getConnComponentBuckets()) {
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
			break;
		}
		}

		lastNode.setMaxChildren(count);
	}

	// EXPANSION (One child at a time)
	@Override
	protected MCTSNode Expand(MCTSNode lastNode) {
		// System.out.println(lastNode.getTreePhaseText());
		switch (lastNode.getTreePhase()) {
		case RECRUIT: {
			// System.out.println("Recruit 1");

			// Create permutation array
			int n = AIUtil.calculateRecruitedTroops(lastNode);

			int m = lastNode.getGame().getCurrentPlayer().getTerritories()
					.size();

			Boolean[] perm = new Boolean[n + m - 1];
			int d = 0;
			for (int i = 0; i < n; i++)
				perm[d++] = true;

			for (int i = 0; i < m - 1; i++)
				perm[d++] = false;

			MCTSNode maxChild = null;

			while (maxChild == null) {

				AIUtil.shuffleArray(perm);

				MCTSNode tempChild = lastNode.clone();

				Iterator<Territory> it = tempChild.getGame().getCurrentPlayer()
						.getTerritories().values().iterator();
				Territory current = it.next();
				// Place troops according to permutation
				for (int p = 0; p < perm.length; p++) {
					if (perm[p]) {
						current.incrementTroops();
						tempChild.setAttackSource(current.getName());
					} else {
						current = it.next();
					}
				}
				maxChild = tempChild;
			}

			maxChild.setVisitCount(0);
			maxChild.setWinCount(0);
			maxChild.setParent(lastNode);
			maxChild.setChildren(new ArrayList<MCTSNode>());

			maxChild.setTreePhase(NodeType.ATTACK);

			maxChild.setMoveReq(false);

			calculateMaxChildren(maxChild);

			maxChild.depth = lastNode.depth + 1;
			if (maxChild.depth > maxTreeDepth) {
				maxTreeDepth = maxChild.depth;
			}

			lastNode.addChild(maxChild);
			return maxChild;
		}

		case ATTACK: {

			Random r = new Random();

			if (lastNode.attackChildren == null) {
				lastNode.attackChildren = new ArrayList<MCTSNode>();
				MCTSNode noAttackChild = lastNode.clone();
				noAttackChild.setTreePhase(NodeType.MANOEUVRE);
				noAttackChild.setAttackSource("");
				noAttackChild.setAttackDest("");
				// Add option to not attack
				lastNode.attackChildren.add(noAttackChild);

				// Populate treeset
				Iterator<Territory> it = lastNode.getGame().getCurrentPlayer()
						.getTerritories().values().iterator();
				while (it.hasNext()) {
					Territory t = it.next();
					if (t.getNrTroops() > 1) {
						for (Territory n : t.getNeighbours()) {
							Territory dest = lastNode.getGame()
									.getOtherPlayer()
									.getTerritoryByName(n.getName());
							if (dest != null) {
								MCTSNode newChild = lastNode.clone();
								newChild.setAttackSource(t.getName());
								newChild.setAttackDest(dest.getName());
								lastNode.attackChildren.add(newChild);
							}
						}
					}
				}
			}

			int index = 0;

			if (lastNode.attackChildren.size() == 0) {
				MCTSNode noAttackChild = lastNode.clone();
				noAttackChild.setAttackSource("");
				noAttackChild.setAttackDest("");
				noAttackChild.setTreePhase(NodeType.MANOEUVRE);
				// Add option to not attack
				lastNode.attackChildren.add(noAttackChild);
			}
			index = r.nextInt(lastNode.attackChildren.size());

			int i = 0;
			// System.out.println(lastNode.attackChildren.size() + " <-> " +
			// lastNode.maxChildren());
			Iterator<MCTSNode> iterator = lastNode.attackChildren.iterator();
			MCTSNode child = iterator.next();
			while (iterator.hasNext() && i <= index) {
				child = iterator.next();
				i++;
			}
			// System.out.println(lastNode.attackChildren.remove(child));
			lastNode.attackChildren.remove(child);

			child.setVisitCount(0);
			child.setWinCount(0);
			child.setParent(lastNode);
			child.setChildren(new ArrayList<MCTSNode>());

			if (child.getAttackDest().length() == 0) {
				child.setTreePhase(NodeType.MANOEUVRE);
			} else {
				child.setTreePhase(NodeType.RANDOMEVENT);
			}

			child.setMoveReq(false);

			calculateMaxChildren(child);

			child.depth = lastNode.depth + 1;
			if (child.depth > maxTreeDepth) {
				maxTreeDepth = child.depth;
			}

			lastNode.addChild(child);
			return child;
		}

		case RANDOMEVENT: {

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

			if (sourceTroops == 1) {
				System.out.println("WTF");
			}

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
			if (newChild.moveRequired()) {
				newChild.setTreePhase(NodeType.MOVEAFTERATTACK);
			} else {
				newChild.setTreePhase(NodeType.ATTACK);
			}
			calculateMaxChildren(newChild);

			newChild.depth = lastNode.depth + 1;
			if (newChild.depth > maxTreeDepth) {
				maxTreeDepth = newChild.depth;
			}

			lastNode.addChild(newChild);

			return newChild;
		}

		case MOVEAFTERATTACK: {
			int totalTroops = lastNode.getGame().getCurrentPlayer()
					.getTerritoryByName(lastNode.getAttackSource())
					.getNrTroops();
			while (true) {
				int troops = rand.nextInt(totalTroops - 1) + 1;

				// Checks if child already exists
				boolean unique = true;
				for (MCTSNode child : lastNode.getChildren()) {
					if (child.getMoveAfterAttackCount() == troops) {
						unique = false;
						break;
					}
				}

				// Add unique child to existing children
				if (unique) {
					MCTSNode newChild = lastNode.clone();
					newChild.setVisitCount(0);
					newChild.setWinCount(0);
					newChild.setParent(lastNode);
					newChild.setChildren(new ArrayList<MCTSNode>());

					newChild.setTreePhase(NodeType.ATTACK);

					AIUtil.resolveMoveAction(
							newChild.getGame()
									.getCurrentPlayer()
									.getTerritoryByName(
											newChild.getAttackSource()),
							newChild.getGame()
									.getCurrentPlayer()
									.getTerritoryByName(
											newChild.getAttackDest()), troops);

					newChild.setMoveAfterAttackCount(troops);

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

		case MANOEUVRE: {

			// No territories with more than 1 troop. Thus only option is
			// not
			// maneuvering
			if (lastNode.maxChildren() == 1) {
				MCTSNode newChild = lastNode.clone();
				newChild.setVisitCount(0);
				newChild.setWinCount(0);
				newChild.setParent(lastNode);
				newChild.setChildren(new ArrayList<MCTSNode>());

				newChild.setTreePhase(NodeType.RECRUIT);

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

			Random r = new Random();

			if (lastNode.manChildren == null) {
				lastNode.manChildren = new ArrayList<MCTSNode>();
				MCTSNode noManChild = lastNode.clone();
				// Add option to not attack
				lastNode.manChildren.add(noManChild);

				// Populate treeset
				LinkedList<LinkedList<Territory>> connComponentBuckets = lastNode
						.getConnComponentBuckets();
				for (LinkedList<Territory> bucket : connComponentBuckets) {
					if (bucket.size() > 1) {
						for (Territory src : bucket) {
							if (src.getNrTroops() > 1) {
								for (Territory dest : bucket) {
									if (!src.getName().equals(dest.getName())) {
										// Unique source-dest combo
										for (int i = 1; i < src.getNrTroops(); i++) {
											MCTSNode newChild = lastNode
													.clone();
											newChild.setManSource(src);
											newChild.setManDest(dest);
											newChild.setManTroopCount(i + "");
											AIUtil.resolveMoveAction(
													newChild.getGame()
															.getCurrentPlayer()
															.getTerritoryByName(
																	src.getName()),
													newChild.getGame()
															.getCurrentPlayer()
															.getTerritoryByName(
																	dest.getName()),
													i);
											lastNode.manChildren.add(newChild);
										}
									}
								}
							}
						}
					}
				}
			}

			int index = r.nextInt(lastNode.manChildren.size());
			int i = 0;
			Iterator<MCTSNode> iterator = lastNode.manChildren.iterator();

			MCTSNode child = iterator.next();
			while (iterator.hasNext() && i < index) {
				child = iterator.next();
				i++;
			}
			lastNode.manChildren.remove(child);

			child.setVisitCount(0);
			child.setWinCount(0);
			child.setParent(lastNode);
			child.setChildren(new ArrayList<MCTSNode>());

			child.setTreePhase(NodeType.RECRUIT);

			child.setMoveReq(false);
			child.switchMaxPlayer();
			child.getGame().changeCurrentPlayer();

			calculateMaxChildren(child);

			child.depth = lastNode.depth + 1;
			if (child.depth > maxTreeDepth) {
				maxTreeDepth = child.depth;
			}

			lastNode.addChild(child);
			return child;
		}

		}

		return null;
	}

	/****************************************************************/

	@Override
	public void recruitPhase(Collection<Territory> meTerritories, int number) {
		System.gc();

		LinkedList<String> reply = new LinkedList<String>();

		myTurn = true;

		MCTSNode root = new MCTSNode();
		root.setTreePhase(NodeType.RECRUIT);
		root.setGame(game.clone());
		root.setMaxPlayer(true);
		root.setVisitCount(0);
		root.setWinCount(0);
		root.depth = 1;
		root.setChildren(new ArrayList<MCTSNode>());
		int n = number;
		int m = game.getCurrentPlayer().getTerritories().size();
		root.setMaxChildren((int) (AIUtil.nCk(n + m - 1, n)));

		treeDepth = 0;
		maxTreeDepth = Integer.MIN_VALUE;
		treeNodeCount = 0;

		MCTSNode action = MCTSSearch(root);

		Iterator<Territory> it = action.getGame().getCurrentPlayer()
				.getTerritories().values().iterator();
		while (it.hasNext()) {
			Territory T = it.next();
			reply.add(T.getId() + "");
			reply.add(T.getNrTroops() + "");
		}
		game = action.getGame();

		APM.sendSuccess(APM.getMesID(), "place_troops", reply);

	}

	
	@Override
	public LinkedList<String> getAttackSourceDestination() {
		System.gc();
		numberOfMovesTaken++;

		LinkedList<String> reply = new LinkedList<String>();

		MCTSNode root = new MCTSNode();
		root.setTreePhase(NodeType.ATTACK);
		root.setGame(game.clone());
		root.setMaxPlayer(true);
		root.setVisitCount(0);
		root.setWinCount(0);
		root.depth = 1;
		root.setChildren(new ArrayList<MCTSNode>());
		calculateMaxChildren(root);

		if (root.maxChildren() == 1) {
			return reply;
		}

		treeDepth = 0;
		maxTreeDepth = Integer.MIN_VALUE;
		treeNodeCount = 0;

		MCTSNode action = MCTSSearch(root);

		Territory Source = action.getGame().getCurrentPlayer()
				.getTerritoryByName(action.getAttackSource());
		Territory Dest = action.getGame().getOtherPlayer()
				.getTerritoryByName(action.getAttackDest());

		if (Source == null || Dest == null) {
			System.out.println("Not attacking");
			return reply;
		}
		System.out.println("Attacking from " + Source.toString() + "  to  "
				+ Dest.toString());
		reply.add(Source.getId() + "");
		reply.add(Dest.getId() + "");

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
		System.gc();

		LinkedList<String> reply = new LinkedList<String>();

		MCTSNode root = new MCTSNode();
		root.setTreePhase(NodeType.MANOEUVRE);
		root.setGame(game.clone());
		root.setMaxPlayer(true);
		root.setVisitCount(0);
		root.setWinCount(0);
		root.depth = 1;
		root.setChildren(new ArrayList<MCTSNode>());
		calculateMaxChildren(root);

		treeDepth = 0;
		maxTreeDepth = Integer.MIN_VALUE;
		treeNodeCount = 0;

		MCTSNode action = MCTSSearch(root);
		// MCTSNode action = MCTSSearchInfinite(root);

		Territory Source = action.getManSource();
		Territory Dest = action.getManDest();

		/*
		 * if (tv != null) tv.close(); tv = new TreeView(root);
		 * tv.showTree("After " + timeForMCTSSearch + " ms");
		 */

		if (Source == null || Dest == null || action.getManTroopCount() == null) {
			System.out.println("No Manoeuvre");
			return reply;
		}
		reply.add(Source.getId() + "");
		reply.add(Dest.getId() + "");
		reply.add(action.getManTroopCount() + "");

		System.out.println("Manoeuvre from " + Source.toString() + " to "
				+ Dest.toString() + " with " + action.getManTroopCount());

		AIUtil.resolveMoveAction(
				game.getCurrentPlayer().getTerritoryByName(Source.getName()),
				game.getCurrentPlayer().getTerritoryByName(Dest.getName()),
				Integer.parseInt(action.getManTroopCount()));

		return reply;
	}

}

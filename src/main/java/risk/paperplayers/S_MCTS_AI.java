package risk.paperplayers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import risk.aiplayers.MonteCarloTreeSearchPlayer;
import risk.aiplayers.util.AIFeatures;
import risk.aiplayers.util.AIParameter;
import risk.aiplayers.util.AIUtil;
import risk.aiplayers.util.GameTreeNode;
import risk.aiplayers.util.MCTSNode;
import risk.commonObjects.Territory;

public class S_MCTS_AI extends MonteCarloTreeSearchPlayer {

	MCTSNode globalNode;

	boolean myTurn = false;

	public static void main(String[] args) {
		String tempName = args[0];
		long time = Long.parseLong(args[1]);
			new S_MCTS_AI(tempName, null, null, 2, time);
	}

	public S_MCTS_AI(String name, String opp, String map, int id, long time) {
		super(name, opp, map, id, time, new AIParameter());
	}
	

	public S_MCTS_AI(String name, String opp, String map, int id, long time, int K) {
		super(name, opp, map, id, time, new AIParameter(1.1, K));
	}

	@Override
	protected void calculateMaxChildren(MCTSNode lastNode) {
		int count = 0;
		switch (lastNode.getTreePhase()) {
		case GameTreeNode.RECRUIT: {
			int troops = AIUtil.calculateRecruitedTroops(lastNode);

			int n = troops;
			int m = lastNode.getGame().getCurrentPlayer().getTerritories()
					.size();
			count = (int) (AIUtil.nCk(n + m - 1, n));

			if (count < 0)
				count = Integer.MAX_VALUE;

			break;
		}
		case GameTreeNode.ATTACK: {
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
						if (tempT != null) {
							count++;
						}
					}
				}
			}

			break;
		}
		case GameTreeNode.MOVEAFTERATTACK: {
			count = lastNode.getGame().getCurrentPlayer()
					.getTerritoryByName(lastNode.getParent().getAttackSource())
					.getNrTroops() - 1;
			if (count == 0) {
				System.out.println("WTF");
			}
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
								// Unique source-dest combo
								if (!src.getName().equals(dest.getName())) {
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

		lastNode.maxChildren = count;
	}

	// EXPANSION (One child at a time)
	@Override
	protected MCTSNode Expand(MCTSNode lastNode) {
		// System.out.println(lastNode.getTreePhaseText());
		switch (lastNode.getTreePhase()) {
		case GameTreeNode.RECRUIT: {
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

			// System.out.println("Recruit 2");
			MCTSNode maxChild = null;
			double maxRating = Double.NEGATIVE_INFINITY;
			// int count = 0;
			while (maxChild == null) {
				// count++;
				for (int i = 0; i < params.MCTSRecruitBranchQualityFactor; i++) {
					AIUtil.shuffleArray(perm);

					MCTSNode tempChild = lastNode.clone();
					tempChild.setTreePhase(GameTreeNode.RECRUIT);

					Iterator<Territory> it = tempChild.getGame()
							.getCurrentPlayer().getTerritories().values()
							.iterator();
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
					double value = getValue(tempChild, lastNode);
					if (value >= maxRating) {
						maxRating = value;
						maxChild = tempChild;
					}
				}

			}

			maxChild.setVisitCount(0);
			maxChild.setWinCount(0);
			maxChild.setParent(lastNode);
			maxChild.setChildren(new ArrayList<MCTSNode>());

			maxChild.setMoveReq(false);

			calculateMaxChildren(maxChild);

			maxChild.depth = lastNode.depth + 1;
			if (maxChild.depth > maxTreeDepth) {
				maxTreeDepth = maxChild.depth;
			}

			lastNode.addChild(maxChild);
			return maxChild;
		}

		case GameTreeNode.ATTACK: {
			if (lastNode.numberOfAttackBranches == 0) {
				lastNode.numberOfAttackBranches = Math.min(
						params.MCTSAttackBranchQualityFactor,
						lastNode.maxChildren / 2);
			}

			// Generate all and pick randomly from top 30

			if (lastNode.attackChildren == null) {
				lastNode.attackChildren = new ArrayList<MCTSNode>();

				if (AIFeatures.occupiedTerritoryFeature(lastNode) < params.leadWinRate
						&& AIFeatures.armyStrength(lastNode) < params.leadWinRate) {
					MCTSNode noAttackChild = lastNode.clone();
					noAttackChild.setTreePhase(GameTreeNode.MANOEUVRE);
					noAttackChild.setAttackSource("");
					noAttackChild.setAttackDest("");
					noAttackChild.setValue(getValue(noAttackChild, lastNode));
					// Add option to not attack
					lastNode.attackChildren.add(noAttackChild);
				}

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
								newChild.setTreePhase(GameTreeNode.RANDOMEVENT);
								newChild.setValue(getWeightedEval(newChild, lastNode));
								lastNode.attackChildren.add(newChild);
							}
						}
					}
				}
			}

			Random r = new Random();
			int index = 0;

			if (lastNode.attackChildren.size() == 0) {
				MCTSNode noAttackChild = lastNode.clone();
				noAttackChild.setTreePhase(GameTreeNode.MANOEUVRE);
				noAttackChild.setAttackSource("");
				noAttackChild.setAttackDest("");
				noAttackChild.setValue(getValue(noAttackChild, lastNode));
				// Add option to not attack
				lastNode.attackChildren.add(noAttackChild);
			}

			int count = 0;
			while (true) {
				count++;

				// Fix search range
				if (lastNode.attackChildren.size() < lastNode.numberOfAttackBranches + 1) {
					lastNode.numberOfAttackBranches--;
				}

				if (lastNode.numberOfAttackBranches < 1) {
					lastNode.numberOfAttackBranches = 1;
				}

				MCTSNode maxChild = null;
				double maxRating = Double.NEGATIVE_INFINITY;

				for (int i = 0; i < lastNode.numberOfAttackBranches; i++) {
					index = r.nextInt(lastNode.attackChildren.size());

					MCTSNode temp = lastNode.attackChildren.get(index);
					double value = temp.getValue();

					if (value >= maxRating) {
						maxRating = value;
						maxChild = temp;
					}

					// End game
					if (count == 50 && value >= Double.MAX_VALUE - 1) {
						maxChild.setVisitCount(0);
						maxChild.setWinCount(0);
						maxChild.setParent(lastNode);
						maxChild.setChildren(new ArrayList<MCTSNode>());

						if (maxChild.getAttackDest().length() == 0) {
							lastNode.noAttackAdded = true;
							maxChild.setTreePhase(GameTreeNode.MANOEUVRE);
						}
						calculateMaxChildren(maxChild);

						maxChild.depth = lastNode.depth + 1;
						if (maxChild.depth > maxTreeDepth) {
							maxTreeDepth = maxChild.depth;
						}

						lastNode.addChild(maxChild);
						return maxChild;
					}
				}

				if (maxChild == null) {
					maxChild = lastNode.clone();
					maxChild.setTreePhase(GameTreeNode.MANOEUVRE);
					maxChild.setAttackSource("");
					maxChild.setAttackDest("");
					maxChild.updateHash(lastNode);
				}

				// Checks if child already exists
				boolean unique = true;
				for (MCTSNode child : lastNode.getChildren()) {
					if (maxChild == null) {
						unique = false;
						break;
					}
					if (child.getAttackSource().equalsIgnoreCase(
							maxChild.getAttackSource())
							&& child.getAttackDest().equalsIgnoreCase(
									maxChild.getAttackDest())) {
						unique = false;
						break;
					}
				}

				// Add unique child to existing children
				if (unique) {
					lastNode.attackChildren.remove(maxChild);

					maxChild.setVisitCount(0);
					maxChild.setWinCount(0);
					maxChild.setParent(lastNode);
					maxChild.setChildren(new ArrayList<MCTSNode>());

					if (maxChild.getAttackDest().length() == 0) {
						maxChild.setTreePhase(GameTreeNode.MANOEUVRE);
					}
					calculateMaxChildren(maxChild);

					maxChild.depth = lastNode.depth + 1;
					if (maxChild.depth > maxTreeDepth) {
						maxTreeDepth = maxChild.depth;
					}

					lastNode.addChild(maxChild);
					return maxChild;
				}
			}
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
				newChild.setTreePhase(GameTreeNode.MOVEAFTERATTACK);
			} else {
				newChild.setTreePhase(GameTreeNode.ATTACK);
			}
			calculateMaxChildren(newChild);
			newChild.updateHash(lastNode);
			
			newChild.depth = lastNode.depth + 1;
			if (newChild.depth > maxTreeDepth) {
				maxTreeDepth = newChild.depth;
			}

			lastNode.addChild(newChild);

			return newChild;
		}

		case GameTreeNode.MOVEAFTERATTACK: {
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

					newChild.setTreePhase(GameTreeNode.ATTACK);

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
					newChild.updateHash(lastNode);
					
					newChild.depth = lastNode.depth + 1;
					if (newChild.depth > maxTreeDepth) {
						maxTreeDepth = newChild.depth;
					}

					lastNode.addChild(newChild);
					return newChild;
				}

			}

		}

		case GameTreeNode.MANOEUVRE: {

			if (lastNode.numberOfManoeuvreBranches == 0) {
				lastNode.numberOfManoeuvreBranches = Math.min(
						params.MCTSManBranchQualityFactor,
						lastNode.maxChildren / 2);
			}

			// System.out.println("Man 1");
			if (lastNode.manChildren == null) {
				lastNode.manChildren = new ArrayList<MCTSNode>();
				lastNode.manTroopBins = new ArrayList<Integer>();

				MCTSNode noManChild = lastNode.clone();
				noManChild.setTreePhase(GameTreeNode.RECRUIT);
				noManChild.switchMaxPlayer();
				noManChild.getGame().changeCurrentPlayer();
				// Add option to not manoeuvre
				lastNode.manChildren.add(noManChild);
				lastNode.manTroopBins.add(0);

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
										MCTSNode newChild = lastNode.clone();
										newChild.setManSource(src);
										newChild.setManDest(dest);
										newChild.setTreePhase(GameTreeNode.RECRUIT);
										lastNode.manTroopBins
												.add(lastNode.manTroopBins
														.get(lastNode.manTroopBins
																.size() - 1)
														+ src.getNrTroops() - 1);
										lastNode.manChildren.add(newChild);
									}
								}
							}
						}
					}
				}

			}

			Random r = new Random();
			int count = 0;
			while (true) {
				count++;
				double maxRating = Double.NEGATIVE_INFINITY;
				MCTSNode maxChild = null;

				if (lastNode.maxChildren == 1) {
					maxChild = lastNode.clone();
					maxChild.setTreePhase(GameTreeNode.RECRUIT);
					maxChild.switchMaxPlayer();
					maxChild.getGame().changeCurrentPlayer();
					maxChild.updateHash(lastNode);
				}

				// Fix search range
				if ((lastNode.maxChildren - lastNode.getChildren().size()) < lastNode.numberOfManoeuvreBranches + 1) {
					lastNode.numberOfManoeuvreBranches--;
				}

				if (lastNode.numberOfManoeuvreBranches < 1) {
					lastNode.numberOfManoeuvreBranches = 1;
				}

				for (int i = 0; i < lastNode.numberOfManoeuvreBranches; i++) {
					int index = r.nextInt(lastNode.maxChildren);

					int first, last, middle = -1, nrTroops = -1;
					MCTSNode temp = null;

					if (index > 0) {
						first = 0;
						last = lastNode.manTroopBins.size() - 1;
						middle = (first + last) / 2;

						while (first <= last) {
							int value = lastNode.manTroopBins.get(middle);

							if (value < index
									&& index < lastNode.manTroopBins
											.get(middle + 1)) {
								nrTroops = index - value;
								middle++;
								break;
							} else if (value == index) {
								nrTroops = value
										- lastNode.manTroopBins.get(middle - 1);
								break;
							} else if (value < index) {
								first = middle + 1;
							} else {
								last = middle - 1;
							}
							middle = (first + last) / 2;

						}
						temp = lastNode.manChildren.get(middle).clone();

						temp.setManTroopCount(nrTroops + "");
						AIUtil.resolveMoveAction(
								temp.getGame()
										.getCurrentPlayer()
										.getTerritoryByName(
												temp.getManSource().getName()),
								temp.getGame()
										.getCurrentPlayer()
										.getTerritoryByName(
												temp.getManDest().getName()),
								nrTroops);
						temp.switchMaxPlayer();
						temp.getGame().changeCurrentPlayer();				
					} else {
						temp = lastNode.manChildren.get(0);
					}

					double value = getValue(temp, lastNode);

					// End game
					if (count == 50 && value >= Double.MAX_VALUE - 1) {
						maxChild = lastNode.clone();
						maxChild.setVisitCount(0);
						maxChild.setWinCount(0);
						maxChild.setParent(lastNode);
						maxChild.setChildren(new ArrayList<MCTSNode>());

						maxChild.setTreePhase(GameTreeNode.RECRUIT);

						maxChild.setMoveReq(false);
						maxChild.switchMaxPlayer();
						maxChild.getGame().changeCurrentPlayer();

						calculateMaxChildren(maxChild);
						maxChild.updateHash(lastNode);
						
						maxChild.depth = lastNode.depth + 1;
						if (maxChild.depth > maxTreeDepth) {
							maxTreeDepth = maxChild.depth;
						}

						lastNode.addChild(maxChild);
						return maxChild;
					}
					
					if (value >= maxRating) {
						maxRating = value;
						maxChild = temp;
					}
				}

				// Checks if child already exists
				boolean unique = true;
				for (MCTSNode child : lastNode.getChildren()) {
					if (child.getManSource() == null
							&& maxChild.getManSource() == null) {
						unique = false;
						break;
					}
					if (child.getManSource() == null) {
						continue;
					}
					if (maxChild.getManSource() != null
							&& maxChild.getManDest() != null
							&& child.getManSource()
									.getName()
									.equalsIgnoreCase(
											maxChild.getManSource().getName())
							&& child.getManDest()
									.getName()
									.equalsIgnoreCase(
											maxChild.getManDest().getName())
							&& child.getManTroopCount().equalsIgnoreCase(
									maxChild.getManTroopCount())) {
						unique = false;
						break;
					}
				}

				// Add unique child to existing children
				if (unique) {
					maxChild.setVisitCount(0);
					maxChild.setWinCount(0);
					maxChild.setParent(lastNode);
					maxChild.setChildren(new ArrayList<MCTSNode>());

					maxChild.setMoveReq(false);
					
					calculateMaxChildren(maxChild);

					maxChild.depth = lastNode.depth + 1;
					if (maxChild.depth > maxTreeDepth) {
						maxTreeDepth = maxChild.depth;
					}

					lastNode.addChild(maxChild);
					return maxChild;
				}
			}
		}
		}

		return null;
	}

	/****************************************************************/

	@Override
	public void recruitPhase(Collection<Territory> myTerritories,
			int numberOfTroops) {

		LinkedList<String> reply = new LinkedList<String>();

		myTurn = true;

		MCTSNode root = new MCTSNode();
		root.setTreePhase(GameTreeNode.RECRUIT);
		root.setGame(game.clone());
		root.setMaxPlayer(true);
		root.setVisitCount(0);
		root.setWinCount(0);
		root.depth = 1;
		root.setChildren(new ArrayList<MCTSNode>());

		int n = numberOfTroops;
		int m = game.getCurrentPlayer().getTerritories().size();
		root.maxChildren = (int) (AIUtil.nCk(n + m - 1, n));

		treeDepth = 0;
		maxTreeDepth = Integer.MIN_VALUE;
		treeNodeCount = 0;

		// System.out.println("Started MCTS from " +
		// root.getTreePhaseText());
		MCTSNode action = MCTSSearch(root);
		// double time = (System.nanoTime() - startTime) / 1000000.0;

		//printStats(root, time);

		/*
		 * 
		 * if (tv != null) tv.close(); tv = new TreeView(globalNode);
		 * tv.showTree("After " + timeForMCTSSearch + " ms");
		 */

		Iterator<Territory> it = action.getGame().getCurrentPlayer()
				.getTerritories().values().iterator();
		while (it.hasNext()) {
			Territory T = it.next();
			reply.add(T.getId() + "");
			reply.add(T.getNrTroops() + "");
		}
		game = action.getGame();

		globalNode = action;
		globalNode.setParent(null);

		APM.sendSuccess(APM.getMesID(), "place_troops", reply);

	}

	@Override
	public LinkedList<String> getAttackSourceDestination() {

		numberOfMovesTaken++;

		LinkedList<String> reply = new LinkedList<String>();

		// globalNode.setGame(game.clone());
		globalNode.depth = 1;

		treeDepth = 0;
		maxTreeDepth = Integer.MIN_VALUE;
		treeNodeCount = 0;
		// int nrOfPlayouts = globalNode.getVisitCount();

		// System.out.println("Started MCTS from " + root.getTreePhaseText());
		MCTSNode action = MCTSSearch(globalNode);
		// double time = (System.nanoTime() - startTime) / 1000000.0;

		//printStats(globalNode, time);
		
		/*
		 * if (tv != null) tv.close(); tv = new TreeView(root);
		 * tv.showTree("After " + timeForMCTSSearch + " ms");
		 */

		Territory Source = action.getGame().getCurrentPlayer()
				.getTerritoryByName(action.getAttackSource());
		Territory Dest = action.getGame().getOtherPlayer()
				.getTerritoryByName(action.getAttackDest());

		globalNode = action;
		globalNode.setParent(null);

		if (Source != null && Dest != null) {
			reply.add(Source.getId() + "");
			reply.add(Dest.getId() + "");
		}

		return reply;

	}

	@Override
	public void resolveAttack(int a1, int a2, int a3, int d1, int d2, int sID,
			int dID) {

		Territory s = game.getCurrentPlayer().getTerritoryByID(sID);
		Territory d = game.getOtherPlayer().getTerritoryByID(dID);

		if (myTurn) {
			MCTSNode temp = new MCTSNode();
			temp.setGame(game);
			temp.setDiceRolls(a1, a2, a3, d1, d2);
			double prob = AIUtil.getProb(temp);

			for (MCTSNode child : globalNode.getChildren()) {
				if (AIUtil.getProb(child) == prob) {
					globalNode = child;
					globalNode.setParent(null);
					break;
				}
			}
		}

		if (d.getNrTroops() == 1 && a3 > d2) { // defender defeated
			lastAttackSource = s;
			lastAttackDestination = d;
		}

		super.resolveAttack(a1, a2, a3, d1, d2, sID, dID);

	}

	@Override
	public LinkedList<String> getMoveAfterAttack() {
		LinkedList<String> reply = new LinkedList<String>();
		reply.add(lastAttackSource.getId() + "");
		reply.add(lastAttackDestination.getId() + "");

		int troops = -1;
		double maxValue = Double.NEGATIVE_INFINITY;

		for (int i = 1; i < lastAttackSource.getNrTroops(); i++) {
			MCTSNode temp = new MCTSNode();
			temp.setGame(game.clone());
			temp.setTreePhase(GameTreeNode.ATTACK);
			AIUtil.resolveMoveAction(
					temp.getGame().getCurrentPlayer()
							.getTerritoryByName(lastAttackSource.getName()),
					temp.getGame()
							.getCurrentPlayer()
							.getTerritoryByName(lastAttackDestination.getName()),
					i);
			double value = getValue(temp, null);
			if (value >= maxValue) {
				troops = i;
				maxValue = value;
			}
		}

		if (myTurn) {
			boolean found = false;
			for (MCTSNode child : globalNode.getChildren()) {
				if (child.getMoveAfterAttackCount() == troops) {
					found = true;
					globalNode = child;
					globalNode.setParent(null);
					break;
				}
			}

			if (!found) {
				globalNode = new MCTSNode();
				globalNode.setTreePhase(GameTreeNode.ATTACK);
				globalNode.setGame(game.clone());

				AIUtil.resolveMoveAction(
						globalNode.getGame().getCurrentPlayer()
								.getTerritoryByName(lastAttackSource.getName()),
						globalNode
								.getGame()
								.getCurrentPlayer()
								.getTerritoryByName(
										lastAttackDestination.getName()),
						troops);

				globalNode.setMaxPlayer(true);
				globalNode.setVisitCount(0);
				globalNode.setWinCount(0);
				globalNode.depth = 1;
				globalNode.setChildren(new ArrayList<MCTSNode>());
				calculateMaxChildren(globalNode);
			}
		}

		reply.add(troops + "");

		return reply;
	}

	@Override
	// Manoeuvre
	public LinkedList<String> getManSourceDestination() {

		LinkedList<String> reply = new LinkedList<String>();

		treeDepth = 0;
		maxTreeDepth = Integer.MIN_VALUE;
		treeNodeCount = 0;

		globalNode.depth = 1;

		// System.out.println("Started MCTS from " + root.getTreePhaseText());
		MCTSNode action = MCTSSearch(globalNode);
		// double time = (System.nanoTime() - startTime) / 1000000.0;

		//printStats(globalNode, time);

		/*
		 * if (tv != null) tv.close(); tv = new TreeView(root);
		 * tv.showTree("After " + timeForMCTSSearch + " ms");
		 */

		Territory Source = action.getManSource();
		Territory Dest = action.getManDest();

		if (Source == null || Dest == null || action.getManTroopCount() == null) {
			/* System.out.println("No Manoeuvre"); */
			myTurn = false;
			return reply;
		}
		reply.add(Source.getId() + "");
		reply.add(Dest.getId() + "");
		reply.add(action.getManTroopCount() + "");

		// System.out.println("Manoeuvre from " + Source.toString() + " to "
		// + Dest.toString() + " with " + action.getManTroopCount());

		AIUtil.resolveMoveAction(
				game.getCurrentPlayer().getTerritoryByName(Source.getName()),
				game.getCurrentPlayer().getTerritoryByName(Dest.getName()),
				Integer.parseInt(action.getManTroopCount()));

		myTurn = false;

		return reply;
	}

}

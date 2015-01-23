package risk.aiplayers.MCTSPlayers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import risk.aiplayers.MonteCarloTreeSearchPlayer;
import risk.aiplayers.util.AIFeatures;
import risk.aiplayers.util.AIParameter;
import risk.aiplayers.util.AIUtil;
import risk.aiplayers.util.MCTSNode;
import risk.aiplayers.util.NodeType;
import risk.commonObjects.Territory;

public class MCTSFairExpansion_AI extends MonteCarloTreeSearchPlayer {

	MCTSNode globalNode;

	boolean myTurn = false;

	public static void main(String[] args) {
		String tempName = args[0];
		long time = Long.parseLong(args[1]);
		new MCTSFairExpansion_AI(tempName, null, null, 2, time);
	}

	public MCTSFairExpansion_AI(String name, String opp, String map, int id,
			long time) {
		super(name, opp, map, id, time, new AIParameter());
	}
	
	// EXPANSION (One child at a time)
	@Override
	protected MCTSNode Expand(MCTSNode lastNode) {
		 //System.out.println(lastNode.getTreePhaseText());
		switch (lastNode.getTreePhase()) {
		case RECRUIT: {
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

			// Generate all and pick randomly from top 30
				// System.out.println("Recruit 2");
				MCTSNode maxChild = null;
				double maxRating = Double.NEGATIVE_INFINITY;
				while (maxChild == null) {
//					System.out.println("Loop Fair ");
					for (int i = 0; i < params.MCTSRecruitBranchQualityFactor; i++) {
						AIUtil.shuffleArray(perm);

						MCTSNode tempChild = lastNode.clone();

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
						
						tempChild.setTreePhase(NodeType.ATTACK);
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

				maxChild.depth = lastNode.depth + 1;
				if (maxChild.depth > maxTreeDepth) {
					maxTreeDepth = maxChild.depth;
				}

				lastNode.addChild(maxChild);
				return maxChild;
			}

		case ATTACK: {
			if (lastNode.numberOfAttackBranches == 0) {
				lastNode.numberOfAttackBranches = Math.min(
						params.MCTSAttackBranchQualityFactor,
						lastNode.maxChildren() / 2);
			}

			// Generate all and pick randomly from top 30

			if (lastNode.attackChildren == null) {
				lastNode.attackChildren = new ArrayList<MCTSNode>();

				if (AIFeatures.occupiedTerritoryFeature(lastNode) < params.leadWinRate
						&& AIFeatures.armyStrength(lastNode) < params.leadWinRate) {
					MCTSNode noAttackChild = lastNode.clone();
					noAttackChild.setTreePhase(NodeType.MANOEUVRE);
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
								newChild.setTreePhase(NodeType.RANDOMEVENT);
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
				noAttackChild.setTreePhase(NodeType.MANOEUVRE);
				noAttackChild.setAttackSource("");
				noAttackChild.setAttackDest("");
				noAttackChild.setValue(getValue(noAttackChild, lastNode));
				// Add option to not attack
				lastNode.attackChildren.add(noAttackChild);
			}

			int count = 0;
			while (true) {
//				System.out.println("Loop2 fair ");
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
							maxChild.setTreePhase(NodeType.MANOEUVRE);
						}

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
					maxChild.setTreePhase(NodeType.MANOEUVRE);
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
						maxChild.setTreePhase(NodeType.MANOEUVRE);
					}

					maxChild.depth = lastNode.depth + 1;
					if (maxChild.depth > maxTreeDepth) {
						maxTreeDepth = maxChild.depth;
					}

					lastNode.addChild(maxChild);
					return maxChild;
				}
			}
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

			getValue(newChild, lastNode);
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
//				System.out.println("Loop3 Fair ");
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

					getValue(newChild, lastNode);
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

			if (lastNode.numberOfManoeuvreBranches == 0) {
				lastNode.numberOfManoeuvreBranches = Math.min(
						params.MCTSManBranchQualityFactor,
						lastNode.maxChildren() / 4);
			}

			if(lastNode.manSources == null) {
				lastNode.manSources = new ArrayList<Territory>();
				lastNode.manDests = new ArrayList<Territory>();
				lastNode.manTroopBins = new ArrayList<Integer>();

//				 Add option to not manoeuvre
				lastNode.manSources.add(null);
				lastNode.manDests.add(null);
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
										lastNode.manTroopBins
												.add(lastNode.manTroopBins
														.get(lastNode.manTroopBins
																.size() - 1)
														+ src.getNrTroops() - 1);
										lastNode.manSources.add(src);
										lastNode.manDests.add(dest);
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
//				System.out.println("Loop4 Fair ");
				count++;
				//System.out.println(count);
				double maxRating = Double.NEGATIVE_INFINITY;
				MCTSNode maxChild = null;

				if (lastNode.maxChildren() == 1) {
					maxChild = lastNode.clone();
					maxChild.setTreePhase(NodeType.RECRUIT);
					maxChild.switchMaxPlayer();
					maxChild.getGame().changeCurrentPlayer();
					maxChild.updateHash(lastNode);
				}

				// Fix search range
				if ((lastNode.maxChildren() - lastNode.getChildren().size()) < lastNode.numberOfManoeuvreBranches + 1) {
					lastNode.numberOfManoeuvreBranches--;
				}

				if (lastNode.numberOfManoeuvreBranches < 1) {
					lastNode.numberOfManoeuvreBranches = 1;
				}

				for (int i = 0; i < lastNode.numberOfManoeuvreBranches; i++) {
					int index = r.nextInt(lastNode.maxChildren());

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
						temp = lastNode.clone();
						temp.setManSource(lastNode.manSources.get(middle));
						temp.setManDest(lastNode.manDests.get(middle));
						temp.setTreePhase(NodeType.RECRUIT);
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
						temp = lastNode.clone();
						temp.setTreePhase(NodeType.RECRUIT);
						temp.switchMaxPlayer();
						temp.getGame().changeCurrentPlayer();
					}
					
					double value = getValue(temp, lastNode);
					// End game
					if (count == 50 && value >= Double.MAX_VALUE - 1) {
						maxChild = lastNode.clone();
						maxChild.setVisitCount(0);
						maxChild.setWinCount(0);
						maxChild.setParent(lastNode);
						maxChild.setChildren(new ArrayList<MCTSNode>());

						maxChild.setTreePhase(NodeType.RECRUIT);
						maxChild.switchMaxPlayer();
						maxChild.getGame().changeCurrentPlayer();
						
						maxChild.setMoveReq(false);

						getValue(maxChild, lastNode);
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
			root.setTreePhase(NodeType.RECRUIT);
			root.setGame(game.clone());
			root.setMaxPlayer(true);
			root.setVisitCount(0);
			root.setWinCount(0);
			root.depth = 1;
			root.setChildren(new ArrayList<MCTSNode>());

			int n = numberOfTroops;
			int m = game.getCurrentPlayer().getTerritories().size();
			root.setMaxChildren((int) (AIUtil.nCk(n + m - 1, n)));

			treeDepth = 0;
			maxTreeDepth = Integer.MIN_VALUE;
			treeNodeCount = 0;

			// System.out.println("Started MCTS from " +
			// root.getTreePhaseText());
			MCTSNode action = MCTSSearch(root);
			double time = (System.nanoTime() - startTime) / 1000000.0;
			printStats(root, time);
			/*
			 * System.out.println("Ended MCTS in " + time + " ms");
			 * 
			 * System.out.println("Depth : " + maxTreeDepth);
			 * System.out.println("Node Count : " + treeNodeCount);
			 * System.out.println("Root playouts : " + root.getVisitCount());
			 * 
			 * System.out.println("HashMap ratio - " + (double) foundIt /
			 * (double) (foundIt + missedIt) * 100 + " %");
			 * 
			 * System.out.println("HashMap size - " + NodeValues.size());
			 * 
			 * System.out.println("Playouts / second - " + Math.round((double)
			 * root.getVisitCount() / (double) (time / 1000.0)));
			 * System.out.println();
			 */

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

		/*
		 * MCTSNode root = new MCTSNode();
		 * root.setTreePhase(NodeType.ATTACK); root.setGame(game.clone());
		 * root.setMaxPlayer(true); root.setVisitCount(0); root.setWinCount(0);
		 * root.depth = 1; root.setChildren(new ArrayList<MCTSNode>());
		 * calculateMaxChildren(root);
		 * 
		 * if (root.maxChildren == 1) { return reply; }
		 */

		treeDepth = 0;
		maxTreeDepth = Integer.MIN_VALUE;
		treeNodeCount = 0;
		// int nrOfPlayouts = globalNode.getVisitCount();

		// System.out.println("Started MCTS from " + root.getTreePhaseText());
		MCTSNode action = MCTSSearch(globalNode);
		 /*double time = (System.nanoTime() - startTime) / 1000000.0;
		System.out.println("Ended MCTS in " + time + " ms");
		  
		  System.out.println("Depth : " + maxTreeDepth);
		 System.out.println("Node Count: " + treeNodeCount);
		  System.out.println("Root playouts : " + (globalNode.getVisitCount() - nrOfPlayouts));
		  
		 
		 * System.out.println("HashMap ratio - " + (double) foundIt / (double)
		 * (foundIt + missedIt) * 100 + " %");
		 * 
		 * System.out.println("HashMap size - " + NodeValues.size());
		 * 
		 * System.out.println("Playouts / second - " + Math.round((double)
		 * root.getVisitCount() / (double) (time / 1000.0)));
		 */

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

		if (Source == null || Dest == null) {
			// System.out.println("Not attacking");
		} else {
			// System.out.println("Attacking from " + Source.toString() +
			// "  to  "
			// + Dest.toString());

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
			temp.setTreePhase(NodeType.ATTACK);
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
				globalNode.setTreePhase(NodeType.ATTACK);
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
			}
		}

		reply.add(troops + "");

		return reply;
	}

	@Override
	// Manoeuvre
	public LinkedList<String> getManSourceDestination() {

		LinkedList<String> reply = new LinkedList<String>();
		/*
		 * MCTSNode root = new MCTSNode();
		 * root.setTreePhase(NodeType.MANOEUVRE);
		 * root.setGame(game.clone()); root.setMaxPlayer(true);
		 * root.setVisitCount(0); root.setWinCount(0); root.depth = 1;
		 * root.setChildren(new ArrayList<MCTSNode>());
		 * calculateMaxChildren(root);
		 */

		treeDepth = 0;
		maxTreeDepth = Integer.MIN_VALUE;
		treeNodeCount = 0;

		globalNode.depth = 1;

		// System.out.println("Started MCTS from " + root.getTreePhaseText());
		MCTSNode action = MCTSSearch(globalNode);
		/*
		 * double time = (System.nanoTime() - startTime) / 1000000.0;
		 * System.out.println("Ended MCTS in " + time + " ms");
		 * 
		 * System.out.println("Depth : " + maxTreeDepth);
		 * System.out.println("Node Count : " + treeNodeCount);
		 * System.out.println("Root playouts : " + root.getVisitCount());
		 * 
		 * System.out.println("HashMap ratio - " + (double) foundIt / (double)
		 * (foundIt + missedIt) * 100 + " %");
		 * 
		 * System.out.println("HashMap size - " + NodeValues.size());
		 * 
		 * System.out.println("Playouts / second - " + Math.round((double)
		 * root.getVisitCount() / (double) (time / 1000.0)));
		 */

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

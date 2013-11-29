package risk.aiplayers.MCTSPlayers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import risk.aiplayers.MonteCarloTreeSearchPlayer;
import risk.aiplayers.util.AIFeatures;
import risk.aiplayers.util.AIUtil;
import risk.aiplayers.util.BinaryTree;
import risk.aiplayers.util.GameTreeNode;
import risk.aiplayers.util.MCTSNode;
import risk.commonObjects.Territory;

public class MCTS_Advanced_AI extends MonteCarloTreeSearchPlayer {


	MCTSNode globalNode;

	boolean myTurn = false;

	public static void main(String[] args) {
		String tempName = args[0];
		long time = Long.parseLong(args[1]);
		new MCTS_Advanced_AI(tempName, null, null, 2, time);
	}

	public MCTS_Advanced_AI(String name, String opp, String map,
			int id, long time) {
		super(name, opp, map, id, time);
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

			// Generate all and pick randomly from top 30
			if (false) {
				// System.out.println("Recruit 1");
				if (lastNode.recruitChildren == null) {
					lastNode.recruitChildren = new ArrayList<MCTSNode>();

					BinaryTree tree = null;
					try {
						tree = new BinaryTree(n, m - 1);
					} catch (Exception e) {
						System.out.println("WTF");
					}

					// Print all possible combinations
					for (boolean[] permutation : tree.getPermutations()) {
						MCTSNode newChild = lastNode.clone();
						// Place troops according to permutation
						Iterator<Territory> it = newChild.getGame()
								.getCurrentPlayer().getTerritories().values()
								.iterator();
						Territory current = it.next();
						for (int p = 0; p < permutation.length; p++) {
							if (permutation[p]) {
								current.incrementTroops();
								newChild.setAttackSource(current.getName());
							} else {
								current = it.next();
							}
						}
						newChild.setValue(getValue(newChild));
						lastNode.recruitChildren.add(newChild);
					}

					Collections.sort(lastNode.recruitChildren);

				}

				Random r = new Random();
				int index = r.nextInt(Math.max(30,
						lastNode.recruitChildren.size()));
				int i = 0;
				Iterator<MCTSNode> iterator = lastNode.recruitChildren
						.iterator();
				MCTSNode child = iterator.next();
				while (iterator.hasNext() && i < index) {
					child = iterator.next();
					i++;
				}
				lastNode.recruitChildren.remove(child);

				child.setVisitCount(0);
				child.setWinCount(0);
				child.setParent(lastNode);
				child.setChildren(new ArrayList<MCTSNode>());

				child.setTreePhase(GameTreeNode.ATTACK);

				child.setMoveReq(false);

				calculateMaxChildren(child);

				child.depth = lastNode.depth + 1;
				if (child.depth > maxTreeDepth) {
					maxTreeDepth = child.depth;
				}

				lastNode.addChild(child);
				return child;
			} else {
				// System.out.println("Recruit 2");
				MCTSNode maxChild = null;
				double maxRating = Double.NEGATIVE_INFINITY;
				int count = 0;
				while (maxChild == null) {
					count++;
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
						double value = getValue(tempChild);
						if (count == 100) {
							System.out.println("recruit");

						}
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

				maxChild.setTreePhase(GameTreeNode.ATTACK);

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

		case GameTreeNode.ATTACK: {
			if (lastNode.numberOfAttackBranches == 0) {
				lastNode.numberOfAttackBranches = params.MCTSAttackBranchQualityFactor;
			}
			// Generate all and pick randomly from top 30
			if (5 * lastNode.numberOfAttackBranches > lastNode.maxChildren) {
				if (lastNode.attackChildren == null) {
					lastNode.attackChildren = new ArrayList<MCTSNode>();

					if (AIFeatures.occupiedTerritoryFeature(lastNode) < params.leadWinRate
							&& AIFeatures.armyStrength(lastNode) < params.leadWinRate) {
						MCTSNode noAttackChild = lastNode.clone();
						noAttackChild.setTreePhase(GameTreeNode.MANOEUVRE);
						noAttackChild.setAttackSource("");
						noAttackChild.setAttackDest("");
						noAttackChild.setValue(getValue(noAttackChild));
						// Add option to not attack
						lastNode.attackChildren.add(noAttackChild);
					}

					// Populate treeset
					Iterator<Territory> it = lastNode.getGame()
							.getCurrentPlayer().getTerritories().values()
							.iterator();
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
									newChild.setValue(getWeightedEval(newChild));
									lastNode.attackChildren.add(newChild);
								}
							}
						}
					}
					Collections.sort(lastNode.attackChildren);
				}

				Random r = new Random();
				int index = 0;

				if (lastNode.attackChildren.size() == 0) {
					MCTSNode noAttackChild = lastNode.clone();
					noAttackChild.setTreePhase(GameTreeNode.MANOEUVRE);
					noAttackChild.setAttackSource("");
					noAttackChild.setAttackDest("");
					// Add option to not attack
					lastNode.attackChildren.add(noAttackChild);
				}

				index = r.nextInt(Math.min(30, lastNode.attackChildren.size()));

				int i = 0;
				// System.out.println(lastNode.attackChildren.size() + " <-> " +
				// lastNode.maxChildren);
				Iterator<MCTSNode> iterator = lastNode.attackChildren
						.iterator();
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
					child.setTreePhase(GameTreeNode.MANOEUVRE);
				} else {
					child.setTreePhase(GameTreeNode.RANDOMEVENT);
				}

				child.setMoveReq(false);

				calculateMaxChildren(child);

				child.depth = lastNode.depth + 1;
				if (child.depth > maxTreeDepth) {
					maxTreeDepth = child.depth;
				}

				lastNode.addChild(child);
				return child;
			} else {

				Random r = new Random();

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

					// System.out.println(count++);
					// Add the option to not attack
					MCTSNode maxChild = null;
					double maxRating = Double.NEGATIVE_INFINITY;
					if (!lastNode.noAttackAdded) {
						if (AIFeatures.occupiedTerritoryFeature(lastNode) < params.leadWinRate
								&& AIFeatures.armyStrength(lastNode) < params.leadWinRate) {
							maxChild = lastNode.clone();
							maxChild.setAttackSource("");
							maxChild.setAttackDest("");
							maxChild.setTreePhase(GameTreeNode.MANOEUVRE);
							maxRating = getValue(maxChild);
						}
					}

					for (int i = 0; i < lastNode.numberOfAttackBranches; i++) {

						// Get a random source
						int sourceIndex = r.nextInt(lastNode.getGame()
								.getCurrentPlayer().getTerritories().size());

						Iterator<Territory> it = lastNode.getGame()
								.getCurrentPlayer().getTerritories().values()
								.iterator();
						Territory source = null;
						for (int j = 0; j <= sourceIndex; j++) {
							source = it.next();
						}

						// Only consider fortified territories
						if (source.getNrTroops() > 1) {
							// Get a random destination
							int destIndex = r
									.nextInt(source.getNeighbours().length);
							Territory n = source.getNeighbours()[destIndex];

							Territory tempT = lastNode.getGame()
									.getOtherPlayer()
									.getTerritoryByName(n.getName());
							// Only consider targets that have less troops than
							// the
							// source
							if (tempT != null) {
								MCTSNode tempChild = lastNode.clone();
								tempChild.setAttackSource(source.getName());
								tempChild.setAttackDest(tempT.getName());
								tempChild
										.setTreePhase(GameTreeNode.RANDOMEVENT);

								double value = getWeightedEval(tempChild);
								/*
								 * if (count == 100) {
								 * System.out.println("Attack"); System.out
								 * .println(lastNode.numberOfAttackBranches); }
								 */

								if (value >= maxRating) {
									maxRating = value;
									maxChild = tempChild;
								}

								// End game
								if (count == 50
										&& value >= Double.MAX_VALUE - 1) {
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
						}

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
			// Generate all and pick randomly from top 30
			if (5 * lastNode.numberOfManoeuvreBranches > lastNode.maxChildren) {
				// System.out.println("Man 1");
				if (lastNode.manChildren == null) {
					lastNode.manChildren = new ArrayList<MCTSNode>();

					// if (AIFeatures.occupiedTerritoryFeature(lastNode) <
					// params.leadWinRate && AIFeatures.armyStrength(lastNode) <
					// params.leadWinRate) {
					MCTSNode noManChild = lastNode.clone();
					noManChild.setValue(getValue(noManChild));
					// Add option to not manoeuvre
					lastNode.manChildren.add(noManChild);
					// }

					// Populate treeset
					LinkedList<LinkedList<Territory>> connComponentBuckets = lastNode
							.getConnComponentBuckets();
					for (LinkedList<Territory> bucket : connComponentBuckets) {
						if (bucket.size() > 1) {
							for (Territory src : bucket) {
								if (src.getNrTroops() > 1) {
									for (Territory dest : bucket) {
										if (!src.getName().equals(
												dest.getName())) {
											// Unique source-dest combo
											for (int i = 1; i < src
													.getNrTroops(); i++) {
												MCTSNode newChild = lastNode
														.clone();
												newChild.setManSource(src);
												newChild.setManDest(dest);
												newChild.setManTroopCount(i
														+ "");
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
												newChild.setValue(getValue(newChild));
												lastNode.manChildren
														.add(newChild);
											}
										}
									}
								}
							}
						}
					}
					Collections.sort(lastNode.manChildren);

				}

				Random r = new Random();
				int index = r
						.nextInt(Math.max(30, lastNode.manChildren.size()));
				int i = 0;
				Iterator<MCTSNode> iterator = lastNode.manChildren.iterator();

				MCTSNode child = iterator.next();
				while (iterator.hasNext() && i <= index) {
					child = iterator.next();
					i++;
				}
				lastNode.manChildren.remove(child);

				child.setVisitCount(0);
				child.setWinCount(0);
				child.setParent(lastNode);
				child.setChildren(new ArrayList<MCTSNode>());

				child.setTreePhase(GameTreeNode.RECRUIT);

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
			} else {
				// System.out.println("Man 2");

				LinkedList<LinkedList<Territory>> connComponentBuckets = lastNode
						.getConnComponentBuckets();
				int size = connComponentBuckets.size();
				// No territories with more than 1 troop. Thus only option is
				// not
				// maneuvering
				if (lastNode.maxChildren == 1) {
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

				Random r = new Random();

				// Repeat until done
				int count = 0;
				while (true) {
					count++;
					// System.out.println(count);

					// Fix search range
					if ((lastNode.maxChildren - lastNode.getChildren().size()) < lastNode.numberOfManoeuvreBranches + 1) {
						lastNode.numberOfManoeuvreBranches--;
					}

					if (lastNode.numberOfManoeuvreBranches < 1) {
						lastNode.numberOfManoeuvreBranches = 1;
					}

					// System.out.println((lastNode.maxChildren -
					// lastNode.getChildren().size()) + " <-> " + count++);
					MCTSNode maxChild = null;
					double maxRating = Double.NEGATIVE_INFINITY;
					// Not maneuvering

					if (!lastNode.noManAdded) {
						maxChild = lastNode.clone();
						maxRating = getValue(maxChild);
					}

					for (int i = 0; i < lastNode.numberOfManoeuvreBranches; i++) {

						LinkedList<Territory> connComponent = null;

						// Confirm connComponent contains fortified territories
						boolean found = false;
						while (!found) {
							int cIndex = r.nextInt(size);
							connComponent = connComponentBuckets.get(cIndex);

							if (connComponent.size() == 1)
								continue;

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
						while (dIndex == sIndex && connComponent.size() != 1) {
							dIndex = r.nextInt(connComponent.size());
						}

						Territory dest = connComponent.get(dIndex);

						int troopNumber = Math.round(r.nextInt(source
								.getNrTroops() - 1)) + 1;

						MCTSNode temp = lastNode.clone();
						temp.setManSource(temp.getGame().getCurrentPlayer()
								.getTerritoryByName(source.getName()));
						temp.setManDest(temp.getGame().getCurrentPlayer()
								.getTerritoryByName(dest.getName()));
						temp.setManTroopCount(troopNumber + "");

						AIUtil.resolveMoveAction(
								temp.getGame().getCurrentPlayer()
										.getTerritoryByName(source.getName()),
								temp.getGame().getCurrentPlayer()
										.getTerritoryByName(dest.getName()),
								troopNumber);

						double value = getValue(temp);
						/*
						 * if (count == 50) { System.out.println("manoeuvre");
						 * System.out
						 * .println(lastNode.numberOfManoeuvreBranches);
						 * System.out.println("Max: " + lastNode.maxChildren +
						 * " - " + "fill: " + lastNode.getChildren().size());
						 * return lastNode; }
						 */
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
												maxChild.getManSource()
														.getName())
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

						maxChild.setTreePhase(GameTreeNode.RECRUIT);
						maxChild.setMoveReq(false);
						maxChild.switchMaxPlayer();
						maxChild.getGame().changeCurrentPlayer();

						calculateMaxChildren(maxChild);

						if (maxChild.getManDest() == null) {
							lastNode.noManAdded = true;
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
		}
		}

		return null;
	}

	
	/****************************************************************/

	@Override
	public void recruitPhase(Collection<Territory> myTers, int number) {

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

		int n = number;
		int m = game.getCurrentPlayer().getTerritories().size();
		root.maxChildren = (int) (AIUtil.nCk(n + m - 1, n));

		treeDepth = 0;
		maxTreeDepth = Integer.MIN_VALUE;
		treeNodeCount = 0;

		// System.out.println("Started MCTS from " +
		// root.getTreePhaseText());
		MCTSNode action = MCTSSearch(root);
		double time = (System.nanoTime() - startTime) / 1000000.0;
		/*
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
		 * System.out.println();
		 */

		/*
		 * if (tv != null) tv.close(); tv = new TreeView(root);
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

		globalNode.depth = 1;
		/*
		 * MCTSNode root = new MCTSNode();
		 * root.setTreePhase(GameTreeNode.ATTACK); root.setGame(game.clone());
		 * root.setMaxPlayer(true); root.setVisitCount(0); root.setWinCount(0);
		 * root.depth = 1; root.setChildren(new ArrayList<MCTSNode>());
		 * calculateMaxChildren(root);
		 * 
		 * if (root.maxChildren == 1) { return reply; }
		 */

		treeDepth = 0;
		maxTreeDepth = Integer.MIN_VALUE;
		treeNodeCount = 0;
		int nrOfPlayouts = globalNode.getVisitCount();

		// System.out.println("Started MCTS from " + root.getTreePhaseText());
		MCTSNode action = MCTSSearch(globalNode);
		double time = (System.nanoTime() - startTime) / 1000000.0;

		/*
		 * System.out.println("Ended MCTS in " + time + " ms");
		 * 
		 * System.out.println("Depth : " + maxTreeDepth);
		 * System.out.println("Node Count: " + treeNodeCount);
		 * System.out.println("Root playouts : " + (globalNode.getVisitCount() -
		 * nrOfPlayouts));
		 */
		/*
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

		// globalNode.setGame(game.clone());
		globalNode.depth = 1;

		treeDepth = 0;
		maxTreeDepth = Integer.MIN_VALUE;
		treeNodeCount = 0;

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
			// System.out.println("No Manoeuvre");

			myTurn = false;
			return reply;
		}
		reply.add(Source.getId() + "");
		reply.add(Dest.getId() + "");
		reply.add(action.getManTroopCount() + "");
		/*
		 * System.out.println("Manoeuvre from " + Source.toString() + " to " +
		 * Dest.toString() + " with " + action.getManTroopCount());
		 */

		AIUtil.resolveMoveAction(
				game.getCurrentPlayer().getTerritoryByName(Source.getName()),
				game.getCurrentPlayer().getTerritoryByName(Dest.getName()),
				Integer.parseInt(action.getManTroopCount()));

		myTurn = false;

		return reply;
	}

}

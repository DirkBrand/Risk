package risk.aiplayers.MCTSPlayers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import risk.aiplayers.util.AIFeatures;
import risk.aiplayers.util.AIParameter;
import risk.aiplayers.util.AIUtil;
import risk.aiplayers.util.GameTreeNode;
import risk.aiplayers.util.MCTSNode;
import risk.commonObjects.Territory;

public class MCTSHashing_Unicness_AI extends MCTSMove_After_Attack_AI {

	public static void main(String[] args) {
		String tempName = args[0];
		long time = Long.parseLong(args[1]);
		new MCTSHashing_Unicness_AI(tempName, null, null, 2, time);
	}

	public MCTSHashing_Unicness_AI(String name, String opp, String map, int id,
			long time) {
		super(name, opp, map, id, time);
	}

	@Override
	protected MCTSNode Expand(MCTSNode lastNode) {
		int quickfix = 0;
		switch (lastNode.getTreePhase()) {
		//********************************RECRUIT************************************//
		case GameTreeNode.RECRUIT: {
			calculateMaxChildren(lastNode);
			//TODO : check this out with Mark DiBrando, search change has to be fixed accordingly. - cf ATTACK
			if (lastNode.numberOfRecruitBranches == 0) {
				lastNode.numberOfRecruitBranches = Math.min(
						params.MCTSRecruitBranchQualityFactor,
						lastNode.maxChildren / 2);
			}

			//Avoiding 0. maxChildren = 1. 1/2=0 (int)
			if(lastNode.numberOfRecruitBranches < 1)
				lastNode.numberOfRecruitBranches = 1;

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
			while(true)
			{
				// Generate all and pick randomly from top 30
				// System.out.println("Recruit 2");
				MCTSNode maxChild = null;
				double maxRating = Double.NEGATIVE_INFINITY;
				while (maxChild == null) {
					/*System.out.println("Recruit : " + lastNode.numberOfRecruitBranches +" "+ params.MCTSRecruitBranchQualityFactor
							+ " " + lastNode.maxChildren / 2);*/
					for (int i = 0; i < lastNode.numberOfRecruitBranches; i++) {
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
								tempChild.setAttackSource(current.getName());  //TODO : Is this useful ?
							} else {
								current = it.next();
							}
						}
						double value = AIUtil.eval(tempChild, AIParameter.evalWeights, maxRecruitable); 
						// Was getValue() here before. - DuplicationAvoidance in sampling ?
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

				System.out.println("7");
				maxChild.updateHash(lastNode);
				System.out.println("7");
				//DUPLICATION AVOIDANCE
				//TODO : Loop issue caused when the maxChildren is small
				long key = maxChild.getHash();
				Double value = NodeValues.get(key);
				if(value != null) {
					System.out.println("Duplo Recruit " + lastNode.numberOfRecruitBranches + " " + lastNode.maxChildren);
					continue;
				}
				else
					getValue(maxChild);
				//END OF DUPLICATION AVOIDANCE

				lastNode.addChild(maxChild);
				return maxChild;
			}
		}
		//********************************ATTACK************************************//
		case GameTreeNode.ATTACK: {
			//To define somewhere else
			int reasonableChildrenNumber = 30;
			if(lastNode.maxChildren < reasonableChildrenNumber)
			{/*AddEveryPossibleChild, return noAttackOne (since we are sure that this one is included.)*/}
			//To do this quickly : "copy - paste" Populate treeset.
			
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
					// noAttackChild.setValue(getValue(noAttackChild));
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
								//newChild.setValue(getWeightedEval(newChild)); //This shit was calling getHash() .. Ogodwhy.
								newChild.setTreePhase(GameTreeNode.RANDOMEVENT);
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
				// noAttackChild.setValue(getValue(noAttackChild));
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
					double value = AIUtil.eval(temp, AIParameter.evalWeights, maxRecruitable); 

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
						
						System.out.println("1");
						maxChild.updateHash(lastNode);
						System.out.println("1");
						
						getValue(maxChild);
						lastNode.addChild(maxChild);
						lastNode.attackChildren.remove(maxChild);
						return maxChild;
					}
				}

				if (maxChild == null) {
					maxChild = lastNode.clone();
					maxChild.setTreePhase(GameTreeNode.MANOEUVRE);
					maxChild.setAttackSource("");
					maxChild.setAttackDest("");
				}

				if (maxChild.getAttackDest().length() == 0) {
					maxChild.setTreePhase(GameTreeNode.MANOEUVRE);
				}

				// Add unique child to existing children			
				maxChild.setVisitCount(0);
				maxChild.setWinCount(0);
				maxChild.setParent(lastNode);
				maxChild.setChildren(new ArrayList<MCTSNode>());
				maxChild.depth = lastNode.depth + 1;
				if (maxChild.depth > maxTreeDepth) {
					maxTreeDepth = maxChild.depth;
				}

				System.out.println("2");
				maxChild.updateHash(lastNode);
				System.out.println("2");
				//TODO : Fix this. Duplo Atk 1
//				Well Sir, you are trying to create a hashcode that we already know: 1237574272214013321
//				Duplo Atk 1
//				Well Sir, you are trying to create a hashcode that we already know: 1237574272214013321 etc.
				// Looping when 1 possible attack : noAttack. This means that either this node can't be added or, when in this situation, expand is
				// called more than once on the same node.
				
				//Duplication Avoidance
				long key = maxChild.getHash();
				Double value = NodeValues.get(key);
				if(value != null && quickfix<4) { //TODO: This really sucks.
					quickfix++;
					System.out.println("Duplo Atk " + lastNode.maxChildren + " " + lastNode.attackChildren.size() + " " + maxChild.getAttackDest() + " " + maxChild.getAttackSource());
					continue;
				} else {
					getValue(maxChild);
					//Duplication Avoidance

					if(!lastNode.attackChildren.remove(maxChild))
						System.out.println("Node not properly removed from attackChildren");
					calculateMaxChildren(maxChild);
					lastNode.addChild(maxChild);	
					return maxChild;
				}
			}
		}
		//****************************RANDOM EVENT****************************************//
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
			
			System.out.println("3");
			newChild.updateHash(lastNode);
			System.out.println("3");
			lastNode.addChild(newChild);
			return newChild;
		}
		//**********************MOVE AFTER ATTACK***********************************//
		case GameTreeNode.MOVEAFTERATTACK: {
			int totalTroops = lastNode.getGame().getCurrentPlayer()
					.getTerritoryByName(lastNode.getAttackSource())
					.getNrTroops();
			while (true) {
				int troops = rand.nextInt(totalTroops - 1) + 1;

				//TODO : Update hash - check uniqueness with it
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
					System.out.println("4");
					newChild.updateHash(lastNode);
					System.out.println("4");
					lastNode.addChild(newChild);
					return newChild;
				}

			}

		}
		//******************************MANOEUVRE***********************************//
		case GameTreeNode.MANOEUVRE: {

			if (lastNode.numberOfManoeuvreBranches == 0) {
				lastNode.numberOfManoeuvreBranches = Math.min(
						params.MCTSManBranchQualityFactor,
						lastNode.maxChildren / 4);
			}

			// System.out.println("Man 1");
			if (lastNode.manChildren == null) {
				lastNode.manChildren = new ArrayList<MCTSNode>();
				lastNode.manTroopBins = new ArrayList<Integer>();

				MCTSNode noManChild = lastNode.clone();
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
				//System.out.println(count);
				double maxRating = Double.NEGATIVE_INFINITY;
				MCTSNode maxChild = null;

				if (lastNode.maxChildren == 1) {
					maxChild = lastNode.clone();
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
					} else {
						temp = lastNode.manChildren.get(0);
					}

					double value = AIUtil.eval(temp, AIParameter.evalWeights, maxRecruitable); ;
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

						maxChild.depth = lastNode.depth + 1;
						if (maxChild.depth > maxTreeDepth) {
							maxTreeDepth = maxChild.depth;
						}
						
						System.out.println("5");
						maxChild.updateHash(lastNode);
						System.out.println("5");
						lastNode.addChild(maxChild);
						return maxChild;
					}

					if (value >= maxRating) {
						maxRating = value;
						maxChild = temp;
					}
				}

				//TODO : Update hash - check uniqueness with it
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

					maxChild.setTreePhase(GameTreeNode.RECRUIT);

					maxChild.setMoveReq(false);
					maxChild.switchMaxPlayer();
					maxChild.getGame().changeCurrentPlayer();

					calculateMaxChildren(maxChild);

					maxChild.depth = lastNode.depth + 1;
					if (maxChild.depth > maxTreeDepth) {
						maxTreeDepth = maxChild.depth;
					}

					System.out.println("6");
					maxChild.updateHash(lastNode);
					System.out.println("6");
					lastNode.addChild(maxChild);
					return maxChild;
				}
			} // while : true
		} // case manoeuvre
		} // switch
		return null;
	}

}

package risk.aiplayers.MCTSPlayers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import org.paukov.combinatorics.*;

import risk.aiplayers.util.AIFeatures;
import risk.aiplayers.util.AIParameter;
import risk.aiplayers.util.AIUtil;
import risk.aiplayers.util.GameTreeNode;
import risk.aiplayers.util.MCTSNode;
import risk.commonObjects.Territory;

/**
 * This AI is using incremental hash to avoid duplications.
 * It also generates all children nodes for a specific parent node when
 * it has a limited amount of possible children. - avoiding further duplications. 
 * @author glebris
 *
 */
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
			
			if(lastNode.maxChildren < GameTreeNode.reasonableChildrenNumber)
			{/*AddEveryPossibleChild, return the last one */
				// Create permutation array
				// System.out.println("Generating recruit nodes " + lastNode.maxChildren + " " + lastNode.getHash());

				int n = AIUtil.calculateRecruitedTroops(lastNode);

				int m = lastNode.getGame().getCurrentPlayer().getTerritories()
						.size();

				Integer[] initialInt = new Integer[n + m - 1];
				int d = 0;
				for (int i = 0; i < n; i++)
					initialInt[d++] = 1;

				for (int i = 0; i < m - 1; i++)
					initialInt[d++] = 0;

				// Create the initial vector
				ICombinatoricsVector<Integer> initialVector = Factory.createVector(
						initialInt);

				// Create the generator
				Generator<Integer> generator = Factory.createPermutationGenerator(initialVector);
				MCTSNode tempChild = null;
				for (ICombinatoricsVector<Integer> perm : generator) {
					tempChild = lastNode.clone();

					Iterator<Territory> it = tempChild.getGame()
							.getCurrentPlayer().getTerritories().values()
							.iterator();
					Territory current = it.next();
					// Place troops according to permutation
					for (int p = 0; p < perm.getSize(); p++) {
						if (perm.getValue(p)==1) {
							current.incrementTroops();
							tempChild.setAttackSource(current.getName());
						} else {
							current = it.next();
						}
					}
					tempChild.setVisitCount(0);
					tempChild.setWinCount(0);
					tempChild.setParent(lastNode);
					tempChild.setChildren(new ArrayList<MCTSNode>());

					tempChild.setTreePhase(GameTreeNode.ATTACK);

					tempChild.setMoveReq(false);

					calculateMaxChildren(tempChild);

					tempChild.depth = lastNode.depth + 1;
					if (tempChild.depth > maxTreeDepth) {
						maxTreeDepth = tempChild.depth;
					}

//					tempChild.updateHash(lastNode);
					getValue(tempChild, lastNode);

					lastNode.addChild(tempChild);
				}
				return tempChild;
			}

			lastNode.numberOfRecruitBranches = Math.min(
					params.MCTSRecruitBranchQualityFactor,
					(lastNode.maxChildren - lastNode.getNumberOfChildren())/ 2);


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
			inRecruit:
				while(true)
				{
					// Generate all and pick randomly from top 30
					MCTSNode maxChild = null;
					double maxRating = Double.NEGATIVE_INFINITY;
					while (maxChild == null) {
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
									tempChild.setAttackSource(current.getName());
								} else {
									current = it.next();
								}
							}
							double value = AIUtil.eval(tempChild, AIParameter.evalWeights, maxRecruitable); 
							// Was getValue() here before. - DuplicationAvoidance in sampling ?
							//TODO: In every sample, I compute again value. (not hash though) - is it a long process ?
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

//					maxChild.updateHash(lastNode);
					//DUPLICATION AVOIDANCE
					Double value = getValue(maxChild, lastNode);
					long key = maxChild.getHash();
//					Double value = NodeValues.get(key);
					if(value != null) {
						Iterator<MCTSNode> it = lastNode.getChildren().iterator();
						if(quickfix < 4) {
							while (it.hasNext()) {
								MCTSNode child = it.next();
								if(key == child.getHash()) {
									quickfix++;
//									System.out.println("Duplo Recruit " + lastNode.numberOfRecruitBranches + " " + lastNode.maxChildren + " children " +lastNode.getChildren().size() +  " " +lastNode.getHash());													
									continue inRecruit;
								}
							}
						}
						while (it.hasNext()) {
							MCTSNode child = it.next();
							if(key == child.getHash() ) {
//								System.out.println("Expanding da Expansion " + child.depth + " " + child.getHash());
								if(child.getChildren().size() < child.maxChildren)
									return Expand(child);
								else
									return child;
							}
						}
					}
					else
						getValue(maxChild, lastNode);
					//END OF DUPLICATION AVOIDANCE

					lastNode.addChild(maxChild);
					return maxChild;
				}
		}
		//********************************ATTACK************************************//
		case GameTreeNode.ATTACK: {
			if(lastNode.maxChildren < GameTreeNode.reasonableChildrenNumber)
			{/*AddEveryPossibleChild, return noAttack (since we are sure that this one is included.)*/
				//System.out.println("Expanding all children, parent's hash is: " + lastNode.getHash());
				//After a little look-up, seems that this is working well. Not called twice on the same.
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
								newChild.setVisitCount(0);
								newChild.setWinCount(0);
								newChild.setParent(lastNode);
								newChild.setChildren(new ArrayList<MCTSNode>());
								calculateMaxChildren(newChild);
								newChild.depth = lastNode.depth + 1;
//								newChild.updateHash(lastNode);
								getValue(newChild, lastNode);
								lastNode.addChild(newChild);
							}
						}
					}
				}

				MCTSNode noAttackChild = lastNode.clone();
				noAttackChild.setTreePhase(GameTreeNode.MANOEUVRE);
				noAttackChild.setAttackSource("");
				noAttackChild.setAttackDest("");
				noAttackChild.setVisitCount(0);
				noAttackChild.setWinCount(0);
				noAttackChild.setParent(lastNode);
				noAttackChild.setChildren(new ArrayList<MCTSNode>());
				calculateMaxChildren(noAttackChild);
				noAttackChild.depth = lastNode.depth + 1;
//				noAttackChild.updateHash(lastNode);
				getValue(noAttackChild, lastNode);
				lastNode.addChild(noAttackChild);
				if(noAttackChild.depth > maxTreeDepth)
					maxTreeDepth = noAttackChild.depth;
				return noAttackChild;
			}

			lastNode.numberOfAttackBranches = Math.min(
					params.MCTSAttackBranchQualityFactor,
					(lastNode.maxChildren - lastNode.getNumberOfChildren()) / 2);


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
			inAtk:
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

//							maxChild.updateHash(lastNode);
							getValue(maxChild, lastNode);
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
						maxChild.updateHash(lastNode);
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

//					maxChild.updateHash(lastNode);

					//Duplication Avoidance
					Double value = getValue(maxChild, lastNode);
					long key = maxChild.getHash();
//					Double value = NodeValues.get(key);
					if(value != null) { //TODO: This quickfix really sucks.
						Iterator<MCTSNode> it = lastNode.getChildren().iterator();
						if(quickfix<4) {
							while (it.hasNext()) {
								MCTSNode child = it.next();
								if(key == child.getHash()) {
									quickfix++;
//									System.out.println("Duplo Atk " + lastNode.maxChildren + " " + 
//											lastNode.attackChildren.size() + " " + maxChild.getAttackDest() + 
//											" " + maxChild.getAttackSource());
									continue inAtk;
								}
							}
						} else {
							while (it.hasNext()) {
								MCTSNode child = it.next();
								if(key == child.getHash() ) {
//									System.out.println("Expanding da Expansion " + child.depth);
									if(child.getChildren().size() < child.maxChildren)
										return Expand(child);
									else
										return child;
								}
							}
						}
					}
					getValue(maxChild, lastNode);
					//Duplication Avoidance

					if(!lastNode.attackChildren.remove(maxChild))
						System.out.println("Node not properly removed from attackChildren");
					calculateMaxChildren(maxChild);
					lastNode.addChild(maxChild);	
					return maxChild;
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

			newChild.updateHash(lastNode);
			lastNode.addChild(newChild);
			return newChild;
		}
		//**********************MOVE AFTER ATTACK***********************************//
		case GameTreeNode.MOVEAFTERATTACK: {
			int totalTroops = lastNode.getGame().getCurrentPlayer()
					.getTerritoryByName(lastNode.getAttackSource())
					.getNrTroops();

			if(totalTroops < GameTreeNode.reasonableChildrenNumber/3){
				MCTSNode newChild = null;
				for(int troops=1; troops<totalTroops; troops++) {
					newChild = lastNode.clone();
					newChild.setTreePhase(GameTreeNode.ATTACK);
					newChild.setMoveAfterAttackCount(troops);
					AIUtil.resolveMoveAction(
							newChild.getGame()
							.getCurrentPlayer()
							.getTerritoryByName(
									newChild.getAttackSource()),
									newChild.getGame()
									.getCurrentPlayer()
									.getTerritoryByName(
											newChild.getAttackDest()), troops);
//					newChild.updateHash(lastNode);
					getValue(newChild, lastNode);
					newChild.setVisitCount(0);
					newChild.setWinCount(0);
					newChild.setParent(lastNode);
					newChild.setChildren(new ArrayList<MCTSNode>());
					newChild.setMoveAfterAttackCount(troops);
					newChild.setMoveReq(false);
					calculateMaxChildren(newChild);

					newChild.depth = lastNode.depth + 1;
					if (newChild.depth > maxTreeDepth) {
						maxTreeDepth = newChild.depth;
					}
					lastNode.addChild(newChild);
				}
				return newChild;
			}

			inMoA:
				while (true) {
					MCTSNode newChild = lastNode.clone();
					newChild.setTreePhase(GameTreeNode.ATTACK);
					int troops = rand.nextInt(totalTroops - 1) + 1;
					newChild.setMoveAfterAttackCount(troops);
					AIUtil.resolveMoveAction(
							newChild.getGame()
							.getCurrentPlayer()
							.getTerritoryByName(
									newChild.getAttackSource()),
									newChild.getGame()
									.getCurrentPlayer()
									.getTerritoryByName(
											newChild.getAttackDest()), troops);
//					newChild.updateHash(lastNode);

					//Duplication Avoidance
					Double value = getValue(newChild, lastNode);
					long key = newChild.getHash();
//					Double value = NodeValues.get(key);
					if(value != null) {
						Iterator<MCTSNode> it = lastNode.getChildren().iterator();
						while (it.hasNext()) {
							MCTSNode child = it.next();
							if(key == child.getHash()) {
								//System.out.println("Duplo MoA " + lastNode.maxChildren + " " + newChild.getMoveAfterAttackCount());
								continue inMoA;
							}
						}
					}
					getValue(newChild, lastNode);
					//Duplication Avoidance

					// Add unique child to existing children
					newChild.setVisitCount(0);
					newChild.setWinCount(0);
					newChild.setParent(lastNode);
					newChild.setChildren(new ArrayList<MCTSNode>());
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
		//******************************MANOEUVRE***********************************//
		case GameTreeNode.MANOEUVRE: {
			if(lastNode.maxChildren < GameTreeNode.reasonableChildrenNumber)
			{/*AddEveryPossibleChild, return noManoeuvreOne (since we are sure that this one is included.)*/
				//System.out.println("Generating manoeuvres: " + lastNode.getHash());
				for (LinkedList<Territory> bucket : lastNode
						.getConnComponentBuckets()) {
					if (bucket.size() > 1) {
						for (Territory src : bucket) {
							if (src.getNrTroops() > 1) {
								for (Territory dest : bucket) {
									if (!src.getName().equals(dest.getName())) {
										// Unique source-dest combo
										for(int troops=1; troops<src.getNrTroops(); troops++){
											MCTSNode newChild = lastNode.clone();
											newChild.setManSource(src);
											newChild.setManDest(dest);
											newChild.setManTroopCount(troops + "");
											AIUtil.resolveMoveAction(
													newChild.getGame()
													.getCurrentPlayer()
													.getTerritoryByName(
															newChild.getManSource().getName()),
															newChild.getGame()
															.getCurrentPlayer()
															.getTerritoryByName(
																	newChild.getManDest().getName()),
																	troops);
											newChild.setTreePhase(GameTreeNode.RECRUIT);
											newChild.setVisitCount(0);
											newChild.setWinCount(0);
											newChild.setParent(lastNode);
											newChild.setChildren(new ArrayList<MCTSNode>());
											newChild.setMoveReq(false);
											newChild.switchMaxPlayer();
											newChild.getGame().changeCurrentPlayer();
											calculateMaxChildren(newChild);
											newChild.depth = lastNode.depth + 1;
//											newChild.updateHash(lastNode);
											getValue(newChild, lastNode);
											lastNode.addChild(newChild);
										}
									}
								}
							}
						}
					}
				}
				MCTSNode noManChild = lastNode.clone();
				noManChild.setManSource(null);
				noManChild.setManDest(null);
				noManChild.setTreePhase(GameTreeNode.RECRUIT);
				noManChild.setVisitCount(0);
				noManChild.setWinCount(0);
				noManChild.setParent(lastNode);
				noManChild.setChildren(new ArrayList<MCTSNode>());
				noManChild.setMoveReq(false);
				noManChild.switchMaxPlayer();
				noManChild.getGame().changeCurrentPlayer();
				calculateMaxChildren(noManChild);
				noManChild.depth = lastNode.depth + 1;
				if (noManChild.depth > maxTreeDepth) {
					maxTreeDepth = noManChild.depth;
				}
//				noManChild.updateHash(lastNode);
				getValue(noManChild, lastNode);
				lastNode.addChild(noManChild);
				return noManChild;

			}

			lastNode.numberOfManoeuvreBranches = Math.min(
					params.MCTSManBranchQualityFactor,
					(lastNode.maxChildren - lastNode.getNumberOfChildren())/ 4);

			if(lastNode.numberOfManoeuvreBranches <1)
				lastNode.numberOfManoeuvreBranches = 1 ;

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
			Random r1 = new Random();
			int count = 0;
			inMan:
				while (true) {
					count++;
					double maxRating = Double.NEGATIVE_INFINITY;
					MCTSNode maxChild = null;

					if (lastNode.maxChildren == 1) {
						maxChild = lastNode.clone();
						maxChild.setTreePhase(GameTreeNode.RECRUIT);
						maxChild.switchMaxPlayer();
						maxChild.getGame().changeCurrentPlayer();
					}

					// Fix search range
					if ((lastNode.maxChildren - lastNode.getChildren().size()) < lastNode.numberOfManoeuvreBranches + 1) {
						lastNode.numberOfManoeuvreBranches--;
					}

					if (lastNode.numberOfManoeuvreBranches < 1) {
						lastNode.numberOfManoeuvreBranches = 1;
					}

					//Which is why is duplication and later on detected.
					for (int i = 0; i < lastNode.numberOfManoeuvreBranches; i++) {
						int index = r1.nextInt(lastNode.maxChildren);
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
											- lastNode.manTroopBins
											.get(middle - 1);
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
											temp.getManSource()
											.getName()),
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

					double value = AIUtil.eval(temp, AIParameter.evalWeights, maxRecruitable);
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
//						maxChild.updateHash(lastNode);
						lastNode.addChild(maxChild);
						return maxChild;
					}

					if (value >= maxRating) {
						//Test
						maxRating = value;
						maxChild = temp;
					}
				}
//			maxChild.updateHash(lastNode);
			
			//Duplication Avoidance
			Double value = getValue(maxChild, lastNode);
			long key = maxChild.getHash();
//			Double value = NodeValues.get(key);
			if(value != null) { //TODO: quickfix ugh.
				Iterator<MCTSNode> it = lastNode.getChildren().iterator();
				if(quickfix < 4) {
					while (it.hasNext()) {
						MCTSNode child = it.next();
						if(key == child.getHash() ) {
							quickfix++;
//							System.out.println("Duplo Manoeuvre maxChild source: " + maxChild.getManSource() + 
//									" children " + lastNode.getChildren().size() + " " + lastNode.getHash() +
//									" " + maxChild.getHash() + " " + child.getHash());
							continue inMan;
						}
					}
				} else {
					while(it.hasNext()) {
						MCTSNode child = it.next();
						if(key == child.getHash() ) {
//							System.out.println("Expanding da Expansion " + child.depth);
							if(child.getChildren().size() < child.maxChildren)
								return Expand(child);
							else
								return child;
						}
					}
				}
			}
			getValue(maxChild, lastNode);
			//Duplication Avoidance

			// Add unique child to existing children

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
		} // while : true
		} // case manoeuvre
	} // switch
	return null;
}
}

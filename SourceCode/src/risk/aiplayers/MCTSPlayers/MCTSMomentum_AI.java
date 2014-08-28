package risk.aiplayers.MCTSPlayers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import risk.aiplayers.util.AIFeatures;
import risk.aiplayers.util.AIUtil;
import risk.aiplayers.util.GameTreeNode;
import risk.aiplayers.util.MCTSNode;
import risk.commonObjects.Territory;

/**
 * Small upgrade from Move_After_Attack :
 * always consider the same attack action as before. "MoMentuM"
 * @author glebris
 *
 */
public class MCTSMomentum_AI extends MCTSMove_After_Attack_AI{

	public static void main(String[] args) {
		String tempName = args[0];
		long time = Long.parseLong(args[1]);
		new MCTSMomentum_AI(tempName, null, null, 2, time);
	}

	public MCTSMomentum_AI(String name, String opp, String map, int id,
			long time) {
		super(name, opp, map, id, time);
	}

	@Override
	protected MCTSNode Expand(MCTSNode lastNode) {
		//System.out.println(lastNode.getTreePhaseText());
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
			// System.out.println("Recruit 2");
			MCTSNode maxChild = null;
			double maxRating = Double.NEGATIVE_INFINITY;
			while (maxChild == null) {
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
					tempChild.setTreePhase(GameTreeNode.ATTACK);
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
						(lastNode.maxChildren - lastNode.getChildren().size()) / 2);
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
							if (dest != null && 
									!( t.getName()==lastNode.getAttackSource() && dest.getName()==lastNode.getAttackDest() )
									) {
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

			if(!((lastNode.getAttackDest() == "" || lastNode.getAttackDest() == null))) {
				if(lastNode.getGame().getCurrentPlayer().getTerritoryByName(lastNode.getAttackSource()).getNrTroops()>1 && lastNode.Momentum) {
					lastNode.Momentum = false; //To not lock on this and add x times the same attack node.
					MCTSNode MomentumChild = lastNode.clone();
					MomentumChild.setTreePhase(GameTreeNode.RANDOMEVENT);
					MomentumChild.setValue(getValue(MomentumChild, lastNode));

					MomentumChild.setVisitCount(0);
					MomentumChild.setWinCount(0);
					MomentumChild.setParent(lastNode);
					MomentumChild.setChildren(new ArrayList<MCTSNode>());

					calculateMaxChildren(MomentumChild);

					MomentumChild.depth = lastNode.depth + 1;
					if (MomentumChild.depth > maxTreeDepth) {
						maxTreeDepth = MomentumChild.depth;
					}
					lastNode.addChild(MomentumChild);
					return MomentumChild;
				}
			} //Remove it from attackChildren - Done : it is not added in the first place.

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
				//System.out.println("1 " + lastNode.maxChildren + " " + lastNode.getHash()); //TODO: Stuck here. Not anymore ?
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
						maxChild.updateHash(lastNode);
						
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

					if(maxChild.getTreePhase() == GameTreeNode.RANDOMEVENT) {
						maxChild.Momentum = true;
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
				newChild.Momentum = false;
			} else {
				newChild.setTreePhase(GameTreeNode.ATTACK);
				newChild.Momentum = true;
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
						lastNode.maxChildren / 4);
			}

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

					int nrTroops = -1;
					MCTSNode temp = null;
					int little = 0,big = 0, tempIndex = 0;
					boolean found = false;
					if (index > 0) {
						tempIndex = lastNode.manTroopBins.size()-1;
						while(!found) {
							little=lastNode.manTroopBins.get(tempIndex-1);
							big=lastNode.manTroopBins.get(tempIndex);

							if(little<index && index<=big) {
								temp = lastNode.manChildren.get(tempIndex).clone();
								found = true;
							}
							tempIndex--;
						}

						nrTroops = index - little;
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

					maxChild.setTreePhase(GameTreeNode.RECRUIT);

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
}

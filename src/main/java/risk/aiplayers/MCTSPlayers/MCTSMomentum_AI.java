package risk.aiplayers.MCTSPlayers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import risk.aiplayers.util.AIFeatures;
import risk.aiplayers.util.AIUtil;
import risk.aiplayers.util.MCTSNode;
import risk.aiplayers.util.NodeType;
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
			MCTSNode maxChild = null;
			double maxRating = Double.NEGATIVE_INFINITY;
			while (maxChild == null) {
//				System.out.println("Loop Mom ");
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
						(lastNode.maxChildren() - lastNode.getChildren().size()) / 2);
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
							if (dest != null && 
									!( t.getName()==lastNode.getAttackSource() && dest.getName()==lastNode.getAttackDest() )
									) {
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

			if(!((lastNode.getAttackDest() == "" || lastNode.getAttackDest() == null))) {
				if(lastNode.getGame().getCurrentPlayer().getTerritoryByName(lastNode.getAttackSource()).getNrTroops()>1 && lastNode.Momentum) {
					lastNode.Momentum = false; //To not lock on this and add x times the same attack node.
					MCTSNode MomentumChild = lastNode.clone();
					MomentumChild.setTreePhase(NodeType.RANDOMEVENT);
					MomentumChild.setValue(getWeightedEval(MomentumChild, lastNode));

					MomentumChild.setVisitCount(0);
					MomentumChild.setWinCount(0);
					MomentumChild.setParent(lastNode);
					MomentumChild.setChildren(new ArrayList<MCTSNode>());

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
				noAttackChild.setTreePhase(NodeType.MANOEUVRE);
				noAttackChild.setAttackSource("");
				noAttackChild.setAttackDest("");
				noAttackChild.setValue(getValue(noAttackChild, lastNode));
				// Add option to not attack
				lastNode.attackChildren.add(noAttackChild);
			}

			int count = 0;
			while (true) {
//				System.out.println("Loop1 Mom ");
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
					System.out.println("Clone6 " + lastNode.numberOfAttackBranches);
					maxChild = lastNode.clone();
					maxChild.setTreePhase(NodeType.MANOEUVRE);
					maxChild.setAttackSource("");
					maxChild.setAttackDest("");
					maxChild.updateHash(lastNode);

					maxChild.depth = lastNode.depth + 1;
					if (maxChild.depth > maxTreeDepth) {
						maxTreeDepth = maxChild.depth;
					}
					
					lastNode.addChild(maxChild);
					return maxChild;
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

					if(maxChild.getTreePhase() == NodeType.RANDOMEVENT) {
						maxChild.Momentum = true;
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
				newChild.Momentum = false;
			} else {
				newChild.setTreePhase(NodeType.ATTACK);
				newChild.Momentum = true;
			}
			newChild.updateHash(lastNode);
			
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
//				System.out.println("Loop2 Mom ");
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
//				System.out.println("Loop3 Mom ");
				count++;
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
								temp = lastNode.clone();
								temp.setManSource(lastNode.manSources.get(tempIndex));
								temp.setManDest(lastNode.manDests.get(tempIndex));
								temp.setTreePhase(NodeType.RECRUIT);
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

						maxChild.setMoveReq(false);
						maxChild.switchMaxPlayer();
						maxChild.getGame().changeCurrentPlayer();

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

					maxChild.setTreePhase(NodeType.RECRUIT);

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
}

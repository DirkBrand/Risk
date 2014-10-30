package risk.aiplayers.MCTSPlayers;


import java.util.Arrays;
import java.util.Iterator;

import risk.aiplayers.util.AIUtil;
import risk.aiplayers.util.GameTreeNode;
import risk.aiplayers.util.MCTSNode;
import risk.commonObjects.Territory;

/**
 * MCTS AI which Simulate following the baseline strategy.
 * @author Bruce Almighty
 *
 */
public class MCTSMove_After_Simulate_Baseline_AI extends MCTSMove_After_Attack_AI {

	Territory lastAttackDest = null;
	public static void main(String[] args) {
		String tempName = args[0];
		long time = Long.parseLong(args[1]);
		new MCTSMove_After_Simulate_Baseline_AI(tempName, null, null, 2, time);
	}

	public MCTSMove_After_Simulate_Baseline_AI(String name, String opp, String map, int id,
			long time) {
		super(name, opp, map, id, time);
	}

	/**
	 * Simulation following the baseline strategy.
	 */
	@Override
	protected int Simulate(MCTSNode lastNode) {
		MCTSNode playNode = lastNode.clone();
		while (!AIUtil.isTerminalNode(playNode)) {
			switch (playNode.getTreePhase()) {
			case GameTreeNode.RECRUIT: {

				int number = AIUtil.calculateRecruitedTroops(playNode);

				int maxID = 0;
				double max = Double.MIN_VALUE;

				Iterator<Territory> it =  playNode.getGame().getCurrentPlayer().getTerritories().values().iterator();
				while (it.hasNext()) {
					Territory t = it.next();
					double sum = 0.0;
					for (Territory n : t.getNeighbours()) {
						Territory temp = playNode.getGame().getOtherPlayer().getTerritoryByName(n.getName());
						if (temp != null) {
							sum += temp.getNrTroops();
						}
					}
					if (sum == 0.0)
						continue;
					double ratio = t.getNrTroops() / sum;

					// Looking for the best ratio.
					if (ratio > max) {
						max = ratio;
						maxID = t.getId();
					}
				}

				playNode.setRecruitedTer(playNode.getGame().getCurrentPlayer()
						.getTerritoryByID(maxID));

				playNode.getRecruitedTer().setNrTroops(
						playNode.getRecruitedTer().getNrTroops() + number);

				playNode.setAttackSource(playNode.getRecruitedTer().getName());

				playNode.setTreePhase(GameTreeNode.ATTACK);
				playNode.setMoveReq(false);

				break;
			}
			// To fasten it up : memorizing lastAttackDest to directly attack it again.
			// Then when it is no longer possible to attack (Manoeuvre - MoveAfterAttack), it goes back to null. 
			case GameTreeNode.ATTACK: {

				Territory attackSource = playNode.getGame().getCurrentPlayer()
						.getTerritoryByName(playNode.getAttackSource());

				int minId = -1;

				// Determines whether should attack again
				if (attackSource.getNrTroops() == 1) {
					lastAttackDest = null;
					playNode.setTreePhase(GameTreeNode.MANOEUVRE);
					break;
				}

				//Checking if already attacking a specific territory (Baseline will keep on attacking this one)
				if(lastAttackDest != null) {
					minId = lastAttackDest.getId();
				} else {
					if (attackSource.getNrTroops() > 1) {
						int min = Integer.MAX_VALUE;
						for (Territory t : attackSource.getNeighbours()) {
							Territory temp = playNode.getGame().getOtherPlayer().getTerritoryByName(t.getName());
							if (temp != null) {
								if(temp.getNrTroops() < min) {
									min = temp.getNrTroops();
									minId = temp.getId();
								}
							}
						}

						//No interesting territory to attack.
						if (minId == -1) {
							lastAttackDest = null;
							playNode.setTreePhase(GameTreeNode.MANOEUVRE);
							break;
						}

						lastAttackDest = playNode.getGame().getOtherPlayer()
								.getTerritoryByID(minId);
					}
				}

				playNode.setAttackSource(attackSource.getName());
				playNode.setAttackDest(playNode.getGame().getOtherPlayer()
						.getTerritoryByID(minId).getName());
				playNode.setTreePhase(GameTreeNode.RANDOMEVENT);

				break;
			}
			case GameTreeNode.RANDOMEVENT: {
				int sourceTroops = playNode.getGame().getCurrentPlayer()
						.getTerritoryByName(playNode.getAttackSource())
						.getNrTroops();
				int destTroops = playNode.getGame().getOtherPlayer()
						.getTerritoryByName(playNode.getAttackDest())
						.getNrTroops();

				int attackD[] = new int[] { 0, 0, 0 };
				int defendD[] = new int[] { 0, 0 };

				// Attacker
				if (sourceTroops == 2) {
					attackD[0] = AIUtil.genRoll();
				} else if (sourceTroops == 3) {
					attackD[0] = AIUtil.genRoll();
					attackD[1] = AIUtil.genRoll();
				} else if (sourceTroops > 3) {
					attackD[0] = AIUtil.genRoll();
					attackD[1] = AIUtil.genRoll();
					attackD[2] = AIUtil.genRoll();
				}

				if (destTroops == 2 || destTroops == 1) {
					defendD[0] = AIUtil.genRoll();
				} else if (destTroops > 2) {
					defendD[0] = AIUtil.genRoll();
					defendD[1] = AIUtil.genRoll();
				}

				Arrays.sort(attackD);
				Arrays.sort(defendD);

				playNode.setDiceRolls(attackD[0], attackD[1], attackD[2],
						defendD[0], defendD[1]);

				AIUtil.resolveAttackAction(playNode);
				if (playNode.moveRequired()) {
					lastAttackDest=null; // Territory just conquered, no longer targeted.
					playNode.setTreePhase(GameTreeNode.MOVEAFTERATTACK);
				} else {
					playNode.setTreePhase(GameTreeNode.ATTACK);
				}

				break;
			}
			case GameTreeNode.MOVEAFTERATTACK: {
				int totalTroops = playNode.getGame().getCurrentPlayer()
						.getTerritoryByName(playNode.getAttackSource())
						.getNrTroops();
				int troops = totalTroops -1;

				AIUtil.resolveMoveAction(playNode.getGame().getCurrentPlayer()
						.getTerritoryByName(playNode.getAttackSource()),
						playNode.getGame().getCurrentPlayer()
						.getTerritoryByName(playNode.getAttackDest()),
						troops);

				playNode.setMoveReq(false);

				playNode.setTreePhase(GameTreeNode.ATTACK);

				break;
			}
			case GameTreeNode.MANOEUVRE: {
				AIUtil.updateRegions(playNode.getGame());
				int minID = -1;
				int min = Integer.MAX_VALUE;
				int maxID = -1;
				int max = Integer.MIN_VALUE;

				Iterator<Territory> it = playNode.getGame().getCurrentPlayer()
						.getTerritories().values().iterator();

				// Most populated territory owned by current.
				while (it.hasNext()) {
					Territory t = it.next();
					if (t.getNrTroops() > max) {
						max = t.getNrTroops();
						maxID = t.getId();
					}
				}
				Territory source = playNode.getGame().getCurrentPlayer()
						.getTerritoryByID(maxID);

				it = playNode.getGame().getCurrentPlayer().getTerritories()
						.values().iterator();

				// Least populated territory owned by current connected to the most one.
				while (it.hasNext()) {
					Territory t = it.next();
					if (t.getNrTroops() < min
							&& t.connectedRegion == source.connectedRegion) {
						min = t.getNrTroops();
						minID = t.getId();
					}
				}

				//Should not happen - source at least will be considered as dest.
				if (minID == -1) {
					playNode.setTreePhase(GameTreeNode.RECRUIT);
					playNode.switchMaxPlayer();
					playNode.getGame().changeCurrentPlayer();
					break;
				}
				Territory dest = playNode.getGame().getCurrentPlayer()
						.getTerritoryByID(minID);

				if (source.getId() != dest.getId()) {
					int total = source.getNrTroops() + dest.getNrTroops();
					source.setNrTroops((int) (total / 2.0));
					dest.setNrTroops(total - (int) (total / 2.0));
				}

				playNode.setTreePhase(GameTreeNode.RECRUIT);
				playNode.switchMaxPlayer();
				playNode.getGame().changeCurrentPlayer();

				break;
			}
			}
		}

		if (playNode.getGame().getOtherPlayer().getTerritories().size() == 0) {
			if (playNode.isMaxPlayer())
				return 1; // A win for the max player
			else
				return 0; // A loss for the max player
		} else if (playNode.getGame().getCurrentPlayer().getTerritories()
				.size() == 0) {
			if (playNode.isMaxPlayer())
				return 0; // A loss for the max player
			else
				return 1; // A win for the max player
		} else {
			return 0;
		}

	}
}
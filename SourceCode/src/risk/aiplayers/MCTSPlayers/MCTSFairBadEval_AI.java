package risk.aiplayers.MCTSPlayers;

import risk.aiplayers.util.AIUtil;
import risk.aiplayers.util.MCTSNode;
import risk.commonObjects.GameState;

/**
 * MCTS Fair expansion AI that uses equal weights for the players.
 * 
 * @author Dirk
 *
 */
public class MCTSFairBadEval_AI extends MCTSFairExpansion_AI {
	double weights[] = new double[] { 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0 };
	
	public MCTSFairBadEval_AI(String name, String opp, String map,
			int id, long time) {
		super(name, opp, map, id, time);
	} 

	public static void main(String[] args) {
		String tempName = args[0];
		long time = Long.parseLong(args[1]);
		new MCTSFairBadEval_AI(tempName, null, null, 2, time);
	}

	@Override
	protected double getValue(MCTSNode node, MCTSNode parent) {

		long key = node.getHash();
		Double value = NodeValues.get(key);
		if (value != null) {
			foundIt++;
			return value;
		} else {
			missedIt++;
			value = AIUtil.eval(node, weights, maxRecruitable);
			NodeValues.put(node.getHash(), value);
			return value;
		}
	}

}

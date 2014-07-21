package risk.aiplayers.MCTSPlayers;


import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import risk.aiplayers.MonteCarloTreeSearchPlayer;
import risk.aiplayers.util.AIFeatures;
import risk.aiplayers.util.AIParameter;
import risk.aiplayers.util.AIUtil;
import risk.aiplayers.util.BinaryTree;
import risk.aiplayers.util.GameTreeNode;
import risk.aiplayers.util.MCTSNode;
import risk.commonObjects.GameState;
import risk.commonObjects.Player;
import risk.commonObjects.Territory;

/**
 * MCTS AI adapted from MCTSFair_Expansion.
 * It now takes into account the previously built MCTS when moving after a successful attack. 
 * @author Wolfgang Amadeus Mozart
 *
 */
public class MCTSMove_After_Attack_AI extends MCTSFairExpansion_AI {

	public static void main(String[] args) {
		String tempName = args[0];
		long time = Long.parseLong(args[1]);
		new MCTSFairExpansion_AI(tempName, null, null, 2, time);
	}

	public MCTSMove_After_Attack_AI(String name, String opp, String map, int id,
			long time) {
		super(name, opp, map, id, time);
	}

	@Override
	public LinkedList<String> getMoveAfterAttack() {
		LinkedList<String> reply = new LinkedList<String>();
		reply.add(lastAttackSource.getId() + "");
		reply.add(lastAttackDestination.getId() + "");

		globalNode.depth = 1;
		treeDepth = 0;
		maxTreeDepth = Integer.MIN_VALUE;
		treeNodeCount = 0;
		MCTSNode action = MCTSSearch(globalNode);	
		globalNode=action;
		globalNode.setParent(null);

		reply.add(action.getMoveAfterAttackCount() + "");

		return reply;
	}
}

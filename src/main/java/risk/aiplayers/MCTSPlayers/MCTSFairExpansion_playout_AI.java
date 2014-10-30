package risk.aiplayers.MCTSPlayers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import risk.aiplayers.util.AIFeatures;
import risk.aiplayers.util.AIUtil;
import risk.aiplayers.util.BinaryTree;
import risk.aiplayers.util.GameTreeNode;
import risk.aiplayers.util.MCTSNode;
import risk.commonObjects.GameState;
import risk.commonObjects.Player;
import risk.commonObjects.Territory;

public class MCTSFairExpansion_playout_AI extends MCTSFairExpansion_AI {

    int playoutsForMCTS;

	public static void main(String[] args) {
		String tempName = args[0];
		int playouts = Integer.parseInt(args[1]);
		new MCTSFairExpansion_playout_AI(tempName, null, null, 2, playouts);
	}

	public MCTSFairExpansion_playout_AI(String name, String opp, String map,
			int id, int playouts) {
		super(name, opp, map, id, 0);

		this.playoutsForMCTS = playouts;
	}

	

	// Main MCTS method
	@Override
	public MCTSNode MCTSSearch(MCTSNode rootNode) {
		int playoutCount = 0;
		while (playoutCount < playoutsForMCTS) {
			MCTSNode currentNode = rootNode;

			// System.out.println("Tree Policy");
			MCTSNode leafNode = TreePolicy(currentNode);

			// System.out.println("Simulate");
			int R = Simulate(leafNode);

			// System.out.println("BackPropagate");
			BackPropagate(leafNode, R);
			playoutCount++;
		}
		// Choosing the best child of the root
		double max = Double.NEGATIVE_INFINITY;
		MCTSNode selected = null;
		for (MCTSNode child : rootNode.getChildren()) {
			if (child.getVisitCount() > max) {
				selected = child;
				max = child.getVisitCount();
			}
		}
		return selected;
	}

	
	
}

package risk.paperplayers;

import risk.aiplayers.util.MCTSNode;

public class S_MCTS_Explore_AI extends S_MCTS_Naive_AI {

	int playoutsForMCTS;

	public static void main(String[] args) {
		String tempName = args[0];
		int playouts = Integer.parseInt(args[1]);
		new S_MCTS_Explore_AI(tempName, null, null, 2, playouts);
	}

	public S_MCTS_Explore_AI(String name, String opp, String map,
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

package risk.aiGameCoordinator;

import risk.aiplayers.AIPlayer;
import risk.aiplayers.EMMPlayers.EMMGreedyAI;
import risk.paperplayers.S_BaselineAI;
import risk.paperplayers.S_EMM_AI;
import risk.paperplayers.S_MCTS_AI;
import risk.paperplayers.S_MCTS_Explore_AI;
import risk.paperplayers.S_MCTS_Naive_AI;

public class GameCoordinatorForClop {
	static String AI1_Name;
	static String AI2_Name;
	static String map;
	static double[] weights;
	double [] fixedWeights =  new double [] {7.47,	29.92,	-26.78,	-3.43,	57.16,	-20.67,	11.43,	37.33,	6.55,	35.09,	-0.20,	43.02,	-6.70};
	

	int doneCount = 0;

	public static void main(String[] args) {

		AI1_Name = args[0];
		AI2_Name = args[1];
		map = args[2];
		weights = new double[] { Double.parseDouble(args[3]),
				Double.parseDouble(args[4]), Double.parseDouble(args[5]),
				Double.parseDouble(args[6]), Double.parseDouble(args[7]),
				Double.parseDouble(args[8]), Double.parseDouble(args[9]),
				Double.parseDouble(args[10]), Double.parseDouble(args[11]),
				Double.parseDouble(args[12]), Double.parseDouble(args[13]),
				Double.parseDouble(args[14]), Double.parseDouble(args[15]) };

		new GameCoordinatorForClop();
	}

	public GameCoordinatorForClop() {
		Thread t1 = new Thread(new AIRunThread(AI2_Name, null, null, 2));
		t1.start();

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Thread t2 = new Thread(new AIRunThread(AI1_Name, AI2_Name, map, 1));
		t2.start();		
		
		Thread t3 = new Thread(new checkForDrawThread());
		t3.start();
	}
	
	public void draw() {
		System.out.println("None");
		System.exit(-1);
	}
	
	class checkForDrawThread implements Runnable {

		@Override
		public void run() {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if (doneCount != 2) {
				draw();
			}
		}
		
	}

	class AIRunThread implements Runnable {
		String ai1;
		String ai2;
		String theMap;
		AIPlayer theAI;

		int id;

		public AIRunThread(String AI1name, String AI2name, String mapName,
				int playerID) {
			this.ai1 = AI1name;
			this.ai2 = AI2name;
			this.theMap = mapName;
			this.id = playerID;
		}

		@Override
		public void run() {
			// Start AI
			switch (ai1) {
			case "BaselineAI": {
				theAI = new S_BaselineAI(ai1, ai2, theMap, id);
				break;
			}
			case "GreedyAI": {
				theAI = new EMMGreedyAI(ai1, ai2, theMap, id, weights);
				break;
			}
			case "EMMAI": {
				theAI = new S_EMM_AI(ai1, ai2, theMap, id, 4);
				break;
			}
			case "MCTSAI": {
				theAI = new S_MCTS_AI(ai1, ai2, theMap, id, 5000);
				break;
			}
			case "MCTSNaiveAI": {
				theAI = new S_MCTS_Naive_AI(ai1, ai2, theMap, id, 5000);
				break;
			}
			case "GreedyFixedAI": {
				theAI = new S_MCTS_Explore_AI(ai1, ai2, theMap, id, 5000);
				break;
			}
			default: {
				System.out.println("Unknown AI: " + ai1);
				break;
			}
			}

			doneCount++;

		}

	}
}

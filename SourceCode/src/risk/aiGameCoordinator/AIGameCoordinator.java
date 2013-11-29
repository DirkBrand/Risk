package risk.aiGameCoordinator;

import risk.aiplayers.BaselineAI;
import risk.aiplayers.SubmissiveAI;
import risk.aiplayers.EMMPlayers.EMMBaselineAI;
import risk.aiplayers.EMMPlayers.EMMGreedyAI;
import risk.aiplayers.EMMPlayers.EMM_Advanced_AI;
import risk.aiplayers.MCTSPlayers.MCTSBaselineAI;
import risk.aiplayers.MCTSPlayers.MCTSBaseline_playout_AI;
import risk.aiplayers.MCTSPlayers.MCTSFairBadEval_AI;
import risk.aiplayers.MCTSPlayers.MCTSFairExpansion_AI;
import risk.aiplayers.MCTSPlayers.MCTSFairExpansion_playout_AI;
import risk.aiplayers.MCTSPlayers.MCTSRandomAI;
import risk.aiplayers.MCTSPlayers.MCTS_Advanced_AI;
import risk.aiplayers.MCTSPlayers.MCTS_Advanced_playout_AI;

public class AIGameCoordinator {
	static String AI1_Name;
	static String AI2_Name;
	static String map;
	
	static double[] weights;

	int timeForMCTS_Milliseconds = 3000;
	int playoutsForMCTS = 1500;
	int EMM_depth = 4;
	

	public static void main(String[] args) {
		if (args.length != 3) {
			System.err.println("Insufficient Arguments!");
			System.exit(0);
		}
		AI1_Name = args[0];
		AI2_Name = args[1];
		map = args[2];
		weights = new double[] { 1,1,1,1,1,1,1,1,1,1,1,1,1 };

		new AIGameCoordinator();
	}

	public AIGameCoordinator() {
		Thread t1 = new Thread(new AIRunThread(AI2_Name, null, null,2));
		t1.start();
		
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Thread t2 = new Thread(new AIRunThread(AI1_Name, AI2_Name, map,1));
		t2.start();
	}
	
	class AIRunThread implements Runnable {
		String ai1;
		String ai2;
		String theMap;
		

		int id;

		public AIRunThread(String AI1name, String AI2name, String mapName,
				int playerID) {
			this.ai1 = AI1name;
			this.ai2 = AI2name;
			this.theMap = mapName;
			this.id = playerID;}

		@Override
		public void run() {
			// Start AI
			switch (ai1) {
			case "BaselineAI": {
				new BaselineAI(ai1, ai2, theMap, id);
				break;
			}
			case "Submissive_AI": {
				new SubmissiveAI(ai1, ai2, theMap, id);
				break;
			}
			case "EMM_Advanced_AI": {
				new EMM_Advanced_AI(ai1, ai2, theMap, id, EMM_depth);
				break;
			}
			case "EMMBaselineAI": {
				new EMMBaselineAI(ai1, ai2, theMap, id, EMM_depth);
				break;
			}
			case "MCTS_Advanced_AI": {
				new MCTS_Advanced_AI(ai1, ai2, theMap, id, timeForMCTS_Milliseconds);
				break;
			}
			case "MCTS_Advanced_playout_AI": {
				new MCTS_Advanced_playout_AI(ai1, ai2, theMap, id, playoutsForMCTS);
				break;
			}
			case "MCTSFairExpansion_AI": {
				new MCTSFairExpansion_AI(ai1, ai2, theMap, id, timeForMCTS_Milliseconds);
				break;
			}
			case "MCTSFairExpansion_playout_AI": {
				new MCTSFairExpansion_playout_AI(ai1, ai2, theMap, id, playoutsForMCTS);
				break;
			}
			case "MCTSFairBadEval_AI": {
				new MCTSFairBadEval_AI(ai1, ai2, theMap, id, timeForMCTS_Milliseconds);
				break;
			}
			case "MCTSRandomAI": {
				new MCTSRandomAI(ai1, ai2, theMap, id, timeForMCTS_Milliseconds);
				break;
			}
			case "MCTSBaselineAI": {
				new MCTSBaselineAI(ai1, ai2, theMap, id, timeForMCTS_Milliseconds);
				break;
			}
			case "MCTSBaseline_playout_AI": {
				new MCTSBaseline_playout_AI(ai1, ai2, theMap, id, playoutsForMCTS);
				break;
			}
			case "EMMGreedyAI": {
				new EMMGreedyAI(ai1, ai2, theMap, id, weights);
				break;
			}
			default: {
				System.out.println("Unknown AI: " + ai1);
				break;
			}
			}


		}
		
	}

}

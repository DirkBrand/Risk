package risk.aiGameCoordinator;

import risk.aiplayers.Baseline_AI;
import risk.aiplayers.Submissive_AI;
import risk.aiplayers.EMMPlayers.EMMBaseline_AI;
import risk.aiplayers.EMMPlayers.EMMGreedy_AI;
import risk.aiplayers.EMMPlayers.EMM_Advanced_AI;
import risk.aiplayers.MCTSPlayers.MCTSBaseline_playout_AI;
import risk.aiplayers.MCTSPlayers.MCTSFairBadEval_AI;
import risk.aiplayers.MCTSPlayers.MCTSFairExpansion_AI;
import risk.aiplayers.MCTSPlayers.MCTSFairExpansion_playout_AI;
import risk.aiplayers.MCTSPlayers.MCTSFull_Baseline_AI;
import risk.aiplayers.MCTSPlayers.MCTSGenerate_Low_Children_AI;
import risk.aiplayers.MCTSPlayers.MCTSHashing_Uniqueness_AI;
import risk.aiplayers.MCTSPlayers.MCTSMomentum_AI;
import risk.aiplayers.MCTSPlayers.MCTSMove_After_Attack_AI;
import risk.aiplayers.MCTSPlayers.MCTSMove_After_Simulate_Baseline_AI;
import risk.aiplayers.MCTSPlayers.MCTSRandom_AI;
import risk.aiplayers.MCTSPlayers.MCTSSample_Duplic_AI;
import risk.aiplayers.MCTSPlayers.MCTS_Advanced_AI;
import risk.aiplayers.MCTSPlayers.MCTS_Advanced_playout_AI;
import risk.aiplayers.paperplayers.S_EMM_AI;
import risk.aiplayers.paperplayers.S_MCTS_AI;
import risk.aiplayers.paperplayers.S_MCTS_Baseline_AI;
import risk.aiplayers.paperplayers.S_MCTS_Explore_AI;
import risk.aiplayers.paperplayers.S_MCTS_Naive_AI;
import risk.aiplayers.paperplayers.S_Simulation_AI;

public class AIGameCoordinator {
	static String AI1_Name;
	static String AI2_Name;
	
	static double[] weights;

	int timeForMCTS_Milliseconds = 2500;
	int playoutsForMCTS = 1500;
	int EMM_depth = 5;

	String map = "OriginalRiskMapFile.txt";

	public static void main(String[] args) {
		if (args.length != 2) {
			System.err.println("Insufficient Arguments!");
			System.exit(0);
		}
		AI1_Name = args[0];
		AI2_Name = args[1];

		new AIGameCoordinator();
	}

	public AIGameCoordinator() {
		Thread t1 = new Thread(new AIRunThread(AI2_Name, null, null,2));
		t1.start();
		
		try {
			Thread.sleep(1000);
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
			case "Baseline_AI": {
				new Baseline_AI(ai1, ai2, theMap, id);
				break;
			}
			case "Submissive_AI": {
				new Submissive_AI(ai1, ai2, theMap, id);
				break;
			}
			case "EMM_Advanced_AI": {
				new EMM_Advanced_AI(ai1, ai2, theMap, id, EMM_depth);
				break;
			}
			case "EMMBaseline_AI": {
				new EMMBaseline_AI(ai1, ai2, theMap, id, EMM_depth);
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
			case "MCTSMove_After_Attack_AI" : {
				new MCTSMove_After_Attack_AI(ai1, ai2, theMap, id, timeForMCTS_Milliseconds);
				break;
			}
			case "MCTSFairBadEval_AI": {
				new MCTSFairBadEval_AI(ai1, ai2, theMap, id, timeForMCTS_Milliseconds);
				break;
			}
			case "MCTSRandom_AI": {
				new MCTSRandom_AI(ai1, ai2, theMap, id, timeForMCTS_Milliseconds);
				break;
			}
			case "MCTSBaseline_playout_AI": {
				new MCTSBaseline_playout_AI(ai1, ai2, theMap, id, playoutsForMCTS);
				break;
			}
			case "MCTSFull_Baseline_AI": {
				new MCTSFull_Baseline_AI(ai1, ai2, theMap, id, timeForMCTS_Milliseconds);
				break;
			}
			case "MCTSMove_After_Simulate_Baseline_AI": {
				new MCTSMove_After_Simulate_Baseline_AI(ai1, ai2, theMap, id, timeForMCTS_Milliseconds);
				break;
			}
			case "MCTSHashing_Uniqueness_AI": {
				new MCTSHashing_Uniqueness_AI(ai1, ai2, theMap, id, timeForMCTS_Milliseconds);
				break;
			}
			case "MCTSSample_Duplic_AI": {
				new MCTSSample_Duplic_AI(ai1, ai2, theMap, id, timeForMCTS_Milliseconds);
				break;
			}
			case "MCTSGenerate_Low_Children_AI": {
				new MCTSGenerate_Low_Children_AI(ai1, ai2, theMap, id, timeForMCTS_Milliseconds);
				break;
			}
			case "MCTSMomentum_AI": {
				new MCTSMomentum_AI(ai1, ai2, theMap, id, timeForMCTS_Milliseconds);
				break;
			}
			case "EMMGreedy_AI": {
				new EMMGreedy_AI(ai1, ai2, theMap, id, weights);
				break;
			}case "Simulation_AI": {
				new S_Simulation_AI(ai1, ai2, theMap, id);
				break;
			}
			case "EMM_AI": {
				new S_EMM_AI(ai1, ai2, theMap, id, EMM_depth);
				break;
			}
			case "MCTS_AI": {
				new S_MCTS_AI(ai1, ai2, theMap, id,
						timeForMCTS_Milliseconds);
				break;
			}
			case "MCTSNaive_AI": {
				new S_MCTS_Naive_AI(ai1, ai2, theMap, id,
						timeForMCTS_Milliseconds);
				break;
			}
			case "MCTSExplore_AI": {
				new S_MCTS_Explore_AI(ai1, ai2, theMap, id,
						timeForMCTS_Milliseconds);
				break;
			}
			case "MCTSBaseline_AI": {
				new S_MCTS_Baseline_AI(ai1, ai2, theMap, id,
						timeForMCTS_Milliseconds);
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

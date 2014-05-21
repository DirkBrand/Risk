package risk.aiplayers.util;

import java.util.Arrays;
import java.util.Random;

public class AIParameter {

	public double epsilon = 1e-6;

	public Random r = new Random();
	
	// EMM
	public double EMMAttackThreshold = 0.3;
	
	public int EMMRecruitBranchingLimit = 10;
	public int EMMAttackBranchingLimit = 10;
	public int EMMMoveAfterAttackBranchingLimit = 10;
	public int EMMManBranchingLimit = 10;
	 
	public double EMMRecruitBranchQualityFactor = 0.02; // Top 2% 
	public double EMMAttackBranchQualityFactor = 0.02; 
	public double EMMManBranchQualityFactor = 0.02;
	
	
	// MCTS
	
	// Weight of exploration
	//public double c = 1/Math.sqrt(2);
	public double c = 0.4;
	//public double c = 0;

	// Urgency of pool
	public double fpu = 1.1;

	public double MCTSAttackThreshold = 0.5;
	
	public double leadWinRate = 0.95;
	
	public int MCTSRecruitBranchQualityFactor = 20; // Top 5% 
	public int MCTSAttackBranchQualityFactor = 20;
	public int MCTSManBranchQualityFactor = 20;

	//public int MCTSSampleFactor = 50;
	
	// Probability of attacker winning
	static double probMatrix[][] = new double[][] {
			{ 0.417, 0.106, 0.027, 0.007, 0.002, 0.0, 0.0, 0.0, 0.0, 0.0 },
			{ 0.754, 0.363, 0.206, 0.091, 0.049, 0.021, 0.011, 0.005, 0.003,
					0.001 },
			{ 0.916, 0.656, 0.470, 0.315, 0.206, 0.134, 0.084, 0.054, 0.033,
					0.021 },
			{ 0.972, 0.785, 0.642, 0.477, 0.359, 0.253, 0.181, 0.123, 0.086,
					0.057 },
			{ 0.99, 0.89, 0.769, 0.638, 0.506, 0.397, 0.297, 0.224, 0.162,
					0.118 },
			{ 0.997, 0.934, 0.857, 0.745, 0.638, 0.521, 0.423, 0.329, 0.258,
					0.193 },
			{ 0.999, 0.967, 0.91, 0.834, 0.736, 0.64, 0.536, 0.446, 0.357,
					0.287 },
			{ 1, 0.98, 0.947, 0.888, 0.818, 0.73, 0.643, 0.547, 0.464, 0.38 },
			{ 1, 0.99, 0.967, 0.930, 0.873, 0.808, 0.726, 0.646, 0.558, 0.480 },
			{ 1, 0.994, 0.981, 0.954, 0.916, 0.861, 0.8, 0.724, 0.650, 0.568 } };
	
	public static double evalWeights[] = new double[]{11.6225,1.7275,-23.65,-6.49,31.41,3.6625,4.345,31.915,-4.74,19.905,5.695,37.51,1.8775};
	
	
	// Setters
	public void setFPU(double fpu) {
		this.fpu = fpu;
	}
	
	
	
	public void setC(double c) {
		this.c = c;
	}
	
	public static double getProbOfWin(int sourceNrTroops, int destNrTroops) {
		int r = sourceNrTroops - 1;
		if (r > 9) r = 9;
		
		int c = destNrTroops - 1;
		if (c > 9) c = 9;
		
		if (r < 0) {
			r = 0;
		}
		if (c < 0) {
			c = 0;
		}
		
		return probMatrix[r][c];
	}

}

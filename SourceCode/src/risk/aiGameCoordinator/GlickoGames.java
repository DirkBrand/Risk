package risk.aiGameCoordinator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.Random;
import java.util.StringTokenizer;

import risk.aiplayers.BaselineAI;
import risk.aiplayers.SubmissiveAI;
import risk.aiplayers.EMMPlayers.EMMBaselineAI;
import risk.aiplayers.EMMPlayers.EMMGreedyAI;
import risk.aiplayers.EMMPlayers.EMM_Advanced_AI;
import risk.aiplayers.EMMPlayers.EMM_FairExpansion_AI;
import risk.aiplayers.EMMPlayers.EMM_Random_AI;
import risk.aiplayers.MCTSPlayers.MCTSBaselineAI;
import risk.aiplayers.MCTSPlayers.MCTSBaseline_playout_AI;
import risk.aiplayers.MCTSPlayers.MCTSFairBadEval_AI;
import risk.aiplayers.MCTSPlayers.MCTSFairExpansion_AI;
import risk.aiplayers.MCTSPlayers.MCTSFairExpansion_playout_AI;
import risk.aiplayers.MCTSPlayers.MCTSRandomAI;
import risk.aiplayers.MCTSPlayers.MCTS_Advanced_AI;
import risk.aiplayers.MCTSPlayers.MCTS_Advanced_playout_AI;
import Glicko2.Rating;
import Glicko2.RatingCalculator;
import Glicko2.RatingPeriodResults;

public class GlickoGames { 
	private static final boolean FROMPOOL = false;
	boolean firstDone = false;
	boolean secondDone = false;

	String map = "OriginalRiskMapFile.txt";

	String[] allPlayers = new String[] { "SubmissiveAI", "BaselineAI",
			"MCTS_Advanced_AI", "MCTS_Advanced_playout_AI", "MCTSRandomAI",
			"MCTSBaselineAI", "MCTSBaseline_playout_AI",
			"MCTSFairExpansion_AI", "MCTSFairExpansion_playout_AI", "EMMGreedyAI",
			"EMM_Advanced_AI", "EMMBaselineAI", "MCTSFairBadEval_AI",
			"EMM_FairExpansion_AI", "EMM_Random_AI" };

	String[] pick_pool = new String[] { "MCTSBaseline_playout_AI",
			"MCTS_Advanced_playout_AI", "MCTSBaselineAI", "EMMBaselineAI",
			"EMM_FairExpansion_AI", "MCTSFairExpansion_playout_AI" };

	double[] weights = new double[] { 11.6225, 1.7275, -23.65, -6.49, 31.41,
			3.6625, 4.345, 31.915, -4.74, 19.905, 5.695, 37.51, 1.8775 };
	int timeForMCTS_Milliseconds = 2500;
	int playoutsForMCTS = 1000;
	int EMM_depth = 4;

	Random r = new Random();

	ByteArrayOutputStream baos = null;
	PrintStream ps = null;
	PrintStream old = null;

	// Glicko Stuff
	private RatingCalculator ratingSystem = new RatingCalculator(0.0, 0.0);
	private RatingPeriodResults results = new RatingPeriodResults();
	// Players
	private LinkedList<Rating> playerRatingList = new LinkedList<Rating>();

	public static void main(String[] args) {
		new GlickoGames();
	}

	public GlickoGames() {
		initializePlayers();
		// manual();

		old = System.out;

		int count = 0;
		while (true) {
			count++;

			System.setOut(old);
			System.out.println("Started Glicko");
			baos = new ByteArrayOutputStream();
			ps = new PrintStream(baos);
			System.setOut(ps);

			firstDone = false;
			secondDone = false;

			Rating[] bestPair = determineNextPlayers();
			// Rating[] bestPair = randomPairing();

			String name1 = bestPair[0].getUid();
			String name2 = bestPair[1].getUid();
			boolean switchPair = r.nextBoolean();
			if (switchPair) {
				String temp = name1;
				name1 = name2;
				name2 = temp;
			}

			Thread t1 = new Thread(new AIRunThread(name2, null, null, 2));
			t1.start();

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			Thread t2 = new Thread(new AIRunThread(name1, name2, map, 1));
			t2.start();

			while (!firstDone && !secondDone) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			System.err.println("First and Second Done");

			String result = baos.toString();
			if (result.contains(name1)) {
				if (switchPair) {
					results.addResult(bestPair[1], bestPair[0]);
					writeWinLoss(bestPair[1], bestPair[0]);
				} else {
					results.addResult(bestPair[0], bestPair[1]);
					writeWinLoss(bestPair[0], bestPair[1]);
				}
			} else if (result.contains(name2)) {
				if (switchPair) {
					results.addResult(bestPair[0], bestPair[1]);
					writeWinLoss(bestPair[0], bestPair[1]);
				} else {
					results.addResult(bestPair[1], bestPair[0]);
					writeWinLoss(bestPair[1], bestPair[0]);
				}
			} else {
				results.addDraw(bestPair[0], bestPair[1]);
			}

			if (count % 1 == 0) {
				System.setOut(old);
				ratingSystem.updateRatings(results);
				printResults("Results updated");
				System.setOut(ps);
			}

			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			try {
				baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private Rating[] randomPairing() {
		LinkedList<Rating> poolList = new LinkedList<Rating>();
		for (String s : pick_pool) {
			for (Rating r : playerRatingList) {
				if (r.getUid().equalsIgnoreCase(s)) {
					poolList.add(r);
				}
			}
		}

		Rating[] bestPair = new Rating[2];
		Random r = new Random();
		int xInd = r.nextInt(poolList.size());
		int yInd = r.nextInt(poolList.size());
		while (yInd == xInd) {
			yInd = r.nextInt(poolList.size());
		}

		bestPair[0] = poolList.get(xInd);
		bestPair[1] = poolList.get(yInd);

		return bestPair;
	}

	private void writeWinLoss(Rating winner, Rating loser) {
		File file = new File("glickoWinLoss.txt");

		try {
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			BufferedWriter bw = new BufferedWriter(new FileWriter(
					file.getAbsoluteFile(), true));

			bw.write("\nWinner - " + winner.getUid() + " | Loser - "
					+ loser.getUid());
			System.err.println("\nWinner - " + winner.getUid() + " | Loser - "
					+ loser.getUid());
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void printResults(String text) {
		File file = new File("glickoRatings.txt");

		try {
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			BufferedWriter bw = new BufferedWriter(new FileWriter(
					file.getAbsoluteFile()));

			System.out.println("\n" + text + "\n");
			for (Rating r : playerRatingList) {
				bw.write(r.toString() + "\n");
				System.out.println(r);
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadRatings() {
		try {
			BufferedReader inStream = new BufferedReader(new FileReader(
					"glickoRatings.txt"));
			StringTokenizer st = null;
			String line = inStream.readLine();

			while (line != null) {
				st = new StringTokenizer(line, " / ");
				String uID = st.nextToken();
				String rate = st.nextToken();
				String RD = st.nextToken();
				st.nextToken();
				int num = Integer.parseInt(st.nextToken());
				for (Rating r : playerRatingList) {
					if (r.getUid().equalsIgnoreCase(uID)) {
						r.setRating(Double.parseDouble(rate));
						r.setRatingDeviation(Double.parseDouble(RD));
						r.setNumberOfResults(num);
						break;
					}
				}
				line = inStream.readLine();
			}
			inStream.close();
		} catch (IOException io) {
			io.printStackTrace();
		}
	}

	private void initializePlayers() {
		if (FROMPOOL) {
			for (String player : pick_pool) {
				playerRatingList.add(new Rating(player, ratingSystem));
			}
		} else {
			for (String player : allPlayers) {
				playerRatingList.add(new Rating(player, ratingSystem));
			}
		}

		loadRatings();
	}

	// Returns pair of players that should play next
	private Rating[] determineNextPlayers() {
		double minDiff = Double.POSITIVE_INFINITY;
		LinkedList<Rating> poolList = new LinkedList<Rating>();
		for (String s : pick_pool) {
			for (Rating r : playerRatingList) {
				if (r.getUid().equalsIgnoreCase(s)) {
					poolList.add(r);
				}
			}
		}

		Rating[] bestPair = new Rating[2];

		for (int i = 0; i < poolList.size() - 1; i++) {
			for (int j = i + 1; j < poolList.size(); j++) {
				Rating X = poolList.get(i);
				Rating Y = poolList.get(j);

				double diff = Math.abs(X.getRating() - Y.getRating())
						/ Math.sqrt(Math.pow(X.getRatingDeviation(), 2)
								+ Math.pow(Y.getRatingDeviation(), 2));

				if (diff < minDiff) {
					minDiff = diff;
					bestPair[0] = X;
					bestPair[1] = Y;
				}

			}
		}

		return bestPair;
	}

	public void manual() {
		results.addResult(playerRatingList.get(3), playerRatingList.get(6));
		ratingSystem.updateRatings(results);
		printResults("Results updated");
		System.exit(0);
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
			this.id = playerID;
		}

		@Override
		public void run() {
			// Start AI
			switch (ai1) {
			case "BaselineAI": {
				new BaselineAI(ai1, ai2, theMap, id);
				break;
			}
			case "SubmissiveAI": {
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
			case "EMM_FairExpansion_AI": {
				new EMM_FairExpansion_AI(ai1, ai2, theMap, id, EMM_depth);
				break;
			}
			case "EMM_Random_AI": {
				new EMM_Random_AI(ai1, ai2, theMap, id, EMM_depth);
				break;
			}
			case "MCTS_Advanced_AI": {
				new MCTS_Advanced_AI(ai1, ai2, theMap, id,
						timeForMCTS_Milliseconds);
				break;
			}
			case "MCTS_Advanced_playout_AI": {
				new MCTS_Advanced_playout_AI(ai1, ai2, theMap, id,
						playoutsForMCTS);
				break;
			}
			case "MCTSFairExpansion_AI": {
				new MCTSFairExpansion_AI(ai1, ai2, theMap, id,
						timeForMCTS_Milliseconds);
				break;
			}
			case "MCTSFairExpansion_playout_AI": {
				new MCTSFairExpansion_playout_AI(ai1, ai2, theMap, id,
						playoutsForMCTS);
				break;
			}
			case "MCTSFairBadEval_AI": {
				new MCTSFairBadEval_AI(ai1, ai2, theMap, id,
						timeForMCTS_Milliseconds);
				break;
			}
			case "MCTSRandomAI": {
				new MCTSRandomAI(ai1, ai2, theMap, id, timeForMCTS_Milliseconds);
				break;
			}
			case "MCTSBaselineAI": {
				new MCTSBaselineAI(ai1, ai2, theMap, id,
						timeForMCTS_Milliseconds);
				break;
			}
			case "MCTSBaseline_playout_AI": {
				new MCTSBaseline_playout_AI(ai1, ai2, theMap, id,
						playoutsForMCTS);
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

			Random r = new Random();
			try {
				Thread.sleep(r.nextInt(1000) + 500);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (id == 1) {
				firstDone = true;
			} else {
				secondDone = true;
			}

		}

	}
}

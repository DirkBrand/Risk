package risk.aiplayers.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

import risk.commonObjects.Territory;

public class AIFeatures {

	// Armies feature : Measures the current player's relative army strength
	public static double armyStrength(GameTreeNode node) {
		double totalAP = 0.0;
		double total = 0.0;
		Iterator<Territory> it = node.getGame().getCurrentPlayer()
				.getTerritories().values().iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			totalAP += t.getNrTroops();
		}
		total += totalAP;
		it = node.getGame().getOtherPlayer().getTerritories().values().iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			total += t.getNrTroops();
		}

		return totalAP / total;
	}

	/********************************************************/

	// Best enemy feature : Measures relative strength of enemy
	public static double enemyStrength(GameTreeNode node) {
		double totalEP = 0.0;
		double totalArmies = 0.0;
		Iterator<Territory> it = node.getGame().getOtherPlayer()
				.getTerritories().values().iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			totalEP += t.getNrTroops();
		}
		totalArmies += totalEP;
		it = node.getGame().getCurrentPlayer().getTerritories().values().iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			totalArmies += t.getNrTroops();
		}

		double territoryCountEnemy = node.getGame().getOtherPlayer()
				.getTerritories().size();
		double totalTerritoryCount = node.getGame().getCurrentPlayer()
				.getTerritories().size()
				+ territoryCountEnemy;

		return -((totalEP / totalArmies + territoryCountEnemy
				/ totalTerritoryCount) / 2);
	}

	/********************************************************/
	// More than one troop feature :
	// Percentage of territories with more than one troop
	public static double fortifiedTerritories(GameTreeNode node) {

		double totalFT = 0.0;
		double totalTer = node.getGame().getCurrentPlayer().getTerritories()
				.size();
		Iterator<Territory> it = node.getGame().getCurrentPlayer()
				.getTerritories().values().iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			if (t.getNrTroops() > 1)
				totalFT += 1;
		}

		return totalFT / totalTer;
	}

	/********************************************************/

	// Hinterland feature :
	// Percentage of territories with no neighboring enemy territories
	public static double hinterlandStrength(GameTreeNode node) {
		double totalHT = 0.0;
		double totalTer = node.getGame().getCurrentPlayer().getTerritories()
				.size();
		Iterator<Territory> it = node.getGame().getCurrentPlayer()
				.getTerritories().values().iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			if (AIUtil.isHinterland(node, t))
				totalHT += 1;
		}

		return totalHT / totalTer;
	}

	/********************************************************/

	// Distance to frontier feature :
	// Measures army distribution throughout player's territories
	public static double distanceToFrontier(GameTreeNode node) {
		double totalArmies = node.getGame().getCurrentPlayer().getTerritories()
				.size();

		int sum = 0;
		Iterator<Territory> it = node.getGame().getCurrentPlayer()
				.getTerritories().values().iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			int d = AIUtil.distance(node, t);
			sum += t.getNrTroops() * d;
		}

		return totalArmies / (double) sum;

	}

	/********************************************************/
	// Continent Safety Feature :
	// Measure of enemy threat to continents completely controlled by current
	// player
	public static double continentSafetyFeature(GameTreeNode node) {

		// Determine which continents are conquered
		// Initiate buckets
		LinkedList<LinkedList<Territory>> contBuckets = new LinkedList<LinkedList<Territory>>();
		for (int i = 0; i < node.getGame().getAllContinents().length; i++) {
			contBuckets.add(new LinkedList<Territory>());
		}

		// Populate the buckets of territories
		Iterator<Territory> it = node.getGame().getCurrentPlayer()
				.getTerritories().values().iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			for (int i = 0; i < node.getGame().getAllContinents().length; i++) {
				String contTemp = t.getContinent();
				if (contTemp
						.equalsIgnoreCase(node.getGame().getAllContinents()[i]
								.getName())) {
					contBuckets.get(i).add(t);
					break;
				}
			}
		}

		int index = 0;
		double sum = 0;
		for (LinkedList<Territory> bucket : contBuckets) {
			// Completely own continent
			if (bucket.size() == node.getGame().getAllContinents()[index]
					.getNumberOfTerritories()) {
				double threatSquaredSum = 0;
				double maxThreat = Double.NEGATIVE_INFINITY;
				for (Territory t : bucket) {
					double threat = Math.pow(AIUtil.threat(node, t, node
							.getGame().getOtherPlayerID()), 2);
					threatSquaredSum += threat;

					if (threat > maxThreat)
						maxThreat = threat;
				}
				double cr = AIUtil.continentRating(node, index);
				double threatSum = (threatSquaredSum + maxThreat);

				sum += threatSum * cr;
				index++;
			}
		}

		return -sum;
	}

	/********************************************************/
	// Continent Safety Feature :
	// Measure of threat of current player to continents completely controlled
	// by the enemy player
	public static double continentThreatFeature(GameTreeNode node) {

		// Determine which continents are conquered
		// Initiate buckets
		LinkedList<LinkedList<Territory>> contBuckets = new LinkedList<LinkedList<Territory>>();
		for (int i = 0; i < node.getGame().getAllContinents().length; i++) {
			contBuckets.add(new LinkedList<Territory>());
		}

		// Populate the buckets of territories
		Iterator<Territory> it = node.getGame().getOtherPlayer()
				.getTerritories().values().iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			for (int i = 0; i < node.getGame().getAllContinents().length; i++) {
				String contTemp = t.getContinent();
				if (contTemp
						.equalsIgnoreCase(node.getGame().getAllContinents()[i]
								.getName())) {
					contBuckets.get(i).add(t);
					break;
				}
			}
		}

		int index = 0;
		double sum = 0;
		for (LinkedList<Territory> bucket : contBuckets) {
			// Completely own continent
			if (bucket.size() == node.getGame().getAllContinents()[index]
					.getNumberOfTerritories()) {
				double threatSquaredSum = 0;
				for (Territory t : bucket) {
					double threat = Math.pow(AIUtil.threat(node, t, node
							.getGame().getCurrentPlayerID()), 2);
					threatSquaredSum += threat;
				}
				sum += (threatSquaredSum) * AIUtil.continentRating(node, index);
				index++;
			}
		}

		return sum;
	}

	/********************************************************/
	// Enemy Recruitment
	// Estimation of number of armies enemy will record in their next turn
	public static double enemyRecruitFeature(GameTreeNode node) {
		int n = 0;

		// Territory Bonus
		n += (int) (node.getGame().getOtherPlayer().getTerritories().size() / 3);

		// Continent Bonus
		int[] contBuckets = new int[node.getGame().getAllContinents().length];
		Arrays.fill(contBuckets, 0);

		Iterator<Territory> it = node.getGame().getOtherPlayer()
				.getTerritories().values().iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			for (int i = 0; i < node.getGame().getAllContinents().length; i++) {
				String contTemp = t.getContinent();
				if (contTemp
						.equalsIgnoreCase(node.getGame().getAllContinents()[i]
								.getName())) {
					contBuckets[i]++;
				}
			}
		}

		for (int i = 0; i < contBuckets.length; i++) {
			if (contBuckets[i] == node.getGame().getAllContinents()[i]
					.getNumberOfTerritories()) {
				n += node.getGame().getAllContinents()[i].getBonus();
			}
		}

		if (n < 1 || n >= 1000000)
			n = 1;

		return (double) (-n);
	}

	/********************************************************/
	// Enemy Occupied Continents
	// Number of continents completely controlled by enemy
	public static double enemyOccupiedContinentsFeature(GameTreeNode node) {
		int count = 0;
		// Determine which continents are conquered
		// Initiate buckets
		int[] contBuckets = new int[node.getGame().getAllContinents().length];
		Arrays.fill(contBuckets, 0);

		Iterator<Territory> it = node.getGame().getOtherPlayer()
				.getTerritories().values().iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			for (int i = 0; i < node.getGame().getAllContinents().length; i++) {
				String contTemp = t.getContinent();
				if (contTemp
						.equalsIgnoreCase(node.getGame().getAllContinents()[i]
								.getName())) {
					contBuckets[i]++;
				}
			}
		}

		for (int i = 0; i < contBuckets.length; i++) {
			if (contBuckets[i] == node.getGame().getAllContinents()[i]
					.getNumberOfTerritories()) {
				count++;
			}
		}
		
		return -(double)count;
	}
	
	/********************************************************/
	// Maximum Threat Feature
	// Measurement of the probability that player will conquer at least one territory
	public static double maximumThreatFeature(GameTreeNode node) {
		double maxProb = Double.NEGATIVE_INFINITY;
		AIParameter params = new AIParameter();
		
		Iterator<Territory> it = node.getGame()
				.getCurrentPlayer().getTerritories().values().iterator();
		while(it.hasNext()) {
			Territory t = it.next();
			if (t.getNrTroops() > 1) {
				for (Territory n : t.getNeighbours()) {
					Territory tempT = node.getGame().getOtherPlayer().getTerritoryByName(n.getName());
					if (tempT != null) {
						double prob = params.getProbOfWin(t.getNrTroops(), n.getNrTroops());
						if (prob > maxProb) {
							maxProb = prob;
						}
					}
				}
			}
		}
		
		return maxProb;
	}

	/********************************************************/
	// Occupied Territories Feature
	// Number of territories that the player owns in relation to total number of territories
	public static double occupiedTerritoryFeature(GameTreeNode node) {
		double total = 0.0;
		double totalPlayer = 0.0;
		Iterator<Territory> it = node.getGame().getCurrentPlayer()
				.getTerritories().values().iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			totalPlayer += t.getNrTroops();
		}
		total += totalPlayer;
		it = node.getGame().getOtherPlayer().getTerritories().values().iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			total += t.getNrTroops();
		}
		
		return totalPlayer/total;
		
	}

	/********************************************************/
	// Own estimated recruitment feature
	// Expected number of territories player will recruit next turn
	public static double ownRecruitFeature(GameTreeNode node) {
		return AIUtil.calculateRecruitedTroops(node);
	}
	
	/********************************************************/
	// Own Occupied Continents Feature
	// Numbre of continents completely controlled by the player
	public static double ownOccupiedContinentsFeature(GameTreeNode node) {
		int count = 0;
		// Determine which continents are conquered
		// Initiate buckets
		int[] contBuckets = new int[node.getGame().getAllContinents().length];
		Arrays.fill(contBuckets, 0);

		Iterator<Territory> it = node.getGame().getCurrentPlayer()
				.getTerritories().values().iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			for (int i = 0; i < node.getGame().getAllContinents().length; i++) {
				String contTemp = t.getContinent();
				if (contTemp
						.equalsIgnoreCase(node.getGame().getAllContinents()[i]
								.getName())) {
					contBuckets[i]++;
				}
			}
		}

		for (int i = 0; i < contBuckets.length; i++) {
			if (contBuckets[i] == node.getGame().getAllContinents()[i]
					.getNumberOfTerritories()) {
				count++;
			}
		}
		
		return (double)count;
	}
	
}

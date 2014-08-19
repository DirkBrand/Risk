package risk.aiplayers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import risk.aiplayers.util.AIParameter;
import risk.aiplayers.util.AIUtil;
import risk.aiplayers.util.GameTreeNode;
import risk.aiplayers.util.MCTSNode;
import risk.commonObjects.GameState;
import risk.commonObjects.Player;
import risk.commonObjects.Territory;

public abstract class MonteCarloTreeSearchPlayer extends AIPlayer {

	String line = "";
	int ind = 0;

	int simCount = 0;

	protected long startTime;
	protected long allottedTime;

	protected long timeForMCTSSearch;

	// For tree stats
	protected int treeDepth = 0;
	protected int maxTreeDepth = 0;
	protected int treeNodeCount = 0;

	// For hashmap stats
	protected int foundIt = 0;
	protected int missedIt = 0;

	protected HashMap<Long, Double> NodeValues = new HashMap<Long, Double>();

	protected AIParameter params;

	public MonteCarloTreeSearchPlayer(String name, String opp, String map,
			int id, long time,AIParameter params) {
		super(MCTS_AI, name, opp, map, id);
		this.timeForMCTSSearch = time;

		this.params = params;

		// Communication
		boolean goingToController = false;

		do {
			try {
				goingToController = false;
				while ((line = APM.input.readLine()) != null) {
					ind = 0;

					int messageID = 0;
					String request = "";
					LinkedList<String> args = new LinkedList<String>();

					char temp = readChar();
					if (temp == '.') {
						temp = readChar();
						String controllerPortStr = "";
						String controllerAddStr = "";

						while (temp != '.') {
							controllerPortStr += temp;
							temp = readChar();
						}
						temp = readChar();
						while (temp != '.') {
							controllerAddStr += temp;
							temp = readChar();
						}
						APM.send("=");
						goingToController = true;
						connectToController(
								controllerAddStr.replaceAll(",", "\\."),
								Integer.parseInt(controllerPortStr));
						break;
					}

					temp = readChar();
					String idStr = "";
					while (temp != ']') {
						idStr += temp;
						temp = readChar();
					}
					messageID = Integer.parseInt(idStr);

					temp = readChar();
					while (temp != '[') {
						request += temp;
						temp = readChar();
					}

					// READING ARGUMENTS
					while (temp != ']') {
						String argStr = "";
						temp = readChar();
						if (temp == ']') {
							break;
						}
						while (temp != ',' && temp != ']') {
							argStr += temp;
							temp = readChar();
						}
						args.add(argStr);
					}
					if (request.equals("result")) {
						stillRunning = false;
					}

					APM.process(messageID, request, args);
				}
				if (!goingToController || !stillRunning) {
					APM.controllerSocket.close();
					break;
				}
			} catch (SocketException e) {
				System.err.println("Connection to server broken");
				goingToController = false;
			} catch (Exception e) {
				e.printStackTrace();
				goingToController = false;
			}
		} while (goingToController && stillRunning);
	}

	public char readChar() {
		char temp = ' ';
		while (Character.isWhitespace(temp)) {
			temp = line.charAt(ind++);
		}
		return temp;
	}

	// Main MCTS method
	/**
	 * The main MCTS algorithm. Consists of selection, expansion, simulation and
	 * backpropagation.
	 * 
	 * @param rootNode
	 *            The root of the tree.
	 * @return The best child action (most visited child).
	 */
	protected MCTSNode MCTSSearch(MCTSNode rootNode) {

		startTimer(timeForMCTSSearch);
		while (System.nanoTime() - startTime <= allottedTime) {
			MCTSNode currentNode = rootNode;
			MCTSNode leafNode = TreePolicy(currentNode);

			int R = Simulate(leafNode);

			BackPropagate(leafNode, R);
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

	// SELECTION
	/**
	 * The selection phase tree policy.
	 * 
	 * @param currentNode
	 * @return The node that has been expanded.
	 */
	protected MCTSNode TreePolicy(MCTSNode currentNode) {
		while (!AIUtil.isTerminalNode(currentNode)) {
			double urgency = AIUtil.getUrgencyScore(currentNode, params);
			// System.out.println(urgency);
			if (currentNode.getChildren().size() == 0) {
				if (currentNode.getTreePhase() == GameTreeNode.RANDOMEVENT) {
					for (int i = 0; i < currentNode.maxChildren; i++) {
						treeNodeCount++;
						// System.out.println("In Random expand");
						Expand(currentNode);
						// System.out.println("Out Random expand");
					}
					// System.out.println("In Random bestChild");
					return bestChild(currentNode);
				} else {
					treeNodeCount++;
					// System.out.println("In Expand 1");
					return Expand(currentNode);
				}
			}
			// If not fully expanded and (wr + ucb) < fpu -> Expand
			else if (currentNode.getChildren().size() < currentNode.maxChildren
					&& urgency < params.fpu) {
				treeNodeCount++;
				// System.out.println("In Expand 2 " + currentNode.getHash() + " maxC C " + currentNode.maxChildren + " " + currentNode.getChildren().size());
				return Expand(currentNode);
			} else {
				// System.out.println("In BestChild");
				currentNode = bestChild(currentNode);
				// System.out.println("Out BestChild");
			}
		}
		return currentNode;
	}

	/**
	 * Returns the child node of the currentNode that has the highest UCB value.
	 * 
	 * @param currentNode
	 * @return
	 */
	protected MCTSNode bestChild(MCTSNode currentNode) {
		MCTSNode selected = null;
		double max = Double.NEGATIVE_INFINITY;

		if (currentNode.getTreePhase() == GameTreeNode.RANDOMEVENT) {
			Random r = new Random();
			if (currentNode.getChildren().size() == 1) {
				return currentNode.getChildren().get(0);
			} else if (currentNode.getChildren().size() == 2) {
				MCTSNode child1 = currentNode.getChildren().get(0);
				MCTSNode child2 = currentNode.getChildren().get(1);

				if (r.nextDouble() < Math.pow(AIUtil.getProb(child1), 2)) {
					return child1;
				} else {
					return child2;
				}

			} else {
				MCTSNode child1 = currentNode.getChildren().get(0);
				double child1Prob = Math.pow(AIUtil.getProb(child1), 2);
				MCTSNode child2 = currentNode.getChildren().get(1);
				double child2Prob = Math.pow(AIUtil.getProb(child2), 2);
				MCTSNode child3 = currentNode.getChildren().get(2);

				double prob = r.nextDouble();
				if (prob < child1Prob) {
					return child1;
				} else {
					if (prob < (child1Prob + child2Prob)) {
						return child2;
					} else {
						return child3;
					}
				}
			}

		} else {
			for (MCTSNode child : currentNode.getChildren()) {
				double value = 0;

				value = AIUtil.ucb(child, params);

				if (value > max) {
					selected = child;
					max = value;
				}
			}
		}
		return selected;
	}

	// SIMULATION
	/**
	 * Performs a simulation (or playout) from the provided node to a terminal
	 * condition (player wins).
	 * 
	 * @param lastNode
	 * @return Returns the value of the playout (1 for win, 0 for loss)
	 */
	protected int Simulate(MCTSNode lastNode) {
		simCount++;
		MCTSNode playNode = lastNode.clone();
		while (!AIUtil.isTerminalNode(playNode)) {
			// System.out.println(playNode.getTreePhaseText());
			switch (playNode.getTreePhase()) {
			case GameTreeNode.RECRUIT: {
				// Baseline AI recruit scheme

				int number = AIUtil.calculateRecruitedTroops(playNode);

				Random rand = new Random();
				boolean found = false;
				int id = -1;
				while (!found) {
					int i = rand.nextInt(playNode.getGame().getCurrentPlayer()
							.getTerritories().size());
					Territory t = null;
					Iterator<Territory> it = playNode.getGame()
							.getCurrentPlayer().getTerritories().values()
							.iterator();
					for (int j = 0; j <= i; j++) {
						t = it.next();
					}
					if (!AIUtil.isHinterland(playNode, t)) {
						found = true;
						id = t.getId();
					}
				}

				playNode.setRecruitedTer(playNode.getGame().getCurrentPlayer()
						.getTerritoryByID(id));
				playNode.getRecruitedTer().setNrTroops(
						playNode.getRecruitedTer().getNrTroops() + number);

				playNode.setAttackSource(playNode.getRecruitedTer().getName());

				playNode.setTreePhase(GameTreeNode.ATTACK);
				playNode.setMoveReq(false);

				break;
			}
			case GameTreeNode.ATTACK: {

				Territory attackSource = playNode.getGame().getCurrentPlayer()
						.getTerritoryByName(playNode.getAttackSource());

				// Determines whether should attack again
				if (attackSource.getNrTroops() == 1) {
					playNode.setTreePhase(GameTreeNode.MANOEUVRE);
					break;
				}

				Territory attackDest = null;

				for (Territory n : attackSource.getNeighbours()) {
					Territory tempT = playNode.getGame().getOtherPlayer()
							.getTerritoryByID(n.getId());

					// Attack neighbour that gives highest win possibility
					if (tempT != null
							&& AIParameter.getProbOfWin(
									attackSource.getNrTroops(),
									tempT.getNrTroops()) > params.MCTSAttackThreshold) {
						attackDest = tempT;
						break;
					}
				}

				if (attackDest == null) {
					playNode.setTreePhase(GameTreeNode.MANOEUVRE);
					break;
				}

				playNode.setAttackSource(attackSource.getName());
				playNode.setAttackDest(attackDest.getName());

				playNode.setTreePhase(GameTreeNode.RANDOMEVENT);

				break;
			}
			case GameTreeNode.RANDOMEVENT: {
				int sourceTroops = playNode.getGame().getCurrentPlayer()
						.getTerritoryByName(playNode.getAttackSource())
						.getNrTroops();
				int destTroops = playNode.getGame().getOtherPlayer()
						.getTerritoryByName(playNode.getAttackDest())
						.getNrTroops();

				int attackD[] = new int[] { 0, 0, 0 };
				int defendD[] = new int[] { 0, 0 };

				// Attacker
				if (sourceTroops == 2) {
					attackD[0] = AIUtil.genRoll();
				} else if (sourceTroops == 3) {
					attackD[0] = AIUtil.genRoll();
					attackD[1] = AIUtil.genRoll();
				} else if (sourceTroops > 3) {
					attackD[0] = AIUtil.genRoll();
					attackD[1] = AIUtil.genRoll();
					attackD[2] = AIUtil.genRoll();
				}

				if (destTroops == 2 || destTroops == 1) {
					defendD[0] = AIUtil.genRoll();
				} else if (destTroops > 2) {
					defendD[0] = AIUtil.genRoll();
					defendD[1] = AIUtil.genRoll();
				}

				Arrays.sort(attackD);
				Arrays.sort(defendD);

				playNode.setDiceRolls(attackD[0], attackD[1], attackD[2],
						defendD[0], defendD[1]);

				AIUtil.resolveAttackAction(playNode);
				if (playNode.moveRequired()) {
					playNode.setTreePhase(GameTreeNode.MOVEAFTERATTACK);
				} else {
					playNode.setTreePhase(GameTreeNode.ATTACK);
				}

				break;
			}
			case GameTreeNode.MOVEAFTERATTACK: {
				int totalTroops = playNode.getGame().getCurrentPlayer()
						.getTerritoryByName(playNode.getAttackSource())
						.getNrTroops();
				int troops = rand.nextInt(totalTroops - 1) + 1;

				AIUtil.resolveMoveAction(playNode.getGame().getCurrentPlayer()
						.getTerritoryByName(playNode.getAttackSource()),
						playNode.getGame().getCurrentPlayer()
						.getTerritoryByName(playNode.getAttackDest()),
						troops);

				playNode.setMoveReq(false);

				playNode.setTreePhase(GameTreeNode.ATTACK);

				break;
			}
			case GameTreeNode.MANOEUVRE: {
				AIUtil.updateRegions(playNode.getGame());
				int minID = -1;
				int min = Integer.MAX_VALUE;
				int maxID = -1;
				int max = Integer.MIN_VALUE;

				Iterator<Territory> it = playNode.getGame().getCurrentPlayer()
						.getTerritories().values().iterator();
				while (it.hasNext()) {
					Territory t = it.next();
					if (t.getNrTroops() > max) {
						max = t.getNrTroops();
						maxID = t.getId();
					}
				}
				Territory source = playNode.getGame().getCurrentPlayer()
						.getTerritoryByID(maxID);

				it = playNode.getGame().getCurrentPlayer().getTerritories()
						.values().iterator();
				while (it.hasNext()) {
					Territory t = it.next();
					if (t.getNrTroops() < min
							&& t.connectedRegion == source.connectedRegion) {
						min = t.getNrTroops();
						minID = t.getId();
					}
				}

				if (minID == -1) {
					playNode.setTreePhase(GameTreeNode.RECRUIT);
					playNode.switchMaxPlayer();
					playNode.getGame().changeCurrentPlayer();
					break;
				}
				Territory dest = playNode.getGame().getCurrentPlayer()
						.getTerritoryByID(minID);

				if (source.getId() != dest.getId()) {
					int total = source.getNrTroops() + dest.getNrTroops();
					source.setNrTroops((int) (total / 2.0));
					dest.setNrTroops(total - (int) (total / 2.0));
				}

				playNode.setTreePhase(GameTreeNode.RECRUIT);
				playNode.switchMaxPlayer();
				playNode.getGame().changeCurrentPlayer();

				break;
			}
			}
		}

		if (playNode.getGame().getOtherPlayer().getTerritories().size() == 0) {
			if (playNode.isMaxPlayer())
				return 1; // A win for the max player
			else
				return 0; // A loss for the max player
		} else if (playNode.getGame().getCurrentPlayer().getTerritories()
				.size() == 0) {
			if (playNode.isMaxPlayer())
				return 0; // A loss for the max player
			else
				return 1; // A win for the max player
		} else {
			return 0;
		}

	}

	// BACKPROPAGATION
	/**
	 * Propagates the value of a simulation from the lastNode to the root node
	 * in a negamax scheme.
	 * 
	 * @param lastNode
	 * @param r
	 *            The value of the simulation.
	 */
	protected void BackPropagate(MCTSNode lastNode, int r) {
		while (lastNode.getParent() != null) {
			if (lastNode.getParent().isMaxPlayer())
				lastNode.setWinCount(lastNode.getWinCount() + r);
			else
				lastNode.setWinCount(lastNode.getWinCount() + ((r + 1) % 2));

			lastNode.setVisitCount(lastNode.getVisitCount() + 1);

			lastNode = lastNode.getParent();
		}
		lastNode.setWinCount(lastNode.getWinCount() + r);
		lastNode.setVisitCount(lastNode.getVisitCount() + 1);
	}

	// MCTS methods that must be implemented
	protected abstract void calculateMaxChildren(MCTSNode lastNode);

	protected abstract MCTSNode Expand(MCTSNode lastNode);

	/**
	 * Starts the timer for the MCTS algorithm
	 * 
	 * @param millisecsAllowed
	 *            The time allowed per turn, in milliseconds
	 */
	private void startTimer(long millisecsAllowed) {
		startTime = System.nanoTime();
		this.allottedTime = millisecsAllowed * 1000000;
	}

	protected void printStats(MCTSNode root, Double time) {

		System.out.println("Ended MCTS in " + time + " ms");

		System.out.println("Depth : " + maxTreeDepth);
		System.out.println("Node Count : " + treeNodeCount);
		System.out.println("Root playouts : " + root.getVisitCount());
		System.out.println("Simulation Count : " + root.getVisitCount());
		simCount = 0;
		System.out.println("HashMap ratio - " + (double) foundIt / (double)
				(foundIt + missedIt) * 100 + " %");

		System.out.println("HashMap size - " + NodeValues.size());

		System.out.println("Playouts / second - " + Math.round((double)
				root.getVisitCount() / (double) (time / 1000.0)));
		System.out.println();

	}

	/**
	 * Determines whether the player wants to attack again.
	 */
	@Override
	public boolean attackAgain() {
		Iterator<Territory> it = game.getCurrentPlayer().getTerritories()
				.values().iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			if (t.getNrTroops() > 1) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determines the value of the given node by either retrieving the value
	 * from the hashmap, or calculating a new value with the evaluation
	 * function.
	 * 
	 * @param node
	 *            The MCTSnode for which the value is calculated.
	 * @return Returns the value of the node.
	 */
	protected double getValue(MCTSNode node) {

		if (NodeValues.size() >= 125000) { //TODO: was 500 000 changed to 250 000 for my PC then 125 000 for when 2 MCTS play.
			NodeValues = new HashMap<Long, Double>();
		}

		long key = node.getHash();
		Double value = NodeValues.get(key);
		if (value != null) {
			foundIt++;
			return value;
		} else {
			missedIt++;
			value = AIUtil.eval(node, AIParameter.evalWeights, maxRecruitable);
			NodeValues.put(key, value);
			node.setValue(value);
			return value;
		}
	}

	/**
	 * Returns the weighted value of a node, according to the associated
	 * probabilities of its children.
	 * 
	 * @param childNode
	 *            The node for which the value is calculated.
	 * @return The weighted value of the node.
	 */
	protected double getWeightedEval(MCTSNode childNode) {
		int sourceTroops = childNode.getGame().getCurrentPlayer()
				.getTerritoryByName(childNode.getAttackSource()).getNrTroops();
		int destTroops = childNode.getGame().getOtherPlayer()
				.getTerritoryByName(childNode.getAttackDest()).getNrTroops();

		double value = 0.0;

		// Attacker
		if (sourceTroops == 2) {
			if (destTroops == 2 || destTroops == 1) {
				MCTSNode newChild = childNode.clone();
				newChild.setDiceRolls(0, 0, 1, 0, 6);
				AIUtil.resolveAttackAction(newChild);

				value += AIUtil.getProb(newChild) * getValue(newChild);

				MCTSNode newChild2 = childNode.clone();
				newChild2.setDiceRolls(0, 0, 6, 0, 1);
				AIUtil.resolveAttackAction(newChild2);

				value += AIUtil.getProb(newChild2) * getValue(newChild2);

			} else {
				MCTSNode newChild = childNode.clone();
				newChild.setDiceRolls(0, 0, 1, 5, 6);
				AIUtil.resolveAttackAction(newChild);

				value += AIUtil.getProb(newChild) * getValue(newChild);

				MCTSNode newChild2 = childNode.clone();
				newChild2.setDiceRolls(0, 0, 6, 1, 2);
				AIUtil.resolveAttackAction(newChild2);

				value += AIUtil.getProb(newChild2) * getValue(newChild2);
			}
		} else if (sourceTroops == 3) {
			if (destTroops == 2 || destTroops == 1) {
				MCTSNode newChild = childNode.clone();
				newChild.setDiceRolls(0, 5, 6, 0, 1);
				AIUtil.resolveAttackAction(newChild);

				value += AIUtil.getProb(newChild) * getValue(newChild);

				MCTSNode newChild2 = childNode.clone();
				newChild2.setDiceRolls(0, 1, 2, 0, 6);
				AIUtil.resolveAttackAction(newChild2);

				value += AIUtil.getProb(newChild2) * getValue(newChild2);
			} else {
				MCTSNode newChild = childNode.clone();
				newChild.setDiceRolls(0, 1, 2, 5, 6);
				AIUtil.resolveAttackAction(newChild);

				value += AIUtil.getProb(newChild) * getValue(newChild);

				MCTSNode newChild2 = childNode.clone();
				newChild2.setDiceRolls(0, 1, 6, 2, 5);
				AIUtil.resolveAttackAction(newChild2);

				value += AIUtil.getProb(newChild2) * getValue(newChild2);

				MCTSNode newChild3 = childNode.clone();
				newChild3.setDiceRolls(0, 5, 6, 1, 2);
				AIUtil.resolveAttackAction(newChild3);

				value += AIUtil.getProb(newChild3) * getValue(newChild3);
			}

		} else {
			if (destTroops == 2 || destTroops == 1) {
				MCTSNode newChild = childNode.clone();
				newChild.setDiceRolls(4, 5, 6, 0, 1);
				AIUtil.resolveAttackAction(newChild);

				value += AIUtil.getProb(newChild) * getValue(newChild);

				MCTSNode newChild2 = childNode.clone();
				newChild2.setDiceRolls(1, 2, 3, 0, 6);
				AIUtil.resolveAttackAction(newChild2);

				value += AIUtil.getProb(newChild2) * getValue(newChild2);

			} else {
				MCTSNode newChild = childNode.clone();
				newChild.setDiceRolls(4, 5, 6, 1, 2);
				AIUtil.resolveAttackAction(newChild);

				value += AIUtil.getProb(newChild) * getValue(newChild);

				MCTSNode newChild2 = childNode.clone();
				newChild2.setDiceRolls(1, 2, 6, 3, 5);
				AIUtil.resolveAttackAction(newChild2);

				value += AIUtil.getProb(newChild2) * getValue(newChild2);

				MCTSNode newChild3 = childNode.clone();
				newChild3.setDiceRolls(1, 2, 3, 5, 6);
				AIUtil.resolveAttackAction(newChild3);

				value += AIUtil.getProb(newChild3) * getValue(newChild3);
			}
		}

		return value;

	}

	/**
	 * To print out a log of the current game state if necessary. For testing
	 * mainly.
	 * 
	 * @param node
	 *            . The MCTSnode that contains all the game state information.
	 */
	protected void writeGameState(MCTSNode node) {

		File log = new File("gameLog" + getName() + ".txt");
		try {
			FileWriter fileWriter = new FileWriter(log, true);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

			bufferedWriter.write("\n\n<<Node " + node.getTreePhaseText()
					+ " Expanded>>\n");

			if (node.getTreePhase() == GameTreeNode.ATTACK) {
				bufferedWriter.write("\nAttacking from: "
						+ node.getAttackSource() + " to "
						+ node.getAttackDest() + " with "
						+ Arrays.toString(node.getDiceRolls()) + "\n\n");
			}
			bufferedWriter.write("\n"
					+ node.getGame().getCurrentPlayer().getName()
					+ " Territories\n\n");
			Iterator<Territory> it = node.getGame().getCurrentPlayer()
					.getTerritories().values().iterator();
			while (it.hasNext()) {
				Territory t = it.next();
				bufferedWriter.write(t.getName() + " - " + t.getNrTroops()
						+ "\n");
			}

			bufferedWriter.write("\n"
					+ node.getGame().getOtherPlayer().getName()
					+ " Territories\n\n");
			it = node.getGame().getOtherPlayer().getTerritories().values()
					.iterator();
			while (it.hasNext()) {
				Territory t = it.next();
				bufferedWriter.write(t.getName() + " - " + t.getNrTroops()
						+ "\n");
			}

			bufferedWriter.close();

		} catch (IOException e) {
			System.out.println("COULD NOT LOG RESULTS!!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

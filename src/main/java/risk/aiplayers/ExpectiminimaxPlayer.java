package risk.aiplayers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

import risk.aiplayers.util.AIParameter;
import risk.aiplayers.util.AIUtil;
import risk.aiplayers.util.CacheMap;
import risk.aiplayers.util.EMMNode;
import risk.aiplayers.util.NodeType;
import risk.commonObjects.Territory;

public abstract class ExpectiminimaxPlayer extends AIPlayer {

	String line = "";
	int ind = 0;

	protected int maxDepth = -1;

	protected AIParameter params;

	protected int nodeCount = 0;
	protected int trimCount = 0;

	protected int foundIt = 0;
	protected int missedIt = 0;
	protected static final int NODE_VALUES_CACHE_SIZE = 1000000;
	
	public double weights[];

	protected CacheMap<Long, Double> NodeValues = new CacheMap<Long, Double>(NODE_VALUES_CACHE_SIZE);

	public ExpectiminimaxPlayer(String name, String opp, String map, int id,
			int depth, double [] weights) {

		super(EMM_AI, name, opp, map, id);

		params = new AIParameter();
		this.weights = weights;
		

		this.maxDepth = depth;

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
				if (!goingToController || !stillRunning)
					APM.controllerSocket.close();
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

	protected double EMM_AB(EMMNode node, int depth, double alpha, double beta) {

		if (node.getGame().getOtherPlayer().getTerritories().size() == 0) {
			if (node.isMaxPlayer())
				return Double.POSITIVE_INFINITY; // A win for the max player
			else
				return Double.NEGATIVE_INFINITY; // A loss for the max player
		} else if (node.getGame().getCurrentPlayer().getTerritories().size() == 0) {
			if (node.isMaxPlayer())
				return Double.NEGATIVE_INFINITY; // A loss for the max player
			else
				return Double.POSITIVE_INFINITY; // A win for the max player
		}

		if (depth == 0) {
			return getValue(node); // Return heuristic value of the game
		}

		double a = 0.0;
		if (node.getTreePhase() == NodeType.RANDOMEVENT) {
			a = 0.0;
			for (EMMNode child : getRandomAttackActions(node)) {
				nodeCount++;

				double value = AIUtil.getProb(child)
						* EMM_AB(child, depth - 1, alpha, beta);
				a += value;
			}
		} else {
			if (node.isMaxPlayer()) {
				for (EMMNode child : getActions(node, depth)) {
					nodeCount++;

					alpha = Math.max(alpha,
							EMM_AB(child, depth - 1, alpha, beta));

					if (beta <= alpha) {
						trimCount++;
						break;
					}
				}
				return alpha;
			} else {
				for (EMMNode child : getActions(node, depth)) {
					nodeCount++;

					beta = Math
							.min(beta, EMM_AB(child, depth - 1, alpha, beta));

					if (beta <= alpha) {
						trimCount++;
						break;
					}
				}
				return beta;
			}
		}
		return a;
	}

	protected LinkedList<EMMNode> getActions(EMMNode node, int depth) {
		LinkedList<EMMNode> actions = null;
		// writeGameState(node);
		switch (node.getTreePhase()) {
		case RECRUIT: {
			// Choose where to recruit troops
			// System.out.println("Recruit");
			actions = getRecruitActions(node, depth);
			break;
		}
		case ATTACK: {
			// Move after attack is resolved by moving all troops across.
			if (node.moveRequired()) {
				// System.out.println("MoveAfter");
				actions = getMoveAfterAttackActions(node);
			}

			// Choose an attack source and destination
			// System.out.println("Attack");
			actions = getAttackActions(node);

			if (actions.size() == 0) {
				actions = getMoveActions(node);
			}
			break;
		}
		case MANOEUVRE: {
			// Choose a manoeuvre source and destination
			// System.out.println("Manoeuvre");
			actions = getMoveActions(node);
			break;
		}
		//TODO: Should there be an "assert false" for these options? 
		case RANDOMEVENT:
		case MOVEAFTERATTACK:
			break;
		}

		return actions;
	}

	protected abstract LinkedList<EMMNode> getMoveAfterAttackActions(
			EMMNode node);

	protected abstract LinkedList<EMMNode> getRecruitActions(EMMNode node,
			int depth);

	protected abstract LinkedList<EMMNode> getAttackActions(EMMNode node);

	protected abstract LinkedList<EMMNode> getMoveActions(EMMNode node);

	protected LinkedList<EMMNode> getRandomAttackActions(EMMNode node) {
		LinkedList<EMMNode> stochasticActions = new LinkedList<EMMNode>();

		int sourceTroops = node.getGame().getCurrentPlayer()
				.getTerritoryByName(node.getAttackSource()).getNrTroops();
		int destTroops = node.getGame().getOtherPlayer()
				.getTerritoryByName(node.getAttackDest()).getNrTroops();

		// Get list of possible outcomes

		// Attacker
		if (sourceTroops == 2) {
			if (destTroops == 2 || destTroops == 1) {
				// Attacker Wins
				EMMNode tempA = node.clone();
				tempA.setDiceRolls(0, 0, 6, 0, 1);
				AIUtil.resolveAttackAction(tempA);
				tempA.setTreePhase(NodeType.ATTACK);
				stochasticActions.add(tempA);

				// Defender Wins
				EMMNode tempB = node.clone();
				tempB.setDiceRolls(0, 0, 1, 0, 6);
				AIUtil.resolveAttackAction(tempB);
				tempB.setTreePhase(NodeType.ATTACK);
				stochasticActions.add(tempB);

			} else {
				// Attacker Wins
				EMMNode tempA = node.clone();
				tempA.setDiceRolls(0, 0, 6, 1, 2);
				AIUtil.resolveAttackAction(tempA);
				tempA.setTreePhase(NodeType.ATTACK);
				stochasticActions.add(tempA);

				// Defender Wins
				EMMNode tempB = node.clone();
				tempB.setDiceRolls(0, 0, 1, 5, 6);
				AIUtil.resolveAttackAction(tempB);
				tempB.setTreePhase(NodeType.ATTACK);
				stochasticActions.add(tempB);

			}
		} else if (sourceTroops == 3) {
			if (destTroops == 2 || destTroops == 1) {
				// Attacker Wins
				EMMNode tempA = node.clone();
				tempA.setDiceRolls(0, 5, 6, 0, 1);
				AIUtil.resolveAttackAction(tempA);
				tempA.setTreePhase(NodeType.ATTACK);
				stochasticActions.add(tempA);

				// Defender Wins
				EMMNode tempB = node.clone();
				tempB.setDiceRolls(0, 1, 2, 0, 6);
				AIUtil.resolveAttackAction(tempB);
				tempB.setTreePhase(NodeType.ATTACK);
				stochasticActions.add(tempB);

			} else {
				// Attacker Wins both
				EMMNode tempA = node.clone();
				tempA.setDiceRolls(0, 5, 6, 1, 2);
				AIUtil.resolveAttackAction(tempA);
				tempA.setTreePhase(NodeType.ATTACK);
				stochasticActions.add(tempA);

				// One Each
				EMMNode tempB = node.clone();
				tempB.setDiceRolls(0, 1, 6, 2, 5);
				AIUtil.resolveAttackAction(tempB);
				tempB.setTreePhase(NodeType.ATTACK);
				stochasticActions.add(tempB);

				// Defender Wins both
				EMMNode tempC = node.clone();
				tempC.setDiceRolls(0, 1, 2, 5, 6);
				AIUtil.resolveAttackAction(tempC);
				tempC.setTreePhase(NodeType.ATTACK);
				stochasticActions.add(tempC);
			}

		} else {
			if (destTroops == 2 || destTroops == 1) {
				// Attacker Wins
				EMMNode tempA = node.clone();
				tempA.setDiceRolls(4, 5, 6, 0, 1);
				AIUtil.resolveAttackAction(tempA);
				tempA.setTreePhase(NodeType.ATTACK);
				stochasticActions.add(tempA);

				// Defender Wins
				EMMNode tempB = node.clone();
				tempB.setDiceRolls(1, 2, 3, 0, 6);
				AIUtil.resolveAttackAction(tempB);
				tempB.setTreePhase(NodeType.ATTACK);
				stochasticActions.add(tempB);

			} else {
				// Attacker Wins both
				EMMNode tempA = node.clone();
				tempA.setDiceRolls(4, 5, 6, 1, 2);
				AIUtil.resolveAttackAction(tempA);
				tempA.setTreePhase(NodeType.ATTACK);
				stochasticActions.add(tempA);

				// One Each
				EMMNode tempB = node.clone();
				tempB.setDiceRolls(1, 2, 6, 3, 5);
				AIUtil.resolveAttackAction(tempB);
				tempB.setTreePhase(NodeType.ATTACK);
				stochasticActions.add(tempB);

				// Defender Wins both
				EMMNode tempC = node.clone();
				tempC.setDiceRolls(1, 2, 3, 5, 6);
				AIUtil.resolveAttackAction(tempC);
				tempC.setTreePhase(NodeType.ATTACK);
				stochasticActions.add(tempC);
			}
		}
		return stochasticActions;
	}

	@Override
	public boolean attackAgain() {
		int count = 0;

		Iterator<Territory> it = game.getCurrentPlayer().getTerritories()
				.values().iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			for (Territory n : t.getNeighbours()) {
				if (game.getOtherPlayer().getTerritoryByName(n.getName()) != null) {
					count++;
				}
			}
		}

		if (count > 0) {
			return true;
		}
		return false;
	}

	public double getWeightedEval(EMMNode child) {
		int sourceTroops = child.getGame().getCurrentPlayer()
				.getTerritoryByName(child.getAttackSource()).getNrTroops();
		int destTroops = child.getGame().getOtherPlayer()
				.getTerritoryByName(child.getAttackDest()).getNrTroops();

		double value = 0.0;

		if (AIParameter.getProbOfWin(sourceTroops, destTroops) >= 0.99) {
			return Double.MAX_VALUE;
		}

		// Attacker
		if (sourceTroops == 2) {
			if (destTroops == 2 || destTroops == 1) {
				EMMNode newChild = child.clone();
				newChild.setDiceRolls(0, 0, 1, 0, 6);
				AIUtil.resolveAttackAction(newChild);

				value += AIUtil.getProb(newChild) * getValue(newChild);

				EMMNode newChild2 = child.clone();
				newChild2.setDiceRolls(0, 0, 6, 0, 1);
				AIUtil.resolveAttackAction(newChild2);

				value += AIUtil.getProb(newChild2) * getValue(newChild2);

			} else {
				EMMNode newChild = child.clone();
				newChild.setDiceRolls(0, 0, 1, 5, 6);
				AIUtil.resolveAttackAction(newChild);

				value += AIUtil.getProb(newChild) * getValue(newChild);

				EMMNode newChild2 = child.clone();
				newChild2.setDiceRolls(0, 0, 6, 1, 2);
				AIUtil.resolveAttackAction(newChild2);

				value += AIUtil.getProb(newChild2) * getValue(newChild2);
			}
		} else if (sourceTroops == 3) {
			if (destTroops == 2 || destTroops == 1) {
				EMMNode newChild = child.clone();
				newChild.setDiceRolls(0, 5, 6, 0, 1);
				AIUtil.resolveAttackAction(newChild);

				value += AIUtil.getProb(newChild) * getValue(newChild);

				EMMNode newChild2 = child.clone();
				newChild2.setDiceRolls(0, 1, 2, 0, 6);
				AIUtil.resolveAttackAction(newChild2);

				value += AIUtil.getProb(newChild2) * getValue(newChild2);
			} else {
				EMMNode newChild = child.clone();
				newChild.setDiceRolls(0, 1, 2, 5, 6);
				AIUtil.resolveAttackAction(newChild);

				value += AIUtil.getProb(newChild) * getValue(newChild);

				EMMNode newChild2 = child.clone();
				newChild2.setDiceRolls(0, 1, 6, 2, 5);
				AIUtil.resolveAttackAction(newChild2);

				value += AIUtil.getProb(newChild2) * getValue(newChild2);

				EMMNode newChild3 = child.clone();
				newChild3.setDiceRolls(0, 5, 6, 1, 2);
				AIUtil.resolveAttackAction(newChild3);

				value += AIUtil.getProb(newChild3) * getValue(newChild3);
			}

		} else {
			if (destTroops == 2 || destTroops == 1) {
				EMMNode newChild = child.clone();
				newChild.setDiceRolls(4, 5, 6, 0, 1);
				AIUtil.resolveAttackAction(newChild);

				value += AIUtil.getProb(newChild) * getValue(newChild);

				EMMNode newChild2 = child.clone();
				newChild2.setDiceRolls(1, 2, 3, 0, 6);
				AIUtil.resolveAttackAction(newChild2);

				value += AIUtil.getProb(newChild2) * getValue(newChild2);

			} else {
				EMMNode newChild = child.clone();
				newChild.setDiceRolls(4, 5, 6, 1, 2);
				AIUtil.resolveAttackAction(newChild);

				value += AIUtil.getProb(newChild) * getValue(newChild);

				EMMNode newChild2 = child.clone();
				newChild2.setDiceRolls(1, 2, 6, 3, 5);
				AIUtil.resolveAttackAction(newChild2);

				value += AIUtil.getProb(newChild2) * getValue(newChild2);

				EMMNode newChild3 = child.clone();
				newChild3.setDiceRolls(1, 2, 3, 5, 6);
				AIUtil.resolveAttackAction(newChild3);

				value += AIUtil.getProb(newChild3) * getValue(newChild3);
			}
		}

		return value;

	}

	public double getValue(EMMNode node) {

		long key = node.getHash();
		Double value = NodeValues.get(key);
		if (value != null) {
			foundIt++;
			return value;
		} else {
			missedIt++;
			value = AIUtil.eval(node, AIParameter.evalWeights, maxRecruitable);
			NodeValues.put(node.getHash(), value);
			return value;
		}
	}

	protected void writeGameState(EMMNode node) {

		File log = new File("gameLog" + getName() + ".txt");
		try {
			FileWriter fileWriter = new FileWriter(log, true);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

			bufferedWriter.write("\n\n<<Node " + node.getTreePhaseText()
					+ " Expanded>>\n");

			if (node.getTreePhase() == NodeType.ATTACK) {
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

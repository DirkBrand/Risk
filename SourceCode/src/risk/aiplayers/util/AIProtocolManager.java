package risk.aiplayers.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;

import risk.aiplayers.AIPlayer;
import risk.commonObjects.GameState;
import risk.commonObjects.Territory;

public class AIProtocolManager {

	public Socket controllerSocket;

	public BufferedReader input;
	public PrintWriter output;

	final LinkedList<String> BLANK_ARGS = new LinkedList<String>();

	AIPlayer ai;

	int lastID = 0;

	int setupTroopsPlaced = 0;
	int placeTroopsCount = 0;
	boolean justMoved = true;
	boolean firstManouevre = false;

	public AIProtocolManager(Socket controller, AIPlayer ai) {
		this.ai = ai;
		this.controllerSocket = controller;

		try {
			input = new BufferedReader(new InputStreamReader(
					controllerSocket.getInputStream()));
			output = new PrintWriter(controllerSocket.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}

		cleanFile();

	}

	private void cleanFile() {
		File log = new File("gameLog" + ai.getName() + ".txt");
		try {
			FileWriter fileWriter = new FileWriter(log);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

			bufferedWriter.write("START");
			bufferedWriter.close();

		} catch (IOException e) {
			System.out.println("COULD NOT LOG RESULTS!!");
		}
	}

	public void process(int id, String message, LinkedList<String> args) {
		lastID = id;
		if (message.length() == 0)
			return;
		
		//System.out.println(ai.getName() + " - Received: " + message);

		switch (message) {
		case "name": {
			LinkedList<String> rep = new LinkedList<String>();
			rep.add(ai.getName());
			rep.add(ai.getId() + "");
			sendSuccess(id, message, rep);
			break;
		}
		case "opponents": {
			sendSuccess(id, message, BLANK_ARGS);
			break;
		}

		case "maps": {
			sendSuccess(id, message, BLANK_ARGS);
			break;
		}

		case "start_choices": {
			if (ai.getOpponentName() != null) {
				LinkedList<String> choices = new LinkedList<String>();
				choices.add(ai.getName());
				choices.add(ai.getOpponentName());
				choices.add(ai.getMap());
				sendSuccess(getMesID(), message, choices);
			}
			break;
		}

		case "start_game": {
			ai.startGame(args.get(0), args.get(1), args.get(2));
			sendSuccess(id, message, BLANK_ARGS);
			break;
		}

		case "initial_own_territories": { // args is a list of territories.
			ai.getGameState().setPhase(GameState.SETUP);
			ai.setInitialTerritories(args);
			sendSuccess(id, message, BLANK_ARGS);
			break;
		}

		case "place_troops": {
			placeTroopsCount++;
			if (placeTroopsCount == 2) {
				ai.setPhase(GameState.RECRUIT);
				placeTroopsCount = 0;
			}
			ai.recruit(Integer.parseInt(args.get(0)));
			break;
		}

		case "troops_placed": {
			for (int i = 0; i < args.size() / 2; i++) {
				ai.troopPlaced(Integer.parseInt(args.get(i * 2)),
						Integer.parseInt(args.get(i * 2 + 1)));
			}

			if (ai.getGameState().getPhase() == GameState.SETUP) {
				setupTroopsPlaced++;
			} else if (ai.getGameState().getPhase() == GameState.RECRUIT) {
				ai.getGameState().setPhase(GameState.BATTLE);
			}

			sendSuccess(id, message, BLANK_ARGS);

			break;
		}

		case "attack": {
			if (ai.getGameState().getPhase() != GameState.BATTLE) {
				ai.getGameState().setPhase(GameState.BATTLE);
				LinkedList<String> reply = ai.getAttackSourceDestination();
				if (reply.size() == 0)
					ai.setPhase(GameState.MANOEUVRE);

				sendSuccess(getMesID(), "attack", reply);
			} else {
				if (ai.attackAgain()) {
					LinkedList<String> reply = ai.getAttackSourceDestination();
					if (reply == null || reply.size() == 0)
						ai.setPhase(GameState.MANOEUVRE);
					sendSuccess(getMesID(), "attack", reply);
				} else {
					ai.setPhase(GameState.MANOEUVRE);
					sendSuccess(id, message, BLANK_ARGS);
				}
			}
			break;
		}

		case "attack_result": {
			ai.resolveAttack(Integer.parseInt(args.get(0)),
					Integer.parseInt(args.get(1)),
					Integer.parseInt(args.get(2)),
					Integer.parseInt(args.get(3)),
					Integer.parseInt(args.get(4)),
					Integer.parseInt(args.get(5)),
					Integer.parseInt(args.get(6)));

			if (ai.moveAfterAttackRequired)
				justMoved = false;

			sendSuccess(id, message, BLANK_ARGS);
			break;
		}

		case "manoeuvre": {
			if (!firstManouevre) {
				ai.updateConnectedRegions();
				firstManouevre = true;
			}

			if (ai.getPhase() == GameState.BATTLE) {
				LinkedList<String> reply = ai.getMoveAfterAttack();

				ai.resolveManoeuvre(Integer.parseInt(reply.get(0)),
						Integer.parseInt(reply.get(1)),
						Integer.parseInt(reply.get(2)));
				sendSuccess(getMesID(), "manoeuvre", reply);
			} else if (ai.getPhase() == GameState.MANOEUVRE) {
				LinkedList<String> reply = ai.getManSourceDestination();

				endTurn();
				sendSuccess(getMesID(), "manoeuvre", reply);
			}

			break;
		}

		case "move_troops": {
			if (args.size() > 0) {
				ai.resolveManoeuvre(Integer.parseInt(args.get(0)),
						Integer.parseInt(args.get(1)),
						Integer.parseInt(args.get(2)));
			}

			if (justMoved) {
				endTurn();
			}
			justMoved = true;
			sendSuccess(id, message, BLANK_ARGS);
			break;
		}

		case "result": {
			if (args.size() > 0) {
				System.out.println(args.get(0));
			}
			// System.out.println(ai.getName() + " - " + ai.numberOfMovesTaken);

			sendSuccess(id, message, BLANK_ARGS);

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			ai.stillRunning = false;
			break;
		}

		default: {
			System.out.println("UNKNOWN REQUEST: " + message);
		}

		}

		if (!message.equals("name") && !message.equals("start_choices")) {
		//writeGameState(message, args);
		}

	}

	private void writeGameState(String message, LinkedList<String> args) {
		File log = new File("gameLog" + ai.getName() + ".txt");
		int count1 = 0;
		int count2 = 0;
		try {
			FileWriter fileWriter = new FileWriter(log, true);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

			bufferedWriter.write("\n\n<<After " + message + " "
					+ args.toString() + ">>\n\n");

			bufferedWriter.write("\n"
					+ ai.getGameState().getCurrentPlayer().getName()
					+ " Territories\n\n");
			Iterator<Territory> it = ai.getGameState().getCurrentPlayer()
					.getTerritories().values().iterator();
			while (it.hasNext()) {
				Territory t = it.next();
				bufferedWriter.write(t.getId() + " " + t.getName() + " - "
						+ t.getNrTroops() + "\n");
				count1 += t.getNrTroops();
			}
			bufferedWriter.write("\n" + count1 + "\n");

			bufferedWriter.write("\n"
					+ ai.getGameState().getOtherPlayer().getName()
					+ " Territories\n\n");
			it = ai.getGameState().getOtherPlayer().getTerritories().values()
					.iterator();
			while (it.hasNext()) {
				Territory t = it.next();
				bufferedWriter.write(t.getId() + " " + t.getName() + " - "
						+ t.getNrTroops() + "\n");
				count2 += t.getNrTroops();
			}
			bufferedWriter.write("\n" + count2 + "\n");

			bufferedWriter.close();

		} catch (IOException e) {
			System.out.println("COULD NOT LOG RESULTS!!");
		}
	}

	private void endTurn() {
		ai.getGameState().setPhase(GameState.RECRUIT);
		ai.getGameState().changeCurrentPlayer();

		setupTroopsPlaced = 0;
		placeTroopsCount = 0;
		justMoved = true;
		firstManouevre = false;

	}

	public void sendSuccess(int id, String command, LinkedList<String> args) {
		String message = "[";
		if (id != 0) {
			message += id;
		}
		message += "]" + command + "=[";
		if (args != null && args.size() > 0) {
			for (String s : args) {
				message += s + ",";
			}
			message = message.substring(0, message.length() - 1);
		}
		message += ']';
		send(message);
	}

	public void send(String mes) {
		if (controllerSocket != null && !controllerSocket.isClosed()) {
			output.println(mes);
		}
		// System.out.println(mes);
	}

	public int getMesID() {
		return lastID;
	}
}

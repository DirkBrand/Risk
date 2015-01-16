package risk.humanEngine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;

import risk.commonObjects.GameState;
import risk.commonObjects.Player;

public class EngineProtocolManager {

	private Socket controllerSocket;

	private BufferedReader input;
	private PrintWriter output;

	final LinkedList<String> BLANK_ARGS = new LinkedList<String>();

	private EngineLogic EL;

	public int setupTroopsPlaced = 0;

	public boolean justMoved = true;

	public EngineProtocolManager(Socket controller, EngineLogic engineLogic) {
		this.EL = engineLogic;
		this.controllerSocket = controller;

		try {
			input = new BufferedReader(new InputStreamReader(
					controllerSocket.getInputStream()));
			output = new PrintWriter(controllerSocket.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}

		new Thread(new CommunicationThread(controller, input)).start();
	}

	public void process(int id, String message, LinkedList<String> args) {
		EL.setMessageID(id);

		if (message.length() == 0)
			return;

		switch (message) {
		case "name": {
			LinkedList<String> rep = new LinkedList<String>();
			rep.add(EL.getUsername());
			sendSuccess(id, message, rep);
			break;
		}
		case "opponents": {
			EL.setOpponents(args);
			sendSuccess(id, message, BLANK_ARGS);
			break;
		}

		case "maps": {
			EL.setMaps(args);
			sendSuccess(id, message, BLANK_ARGS);
			break;
		}

		case "start_choices": {
			break;
		}

		case "start_game": {
			EL.startGame(args.get(0), args.get(1), args.get(2));
			sendSuccess(id, message, BLANK_ARGS);
			break;
		}

		case "initial_own_territories": { // args is a list of territories.
			EL.setPhase(GameState.SETUP);
			EL.setInitialTerritories(args);
			sendSuccess(id, message, BLANK_ARGS);
			break;
		}

		case "place_troops": {
			if (EL.getPhase() == GameState.SETUP) {
				EL.setupRecruit(Integer.parseInt(args.get(0)));
				EL.showToaster(
						"It is the setup phase.  Place your initial troops.",
						3000);
			} else if (EL.getPhase() == GameState.RECRUIT) {
				reset();
				EL.recruitment(Integer.parseInt(args.get(0)));
				EL.showToaster(
						"It is the Recruitment phase.  You have "
								+ args.getLast() + " troops to place.", 2500);
			}
			break;
		}

		case "troops_placed": {
			for (int i = 0; i < args.size() / 2; i++) {
				EL.troopPlaced(Integer.parseInt(args.get(i * 2)),
						Integer.parseInt(args.get(i * 2 + 1)));
			}
			EL.updateMap();
			sendSuccess(id, message, BLANK_ARGS);
			if (EL.getPhase() == GameState.SETUP) {
				setupTroopsPlaced++;
				if (setupTroopsPlaced == 3) {
					EL.setOtherPlayerHasSetUp();
					for (Player p : EL.game.getPlayers()) {
						if (p.getId() != EL.getMePlayer().getId()) {
							EL.showToaster(p.getName()
									+ " has finished setting up.", 2500);
						}
					}
				}

				if (EL.otherPlayerHasSetUp && EL.mePlayerHasSetUp) {
					EL.setPhase(GameState.RECRUIT);
					EL.flipGlassPane();
				}
			} else if (EL.getPhase() == GameState.RECRUIT) {
				EL.showToaster(EL.getCurrentPlayer().getName()
						+ " finished recruiting troops.", 2500);
				EL.setPhase(GameState.BATTLE);
			}
			justMoved = true;
			break;
		}

		case "attack": {
			if (EL.getPhase() != GameState.BATTLE) {
				EL.setPhase(GameState.BATTLE);
				EL.getAttackSourceDestination();
				EL.showToaster(
						"It is the Battle phase. Pick a source and Destination for your attack.",
						2500);
			} else {
				// EL.determineAttackAgain();
			}
			break;
		}

		case "attack_result": {
			String result = EL.resolveAttack(Integer.parseInt(args.get(0)),
					Integer.parseInt(args.get(1)),
					Integer.parseInt(args.get(2)),
					Integer.parseInt(args.get(3)),
					Integer.parseInt(args.get(4)),
					Integer.parseInt(args.get(5)),
					Integer.parseInt(args.get(6)));
			sendSuccess(id, message, BLANK_ARGS);
			if (!EL.getUsername().equalsIgnoreCase(
					EL.getCurrentPlayer().getName())) {
				EL.showToaster(EL.getCurrentPlayer().getName()
						+ " attacked you!\n" + result, 2500);
			} else {
				EL.postBattleResult(result);
			}

			if (EL.moveAfterAttackRequired)
				justMoved = false;
			break;
		}

		case "manoeuvre": {
			EL.getManSourceDestination();
			break;
		}

		case "move_troops": {
			if (args.size() != 0) {
				EL.resolveManoeuvre(Integer.parseInt(args.get(0)),
						Integer.parseInt(args.get(1)),
						Integer.parseInt(args.get(2)));	
				if (!EL.getUsername().equalsIgnoreCase(
						EL.getCurrentPlayer().getName())) {
					if (Integer.parseInt(args.get(2)) > 0) {
						EL.showToaster(
								EL.getCurrentPlayer().getName()
										+ " has moved "
										+ args.get(2)
										+ " troops from "
										+ EL.getCurrentPlayer()
												.getTerritoryByID(
														Integer.parseInt(args
																.get(0))).getName()
										+ " to "
										+ EL.getCurrentPlayer()
												.getTerritoryByID(
														Integer.parseInt(args
																.get(1))).getName(),
								3000);
					}
				}			
			}

			sendSuccess(id, message, BLANK_ARGS);
			if (justMoved) {
				EL.setPhase(GameState.RECRUIT);
				EL.showToaster(EL.getCurrentPlayer().getName()
						+ "'s turn is ending... ", 4000);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				EL.endTurn();
				reset();
			}
			justMoved = true;
			break;
		}

		case "result": {
			EL.endOfGame(args.getFirst());
			break;
		}

		default: {
			System.out.println("UNKNOWN REQUEST: " + message);
		}

		}
	}

	private void reset() {
		EL.moveAfterAttackRequired = false;
		setupTroopsPlaced = 0;
		justMoved = true;
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

	/* private void sendFailure(int id, String mess) {
		String message = "[";
		if (id != 0) {
			message += id;
		}
		message += "]?";
		if (mess != null) {
			message += mess;
		}

		send(message);
	} */

	public void sendMessage(String message) {
		send(message);
	}

	private void send(String mes) {
		// TODO: messy - I think just the last check should be necessary by keeping output
		// correctly set - RSK 20150115
		if (controllerSocket != null && !controllerSocket.isClosed() && output != null) {
			output.println(mes);
		}
	}

	public class CommunicationThread implements Runnable {
		BufferedReader input;
		Socket controller;
		String line = "";
		int i = 0;

		public CommunicationThread(Socket controller, BufferedReader input) {
			this.controller = controller;
			this.input = input;
		}

		@Override
		public void run() {
			try {
				while ((line = input.readLine()) != null) {
					i = 0;

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
						send("=");
						EL.establishControllerConnection(
								controllerAddStr.replaceAll(",", "\\."),
								Integer.parseInt(controllerPortStr));
						break;
					} else if (temp == '[') {
						temp = readChar();
						String idStr = "";
						while (Character.isDigit(temp)) {
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
						}

						process(messageID, request, args);
					}
				}
				
				controller.close();
			} catch (SocketException e) {
				System.err.println("Connection to server broken");
				System.exit(0);
			} catch (NullPointerException e) {
				System.err.println("No connection to server - input not initialized");
				// TODO: We should really exit in this situation?  Avoided doing so since most tests
				// fail if we make that change - so we should fix those tests, I guess
				// RSK: 20150115
				// System.exit(0);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		private char readChar() throws Exception {
			char temp = ' ';
			while (Character.isWhitespace(temp)) {

				temp = line.charAt(i++);
				if (temp == '\0')
					return ' ';
			}

			return temp;

		}
	}
}

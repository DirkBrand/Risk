package risk.server.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketException;
import java.util.Iterator;
import java.util.LinkedList;

import risk.commonObjects.ConnectedPlayer;
import risk.commonObjects.GameState;
import risk.commonObjects.Logger;
import risk.commonObjects.Player;
import risk.commonObjects.Territory;
import risk.server.facilitator.Facilitator;

public class ControllerProtocolManager {

	public String[] messages = new String[] { "opponents", "maps",
			"start_choices", "start_game", "initial_own_territories",
			"place_troops", "troops_placed", "attack", "attack_result",
			"manoeuvre", "move_troops", "result" };

	final LinkedList<String> BLANK_ARGS = new LinkedList<String>();

	LinkedList<ConnectedPlayer> clients;

	LinkedList<CommunicationThread> channels;

	Logger log;

	private int mesID = 1;

	LinkedList<ConnectedPlayer[]> playerPairs = new LinkedList<ConnectedPlayer[]>();
	LinkedList<String> mapNames = new LinkedList<String>();

	Facilitator facilitator;
	ControllerLogic controller;

	int troopPlacedCount[] = new int[] { 0, 0 };
	int troopPlacedTotalCount = 0;

	int setupPlacedCount = 0;
	int attackResultCount = 0;
	int initialCount = 0;

	int resultCount = 0;

	public ControllerProtocolManager(Logger log, ControllerLogic controllerLogic) {
		this.log = log;
		this.controller = controllerLogic;

		clients = new LinkedList<ConnectedPlayer>();
		channels = new LinkedList<CommunicationThread>();
		//cleanFile();

		}
		
		private void cleanFile() {
			File log = new File("gameLog.txt");
			try {
				FileWriter fileWriter = new FileWriter(log);
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

				bufferedWriter.write("START");
				bufferedWriter.close();

			} catch (IOException e) {
				System.out.println("COULD NOT LOG RESULTS!!");
			}
		}

	public int getMesID() {
		return mesID;
	}

	public void setMesID(int id) {
		mesID = id;
	}

	public void connectPlayer(ConnectedPlayer cp) {
		for (Player p : controller.getGameState().getPlayers()) {
			if (p.getId() == cp.getId()) {
				cp.setName(p.getName());

				// Determine if connected player is AI.
				if (p.getName().endsWith("AI")) {
					cp.setAI(true);
				}
			}
		}
		//System.out.println(cp.getName() + " connected to the Controller!");
		
		clients.add(cp);
		CommunicationThread ct = new CommunicationThread(cp);
		Thread t = new Thread(ct);
		t.start();
		channels.add(ct);

		
		if (clients.size() == 2) {
			controller.startGame();
		}
	}

	public void addPlayer(ConnectedPlayer cp) {
		clients.add(cp);
	}

	public String getCommand(int n) {
		return messages[n];
	}

	public synchronized void sendMessage(int destID, int messageID, String message,
			LinkedList<String> arguments){
		String finalMessage = message;
		if (messageID != 0) {
			finalMessage = "[" + messageID + "]" + finalMessage;
		}
		if (arguments != null) {
			finalMessage += "[";
			String args = "";
			for (String s : arguments)
				args += s + ',';

			if (args.length() != 0)
				finalMessage += args.substring(0, args.length() - 1);
			finalMessage += "]";
		}
		for (ConnectedPlayer cp : clients) {
			if (cp.getId() == destID) {				
				cp.send(finalMessage);
			}
		}

		mesID++;
		//System.out.println(finalMessage);
		//writeGameState(finalMessage);

	}

	private synchronized void writeGameState(String message) {
		File log = new File("gameLog.txt");
		try {
			FileWriter fileWriter = new FileWriter(log, true);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

			bufferedWriter.write("\n\n<<" + message + ">>\n\n");

			bufferedWriter.write("\n" + controller.getCurrentPlayer().getName()
					+ " Territories\n\n");
			Iterator<Territory> it = controller.getCurrentPlayer()
					.getTerritories().values().iterator();
			while (it.hasNext()) {
				Territory t = it.next();
				bufferedWriter.write(t.getId() + " " + t.getName() + " - " + t.getNrTroops()
						+ "\n");
			}

			bufferedWriter.write("\n" + controller.getOtherPlayer().getName()
					+ " Territories\n\n");
			it = controller.getOtherPlayer()
					.getTerritories().values().iterator();
			while (it.hasNext()) {
				Territory t = it.next();
				bufferedWriter.write(t.getId() + " " + t.getName() + " - " + t.getNrTroops()
						+ "\n");
			}

			bufferedWriter.close();

		} catch (IOException e) {
			System.out.println("COULD NOT LOG RESULTS!!");
		}
	}

	public void closeConnection(int id) {
		if (clients.get(id) != null)
			clients.get(id).closeAll();

		clients.set(id, null);

	}



	public void processSuccess(ConnectedPlayer cp, int messageID,
			String messageFromClient, LinkedList<String> arguments)
			throws Exception {
		/*
		 * if ((messageID != (mesID - 1)) && (messageID != (mesID - 2))) throw
		 * new Exception("Missmatched ID by " + cp.getName() + " - " + (mesID -
		 * 1) + " expected, " + messageID + " received.");
		 */

		/*System.out.println(">> I received " + messageFromClient + " from <"
				+ cp.getName() + "> with args: " + arguments.toString());*/
		
		switch (messageFromClient) {

		// CONTROLLER PROTOCOL
		case "initial_own_territories": {
			initialCount++;
			if (initialCount == 1) {
				Player player2 = controller.getGameState().getPlayers().get(1);

				LinkedList<String> pt = new LinkedList<String>();
				Iterator<Territory> it = player2.getTerritories().values().iterator();
				while (it.hasNext()) {
					Territory t = it.next();
					pt.add(t.getId() + "");
				}
				sendMessage(player2.getId(), mesID, getCommand(4), pt);
			} else if (initialCount == 2) {
				setupPlacedCount = 0;
				controller.starterAllocation(0);
			}
			break;
		}

		case "place_troops": {
			controller.placeTroopsReply(cp.getId(), arguments);
			if (controller.getPhase() == GameState.SETUP) {
				sendMessage((cp.getId() + 1) % 2, mesID, getCommand(6),
						arguments);
			} else {
				sendMessage(controller.getOtherPlayerID(), mesID,
						getCommand(6), arguments);
			}
			break;
		}

		case "troops_placed": {
			if (controller.getPhase() == GameState.SETUP) {
				troopPlacedCount[cp.getId()]++;
				if (troopPlacedCount[0] == 1 && troopPlacedCount[1] == 0) {
					sendMessage(controller.getGameState().getPlayers().get(1)
							.getId(), getMesID(), getCommand(6),
							controller.placed1);
				} else if (troopPlacedCount[0] == 1 && troopPlacedCount[1] == 1) {
					controller.starterAllocation(1);
				} else if (troopPlacedCount[0] == 2 && troopPlacedCount[1] == 1) {
					sendMessage(controller.getGameState().getPlayers().get(1)
							.getId(), getMesID(), getCommand(6),
							controller.placed1);
				} else if (troopPlacedCount[0] == 2 && troopPlacedCount[1] == 2) {
					setupPlacedCount = 0;

					if (clients.get(0).isAI() && clients.get(1).isAI()) {
						// send place_troops message to players
						LinkedList<String> num1 = new LinkedList<String>();
						num1.add(controller.playerRec + "");
						sendMessage(clients.get(0).getId(), getMesID(),
								getCommand(5), num1);
					} else {
						LinkedList<String> num1 = new LinkedList<String>();
						num1.add(controller.playerRec + "");
						LinkedList<String> num2 = new LinkedList<String>();
						num2.add(controller.playerRec + "");

						sendMessage(clients.get(1).getId(), getMesID(),
								getCommand(5), num2);
						sendMessage(clients.get(0).getId(), getMesID(),
								getCommand(5), num1);
					}
				} else if (((troopPlacedCount[0] == 3 && troopPlacedCount[1] == 2) || (troopPlacedCount[0] == 2 && troopPlacedCount[1] == 3))
						&& clients.get(0).isAI() && clients.get(1).isAI()) {

					LinkedList<String> num2 = new LinkedList<String>();
					num2.add(controller.playerRec + "");
					sendMessage(clients.get(1).getId(), getMesID(),
							getCommand(5), num2);

				} else if (troopPlacedCount[0] == 3 && troopPlacedCount[1] == 3) {
					controller.setPhase(GameState.RECRUIT);
					int recruitedNumber = controller
							.calculatedRecruitedTroops();
					LinkedList<String> args = new LinkedList<String>();
					args.add(recruitedNumber + "");
					sendMessage(controller.getCurrentPlayerID(), messageID,
							getCommand(5), args);

				}
			} else if (controller.getPhase() == GameState.RECRUIT) {
				controller.setPhase(GameState.BATTLE);
				sendMessage(controller.getCurrentPlayerID(), mesID,
						getCommand(7), BLANK_ARGS);
			}
			break;
		}

		case "attack": {
			if (arguments.isEmpty()) {
				controller.setPhase(GameState.MANOEUVRE);
				sendMessage(cp.getId(), mesID, getCommand(9), BLANK_ARGS);
			} else {
				attackResultCount = 0;
				controller.resolveAttack(arguments.get(0), arguments.get(1));
			}
			break;
		}

		case "attack_result": {
			attackResultCount++;
			if (attackResultCount == 1) {
				sendMessage(controller.getCurrentPlayerID(), getMesID(),
						getCommand(8), controller.attackResult);
			} else if (attackResultCount == 2) {
				if (controller.moveRequired) {
					sendMessage(controller.getCurrentPlayerID(), mesID,
							getCommand(9), BLANK_ARGS);
				} else {
					sendMessage(controller.getCurrentPlayerID(), mesID,
							getCommand(7), BLANK_ARGS);
				}
			}
			break;
		}

		case "manoeuvre": {
			if (arguments.isEmpty()) {
				if (controller.getPhase() == GameState.MANOEUVRE) {
					sendMessage(controller.getOtherPlayerID(), mesID,
							getCommand(10), arguments);
				} else if (controller.getPhase() == GameState.BATTLE) {
					throw new Exception(
							"Cannot move no troops after defeating territory");
				}
			} else {
				controller.resolveManoeuvre(Integer.parseInt(arguments.get(0)),
						Integer.parseInt(arguments.get(1)),
						Integer.parseInt(arguments.get(2)));
				sendMessage(controller.getOtherPlayerID(), mesID,
						getCommand(10), arguments);
			}
			break;
		}

		case "move_troops": {
			if (controller.getPhase() == GameState.BATTLE) {
				sendMessage(controller.getCurrentPlayerID(), mesID,
						getCommand(7), BLANK_ARGS);
			} else if (controller.getPhase() == GameState.MANOEUVRE) {
				resetCounters();
				controller.nextTurn();
			}
			break;
		}

		case "result": {
			resultCount++;
			if (resultCount == 1) {
				clients.remove(cp);
				cp.closeAll();
				System.out.println(controller.gameResult.toString());
				sendMessage(controller.getCurrentPlayerID(), getMesID(),
						getCommand(11), controller.gameResult);
			} else if (resultCount == 2) {
				clients.remove(cp);
				cp.closeAll();
				controller.server1.close();
				controller.server2.close();
			}
			break;
		}

		default: {
			System.out.println("UNKNOWN REQUEST");
		}

		}
	}

	public void resetCounters() {
		troopPlacedCount = new int[] { 0, 0 };
		troopPlacedTotalCount = 0;

		setupPlacedCount = 0;
		attackResultCount = 0;
		initialCount = 0;
	}

	public void processFailure(int messageID, String err) {
		String mes = "Failure triggered by request: " + err
				+ "\n with message id : " + messageID;
		log.log(Logger.MINIMAL, mes);
		System.out.println("FAIL");
		System.exit(0);
	}

	public void processError(int messageID, String err) {
		System.err.println(err);
		/* TODO */
	}

	// Reads replies from engines
	public class CommunicationThread implements Runnable {
		ConnectedPlayer client;
		BufferedReader input;
		
		String line = "";
		int i = 0;

		public CommunicationThread(ConnectedPlayer cp) {
			client = cp;
			input = cp.getInput();
		}

		@Override
		public void run() {
			try {
				while ((line = input.readLine()) != null) {
					i = 0;
					
					int messageID = 0;
					LinkedList<String> arguments = new LinkedList<String>();
					boolean failure = false;
					boolean success = false;

					char temp = readChar();
					if (temp == '[')
						temp = readChar();

					String idStr = "";
					while (Character.isDigit(temp)) {
						idStr += temp;
						temp = readChar();
						if (temp == ']') {
							temp = readChar();
							break;
						}
						if (!Character.isDigit(temp)) {
							throw new Exception(
									"Incorrect ID specified for message");

						}
					}
					messageID = Integer.parseInt(idStr);
					String mes = "";
					while (temp != '=' && temp != '?') {
						mes += temp;
						temp = readChar();
					}
					if (temp == '=') {
						success = true;
					} else if (temp == '?') {
						success = false;
					} else {
						throw new Exception("Incorrect symbol: " + temp);
					}
					temp = readChar(); // read next symbol after =/?

					if (temp == '#') {
						failure = true;
						temp = readChar();
					}

					if (success) { // Successful response
						if (temp == '[') {
							temp = readChar();
							while (temp != ']') {
								String resp = "";
								while (temp != ',' && temp != ']') {
									resp += temp;
									temp = readChar();
								}
								if (resp.length() != 0) {
									arguments.add(resp);
								}
								if (temp == ']')
									break;
								else
									temp = readChar();

							}
						}
						processSuccess(client, messageID, mes, arguments);
					} else { // Error response
						String err = "";
						while (temp != ' ') {
							err += temp;
							temp = readChar();
						}
						if (failure)
							processFailure(messageID, err);
						else
							processError(messageID, err);
					}

				}

				//System.out.println("Controller Protocol Thread Over");

			} catch (Exception se) {
				//System.out.println("Connection Closed");
				for (ConnectedPlayer cp : clients) {
					cp.closeAll();
				}
				controller.closeServers();
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

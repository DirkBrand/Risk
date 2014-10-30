package risk.server.facilitator;

import java.io.BufferedReader;
import java.io.File;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;

import risk.commonObjects.ConnectedPlayer;
import risk.commonObjects.Logger;
import risk.server.controller.ControllerLogic;

public class FacilitatorProtocolManager {

	public String[] messages = new String[] { "opponents", "maps",
			"start_choices", "start_game", "initial_own_territories",
			"place_troops", "troops_placed", "attack", "attack_result",
			"manoeuvre", "move_troops", "result" };

	String lastMessage = "";

	final LinkedList<String> BLANK_ARGS = new LinkedList<String>();

	LinkedList<ConnectedPlayer> clients;
	LinkedList<CommunicationThread> channels;

	Logger log;

	public static int mesID = 1;

	LinkedList<ConnectedPlayer[]> playerPairs = new LinkedList<ConnectedPlayer[]>();
	LinkedList<String> mapNames = new LinkedList<String>();

	Facilitator facilitator;
	ControllerLogic controller;

	int controllerPort = 4000;

	int setupPlacedCount = 0;
	int attackResultCount = 0;
	int startCount = 0;
	int startChoicesCount = 0;

	public FacilitatorProtocolManager(Logger log, Facilitator facilitator) {
		this.log = log;
		this.facilitator = facilitator;

		clients = new LinkedList<ConnectedPlayer>();
		channels = new LinkedList<CommunicationThread>();
	}

	public int getMesID() {
		return mesID;
	}

	public void setMesID(int id) {
		mesID = id;
	}

	public void connectPlayer(ConnectedPlayer cp) {
		clients.add(cp);
		CommunicationThread ct = new CommunicationThread(cp);
		Thread t = new Thread(ct);
		t.start();
		channels.add(ct);

		try {
			sendMessage(cp.getId(), mesID, "name", BLANK_ARGS);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void startPlayer(int id, Socket client) {
		ConnectedPlayer cp = new ConnectedPlayer(client, id, client.getPort(), client.getInetAddress());
		connectPlayer(cp);

	}

	public String getCommand(int n) {
		return messages[n];
	}

	public void sendMessage(int destID, int messageID, String message,
			LinkedList<String> arguments) throws Exception {
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
		boolean found = false;
		for (ConnectedPlayer cp : clients) {
			if (cp.getId() == destID) {
				found = true;
				cp.send(finalMessage);
				//facilitator.printToGui(cp.getName() + "    " + finalMessage);
			}
		}
		if (!found)
			throw new Exception("Invalid Destination ID");

		mesID++;
		lastMessage = message;
		//System.out.println(finalMessage);
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
		  if ((messageID != (mesID - 1)) && (messageID != (mesID - 2))) throw
		  new Exception("Missmatched ID by " + cp.getName() + " - " + (mesID -
		 1) + " expected, " + messageID + " received.");
		 */
		/*System.out.println(">> I received " + messageFromClient + " from <"
		+ cp.getName() + "> with args: " + arguments.toString());*/

		switch (messageFromClient) {
		// FACILITATOR PROTOCOL
		case "name": {
			facilitator.printToGui(arguments.get(0) + " Connected.");
			
			cp.setName(arguments.get(0));
			if (arguments.get(0).endsWith("AI")) {
				// For AI players
				cp.setAI(true);
				cp.setPlayerOrderNumber(Integer.parseInt(arguments.get(1)));
				if (playerPairs.size() > 0
						&& cp.getName().equalsIgnoreCase(
								playerPairs.getLast()[1].getName())) {

					// Sending the Start_Game to the human player
					playerPairs.getLast()[1] = cp;
					LinkedList<String> args = new LinkedList<String>();
					args.add(playerPairs.getLast()[0].getName());
					args.add(playerPairs.getLast()[1].getName());
					args.add(mapNames.getLast());
					sendMessage(playerPairs.getLast()[0].getId(), mesID,
							getCommand(3), args);
				} else {
					sendMessage(cp.getId(), mesID, getCommand(2), BLANK_ARGS);
				}
			} else {
				LinkedList<String> opps = new LinkedList<String>();
				for (ConnectedPlayer c : clients) {
					if (c.getId() != cp.getId())
						opps.add(c.getName());
				}

				try { // Send Opponents Message (Id = 0)
					sendMessage(cp.getId(), mesID, getCommand(0), opps);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		}
		case "opponents": {
			LinkedList<String> maps = readMaps("./MapFiles");
			try { // Send Maps message (Id = 1)
				sendMessage(cp.getId(), mesID, getCommand(1), maps);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}

		case "maps": {
			try { // Send startChoices message (Id = 2)
				
				sendMessage(cp.getId(), mesID, getCommand(2), BLANK_ARGS);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}

		case "start_choices": { // arguments are : player, opponent, mapName
			if (cp.isAI()) {
				ConnectedPlayer[] pair = new ConnectedPlayer[2];
				pair[0] = cp;

				for (ConnectedPlayer c : clients) {
					if (c.getId() != cp.getId()
							&& c.getName().equalsIgnoreCase(arguments.get(1))
							&& c.getSocket() != null) {
						pair[1] = c;
						break;
					}
				}

				playerPairs.add(pair);
				mapNames.add(arguments.getLast());

				LinkedList<String> args = new LinkedList<String>();
				args.add(pair[0].getName());
				args.add(pair[1].getName());
				args.add(mapNames.getLast());
				sendMessage(pair[0].getId(), mesID, getCommand(3), args);

			} else {
				try { // Send start game message (Id = 3)
					ConnectedPlayer[] pair = new ConnectedPlayer[2];
					pair[0] = cp;

					for (ConnectedPlayer c : clients) {
						if (c.getId() != cp.getId()
								&& c.getName().equalsIgnoreCase(
										arguments.get(1))) {
							pair[1] = c;
							break;
						}
					}

					playerPairs.add(pair);
					mapNames.add(arguments.getLast());

					if (pair[1].isAI()) {
						facilitator.launchAI(pair[1].getName());
					} else {
						LinkedList<String> args = new LinkedList<String>();
						args.add(pair[0].getName());
						args.add(pair[1].getName());
						args.add(mapNames.getLast());
						sendMessage(pair[0].getId(), mesID, getCommand(3), args);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			break;
		}

		case "start_game": {
			startCount++;
			if (cp.getPlayerOrderNumber() == 1) {
				// Sending Start_Game to the AI player.
				LinkedList<String> mes = new LinkedList<String>();
				mes.add(playerPairs.getLast()[0].getName());
				mes.add(playerPairs.getLast()[1].getName());
				mes.add(mapNames.getLast());
				sendMessage(playerPairs.getLast()[1].getId(), mesID,
						getCommand(3), mes);
			} else if (cp.getPlayerOrderNumber() == 2) {
				while (startCount == 0);
				clients.remove(playerPairs.getLast()[0]);
				clients.remove(playerPairs.getLast()[1]);
				facilitator.startGame(playerPairs.removeLast(),
						mapNames.removeLast(), log, controllerPort);
				
				controllerPort += 2;
				if (controllerPort > 5000) controllerPort = 4000;
				
				startCount -= 2;
			}
			break;
		}

		default: {
			facilitator.printToGui("UNKNOWN REQUEST: " + messageFromClient);
			break;
		}

		}
	}

	public void processFailure(int messageID, String err) {
		String mes = "Failure triggered by request: " + err
				+ "\n with message id : " + messageID;
		log.log(Logger.MINIMAL, mes);
		System.exit(0);
	}

	public void processError(int messageID, String err) {
		System.err.println(err);
		/* TODO */
	}

	private LinkedList<String> readMaps(String path) {
		String file;
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();

		LinkedList<String> maps = new LinkedList<String>();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				file = listOfFiles[i].getName();
				if (file.endsWith(".txt")) {
					maps.add(file);
				}
			}
		}

		return maps;
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

					/*
					 * while (client.getSocket().getInputStream().available() ==
					 * 0) { if (isInterrupted()) { return; } }
					 */
					char temp = readChar();
					if (temp == '=') {
						break;
					}
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

				facilitator
						.printToGui("Facilitator Protocol Thread Over for : "
								+ client.getName());
				client.getSocket().close();

			} catch (SocketException se) {
				facilitator.printToGui("Connection Closed");
			} catch (Exception e) {
				facilitator.printToGui("Connection Closed");
				e.printStackTrace();
			}

		}

		private char readChar() throws Exception {
			char temp = ' ';
			while (Character.isWhitespace(temp)) {
				temp = (char) line.charAt(i++);
				if (temp == '\0')
					return ' ';
			}

			return temp;

		}
	}

	public void addAIClient(ConnectedPlayer cp) {
		clients.add(cp);
	}
}

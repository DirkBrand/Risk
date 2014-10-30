package risk.aiplayers;

import java.net.SocketException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import risk.commonObjects.GameState;
import risk.commonObjects.Player;
import risk.commonObjects.Territory;

public class Submissive_AI extends AIPlayer {

	String line = "";
	int ind = 0;

	public static void main(String[] args) {
		String tempName = args[0];

		new Submissive_AI(tempName, null, null, 2);
	}

	public Submissive_AI(String name, String opp, String map, int id) {
		super(SUBMISSIVE_AI, name, opp, map, id);

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

	

	// Recruitment
	@Override
	public void recruitPhase(Collection<Territory> myTerritories,
			int numberOfTroops) {


		LinkedList<String> reply = new LinkedList<String>();

	
			int minID = -1;
			int min = Integer.MAX_VALUE;

			Iterator<Territory> it = game.getCurrentPlayer().getTerritories().values().iterator();
			while (it.hasNext()) {
				Territory t = it.next();
				if (t.getNrTroops() < min) {
					min = t.getNrTroops();
					minID = t.getId();
				}
			}
			Territory minT = game.getCurrentPlayer().getTerritoryByID(minID);

			minT.setNrTroops(minT.getNrTroops() + (numberOfTroops / 2));

			reply.add(minT.getId() + "");
			reply.add(minT.getNrTroops() + "");

			minID = -1;
			min = Integer.MAX_VALUE;

			it = game.getCurrentPlayer().getTerritories().values().iterator();
			while (it.hasNext()) {
				Territory t = it.next();
				if (t.getNrTroops() < min) {
					min = t.getNrTroops();
					minID = t.getId();
				}
			}
			minT = game.getCurrentPlayer().getTerritoryByID(minID);
			minT.setNrTroops(minT.getNrTroops() + (numberOfTroops - numberOfTroops / 2));

			reply.add(minT.getId() + "");
			reply.add(minT.getNrTroops() + "");
		
		APM.sendSuccess(APM.getMesID(), "place_troops", reply);
	}



	// Battle
	public LinkedList<String> getAttackSourceDestination() {
		return new LinkedList<String>();
	}

	

	public boolean attackAgain() {
		return false;
	}

	// Manoeuvre
	public LinkedList<String> getManSourceDestination() {
		int minID = 0;
		int min = Integer.MAX_VALUE;
		int maxID = 0;
		int max = Integer.MIN_VALUE;

		Iterator<Territory> it = game.getCurrentPlayer().getTerritories().values().iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			if (t.getNrTroops() < min) {
				min = t.getNrTroops();
				minID = t.getId();
			}
			if (t.getNrTroops() > max) {
				max = t.getNrTroops();
				maxID = t.getId();
			}
		}

		Territory source = game.getCurrentPlayer().getTerritoryByID(maxID);
		Territory dest = game.getCurrentPlayer().getTerritoryByID(minID);
		int sourceBefore = source.getNrTroops();

		LinkedList<String> reply = new LinkedList<String>();
		if (source.getId() != dest.getId()) {
			int total = source.getNrTroops() + dest.getNrTroops();
			source.setNrTroops((int) (total / 2.0));
			dest.setNrTroops(total - (int) (total / 2.0));

			reply.add(source.getId() + "");
			reply.add(dest.getId() + "");
			reply.add(Math.abs(sourceBefore - source.getNrTroops()) + "");
		}
		return reply;
	}

	@Override
	public LinkedList<String> getMoveAfterAttack() {
		return new LinkedList<String>();
	}


}

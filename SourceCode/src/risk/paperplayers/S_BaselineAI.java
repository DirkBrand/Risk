package risk.paperplayers;

import java.net.SocketException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import risk.aiplayers.AIPlayer;
import risk.commonObjects.GameState;
import risk.commonObjects.Player;
import risk.commonObjects.Territory;

public class S_BaselineAI extends AIPlayer {


	int whereRecruitedId;
	
	String line = "";
	int ind = 0;

	public static void main(String[] args) {
		String tempName = args[0];
		
			new S_BaselineAI(tempName, null, null, 2);
	}

	public S_BaselineAI(String name, String opp, String map, int id) {
		super(BASELINE_AI, name, opp, map, id);
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
				e.printStackTrace();
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

	

	@Override
	public void recruitPhase(Collection<Territory> myTerritories,
			int numberOfTroops) {

		
		LinkedList<String> reply = new LinkedList<String>();

		whereRecruitedId = -1;

		
			int maxID = 0;
			String maxName = "";
			double max = Double.MIN_VALUE;

			Iterator<Territory> it =  game.getCurrentPlayer().getTerritories().values().iterator();
			while (it.hasNext()) {
				Territory t = it.next();
				double sum = 0.0;
				for (Territory n : t.getNeighbours()) {
					if (game.getOtherPlayer().getTerritoryByName(n.getName()) != null) {
						sum += n.getNrTroops();
					}
				}
				if (sum == 0.0)
					continue;
				double ratio = t.getNrTroops() / sum;
				if (ratio > max) {
					max = ratio;
					maxID = t.getId();
					maxName = t.getName();
				}
			}
			whereRecruitedId = maxID;

			Territory source = game.getCurrentPlayer().getTerritoryByName(maxName);
			
			// Resolve the recruitment
			source.setNrTroops(source.getNrTroops() + numberOfTroops);
			reply.add(source.getId() + "");
			reply.add(source.getNrTroops() + "");


		APM.sendSuccess(APM.getMesID(), "place_troops", reply);

	}

	@Override
	public void resolveAttack(int a1, int a2, int a3, int d1, int d2, int sID,
			int dID) {
		Territory s = game.getCurrentPlayer().getTerritoryByID(sID);
		Territory d = game.getOtherPlayer().getTerritoryByID(dID);

		if (d.getNrTroops() == 1 && a3 > d2) { // defender defeated
			whereRecruitedId = d.getId();

			lastAttackSource = s;
			lastAttackDestination = d;
		}
		super.resolveAttack(a1, a2, a3, d1, d2, sID, dID);

	}

	@Override
	public LinkedList<String> getAttackSourceDestination() {
		LinkedList<String> reply = new LinkedList<String>();

		Territory attackSource = game.getCurrentPlayer().getTerritoryByID(
				whereRecruitedId);

		if (attackSource.getNrTroops() > 1) {
			int min = Integer.MAX_VALUE;
			int minId = -1;
			for (Territory t : attackSource.getNeighbours()) {
				if (t.getNrTroops() < min
						&& game.getOtherPlayer().getTerritoryByName(t.getName()) != null) {
					min = t.getNrTroops();
					minId = t.getId();
				}
			}

			if (minId != -1) {
				reply.add(whereRecruitedId + "");
				reply.add(minId + "");
			}
		} else {
			System.err.println("Cannot attack from there!");
		}

		return reply;
	}

	@Override
	public boolean attackAgain() {
		Territory attackSource = game.getCurrentPlayer().getTerritoryByID(
				whereRecruitedId);
		if (attackSource.getNrTroops() <= 1)
			return false;

		int min = Integer.MAX_VALUE;
		int minId = -1;
		for (Territory t : attackSource.getNeighbours()) {
			if (t.getNrTroops() < min
					&& game.getOtherPlayer().getTerritoryByName(t.getName()) != null) {
				min = t.getNrTroops();
				minId = t.getId();
			}
		}
		if (minId == -1)
			return false;

		return true;
	}

	@Override
	public LinkedList<String> getMoveAfterAttack() {
		LinkedList<String> reply = new LinkedList<String>();
		reply.add(lastAttackSource.getId() + "");
		reply.add(lastAttackDestination.getId() + "");
		reply.add((lastAttackSource.getNrTroops() - 1) + "");

		return reply;
	}

	// Manoeuvre
	@Override
	public LinkedList<String> getManSourceDestination() {
		String minID = "";
		int min = Integer.MAX_VALUE;
		String maxID = "";
		int max = Integer.MIN_VALUE;

		Iterator<Territory> it =  game.getCurrentPlayer().getTerritories().values().iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			if (t.getNrTroops() > max) {
				max = t.getNrTroops();
				maxID = t.getName();
			}
		}
		Territory source = game.getCurrentPlayer().getTerritoryByName(maxID);

		it =  game.getCurrentPlayer().getTerritories().values().iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			if (t.getNrTroops() < min
					&& t.connectedRegion == source.connectedRegion) {
				min = t.getNrTroops();
				minID = t.getName();
			}
		}

		if (minID.length() == 0) {
			LinkedList<String> reply = new LinkedList<String>();
			return reply;
		}
		Territory dest = game.getCurrentPlayer().getTerritoryByName(minID);

		int sourceBefore = source.getNrTroops();

		LinkedList<String> reply = new LinkedList<String>();
		if (source.getId() != dest.getId()) {
			int total = source.getNrTroops() + dest.getNrTroops();
			source.setNrTroops((int)(total / 2.0));
			dest.setNrTroops(total - (int)(total / 2.0));

			reply.add(source.getId() + "");
			reply.add(dest.getId() + "");
			reply.add(Math.abs(sourceBefore - source.getNrTroops()) + "");
		}
		return reply;
	}

	
}

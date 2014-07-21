package risk.aiplayers.util;

import java.util.HashMap;
import java.util.Iterator;

import risk.aiplayers.AIPlayer;
import risk.commonObjects.GameState;
import risk.commonObjects.Territory;

public abstract class GameTreeNode implements Cloneable, Comparable<GameTreeNode>{

	private GameState game;
	private int treePhase;
	private boolean maxPlayer;
	private long hashCode = 0L; // Keeping it in memory since children nodes should be using it.
	private double value;

	// Recruitment Phase
	private int recruitedNumber;

	// Attack Phase
	private String attackSource;
	private String attackDest;
	private int[] diceRolls;
	private boolean moveReq = false;

	//Manoeuvring
	private String manSourceID;
	private String manDestID;
	private String manTroopCount;


	public static final int RECRUIT = 0;
	public static final int ATTACK = 1;
	public static final int MOVEAFTERATTACK = 2;
	public static final int RANDOMEVENT = 3;
	public static final int MANOEUVRE = 4;

	public GameTreeNode() {
	}

	// Clone method
	@Override
	public GameTreeNode clone() {
		try {
			GameTreeNode copy = (GameTreeNode) super.clone();

			GameState gameC = game.clone();

			copy.setHash(0L);
			copy.setGame(gameC);
			copy.setValue(0);
			copy.setAttackSource(attackSource);
			copy.setAttackDest(attackDest);

			return copy;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public int compareTo(GameTreeNode node) {
		double val1 = this.value;
		double val2 = node.value;
		return Double.compare(val2, val1);
	}

	public long getHash() {
		if(this.hashCode != 0L) {
			return this.hashCode;
		}
		System.out.println("I am not really supposed to pass here more than once. Maybe twice");
		long key = 0;
		Iterator<Territory> It = this.game.getPlayers().get(0).getTerritories().values().iterator();
		while (It.hasNext()) {
			Territory t = It.next();
			int troopNumber = Math.min(t.getNrTroops(), 49);
			long za = AIPlayer.ZobristArray[t.getId()-1][troopNumber][0]; // Id starts at 1.
			key = key ^ za;
		}
		It = this.game.getPlayers().get(1).getTerritories().values().iterator();
		while (It.hasNext()) {
			Territory t = It.next();
			int troopNumber = Math.min(t.getNrTroops(), 49);
			long za = AIPlayer.ZobristArray[t.getId()-1][troopNumber][1];
			key = key ^ za;
		}

		key = key ^ AIPlayer.ZobristPlayerFactor[this.game.getCurrentPlayerID()];
		key = key ^ AIPlayer.ZobristPhaseFactor[this.getTreePhase()];

		/* If it is a phase coming from attack : we have to know AttackSource and Destination to distinguish
		 * between multiple attack possibilities. The thing is, if there actually is an AttackSource and Destination
		 * it can only means that the current phase is RandomEvent - attack in progress.
		 */
		//TODO : fix this. NullPointerException raised. Thing is : according to my predictions, I only need to hash totally on a new tree root. - Recruit, right ?
		// soooo, it should be alright when everything will be on the same page.
		if(this.getTreePhase() == RANDOMEVENT) {
			key = key ^ AIPlayer.ZobristAttackDestination[this.game.getOtherPlayer().getTerritoryByName(this.getAttackDest()).getId()];
			key = key ^ AIPlayer.ZobristAttackSource[this.game.getCurrentPlayer().getTerritoryByName(this.getAttackSource()).getId()];
		}

		this.setHash(key);
		return key;	
	}
	//TODO : Fix this issue with territory IDs going from 1 to territories.length()
	/*
	 * Sets the child's hashcode using the parent's.
	 */
	public void updateHash(GameTreeNode parent) {
		if(this.hashCode == 0L) {
			long childHash = parent.getHash();
			int playerId = parent.game.getCurrentPlayerID();
			switch(parent.getTreePhase()) {
			//Child in ATTACK
			case(RECRUIT): {
				Iterator<Territory> Itp = parent.game.getCurrentPlayer().getTerritories().values().iterator();
				Iterator<Territory> Itc = this.game.getCurrentPlayer().getTerritories().values().iterator();
				while (Itp.hasNext() && Itc.hasNext()) {
					Territory tp = Itp.next();
					Territory tc = Itc.next();
					int troopP = Math.min(tp.getNrTroops(), 49);
					int troopC = Math.min(tc.getNrTroops(), 49);
					if(troopP != troopC) {
						childHash = childHash ^ AIPlayer.ZobristArray[tp.getId()-1][troopP][playerId];
						childHash = childHash ^ AIPlayer.ZobristArray[tc.getId()-1][troopC][playerId];
					}
				}
				if(Itp.hasNext() || Itc.hasNext()) {
					System.out.println("What happened in recruit.. - different territories");
				}
				break;
			}
			//Child in RE or MANOEUVRE
			case(ATTACK): {
				if(this.getTreePhase() == RANDOMEVENT) {
					Territory Source = this.game.getCurrentPlayer().getTerritoryByName(this.getAttackSource());
					Territory Dest = this.game.getOtherPlayer().getTerritoryByName(this.getAttackDest());
					int destId = Dest.getId();
					int sourceId = Source.getId();
					childHash = childHash ^ AIPlayer.ZobristAttackDestination[destId-1];
					childHash = childHash ^ AIPlayer.ZobristAttackSource[sourceId-1];			
				}
				else if(this.getTreePhase() == MANOEUVRE) {
					// Nothing to do here ? - no AttackDest|Source
				}
				else System.out.println("Little John is looking for his mom TreePhase");
				break;
			}
			//Child in ATTACK or MOA
			case(RANDOMEVENT): {
				Territory Source = this.game.getCurrentPlayer().getTerritoryByName(parent.getAttackSource());
				// Still other player's territory
				if(this.getTreePhase() == ATTACK) {
					Territory Dest = this.game.getOtherPlayer().getTerritoryByName(parent.getAttackDest());
					int destId = Dest.getId();
					int troopP = Math.min(parent.game.getOtherPlayer().getTerritoryByID(destId).getNrTroops(), 49);
					int troopC = Math.min(this.game.getOtherPlayer().getTerritoryByID(destId).getNrTroops(),  49);
					childHash = childHash ^ AIPlayer.ZobristArray[destId-1][troopP][this.game.getOtherPlayer().getId()];
					childHash = childHash ^ AIPlayer.ZobristArray[destId-1][troopC][this.game.getOtherPlayer().getId()];

					//XOR Out AttackDest - not needed anymore
					childHash = childHash ^ AIPlayer.ZobristAttackDestination[destId-1];
				}
				//OtherPlayer lost this territory
				else if(this.getTreePhase() == MOVEAFTERATTACK) {
					Territory Dest = this.game.getCurrentPlayer().getTerritoryByName(this.getAttackDest());
					int destId = Dest.getId();
					int troopP = Math.min(parent.game.getOtherPlayer().getTerritoryByID(destId).getNrTroops(), 49);
					int troopC = Math.min(this.game.getCurrentPlayer().getTerritoryByID(destId).getNrTroops(),  49);
					childHash = childHash ^ AIPlayer.ZobristArray[destId-1][troopP][this.game.getOtherPlayer().getId()];
					childHash = childHash ^ AIPlayer.ZobristArray[destId-1][troopC][playerId];		

					//XOR Out AttackDest - not needed anymore
					childHash = childHash ^ AIPlayer.ZobristAttackDestination[destId-1];
				}
				else {
					System.out.println("Little John is looking for his mom TreePhase");
				}
				//Treating Source Territory
				int sourceId = Source.getId();
				int troopPsource = parent.game.getCurrentPlayer().getTerritoryByID(sourceId).getNrTroops();
				int troopCsource = this.game.getCurrentPlayer().getTerritoryByID(sourceId).getNrTroops();
				childHash = childHash ^ AIPlayer.ZobristArray[sourceId-1][troopPsource][playerId];
				childHash = childHash ^ AIPlayer.ZobristArray[sourceId-1][troopCsource][playerId];			

				//XOR Out AttackSource - not needed anymore //TODO: Keep an eye on this. Are these well removed ?
				childHash = childHash ^ AIPlayer.ZobristAttackSource[sourceId-1];
				break;
			}
			//Child in ATTACK
			case(MOVEAFTERATTACK): {//TODO : Clear this thing. Is there a faster way ?
				Iterator<Territory> Itp = parent.game.getCurrentPlayer().getTerritories().values().iterator();
				Iterator<Territory> Itc = this.game.getCurrentPlayer().getTerritories().values().iterator();
				while (Itp.hasNext() && Itc.hasNext()) {
					Territory tp = Itp.next();
					Territory tc = Itc.next();
					int troopP = Math.min(tp.getNrTroops(), 49);
					int troopC = Math.min(tc.getNrTroops(), 49);
					if(troopP != troopC) {
						childHash = childHash ^ AIPlayer.ZobristArray[tp.getId()-1][troopP][playerId];
						childHash = childHash ^ AIPlayer.ZobristArray[tc.getId()-1][troopC][playerId];
					}
				}
				if(Itp.hasNext() || Itc.hasNext()) {
					System.out.println("What happened in MoveAfterAttack.. - different territories");
				}
				break;
			}
			//Child in RECRUIT - Other player.
			case(MANOEUVRE): {
				//No manoeuvre this turn.
				if(parent.getManSourceID()==null && parent.getManDestID() == null)
					break;
				else {
					Territory manSourceChild = this.game.getCurrentPlayer().getTerritoryByName(parent.getManSourceID());
					Territory manDestChild = this.game.getCurrentPlayer().getTerritoryByName(parent.getManDestID());

					Territory manSourceParent = parent.game.getCurrentPlayer().getTerritoryByName(parent.getManSourceID());
					Territory manDestParent = parent.game.getCurrentPlayer().getTerritoryByName(parent.getManDestID());
					
					int manDestCTroops = Math.min(manDestChild.getNrTroops(), 49);
					int manSrcCTroops = Math.min(manSourceChild.getNrTroops(), 49);
					int manDestPTroops = Math.min(manDestParent.getNrTroops(), 49);
					int manSrcPTroops = Math.min(manSourceParent.getNrTroops(), 49);

					childHash = childHash ^ AIPlayer.ZobristArray[manDestChild.getId()-1][manDestCTroops][playerId];
					childHash = childHash ^ AIPlayer.ZobristArray[manSourceChild.getId()-1][manSrcCTroops][playerId];

					childHash = childHash ^ AIPlayer.ZobristArray[manDestParent.getId()-1][manDestPTroops][playerId];
					childHash = childHash ^ AIPlayer.ZobristArray[manSourceParent.getId()-1][manSrcPTroops][playerId];

					//Changing player in hashcode
					childHash = childHash ^ AIPlayer.ZobristPlayerFactor[playerId];
					childHash = childHash ^ AIPlayer.ZobristPlayerFactor[this.game.getCurrentPlayerID()];
				}
				break;
			}
			default: {
				System.out.println("WTF WTF - Speciale Cassdedi TMTC - Non existant game phase in updateHash");
				break;	
			}
			}
			// Changing game phase in hashcode
			childHash = childHash ^ AIPlayer.ZobristPhaseFactor[parent.getTreePhase()];
			childHash = childHash ^ AIPlayer.ZobristPhaseFactor[this.getTreePhase()];
			this.setHash(childHash);
		}
		else {
			System.out.println("Well Sir, you are trying to create a hashcode that we already know: " + this.hashCode);
		}
	}

	// Getters & Setters
	public GameState getGame() {
		return game;
	}

	public void setGame(GameState game) {
		this.game = game;
	}

	public void setHash(long hash) {
		this.hashCode = hash;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public int getTreePhase() {
		return treePhase;
	}

	public String getTreePhaseText() {
		switch (this.getTreePhase())  {
		case RECRUIT: {
			return "RECRUIT";
		}
		case ATTACK: {
			return "ATTACK";			
		}
		case MOVEAFTERATTACK: {
			return "MOVE_AFTER_ATTACK";			
		}
		case RANDOMEVENT: {
			return "RANDOM_EVENT";			
		}
		case MANOEUVRE: {
			return "MANOEUVRE";			
		}
		}
		return null;
	}

	public void setTreePhase(int treePhase) {
		this.treePhase = treePhase;
	}

	public boolean isMaxPlayer() {
		return maxPlayer;
	}

	public void setMaxPlayer(boolean maxPlayer) {
		this.maxPlayer = maxPlayer;
	}

	public void switchMaxPlayer() {
		this.maxPlayer = !this.maxPlayer;
	}

	public int[] getDiceRolls() {
		return diceRolls;
	}

	public void setDiceRolls(int a1, int a2, int a3, int d1, int d2) {
		diceRolls = new int[] { a1, a2, a3, d1, d2 };
	}

	public int getRecruitedNumber() {
		return recruitedNumber;
	}

	public void setRecruitedNumber(int recruitedNumber) {
		this.recruitedNumber = recruitedNumber;
	}

	public String getAttackSource() {
		return attackSource;
	}

	public void setAttackSource(String attackSource) {
		this.attackSource = attackSource;
	}

	public String getAttackDest() {
		return attackDest;
	}

	public void setAttackDest(String attackDest) {
		this.attackDest = attackDest;
	}

	public boolean moveRequired() {
		return moveReq;
	}

	public void setMoveReq(boolean moveReq) {
		this.moveReq = moveReq;
	}

	public String getManSourceID() {
		return manSourceID;
	}

	public void setManSourceID(String manSourceID) {
		this.manSourceID = manSourceID;
	}

	public String getManTroopCount() {
		return manTroopCount;
	}

	public void setManTroopCount(String manTroopCount) {
		this.manTroopCount = manTroopCount;
	}

	public String getManDestID() {
		return manDestID;
	}

	public void setManDestID(String manDestID) {
		this.manDestID = manDestID;
	}
}

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
	
	public static final int reasonableChildrenNumber = 40;

	public GameTreeNode() {
	}

	// Clone method
	@Override
	public GameTreeNode clone() {
		try {
			GameTreeNode copy = (GameTreeNode) super.clone();

			GameState gameC = game.clone();
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
	
	public long getHash() {
		long key = 0;
		Iterator<Territory> It = this.getGame().getPlayers().get(0).getTerritories().values().iterator();
		while (It.hasNext()) {
			Territory t = It.next();
			int troopNumber = Math.min(t.getNrTroops(), 49);
			long za = AIPlayer.ZobristArray[t.getId()-1][troopNumber][0]; // Id starts at 1.
			key = key ^ za;
		}
		It = this.getGame().getPlayers().get(1).getTerritories().values().iterator();
		while (It.hasNext()) {
			Territory t = It.next();
			int troopNumber = Math.min(t.getNrTroops(), 49);
			long za = AIPlayer.ZobristArray[t.getId()-1][troopNumber][1];
			key = key ^ za;
		}

		key = key ^ AIPlayer.ZobristPlayerFactor[this.getGame().getCurrentPlayerID()];
		key = key ^ AIPlayer.ZobristPhaseFactor[this.getTreePhase()];

		/* If it is a phase coming from attack : we have to know AttackSource and Destination to distinguish
		 * between multiple attack possibilities. The thing is, if there actually is an AttackSource and Destination
		 * it can only means that the current phase is RandomEvent - attack in progress.
		 */
		//TODO : fix this. NullPointerException raised. Thing is : according to my predictions, I only need to hash totally on a new tree root. - Recruit, right ?
		// soooo, it should be alright when everything will be on the same page.
//		if(this.getTreePhase() == RANDOMEVENT) {
//			System.out.println("Unusual");
//			key = key ^ AIPlayer.ZobristAttackDestination[this.game.getOtherPlayer().getTerritoryByName(this.getAttackDest()).getId()];
//			key = key ^ AIPlayer.ZobristAttackSource[this.game.getCurrentPlayer().getTerritoryByName(this.getAttackSource()).getId()];
//		}

		return key;	
	}

	@Override
	public int compareTo(GameTreeNode node) {
		double val1 = this.value;
		double val2 = node.value;
		return Double.compare(val2, val1);
	}

	// Getters & Setters
	public GameState getGame() {
		return game;
	}

	public void setGame(GameState game) {
		this.game = game;
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

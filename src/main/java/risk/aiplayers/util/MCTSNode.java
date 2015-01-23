package risk.aiplayers.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import risk.aiplayers.AIPlayer;
import risk.commonObjects.Territory;

public class MCTSNode extends GameTreeNode implements Cloneable {

	// For MCTS
	private long hashCode = 0L; // Keeping it in memory since children nodes should be using it.
	public boolean Momentum = false; 
	private List<MCTSNode> children;
	private MCTSNode parent;
	private int visitCount;
	private int winCount;
	private int maxChildren = -1; // Indicates not yet calculated

	private double randomNodeExpectedWinRate;

	public int depth;


	// Recruiting
	private Territory recruitedTer;
	public int  whereRecruitedId = -1;
	public double ave; // average ?
	public int numberOfRecruitBranches = 0;
	public ArrayList<MCTSNode> recruitChildren = null;
	public PriorityQueue<MCTSNode> recruitQueue = null;

	// Attacking
	public boolean noAttackAdded = false;
	public int numberOfAttackBranches = 0;
	private int moveAfterAttackCount = -1;
	public ArrayList<MCTSNode> attackChildren = null;
	public PriorityQueue<MCTSNode> attackQueue = null;

	//MovingAfterConquest
	public PriorityQueue<MCTSNode> MoAQueue = null;
	
	// Manoeuvering
	public boolean noManAdded = false;
	private Territory manSource;
	private Territory manDest;
	public int numberOfManoeuvreBranches = 0;
	public PriorityQueue<MCTSNode> manQueue = null;
	public ArrayList<MCTSNode> manChildren = null;
	public ArrayList<Territory> manSources = null;
	public ArrayList<Territory> manDests = null;
	public ArrayList<Integer> manTroopBins; //Each element in this represent a possible manoeuvre. A to B with alpha troops.
	private LinkedList<LinkedList<Territory>> connComponentBuckets;

	public MCTSNode() {
		super();
	}

	// Clone method
	@Override
	public MCTSNode clone() {
//		try {
		MCTSNode copy = (MCTSNode) super.clone();
		if (recruitedTer != null) {
			Territory temp = recruitedTer.clone();
			copy.setRecruitedTer(temp);
		}

		if (manSource != null) {
			Territory tempManS = manSource.clone();
			copy.setManSource(tempManS);
		}
		if (manDest != null) {
			Territory tempManD = manDest.clone();
			copy.setManDest(tempManD);
		}
		
		if(this.getTreePhase() != RECRUIT) { //TODO:manSrc and Dest is kept otherwise and bring errors. ??
			copy.setManDest(null);
			copy.setManSource(null);
		}
			
		copy.setHash(0L);
		copy.whereRecruitedId = whereRecruitedId;
		copy.numberOfAttackBranches = 0;
		copy.numberOfManoeuvreBranches = 0;
		copy.maxChildren = 0;
		copy.numberOfRecruitBranches = 0;
		copy.recruitQueue = null;
		copy.recruitChildren = null;
		copy.attackQueue = null;
		copy.attackChildren = null;
		copy.MoAQueue = null;
		copy.manQueue = null;
		copy.manChildren = null;
		copy.manSources = null;
		copy.manDests = null;
		copy.manTroopBins = null;
		copy.noManAdded = false;
		copy.noAttackAdded = false;
		
		return copy;
//		}
//		catch(OutOfMemoryError o) {
//			System.out.println("Calm your tits man !");
//			return null; //TODO: Global boolean StopExpanding ?
//		}
	}

	// Manually set the maximum number of children - use with caution!
	// TODO: Get rid of this entirely if possible - better way is
	// to put the maxChildren for each node type into it's own NodeHandler class?
	public void setMaxChildren(int i) {
		maxChildren = i;
	}
	
	private void setMaxChildrenRecruit() {
		int troops = AIUtil.calculateRecruitedTroops(this);
		int territories = getGame().getCurrentPlayer().getTerritories().size();
		int maxChildren = (int) (AIUtil.nCk(troops + territories - 1, troops));
		// TODO/FIXME: This might be a bug - can there be overflow with positive count resulting?
		if (maxChildren < 0) maxChildren = Integer.MAX_VALUE;
	}
	
	private void setMaxChildrenAttack() {
		maxChildren = 1;
		// TODO: shouldn't this be set to true?
		noAttackAdded = false;
		Iterator<Territory> it = getGame().getCurrentPlayer()
				.getTerritories().values().iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			// Only consider fortified territories
			if (t.getNrTroops() > 1) {
				for (Territory n : t.getNeighbours()) {
					Territory tempT = getGame().getOtherPlayer()
							.getTerritoryByName(n.getName());
					// Only consider targets that have less troops than
					// the source - seems like a TODO, but here
					// is perhaps not the right place for it
					if (tempT != null) {
						maxChildren++;
					}
				}
			}
		}
	}

	private void setMaxChildrenMoveAfterAttack() {
		maxChildren = getGame().getCurrentPlayer()
				.getTerritoryByName(
						getParent().getAttackSource())
				.getNrTroops() - 1;
		assert maxChildren != 0;
	}

	private void setMaxChildrenRandomEvent() {
		int sourceTroops = getGame().getCurrentPlayer()
				.getTerritoryByName(getAttackSource())
				.getNrTroops();
		int destTroops = getGame().getOtherPlayer()
				.getTerritoryByName(getAttackDest())
				.getNrTroops();

		if (sourceTroops == 2) {
			maxChildren = 2;
		} else if (sourceTroops == 3) {
			if (destTroops == 2 || destTroops == 1) {
				maxChildren = 2;
			} else {
				maxChildren = 3;
			}
		} else {
			if (destTroops == 2 || destTroops == 1) {
				maxChildren = 2;
			} else {
				maxChildren = 3;
			}
		}
	
	}
	
	private void setMaxChildrenManouevre() {
		int size = AIUtil.updateRegions(getGame());
		// Create list of connected components
		setConnComponentBuckets(new LinkedList<LinkedList<Territory>>());
		for (int i = 0; i < size; i++) {
			getConnComponentBuckets().add(
					new LinkedList<Territory>());
		}

		// Since not manoeuvring is an option
		maxChildren = 1;

		Iterator<Territory> it = getGame().getCurrentPlayer()
				.getTerritories().values().iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			getConnComponentBuckets().get(t.connectedRegion).add(t);
		}

		for (LinkedList<Territory> bucket : getConnComponentBuckets()) {
			if (bucket.size() > 1) {
				for (Territory src : bucket) {
					if (src.getNrTroops() > 1) {
						for (Territory dest : bucket) {
							if (!src.getName().equals(dest.getName())) {
								// Unique source-dest combo
								maxChildren += (src.getNrTroops() - 1);
							}
						}
					}
				}
			}
		}

	}
	// Calculate actual maximum number of children according to our rules
	// For artificially limiting the number, one can use setMaxChildren
	public int maxChildren() {
		if (maxChildren < 0) {
			// Set the maxChildren variable appropriately
			// TODO: Replace this switch by appropriate method overloading on type of Node/NodeHandler
			switch (getTreePhase()) {
			case GameTreeNode.RECRUIT: {
				setMaxChildrenRecruit();
				break;
			}
			case GameTreeNode.ATTACK: {
				setMaxChildrenAttack();
				break;
			}
			case GameTreeNode.MOVEAFTERATTACK: {
				setMaxChildrenMoveAfterAttack();
				break;
			}
			case GameTreeNode.RANDOMEVENT: {
				setMaxChildrenRandomEvent();
				break;
			}
			case GameTreeNode.MANOEUVRE: {
				setMaxChildrenManouevre();
				break;
			}
			}
		}
		return maxChildren;
	}

	@Override
	public long getHash() {
		if(this.hashCode != 0L) {
			return this.hashCode;
		}
		long key = 0;
		Iterator<Territory> It = this.getGame().getPlayers().get(0).getTerritories().values().iterator();
		while (It.hasNext()) {
			Territory t = It.next();
			int troopNumber = Math.min(t.getNrTroops(), AIPlayer.MAX_TROOPS);
			long za = AIPlayer.ZobristArray[t.getId()-1][troopNumber][0]; // Id starts at 1.
			key = key ^ za;
		}
		It = this.getGame().getPlayers().get(1).getTerritories().values().iterator();
		while (It.hasNext()) {
			Territory t = It.next();
			int troopNumber = Math.min(t.getNrTroops(), AIPlayer.MAX_TROOPS);
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
		// so, it should be all right when everything will be on the same page.
//		if(this.getTreePhase() == RANDOMEVENT) {
//			System.out.println("Unusual");
//			key = key ^ AIPlayer.ZobristAttackDestination[this.game.getOtherPlayer().getTerritoryByName(this.getAttackDest()).getId()];
//			key = key ^ AIPlayer.ZobristAttackSource[this.game.getCurrentPlayer().getTerritoryByName(this.getAttackSource()).getId()];
//		}

		this.setHash(key);
		return key;	
	}


	//TODO : Fix this issue with territory IDs going from 1 to territories.length()
	/**
	 * Sets the child's hashcode using the parent's.
	 * @param parent
	 *            The parent node from which the hash is updated.
	 */
	public void updateHash(MCTSNode parent) {
		if(this.hashCode == 0L && parent != null) {
			long childHash = parent.getHash();
			int playerId = parent.getGame().getCurrentPlayerID();
			switch(parent.getTreePhase()) {
			//Child in ATTACK
			case(RECRUIT): {
				Iterator<Territory> Itp = parent.getGame().getCurrentPlayer().getTerritories().values().iterator();
				Iterator<Territory> Itc = this.getGame().getCurrentPlayer().getTerritories().values().iterator();
				while (Itp.hasNext() && Itc.hasNext()) {
					Territory tp = Itp.next();
					Territory tc = Itc.next();
					int troopP = Math.min(tp.getNrTroops(), AIPlayer.MAX_TROOPS);
					int troopC = Math.min(tc.getNrTroops(), AIPlayer.MAX_TROOPS);
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
					Territory Source = this.getGame().getCurrentPlayer().getTerritoryByName(this.getAttackSource());
					Territory Dest = this.getGame().getOtherPlayer().getTerritoryByName(this.getAttackDest());
					int sourceId = Source.getId();
					int destId = Dest.getId();
					childHash = childHash ^ AIPlayer.ZobristAttackDestination[destId-1];
					childHash = childHash ^ AIPlayer.ZobristAttackSource[sourceId-1];			
				}
				else if(this.getTreePhase() == MANOEUVRE) {
					// Nothing to do here ? - no AttackDest|Source
				}
				else System.out.println("Little John is looking for his mom TreePhase " + this.getTreePhaseText() + " " + parent.getHash());
				break;
			}
			//Child in ATTACK or MOA
			case(RANDOMEVENT): {
				Territory Source = this.getGame().getCurrentPlayer().getTerritoryByName(parent.getAttackSource());
				// Still other player's territory
				if(this.getTreePhase() == ATTACK) {
					Territory Dest = this.getGame().getOtherPlayer().getTerritoryByName(parent.getAttackDest());
					int destId = Dest.getId();
					int troopP = Math.min(parent.getGame().getOtherPlayer().getTerritoryByID(destId).getNrTroops(), AIPlayer.MAX_TROOPS);
					int troopC = Math.min(this.getGame().getOtherPlayer().getTerritoryByID(destId).getNrTroops(),  AIPlayer.MAX_TROOPS);
					childHash = childHash ^ AIPlayer.ZobristArray[destId-1][troopP][this.getGame().getOtherPlayer().getId()];
					childHash = childHash ^ AIPlayer.ZobristArray[destId-1][troopC][this.getGame().getOtherPlayer().getId()];

					//XOR Out AttackDest - not needed anymore
					childHash = childHash ^ AIPlayer.ZobristAttackDestination[destId-1];
				}
				//OtherPlayer lost this territory
				else if(this.getTreePhase() == MOVEAFTERATTACK) {
					Territory Dest = this.getGame().getCurrentPlayer().getTerritoryByName(this.getAttackDest());
					int destId = Dest.getId();
					int troopP = Math.min(parent.getGame().getOtherPlayer().getTerritoryByID(destId).getNrTroops(), AIPlayer.MAX_TROOPS);
					int troopC = Math.min(this.getGame().getCurrentPlayer().getTerritoryByID(destId).getNrTroops(),  AIPlayer.MAX_TROOPS);
					childHash = childHash ^ AIPlayer.ZobristArray[destId-1][troopP][this.getGame().getOtherPlayer().getId()];
					childHash = childHash ^ AIPlayer.ZobristArray[destId-1][troopC][playerId];		

					//XOR Out AttackDest - not needed anymore
					childHash = childHash ^ AIPlayer.ZobristAttackDestination[destId-1];
				}
				else {
					System.out.println("Little John is looking for his mom TreePhase");
				}
				//Treating Source Territory
				int sourceId = Source.getId();
				int troopPsource = Math.min(parent.getGame().getCurrentPlayer().getTerritoryByID(sourceId).getNrTroops(), AIPlayer.MAX_TROOPS);
				int troopCsource = Math.min(this.getGame().getCurrentPlayer().getTerritoryByID(sourceId).getNrTroops(), AIPlayer.MAX_TROOPS);
				childHash = childHash ^ AIPlayer.ZobristArray[sourceId-1][troopPsource][playerId];
				childHash = childHash ^ AIPlayer.ZobristArray[sourceId-1][troopCsource][playerId];			

				//XOR Out AttackSource - not needed anymore
				childHash = childHash ^ AIPlayer.ZobristAttackSource[sourceId-1];
				break;
			}
			//Child in ATTACK
			case(MOVEAFTERATTACK): {//TODO : Clear this thing. Is there a faster way ?
				Iterator<Territory> Itp = parent.getGame().getCurrentPlayer().getTerritories().values().iterator();
				Iterator<Territory> Itc = this.getGame().getCurrentPlayer().getTerritories().values().iterator();
				while (Itp.hasNext() && Itc.hasNext()) {
					Territory tp = Itp.next();
					Territory tc = Itc.next();
					int troopP = Math.min(tp.getNrTroops(), AIPlayer.MAX_TROOPS);
					int troopC = Math.min(tc.getNrTroops(), AIPlayer.MAX_TROOPS);
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
				if(this.getManSource()==null && this.getManDest() == null) {
					//Changing player in hashcode
					childHash = childHash ^ AIPlayer.ZobristPlayerFactor[playerId];
					childHash = childHash ^ AIPlayer.ZobristPlayerFactor[this.getGame().getCurrentPlayerID()];
					break;
				} else if(this.getManSource() == null || this.getManDest() == null) {
					System.out.println("FATAL PROBLEMATIC ERROR. We have a problem here Sir - Manoeuvre - Source OR Dest is null");
					System.out.println("Debug Man Source " + this.getManSource() + " Dest " + this.getManDest());
					System.out.println("Debug Man2 players: " + this.getGame().getCurrentPlayerID() + " " + parent.getGame().getCurrentPlayerID());
					
				} else {
					try{
					Territory manSourceChild = this.getGame().getOtherPlayer().getTerritoryByName(this.getManSource().getName());
					Territory manDestChild = this.getGame().getOtherPlayer().getTerritoryByName(this.getManDest().getName());

					Territory manSourceParent = parent.getGame().getCurrentPlayer().getTerritoryByName(this.getManSource().getName());
					Territory manDestParent = parent.getGame().getCurrentPlayer().getTerritoryByName(this.getManDest().getName());

					int manDestCTroops = Math.min(manDestChild.getNrTroops(), AIPlayer.MAX_TROOPS);
					int manSrcCTroops = Math.min(manSourceChild.getNrTroops(), AIPlayer.MAX_TROOPS);
					int manDestPTroops = Math.min(manDestParent.getNrTroops(), AIPlayer.MAX_TROOPS);
					int manSrcPTroops = Math.min(manSourceParent.getNrTroops(), AIPlayer.MAX_TROOPS);

					childHash = childHash ^ AIPlayer.ZobristArray[manDestChild.getId()-1][manDestCTroops][playerId];
					childHash = childHash ^ AIPlayer.ZobristArray[manSourceChild.getId()-1][manSrcCTroops][playerId];

					childHash = childHash ^ AIPlayer.ZobristArray[manDestParent.getId()-1][manDestPTroops][playerId];
					childHash = childHash ^ AIPlayer.ZobristArray[manSourceParent.getId()-1][manSrcPTroops][playerId];

					//Changing player in hashcode
					childHash = childHash ^ AIPlayer.ZobristPlayerFactor[playerId];
					childHash = childHash ^ AIPlayer.ZobristPlayerFactor[this.getGame().getCurrentPlayerID()];
					}
					catch(Exception e){
						System.out.println("Here2 ! " + parent.getHash());
						System.out.println(this.getGame().getOtherPlayer().getTerritoryByName(this.getManSource().getName()) + " " 
								+ this.getGame().getOtherPlayer().getTerritoryByName(this.getManDest().getName()) + " " 
								+ parent.getGame().getCurrentPlayer().getTerritoryByName(this.getManSource().getName()) + " " 
								+ parent.getGame().getCurrentPlayer().getTerritoryByName(this.getManDest().getName()) + " "
								+ this.getManSource() + " " + this.getManDest() + " " + this.getManTroopCount() + " " 
								+ parent.getGame().getCurrentPlayer().getName()
								+ " Parent maxC " + parent.maxChildren);
						System.out.println(parent.getConnComponentBuckets());
						this.writeGameState("ErrorChild");
						parent.writeGameState("ErrorParent");
						System.exit(1);
					}
				}
				break;
			}
			default: {
				System.out.println("Non existant game phase in updateHash - ERROR4012654");
				break;	
			}
			}
			// Changing game phase in hashcode
			childHash = childHash ^ AIPlayer.ZobristPhaseFactor[parent.getTreePhase()];
			childHash = childHash ^ AIPlayer.ZobristPhaseFactor[this.getTreePhase()];
			this.setHash(childHash);
		}
		else {
			if(parent == null) {
				// System.out.println("Parent null");
			}
			// System.out.println("Well Sir, you are trying to create a hashcode that we already know: " + this.hashCode + " " + this.getTreePhaseText());
		}
	}
	

	public void writeGameState(String message) {
		File log = new File("gameLog" + message  + ".txt");
		int count1 = 0;
		int count2 = 0;
		try {
			FileWriter fileWriter = new FileWriter(log, true);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

			bufferedWriter.write("\n\n<<After " + message + " "
					 + ">>\n\n");

			bufferedWriter.write("\n"
					+ this.getGame().getCurrentPlayer().getName()
					+ " Territories\n\n");
			Iterator<Territory> it = this.getGame().getCurrentPlayer()
					.getTerritories().values().iterator();
			while (it.hasNext()) {
				Territory t = it.next();
				bufferedWriter.write(t.getId() + " " + t.getName() + " - "
						+ t.getNrTroops() + "\n");
				count1 += t.getNrTroops();
			}
			bufferedWriter.write("\n" + count1 + "\n");

			bufferedWriter.write("\n"
					+ this.getGame().getOtherPlayer().getName()
					+ " Territories\n\n");
			it = this.getGame().getOtherPlayer().getTerritories().values()
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

	
	public void setHash(long hash) {
		this.hashCode = hash;
	}
	
	public MCTSNode getParent() {
		return parent;
	}

	public void setParent(MCTSNode parent) {
		this.parent = parent;
	}

	public List<MCTSNode> getChildren() {
		return children;
	}

	public void setChildren(List<MCTSNode> children) {
		this.children = children;
	}

	public int getNumberOfChildren() {
		if (children == null) {
			return 0;
		}
		return children.size();
	}

	public void addChild(MCTSNode child) {
		if (children == null) {
			children = new ArrayList<MCTSNode>();
		}
		children.add(child);
	}

	public void removeChildAt(int index) throws IndexOutOfBoundsException {
		children.remove(index);
	}

	public int getVisitCount() {
		return visitCount;
	}

	public void setVisitCount(int visitCount) {
		this.visitCount = visitCount;
	}

	public int getWinCount() {
		return winCount;
	}

	public void setWinCount(int winCount) {
		this.winCount = winCount;
	}

	public Territory getRecruitedTer() {
		return recruitedTer;
	}

	public void setRecruitedTer(Territory recruitedTer) {
		this.recruitedTer = recruitedTer;
	}

	public Territory getManSource() {
		return manSource;
	}

	public void setManSource(Territory manSource) {
		this.manSource = manSource;
	}

	public Territory getManDest() {
		return manDest;
	}

	public void setManDest(Territory manDest) {
		this.manDest = manDest;
	}

	public int arity() {
		return children == null ? 0 : children.size();
	}

	public double getRandomNodeExpectedWinRate() {
		return randomNodeExpectedWinRate;
	}

	public void setRandomNodeExpectedWinRate(double randomNodeExpectedWinRate) {
		this.randomNodeExpectedWinRate = randomNodeExpectedWinRate;
	}

	public LinkedList<LinkedList<Territory>> getConnComponentBuckets() {
		return connComponentBuckets;
	}

	public void setConnComponentBuckets(
			LinkedList<LinkedList<Territory>> connComponentBuckets) {
		this.connComponentBuckets = connComponentBuckets;
	}

	public int getMoveAfterAttackCount() {
		return moveAfterAttackCount;
	}

	public void setMoveAfterAttackCount(int moveAfterAttackCount) {
		this.moveAfterAttackCount = moveAfterAttackCount;
	}
	
	@Override
	public String toString() {
		return getManSource() + " - " + getManDest() + "";
	}
	}

package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.LinkedList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import risk.commonObjects.AbstractGameState;
import risk.commonObjects.GameState;
import risk.commonObjects.Player;
import risk.commonObjects.Territory;

/**
 * The class <code>AbstractGameStateTest</code> contains tests for the class <code>{@link AbstractGameState}</code>.
 *
 * @generatedBy CodePro at 10/10/13 1:27 PM
 * @author Dirka
 * @version $Revision: 1.0 $
 */
public class AbstractGameStateTest {
	/**
	 * Run the void changeCurrentPlayer() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/10/13 1:27 PM
	 */
	@Test
	public void testChangeCurrentPlayer_1()
		throws Exception {
		LinkedList<Player> players = new LinkedList<Player>();
		players.add(new Player(0, "0"));
		players.add(new Player(1, "1"));
		GameState fixture = new GameState("", players, 1, new HashMap<String, Territory>(), new HashMap<String, Territory>(), 1);

		fixture.setPhase(1);
		fixture.setMapLocation("");
		fixture.setMapName("");
		fixture.setImgLocation("");
		fixture.setCurrentPlayer(1);
		fixture.changeCurrentPlayer();

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
		//       at java.util.LinkedList.checkElementIndex(Unknown Source)
		//       at java.util.LinkedList.get(Unknown Source)
		//       at risk.commonObjects.GameState.<init>(GameState.java:27)
	}

	/**
	 * Run the Player getCurrentPlayer() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/10/13 1:27 PM
	 */
	@Test
	public void testGetCurrentPlayer_1()
		throws Exception {
		LinkedList<Player> players = new LinkedList<Player>();
		players.add(new Player(0, "0"));
		players.add(new Player(1, "1"));
		GameState fixture = new GameState("", players, 1, new HashMap<String, Territory>(), new HashMap<String, Territory>(), 1);

		fixture.setPhase(1);
		fixture.setMapLocation("");
		fixture.setMapName("");
		fixture.setImgLocation("");
		fixture.setCurrentPlayer(1);

		Player result = fixture.getCurrentPlayer();

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
		//       at java.util.LinkedList.checkElementIndex(Unknown Source)
		//       at java.util.LinkedList.get(Unknown Source)
		//       at risk.commonObjects.GameState.<init>(GameState.java:27)
		assertNotNull(result);
	}

	/**
	 * Run the Player getCurrentPlayer() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/10/13 1:27 PM
	 */
	@Test
	public void testGetCurrentPlayer_2()
		throws Exception {
		LinkedList<Player> players = new LinkedList<Player>();
		players.add(new Player(0, "0"));
		players.add(new Player(1, "1"));
		GameState fixture = new GameState("", players, 1, new HashMap<String, Territory>(), new HashMap<String, Territory>(), 1);

		fixture.setPhase(1);
		fixture.setMapLocation("");
		fixture.setMapName("");
		fixture.setImgLocation("");
		fixture.setCurrentPlayer(1);

		Player result = fixture.getCurrentPlayer();

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
		//       at java.util.LinkedList.checkElementIndex(Unknown Source)
		//       at java.util.LinkedList.get(Unknown Source)
		//       at risk.commonObjects.GameState.<init>(GameState.java:27)
		assertNotNull(result);
	}

	/**
	 * Run the Player getCurrentPlayer() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/10/13 1:27 PM
	 */
	@Test
	public void testGetCurrentPlayer_3()
		throws Exception {

		LinkedList<Player> players = new LinkedList<Player>();
		players.add(new Player(0, "0"));
		players.add(new Player(1, "1"));
		GameState fixture = new GameState("", players, 1, new HashMap<String, Territory>(), new HashMap<String, Territory>(), 1);

		fixture.setPhase(1);
		fixture.setMapLocation("");
		fixture.setMapName("");
		fixture.setImgLocation("");
		fixture.setCurrentPlayer(1);

		Player result = fixture.getCurrentPlayer();

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
		//       at java.util.LinkedList.checkElementIndex(Unknown Source)
		//       at java.util.LinkedList.get(Unknown Source)
		//       at risk.commonObjects.GameState.<init>(GameState.java:27)
		assertNotNull(result);
	}

	/**
	 * Run the int getCurrentPlayerID() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/10/13 1:27 PM
	 */
	@Test
	public void testGetCurrentPlayerID_1()
		throws Exception {

		LinkedList<Player> players = new LinkedList<Player>();
		players.add(new Player(0, "0"));
		players.add(new Player(1, "1"));
		GameState fixture = new GameState("", players, 1, new HashMap<String, Territory>(), new HashMap<String, Territory>(), 1);

		fixture.setPhase(1);
		fixture.setMapLocation("");
		fixture.setMapName("");
		fixture.setImgLocation("");
		fixture.setCurrentPlayer(1);
		int result = fixture.getCurrentPlayerID();

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
		//       at java.util.LinkedList.checkElementIndex(Unknown Source)
		//       at java.util.LinkedList.get(Unknown Source)
		//       at risk.commonObjects.GameState.<init>(GameState.java:27)
		assertEquals(1, result);
	}

	/**
	 * Run the String getImgLocation() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/10/13 1:27 PM
	 */
	@Test
	public void testGetImgLocation_1()
		throws Exception {

		LinkedList<Player> players = new LinkedList<Player>();
		players.add(new Player(0, "0"));
		players.add(new Player(1, "1"));
		GameState fixture = new GameState("", players, 1, new HashMap<String, Territory>(), new HashMap<String, Territory>(), 1);

		fixture.setPhase(1);
		fixture.setMapLocation("");
		fixture.setMapName("");
		fixture.setImgLocation("");
		fixture.setCurrentPlayer(1);

		String result = fixture.getImgLocation();

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
		//       at java.util.LinkedList.checkElementIndex(Unknown Source)
		//       at java.util.LinkedList.get(Unknown Source)
		//       at risk.commonObjects.GameState.<init>(GameState.java:27)
		assertNotNull(result);
	}

	/**
	 * Run the String getMapLocation() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/10/13 1:27 PM
	 */
	@Test
	public void testGetMapLocation_1()
		throws Exception {

		LinkedList<Player> players = new LinkedList<Player>();
		players.add(new Player(0, "0"));
		players.add(new Player(1, "1"));
		GameState fixture = new GameState("", players, 1, new HashMap<String, Territory>(), new HashMap<String, Territory>(), 1);
		

		fixture.setPhase(1);
		fixture.setMapLocation("");
		fixture.setMapName("");
		fixture.setImgLocation("");
		fixture.setCurrentPlayer(1);

		String result = fixture.getMapLocation();

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
		//       at java.util.LinkedList.checkElementIndex(Unknown Source)
		//       at java.util.LinkedList.get(Unknown Source)
		//       at risk.commonObjects.GameState.<init>(GameState.java:27)
		assertNotNull(result);
	}

	/**
	 * Run the String getMapName() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/10/13 1:27 PM
	 */
	@Test
	public void testGetMapName_1()
		throws Exception {

		LinkedList<Player> players = new LinkedList<Player>();
		players.add(new Player(0, "0"));
		players.add(new Player(1, "1"));
		GameState fixture = new GameState("", players, 1, new HashMap<String, Territory>(), new HashMap<String, Territory>(), 1);
		

		fixture.setPhase(1);
		fixture.setMapLocation("");
		fixture.setMapName("");
		fixture.setImgLocation("");
		fixture.setCurrentPlayer(1);

		String result = fixture.getMapName();

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
		//       at java.util.LinkedList.checkElementIndex(Unknown Source)
		//       at java.util.LinkedList.get(Unknown Source)
		//       at risk.commonObjects.GameState.<init>(GameState.java:27)
		assertNotNull(result);
	}

	/**
	 * Run the Player getOtherPlayer() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/10/13 1:27 PM
	 */
	@Test
	public void testGetOtherPlayer_1()
		throws Exception {

		LinkedList<Player> players = new LinkedList<Player>();
		players.add(new Player(0, "0"));
		players.add(new Player(1, "1"));
		GameState fixture = new GameState("", players, 1, new HashMap<String, Territory>(), new HashMap<String, Territory>(), 1);
		

		fixture.setPhase(1);
		fixture.setMapLocation("");
		fixture.setMapName("");
		fixture.setImgLocation("");
		fixture.setCurrentPlayer(1);

		Player result = fixture.getOtherPlayer();

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
		//       at java.util.LinkedList.checkElementIndex(Unknown Source)
		//       at java.util.LinkedList.get(Unknown Source)
		//       at risk.commonObjects.GameState.<init>(GameState.java:27)
		assertNotNull(result);
	}

	/**
	 * Run the Player getOtherPlayer() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/10/13 1:27 PM
	 */
	@Test
	public void testGetOtherPlayer_2()
		throws Exception {

		LinkedList<Player> players = new LinkedList<Player>();
		players.add(new Player(0, "0"));
		players.add(new Player(1, "1"));
		GameState fixture = new GameState("", players, 1, new HashMap<String, Territory>(), new HashMap<String, Territory>(), 1);
		

		fixture.setPhase(1);
		fixture.setMapLocation("");
		fixture.setMapName("");
		fixture.setImgLocation("");
		fixture.setCurrentPlayer(1);

		Player result = fixture.getOtherPlayer();

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
		//       at java.util.LinkedList.checkElementIndex(Unknown Source)
		//       at java.util.LinkedList.get(Unknown Source)
		//       at risk.commonObjects.GameState.<init>(GameState.java:27)
		assertNotNull(result);
	}

	/**
	 * Run the Player getOtherPlayer() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/10/13 1:27 PM
	 */
	@Test
	public void testGetOtherPlayer_3()
		throws Exception {

		LinkedList<Player> players = new LinkedList<Player>();
		players.add(new Player(0, "0"));
		players.add(new Player(1, "1"));
		GameState fixture = new GameState("", players, 1, new HashMap<String, Territory>(), new HashMap<String, Territory>(), 1);
		

		fixture.setPhase(1);
		fixture.setMapLocation("");
		fixture.setMapName("");
		fixture.setImgLocation("");
		fixture.setCurrentPlayer(1);

		Player result = fixture.getOtherPlayer();

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
		//       at java.util.LinkedList.checkElementIndex(Unknown Source)
		//       at java.util.LinkedList.get(Unknown Source)
		//       at risk.commonObjects.GameState.<init>(GameState.java:27)
		assertNotNull(result);
	}

	/**
	 * Run the int getOtherPlayerID() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/10/13 1:27 PM
	 */
	@Test
	public void testGetOtherPlayerID_1()
		throws Exception {

		LinkedList<Player> players = new LinkedList<Player>();
		players.add(new Player(0, "0"));
		players.add(new Player(1, "1"));
		GameState fixture = new GameState("", players, 1, new HashMap<String, Territory>(), new HashMap<String, Territory>(), 1);
		

		fixture.setPhase(1);
		fixture.setMapLocation("");
		fixture.setMapName("");
		fixture.setImgLocation("");
		fixture.setCurrentPlayer(1);

		int result = fixture.getOtherPlayerID();

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
		//       at java.util.LinkedList.checkElementIndex(Unknown Source)
		//       at java.util.LinkedList.get(Unknown Source)
		//       at risk.commonObjects.GameState.<init>(GameState.java:27)
		assertEquals(0, result);
	}

	/**
	 * Run the int getPhase() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/10/13 1:27 PM
	 */
	@Test
	public void testGetPhase_1()
		throws Exception {

		LinkedList<Player> players = new LinkedList<Player>();
		players.add(new Player(0, "0"));
		players.add(new Player(1, "1"));
		GameState fixture = new GameState("", players, 1, new HashMap<String, Territory>(), new HashMap<String, Territory>(), 1);

		fixture.setPhase(1);
		fixture.setMapLocation("");
		fixture.setMapName("");
		fixture.setImgLocation("");
		fixture.setCurrentPlayer(1);
		int result = fixture.getPhase();

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
		//       at java.util.LinkedList.checkElementIndex(Unknown Source)
		//       at java.util.LinkedList.get(Unknown Source)
		//       at risk.commonObjects.GameState.<init>(GameState.java:27)
		assertEquals(1, result);
	}

	/**
	 * Run the LinkedList<Player> getPlayers() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/10/13 1:27 PM
	 */
	@Test
	public void testGetPlayers_1()
		throws Exception {

		LinkedList<Player> players = new LinkedList<Player>();
		players.add(new Player(0, "0"));
		players.add(new Player(1, "1"));
		GameState fixture = new GameState("", players, 1, new HashMap<String, Territory>(), new HashMap<String, Territory>(), 1);
		

		fixture.setPhase(1);
		fixture.setMapLocation("");
		fixture.setMapName("");
		fixture.setImgLocation("");
		fixture.setCurrentPlayer(1);
		LinkedList<Player> result = fixture.getPlayers();

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
		//       at java.util.LinkedList.checkElementIndex(Unknown Source)
		//       at java.util.LinkedList.get(Unknown Source)
		//       at risk.commonObjects.GameState.<init>(GameState.java:27)
		assertNotNull(result);
	}

	/**
	 * Run the void setCurrentPlayer(int) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/10/13 1:27 PM
	 */
	@Test
	public void testSetCurrentPlayer_1()
		throws Exception {

		LinkedList<Player> players = new LinkedList<Player>();
		players.add(new Player(0, "0"));
		players.add(new Player(1, "1"));
		GameState fixture = new GameState("", players, 1, new HashMap<String, Territory>(), new HashMap<String, Territory>(), 1);
		

		fixture.setPhase(1);
		fixture.setMapLocation("");
		fixture.setMapName("");
		fixture.setImgLocation("");
		fixture.setCurrentPlayer(1);
		int current = 1;

		fixture.setCurrentPlayer(current);

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
		//       at java.util.LinkedList.checkElementIndex(Unknown Source)
		//       at java.util.LinkedList.get(Unknown Source)
		//       at risk.commonObjects.GameState.<init>(GameState.java:27)
	}

	/**
	 * Run the void setImgLocation(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/10/13 1:27 PM
	 */
	@Test
	public void testSetImgLocation_1()
		throws Exception {

		LinkedList<Player> players = new LinkedList<Player>();
		players.add(new Player(0, "0"));
		players.add(new Player(1, "1"));
		GameState fixture = new GameState("", players, 1, new HashMap<String, Territory>(), new HashMap<String, Territory>(), 1);

		fixture.setPhase(1);
		fixture.setMapLocation("");
		fixture.setMapName("");
		fixture.setImgLocation("");
		fixture.setCurrentPlayer(1);
		String mapLocation = "";

		fixture.setImgLocation(mapLocation);

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
		//       at java.util.LinkedList.checkElementIndex(Unknown Source)
		//       at java.util.LinkedList.get(Unknown Source)
		//       at risk.commonObjects.GameState.<init>(GameState.java:27)
	}

	/**
	 * Run the void setMapLocation(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/10/13 1:27 PM
	 */
	@Test
	public void testSetMapLocation_1()
		throws Exception {

		LinkedList<Player> players = new LinkedList<Player>();
		players.add(new Player(0, "0"));
		players.add(new Player(1, "1"));
		GameState fixture = new GameState("", players, 1, new HashMap<String, Territory>(), new HashMap<String, Territory>(), 1);
		

		fixture.setPhase(1);
		fixture.setMapLocation("");
		fixture.setMapName("");
		fixture.setImgLocation("");
		fixture.setCurrentPlayer(1);
		String mapLocation = "";

		fixture.setMapLocation(mapLocation);

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
		//       at java.util.LinkedList.checkElementIndex(Unknown Source)
		//       at java.util.LinkedList.get(Unknown Source)
		//       at risk.commonObjects.GameState.<init>(GameState.java:27)
	}

	/**
	 * Run the void setMapName(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/10/13 1:27 PM
	 */
	@Test
	public void testSetMapName_1()
		throws Exception {

		LinkedList<Player> players = new LinkedList<Player>();
		players.add(new Player(0, "0"));
		players.add(new Player(1, "1"));
		GameState fixture = new GameState("", players, 1, new HashMap<String, Territory>(), new HashMap<String, Territory>(), 1);
		

		fixture.setPhase(1);
		fixture.setMapLocation("");
		fixture.setMapName("");
		fixture.setImgLocation("");
		fixture.setCurrentPlayer(1);
		String mapName = "";

		fixture.setMapName(mapName);

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
		//       at java.util.LinkedList.checkElementIndex(Unknown Source)
		//       at java.util.LinkedList.get(Unknown Source)
		//       at risk.commonObjects.GameState.<init>(GameState.java:27)
	}

	/**
	 * Run the void setPhase(int) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/10/13 1:27 PM
	 */
	@Test
	public void testSetPhase_1()
		throws Exception {

		LinkedList<Player> players = new LinkedList<Player>();
		players.add(new Player(0, "0"));
		players.add(new Player(1, "1"));
		GameState fixture = new GameState("", players, 1, new HashMap<String, Territory>(), new HashMap<String, Territory>(), 1);

		fixture.setPhase(1);
		fixture.setMapLocation("");
		fixture.setMapName("");
		fixture.setImgLocation("");
		fixture.setCurrentPlayer(1);
		int phase = 1;

		fixture.setPhase(phase);

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
		//       at java.util.LinkedList.checkElementIndex(Unknown Source)
		//       at java.util.LinkedList.get(Unknown Source)
		//       at risk.commonObjects.GameState.<init>(GameState.java:27)
	}

	/**
	 * Run the void setPlayers(LinkedList<Player>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/10/13 1:27 PM
	 */
	@Test
	public void testSetPlayers_1()
		throws Exception {

		LinkedList<Player> players = new LinkedList<Player>();
		players.add(new Player(0, "0"));
		players.add(new Player(1, "1"));
		GameState fixture = new GameState("", players, 1, new HashMap<String, Territory>(), new HashMap<String, Territory>(), 1);

		fixture.setPhase(1);
		fixture.setMapLocation("");
		fixture.setMapName("");
		fixture.setImgLocation("");
		fixture.setCurrentPlayer(1);
		LinkedList<Player> players2 = new LinkedList();

		fixture.setPlayers(players2);

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
		//       at java.util.LinkedList.checkElementIndex(Unknown Source)
		//       at java.util.LinkedList.get(Unknown Source)
		//       at risk.commonObjects.GameState.<init>(GameState.java:27)
	}

	/**
	 * Perform pre-test initialization.
	 *
	 * @throws Exception
	 *         if the initialization fails for some reason
	 *
	 * @generatedBy CodePro at 10/10/13 1:27 PM
	 */
	@Before
	public void setUp()
		throws Exception {
		// add additional set up code here
	}

	/**
	 * Perform post-test clean-up.
	 *
	 * @throws Exception
	 *         if the clean-up fails for some reason
	 *
	 * @generatedBy CodePro at 10/10/13 1:27 PM
	 */
	@After
	public void tearDown()
		throws Exception {
		// Add additional tear down code here
	}

	/**
	 * Launch the test.
	 *
	 * @param args the command line arguments
	 *
	 * @generatedBy CodePro at 10/10/13 1:27 PM
	 */
	public static void main(String[] args) {
		new org.junit.runner.JUnitCore().run(AbstractGameStateTest.class);
	}
}
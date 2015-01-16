package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.LinkedList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import risk.commonObjects.GameState;
import risk.commonObjects.Player;
import risk.commonObjects.Territory;

/**
 * The class <code>GameStateTest</code> contains tests for the class <code>{@link GameState}</code>.
 *
 * @generatedBy CodePro at 10/10/13 1:30 PM
 * @author Dirka
 * @version $Revision: 1.0 $
 */
public class GameStateTest {
	/**
	 * Run the GameState() constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/10/13 1:30 PM
	 */
	@Test
	public void testGameState_1()
		throws Exception {

		GameState result = new GameState();

		// add additional test code here
		assertNotNull(result);
		assertEquals(0, result.getCurrentPlayerID());
		assertEquals(null, result.getMapLocation());
		assertEquals(null, result.getImgLocation());
		assertEquals(null, result.getMapName());
		assertEquals(null, result.getPlayers());
		assertEquals(0, result.getPhase());
	}

	/**
	 * Run the GameState(String,LinkedList<Player>,int,int) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/10/13 1:30 PM
	 */
	@Test
	public void testGameState_2()
		throws Exception {
		String mapLoc = "";
		LinkedList<Player> players = new LinkedList<Player>();
		int phase = 1;
		int current = 1;

		GameState result = new GameState(mapLoc, players, phase, current);

		// add additional test code here
		assertNotNull(result);
		assertEquals(null, result.getCurrentPlayer());
		assertEquals(1, result.getCurrentPlayerID());
		assertEquals(null, result.getOtherPlayer());
		assertEquals("", result.getMapLocation());
		assertEquals(null, result.getImgLocation());
		assertEquals(null, result.getMapName());
		assertEquals(1, result.getPhase());
	}

	/**
	 * Run the GameState(String,LinkedList<Player>,int,TreeSet<Territory>,TreeSet<Territory>,int) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/10/13 1:30 PM
	 */
	@Test
	public void testGameState_3()
		throws Exception {
		String mapLoc = "";
		LinkedList<Player> players = new LinkedList<Player>();
		players.add(new Player(0, "0"));
		players.add(new Player(1, "1"));
		int phase = 1;
		HashMap<String, Territory> play1Ter = new HashMap<String, Territory>();
		HashMap<String,Territory> play2Ter = new HashMap<String, Territory>();
		int current = 1;

		GameState result = new GameState(mapLoc, players, phase, play1Ter, play2Ter, current);

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
		//       at java.util.LinkedList.checkElementIndex(Unknown Source)
		//       at java.util.LinkedList.get(Unknown Source)
		//       at risk.commonObjects.GameState.<init>(GameState.java:27)
		assertNotNull(result);
	}

	/**
	 * Run the GameState clone() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/10/13 1:30 PM
	 */
	@Test
	public void testClone_1()
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

		GameState result = fixture.clone();

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
		//       at java.util.LinkedList.checkElementIndex(Unknown Source)
		//       at java.util.LinkedList.get(Unknown Source)
		//       at risk.commonObjects.GameState.<init>(GameState.java:27)
		assertNotNull(result);
	}

	/**
	 * Run the GameState clone() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/10/13 1:30 PM
	 */
	@Test
	public void testClone_2()
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

		GameState result = fixture.clone();

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
		//       at java.util.LinkedList.checkElementIndex(Unknown Source)
		//       at java.util.LinkedList.get(Unknown Source)
		//       at risk.commonObjects.GameState.<init>(GameState.java:27)
		assertNotNull(result);
	}

	/**
	 * Run the GameState clone() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/10/13 1:30 PM
	 */
	@Test
	public void testClone_3()
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

		GameState result = fixture.clone();

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
		//       at java.util.LinkedList.checkElementIndex(Unknown Source)
		//       at java.util.LinkedList.get(Unknown Source)
		//       at risk.commonObjects.GameState.<init>(GameState.java:27)
		assertNotNull(result);
	}

	/**
	 * Perform pre-test initialization.
	 *
	 * @throws Exception
	 *         if the initialization fails for some reason
	 *
	 * @generatedBy CodePro at 10/10/13 1:30 PM
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
	 * @generatedBy CodePro at 10/10/13 1:30 PM
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
	 * @generatedBy CodePro at 10/10/13 1:30 PM
	 */
	public static void main(String[] args) {
		new org.junit.runner.JUnitCore().run(GameStateTest.class);
	}
}
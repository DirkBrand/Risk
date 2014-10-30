package tests;

import org.junit.*;

import risk.commonObjects.Territory;
import static org.junit.Assert.*;

/**
 * The class <code>TerritoryTest</code> contains tests for the class <code>{@link Territory}</code>.
 *
 * @generatedBy CodePro at 10/11/13 10:07 AM
 * @author Dirka
 * @version $Revision: 1.0 $
 */
public class TerritoryTest {
	/**
	 * Run the Territory(String) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testTerritory_1()
		throws Exception {
		String name = "";

		Territory result = new Territory(name);

		// add additional test code here
		assertNotNull(result);
		assertEquals(" - 0", result.toString());
		assertEquals(0, result.getId());
		assertEquals("", result.getName());
		assertEquals(null, result.getNeighbours());
		assertEquals(0, result.getXCoordinate());
		assertEquals(0, result.getContinentNum());
		assertEquals(0, result.getYCoordinate());
		assertEquals(null, result.getContinent());
		assertEquals(0, result.getNrTroops());
	}

	/**
	 * Run the Territory(int,String,int,String,int,int,int) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testTerritory_2()
		throws Exception {
		int id = 1;
		String name = "";
		int contID = 1;
		String cont = "";
		int x = 1;
		int y = 1;
		int nrTroops = 1;

		Territory result = new Territory(id, name, contID, cont, x, y, nrTroops);

		// add additional test code here
		assertNotNull(result);
		assertEquals(" - 1", result.toString());
		assertEquals(1, result.getId());
		assertEquals("", result.getName());
		assertEquals(null, result.getNeighbours());
		assertEquals(1, result.getXCoordinate());
		assertEquals(1, result.getContinentNum());
		assertEquals(1, result.getYCoordinate());
		assertEquals("", result.getContinent());
		assertEquals(1, result.getNrTroops());
	}

	/**
	 * Run the Territory clone() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */

	@Test(expected = NullPointerException.class)
	public void testClone_1()
		throws Exception {
		Territory fixture = new Territory(1, "", 1, "", 1, 1, 1);
		fixture.setCoordinates(1, 1);
		fixture.setContinentID(1);
		fixture.setName("");
		fixture.setId(1);
		fixture.setContinent("");
		fixture.depth = 1;
		fixture.connectedRegion = 1;

		Territory result = fixture.clone();

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.NullPointerException
		//       at risk.commonObjects.Territory.clone(Territory.java:109)
		assertNotNull(result);
	}

	/**
	 * Run the Territory clone() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */

	@Test(expected = NullPointerException.class)
	public void testClone_2()
		throws Exception {
		Territory fixture = new Territory(1, "", 1, "", 1, 1, 1);
		fixture.setCoordinates(1, 1);
		fixture.setContinentID(1);
		fixture.setName("");
		fixture.setId(1);
		fixture.setContinent("");
		fixture.depth = 1;
		fixture.connectedRegion = 1;

		Territory result = fixture.clone();

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.NullPointerException
		//       at risk.commonObjects.Territory.clone(Territory.java:109)
		assertNotNull(result);
	}

	/**
	 * Run the int compareTo(Territory) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testCompareTo_1()
		throws Exception {
		Territory fixture = new Territory(1, "", 1, "", 1, 1, 1);
		fixture.setCoordinates(1, 1);
		fixture.setContinentID(1);
		fixture.setName("");
		fixture.setId(1);
		fixture.setContinent("");
		fixture.depth = 1;
		fixture.connectedRegion = 1;
		Territory o = new Territory("");

		int result = fixture.compareTo(o);

		// add additional test code here
		assertEquals(0, result);
	}

	/**
	 * Run the void decrementTroops() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testDecrementTroops_1()
		throws Exception {
		Territory fixture = new Territory(1, "", 1, "", 1, 1, 1);
		fixture.setCoordinates(1, 1);
		fixture.setContinentID(1);
		fixture.setName("");
		fixture.setId(1);
		fixture.setContinent("");
		fixture.depth = 1;
		fixture.connectedRegion = 1;

		fixture.decrementTroops();

		// add additional test code here
	}

	/**
	 * Run the String getContinent() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testGetContinent_1()
		throws Exception {
		Territory fixture = new Territory(1, "", 1, "", 1, 1, 1);
		fixture.setCoordinates(1, 1);
		fixture.setContinentID(1);
		fixture.setName("");
		fixture.setId(1);
		fixture.setContinent("");
		fixture.depth = 1;
		fixture.connectedRegion = 1;

		String result = fixture.getContinent();

		// add additional test code here
		assertEquals("", result);
	}

	/**
	 * Run the int getContinentNum() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testGetContinentNum_1()
		throws Exception {
		Territory fixture = new Territory(1, "", 1, "", 1, 1, 1);
		fixture.setCoordinates(1, 1);
		fixture.setContinentID(1);
		fixture.setName("");
		fixture.setId(1);
		fixture.setContinent("");
		fixture.depth = 1;
		fixture.connectedRegion = 1;

		int result = fixture.getContinentNum();

		// add additional test code here
		assertEquals(1, result);
	}

	/**
	 * Run the int getId() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testGetId_1()
		throws Exception {
		Territory fixture = new Territory(1, "", 1, "", 1, 1, 1);
		fixture.setCoordinates(1, 1);
		fixture.setContinentID(1);
		fixture.setName("");
		fixture.setId(1);
		fixture.setContinent("");
		fixture.depth = 1;
		fixture.connectedRegion = 1;

		int result = fixture.getId();

		// add additional test code here
		assertEquals(1, result);
	}

	/**
	 * Run the String getName() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testGetName_1()
		throws Exception {
		Territory fixture = new Territory(1, "", 1, "", 1, 1, 1);
		fixture.setCoordinates(1, 1);
		fixture.setContinentID(1);
		fixture.setName("");
		fixture.setId(1);
		fixture.setContinent("");
		fixture.depth = 1;
		fixture.connectedRegion = 1;

		String result = fixture.getName();

		// add additional test code here
		assertEquals("", result);
	}

	/**
	 * Run the Territory[] getNeighbours() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testGetNeighbours_1()
		throws Exception {
		Territory fixture = new Territory(1, "", 1, "", 1, 1, 1);
		fixture.setCoordinates(1, 1);
		fixture.setContinentID(1);
		fixture.setName("");
		fixture.setId(1);
		fixture.setContinent("");
		fixture.depth = 1;
		fixture.connectedRegion = 1;

		Territory[] result = fixture.getNeighbours();

		// add additional test code here
		assertEquals(null, result);
	}

	/**
	 * Run the int getNrTroops() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testGetNrTroops_1()
		throws Exception {
		Territory fixture = new Territory(1, "", 1, "", 1, 1, 1);
		fixture.setCoordinates(1, 1);
		fixture.setContinentID(1);
		fixture.setName("");
		fixture.setId(1);
		fixture.setContinent("");
		fixture.depth = 1;
		fixture.connectedRegion = 1;

		int result = fixture.getNrTroops();

		// add additional test code here
		assertEquals(1, result);
	}

	/**
	 * Run the int getXCoordinate() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testGetXCoordinate_1()
		throws Exception {
		Territory fixture = new Territory(1, "", 1, "", 1, 1, 1);
		fixture.setCoordinates(1, 1);
		fixture.setContinentID(1);
		fixture.setName("");
		fixture.setId(1);
		fixture.setContinent("");
		fixture.depth = 1;
		fixture.connectedRegion = 1;

		int result = fixture.getXCoordinate();

		// add additional test code here
		assertEquals(1, result);
	}

	/**
	 * Run the int getYCoordinate() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testGetYCoordinate_1()
		throws Exception {
		Territory fixture = new Territory(1, "", 1, "", 1, 1, 1);
		fixture.setCoordinates(1, 1);
		fixture.setContinentID(1);
		fixture.setName("");
		fixture.setId(1);
		fixture.setContinent("");
		fixture.depth = 1;
		fixture.connectedRegion = 1;

		int result = fixture.getYCoordinate();

		// add additional test code here
		assertEquals(1, result);
	}

	/**
	 * Run the void incrementTroops() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testIncrementTroops_1()
		throws Exception {
		Territory fixture = new Territory(1, "", 1, "", 1, 1, 1);
		fixture.setCoordinates(1, 1);
		fixture.setContinentID(1);
		fixture.setName("");
		fixture.setId(1);
		fixture.setContinent("");
		fixture.depth = 1;
		fixture.connectedRegion = 1;

		fixture.incrementTroops();

		// add additional test code here
	}

	/**
	 * Run the void setContinent(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testSetContinent_1()
		throws Exception {
		Territory fixture = new Territory(1, "", 1, "", 1, 1, 1);
		fixture.setCoordinates(1, 1);
		fixture.setContinentID(1);
		fixture.setName("");
		fixture.setId(1);
		fixture.setContinent("");
		fixture.depth = 1;
		fixture.connectedRegion = 1;
		String continent = "";

		fixture.setContinent(continent);

		// add additional test code here
	}

	/**
	 * Run the void setContinentID(int) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testSetContinentID_1()
		throws Exception {
		Territory fixture = new Territory(1, "", 1, "", 1, 1, 1);
		fixture.setCoordinates(1, 1);
		fixture.setContinentID(1);
		fixture.setName("");
		fixture.setId(1);
		fixture.setContinent("");
		fixture.depth = 1;
		fixture.connectedRegion = 1;
		int continentNum = 1;

		fixture.setContinentID(continentNum);

		// add additional test code here
	}

	/**
	 * Run the void setCoordinates(int,int) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testSetCoordinates_1()
		throws Exception {
		Territory fixture = new Territory(1, "", 1, "", 1, 1, 1);
		fixture.setCoordinates(1, 1);
		fixture.setContinentID(1);
		fixture.setName("");
		fixture.setId(1);
		fixture.setContinent("");
		fixture.depth = 1;
		fixture.connectedRegion = 1;
		int x = 1;
		int y = 1;

		fixture.setCoordinates(x, y);

		// add additional test code here
	}

	/**
	 * Run the void setId(int) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testSetId_1()
		throws Exception {
		Territory fixture = new Territory(1, "", 1, "", 1, 1, 1);
		fixture.setCoordinates(1, 1);
		fixture.setContinentID(1);
		fixture.setName("");
		fixture.setId(1);
		fixture.setContinent("");
		fixture.depth = 1;
		fixture.connectedRegion = 1;
		int id = 1;

		fixture.setId(id);

		// add additional test code here
	}

	/**
	 * Run the void setName(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testSetName_1()
		throws Exception {
		Territory fixture = new Territory(1, "", 1, "", 1, 1, 1);
		fixture.setCoordinates(1, 1);
		fixture.setContinentID(1);
		fixture.setName("");
		fixture.setId(1);
		fixture.setContinent("");
		fixture.depth = 1;
		fixture.connectedRegion = 1;
		String name = "";

		fixture.setName(name);

		// add additional test code here
	}

	/**
	 * Run the void setNeighbours(Territory[]) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testSetNeighbours_1()
		throws Exception {
		Territory fixture = new Territory(1, "", 1, "", 1, 1, 1);
		fixture.setCoordinates(1, 1);
		fixture.setContinentID(1);
		fixture.setName("");
		fixture.setId(1);
		fixture.setContinent("");
		fixture.depth = 1;
		fixture.connectedRegion = 1;
		Territory[] neighbours = new Territory[] {};

		fixture.setNeighbours(neighbours);

		// add additional test code here
	}

	/**
	 * Run the void setNrTroops(int) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testSetNrTroops_1()
		throws Exception {
		Territory fixture = new Territory(1, "", 1, "", 1, 1, 1);
		fixture.setCoordinates(1, 1);
		fixture.setContinentID(1);
		fixture.setName("");
		fixture.setId(1);
		fixture.setContinent("");
		fixture.depth = 1;
		fixture.connectedRegion = 1;
		int nrTroops = 1;

		fixture.setNrTroops(nrTroops);

		// add additional test code here
	}

	/**
	 * Run the String toString() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testToString_1()
		throws Exception {
		Territory fixture = new Territory(1, "", 1, "", 1, 1, 1);
		fixture.setCoordinates(1, 1);
		fixture.setContinentID(1);
		fixture.setName("");
		fixture.setId(1);
		fixture.setContinent("");
		fixture.depth = 1;
		fixture.connectedRegion = 1;

		String result = fixture.toString();

		// add additional test code here
		assertEquals(" - 1", result);
	}

	/**
	 * Perform pre-test initialization.
	 *
	 * @throws Exception
	 *         if the initialization fails for some reason
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
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
	 * @generatedBy CodePro at 10/11/13 10:07 AM
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
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	public static void main(String[] args) {
		new org.junit.runner.JUnitCore().run(TerritoryTest.class);
	}
}
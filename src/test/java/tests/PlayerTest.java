package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.TreeSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import risk.commonObjects.Player;
import risk.commonObjects.Territory;

/**
 * The class <code>PlayerTest</code> contains tests for the class <code>{@link Player}</code>.
 *
 * @generatedBy CodePro at 10/11/13 10:07 AM
 * @author Dirka
 * @version $Revision: 1.0 $
 */
public class PlayerTest {
	/**
	 * Run the Player(int,String) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testPlayer_1()
		throws Exception {
		int id = 1;
		String name = "";

		Player result = new Player(id, name);

		// add additional test code here
		assertNotNull(result);
		assertEquals(1, result.getId());
		assertEquals("", result.getName());
		assertEquals(0, result.getPort());
		assertEquals(null, result.getIpAddress());
	}

	/**
	 * Run the Player(int,String,TreeSet<Territory>) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testPlayer_2()
		throws Exception {
		int id = 1;
		String name = "";
		HashMap<String,Territory> ters = new HashMap<String, Territory>();

		Player result = new Player(id, name, ters);

		// add additional test code here
		assertNotNull(result);
		assertEquals(1, result.getId());
		assertEquals("", result.getName());
		assertEquals(0, result.getPort());
		assertEquals(null, result.getIpAddress());
	}

	/**
	 * Run the Player(int,String,String,int) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testPlayer_3()
		throws Exception {
		int id = 1;
		String name = "";
		String ip = "";
		int port = 1;

		Player result = new Player(id, name, ip, port);

		// add additional test code here
		assertNotNull(result);
		assertEquals(1, result.getId());
		assertEquals("", result.getName());
		assertEquals(1, result.getPort());
		assertEquals("", result.getIpAddress());
	}

	/**
	 * Run the Player clone() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testClone_1()
		throws Exception {
		Player fixture = new Player(1, "", new HashMap<String, Territory>());
		fixture.setId(1);
		fixture.setIpAddress("");
		fixture.setPort(1);
		fixture.setName("");

		Player result = fixture.clone();

		// add additional test code here
		assertNotNull(result);
		assertEquals(1, result.getId());
		assertEquals("", result.getName());
		assertEquals(1, result.getPort());
		assertEquals("", result.getIpAddress());
	}

	/**
	 * Run the Player clone() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testClone_2()
		throws Exception {
		Player fixture = new Player(1, "", new HashMap<String, Territory>());
		fixture.setId(1);
		fixture.setPort(1);
		fixture.setIpAddress("");
		fixture.setName("");

		Player result = fixture.clone();

		// add additional test code here
		assertNotNull(result);
		assertEquals(1, result.getId());
		assertEquals("", result.getName());
		assertEquals(1, result.getPort());
		assertEquals("", result.getIpAddress());
	}

	/**
	 * Run the Player clone() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testClone_3()
		throws Exception {
		Player fixture = new Player(1, "", new HashMap<String, Territory>());
		fixture.setId(1);
		fixture.setPort(1);
		fixture.setIpAddress("");
		fixture.setName("");

		Player result = fixture.clone();

		// add additional test code here
		assertNotNull(result);
		assertEquals(1, result.getId());
		assertEquals("", result.getName());
		assertEquals(1, result.getPort());
		assertEquals("", result.getIpAddress());
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
		Player fixture = new Player(1, "", new HashMap<String, Territory>());
		fixture.setId(1);
		fixture.setPort(1);
		fixture.setIpAddress("");
		fixture.setName("");

		int result = fixture.getId();

		// add additional test code here
		assertEquals(1, result);
	}

	/**
	 * Run the String getIpAddress() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testGetIpAddress_1()
		throws Exception {
		Player fixture = new Player(1, "", new HashMap<String, Territory>());
		fixture.setId(1);
		fixture.setPort(1);
		fixture.setIpAddress("");
		fixture.setName("");

		String result = fixture.getIpAddress();

		// add additional test code here
		assertEquals("", result);
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
		Player fixture = new Player(1, "", new HashMap<String, Territory>());
		fixture.setId(1);
		fixture.setPort(1);
		fixture.setIpAddress("");
		fixture.setName("");

		String result = fixture.getName();

		// add additional test code here
		assertEquals("", result);
	}

	/**
	 * Run the int getPort() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testGetPort_1()
		throws Exception {
		Player fixture = new Player(1, "", new HashMap<String, Territory>());
		fixture.setId(1);
		fixture.setPort(1);
		fixture.setIpAddress("");
		fixture.setName("");

		int result = fixture.getPort();

		// add additional test code here
		assertEquals(1, result);
	}

	/**
	 * Run the TreeSet<Territory> getTerritories() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testGetTerritories_1()
		throws Exception {
		Player fixture = new Player(1, "", new HashMap<String, Territory>());
		fixture.setId(1);
		fixture.setPort(1);
		fixture.setIpAddress("");
		fixture.setName("");

		HashMap<String, Territory> result = fixture.getTerritories();

		// add additional test code here
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	/**
	 * Run the Territory getTerritoryByID(int) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testGetTerritoryByID_1()
		throws Exception {
		Player fixture = new Player(1, "", new HashMap<String, Territory>());
		fixture.setId(1);
		fixture.setPort(1);
		fixture.setIpAddress("");
		fixture.setName("");
		int id = 1;

		Territory result = fixture.getTerritoryByID(id);

		// add additional test code here
		assertEquals(null, result);
	}

	/**
	 * Run the Territory getTerritoryByID(int) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testGetTerritoryByID_2()
		throws Exception {
		Player fixture = new Player(1, "", new HashMap<String, Territory>());
		fixture.setId(1);
		fixture.setIpAddress("");
		fixture.setPort(1);
		fixture.setName("");
		int id = 1;

		Territory result = fixture.getTerritoryByID(id);

		// add additional test code here
		assertEquals(null, result);
	}

	/**
	 * Run the Territory getTerritoryByID(int) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testGetTerritoryByID_3()
		throws Exception {
		Player fixture = new Player(1, "", new HashMap<String, Territory>());
		fixture.setId(1);
		fixture.setPort(1);
		fixture.setIpAddress("");
		fixture.setName("");
		int id = 1;

		Territory result = fixture.getTerritoryByID(id);

		// add additional test code here
		assertEquals(null, result);
	}

	/**
	 * Run the Territory getTerritoryByName(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testGetTerritoryByName_1()
		throws Exception {
		Player fixture = new Player(1, "", new HashMap<String, Territory>());
		fixture.setId(1);
		fixture.setPort(1);
		fixture.setIpAddress("");
		fixture.setName("");
		String name = "";

		Territory result = fixture.getTerritoryByName(name);

		// add additional test code here
		assertEquals(null, result);
	}

	/**
	 * Run the Territory getTerritoryByName(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testGetTerritoryByName_2()
		throws Exception {
		Player fixture = new Player(1, "", new HashMap<String, Territory>());
		fixture.setId(1);
		fixture.setIpAddress("");
		fixture.setPort(1);
		fixture.setName("");
		String name = "";

		Territory result = fixture.getTerritoryByName(name);

		// add additional test code here
		assertEquals(null, result);
	}

	/**
	 * Run the Territory getTerritoryByName(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testGetTerritoryByName_3()
		throws Exception {
		Player fixture = new Player(1, "", new HashMap<String, Territory>());
		fixture.setId(1);
		fixture.setPort(1);
		fixture.setIpAddress("");
		fixture.setName("");
		String name = "";

		Territory result = fixture.getTerritoryByName(name);

		// add additional test code here
		assertEquals(null, result);
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
		Player fixture = new Player(1, "", new HashMap<String, Territory>());
		fixture.setId(1);
		fixture.setPort(1);
		fixture.setIpAddress("");
		fixture.setName("");
		int id = 1;

		fixture.setId(id);

		// add additional test code here
	}

	/**
	 * Run the void setIpAddress(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testSetIpAddress_1()
		throws Exception {
		Player fixture = new Player(1, "", new HashMap<String, Territory>());
		fixture.setId(1);
		fixture.setPort(1);
		fixture.setIpAddress("");
		fixture.setName("");
		String ipAddress = "";

		fixture.setIpAddress(ipAddress);

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
		Player fixture = new Player(1, "", new HashMap<String, Territory>());
		fixture.setId(1);
		fixture.setPort(1);
		fixture.setIpAddress("");
		fixture.setName("");
		String name = "";

		fixture.setName(name);

		// add additional test code here
	}

	/**
	 * Run the void setPort(int) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testSetPort_1()
		throws Exception {
		Player fixture = new Player(1, "", new HashMap<String, Territory>());
		fixture.setId(1);
		fixture.setPort(1);
		fixture.setIpAddress("");
		fixture.setName("");
		int port = 1;

		fixture.setPort(port);

		// add additional test code here
	}

	/**
	 * Run the void setTerritories(TreeSet<Territory>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testSetTerritories_1()
		throws Exception {
		Player fixture = new Player(1, "", new HashMap<String, Territory>());
		fixture.setId(1);
		fixture.setPort(1);
		fixture.setIpAddress("");
		fixture.setName("");
		HashMap<String, Territory> territories = new HashMap<String, Territory>();

		fixture.setTerritories(territories);

		// add additional test code here
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
		new org.junit.runner.JUnitCore().run(PlayerTest.class);
	}
}
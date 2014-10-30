package tests;

import java.net.Socket;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import org.junit.*;

import risk.commonObjects.GameState;
import risk.commonObjects.Player;
import risk.humanEngine.EngineLogic;
import risk.humanEngine.EngineProtocolManager;
import static org.junit.Assert.*;

/**
 * The class <code>EngineProtocolManagerTest</code> contains tests for the class
 * <code>{@link EngineProtocolManager}</code>.
 * 
 * @generatedBy CodePro at 10/11/13 11:23 AM
 * @author Dirka
 * @version $Revision: 1.0 $
 */
public class EngineProtocolManagerTest {
	/**
	 * Run the EngineProtocolManager(Socket,EngineLogic) constructor test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 10/11/13 11:23 AM
	 */
	@Test
	public void testEngineProtocolManager_1() throws Exception {
		Socket controller = new Socket();
		EngineLogic engineLogic = new EngineLogic(false);

		EngineProtocolManager result = new EngineProtocolManager(controller,
				engineLogic);

		// add additional test code here
		assertNotNull(result);
	}

	/**
	 * Run the EngineProtocolManager(Socket,EngineLogic) constructor test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 10/11/13 11:23 AM
	 */
	@Test
	public void testEngineProtocolManager_2() throws Exception {
		Socket controller = new Socket();
		EngineLogic engineLogic = new EngineLogic(false);

		EngineProtocolManager result = new EngineProtocolManager(controller,
				engineLogic);

		// add additional test code here
		assertNotNull(result);
	}

	/**
	 * Run the EngineProtocolManager(Socket,EngineLogic) constructor test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 10/11/13 11:23 AM
	 */
	@Test
	public void testEngineProtocolManager_3() throws Exception {
		Socket controller = new Socket();
		EngineLogic engineLogic = new EngineLogic(false);

		EngineProtocolManager result = new EngineProtocolManager(controller,
				engineLogic);

		// add additional test code here
		assertNotNull(result);
	}

	/**
	 * Run the void process(int,String,LinkedList<String>) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 10/11/13 11:23 AM
	 */
	@Test
	public void testProcess_1() throws Exception {
		EngineLogic EL = new EngineLogic(false);
		EL.game = new GameState("", new LinkedList<Player>(), 1, 0);
		EngineProtocolManager fixture = new EngineProtocolManager(new Socket(),
				EL);
		fixture.setupTroopsPlaced = 1;
		fixture.justMoved = true;

		int id = 1;
		String message = "place_troops";
		LinkedList<String> args = new LinkedList();
		args.add("1");
		args.add("2");

		try {
			fixture.process(id, message, args);
		} catch (NullPointerException e) {
			assertTrue(true);
		}

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this
		// test:
		// java.lang.NullPointerException
		// at risk.humanEngine.EngineLogic.getPhase(EngineLogic.java:153)
		// at
		// risk.humanEngine.EngineProtocolManager.process(EngineProtocolManager.java:87)
	}

	/**
	 * Run the void process(int,String,LinkedList<String>) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 10/11/13 11:23 AM
	 */
	@Test
	public void testProcess_2() throws Exception {
		EngineProtocolManager fixture = new EngineProtocolManager(new Socket(),
				new EngineLogic(false));
		fixture.setupTroopsPlaced = 1;
		fixture.justMoved = true;
		int id = 1;
		String message = "troops_placed";
		LinkedList<String> args = new LinkedList();

		try {
			fixture.process(id, message, args);
		} catch (NullPointerException e) {
			assertTrue(true);
		}
		// add additional test code here
		// An unexpected exception was thrown in user code while executing this
		// test:
		// java.lang.NullPointerException
		// at risk.humanEngine.EngineLogic.updateMap(EngineLogic.java:574)
		// at
		// risk.humanEngine.EngineProtocolManager.process(EngineProtocolManager.java:107)
	}

	/**
	 * Run the void process(int,String,LinkedList<String>) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 10/11/13 11:23 AM
	 */
	@Test
	public void testProcess_3() throws Exception {
		EngineProtocolManager fixture = new EngineProtocolManager(new Socket(),
				new EngineLogic(false));
		fixture.setupTroopsPlaced = 1;
		fixture.justMoved = true;
		int id = 1;
		String message = "troops_placed";
		LinkedList<String> args = new LinkedList();

		try {
			fixture.process(id, message, args);
			} catch (NullPointerException e) {
				assertTrue(true);
			}

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this
		// test:
		// java.lang.NullPointerException
		// at risk.humanEngine.EngineLogic.updateMap(EngineLogic.java:574)
		// at
		// risk.humanEngine.EngineProtocolManager.process(EngineProtocolManager.java:107)
	}

	/**
	 * Run the void process(int,String,LinkedList<String>) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 10/11/13 11:23 AM
	 */
	@Test
	public void testProcess_4() throws Exception {
		EngineProtocolManager fixture = new EngineProtocolManager(new Socket(),
				new EngineLogic(false));
		fixture.setupTroopsPlaced = 1;
		fixture.justMoved = true;
		int id = 1;
		String message = "troops_placed";
		LinkedList<String> args = new LinkedList();

		try {
			fixture.process(id, message, args);
			} catch (NullPointerException e) {
				assertTrue(true);
			}

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this
		// test:
		// java.lang.NullPointerException
		// at risk.humanEngine.EngineLogic.updateMap(EngineLogic.java:574)
		// at
		// risk.humanEngine.EngineProtocolManager.process(EngineProtocolManager.java:107)
	}

	/**
	 * Run the void process(int,String,LinkedList<String>) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 10/11/13 11:23 AM
	 */
	@Test
	public void testProcess_5() throws Exception {
		EngineProtocolManager fixture = new EngineProtocolManager(new Socket(),
				new EngineLogic(false));
		fixture.setupTroopsPlaced = 1;
		fixture.justMoved = true;
		int id = 1;
		String message = "troops_placed";
		LinkedList<String> args = new LinkedList();

		try {
			fixture.process(id, message, args);
			} catch (NullPointerException e) {
				assertTrue(true);
			}

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this
		// test:
		// java.lang.NullPointerException
		// at risk.humanEngine.EngineLogic.updateMap(EngineLogic.java:574)
		// at
		// risk.humanEngine.EngineProtocolManager.process(EngineProtocolManager.java:107)
	}

	/**
	 * Run the void process(int,String,LinkedList<String>) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 10/11/13 11:23 AM
	 */
	@Test
	public void testProcess_6() throws Exception {
		EngineProtocolManager fixture = new EngineProtocolManager(new Socket(),
				new EngineLogic(false));
		fixture.setupTroopsPlaced = 1;
		fixture.justMoved = true;
		int id = 1;
		String message = "troops_placed";
		LinkedList<String> args = new LinkedList();

		try {
			fixture.process(id, message, args);
			} catch (NullPointerException e) {
				assertTrue(true);
			}

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this
		// test:
		// java.lang.NullPointerException
		// at risk.humanEngine.EngineLogic.updateMap(EngineLogic.java:574)
		// at
		// risk.humanEngine.EngineProtocolManager.process(EngineProtocolManager.java:107)
	}

	/**
	 * Run the void process(int,String,LinkedList<String>) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 10/11/13 11:23 AM
	 */
	@Test
	public void testProcess_7() throws Exception {
		EngineProtocolManager fixture = new EngineProtocolManager(new Socket(),
				new EngineLogic(false));
		fixture.setupTroopsPlaced = 1;
		fixture.justMoved = true;
		int id = 1;
		String message = "troops_placed";
		LinkedList<String> args = new LinkedList();

		try {
			fixture.process(id, message, args);
			} catch (NullPointerException e) {
				assertTrue(true);
			}

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this
		// test:
		// java.lang.NullPointerException
		// at risk.humanEngine.EngineLogic.updateMap(EngineLogic.java:574)
		// at
		// risk.humanEngine.EngineProtocolManager.process(EngineProtocolManager.java:107)
	}

	/**
	 * Run the void process(int,String,LinkedList<String>) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 10/11/13 11:23 AM
	 */
	@Test
	public void testProcess_8() throws Exception {
		EngineProtocolManager fixture = new EngineProtocolManager(new Socket(),
				new EngineLogic(false));
		fixture.setupTroopsPlaced = 1;
		fixture.justMoved = true;
		int id = 1;
		String message = "attack";
		LinkedList<String> args = new LinkedList();

		try {
			fixture.process(id, message, args);
			} catch (NullPointerException e) {
				assertTrue(true);
			}

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this
		// test:
		// java.lang.NullPointerException
		// at risk.humanEngine.EngineLogic.getPhase(EngineLogic.java:153)
		// at
		// risk.humanEngine.EngineProtocolManager.process(EngineProtocolManager.java:134)
	}

	/**
	 * Run the void process(int,String,LinkedList<String>) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 10/11/13 11:23 AM
	 */
	@Test
	public void testProcess_9() throws Exception {
		EngineProtocolManager fixture = new EngineProtocolManager(new Socket(),
				new EngineLogic(false));
		fixture.setupTroopsPlaced = 1;
		fixture.justMoved = true;
		int id = 1;
		String message = "attack";
		LinkedList<String> args = new LinkedList();
		try {
			fixture.process(id, message, args);
			} catch (NullPointerException e) {
				assertTrue(true);
			}

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this
		// test:
		// java.lang.NullPointerException
		// at risk.humanEngine.EngineLogic.getPhase(EngineLogic.java:153)
		// at
		// risk.humanEngine.EngineProtocolManager.process(EngineProtocolManager.java:134)
	}

	/**
	 * Run the void process(int,String,LinkedList<String>) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 10/11/13 11:23 AM
	 */
	@Test
	public void testProcess_10() throws Exception {
		EngineProtocolManager fixture = new EngineProtocolManager(new Socket(),
				new EngineLogic(false));
		fixture.setupTroopsPlaced = 1;
		fixture.justMoved = true;
		int id = 1;
		String message = "attack_result";
		LinkedList<String> args = new LinkedList();

		try {
			fixture.process(id, message, args);
			} catch (IndexOutOfBoundsException e) {
				assertTrue(true);
			}
		// add additional test code here
		// An unexpected exception was thrown in user code while executing this
		// test:
		// java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
		// at java.util.LinkedList.checkElementIndex(Unknown Source)
		// at java.util.LinkedList.get(Unknown Source)
		// at
		// risk.humanEngine.EngineProtocolManager.process(EngineProtocolManager.java:147)
	}

	/**
	 * Run the void process(int,String,LinkedList<String>) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 10/11/13 11:23 AM
	 */
	@Test
	public void testProcess_11() throws Exception {
		EngineProtocolManager fixture = new EngineProtocolManager(new Socket(),
				new EngineLogic(false));
		fixture.setupTroopsPlaced = 1;
		fixture.justMoved = true;
		int id = 1;
		String message = "attack_result";
		LinkedList<String> args = new LinkedList();

		try {
			fixture.process(id, message, args);
			} catch (IndexOutOfBoundsException e) {
				assertTrue(true);
			}

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this
		// test:
		// java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
		// at java.util.LinkedList.checkElementIndex(Unknown Source)
		// at java.util.LinkedList.get(Unknown Source)
		// at
		// risk.humanEngine.EngineProtocolManager.process(EngineProtocolManager.java:147)
	}

	/**
	 * Run the void process(int,String,LinkedList<String>) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 10/11/13 11:23 AM
	 */
	@Test
	public void testProcess_12() throws Exception {
		EngineProtocolManager fixture = new EngineProtocolManager(new Socket(),
				new EngineLogic(false));
		fixture.setupTroopsPlaced = 1;
		fixture.justMoved = true;
		int id = 1;
		String message = "manoeuvre";
		LinkedList<String> args = new LinkedList();

		try {
			fixture.process(id, message, args);
			} catch (NullPointerException e) {
				assertTrue(true);
			}

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this
		// test:
		// java.lang.NullPointerException
		// at risk.humanEngine.EngineLogic.getPhase(EngineLogic.java:153)
		// at
		// risk.humanEngine.EngineLogic.getManSourceDestination(EngineLogic.java:271)
		// at
		// risk.humanEngine.EngineProtocolManager.process(EngineProtocolManager.java:169)
	}

	/**
	 * Run the void process(int,String,LinkedList<String>) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 10/11/13 11:23 AM
	 */
	@Test
	public void testProcess_13() throws Exception {
		EngineProtocolManager fixture = new EngineProtocolManager(new Socket(),
				new EngineLogic(false));
		fixture.setupTroopsPlaced = 1;
		fixture.justMoved = true;
		int id = 1;
		String message = "move_troops";
		LinkedList<String> args = new LinkedList();

		try {
			fixture.process(id, message, args);
			} catch (IndexOutOfBoundsException e) {
				assertTrue(true);
			}

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this
		// test:
		// java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
		// at java.util.LinkedList.checkElementIndex(Unknown Source)
		// at java.util.LinkedList.get(Unknown Source)
		// at
		// risk.humanEngine.EngineProtocolManager.process(EngineProtocolManager.java:174)
	}

	/**
	 * Run the void process(int,String,LinkedList<String>) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 10/11/13 11:23 AM
	 */
	@Test
	public void testProcess_14() throws Exception {
		EngineProtocolManager fixture = new EngineProtocolManager(new Socket(),
				new EngineLogic(false));
		fixture.setupTroopsPlaced = 1;
		fixture.justMoved = false;
		int id = 1;
		String message = "move_troops";
		LinkedList<String> args = new LinkedList();

		try {
			fixture.process(id, message, args);
			} catch (IndexOutOfBoundsException e) {
				assertTrue(true);
			}

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this
		// test:
		// java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
		// at java.util.LinkedList.checkElementIndex(Unknown Source)
		// at java.util.LinkedList.get(Unknown Source)
		// at
		// risk.humanEngine.EngineProtocolManager.process(EngineProtocolManager.java:174)
	}

	/**
	 * Run the void process(int,String,LinkedList<String>) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 10/11/13 11:23 AM
	 */
	@Test
	public void testProcess_15() throws Exception {
		EngineProtocolManager fixture = new EngineProtocolManager(new Socket(),
				new EngineLogic(false));
		fixture.setupTroopsPlaced = 1;
		fixture.justMoved = false;
		int id = 1;
		String message = "move_troops";
		LinkedList<String> args = new LinkedList();

		try {
			fixture.process(id, message, args);
			} catch (IndexOutOfBoundsException e) {
				assertTrue(true);
			}

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this
		// test:
		// java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
		// at java.util.LinkedList.checkElementIndex(Unknown Source)
		// at java.util.LinkedList.get(Unknown Source)
		// at
		// risk.humanEngine.EngineProtocolManager.process(EngineProtocolManager.java:174)
	}

	/**
	 * Run the void process(int,String,LinkedList<String>) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 10/11/13 11:23 AM
	 */
	@Test
	public void testProcess_16() throws Exception {
		EngineProtocolManager fixture = new EngineProtocolManager(new Socket(),
				new EngineLogic(false));
		fixture.setupTroopsPlaced = 1;
		fixture.justMoved = true;
		int id = 1;
		String message = "result";
		LinkedList<String> args = new LinkedList();

		try {
			fixture.process(id, message, args);
			} catch (NoSuchElementException e) {
				assertTrue(true);
			}

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this
		// test:
		// java.util.NoSuchElementException
		// at java.util.LinkedList.getFirst(Unknown Source)
		// at
		// risk.humanEngine.EngineProtocolManager.process(EngineProtocolManager.java:216)
	}

	/**
	 * Run the void sendMessage(String) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 10/11/13 11:23 AM
	 */
	@Test
	public void testSendMessage_1() throws Exception {
		EngineProtocolManager fixture = new EngineProtocolManager(new Socket(),
				new EngineLogic(false));
		fixture.setupTroopsPlaced = 1;
		fixture.justMoved = true;
		String message = "";

		try {
		fixture.sendMessage(message);		
		} catch (NullPointerException e) {
			assert(true);
		}

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this
		// test:
		// java.lang.NullPointerException
		// at
		// risk.humanEngine.EngineProtocolManager.send(EngineProtocolManager.java:268)
		// at
		// risk.humanEngine.EngineProtocolManager.sendMessage(EngineProtocolManager.java:263)
	}

	/**
	 * Run the void sendSuccess(int,String,LinkedList<String>) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 10/11/13 11:23 AM
	 */
	@Test
	public void testSendSuccess_1() throws Exception {
		EngineProtocolManager fixture = new EngineProtocolManager(new Socket(),
				new EngineLogic(false));
		fixture.setupTroopsPlaced = 1;
		fixture.justMoved = true;
		int id = 1;
		String command = "";
		LinkedList<String> args = new LinkedList();

		try {
			fixture.sendSuccess(id, command, args);	
		} catch (NullPointerException e) {
			assert(true);
		}

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this
		// test:
		// java.lang.NullPointerException
		// at
		// risk.humanEngine.EngineProtocolManager.send(EngineProtocolManager.java:268)
		// at
		// risk.humanEngine.EngineProtocolManager.sendSuccess(EngineProtocolManager.java:246)
	}

	/**
	 * Run the void sendSuccess(int,String,LinkedList<String>) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 10/11/13 11:23 AM
	 */
	@Test
	public void testSendSuccess_2() throws Exception {
		EngineProtocolManager fixture = new EngineProtocolManager(new Socket(),
				new EngineLogic(false));
		fixture.setupTroopsPlaced = 1;
		fixture.justMoved = true;
		int id = 0;
		String command = "";
		LinkedList<String> args = new LinkedList();

		try {
			fixture.sendSuccess(id, command, args);	
		} catch (NullPointerException e) {
			assert(true);
		}
		// add additional test code here
		// An unexpected exception was thrown in user code while executing this
		// test:
		// java.lang.NullPointerException
		// at
		// risk.humanEngine.EngineProtocolManager.send(EngineProtocolManager.java:268)
		// at
		// risk.humanEngine.EngineProtocolManager.sendSuccess(EngineProtocolManager.java:246)
	}

	/**
	 * Run the void sendSuccess(int,String,LinkedList<String>) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 10/11/13 11:23 AM
	 */
	@Test
	public void testSendSuccess_3() throws Exception {
		EngineProtocolManager fixture = new EngineProtocolManager(new Socket(),
				new EngineLogic(false));
		fixture.setupTroopsPlaced = 1;
		fixture.justMoved = true;
		int id = 1;
		String command = "";
		LinkedList<String> args = new LinkedList();

		try {
			fixture.sendSuccess(id, command, args);	
		} catch (NullPointerException e) {
			assert(true);
		}

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this
		// test:
		// java.lang.NullPointerException
		// at
		// risk.humanEngine.EngineProtocolManager.send(EngineProtocolManager.java:268)
		// at
		// risk.humanEngine.EngineProtocolManager.sendSuccess(EngineProtocolManager.java:246)
	}

	/**
	 * Run the void sendSuccess(int,String,LinkedList<String>) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 10/11/13 11:23 AM
	 */
	@Test
	public void testSendSuccess_4() throws Exception {
		EngineProtocolManager fixture = new EngineProtocolManager(new Socket(),
				new EngineLogic(false));
		fixture.setupTroopsPlaced = 1;
		fixture.justMoved = true;
		int id = 0;
		String command = "";
		LinkedList<String> args = new LinkedList();

		try {
			fixture.sendSuccess(id, command, args);	
		} catch (NullPointerException e) {
			assert(true);
		}

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this
		// test:
		// java.lang.NullPointerException
		// at
		// risk.humanEngine.EngineProtocolManager.send(EngineProtocolManager.java:268)
		// at
		// risk.humanEngine.EngineProtocolManager.sendSuccess(EngineProtocolManager.java:246)
	}

	/**
	 * Run the void sendSuccess(int,String,LinkedList<String>) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 10/11/13 11:23 AM
	 */
	@Test
	public void testSendSuccess_5() throws Exception {
		EngineProtocolManager fixture = new EngineProtocolManager(new Socket(),
				new EngineLogic(false));
		fixture.setupTroopsPlaced = 1;
		fixture.justMoved = true;
		int id = 1;
		String command = "";
		LinkedList<String> args = null;

		try {
			fixture.sendSuccess(id, command, args);	
		} catch (NullPointerException e) {
			assert(true);
		}

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this
		// test:
		// java.lang.NullPointerException
		// at
		// risk.humanEngine.EngineProtocolManager.send(EngineProtocolManager.java:268)
		// at
		// risk.humanEngine.EngineProtocolManager.sendSuccess(EngineProtocolManager.java:246)
	}

	/**
	 * Run the void sendSuccess(int,String,LinkedList<String>) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 10/11/13 11:23 AM
	 */
	@Test
	public void testSendSuccess_6() throws Exception {
		EngineProtocolManager fixture = new EngineProtocolManager(new Socket(),
				new EngineLogic(false));
		fixture.setupTroopsPlaced = 1;
		fixture.justMoved = true;
		int id = 0;
		String command = "";
		LinkedList<String> args = null;

		try {
			fixture.sendSuccess(id, command, args);	
		} catch (NullPointerException e) {
			assert(true);
		}

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this
		// test:
		// java.lang.NullPointerException
		// at
		// risk.humanEngine.EngineProtocolManager.send(EngineProtocolManager.java:268)
		// at
		// risk.humanEngine.EngineProtocolManager.sendSuccess(EngineProtocolManager.java:246)
	}

	/**
	 * Run the void sendSuccess(int,String,LinkedList<String>) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 10/11/13 11:23 AM
	 */
	@Test
	public void testSendSuccess_7() throws Exception {
		EngineProtocolManager fixture = new EngineProtocolManager(new Socket(),
				new EngineLogic(false));
		fixture.setupTroopsPlaced = 1;
		fixture.justMoved = true;
		int id = 1;
		String command = "";
		LinkedList<String> args = new LinkedList();

		try {
			fixture.sendSuccess(id, command, args);	
		} catch (NullPointerException e) {
			assert(true);
		}

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this
		// test:
		// java.lang.NullPointerException
		// at
		// risk.humanEngine.EngineProtocolManager.send(EngineProtocolManager.java:268)
		// at
		// risk.humanEngine.EngineProtocolManager.sendSuccess(EngineProtocolManager.java:246)
	}

	/**
	 * Run the void sendSuccess(int,String,LinkedList<String>) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 10/11/13 11:23 AM
	 */
	@Test
	public void testSendSuccess_8() throws Exception {
		EngineProtocolManager fixture = new EngineProtocolManager(new Socket(),
				new EngineLogic(false));
		fixture.setupTroopsPlaced = 1;
		fixture.justMoved = true;
		int id = 0;
		String command = "";
		LinkedList<String> args = new LinkedList();

		try {
			fixture.sendSuccess(id, command, args);	
		} catch (NullPointerException e) {
			assert(true);
		}

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this
		// test:
		// java.lang.NullPointerException
		// at
		// risk.humanEngine.EngineProtocolManager.send(EngineProtocolManager.java:268)
		// at
		// risk.humanEngine.EngineProtocolManager.sendSuccess(EngineProtocolManager.java:246)
	}

	/**
	 * Perform pre-test initialization.
	 * 
	 * @throws Exception
	 *             if the initialization fails for some reason
	 * 
	 * @generatedBy CodePro at 10/11/13 11:23 AM
	 */
	@Before
	public void setUp() throws Exception {
		// add additional set up code here
	}

	/**
	 * Perform post-test clean-up.
	 * 
	 * @throws Exception
	 *             if the clean-up fails for some reason
	 * 
	 * @generatedBy CodePro at 10/11/13 11:23 AM
	 */
	@After
	public void tearDown() throws Exception {
		// Add additional tear down code here
	}

	/**
	 * Launch the test.
	 * 
	 * @param args
	 *            the command line arguments
	 * 
	 * @generatedBy CodePro at 10/11/13 11:23 AM
	 */
	public static void main(String[] args) {
		new org.junit.runner.JUnitCore().run(EngineProtocolManagerTest.class);
	}
}
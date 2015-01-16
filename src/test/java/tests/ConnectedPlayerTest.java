package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import risk.commonObjects.ConnectedPlayer;

/**
 * The class <code>ConnectedPlayerTest</code> contains tests for the class <code>{@link ConnectedPlayer}</code>.
 *
 * @generatedBy CodePro at 10/11/13 10:07 AM
 * @author Dirka
 * @version $Revision: 1.0 $
 */
public class ConnectedPlayerTest {
	/**
	 * Run the ConnectedPlayer(int,String) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testConnectedPlayer_1()
		throws Exception {
		int id = 1;
		String name = "";

		ConnectedPlayer result = new ConnectedPlayer(id, name);

		// add additional test code here
		assertNotNull(result);
		assertEquals(1, result.getId());
		assertEquals("", result.getName());
		assertEquals("", result.getPath());
		assertEquals(null, result.getOutput());
		assertEquals(null, result.getInput());
		assertEquals(false, result.isAI());
		assertEquals(0, result.getPortNR());
		assertEquals(null, result.getSocket());
		assertEquals(null, result.getInetAddress());
	}

	/**
	 * Run the ConnectedPlayer(Socket,int,int,InetAddress) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testConnectedPlayer_2()
		throws Exception {
		Socket client = new Socket();
		int id = 1;
		int portNR = 1;
		InetAddress host = InetAddress.getLocalHost();

		ConnectedPlayer result = new ConnectedPlayer(client, id, portNR, host);

		// add additional test code here
		assertNotNull(result);
		assertEquals(1, result.getId());
		assertEquals(null, result.getName());
		assertEquals("", result.getPath());
		assertEquals(null, result.getOutput());
		assertEquals(null, result.getInput());
		assertEquals(false, result.isAI());
		assertEquals(1, result.getPortNR());
	}

	/**
	 * Run the ConnectedPlayer(Socket,int,int,InetAddress) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testConnectedPlayer_3()
		throws Exception {
		Socket client = new Socket();
		int id = 1;
		int portNR = 1;
		InetAddress host = InetAddress.getLocalHost();

		ConnectedPlayer result = new ConnectedPlayer(client, id, portNR, host);

		// add additional test code here
		assertNotNull(result);
		assertEquals(1, result.getId());
		assertEquals(null, result.getName());
		assertEquals("", result.getPath());
		assertEquals(null, result.getOutput());
		assertEquals(null, result.getInput());
		assertEquals(false, result.isAI());
		assertEquals(1, result.getPortNR());
	}

	/**
	 * Run the ConnectedPlayer(Socket,int,int,InetAddress) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testConnectedPlayer_4()
		throws Exception {
		Socket client = new Socket();
		int id = 1;
		int portNR = 1;
		InetAddress host = InetAddress.getLocalHost();

		ConnectedPlayer result = new ConnectedPlayer(client, id, portNR, host);

		// add additional test code here
		assertNotNull(result);
		assertEquals(1, result.getId());
		assertEquals(null, result.getName());
		assertEquals("", result.getPath());
		assertEquals(null, result.getOutput());
		assertEquals(null, result.getInput());
		assertEquals(false, result.isAI());
		assertEquals(1, result.getPortNR());
	}

	/**
	 * Run the void closeAll() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testCloseAll_1()
		throws Exception {
		ConnectedPlayer fixture = new ConnectedPlayer(new Socket(), 1, 1, InetAddress.getLocalHost());
		fixture.setAI(true);
		fixture.setPath("");
		fixture.setPortNR(1);
		fixture.setId(1);
		fixture.setHostAddress(InetAddress.getLocalHost());
		fixture.setName("");

		fixture.closeAll();

		// add additional test code here
	}

	/**
	 * Run the void closeAll() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testCloseAll_2()
		throws Exception {
		ConnectedPlayer fixture = new ConnectedPlayer(new Socket(), 1, 1, InetAddress.getLocalHost());
		fixture.setAI(true);
		fixture.setPath("");
		fixture.setPortNR(1);
		fixture.setId(1);
		fixture.setHostAddress(InetAddress.getLocalHost());
		fixture.setName("");

		fixture.closeAll();

		// add additional test code here
	}

	/**
	 * Run the void closeAll() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testCloseAll_3()
		throws Exception {
		ConnectedPlayer fixture = new ConnectedPlayer(new Socket(), 1, 1, InetAddress.getLocalHost());
		fixture.setAI(true);
		fixture.setPath("");
		fixture.setPortNR(1);
		fixture.setId(1);
		fixture.setHostAddress(InetAddress.getLocalHost());
		fixture.setName("");

		fixture.closeAll();

		// add additional test code here
	}

	/**
	 * Run the void closeAll() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test(expected = NullPointerException.class)
	public void testCloseAll_4()
		throws Exception {
		ConnectedPlayer fixture = new ConnectedPlayer((Socket) null, 1, 1, InetAddress.getLocalHost());
		fixture.setAI(true);
		fixture.setPath("");
		fixture.setPortNR(1);
		fixture.setId(1);
		fixture.setHostAddress(InetAddress.getLocalHost());
		fixture.setName("");

		fixture.closeAll();

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.NullPointerException
		//       at risk.commonObjects.ConnectedPlayer.<init>(ConnectedPlayer.java:39)
	}

	/**
	 * Run the void closeAll() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test(expected = NullPointerException.class)
	public void testCloseAll_5()
		throws Exception {
		ConnectedPlayer fixture = new ConnectedPlayer((Socket) null, 1, 1, InetAddress.getLocalHost());
		fixture.setAI(true);
		fixture.setPath("");
		fixture.setPortNR(1);
		fixture.setId(1);
		fixture.setHostAddress(InetAddress.getLocalHost());
		fixture.setName("");

		fixture.closeAll();

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.NullPointerException
		//       at risk.commonObjects.ConnectedPlayer.<init>(ConnectedPlayer.java:39)
	}

	/**
	 * Run the void closeAll() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test(expected = NullPointerException.class)
	public void testCloseAll_6()
		throws Exception {
		ConnectedPlayer fixture = new ConnectedPlayer((Socket) null, 1, 1, InetAddress.getLocalHost());
		fixture.setAI(true);
		fixture.setPath("");
		fixture.setPortNR(1);
		fixture.setId(1);
		fixture.setHostAddress(InetAddress.getLocalHost());
		fixture.setName("");

		fixture.closeAll();

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.NullPointerException
		//       at risk.commonObjects.ConnectedPlayer.<init>(ConnectedPlayer.java:39)
	}

	/**
	 * Run the void closeAll() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test(expected = NullPointerException.class)
	public void testCloseAll_7()
		throws Exception {
		ConnectedPlayer fixture = new ConnectedPlayer((Socket) null, 1, 1, InetAddress.getLocalHost());
		fixture.setAI(true);
		fixture.setPath("");
		fixture.setPortNR(1);
		fixture.setId(1);
		fixture.setHostAddress(InetAddress.getLocalHost());
		fixture.setName("");

		fixture.closeAll();

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.NullPointerException
		//       at risk.commonObjects.ConnectedPlayer.<init>(ConnectedPlayer.java:39)
	}

	/**
	 * Run the void closeAll() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testCloseAll_8()
		throws Exception {
		ConnectedPlayer fixture = new ConnectedPlayer(new Socket(), 1, 1, InetAddress.getLocalHost());
		fixture.setAI(true);
		fixture.setPath("");
		fixture.setPortNR(1);
		fixture.setId(1);
		fixture.setHostAddress(InetAddress.getLocalHost());
		fixture.setName("");

		fixture.closeAll();

		// add additional test code here
	}

	/**
	 * Run the void closeAll() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testCloseAll_9()
		throws Exception {
		ConnectedPlayer fixture = new ConnectedPlayer(new Socket(), 1, 1, InetAddress.getLocalHost());
		fixture.setAI(true);
		fixture.setPath("");
		fixture.setPortNR(1);
		fixture.setId(1);
		fixture.setHostAddress(InetAddress.getLocalHost());
		fixture.setName("");

		fixture.closeAll();

		// add additional test code here
	}

	/**
	 * Run the void closeAll() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testCloseAll_10()
		throws Exception {
		ConnectedPlayer fixture = new ConnectedPlayer(new Socket(), 1, 1, InetAddress.getLocalHost());
		fixture.setAI(true);
		fixture.setPath("");
		fixture.setPortNR(1);
		fixture.setId(1);
		fixture.setHostAddress(InetAddress.getLocalHost());
		fixture.setName("");

		fixture.closeAll();

		// add additional test code here
	}

	/**
	 * Run the void closeAll() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testCloseAll_11()
		throws Exception {
		ConnectedPlayer fixture = new ConnectedPlayer(new Socket(), 1, 1, InetAddress.getLocalHost());
		fixture.setAI(true);
		fixture.setPath("");
		fixture.setPortNR(1);
		fixture.setId(1);
		fixture.setHostAddress(InetAddress.getLocalHost());
		fixture.setName("");

		fixture.closeAll();

		// add additional test code here
	}

	/**
	 * Run the void closeAll() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testCloseAll_12()
		throws Exception {
		ConnectedPlayer fixture = new ConnectedPlayer(new Socket(), 1, 1, InetAddress.getLocalHost());
		fixture.setAI(true);
		fixture.setPath("");
		fixture.setPortNR(1);
		fixture.setId(1);
		fixture.setHostAddress(InetAddress.getLocalHost());
		fixture.setName("");

		fixture.closeAll();

		// add additional test code here
	}

	/**
	 * Run the void closeAll() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testCloseAll_13()
		throws Exception {
		ConnectedPlayer fixture = new ConnectedPlayer(new Socket(), 1, 1, InetAddress.getLocalHost());
		fixture.setAI(true);
		fixture.setPath("");
		fixture.setPortNR(1);
		fixture.setId(1);
		fixture.setHostAddress(InetAddress.getLocalHost());
		fixture.setName("");

		fixture.closeAll();

		// add additional test code here
	}

	/**
	 * Run the void closeAll() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testCloseAll_14()
		throws Exception {
		ConnectedPlayer fixture = new ConnectedPlayer(new Socket(), 1, 1, InetAddress.getLocalHost());
		fixture.setAI(true);
		fixture.setPath("");
		fixture.setPortNR(1);
		fixture.setId(1);
		fixture.setHostAddress(InetAddress.getLocalHost());
		fixture.setName("");

		fixture.closeAll();

		// add additional test code here
	}

	/**
	 * Run the void closeAll() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testCloseAll_15()
		throws Exception {
		ConnectedPlayer fixture = new ConnectedPlayer(new Socket(), 1, 1, InetAddress.getLocalHost());
		fixture.setAI(true);
		fixture.setPath("");
		fixture.setPortNR(1);
		fixture.setId(1);
		fixture.setHostAddress(InetAddress.getLocalHost());
		fixture.setName("");

		fixture.closeAll();

		// add additional test code here
	}

	/**
	 * Run the void closeAll() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testCloseAll_16()
		throws Exception {
		ConnectedPlayer fixture = new ConnectedPlayer(new Socket(), 1, 1, InetAddress.getLocalHost());
		fixture.setAI(true);
		fixture.setPath("");
		fixture.setPortNR(1);
		fixture.setId(1);
		fixture.setHostAddress(InetAddress.getLocalHost());
		fixture.setName("");

		fixture.closeAll();

		// add additional test code here
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
		ConnectedPlayer fixture = new ConnectedPlayer(new Socket(), 1, 1, InetAddress.getLocalHost());
		fixture.setAI(true);
		fixture.setPath("");
		fixture.setPortNR(1);
		fixture.setId(1);
		fixture.setHostAddress(InetAddress.getLocalHost());
		fixture.setName("");

		int result = fixture.getId();

		// add additional test code here
		assertEquals(1, result);
	}

	/**
	 * Run the InetAddress getInetAddress() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testGetInetAddress_1()
		throws Exception {
		ConnectedPlayer fixture = new ConnectedPlayer(new Socket(), 1, 1, InetAddress.getLocalHost());
		fixture.setAI(true);
		fixture.setPath("");
		fixture.setPortNR(1);
		fixture.setId(1);
		fixture.setHostAddress(InetAddress.getLocalHost());
		fixture.setName("");

		InetAddress result = fixture.getInetAddress();

		// add additional test code here
		assertNotNull(result);
		/* TODO: Find out portable way to put sensible tests here, or delete
		assertEquals("Dirka-PC/10.10.11.82", result.toString());
		assertEquals("10.10.11.82", result.getHostAddress());
		assertEquals("Dirka-PC", result.getHostName());
		assertEquals("Dirka-PC", result.getCanonicalHostName()); */
		System.out.println(result.toString());
		assertEquals(false, result.isAnyLocalAddress());
		assertEquals(false, result.isLinkLocalAddress());
		/* assertEquals(false, result.isLoopbackAddress());
		 * TODO: Not sure why this is necessary, but fails on my home system - RSK 20150115
		 */
		assertEquals(false, result.isMCLinkLocal());
		assertEquals(false, result.isMCNodeLocal());
		assertEquals(false, result.isMCSiteLocal());
		assertEquals(false, result.isMulticastAddress());
		/*assertEquals(true, result.isSiteLocalAddress());
		 * TODO: Not sure why this is necessary, but fails on my home system - RSK 20150115
		 */
		assertEquals(false, result.isMCGlobal());
		assertEquals(false, result.isMCOrgLocal());
	}

	/**
	 * Run the BufferedReader getInput() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testGetInput_1()
		throws Exception {
		ConnectedPlayer fixture = new ConnectedPlayer(new Socket(), 1, 1, InetAddress.getLocalHost());
		fixture.setAI(true);
		fixture.setPath("");
		fixture.setPortNR(1);
		fixture.setId(1);
		fixture.setHostAddress(InetAddress.getLocalHost());
		fixture.setName("");

		BufferedReader result = fixture.getInput();

		// add additional test code here
		assertEquals(null, result);
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
		ConnectedPlayer fixture = new ConnectedPlayer(new Socket(), 1, 1, InetAddress.getLocalHost());
		fixture.setAI(true);
		fixture.setPath("");
		fixture.setPortNR(1);
		fixture.setId(1);
		fixture.setHostAddress(InetAddress.getLocalHost());
		fixture.setName("");

		String result = fixture.getName();

		// add additional test code here
		assertEquals("", result);
	}

	/**
	 * Run the PrintWriter getOutput() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testGetOutput_1()
		throws Exception {
		ConnectedPlayer fixture = new ConnectedPlayer(new Socket(), 1, 1, InetAddress.getLocalHost());
		fixture.setAI(true);
		fixture.setPath("");
		fixture.setPortNR(1);
		fixture.setId(1);
		fixture.setHostAddress(InetAddress.getLocalHost());
		fixture.setName("");

		PrintWriter result = fixture.getOutput();

		// add additional test code here
		assertEquals(null, result);
	}

	/**
	 * Run the String getPath() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testGetPath_1()
		throws Exception {
		ConnectedPlayer fixture = new ConnectedPlayer(new Socket(), 1, 1, InetAddress.getLocalHost());
		fixture.setAI(true);
		fixture.setPath("");
		fixture.setPortNR(1);
		fixture.setId(1);
		fixture.setHostAddress(InetAddress.getLocalHost());
		fixture.setName("");

		String result = fixture.getPath();

		// add additional test code here
		assertEquals("", result);
	}

	/**
	 * Run the int getPortNR() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testGetPortNR_1()
		throws Exception {
		ConnectedPlayer fixture = new ConnectedPlayer(new Socket(), 1, 1, InetAddress.getLocalHost());
		fixture.setAI(true);
		fixture.setPath("");
		fixture.setPortNR(1);
		fixture.setId(1);
		fixture.setHostAddress(InetAddress.getLocalHost());
		fixture.setName("");

		int result = fixture.getPortNR();

		// add additional test code here
		assertEquals(1, result);
	}

	/**
	 * Run the Socket getSocket() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testGetSocket_1()
		throws Exception {
		ConnectedPlayer fixture = new ConnectedPlayer(new Socket(), 1, 1, InetAddress.getLocalHost());
		fixture.setAI(true);
		fixture.setPath("");
		fixture.setPortNR(1);
		fixture.setId(1);
		fixture.setHostAddress(InetAddress.getLocalHost());
		fixture.setName("");

		Socket result = fixture.getSocket();

		// add additional test code here
		assertNotNull(result);
		assertEquals("Socket[unconnected]", result.toString());
		assertEquals(null, result.getChannel());
		assertEquals(0, result.getPort());
		assertEquals(false, result.isClosed());
		assertEquals(false, result.isBound());
		assertEquals(false, result.isConnected());
		assertEquals(8192, result.getSendBufferSize());
		assertEquals(false, result.getTcpNoDelay());
		assertEquals(0, result.getTrafficClass());
		assertEquals(null, result.getRemoteSocketAddress());
		assertEquals(false, result.isInputShutdown());
		assertEquals(false, result.isOutputShutdown());
		assertEquals(0, result.getSoTimeout());
		assertEquals(null, result.getInetAddress());
		assertEquals(null, result.getLocalSocketAddress());
		/* assertEquals(8192, result.getReceiveBufferSize());
		 * TODO: Assuming the exact size is not important, but this fails on my laptop
		 * Could be a Windows vs Linux thing? RSK 20150115
		 */
		assertEquals(false, result.getReuseAddress());
		assertEquals(false, result.getKeepAlive());
		assertEquals(-1, result.getSoLinger());
		assertEquals(false, result.getOOBInline());
		assertEquals(-1, result.getLocalPort());
	}

	/**
	 * Run the boolean isAI() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testIsAI_1()
		throws Exception {
		ConnectedPlayer fixture = new ConnectedPlayer(new Socket(), 1, 1, InetAddress.getLocalHost());
		fixture.setAI(true);
		fixture.setPath("");
		fixture.setPortNR(1);
		fixture.setId(1);
		fixture.setHostAddress(InetAddress.getLocalHost());
		fixture.setName("");

		boolean result = fixture.isAI();

		// add additional test code here
		assertEquals(true, result);
	}

	/**
	 * Run the boolean isAI() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testIsAI_2()
		throws Exception {
		ConnectedPlayer fixture = new ConnectedPlayer(new Socket(), 1, 1, InetAddress.getLocalHost());
		fixture.setAI(false);
		fixture.setPath("");
		fixture.setPortNR(1);
		fixture.setId(1);
		fixture.setHostAddress(InetAddress.getLocalHost());
		fixture.setName("");

		boolean result = fixture.isAI();

		// add additional test code here
		assertEquals(false, result);
	}

	/**
	 * Run the void send(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testSend_1()
		throws Exception {
		ConnectedPlayer fixture = new ConnectedPlayer(new Socket(), 1, 1, InetAddress.getLocalHost());
		fixture.setAI(true);
		fixture.setPath("");
		fixture.setPortNR(1);
		fixture.setId(1);
		fixture.setHostAddress(InetAddress.getLocalHost());
		fixture.setName("");

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.NullPointerException
		//       at risk.commonObjects.ConnectedPlayer.send(ConnectedPlayer.java:47)
	}

	/**
	 * Run the void setAI(boolean) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testSetAI_1()
		throws Exception {
		ConnectedPlayer fixture = new ConnectedPlayer(new Socket(), 1, 1, InetAddress.getLocalHost());
		fixture.setAI(true);
		fixture.setPath("");
		fixture.setPortNR(1);
		fixture.setId(1);
		fixture.setHostAddress(InetAddress.getLocalHost());
		fixture.setName("");
		boolean ai = true;

		fixture.setAI(ai);

		// add additional test code here
	}

	/**
	 * Run the void setHostAddress(InetAddress) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testSetHostAddress_1()
		throws Exception {
		ConnectedPlayer fixture = new ConnectedPlayer(new Socket(), 1, 1, InetAddress.getLocalHost());
		fixture.setAI(true);
		fixture.setPath("");
		fixture.setPortNR(1);
		fixture.setId(1);
		fixture.setHostAddress(InetAddress.getLocalHost());
		fixture.setName("");
		InetAddress hostAddress = InetAddress.getLocalHost();

		fixture.setHostAddress(hostAddress);

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
		ConnectedPlayer fixture = new ConnectedPlayer(new Socket(), 1, 1, InetAddress.getLocalHost());
		fixture.setAI(true);
		fixture.setPath("");
		fixture.setPortNR(1);
		fixture.setId(1);
		fixture.setHostAddress(InetAddress.getLocalHost());
		fixture.setName("");
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
		ConnectedPlayer fixture = new ConnectedPlayer(new Socket(), 1, 1, InetAddress.getLocalHost());
		fixture.setAI(true);
		fixture.setPath("");
		fixture.setPortNR(1);
		fixture.setId(1);
		fixture.setHostAddress(InetAddress.getLocalHost());
		fixture.setName("");
		String name = "";

		fixture.setName(name);

		// add additional test code here
	}

	/**
	 * Run the void setPath(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testSetPath_1()
		throws Exception {
		ConnectedPlayer fixture = new ConnectedPlayer(new Socket(), 1, 1, InetAddress.getLocalHost());
		fixture.setAI(true);
		fixture.setPath("");
		fixture.setPortNR(1);
		fixture.setId(1);
		fixture.setHostAddress(InetAddress.getLocalHost());
		fixture.setName("");
		String p = "";

		fixture.setPath(p);

		// add additional test code here
	}

	/**
	 * Run the void setPortNR(int) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testSetPortNR_1()
		throws Exception {
		ConnectedPlayer fixture = new ConnectedPlayer(new Socket(), 1, 1, InetAddress.getLocalHost());
		fixture.setAI(true);
		fixture.setPath("");
		fixture.setPortNR(1);
		fixture.setId(1);
		fixture.setHostAddress(InetAddress.getLocalHost());
		fixture.setName("");
		int portNR = 1;

		fixture.setPortNR(portNR);

		// add additional test code here
	}

	/**
	 * Run the void startIO() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testStartIO_1()
		throws Exception {
		ConnectedPlayer fixture = new ConnectedPlayer(new Socket(), 1, 1, InetAddress.getLocalHost());
		fixture.setAI(true);
		fixture.setPath("");
		fixture.setPortNR(1);
		fixture.setId(1);
		fixture.setHostAddress(InetAddress.getLocalHost());
		fixture.setName("");

		fixture.startIO();

		// add additional test code here
	}

	/**
	 * Run the void startIO() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testStartIO_2()
		throws Exception {
		ConnectedPlayer fixture = new ConnectedPlayer(new Socket(), 1, 1, InetAddress.getLocalHost());
		fixture.setAI(true);
		fixture.setPath("");
		fixture.setPortNR(1);
		fixture.setId(1);
		fixture.setHostAddress(InetAddress.getLocalHost());
		fixture.setName("");

		fixture.startIO();

		// add additional test code here
	}

	/**
	 * Run the void startIO() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:07 AM
	 */
	@Test
	public void testStartIO_3()
		throws Exception {
		ConnectedPlayer fixture = new ConnectedPlayer(new Socket(), 1, 1, InetAddress.getLocalHost());
		fixture.setAI(true);
		fixture.setPath("");
		fixture.setPortNR(1);
		fixture.setId(1);
		fixture.setHostAddress(InetAddress.getLocalHost());
		fixture.setName("");

		fixture.startIO();

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
		new org.junit.runner.JUnitCore().run(ConnectedPlayerTest.class);
	}
}
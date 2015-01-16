package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import risk.commonObjects.Logger;

/**
 * The class <code>LoggerTest</code> contains tests for the class <code>{@link Logger}</code>.
 *
 * @generatedBy CodePro at 10/11/13 10:10 AM
 * @author Dirka
 * @version $Revision: 1.0 $
 */
public class LoggerTest {
	/**
	 * Run the Logger() constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:10 AM
	 */
	@Test
	public void testLogger_1()
		throws Exception {

		Logger result = new Logger();

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.SecurityException: Cannot write to files while generating test cases
		//       at com.instantiations.assist.eclipse.junit.CodeProJUnitSecurityManager.checkWrite(CodeProJUnitSecurityManager.java:76)
		//       at java.io.FileOutputStream.<init>(Unknown Source)
		//       at java.io.FileOutputStream.<init>(Unknown Source)
		//       at java.io.FileWriter.<init>(Unknown Source)
		//       at risk.commonObjects.Logger.<init>(Logger.java:31)
		//       at risk.commonObjects.Logger.<init>(Logger.java:26)
		assertNotNull(result);
	}

	/**
	 * Run the Logger(int) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:10 AM
	 */
	@Test
	public void testLogger_2()
		throws Exception {
		int level = 1;

		Logger result = new Logger(level);

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.SecurityException: Cannot write to files while generating test cases
		//       at com.instantiations.assist.eclipse.junit.CodeProJUnitSecurityManager.checkWrite(CodeProJUnitSecurityManager.java:76)
		//       at java.io.FileOutputStream.<init>(Unknown Source)
		//       at java.io.FileOutputStream.<init>(Unknown Source)
		//       at java.io.FileWriter.<init>(Unknown Source)
		//       at risk.commonObjects.Logger.<init>(Logger.java:31)
		assertNotNull(result);
	}

	/**
	 * Run the Logger(int) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:10 AM
	 */
	@Test
	public void testLogger_3()
		throws Exception {
		int level = 1;

		Logger result = new Logger(level);

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.SecurityException: Cannot write to files while generating test cases
		//       at com.instantiations.assist.eclipse.junit.CodeProJUnitSecurityManager.checkWrite(CodeProJUnitSecurityManager.java:76)
		//       at java.io.FileOutputStream.<init>(Unknown Source)
		//       at java.io.FileOutputStream.<init>(Unknown Source)
		//       at java.io.FileWriter.<init>(Unknown Source)
		//       at risk.commonObjects.Logger.<init>(Logger.java:31)
		assertNotNull(result);
	}

	/**
	 * Run the void endLog() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:10 AM
	 */
	@Test
	public void testEndLog_1()
		throws Exception {
		Logger fixture = new Logger();
		fixture.setDebugLevel(1);

		fixture.endLog();

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.SecurityException: Cannot write to files while generating test cases
		//       at com.instantiations.assist.eclipse.junit.CodeProJUnitSecurityManager.checkWrite(CodeProJUnitSecurityManager.java:76)
		//       at java.io.FileOutputStream.<init>(Unknown Source)
		//       at java.io.FileOutputStream.<init>(Unknown Source)
		//       at java.io.FileWriter.<init>(Unknown Source)
		//       at risk.commonObjects.Logger.<init>(Logger.java:31)
		//       at risk.commonObjects.Logger.<init>(Logger.java:26)
	}

	/**
	 * Run the void endLog() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:10 AM
	 */
	@Test
	public void testEndLog_2()
		throws Exception {
		Logger fixture = new Logger();
		fixture.setDebugLevel(1);

		fixture.endLog();

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.SecurityException: Cannot write to files while generating test cases
		//       at com.instantiations.assist.eclipse.junit.CodeProJUnitSecurityManager.checkWrite(CodeProJUnitSecurityManager.java:76)
		//       at java.io.FileOutputStream.<init>(Unknown Source)
		//       at java.io.FileOutputStream.<init>(Unknown Source)
		//       at java.io.FileWriter.<init>(Unknown Source)
		//       at risk.commonObjects.Logger.<init>(Logger.java:31)
		//       at risk.commonObjects.Logger.<init>(Logger.java:26)
	}

	/**
	 * Run the int getDebugLevel() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:10 AM
	 */
	@Test
	public void testGetDebugLevel_1()
		throws Exception {
		Logger fixture = new Logger();
		fixture.setDebugLevel(1);

		int result = fixture.getDebugLevel();

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.SecurityException: Cannot write to files while generating test cases
		//       at com.instantiations.assist.eclipse.junit.CodeProJUnitSecurityManager.checkWrite(CodeProJUnitSecurityManager.java:76)
		//       at java.io.FileOutputStream.<init>(Unknown Source)
		//       at java.io.FileOutputStream.<init>(Unknown Source)
		//       at java.io.FileWriter.<init>(Unknown Source)
		//       at risk.commonObjects.Logger.<init>(Logger.java:31)
		//       at risk.commonObjects.Logger.<init>(Logger.java:26)
		assertEquals(1, result);
	}

	/**
	 * Run the void log(int,String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:10 AM
	 */
	@Test
	public void testLog_1()
		throws Exception {
		Logger fixture = new Logger();
		fixture.setDebugLevel(1);
		int level = 1;
		String message = "";

		fixture.log(level, message);

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.SecurityException: Cannot write to files while generating test cases
		//       at com.instantiations.assist.eclipse.junit.CodeProJUnitSecurityManager.checkWrite(CodeProJUnitSecurityManager.java:76)
		//       at java.io.FileOutputStream.<init>(Unknown Source)
		//       at java.io.FileOutputStream.<init>(Unknown Source)
		//       at java.io.FileWriter.<init>(Unknown Source)
		//       at risk.commonObjects.Logger.<init>(Logger.java:31)
		//       at risk.commonObjects.Logger.<init>(Logger.java:26)
	}

	/**
	 * Run the void log(int,String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:10 AM
	 */
	@Test
	public void testLog_2()
		throws Exception {
		Logger fixture = new Logger();
		fixture.setDebugLevel(1);
		int level = 1;
		String message = "";

		fixture.log(level, message);

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.SecurityException: Cannot write to files while generating test cases
		//       at com.instantiations.assist.eclipse.junit.CodeProJUnitSecurityManager.checkWrite(CodeProJUnitSecurityManager.java:76)
		//       at java.io.FileOutputStream.<init>(Unknown Source)
		//       at java.io.FileOutputStream.<init>(Unknown Source)
		//       at java.io.FileWriter.<init>(Unknown Source)
		//       at risk.commonObjects.Logger.<init>(Logger.java:31)
		//       at risk.commonObjects.Logger.<init>(Logger.java:26)
	}

	/**
	 * Run the void log(int,String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:10 AM
	 */
	@Test
	public void testLog_3()
		throws Exception {
		Logger fixture = new Logger();
		fixture.setDebugLevel(1);
		int level = 1;
		String message = "";

		fixture.log(level, message);

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.SecurityException: Cannot write to files while generating test cases
		//       at com.instantiations.assist.eclipse.junit.CodeProJUnitSecurityManager.checkWrite(CodeProJUnitSecurityManager.java:76)
		//       at java.io.FileOutputStream.<init>(Unknown Source)
		//       at java.io.FileOutputStream.<init>(Unknown Source)
		//       at java.io.FileWriter.<init>(Unknown Source)
		//       at risk.commonObjects.Logger.<init>(Logger.java:31)
		//       at risk.commonObjects.Logger.<init>(Logger.java:26)
	}

	/**
	 * Run the void setDebugLevel(int) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:10 AM
	 */
	@Test
	public void testSetDebugLevel_1()
		throws Exception {
		Logger fixture = new Logger();
		fixture.setDebugLevel(1);
		int debugLevel = 1;

		fixture.setDebugLevel(debugLevel);

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.SecurityException: Cannot write to files while generating test cases
		//       at com.instantiations.assist.eclipse.junit.CodeProJUnitSecurityManager.checkWrite(CodeProJUnitSecurityManager.java:76)
		//       at java.io.FileOutputStream.<init>(Unknown Source)
		//       at java.io.FileOutputStream.<init>(Unknown Source)
		//       at java.io.FileWriter.<init>(Unknown Source)
		//       at risk.commonObjects.Logger.<init>(Logger.java:31)
		//       at risk.commonObjects.Logger.<init>(Logger.java:26)
	}

	/**
	 * Run the String toString() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:10 AM
	 */
	@Test
	public void testToString_1()
		throws Exception {
		Logger fixture = new Logger();
		fixture.setDebugLevel(1);

		String result = fixture.toString();

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.SecurityException: Cannot write to files while generating test cases
		//       at com.instantiations.assist.eclipse.junit.CodeProJUnitSecurityManager.checkWrite(CodeProJUnitSecurityManager.java:76)
		//       at java.io.FileOutputStream.<init>(Unknown Source)
		//       at java.io.FileOutputStream.<init>(Unknown Source)
		//       at java.io.FileWriter.<init>(Unknown Source)
		//       at risk.commonObjects.Logger.<init>(Logger.java:31)
		//       at risk.commonObjects.Logger.<init>(Logger.java:26)
		assertNotNull(result);
	}

	/**
	 * Run the String toString() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:10 AM
	 */
	@Test
	public void testToString_2()
		throws Exception {
		Logger fixture = new Logger();
		fixture.setDebugLevel(1);

		String result = fixture.toString();

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.SecurityException: Cannot write to files while generating test cases
		//       at com.instantiations.assist.eclipse.junit.CodeProJUnitSecurityManager.checkWrite(CodeProJUnitSecurityManager.java:76)
		//       at java.io.FileOutputStream.<init>(Unknown Source)
		//       at java.io.FileOutputStream.<init>(Unknown Source)
		//       at java.io.FileWriter.<init>(Unknown Source)
		//       at risk.commonObjects.Logger.<init>(Logger.java:31)
		//       at risk.commonObjects.Logger.<init>(Logger.java:26)
		assertNotNull(result);
	}

	/**
	 * Run the String toString() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:10 AM
	 */
	@Test
	public void testToString_3()
		throws Exception {
		Logger fixture = new Logger();
		fixture.setDebugLevel(1);

		String result = fixture.toString();

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.SecurityException: Cannot write to files while generating test cases
		//       at com.instantiations.assist.eclipse.junit.CodeProJUnitSecurityManager.checkWrite(CodeProJUnitSecurityManager.java:76)
		//       at java.io.FileOutputStream.<init>(Unknown Source)
		//       at java.io.FileOutputStream.<init>(Unknown Source)
		//       at java.io.FileWriter.<init>(Unknown Source)
		//       at risk.commonObjects.Logger.<init>(Logger.java:31)
		//       at risk.commonObjects.Logger.<init>(Logger.java:26)
		assertNotNull(result);
	}

	/**
	 * Run the String toString() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:10 AM
	 */
	@Test
	public void testToString_4()
		throws Exception {
		Logger fixture = new Logger();
		fixture.setDebugLevel(1);

		String result = fixture.toString();

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.SecurityException: Cannot write to files while generating test cases
		//       at com.instantiations.assist.eclipse.junit.CodeProJUnitSecurityManager.checkWrite(CodeProJUnitSecurityManager.java:76)
		//       at java.io.FileOutputStream.<init>(Unknown Source)
		//       at java.io.FileOutputStream.<init>(Unknown Source)
		//       at java.io.FileWriter.<init>(Unknown Source)
		//       at risk.commonObjects.Logger.<init>(Logger.java:31)
		//       at risk.commonObjects.Logger.<init>(Logger.java:26)
		assertNotNull(result);
	}

	/**
	 * Run the String toString() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 10:10 AM
	 */
	@Test
	public void testToString_5()
		throws Exception {
		Logger fixture = new Logger();
		fixture.setDebugLevel(1);

		String result = fixture.toString();

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.SecurityException: Cannot write to files while generating test cases
		//       at com.instantiations.assist.eclipse.junit.CodeProJUnitSecurityManager.checkWrite(CodeProJUnitSecurityManager.java:76)
		//       at java.io.FileOutputStream.<init>(Unknown Source)
		//       at java.io.FileOutputStream.<init>(Unknown Source)
		//       at java.io.FileWriter.<init>(Unknown Source)
		//       at risk.commonObjects.Logger.<init>(Logger.java:31)
		//       at risk.commonObjects.Logger.<init>(Logger.java:26)
		assertNotNull(result);
	}

	/**
	 * Perform pre-test initialization.
	 *
	 * @throws Exception
	 *         if the initialization fails for some reason
	 *
	 * @generatedBy CodePro at 10/11/13 10:10 AM
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
	 * @generatedBy CodePro at 10/11/13 10:10 AM
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
	 * @generatedBy CodePro at 10/11/13 10:10 AM
	 */
	public static void main(String[] args) {
		new org.junit.runner.JUnitCore().run(LoggerTest.class);
	}
}
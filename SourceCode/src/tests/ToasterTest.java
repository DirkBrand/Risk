package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.plaf.IconUIResource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import risk.gui.Toaster;

/**
 * The class <code>ToasterTest</code> contains tests for the class <code>{@link Toaster}</code>.
 *
 * @generatedBy CodePro at 10/11/13 11:17 AM
 * @author Dirka
 * @version $Revision: 1.0 $
 */
public class ToasterTest {
	/**
	 * Run the Toaster(JFrame,int) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 11:17 AM
	 */
	@Test
	public void testToaster_1()
		throws Exception {
		JFrame parent = new JFrame();
		int delayTime = 1;

		Toaster result = new Toaster(parent, delayTime);

		// add additional test code here
		assertNotNull(result);
		assertEquals(null, result.getBackgroundImage());
		assertEquals(300, result.getToasterWidth());
		assertEquals(1, result.getDisplayTime());
		assertEquals(80, result.getToasterHeight());
		assertEquals(20, result.getStep());
		assertEquals(10, result.getStepTime());
		assertEquals(0, result.getMargin());
	}

	/**
	 * Run the Toaster(JFrame,int) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 11:17 AM
	 */
	@Test
	public void testToaster_2()
		throws Exception {
		JFrame parent = new JFrame();
		int delayTime = 1;

		Toaster result = new Toaster(parent, delayTime);

		// add additional test code here
		assertNotNull(result);
		assertEquals(null, result.getBackgroundImage());
		assertEquals(300, result.getToasterWidth());
		assertEquals(1, result.getDisplayTime());
		assertEquals(80, result.getToasterHeight());
		assertEquals(20, result.getStep());
		assertEquals(10, result.getStepTime());
		assertEquals(0, result.getMargin());
	}

	/**
	 * Run the Toaster(JFrame,int) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 11:17 AM
	 */
	@Test
	public void testToaster_3()
		throws Exception {
		JFrame parent = new JFrame();
		int delayTime = 1;

		Toaster result = new Toaster(parent, delayTime);

		// add additional test code here
		assertNotNull(result);
		assertEquals(null, result.getBackgroundImage());
		assertEquals(300, result.getToasterWidth());
		assertEquals(1, result.getDisplayTime());
		assertEquals(80, result.getToasterHeight());
		assertEquals(20, result.getStep());
		assertEquals(10, result.getStepTime());
		assertEquals(0, result.getMargin());
	}

	/**
	 * Run the Image getBackgroundImage() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 11:17 AM
	 */
	@Test
	public void testGetBackgroundImage_1()
		throws Exception {
		Toaster fixture = new Toaster(new JFrame(), 1);
		fixture.setToasterHeight(1);
		fixture.setStep(1);
		fixture.setBackgroundImage((Image) null);
		fixture.setToasterColor(new Color(1));
		fixture.setBorderColor(new Color(1));
		fixture.setMessageColor(new Color(1));
		fixture.setToasterWidth(1);
		fixture.setMargin(1);
		fixture.setStepTime(1);
		fixture.useAlwaysOnTop = true;

		Image result = fixture.getBackgroundImage();

		// add additional test code here
		assertEquals(null, result);
	}

	/**
	 * Run the Color getBorderColor() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 11:17 AM
	 */
	@Test
	public void testGetBorderColor_1()
		throws Exception {
		Toaster fixture = new Toaster(new JFrame(), 1);
		fixture.setToasterHeight(1);
		fixture.setStep(1);
		fixture.setBackgroundImage((Image) null);
		fixture.setToasterColor(new Color(1));
		fixture.setBorderColor(new Color(1));
		fixture.setMessageColor(new Color(1));
		fixture.setToasterWidth(1);
		fixture.setMargin(1);
		fixture.setStepTime(1);
		fixture.useAlwaysOnTop = true;

		Color result = fixture.getBorderColor();

		// add additional test code here
		assertNotNull(result);
		assertEquals("java.awt.Color[r=0,g=0,b=1]", result.toString());
		assertEquals(255, result.getAlpha());
		assertEquals(-16777215, result.getRGB());
		assertEquals(1, result.getBlue());
		assertEquals(0, result.getRed());
		assertEquals(0, result.getGreen());
		assertEquals(1, result.getTransparency());
	}

	/**
	 * Run the int getDisplayTime() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 11:17 AM
	 */
	@Test
	public void testGetDisplayTime_1()
		throws Exception {
		Toaster fixture = new Toaster(new JFrame(), 1);
		fixture.setToasterHeight(1);
		fixture.setStep(1);
		fixture.setBackgroundImage((Image) null);
		fixture.setToasterColor(new Color(1));
		fixture.setBorderColor(new Color(1));
		fixture.setMessageColor(new Color(1));
		fixture.setToasterWidth(1);
		fixture.setMargin(1);
		fixture.setStepTime(1);
		fixture.useAlwaysOnTop = true;

		int result = fixture.getDisplayTime();

		// add additional test code here
		assertEquals(1, result);
	}

	/**
	 * Run the int getMargin() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 11:17 AM
	 */
	@Test
	public void testGetMargin_1()
		throws Exception {
		Toaster fixture = new Toaster(new JFrame(), 1);
		fixture.setToasterHeight(1);
		fixture.setStep(1);
		fixture.setBackgroundImage((Image) null);
		fixture.setToasterColor(new Color(1));
		fixture.setBorderColor(new Color(1));
		fixture.setMessageColor(new Color(1));
		fixture.setToasterWidth(1);
		fixture.setMargin(1);
		fixture.setStepTime(1);
		fixture.useAlwaysOnTop = true;

		int result = fixture.getMargin();

		// add additional test code here
		assertEquals(1, result);
	}

	/**
	 * Run the Color getMessageColor() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 11:17 AM
	 */
	@Test
	public void testGetMessageColor_1()
		throws Exception {
		Toaster fixture = new Toaster(new JFrame(), 1);
		fixture.setToasterHeight(1);
		fixture.setStep(1);
		fixture.setBackgroundImage((Image) null);
		fixture.setToasterColor(new Color(1));
		fixture.setBorderColor(new Color(1));
		fixture.setMessageColor(new Color(1));
		fixture.setToasterWidth(1);
		fixture.setMargin(1);
		fixture.setStepTime(1);
		fixture.useAlwaysOnTop = true;

		Color result = fixture.getMessageColor();

		// add additional test code here
		assertNotNull(result);
		assertEquals("java.awt.Color[r=0,g=0,b=1]", result.toString());
		assertEquals(255, result.getAlpha());
		assertEquals(-16777215, result.getRGB());
		assertEquals(1, result.getBlue());
		assertEquals(0, result.getRed());
		assertEquals(0, result.getGreen());
		assertEquals(1, result.getTransparency());
	}

	/**
	 * Run the int getStep() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 11:17 AM
	 */
	@Test
	public void testGetStep_1()
		throws Exception {
		Toaster fixture = new Toaster(new JFrame(), 1);
		fixture.setToasterHeight(1);
		fixture.setStep(1);
		fixture.setBackgroundImage((Image) null);
		fixture.setToasterColor(new Color(1));
		fixture.setBorderColor(new Color(1));
		fixture.setMessageColor(new Color(1));
		fixture.setToasterWidth(1);
		fixture.setMargin(1);
		fixture.setStepTime(1);
		fixture.useAlwaysOnTop = true;

		int result = fixture.getStep();

		// add additional test code here
		assertEquals(1, result);
	}

	/**
	 * Run the int getStepTime() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 11:17 AM
	 */
	@Test
	public void testGetStepTime_1()
		throws Exception {
		Toaster fixture = new Toaster(new JFrame(), 1);
		fixture.setToasterHeight(1);
		fixture.setStep(1);
		fixture.setBackgroundImage((Image) null);
		fixture.setToasterColor(new Color(1));
		fixture.setBorderColor(new Color(1));
		fixture.setMessageColor(new Color(1));
		fixture.setToasterWidth(1);
		fixture.setMargin(1);
		fixture.setStepTime(1);
		fixture.useAlwaysOnTop = true;

		int result = fixture.getStepTime();

		// add additional test code here
		assertEquals(1, result);
	}

	/**
	 * Run the Color getToasterColor() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 11:17 AM
	 */
	@Test
	public void testGetToasterColor_1()
		throws Exception {
		Toaster fixture = new Toaster(new JFrame(), 1);
		fixture.setToasterHeight(1);
		fixture.setStep(1);
		fixture.setBackgroundImage((Image) null);
		fixture.setToasterColor(new Color(1));
		fixture.setBorderColor(new Color(1));
		fixture.setMessageColor(new Color(1));
		fixture.setToasterWidth(1);
		fixture.setMargin(1);
		fixture.setStepTime(1);
		fixture.useAlwaysOnTop = true;

		Color result = fixture.getToasterColor();

		// add additional test code here
		assertNotNull(result);
		assertEquals("java.awt.Color[r=0,g=0,b=1]", result.toString());
		assertEquals(255, result.getAlpha());
		assertEquals(-16777215, result.getRGB());
		assertEquals(1, result.getBlue());
		assertEquals(0, result.getRed());
		assertEquals(0, result.getGreen());
		assertEquals(1, result.getTransparency());
	}

	/**
	 * Run the int getToasterHeight() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 11:17 AM
	 */
	@Test
	public void testGetToasterHeight_1()
		throws Exception {
		Toaster fixture = new Toaster(new JFrame(), 1);
		fixture.setToasterHeight(1);
		fixture.setStep(1);
		fixture.setBackgroundImage((Image) null);
		fixture.setToasterColor(new Color(1));
		fixture.setBorderColor(new Color(1));
		fixture.setMessageColor(new Color(1));
		fixture.setToasterWidth(1);
		fixture.setMargin(1);
		fixture.setStepTime(1);
		fixture.useAlwaysOnTop = true;

		int result = fixture.getToasterHeight();

		// add additional test code here
		assertEquals(1, result);
	}

	

	/**
	 * Run the int getToasterWidth() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 11:17 AM
	 */
	@Test
	public void testGetToasterWidth_1()
		throws Exception {
		Toaster fixture = new Toaster(new JFrame(), 1);
		fixture.setToasterHeight(1);
		fixture.setStep(1);
		fixture.setBackgroundImage((Image) null);
		fixture.setToasterColor(new Color(1));
		fixture.setBorderColor(new Color(1));
		fixture.setMessageColor(new Color(1));
		fixture.setToasterWidth(1);
		fixture.setMargin(1);
		fixture.setStepTime(1);
		fixture.useAlwaysOnTop = true;

		int result = fixture.getToasterWidth();

		// add additional test code here
		assertEquals(1, result);
	}

	/**
	 * Run the void setBackgroundImage(Image) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 11:17 AM
	 */
	@Test
	public void testSetBackgroundImage_1()
		throws Exception {
		Toaster fixture = new Toaster(new JFrame(), 1);
		fixture.setToasterHeight(1);
		fixture.setStep(1);
		fixture.setBackgroundImage((Image) null);
		fixture.setToasterColor(new Color(1));
		fixture.setBorderColor(new Color(1));
		fixture.setMessageColor(new Color(1));
		fixture.setToasterWidth(1);
		fixture.setMargin(1);
		fixture.setStepTime(1);
		fixture.useAlwaysOnTop = true;
		Image backgroundImage = null;

		fixture.setBackgroundImage(backgroundImage);

		// add additional test code here
	}

	/**
	 * Run the void setBorderColor(Color) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 11:17 AM
	 */
	@Test
	public void testSetBorderColor_1()
		throws Exception {
		Toaster fixture = new Toaster(new JFrame(), 1);
		fixture.setToasterHeight(1);
		fixture.setStep(1);
		fixture.setBackgroundImage((Image) null);
		fixture.setToasterColor(new Color(1));
		fixture.setBorderColor(new Color(1));
		fixture.setMessageColor(new Color(1));
		fixture.setToasterWidth(1);
		fixture.setMargin(1);
		fixture.setStepTime(1);
		fixture.useAlwaysOnTop = true;
		Color borderColor = new Color(1);

		fixture.setBorderColor(borderColor);

		// add additional test code here
	}

	/**
	 * Run the void setDisplayTime(int) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 11:17 AM
	 */
	@Test
	public void testSetDisplayTime_1()
		throws Exception {
		Toaster fixture = new Toaster(new JFrame(), 1);
		fixture.setToasterHeight(1);
		fixture.setStep(1);
		fixture.setBackgroundImage((Image) null);
		fixture.setToasterColor(new Color(1));
		fixture.setBorderColor(new Color(1));
		fixture.setMessageColor(new Color(1));
		fixture.setToasterWidth(1);
		fixture.setMargin(1);
		fixture.setStepTime(1);
		fixture.useAlwaysOnTop = true;
		int displayTime = 1;

		fixture.setDisplayTime(displayTime);

		// add additional test code here
	}

	/**
	 * Run the void setMargin(int) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 11:17 AM
	 */
	@Test
	public void testSetMargin_1()
		throws Exception {
		Toaster fixture = new Toaster(new JFrame(), 1);
		fixture.setToasterHeight(1);
		fixture.setStep(1);
		fixture.setBackgroundImage((Image) null);
		fixture.setToasterColor(new Color(1));
		fixture.setBorderColor(new Color(1));
		fixture.setMessageColor(new Color(1));
		fixture.setToasterWidth(1);
		fixture.setMargin(1);
		fixture.setStepTime(1);
		fixture.useAlwaysOnTop = true;
		int margin = 1;

		fixture.setMargin(margin);

		// add additional test code here
	}

	/**
	 * Run the void setMessageColor(Color) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 11:17 AM
	 */
	@Test
	public void testSetMessageColor_1()
		throws Exception {
		Toaster fixture = new Toaster(new JFrame(), 1);
		fixture.setToasterHeight(1);
		fixture.setStep(1);
		fixture.setBackgroundImage((Image) null);
		fixture.setToasterColor(new Color(1));
		fixture.setBorderColor(new Color(1));
		fixture.setMessageColor(new Color(1));
		fixture.setToasterWidth(1);
		fixture.setMargin(1);
		fixture.setStepTime(1);
		fixture.useAlwaysOnTop = true;
		Color messageColor = new Color(1);

		fixture.setMessageColor(messageColor);

		// add additional test code here
	}

	/**
	 * Run the void setStep(int) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 11:17 AM
	 */
	@Test
	public void testSetStep_1()
		throws Exception {
		Toaster fixture = new Toaster(new JFrame(), 1);
		fixture.setToasterHeight(1);
		fixture.setStep(1);
		fixture.setBackgroundImage((Image) null);
		fixture.setToasterColor(new Color(1));
		fixture.setBorderColor(new Color(1));
		fixture.setMessageColor(new Color(1));
		fixture.setToasterWidth(1);
		fixture.setMargin(1);
		fixture.setStepTime(1);
		fixture.useAlwaysOnTop = true;
		int step = 1;

		fixture.setStep(step);

		// add additional test code here
	}

	/**
	 * Run the void setStepTime(int) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 11:17 AM
	 */
	@Test
	public void testSetStepTime_1()
		throws Exception {
		Toaster fixture = new Toaster(new JFrame(), 1);
		fixture.setToasterHeight(1);
		fixture.setStep(1);
		fixture.setBackgroundImage((Image) null);
		fixture.setToasterColor(new Color(1));
		fixture.setBorderColor(new Color(1));
		fixture.setMessageColor(new Color(1));
		fixture.setToasterWidth(1);
		fixture.setMargin(1);
		fixture.setStepTime(1);
		fixture.useAlwaysOnTop = true;
		int stepTime = 1;

		fixture.setStepTime(stepTime);

		// add additional test code here
	}

	/**
	 * Run the void setToasterColor(Color) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 11:17 AM
	 */
	@Test
	public void testSetToasterColor_1()
		throws Exception {
		Toaster fixture = new Toaster(new JFrame(), 1);
		fixture.setToasterHeight(1);
		fixture.setStep(1);
		fixture.setBackgroundImage((Image) null);
		fixture.setToasterColor(new Color(1));
		fixture.setBorderColor(new Color(1));
		fixture.setMessageColor(new Color(1));
		fixture.setToasterWidth(1);
		fixture.setMargin(1);
		fixture.setStepTime(1);
		fixture.useAlwaysOnTop = true;
		Color toasterColor = new Color(1);

		fixture.setToasterColor(toasterColor);

		// add additional test code here
	}

	/**
	 * Run the void setToasterHeight(int) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 11:17 AM
	 */
	@Test
	public void testSetToasterHeight_1()
		throws Exception {
		Toaster fixture = new Toaster(new JFrame(), 1);
		fixture.setToasterHeight(1);
		fixture.setStep(1);
		fixture.setBackgroundImage((Image) null);
		fixture.setToasterColor(new Color(1));
		fixture.setBorderColor(new Color(1));
		fixture.setMessageColor(new Color(1));
		fixture.setToasterWidth(1);
		fixture.setMargin(1);
		fixture.setStepTime(1);
		fixture.useAlwaysOnTop = true;
		int toasterHeight = 1;

		fixture.setToasterHeight(toasterHeight);

		// add additional test code here
	}

	/**
	 * Run the void setToasterMessageFont(Font) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 11:17 AM
	 */
	@Test
	public void testSetToasterMessageFont_1()
		throws Exception {
		Toaster fixture = new Toaster(new JFrame(), 1);
		fixture.setToasterHeight(1);
		fixture.setStep(1);
		fixture.setBackgroundImage((Image) null);
		fixture.setToasterColor(new Color(1));
		fixture.setBorderColor(new Color(1));
		fixture.setMessageColor(new Color(1));
		fixture.setToasterWidth(1);
		fixture.setMargin(1);
		fixture.setStepTime(1);
		fixture.useAlwaysOnTop = true;
		
		fixture.setToasterMessageFont(new Font("", 0, 0));

		// add additional test code here
	}

	/**
	 * Run the void setToasterWidth(int) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 11:17 AM
	 */
	@Test
	public void testSetToasterWidth_1()
		throws Exception {
		Toaster fixture = new Toaster(new JFrame(), 1);
		fixture.setToasterHeight(1);
		fixture.setStep(1);
		fixture.setBackgroundImage((Image) null);
		fixture.setToasterColor(new Color(1));
		fixture.setBorderColor(new Color(1));
		fixture.setMessageColor(new Color(1));
		fixture.setToasterWidth(1);
		fixture.setMargin(1);
		fixture.setStepTime(1);
		fixture.useAlwaysOnTop = true;
		int toasterWidth = 1;

		fixture.setToasterWidth(toasterWidth);

		// add additional test code here
	}

	/**
	 * Run the void showToaster(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 11:17 AM
	 */
	@Test
	public void testShowToaster_1()
		throws Exception {
		Toaster fixture = new Toaster(new JFrame(), 1);
		fixture.setToasterHeight(1);
		fixture.setStep(1);
		fixture.setBackgroundImage((Image) null);
		fixture.setToasterColor(new Color(1));
		fixture.setBorderColor(new Color(1));
		fixture.setMessageColor(new Color(1));
		fixture.setToasterWidth(1);
		fixture.setMargin(1);
		fixture.setStepTime(1);
		fixture.useAlwaysOnTop = true;
		String msg = "";

		fixture.showToaster(msg);

		// add additional test code here
	}

	/**
	 * Run the void showToaster(Icon,String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 11:17 AM
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testShowToaster_2()
		throws Exception {
		Toaster fixture = new Toaster(new JFrame(), 1);
		fixture.setToasterHeight(1);
		fixture.setStep(1);
		fixture.setBackgroundImage((Image) null);
		fixture.setToasterColor(new Color(1));
		fixture.setBorderColor(new Color(1));
		fixture.setMessageColor(new Color(1));
		fixture.setToasterWidth(1);
		fixture.setMargin(1);
		fixture.setStepTime(1);
		fixture.useAlwaysOnTop = true;
		Icon icon = new IconUIResource((Icon) null);
		String msg = "";

		fixture.showToaster(icon, msg);

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.IllegalArgumentException: null delegate icon argument
		//       at javax.swing.plaf.IconUIResource.<init>(Unknown Source)
	}

	/**
	 * Run the void showToaster(Icon,String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 10/11/13 11:17 AM
	 */
	@Test
	public void testShowToaster_3()
		throws Exception {
		Toaster fixture = new Toaster(new JFrame(), 1);
		fixture.setToasterHeight(1);
		fixture.setStep(1);
		fixture.setBackgroundImage((Image) null);
		fixture.setToasterColor(new Color(1));
		fixture.setBorderColor(new Color(1));
		fixture.setMessageColor(new Color(1));
		fixture.setToasterWidth(1);
		fixture.setMargin(1);
		fixture.setStepTime(1);
		fixture.useAlwaysOnTop = true;
		Icon icon = null;
		String msg = "";

		fixture.showToaster(icon, msg);

		// add additional test code here
	}

	/**
	 * Perform pre-test initialization.
	 *
	 * @throws Exception
	 *         if the initialization fails for some reason
	 *
	 * @generatedBy CodePro at 10/11/13 11:17 AM
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
	 * @generatedBy CodePro at 10/11/13 11:17 AM
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
	 * @generatedBy CodePro at 10/11/13 11:17 AM
	 */
	public static void main(String[] args) {
		new org.junit.runner.JUnitCore().run(ToasterTest.class);
	}
}
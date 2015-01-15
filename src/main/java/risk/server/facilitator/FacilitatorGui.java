package risk.server.facilitator;

import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class FacilitatorGui extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7041025301106358279L;

	private JPanel contentPane;

	JTextArea textArea;

	int screen = 1;

	/**
	 * Launch the application.
	 */
	public FacilitatorGui() {

		Initialize();

		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();


		int xoffs = 0;
		int yoffs = 0;
		
		if (gs.length > 1) {
			GraphicsDevice gd = gs[screen];
			GraphicsConfiguration[] gc = gd.getConfigurations();
			Rectangle gcBounds = gc[0].getBounds();
			xoffs = gcBounds.x;
			yoffs = gcBounds.y;
		}
		this.setBounds(200 + xoffs, 200 + yoffs, 850, 500);
	}

	/**
	 * Create the frame.
	 */
	public void Initialize() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setFont(new Font("System", Font.BOLD, 14));
		setTitle("Risk Server");
		setResizable(false);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(5, 5, 834, 462);
		contentPane.add(scrollPane);

		textArea = new JTextArea();
		textArea.setFont(new Font("Monospaced", Font.BOLD, 16));
		textArea.setLineWrap(true);
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);

		setVisible(true);
	}

	public void printToArea(String message) {
		textArea.append(message + "\n");
	}

}

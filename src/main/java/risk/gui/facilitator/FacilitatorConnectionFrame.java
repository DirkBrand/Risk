package risk.gui.facilitator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
// import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import risk.gui.gameSetup.ControlGUI;

public class FacilitatorConnectionFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3550717079673868479L;
	// private JPanel contentPane;
	private static JTextField ipField;
	ControlGUI window;
	private JTextField nameField;

	/**
	 * Create the frame.
	 */
	public FacilitatorConnectionFrame(String title, ControlGUI window) {
		setTitle(title);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		setBounds(500, 200, 321, 160);

		ImageIcon img = new ImageIcon("images/RISK_Icon.jpg");
		setIconImage(img.getImage());
		
		this.window = window;

		ipField = new JTextField();
		ipField.setBounds(179, 50, 126, 27);
		getContentPane().add(ipField);
		ipField.setColumns(10);

		JLabel lblControllerIpAddress = new JLabel("Address:");
		lblControllerIpAddress.setHorizontalAlignment(SwingConstants.RIGHT);
		lblControllerIpAddress.setBounds(62, 55, 109, 16);
		getContentPane().add(lblControllerIpAddress);

		JButton btnNewButton = new JButton("Connect");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JButton newBut = (JButton) arg0.getSource();
				if (ipField.getText().length() == 0) {
					JOptionPane.showMessageDialog(newBut.getParent(),
							"Please enter an address for the controller.",
							"Warning", JOptionPane.WARNING_MESSAGE);

				} else if (nameField.getText().length() == 0) {
					JOptionPane.showMessageDialog(newBut.getParent(),
							"Please enter a username!", "Warning",
							JOptionPane.WARNING_MESSAGE);
				} else {					
					transferControl(nameField.getText(), ipField.getText());
				}
			}
		});
		btnNewButton.setBounds(105, 94, 98, 26);
		getContentPane().add(btnNewButton);

		nameField = new JTextField();
		nameField.setColumns(10);
		nameField.setBounds(179, 12, 126, 27);
		getContentPane().add(nameField);

		JLabel lblUsername = new JLabel("Username:");
		lblUsername.setHorizontalAlignment(SwingConstants.RIGHT);
		lblUsername.setBounds(62, 22, 109, 16);
		getContentPane().add(lblUsername);
		setVisible(true);
	}

	public void setBoxes(String ip, String name, boolean check) {
		ipField.setText(ip);
		nameField.setText(name);
	}

	protected void transferControl(String name, String ip) {
		dispose();
		window.giveControl(name, ip);

	}
}

package risk.gui.gameSetup;

//import static java.awt.GraphicsDevice.WindowTranslucency.PERPIXEL_TRANSPARENT;
//import static java.awt.GraphicsDevice.WindowTranslucency.TRANSLUCENT;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.LinkedList;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import risk.commonObjects.ConnectedPlayer;
import risk.commonObjects.GameState;
import risk.commonObjects.Player;
import risk.gui.clientGui.ClientGUI;
import risk.gui.facilitator.FacilitatorConnectionFrame;
import risk.humanEngine.EngineLogic;

public class ControlGUI {

	EngineLogic EL;

	private LinkedList<String> opponents = new LinkedList<String>();
	private LinkedList<String> maps = new LinkedList<String>();

	Color playColor = Color.red;
	Color oppColor = Color.black;

	GameState game;

	public JFrame frame;

	private static JFrame addressFrame;
	static ControlGUI window;
	private JColorChooser playerChooser;
	private JColorChooser oppChooser;

	private JLabel lblO2;
	private JLabel lblO;

	private JList oppList;
	private JList mapsList;

	private static JTextField textField_2;
	private static JTextField textField_3;

	private JLabel lblUsername;

	/**
	 * Create the application.
	 * 
	 * @param engineLogic
	 */
	public ControlGUI(EngineLogic engineLogic, boolean facilitatorFirst) {
		this.EL = engineLogic;

		if (facilitatorFirst) {
			FacilitatorConnectionFrame fac = new FacilitatorConnectionFrame(
					"Facilitator Connection", this);
		}
		initialize();

		// Only temporary
		/* TODO */
		//fac.setBoxes("localhost", "Dirk", true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Game Lobby");
		frame.setResizable(false);
		frame.setBounds(400, 200, 386, 406);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		ImageIcon img = new ImageIcon("images/RISK_Icon.jpg");
		frame.setIconImage(img.getImage());

		JLabel lblOpponents = new JLabel("Opponents");
		lblOpponents.setHorizontalAlignment(SwingConstants.CENTER);
		lblOpponents.setFont(new Font("Trebuchet MS", Font.BOLD, 14));
		lblOpponents.setBounds(241, 23, 94, 21);
		frame.getContentPane().add(lblOpponents);

		String[] opp = new String[opponents.size() + 1];
		int index = 1;
		for (String s : opponents)
			opp[index++] = s;

		opp[0] = "HumanPlayer";
		oppList = new JList(opp);
		oppList.setFont(new Font("Trebuchet MS", Font.BOLD, 14));
		oppList.setVisibleRowCount(-1);

		JScrollPane listPane = new JScrollPane((Component) null);
		listPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		listPane.setBounds(205, 56, 163, 128);
		listPane.setViewportView(oppList);
		frame.getContentPane().add(listPane);

		playerChooser = new JColorChooser();
		playerChooser.setPreviewPanel(new JPanel());
		AbstractColorChooserPanel[] oldPanels = playerChooser
				.getChooserPanels();

		for (int i = 0; i < oldPanels.length; i++) {
			String clsName = oldPanels[i].getClass().getName();
			playerChooser.removeChooserPanel(oldPanels[i]);

		}
		playerChooser.addChooserPanel(new AbstractColorChooserPanel() {
			public void buildChooser() {
				setLayout(new GridLayout(0, 3));
				makeAddButton("Red", Color.red);
				makeAddButton("Green", new Color(81, 163, 5));
				makeAddButton("Blue", Color.blue);
				makeAddButton("Orange", new Color(232, 174, 39));
				makeAddButton("Black", Color.black);
				makeAddButton("Grey", Color.GRAY);
			}

			public void updateChooser() {
			}

			public String getDisplayName() {
				return "ChooseColor";
			}

			public Icon getSmallDisplayIcon() {
				return null;
			}

			public Icon getLargeDisplayIcon() {
				return null;
			}

			private void makeAddButton(String name, Color color) {
				JButton button = new JButton(name);
				button.setBackground(color);
				button.setAction(setColorAction);
				add(button);
			}

			Action setColorAction = new AbstractAction() {
				public void actionPerformed(ActionEvent evt) {
					JButton button = (JButton) evt.getSource();
					getColorSelectionModel().setSelectedColor(
							button.getBackground());
					playColor = button.getBackground();
					lblO.setForeground(playColor);
				}
			};
		});
		playerChooser.setBounds(26, 115, 113, 46);

		frame.getContentPane().add(playerChooser);

		JButton btnStartGame = new JButton("START GAME");
		btnStartGame.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				if (oppList.getSelectedIndex() == -1) {
					JOptionPane.showMessageDialog(frame,
							"Please select an opponent!", "Warning",
							JOptionPane.WARNING_MESSAGE);

				} else if (mapsList.getSelectedIndex() == -1) {
					JOptionPane.showMessageDialog(frame,
							"Please select a map!", "Warning",
							JOptionPane.WARNING_MESSAGE);

				} else if (playColor.toString().equalsIgnoreCase(
						oppColor.toString())) {
					JOptionPane.showMessageDialog(frame,
							"Players cannot be the same colour!", "Warning",
							JOptionPane.WARNING_MESSAGE);

				} else {
					Player human = new Player(1, EL.getUsername());
					String opponentName = oppList.getSelectedValue().toString();
					Player opponent = new Player(2, opponentName);
					String map = mapsList.getSelectedValue().toString();

					EL.setColours(playColor, oppColor);
					EL.sendInitialChoices(human, opponent, map);
				}
			}
		});

		btnStartGame.setBounds(127, 341, 123, 26);
		frame.getContentPane().add(btnStartGame);

		JLabel lblPlayerColour = new JLabel("Player Colour:");
		lblPlayerColour.setHorizontalAlignment(SwingConstants.CENTER);
		lblPlayerColour.setFont(new Font("Trebuchet MS", Font.BOLD, 14));
		lblPlayerColour.setBounds(39, 90, 113, 21);
		frame.getContentPane().add(lblPlayerColour);

		JLabel lblOpponentColour = new JLabel("Opponent Colour:");
		lblOpponentColour.setHorizontalAlignment(SwingConstants.CENTER);
		lblOpponentColour.setFont(new Font("Trebuchet MS", Font.BOLD, 14));
		lblOpponentColour.setBounds(26, 190, 144, 21);
		frame.getContentPane().add(lblOpponentColour);

		oppChooser = new JColorChooser();
		oppChooser.setPreviewPanel(new JPanel());
		oldPanels = oppChooser.getChooserPanels();
		for (int i = 0; i < oldPanels.length; i++) {
			String clsName = oldPanels[i].getClass().getName();
			oppChooser.removeChooserPanel(oldPanels[i]);

		}
		oppChooser.addChooserPanel(new AbstractColorChooserPanel() {

			public void buildChooser() {
				setLayout(new GridLayout(0, 3));
				makeAddButton("Red", Color.red);
				makeAddButton("Green", new Color(81, 163, 5));
				makeAddButton("Blue", Color.blue);
				makeAddButton("Orange", new Color(232, 174, 39));
				makeAddButton("Black", Color.black);
				makeAddButton("Grey", Color.GRAY);
			}

			public void updateChooser() {
			}

			public String getDisplayName() {
				return "ChooseColor";
			}

			public Icon getSmallDisplayIcon() {
				return null;
			}

			public Icon getLargeDisplayIcon() {
				return null;
			}

			private void makeAddButton(String name, Color color) {
				JButton button = new JButton(name);
				button.setBackground(color);
				button.setAction(setColorAction);
				add(button);
			}

			Action setColorAction = new AbstractAction() {
				public void actionPerformed(ActionEvent evt) {
					JButton button = (JButton) evt.getSource();
					getColorSelectionModel().setSelectedColor(
							button.getBackground());
					oppColor = button.getBackground();
					lblO2.setForeground(oppColor);
				}
			};
		});

		oppChooser.getSelectionModel().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
			}
		});
		oppChooser.setBounds(26, 212, 113, 46);
		frame.getContentPane().add(oppChooser);

		lblO = new JLabel("\u2605");
		lblO.setForeground(playColor);
		lblO.setFont(new Font("Dialog", Font.BOLD, 25));
		lblO.setBounds(157, 115, 26, 32);
		frame.getContentPane().add(lblO);

		lblO2 = new JLabel("\u2605");
		lblO2.setForeground(oppColor);
		lblO2.setFont(new Font("Dialog", Font.BOLD, 25));
		lblO2.setBounds(157, 212, 26, 32);
		frame.getContentPane().add(lblO2);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(205, 225, 163, 105);
		frame.getContentPane().add(scrollPane);

		mapsList = new JList(maps.toArray());
		mapsList.setFont(new Font("Trebuchet MS", Font.PLAIN, 14));
		scrollPane.setViewportView(mapsList);

		JLabel lblMaps = new JLabel("Maps");
		lblMaps.setHorizontalAlignment(SwingConstants.CENTER);
		lblMaps.setFont(new Font("Trebuchet MS", Font.BOLD, 14));
		lblMaps.setBounds(241, 195, 94, 21);
		frame.getContentPane().add(lblMaps);

		lblUsername = new JLabel();
		lblUsername.setHorizontalAlignment(SwingConstants.CENTER);
		lblUsername.setFont(new Font("Trebuchet MS", Font.BOLD, 18));
		lblUsername.setBounds(32, 15, 107, 32);
		frame.getContentPane().add(lblUsername);
	}

	public void close() {
		frame.dispose();
	}

	public void giveControl(String name, String ip) {
		EL.establishFacilitatorConnection(ip);
		EL.setIP(ip);
		EL.setUsername(name);
		lblUsername.setText(EL.getUsername());
		frame.setVisible(true);
	}

	public void setOpponents(LinkedList<String> opps) {

		opponents = opps;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				DefaultListModel listModel = new DefaultListModel();
				for (String op : opponents) {
					listModel.addElement(op);
				}
				oppList.setModel(listModel);
			}
		});
	}

	public void setMaps(LinkedList<String> mapslist) {
		this.maps = mapslist;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				DefaultListModel listModel = new DefaultListModel();

				for (String mp : maps) {
					listModel.addElement(mp);
				}
				mapsList.setModel(listModel);
			}
		});
	}
}

package risk.gui.clientGui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import risk.commonObjects.GameState;
import risk.commonObjects.Player;
import risk.commonObjects.Territory;
import risk.gui.Toaster;
import risk.gui.gameSetup.ControlGUI;
import risk.humanEngine.EngineLogic;

public class ClientGUI {

	GameState game;

	LinkedList<Territory> terrs = new LinkedList<Territory>();
	LinkedList<Territory> selectedTerrs = new LinkedList<Territory>();

	private static JFrame frame;

	Color playerColour = Color.red;
	Color oppColour = Color.blue;

	// private GridBagConstraints c_1;
	private GridBagConstraints c_2;
	private JTable territoryTable;

	JPanel content;

	JPanel cardPanel;
	CardLayout cards;

	JSeparator separator1;
	JSeparator separator2;
	JLabel lblPlayer1;
	JLabel lblPlayer2;
	JLabel lblPhase;
	JLabel lblPhaseValue;

	int recruitedTroops;
	int recruitedSum;

	int spinnerMin = 0;

	private JSeparator battlePanelSeparator2;
	private JTextArea battleResultArea;
	private JLabel lblManPanelSource;
	private JLabel lblManPanelDestination;
	private JComboBox<String> manSourceComboBox;
	private JComboBox<String> manDestComboBox;
	private JLabel lblManPanelNumber;
	private JLabel lblTroopCount;
	private JComboBox<String> battleSourceComboBox;
	private JComboBox<String> battleDestComboBox;
	private JSlider spinner;

	private JButton btnTroopDone;
	private JButton btnAttackAgain;
	private JButton btnDoneAttack;
	private JButton btnManDone;

	private JPanel allPanel;

	JPanel mapPanel;

	private MapBackgroundPanel mapContentPanel;

	private boolean yourTurn = false;

	EngineLogic EL;

	public ClientGUI(GameState game, EngineLogic engineLogic) {

		this.EL = engineLogic;
		this.game = game;

		initialize();

		updateMap();
		frame.setVisible(true);

		NotMyTurnGlassPane glassPane = new NotMyTurnGlassPane();
		glassPane.setOpaque(false);
		glassPane.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		frame.setGlassPane(glassPane);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(200, 200, 1300, 600);
		frame.setFont(new Font("System", Font.BOLD, 14));
		frame.setTitle("RISK <" + EL.getUsername() + ">");
		frame.setResizable(false);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				int choice = JOptionPane.showConfirmDialog(frame, "Would you like to restart the game?", "Game Over",
						JOptionPane.YES_NO_OPTION);

				if (choice == JOptionPane.YES_OPTION) {
					frame.dispose();
					//EL = new EngineLogic(false);
					EL.initialize();
					EL.controlGui = new ControlGUI(EL, false);
					EL.controlGui.giveControl(EL.getUsername(), EL.getIP());
				} else {
					System.exit(0);
				}
			}
		});

		ImageIcon img = new ImageIcon("images/RISK_Icon.jpg");
		frame.setIconImage(img.getImage());

		allPanel = new JPanel();

		/* LEFT PANEL CONTENT */

		content = new JPanel();
		content.setBackground(new Color(19, 172, 207));
		content.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		content.setLayout(null);

		separator2 = new JSeparator(SwingConstants.HORIZONTAL);
		separator2.setBounds(1, 94, (int) (frame.getWidth() / 3.5), 3);
		content.add(separator2);
		separator2.setForeground(new Color(0, 102, 255));
		separator2.setBackground(Color.BLUE);

		lblPlayer1 = new JLabel(game.getPlayers().get(0).getName());
		lblPlayer1.setBounds(20, 15, 200, 20);
		content.add(lblPlayer1);
		lblPlayer1.setFont(new Font("Trebuchet MS", Font.BOLD, 18));
		lblPlayer1.setHorizontalAlignment(SwingConstants.CENTER);

		lblPlayer2 = new JLabel("<html>" + game.getPlayers().get(1).getName()
				+ "</html>");
		lblPlayer2.setBounds((int) (frame.getWidth() / 3.5) - 220, 15, 200, 20);
		content.add(lblPlayer2);
		lblPlayer2.setHorizontalAlignment(SwingConstants.CENTER);
		lblPlayer2.setFont(new Font("Trebuchet MS", Font.BOLD, 18));

		lblPhase = new JLabel("Phase:");
		lblPhase.setBounds((int) (frame.getWidth() / 3.5) / 2 - 95, 61, 75, 20);
		content.add(lblPhase);
		lblPhase.setHorizontalAlignment(SwingConstants.CENTER);
		lblPhase.setFont(new Font("Trebuchet MS", Font.BOLD, 18));

		lblPhaseValue = new JLabel("Recruitment");
		lblPhaseValue.setBounds((int) (frame.getWidth() / 3.5) / 2 - 20, 61,
				135, 20);
		content.add(lblPhaseValue);
		lblPhaseValue.setHorizontalAlignment(SwingConstants.LEFT);
		lblPhaseValue.setFont(new Font("Trebuchet MS", Font.BOLD, 18));

		separator1 = new JSeparator(SwingConstants.HORIZONTAL);
		separator1.setBounds(1, 47, (int) (frame.getWidth() / 3.5), 3);
		content.add(separator1);
		separator1.setBackground(Color.BLACK);
		separator1.setForeground(Color.BLACK);

		cards = new CardLayout();

		cardPanel = new JPanel();
		cardPanel.setLayout(cards);
		cards.show(cardPanel, "name_troopPanel");
		cardPanel.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		cardPanel.setBounds(11, 108, (int) (frame.getWidth() / 3.5) - 30,
				frame.getHeight() - 164);
		content.add(cardPanel);

		/* Recruitement */

		JPanel troopPanel = new JPanel();
		troopPanel.setBackground(new Color(34, 224, 91));
		troopPanel.setLayout(null);

		JSeparator separator_1 = new JSeparator(SwingConstants.HORIZONTAL);
		separator_1.setBounds(0, 44, cardPanel.getWidth(), 2);
		separator_1.setForeground(new Color(0, 102, 0));
		separator_1.setBackground(Color.LIGHT_GRAY);
		troopPanel.add(separator_1);

		JLabel lblRecruitedTroops = new JLabel("Recruited Troops:");
		lblRecruitedTroops.setHorizontalAlignment(SwingConstants.CENTER);
		lblRecruitedTroops.setFont(new Font("Trebuchet MS", Font.BOLD, 18));
		lblRecruitedTroops.setBounds(10, 11, 200, 22);
		troopPanel.add(lblRecruitedTroops);

		lblTroopCount = new JLabel(recruitedTroops + "");
		lblTroopCount.setHorizontalAlignment(SwingConstants.LEFT);
		lblTroopCount.setFont(new Font("Trebuchet MS", Font.BOLD, 18));
		lblTroopCount.setBounds(206, 12, 80, 20);
		troopPanel.add(lblTroopCount);

		TerritoryTableModel temp = new TerritoryTableModel();
		// Player tempPlay = game.getCurrentPlayer();

		Object[][] data = new Object[0][0];

		temp.setData(data);
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JLabel.CENTER);

		territoryTable = new JTable(temp);
		TerritoryTableButtonRenderer buttonRenderer = new TerritoryTableButtonRenderer();
		territoryTable.getColumn("Add").setCellRenderer(buttonRenderer);
		territoryTable.getColumn("Min").setCellRenderer(buttonRenderer);

		territoryTable.setRowHeight(22);
		territoryTable.getColumnModel().getColumn(1)
				.setCellRenderer(rightRenderer);
		territoryTable.getColumnModel().getColumn(1).setMaxWidth(50);
		territoryTable.getColumnModel().getColumn(3).setMaxWidth(50);
		territoryTable.getColumnModel().getColumn(2).setMaxWidth(50);

		territoryTable.addMouseListener(new JTableButtonMouseListener(
				territoryTable));

		JScrollPane territoryTableScrollPane = new JScrollPane(territoryTable);
		territoryTableScrollPane.setBounds(10, 57, cardPanel.getWidth() - 24,
				cardPanel.getHeight() - 120);
		territoryTableScrollPane.setBackground(Color.lightGray);
		territoryTableScrollPane.setBorder(BorderFactory
				.createLineBorder(Color.black));
		troopPanel.add(territoryTableScrollPane);

		JSeparator troopPanelSeparator_2 = new JSeparator(
				SwingConstants.HORIZONTAL);
		troopPanelSeparator_2.setForeground(new Color(0, 102, 0));
		troopPanelSeparator_2.setBackground(Color.LIGHT_GRAY);
		troopPanelSeparator_2.setBounds(0, cardPanel.getHeight() - 50,
				cardPanel.getWidth(), 2);
		troopPanel.add(troopPanelSeparator_2);

		btnTroopDone = new JButton("DONE");
		btnTroopDone.setFont(new Font("Trebuchet MS", Font.BOLD, 13));
		btnTroopDone.setBounds(
				cardPanel.getX() + cardPanel.getWidth() / 2 - 60,
				troopPanelSeparator_2.getY() + 15, 89, 22);
		btnTroopDone.addActionListener(new LoaderAction());
		troopPanel.add(btnTroopDone);

		/* Battle */

		JPanel battlePanel = new JPanel();
		battlePanel.setBackground(new Color(34, 224, 91));
		battlePanel.setLayout(null);

		JSeparator battlePanelSeparator1 = new JSeparator();
		battlePanelSeparator1.setForeground(Color.BLACK);
		battlePanelSeparator1.setBackground(Color.BLACK);
		battlePanelSeparator1.setBounds(0, 73, cardPanel.getWidth(), 3);
		battlePanel.add(battlePanelSeparator1);

		JLabel lblBattleSource = new JLabel("Source:");
		lblBattleSource.setHorizontalAlignment(SwingConstants.LEFT);
		lblBattleSource.setFont(new Font("Trebuchet MS", Font.BOLD, 18));
		lblBattleSource.setBounds(20, 11, 75, 20);
		battlePanel.add(lblBattleSource);

		JLabel lblBattleDest = new JLabel("Destination:");
		lblBattleDest.setHorizontalAlignment(SwingConstants.LEFT);
		lblBattleDest.setFont(new Font("Trebuchet MS", Font.BOLD, 18));
		lblBattleDest.setBounds(20, 42, 130, 20);
		battlePanel.add(lblBattleDest);

		String[] ters = new String[game.getCurrentPlayer().getTerritories()
				.size()];

		battleSourceComboBox = new JComboBox<String>(ters);
		battleSourceComboBox.setBounds(140, 14, 151, 20);
		battlePanel.add(battleSourceComboBox);

		battleDestComboBox = new JComboBox<String>(ters);
		battleDestComboBox.setBounds(140, 45, 151, 20);
		battlePanel.add(battleDestComboBox);

		battlePanelSeparator2 = new JSeparator();
		battlePanelSeparator2.setForeground(Color.BLACK);
		battlePanelSeparator2.setBackground(Color.BLACK);
		battlePanelSeparator2.setBounds(0, cardPanel.getHeight() - 50,
				cardPanel.getWidth(), 3);
		battlePanel.add(battlePanelSeparator2);

		battleResultArea = new JTextArea();
		battleResultArea.setLineWrap(true);

		JScrollPane battleResultScrollPane = new JScrollPane();
		battleResultScrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		battleResultScrollPane.setViewportView(battleResultArea);
		battleResultScrollPane.setBounds(10, 84, cardPanel.getWidth() - 24,
				cardPanel.getHeight() - 150);
		battlePanel.add(battleResultScrollPane);

		btnAttackAgain = new JButton("Attack!");
		btnAttackAgain.addActionListener(new LoaderAction());
		btnAttackAgain.setBounds(cardPanel.getX() + cardPanel.getWidth() / 2
				- 130, battlePanelSeparator2.getY() + 15, 98, 23);
		battlePanel.add(btnAttackAgain);

		btnDoneAttack = new JButton("Done Attacking");
		btnDoneAttack.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				EL.sendAttackReply(new LinkedList<String>());
				EL.setPhase(GameState.MANOEUVRE);
			}
		});
		btnDoneAttack.setBounds(cardPanel.getX() + cardPanel.getWidth() / 2
				- 15, battlePanelSeparator2.getY() + 15, 128, 23);
		battlePanel.add(btnDoneAttack);

		/* Manoeuvre */

		JPanel manPanel = new JPanel();
		manPanel.setBackground(new Color(34, 224, 91));
		manPanel.setLayout(null);

		lblManPanelSource = new JLabel("Source: ");
		lblManPanelSource.setHorizontalAlignment(SwingConstants.LEFT);
		lblManPanelSource.setFont(new Font("Trebuchet MS", Font.BOLD, 18));
		lblManPanelSource.setBounds(20, 11, 75, 20);
		manPanel.add(lblManPanelSource);

		lblManPanelDestination = new JLabel("Destination:");
		lblManPanelDestination.setHorizontalAlignment(SwingConstants.LEFT);
		lblManPanelDestination.setFont(new Font("Trebuchet MS", Font.BOLD, 18));
		lblManPanelDestination.setBounds(20, 42, 109, 20);
		manPanel.add(lblManPanelDestination);

		manSourceComboBox = new JComboBox<String>();
		manSourceComboBox.setBounds(140, 14, 121, 20);
		manPanel.add(manSourceComboBox);

		manDestComboBox = new JComboBox<String>();
		manDestComboBox.setBounds(140, 45, 121, 20);
		manPanel.add(manDestComboBox);

		lblManPanelNumber = new JLabel("Number:");
		lblManPanelNumber.setHorizontalAlignment(SwingConstants.LEFT);
		lblManPanelNumber.setFont(new Font("Trebuchet MS", Font.BOLD, 18));
		lblManPanelNumber.setBounds(20, 92, 109, 20);
		manPanel.add(lblManPanelNumber);

		spinner = new JSlider(JSlider.HORIZONTAL);
		spinner.setBorder(BorderFactory.createLineBorder(Color.black));
		spinner.setBounds(140, 92, 170, 50);
		spinner.setMajorTickSpacing(2);
		spinner.setMinorTickSpacing(1);
		spinner.setPaintLabels(true);
		spinner.setPaintTicks(true);
		spinner.setSnapToTicks(true);
		manPanel.add(spinner);

		btnManDone = new JButton("DONE");
		btnManDone.setFont(new Font("Trebuchet MS", Font.BOLD, 13));
		btnManDone.addActionListener(new LoaderAction());
		btnManDone.setBounds(cardPanel.getX() + cardPanel.getWidth() / 2 - 60,
				spinner.getY() + 60, 98, 23);
		manPanel.add(btnManDone);

		/* Add to card panel */
		cardPanel.add(troopPanel, "name_troopPanel");
		cardPanel.add(battlePanel, "name_battlePanel");
		cardPanel.add(manPanel, "name_manPanel");

		/* MAP PANEL */
		mapPanel = new JPanel();
		mapPanel.setBackground(Color.white);
		double factor = 0.7;

		try {
			mapContentPanel = new MapBackgroundPanel("images/"
					+ game.getImgLocation(), factor);
		} catch (IOException e) {
			System.out.println("Map panel could not be added\n");
			e.printStackTrace();
		}
		mapPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		mapPanel.setLayout(new BorderLayout(0, 0));
		mapContentPanel.setName("mapPanel");

		mapPanel.add(mapContentPanel);
		System.out.println();
		frame.setSize(mapContentPanel.getImageWidth() + 570,
				mapContentPanel.getImageHeight() + 50);
		/* PUT TOGETHER */
		GridBagLayout lay = new GridBagLayout();

		allPanel.setLayout(lay);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;

		c.insets = new Insets(2, 2, 2, 2);

		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = 0;

		allPanel.add(content, c);

		allPanel.setLayout(lay);
		c_2 = new GridBagConstraints();
		c_2.insets = new Insets(2, 2, 2, 2);
		c_2.fill = GridBagConstraints.BOTH;

		c_2.weightx = 2.5;
		c_2.weighty = 1.0;
		c_2.gridx = 1;
		c_2.gridy = 0;

		allPanel.add(mapPanel, c_2);

		frame.setMinimumSize(new Dimension(1300, 600));
		frame.getContentPane().add(allPanel);
	}

	public void updateMap() {
		MapBackgroundPanel map = (MapBackgroundPanel) mapPanel.getComponent(0);
		map.updateMap(game.getPlayers().get(0).getTerritories(), game
				.getPlayers().get(1).getTerritories(), playerColour, oppColour);
		map.repaint();
	}

	private Object[][] generateTableData() {
		Object[][] data = new Object[game.getCurrentPlayer().getTerritories()
				.size()][4];
		int ind = 0;

		Iterator<Territory> it = EL.getMePlayer().getTerritories().values().iterator();
		while (it.hasNext()) {
			Territory ter = it.next();
			data[ind][0] = ter.getName();
			data[ind][1] = ter.getNrTroops();
			JButton but1 = new JButton("+");
			but1.setName("plus" + ind);
			but1.addMouseListener(new MouseListener() {
				@Override
				public void mouseReleased(MouseEvent e) {
				}

				@Override
				public void mousePressed(MouseEvent e) {
					JButton b = (JButton) e.getSource();
					String t = b.getName().substring(4);
					int row = Integer.parseInt(t);
					int curValue = (int) territoryTable.getModel().getValueAt(
							row, 1);
					if (recruitedSum + 1 <= recruitedTroops) { // Increase
																// Allowed
						territoryTable.getModel().setValueAt(curValue + 1, row,
								1);
						recruitedSum++;
						lblTroopCount.setText(recruitedSum + " / "
								+ recruitedTroops + "");
					}
				}

				@Override
				public void mouseExited(MouseEvent e) {
				}

				@Override
				public void mouseEntered(MouseEvent e) {
				}

				@Override
				public void mouseClicked(MouseEvent e) {
				}
			});
			data[ind][2] = but1;

			JButton but2 = new JButton("-");
			but2.setName("mins" + ind);
			but2.addMouseListener(new MouseListener() {
				@Override
				public void mouseReleased(MouseEvent e) {
				}

				@Override
				public void mousePressed(MouseEvent e) {
					JButton b = (JButton) e.getSource();
					int row = Integer.parseInt(b.getName().substring(4));
					int curValue = (int) territoryTable.getModel().getValueAt(
							row, 1);
					int nrT = 0;
					Iterator<Territory> it = game.getCurrentPlayer()
							.getTerritories().values().iterator();
					while (it.hasNext()) {
						Territory ter = it.next();
						if (ter.getName().equals(
								territoryTable.getModel().getValueAt(row, 0)))
							nrT = ter.getNrTroops();
					}
					if (recruitedSum - 1 >= 0 && curValue - 1 >= nrT) { // Decrease
																		// allowed
						territoryTable.getModel().setValueAt(curValue - 1, row,
								1);
						recruitedSum--;
						lblTroopCount.setText(recruitedSum + " / "
								+ recruitedTroops + "");
					}
				}

				@Override
				public void mouseExited(MouseEvent e) {
				}

				@Override
				public void mouseEntered(MouseEvent e) {
				}

				@Override
				public void mouseClicked(MouseEvent e) {
				}
			});
			data[ind++][3] = but2;
		}
		return data;
	}

	// Update the table for recruiting
	public void updateRecruitTable() {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				TerritoryTableModel temp = new TerritoryTableModel();

				Object[][] data = generateTableData();

				temp.setData(data);
				territoryTable.setModel(temp);
				TerritoryTableButtonRenderer buttonRenderer = new TerritoryTableButtonRenderer();
				territoryTable.getColumn("Add").setCellRenderer(buttonRenderer);
				territoryTable.getColumn("Min").setCellRenderer(buttonRenderer);

				territoryTable.getColumnModel().getColumn(1).setMaxWidth(50);
				territoryTable.getColumnModel().getColumn(3).setMaxWidth(50);
				territoryTable.getColumnModel().getColumn(2).setMaxWidth(50);

			}
		});
	}

	// Update the source and destination comboboxes of the relevant panel in
	// either the battle or manoeuvre phase
	public void updateSourceDestinationComboBoxes() {
		if (game.getPhase() == GameState.BATTLE) {
			for (Player p : game.getPlayers()) {
				if (p.getName().equalsIgnoreCase(EL.getUsername())) {
					LinkedList<String> temp = new LinkedList<String>();
					Iterator<Territory> it = p.getTerritories().values().iterator();
					while (it.hasNext()) {
						Territory tempT = it.next();
						if (tempT.getNrTroops() > 1) {
							int count = 0;
							for (Territory nt : tempT.getNeighbours()) {
								if (game.getOtherPlayer().getTerritoryByName(
										nt.getName()) != null)
									count++;
							}
							if (count > 0) {
								temp.add(tempT.getName());
							}
						}
					}
					String[] arr = new String[temp.size()];
					int i = 0;
					for (String s : temp)
						arr[i++] = s;

					DefaultComboBoxModel<String> model1 = new DefaultComboBoxModel<String>(arr);

					battleSourceComboBox.setModel(model1);
					battleSourceComboBox.addItemListener(new ItemListener() {
						@Override
						public void itemStateChanged(ItemEvent e) {
							if (e.getStateChange() == ItemEvent.SELECTED) {
								Territory tempT = null; // Selected Territory

								Iterator<Territory> it = game
										.getCurrentPlayer().getTerritories().values()
										.iterator();
								while (it.hasNext()) {
									Territory t = it.next();
									if (t.getName().equalsIgnoreCase(
											e.getItem().toString()))
										tempT = t;
								}

								LinkedList<String> temp = new LinkedList<String>();
								for (Territory t : tempT.getNeighbours()) {
									it = game.getOtherPlayer().getTerritories().values()
											.iterator();
									while (it.hasNext()) {
										Territory n = it.next();
										if (t.getName().equalsIgnoreCase(
												n.getName()))
											temp.add(t.getName());
									}
								}
								String[] arr = new String[temp.size()];
								int i = 0;
								for (String s : temp)
									arr[i++] = s;

								DefaultComboBoxModel<String> model2 = new DefaultComboBoxModel<String>(
										arr);
								battleDestComboBox.setModel(model2);
							}
						}
					});
				} else {
					LinkedList<String> temp = new LinkedList<String>();
					Territory tempT = null;
					Iterator<Territory> it = game
							.getCurrentPlayer().getTerritories().values()
							.iterator();
					while (it.hasNext()) {
						Territory t = it.next();
						if (battleSourceComboBox.getItemAt(0)!= null && t.getName().equalsIgnoreCase(
								battleSourceComboBox.getItemAt(0).toString()))
							tempT = t;
					}
					if (tempT != null) {
						for (Territory t : tempT.getNeighbours()) {
							it = game.getOtherPlayer().getTerritories().values()
									.iterator();
							while (it.hasNext()) {
								Territory n = it.next();
								if (t.getName().equalsIgnoreCase(n.getName()))
									temp.add(t.getName());
							}
						}
					}
					String[] arr = new String[temp.size()];
					int i = 0;
					for (String s : temp)
						arr[i++] = s;

					DefaultComboBoxModel<String> model2 = new DefaultComboBoxModel<String>(arr);
					battleDestComboBox.setModel(model2);
				}
			}
		} else if (game.getPhase() == GameState.MANOEUVRE) {
			for (Player p : game.getPlayers()) {

				if (p.getName().equalsIgnoreCase(EL.getUsername())) {
					// Source Territories
					LinkedList<String> temp = new LinkedList<String>();
					Iterator<Territory> it = p.getTerritories().values().iterator();
					while (it.hasNext()) {
						Territory tempT = it.next();
						if (tempT.getNrTroops() > 1)
							temp.add(tempT.getName());
					}
					String[] arr1 = new String[temp.size()];
					int i = 0;
					for (String s : temp)
						arr1[i++] = s;
					DefaultComboBoxModel<String> model1 = new DefaultComboBoxModel<String>(arr1);

					manSourceComboBox.setModel(model1);
					manSourceComboBox.addItemListener(new ItemListener() {
						@Override
						public void itemStateChanged(ItemEvent e) {
							if (e.getStateChange() == ItemEvent.SELECTED) {
								updateSpinner();
								Territory tempT = null; // Selected Territory

								Iterator<Territory> it = game
										.getCurrentPlayer().getTerritories().values()
										.iterator();
								while (it.hasNext()) {
									Territory t = it.next();
									if (t.getName().equalsIgnoreCase(
											e.getItem().toString()))
										tempT = t;
								}

								LinkedList<String> temp = new LinkedList<String>();
								it = game.getCurrentPlayer().getTerritories().values()
										.iterator();
								while (it.hasNext()) {
									Territory t = it.next();
									if (t.connectedRegion == tempT.connectedRegion)
										temp.add(t.getName());
								}
								String[] arr = new String[temp.size()];
								int i = 0;
								for (String s : temp)
									arr[i++] = s;

								DefaultComboBoxModel<String> model2 = new DefaultComboBoxModel<String>(
										arr);
								manDestComboBox.setModel(model2);
							}
						}
					});

					// Destination territories (only reachable territories)
					Territory tempT = null; // Selected Territory

					it = game
							.getCurrentPlayer().getTerritories().values()
							.iterator();
					while (it.hasNext()) {
						Territory t = it.next();
						if (manSourceComboBox.getItemAt(0) != null && t.getName().equalsIgnoreCase(
								manSourceComboBox.getItemAt(0).toString()))
							tempT = t;
					}

					temp = new LinkedList<String>();
					it = game.getCurrentPlayer().getTerritories().values().iterator();
					while (it.hasNext()) {
						Territory t = it.next();
						if (t.connectedRegion == tempT.connectedRegion)
							temp.add(t.getName());
					}
					String[] arr = new String[temp.size()];
					i = 0;
					for (String s : temp)
						arr[i++] = s;

					DefaultComboBoxModel<String> model2 = new DefaultComboBoxModel<String>(arr);
					manDestComboBox.setModel(model2);
				}
			}
		}

	}

	// Update the spinner in the manoeuvre panel
	private void updateSpinner() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Territory source = game.getCurrentPlayer().getTerritoryByName(
						manSourceComboBox.getSelectedItem().toString());
				spinner.setMinimum(0);
				spinner.setValue(1);
				spinner.setMaximum(source.getNrTroops() - 1);
				spinner.repaint();
			}
		});
	}

	// Reset the recruited number of troops
	public void setRecruitedNumber(int number) {
		recruitedTroops = number;
		recruitedSum = 0;
	}

	public void moveBattle(int moveTroopsMin, int moveTroopsMax) {
		JOptionPane optionPane = new JOptionPane();
		JSlider slider = getSlider(optionPane);
		slider.setMinimum(moveTroopsMin);
		slider.setMaximum(moveTroopsMax);

		Object[] options = new Object[] { "OK" };

		optionPane
				.setMessage(new Object[] {
						"You conquered a territory!\nHow many troops should move across?",
						slider });
		optionPane.setMessageType(JOptionPane.QUESTION_MESSAGE);
		optionPane.setOptions(options);

		JDialog dialog = optionPane.createDialog(frame, "Move After Attack");
		dialog.setVisible(true);
		int choice = (int) optionPane.getInputValue();

		LinkedList<String> reply = new LinkedList<String>();
		reply.add(EL.getLastAttackSource().getId() + "");
		reply.add(EL.getLastAttackDestination().getId() + "");
		reply.add(choice + "");
		EL.resolveManoeuvre(EL.getLastAttackSource().getId(), EL
				.getLastAttackDestination().getId(), choice);
		EL.sendManoeuvreReply(reply);

		updateSourceDestinationComboBoxes();
	}

	private static JSlider getSlider(final JOptionPane optionPane) {
		JSlider slider = new JSlider();
		slider.setMajorTickSpacing(2);
		slider.setMinorTickSpacing(1);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		ChangeListener changeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent changeEvent) {
				JSlider theSlider = (JSlider) changeEvent.getSource();
				if (!theSlider.getValueIsAdjusting()) {
					optionPane.setInputValue(new Integer(theSlider.getValue()));
				}
			}
		};
		slider.addChangeListener(changeListener);
		return slider;
	}

	// Updates panels after phase change
	public void changePhase() {
		switch (game.getPhase()) {
		case GameState.SETUP: {
			cards.show(cardPanel, "name_troopPanel");
			lblPhaseValue.setText("Setup");
			lblTroopCount.setText(recruitedSum + " / " + recruitedTroops + "");
			updateRecruitTable();
			break;
		}
		case GameState.RECRUIT: {
			cards.show(cardPanel, "name_troopPanel");
			lblPhaseValue.setText("Recruitment");
			lblTroopCount.setText(recruitedSum + " / " + recruitedTroops + "");
			if (yourTurn)
				updateRecruitTable();
			break;
		}
		case GameState.BATTLE: {
			cards.show(cardPanel, "name_battlePanel");
			lblPhaseValue.setText("Battle");
			battleResultArea.setText("");
			if (yourTurn)
				updateSourceDestinationComboBoxes();
			break;
		}
		case GameState.MANOEUVRE: {
			cards.show(cardPanel, "name_manPanel");
			lblPhaseValue.setText("Manoeuvre");
			if (yourTurn)
				updateConnectedRegions();
			if (yourTurn)
				updateSourceDestinationComboBoxes();
			if (yourTurn)
				updateSpinner();
			break;
		}
		default: {
			System.out.println("Invalid Phase for Client GUI");
		}
		}

	}

	// Handles different actions for the different phase change buttons
	class LoaderAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
						switch (game.getPhase()) {
						case GameState.SETUP: {
							if (recruitedSum != recruitedTroops) {
								throw new Exception(
										"You have not recruited all your troops!");
							}

							LinkedList<String> reply = new LinkedList<String>();
							TerritoryTableModel model = (TerritoryTableModel) territoryTable
									.getModel();
							for (int i = 0; i < model.getRowCount(); i++) {
								String tName = (String) model.getValueAt(i, 0);
								int id = EL.getTerritoryIDByName(tName);
								int nrTroops = (int) model.getValueAt(i, 1);
								if (nrTroops > 1) {
									reply.add(id + "");
									reply.add(model.getValueAt(i, 1).toString());
								}
							}
							EL.updateRecruitedTroops(reply);
							EL.sendPlacementReply(reply);
							updateMap();
							break;
						}

						case GameState.RECRUIT: {
							if (recruitedSum != recruitedTroops) {
								throw new Exception(
										"You have not recruited all your troops!");
							}

							LinkedList<String> reply = new LinkedList<String>();
							TerritoryTableModel model = (TerritoryTableModel) territoryTable
									.getModel();
							for (int i = 0; i < model.getRowCount(); i++) {
								String tName = (String) model.getValueAt(i, 0);
								int id = EL.getTerritoryIDByName(tName);
								int nrTroops = (int) model.getValueAt(i, 1);
								if (nrTroops > 1) {
									reply.add(id + "");
									reply.add(model.getValueAt(i, 1).toString());
								}
							}
							EL.updateRecruitedTroops(reply);
							EL.sendPlacementReply(reply);
							updateMap();
							break;
						}

						case GameState.BATTLE: {
							if (battleSourceComboBox.getSelectedIndex() == -1) {
								throw new Exception(
										"You have not selected a source!");
							}
							if (battleDestComboBox.getSelectedIndex() == -1) {
								throw new Exception(
										"You have not selected a destination!");
							}

							String source = battleSourceComboBox
									.getSelectedItem().toString();
							String dest = battleDestComboBox.getSelectedItem()
									.toString();
							int sourceID = EL.getTerritoryIDByName(source);
							int destID = EL.getTerritoryIDByName(dest);

							if (!isAdjacent(sourceID, destID))
								throw new Exception(source + " and " + dest
										+ " are not adjacent!");

							if (game.getCurrentPlayer()
									.getTerritoryByID(sourceID).getNrTroops() <= 1) {
								throw new Exception(
										source
												+ " does not have enough troops to attack with...");
							}

							LinkedList<String> reply = new LinkedList<String>();
							reply.add(sourceID + "");
							reply.add(destID + "");

							EL.sendAttackReply(reply);
							break;
						}

						case GameState.MANOEUVRE: {
							if (manSourceComboBox.getSelectedIndex() == -1) {
								throw new Exception(
										"You have not selected a source!");
							}
							if (manDestComboBox.getSelectedIndex() == -1) {
								throw new Exception(
										"You have not selected a destination!");
							}

							String source = manSourceComboBox.getSelectedItem()
									.toString();
							String dest = manDestComboBox.getSelectedItem()
									.toString();
							int number = spinner.getValue();
							int sourceID = EL.getTerritoryIDByName(source);
							int destID = EL.getTerritoryIDByName(dest);

							Player me = game.getCurrentPlayer();
							Territory sourceTer = me.getTerritoryByID(sourceID);
							Territory destTer = me.getTerritoryByID(destID);
							if (sourceTer.connectedRegion != destTer.connectedRegion)
								throw new Exception(sourceTer.getName()
										+ " and " + destTer.getName()
										+ " are not connected!");

							if (game.getCurrentPlayer()
									.getTerritoryByID(sourceID).getNrTroops() <= 1) {
								throw new Exception(
										source
												+ " does not have enough troops to move to "
												+ dest + "...");
							}

							LinkedList<String> reply = new LinkedList<String>();
							if (number > 0) {
								reply.add(sourceID + "");
								reply.add(destID + "");
								reply.add(number + "");
								EL.resolveManoeuvre(sourceID, destID, number);
							}
							EL.endTurn();
							EL.sendManoeuvreReply(reply);

							break;
						}
						}
					} catch (Exception e) {
						JOptionPane.showMessageDialog(frame, e.getMessage(),
								"Warning", JOptionPane.WARNING_MESSAGE);
					}
				}

			});
		}

	}

	private boolean isAdjacent(int sourceID, int destID) {
		for (Territory t : game.getCurrentPlayer().getTerritoryByID(sourceID)
				.getNeighbours()) {
			if (t.getId() == destID)
				return true;
		}
		return false;
	}

	// --------------------------------------------------------------------------------

	// DFS for updating regions
	private void updateConnectedRegions() {
		Iterator<Territory> it = game.getCurrentPlayer().getTerritories().values()
				.iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			t.connectedRegion = -1;
		}
		int regionCounter = 0;
		it = game.getCurrentPlayer().getTerritories().values().iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			if (t.connectedRegion == -1) {
				visit(t, regionCounter);
				regionCounter++;
			}
		}

	}

	private void visit(Territory t, int counter) {
		t.connectedRegion = counter;

		for (Territory n : t.getNeighbours()) {
			if (n.connectedRegion == -1) {
				visit(n, counter);
			}
		}
	}

	// --------------------------------------------------------------------------------

	public void setCurrentPlayer(String nameOfPlayingPlayer) {
		if (lblPlayer1.getText().equalsIgnoreCase(nameOfPlayingPlayer)) {
			lblPlayer2.setFont(new Font("Trebuchet MS", Font.BOLD, 18));
			lblPlayer1.setFont(new Font("Trebuchet MS", Font.BOLD, 25));
		} else {
			lblPlayer1.setFont(new Font("Trebuchet MS", Font.BOLD, 18));
			lblPlayer2.setFont(new Font("Trebuchet MS", Font.BOLD, 25));
		}
	}

	public void postBattleResult(String result) {
		battleResultArea.append(result + "\n");
	}
	
	
	

	// Popup dialog asking user if they wish to attack again
	public void attackAgain() {
		int choice = JOptionPane.showConfirmDialog(frame,
				"Would you like to attack again?", "Attack Aftermath",
				JOptionPane.YES_NO_OPTION);

		if (choice == JOptionPane.YES_OPTION) {
			cards.show(cardPanel, "name_battlePanel");
			lblPhaseValue.setText("Battle");
		} else {
			EL.sendAttackReply(new LinkedList<String>());
			EL.setPhase(GameState.MANOEUVRE);
		}

	}

	public void yourTurn(boolean turn) {
		yourTurn = turn;
		setCurrentPlayer(game.getCurrentPlayer().getName());
		changePhase();
	}

	public void flipGlassPane() {
		frame.getGlassPane().setVisible(!yourTurn);
	}

	public void setColours(Color playerColour, Color oppColour) {
		this.playerColour = playerColour;
		lblPlayer1.setForeground(playerColour);
		this.oppColour = oppColour;
		lblPlayer2.setForeground(oppColour);
	}

	public void endGame(String message) {

		int choice = JOptionPane.showConfirmDialog(frame, "The winner is: " + message + "!"
				+ "\nWould you like to play again?", "Game Over",
				JOptionPane.YES_NO_OPTION);

		if (choice == JOptionPane.YES_OPTION) {
			frame.dispose();
			//EL = new EngineLogic(false);
			EL.initialize();
			EL.controlGui = new ControlGUI(EL, false);
			EL.controlGui.giveControl(EL.getUsername(), EL.getIP());

		} else {
			System.exit(0);
		}
	}

	public void showToaster(String message, int delayTime) {
		Toaster toasterManager = new Toaster(frame, delayTime);
		toasterManager.showToaster(message);
		try {
			Thread.sleep(delayTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}

class TerritoryTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2419991423856763822L;
	private Object[][] rows = {
			{ "One", "Two", new JButton("Button One"),
					new JButton("Button Two") },
			{ "One", "Two", new JButton("Button One"),
					new JButton("Button Two") } };
	private String[] columns = { "Territory", "Nr", "Add", "Min" };

	public void setData(Object[][] data) {
		rows = data;
	}

	public Object[][] getData() {
		return rows;
	}

	@Override
	public String getColumnName(int column) {
		return columns[column];
	}

	@Override
	public int getRowCount() {
		return rows.length;
	}

	@Override
	public int getColumnCount() {
		return columns.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return rows[rowIndex][columnIndex];
	}

	@Override
	public void setValueAt(Object obj, int rowIndex, int columnIndex) {
		rows[rowIndex][columnIndex] = obj;
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	@Override
	public Class<? extends Object> getColumnClass(int column) {
		return getValueAt(0, column).getClass();
	}
}

class TerritoryTableButtonRenderer implements TableCellRenderer {

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		JButton button = (JButton) value;

		if (isSelected) {
			button.setForeground(table.getSelectionForeground());
			button.setBackground(table.getSelectionBackground());
		} else {
			button.setForeground(table.getForeground());
			button.setBackground(UIManager.getColor("Button.background"));
		}
		return button;
	}
}

class JTableButtonMouseListener implements MouseListener {
	private JTable table;

	public JTableButtonMouseListener(JTable table) {
		this.table = table;
	}

	private void forwardEvent(MouseEvent e) {
		TableColumnModel columnModel = table.getColumnModel();
		int column = columnModel.getColumnIndexAtX(e.getX());

		int row = e.getY() / table.getRowHeight();

		Object value;
		JButton button;
		MouseEvent buttonEvent;

		if (row >= table.getRowCount() || row < 0
				|| column >= table.getColumnCount() || column < 0)
			return;

		value = table.getValueAt(row, column);

		if (!(value instanceof JButton))
			return;
		((JButton) value).doClick();

		button = (JButton) value;

		buttonEvent = (MouseEvent) SwingUtilities.convertMouseEvent(table, e,
				button);
		button.dispatchEvent(buttonEvent);

		// This is necessary so that when a button is pressed and released
		// it gets rendered properly. Otherwise, the button may still appear
		// pressed down when it has been released.
		table.repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		forwardEvent(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		forwardEvent(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		forwardEvent(e);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		forwardEvent(e);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		forwardEvent(e);
	}
}

class NotMyTurnGlassPane extends JPanel {
	private static final long serialVersionUID = -5344758920442881290L;

	public void paintComponent(Graphics g) { // Set the color to with red with a
												// 50% alpha
		g.setColor(new Color(0, 0, 0, 0.2f));

		// Fill a rectangle with the 50% red color
		g.fillRect(5, 5, this.getWidth() - 10, this.getHeight() - 10);
		ImageIcon ii = new ImageIcon("images/ajax-loader2.gif");

		ii.paintIcon(this, g, this.getWidth() / 2, this.getHeight() / 2 - 40);
	}
}
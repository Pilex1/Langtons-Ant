package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import automata.LangtonsAnt;
import automata.RuleParsingException;
import net.miginfocom.swing.MigLayout;
import util.Util;

public class MainPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1200L;
	private static final int REFRESH_RATE = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0]
			.getDisplayMode().getRefreshRate();

	private LangtonsAnt langtonsAnt;

	private JSlider sliderSpeed;
	private JCheckBox chckbxIterateCycles;
	private JMultilineLabel lblCustomRule;
	private JFormattedTextField txtRule;
	private JLabel lblSpeedIndicator;
	private JLabel lblCyclesPassed;
	private JLabel lblPixelSize;
	private JSlider sliderPixelSize;
	private JPanel guiPanel;
	private JCheckBox chckbxLoopAnts;
	private JButton btnRandomRuleOneStep;

	public MainPanel() {
		setFont(new Font("Source Sans Pro Light", Font.PLAIN, 18));
		setPreferredSize(new Dimension(1920, 1080));
		setBackground(Color.WHITE);
		setLayout(new MigLayout("", "[80%][20%]", "[1053px]"));

		guiPanel = new JPanel();
		guiPanel.setFont(new Font("Source Sans Pro Light", Font.PLAIN, 18));
		guiPanel.setPreferredSize(new Dimension(300, 800));
		guiPanel.setBorder(new LineBorder(Color.LIGHT_GRAY, 2, true));
		guiPanel.setBackground(Color.WHITE);
		add(guiPanel, "cell 1 0,grow");
		guiPanel.setLayout(new MigLayout("", "[100%,grow]", "[][][][][][]"));

		JPanel panelSpawnAnt = new JPanel();
		panelSpawnAnt.setBackground(Color.WHITE);
		guiPanel.add(panelSpawnAnt, "cell 0 0,grow");
		panelSpawnAnt.setLayout(new MigLayout("", "[100%]", "[][]"));

		JButton btnSpawnAntMiddle = new JButton("Spawn ant in middle of world");
		btnSpawnAntMiddle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				langtonsAnt.addAnt(langtonsAnt.getGridx() / 2, langtonsAnt.getGridy() / 2);
			}
		});
		btnSpawnAntMiddle.setFont(new Font("Source Sans Pro Light", Font.PLAIN, 18));
		btnSpawnAntMiddle.setAlignmentX(Component.CENTER_ALIGNMENT);
		panelSpawnAnt.add(btnSpawnAntMiddle, "cell 0 0,grow");

		JButton btnSpawnAntRandom = new JButton("Spawn ant in random location");
		panelSpawnAnt.add(btnSpawnAntRandom, "cell 0 1,grow");
		btnSpawnAntRandom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				langtonsAnt.addAnt(Util.randInt(0, langtonsAnt.getGridx() - 1),
						Util.randInt(0, langtonsAnt.getGridy() - 1));
			}
		});
		btnSpawnAntRandom.setFont(new Font("Source Sans Pro Light", Font.PLAIN, 18));

		JPanel panel = new JPanel();
		panel.setFont(new Font("Source Sans Pro Light", Font.PLAIN, 18));
		panel.setBackground(Color.WHITE);
		guiPanel.add(panel, "cell 0 1,grow");
		panel.setLayout(new MigLayout("", "[100%]", "[][]"));

		JButton btnNewButton = new JButton("Kill last ant");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				langtonsAnt.removeLastAnt();
			}
		});
		btnNewButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnNewButton.setFont(new Font("Source Sans Pro Light", Font.PLAIN, 18));
		panel.add(btnNewButton, "cell 0 0,grow");

		JButton btnKillAllAnts = new JButton("Kill all ants");
		btnKillAllAnts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				langtonsAnt.removeAllAnts();
			}
		});
		btnKillAllAnts.setFont(new Font("Source Sans Pro Light", Font.PLAIN, 18));
		btnKillAllAnts.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(btnKillAllAnts, "cell 0 1,grow");

		JPanel panelIteratingCycles = new JPanel();
		panelIteratingCycles.setBackground(Color.WHITE);
		guiPanel.add(panelIteratingCycles, "cell 0 2,grow");
		panelIteratingCycles.setLayout(new MigLayout("", "[100%]", "[][][][][]"));

		JButton btnNextCycle = new JButton("Next cycle");
		btnNextCycle.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnNextCycle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				langtonsAnt.nextCycle();
			}
		});
		panelIteratingCycles.add(btnNextCycle, "cell 0 0,growx");
		btnNextCycle.setPreferredSize(new Dimension(300, 23));
		btnNextCycle.setFont(new Font("Source Sans Pro Light", Font.PLAIN, 18));

		chckbxIterateCycles = new JCheckBox("Iterate cycles");
		chckbxIterateCycles.setPreferredSize(new Dimension(300, 23));
		chckbxIterateCycles.setFont(new Font("Source Sans Pro Light", Font.PLAIN, 18));
		panelIteratingCycles.add(chckbxIterateCycles, "cell 0 1");
		chckbxIterateCycles.setSelected(true);
		chckbxIterateCycles.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (chckbxIterateCycles.isSelected()) {
					langtonsAnt.resumeAnimation();
				} else {
					langtonsAnt.pauseAnimation();
				}
			}
		});
		chckbxIterateCycles.setBackground(Color.WHITE);

		sliderSpeed = new JSlider();
		sliderSpeed.setMinorTickSpacing(10);
		sliderSpeed.setMajorTickSpacing(100);
		panelIteratingCycles.add(sliderSpeed, "cell 0 3,grow");
		sliderSpeed.setValue(60);
		sliderSpeed.setForeground(Color.BLACK);
		sliderSpeed.setMaximum(10000);
		sliderSpeed.setMinimum(1);
		sliderSpeed.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int speed = sliderSpeed.getValue();
				if (speed == sliderSpeed.getMaximum()) {
					lblSpeedIndicator.setText("Speed: unlimited");
					langtonsAnt.setSpeed(-1);
				} else {
					lblSpeedIndicator.setText("Speed: " + speed);
					langtonsAnt.setSpeed(speed);
				}
			}
		});
		sliderSpeed.setBackground(Color.WHITE);

		lblSpeedIndicator = new JLabel("Speed: " + sliderSpeed.getValue());
		lblSpeedIndicator.setPreferredSize(new Dimension(300, 14));
		panelIteratingCycles.add(lblSpeedIndicator, "cell 0 2,grow");
		lblSpeedIndicator.setFont(new Font("Source Sans Pro Light", Font.PLAIN, 18));

		lblCyclesPassed = new JLabel("Cycles passed: 0");
		panelIteratingCycles.add(lblCyclesPassed, "cell 0 4,growx");
		lblCyclesPassed.setPreferredSize(new Dimension(300, 14));
		lblCyclesPassed.setFont(new Font("Source Sans Pro Light", Font.PLAIN, 18));

		JPanel panelPixelSize = new JPanel();
		panelPixelSize.setPreferredSize(new Dimension(300, 10));
		panelPixelSize.setBackground(Color.WHITE);
		guiPanel.add(panelPixelSize, "cell 0 3,grow");
		panelPixelSize.setLayout(new MigLayout("", "100%", ""));

		sliderPixelSize = new JSlider();
		sliderPixelSize.setPreferredSize(new Dimension(300, 26));
		panelPixelSize.add(sliderPixelSize, "cell 0 1,grow");
		sliderPixelSize.setValue(5);
		sliderPixelSize.setMaximum(30);
		sliderPixelSize.setMinimum(2);
		sliderPixelSize.setBackground(Color.WHITE);
		sliderPixelSize.setForeground(Color.BLACK);
		sliderPixelSize.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				langtonsAnt.setMinPixelSize(sliderPixelSize.getValue());
				lblPixelSize.setText("Pixel size: " + sliderPixelSize.getValue() + " px");
			}
		});

		lblPixelSize = new JLabel("Pixel size: " + sliderPixelSize.getValue() + " px");
		panelPixelSize.add(lblPixelSize, "cell 0 0,grow");
		lblPixelSize.setFont(new Font("Source Sans Pro Light", Font.PLAIN, 18));

		JPanel panelCustomRule = new JPanel();
		panelCustomRule.setBackground(Color.WHITE);
		guiPanel.add(panelCustomRule, "cell 0 4,grow");
		panelCustomRule.setLayout(new MigLayout("", "[100%,grow]", "[grow][][][][][][]"));

		JMultilineLabel txtCustomRuleInstructions = new JMultilineLabel(new Font("Source Sans Pro Light", Font.PLAIN, 18),
				"Any combination of 'L', 'R', 'N', 'E', 'S', 'W', to specify the movement of the ant in that particular state, followed by an optional number to specify by how many steps to move.");
		panelCustomRule.add(txtCustomRuleInstructions, "cell 0 2,grow");

		txtRule = new JFormattedTextField();
		txtRule.setFont(new Font("Source Sans Pro Light", Font.PLAIN, 18));
		panelCustomRule.add(txtRule, "cell 0 3,grow");
		txtRule.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					langtonsAnt.clear();
					langtonsAnt.setRule(txtRule.getText());
					lblCustomRule.setText("Custom Rule: " + txtRule.getText());
				} catch (RuleParsingException ex) {
					System.err.println(ex.getMessage());
				}
			}
		});
		txtRule.setText("RL");
		txtRule.setColumns(10);

		lblCustomRule = new JMultilineLabel(new Font("Source Sans Pro Light", Font.PLAIN, 18),
				"Custom Rule: " + txtRule.getText());
		panelCustomRule.add(lblCustomRule, "cell 0 1,grow");

		chckbxLoopAnts = new JCheckBox("Loop ants from edge of world", true);
		chckbxLoopAnts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (chckbxLoopAnts.isSelected()) {
					langtonsAnt.setLoopEdgeWorld(true);
				} else {
					langtonsAnt.setLoopEdgeWorld(false);
				}
			}
		});

		JButton btnRandomRule = new JButton("Generate random rule");
		btnRandomRule.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				langtonsAnt.clear();
				String rule = LangtonsAnt.genRandomRule(2, 10, 1, 10);
				txtRule.setText(rule);
				lblCustomRule.setText("Custom Rule: " + rule);
				try {
					langtonsAnt.setRule(rule);
				} catch (RuleParsingException e1) {
					System.err.println("Error in random rule generator!");
					e1.printStackTrace();
				}
			}
		});
		btnRandomRule.setFont(new Font("Source Sans Pro Light", Font.PLAIN, 18));
		panelCustomRule.add(btnRandomRule, "flowx,cell 0 4,growx,aligny center");

		btnRandomRuleOneStep = new JButton("Generate random rule with one step");
		btnRandomRuleOneStep.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				langtonsAnt.clear();
				String rule = LangtonsAnt.genRandomRule(2, 20, 1, 1);
				txtRule.setText(rule);
				lblCustomRule.setText("Custom Rule: " + rule);
				try {
					langtonsAnt.setRule(rule);
				} catch (RuleParsingException e1) {
					System.err.println("Error in random rule generator!");
					e1.printStackTrace();
				}
			}
		});
		btnRandomRuleOneStep.setFont(new Font("Source Sans Pro Light", Font.PLAIN, 18));
		panelCustomRule.add(btnRandomRuleOneStep, "cell 0 5,grow");
		chckbxLoopAnts.setBackground(Color.WHITE);
		chckbxLoopAnts.setFont(new Font("Source Sans Pro Light", Font.PLAIN, 18));
		panelCustomRule.add(chckbxLoopAnts, "cell 0 6");

		JPanel panelResetBtns = new JPanel();
		panelResetBtns.setBackground(Color.WHITE);
		guiPanel.add(panelResetBtns, "cell 0 5,growx");
		panelResetBtns.setLayout(new MigLayout("", "100%", ""));

		JButton btnClear = new JButton("Clear");
		btnClear.setFont(new Font("Source Sans Pro Light", Font.PLAIN, 18));
		panelResetBtns.add(btnClear, "cell 0 0,grow");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				langtonsAnt.clear();
			}
		});

		JButton btnReset = new JButton("Reset");
		btnReset.setFont(new Font("Source Sans Pro Light", Font.PLAIN, 18));
		panelResetBtns.add(btnReset, "cell 0 1,grow");
		btnReset.setForeground(Color.BLACK);
		btnReset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				txtRule.setText("RL");
				sliderSpeed.setValue(60);
				sliderPixelSize.setValue(5);
				langtonsAnt.clear();
			}
		});

		langtonsAnt = new LangtonsAnt(sliderPixelSize.getValue(), sliderSpeed.getValue(), false);
		langtonsAnt.setBorder(new LineBorder(new Color(192, 192, 192), 2, true));
		langtonsAnt.setFont(new Font("Source Sans Pro Light", Font.PLAIN, 18));
		add(langtonsAnt, "cell 0 0,grow");

		new Thread() {
			@Override
			public void run() {
				while (true) {
					try {
						lblCyclesPassed.setText("Cycles passed: " + langtonsAnt.getCycles());
						Thread.sleep((long) (1000D / REFRESH_RATE));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	void initialize() {
		langtonsAnt.initialize();
	}

}

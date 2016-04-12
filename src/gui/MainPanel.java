package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import automata.LangtonsAnt;
import automata.RuleParsingException;
import net.miginfocom.swing.MigLayout;
import java.awt.BorderLayout;

public class MainPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	private static final int REFRESH_RATE = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0]
			.getDisplayMode().getRefreshRate();
	
	
	private LangtonsAnt langtonsAnt;

	private JSlider sliderSpeed;
	private JCheckBox chckbxRunAutomata;
	private JLabel lblCustomRule;
	private JFormattedTextField txtRule;
	private JButton btnReset;
	private JLabel lblSpeedIndicator;
	private JButton btnClear;
	private JLabel lblCyclesPassed;
	private JLabel lblPixelSize;
	private JSlider sliderPixelSize;
	private JPanel guiPanel;
	private JPanel panelSpeed;
	private JPanel panelPixelSize;
	private JPanel panelCustomRule;
	private JPanel panelResetBtns;

	public MainPanel() {
		setBackground(Color.WHITE);
		setLayout(new BorderLayout(0, 0));

		guiPanel = new JPanel();
		guiPanel.setPreferredSize(new Dimension(300, 3));
		guiPanel.setBorder(new LineBorder(SystemColor.windowBorder, 2, true));
		guiPanel.setBackground(Color.WHITE);
		add(guiPanel, BorderLayout.EAST);
		guiPanel.setLayout(new MigLayout("", "[236px]", "[26px][26px][26px][26px][26px][26px]"));

		chckbxRunAutomata = new JCheckBox("Run automata");
		guiPanel.add(chckbxRunAutomata, "cell 0 0,grow");
		chckbxRunAutomata.setSelected(true);
		chckbxRunAutomata.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (chckbxRunAutomata.isSelected()) {
					langtonsAnt.resumeAnimation();
				} else {
					langtonsAnt.pauseAnimation();
				}
			}
		});
		chckbxRunAutomata.setBackground(Color.WHITE);

		panelSpeed = new JPanel();
		guiPanel.add(panelSpeed, "cell 0 1,grow");
		panelSpeed.setLayout(new MigLayout("", "[236px]", "[26px][26px]"));

		sliderSpeed = new JSlider();
		sliderSpeed.setMinorTickSpacing(10);
		sliderSpeed.setMajorTickSpacing(100);
		panelSpeed.add(sliderSpeed, "cell 0 1,grow");
		sliderSpeed.setValue(60);
		sliderSpeed.setForeground(Color.BLACK);
		sliderSpeed.setMaximum(1000);
		sliderSpeed.setMinimum(1);
		sliderSpeed.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int fps = sliderSpeed.getValue();
				lblSpeedIndicator.setText("Speed: " + (fps==sliderSpeed.getMaximum()?"Unlimited":Integer.toString(fps)) + " fps");
				langtonsAnt.setFps((fps==sliderSpeed.getMaximum()?-1:fps));
			}
		});
		sliderSpeed.setBackground(Color.WHITE);

		lblSpeedIndicator = new JLabel("Speed: " + sliderSpeed.getValue() + " fps");
		panelSpeed.add(lblSpeedIndicator, "cell 0 0,grow");
		lblSpeedIndicator.setFont(new Font("Source Sans Pro Light", Font.PLAIN, 18));

		panelPixelSize = new JPanel();
		guiPanel.add(panelPixelSize, "cell 0 2,grow");
		panelPixelSize.setLayout(new MigLayout("", "[236px]", "[26px][26px]"));

		sliderPixelSize = new JSlider();
		sliderPixelSize.setMajorTickSpacing(5);
		sliderPixelSize.setMinorTickSpacing(2);
		panelPixelSize.add(sliderPixelSize, "cell 0 1,grow");
		sliderPixelSize.setValue(5);
		sliderPixelSize.setMaximum(30);
		sliderPixelSize.setMinimum(2);
		sliderPixelSize.setBackground(Color.WHITE);
		sliderPixelSize.setForeground(Color.BLACK);
		sliderPixelSize.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				langtonsAnt.setPixelSize(sliderPixelSize.getValue());
				lblPixelSize.setText("Pixel size: " + sliderPixelSize.getValue()+" px");
			}
		});

		lblPixelSize = new JLabel("Pixel size: " + sliderPixelSize.getValue());
		panelPixelSize.add(lblPixelSize, "cell 0 0,grow");
		lblPixelSize.setFont(new Font("Source Sans Pro Light", Font.PLAIN, 18));


		panelCustomRule = new JPanel();
		guiPanel.add(panelCustomRule, "cell 0 3,grow");
		panelCustomRule.setLayout(new MigLayout("", "[236px]", "[26px][26px]"));

		txtRule = new JFormattedTextField();
		panelCustomRule.add(txtRule, "cell 0 1,grow");
		txtRule.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					langtonsAnt.clear();
					langtonsAnt.setRule(txtRule.getText());
					lblCustomRule
							.setText("Custom Rule: " + txtRule.getText() + " (any combination of \"R\" and \"L\")");
				} catch (RuleParsingException ex) {
					System.out.println(ex.getMessage());
				}
			}
		});
		txtRule.setText("RL");
		txtRule.setColumns(10);

		lblCustomRule = new JLabel("Custom Rule: " + txtRule.getText() + " (any combination of \"R\" and \"L\")");
		panelCustomRule.add(lblCustomRule, "cell 0 0,grow");

		panelResetBtns = new JPanel();
		guiPanel.add(panelResetBtns, "cell 0 4,grow");
		panelResetBtns.setLayout(new MigLayout("", "[236px]", "[26px][26px]"));

		btnReset = new JButton("Reset");
		panelResetBtns.add(btnReset, "cell 0 0,grow");
		btnReset.setForeground(Color.BLACK);

		btnClear = new JButton("Clear");
		panelResetBtns.add(btnClear, "cell 0 1,grow");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				langtonsAnt.clear();
			}
		});
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
		add(langtonsAnt);

		lblCyclesPassed = new JLabel("Cycles passed: " + langtonsAnt.getCycles());
		guiPanel.add(lblCyclesPassed, "cell 0 5,grow");

		new Thread() {
			@Override
			public void run() {
				while (true) {
					try {
						lblCyclesPassed.setText("Cycles passed: " + langtonsAnt.getCycles());
						Thread.sleep((long) (1000D / REFRESH_RATE));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

}

package io.github.jadefalke2.components;

import io.github.jadefalke2.InputLine;
import io.github.jadefalke2.Script;
import io.github.jadefalke2.TAS;
import io.github.jadefalke2.actions.StickAction;
import io.github.jadefalke2.stickRelatedClasses.*;
import io.github.jadefalke2.util.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class SideJoystickPanel extends JPanel {

	private final JLabel frameAmountLabel;
	private final JoystickPanel lstickPanel;
	private final JoystickPanel rstickPanel;
	private final Settings settings;

	private InputLine[] inputLines;
	private Script script;

	public SideJoystickPanel (TAS parent, PianoRoll pianoRoll, Script startScript) {

		frameAmountLabel = new JLabel("Currently no frames are being edited");
		frameAmountLabel.setHorizontalAlignment(JLabel.CENTER);
		frameAmountLabel.setFont(new Font(frameAmountLabel.getFont().getName(), frameAmountLabel.getFont().getStyle(), 15));
		settings = parent.getPreferences();
		this.script = startScript;

		//TODO remove the duplicate listener here
		ActionListener smoothTransitionListenerL = e -> {
			SmoothTransitionDialog dialog = new SmoothTransitionDialog(settings, inputLines[0].getStickL(), inputLines[inputLines.length-1].getStickL());
			dialog.setVisible(true);
			StickPosition[] replacementStickPos = dialog.getSmoothTransitionData(inputLines.length);
			InputLine[] originalLines = pianoRoll.getSelectedInputRows();
			InputLine[] replacementLines = Arrays.stream(originalLines).map(InputLine::clone).toArray(InputLine[]::new);

			for(int i = 0;i<originalLines.length;i++){
				replacementLines[i].setStickL(replacementStickPos[i]);
			}
			pianoRoll.replaceSelectedRows(replacementLines);
		};

		ActionListener smoothTransitionListenerR = e -> {
			SmoothTransitionDialog dialog = new SmoothTransitionDialog(settings, inputLines[0].getStickR(), inputLines[inputLines.length-1].getStickR());
			dialog.setVisible(true);
			StickPosition[] replacementStickPos = dialog.getSmoothTransitionData(inputLines.length);
			InputLine[] originalLines = pianoRoll.getSelectedInputRows();
			InputLine[] replacementLines = Arrays.stream(originalLines).map(InputLine::clone).toArray(InputLine[]::new);

			for(int i = 0;i<originalLines.length;i++){
				replacementLines[i].setStickR(replacementStickPos[i]);
			}
			pianoRoll.replaceSelectedRows(replacementLines);
		};

		CustomChangeListener joystickPanelListener = e -> parent.executeAction(new StickAction(inputLines, getStickType(e.getSource()), e.getOldValue(), e.getNewValue(), pianoRoll.getModel(), pianoRoll.getSelectedRows()));

		lstickPanel = new JoystickPanel(parent.getPreferences(), smoothTransitionListenerL);
		rstickPanel = new JoystickPanel(parent.getPreferences(), smoothTransitionListenerR);
		lstickPanel.setOnChangeListener(joystickPanelListener);
		rstickPanel.setOnChangeListener(joystickPanelListener);

		lstickPanel.setAllEnabled(false);
		rstickPanel.setAllEnabled(false);

		pianoRoll.getSelectionModel().addListSelectionListener(e -> {
			int[] selectedRows = pianoRoll.getSelectedRows();

			switch (selectedRows.length) {
				case 0 -> {
					//no frames selected
					frameAmountLabel.setText("Currently no frames are being edited");
					lstickPanel.setAllEnabled(false);
					rstickPanel.setAllEnabled(false);
				}
				case 1 -> {
					//one frame is selected
					frameAmountLabel.setText("Currently editing frame " + selectedRows[0]);
					setEditingRows(selectedRows, script.getLines());
				}
				default -> {
					//more than 1 frame is selected
					frameAmountLabel.setText("Currently editing frames " + selectedRows[0] + " - " + selectedRows[selectedRows.length - 1]);
					setEditingRows(selectedRows, script.getLines());
				}
			}
		});

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.BOTH;
		c.weighty = 0;
		c.gridy = 0;
		add(frameAmountLabel, c);

		c.weighty = 1;
		c.gridy = 1;
		add(lstickPanel, c);

		c.gridy = 2;
		add(rstickPanel, c);
	}

	public JoystickPanel.StickType getStickType(Object joystickPanel){
		if(joystickPanel == lstickPanel) return JoystickPanel.StickType.L_STICK;
		if(joystickPanel == rstickPanel) return JoystickPanel.StickType.R_STICK;
		throw new IllegalArgumentException("Unknown StickType of panel: "+joystickPanel);
	}

	public void setEditingRows(int[] rows, InputLine[] scriptLines){
		inputLines = script.getLines(rows);

		setEditingRows(rows[0], inputLines[0], scriptLines, lstickPanel, JoystickPanel.StickType.L_STICK);
		setEditingRows(rows[0], inputLines[0], scriptLines, rstickPanel, JoystickPanel.StickType.R_STICK);
		lstickPanel.setAllEnabled(true);
		rstickPanel.setAllEnabled(true);
	}

	//TODO clean up this mess
	public void setEditingRows(int firstIndex, InputLine firstLine, InputLine[] inputLines, JoystickPanel joystickPanel, JoystickPanel.StickType stickType){
		StickPosition[] stickPositions = new StickPosition[Math.min(firstIndex, settings.getLastStickPositionCount())];
		// sets the contents of the stickpositions array to be the previous stick positions of the same stick
		for (int i = 0; i < stickPositions.length; i++){
			InputLine currentLine = inputLines[firstIndex - stickPositions.length + i];
			stickPositions[i] = stickType == JoystickPanel.StickType.L_STICK ? currentLine.getStickL() : currentLine.getStickR();
		}
		joystickPanel.setStickPositions(stickPositions);
		joystickPanel.setStickPosition(stickType == JoystickPanel.StickType.L_STICK ? firstLine.getStickL() : firstLine.getStickR());
	}

	public void setScript(Script script){
		this.script = script;
	}

}

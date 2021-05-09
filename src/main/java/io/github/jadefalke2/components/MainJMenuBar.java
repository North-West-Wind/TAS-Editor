package io.github.jadefalke2.components;

import io.github.jadefalke2.TAS;
import io.github.jadefalke2.util.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.prefs.Preferences;

public class MainJMenuBar extends JMenuBar {

	public MainJMenuBar(MainEditorWindow mainEditorWindow, TAS parent){
		// TODO: Handle the rest of the listeners and make things enabled or disabled correctly

		JMenu fileJMenu = add(new JMenu("File"));
		JMenuItem newJMenuItem = fileJMenu.add(new JMenuItem("New"));
		newJMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
		newJMenuItem.addActionListener(e -> {});
		newJMenuItem.setEnabled(false);

		JMenuItem newWindowJMenuItem = fileJMenu.add(new JMenuItem("New Window"));
		newWindowJMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		newWindowJMenuItem.addActionListener(e -> new TAS());
		newWindowJMenuItem.setEnabled(true);

		JMenuItem openJMenuItem = fileJMenu.add(new JMenuItem("Open..."));
		openJMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));

		openJMenuItem.addActionListener(e -> {
			try {
				mainEditorWindow.setScript(new TxtFileChooser().getFile(true));
			} catch (FileNotFoundException corruptedScriptException) {
				corruptedScriptException.printStackTrace();
			}
		});

		openJMenuItem.setEnabled(true);

		JMenuItem saveJMenuItem = fileJMenu.add(new JMenuItem("Save"));
		saveJMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
		saveJMenuItem.addActionListener(e -> mainEditorWindow.saveFile());

		JMenuItem saveAsJMenuItem = fileJMenu.add(new JMenuItem("Save As..."));
		saveAsJMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		saveAsJMenuItem.addActionListener(e -> {
			try {
				new TxtFileChooser().saveFileAs(mainEditorWindow.getScript());
			} catch(IOException err){
				err.printStackTrace();
			}
		});

		fileJMenu.addSeparator();

		JMenuItem exitJMenuItem = fileJMenu.add(new JMenuItem("Exit"));
		exitJMenuItem.addActionListener(e -> System.exit(0));

		JMenu editJMenu = add(new JMenu("Edit"));

		JMenuItem undoJMenuItem = editJMenu.add(new JMenuItem("Undo"));
		undoJMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
		undoJMenuItem.addActionListener(e -> parent.undo());

		JMenuItem redoJMenuItem = editJMenu.add(new JMenuItem("Redo"));
		redoJMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		redoJMenuItem.addActionListener(e -> parent.redo());

		editJMenu.addSeparator();

		JMenuItem cutJMenuItem = editJMenu.add(new JMenuItem("Cut"));
		cutJMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
		cutJMenuItem.addActionListener(e -> parent.cut());

		JMenuItem copyJMenuItem = editJMenu.add(new JMenuItem("Copy"));
		copyJMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
		copyJMenuItem.addActionListener(e -> parent.copy());

		JMenuItem pasteJMenuItem = editJMenu.add(new JMenuItem("Paste"));
		pasteJMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
		pasteJMenuItem.addActionListener(e -> {
			try {
				parent.paste();
			} catch (IOException | UnsupportedFlavorException ioException) {
				ioException.printStackTrace();
			}
		});

		JMenuItem deleteJMenuItem = editJMenu.add(new JMenuItem("Delete"));
		deleteJMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		deleteJMenuItem.addActionListener(e -> parent.delete());

		JMenuItem addNewLineItem = editJMenu.add(new JMenuItem("Add line"));
		addNewLineItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		addNewLineItem.addActionListener(e -> mainEditorWindow.getPianoRoll().addEmptyRow());

		editJMenu.addSeparator();

		JMenuItem settingsItem = editJMenu.add("Settings");
		settingsItem.addActionListener(e -> parent.openSettings());

		JMenu viewJMenu = add(new JMenu("View"));

		Settings preferences = parent.getPreferences();

		JCheckBoxMenuItem darkThemeJMenuItem = new JCheckBoxMenuItem("Toggle Dark Theme", preferences.darkTheme);

		viewJMenu.add(darkThemeJMenuItem);

		darkThemeJMenuItem.addItemListener(e -> {
			preferences.darkTheme = darkThemeJMenuItem.getState();
			parent.updateLookAndFeel();
		});

		JMenu helpJMenu = add(new JMenu("Help"));
		JMenuItem discordJMenuItem = helpJMenu.add(new JMenuItem("Join the SMO TASing Discord"));

		discordJMenuItem.addActionListener(e -> {
			try {
				Desktop.getDesktop().browse(new URL("https://discord.gg/atKSg9fygq").toURI());
			} catch (IOException | URISyntaxException ex) {
				ex.printStackTrace();
			}
		});

		helpJMenu.addSeparator();
		JMenuItem aboutJMenuItem = helpJMenu.add(new JMenuItem("About SMO TAS Editor"));
		aboutJMenuItem.addActionListener(e -> {
			try {
				Desktop.getDesktop().browse(new URL("https://github.com/Jadefalke2/TAS-editor").toURI());
			} catch (IOException | URISyntaxException ex) {
				ex.printStackTrace();
			}
		});

	}
}
package com.github.drumber.video.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.*;

import com.github.drumber.video.VideoPlugin;
import com.github.drumber.video.listener.StatusListener;

import de.lars.remotelightcore.settings.SettingsManager;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingDouble;
import de.lars.remotelightcore.settings.types.SettingInt;
import de.lars.remotelightcore.settings.types.SettingString;

public class SettingsPanel extends JPanel implements StatusListener {
	
	private final VideoPlugin plugin;
	private JTextField fieldFilePath;
	private JSpinner spinnerHeight;
	private JButton btnStart;
	private JLabel lblStatus;
	
	private SettingString sFilePath;
	private SettingInt sHeight;
	
	public SettingsPanel(VideoPlugin plugin) {
		this.plugin = plugin;
		SettingsManager sm = plugin.getInterface().getSettingsManager();
		sFilePath = sm.addSetting(new SettingString("plugin.video.filepath", null, SettingCategory.Intern, null, ""));
		sHeight = sm.addSetting(new SettingInt("plugin.video.height", null, SettingCategory.Intern, null, 50, 1, Integer.MAX_VALUE, 1));
		SettingDouble sTweak = sm.addSetting(new SettingDouble("plugin.video.tweak", null, SettingCategory.Intern, null, 0.0, -999.0, 999, 0.5));
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		JLabel lblFilePath = new JLabel("Video File");
		lblFilePath.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(lblFilePath);
		
		JPanel panelFilePath = new JPanel();
		panelFilePath.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelFilePath.setLayout(new BoxLayout(panelFilePath, BoxLayout.X_AXIS));
		add(panelFilePath);
		
		fieldFilePath = new JTextField(sFilePath.get());
		fieldFilePath.setMaximumSize(new Dimension(Integer.MAX_VALUE, fieldFilePath.getPreferredSize().height));
		panelFilePath.add(fieldFilePath);
		
		JButton btnBrowseFile = new JButton("Browse");
		btnBrowseFile.addActionListener(e -> openFileChooser());
		panelFilePath.add(btnBrowseFile);
		
		JPanel panelHeight = new JPanel();
		panelHeight.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelHeight.setLayout(new FlowLayout(FlowLayout.LEFT));
		add(panelHeight);
		
		JLabel lblHeight = new JLabel("Height (LEDs)");
		panelHeight.add(lblHeight);
		
		spinnerHeight = new JSpinner(new SpinnerNumberModel(sHeight.get().intValue(), 10, 99999, 1));
		panelHeight.add(spinnerHeight);
		panelHeight.setMaximumSize(new Dimension(Integer.MAX_VALUE, spinnerHeight.getPreferredSize().height + 5));
		
		JPanel panelTweaks = new JPanel();
		panelTweaks.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelTweaks.setLayout(new FlowLayout(FlowLayout.LEFT));
		add(panelTweaks);
		
		JLabel lblTweaks = new JLabel("Tweak playback speed");
		panelTweaks.add(lblTweaks);
		
		JSpinner spinnerTweaks = new JSpinner(new SpinnerNumberModel(sTweak.get().doubleValue(), sTweak.getMin(), sTweak.getMax(), sTweak.getStepsize()));
		spinnerTweaks.addChangeListener(e -> sTweak.set(spinnerTweaks.getValue()));
		panelTweaks.add(spinnerTweaks);
		panelTweaks.setMaximumSize(new Dimension(Integer.MAX_VALUE, spinnerTweaks.getPreferredSize().height + 5));
		
		btnStart = new JButton(plugin.isRunning() ? "Stop" : "Start");
		btnStart.setAlignmentX(Component.LEFT_ALIGNMENT);
		btnStart.addActionListener(e -> onStartClicked());
		add(btnStart);
		
		lblStatus = new JLabel();
		lblStatus.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(Box.createVerticalStrut(10));
		add(lblStatus);
		
		plugin.registerStatusListener(this);
	}
	
	private void openFileChooser() {
		String defaultPath = fieldFilePath.getText();
		if(defaultPath.trim().isEmpty()) {
			defaultPath = null;
		}
		
		JFileChooser jfc = new JFileChooser(defaultPath);
		jfc.setDialogTitle("Choose a video file");
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		
		int returnValue = jfc.showOpenDialog(null);
		if(returnValue == JFileChooser.APPROVE_OPTION) {
			fieldFilePath.setText(jfc.getSelectedFile().getAbsolutePath());
		}
	}
	
	private void onStartClicked() {
		if(plugin.isRunning()) {
			plugin.stop();
			return;
		}
		
		String filePath = fieldFilePath.getText();
		if(filePath == null || filePath.trim().isEmpty()) {
			onError("Please select a video file.");
			return;
		}
		int height = (int) spinnerHeight.getValue();
		
		sFilePath.set(filePath);
		sHeight.set(height);
		
		plugin.start(filePath, height);
	}

	@Override
	public void onRunUpdate(boolean running) {
		btnStart.setText(running ? "Stop" : "Start");
	}

	@Override
	public void onFrameProcessing(int current, int max) {
		lblStatus.setText(String.format("Grabbing frames %d/%d", current, max));
	}

	@Override
	public void onError(String errorMessage) {
		lblStatus.setText("Error: " + errorMessage);
	}


}

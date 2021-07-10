package com.github.drumber.video;

import java.io.File;
import java.util.function.Supplier;

import com.github.drumber.video.listener.StatusListener;

import de.lars.remotelightclient.ui.panels.tools.ToolsPanel;
import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.settings.types.SettingDouble;
import de.lars.remotelightplugins.Plugin;

public class VideoPlugin extends Plugin {
	
	private VideoRunner videoRunner;
	
	private StatusListener statusListener;
	private boolean isProccessing;
	
	private String lastFile;
	private int lastHeight;
	
	@Override
	public void onEnable() {
		ToolsPanel.getEntryList().add(new VideoEntryPanel(this));
	}

	@Override
	public boolean isLoaded() {
		return true;
	}
	
	public void start(String filePath, int height) {
		if(isRunning() || filePath == null || isProccessing) return;
		isProccessing = true;
		File file = new File(filePath);
		if(!file.isFile()) {
			fireError("Please select a file.");
			return;
		}
		
		new Thread(() -> {
			if(!filePath.equals(lastFile) || lastHeight != height || videoRunner == null) {
				VideoConverter converter = new VideoConverter(filePath, height);
				converter.grabFrames(statusListener);
				
				videoRunner = new VideoRunner(converter.getVideoFrames(), converter.getFrameRate(), RemoteLightCore.getLedNum(), getPlaybackTweak());
			}
			
			isProccessing = false;
			lastFile = file.getAbsolutePath();
			lastHeight = height;
			
			videoRunner.setListener(statusListener);
			videoRunner.run();
		}, "Video Grabber").start();
	}
	
	public void stop() {
		if(isRunning()) {
			videoRunner.stopPlayback();
		}
	}
	
	public boolean isRunning() {
		return videoRunner != null && videoRunner.isRunning();
	}
	
	public void registerStatusListener(StatusListener listener) {
		statusListener = listener;
		if(videoRunner != null) {
			videoRunner.setListener(statusListener);
		}
	}
	
	public void fireError(String msg) {
		if(statusListener != null) {
			statusListener.onError(msg);
		}
	}
	
	public Supplier<Double> getPlaybackTweak() {
		return () -> {
			SettingDouble s = getInterface().getSettingsManager().getSetting(SettingDouble.class, "plugin.video.tweak");
			return s != null ? s.get() : 0.0;
		};
	}

}

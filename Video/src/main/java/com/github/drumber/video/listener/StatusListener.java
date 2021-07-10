package com.github.drumber.video.listener;

public interface StatusListener {
	
	public void onRunUpdate(boolean running);
	
	public void onFrameProcessing(int current, int max);
	
	public void onError(String errorMessage);

}

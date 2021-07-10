package com.github.drumber.video;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import org.tinylog.Logger;

import com.github.drumber.video.listener.StatusListener;

import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.utils.color.Color;
import de.lars.remotelightcore.utils.color.PixelColorUtils;

public class VideoRunner extends Thread {
	
	private AtomicBoolean running = new AtomicBoolean(false);
	private StatusListener listener;
	private final List<BufferedImage> frames;
	private final double frameRate;
	private final int ledCount;
	private Supplier<Double> playbackTweak;
	
	private boolean shouldStop;
	
	public VideoRunner(List<BufferedImage> frames, double frameRate, int ledCount, Supplier<Double> playbackTweak) {
		this.frames = frames;
		this.frameRate = frameRate;
		this.ledCount = ledCount;
		this.playbackTweak = playbackTweak;
	}
	
	public StatusListener getListener() {
		return listener;
	}

	public void setListener(StatusListener listener) {
		this.listener = listener;
	}

	@Override
	public void run() {
		if(isRunning()) return;
		running.set(true);
		fireRunning();
		shouldStop = false;
		
		double delay = 1000.0 / frameRate;
		int currentFrame = 0;
		
		while(currentFrame < frames.size() && !shouldStop) {
			long startTime = System.currentTimeMillis();
			
			// show frame
			BufferedImage image = frames.get(currentFrame);
			System.out.printf("Showing frame %d/%d scale: %d/%d \n", currentFrame + 1, frames.size(), image.getWidth(), image.getHeight());
			showFrame(image);
			
			long wait = Math.round(delay - (System.currentTimeMillis() - startTime));
			wait += playbackTweak.get();
			try {
				Thread.sleep(Math.max(0, wait));
			} catch (InterruptedException e) {
				Logger.error(e, "Video thread was interrupted.");
				break;
			}
			
			currentFrame++;
		}
		
		System.out.println("Finished video");
		running.set(false);
		fireRunning();
	}
	
	private void showFrame(BufferedImage image) {
		int ledsInRow = Math.min(ledCount / image.getHeight(), image.getWidth());
		Color[] strip = PixelColorUtils.colorAllPixels(Color.BLACK, ledCount);
		
		for(int y = 0; y < image.getHeight(); y++) {
			for(int x = 0; x < ledsInRow; x++) {
				Color pixel = new Color(image.getRGB(x, y));
				int index = y*ledsInRow + x;
				strip[index] = pixel;
			}
		}
		
		OutputManager.addToOutput(strip);
	}
	
	public boolean isRunning() {
		return running.get();
	}
	
	private void fireRunning() {
		if(listener != null) {
			listener.onRunUpdate(isRunning());
		}
	}
	
	public void stopPlayback() {
		shouldStop = true;
	}

}

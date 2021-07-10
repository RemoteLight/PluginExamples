package com.github.drumber.video;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.tinylog.Logger;

public class VideoConverter {
	
	private final String filePath;
	
	private List<BufferedImage> videoFrames;
	
	public VideoConverter(String filePath) {
		this.filePath = filePath;
		videoFrames = new ArrayList<BufferedImage>();
	}
	
	public void grabFrames() {
		File file = new File(filePath);
		if(!file.isFile())
			throw new IllegalArgumentException(filePath + " is not a file.");
		
		// clear old frame list
		videoFrames.clear();
		
		int currentFrame = 0;
		
		try(FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(file)) {
			frameGrabber.start();
			
			int frameLength = frameGrabber.getLengthInFrames();
			double frameRate = frameGrabber.getFrameRate();
			
			Logger.info("Video Information:\nFormat: %s\nAspect Ratio: %f\nFramerate: %f\nFrames: %d\nDuration: %d min",
					frameGrabber.getFormat(),
					frameGrabber.getAspectRatio(),
					frameRate,
					frameLength,
					frameLength / frameRate);
			
			while(currentFrame <= frameLength) {
				Frame frame = frameGrabber.grabImage();
				
				if(frame != null) {
					BufferedImage image = frameToBufferedImage(frame);
					videoFrames.add(image);
				}
				
				System.out.printf("Frame %d/%d\n", currentFrame, frameLength);
				currentFrame++;
			}
			
			Logger.info("Finished frame grabbing. Frame count: " + videoFrames.size());
			
			
		} catch(Exception e) {
			Logger.error(e, "Error while grabbing frames from video.");
		}
	}
	
	public List<BufferedImage> getVideoFrames() {
		return new ArrayList<BufferedImage>(videoFrames);
	}
	
	
	private BufferedImage frameToBufferedImage(Frame frame) {
		try (Java2DFrameConverter converter = new Java2DFrameConverter()) {
			return converter.convert(frame);
		}
	}

}

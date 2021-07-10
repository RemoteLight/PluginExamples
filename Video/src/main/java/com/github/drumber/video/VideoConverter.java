package com.github.drumber.video;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.tinylog.Logger;

import com.github.drumber.video.listener.StatusListener;

public class VideoConverter {
	
	private final String filePath;
	private final int height;
	
	private List<BufferedImage> videoFrames;
	private double frameRate;
	
	public VideoConverter(String filePath, int height) {
		this.filePath = filePath;
		this.height = height;
		videoFrames = new ArrayList<BufferedImage>();
	}
	
	public void grabFrames(StatusListener listener) {
		File file = new File(filePath);
		if(!file.isFile()) {
			if(listener != null) {
				listener.onError(filePath + " is not a file.");
			}
			return;
		}
		
		// clear old frame list
		videoFrames.clear();
		
		int currentFrame = 0;
		
		try(FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(file)) {
			frameGrabber.start();
			
			// calculate width by respecting the aspect ratio
			int width = (int) Math.floor((frameGrabber.getImageWidth() / frameGrabber.getImageHeight()) * height);
			
			//frameGrabber.setImageScalingFlags(swscale.SWS_BICUBIC);
			//frameGrabber.setImageHeight(height);
			//frameGrabber.setImageWidth(width);
			
			int frameLength = frameGrabber.getLengthInFrames();
			frameRate = frameGrabber.getFrameRate();
			
			Logger.info(String.format("Video Information:\nFormat: %s\nSize: %dx%d\nFramerate: %f\nFrames: %d\nDuration: %f min",
					frameGrabber.getFormat(),
					frameGrabber.getImageWidth(),
					frameGrabber.getImageHeight(),
					frameRate,
					frameLength,
					frameLength / frameRate / 60.0));
			
			while(currentFrame <= frameLength) {
				Frame frame = frameGrabber.grabImage();
				
				if(frame != null) {
					BufferedImage image = frameToBufferedImage(frame);
					image = resizeImage(image, width, height);
					videoFrames.add(image);
				} else {
					System.out.println("Missing frame on position " + currentFrame);
					BufferedImage filler;
					if(videoFrames.size() > 0) {
						BufferedImage prev = videoFrames.get(videoFrames.size() - 1);
						WritableRaster raster = prev.copyData(null);
						filler = new BufferedImage(prev.getColorModel(), raster, prev.isAlphaPremultiplied(), null);
					} else {
						filler = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
					}
					videoFrames.add(filler);
				}
				
				System.out.printf("Frame %d/%d\n", currentFrame, frameLength);
				fireStatusUpdate(listener, currentFrame, frameLength);
				currentFrame++;
			}
			
			Logger.info("Finished frame grabbing. Frame count: " + videoFrames.size());
			
			frameGrabber.stop();
		} catch(Exception e) {
			Logger.error(e, "Error while grabbing frames from video.");
			if(listener != null) {
				listener.onError("Error while grabbing frames from video: " + e.getMessage());
			}
		}
	}
	
	public List<BufferedImage> getVideoFrames() {
		return new ArrayList<BufferedImage>(videoFrames);
	}
	
	public double getFrameRate() {
		return frameRate;
	}
	
	
	private BufferedImage resizeImage(BufferedImage img, int width, int height) {
		Image tmp = img.getScaledInstance(width, height, Image.SCALE_FAST);
		BufferedImage out = new BufferedImage(width, height, img.getType());
		
		Graphics2D g2 = out.createGraphics();
		g2.drawImage(tmp, 0, 0, null);
		g2.dispose();
		return out;
	}
	
	private BufferedImage frameToBufferedImage(Frame frame) {
		try (Java2DFrameConverter converter = new Java2DFrameConverter()) {
			return converter.convert(frame);
		}
	}
	
	private void fireStatusUpdate(StatusListener l, int frame, int frameLength) {
		if(l != null) {
			l.onFrameProcessing(frame, frameLength);
		}
	}

}

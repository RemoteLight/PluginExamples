package de.lars.mavenexample;

import java.awt.Color;
import java.util.Random;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.animation.Animation;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.utils.color.PixelColorUtils;
import de.lars.remotelightcore.utils.color.RainbowWheel;

public class SimpleAnimation extends Animation {
	
	private Color[] strip;
	private final Color[] rainbow;
	private int iColor1, iColor2;

	public SimpleAnimation() {
		super("SimpleAnimation");
		// get a color array of rainbow colors
		rainbow = RainbowWheel.getRainbow();
	}
	
	@Override
	public void onEnable() {
		// initialize strip array with black
		strip = PixelColorUtils.colorAllPixels(Color.BLACK, RemoteLightCore.getLedNum());
		Random ran = new Random();
		// set both to random start indexes
		iColor1 = ran.nextInt(rainbow.length);
		iColor2 = ran.nextInt(rainbow.length);
	}
	
	@Override
	public void onLoop() {
		for(int i = 0; i < strip.length; i++) {
			int rainbowIndex = iColor1;
			if(i % 2 == 0)
				rainbowIndex = iColor2;
			strip[i] = rainbow[rainbowIndex];
		}
		
		if(++iColor1 >= rainbow.length)
			iColor1 = 0;
		if(--iColor2 < 0)
			iColor2 = rainbow.length - 1;
		
		// show strip
		OutputManager.addToOutput(strip);
	}

}

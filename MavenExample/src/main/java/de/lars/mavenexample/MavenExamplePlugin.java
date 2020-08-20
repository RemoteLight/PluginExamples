package de.lars.mavenexample;

import de.lars.remotelightplugins.Plugin;

public class MavenExamplePlugin extends Plugin {

	@Override
	public void onEnable() {
		System.out.println("Enabling " + getName() + ". Have fun :)");
		// initialize animation
		SimpleAnimation animation = new SimpleAnimation();
		// register animation
		getInterface().getAnimationManager().addAnimation(animation);
	}

	@Override
	public boolean isLoaded() {
		return true;
	}

}

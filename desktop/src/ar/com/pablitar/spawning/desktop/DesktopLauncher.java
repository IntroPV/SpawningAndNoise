package ar.com.pablitar.spawning.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import ar.com.pablitar.spawning.Spawning;

public class DesktopLauncher {
	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = Spawning.VIEWPORT_WIDTH();
		config.height = Spawning.VIEWPORT_HEIGHT();
		new LwjglApplication(new Spawning(), config);
	}
}

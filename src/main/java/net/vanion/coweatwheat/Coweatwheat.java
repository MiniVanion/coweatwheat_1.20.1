package net.vanion.coweatwheat;

import net.fabricmc.api.ModInitializer;

public class Coweatwheat implements ModInitializer {
	@Override
	public void onInitialize() {
		// Initialization logic here.
		// Since configuration is hardcoded, nothing extra is needed.
		System.out.println("CowEatWheat mod initializing on " +
				(isServer() ? "server" : "client"));
	}

	// Optional: if you need to differentiate, you could check environment,
	// but Fabric Loader will load common mods on both sides.
	private boolean isServer() {
		return System.getProperty("fabric.isServer", "false").equals("true");
	}
}

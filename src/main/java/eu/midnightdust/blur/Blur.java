package eu.midnightdust.blur;

import net.ornithemc.osl.entrypoints.api.client.ClientModInitializer;
import net.ornithemc.osl.lifecycle.api.client.MinecraftClientEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Blur implements ClientModInitializer {
	public static final String MOD_ID = "blur";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	@Override
	public void initClient() {
		MinecraftClientEvents.READY.register(minecraft -> BlurConfig.load());
	}
}

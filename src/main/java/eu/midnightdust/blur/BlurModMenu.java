package eu.midnightdust.blur;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screen.Screen;
import org.polyfrost.oneconfig.api.config.v1.Config;

public final class BlurModMenu implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return this::createScreen;
	}

	private Screen createScreen(Screen parent) {
		try {
			Class<?> helper = Class.forName("org.polyfrost.oneconfig.utils.v1.dsl.ScreensKt");
			Object screen = helper.getMethod("createScreen", Config.class).invoke(null, BlurConfig.INSTANCE);
			return (Screen) screen;
		} catch (ReflectiveOperationException | ClassCastException ignored) {
			return parent;
		}
	}
}

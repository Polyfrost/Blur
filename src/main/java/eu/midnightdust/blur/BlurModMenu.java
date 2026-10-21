package eu.midnightdust.blur;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import org.polyfrost.oneconfig.api.ui.v1.OneConfigUI;
import org.polyfrost.oneconfig.api.ui.v1.page.OneConfigPages;

public final class BlurModMenu implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		// Without Mod Menu the OneConfig compat layer still collects this factory
		// and would register a duplicate entry next to the native config
		if (!FabricLoader.getInstance().isModLoaded("modmenu")) return null;
		return parent -> (Screen) OneConfigUI.createScreen(OneConfigPages.modConfig(BlurConfig.INSTANCE.id));
	}
}

package eu.midnightdust.blur.mixin;

import eu.midnightdust.blur.BlurRenderer;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class ScreenMixin {
	@Inject(method = "renderBackground()V", at = @At("HEAD"), cancellable = true)
	private void blur$renderBackground(CallbackInfo ci) {
		if (BlurRenderer.renderBackground((Screen) (Object) this)) ci.cancel();
	}
}

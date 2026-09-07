package eu.midnightdust.blur.mixin;

import eu.midnightdust.blur.BlurRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin {
	@Inject(method = "render(IIF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/TitleScreen;drawBackground(IIF)V", shift = At.Shift.AFTER))
	private void blur$renderBackground(int mouseX, int mouseY, float delta, CallbackInfo ci) {
		BlurRenderer.renderBackground((Screen) (Object) this);
	}
}

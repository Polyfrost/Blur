package eu.midnightdust.blur.mixin;

import eu.midnightdust.blur.BlurRenderer;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
	@Inject(method = "render(FJ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/pipeline/RenderTarget;bindWrite(Z)V"))
	private void blur$render(float tickDelta, long limitTime, CallbackInfo ci) {
		BlurRenderer.render();
	}
}

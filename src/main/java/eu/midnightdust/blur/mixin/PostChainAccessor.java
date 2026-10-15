package eu.midnightdust.blur.mixin;

import net.minecraft.client.render.PostChain;
import net.minecraft.client.render.PostPass;
import net.minecraft.client.render.pipeline.RenderTarget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(PostChain.class)
public interface PostChainAccessor {
	@Accessor("passes")
	List<PostPass> blur$getPasses();

	@Accessor("customRenderTargets")
	Map<String, RenderTarget> blur$getTargets();
}

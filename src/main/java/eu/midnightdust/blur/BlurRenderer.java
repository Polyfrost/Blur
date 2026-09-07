package eu.midnightdust.blur;

import eu.midnightdust.blur.mixin.PostChainAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiElement;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.CommandBlockScreen;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.inventory.BookEditScreen;
import net.minecraft.client.gui.screen.inventory.menu.InventoryMenuScreen;
import net.minecraft.client.gui.screen.inventory.menu.SignEditScreen;
import net.minecraft.client.render.PostChain;
import net.minecraft.client.render.PostPass;
import net.minecraft.client.render.shaders.Uniform;
import net.minecraft.resource.Identifier;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.io.IOException;

public final class BlurRenderer extends GuiElement {
	private static final Identifier SHADER = new Identifier(Blur.MOD_ID, "shaders/post/fade_in_blur.json");
	private static final BlurRenderer GUI = new BlurRenderer();

	private static PostChain shader;
	private static int shaderWidth;
	private static int shaderHeight;
	private static boolean blurTarget;
	private static boolean backgroundTarget;
	private static long transitionStarted;
	private static float blurStart;
	private static float backgroundStart;
	private static float blurProgress;
	private static float backgroundProgress;

	private BlurRenderer() {
	}

	public static void render() {
		Minecraft minecraft = Minecraft.getInstance();
		updateTarget(minecraft.screen);
		updateProgress();

		if (blurProgress < 0.001F) {
			closeShader();
			return;
		}

		if (!loadShader(minecraft)) return;
		for (PostPass pass : ((PostChainAccessor) shader).blur$getPasses()) {
			Uniform radius = pass.getEffect().getUniform("Radius");
			Uniform progress = pass.getEffect().getUniform("Progress");
			if (radius != null) radius.set(BlurConfig.radius * (isReducedBlur(minecraft.screen) ? 0.5F : 1F));
			if (progress != null) progress.set(blurProgress);
		}
		shader.process(0F);
	}

	public static boolean renderBackground(Screen screen) {
		if (!backgroundTarget || !BlurConfig.useGradient || backgroundProgress < 0.001F) return false;

		int width = screen.width;
		int height = screen.height;
		float scale = (float) Math.sqrt(width * width + height * height) / Math.min(width, height);
		GL11.glPushMatrix();
		GL11.glTranslatef(width / 2F, height / 2F, 0F);
		GL11.glRotatef(rotation(), 0F, 0F, 1F);
		GL11.glScalef(scale, scale, 1F);
		GUI.fillGradient(-width / 2, -height / 2, width / 2, height / 2, color(false), color(true));
		GL11.glPopMatrix();
		return true;
	}

	private static void updateTarget(Screen screen) {
		boolean nextBlur = shouldBlur(screen);
		boolean nextBackground = shouldDrawBackground(screen, nextBlur);
		if (nextBlur == blurTarget && nextBackground == backgroundTarget) return;

		updateProgress();
		blurTarget = nextBlur;
		backgroundTarget = nextBackground;
		blurStart = blurProgress;
		backgroundStart = backgroundProgress;
		transitionStarted = System.nanoTime();
	}

	private static void updateProgress() {
		if (transitionStarted == 0L) return;
		float elapsed = (System.nanoTime() - transitionStarted) / 1_000_000F;
		float blurProgressTime = Math.min(elapsed / (blurTarget ? BlurConfig.fadeTimeMillis : BlurConfig.fadeOutTimeMillis), 1F);
		float backgroundProgressTime = Math.min(elapsed / (backgroundTarget ? BlurConfig.fadeTimeMillis : BlurConfig.fadeOutTimeMillis), 1F);
		blurProgress = interpolate(blurStart, blurTarget ? 1F : 0F, BlurConfig.blurAnimationCurve.apply(blurProgressTime));
		backgroundProgress = interpolate(backgroundStart, backgroundTarget ? 1F : 0F, BlurConfig.backgroundAnimationCurve.apply(backgroundProgressTime));
	}

	private static boolean loadShader(Minecraft minecraft) {
		if (shader != null && (shaderWidth != minecraft.width || shaderHeight != minecraft.height)) {
			closeShader();
		}
		if (shader != null) return true;

		try {
			shader = new PostChain(minecraft.getTextureManager(), minecraft.getResourceManager(), minecraft.getRenderTarget(), SHADER);
			shader.resize(minecraft.width, minecraft.height);
			shaderWidth = minecraft.width;
			shaderHeight = minecraft.height;
			return true;
		} catch (IOException | RuntimeException e) {
			Blur.LOGGER.warn("Could not load Blur+ shader", e);
			closeShader();
			return false;
		}
	}

	private static void closeShader() {
		if (shader != null) shader.close();
		shader = null;
		shaderWidth = 0;
		shaderHeight = 0;
	}

	private static boolean shouldBlur(Screen screen) {
		if (screen == null) return false;
		String name = screen.getClass().getName();
		if (BlurConfig.forceDisabledScreens.contains(name)) return false;
		if (BlurConfig.forceEnabledScreens.contains(name)) return true;
		if (screen instanceof ChatScreen) return false;
		if (screen instanceof TitleScreen) return BlurConfig.blurTitleScreen;
		if (screen instanceof DeathScreen) return BlurConfig.blurDeathScreen;
		if (screen instanceof BookEditScreen) return BlurConfig.blurBooks;
		if (screen instanceof SignEditScreen) return BlurConfig.blurSigns;
		if (screen instanceof CommandBlockScreen) return BlurConfig.blurCommandBlocks;
		if (screen instanceof InventoryMenuScreen) return BlurConfig.blurContainers;
		return true;
	}

	private static boolean shouldDrawBackground(Screen screen, boolean blur) {
		if (!blur) return false;
		return !(screen instanceof TitleScreen) || BlurConfig.darkenTitleScreen;
	}

	public static void renderScreenId() {
		Minecraft minecraft = Minecraft.getInstance();
		if (BlurConfig.showScreenID && minecraft.screen != null) {
			minecraft.textRenderer.drawWithShadow(minecraft.screen.getClass().getName(), 2F, 2F, 0xFF80DFFF);
		}
	}

	private static boolean isReducedBlur(Screen screen) {
		return BlurConfig.reduceInGameBlur && screen instanceof InventoryMenuScreen;
	}

	private static float interpolate(float start, float end, float progress) {
		return start + (end - start) * progress;
	}

	private static int color(boolean second) {
		int alpha = second ? BlurConfig.gradientEndAlpha : BlurConfig.gradientStartAlpha;
		int rgb;
		if (BlurConfig.rainbowMode) {
			float hue = ((System.nanoTime() / 1_000_000_000F) * 0.2F + (second ? 0.35F : 0F)) % 1F;
			rgb = Color.HSBtoRGB(hue, 1F, 1F) & 0xFFFFFF;
		} else {
			String value = second ? BlurConfig.gradientEnd : BlurConfig.gradientStart;
			rgb = Integer.parseInt(value.substring(1), 16);
		}
		return ((int) (alpha * backgroundProgress) << 24) | rgb;
	}

	private static float rotation() {
		if (!BlurConfig.rainbowMode) return BlurConfig.gradientRotation;
		return (BlurConfig.gradientRotation + System.nanoTime() / 50_000_000F) % 360F;
	}
}

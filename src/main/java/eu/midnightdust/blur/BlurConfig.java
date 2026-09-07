package eu.midnightdust.blur;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class BlurConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public static int configVersion = 3;
	public static boolean blurContainers = true;
	public static boolean blurBooks = true;
	public static boolean blurSigns = true;
	public static boolean blurCommandBlocks = true;
	public static boolean blurDeathScreen = false;
	public static boolean blurTitleScreen = false;
	public static boolean darkenTitleScreen = false;
	public static boolean reduceInGameBlur = false;
	public static boolean showScreenID = false;
	public static List<String> forceEnabledScreens = new ArrayList<>(Arrays.asList(
		"dev.emi.emi.screen.RecipeScreen",
		"mezz.jei.gui.recipes.RecipesGui",
		"me.shedaniel.rei.impl.client.gui.screen.DefaultDisplayViewingScreen"
	));
	public static List<String> forceDisabledScreens = new ArrayList<>(Arrays.asList(
		"net.irisshaders.iris.gui.screen.ShaderPackScreen"
	));
	public static boolean useGradient = true;
	public static int radius = 8;
	public static String gradientStart = "#000000";
	public static int gradientStartAlpha = 75;
	public static String gradientEnd = "#000000";
	public static int gradientEndAlpha = 75;
	public static int gradientRotation = 0;
	public static boolean rainbowMode = false;
	public static int fadeTimeMillis = 300;
	public static int fadeOutTimeMillis = 300;
	public static Easing blurAnimationCurve = Easing.FLAT;
	public static Easing backgroundAnimationCurve = Easing.FLAT;

	private BlurConfig() {
	}

	public static void load() {
		File file = file();
		if (!file.isFile()) {
			save();
			return;
		}

		try (FileReader reader = new FileReader(file)) {
			Data data = GSON.fromJson(reader, Data.class);
			if (data != null) data.apply();
		} catch (IOException | RuntimeException e) {
			Blur.LOGGER.warn("Could not read Blur+ configuration", e);
		}
		boolean migrate = configVersion < 3;
		normalize();
		if (migrate) {
			addForceEnabled("mezz.jei.gui.recipes.RecipesGui");
			addForceEnabled("me.shedaniel.rei.impl.client.gui.screen.DefaultDisplayViewingScreen");
			save();
		}
	}

	public static void save() {
		File file = file();
		File parent = file.getParentFile();
		if (!parent.isDirectory() && !parent.mkdirs()) {
			Blur.LOGGER.warn("Could not create Blur+ configuration directory");
			return;
		}

		try (FileWriter writer = new FileWriter(file)) {
			GSON.toJson(new Data(), writer);
		} catch (IOException e) {
			Blur.LOGGER.warn("Could not save Blur+ configuration", e);
		}
	}

	private static File file() {
		return new File(Minecraft.getInstance().gameDir, "config/blur.json");
	}

	private static void normalize() {
		configVersion = Math.max(configVersion, 3);
		gradientStart = color(gradientStart);
		gradientEnd = color(gradientEnd);
		radius = clamp(radius, 0, 100);
		gradientStartAlpha = clamp(gradientStartAlpha, 0, 255);
		gradientEndAlpha = clamp(gradientEndAlpha, 0, 255);
		gradientRotation = clamp(gradientRotation, 0, 360);
		fadeTimeMillis = clamp(fadeTimeMillis, 1, 2000);
		fadeOutTimeMillis = clamp(fadeOutTimeMillis, 1, 2000);
		if (forceEnabledScreens == null) forceEnabledScreens = new ArrayList<>();
		if (forceDisabledScreens == null) forceDisabledScreens = new ArrayList<>();
		if (blurAnimationCurve == null) blurAnimationCurve = Easing.FLAT;
		if (backgroundAnimationCurve == null) backgroundAnimationCurve = Easing.FLAT;
	}

	private static int clamp(int value, int min, int max) {
		return Math.max(min, Math.min(max, value));
	}

	private static void addForceEnabled(String screen) {
		if (!forceEnabledScreens.contains(screen)) forceEnabledScreens.add(screen);
	}

	private static String color(String value) {
		return value != null && value.matches("#[0-9a-fA-F]{6}") ? value : "#000000";
	}

	public enum Easing {
		FLAT,
		SINE,
		QUAD,
		CUBIC,
		QUART,
		QUINT,
		EXPO,
		CIRC,
		BACK,
		ELASTIC;

		public float apply(float value) {
			switch (this) {
				case SINE: return (float) (0.5D - Math.cos(value * Math.PI) / 2D);
				case QUAD: return value * value;
				case CUBIC: return value * value * value;
				case QUART: return value * value * value * value;
				case QUINT: return value * value * value * value * value;
				case EXPO: return value == 0F ? 0F : (float) Math.pow(2D, 10D * value - 10D);
				case CIRC: return (float) (1D - Math.sqrt(1D - value * value));
				case BACK: return 2.70158F * value * value * value - 1.70158F * value * value;
				case ELASTIC:
					return value == 0F || value == 1F ? value : (float) (-Math.pow(2D, 10D * value - 10D) * Math.sin((value * 10D - 10.75D) * (2D * Math.PI / 3D)));
				default: return value;
			}
		}
	}

	private static final class Data {
		int configVersion = BlurConfig.configVersion;
		boolean blurContainers = BlurConfig.blurContainers;
		boolean blurBooks = BlurConfig.blurBooks;
		boolean blurSigns = BlurConfig.blurSigns;
		boolean blurCommandBlocks = BlurConfig.blurCommandBlocks;
		boolean blurDeathScreen = BlurConfig.blurDeathScreen;
		boolean blurTitleScreen = BlurConfig.blurTitleScreen;
		boolean darkenTitleScreen = BlurConfig.darkenTitleScreen;
		boolean reduceInGameBlur = BlurConfig.reduceInGameBlur;
		boolean showScreenID = BlurConfig.showScreenID;
		List<String> forceEnabledScreens = new ArrayList<>(BlurConfig.forceEnabledScreens);
		List<String> forceDisabledScreens = new ArrayList<>(BlurConfig.forceDisabledScreens);
		boolean useGradient = BlurConfig.useGradient;
		int radius = BlurConfig.radius;
		String gradientStart = BlurConfig.gradientStart;
		int gradientStartAlpha = BlurConfig.gradientStartAlpha;
		String gradientEnd = BlurConfig.gradientEnd;
		int gradientEndAlpha = BlurConfig.gradientEndAlpha;
		int gradientRotation = BlurConfig.gradientRotation;
		boolean rainbowMode = BlurConfig.rainbowMode;
		int fadeTimeMillis = BlurConfig.fadeTimeMillis;
		int fadeOutTimeMillis = BlurConfig.fadeOutTimeMillis;
		Easing blurAnimationCurve = BlurConfig.blurAnimationCurve;
		Easing backgroundAnimationCurve = BlurConfig.backgroundAnimationCurve;

		void apply() {
			BlurConfig.configVersion = configVersion;
			BlurConfig.blurContainers = blurContainers;
			BlurConfig.blurBooks = blurBooks;
			BlurConfig.blurSigns = blurSigns;
			BlurConfig.blurCommandBlocks = blurCommandBlocks;
			BlurConfig.blurDeathScreen = blurDeathScreen;
			BlurConfig.blurTitleScreen = blurTitleScreen;
			BlurConfig.darkenTitleScreen = darkenTitleScreen;
			BlurConfig.reduceInGameBlur = reduceInGameBlur;
			BlurConfig.showScreenID = showScreenID;
			BlurConfig.forceEnabledScreens = forceEnabledScreens;
			BlurConfig.forceDisabledScreens = forceDisabledScreens;
			BlurConfig.useGradient = useGradient;
			BlurConfig.radius = radius;
			BlurConfig.gradientStart = gradientStart;
			BlurConfig.gradientStartAlpha = gradientStartAlpha;
			BlurConfig.gradientEnd = gradientEnd;
			BlurConfig.gradientEndAlpha = gradientEndAlpha;
			BlurConfig.gradientRotation = gradientRotation;
			BlurConfig.rainbowMode = rainbowMode;
			BlurConfig.fadeTimeMillis = fadeTimeMillis;
			BlurConfig.fadeOutTimeMillis = fadeOutTimeMillis;
			BlurConfig.blurAnimationCurve = blurAnimationCurve;
			BlurConfig.backgroundAnimationCurve = backgroundAnimationCurve;
		}
	}
}

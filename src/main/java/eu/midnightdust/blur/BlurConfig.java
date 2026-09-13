package eu.midnightdust.blur;

import org.polyfrost.oneconfig.api.config.v1.Config;
import org.polyfrost.oneconfig.api.config.v1.annotations.Dropdown;
import org.polyfrost.oneconfig.api.config.v1.annotations.Include;
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider;
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch;
import org.polyfrost.oneconfig.api.config.v1.annotations.Text;
import org.polyfrost.oneconfig.api.config.v1.annotations.TextList;

import java.util.Arrays;

/** OneConfig presentation and persistence for Blur+'s existing JSON schema. */
public final class BlurConfig extends Config {
	private static final String SCREENS = "Screens";
	private static final String STYLE = "Style";
	private static final String ANIMATION = "Animation";
	private static final String ADVANCED = "Advanced";

	public static final BlurConfig INSTANCE = new BlurConfig();

	@Include
	public static int configVersion = 3;

	@Switch(title = "Blur containers", category = SCREENS)
	public static boolean blurContainers = true;

	@Switch(title = "Blur books", category = SCREENS)
	public static boolean blurBooks = true;

	@Switch(title = "Blur signs", category = SCREENS)
	public static boolean blurSigns = true;

	@Switch(title = "Blur command blocks", category = SCREENS)
	public static boolean blurCommandBlocks = true;

	@Switch(title = "Blur death screen", category = SCREENS)
	public static boolean blurDeathScreen = false;

	@Switch(title = "Blur title screen", category = SCREENS)
	public static boolean blurTitleScreen = false;

	@Switch(title = "Darken title screen", category = SCREENS)
	public static boolean darkenTitleScreen = false;

	@Switch(title = "Reduce container blur", category = SCREENS)
	public static boolean reduceInGameBlur = false;

	@Switch(title = "Show screen ID", category = ADVANCED)
	public static boolean showScreenID = false;

	@TextList(title = "Always blur these screens", category = ADVANCED)
	public static String[] forceEnabledScreens = {
		"dev.emi.emi.screen.RecipeScreen",
		"mezz.jei.gui.recipes.RecipesGui",
		"me.shedaniel.rei.impl.client.gui.screen.DefaultDisplayViewingScreen"
	};

	@TextList(title = "Never blur these screens", category = ADVANCED)
	public static String[] forceDisabledScreens = {
		"net.irisshaders.iris.gui.screen.ShaderPackScreen"
	};

	@Switch(title = "Use gradient", category = STYLE)
	public static boolean useGradient = true;

	@Slider(title = "Blur radius", category = STYLE, min = 0, max = 100)
	public static int radius = 8;

	@Text(title = "Gradient start color", description = "A hexadecimal RGB color, for example #000000.", category = STYLE, regex = "#[0-9a-fA-F]{6}")
	public static String gradientStart = "#000000";

	@Slider(title = "Gradient start opacity", category = STYLE, min = 0, max = 255)
	public static int gradientStartAlpha = 75;

	@Text(title = "Gradient end color", description = "A hexadecimal RGB color, for example #000000.", category = STYLE, regex = "#[0-9a-fA-F]{6}")
	public static String gradientEnd = "#000000";

	@Slider(title = "Gradient end opacity", category = STYLE, min = 0, max = 255)
	public static int gradientEndAlpha = 75;

	@Slider(title = "Gradient rotation", category = STYLE, min = 0, max = 360)
	public static int gradientRotation = 0;

	@Switch(title = "Rainbow gradient", category = STYLE)
	public static boolean rainbowMode = false;

	@Slider(title = "Fade in", description = "Milliseconds until blur and background reach full strength.", category = ANIMATION, min = 1, max = 2000)
	public static int fadeTimeMillis = 300;

	@Slider(title = "Fade out", description = "Milliseconds until blur and background disappear.", category = ANIMATION, min = 1, max = 2000)
	public static int fadeOutTimeMillis = 300;

	@Dropdown(title = "Blur easing", category = ANIMATION, options = {"FLAT", "SINE", "QUAD", "CUBIC", "QUART", "QUINT", "EXPO", "CIRC", "BACK", "ELASTIC"})
	public static String blurAnimationCurve = "FLAT";

	@Dropdown(title = "Background easing", category = ANIMATION, options = {"FLAT", "SINE", "QUAD", "CUBIC", "QUART", "QUINT", "EXPO", "CIRC", "BACK", "ELASTIC"})
	public static String backgroundAnimationCurve = "FLAT";

	private BlurConfig() {
		super("blur.json", "assets/blur/icon.png", "Legacy GUI Blur", Category.VISUALS);
	}

	public void load() {
		preload();
		boolean migrate = configVersion < 3;
		normalize();
		if (migrate) {
			addForceEnabled("mezz.jei.gui.recipes.RecipesGui");
			addForceEnabled("me.shedaniel.rei.impl.client.gui.screen.DefaultDisplayViewingScreen");
		}
		save();
	}

	public static Easing getBlurAnimationCurve() {
		return Easing.parse(blurAnimationCurve);
	}

	public static Easing getBackgroundAnimationCurve() {
		return Easing.parse(backgroundAnimationCurve);
	}

	public static boolean contains(String[] screens, String screen) {
		return screens != null && Arrays.asList(screens).contains(screen);
	}

	private static void normalize() {
		configVersion = 3;
		gradientStart = color(gradientStart);
		gradientEnd = color(gradientEnd);
		radius = clamp(radius, 0, 100);
		gradientStartAlpha = clamp(gradientStartAlpha, 0, 255);
		gradientEndAlpha = clamp(gradientEndAlpha, 0, 255);
		gradientRotation = clamp(gradientRotation, 0, 360);
		fadeTimeMillis = clamp(fadeTimeMillis, 1, 2000);
		fadeOutTimeMillis = clamp(fadeOutTimeMillis, 1, 2000);
		if (forceEnabledScreens == null) forceEnabledScreens = new String[0];
		if (forceDisabledScreens == null) forceDisabledScreens = new String[0];
		blurAnimationCurve = getBlurAnimationCurve().name();
		backgroundAnimationCurve = getBackgroundAnimationCurve().name();
	}

	private static void addForceEnabled(String screen) {
		if (contains(forceEnabledScreens, screen)) return;
		forceEnabledScreens = Arrays.copyOf(forceEnabledScreens, forceEnabledScreens.length + 1);
		forceEnabledScreens[forceEnabledScreens.length - 1] = screen;
	}

	private static int clamp(int value, int min, int max) {
		return Math.max(min, Math.min(max, value));
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

		public static Easing parse(String value) {
			try {
				return value == null ? FLAT : valueOf(value);
			} catch (IllegalArgumentException ignored) {
				return FLAT;
			}
		}

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
}

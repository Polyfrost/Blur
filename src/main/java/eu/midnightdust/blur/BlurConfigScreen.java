package eu.midnightdust.blur;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class BlurConfigScreen extends Screen {
	private final Screen parent;
	private int page;
	private TextFieldWidget firstInput;
	private TextFieldWidget secondInput;

	public BlurConfigScreen(Screen parent) {
		this.parent = parent;
	}

	@Override
	public void init() {
		buttons.clear();
		buttons.add(new ButtonWidget(0, width / 2 - 155, height - 28, 100, 20, "Done"));
		buttons.add(new ButtonWidget(1, width / 2 - 50, height - 28, 48, 20, "<"));
		buttons.add(new ButtonWidget(2, width / 2 + 2, height - 28, 48, 20, ">"));

		if (page == 0) addScreenButtons();
		if (page == 1) addStyleButtons();
		if (page == 2) addAdvancedInputs();
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		renderBackground();
		super.render(mouseX, mouseY, delta);
		drawCenteredString(textRenderer, "Blur+ Config", width / 2, 16, -1);
		drawCenteredString(textRenderer, page == 0 ? "Screens" : page == 1 ? "Style and animations" : "Advanced", width / 2, 30, 0xA0A0A0);

		if (page == 1) {
			drawString(textRenderer, "Start color", width / 2 - 155, 154, -1);
			drawString(textRenderer, "End color", width / 2 + 10, 154, -1);
			firstInput.render();
			secondInput.render();
		}

		if (page == 2) {
			drawString(textRenderer, "Force-enabled screens (comma separated)", width / 2 - 150, 54, -1);
			drawString(textRenderer, "Force-disabled screens (comma separated)", width / 2 - 150, 104, -1);
			firstInput.render();
			secondInput.render();
			drawCenteredString(textRenderer, "Edit config/blur.json for long lists.", width / 2, 154, 0xA0A0A0);
		}
	}

	@Override
	public void tick() {
		if (firstInput != null) firstInput.tick();
		if (secondInput != null) secondInput.tick();
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		super.mouseClicked(mouseX, mouseY, button);
		if (firstInput != null) firstInput.mouseClicked(mouseX, mouseY, button);
		if (secondInput != null) secondInput.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	protected void keyPressed(char character, int keyCode) {
		if (firstInput != null) firstInput.keyPressed(character, keyCode);
		if (secondInput != null) secondInput.keyPressed(character, keyCode);
		super.keyPressed(character, keyCode);
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		applyInputs();
		switch (button.id) {
			case 0:
				BlurConfig.save();
				minecraft.openScreen(parent);
				return;
			case 1:
				page = (page + 2) % 3;
				break;
			case 2:
				page = (page + 1) % 3;
				break;
			case 10: BlurConfig.blurContainers = !BlurConfig.blurContainers; break;
			case 11: BlurConfig.blurBooks = !BlurConfig.blurBooks; break;
			case 12: BlurConfig.blurSigns = !BlurConfig.blurSigns; break;
			case 13: BlurConfig.blurCommandBlocks = !BlurConfig.blurCommandBlocks; break;
			case 14: BlurConfig.blurDeathScreen = !BlurConfig.blurDeathScreen; break;
			case 15: BlurConfig.blurTitleScreen = !BlurConfig.blurTitleScreen; break;
			case 16: BlurConfig.darkenTitleScreen = !BlurConfig.darkenTitleScreen; break;
			case 17: BlurConfig.reduceInGameBlur = !BlurConfig.reduceInGameBlur; break;
			case 18: BlurConfig.showScreenID = !BlurConfig.showScreenID; break;
			case 20: BlurConfig.useGradient = !BlurConfig.useGradient; break;
			case 21: BlurConfig.rainbowMode = !BlurConfig.rainbowMode; break;
			case 22: BlurConfig.radius = bounded(BlurConfig.radius, 0, 100); break;
			case 23: BlurConfig.gradientStartAlpha = bounded(BlurConfig.gradientStartAlpha, 0, 255); break;
			case 24: BlurConfig.gradientEndAlpha = bounded(BlurConfig.gradientEndAlpha, 0, 255); break;
			case 25: BlurConfig.gradientRotation = bounded(BlurConfig.gradientRotation, 0, 360); break;
			case 26: BlurConfig.fadeTimeMillis = bounded(BlurConfig.fadeTimeMillis, 1, 2000); break;
			case 27: BlurConfig.fadeOutTimeMillis = bounded(BlurConfig.fadeOutTimeMillis, 1, 2000); break;
			case 28: BlurConfig.blurAnimationCurve = next(BlurConfig.blurAnimationCurve); break;
			case 29: BlurConfig.backgroundAnimationCurve = next(BlurConfig.backgroundAnimationCurve); break;
			default: return;
		}
		init();
	}

	@Override
	public void removed() {
		applyInputs();
		BlurConfig.save();
	}

	private void addScreenButtons() {
		addToggle(10, "Containers", BlurConfig.blurContainers, 0);
		addToggle(11, "Books", BlurConfig.blurBooks, 1);
		addToggle(12, "Signs", BlurConfig.blurSigns, 2);
		addToggle(13, "Command blocks", BlurConfig.blurCommandBlocks, 3);
		addToggle(14, "Death screen", BlurConfig.blurDeathScreen, 4);
		addToggle(15, "Title screen", BlurConfig.blurTitleScreen, 5);
		addToggle(16, "Darken title", BlurConfig.darkenTitleScreen, 6);
		addToggle(17, "Reduce in-game blur", BlurConfig.reduceInGameBlur, 7);
		addToggle(18, "Show screen ID", BlurConfig.showScreenID, 8);
	}

	private void addStyleButtons() {
		addToggle(20, "Gradient", BlurConfig.useGradient, 0);
		addToggle(21, "Rainbow", BlurConfig.rainbowMode, 1);
		addValue(22, "Radius", BlurConfig.radius, 2);
		addValue(23, "Start alpha", BlurConfig.gradientStartAlpha, 3);
		addValue(24, "End alpha", BlurConfig.gradientEndAlpha, 4);
		addValue(25, "Rotation", BlurConfig.gradientRotation, 5);
		addValue(26, "Fade in", BlurConfig.fadeTimeMillis, 6);
		addValue(27, "Fade out", BlurConfig.fadeOutTimeMillis, 7);
		addValue(28, "Blur curve", BlurConfig.blurAnimationCurve, 8);
		addValue(29, "Background curve", BlurConfig.backgroundAnimationCurve, 9);
		firstInput = input(10, width / 2 - 155, 166, 145, BlurConfig.gradientStart);
		secondInput = input(11, width / 2 + 10, 166, 145, BlurConfig.gradientEnd);
	}

	private void addAdvancedInputs() {
		firstInput = input(10, 70, join(BlurConfig.forceEnabledScreens));
		secondInput = input(11, 120, join(BlurConfig.forceDisabledScreens));
	}

	private TextFieldWidget input(int id, int y, String value) {
		return input(id, width / 2 - 150, y, 300, value);
	}

	private TextFieldWidget input(int id, int x, int y, int width, String value) {
		TextFieldWidget input = new TextFieldWidget(id, textRenderer, x, y, width, 20);
		input.setMaxLength(2048);
		input.setText(value);
		return input;
	}

	private void addToggle(int id, String name, boolean value, int index) {
		buttons.add(new ButtonWidget(id, x(index), y(index), 150, 20, name + ": " + (value ? "On" : "Off")));
	}

	private void addValue(int id, String name, Object value, int index) {
		buttons.add(new ButtonWidget(id, x(index), y(index), 150, 20, name + ": " + value));
	}

	private void applyInputs() {
		if (page == 1 && firstInput != null) {
			if (firstInput.getText().matches("#[0-9a-fA-F]{6}")) BlurConfig.gradientStart = firstInput.getText();
			if (secondInput.getText().matches("#[0-9a-fA-F]{6}")) BlurConfig.gradientEnd = secondInput.getText();
		}
		if (page == 2 && firstInput != null) {
			BlurConfig.forceEnabledScreens = split(firstInput.getText());
			BlurConfig.forceDisabledScreens = split(secondInput.getText());
		}
	}

	private int x(int index) {
		return width / 2 - 155 + index % 2 * 160;
	}

	private int y(int index) {
		return 46 + index / 2 * 22;
	}

	private int bounded(int value, int min, int max) {
		int next = value + (Screen.isShiftDown() ? -1 : 1);
		return next > max ? min : next < min ? max : next;
	}

	private BlurConfig.Easing next(BlurConfig.Easing easing) {
		BlurConfig.Easing[] values = BlurConfig.Easing.values();
		int index = (easing.ordinal() + (Screen.isShiftDown() ? values.length - 1 : 1)) % values.length;
		return values[index];
	}

	private static String join(List<String> values) {
		return String.join(",", values);
	}

	private static List<String> split(String value) {
		if (value.trim().isEmpty()) return new ArrayList<>();
		List<String> values = new ArrayList<>();
		for (String entry : Arrays.asList(value.split(","))) {
			String trimmed = entry.trim();
			if (!trimmed.isEmpty()) values.add(trimmed);
		}
		return values;
	}
}

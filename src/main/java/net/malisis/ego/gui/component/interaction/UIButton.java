/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 PaleoCrafter, Ordinastie
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.malisis.ego.gui.component.interaction;

import net.malisis.ego.font.FontOptions;
import net.malisis.ego.gui.MalisisGui;
import net.malisis.ego.gui.component.MouseButton;
import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.component.content.IContent;
import net.malisis.ego.gui.component.content.IContentHolder;
import net.malisis.ego.gui.element.position.Position;
import net.malisis.ego.gui.element.position.Position.IPosition;
import net.malisis.ego.gui.element.size.Size;
import net.malisis.ego.gui.event.MouseEvent.MouseClick;
import net.malisis.ego.gui.event.MouseEvent.MouseDown;
import net.malisis.ego.gui.event.MouseEvent.MouseRightClick;
import net.malisis.ego.gui.event.MouseEvent.MouseUp;
import net.malisis.ego.gui.render.GuiIcon;
import net.malisis.ego.gui.render.shape.GuiShape;
import net.malisis.ego.gui.text.GuiText;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;

/**
 * UIButton
 *
 * @author Ordinastie, PaleoCrafter
 */
public class UIButton extends UIComponent implements IContentHolder
{
	/** The {@link FontOptions} to use by default for the {@link UIButton} content. */
	protected final FontOptions fontOptions = FontOptions.builder()
														 .color(0xFFFFFF)
														 .shadow()
														 .when(this::isHovered)
														 .color(0xFFFFA0)
														 .when(this::isDisabled)
														 .color(0xCCCCCC)
														 .build();

	protected IPosition offsetPosition = Position.of(() -> isPressed() ? 1 : 0, () -> isPressed() ? 1 : 0);
	protected IPosition contentPosition = null;

	/** Content used for this {@link UIButton}. */
	protected IContent content;
	/** Whether this {@link UIButton} is currently being pressed. */
	protected boolean isPressed = false;

	/**
	 * Instantiates a new {@link UIButton}.
	 */
	public UIButton()
	{
		setAutoSize();
		setBackground(GuiShape.builder(this).border(5).icon(() -> {
			if (isDisabled())
				return GuiIcon.BUTTON_DISABLED;
			if (isHovered())
				return isPressed() ? GuiIcon.BUTTON_HOVER_PRESSED : GuiIcon.BUTTON_HOVER;
			return GuiIcon.BUTTON;
		}).build());
		setForeground(this::content);
	}

	/**
	 * Instantiates a new {@link UIButton} with specified label.
	 *
	 * @param text the text
	 */
	public UIButton(String text)
	{
		this();
		setText(text);
	}

	/**
	 * Instantiates a new {@link UIButton} with specified content.
	 *
	 * @param content the content
	 */
	public UIButton(UIComponent content)
	{
		this();
		setContent(content);
	}

	//#region Getters/Setters

	/**
	 * Sets the content for this {@link UIButton}.
	 *
	 * @param content the content
	 */
	public void setContent(IContent content)
	{
		this.content = content;
		content.setParent(this);
		content.setPosition(Position.middleCenter(content).plus(offsetPosition));
	}

	public void setText(String text)
	{
		GuiText gt = GuiText.of(text, fontOptions);
		setContent(gt);
	}

	/**
	 * Gets the {@link UIComponent} used as content for this {@link UIButton}.
	 *
	 * @return the content component
	 */
	@Override
	public IContent content()
	{
		return content;
	}

	/**
	 * Checks if this {@link UIButton} is currently being pressed.
	 *
	 * @return true, if is pressed
	 */
	public boolean isPressed()
	{
		return isPressed;
	}

	/**
	 * Sets whether the size of this {@link UIButton} should be calculated automatically.
	 */
	public void setAutoSize()
	{
		setSize(Size.sizeOfContent(this, 6, 6));
	}

	public FontOptions defaultFontOptions()
	{
		return fontOptions;
	}

	/**
	 * Convenience methods to register onClick callback.
	 *
	 * @param onClick callback when this {@link UIButton} is clicked.
	 */
	public void onClick(Runnable onClick)
	{
		register(MouseClick.class, e -> {
			onClick.run();
			return true;
		});
	}

	//#end Getters/Setters
	@Override
	public void click(MouseButton button)
	{
		if (!isEnabled())
			return;

		fireEvent(button == MouseButton.LEFT ? new MouseClick<>(this) : new MouseRightClick<>(this));
		if (button == MouseButton.LEFT)
			MalisisGui.playSound(SoundEvents.UI_BUTTON_CLICK);
	}

	@Override
	public void mouseDown(MouseButton button)
	{
		if (!isEnabled())
			return;
		if (fireEvent(new MouseDown<>(this, button)))
			return;
		if (button == MouseButton.LEFT)
			isPressed = true;
	}

	@Override
	public void mouseUp(MouseButton button)
	{
		if (!isEnabled())
			return;
		if (fireEvent(new MouseUp<>(this, button)))
			return;
		if (button == MouseButton.LEFT)
			isPressed = false;
	}

	@Override
	public boolean keyTyped(char keyChar, int keyCode)
	{
		if (keyCode != Keyboard.KEY_RETURN && keyCode != Keyboard.KEY_NUMPADENTER && keyCode != Keyboard.KEY_SPACE)
			return false;

		fireEvent(new MouseClick<>(this));
		MalisisGui.playSound(SoundEvents.UI_BUTTON_CLICK);
		return true;
	}

	@Override
	public String getPropertyString()
	{
		return "[" + TextFormatting.GREEN + content + TextFormatting.RESET + "] " + super.getPropertyString();
	}

}

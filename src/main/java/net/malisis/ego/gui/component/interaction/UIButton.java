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

import static com.google.common.base.Preconditions.*;

import net.malisis.ego.font.FontOptions;
import net.malisis.ego.gui.EGOGui;
import net.malisis.ego.gui.component.MouseButton;
import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.component.content.IContent;
import net.malisis.ego.gui.component.content.IContent.IContentHolder;
import net.malisis.ego.gui.component.content.IContent.IContentSetter;
import net.malisis.ego.gui.element.IOffset;
import net.malisis.ego.gui.element.Padding;
import net.malisis.ego.gui.element.Padding.IPadded;
import net.malisis.ego.gui.element.position.Position;
import net.malisis.ego.gui.element.position.Position.IPosition;
import net.malisis.ego.gui.element.size.Size;
import net.malisis.ego.gui.event.mouse.MouseEvent.MouseDown;
import net.malisis.ego.gui.event.mouse.MouseEvent.MouseLeftClick;
import net.malisis.ego.gui.event.mouse.MouseEvent.MouseRightClick;
import net.malisis.ego.gui.event.mouse.MouseEvent.MouseUp;
import net.malisis.ego.gui.render.GuiIcon;
import net.malisis.ego.gui.render.shape.GuiShape;
import net.malisis.ego.gui.text.GuiText;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;

import java.util.function.Consumer;

import javax.annotation.Nonnull;

/**
 * UIButton
 *
 * @author Ordinastie, PaleoCrafter
 */
public class UIButton extends UIComponent implements IContentHolder, IContentSetter, IOffset, IPadded
{
	protected final IPosition offsetPosition = Position.of(() -> isPressed() ? 1 : 0, () -> isPressed() ? 1 : 0);

	/** Content used for this {@link UIButton}. */
	protected IContent content;
	/** Whether this {@link UIButton} is currently being pressed. */
	protected boolean isPressed = false;

	protected Padding padding = Padding.NO_PADDING;

	/**
	 * Instantiates a new {@link UIButton}.
	 */
	public UIButton()
	{
		//setAutoSize();
		setBackground(GuiShape.builder(this)
							  .border(5)
							  .icon(() -> {
								  if (isDisabled())
									  return GuiIcon.BUTTON_DISABLED;
								  if (isHovered())
									  return isPressed() ? GuiIcon.BUTTON_HOVER_PRESSED : GuiIcon.BUTTON_HOVER;
								  return GuiIcon.BUTTON;
							  })
							  .build());
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

	public void setPadding(Padding padding)
	{
		this.padding = padding;
	}

	@Nonnull
	@Override
	public Padding padding()
	{
		return padding;
	}

	/**
	 * Sets the content for this {@link UIButton}.
	 *
	 * @param content the content
	 */
	@Override
	public void setContent(IContent content)
	{
		this.content = content;
		setForeground(content);
	}

	public void setText(String text)
	{
		FontOptions fontOptions = FontOptions.EMPTY;
		if (content instanceof GuiText)
			fontOptions = ((GuiText) content).getFontOptions();
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

	@Override
	public IPosition offset()
	{
		return offsetPosition;
	}

	/**
	 * Checks if this {@link UIButton} is currently being pressed.
	 *
	 * @return true, if is pressed
	 */
	public boolean isPressed()
	{
		return isPressed && isHovered();
	}

	/**
	 * Sets whether the size of this {@link UIButton} should be calculated automatically.
	 */
	public void setAutoSize()
	{
		setSize(Size.sizeOfContent(this, 6, 6));
	}

	/**
	 * Convenience methods to register onLeftClick callback.
	 *
	 * @param onClick callback when this {@link UIButton} is clicked.
	 */
	public void onClick(Runnable onClick)
	{
		onLeftClick(e -> {
			onClick.run();
			return true;
		});
	}

	public void onClick(Consumer<UIButton> onClick)
	{
		onLeftClick(e -> {
			onClick.accept(this);
			return true;
		});
	}

	//#end Getters/Setters
	@Override
	public void click(MouseButton button)
	{
		if (!isEnabled())
			return;

		fireEvent(button == MouseButton.LEFT ? new MouseLeftClick<>(this) : new MouseRightClick<>(this));
		if (button == MouseButton.LEFT)
			EGOGui.playSound(SoundEvents.UI_BUTTON_CLICK);
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

		fireEvent(new MouseLeftClick<>(this));
		EGOGui.playSound(SoundEvents.UI_BUTTON_CLICK);
		return true;
	}

	@Override
	public String getPropertyString()
	{
		return "[" + TextFormatting.GREEN + content + TextFormatting.RESET + "] " + super.getPropertyString();
	}

	public static UIButtonBuilder builder()
	{
		return new UIButtonBuilder();
	}

	public static class UIButtonBuilder extends UIContentHolderBuilder<UIButtonBuilder, UIButton>
	{
		private static final Padding TEXT_PADDING = Padding.of(5);
		private static final Padding ICON_PADDING = Padding.of(2);
		protected Consumer<UIButton> onClick;
		protected Padding padding = Padding.of(5);

		protected UIButtonBuilder()
		{
			padding(TEXT_PADDING);
			tb().middleAligned();
			tb().centered();
			sizeOfContent();
			fob().color(0xFFFFFF)
				 .shadow()
				 .when(UIButton::isHovered)
				 .color(0xFFFFA0)
				 .when(UIButton::isDisabled)
				 .color(0xCCCCCC)
				 .base();
		}

		public UIButtonBuilder padding(Padding padding)
		{
			this.padding = padding;
			return this;
		}

		public UIButtonBuilder padding(int padding)
		{
			this.padding = Padding.of(padding);
			return this;
		}

		@Override
		public UIButtonBuilder icon(GuiIcon icon)
		{
			if (padding == TEXT_PADDING) //still default padding
				padding(ICON_PADDING);
			return super.icon(icon);
		}

		public UIButtonBuilder onClick(Consumer<UIButton> onClick)
		{
			this.onClick = checkNotNull(onClick);
			return this;
		}

		public UIButtonBuilder onClick(Runnable onClick)
		{
			return onClick(btn -> onClick.run());
		}

		public UIButtonBuilder link(String url)
		{
			return onClick(() -> EGOGui.openLink(url));
		}

		@Override
		public UIButton build()
		{
			UIButton button = build(new UIButton());
			button.setPadding(padding);

			if (onClick != null)
				button.onClick(onClick);

			return button;
		}
	}
}

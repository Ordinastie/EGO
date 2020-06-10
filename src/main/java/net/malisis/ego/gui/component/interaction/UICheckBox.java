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
import net.malisis.ego.gui.component.MouseButton;
import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.component.content.IContent;
import net.malisis.ego.gui.component.content.IContent.IContentHolder;
import net.malisis.ego.gui.component.content.IContent.IContentSetter;
import net.malisis.ego.gui.element.size.Size;
import net.malisis.ego.gui.event.ValueChange;
import net.malisis.ego.gui.event.ValueChange.IValueChangeBuilder;
import net.malisis.ego.gui.event.ValueChange.IValueChangeEventRegister;
import net.malisis.ego.gui.render.GuiIcon;
import net.malisis.ego.gui.render.shape.GuiShape;
import net.malisis.ego.gui.text.GuiText;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;

/**
 * UICheckBox
 *
 * @author Ordinastie
 */
public class UICheckBox extends UIComponent implements IContentHolder, IContentSetter, IValueChangeEventRegister<UICheckBox, Boolean>
{
	protected final FontOptions fontOptions = FontOptions.builder()
														 .color(0x444444)
														 .when(this::isHovered)
														 .color(0x777777)
														 .when(this::isDisabled)
														 .color(0xCCCCCC)
														 .build();
	/** The content for this {@link UICheckBox}. */
	protected IContent content;
	/** Whether this {@link UICheckBox} is checked. */
	protected boolean checked;

	public UICheckBox(String text)
	{
		setText(text);
		//TODO: height = max(14, heightOfContent) ?
		setSize(Size.sizeOfContent(this, 15, 4));

		//Background
		setBackground(GuiShape.builder(this)
							  .position(1, 0)
							  .size(12, 12)
							  .icon(this, GuiIcon.CHECKBOX_BG, GuiIcon.CHECKBOX_BG_HOVER, GuiIcon.CHECKBOX_BG_DISABLED)
							  .build());

		//Foreground
		GuiShape check = GuiShape.builder(this)
								 .position(1, 1)
								 .size(12, 10)
								 .zIndex(10)
								 .icon(this, GuiIcon.CHECKBOX, GuiIcon.CHECKBOX_HOVER, GuiIcon.CHECKBOX_DISABLED)
								 .build();

		setForeground(r -> {
			if (isChecked())
				check.render(r);
			if (content() != null)
				content().render(r);
		});
	}

	public UICheckBox()
	{
		this(null);
	}

	//#region Getters/Setters

	/**
	 * Sets the content for this {@link UICheckBox}.
	 *
	 * @param content the content
	 */
	@Override
	public void setContent(IContent content)
	{
		this.content = content;
		//		content.setParent(this);
		//		content.setPosition(Position.of(15, 2));
	}

	public void setText(String text)
	{
		GuiText gt = GuiText.of(text, fontOptions);
		setContent(gt);
	}

	/**
	 * Gets the {@link UIComponent} used as content for this {@link UICheckBox}.
	 *
	 * @return the content component
	 */
	@Override
	public IContent content()
	{
		return content;
	}

	//#end Getters/Setters

	/**
	 * Checks if this {@link UICheckBox} is checked.
	 *
	 * @return whether this {@link UICheckBox} is checked or not.
	 */
	public boolean isChecked()
	{
		return checked;
	}

	/**
	 * Sets the state for this {@link UICheckBox}.<br>
	 * Does not fire {@link ValueChange} event.
	 *
	 * @param checked true if checked
	 * @return this {@link UIComponent}
	 */
	public UICheckBox setChecked(boolean checked)
	{
		this.checked = checked;
		return this;
	}

	/**
	 * Checks this {@link UICheckBox} if it is unchecked. Unchecks it otherwise.
	 */
	public void toggle()
	{
		if (isChecked())
			uncheck();
		else
			check();
	}

	/**
	 * Checks this {@link UICheckBox}. Does nothing if already checked.
	 */
	public void check()
	{
		if (checked || fireEvent(new ValueChange.Pre<>(this, false, true)))
			return;

		checked = true;
		fireEvent(new ValueChange.Post<>(this, false, true));
	}

	/**
	 * Unchecks this {@link UICheckBox}. Does nothing if already unchecked.
	 */
	public void uncheck()
	{
		if (!checked || fireEvent(new ValueChange.Pre<>(this, true, false)))
			return;

		checked = false;
		fireEvent(new ValueChange.Post<>(this, true, false));
	}

	@Override
	public void click(MouseButton button)
	{
		if (isDisabled() || button != MouseButton.LEFT)
			return;
		toggle();
	}

	@Override
	public boolean keyTyped(char keyChar, int keyCode)
	{
		if (keyCode != Keyboard.KEY_SPACE)
			return false;
		toggle();
		return true;
	}

	@Override
	public String getPropertyString()
	{
		return (checked ? "checked " : "") + "[" + TextFormatting.GREEN + content + TextFormatting.RESET + "] " + super.getPropertyString();
	}

	public static UICheckBoxBuilder builder()
	{
		return new UICheckBoxBuilder();
	}

	public static class UICheckBoxBuilder extends UIContentHolderBuilder<UICheckBoxBuilder, UICheckBox>
			implements IValueChangeBuilder<UICheckBoxBuilder, UICheckBox, Boolean>
	{
		protected boolean check;
		protected boolean uncheck;

		protected UICheckBoxBuilder()
		{
			widthOfContent(15);
			heightOfContent(4);
			fob().color(0x444444)
				 .when(UICheckBox::isHovered)
				 .color(0x777777)
				 .when(UICheckBox::isDisabled)
				 .color(0xCCCCCC)
				 .base();
		}

		public UICheckBoxBuilder checked(boolean checked)
		{
			if (checked)
				return check();
			else
				return uncheck();
		}

		public UICheckBoxBuilder check()
		{
			check = true;
			uncheck = false;
			return this;
		}

		public UICheckBoxBuilder uncheck()
		{
			check = false;
			uncheck = true;
			return this;
		}

		@Override
		public UICheckBox build()
		{
			UICheckBox cb = build(new UICheckBox());
			if (check)
				cb.check();
			if (uncheck)
				cb.uncheck();
			return cb;
		}
	}
}

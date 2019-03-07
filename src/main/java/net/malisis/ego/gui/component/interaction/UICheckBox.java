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
import net.malisis.ego.gui.component.content.IContentHolder;
import net.malisis.ego.gui.element.position.Position;
import net.malisis.ego.gui.element.size.Size;
import net.malisis.ego.gui.event.ValueChange;
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
public class UICheckBox extends UIComponent implements IContentHolder, IValueChangeEventRegister<UICheckBox, Boolean>
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
		setBackground(GuiShape.builder(this).position(1, 0).size(12, 12).icon(GuiIcon.CHECKBOX_BG).build());

		//Foreground
		GuiShape overlay = GuiShape.builder(this).position(2, 1).size(10, 10).color(0xFFFFFF).alpha(80).build();
		GuiShape check = GuiShape.builder(this)
								 .position(1, 1)
								 .size(12, 10)
								 .zIndex(10)
								 .icon(GuiIcon.forComponent(this, GuiIcon.CHECKBOX, GuiIcon.CHECKBOX_HOVER, GuiIcon.CHECKBOX_DISABLED))
								 .build();

		setForeground(r -> {
			if (isHovered())
				overlay.render(r);
			if (isChecked())
				check.render(r);
			r.next();
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
	public void setContent(IContent content)
	{
		this.content = content;
		content.setParent(this);
		content.setPosition(Position.of(15, 2));
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
	 * Sets the state for this {@link UICheckBox}. Does not fire {@link ValueChange} event.
	 *
	 * @param checked true if checked
	 * @return this {@link UIComponent}
	 */
	public UICheckBox setChecked(boolean checked)
	{
		this.checked = checked;
		return this;
	}

	@Override
	public void click(MouseButton button)
	{
		if (isDisabled() || button != MouseButton.LEFT)
			return;
		if (fireEvent(new ValueChange.Pre<>(this, !checked, checked)))
			checked = !checked;
		fireEvent(new ValueChange.Post<>(this, checked, !checked));
	}

	@Override
	public boolean keyTyped(char keyChar, int keyCode)
	{
		if (keyCode != Keyboard.KEY_SPACE)
			return false;

		if (fireEvent(new ValueChange.Pre<>(this, !checked, checked)))
			checked = !checked;
		fireEvent(new ValueChange.Post<>(this, checked, !checked));
		return true;
	}

	@Override
	public String getPropertyString()
	{
		return (checked ? "checked " : "") + "[" + TextFormatting.GREEN + content + TextFormatting.RESET + "] " + super.getPropertyString();
	}
}

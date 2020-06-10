/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Ordinastie
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
import net.malisis.ego.gui.event.ValueChange;
import net.malisis.ego.gui.event.ValueChange.IValueChangeEventRegister;
import net.malisis.ego.gui.render.GuiIcon;
import net.malisis.ego.gui.render.shape.GuiShape;
import net.malisis.ego.gui.text.GuiText;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Ordinastie
 */
public class UIRadioButton extends UIComponent implements IContentHolder, IContentSetter, IValueChangeEventRegister<UIRadioButton, Boolean>
{
	//TODO:needs to be cleared at some point
	private final static HashMap<String, List<UIRadioButton>> radioButtons = new HashMap<>();

	protected final FontOptions fontOptions = FontOptions.builder()
														 .color(0x444444)
														 .when(this::isHovered)
														 .color(0x777777)
														 .when(this::isDisabled)
														 .color(0xCCCCCC)
														 .build();

	private IContent content;
	private String name;
	private boolean selected;

	public UIRadioButton(String name, String text)
	{
		this.name = name;
		setText(text);

		//Background
		setBackground(GuiShape.builder(this)
							  .position(1, 1)
							  .size(8, 8)
							  .icon(this, GuiIcon.RADIO_BG, GuiIcon.RADIO_BG_HOVER, GuiIcon.RADIO_BG_DISABLED)
							  .build());

		//Foreground
		GuiShape radio = GuiShape.builder(this)
								 .position(2, 2)
								 .size(6, 6)
								 .icon(this, GuiIcon.RADIO, GuiIcon.RADIO_HOVER, GuiIcon.RADIO_DISABLED)
								 .build();
		setForeground(r -> {
			if (isSelected())
				radio.render(r);
			if (content() != null)
				content().render(r);
		});

		addRadioButton(this);
	}

	public UIRadioButton(String name)
	{
		this(name, null);
	}

	//#region Getters/Setters

	/**
	 * Sets the content for this {@link UIRadioButton}.
	 *
	 * @param content the content
	 */
	public void setContent(IContent content)
	{
		this.content = content;
		//		content.setParent(this);
		//		content.setPosition(Position.of(12, 1));
	}

	public void setText(String text)
	{
		GuiText gt = GuiText.of(text, fontOptions);
		setContent(gt);
	}

	/**
	 * Gets the {@link UIComponent} used as content for this {@link UIRadioButton}.
	 *
	 * @return the content component
	 */
	@Override
	public IContent content()
	{
		return content;
	}

	/**
	 * Checks if this {@link UIRadioButton} is selected.
	 *
	 * @return true, if is selected
	 */
	public boolean isSelected()
	{
		return selected;
	}

	/**
	 * Sets state of this {@link UIRadioButton} to selected.<br>
	 * If a radio button with the same name is currently selected, unselects it.<br>
	 * Does not fire {@link ValueChange} event.
	 */
	public void setSelected()
	{
		UIRadioButton rb = getSelected(name);
		if (rb != null)
			rb.selected = false;
		selected = true;
	}

	public void select()
	{
		if (selected)
			return;
		UIRadioButton old = getSelected(name);
		if (fireEvent(new ValueChange.Pre<>(this, old, this)))
			return;
		setSelected();
		fireEvent(new ValueChange.Post<>(this, old, this));
	}

	//#end Getters/Setters
	@Override
	public void click(MouseButton button)
	{
		if (isDisabled() || button != MouseButton.LEFT)
			return;
		select();
	}

	@Override
	public boolean keyTyped(char keyChar, int keyCode)
	{
		if (keyCode != Keyboard.KEY_SPACE)
			return false;
		select();
		return true;
	}

	public static void addRadioButton(UIRadioButton rb)
	{
		List<UIRadioButton> listRb = radioButtons.get(rb.name);
		if (listRb == null)
			listRb = new ArrayList<>();
		listRb.add(rb);
		radioButtons.put(rb.name, listRb);
	}

	public static UIRadioButton getSelected(String name)
	{
		List<UIRadioButton> listRb = radioButtons.get(name);
		if (listRb == null)
			return null;
		for (UIRadioButton rb : listRb)
		{
			if (rb.selected)
				return rb;
		}
		return null;
	}

	public static UIRadioButton getSelected(UIRadioButton rb)
	{
		return getSelected(rb.name);
	}

	public static UIRadioButtonBuilder builder(String name)
	{
		return new UIRadioButtonBuilder().name(name);
	}

	public static class UIRadioButtonBuilder extends UIContentHolderBuilder<UIRadioButtonBuilder, UIRadioButton>
			implements IValueChangeEventRegister<UIRadioButton, Boolean>
	{

		protected boolean selected;

		protected UIRadioButtonBuilder()
		{
			widthOfContent(14);
			heightOfContent();

			fontOptionsBuilder.color(0x444444)
							  .when(UIRadioButton::isHovered)
							  .color(0x777777)
							  .when(UIRadioButton::isDisabled)
							  .color(0xCCCCCC)
							  .base();
		}

		public UIRadioButton.UIRadioButtonBuilder select()
		{
			selected = true;
			return this;
		}

		@Override
		public UIRadioButton build()
		{
			UIRadioButton rb = build(new UIRadioButton(name));
			if (selected)
				rb.select();
			return rb;
		}
	}
}

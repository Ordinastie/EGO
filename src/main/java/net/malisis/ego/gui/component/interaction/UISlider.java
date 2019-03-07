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

import static com.google.common.base.Preconditions.*;
import static net.malisis.ego.gui.element.position.Positions.middleAligned;

import com.google.common.base.Converter;
import net.malisis.ego.font.FontOptions;
import net.malisis.ego.gui.component.MouseButton;
import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.component.content.IContentHolder;
import net.malisis.ego.gui.element.size.Size;
import net.malisis.ego.gui.event.ValueChange;
import net.malisis.ego.gui.event.ValueChange.IValueChangeEventRegister;
import net.malisis.ego.gui.render.GuiIcon;
import net.malisis.ego.gui.render.shape.GuiShape;
import net.malisis.ego.gui.text.GuiText;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

/**
 * @author Ordinastie
 */
public class UISlider<T> extends UIComponent implements IContentHolder, IValueChangeEventRegister<UISlider<T>, T>
{
	private static int SLIDER_WIDTH = 8;

	/** Text to display over the slider. */
	protected final GuiText text;

	/** Current value. */
	protected T value;
	/** Position offset of the slider. */
	protected float offset;

	/** Amount of offset scrolled by when using the scroll wheel. */
	protected float scrollStep = 0.05F;

	/** Converter from float (0-1 offset) to the value. */
	protected Converter<Float, T> converter;

	public UISlider(int width, Converter<Float, T> converter, String text)
	{
		this.converter = checkNotNull(converter);
		value = converter.convert(0F);

		this.text = GuiText.builder()
						   .parent(this)
						   .text(text)
						   .position(this::textPosition, o -> middleAligned(o, 0))
						   .bind("value", this::getValue)
						   .zIndex(this::getZIndex)
						   .fontOptions(FontOptions.builder().color(0xFFFFFF).shadow().when(this::isHovered).color(0xFFFFA0).build())
						   .build();

		setSize(Size.of(width, 20));

		GuiShape sliderShape = GuiShape.builder(this)
									   .position(this::scrollPosition, 0)
									   .size(Size.of(SLIDER_WIDTH, () -> size().height()))
									   .icon(GuiIcon.SLIDER)
									   .border(5)
									   .build();
		setBackground(GuiShape.builder(this).icon(GuiIcon.SLIDER_BG).build());
		setForeground(this.text.and(sliderShape));
	}

	//#region Getters/Setters
	@Override
	public GuiText content()
	{
		return text;
	}

	/**
	 * Sets the value for this {@link UISlider}.
	 *
	 * @param value the value
	 * @return this UI slider
	 */
	public UISlider<T> setValue(T value)
	{
		if (this.value == value)
			return this;
		T old = this.value;
		if (!fireEvent(new ValueChange.Pre<>(this, old, value)))
			return this;

		this.value = value;
		offset = MathHelper.clamp(converter.reverse().convert(value), 0, 1);

		fireEvent(new ValueChange.Post<>(this, old, value));
		return this;
	}

	/**
	 * Gets the value for this {@link UISlider}.
	 *
	 * @return the value
	 */
	public T getValue()
	{
		return value;
	}

	public void setFontOptions(FontOptions fontOptions)
	{
		text.setFontOptions(fontOptions);
	}

	public int scrollPosition()
	{
		return (int) (offset * (size().width() - SLIDER_WIDTH));
	}

	public int textPosition()
	{
		int w = size().width(); //width
		int tw = text.size().width(); //text width
		int tx = (w - tw) / 2; //text x
		int sx = scrollPosition(); //scroll x

		if (sx > w / 2)
		{
			if (tx + tw + 2 > sx)
				return sx - tw - 2;
		}
		else
		{
			if (sx + SLIDER_WIDTH + 2 > tx)
				return sx + SLIDER_WIDTH + 2;
		}
		return tx;
	}

	/**
	 * Sets the amount of offset to scroll with the wheel.
	 *
	 * @param scrollStep the scroll step
	 */
	public void setScrollStep(float scrollStep)
	{
		this.scrollStep = scrollStep;
	}

	//#end Getters/Setters
	@Override
	public void click(MouseButton button)
	{
		slideTo();
	}

	@Override
	public void scrollWheel(int delta)
	{
		slideTo(offset + delta * scrollStep);
	}

	@Override
	public void mouseDrag(MouseButton button)
	{
		slideTo();
	}

	/**
	 * Slides the slider to the specified pixel position.<br>
	 */
	public void slideTo()
	{
		int l = size().width() - SLIDER_WIDTH;
		int pos = MathHelper.clamp(mousePosition().x() - SLIDER_WIDTH / 2, 0, l);
		slideTo((float) pos / l);
	}

	/**
	 * Slides the slider to the specified offset between 0 and 1.<br>
	 * Sets the value relative to the offset.
	 *
	 * @param offset the offset
	 */
	public void slideTo(float offset)
	{
		if (!isEnabled())
			return;

		setValue(converter.convert(MathHelper.clamp(offset, 0, 1)));
	}

	@Override
	public String getPropertyString()
	{
		return "[" + TextFormatting.GREEN + text + " | " + text.position() + "@" + text.size() + TextFormatting.RESET + "] "
				+ super.getPropertyString();
	}
}

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

import com.google.common.base.Converter;
import net.malisis.ego.font.FontOptions;
import net.malisis.ego.gui.component.MouseButton;
import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.component.content.IContent.IContentHolder;
import net.malisis.ego.gui.element.size.Size;
import net.malisis.ego.gui.event.ValueChange;
import net.malisis.ego.gui.event.ValueChange.IValueChangeBuilder;
import net.malisis.ego.gui.event.ValueChange.IValueChangeEventRegister;
import net.malisis.ego.gui.render.GuiIcon;
import net.malisis.ego.gui.render.shape.GuiShape;
import net.malisis.ego.gui.text.GuiText;
import net.malisis.ego.gui.text.UITextComponentBuilder;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

import java.util.function.Predicate;

/**
 * @author Ordinastie
 */
public class UISlider<T> extends UIComponent implements IContentHolder, IValueChangeEventRegister<UISlider<T>, T>
{
	private static final int SLIDER_WIDTH = 8;

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

	protected UISlider(UISliderBuilder<T> builder, Converter<Float, T> converter)
	{
		this.converter = checkNotNull(converter);
		builder.tb()
			   .x(this::textPosition);
		text = builder.buildText(this);
		value = converter.convert(0F);

		GuiShape sliderShape = GuiShape.builder(this)
									   .x(this::scrollPosition)
									   .size(Size.of(SLIDER_WIDTH, () -> size().height()))
									   .icon(GuiIcon.SLIDER)
									   .border(5)
									   .build();

		setBackground(GuiShape.builder(this)
							  .icon(GuiIcon.SLIDER_BG)
							  .build());
		setForeground(sliderShape.and(text));
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
	@SuppressWarnings("ConstantConditions")
	public UISlider<T> setValue(T value)
	{
		if (this.value == value)
			return this;
		T old = this.value;
		if (fireEvent(new ValueChange.Pre<>(this, old, value)))
			return this;

		this.value = value;
		offset = MathHelper.clamp(converter.reverse()
										   .convert(value), 0, 1);

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
		int tw = text.size()
					 .width(); //text width
		int tx = (w - tw) / 2; //text x
		int sx = scrollPosition(); //scroll x

		if (sx > w / 2)
		{
			int x = sx - tw - 2;
			if (tx + tw + 2 > sx && x > 0)
				return x;
		}
		else
		{
			int x = sx + SLIDER_WIDTH + 2;
			if (sx + SLIDER_WIDTH + 2 > tx && x < w - tw)
				return x;
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

	public static <T> UISliderBuilder<T> builder(Converter<Float, T> converter)
	{
		return new UISliderBuilder<>(converter);
	}

	public static <E extends Enum<E>> UISliderBuilder<E> builder(Class<E> clazz)
	{
		E[] values = clazz.getEnumConstants();
		Converter<Float, E> converter = Converter.from(f -> values[Math.round(f == null ? 0 : f * (values.length - 1))],
													   e -> e == null ? 0 : (float) e.ordinal() / (values.length - 1));

		return new UISliderBuilder<>(converter).scrollStep(1f / (values.length - 1));
	}

	public static UISliderBuilder<Integer> builder(int min, int max)
	{
		Converter<Float, Integer> converter = Converter.from(f -> f == null ? min : (int) (min + f * (max - min)),
															 i -> i == null ? 0 : (float) i / (max - min));

		return new UISliderBuilder<>(converter).scrollStep(1f / (max - min));
	}

	public static class UISliderBuilder<T> extends UITextComponentBuilder<UISliderBuilder<T>, UISlider<T>>
			implements IValueChangeBuilder<UISliderBuilder<T>, UISlider<T>, T>
	{
		protected Converter<Float, T> converter;
		protected float scrollStep = 0.05F;
		protected T defaultValue = null;

		protected UISliderBuilder(Converter<Float, T> converter)
		{
			this.converter = converter;
			tb().text("{value}")
				.middleAligned();
			bind("value", s -> s::getValue);
			fob().color(0xFFFFFF)
				 .shadow()
				 .when((Predicate<UISlider<T>>) UISlider::isHovered)
				 .color(0xFFFFA0)
				 .base();
		}

		@Override
		public UISliderBuilder<T> self()
		{
			return this;
		}

		public UISliderBuilder<T> width(int width)
		{
			return size(width, 20);
		}

		public UISliderBuilder<T> scrollStep(float scrollStep)
		{
			checkArgument(scrollStep > 0 && scrollStep < 1);
			this.scrollStep = scrollStep;
			return this;
		}

		public UISliderBuilder<T> steps(int step)
		{
			checkArgument(step > 0);
			scrollStep = 1F / step;
			return this;
		}

		public UISliderBuilder<T> value(T value)
		{
			defaultValue = value;
			return this;
		}

		@Override
		public UISlider<T> build()
		{
			UISlider<T> slider = build(new UISlider<>(this, converter));
			slider.setScrollStep(scrollStep);
			if (defaultValue != null)
				slider.setValue(defaultValue);

			return slider;
		}
	}
}

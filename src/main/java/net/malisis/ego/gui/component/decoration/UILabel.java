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

package net.malisis.ego.gui.component.decoration;

import net.malisis.ego.font.FontOptions;
import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.component.control.IScrollable;
import net.malisis.ego.gui.component.scrolling.UIScrollBar;
import net.malisis.ego.gui.element.IClipable;
import net.malisis.ego.gui.element.position.Position.IPosition;
import net.malisis.ego.gui.element.size.Size;
import net.malisis.ego.gui.element.size.Size.ISize;
import net.malisis.ego.gui.text.GuiText;
import net.malisis.ego.gui.text.UITextComponentBuilder;
import net.minecraft.util.text.TextFormatting;

import java.util.function.Function;

import javax.annotation.Nonnull;

/**
 * UILabel.
 *
 * @author Ordinastie
 */
public class UILabel extends UIComponent implements IScrollable, IClipable
{
	protected final GuiText text;
	protected final IPosition offset = UIScrollBar.scrollingOffset(this);

	/**
	 * Instantiates a new {@link UILabel}.
	 */
	protected UILabel(UILabelBuilder builder)
	{
		text = builder.buildText(this);
		setForeground(text);
	}

	// #region getters/setters
	@Override
	public GuiText content()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text.setText(text);
	}

	public String getText()
	{
		return text.getRawText();
	}

	public void setFontOptions(FontOptions fontOptions)
	{
		text.setFontOptions(fontOptions);
	}

	@Override
	public IPosition contentPosition()
	{
		return text.position();
	}

	@Override
	public ISize contentSize()
	{
		return text.size();
	}

	@Override
	public void setSize(@Nonnull ISize size)
	{
		super.setSize(size);
		text.setWrapSize(innerSize().width());
	}

	@Override
	@Nonnull
	public ISize size()
	{
		return size;
	}

	@Override
	public IPosition offset()
	{
		return offset;
	}
	// #end getters/setters

	@Override
	public ClipArea getClipArea()
	{
		return ClipArea.from(this);
	}

	@Override
	public String getPropertyString()
	{
		return "[" + TextFormatting.DARK_AQUA + text + TextFormatting.RESET + "] " + super.getPropertyString();
	}

	public static UILabelBuilder builder()
	{
		return new UILabelBuilder();
	}

	public static class UILabelBuilder extends UITextComponentBuilder<UILabelBuilder, UILabel>
	{
		private boolean autoSize = true;

		protected UILabelBuilder()
		{
			//by default, label size spans to fit the text
			guiTextBuilder.wrapSize(0);
			size(Size::sizeOfContent);
		}

		@Override
		public UILabelBuilder size(Function<UILabel, ISize> func)
		{
			super.size(func);
			autoSize = false;
			return this;
		}

		@Override
		public UILabel build()
		{
			UILabel label = build(new UILabel(this));
			//autosize means label size matches text size
			if (!autoSize)
				wrapSize(label.innerSize()::width); //label has custom size, wrap should match it
			return label;
		}

	}

}

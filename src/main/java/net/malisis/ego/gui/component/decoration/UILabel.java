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

import static com.google.common.base.Preconditions.*;

import net.malisis.ego.font.FontOptions;
import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.component.control.IScrollable;
import net.malisis.ego.gui.component.scrolling.UIScrollBar;
import net.malisis.ego.gui.element.IClipable;
import net.malisis.ego.gui.element.position.Position.IPosition;
import net.malisis.ego.gui.element.size.Size;
import net.malisis.ego.gui.element.size.Size.ISize;
import net.malisis.ego.gui.element.size.Sizes;
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
	protected GuiText text;
	protected final IPosition offset = UIScrollBar.scrollingOffset(this);

	/**
	 * Instantiates a new {@link UILabel}.
	 */
	protected UILabel()
	{

	}

	// #region getters/setters
	public void setGuiText(GuiText guiText)
	{
		this.text = checkNotNull(guiText);
		setForeground(text);
	}

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
		return text.getBase();
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
		//text.setWrapSize(innerSize().width());
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

	public static class UILabelBuilder extends UILabelBuilderG<UILabelBuilder, UILabel>
	{
		@Override
		public UILabel build()
		{
			return build(new UILabel());
		}
	}

	public abstract static class UILabelBuilderG<BUILDER extends UILabelBuilderG<?, ?>, COMPONENT extends UILabel>
			extends UITextComponentBuilder<BUILDER, COMPONENT>
	{
		protected UILabelBuilderG()
		{
			//by default, label size spans to fit the text
			guiTextBuilder.wrapSize(0);
			width = l -> Sizes.widthOfContent(l, 0);
			height = l -> Sizes.heightOfContent(l, 0);
		}

		public BUILDER contentSize()
		{
			super.size(Size::sizeOfContent);
			wrapSize(0);
			wrapSize = null;
			return self();
		}

		@Override
		public BUILDER size(Function<COMPONENT, ISize> func)
		{
			super.size(func);
			//automatically wrap to the resulting size of the UILabel
			wrapSize(l -> l.innerSize()::width);
			return self();
		}

		@Override
		public COMPONENT build(COMPONENT label)
		{
			label.setGuiText(buildText(label));
			super.build(label);
			return label;
		}
	}

}

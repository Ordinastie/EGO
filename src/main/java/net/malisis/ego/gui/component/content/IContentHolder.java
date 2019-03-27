/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Ordinastie
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

package net.malisis.ego.gui.component.content;

import static com.google.common.base.Preconditions.*;

import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.component.UIComponentBuilder;
import net.malisis.ego.gui.component.decoration.UIImage;
import net.malisis.ego.gui.element.position.Position;
import net.malisis.ego.gui.element.position.Position.IPosition;
import net.malisis.ego.gui.element.size.Size;
import net.malisis.ego.gui.element.size.Size.ISize;
import net.malisis.ego.gui.render.GuiIcon;
import net.malisis.ego.gui.render.GuiTexture;
import net.malisis.ego.gui.text.UITextComponentBuilder;

import java.util.function.Function;

/**
 * @author Ordinastie
 */
public interface IContentHolder
{
	public IContent content();

	public default IPosition contentPosition()
	{
		return content() != null ? content().position() : Position.ZERO;
	}

	public default ISize contentSize()
	{
		return content() != null ? content().size() : Size.ZERO;
	}

	public interface IContentSetter
	{
		public void setContent(IContent content);
	}

	public abstract class UIContentHolderBuilder<BUILDER extends UIComponentBuilder<?, ?>, COMPONENT extends UIComponent & IContentSetter>
			extends UITextComponentBuilder<BUILDER, COMPONENT>
	{

		protected Function<COMPONENT, IContent> content = this::buildText;

		public BUILDER content(Function<COMPONENT, IContent> content)
		{
			this.content = checkNotNull(content);
			return self();
		}

		public BUILDER image(UIImage image)
		{
			return content(image);
		}

		public BUILDER content(IContent content)
		{
			return content(b -> content);
		}

		public BUILDER icon(GuiIcon icon)
		{
			return content(UIImage.builder()
								  .icon(icon)
								  .build());
		}

		public BUILDER texture(GuiTexture texture)
		{
			return content(UIImage.builder()
								  .icon(new GuiIcon(texture))
								  .build());
		}

		@Override
		protected COMPONENT build(COMPONENT component)
		{
			super.build(component);

			if (content != null)
				component.setContent(content.apply(component));

			return component;
		}
	}
}

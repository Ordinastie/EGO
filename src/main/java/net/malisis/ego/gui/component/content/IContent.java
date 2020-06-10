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
import net.malisis.ego.gui.element.IChild;
import net.malisis.ego.gui.element.position.Position;
import net.malisis.ego.gui.element.position.Position.IPosition;
import net.malisis.ego.gui.element.position.Position.IPositioned;
import net.malisis.ego.gui.element.size.Size;
import net.malisis.ego.gui.element.size.Size.ISize;
import net.malisis.ego.gui.element.size.Size.ISized;
import net.malisis.ego.gui.render.GuiIcon;
import net.malisis.ego.gui.render.GuiTexture;
import net.malisis.ego.gui.render.IGuiRenderer;
import net.malisis.ego.gui.render.shape.GuiShape;
import net.malisis.ego.gui.text.UITextComponentBuilder;

import java.util.function.Function;

/**
 * @author Ordinastie
 */
public interface IContent extends IPositioned, ISized, IGuiRenderer, IChild
{
	interface IContentHolder
	{
		IContent content();

		default IPosition contentPosition()
		{
			return content() != null ? content().position() : Position.ZERO;
		}

		default ISize contentSize()
		{
			return content() != null ? content().size() : Size.ZERO;
		}
	}

	interface IContentSetter extends IContentHolder
	{
		void setContent(IContent content);
	}

	abstract class UIContentHolderBuilder<BUILDER extends UIComponentBuilder<?, ?>, COMPONENT extends UIComponent & IContentSetter>
			extends UITextComponentBuilder<BUILDER, COMPONENT>
	{
		protected Function<COMPONENT, IContent> content = this::buildText;

		public UIContentHolderBuilder()
		{

		}

		public BUILDER content(Function<COMPONENT, IContent> content)
		{
			this.content = checkNotNull(content);
			return self();
		}

		public BUILDER content(IContent content)
		{
			return content(c -> content);
		}

		public BUILDER icon(GuiIcon icon)
		{
			return content(c -> GuiShape.builder(c)
										.middleCenter()
										.icon(icon)
										.iconSize()
										.fixed(false)
										.build());
		}

		public BUILDER texture(GuiTexture texture)
		{
			return icon(GuiIcon.full(texture));
		}

		@Override
		protected COMPONENT build(COMPONENT component)
		{
			//set content first so size is correct when added to container
			if (content != null)
				component.setContent(content.apply(component));

			super.build(component);

			return component;
		}
	}
}

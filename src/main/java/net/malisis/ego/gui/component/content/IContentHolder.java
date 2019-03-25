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

import net.malisis.ego.gui.component.decoration.UIImage;
import net.malisis.ego.gui.component.interaction.UIButton;
import net.malisis.ego.gui.element.position.Position;
import net.malisis.ego.gui.element.position.Position.IPosition;
import net.malisis.ego.gui.element.size.Size;
import net.malisis.ego.gui.element.size.Size.ISize;
import net.malisis.ego.gui.render.GuiIcon;
import net.malisis.ego.gui.render.GuiTexture;
import net.malisis.ego.gui.text.ITextBuilder;

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

	public static interface IContentBuilder<BUILDER> extends ITextBuilder<BUILDER>
	{
		public BUILDER content(Function<UIButton, IContent> content);

		public default BUILDER image(UIImage image)
		{
			return content(image);
		}

		public default BUILDER content(IContent content)
		{
			return content(b -> content);
		}

		public default BUILDER icon(GuiIcon icon)
		{
			return content(UIImage.builder().icon(icon).build());
		}

		public default BUILDER texture(GuiTexture texture)
		{
			return content(UIImage.builder().icon(new GuiIcon(texture)).build());
		}
	}
}

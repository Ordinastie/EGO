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

package net.malisis.ego.gui.element.size;

import net.malisis.ego.gui.component.UIComponentBuilder;
import net.malisis.ego.gui.component.content.IContent.IContentHolder;
import net.malisis.ego.gui.element.IChild;
import net.malisis.ego.gui.element.position.Position.IPositioned;
import net.malisis.ego.gui.element.size.Size.ISize;
import net.malisis.ego.gui.element.size.Size.ISized;
import net.malisis.ego.gui.render.shape.GuiShape;

import java.util.function.Function;
import java.util.function.IntSupplier;

/**
 * Interface helper for builders generating objects implementing {@link ISized}.<br>
 * <br>
 * Typical implementation :
 * <pre>{@code
 * protected Function<COMPONENT, IntSupplier> width = o -> Sizes.parentWidth(o, 1F, 0);
 * protected Function<COMPONENT, IntSupplier> height = o -> Sizes.parentHeight(o, 1F, 0);
 * protected Function<COMPONENT, ISize> size = o -> Size.of(width.apply(o), height.apply(o));
 *
 * public BUILDER size(Function<COMPONENT, ISize> func)
 * {
 * 		size = checkNotNull(func);
 * 		return self();
 * }
 *
 * public BUILDER width(Function<COMPONENT, IntSupplier> width)
 * {
 * 		this.width = checkNotNull(width);
 * 		return self();
 * }
 *
 * public BUILDER height(Function<COMPONENT, IntSupplier> height)
 * {
 * 		this.height = checkNotNull(height);
 * 		return self();
 * }
 * }
 * </pre>
 *
 * @param <BUILDER> type of Builder implementing this interface.
 * @param <OWNER> type of object generated by the builder.
 * @author Ordinastie
 * @see GuiShape.Builder
 * @see UIComponentBuilder
 */
public interface ISizeBuilder<BUILDER, OWNER extends IPositioned & ISized & IChild>
{
	BUILDER size(Function<OWNER, ISize> func);

	BUILDER width(Function<OWNER, IntSupplier> func);

	BUILDER height(Function<OWNER, IntSupplier> func);

	default BUILDER size(ISize size)
	{
		return size(o -> size);
	}

	default BUILDER size(int width, int height)
	{
		return size(o -> Size.of(width, height));
	}

	default BUILDER width(int width)
	{
		return width(o -> () -> width);
	}

	default BUILDER height(int height)
	{
		return height(o -> () -> height);
	}

	default BUILDER width(IntSupplier func)
	{
		return width(o -> func);
	}

	default BUILDER height(IntSupplier func)
	{
		return height(o -> func);
	}

	default BUILDER parentWidth()
	{
		return width(o -> Sizes.parentWidth(o, 1F, 0));
	}

	default BUILDER parentWidth(float width)
	{
		return width(o -> Sizes.parentWidth(o, width, 0));
	}

	default BUILDER parentWidth(int offset)
	{
		return width(o -> Sizes.parentWidth(o, 1F, offset));
	}

	default BUILDER parentWidth(float width, int offset)
	{
		return width(o -> Sizes.parentWidth(o, width, offset));
	}

	default BUILDER parentHeight()
	{
		return height(o -> Sizes.parentHeight(o, 1F, 0));
	}

	default BUILDER parentHeight(float height)
	{
		return height(o -> Sizes.parentHeight(o, height, 0));
	}

	default BUILDER parentHeight(int offset)
	{
		return height(o -> Sizes.parentHeight(o, 1F, offset));
	}

	default BUILDER parentHeight(float height, int offset)
	{
		return height(o -> Sizes.parentHeight(o, height, offset));
	}

	default BUILDER parentSize()
	{
		return parentSize(1F, 0);
	}

	default BUILDER parentSize(float size)
	{
		return parentSize(size, 0);
	}

	default BUILDER parentSize(int offset)
	{
		return parentSize(1F, offset);
	}

	default BUILDER parentSize(float size, int offset)
	{
		parentWidth(size, offset);
		return parentHeight(size, offset);
	}

	default BUILDER fillWidth()
	{
		return width(o -> Sizes.fillWidth(o, 0));
	}

	default BUILDER fillWidth(int spacing)
	{
		return width(o -> Sizes.fillWidth(o, spacing));
	}

	default BUILDER fillHeight()
	{
		return height(o -> Sizes.fillHeight(o, 0));
	}

	default BUILDER fillHeight(int spacing)
	{
		return height(o -> Sizes.fillHeight(o, spacing));
	}

	default BUILDER widthRelativeTo(ISized other)
	{
		return width(o -> Sizes.widthRelativeTo(other, 1, 0));
	}

	default BUILDER widthRelativeTo(ISized other, float width)
	{
		return width(o -> Sizes.widthRelativeTo(other, width, 0));
	}

	default BUILDER widthRelativeTo(ISized other, float width, int offset)
	{
		return width(o -> Sizes.widthRelativeTo(other, width, offset));
	}

	default BUILDER heightRelativeTo(ISized other)
	{
		return height(o -> Sizes.heightRelativeTo(other, 1, 0));
	}

	default BUILDER heightRelativeTo(ISized other, float height)
	{
		return height(o -> Sizes.heightRelativeTo(other, height, 0));
	}

	default BUILDER heightRelativeTo(ISized other, float height, int offset)
	{
		return height(o -> Sizes.heightRelativeTo(other, height, offset));
	}

	default BUILDER widthOfContent()
	{
		return width(o -> Sizes.widthOfContent((IContentHolder) o, 0));
	}

	default BUILDER widthOfContent(int offset)
	{
		return width(o -> Sizes.widthOfContent((IContentHolder) o, offset));
	}

	default BUILDER heightOfContent()
	{
		return height(o -> Sizes.heightOfContent((IContentHolder) o, 0));
	}

	default BUILDER heightOfContent(int offset)
	{
		return height(o -> Sizes.heightOfContent((IContentHolder) o, offset));
	}

	default BUILDER sizeOfContent()
	{
		return sizeOfContent(0, 0);
	}

	default BUILDER sizeOfContent(int offset)
	{
		return sizeOfContent(offset, offset);
	}

	default BUILDER sizeOfContent(int xOffset, int yOffset)
	{
		widthOfContent(xOffset);
		return heightOfContent(yOffset);
	}

}

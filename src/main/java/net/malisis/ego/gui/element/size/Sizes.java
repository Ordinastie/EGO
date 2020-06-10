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

import static com.google.common.base.Preconditions.*;

import net.malisis.ego.gui.component.content.IContent.IContentHolder;
import net.malisis.ego.gui.component.scrolling.UIScrollBar;
import net.malisis.ego.gui.element.IChild;
import net.malisis.ego.gui.element.Margin;
import net.malisis.ego.gui.element.Padding;
import net.malisis.ego.gui.element.position.Position.IPositioned;
import net.malisis.ego.gui.element.size.Size.ISized;

import java.util.function.IntSupplier;

/**
 * @author Ordinastie
 */
public class Sizes
{
	public static IntSupplier innerWidth(ISized owner)
	{
		checkNotNull(owner);
		return () -> owner.size()
						  .width() - Padding.of(owner)
											.horizontal() - UIScrollBar.scrollbarWidth(owner);
	}

	public static IntSupplier innerHeight(ISized owner)
	{
		checkNotNull(owner);
		return () -> owner.size()
						  .height() - Padding.of(owner)
											 .vertical() - UIScrollBar.scrollbarHeight(owner);
	}

	public static <T extends IChild> IntSupplier parentWidth(T owner, float width, int offset)
	{
		checkNotNull(owner);
		return () -> (int) ((Size.innerWidthOf(owner.getParent()) - Margin.horizontalOf(owner)) * width + offset);
	}

	public static <T extends IChild> IntSupplier parentHeight(T owner, float height, int offset)
	{
		checkNotNull(owner);
		return () -> (int) ((Size.innerHeightOf(owner.getParent()) - Margin.verticalOf(owner)) * height + offset);
	}

	public static <T extends IPositioned & IChild> IntSupplier fillWidth(T owner, int spacing)
	{
		checkNotNull(owner);
		//remove left padding as it's counted both in parent.innerSize and owner.position
		return () -> Size.innerWidthOf(owner.getParent()) - owner.x() - spacing + Padding.leftOf(owner.getParent());
	}

	public static <T extends IPositioned & IChild> IntSupplier fillHeight(T owner, int spacing)
	{
		checkNotNull(owner);
		//remove top padding as it's counted both in parent.innerSize and owner.position
		return () -> Size.innerHeightOf(owner.getParent()) - owner.y() - spacing + Padding.topOf(owner.getParent());
	}

	public static IntSupplier widthRelativeTo(ISized other, float width, int offset)
	{
		checkNotNull(other);
		return () -> (int) (other.width() * width + offset);
	}

	public static IntSupplier heightRelativeTo(ISized other, float height, int offset)
	{
		checkNotNull(other);
		return () -> (int) (other.height() * height + offset);
	}

	public static IntSupplier widthOfContent(IContentHolder owner, int offset)
	{
		checkNotNull(owner);
		return () -> owner.contentSize()
						  .width() + Padding.horizontalOf(owner) + offset;
	}

	public static IntSupplier heightOfContent(IContentHolder owner, int offset)
	{
		checkNotNull(owner);
		return () -> owner.contentSize()
						  .height() + Padding.verticalOf(owner) + offset;
	}
}

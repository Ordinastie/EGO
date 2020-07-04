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

import net.malisis.ego.gui.EGOGui;
import net.malisis.ego.gui.component.content.IContent.IContentHolder;
import net.malisis.ego.gui.element.IChild;
import net.malisis.ego.gui.element.position.Position.IPositioned;

import java.util.function.IntSupplier;

import javax.annotation.Nonnull;

/**
 * @author Ordinastie
 */
public class Size
{
	public static boolean CACHED = true;
	public static final ISize ZERO = Size.of(0, 0);
	public static final ISize DEFAULT = Size.of(16, 16);

	public interface ISized
	{
		@Nonnull
		default ISize size()
		{
			return Size.DEFAULT;
		}

		default ISize innerSize()
		{
			return size();
		}

		/**
		 * @return size().width()
		 */
		default int width()
		{
			return size().width();
		}

		/**
		 * @return size().height()
		 */
		default int height()
		{
			return size().height();
		}
	}

	public interface ISize
	{
		int width();

		int height();

		default ISize offset(int w, int h)
		{
			return plus(Size.of(w, h));
		}

		default ISize plus(ISize other)
		{
			if (other == null || other == Size.ZERO)
				return this;
			if (this == Size.ZERO)
				return other;

			return Size.of(() -> width() + other.width(), () -> height() + other.height());
		}

		default ISize minus(ISize other)
		{
			if (other == null || other == Size.ZERO)
				return this;
			if (this == Size.ZERO)
				return other;

			return Size.of(() -> width() - other.width(), () -> height() - other.height());
		}
	}

	public static class FixedSize implements ISize
	{
		protected int width;
		protected int height;

		public FixedSize(int width, int height)
		{
			this.width = width;
			this.height = height;
		}

		@Override
		public int width()
		{
			return width;
		}

		@Override
		public int height()
		{
			return height;
		}

		@Override
		public String toString()
		{
			return width + "," + height;
		}
	}

	public static class DynamicSize extends FixedSize
	{
		protected final IntSupplier widthFunction;
		protected final IntSupplier heightFunction;
		protected boolean locked = false; //prevents infinite recursivity

		DynamicSize(int width, int height, IntSupplier widthFunction, IntSupplier heightFunction)
		{
			super(width, height);
			this.widthFunction = widthFunction;
			this.heightFunction = heightFunction;
		}

		protected int updateWidth()
		{
			return widthFunction.getAsInt();
		}

		protected int updateHeight()
		{
			return heightFunction.getAsInt();
		}

		@Override
		public int width()
		{
			if (widthFunction != null && !locked)
			{
				locked = true;
				width = updateWidth();
				locked = false;
			}
			return width;
		}

		@Override
		public int height()
		{
			if (heightFunction != null && !locked)
			{
				locked = true;
				height = updateHeight();
				locked = false;
			}
			return height;
		}

		public ISize cached()
		{
			return new CachedSize(width, height, widthFunction, heightFunction);
		}
	}

	public static class CachedSize extends DynamicSize
	{
		protected int counterWidth = -1;
		protected int counterHeight = -1;

		public CachedSize(int width, int height, IntSupplier widthFunction, IntSupplier heightFunction)
		{
			super(width, height, widthFunction, heightFunction);
		}

		@Override
		public int width()
		{
			locked = Size.CACHED && !EGOGui.needsUpdate(counterWidth);
			counterWidth = EGOGui.counter;
			return super.width();
		}

		@Override
		public int height()
		{
			locked = Size.CACHED && !EGOGui.needsUpdate(counterHeight);
			counterHeight = EGOGui.counter;
			return super.height();
		}
	}

	public static ISize of(Object obj)
	{
		return obj instanceof ISized ? ((ISized) obj).size() : ZERO;
	}

	public static ISize innerOf(Object obj)
	{
		return obj instanceof ISized ? ((ISized) obj).innerSize() : ZERO;
	}

	public static int widthOf(Object obj)
	{
		return of(obj).width();
	}

	public static int heightOf(Object obj)
	{
		return of(obj).height();
	}

	public static int innerWidthOf(Object obj)
	{
		return innerOf(obj).width();
	}

	public static int innerHeightOf(Object obj)
	{
		return innerOf(obj).height();
	}

	//Size shortcuts
	public static ISize of(int width, int height)
	{
		return new FixedSize(width, height);
	}

	public static ISize of(int width, IntSupplier heightSupplier)
	{
		return new CachedSize(width, 0, null, heightSupplier);
	}

	public static ISize of(IntSupplier widthSupplier, int height)
	{
		return new CachedSize(0, height, widthSupplier, null);
	}

	public static ISize of(IntSupplier widthSupplier, IntSupplier heightSupplier)
	{
		return new CachedSize(0, 0, widthSupplier, heightSupplier);
	}

	public static <T extends ISized & IChild> ISize relativeTo(T other)
	{
		return new CachedSize(0, 0, Sizes.widthRelativeTo(other, 1.0F, 0), Sizes.heightRelativeTo(other, 1.0F, 0));
	}

	public static <T extends ISized & IChild> ISize inherited(T owner)
	{
		return new CachedSize(0, 0, Sizes.parentWidth(owner, 1.0F, 0), Sizes.parentHeight(owner, 1.0F, 0));
	}

	public static ISize sizeOfContent(IContentHolder owner)
	{
		return sizeOfContent(owner, 0, 0);
	}

	public static ISize sizeOfContent(IContentHolder owner, int widthOffset, int heightOffset)
	{
		return new CachedSize(0, 0, Sizes.widthOfContent(owner, widthOffset), Sizes.heightOfContent(owner, heightOffset));
	}

	public static <T extends IPositioned & IChild> ISize fill(T owner)
	{
		return fill(owner, 0, 0);
	}

	public static <T extends IPositioned & IChild> ISize fill(T owner, int rightOffset, int bottomOffset)
	{
		return new CachedSize(0, 0, Sizes.fillWidth(owner, rightOffset), Sizes.fillHeight(owner, bottomOffset));
	}

}

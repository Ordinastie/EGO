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

package net.malisis.ego.gui.element;

/**
 * @author Ordinastie
 */
public interface Padding
{
	Padding NO_PADDING = new FixedPadding(0, 0, 0, 0);

	/**
	 * Left padding.
	 *
	 * @return the left padding
	 */
	int left();

	/**
	 * Right padding.
	 *
	 * @return the right padding
	 */
	int right();

	/**
	 * Top padding.
	 *
	 * @return the top padding.
	 */
	int top();

	/**
	 * Bottom padding.
	 *
	 * @return the bottom padding.
	 */
	int bottom();

	/**
	 * Horizontal padding.
	 *
	 * @return the horizontal padding
	 */
	default int horizontal()
	{
		return left() + right();
	}

	/**
	 * Vertical padding
	 *
	 * @return the vertical padding
	 */
	default int vertical()
	{
		return top() + bottom();
	}

	class FixedPadding implements Padding
	{
		protected final int top, bottom, left, right;

		protected FixedPadding(int top, int bottom, int left, int right)
		{
			this.top = top;
			this.bottom = bottom;
			this.left = left;
			this.right = right;
		}

		public int left()
		{
			return left;
		}

		public int right()
		{
			return right;
		}

		public int top()
		{
			return top;
		}

		public int bottom()
		{
			return bottom;
		}

		@Override
		public String toString()
		{
			return "Pad{" + top() + "." + bottom() + "." + left() + "." + right() + "}";
		}
	}

	static Padding of(int padding)
	{
		return Padding.of(padding, padding, padding, padding);
	}

	static Padding of(int horizontal, int vertical)
	{
		return Padding.of(vertical, vertical, horizontal, horizontal);
	}

	static Padding of(int top, int bottom, int left, int right)
	{
		return top == 0 && bottom == 0 && left == 0 && right == 0 ? NO_PADDING : new FixedPadding(top, bottom, left, right);
	}

	/**
	 * Gets the {@link Padding} of an object.<br>
	 * If objects is not and instance of {@link IPadded}, return NO_PADDING.
	 *
	 * @param component object to get the padding from
	 * @return the padding
	 */
	static Padding of(Object component)
	{
		if (component instanceof Padding)
			return (Padding) component;
		else if (component instanceof IPadded)
			return ((IPadded) component).padding();
		return Padding.NO_PADDING;
	}

	static int topOf(Object obj)
	{
		return of(obj).top();
	}

	static int bottomOf(Object obj)
	{
		return of(obj).left();
	}

	static int leftOf(Object obj)
	{
		return of(obj).left();
	}

	static int rightOf(Object obj)
	{
		return of(obj).right();
	}

	static int verticalOf(Object obj)
	{
		return of(obj).vertical();
	}

	static int horizontalOf(Object obj)
	{
		return of(obj).horizontal();
	}

	/**
	 * IPadded defines an object that provides a Padding element.
	 */
	interface IPadded
	{
		default Padding padding()
		{
			return NO_PADDING;
		}
	}
}

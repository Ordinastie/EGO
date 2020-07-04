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

package net.malisis.ego.gui.element;

import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.component.control.IControlComponent;

/**
 * IClipable indicates an object (usually {@link UIComponent}) that they need to provide a ClipArea.<br>
 * That area will be used to clip content with glScissor.
 *
 * @author Ordinastie
 */
public interface IClipable
{
	ClipArea NOCLIP = new ClipArea(0, 0, 0, 0);
	ClipArea FULLCLIP = new ClipArea(-1, -1, -1, -1);

	/**
	 * Gets {@link ClipArea} to be used for glScissor
	 *
	 * @return the clip area.
	 */
	ClipArea getClipArea();

	static ClipArea of(Object clipable)
	{
		if (!(clipable instanceof IClipable))
			return NOCLIP;

		ClipArea area = ((IClipable) clipable).getClipArea();
		if (area.noClip())
			return area;
		return area.width() > 0 && area.height() > 0 ? area : FULLCLIP;
	}

	static ClipArea intersected(Object clipable)
	{
		ClipArea area = of(clipable);
		while (clipable instanceof IChild && !(clipable instanceof IControlComponent))
		{
			clipable = ((IChild) clipable).getParent();
			area = area.intersect(of(clipable));
		}

		return area;
	}

	class ClipArea
	{
		public int x;
		public int y;
		public int X;
		public int Y;

		public ClipArea(int x, int y, int X, int Y)
		{
			this.x = x;
			this.y = y;
			this.X = X;
			this.Y = Y;
		}

		public int width()
		{
			return X - x;
		}

		public int height()
		{
			return Y - y;
		}

		public boolean isInside(int x, int y)
		{
			return x >= this.x && x < X && y >= this.y && y < Y;
		}

		public ClipArea intersect(ClipArea other)
		{
			if (fullClip() || other.fullClip())
				return this;
			if (noClip())
				return other;
			if (other.noClip())
				return this;

			return from(Math.max(x, other.x), Math.max(y, other.y), Math.min(X, other.X), Math.min(Y, other.Y));
		}

		public boolean noClip()
		{
			return this == NOCLIP;
		}

		public boolean fullClip()
		{
			return this == FULLCLIP;
		}

		@Override
		public String toString()
		{
			if (noClip())
				return "NOCLIP";
			if (fullClip())
				return "FULLCLIP";
			return x + "," + y + " -> " + X + "," + Y + " (" + width() + "," + height() + ")";
		}

		public static ClipArea from(int x, int y, int X, int Y)
		{
			ClipArea area = new ClipArea(x, y, X, Y);
			return area.width() > 0 && area.height() > 0 ? area : FULLCLIP;
		}

		public static ClipArea from(UIComponent clipable)
		{
			Padding padding = Padding.of(clipable);
			int x = clipable.screenPosition()
							.x() + padding.left() + clipable.controlSpace(ISpace::left);
			int y = clipable.screenPosition()
							.y() + padding.top() + clipable.controlSpace(ISpace::top);

			return from(x, y, x + clipable.innerSize()
										  .width(), y + clipable.innerSize()
																.height());
		}

	}
}

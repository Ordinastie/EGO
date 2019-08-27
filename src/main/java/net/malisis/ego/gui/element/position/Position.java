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

package net.malisis.ego.gui.element.position;

import static net.malisis.ego.gui.element.position.Positions.bottomAligned;
import static net.malisis.ego.gui.element.position.Positions.centered;
import static net.malisis.ego.gui.element.position.Positions.leftAligned;
import static net.malisis.ego.gui.element.position.Positions.leftAlignedTo;
import static net.malisis.ego.gui.element.position.Positions.middleAligned;
import static net.malisis.ego.gui.element.position.Positions.middleAlignedTo;
import static net.malisis.ego.gui.element.position.Positions.rightAligned;
import static net.malisis.ego.gui.element.position.Positions.topAligned;

import net.malisis.ego.EGO;
import net.malisis.ego.gui.MalisisGui;
import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.component.control.IScrollable;
import net.malisis.ego.gui.element.IChild;
import net.malisis.ego.gui.element.IOffset;
import net.malisis.ego.gui.element.size.Size.ISized;

import java.util.function.IntSupplier;

/**
 * @author Ordinastie
 */

public class Position
{
	public static boolean CACHED = true;
	public static final IPosition ZERO = Position.of(0, 0);

	public interface IPositioned
	{
		public default IPosition position()
		{
			return Position.ZERO;
		}
	}

	public interface IPosition
	{
		public int x();

		public int y();

		public default IPosition offset(int x, int y)
		{
			return plus(Position.of(x, y));
		}

		public default IPosition plus(IPosition other)
		{
			if (other == null || other == Position.ZERO)
				return this;
			if (this == Position.ZERO)
				return other;

			return new DynamicPosition(0, 0, () -> x() + other.x(), () -> y() + other.y());
		}

		public default IPosition minus(IPosition other)
		{
			if (other == null || other == Position.ZERO)
				return this;
			if (this == Position.ZERO)
				return other;

			return new DynamicPosition(0, 0, () -> x() - other.x(), () -> y() - other.y());
		}
	}

	public static class DynamicPosition implements IPosition
	{
		protected int counterX = -1;
		protected int counterY = -1;
		protected int lock = 0;
		protected int x;
		protected int y;
		protected final IntSupplier xFunction;
		protected final IntSupplier yFunction;

		DynamicPosition(int x, int y, IntSupplier xFunction, IntSupplier yFunction)
		{
			this.x = x;
			this.y = y;
			this.xFunction = xFunction;
			this.yFunction = yFunction;
		}

		@Override
		public int x()
		{
			if (xFunction != null && (!Position.CACHED || MalisisGui.needsUpdate(counterX)))
			{
				if (lock++ >= 5)
				{
					EGO.log.error("Possible infinite recursion detected for x. (" + lock + ")");
					return x;
				}
				counterX = MalisisGui.counter;
				x = xFunction.getAsInt();
			}

			lock = 0;
			return x;
		}

		@Override
		public int y()
		{
			if (yFunction != null && (!Position.CACHED || MalisisGui.needsUpdate(counterY)))
			{
				if (lock++ >= 5)
				{
					EGO.log.error("Possible infinite recursion detected for width. (" + lock + ")");
					return y;
				}
				counterY = MalisisGui.counter;
				y = yFunction.getAsInt();
			}

			lock = 0;
			return y;
		}

		@Override
		public String toString()
		{
			return x() + "," + y();
		}
	}

	public static class ScreenPosition implements IPosition
	{
		private final IPositioned owner;
		/* Determines whether the position with be relative to the offset ot the owner (if owner is IOffset). */
		private final boolean fixed;

		private int x;
		private int y;
		private int counter = -1;

		public ScreenPosition(IPositioned owner)
		{
			this(owner, false);
		}

		public ScreenPosition(IPositioned owner, boolean fixed)
		{
			this.owner = owner;
			this.fixed = fixed;
		}

		public int updateX()
		{
			int x = owner.position()
						 .x();
			if (owner instanceof IChild<?>)
			{
				Object parent = ((IChild<?>) owner).getParent();
				if (parent instanceof UIComponent)
					x += ((UIComponent) parent).screenPosition()
											   .x();
				if (!fixed && parent instanceof IOffset)
					x += ((IOffset) parent).offset()
										   .x();
			}

			return x;
		}

		public int updateY()
		{
			int y = owner.position()
						 .y();
			if (owner instanceof IChild<?>)
			{
				Object parent = ((IChild<?>) owner).getParent();
				if (parent instanceof UIComponent)
					y += ((UIComponent) parent).screenPosition()
											   .y();
				if (!fixed && parent instanceof IScrollable)
					y += ((IScrollable) parent).offset()
											   .y();
			}
			return y;
		}

		private void update()
		{
			x = updateX();
			y = updateY();
			counter = MalisisGui.counter;
		}

		@Override
		public int x()
		{
			if (!Position.CACHED || MalisisGui.needsUpdate(counter))
				update();
			return x;
		}

		@Override
		public int y()
		{
			if (!Position.CACHED || MalisisGui.needsUpdate(counter))
				update();
			return y;
		}

		@Override
		public String toString()
		{
			return x() + "," + y();
		}
	}

	//Position shortcuts
	public static IPosition of(int x, int y)
	{
		return new DynamicPosition(x, y, null, null);
	}

	public static IPosition of(int x, IntSupplier y)
	{
		return new DynamicPosition(x, 0, null, y);
	}

	public static IPosition of(IntSupplier x, int y)
	{
		return new DynamicPosition(0, y, x, null);
	}

	public static IPosition of(IntSupplier x, IntSupplier y)
	{
		return new DynamicPosition(0, 0, x, y);
	}

	/**
	 * Creates a fixed {@link IPosition} based on the current values of <code>other</code>.
	 *
	 * @param other
	 * @return
	 */
	public static IPosition fixed(IPosition other)
	{
		return new DynamicPosition(other.x(), other.y(), null, null);
	}

	//position relative to parent
	public static IPosition inParent(IChild<?> owner, int x, int y)
	{
		return new DynamicPosition(0, 0, leftAligned(owner, x), topAligned(owner, y));
	}

	/**
	 * Positions the <code>owner</code> as the top left of its parent.<br>
	 * Does respect parent padding if any.
	 *
	 * @param owner the owner
	 * @return the i position
	 */
	public static IPosition topLeft(IChild<?> owner)
	{
		return of(leftAligned(owner, 0), topAligned(owner, 0));
	}

	public static <T extends ISized & IChild<U>, U extends ISized> IPosition topCenter(T owner)
	{
		return of(centered(owner, 0), topAligned(owner, 0));
	}

	public static <T extends ISized & IChild<U>, U extends ISized> IPosition topRight(T owner)
	{
		return of(rightAligned(owner, 0), topAligned(owner, 0));
	}

	public static <T extends ISized & IChild<U>, U extends ISized> IPosition middleLeft(T owner)
	{
		return of(leftAligned(owner, 0), middleAligned(owner, 0));
	}

	public static <T extends ISized & IChild<U>, U extends ISized> IPosition middleCenter(T owner)
	{
		return of(centered(owner, 0), middleAligned(owner, 0));
	}

	public static <T extends ISized & IChild<U>, U extends ISized> IPosition middleRight(T owner)
	{
		return of(rightAligned(owner, 0), middleAligned(owner, 0));
	}

	public static <T extends ISized & IChild<U>, U extends ISized> IPosition bottomLeft(T owner)
	{
		return of(leftAligned(owner, 0), bottomAligned(owner, 0));
	}

	public static <T extends ISized & IChild<U>, U extends ISized> IPosition bottomCenter(T owner)
	{
		return of(centered(owner, 0), bottomAligned(owner, 0));
	}

	public static <T extends ISized & IChild<U>, U extends ISized> IPosition bottomRight(T owner)
	{
		return of(rightAligned(owner, 0), bottomAligned(owner, 0));
	}

	//position relative to other
	public static <T extends IPositioned & ISized> IPosition leftOf(ISized owner, T other, int spacing)
	{
		return of(Positions.leftOf(owner, other, spacing), middleAlignedTo(owner, other, 0));
	}

	public static <T extends IPositioned & ISized> IPosition rightOf(ISized owner, T other, int spacing)
	{
		return of(Positions.rightOf(other, spacing), middleAlignedTo(owner, other, 0));
	}

	public static <T extends IPositioned & ISized, U extends IPositioned & ISized> IPosition above(T owner, U other, int spacing)
	{
		return of(leftAlignedTo(other, 0), Positions.above(owner, other, spacing));
	}

	public static <T extends IPositioned & ISized> IPosition below(Object owner, T other, int spacing)
	{
		return of(leftAlignedTo(other, 0), Positions.below(other, spacing));
	}

}

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

import net.malisis.ego.gui.EGOGui;
import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.element.IChild;
import net.malisis.ego.gui.element.IOffset;
import net.malisis.ego.gui.element.size.Size.ISized;

import java.util.function.IntSupplier;

import javax.annotation.Nonnull;

/**
 * @author Ordinastie
 */

public class Position
{
	public static boolean CACHED = true;
	public static final IPosition ZERO = Position.of(0, 0);

	public interface IPositioned
	{
		@Nonnull
		default IPosition position()
		{
			return Position.ZERO;
		}

		/**
		 * @return position().x()
		 */
		default int x()
		{
			return position().x();
		}

		/**
		 * @return position().y()
		 */
		default int y()
		{
			return position().y();
		}

		/**
		 * Determines whether the position with be relative to the offset of the owner (if owner is {@link IOffset}).
		 */
		default boolean fixed()
		{
			return false;
		}

	}

	public interface IPosition
	{
		int x();

		int y();

		default IPosition offset(int x, int y)
		{
			return plus(Position.of(x, y));
		}

		default IPosition plus(IPosition other)
		{
			if (other == null || other == Position.ZERO)
				return this;
			if (this == Position.ZERO)
				return other;

			return new CachedPosition(0, 0, () -> x() + other.x(), () -> y() + other.y());
		}

		default IPosition minus(IPosition other)
		{
			if (other == null || other == Position.ZERO)
				return this;
			if (this == Position.ZERO)
				return other;

			return new CachedPosition(0, 0, () -> x() - other.x(), () -> y() - other.y());
		}
	}

	public static class FixedPosition implements IPosition
	{
		protected int x;
		protected int y;

		public FixedPosition(int x, int y)
		{
			this.x = x;
			this.y = y;
		}

		@Override
		public int x()
		{
			return x;
		}

		@Override
		public int y()
		{
			return y;
		}

		@Override
		public String toString()
		{
			return x + "," + y;
		}
	}

	public static class DynamicPosition extends FixedPosition
	{
		protected final IntSupplier xFunction;
		protected final IntSupplier yFunction;
		protected boolean locked = false; //prevents infinite recursivity

		DynamicPosition(int x, int y, IntSupplier xFunction, IntSupplier yFunction)
		{
			super(x, y);
			this.xFunction = xFunction;
			this.yFunction = yFunction;
		}

		protected int updateX()
		{
			return xFunction.getAsInt();
		}

		protected int updateY()
		{
			return yFunction.getAsInt();
		}

		@Override
		public int x()
		{
			if (xFunction != null && !locked)
			{
				locked = true;
				x = updateX();
				locked = false;
			}
			return x;
		}

		@Override
		public int y()
		{
			if (yFunction != null && !locked)
			{
				locked = true;
				y = updateY();
				locked = false;
			}
			return y;
		}

		public IPosition cached()
		{
			return new CachedPosition(x, y, xFunction, yFunction);
		}
	}

	public static class CachedPosition extends DynamicPosition
	{
		protected int counterX = -1;
		protected int counterY = -1;

		public CachedPosition(int x, int y, IntSupplier xFunction, IntSupplier yFunction)
		{
			super(x, y, xFunction, yFunction);
		}

		@Override
		public int x()
		{
			locked = Position.CACHED && !EGOGui.needsUpdate(counterX);
			counterX = EGOGui.counter;
			return super.x();
		}

		@Override
		public int y()
		{
			locked = Position.CACHED && !EGOGui.needsUpdate(counterY);
			counterY = EGOGui.counter;
			return super.y();
		}
	}

	public static class ScreenPosition extends CachedPosition
	{
		private final IPositioned owner;

		public ScreenPosition(IPositioned owner)
		{
			super(0, 0, () -> 0, () -> 0);
			this.owner = owner;
		}

		@Override
		protected int updateX()
		{
			int x = owner.x();
			if (owner instanceof IChild)
			{
				Object parent = ((IChild) owner).getParent();
				if (parent instanceof UIComponent)
					x += ((UIComponent) parent).screenPosition()
											   .x();
				if (!owner.fixed() && parent instanceof IOffset)
					x += ((IOffset) parent).offset()
										   .x();
			}

			return x;
		}

		@Override
		protected int updateY()
		{
			int y = owner.y();
			if (owner instanceof IChild)
			{
				Object parent = ((IChild) owner).getParent();
				if (parent instanceof UIComponent)
					y += ((UIComponent) parent).screenPosition()
											   .y();
				if (!owner.fixed() && parent instanceof IOffset)
					y += ((IOffset) parent).offset()
										   .y();
			}
			return y;
		}

		@Override
		public String toString()
		{
			return x + "," + y;
		}
	}

	//Position shortcuts
	public static IPosition of(int x, int y)
	{
		return new FixedPosition(x, y);
	}

	public static IPosition of(int x, IntSupplier y)
	{
		return new CachedPosition(x, 0, null, y);
	}

	public static IPosition of(IntSupplier x, int y)
	{
		return new CachedPosition(0, y, x, null);
	}

	public static IPosition of(IntSupplier x, IntSupplier y)
	{
		return new CachedPosition(0, 0, x, y);
	}

	public static IPosition of(int x, int y, IntSupplier xFunc, IntSupplier yFunc, boolean cached)
	{
		if (xFunc == null && yFunc == null)
			return of(x, y);
		DynamicPosition p = new DynamicPosition(x, y, xFunc, yFunc);
		return cached ? p.cached() : p;
	}

	/**
	 * Creates a fixed {@link IPosition} based on the current values of <code>other</code>.
	 *
	 * @param other the position to copy the current values from
	 * @return the position
	 */
	public static IPosition of(IPosition other)
	{
		return new FixedPosition(other.x(), other.y());
	}

	//positions relative to parent

	/**
	 * Positions the <code>owner</code> at the top left of its parent.<br>
	 * Does respect parent padding if any.
	 *
	 * @param owner the element to be positioned
	 * @return the position
	 */
	public static IPosition topLeft(IChild owner)
	{
		return of(leftAligned(owner, 0), topAligned(owner, 0));
	}

	/**
	 * Positions the <code>owner</code> at the top center of its parent.<br>
	 * Does respect parent padding if any.
	 *
	 * @param owner the element to be positioned
	 * @return the position
	 */

	public static <T extends ISized & IChild> IPosition topCenter(T owner)
	{
		return of(centered(owner, 0), topAligned(owner, 0));
	}

	/**
	 * Positions the <code>owner</code> at the top right of its parent.<br>
	 * Does respect parent padding if any.
	 *
	 * @param owner the element to be positioned
	 * @return the position
	 */
	public static <T extends ISized & IChild> IPosition topRight(T owner)
	{
		return of(rightAligned(owner, 0), topAligned(owner, 0));
	}

	/**
	 * Positions the <code>owner</code> at the middle left of its parent.<br>
	 * Does respect parent padding if any.
	 *
	 * @param owner the element to be positioned
	 * @return the position
	 */
	public static <T extends ISized & IChild> IPosition middleLeft(T owner)
	{
		return of(leftAligned(owner, 0), middleAligned(owner, 0));
	}

	/**
	 * Positions the <code>owner</code> at the middle center of its parent.<br>
	 * Does respect parent padding if any.
	 *
	 * @param owner the element to be positioned
	 * @return the position
	 */
	public static <T extends ISized & IChild> IPosition middleCenter(T owner)
	{
		return of(centered(owner, 0), middleAligned(owner, 0));
	}

	/**
	 * Positions the <code>owner</code> at the middle right of its parent.<br>
	 * Does respect parent padding if any.
	 *
	 * @param owner the element to be positioned
	 * @return the position
	 */
	public static <T extends ISized & IChild> IPosition middleRight(T owner)
	{
		return of(rightAligned(owner, 0), middleAligned(owner, 0));
	}

	/**
	 * Positions the <code>owner</code> at the bottom left of its parent.<br>
	 * Does respect parent padding if any.
	 *
	 * @param owner the element to be positioned
	 * @return the position
	 */
	public static <T extends ISized & IChild> IPosition bottomLeft(T owner)
	{
		return of(leftAligned(owner, 0), bottomAligned(owner, 0));
	}

	/**
	 * Positions the <code>owner</code> at the bottom center of its parent.<br>
	 * Does respect parent padding if any.
	 *
	 * @param owner the element to be positioned
	 * @return the position
	 */
	public static <T extends ISized & IChild> IPosition bottomCenter(T owner)
	{
		return of(centered(owner, 0), bottomAligned(owner, 0));
	}

	/**
	 * Positions the <code>owner</code> at the bottom right of its parent.<br>
	 * Does respect parent padding if any.
	 *
	 * @param owner the element to be positioned
	 * @return the position
	 */
	public static <T extends ISized & IChild> IPosition bottomRight(T owner)
	{
		return of(rightAligned(owner, 0), bottomAligned(owner, 0));
	}

	//position relative to other

	/**
	 * Positions the <code>owner</code> to the left of and middle aligned to <code>other</code>.<br>
	 * Does respect parent padding if any.
	 *
	 * @param owner the element to be positioned
	 * @param other the element <code>owner</code> is positioned relative to.
	 * @return the position
	 */
	public static <T extends IPositioned & ISized> IPosition leftOf(ISized owner, T other, int spacing)
	{
		return of(Positions.leftOf(owner, other, spacing), middleAlignedTo(owner, other, 0));
	}

	/**
	 * Positions the <code>owner</code> to the right of and middle aligned to <code>other</code>.<br>
	 * Does respect parent padding if any.
	 *
	 * @param owner the element to be positioned
	 * @param other the element <code>owner</code> is positioned relative to.
	 * @return the position
	 */
	public static <T extends IPositioned & ISized> IPosition rightOf(ISized owner, T other, int spacing)
	{
		return of(Positions.rightOf(owner, other, spacing), middleAlignedTo(owner, other, 0));
	}

	/**
	 * Positions the <code>owner</code> above and left aligned to <code>other</code>.<br>
	 * Does respect parent padding if any.
	 *
	 * @param owner the element to be positioned
	 * @param other the element <code>owner</code> is positioned relative to.
	 * @return the position
	 */
	public static <T extends IPositioned & ISized, U extends IPositioned & ISized> IPosition above(T owner, U other, int spacing)
	{
		return of(leftAlignedTo(other, 0), Positions.above(owner, other, spacing));
	}

	/**
	 * Positions the <code>owner</code> below and left aligned to <code>other</code>.<br>
	 * Does respect parent padding if any.
	 *
	 * @param owner the element to be positioned
	 * @param other the element <code>owner</code> is positioned relative to.
	 * @return the position
	 */
	public static <T extends IPositioned & ISized> IPosition below(Object owner, T other, int spacing)
	{
		return of(leftAlignedTo(other, 0), Positions.below(owner, other, spacing));
	}
}

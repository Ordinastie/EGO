package net.malisis.ego.gui.element;

public interface ISpace
{
	/**
	 * Left space.
	 *
	 * @return the left space
	 */
	default int left()
	{
		return 0;
	}

	/**
	 * Right space.
	 *
	 * @return the right space
	 */
	default int right()
	{
		return 0;
	}

	/**
	 * Top space.
	 *
	 * @return the top space.
	 */
	default int top()
	{
		return 0;
	}

	/**
	 * Bottom space.
	 *
	 * @return the bottom space.
	 */
	default int bottom()
	{
		return 0;
	}

	/**
	 * Horizontal space.
	 *
	 * @return the horizontal space
	 */
	default int horizontal()
	{
		return left() + right();
	}

	/**
	 * Vertical space
	 *
	 * @return the vertical space
	 */
	default int vertical()
	{
		return top() + bottom();
	}

	default String toString(String prefix)
	{
		return prefix + "{" + top() + "." + bottom() + "." + left() + "." + right() + "}";
	}

	class Space implements ISpace
	{
		protected final int top, bottom, left, right;

		public Space(int top, int bottom, int left, int right)
		{
			this.top = top;
			this.bottom = bottom;
			this.left = left;
			this.right = right;
		}

		@Override
		public int left()
		{
			return left;
		}

		@Override
		public int right()
		{
			return right;
		}

		@Override
		public int top()
		{
			return top;
		}

		@Override
		public int bottom()
		{
			return bottom;
		}

		@Override
		public String toString()
		{
			return toString("S");
		}
	}
}

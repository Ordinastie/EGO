package net.malisis.ego.gui.element;

import static com.google.common.base.Preconditions.*;

import net.malisis.ego.gui.component.UIComponent;

public interface Margin extends ISpace
{
	Margin NO_MARGIN = new FixedMargin(0, 0, 0, 0);

	class FixedMargin implements Margin
	{
		protected final int top, bottom, left, right;

		public FixedMargin(int top, int bottom, int left, int right)
		{
			this.top = top;
			this.bottom = bottom;
			this.left = left;
			this.right = right;
		}

		/**
		 * Left margin.
		 *
		 * @return the left margin
		 */
		@Override
		public int left()
		{
			return left;
		}

		/**
		 * Right margin.
		 *
		 * @return the right margin
		 */
		@Override
		public int right()
		{
			return right;
		}

		/**
		 * Top margin.
		 *
		 * @return the top margin.
		 */
		@Override
		public int top()
		{
			return top;
		}

		/**
		 * Bottom margin.
		 *
		 * @return the bottom margin.
		 */
		@Override
		public int bottom()
		{
			return bottom;
		}

		@Override
		public String toString()
		{
			return toString("M");
		}
	}

	class InheritedMargin implements Margin
	{
		private final UIComponent component;

		public InheritedMargin(UIComponent component)
		{
			this.component = checkNotNull(component);
		}

		@Override
		public int left()
		{
			return leftOf(component.getParent());
		}

		@Override
		public int right()
		{
			return rightOf(component.getParent());
		}

		@Override
		public int top()
		{
			return topOf(component.getParent());
		}

		@Override
		public int bottom()
		{
			return bottomOf(component.getParent());
		}

		@Override
		public String toString(String prefix)
		{
			return toString("M");
		}
	}

	static Margin of(int margin)
	{
		return Margin.of(margin, margin, margin, margin);
	}

	static Margin of(int horizontal, int vertical)
	{
		return Margin.of(vertical, vertical, horizontal, horizontal);
	}

	static Margin of(int top, int bottom, int left, int right)
	{
		return top == 0 && bottom == 0 && left == 0 && right == 0 ? NO_MARGIN : new FixedMargin(top, bottom, left, right);
	}

	static int vertical(Object top, Object bottom)
	{
		//TODO: handle negatives ?
		return Math.max(of(top).bottom(), of(bottom).top());
	}

	static int horizontal(Object left, Object right)
	{
		//TODO: handle negatives ?
		return Math.max(of(left).right(), of(right).left());
	}

	static Margin of(Object component)
	{
		return component instanceof UIComponent ? ((UIComponent) component).margin() : NO_MARGIN;
	}

	static int leftOf(Object component)
	{
		return of(component).left();
	}

	static int rightOf(Object component)
	{
		return of(component).right();
	}

	static int topOf(Object component)
	{
		return of(component).top();
	}

	static int bottomOf(Object component)
	{
		return of(component).bottom();
	}

	static int horizontalOf(Object component)
	{
		return of(component).horizontal();
	}

	static int verticalOf(Object component)
	{
		return of(component).vertical();
	}

	static int top(IChild component)
	{
		return Math.max(of(component).top(), Padding.of(component.getParent())
													.top());
	}

	static int bottom(IChild component)
	{
		return Math.max(of(component).bottom(), Padding.of(component.getParent())
													   .bottom());
	}

	static int left(IChild component)
	{
		return Math.max(of(component).left(), Padding.of(component.getParent())
													 .left());
	}

	static int right(IChild component)
	{
		return Math.max(of(component).right(), Padding.of(component.getParent())
													  .right());
	}

}
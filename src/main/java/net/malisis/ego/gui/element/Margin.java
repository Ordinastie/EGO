package net.malisis.ego.gui.element;

import static com.google.common.base.Preconditions.*;

import net.malisis.ego.gui.component.UIComponent;

public class Margin
{
	public static final Margin NO_MARGIN = new Margin(0, 0, 0, 0);

	protected final int top, bottom, left, right;

	public Margin(int top, int bottom, int left, int right)
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
	public int left()
	{
		return left;
	}

	/**
	 * Right margin.
	 *
	 * @return the right margin
	 */
	public int right()
	{
		return right;
	}

	/**
	 * Top margin.
	 *
	 * @return the top margin.
	 */
	public int top()
	{
		return top;
	}

	/**
	 * Bottom margin.
	 *
	 * @return the bottom margin.
	 */
	public int bottom()
	{
		return bottom;
	}

	public int horizontal()
	{
		return left() + right();
	}

	public int vertical()
	{
		return top() + bottom();
	}

	@Override
	public String toString()
	{
		return "Margin{" + top() + "." + bottom() + "." + left() + "." + right() + "}";
	}

	public static class InheritedMargin extends Margin
	{
		private final UIComponent component;

		public InheritedMargin(UIComponent component)
		{
			super(0, 0, 0, 0);
			this.component = checkNotNull(component);
		}

		@Override
		public int left()
		{
			return of(component.getParent()).left();
		}

		@Override
		public int right()
		{
			return of(component.getParent()).right();
		}

		@Override
		public int top()
		{
			return of(component.getParent()).top();
		}

		@Override
		public int bottom()
		{
			return of(component.getParent()).bottom();
		}

	}

	public static Margin of(int margin)
	{
		return Margin.of(margin, margin, margin, margin);
	}

	public static Margin of(int horizontal, int vertical)
	{
		return Margin.of(vertical, vertical, horizontal, horizontal);
	}

	public static Margin of(int top, int bottom, int left, int right)
	{
		return top == 0 && bottom == 0 && left == 0 && right == 0 ? NO_MARGIN : new Margin(top, bottom, left, right);
	}

	public static int vertical(Object top, Object bottom)
	{
		//TODO: handle negatives ?
		return Math.max(of(top).bottom(), of(bottom).top());
	}

	public static int horizontal(Object left, Object right)
	{
		//TODO: handle negatives ?
		return Math.max(of(left).right(), of(right).left());
	}

	public static Margin of(Object component)
	{
		return component instanceof UIComponent ? ((UIComponent) component).margin() : NO_MARGIN;
	}

	public static int leftOf(Object component)
	{
		return of(component).left();
	}

	public static int rightOf(Object component)
	{
		return of(component).right();
	}

	public static int topOf(Object component)
	{
		return of(component).top();
	}

	public static int bottomOf(Object component)
	{
		return of(component).bottom();
	}

	public static int horizontalOf(Object component)
	{
		return of(component).horizontal();
	}

	public static int verticalOf(Object component)
	{
		return of(component).vertical();
	}

	public static int top(IChild component)
	{
		return Math.max(of(component).top(), Padding.of(component.getParent())
													.top());
	}

	public static int bottom(IChild component)
	{
		return Math.max(of(component).bottom(), Padding.of(component.getParent())
													   .bottom());
	}

	public static int left(IChild component)
	{
		return Math.max(of(component).left(), Padding.of(component.getParent())
													 .left());
	}

	public static int right(IChild component)
	{
		return Math.max(of(component).right(), Padding.of(component.getParent())
													  .right());
	}

}
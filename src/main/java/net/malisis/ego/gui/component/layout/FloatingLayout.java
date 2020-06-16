package net.malisis.ego.gui.component.layout;

import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.component.container.UIContainer;
import net.malisis.ego.gui.element.Margin;
import net.malisis.ego.gui.element.Padding;
import net.malisis.ego.gui.element.position.Position;

public class FloatingLayout implements ILayout
{
	private final UIContainer parent;
	private UIComponent last;
	private int currentX, currentY;
	private int lineHeight;

	private final int spacing;
	private final int wrapCount;
	private final boolean autoWrap;

	private int count;

	public FloatingLayout(FloatingLayoutBuilder builder, UIContainer parent)
	{
		this.parent = parent;
		spacing = builder.spacing;
		autoWrap = builder.autoWrap;
		wrapCount = builder.wrapCount;
		reset();
	}

	private void reset()
	{
		currentX = 0;
		currentY = Padding.of(parent)
						  .top();
		lineHeight = 0;
		last = null;
		count = 0;

	}

	private Position.IPosition current()
	{
		return Position.of(currentX, currentY);
	}

	private void updateCurrent(UIComponent component)
	{
		currentX += component.size()
							 .width() + Margin.of(component)
											  .left() + spacing;

	}

	private void nextLine()
	{
		currentX = 0;
		currentY += Margin.bottomOf(last) + lineHeight + spacing;
		lineHeight = 0;
		last = null;
		count = 0;
	}

	private int spaceBefore(UIComponent component)
	{
		return last == null ? Padding.leftOf(component.getParent()) : Margin.horizontal(last, component) + spacing;
	}

	private void place(UIComponent component, int space)
	{
		component.setPosition(Position.of(currentX + space, currentY));
		currentX += space + component.width();
	}

	private boolean shouldWrap(UIComponent component, int before)
	{
		if (wrapCount > 0 && count >= wrapCount)
			return true;
		return autoWrap && currentX + before + component.width() > parent.innerSize()
																		 .width() + Margin.right(component);
	}

	@Override
	public void add(UIComponent component)
	{
		int before = spaceBefore(component);
		if (shouldWrap(component, before))
			nextLine();

		place(component, before);
		lineHeight = Math.max(lineHeight, component.size()
												   .height());
		last = component;
		count++;
	}

	@Override
	public void remove(UIComponent component)
	{
		reset();
		parent.components()
			  .forEach(this::add);
	}

	@Override
	public void clear()
	{
		reset();
	}

	public static FloatingLayoutBuilder builder()
	{
		return new FloatingLayoutBuilder();
	}

	public static FloatingLayout create(UIContainer container)
	{
		return new FloatingLayoutBuilder().build(container);
	}

	public static class FloatingLayoutBuilder
	{
		private int spacing = 0;
		private boolean autoWrap = false;
		private int wrapCount = 0;

		public FloatingLayoutBuilder spacing(int spacing)
		{
			this.spacing = spacing;
			return this;
		}

		public FloatingLayoutBuilder autoWrap()
		{
			return autoWrap(true);
		}

		public FloatingLayoutBuilder autoWrap(boolean wrap)
		{
			this.autoWrap = wrap;
			return this;
		}

		public FloatingLayoutBuilder wrapCount(int count)
		{
			this.wrapCount = count;
			return this;
		}

		public FloatingLayout build(UIContainer container)
		{
			return new FloatingLayout(this, container);
		}
	}
}
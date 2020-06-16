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

	public FloatingLayout(UIContainer parent, int space)
	{
		this.parent = parent;
		this.spacing = space;
		reset();
	}

	private void reset()
	{
		currentX = 0;
		currentY = Padding.of(parent)
						  .top();
		last = null;

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
	}

	private int spaceBefore(UIComponent component)
	{
		return last == null ? Margin.left(component) : Margin.horizontal(last, component) + spacing;
	}

	private void place(UIComponent component, int space)
	{
		component.setPosition(Position.of(currentX + space, currentY));
		currentX += space + component.width();
	}

	@Override
	public void add(UIComponent component)
	{
		int before = spaceBefore(component);

		if (currentX + before + component.width() > parent.innerSize()
														  .width() + Margin.right(component))
			nextLine();

		place(component, before);
		lineHeight = Math.max(lineHeight, component.size()
												   .height());
		last = component;
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
}
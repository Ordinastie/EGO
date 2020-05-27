package net.malisis.ego.gui.component.layout;

import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.component.container.UIContainer;
import net.malisis.ego.gui.element.Padding;
import net.malisis.ego.gui.element.position.Position;

public class FloatingLayout implements ILayout
{
	private final UIContainer parent;
	private int currentX, currentY;
	private int lineHeight;
	private final int spacing;

	public FloatingLayout(UIContainer parent, int space)
	{
		this.parent = parent;
		this.spacing = space;
		currentX = Padding.of(parent)
						  .left();
		currentY = Padding.of(parent)
						  .top();
	}

	private Position.IPosition current()
	{
		return Position.of(currentX, currentY);
	}

	private void updateCurrent(UIComponent component)
	{
		currentX += component.size()
							 .width() + spacing;
		lineHeight = Math.max(lineHeight, component.size()
												   .height());
	}

	private void nextLine()
	{
		currentX = Padding.of(parent)
						  .left();
		currentY += lineHeight + spacing;
		lineHeight = 0;
	}

	@Override
	public void add(UIComponent component)
	{
		if (currentX + component.size()
								.width() > parent.size()
												 .width())
		{
			nextLine();
		}
		component.setPosition(current());
		updateCurrent(component);

	}

	@Override
	public void remove(UIComponent component)
	{
	}

	@Override
	public void clear()
	{
	}
}
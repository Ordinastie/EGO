package net.malisis.ego.gui.render.background;

import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.element.Padding;
import net.malisis.ego.gui.element.Padding.IPadded;
import net.malisis.ego.gui.render.GuiIcon;
import net.malisis.ego.gui.render.GuiRenderer;
import net.malisis.ego.gui.render.IGuiRenderer;
import net.malisis.ego.gui.render.shape.GuiShape;

import javax.annotation.Nonnull;

public class WindowBackground implements IGuiRenderer, IPadded
{
	private final GuiShape shape;
	private final Padding padding = Padding.of(5);

	public WindowBackground(UIComponent component)
	{
		shape = GuiShape.builder(component)
						.icon(GuiIcon.WINDOW)
						.border(5)
						.build();
	}

	@Nonnull
	@Override
	public Padding padding()
	{
		return padding;
	}

	@Override
	public void render(GuiRenderer renderer)
	{
		shape.render(renderer);
	}
}



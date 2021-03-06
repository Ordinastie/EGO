package net.malisis.ego.gui.render.background;

import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.element.Padding;
import net.malisis.ego.gui.element.Padding.IPadded;
import net.malisis.ego.gui.render.GuiRenderer;
import net.malisis.ego.gui.render.IGuiRenderer;
import net.malisis.ego.gui.render.shape.GuiShape;

import javax.annotation.Nonnull;

public class PanelBackground implements IGuiRenderer, IPadded
{
	private final GuiShape shape;
	private final Padding padding = Padding.of(3);

	public PanelBackground(UIComponent component)
	{
		shape = GuiShape.builder(component)
						.icon("panel")
						.border(3)
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

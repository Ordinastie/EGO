package net.malisis.ego.gui.render.background;

import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.render.GuiIcon;
import net.malisis.ego.gui.render.GuiRenderer;
import net.malisis.ego.gui.render.IGuiRenderer;
import net.malisis.ego.gui.render.shape.GuiShape;

public class PanelBackground implements IGuiRenderer
{
	private final GuiShape shape;

	public PanelBackground(UIComponent component)
	{
		shape = GuiShape.builder(component).icon(GuiIcon.PANEL).border(3).build();
	}

	@Override
	public void render(GuiRenderer renderer)
	{
		shape.render(renderer);
	}
}

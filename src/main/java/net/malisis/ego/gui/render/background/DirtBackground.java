package net.malisis.ego.gui.render.background;

import static net.minecraft.client.gui.Gui.OPTIONS_BACKGROUND;

import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.render.GuiIcon;
import net.malisis.ego.gui.render.GuiRenderer;
import net.malisis.ego.gui.render.GuiTexture;
import net.malisis.ego.gui.render.IGuiRenderer;
import net.malisis.ego.gui.render.shape.GuiShape;

public class DirtBackground implements IGuiRenderer
{
	private GuiShape shape;

	public DirtBackground(UIComponent screen)
	{
		shape = GuiShape.builder(screen)
						.color(0x404040)
						.icon(new GuiIcon(new GuiTexture(OPTIONS_BACKGROUND, 1, 1),
										  0F,
										  0F,
										  screen.size().width() / 32F,
										  screen.size().height() / 32F))
						.build();

	}

	@Override
	public void render(GuiRenderer renderer)
	{
		shape.render(renderer);
	}
}

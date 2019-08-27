package net.malisis.ego.gui.render.background;

import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.element.position.Position;
import net.malisis.ego.gui.element.size.Sizes;
import net.malisis.ego.gui.render.GuiRenderer;
import net.malisis.ego.gui.render.IGuiRenderer;
import net.malisis.ego.gui.render.shape.GuiShape;

import java.util.function.IntSupplier;

public class OptionPanel implements IGuiRenderer
{
	private static final int BORDER_HEIGHT = 4;

	private GuiShape top;
	private GuiShape middle;
	private GuiShape bottom;

	public OptionPanel(UIComponent component)
	{
		IntSupplier width = Sizes.widthRelativeTo(component, 1, 0);
		top = GuiShape.builder(component)
					  .widthRelativeTo(component)
					  .height(BORDER_HEIGHT)
					  .color(0x000000)
					  .bottomAlpha(120)
					  .build();
		middle = GuiShape.builder(component)
						 .position(s -> Position.below(s, top, 0))
						 .widthRelativeTo(component)
						 .heightRelativeTo(component, 1F, BORDER_HEIGHT * -2)
						 .color(0x000000)
						 .alpha(120)
						 .build();
		bottom = GuiShape.builder(component)
						 .position(s -> Position.below(s, middle, 0))
						 .widthRelativeTo(component)
						 .height(BORDER_HEIGHT)
						 .color(0x000000)
						 .topAlpha(120)
						 .build();

	}

	@Override
	public void render(GuiRenderer renderer)
	{
		top.and(middle)
		   .and(bottom)
		   .render(renderer);
	}
}

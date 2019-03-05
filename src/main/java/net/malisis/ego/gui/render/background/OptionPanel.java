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

	public OptionPanel(UIComponent component, int shadeColor)
	{
		IntSupplier width = Sizes.widthRelativeTo(component, 1, 0);
		top = GuiShape.builder(component).size(width, BORDER_HEIGHT).color(shadeColor).bottomAlpha(120).build();
		middle = GuiShape.builder(component)
						 .position(s -> Position.below(s, top, 0))
						 .size(width, Sizes.heightRelativeTo(component, 1, BORDER_HEIGHT * -2))
						 .color(shadeColor)
						 .alpha(120)
						 .build();
		bottom = GuiShape.builder(component)
						 .position(s -> Position.below(s, middle, 0))
						 .size(width, BORDER_HEIGHT)
						 .color(shadeColor)
						 .topAlpha(120)
						 .build();

	}

	public OptionPanel(UIComponent component)
	{
		this(component, 0x000000);
	}

	@Override
	public void render(GuiRenderer renderer)
	{
		top.and(middle).and(bottom).render(renderer);
	}
}

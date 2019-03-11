package net.malisis.ego.gui.component.scrolling;

import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.component.control.IScrollable;
import net.malisis.ego.gui.element.size.Size;
import net.malisis.ego.gui.render.shape.GuiShape;

/**
 * @author Ordinastie
 */
public final class UISlimScrollbar extends UIScrollBar
{
	/** Background color of the scroll. */
	protected int backgroundColor = 0x999999;
	/** Scroll color **/
	protected int scrollColor = 0xFFFFFF;
	/** Whether the scrollbar should fade in/out */
	protected boolean fade = true;

	public <T extends UIComponent & IScrollable> UISlimScrollbar(T parent, Type type)
	{
		super(parent, type);

		scrollSize = isHorizontal() ? Size.of(15, 2) : Size.of(2, 15);

		setBackground(GuiShape.builder(this).color(this::getBackgroundColor).build());
		setForeground(GuiShape.builder(this).position(scrollPosition).size(scrollSize).color(this::scrollColor).build());
	}

	public void setFade(boolean fade)
	{
		this.fade = fade;
	}

	public boolean isFade()
	{
		return fade;
	}

	/**
	 * Sets the color of the scroll.
	 *
	 * @param scrollColor the new color
	 */
	@Override
	public void setColor(int scrollColor)
	{
		setColor(scrollColor, backgroundColor);
	}

	/**
	 * Sets the color of the scroll and the background.
	 *
	 * @param scrollColor the scroll color
	 * @param backgroundColor the background color
	 */
	public void setColor(int scrollColor, int backgroundColor)
	{
		this.scrollColor = scrollColor;
		this.backgroundColor = backgroundColor;
	}

	public int scrollColor()
	{
		return scrollColor;
	}

	public int getBackgroundColor()
	{
		return backgroundColor;
	}
}
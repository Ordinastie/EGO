package net.malisis.ego.gui.component.layout;

import net.malisis.ego.gui.component.UIComponent;

public interface ILayout
{
	public void add(UIComponent component);

	public void remove(UIComponent component);

	public void clear();
}

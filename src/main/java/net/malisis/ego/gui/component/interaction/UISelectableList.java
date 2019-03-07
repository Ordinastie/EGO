package net.malisis.ego.gui.component.interaction;

import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.component.container.UIListContainer;
import net.malisis.ego.gui.event.GuiEvent;

import java.util.function.Predicate;

public class UISelectableList<T> extends UIListContainer<T>
{
	protected UIComponent selected;
	protected boolean unselectable = false;
	protected Predicate<SelectEvent<T, UISelectableList>> onSelect;

	public void setUnselectable(boolean unselectable)
	{
		this.unselectable = unselectable;
	}

	@Override
	protected void buildElementComponents()
	{
		super.buildElementComponents();
		//componentElements.forEach();
	}

	public void onSelect(Predicate<SelectEvent<T, UISelectableList>> onSelect)
	{
		this.onSelect = onSelect;
		applyEventToElements();
	}

	private void applyEventToElements()
	{
		//componentElements.forEach(c -> click());
	}

	public static class SelectEvent<U, T extends UIComponent> extends GuiEvent<T>
	{
		protected final U element;

		public SelectEvent(T source, U element)
		{
			super(source);
			this.element = element;
		}

		public U element()
		{
			return element;
		}
	}
}

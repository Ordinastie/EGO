package net.malisis.ego.gui.component.interaction;

import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.component.container.UIListContainer;
import net.malisis.ego.gui.event.ValueChange;
import net.malisis.ego.gui.event.ValueChange.IValueChangeEventRegister;

public class UISelectableList<T> extends UIListContainer<T> implements IValueChangeEventRegister<UISelectableList<T>, T>
{
	protected T selected;
	protected boolean unselectable = false;

	public void setUnselectable(boolean unselectable)
	{
		this.unselectable = unselectable;
	}

	@Override
	protected void buildElementComponents()
	{
		super.buildElementComponents();
		applyEventToElements();
	}

	@SuppressWarnings("unchecked")
	private void applyEventToElements()
	{
		componentElements.values().forEach(c -> c.onClick(e -> {
			select((T) c.getData());
			return true;
		}));
	}

	@SuppressWarnings("unchecked")
	public void select(UIComponent component)
	{
		select((T) component.getData());
	}

	public void select(T element)
	{
		UIComponent comp = componentElements.values().stream().filter(c -> c.getData() == element).findFirst().orElse(null);
		if (comp == null)
			return;

		T oldValue = selected;
		if (!fireEvent(new ValueChange.Pre<>(this, oldValue, element)))
			selected = element;
		fireEvent(new ValueChange.Post<>(this, oldValue, selected));
	}

	public T selected()
	{
		return selected;
	}

	public boolean isSelected(UIComponent component)
	{
		return component != null && component.getParent() == this && component.getData() == selected;
	}

	public boolean isSelected(T element)
	{
		return selected == element;
	}
}

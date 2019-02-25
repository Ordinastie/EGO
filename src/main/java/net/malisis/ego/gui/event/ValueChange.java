package net.malisis.ego.gui.event;

import net.malisis.ego.gui.component.UIComponent;

public class ValueChange<T extends UIComponent, S> extends GuiEvent<T>
{
	/** The old value. */
	protected S oldValue;
	/** The new value. */
	protected S newValue;

	/**
	 * Instantiates a new {@link ValueChange}
	 *
	 * @param component the component
	 * @param oldValue the old value
	 * @param newValue the new value
	 */
	public ValueChange(T component, S oldValue, S newValue)
	{
		super(component);
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	/**
	 * Gets the value being changed for the {@link UIComponent}.
	 *
	 * @return the old value
	 */
	public S getOldValue()
	{
		return oldValue;
	}

	/**
	 * Gets the value being set for the {@link UIComponent}.
	 *
	 * @return the new value
	 */
	public S getNewValue()
	{
		return newValue;
	}
}
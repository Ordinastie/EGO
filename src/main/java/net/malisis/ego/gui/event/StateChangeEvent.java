package net.malisis.ego.gui.event;

import net.malisis.ego.gui.component.UIComponent;

public abstract class StateChangeEvent<T extends UIComponent> extends GuiEvent<T>
{
	protected final boolean state;

	public StateChangeEvent(T source, boolean state)
	{
		super(source);
		this.state = state;
	}

	/**
	 * @return the new state for the {@link UIComponent}
	 */
	public boolean state()
	{
		return state;
	}

	//@formatter:off
	public static class VisibilityChangeEvent	<T extends UIComponent> extends StateChangeEvent<T> 		{ public VisibilityChangeEvent(T source, boolean state ) { super(source, state); }}
	public static class VisibleEvent			<T extends UIComponent> extends VisibilityChangeEvent<T> 	{ public VisibleEvent(T source) { super(source, true); }}
	public static class HiddenEvent				<T extends UIComponent> extends VisibilityChangeEvent<T> 	{ public HiddenEvent(T source) { super(source, false); }}

	public static class EnabilityChangeEvent	<T extends UIComponent> extends StateChangeEvent<T> 		{ public EnabilityChangeEvent(T source,	boolean state) { super(source, state); }}
	public static class EnableEvent				<T extends UIComponent> extends EnabilityChangeEvent<T> 	{ public EnableEvent(T source) { super(source, true); }}
	public static class DisableEvent			<T extends UIComponent> extends EnabilityChangeEvent<T> 	{ public DisableEvent(T source) { super(source, false); }}

	public static class FocusChangeEvent		<T extends UIComponent> extends StateChangeEvent<T> 		{ public FocusChangeEvent(T source,	boolean state) { super(source, state); }}
	public static class FocusEvent				<T extends UIComponent> extends FocusChangeEvent<T> 		{ public FocusEvent(T source) { super(source, true); }}
	public static class UnfocusEvent			<T extends UIComponent> extends FocusChangeEvent<T> 		{ public UnfocusEvent(T source) { super(source,	false); }}
	//@formatter:on
}

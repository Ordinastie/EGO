/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Ordinastie
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.malisis.ego.gui.event;

import net.malisis.ego.gui.component.UIComponent;

/**
 * Fired when a {@link UIComponent} changes state.
 *
 * @param <T> the type of <code>UIComponent that fired this event.
 * @author Ordinastie
 */
public abstract class StateChange<T extends UIComponent> extends GuiEvent<T>
{
	protected boolean state;

	public StateChange(T component, boolean state)
	{
		super(component);
		this.state = state;
	}

	/**
	 * @return the new state for the {@link UIComponent} that fired this {@link StateChange} event.
	 */
	public boolean getState()
	{
		return state;
	}

	/**
	 * Fired when a {@link UIComponent} gets hovered.
	 *
	 * @param <T> the type of <code>UIComponent that fired this event.
	 * @author Ordinastie
	 */
	public static class HoveredStateChange<T extends UIComponent> extends StateChange<T>
	{
		public HoveredStateChange(T component, boolean hovered)
		{
			super(component, hovered);
		}
	}

	/**
	 * Fired when a {@link UIComponent} gets focused.
	 *
	 * @param <T> the type of <code>UIComponent that fired this event.
	 * @author Ordinastie
	 */
	public static class FocusStateChange<T extends UIComponent> extends StateChange<T>
	{
		public FocusStateChange(T component, boolean focused)
		{
			super(component, focused);
		}
	}

	/**
	 * Fired when a {@link UIComponent} gets activated or deactivated.
	 *
	 * @param <T> the type of <code>UIComponent that fired this event.
	 * @author Ordinastie
	 */
	public static class ActiveStateChange<T extends UIComponent> extends StateChange<T>
	{
		public ActiveStateChange(T component, boolean active)
		{
			super(component, active);
		}
	}

	/**
	 * Fired when a {@link UIComponent} visibility changes.
	 *
	 * @param <T> the type of <code>UIComponent that fired this event.
	 * @author Ordinastie
	 */
	public static class VisibleStateChange<T extends UIComponent> extends StateChange<T>
	{
		public VisibleStateChange(T component, boolean visible)
		{
			super(component, visible);
		}
	}

	/**
	 * Fired when a {@link UIComponent} enability changes.
	 *
	 * @param <T> the type of <code>UIComponent that fired this event.
	 * @author Ordinastie
	 */
	public static class EnabledStateChange<T extends UIComponent> extends StateChange<T>
	{
		public EnabledStateChange(T component, boolean disabled)
		{
			super(component, disabled);
		}
	}
}
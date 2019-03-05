/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Ordinastie
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

import static com.google.common.base.Preconditions.*;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.malisis.ego.gui.component.UIComponent;

import java.util.function.Predicate;

/**
 * @author Ordinastie
 */
public class EventHandler
{
	//Todo: InheritanceMap/Multimap ?
	private Multimap<Class<?>, Predicate<?>> handlers = HashMultimap.create();

	/**
	 * Registers a {@link Predicate} handler for the type of {@link GuiEvent}.
	 *
	 * @param clazz type of event
	 * @param handler handler
	 */
	public void register(Class<? extends GuiEvent> clazz, Predicate<UIComponent> handler)
	{
		handlers.put(checkNotNull(clazz), checkNotNull(handler));
	}

	/**
	 * Unregisters a handle for the {@link GuiEvent}.
	 *
	 * @param clazz type of event
	 * @param handler handler to be removed
	 */
	public void unregister(Class<? extends GuiEvent> clazz, Predicate<UIComponent> handler)
	{
		handlers.remove(checkNotNull(clazz), handler);
	}

	/**
	 * Fires the {@link GuiEvent}. Triggers all predicates registered for that event type.<br>
	 *
	 * @param event event to fire
	 * @return true, to cancel event propagation to the source's parent.
	 */
	@SuppressWarnings("unchecked")
	public boolean fireEvent(GuiEvent<?> event)
	{
		boolean r = false;
		for (Predicate<?> p : handlers.get(event.getClass()))
			r |= ((Predicate<GuiEvent<?>>) p).test(event);
		return r;
	}
}

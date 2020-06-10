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

package net.malisis.ego.gui.component.control;

import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.element.IChild;
import net.malisis.ego.gui.render.IGuiRenderer;

/**
 * IControlledComponent are special components designed to affect other {@link UIComponent}.
 *
 * @author Ordinastie
 */
public interface IControlComponent extends IGuiRenderer, IChild
{
	/**
	 * Sets the {@link UIComponent} controlled by this {@link IControlComponent}.
	 *
	 * @param component the parent
	 */
	void setParent(UIComponent component);

	/**
	 * Gets the {@link UIComponent} controlled by this {@link IControlComponent}.
	 *
	 * @return the parent
	 */
	@Override
	UIComponent getParent();

	/**
	 * Gets the component at the specified coordinates. See {@link UIComponent#getComponentAt(int, int)}.
	 *
	 * @param x the x
	 * @param y the y
	 * @return the component at
	 */
	UIComponent getComponentAt(int x, int y);

	/**
	 * Gets the {@link UIComponent} matching the specified name.
	 *
	 * @param name the name
	 * @return the component
	 */
	UIComponent getComponent(String name);

	/**
	 * Called when a key is pressed when this {@link IControlComponent} or its parent is focused or hovered.<br>
	 * See {@link UIComponent#keyTyped(char, int)}.
	 *
	 * @param keyChar the key char
	 * @param keyCode the key code
	 * @return true, if successful
	 */
	boolean keyTyped(char keyChar, int keyCode);

	/**
	 * Called when the scrollwheel is used when this {@link IControlComponent} or its parent is focused or hovered.<br>
	 * See {@link UIComponent#scrollWheel(int)}
	 *
	 * @param delta the delta
	 */
	void scrollWheel(int delta);
}

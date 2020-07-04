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
import net.malisis.ego.gui.component.content.IContent;
import net.malisis.ego.gui.element.IChild;
import net.malisis.ego.gui.element.IClipable;
import net.malisis.ego.gui.element.ISpace;
import net.malisis.ego.gui.element.position.Position.IPosition;

/**
 * IControlComponent are special components held by a {@link UIComponent}.<br>
 * They get priority for mouse and keyboard events, and they don't get clipped by {@link IClipable} components.
 *
 * @author Ordinastie
 */
public interface IControlComponent extends IContent, ISpace
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

	IPosition screenPosition();

	default boolean isInsideBounds(int x, int y)
	{
		if (!getParent().isVisible())
			return false;
		int sx = x() + getParent().screenPosition()
								  .x();
		int sy = y() + getParent().screenPosition()
								  .y();
		return x >= sx && x <= sx + size().width() && y >= sy && y <= sy + size().height();
	}

	static int topSpace(Object owner)
	{
		if (owner instanceof IControlComponent)
			return 0;

		return IChild.parent(owner, IControlComponent::topOf);
	}

	static int bottomSpace(Object owner)
	{
		if (owner instanceof IControlComponent)
			return 0;

		return IChild.parent(owner, IControlComponent::bottomOf);
	}

	static int leftSpace(Object owner)
	{
		if (owner instanceof IControlComponent)
			return 0;

		return IChild.parent(owner, IControlComponent::leftOf);
	}

	static int rightSpace(Object owner)
	{
		if (owner instanceof IControlComponent)
			return 0;

		return IChild.parent(owner, IControlComponent::rightOf);
	}

	static int horizontalSpace(Object owner)
	{
		if (owner instanceof IControlComponent)
			return 0;

		return IChild.parent(owner, IControlComponent::horizontalOf);
	}

	static int verticalSpace(Object owner)
	{
		if (owner instanceof IControlComponent)
			return 0;

		return IChild.parent(owner, IControlComponent::verticalOf);
	}

	static int topOf(Object owner)
	{
		return owner instanceof UIComponent ? ((UIComponent) owner).controlSpace(ISpace::top) : 0;
	}

	static int bottomOf(Object owner)
	{
		return owner instanceof UIComponent ? ((UIComponent) owner).controlSpace(ISpace::bottom) : 0;
	}

	static int leftOf(Object owner)
	{
		return owner instanceof UIComponent ? ((UIComponent) owner).controlSpace(ISpace::left) : 0;
	}

	static int rightOf(Object owner)
	{
		return owner instanceof UIComponent ? ((UIComponent) owner).controlSpace(ISpace::right) : 0;
	}

	static int verticalOf(Object owner)
	{
		return owner instanceof UIComponent ? ((UIComponent) owner).controlSpace(ISpace::vertical) : 0;
	}

	static int horizontalOf(Object owner)
	{
		return owner instanceof UIComponent ? ((UIComponent) owner).controlSpace(ISpace::horizontal) : 0;
	}

}

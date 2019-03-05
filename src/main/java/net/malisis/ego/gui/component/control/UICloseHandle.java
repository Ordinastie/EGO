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

import static net.malisis.ego.gui.element.position.Positions.*;

import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.element.Padding;
import net.malisis.ego.gui.element.position.Position;
import net.malisis.ego.gui.element.size.Size;
import net.malisis.ego.gui.render.GuiIcon;
import net.malisis.ego.gui.render.shape.GuiShape;

/**
 * @author Ordinastie
 *
 */
public class UICloseHandle extends UIComponent implements IControlComponent
{
	public <T extends UIComponent & ICloseable> UICloseHandle(T parent)
	{
		Padding padding = Padding.of(parent);
		setPosition(Position.of(rightAligned(this, -padding.right()), topAligned(this, -padding.top())));
		setSize(Size.of(5, 5));
		setZIndex(parent.getZIndex() + 10);
		parent.addControlComponent(this);

		setForeground(GuiShape.builder(this).icon(GuiIcon.CLOSE).build());
	}

	@Override
	public boolean click()
	{
		((ICloseable) getParent()).onClose();
		return true;
	}
}

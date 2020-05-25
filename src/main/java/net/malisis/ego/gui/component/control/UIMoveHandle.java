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

import net.malisis.ego.gui.EGOGui;
import net.malisis.ego.gui.component.MouseButton;
import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.component.UIComponentBuilder;
import net.malisis.ego.gui.element.position.Position;
import net.malisis.ego.gui.element.position.Position.IPosition;
import net.malisis.ego.gui.element.size.Size.ISize;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Ordinastie
 */
public class UIMoveHandle extends UIComponent implements IControlComponent
{
	public enum Type
	{
		BOTH,
		HORIZONTAL,
		VERTICAL
	}

	private final Type type;

	private UIMoveHandle(Type type)
	{
		this.type = type != null ? type : Type.BOTH;
		//	setForeground(GuiShape.builder(this).icon(GuiIcon.MOVE).build());
	}

	@Override
	public void mouseDrag(MouseButton button)
	{
		if (button != MouseButton.LEFT || getParent() == null)
			return;

		UIComponent parent = getParent();
		IPosition current = parent.position();
		IPosition delta = EGOGui.MOUSE_POSITION.moved();

		int x = current.x() + (type == Type.VERTICAL ? 0 : delta.x());
		int y = current.y() + (type == Type.HORIZONTAL ? 0 : delta.y());

		UIComponent cont = parent.getParent();
		if (cont != null && StringUtils.equals(cont.getName(), "Screen"))//need a better way
		{
			//constraint the component to the bounds of the screen
			ISize size = parent.size();
			x = Math.max(x, 0);
			x = Math.min(x, cont.size()
								.width() - size.width());
			y = Math.max(y, 0);
			y = Math.min(y, cont.size()
								.height() - size.height());
		}

		getParent().setPosition(Position.of(x, y));
	}

	public static UIMoveHandlerBuilder builder()
	{
		return new UIMoveHandlerBuilder();
	}

	public static class UIMoveHandlerBuilder extends UIComponentBuilder<UIMoveHandlerBuilder, UIMoveHandle>
	{
		private Type type = Type.BOTH;

		private UIMoveHandlerBuilder()
		{
			x(0);
			y(0);
			width(c -> () -> c.getParent()
							  .size()
							  .width());
			height(c -> () -> c.getParent()
							   .size()
							   .height());
		}

		public UIMoveHandlerBuilder horizontal()
		{
			type = Type.HORIZONTAL;
			return this;
		}

		public UIMoveHandlerBuilder vertical()
		{
			type = Type.VERTICAL;
			return this;
		}

		@Override
		public UIMoveHandle build()
		{
			return build(new UIMoveHandle(type));
		}
	}
}

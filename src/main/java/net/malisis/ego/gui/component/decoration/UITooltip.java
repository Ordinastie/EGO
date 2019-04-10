/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 Ordinastie
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

package net.malisis.ego.gui.component.decoration;

import static net.malisis.ego.gui.element.position.Positions.centered;
import static net.malisis.ego.gui.element.position.Positions.middleAligned;

import net.malisis.ego.font.FontOptions;
import net.malisis.ego.gui.MalisisGui;
import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.component.content.IContent;
import net.malisis.ego.gui.component.content.IContentHolder;
import net.malisis.ego.gui.element.Padding;
import net.malisis.ego.gui.element.position.Position;
import net.malisis.ego.gui.element.position.Position.IPosition;
import net.malisis.ego.gui.element.size.Size;
import net.malisis.ego.gui.render.GuiIcon;
import net.malisis.ego.gui.render.shape.GuiShape;
import net.malisis.ego.gui.text.GuiText;
import net.minecraft.util.text.TextFormatting;

/**
 * UITooltip
 *
 * @author Ordinastie
 */
public class UITooltip extends UIComponent implements IContentHolder, IPosition
{
	protected Padding padding = Padding.of(4);
	protected IContent content;
	/** The default {@link FontOptions} to use for this {@link UITooltip} when using text. */
	protected FontOptions fontOptions = FontOptions.builder()
												   .color(0xFFFFFF)
												   .shadow()
												   .build();

	public UITooltip()
	{
		setZIndex(300);

		setPosition(this);
		setSize(Size.sizeOfContent(this, 8, 4));

		setBackground(GuiShape.builder(this)
							  .icon(GuiIcon.TOOLTIP)
							  .border(5)
							  .build());
	}

	public UITooltip(String text)
	{
		this();
		setText(text);
	}

	@Override
	public int x()
	{
		int xOffset = 8;
		int x = MalisisGui.MOUSE_POSITION.x() + xOffset;
		return Math.min(x, MalisisGui.current()
									 .width() - size().width());
	}

	@Override
	public int y()
	{
		int yOffset = -16;
		int y = MalisisGui.MOUSE_POSITION.y() + yOffset;
		if (y < 0)
			y = MalisisGui.MOUSE_POSITION.y() - yOffset;
		return y;
	}

	//#region Getters/Setters
	@Override
	public IContent content()
	{
		return content;
	}

	public void setContent(IContent content)
	{
		this.content = content;
		if (content instanceof UIComponent)
		{
			UIComponent c = (UIComponent) content;
			c.setParent(this);
			c.setPosition(Position.of(centered(c, 0), middleAligned(c, 2)));
		}
		setForeground(content);
	}

	public void setText(String text)
	{
		GuiText gt = GuiText.builder()
							.parent(this)
							.text(text)
							.fontOptions(fontOptions)
							.position(o -> centered(o, 0), o -> middleAligned(o, 2))
							.build();
		setContent(gt);
	}

	@Override
	public String getPropertyString()
	{
		//call x() and y() directly to prevent infinite recursion from parent because position() == this
		return "[" + TextFormatting.DARK_AQUA + content + TextFormatting.RESET + "] " + x() + "x" + y() + "@" + size() + " | Screen="
				+ screenPosition();
	}
}

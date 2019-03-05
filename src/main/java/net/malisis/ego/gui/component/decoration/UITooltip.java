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
import net.malisis.ego.gui.element.size.Size;
import net.malisis.ego.gui.render.GuiIcon;
import net.malisis.ego.gui.render.shape.GuiShape;
import net.malisis.ego.gui.text.GuiText;

/**
 * UITooltip
 *
 * @author Ordinastie
 */
public class UITooltip extends UIComponent implements IContentHolder
{
	protected Padding padding = Padding.of(4);
	protected IContent content;
	protected int delay = 0;
	/** The default {@link FontOptions} to use for this {@link UITooltip} when using text. */
	protected FontOptions fontOptions = FontOptions.builder().color(0xFFFFFF).shadow().build();

	private int xOffset = 8;
	private int yOffset = -16;

	public UITooltip()
	{
		setZIndex(300);

		setPosition(MalisisGui.MOUSE_POSITION.offset(xOffset, yOffset));
		setSize(Size.sizeOfContent(this, 8, 4));

		setBackground(GuiShape.builder(this).icon(GuiIcon.TOOLTIP).border(5).build());
		setForeground(this::content);

		//animation = new Animation<>(this, new AlphaTransform(0, 255).forTicks(2));
	}

	public UITooltip(String text)
	{
		this();
		setText(text);
	}

	public UITooltip(int delay)
	{
		this();
		setDelay(delay);
	}

	public UITooltip(String text, int delay)
	{
		this();
		setText(text);
		setDelay(delay);
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

	public UITooltip setDelay(int delay)
	{
		this.delay = delay;
		return this;
	}

	public int getDelay()
	{
		return delay;
	}

	protected int getOffsetX()
	{
		return 8;
	}

	protected int getOffsetY()
	{
		return -16;
	}

	//#end Getters/Setters
	public void animate()
	{
		if (delay == 0)
			return;

		setAlpha(0);
		//getGui().animate(animation, delay);
	}
}

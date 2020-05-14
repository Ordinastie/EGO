/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Ordinastie
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

package net.malisis.ego.atlas;

import net.malisis.ego.gui.EGOGui;
import net.malisis.ego.gui.component.MouseButton;
import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.element.position.Position;
import net.malisis.ego.gui.element.position.Position.IPosition;
import net.malisis.ego.gui.element.size.Size;
import net.malisis.ego.gui.element.size.Size.ISize;
import net.malisis.ego.gui.render.GuiIcon;
import net.malisis.ego.gui.render.shape.GuiShape;
import net.minecraft.util.math.MathHelper;

/**
 * @author Ordinastie
 */
public class AtlasComponent extends UIComponent
{
	private Atlas atlas;
	private float factor = 1;
	private int offsetX = 0;
	private int offsetY = 0;

	private float u;
	private float v;

	private String hoveredPosition;
	private String hoveredUV;

	private GuiIcon selected;
	//icon data
	private GuiIcon icon;
	private String iconName;
	private String iconSize;
	private String iconUs;
	private String iconVs;
	private String iconPixels;

	public AtlasComponent(Atlas atlas)
	{
		this.atlas = atlas;

		setBackground(GuiShape.builder(this)
							  .color(0x666666)
							  .alpha(100)
							  .build());

		IPosition atlasPosition = Position.of(() -> offsetX, () -> offsetY);
		ISize atlasSize = Size.of(() -> (int) (atlas.texture()
													.width() * factor), () -> (int) (atlas.texture()
																						  .height() * factor));
		GuiShape atlasIcon = GuiShape.builder(this)
									 .position(atlasPosition)
									 .size(atlasSize)
									 .icon(GuiIcon.full(atlas.texture()))
									 .build();

		GuiShape atlasOutline = GuiShape.builder(this)
										.position(atlasPosition.offset(-1, -1))
										.size(atlasSize.offset(2, 2))
										.alpha(0)
										.border(1, 0xCC6699)
										.build();

		GuiShape iconOutline = GuiShape.builder(this)
									   .x(() -> MathHelper.floor(selected.x() * factor) + offsetX - 1)
									   .y(() -> MathHelper.floor(selected.y() * factor) + offsetY - 1)
									   .width(() -> MathHelper.floor(selected.width() * factor) + 2)
									   .height(() -> MathHelper.floor(selected.height() * factor) + 2)
									   .alpha(0)
									   .border(1, 0xFF6666)
									   .build();

		setForeground(r -> {
			atlasIcon.render(r);
			atlasOutline.render(r);
			if (selected == null)
				return;
			iconOutline.render(r);
		});
	}

	private void updateHoveredData()
	{
		float rw = atlas.texture()
						.width() * factor;
		float rh = atlas.texture()
						.height() * factor;
		u = (mousePosition().x() - offsetX) / rw;
		v = (mousePosition().y() - offsetY) / rh;
		hoveredPosition = (mousePosition().x() - offsetX) + ", " + (mousePosition().y() - offsetX);
		hoveredUV = String.format("%.4f -> %.4f", u, v);
		this.icon = atlas.registeredIcons()
						 .stream()
						 .filter(i -> i.u() <= u && i.U() > u && i.v() <= v && i.V() > v)
						 .findAny()
						 .orElse(null);
		if (selected == null)
			updateIconData(icon);
	}

	private void updateIconData(GuiIcon icon)
	{
		if (icon == null)
		{
			iconName = "None";
			iconSize = "-";
			iconUs = "-";
			iconVs = "-";
			iconPixels = "-";
		}
		else
		{
			String name = icon.location()
							  .toString();
			iconName = name.substring(name.lastIndexOf("/") + 1);
			iconSize = icon.width() + "x" + icon.height();
			iconUs = String.format("%.4f -> %.4f", icon.u(), icon.U());
			iconVs = String.format("%.4f -> %.4f", icon.v(), icon.V());
			iconPixels = icon.x() + "," + icon.y() + " -> " + icon.X() + "," + icon.Y();
		}
	}

	public String hoveredPosition()
	{
		return hoveredPosition;
	}

	public String getHoveredUV()
	{
		return hoveredUV;
	}

	public boolean hasSelectedIcon()
	{
		return selected != null;
	}

	public String iconName()
	{
		return iconName;
	}

	public String iconSize()
	{
		return iconSize;
	}

	public String iconUs()
	{
		return iconUs;
	}

	public String iconVs()
	{
		return iconVs;
	}

	public String iconPixels()
	{
		return iconPixels;
	}

	@Override
	public void scrollWheel(int delta)
	{
		if (delta < 0)
			factor *= .9F;
		else
			factor *= 1.1F;
	}

	@Override
	public void mouseMove()
	{
		updateHoveredData();
	}

	@Override
	public void mouseDrag(MouseButton button)
	{
		if (button != MouseButton.LEFT)
			return;

		IPosition moved = EGOGui.MOUSE_POSITION.moved();
		offsetX += moved.x();
		offsetY += moved.y();
	}

	@Override
	public boolean dragPreventsClick()
	{
		return true;
	}

	@Override
	public void click(MouseButton button)
	{
		if (button == MouseButton.MIDDLE)
		{
			factor = 1;
			offsetX = 0;
			offsetY = 0;
		}
		if (button == MouseButton.LEFT)
		{
			if (icon != selected)
			{
				selected = icon;
				updateIconData(selected);
			}
			else
			{
				selected = null;
				updateIconData(icon);
			}
		}
	}
}

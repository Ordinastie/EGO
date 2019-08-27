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

import net.malisis.ego.gui.MalisisGui;
import net.malisis.ego.gui.component.MouseButton;
import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.element.position.Position;
import net.malisis.ego.gui.element.position.Position.IPosition;
import net.malisis.ego.gui.element.size.Size;
import net.malisis.ego.gui.element.size.Size.ISize;
import net.malisis.ego.gui.render.GuiIcon;
import net.malisis.ego.gui.render.GuiRenderer;
import net.malisis.ego.gui.render.shape.GuiShape;

/**
 * @author Ordinastie
 */
@SuppressWarnings("unchecked")
public class AtlasComponent extends UIComponent
{
	private float factor = 1;
	private int offsetX = 0;
	private int offsetY = 0;
	private IPosition atlasPosition = Position.of(() -> offsetX, () -> offsetY);
	private ISize atlasSize = Size.of(() -> (int) (MalisisGui.DEFAULT_TEXTURE.width() * factor),
									  () -> (int) (MalisisGui.DEFAULT_TEXTURE.height() * factor));

	private float u;
	private float v;

	private String hoveredPosition;
	private String hoveredUV;
	//icon data
	private GuiIcon icon;
	private String iconName;
	private String iconSize;
	private String iconUs;
	private String iconVs;
	private String iconPixels;

	private String filter = "";

	public AtlasComponent()
	{
		GuiIcon icon = GuiIcon.full(MalisisGui.DEFAULT_TEXTURE);
		setBackground(GuiShape.builder(this)
							  .color(0x666666)
							  .alpha(100)
							  .build());
		setForeground(this::renderAtlas);
	}

	private void updateHoveredData()
	{
		float rw = MalisisGui.DEFAULT_TEXTURE.width() * factor;
		float rh = MalisisGui.DEFAULT_TEXTURE.height() * factor;
		u = (mousePosition().x() - offsetX) / rw;
		v = (mousePosition().y() - offsetY) / rh;
		hoveredPosition = (mousePosition().x() - offsetX) + ", " + (mousePosition().y() - offsetX);
		hoveredUV = u + " / " + v;
		updateHoveredIcon();
	}

	private void updateHoveredIcon()
	{
		this.icon = Atlas.registeredIcons()
						 .stream()
						 .filter(i -> i.u() < u && i.U() > u && i.v() < v && i.V() > v)
						 .findAny()
						 .orElse(null);
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
			iconUs = String.format("%.3f -> %.3f", icon.u(), icon.U());
			iconVs = String.format("%.3f -> %.3f", icon.v(), icon.V());
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

	public void setFilter(String filter)
	{
		this.filter = filter;
	}

	public void renderAtlas(GuiRenderer renderer)
	{
		GuiIcon icon = new GuiIcon(MalisisGui.DEFAULT_TEXTURE, 0f, 0f, 1f, 1f);
		GuiShape.builder(this)
				.position(atlasPosition)
				.size(atlasSize)
				.icon(icon)
				.build()
				.render(renderer);

		GuiShape.builder(this)
				.position(atlasPosition)
				.size(atlasSize)
				.alpha(0)
				.border(1, 0xCC6699)
				.build()
				.render(renderer);

	}

	/*	public void drawForeground(GuiRenderer renderer, int mouseX, int mouseY, float partialTick)
		{
			int w = Icon.BLOCK_TEXTURE_WIDTH;
			int h = Icon.BLOCK_TEXTURE_HEIGHT;
			if (factor == -1)
			{
				//set factor to fit the altas in the component
				factor = h > w ? height() / (float) h : width() / (float) w;
			}

			w *= factor;
			h *= factor;

			drawAtlas(renderer, w, h);
			drawHoveredIcon(renderer, w, h);
			drawFilter(renderer, w, h);
		}

		private void drawAtlas(GuiRenderer renderer, int w, int h)
		{
			renderer.drawRectangle(offsetX, offsetY, 0, w, h, 0xFFFFFF, 0xFF);

			renderer.bindTexture(MalisisGui.BLOCK_TEXTURE);
			shape.resetState();
			shape.setSize(w, h);
			shape.setPosition(offsetX, offsetY);
			renderer.drawShape(shape, rp);
			renderer.next();
		}

		private void drawHoveredIcon(GuiRenderer renderer, int w, int h)
		{
			if (icon == null)
				return;
			renderer.next(GL11.GL_LINE_LOOP);
			renderer.disableTextures();
			GL11.glLineWidth(2);
			rp.colorMultiplier.set(0x33AA33);

			setupShape(icon, w, h);
			renderer.drawShape(shape, rp);

			renderer.next();
			renderer.enableTextures();
			renderer.next(GL11.GL_QUADS);
		}

		private void drawFilter(GuiRenderer renderer, int w, int h)
		{
			if (ICONS == null)
				return;
			if (Strings.isNullOrEmpty(filter))
				return;

			renderer.next();
			renderer.disableTextures();
			rp.colorMultiplier.set(0xFFFFFF);
			rp.alpha.set(200);
			for (TextureAtlasSprite icon : ICONS.values())
			{
				if (!icon.getIconName()
						 .contains(filter))
				{
					setupShape(icon, w, h);
					renderer.drawShape(shape, rp);
				}
			}

			renderer.next();
			renderer.enableTextures();
		}
		*/
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

		IPosition moved = MalisisGui.MOUSE_POSITION.moved();
		offsetX += moved.x();
		offsetY += moved.y();
	}

	@Override
	public void click(MouseButton button)
	{
		if (button != MouseButton.MIDDLE)
			return;

		factor = 1;
		offsetX = 0;
		offsetY = 0;
	}
}

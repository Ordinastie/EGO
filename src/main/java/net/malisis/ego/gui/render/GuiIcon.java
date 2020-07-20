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

package net.malisis.ego.gui.render;

import static net.malisis.ego.gui.EGOGui.VANILLAGUI_TEXTURE;

import net.malisis.ego.atlas.Atlas;
import net.malisis.ego.gui.EGOGui;
import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.theme.Theme;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.function.Supplier;

import javax.annotation.Nullable;

/**
 * @author Ordinastie
 */
public class GuiIcon
{
	public static final GuiIcon FULL = new GuiIcon(0, 0, 1, 1);
	public static GuiIcon NONE;
	public static GuiIcon BORDER;

	public static void registerIcons(Atlas atlas)
	{
		//		CLOSE = new GuiIcon(VANILLAGUI_TEXTURE, 268, 30, 15, 15);
		//		MOVE = new GuiIcon(VANILLAGUI_TEXTURE, 268, 15, 15, 15);
		//		RESIZE = new GuiIcon(VANILLAGUI_TEXTURE, 268, 0, 15, 15);

	}

	protected GuiTexture texture = VANILLAGUI_TEXTURE;
	protected ResourceLocation location;
	protected float u = 0;
	protected float v = 0;
	protected float U = 1;
	protected float V = 1;
	protected int x = 0;
	protected int y = 0;
	protected int width = 16;
	protected int height = 16;

	public GuiIcon(ResourceLocation location)
	{
		this.location = location;
	}

	public GuiIcon(float u, float v, float U, float V)
	{
		this.u = u;
		this.v = v;
		this.U = U;
		this.V = V;
	}

	public GuiIcon(GuiTexture texture, float u, float v, float U, float V)
	{
		this(u, v, U, V);
		this.texture = texture;
	}

	public GuiIcon(GuiTexture texture, int x, int y, int width, int height)
	{
		this.texture = texture;

		u = texture.pixelToU(x);
		v = texture.pixelToV(y);
		U = texture.pixelToU(x + width);
		V = texture.pixelToV(y + height);
		this.width = width;
		this.height = height;
	}

	public GuiIcon(GuiTexture texture)
	{
		this(texture, 0f, 0f, 1f, 1f);
	}

	public GuiIcon(GuiTexture texture, TextureAtlasSprite icon)
	{
		this(texture, icon.getMinU(), icon.getMinV(), icon.getMaxU(), icon.getMaxV());
		location = new ResourceLocation(icon.getIconName());
		width = icon.getIconWidth();
		height = icon.getIconHeight();
	}

	public ResourceLocation location()
	{
		return location;
	}

	public float u()
	{
		return u;
	}

	public float v()
	{
		return v;
	}

	public float U()
	{
		return U;
	}

	public float V()
	{
		return V;
	}

	public int x()
	{
		return pixelFromU(u());
	}

	public int y()
	{
		return pixelFromV(v());
	}

	public int X()
	{
		return pixelFromU(U());
	}

	public int Y()
	{
		return pixelFromV(V());
	}

	public int width()
	{
		return width;
	}

	public int height()
	{
		return height;
	}

	public float interpolatedU(float i)
	{
		return u() + i * (U() - u());
	}

	public float interpolatedV(float i)
	{
		return v() + i * (V() - v());
	}

	/**
	 * Gets the horizontal texture coordinate relative to this {@link GuiIcon}.<br>
	 * A negative value return a coordinate relative to the right bound of this icon.
	 *
	 * @param px
	 * @return the horizontal coordinate
	 */
	public float pixelToU(int px)
	{
		float offset = texture != null ? texture.pixelToU(px) : 0;
		return px >= 0 ? u() + offset : U() + offset;
	}

	/**
	 * Gets the vertical texture coordinate relative to this {@link GuiIcon}.<br>
	 * A negative value return a coordinate relative to the lower bound of this icon.
	 *
	 * @param px
	 * @return the vertical coordinate
	 */
	public float pixelToV(int px)
	{
		float offset = texture != null ? texture.pixelToV(px) : 0;
		return px >= 0 ? v() + offset : V() + offset;
	}

	public int pixelFromU(float u)
	{
		if (texture == null)
			return 0;

		return (int) (u * texture.width());
	}

	public int pixelFromV(float v)
	{
		if (texture == null)
			return 0;

		return (int) (v * texture.height());
	}

	public GuiIcon flip(boolean horizontal, boolean vertical)
	{
		return new GuiIcon(texture, horizontal ? U() : u(), vertical ? V() : v(), horizontal ? u() : U(), vertical ? v() : V());
	}

	public GuiIcon clip(float xOffset, float yOffset, float width, float height)
	{
		xOffset = (U() - u()) * xOffset;
		yOffset = (V() - v()) * yOffset;
		width = (U() - u()) * width;
		height = (V() - v()) * height;
		return new GuiIcon(texture, u() + xOffset, v() + yOffset, u() + xOffset + width, v() + yOffset + height);
	}

	public GuiIcon copy()
	{
		return new GuiIcon(texture, u(), v(), U(), v());
	}

	public void stitch(@Nullable GuiTexture texture, int x, int y, int width, int height)
	{
		this.texture = texture;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		if (texture == null) //TODO: missing texture ?
			return;
		u = texture.pixelToU(x);
		v = texture.pixelToV(y);
		U = texture.pixelToU(x + width);
		V = texture.pixelToV(y + height);
	}

	public void bind(GuiRenderer renderer)
	{
		renderer.bindTexture(texture);
	}

	@Override
	public String toString()
	{
		String str = String.format("%.02f", u()) + "," + String.format("%.02f", v()) + " -> " + String.format("%.02f", U()) + ","
				+ String.format("%.02f", V());
		if (texture != null)
			str += " [" + texture.pixelFromU(u()) + "," + texture.pixelFromV(v()) + " -> " + texture.pixelFromU(U()) + ","
					+ texture.pixelFromV(V()) + "]";
		return str;
	}

	public static GuiIcon full(GuiTexture texture)
	{
		return new GuiIcon(texture, 0F, 0F, 1F, 1F);
	}

	public static GuiIcon from(ItemStack itemStack)
	{
		TextureAtlasSprite icon = Minecraft.getMinecraft()
										   .getRenderItem()
										   .getItemModelMesher()
										   .getParticleIcon(itemStack.getItem(), itemStack.getMetadata());

		return new GuiIcon(EGOGui.BLOCK_TEXTURE, icon);
	}

	public static GuiIcon from(Item item)
	{
		return from(new ItemStack(item));
	}

	public static GuiIcon from(IBlockState state)
	{
		TextureAtlasSprite icon = Minecraft.getMinecraft()
										   .getBlockRendererDispatcher()
										   .getBlockModelShapes()
										   .getTexture(state);

		return new GuiIcon(EGOGui.BLOCK_TEXTURE, icon);
	}

	public static GuiIcon from(Block block)
	{
		return from(block.getDefaultState());
	}

	public static Supplier<GuiIcon> forComponent(UIComponent component, String icon, String hover, String disabled)
	{
		return forComponent(component, Theme.icon(icon), Theme.icon(hover), Theme.icon(disabled));
	}

	public static Supplier<GuiIcon> forComponent(UIComponent component, GuiIcon icon, GuiIcon hover, GuiIcon disabled)
	{
		return () -> {
			if (disabled != null && component.isDisabled())
				return disabled;
			else if (hover != null && component.isHovered())
				return hover;
			return icon;
		};
	}
}

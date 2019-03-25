/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 PaleoCrafter, Ordinastie
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

import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.component.UIComponentBuilder;
import net.malisis.ego.gui.element.size.Size;
import net.malisis.ego.gui.element.size.Size.ISize;
import net.malisis.ego.gui.render.GuiIcon;
import net.malisis.ego.gui.render.GuiTexture;
import net.malisis.ego.gui.render.IGuiRenderer;
import net.malisis.ego.gui.render.shape.GuiShape;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.ObjectUtils;

import javax.annotation.Nonnull;
import javax.swing.Icon;

/**
 * UIImage.
 *
 * @author Ordinastie
 */
public class UIImage extends UIComponent
{
	/** Fixed size of ItemStack UIImages. */
	private final ISize ITEMSTACK_SIZE = Size.of(16, 16);
	/** {@link GuiIcon} to use for the texture. */
	private GuiIcon icon = null;
	/** {@link ItemStack} to render. */
	private ItemStack itemStack;

	private final IGuiRenderer ICON_RENDER = GuiShape.builder(this)
													 .icon(this::getIcon)
													 .build();
	private final IGuiRenderer IS_RENDER = (r) -> r.drawItemStack(itemStack);

	/**
	 * Instantiates a new {@link UIImage}.
	 *
	 * @param icon the icon
	 */
	public UIImage(GuiIcon icon)
	{
		setIcon(icon);
		setSize(ITEMSTACK_SIZE);
	}

	/**
	 * Instantiates a new {@link UIImage}.
	 *
	 * @param itemStack the item stack
	 */
	public UIImage(ItemStack itemStack)
	{
		setItemStack(itemStack);
	}

	/**
	 * Sets the icon for this {@link UIImage}.
	 *
	 * @param icon the icon
	 * @return this UIImage
	 */
	public UIImage setIcon(GuiIcon icon)
	{
		itemStack = null;
		this.icon = icon;
		setForeground(ICON_RENDER);
		return this;
	}

	/**
	 * Sets the {@link ItemStack} to render.
	 *
	 * @param itemStack the item stack
	 * @return this UIImage
	 */
	public UIImage setItemStack(ItemStack itemStack)
	{
		icon = null;
		this.itemStack = itemStack;
		setSize(ITEMSTACK_SIZE);
		setForeground(IS_RENDER);
		return this;
	}

	/**
	 * Gets the {@link Icon} for this {@link UIImage}.
	 *
	 * @return the icon
	 */
	public GuiIcon getIcon()
	{
		return icon;
	}

	/**
	 * Gets the {@link ItemStack} for this {@link UIImage}.
	 *
	 * @return the item stack
	 */
	public ItemStack getItemStack()
	{
		return itemStack;
	}

	/**
	 * Sets the size for this {@link UIImage}.<br>
	 * Has no effect if rendering an {@link ItemStack}.
	 *
	 * @param size the new size
	 */
	@Override
	public void setSize(@Nonnull ISize size)
	{
		//UIImage for itemStack have a fixed 16*16 size
		if (itemStack != null)
			size = ITEMSTACK_SIZE;
		super.setSize(size);
	}

	@Override
	public String getPropertyString()
	{
		return ObjectUtils.firstNonNull(itemStack, icon) + " " + super.getPropertyString();
	}

	public static UIImageBuilder builder()
	{
		return new UIImageBuilder();
	}

	public static class UIImageBuilder extends UIComponentBuilder<UIImageBuilder, UIImage>
	{
		protected GuiIcon icon;
		protected ItemStack itemStack;

		protected UIImageBuilder()
		{
		}

		public UIImageBuilder icon(GuiIcon icon)
		{
			this.icon = icon;
			itemStack = null;
			return this;
		}

		public UIImageBuilder texture(GuiTexture texture)
		{
			icon = new GuiIcon(texture);
			itemStack = null;
			return this;
		}

		public UIImageBuilder itemStack(ItemStack itemStack)
		{
			this.itemStack = itemStack;
			icon = null;
			return this;
		}

		public UIImageBuilder item(Item item)
		{
			itemStack = new ItemStack(item);
			icon = null;
			return this;
		}

		@Override
		public UIImage build()
		{
			UIImage image = build(new UIImage((GuiIcon) null));
			if (icon != null)
				image.setIcon(icon);
			if (itemStack != null)
				image.setItemStack(itemStack);

			return image;
		}
	}

}

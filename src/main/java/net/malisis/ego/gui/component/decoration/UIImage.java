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

import static com.google.common.base.Preconditions.*;

import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.component.UIComponentBuilder;
import net.malisis.ego.gui.element.size.Size;
import net.malisis.ego.gui.element.size.Size.ISize;
import net.malisis.ego.gui.render.GuiIcon;
import net.malisis.ego.gui.render.GuiTexture;
import net.malisis.ego.gui.render.shape.GuiShape;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.ObjectUtils;

import java.util.function.Supplier;

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
	/** Adapts to the size of the icon set. */
	private final ISize ICON_SIZE = Size.of(this::iconWidth, this::iconHeight);
	/** {@link GuiIcon} to use for the texture. */
	private Supplier<GuiIcon> icon = null;
	/** {@link ItemStack} to render. */
	private Supplier<ItemStack> itemStack = null;

	private final GuiShape iconShape = GuiShape.builder(this)
											   .icon(this::getIcon)
											   .build();

	/**
	 * Instantiates a new {@link UIImage}.
	 */
	protected UIImage()
	{
		setSize(ICON_SIZE);
		setForeground(r -> {
			if (itemStack != null)
				r.drawItemStack(itemStack.get());
			else if (icon != null)
				iconShape.render(r);
		});
	}

	private int iconWidth()
	{
		if (icon == null)
			return 16;
		GuiIcon guiIcon = icon.get();
		if (guiIcon == null)
			return 16;
		return guiIcon.width();
	}

	private int iconHeight()
	{
		if (icon == null)
			return 16;
		GuiIcon guiIcon = icon.get();
		if (guiIcon == null)
			return 16;
		return guiIcon.height();
	}

	@Nonnull
	@Override
	public ISize size()
	{
		return itemStack != null ? ITEMSTACK_SIZE : size;
	}

	public void setSizeOfIcon()
	{
		setSize(ICON_SIZE);
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
	 */
	public void setIcon(GuiIcon icon)
	{
		setIcon(() -> icon);
	}

	public void setIcon(Supplier<GuiIcon> supplier)
	{
		icon = checkNotNull(supplier);
		itemStack = null;
	}

	/**
	 * Sets the {@link ItemStack} to render.
	 *
	 * @param itemStack the item stack
	 */
	public void setItemStack(ItemStack itemStack)
	{
		setItemStack(() -> itemStack);
	}

	public void setItemStack(Supplier<ItemStack> supplier)
	{
		itemStack = checkNotNull(supplier);
		icon = null;
	}

	/**
	 * Gets the {@link Icon} for this {@link UIImage}.
	 *
	 * @return the icon
	 */
	public GuiIcon getIcon()
	{
		return icon.get();
	}

	/**
	 * Gets the {@link ItemStack} for this {@link UIImage}.
	 *
	 * @return the item stack
	 */
	public ItemStack getItemStack()
	{
		return itemStack.get();
	}

	@Override
	public String getPropertyString()
	{
		return ObjectUtils.firstNonNull(itemStack, icon.get()) + " " + super.getPropertyString();
	}

	public static UIImageBuilder builder()
	{
		return new UIImageBuilder();
	}

	public static class UIImageBuilder extends UIComponentBuilder<UIImageBuilder, UIImage>
	{
		protected Supplier<GuiIcon> icon;
		protected Supplier<ItemStack> itemStack;

		protected UIImageBuilder()
		{
		}

		public UIImageBuilder icon(GuiIcon icon)
		{
			return icon(() -> icon);
		}

		public UIImageBuilder icon(Supplier<GuiIcon> supplier)
		{
			icon = checkNotNull(supplier);
			itemStack = null;
			return this;
		}

		public UIImageBuilder texture(GuiTexture texture)
		{
			return icon(new GuiIcon(texture));
		}

		public UIImageBuilder itemStack(ItemStack itemStack)
		{
			return itemStack(() -> itemStack);
		}

		public UIImageBuilder itemStack(Supplier<ItemStack> supplier)
		{
			itemStack = checkNotNull(supplier);
			icon = null;
			return this;
		}

		public UIImageBuilder item(Item item)
		{
			return itemStack(new ItemStack(item));
		}

		@Override
		public UIImage build()
		{
			UIImage image = build(new UIImage());
			if (icon != null)
				image.setIcon(icon);
			if (itemStack != null)
				image.setItemStack(itemStack);

			image.size()
				 .width();
			image.size()
				 .height();

			return image;
		}
	}

}

/*
 * This file is part of Almura.
 *
 * Copyright (c) AlmuraDev <https://github.com/AlmuraDev/>
 *
 * All Rights Reserved.
 */
package net.malisis.ego.gui.component.decoration;

import static com.google.common.base.Preconditions.*;

import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.component.UIComponentBuilder;
import net.malisis.ego.gui.element.position.Position;
import net.malisis.ego.gui.element.position.Position.IPosition;
import net.malisis.ego.gui.element.size.Size;
import net.malisis.ego.gui.element.size.Size.ISize;
import net.malisis.ego.gui.render.GuiIcon;
import net.malisis.ego.gui.render.GuiTexture;
import net.malisis.ego.gui.render.shape.GuiShape;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class UIPlayerHead extends UIComponent
{
	private static final IPosition BASE = Position.of(8, 8);
	private static final IPosition OVERLAY = Position.of(40, 8);
	private static final ISize SIZE = Size.of(8, 8);

	private ResourceLocation skinLocation;
	private GuiTexture texture;

	public UIPlayerHead(ResourceLocation skinLocation)
	{
		this.skinLocation = checkNotNull(skinLocation);
		this.texture = new GuiTexture(skinLocation, 64, 64);
		GuiShape shape = GuiShape.builder(this)
								 .icon(new GuiIcon(texture, BASE.x(), BASE.y(), SIZE.width(), SIZE.height()))
								 .build();
		GuiShape overlay = GuiShape.builder(this)
								   .icon(new GuiIcon(texture, OVERLAY.x(), OVERLAY.y(), SIZE.width(), SIZE.height()))
								   .build();

		setBackground(shape);
		setForeground(overlay);
	}

	public static UIPlayerHeadBuilder builder(EntityPlayerSP player)
	{
		return new UIPlayerHeadBuilder(player);
	}

	public static class UIPlayerHeadBuilder extends UIComponentBuilder<UIPlayerHeadBuilder, UIPlayerHead>
	{
		private ResourceLocation skinLocation;

		public UIPlayerHeadBuilder(EntityPlayerSP player)
		{
			checkNotNull(player);
			this.skinLocation = player.getLocationSkin();
		}

		@Override
		public UIPlayerHead build()
		{
			return build(new UIPlayerHead(skinLocation));
		}
	}
}

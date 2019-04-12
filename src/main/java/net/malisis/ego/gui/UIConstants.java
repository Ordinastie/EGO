package net.malisis.ego.gui;

import net.malisis.ego.font.FontOptions;
import net.malisis.ego.gui.element.size.Size;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface UIConstants
{
	FontOptions DEFAULT_TEXTBOX_FO = FontOptions.builder()
												.color(0xFFFFFF)
												.shadow(false)
												.build();
	FontOptions READ_ONLY_TEXTBOX_FO = FontOptions.builder()
												  .color(0xC5C5C5)
												  .shadow(false)
												  .build();

	interface Button
	{
		int WIDTH = 200;
		int WIDTH_HALF = 98;
		int WIDTH_THIRD = 64;
		int WIDTH_ICON = 24;

		int HEIGHT = 20;
		int HEIGHT_ICON = 24;

		Size.ISize DEFAULT = Size.of(WIDTH, HEIGHT);
		Size.ISize HALF = Size.of(WIDTH_HALF, HEIGHT);
		Size.ISize THIRD = Size.of(WIDTH_THIRD, HEIGHT);
		Size.ISize ICON = Size.of(WIDTH_ICON, HEIGHT_ICON);
	}
}
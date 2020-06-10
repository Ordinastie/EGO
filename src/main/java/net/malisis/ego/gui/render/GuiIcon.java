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

import net.malisis.ego.EGO;
import net.malisis.ego.atlas.Atlas;
import net.malisis.ego.gui.EGOGui;
import net.malisis.ego.gui.component.UIComponent;
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
	public static GuiIcon FULL;
	/** White canvas to be used for colored background */
	public static GuiIcon NONE;
	/** Just a border with transparent inside */
	public static GuiIcon BORDER;
	//BOX
	public static GuiIcon BOX;
	//UISlot
	public static GuiIcon SLOT;
	//UIPanel
	public static GuiIcon PANEL;
	//UITooltip
	public static GuiIcon TOOLTIP;
	//UIWindow
	public static GuiIcon WINDOW;
	//UIProgressBar
	public static GuiIcon ARROW_EMPTY;
	public static GuiIcon ARROW_FILLED;
	//UISeparator
	//public static  GuiIcon SEPARATOR ;
	//UIButton
	public static GuiIcon BUTTON;
	public static GuiIcon BUTTON_HOVER;
	public static GuiIcon BUTTON_HOVER_PRESSED;
	public static GuiIcon BUTTON_DISABLED;
	//UICheckbox
	public static GuiIcon CHECKBOX_BG;
	public static GuiIcon CHECKBOX_BG_HOVER;
	public static GuiIcon CHECKBOX_BG_DISABLED;
	public static GuiIcon CHECKBOX;
	public static GuiIcon CHECKBOX_HOVER;
	public static GuiIcon CHECKBOX_DISABLED;
	//UIRadioButton
	public static GuiIcon RADIO_BG;
	public static GuiIcon RADIO_BG_HOVER;
	public static GuiIcon RADIO_BG_DISABLED;
	public static GuiIcon RADIO;
	public static GuiIcon RADIO_HOVER;
	public static GuiIcon RADIO_DISABLED;
	//UIScrollbar
	public static GuiIcon SCROLLBAR_BG;
	public static GuiIcon SCROLLBAR_DISABLED_BG;
	public static GuiIcon SCROLLBAR_HORIZONTAL;
	public static GuiIcon SCROLLBAR_HORIZONTAL_DISABLED;
	public static GuiIcon SCROLLBAR_VERTICAL;
	public static GuiIcon SCROLLBAR_VERTICAL_DISABLED;
	//UISelect
	public static GuiIcon SELECT_BG;
	public static GuiIcon SELECT_BG_HOVER;
	public static GuiIcon SELECT_BG_DISABLED;
	public static GuiIcon SELECT_BOX;
	public static GuiIcon SELECT_ARROW;
	//UISlider
	public static GuiIcon SLIDER;
	public static GuiIcon SLIDER_BG;
	//UITab window
	public static GuiIcon TAB_WINDOW_TOP;
	public static GuiIcon TAB_WINDOW_RIGHT;
	public static GuiIcon TAB_WINDOW_LEFT;
	public static GuiIcon TAB_WINDOW_BOTTOM;
	//UITab panel
	public static GuiIcon TAB_PANEL_TOP;
	public static GuiIcon TAB_PANEL_RIGHT;
	public static GuiIcon TAB_PANEL_LEFT;
	public static GuiIcon TAB_PANEL_BOTTOM;
	//UITextfield
	public static GuiIcon TEXTFIELD_BG;
	public static GuiIcon TEXTFIELD_BG_DISABLED;

	//ControlComponents
	public static GuiIcon CLOSE;
	public static GuiIcon MOVE;
	public static GuiIcon RESIZE;

	//VANILLA
	public static GuiIcon CROSSHAIR;
	public static GuiIcon HEART_EMPTY;
	public static GuiIcon HEART_FULL;
	public static GuiIcon HEART_HALF;
	public static GuiIcon ARMOR_EMPTY;
	public static GuiIcon ARMOR_FULL;
	public static GuiIcon ARMOR_HALF;
	public static GuiIcon HUNGER_EMPTY;
	public static GuiIcon HUNGER_FULL;
	public static GuiIcon HUNGER_HALF;
	//bars
	public static GuiIcon BAR_PINK_EMPTY;
	public static GuiIcon BAR_PINK;
	public static GuiIcon BAR_CYAN_EMPTY;
	public static GuiIcon BAR_CYAN;
	public static GuiIcon BAR_RED_EMPTY;
	public static GuiIcon BAR_RED;
	public static GuiIcon BAR_GREEN_EMPTY;
	public static GuiIcon BAR_GREEN;
	public static GuiIcon BAR_YELLOW_EMPTY;
	public static GuiIcon BAR_YELLOW;
	public static GuiIcon BAR_PURPLE_EMPTY;
	public static GuiIcon BAR_PURPLE;
	public static GuiIcon BAR_GRAY_EMPTY;
	public static GuiIcon BAR_GRAY;

	public static GuiIcon ACHIEVEMENT_BG;
	public static GuiIcon EXPERIENCE_ORB;

	public static void registerIcons(Atlas atlas)
	{
		FULL = new GuiIcon(0, 0, 1, 1);
		NONE = register(atlas, "blank");
		BORDER = register(atlas, "border");
		BOX = register(atlas, "box");
		SLOT = register(atlas, "slot");
		PANEL = register(atlas, "panel");
		TOOLTIP = register(atlas, "tooltip");
		WINDOW = register(atlas, "window");

		ARROW_EMPTY = register(atlas, "arrow_bg");
		ARROW_FILLED = register(atlas, "arrow");
		BUTTON = register(atlas, "button");
		BUTTON_HOVER = register(atlas, "button_hovered");
		BUTTON_HOVER_PRESSED = register(atlas, "button_pressed");
		BUTTON_DISABLED = register(atlas, "button_disabled");
		CHECKBOX_BG = register(atlas, "checkbox_bg");
		CHECKBOX_BG_HOVER = register(atlas, "checkbox_bg_hovered");
		CHECKBOX_BG_DISABLED = register(atlas, "checkbox_bg_disabled");
		CHECKBOX = register(atlas, "checkbox");
		CHECKBOX_HOVER = register(atlas, "checkbox_hovered");
		CHECKBOX_DISABLED = register(atlas, "checkbox_disabled");
		RADIO_BG = register(atlas, "radiobutton_bg");
		RADIO_BG_HOVER = register(atlas, "radiobutton_bg_hovered");
		RADIO_BG_DISABLED = register(atlas, "radiobutton_bg_disabled");
		RADIO = register(atlas, "radiobutton");
		RADIO_HOVER = register(atlas, "radiobutton_hovered");
		RADIO_DISABLED = register(atlas, "radiobutton_disabled");
		SCROLLBAR_BG = register(atlas, "scrollbar_bg");
		SCROLLBAR_DISABLED_BG = register(atlas, "scrollbar_bg_disabled");
		SCROLLBAR_HORIZONTAL = register(atlas, "scrollbar_horizontal");
		SCROLLBAR_HORIZONTAL_DISABLED = register(atlas, "scrollbar_horizontal");
		SCROLLBAR_VERTICAL = register(atlas, "scrollbar_vertical");
		SCROLLBAR_VERTICAL_DISABLED = register(atlas, "scrollbar_vertical_disabled");
		SELECT_BG = register(atlas, "select_bg");
		SELECT_BG_HOVER = register(atlas, "select_bg_hovered");
		SELECT_BG_DISABLED = register(atlas, "select_bg_disabled");
		SELECT_BOX = register(atlas, "select_box");
		SELECT_ARROW = register(atlas, "select_arrow");
		SLIDER = register(atlas, "slider");
		SLIDER_BG = register(atlas, "slider_bg");
		TEXTFIELD_BG = register(atlas, "textfield_bg");
		TEXTFIELD_BG_DISABLED = register(atlas, "textfield_disabled");

		CLOSE = new GuiIcon(VANILLAGUI_TEXTURE, 268, 30, 15, 15);
		MOVE = new GuiIcon(VANILLAGUI_TEXTURE, 268, 15, 15, 15);
		RESIZE = new GuiIcon(VANILLAGUI_TEXTURE, 268, 0, 15, 15);

		//UITab window
		TAB_WINDOW_TOP = register(atlas, "tab_window_top");
		TAB_WINDOW_RIGHT = register(atlas, "tab_window_right");
		TAB_WINDOW_LEFT = register(atlas, "tab_window_left");
		TAB_WINDOW_BOTTOM = register(atlas, "tab_window_bottom");
		//UITab panel
		TAB_PANEL_TOP = register(atlas, "tab_panel_top");
		TAB_PANEL_RIGHT = register(atlas, "tab_panel_right");
		TAB_PANEL_LEFT = register(atlas, "tab_panel_left");
		TAB_PANEL_BOTTOM = register(atlas, "tab_panel_bottom");

		//VANILLA
		CROSSHAIR = registerVanilla(atlas, "CROSSHAIR", "gui/icons.png", 0, 0, 16, 16);
		HEART_EMPTY = registerVanilla(atlas, "HEART_EMPTY", "gui/icons.png", 16, 0, 9, 9);
		HEART_FULL = registerVanilla(atlas, "HEART_FULL", "gui/icons.png", 52, 0, 9, 9);
		HEART_HALF = registerVanilla(atlas, "HEART_HALF", "gui/icons.png", 61, 0, 9, 9);
		ARMOR_EMPTY = registerVanilla(atlas, "ARMOR_EMPTY", "gui/icons.png", 16, 9, 9, 9);
		ARMOR_FULL = registerVanilla(atlas, "ARMOR_FULL", "gui/icons.png", 25, 9, 9, 9);
		ARMOR_HALF = registerVanilla(atlas, "ARMOR_HALF", "gui/icons.png", 34, 9, 9, 9);
		HUNGER_EMPTY = registerVanilla(atlas, "HUNGER_EMPTY", "gui/icons.png", 16, 27, 9, 9);
		HUNGER_FULL = registerVanilla(atlas, "HUNGER_FULL", "gui/icons.png", 52, 27, 9, 9);
		HUNGER_HALF = registerVanilla(atlas, "HUNGER_HALF", "gui/icons.png", 61, 27, 9, 9);
		//bars
		BAR_PINK_EMPTY = registerVanilla(atlas, "BAR_PINK_EMPTY", "gui/bars.png", 0, 0, 182, 5);
		BAR_PINK = registerVanilla(atlas, "BAR_PINK", "gui/bars.png", 0, 5, 182, 5);
		BAR_CYAN_EMPTY = registerVanilla(atlas, "BAR_CYAN_EMPTY", "gui/bars.png", 0, 10, 182, 5);
		BAR_CYAN = registerVanilla(atlas, "BAR_CYAN", "gui/bars.png", 0, 15, 182, 5);
		BAR_RED_EMPTY = registerVanilla(atlas, "BAR_RED_EMPTY", "gui/bars.png", 0, 20, 182, 5);
		BAR_RED = registerVanilla(atlas, "BAR_RED", "gui/bars.png", 0, 25, 182, 5);
		BAR_GREEN_EMPTY = registerVanilla(atlas, "BAR_GREEN_EMPTY", "gui/bars.png", 0, 30, 182, 5);
		BAR_GREEN = registerVanilla(atlas, "BAR_GREEN", "gui/bars.png", 0, 35, 182, 5);
		BAR_YELLOW_EMPTY = registerVanilla(atlas, "BAR_YELLOW_EMPTY", "gui/bars.png", 0, 40, 182, 5);
		BAR_YELLOW = registerVanilla(atlas, "BAR_YELLOW", "gui/bars.png", 0, 45, 182, 5);
		BAR_PURPLE_EMPTY = registerVanilla(atlas, "BAR_PURPLE_EMPTY", "gui/bars.png", 0, 50, 182, 5);
		BAR_PURPLE = registerVanilla(atlas, "BAR_PURPLE", "gui/bars.png", 0, 55, 182, 5);
		BAR_GRAY_EMPTY = registerVanilla(atlas, "BAR_GRAY_EMPTY", "gui/bars.png", 0, 60, 182, 5);
		BAR_GRAY = registerVanilla(atlas, "BAR_GRAY", "gui/bars.png", 0, 65, 182, 5);

		ACHIEVEMENT_BG = registerVanilla(atlas, "ACHIEVEMENT_BG", "gui/container/inventory.png", 141, 166, 24, 24);

		EXPERIENCE_ORB = registerVanilla(atlas, "EXPERIENCE_ORB", "entity/experience_orb.png", 4, 4, 8, 8);
	}

	private static GuiIcon register(Atlas atlas, String name)
	{
		return atlas.register(new ResourceLocation(EGO.modid, "textures/gui/widgets/" + name + ".png"));
	}

	private static GuiIcon registerVanilla(Atlas atlas, String name, String path, int x, int y, int w, int h)
	{
		return atlas.register(new ResourceLocation("ego:vanilla/" + name), new ResourceLocation("minecraft", "textures/" + path), x, y, w,
							  h);
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

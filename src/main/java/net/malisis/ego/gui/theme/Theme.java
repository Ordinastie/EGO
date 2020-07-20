package net.malisis.ego.gui.theme;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.malisis.ego.EGO;
import net.malisis.ego.atlas.Atlas;
import net.malisis.ego.gui.EGOGui;
import net.malisis.ego.gui.render.GuiIcon;
import net.malisis.ego.gui.render.IGuiRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Theme
{
	public static final List<Theme> THEMES = Lists.newArrayList();

	protected final Map<String, GuiIcon> ICON_REGISTRY = Maps.newHashMap();
	protected final Map<String, IGuiRenderer> RENDERER_REGISTRY = Maps.newHashMap();

	protected final String name;
	protected final String modId;
	protected final Atlas atlas;
	protected String prefix = "gui/widgets/";
	protected final IResourceManager resourceManager = Minecraft.getMinecraft()
																.getResourceManager();

	public Theme(String name, String modId, Atlas atlas)
	{
		this.name = name;
		this.modId = modId;
		this.atlas = atlas;

		THEMES.add(this);
	}

	public void setPrefix(String prefix)
	{
		if (prefix == null)
			prefix = "";
		this.prefix = prefix;
	}

	public String name()
	{
		return name;
	}

	public Atlas atlas()
	{
		return atlas;
	}

	public ResourceLocation getResourceLocation(String name)
	{
		return new ResourceLocation(modId, "textures/" + prefix + name + ".png");
	}

	public GuiIcon getIcon(String name)
	{
		GuiIcon icon = ICON_REGISTRY.get(name);
		if (icon != null)
			return icon;
		if (this != EGO.VANILLA_THEME)
			return EGO.VANILLA_THEME.getIcon(name);
		EGO.log.info("{} not found in icon registry.", name);
		return GuiIcon.NONE;
	}

	public void loadDefaultIcons()
	{
		atlas.addIconRegister(this::defaultIcons);
	}

	protected GuiIcon registerIcon(String name)
	{
		return registerIcon(name, getResourceLocation(name));
	}

	protected GuiIcon registerIcon(String name, ResourceLocation location)
	{
		try
		{
			if (this != EGO.VANILLA_THEME)
				resourceManager.getResource(location);
			GuiIcon icon = atlas.register(location);
			ICON_REGISTRY.put(name, icon);
		}
		catch (IOException exception)
		{
			EGO.log.debug("Theme {} : File not found ({})", this.name, location);
		}
		return getIcon(name);
	}

	protected GuiIcon registerDerivedIcon(String name, String path, int x, int y, int w, int h)
	{
		GuiIcon icon = atlas.register(new ResourceLocation(modId + ":vanilla/" + name),
									  new ResourceLocation("minecraft", "textures/" + path), x, y, w, h);
		ICON_REGISTRY.put(name, icon);
		return icon;
	}

	public void registerIcon(String name, GuiIcon icon)
	{
		ICON_REGISTRY.put(name, icon);
	}

	public void registerRenderer(String name, IGuiRenderer renderer)
	{
		RENDERER_REGISTRY.put(name, renderer);
	}

	public static GuiIcon icon(String name)
	{
		if (name == null)
			return null;
		return EGOGui.current()
					 .theme()
					 .getIcon(name);
	}

	protected void defaultIcons(Atlas atlas)
	{
		//white canvas
		GuiIcon.NONE = registerIcon("blank");
		//border with empty center
		registerIcon("border");

		//basic grey box
		registerIcon("box");
		registerIcon("slot");
		//UIPanel
		registerIcon("panel");
		//UITooltip
		registerIcon("tooltip");
		//UIWindow
		registerIcon("window");
		//UIProgressBar
		registerIcon("arrow_bg");
		registerIcon("arrow");
		//UIButton
		registerIcon("button");
		registerIcon("button_hovered");
		registerIcon("button_pressed");
		registerIcon("button_disabled");
		//UICheckbox
		registerIcon("checkbox_bg");
		registerIcon("checkbox_bg_hovered");
		registerIcon("checkbox_bg_disabled");
		registerIcon("checkbox");
		registerIcon("checkbox_hovered");
		registerIcon("checkbox_disabled");
		//UIRadioButton
		registerIcon("radiobutton_bg");
		registerIcon("radiobutton_bg_hovered");
		registerIcon("radiobutton_bg_disabled");
		registerIcon("radiobutton");
		registerIcon("radiobutton_hovered");
		registerIcon("radiobutton_disabled");
		//UIWindowScrollbar
		registerIcon("scrollbar_bg");
		registerIcon("scrollbar_bg_disabled");
		registerIcon("scrollbar_horizontal");
		registerIcon("scrollbar_horizontal");
		registerIcon("scrollbar_vertical");
		registerIcon("scrollbar_vertical_disabled");
		//UISelect
		registerIcon("select_bg");
		registerIcon("select_bg_hovered");
		registerIcon("select_bg_disabled");
		registerIcon("select_box");
		registerIcon("select_arrow");
		//UISlider
		registerIcon("slider");
		registerIcon("slider_bg");
		//UITextfield
		registerIcon("textfield_bg");
		registerIcon("textfield_bg_disabled");
		//UITab window
		registerIcon("tab_window_top");
		registerIcon("tab_window_right");
		registerIcon("tab_window_left");
		registerIcon("tab_window_bottom");
		//UITab panel
		registerIcon("tab_panel_top");
		registerIcon("tab_panel_right");
		registerIcon("tab_panel_left");
		registerIcon("tab_panel_bottom");
	}

	protected void defaultDerivedIcons(Atlas atlas)
	{
		//VANILLA
		registerDerivedIcon("crosshair", "gui/icons.png", 0, 0, 16, 16);
		registerDerivedIcon("heart_empty", "gui/icons.png", 16, 0, 9, 9);
		registerDerivedIcon("heart_full", "gui/icons.png", 52, 0, 9, 9);
		registerDerivedIcon("heart_half", "gui/icons.png", 61, 0, 9, 9);
		registerDerivedIcon("armor_empty", "gui/icons.png", 16, 9, 9, 9);
		registerDerivedIcon("armor_full", "gui/icons.png", 25, 9, 9, 9);
		registerDerivedIcon("armor_half", "gui/icons.png", 34, 9, 9, 9);
		registerDerivedIcon("hunger_empty", "gui/icons.png", 16, 27, 9, 9);
		registerDerivedIcon("hunger_full", "gui/icons.png", 52, 27, 9, 9);
		registerDerivedIcon("hunger_half", "gui/icons.png", 61, 27, 9, 9);
		//bars
		registerDerivedIcon("bar_pink_empty", "gui/bars.png", 0, 0, 182, 5);
		registerDerivedIcon("bar_pink", "gui/bars.png", 0, 5, 182, 5);
		registerDerivedIcon("bar_cyan_empty", "gui/bars.png", 0, 10, 182, 5);
		registerDerivedIcon("bar_cyan", "gui/bars.png", 0, 15, 182, 5);
		registerDerivedIcon("bar_red_empty", "gui/bars.png", 0, 20, 182, 5);
		registerDerivedIcon("bar_red", "gui/bars.png", 0, 25, 182, 5);
		registerDerivedIcon("bar_green_empty", "gui/bars.png", 0, 30, 182, 5);
		registerDerivedIcon("bar_green", "gui/bars.png", 0, 35, 182, 5);
		registerDerivedIcon("bar_yellow_empty", "gui/bars.png", 0, 40, 182, 5);
		registerDerivedIcon("bar_yellow", "gui/bars.png", 0, 45, 182, 5);
		registerDerivedIcon("bar_purple_empty", "gui/bars.png", 0, 50, 182, 5);
		registerDerivedIcon("bar_purple", "gui/bars.png", 0, 55, 182, 5);
		registerDerivedIcon("bar_gray_empty", "gui/bars.png", 0, 60, 182, 5);
		registerDerivedIcon("bar_gray", "gui/bars.png", 0, 65, 182, 5);

		registerDerivedIcon("achievement_bg", "gui/container/inventory.png", 141, 166, 24, 24);

		registerDerivedIcon("experience_orb", "entity/experience_orb.png", 4, 4, 8, 8);
	}
}

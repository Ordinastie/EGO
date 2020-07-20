package net.malisis.ego.gui.theme;

import net.malisis.ego.atlas.Atlas;
import net.malisis.ego.gui.render.GuiIcon;

public class VanillaTheme extends Theme
{
	private String prefix;

	public VanillaTheme(String modId, Atlas atlas)
	{
		super("Vanilla", modId, atlas);
		loadDefaultIcons();
	}

	@Override
	protected void defaultIcons(Atlas atlas)
	{
		super.defaultIcons(atlas);
		GuiIcon.NONE = getIcon("blank");
		GuiIcon.BORDER = getIcon("border");
	}
}

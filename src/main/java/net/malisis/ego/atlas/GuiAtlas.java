package net.malisis.ego.atlas;

import net.malisis.ego.font.FontOptions;
import net.malisis.ego.gui.EGOGui;
import net.malisis.ego.gui.component.container.UIContainer;
import net.malisis.ego.gui.component.decoration.UILabel;
import net.malisis.ego.gui.component.interaction.UIButton;
import net.malisis.ego.gui.component.interaction.UITextField;
import net.malisis.ego.gui.element.position.Position;
import net.malisis.ego.gui.element.position.Positions;
import net.malisis.ego.gui.element.size.Size;

public class GuiAtlas extends EGOGui
{
	protected Atlas atlas;
	protected UITextField search;
	protected AtlasComponent ac;

	public GuiAtlas(Atlas atlas)
	{
		this.atlas = atlas;
	}

	@Override
	public void construct()
	{
		ac = new AtlasComponent(atlas);

		FontOptions labelOptions = FontOptions.builder()
											  .color(0xFFFFFF)
											  .shadow()
											  .build();
		FontOptions valueOptions = FontOptions.builder()
											  .color(0x223399)
											  .when(ac::hasSelectedIcon)
											  .color(0x336622)
											  .build();

		UIContainer leftPanel = UIContainer.panel()
										   .size(c -> Size.of(120, 300))
										   .build();
		//label for size
		UILabel atlasLabel = UILabel.builder()
									.parent(leftPanel)
									.text("Atlas")
									.fontOptions(labelOptions)
									.build();
		UILabel atlasName = UILabel.builder()
								   .parent(leftPanel)
								   .rightAligned()
								   .below(atlasLabel)
								   .text(atlas.texture()
											  .getResourceLocation()
											  .toString())
								   .fontOptions(valueOptions)
								   .fitSize(() -> leftPanel.innerSize()
														   .width())
								   .build();
		UILabel sizeValue = UILabel.builder()
								   .parent(leftPanel)
								   .rightAligned()
								   .below(atlasName)
								   .text(atlas.texture()
											  .width() + "x" + atlas.texture()
																	.height())
								   .fontOptions(valueOptions)
								   .build();

		//label for mouse UV
		UILabel mouseLabel = UILabel.builder()
									.parent(leftPanel)
									.below(sizeValue)
									.text("Mouse")
									.fontOptions(labelOptions)
									.build();

		UILabel mousePos = UILabel.builder()
								  .parent(leftPanel)
								  .rightAligned()
								  .below(mouseLabel)
								  .text(ac::hoveredPosition)
								  .fontOptions(valueOptions)
								  .build();

		UILabel mouseUV = UILabel.builder()
								 .parent(leftPanel)
								 .rightAligned()
								 .below(mousePos)
								 .text(ac::getHoveredUV)
								 .fontOptions(valueOptions)
								 .build();

		//current hovered icon
		UILabel iconLabel = UILabel.builder()
								   .parent(leftPanel)
								   .below(mouseUV)
								   .text("Icon")
								   .fontOptions(labelOptions)
								   .build();

		UILabel iconName = UILabel.builder()
								  .parent(leftPanel)
								  .rightAligned()
								  .below(iconLabel)
								  .height(9)
								  .text(ac::iconName)
								  .fontOptions(valueOptions)
								  .fitSize(() -> leftPanel.innerSize()
														  .width())
								  .build();

		UILabel iconSize = UILabel.builder()
								  .parent(leftPanel)
								  .rightAligned()
								  .below(iconName)
								  .text(ac::iconSize)
								  .fontOptions(valueOptions)
								  .build();

		UILabel iconUs = UILabel.builder()
								.parent(leftPanel)
								.rightAligned()
								.below(iconSize)
								.text(ac::iconUs)
								.fontOptions(valueOptions)
								.build();

		UILabel iconVs = UILabel.builder()
								.parent(leftPanel)
								.rightAligned()
								.below(iconUs)
								.text(ac::iconVs)
								.fontOptions(valueOptions)
								.build();

		UILabel iconPixels = UILabel.builder()
									.parent(leftPanel)
									.rightAligned()
									.below(iconVs)
									.text(ac::iconPixels)
									.fontOptions(valueOptions)
									.build();

		//search textbox
		UILabel filterLabel = UILabel.builder()
									 .parent(leftPanel)
									 .below(iconPixels, 10)
									 .text("Filter")
									 .fontOptions(labelOptions)
									 .build();

		search = new UITextField(false);
		search.setPosition(Position.of(Positions.centered(search, 0), Positions.below(filterLabel, 0)));
		search.setSize(Size.of(100, 12));
		leftPanel.add(search);

		UIButton reload = UIButton.builder()
								  .parent(leftPanel)
								  .below(search, 20)
								  .centered()
								  .size(Size.of(100, 20))
								  .text("Reload atlas")
								  .onClick(atlas::init)
								  .build();

		ac.setPosition(Position.rightOf(ac, leftPanel, 5));
		ac.setSize(Size.of(275, 300));

		UIContainer container = UIContainer.builder()
										   .middleCenter()
										   .size(Size.of(400, 300))
										   .build();
		container.add(leftPanel, ac);

		addToScreen(container);
	}
}

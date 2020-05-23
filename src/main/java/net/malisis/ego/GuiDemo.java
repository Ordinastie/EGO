package net.malisis.ego;

import static net.malisis.ego.gui.element.size.Sizes.heightRelativeTo;
import static net.malisis.ego.gui.element.size.Sizes.parentWidth;
import static net.malisis.ego.gui.element.size.Sizes.widthRelativeTo;

import com.google.common.base.Converter;
import com.google.common.collect.ImmutableList;
import net.malisis.ego.font.FontOptions;
import net.malisis.ego.gui.ComponentPosition;
import net.malisis.ego.gui.EGOGui;
import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.component.container.UIContainer;
import net.malisis.ego.gui.component.container.UIListContainer;
import net.malisis.ego.gui.component.container.UITabGroup;
import net.malisis.ego.gui.component.control.UIMoveHandle;
import net.malisis.ego.gui.component.control.UIResizeHandle;
import net.malisis.ego.gui.component.decoration.UIImage;
import net.malisis.ego.gui.component.decoration.UILabel;
import net.malisis.ego.gui.component.decoration.UIProgressBar;
import net.malisis.ego.gui.component.interaction.UIButton;
import net.malisis.ego.gui.component.interaction.UICheckBox;
import net.malisis.ego.gui.component.interaction.UIPasswordField;
import net.malisis.ego.gui.component.interaction.UIRadioButton;
import net.malisis.ego.gui.component.interaction.UISelect;
import net.malisis.ego.gui.component.interaction.UISlider;
import net.malisis.ego.gui.component.interaction.UISlider.UISliderBuilder;
import net.malisis.ego.gui.component.interaction.UITab;
import net.malisis.ego.gui.component.interaction.UITextField;
import net.malisis.ego.gui.component.layout.GridLayout;
import net.malisis.ego.gui.component.scrolling.UIScrollBar;
import net.malisis.ego.gui.component.scrolling.UISlimScrollbar;
import net.malisis.ego.gui.element.position.Position;
import net.malisis.ego.gui.element.size.Size;
import net.malisis.ego.gui.render.GuiIcon;
import net.malisis.ego.gui.render.GuiRenderer;
import net.malisis.ego.gui.render.shape.GuiShape;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class show most of the available components for GUI and how to use them.<br>
 */
public class GuiDemo extends EGOGui
{
	//private static MalisisFont fontMC = MalisisFont.minecraftFont;
	//private static MalisisFont fontBS = new MalisisFont(new ResourceLocation(MalisisDemos.modid + ":fonts/BrushScriptStd.otf"));
	//private static MalisisFont fontH = new MalisisFont(new ResourceLocation(MalisisDemos.modid + ":fonts/HoboStd.otf"));
	private UIContainer panel;
	private UITab tabSlider;
	private UIProgressBar bar;
	private UISelect<String> select;
	private int selectSize = 100;
	private int currentSize = 100;

	public GuiDemo(/*MalisisInventoryContainer inventoryContainer*/)
	{
		//setInventoryContainer(inventoryContainer);
		//guiscreenBackground = false;
	}

	private UIComponent debug()
	{
		UIContainer panel = UIContainer.panel()
									   .middleCenter()
									   .size(250, 150)
									   .withControl(UIMoveHandle.builder()
																.position(0, 0)
																.fillWidth()
																.height(15)
																.build())
									   .build();

		UILabel title = UILabel.builder()
							   .parent(panel)
							   .middleCenter()
							   .text("Panel Title")
							   .textColor(0xFFFFFF)
							   .shadow()
							   .scale(1.3F)
							   .when(UILabel::isHovered)
							   .textColor(0x8396FF)
							   .build();

		UIButton button = UIButton.builder()
								  .parent(panel)
								  .centered()
								  .below(title)
								  .text("Button")
								  .textColor(TextFormatting.DARK_GREEN)
								  .when(UIComponent::isHovered)
								  .textColor(TextFormatting.GREEN)
								  .build();

		return panel;
	}

	@Override
	public void construct()
	{
		boolean debug = true;
		if (debug)
		{
			addToScreen(debug());
			return;
		}

		//allow contents to be drawn outside of the window's borders
		UIContainer window = UIContainer.window()
										.size(400, 240)
										.noClipContent()
										.onLeftClick(e -> {
											//EGO.message("Clicked");
											return false;
										})
										.build();

		//get the first parent
		UIContainer panel1 = panel1();
		//get the second parent
		UIContainer textPanel = textPanel();
		//get the slider demo tab
		UIContainer sliderPanel = sliderPanel();
		//get the UIList panel
		//UIContainer listPanel = listPanel();

		//create a panel to hold the containers
		panel = UIContainer.panel()
						   .parentWidth()
						   .height(140)
						   .build();

		//create the tabs for the containers
		UIImage img = UIImage.builder()
							 .itemStack(new ItemStack(Blocks.END_BRICKS))
							 .build();
		UITab tab1 = new UITab(img);
		tab1.size()
			.width();
		UITab tab2 = new UITab("Inputs");
		tab2.setColor(0xCCCCFF);
		tab2.setActive(true);
		tabSlider = new UITab("Sliders");
		//UITab tab3 = new UITab("Lists");

		//create the group containing the tabs and their corresponding containers
		UITabGroup tabGroup = new UITabGroup(ComponentPosition.TOP, UITabGroup.Type.PANEL);
		tabGroup.addTab(tab1, panel1);
		tabGroup.addTab(tab2, textPanel);
		tabGroup.addTab(tabSlider, sliderPanel);
		//tabGroup.addTab(tab3, listPanel);

		tabGroup.setActiveTab(tab1);
		tabGroup.setSpacing(0);
		tabGroup.attachTo(panel, true);

		//add all the elements to the window
		window.add(tabGroup);
		window.add(panel);

		//add the player inventory (default position is bottom center)
		//		UIPlayerInventory playerInv = new UIPlayerInventory(inventoryContainer.getPlayerInventory());
		//		window.add(playerInv);
		//add a close icon for the player inventory
		//Note : it won't actively remove the player inventory from the parent, it will just stop displaying it
		//new UICloseHandle(this, playerInv);

		//add handles to the window
		new UIResizeHandle(window);
		//new UICloseHandle(this, window);

		//add the window to the screen
		addToScreen(window);
	}

	private UIContainer panel1()
	{
		UIContainer tabCont1 = UIContainer.builder()
										  .middleCenter()
										  .name("Panel 1")
										  .build();

		UIImage img = UIImage.builder()
							 .parent(tabCont1)
							 .icon(GuiIcon.from(Items.DIAMOND_HORSE_ARMOR))
							 .build();
		//Colored Label
		UILabel label1 = UILabel.builder()
								.parent(tabCont1)
								.text("Colored label!")
								.textColor(TextFormatting.YELLOW)
								.underline()
								.position(l -> Position.rightOf(l, img, 4))
								.build();

		//progress bar
		bar = new UIProgressBar(Size.of(16, 16), GuiIcon.from(Items.IRON_PICKAXE), GuiIcon.from(Items.DIAMOND_PICKAXE));
		bar.setPosition(Position.below(bar, img, 2));
		bar.setVertical();

		UILabel.builder()
			   .parent(tabCont1)
			   .text("Smaller label!")
			   .position(l -> Position.rightOf(l, bar, 4))
			   .textColor(0x660066)
			   .scale(2F / 3F)
			   .tooltip("Testing label tooltip.")
			   .build();

		//CheckBox
		UICheckBox cb = UICheckBox.builder()
								  .parent(tabCont1)
								  .text("CheckBox with label")
								  .position(c -> Position.below(c, bar, 2))
								  .tooltip(TextFormatting.AQUA + "with delayed a tooltip!")
								  .build();

		//RadioButton with custom fonts
		UIRadioButton rbMC = UIRadioButton.builder("newRb")
										  .parent(tabCont1)
										  .text("MF")
										  .position(rb -> Position.below(rb, cb, 2))
										  .select()
										  .build();

		UIRadioButton rbBS = UIRadioButton.builder("newRb")
										  .parent(tabCont1)
										  .text("BS")
										  .position(rb -> Position.rightOf(rb, rbMC, 5))
										  .select()
										  .build();

		UIRadioButton.builder("newRb")
					 .parent(tabCont1)
					 .text("H")
					 .position(rb -> Position.rightOf(rb, rbBS, 5))
					 .select()
					 .build();

		//Select
		select = UISelect.builder(
				Arrays.asList("Option 1", "Option 2", "Very ultra longer option 3", "Shorty", "Moar options", "Even more", "Even Steven",
							  "And a potato too"))
						 .parent(tabCont1)
						 .below(rbMC, 5)
						 .select("Option 2")
						 .build();
		//select.maxDisplayedOptions(5);

		//uiselect size change button
		UIButton sizeButton = UIButton.builder()
									  .parent(tabCont1)
									  .text("<-")
									  .position(b -> Position.rightOf(b, select, 5))
									  .size(12, 12)
									  .onClick(() -> {
										  currentSize += 20;
										  if (currentSize > 190)
											  currentSize = selectSize;
										  select.setSize(Size.of(currentSize, 12));
									  })
									  .build();

		List<Item> items = ForgeRegistries.ITEMS.getValues()
												.stream()
												.sorted(Comparator.comparing(i -> new ItemStack(i).getDisplayName()))
												.collect(Collectors.toList());
		UISelect<Item> sel = UISelect.builder(items)
									 .parent(tabCont1)
									 .rightOf(sizeButton, 5)
									 .layout(c -> GridLayout.builder(c)
															.columns(10)
															.cellSize(Size.of(19, 19))
															.spacing(1, 1)
															.build())
									 .size(140, 20)
									 .withOptions(SelectItemComponent::new)
									 .optionsSize(oc -> Size.of(205, 140))
									 .build();

		//3 Buttons
		UIButton btnHorizontal = UIButton.builder()
										 .parent(tabCont1)
										 .text("Horizontal")
										 .position(Position::bottomCenter)
										 .size(90, 20)
										 .build();

		UIButton.builder()
				.parent(tabCont1)
				.text("O")
				.position(b -> Position.leftOf(b, btnHorizontal, 1))
				.size(10, 10)
				.build();

		UIButton.builder()
				.parent(tabCont1)
				.text("O")
				.position(b -> Position.rightOf(b, btnHorizontal, 1))
				.size(10, 10)
				.build();

		//Add all elements
		//	tabCont1.add(bar);
		//tabCont1.add(select);

		//Create 5 buttons with itemStack as images
		UIButton lastBtn = null;
		for (Item item : new Item[] { Items.COOKED_PORKCHOP,
									  Items.COOKED_BEEF,
									  Items.COOKED_MUTTON,
									  Items.COOKED_CHICKEN,
									  Item.getItemFromBlock(Blocks.GLASS_PANE) })
		{
			UIContainer cont = UIContainer.builder()
										  .size(Size::sizeOfContent)
										  .build();
			UIImage i = UIImage.builder()
							   .parent(cont)
							   .item(item)
							   .build();

			UILabel.builder()
				   .parent(cont)
				   .text(item.getTranslationKey() + ".name")
				   .position(l -> Position.rightOf(l, i, 2))
				   .textColor(0xFFFFFF)
				   .shadow()
				   .scale(2 / 3F)
				   .build();

			lastBtn = UIButton.builder()
							  //.parent(tabCont1)
							  .content(cont)
							  //.position(b -> lastBtn == null ? Position.topRight(b) : Position.below(b, lastBtn, 1))
							  .build();
		}

		return tabCont1;
	}

	private UIContainer textPanel()
	{
		UIContainer textTabCont = UIContainer.builder()
											 .name("Text tab")
											 .build();

		//Textfield
		UITextField tf = new UITextField("This is a textfield. You can type in it.");
		tf.setSize(Size.of(parentWidth(tf, 0.5F, -5), 14));
		tf.setPosition(Position.topLeft(tf));
		//tf.setOptions(0x660000, 0xFFCCCC, 0x770000, 0xFF0000, false);

		//Password
		UILabel pwdLabel = UILabel.builder()
								  .parent(textTabCont)
								  .text("Password :")
								  .position(l -> Position.below(l, tf, 5))
								  .build();
		UIPasswordField pwd = new UIPasswordField();
		pwd.setPosition(Position.rightOf(pwd, pwdLabel, 4));
		pwd.setSize(Size.of(() -> tf.size()
									.width() - pwdLabel.size()
													   .width() - 4, 14));
		pwd.setAutoSelectOnFocus(true);

		//Multiline Textfield with FontRendererOptions
		FontOptions fontOptions = FontOptions.builder()
											 .scale(2F / 3F)
											 .color(0x006600)
											 .build();
		//		UITextArea mltf = new UITextArea();
		//		mltf.setPosition(Position.below(mltf, pwd, 5));
		//		mltf.setSize(Size.of(Sizes.relativeWidth(mltf, 0.5f, -2), 50));
		//
		//		mltf.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras quis semper mi. Pellentesque dapibus diam
		//		egestas orci vulputate, a tempor ex hendrerit. Nullam tristique lacinia quam, a dapibus leo gravida eu. Donec placerat,
		//		turpis ut egestas dignissim, sem nibh tincidunt neque, eu facilisis massa felis eu nisl. Aenean pellentesque sed nunc et
		//		ultrices. Aenean facilisis convallis mauris in mollis. In porta hendrerit tellus id vehicula. Sed non interdum eros, vel
		//		condimentum diam. Sed vestibulum tincidunt velit, ac laoreet metus blandit quis. Aliquam sit amet ullamcorper velit. In
		//		tristique viverra imperdiet. Mauris facilisis ac leo non molestie.\r\n"
		//				+ "\r\n"
		//				+ "Phasellus orci metus, bibendum in molestie eu, interdum lacinia nulla. Nulla facilisi. Duis sagittis suscipit
		//				est vitae eleifend. Morbi bibendum tortor nec tincidunt pharetra. Vivamus tortor tortor, egestas sed condimentum
		//				ac, tristique non risus. Curabitur magna metus, porta sit amet dictum in, vulputate a dolor. Phasellus viverra
		//				euismod tortor, porta ultrices metus imperdiet a. Nulla pellentesque ipsum quis eleifend blandit. Aenean neque
		//				nulla, rhoncus et vestibulum eu, feugiat quis erat. Class aptent taciti sociosqu ad litora torquent per conubia
		//				nostra, per inceptos himenaeos. Suspendisse lacus justo, porttitor aliquam tellus eu, commodo tristique leo.
		//				Suspendisse scelerisque blandit nisl at malesuada. Proin ut tincidunt augue. Phasellus vel nisl sapien.\r\n"
		//				+ "\r"
		//				+ "Sed ut lacinia tellus. Nam arcu ligula, accumsan id lorem id, dapibus bibendum tortor. Cras eleifend varius
		//				est, eget eleifend est commodo at. Vivamus sapien purus, faucibus ac urna id, scelerisque sagittis elit. Curabitur
		//				commodo elit nec diam vulputate finibus vitae porttitor magna. Nullam nec feugiat dolor. Pellentesque malesuada
		//				dolor arcu, ut sagittis mi mattis eu. Vivamus et tortor non nulla venenatis hendrerit nec faucibus quam. Aliquam
		//				laoreet leo in risus tempus placerat. In lobortis nulla id enim semper posuere a et libero. Nullam sit amet sapien
		//				commodo, egestas nisi eu, viverra nulla. Cras ac vulputate tellus, nec auctor elit.\r\n"
		//				+ "\n"
		//				+ "In commodo finibus urna, eu consectetur quam commodo dapibus. Pellentesque metus ligula, ullamcorper non lorem
		//				a, dapibus elementum quam. Praesent iaculis pellentesque dui eget pellentesque. Nunc vel varius dui. Aliquam sit
		//				amet ex feugiat, aliquet ipsum nec, sollicitudin dolor. Ut ac rhoncus enim. Quisque maximus diam nec neque
		//				placerat, euismod blandit purus congue. Integer finibus tellus ligula, eget pretium magna luctus vel. Pellentesque
		//				gravida pretium nisl sit amet fermentum. Quisque odio nunc, tristique vitae pretium ut, imperdiet a nunc. Sed eu
		//				purus ultricies, tincidunt sapien et, condimentum nunc. Duis luctus augue ac congue luctus. Integer ut commodo
		//				turpis, vitae hendrerit quam. Vivamus vulputate efficitur est nec dignissim. Praesent convallis posuere lacus ut
		//				suscipit. Aliquam at odio viverra, cursus nulla eget, maximus purus.\r\n"
		//				+ "\r\n"
		//				+ "Donec convallis tortor in pretium hendrerit. Maecenas mollis ullamcorper sapien, rhoncus pretium nibh
		//				condimentum ut. Phasellus tincidunt aliquet ligula in blandit. Nunc ornare vel ligula eu vulputate. Vestibulum
		//				ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Suspendisse vitae ultricies nunc.
		//				Morbi lorem purus, tempor eget magna at, placerat posuere massa. Donec hendrerit risus a pharetra bibendum. ");
		//		//mltf.setText("Some §5colored test");
		//		mltf.setFontOptions(fontOptions);
		//		mltf.getScrollbar().setAutoHide(true);
		//		new UIResizeHandle(mltf);

		//set the textfield size based on the multi line textfield
		//tf.setSize(Size.of(Sizes.widthRelativeTo(mltf, 1.0F, 0), 12));

		//Multiline label
		UILabel ipsum = UILabel.builder()
							   .parent(textTabCont)
							   .position(Position::topRight)
							   .parentWidth(0.5F, -5)
							   .parentHeight()
							   .text(TextFormatting.UNDERLINE + "Contrairement à une opinion répandue, " + TextFormatting.BOLD
											 + "le Lorem Ipsum n'est pas simplement du texte aléatoire" + TextFormatting.RESET
											 + ". Il trouve ses racines dans une oeuvre de la littérature latine classique"
											 + TextFormatting.AQUA + " datant de 45 av. J.-C., le rendant" + TextFormatting.RESET
											 + " vieux de 2000 ans. " + TextFormatting.BLUE + "Un professeur du " + TextFormatting.RESET
											 + "Hampden-Sydney College" + TextFormatting.BLUE + ", en Virginie, s'est intéressé"
											 + TextFormatting.RESET + " à un des mots latins les plus obscurs, " + TextFormatting.UNDERLINE
											 + TextFormatting.DARK_RED + "consectetur" + TextFormatting.RESET
											 + ", extrait d'un passage du Lorem Ipsum, et en étudiant tous les usages de ce mot dans la "
											 + "littérature "
											 + "classique, découvrit la source incontestable du Lorem Ipsum. Il provient en fait des "
											 + "sections 1.10.32 et "
											 + "1.10.33 du \"De Finibus Bonorum et Malorum\" (Des Suprêmes Biens et des Suprêmes Maux) de "
											 + "Cicéron. Cet "
											 + "ouvrage, très populaire pendant la Renaissance, est un traité sur la théorie de l'éthique."
											 + " Les premières "
											 + "lignes du Lorem Ipsum, \"Lorem ipsum dolor sit amet...\", proviennent de la section 1.10"
											 + ".32")
							   .scale(2F / 3F)
							   .textColor(0x338899)
							   .build();
		new UISlimScrollbar(ipsum, UIScrollBar.Type.VERTICAL);

		//Add all elements

		textTabCont.add(tf);
		textTabCont.add(pwd);
		//textTabCont.add(mltf);

		//Add the block's inventory's slots directly into the parent
		//		UIInventory invCont = new UIInventory("Block's inventory", inventoryContainer.getInventory(0));
		//		invCont.setPosition(Position.below(invCont, tf, 10));

		return textTabCont;
	}

	UISlider<Integer> sliderRed;
	UISlider<Integer> sliderGreen;
	UISlider<Integer> sliderBlue;

	private UIContainer sliderPanel()
	{
		UIContainer sliderPanel = UIContainer.builder()
											 .name("Slider Tab")
											 .build();

		//Sliders with event caught to change the panel background color
		Converter<Float, Integer> colorConv = Converter.from(f -> (int) (f * 255), i -> (float) i / 255);
		UISliderBuilder<Integer> builder = UISlider.builder(colorConv)
												   .size(150, 12)
												   .value(255)
												   .scrollStep(1 / 255F)
												   .onChange(this::calculateColor);
		sliderRed = builder.text("{slider.red} {value}")
						   .build();

		sliderGreen = builder.text("{slider.green} {value}")
							 .position(s -> Position.below(s, sliderRed, 2))
							 .build();

		sliderBlue = builder.text("{slider.green} {value}")
							.position(s -> Position.below(s, sliderGreen, 2))
							.build();

		UILabel colorLabel = UILabel.builder()
									.parent(sliderPanel)
									.text("Color : {COLOR}")
									.position(l -> Position.rightOf(l, sliderRed, 2))
									//.bind("COLOR", tabSlider::getColor)
									.build();

		UIButton.builder()
				.parent(sliderPanel)
				.text("Invert")
				.position(b -> Position.rightOf(b, sliderGreen, 2))
				.size(b -> Size.of(widthRelativeTo(colorLabel, 1.0F, 0), heightRelativeTo(sliderBlue, 1.0F, 0)))
				.onClick(() -> {
					sliderRed.setValue(255 - sliderRed.getValue());
					sliderGreen.setValue(255 - sliderGreen.getValue());
					sliderBlue.setValue(255 - sliderBlue.getValue());
				})
				.build();

		//Slider with custom values with days of the week
		Converter<Float, DayOfWeek> dayConv = Converter.from(f -> DayOfWeek.values()[Math.round(f * 6)], d -> (float) d.ordinal() / 6);
		UISlider<DayOfWeek> sliderDay = UISlider.builder(dayConv)
												.position(Position::topCenter)
												.size(240, 30)
												.value(LocalDate.now()
																.getDayOfWeek())
												.scrollStep(1 / 6F)
												.build();

		sliderPanel.add(sliderRed);
		sliderPanel.add(sliderGreen);
		sliderPanel.add(sliderBlue);
		sliderPanel.add(sliderDay);

		return sliderPanel;
	}

	private UIContainer listPanel()
	{
		List<Item> items = ImmutableList.of(Items.APPLE, Items.BED, Items.LEAD, Items.SNOWBALL, Items.ARROW, Items.WHEAT,
											Items.TNT_MINECART, Items.WATER_BUCKET, Items.COOKED_MUTTON);

		UIListContainer<Item> itemList = new UIListContainer<>();
		itemList.setSize(Size.of(parentWidth(itemList, 1.0F, 0), 150));
		itemList.setComponentFactory((list, item) -> {
			return new UIContainer()
			{
				{
					ItemStack is = new ItemStack(item);
					setName(is.getDisplayName());
					setSize(Size.of(parentWidth(this, 1.0F, 0), 20));
					setBackground(GuiShape.builder(this)
										  .color(0xFFFFFF)
										  .border(1, 0x6666DD)
										  .build());

					UIImage img = new UIImage(is);
					img.setPosition(Position.middleCenter(this));
					add(img);

					UILabel.builder()
						   .parent(this)
						   .text(is.getDisplayName() + ".name")
						   .rightOf(img, 4)
						   .middleAlignedTo(img)
						   .build();

					UIButton.builder()
							.parent(this)
							.text("1")
							.position(Position::topRight)
							.size(15, 10)
							.data(1)
							.build();

					int stackSize = is.getMaxStackSize();
					if (stackSize != 1)
					{
						UIButton.builder()
								.parent(this)
								.text("" + stackSize)
								.position(Position::bottomRight)
								.size(15, 10)
								.data(stackSize)
								.build();
					}

				}
			};
		});

		itemList.setElementSpacing(2);
		itemList.setElements(items);

		return itemList;
	}

	public void calculateColor(int newValue)
	{
		if (sliderRed == null || sliderGreen == null || sliderBlue == null)
			return;
		//get the different values
		int r = sliderRed.getValue();
		int g = sliderGreen.getValue();
		int b = sliderBlue.getValue();
		int color = r << 16 | g << 8 | b;
		if (tabSlider != null)
			tabSlider.setColor(color);
	}

	@Override
	public void updateScreen()
	{
		if (bar == null)
			return;
		float t = (System.currentTimeMillis() % 2000) / 2000f;
		bar.setProgress(t);
	}

	public static class SelectItemComponent extends UIComponent
	{
		private Item item;
		private UISelect<Item> select;
		private final GuiShape btnBg;
		private UIImage img;
		private UILabel lbl;

		public SelectItemComponent(UISelect<Item> select, Item item)
		{
			this.select = select;
			this.item = item;

			String name = new ItemStack(item).getDisplayName();

			setSize(Size.of(19, 19));

			btnBg = GuiShape.builder(this)
							.icon(this, GuiIcon.BUTTON, GuiIcon.BUTTON_HOVER, null)
							.border(2)
							.build();

			img = UIImage.builder()
						 .parent(this)
						 .middleLeft(2, 0)
						 .size(16, 16)
						 .item(item)
						 .build();

			lbl = UILabel.builder()
						 .parent(this)
						 .rightOf(img, 2)
						 .middleAlignedTo(img)
						 .text(name)
						 .textColor(TextFormatting.WHITE)
						 .shadow()
						 .build();
			setTooltip(name);

			setBackground(this::background);
			setForeground(this::foreground);
		}

		private void background(GuiRenderer renderer)
		{
			if (select.isTop(this))
				return;

			btnBg.render(renderer);
		}

		private void foreground(GuiRenderer renderer)
		{
			img.render(renderer);
			if (select.isTop(this))
				lbl.render(renderer);
		}
	}
}
package net.malisis.ego;

import static net.malisis.ego.gui.element.position.Positions.bottomAligned;
import static net.malisis.ego.gui.element.position.Positions.centered;
import static net.malisis.ego.gui.element.position.Positions.middleAligned;
import static net.malisis.ego.gui.element.position.Positions.middleAlignedTo;
import static net.malisis.ego.gui.element.position.Positions.rightOf;
import static net.malisis.ego.gui.element.position.Positions.topAligned;
import static net.malisis.ego.gui.element.size.Sizes.heightRelativeTo;
import static net.malisis.ego.gui.element.size.Sizes.parentHeight;
import static net.malisis.ego.gui.element.size.Sizes.parentWidth;
import static net.malisis.ego.gui.element.size.Sizes.widthRelativeTo;

import com.google.common.base.Converter;
import com.google.common.collect.ImmutableList;
import net.malisis.ego.font.FontOptions;
import net.malisis.ego.gui.ComponentPosition;
import net.malisis.ego.gui.MalisisGui;
import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.component.container.UIContainer;
import net.malisis.ego.gui.component.container.UIListContainer;
import net.malisis.ego.gui.component.container.UITabGroup;
import net.malisis.ego.gui.component.control.UIMoveHandle;
import net.malisis.ego.gui.component.control.UIResizeHandle;
import net.malisis.ego.gui.component.decoration.UIImage;
import net.malisis.ego.gui.component.decoration.UILabel;
import net.malisis.ego.gui.component.decoration.UIProgressBar;
import net.malisis.ego.gui.component.decoration.UITooltip;
import net.malisis.ego.gui.component.interaction.UIButton;
import net.malisis.ego.gui.component.interaction.UICheckBox;
import net.malisis.ego.gui.component.interaction.UIPasswordField;
import net.malisis.ego.gui.component.interaction.UIRadioButton;
import net.malisis.ego.gui.component.interaction.UISelect;
import net.malisis.ego.gui.component.interaction.UISlider;
import net.malisis.ego.gui.component.interaction.UITab;
import net.malisis.ego.gui.component.interaction.UITextField;
import net.malisis.ego.gui.component.scrolling.UIScrollBar;
import net.malisis.ego.gui.component.scrolling.UISlimScrollbar;
import net.malisis.ego.gui.element.position.Position;
import net.malisis.ego.gui.element.size.Size;
import net.malisis.ego.gui.render.GuiIcon;
import net.malisis.ego.gui.render.shape.GuiShape;
import net.malisis.ego.gui.text.GuiText;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * This class show most of the available components for GUI and how to use them.<br>
 */
public class GuiDemo extends MalisisGui
{
	//private static MalisisFont fontMC = MalisisFont.minecraftFont;
	//private static MalisisFont fontBS = new MalisisFont(new ResourceLocation(MalisisDemos.modid + ":fonts/BrushScriptStd.otf"));
	//private static MalisisFont fontH = new MalisisFont(new ResourceLocation(MalisisDemos.modid + ":fonts/HoboStd.otf"));
	private UIContainer panel;
	private UITab tabSlider;
	private UIProgressBar bar;
	private UIButton btnL, btnR, btnHorizontal;
	private UIRadioButton rbMC, rbBS, rbH;
	private UICheckBox cb;
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
		UIContainer window = UIContainer.window();
		window.setSize(Size.of(400, 210));

		//RadioButton with custom fonts
		UIButton button = new UIButton();
		GuiText.builder().bind("zindex", button::getZIndex);
		button.onClick(() -> panel.setZIndex(panel.getZIndex() + (GuiScreen.isCtrlKeyDown() ? -25 : 25)));

		cb = new UICheckBox("CheckBox with label");
		cb.setPosition(Position.of(centered(cb, 0), middleAligned(cb, -10)));
		cb.setTooltip(new UITooltip(TextFormatting.AQUA + "with delayed a tooltip!", 5));

		window.add(cb);
		return window;
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

		UIContainer window = UIContainer.window();
		window.setSize(Size.of(400, 240));
		//allow contents to be drawn outside of the window's borders
		window.setClipContent(false);

		//get the first parent
		UIContainer panel1 = panel1();
		//get the second parent
		UIContainer textPanel = textPanel();
		//get the slider demo tab
		UIContainer sliderPanel = sliderPanel();
		//get the UIList panel
		//UIContainer listPanel = listPanel();

		//create a panel to hold the containers
		panel = UIContainer.panel();
		panel.setSize(Size.of(parentWidth(panel, 1.0F, 0), 140));

		//create the tabs for the containers
		UIImage img = new UIImage(new ItemStack(Blocks.END_BRICKS));
		UITab tab1 = new UITab(img);
		tab1.size().width();
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
		new UIMoveHandle(window);
		new UIResizeHandle(window);
		//new UICloseHandle(this, window);

		//add the window to the screen
		addToScreen(window);
		addDebug("Counter", () -> MalisisGui.counter + " (" + MalisisGui.xCounter + " / " + MalisisGui.xTotal + ")");

	}

	private UIContainer panel1()
	{
		UIImage img = new UIImage(GuiIcon.from(Items.DIAMOND_HORSE_ARMOR));
		//Colored Label
		UILabel label1 = new UILabel(TextFormatting.UNDERLINE.toString() + TextFormatting.YELLOW + "Colored label!");
		label1.setPosition(Position.rightOf(label1, img, 4));

		//progress bar
		bar = new UIProgressBar(Size.of(16, 16), GuiIcon.from(Items.IRON_PICKAXE), GuiIcon.from(Items.DIAMOND_PICKAXE));
		bar.setPosition(Position.below(bar, img, 2));
		bar.setVertical();
		//Smaller label with FontRenderOptions
		FontOptions fontOptions = FontOptions.builder().scale(2F / 3F).color(0x660066).build();
		UILabel smallLabel = new UILabel("Smaller label!");
		smallLabel.setPosition(Position.rightOf(smallLabel, bar, 4));
		smallLabel.setFontOptions(fontOptions);
		smallLabel.setTooltip("Testing label tooltip.");

		//CheckBox
		cb = new UICheckBox("CheckBox with label");
		cb.setPosition(Position.below(cb, bar, 2));
		cb.setTooltip(new UITooltip(TextFormatting.AQUA + "with delayed a tooltip!", 5));

		//RadioButton with custom fonts
		rbMC = new UIRadioButton("newRb", "MF");
		rbMC.setPosition(Position.below(rbMC, cb, 2));
		rbMC.setSelected();
		//rbMC.setFont(fontMC);
		rbBS = new UIRadioButton("newRb", "BS");
		rbBS.setPosition(Position.rightOf(rbBS, rbMC, 5));
		//rbBS.setFont(fontBS);
		rbH = new UIRadioButton("newRb", "H");
		rbH.setPosition(Position.rightOf(rbH, rbBS, 5));

		//Select
		select = new UISelect<>(100,
								Arrays.asList("Option 1",
											  "Option 2",
											  "Very ultra longer option 3",
											  "Shorty",
											  "Moar options",
											  "Even more",
											  "Even Steven",
											  "And a potato too"));
		select.setPosition(Position.below(select, rbMC, 5));
		//select.setOptionsWidth(UISelect.SELECT_WIDTH);
		//select.maxDisplayedOptions(5);
		select.select("Option 2");
		//select.setColors(0x660000, 0xFFCCCC, 0xFF0000, 0x999999, 0x6600CC, 0x664444, false);

		//uiselect size change button
		UIButton selectSizeButton = new UIButton("<-");
		selectSizeButton.setPosition(Position.rightOf(selectSizeButton, select, 5));
		selectSizeButton.setSize(Size.of(12, 12));
		selectSizeButton.onClick(() -> {
			currentSize += 20;
			if (currentSize > 190)
				currentSize = selectSize;
			select.setSize(Size.of(currentSize, 12));
		});

		//3 Buttons
		btnHorizontal = new UIButton("Horizontal");
		btnHorizontal.setPosition(Position.of(centered(btnHorizontal, 0), bottomAligned(btnHorizontal, 5)));
		btnHorizontal.setSize(Size.of(90, 20));
		btnHorizontal.setEnabled(true);
		btnL = new UIButton("O");
		btnL.setPosition(Position.leftOf(btnL, btnHorizontal, 1));
		btnL.setSize(Size.of(10, 10));
		btnR = new UIButton("O");
		btnR.setPosition(Position.rightOf(btnR, btnHorizontal, 1));
		btnR.setSize(Size.of(10, 10));

		//Add all elements
		UIContainer tabCont1 = new UIContainer();
		tabCont1.setName("Panel 1");

		tabCont1.add(img);
		tabCont1.add(label1);
		tabCont1.add(bar);
		tabCont1.add(smallLabel);

		tabCont1.add(cb);
		tabCont1.add(rbMC);
		tabCont1.add(rbBS);
		tabCont1.add(rbH);

		tabCont1.add(select);
		tabCont1.add(selectSizeButton);

		tabCont1.add(btnHorizontal);
		tabCont1.add(btnL);
		tabCont1.add(btnR);

		//Create 5 buttons with itemStack as images
		UIButton lastBtn = null;
		for (Item item : new Item[] { Items.COOKED_PORKCHOP,
									  Items.COOKED_BEEF,
									  Items.COOKED_MUTTON,
									  Items.COOKED_CHICKEN,
									  Item.getItemFromBlock(Blocks.GLASS_PANE) })
		{
			img = new UIImage(new ItemStack(item));
			UIContainer cont = new UIContainer();
			cont.setSize(Size.sizeOfContent(cont, 0, 0));
			UILabel lbl = new UILabel(item.getTranslationKey() + ".name");
			lbl.setPosition(Position.rightOf(lbl, img, 2));
			lbl.setFontOptions(FontOptions.builder().color(0xFFFFFF).shadow().scale(2 / 3F).build());
			cont.add(img, lbl);

			UIButton btnImage = new UIButton(cont);
			if (lastBtn == null)
				btnImage.setPosition(Position.topRight(btnImage));
			else
				btnImage.setPosition(Position.below(btnImage, lastBtn, 1));
			tabCont1.add(btnImage);
			lastBtn = btnImage;
		}

		return tabCont1;
	}

	private UIContainer textPanel()
	{
		//Textfield
		UITextField tf = new UITextField("This is a textfield. You can type in it.");
		tf.setSize(Size.of(parentWidth(tf, 0.5F, -5), 14));
		tf.setPosition(Position.topLeft(tf));
		//tf.setOptions(0x660000, 0xFFCCCC, 0x770000, 0xFF0000, false);

		//Password
		UILabel pwdLabel = new UILabel("Password :");
		pwdLabel.setPosition(Position.below(pwdLabel, tf, 5));
		UIPasswordField pwd = new UIPasswordField();
		pwd.setPosition(Position.rightOf(pwd, pwdLabel, 4));
		pwd.setSize(Size.of(() -> tf.size().width() - pwdLabel.size().width() - 4, 14));
		pwd.setAutoSelectOnFocus(true);

		//Multiline Textfield with FontRendererOptions
		FontOptions fontOptions = FontOptions.builder().scale(2F / 3F).color(0x006600).build();
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
		fontOptions = FontOptions.builder().scale(2F / 3F).color(0x338899).build();
		UILabel ipsum = new UILabel(true);
		ipsum.setPosition(Position.topRight(ipsum));
		ipsum.setSize(Size.of(parentWidth(ipsum, .5f, -5), parentHeight(ipsum, 1.0f, 0)));
		//ipsum.setSize(Size.of(	() -> ipsum.getParent() != null ? ipsum.getParent().size().width() - mltf.size().width() - 4 : 0,
		//						Sizes.relativeHeight(ipsum, 1.0F, 0)));
		ipsum.setText(TextFormatting.UNDERLINE + "Contrairement à une opinion répandue, " + TextFormatting.BOLD
							  + "le Lorem Ipsum n'est pas simplement du texte aléatoire" + TextFormatting.RESET
							  + ". Il trouve ses racines dans une oeuvre de la littérature latine classique" + TextFormatting.AQUA
							  + " datant de 45 av. J.-C., le rendant" + TextFormatting.RESET + " vieux de 2000 ans. " + TextFormatting.BLUE
							  + "Un professeur du " + TextFormatting.RESET + "Hampden-Sydney College" + TextFormatting.BLUE
							  + ", en Virginie, s'est intéressé" + TextFormatting.RESET + " à un des mots latins les plus obscurs, "
							  + TextFormatting.UNDERLINE + TextFormatting.DARK_RED + "consectetur" + TextFormatting.RESET
							  + ", extrait d'un passage du Lorem Ipsum, et en étudiant tous les usages de ce mot dans la littérature "
							  + "classique, découvrit la source incontestable du Lorem Ipsum. Il provient en fait des sections 1.10.32 et "
							  + "1.10.33 du \"De Finibus Bonorum et Malorum\" (Des Suprêmes Biens et des Suprêmes Maux) de Cicéron. Cet "
							  + "ouvrage, très populaire pendant la Renaissance, est un traité sur la théorie de l'éthique. Les premières "
							  + "lignes du Lorem Ipsum, \"Lorem ipsum dolor sit amet...\", proviennent de la section 1.10.32");
		ipsum.setFontOptions(fontOptions);
		//ipsum.setFont(new MalisisFont(new ResourceLocation(MalisisDemos.modid + ":fonts/HoboStd.otf")));
		new UISlimScrollbar(ipsum, UIScrollBar.Type.VERTICAL);

		//Add all elements
		UIContainer textTabCont = new UIContainer();
		textTabCont.setName("Text Tab");
		textTabCont.add(tf);
		textTabCont.add(pwdLabel);
		textTabCont.add(pwd);
		//textTabCont.add(mltf);
		textTabCont.add(ipsum);

		//Add the block's inventory's slots directly into the parent
		//		UIInventory invCont = new UIInventory("Block's inventory", inventoryContainer.getInventory(0));
		//		invCont.setPosition(Position.below(invCont, tf, 10));

		return textTabCont;
	}

	UISlider<Integer> sliderRed;
	UISlider<Integer> sliderGreen;
	UISlider<Integer> sliderBlue;
	UILabel sliderColorLabel;

	private UIContainer sliderPanel()
	{
		//Sliders with event caught to change the panel background color
		Converter<Float, Integer> colorConv = Converter.from(f -> (int) (f * 255), i -> (float) i / 255);
		sliderRed = new UISlider<>(150, colorConv, "{slider.red} {value}");
		sliderRed.setSize(Size.of(150, 12));
		sliderRed.setValue(255);
		sliderRed.setScrollStep(1 / 255F);
		sliderRed.onChange(this::calculateColor);
		sliderGreen = new UISlider<>(150, colorConv, "{slider.green} {value}");
		sliderGreen.setPosition(Position.below(sliderGreen, sliderRed, 2));
		sliderGreen.setSize(Size.of(150, 12));
		sliderGreen.setValue(255);
		sliderGreen.setScrollStep(1 / 255F);
		sliderBlue = new UISlider<>(150, colorConv, "{slider.blue} {value}");
		sliderBlue.setPosition(Position.below(sliderBlue, sliderGreen, 2));
		sliderBlue.setSize(Size.of(150, 12));
		sliderBlue.setValue(255);
		sliderBlue.setScrollStep(1 / 255F);

		sliderColorLabel = new UILabel(GuiText.builder().text("Color : {COLOR}").bind("COLOR", tabSlider::getColor).build());
		sliderColorLabel.setPosition(Position.rightOf(sliderColorLabel, sliderRed, 2));

		UIButton invertButton = new UIButton("Invert");
		invertButton.setPosition(Position.rightOf(invertButton, sliderGreen, 2));
		invertButton.setSize(Size.of(widthRelativeTo(sliderColorLabel, 1.0F, 0), heightRelativeTo(sliderBlue, 1.0F, 0)));
		invertButton.onClick(() -> {
			sliderRed.setValue(255 - sliderRed.getValue());
			sliderGreen.setValue(255 - sliderGreen.getValue());
			sliderBlue.setValue(255 - sliderBlue.getValue());
		});

		//Slider with custom values with days of the week
		Converter<Float, DayOfWeek> dayConv = Converter.from(f -> DayOfWeek.values()[Math.round(f * 6)], d -> (float) d.ordinal() / 6);
		UISlider<DayOfWeek> sliderDay = new UISlider<>(70, dayConv, "{value}");
		sliderDay.setPosition(Position.of(centered(sliderDay, 0), topAligned(sliderDay, 64)));
		sliderDay.setSize(Size.of(240, 30));
		sliderDay.setValue(LocalDate.now().getDayOfWeek());
		sliderDay.setScrollStep(1 / 6F);

		UIContainer sliderPanel = new UIContainer();
		sliderPanel.setName("Slider Tab");
		sliderPanel.add(sliderRed);
		sliderPanel.add(sliderGreen);
		sliderPanel.add(sliderBlue);
		sliderPanel.add(sliderColorLabel);
		sliderPanel.add(invertButton);
		sliderPanel.add(sliderDay);

		return sliderPanel;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private UIContainer listPanel()
	{
		List<Item> items = ImmutableList.of(Items.APPLE,
											Items.BED,
											Items.LEAD,
											Items.SNOWBALL,
											Items.ARROW,
											Items.WHEAT,
											Items.TNT_MINECART,
											Items.WATER_BUCKET,
											Items.COOKED_MUTTON);

		UIListContainer<Item> itemList = new UIListContainer<>();
		itemList.setSize(Size.of(parentWidth(itemList, 1.0F, 0), 150));
		itemList.setComponentFactory(item -> {
			return new UIContainer()
			{
				{
					ItemStack is = new ItemStack(item);
					setName(is.getDisplayName());
					setSize(Size.of(parentWidth(this, 1.0F, 0), 20));
					setBackground(GuiShape.builder(this).color(0xFFFFFF).border(1, 0x6666DD).build());

					UIImage img = new UIImage(is);
					img.setPosition(Position.middleCenter(this));
					add(img);

					UILabel label = new UILabel(is.getDisplayName() + ".name");
					label.setPosition(Position.of(rightOf(img, 4), middleAlignedTo(label, img, 0)));
					add(label);

					UIButton btn1 = new UIButton("1");
					btn1.setPosition(Position.topRight(btn1));
					btn1.setSize(Size.of(15, 10));
					btn1.attachData(1);
					add(btn1);

					int stackSize = is.getMaxStackSize();
					if (stackSize != 1)
					{
						UIButton btn2 = new UIButton("" + stackSize);
						btn2.setPosition(Position.bottomRight(btn2));
						btn2.setSize(Size.of(15, 10));
						btn2.attachData(stackSize);
						add(btn2);
					}

				}
			};
		});

		itemList.setElementSpacing(2);
		itemList.setElements(items);

		return itemList;
	}

	public void calculateColor()
	{
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
}
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

package net.malisis.ego.gui;

import static com.google.common.base.Preconditions.*;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import net.malisis.ego.EGO;
import net.malisis.ego.gui.component.DebugComponent;
import net.malisis.ego.gui.component.MouseButton;
import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.component.container.UIContainer;
import net.malisis.ego.gui.element.IKeyListener;
import net.malisis.ego.gui.element.Margin;
import net.malisis.ego.gui.element.size.Size;
import net.malisis.ego.gui.element.size.Size.ISize;
import net.malisis.ego.gui.render.GuiRenderer;
import net.malisis.ego.gui.render.GuiTexture;
import net.malisis.ego.gui.render.IGuiRenderer;
import net.malisis.ego.gui.render.shape.GuiShape;
import net.malisis.ego.gui.theme.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import java.awt.Desktop;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

/**
 * GuiScreenProxy
 *
 * @author Ordinastie
 */
public abstract class EGOGui extends GuiScreen implements ISize
{
	public static final GuiTexture BLOCK_TEXTURE = new GuiTexture(TextureMap.LOCATION_BLOCKS_TEXTURE, 1, 1);
	public static final GuiTexture VANILLAGUI_TEXTURE = new GuiTexture(new ResourceLocation("ego", "textures/gui/gui.png"), 300, 100);

	public static final MousePosition MOUSE_POSITION = new MousePosition();

	private static EGOGui current;
	public static IGuiRenderer GRADIENT_BG = GuiShape.builder()
													 .width(() -> current().width())
													 .height(() -> current().height())
													 .color(0x101010)
													 .topAlpha(0xC0)
													 .bottomAlpha(0xD0)
													 .build();

	/** Whether or not to cancel the next gui close event. */
	public static boolean cancelClose = false;

	protected Theme theme;
	/** Renderer drawing the components. */
	protected GuiRenderer renderer;
	/** Width of the window. */
	protected int displayWidth;
	/** Height of the window. */
	protected int displayHeight;
	/** Currently used gui scale. */
	protected int currentGuiScale;
	/** The resolution for the GUI **/
	protected ScaledResolution resolution;
	/** Top level parent which hold the user components. Spans across the whole screen. */
	protected final UIContainer screen;
	/** Determines if the screen should be darkened when the GUI is opened. */
	protected IGuiRenderer background = null;
	/** Last clicked button */
	protected long lastClickButton = -1;
	/** How long since last click. */
	protected long lastClickTime = 0;
	//	/** Inventory parent that handles the inventories and slots actions. */
	//	protected MalisisInventoryContainer inventoryContainer;
	/** Whether this GUI is considered as an overlay **/
	protected boolean isOverlay = false;

	protected Margin defaultMargin = Margin.NO_MARGIN;
	/** Currently hovered child component. */
	protected UIComponent hoveredComponent;
	/** Currently focused child component. */
	protected UIComponent focusedComponent;
	/** Currently dragged child component. */
	protected UIComponent draggedComponent;
	/** Whether the mouse did move while button was pressed. */
	protected boolean dragging;
	/** Component for which to display the tooltip (can be disabled and not receive events). */
	protected UIComponent tooltip;
	/** Whether this GUI has been constructed. */
	protected boolean constructed = false;
	/** List of {@link IKeyListener} registered. */
	protected Set<IKeyListener> keyListeners = new HashSet<>();

	protected GuiScreen parent = null;
	protected boolean showParentOnClose = false;

	protected List<UIComponent> modalComponents = Lists.newLinkedList();

	/** Debug **/
	private DebugComponent debugComponent;

	public static boolean debug = false;

	public static int counter = 0;
	public static int xCounter = 0;
	public static int xTotal = 0;

	protected EGOGui()
	{
		mc = Minecraft.getMinecraft();
		theme = EGO.VANILLA_THEME;
		itemRender = mc.getRenderItem();
		fontRenderer = mc.fontRenderer;
		renderer = new GuiRenderer();
		screen = UIContainer.builder()
							.name("Screen")
							.size(this) //.size(this::width, this::height)
							.clipContent(false)
							.build();
		Keyboard.enableRepeatEvents(true);

		debug = false;

		counter = 0;
		xCounter = 0;
		xTotal = 0;
	}

	protected EGOGui(GuiScreen parent)
	{
		this();
		this.parent = parent;
	}

	/**
	 * Called before display() if this {@link EGOGui} is not constructed yet.<br>
	 * Called when Ctrl+R is pressed to rebuild the GUI.
	 */
	public abstract void construct();

	protected boolean doConstruct()
	{
		try
		{
			if (!constructed)
			{
				screen.onAddedToScreen(this);
				debugComponent = new DebugComponent(this);
				construct();
				constructed = true;
			}
		}
		catch (Exception e)
		{
			EGO.message("A problem occured while constructing " + getClass().getSimpleName() + ": " + e.getMessage());
			EGO.log.error("A problem occured while constructing " + getClass().getSimpleName(), e);
		}

		return constructed;
	}

	public final void reconstruct()
	{
		modalComponents.clear();
		clearScreen();
		setResolution();
		setHoveredComponent(null);
		setFocusedComponent(null);
		constructed = false;
		doConstruct();
	}

	/**
	 * Sets the {@link Theme} used by this GUI.
	 *
	 * @param theme the theme to use
	 */
	public void setTheme(Theme theme)
	{
		this.theme = checkNotNull(theme);
	}

	/**
	 * @return the theme used by this {@link EGOGui}
	 */
	public Theme theme()
	{
		return theme;
	}

	/**
	 * Gets the {@link GuiRenderer} for this {@link EGOGui}.
	 *
	 * @return the renderer
	 */
	public GuiRenderer getRenderer()
	{
		return renderer;
	}

	@Override
	public int width()
	{
		return width;
	}

	@Override
	public int height()
	{
		return height;
	}

	public int scaleFactor()
	{
		return resolution.getScaleFactor();
	}

	public static boolean needsUpdate(int c)
	{
		if (c >= counter)
			return false;
		return true;
	}

	//	/**
	//	 * Sets the {@link MalisisInventoryContainer} for this {@link MalisisGui}.
	//	 *
	//	 * @param parent the inventory parent
	//	 */
	//	public void setInventoryContainer(MalisisInventoryContainer parent)
	//	{
	//		this.inventoryContainer = parent;
	//	}
	//
	//	public MalisisInventoryContainer inventoryContainer()
	//	{
	//		return inventoryContainer;
	//	}

	/**
	 * Gets the {@link GuiTexture} used by the {@link GuiRenderer}.
	 *
	 * @return the GuiTexture
	 */
	public GuiTexture getGuiTexture()
	{
		return renderer.getDefaultTexture();
	}

	/**
	 * Called when game resolution changes.
	 */
	@Override
	public final void setWorldAndResolution(Minecraft minecraft, int width, int height)
	{
		setResolution();
	}

	/**
	 * Sets the resolution for this {@link EGOGui}.
	 */
	public void setResolution()
	{
		boolean set = resolution == null;
		set |= displayWidth != Display.getWidth() || displayHeight != Display.getHeight();
		set |= currentGuiScale != mc.gameSettings.guiScale;

		if (!set)
			return;

		displayWidth = Display.getWidth();
		displayHeight = Display.getHeight();
		currentGuiScale = mc.gameSettings.guiScale;

		resolution = new ScaledResolution(mc);
		renderer.setScaleFactor(resolution.getScaleFactor());

		width = renderer.isIgnoreScale() ? displayWidth : resolution.getScaledWidth();
		height = renderer.isIgnoreScale() ? displayHeight : resolution.getScaledHeight();

		screen.setSize(Size.of(width, height));
	}

	public void setBackground(IGuiRenderer background)
	{
		screen.setBackground(background);
	}

	public void setDefaultMargin(Margin margin)
	{
		if (margin == null)
			margin = Margin.NO_MARGIN;
		defaultMargin = margin;
	}

	/**
	 * Checks whether this {@link EGOGui} is used as an overlay.
	 *
	 * @return true, if is overlay
	 */
	public boolean isOverlay()
	{
		return isOverlay;
	}

	/**
	 * Adds the {@link UIComponent}s to the screen.
	 *
	 * @param components the components
	 */
	public void addToScreen(UIComponent... components)
	{
		Arrays.stream(components)
			  .filter(Objects::nonNull)
			  .forEach(c -> {
				  screen.add(c);
				  c.onAddedToScreen(this);
			  });
	}

	/**
	 * Removes the {@link UIComponent} from screen.
	 *
	 * @param component the component
	 */
	public void removeFromScreen(UIComponent component)
	{
		screen.remove(component);
	}

	/**
	 * Removes all the components from the screen
	 */
	public void clearScreen()
	{
		screen.setBackground(null);
		screen.removeAll();
	}

	/**
	 * Registers a {@link IKeyListener} that will always receive keys types, even when not focused or hovered.
	 *
	 * @param listener the listener
	 */
	public void registerKeyListener(IKeyListener listener)
	{
		keyListeners.add(listener);
	}

	/**
	 * Unregisters a previously registered IKeyListener.
	 *
	 * @param listener the listener
	 */
	public void unregisterKeyListener(IKeyListener listener)
	{
		keyListeners.remove(listener);
	}

	public Optional<UIComponent> getCurrentModal()
	{
		return Optional.ofNullable(Iterables.getLast(modalComponents, null));
	}

	/**
	 * Gets the {@link UIContainer} at the specified coordinates inside this {@link EGOGui}.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return the component, null if component is {@link #screen}
	 */
	public UIComponent getComponentAt(int x, int y)
	{
		UIComponent component = getCurrentModal().orElse(screen);
		component = component.getComponentAt(x, y);
		return component == screen /*|| component == debugComponent*/ ? null : component;
	}

	/**
	 * Called every frame to handle mouse input.
	 */
	@Override
	public void handleMouseInput()
	{
		try
		{
			MOUSE_POSITION.udpate(this);
			UIComponent component = getComponentAt(MOUSE_POSITION.x(), MOUSE_POSITION.y());
			int button = Mouse.getEventButton();

			if (Mouse.getEventButtonState())
			{
				if (mc.gameSettings.touchscreen && touchValue++ > 0)
					return;

				eventButton = button;
				lastMouseEvent = Minecraft.getSystemTime();
				mousePressed(component, eventButton);
			}
			else if (button != -1)
			{
				if (mc.gameSettings.touchscreen && --touchValue > 0)
					return;

				eventButton = -1;
				mouseReleased(component, button);
			}
			else if (eventButton != -1 && lastMouseEvent > 0)
			{
				mouseDragged(eventButton);
			}

			if (MOUSE_POSITION.hasChanged())
			{
				setHoveredComponent(component);
				tooltip = null;
				if (component != null)
				{
					component.mouseMove();
					tooltip = component.getTooltip();
				}
			}

			int delta = Mouse.getEventDWheel();
			if (delta == 0)
				return;
			else if (delta > 1)
				delta = 1;
			else if (delta < -1)
				delta = -1;

			if (component != null && component.isEnabled())
			{
				component.scrollWheel(delta);
			}
		}
		catch (Exception e)
		{
			EGO.message("A problem occured : " + e.getClass()
												  .getSimpleName() + ": " + e.getMessage());
			e.printStackTrace(new PrintStream(new FileOutputStream(FileDescriptor.out)));
		}
	}

	/**
	 * Called when a mouse button is pressed down.
	 */
	protected void mousePressed(UIComponent component, int button)
	{
		try
		{
			if (component == null)
			{
				setFocusedComponent(null);
				return;
			}

			long time = System.currentTimeMillis();
			//double click
			if (button == lastClickButton && time - lastClickTime < 250 && component == focusedComponent)
			{
				component.doubleClick(MouseButton.getButton(button));
				lastClickTime = 0;
				return;
			}

			component.mouseDown(MouseButton.getButton(button));
			setFocusedComponent(component);
			if (draggedComponent == null)
				draggedComponent = component;

			lastClickTime = time;
			lastClickButton = button;
		}
		catch (Exception e)
		{
			EGO.message("A problem occured : " + e.getClass()
												  .getSimpleName() + ": " + e.getMessage());
			e.printStackTrace(new PrintStream(new FileOutputStream(FileDescriptor.out)));
		}
	}

	/**
	 * Called when the mouse is moved while a button is pressed.
	 */
	protected void mouseDragged(int button)
	{
		try
		{
			if (draggedComponent != null)
			{
				draggedComponent.mouseDrag(MouseButton.getButton(button));
				dragging = draggedComponent.dragPreventsClick();
			}

		}
		catch (Exception e)
		{
			EGO.message("A problem occured : " + e.getClass()
												  .getSimpleName() + ": " + e.getMessage());
			e.printStackTrace(new PrintStream(new FileOutputStream(FileDescriptor.out)));
		}

	}

	/**
	 * Called when a mouse button is released.
	 */
	protected void mouseReleased(UIComponent component, int button)
	{
		try
		{
			if (component == null)
				return;

			MouseButton mb = MouseButton.getButton(button);
			if (draggedComponent != null)
				draggedComponent.mouseUp(mb);

			if (!dragging && component == focusedComponent)
				component.click(mb);

			draggedComponent = null;
			dragging = false;
		}
		catch (Exception e)
		{
			EGO.message("A problem occured : " + e.getClass()
												  .getSimpleName() + ": " + e.getMessage());
			e.printStackTrace(new PrintStream(new FileOutputStream(FileDescriptor.out)));
		}
	}

	/**
	 * Called when a key is pressed on the keyboard.
	 */
	@Override
	protected void keyTyped(char keyChar, int keyCode)
	{
		try
		{
			if (focusedComponent != null && focusedComponent.keyTyped(keyChar, keyCode))
				return;

			boolean ret = false;
			for (IKeyListener listener : keyListeners) //if a component registered itself as IKeyListener, don't call keyTyped() twice
				ret |= listener != focusedComponent && listener.keyTyped(keyChar, keyCode);
			if (ret)
				return;

			if (debugComponent.keyTyped(keyChar, keyCode))
				return;

			if (isGuiCloseKey(keyCode))
			{
				if (getCurrentModal().isPresent())
					closeModal();
				else if (mc.currentScreen == this)
					close();
			}

			//reload current gui
			if (!EGO.isObfEnv && GuiScreen.isCtrlKeyDown() && (current() != null || isOverlay))
			{
				if (keyCode == Keyboard.KEY_R)
				{
					reconstruct();
				}
			}
		}
		catch (Exception e)
		{
			EGO.message("A problem occured while handling key typed for " + e.getClass()
																			 .getSimpleName() + " : " + e.getMessage());
			EGO.log.error("A problem occured while handling key typed for " + getClass().getSimpleName(), e);
			e.printStackTrace(new PrintStream(new FileOutputStream(FileDescriptor.out)));
		}

	}

	/**
	 * Draws this {@link EGOGui}.
	 */
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTick)
	{
		counter++;
		xCounter = 0;
		xTotal = 0;

		renderer.setup(partialTick);

		try
		{
			update();
		}
		catch (Exception e)
		{
			EGO.message("A problem occured while updating screen " + getClass().getSimpleName() + " : " + e.getMessage());
			EGO.log.error("A problem occured while updating screen " + getClass().getSimpleName(), e);
		}

		try
		{
			screen.render(renderer);

			//don't draw tooltip if mouse has itemStack
			boolean renderTooltip = tooltip != null;
			//			if (inventoryContainer != null)
			//				renderTooltip &= !renderer.renderPickedItemStack(inventoryContainer.getPickedItemStack());

			if (renderTooltip)
				tooltip.render(renderer);

		}
		catch (Exception e)
		{
			EGO.message("A problem occured while rendering screen " + getClass().getSimpleName() + " : " + e.getMessage());
			EGO.log.error("A problem occured while rendering screen " + getClass().getSimpleName(), e);
		}

		renderer.clean();
	}

	//#region Debug
	public void addDebug(String name, Supplier<String> supplier)
	{
		debugComponent.addDebug(name, supplier);
	}

	public void watch(String componentName)
	{
		debugComponent.watch(componentName);
	}
	//#end Debug

	/**
	 * Called every frame.
	 */
	public void update()
	{
	}

	/**
	 * Called from TE when TE is updated. Override this method when you want to change displayed informations when the TileEntity changes.
	 */
	public void updateGui()
	{
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

	/**
	 * Displays this {@link EGOGui}.
	 */
	public void display()
	{
		display(false);
	}

	/**
	 * Display this {@link EGOGui}.
	 *
	 * @param cancelClose whether or not to cancel the next close event (used for when the GUI is opened from command)
	 */
	public void display(boolean cancelClose)
	{
		setResolution();
		current = this;
		if (!doConstruct())
			return;

		EGOGui.cancelClose = cancelClose;
		parent = Minecraft.getMinecraft().currentScreen;
		Minecraft.getMinecraft()
				 .displayGuiScreen(this);
	}

	public void displayModal(UIComponent component)
	{
		if (component == null)
			return;

		modalComponents.add(component);
		setFocusedComponent(null);
		setHoveredComponent(null);
		addToScreen(component);
	}

	public void hideModal()
	{
		Optional<UIComponent> o = getCurrentModal();
		if (!o.isPresent())
			return;
		removeFromScreen(o.get());
		modalComponents.remove(o.get());
		focusedComponent = null;
	}

	/**
	 * Closes this {@link EGOGui}.
	 */
	public void close()
	{
		setFocusedComponent(null);
		setHoveredComponent(null);
		Keyboard.enableRepeatEvents(false);
		current = null;
		if (mc.player != null)
			mc.player.closeScreen();

		if (parent != null && showParentOnClose)
			mc.displayGuiScreen(parent);
		else
		{
			mc.displayGuiScreen(null);
			mc.setIngameFocus();
		}
	}

	public void displayOverlay()
	{
		isOverlay = true;
		setResolution();

		if (!doConstruct())
			return;

		MinecraftForge.EVENT_BUS.register(this);
	}

	public void closeOverlay()
	{
		if (mc.currentScreen == this)
			close();
		MinecraftForge.EVENT_BUS.unregister(this);
		onGuiClosed();
	}

	@Override
	public void onGuiClosed()
	{
		//		if (inventoryContainer != null)
		//			inventoryContainer.onContainerClosed(this.mc.player);
	}

	@SubscribeEvent
	public void renderOverlay(RenderGameOverlayEvent.Post event)
	{
		if (event.getType() != RenderGameOverlayEvent.ElementType.ALL || Minecraft.getMinecraft().currentScreen == this)
			return;

		setResolution();
		drawScreen(0, 0, event.getPartialTicks());
	}

	@SubscribeEvent
	public void keyEvent(InputEvent.KeyInputEvent event)
	{
		if (!isOverlay || mc.currentScreen == this)
			return;

		if (Keyboard.getEventKeyState())
			keyTyped(Keyboard.getEventCharacter(), Keyboard.getEventKey());
	}

	/**
	 * Gets the client world.
	 *
	 * @return the client world
	 */
	public World world()
	{
		return Minecraft.getMinecraft().world;
	}

	/**
	 * Gets the client player.
	 *
	 * @return the client player
	 */
	public EntityPlayer player()
	{
		return Minecraft.getMinecraft().player;
	}

	public UIComponent findComponent(String name)
	{
		return modalComponents.stream()
							  .map(c -> c.getComponent(name))
							  .filter(Objects::nonNull)
							  .findAny()
							  .orElse(screen.getComponent(name));
	}

	/**
	 * Gets the current {@link EGOGui} displayed.
	 *
	 * @return null if no GUI being displayed or if not a {@link EGOGui}
	 */
	public static EGOGui current()
	{
		return current;
	}

	/**
	 * Gets the current {@link EGOGui} of the specified type displayed.<br>
	 * If the current gu is not of <i>type</i>, null is returned.
	 *
	 * @param <T> the generic type
	 * @param type the type
	 * @return the t
	 */
	public static <T extends EGOGui> T current(Class<T> type)
	{
		GuiScreen gui = Minecraft.getMinecraft().currentScreen;
		if (!(gui instanceof EGOGui))
			return null;
		try
		{
			return type.cast(gui);
		}
		catch (ClassCastException e)
		{
			return null;
		}
	}

	/**
	 * Gets the current theme in use.
	 *
	 * @return
	 */
	public static Theme currentTheme()
	{
		EGOGui current = current();
		if (current == null)
			return EGO.VANILLA_THEME;
		return current.theme();
	}

	//	/**
	//	 * Sends a GUI action to the server.
	//	 *
	//	 * @param action the action
	//	 * @param slot the slot
	//	 * @param code the keyboard code
	//	 */
	//	public static void sendAction(ActionType action, MalisisSlot slot, int code)
	//	{
	//		if (action == null || current() == null || current().inventoryContainer == null)
	//			return;
	//
	//		int inventoryId = slot != null ? slot.getInventoryId() : 0;
	//		int slotNumber = slot != null ? slot.getSlotIndex() : 0;
	//
	//		current().inventoryContainer.handleAction(action, inventoryId, slotNumber, code);
	//		InventoryActionMessage.sendAction(action, inventoryId, slotNumber, code);
	//	}

	/**
	 * @return the currently hovered {@link UIComponent}. null if there is no current GUI.
	 */
	public static UIComponent getHoveredComponent()
	{
		return current() != null ? current().hoveredComponent : null;
	}

	/**
	 * Sets the hovered state for a {@link UIComponent}. If a <code>UIComponent is currently hovered, it will be "unhovered" first.
	 *
	 * @param component the component that gets his state changed
	 */
	public static void setHoveredComponent(UIComponent component)
	{
		EGOGui gui = current();
		if (gui == null || gui.hoveredComponent == component)
			return;

		if (gui.hoveredComponent != null)
			gui.hoveredComponent.unhover();
		gui.hoveredComponent = null;
		if (component == null/* || !component.isVisible()*/)
		{
			gui.tooltip = null;
			return;
		}

		component.hover();
		gui.hoveredComponent = component;
	}

	/**
	 * Gets the currently focused {@link UIComponent}
	 *
	 * @return the component
	 */
	public static UIComponent getFocusedComponent()
	{
		return current() != null ? current().focusedComponent : null;
	}

	public static void setFocusedComponent(UIComponent component)
	{
		EGOGui gui = current();
		if (gui == null || gui.focusedComponent == component)
			return;

		if (gui.focusedComponent != null)
			gui.focusedComponent.unfocus();
		gui.focusedComponent = null;
		if (component == null || !component.isVisible())
			return;

		component.focus();
		gui.focusedComponent = component;
	}

	public static UIComponent getDraggedComponent()
	{
		return current() != null ? current().draggedComponent : null;
	}

	public static Margin defaultMargin()
	{
		return current != null ? current.defaultMargin : Margin.NO_MARGIN;
	}

	//	/**
	//	 * Gets the {@link MalisisInventoryContainer} for the current {@link MalisisGui}.
	//	 *
	//	 * @return inventory parent
	//	 */
	//	public static MalisisInventoryContainer getInventoryContainer()
	//	{
	//		return current() != null ? current().inventoryContainer : null;
	//
	//	}

	public static void playSound(SoundEvent sound)
	{
		playSound(sound, 1.0F);
	}

	public static void playSound(SoundEvent sound, float level)
	{
		Minecraft.getMinecraft()
				 .getSoundHandler()
				 .playSound(PositionedSoundRecord.getMasterRecord(sound, level));
	}

	public static boolean isGuiCloseKey(int keyCode)
	{
		//	MalisisGui gui = current();
		return keyCode == Keyboard.KEY_ESCAPE;
		//				|| (gui != null && gui.inventoryContainer != null && keyCode == gui.mc.gameSettings.keyBindInventory.getKeyCode());
	}

	public static void closeGui()
	{
		if (current() != null)
			current().close();
	}

	public static void closeModal()
	{
		if (current() != null)
			current().hideModal();

	}

	public static void openLink(String url)
	{
		try
		{
			Desktop.getDesktop()
				   .browse(new URI(url));
		}
		catch (IOException | URISyntaxException e)
		{
			EGO.message("A problem occured while opening url " + url + " : " + e.getMessage());
			EGO.log.error("A problem occured while opening url " + url, e);
		}
	}
}

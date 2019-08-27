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

package net.malisis.ego.gui.component;

import static com.google.common.base.Preconditions.*;

import net.malisis.ego.gui.MalisisGui;
import net.malisis.ego.gui.component.container.UIContainer;
import net.malisis.ego.gui.component.content.IContent;
import net.malisis.ego.gui.component.control.IControlComponent;
import net.malisis.ego.gui.component.decoration.UITooltip;
import net.malisis.ego.gui.element.IChild;
import net.malisis.ego.gui.element.IClipable;
import net.malisis.ego.gui.element.IClipable.ClipArea;
import net.malisis.ego.gui.element.IKeyListener;
import net.malisis.ego.gui.element.IOffset;
import net.malisis.ego.gui.element.Padding;
import net.malisis.ego.gui.element.Padding.IPadded;
import net.malisis.ego.gui.element.position.Position;
import net.malisis.ego.gui.element.position.Position.IPosition;
import net.malisis.ego.gui.element.position.Position.ScreenPosition;
import net.malisis.ego.gui.element.size.Size;
import net.malisis.ego.gui.element.size.Size.ISize;
import net.malisis.ego.gui.element.size.Sizes;
import net.malisis.ego.gui.event.EventHandler;
import net.malisis.ego.gui.event.GuiEvent;
import net.malisis.ego.gui.event.KeyTypedEvent;
import net.malisis.ego.gui.event.MouseEvent.IMouseEventRegister;
import net.malisis.ego.gui.event.MouseEvent.MouseDoubleClick;
import net.malisis.ego.gui.event.MouseEvent.MouseDown;
import net.malisis.ego.gui.event.MouseEvent.MouseDrag;
import net.malisis.ego.gui.event.MouseEvent.MouseLeftClick;
import net.malisis.ego.gui.event.MouseEvent.MouseMove;
import net.malisis.ego.gui.event.MouseEvent.MouseOut;
import net.malisis.ego.gui.event.MouseEvent.MouseOver;
import net.malisis.ego.gui.event.MouseEvent.MouseRightClick;
import net.malisis.ego.gui.event.MouseEvent.MouseUp;
import net.malisis.ego.gui.event.MouseEvent.ScrollWheel;
import net.malisis.ego.gui.event.StateChangeEvent.DisableEvent;
import net.malisis.ego.gui.event.StateChangeEvent.EnableEvent;
import net.malisis.ego.gui.event.StateChangeEvent.FocusEvent;
import net.malisis.ego.gui.event.StateChangeEvent.HiddenEvent;
import net.malisis.ego.gui.event.StateChangeEvent.UnfocusEvent;
import net.malisis.ego.gui.event.StateChangeEvent.VisibleEvent;
import net.malisis.ego.gui.render.GuiRenderer;
import net.malisis.ego.gui.render.IGuiRenderer;
import org.apache.logging.log4j.util.Strings;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

/**
 * {@link UIComponent} is the base of everything drawn onto a GUI.<br>
 * The drawing is separated between background and foreground.<br>
 * Most of the events are launched from UIComponent.
 *
 * @author Ordinastie, PaleoCrafter
 */
public abstract class UIComponent implements IContent, IGuiRenderer, IKeyListener, IChild<UIComponent>, IMouseEventRegister<UIComponent>
{
	/** Reference to the {@link MalisisGui} this {@link UIComponent} was added to. Set when the component is added to screen. */
	protected MalisisGui gui;
	/** Event handler. */
	protected final EventHandler eventHandler = new EventHandler();
	/** List of {@link UIComponent components} controlling this {@link UIContainer}. */
	protected final Set<IControlComponent> controlComponents;
	/** Position of this {@link UIComponent}. */
	protected IPosition position = Position.topLeft(this);
	/** Position of this {@link UIComponent} on screen. */
	private final IPosition screenPosition = new ScreenPosition(this, this instanceof IControlComponent);
	/** Position of the mouse inside this {@link UIComponent}. */
	private final IPosition mousePosition = MalisisGui.MOUSE_POSITION.minus(screenPosition);
	/** Size of this {@link UIComponent}. */
	protected ISize size = Size.inherited(this);
	/** Size available for content. */
	protected ISize innerSize = Size.of(Sizes.innerWidth(this), Sizes.innerHeight(this));
	/** Z index of the component. */
	protected int zIndex = 0;
	/** Color of the component. Effect dependent on renderers. */
	protected int color = 0xFFFFFF;
	/** Alpha transparency of this {@link UIComponent}. */
	protected int alpha = 255;
	/** The parent {@link UIComponent} of this <code>UIComponent</code>. */
	protected UIComponent parent;
	/** The name of this {@link UIComponent}. Can be used to retrieve this back from a parent. */
	protected String name;
	/** The tooltip for this {@link UIComponent} Automatically displayed when the {@link UIComponent} is hovered. */
	protected UITooltip tooltip;
	/** Determines whether this {@link UIComponent} is visible. */
	protected boolean visible = true;
	/** Determines whether this {@link UIComponent} is enabled. If set to false, will cancel any {@link GuiEvent events} received. */
	protected boolean enabled = true;
	/** Hover state of this {@link UIComponent}. */
	protected boolean hovered = false;
	/** Focus state of this {@link UIComponent}. */
	protected boolean focused = false;

	/** Rendering for the background of this {@link UIComponent}. */
	protected IGuiRenderer backgroundRenderer = null;
	/** Rendering for the foreground of this {@link UIComponent}. */
	protected IGuiRenderer foregroundRenderer = null;

	private Object data;

	/**
	 * Instantiates a new {@link UIComponent}.
	 */
	public UIComponent()
	{
		controlComponents = new LinkedHashSet<>();
	}

	// #region getters/setters

	/**
	 * Gets the {@link MalisisGui} this {@link UIComponent} was added to.
	 *
	 * @return the gui
	 */
	public MalisisGui getGui()
	{
		return gui;
	}

	/**
	 * Sets the position of this {@link UIComponent}.
	 *
	 * @param position the new position
	 */
	@Override
	public void setPosition(@Nonnull IPosition position)
	{
		//if(fireEvent(this, this.position, position);
		this.position = checkNotNull(position);
	}

	/**
	 * Gets the position of this {@link UIComponent}.
	 *
	 * @return the position
	 */
	@Override
	@Nonnull
	public IPosition position()
	{
		return position;
	}

	/**
	 * Gets the position of this {@link UIComponent} on the screen
	 *
	 * @return the component position
	 */
	public IPosition screenPosition()
	{
		return screenPosition;
	}

	/**
	 * Gets the position of the mouse inside this {@link UIComponent}
	 *
	 * @return mouse position
	 */
	public IPosition mousePosition()
	{
		if (this instanceof IOffset)
			return mousePosition.minus(((IOffset) this).offset());
		return mousePosition;
	}

	/**
	 * Sets the size of this {@link UIComponent}.
	 *
	 * @param size the new size
	 */
	public void setSize(@Nonnull ISize size)
	{
		//if(fireEvent(this, this.size, size)
		this.size = checkNotNull(size);
	}

	/**
	 * Gets the size of this {@link UIComponent}.
	 *
	 * @return the size
	 */
	@Override
	@Nonnull
	public ISize size()
	{
		return size;
	}

	public ISize innerSize()
	{
		return innerSize;
	}

	/**
	 * Sets the zIndex for this {@link UIComponent}.
	 *
	 * @param zIndex the z index
	 */
	public void setZIndex(int zIndex)
	{
		this.zIndex = zIndex;
		//TODO: fire event to notify parent to reorder components
	}

	/**
	 * Gets the zIndex of this {@link UIComponent}.
	 *
	 * @return the zIndex
	 */
	public int zIndex()
	{
		return zIndex == 0 ? (parent != null ? parent.zIndex() : 0) : zIndex;
	}

	/**
	 * Sets the <code>hovered</code> state of this {@link UIComponent}.
	 *
	 * @param hovered the new state
	 */
	public void setHovered(boolean hovered)
	{
		boolean flag = isHovered() != hovered;
		flag |= MalisisGui.setHoveredComponent(this, hovered);
		if (!flag)
			return;

		this.hovered = hovered;
		fireEvent(isHovered() ? new MouseOver<>(this) : new MouseOut<>(this));
	}

	/**
	 * Gets the <code>hovered</code> state of this {@link UIComponent}.
	 *
	 * @return true, this component is hovered
	 */
	public boolean isHovered()
	{
		return hovered;
	}

	/**
	 * Sets the <code>focused</code> state of this {@link UIComponent}.
	 *
	 * @param focused the state
	 */
	public void setFocused(boolean focused)
	{
		if (!isEnabled())
			return;

		boolean flag = isFocused() != focused;
		flag |= MalisisGui.setFocusedComponent(this, focused);
		if (!flag)
			return;

		this.focused = focused;
		fireEvent(isFocused() ? new FocusEvent<>(this) : new UnfocusEvent<>(this));
	}

	/**
	 * Gets the <code>focused</code> state of this {@link UIComponent}.
	 *
	 * @return true, if this component if focused
	 */
	public boolean isFocused()
	{
		return focused;
	}

	/**
	 * Gets the parent of this {@link UIComponent}.
	 *
	 * @return the parent
	 */
	@Override
	public UIComponent getParent()
	{
		return parent;
	}

	/**
	 * Sets the parent of this {@link UIComponent}.
	 *
	 * @param parent the parent
	 */
	@Override
	public void setParent(UIComponent parent)
	{
		this.parent = parent;
		//fireEvent(new ContentUpdate<>(this));
	}

	/**
	 * Checks if this {@link UIComponent} is visible.
	 *
	 * @return true, if visible
	 */
	public boolean isVisible()
	{
		return visible;
	}

	/**
	 * Sets the visibility of this {@link UIComponent}.
	 *
	 * @param visible the visibility for this component
	 */
	public void setVisible(boolean visible)
	{
		if (isVisible() == visible)
			return;

		this.visible = visible;
		fireEvent(isVisible() ? new VisibleEvent<>(this) : new HiddenEvent<>(this));

		if (getParent() instanceof UIContainer)
			((UIContainer) getParent()).onContentUpdate();
		if (!isVisible())
		{
			setHovered(false);
			setFocused(false);
		}
	}

	/**
	 * Checks if this {@link UIComponent} is enabled.
	 *
	 * @return true if enabled
	 */
	public boolean isEnabled()
	{
		return enabled && (parent == null || parent.isEnabled());
	}

	/**
	 * Checks if this {@link UIComponent} is disabled.
	 *
	 * @return true, if is disabled
	 */
	public boolean isDisabled()
	{
		return !isEnabled();
	}

	/**
	 * Set the state of this {@link UIComponent}.
	 *
	 * @param enabled the new state
	 */
	public void setEnabled(boolean enabled)
	{
		if (isEnabled() == enabled)
			return;

		this.enabled = enabled;
		fireEvent(isEnabled() ? new EnableEvent<>(this) : new DisableEvent<>(this));

		if (isEnabled())
		{
			setHovered(false);
			setFocused(false);
		}
	}

	/**
	 * Gets the name of this {@link UIComponent}.
	 *
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of this {@link UIComponent}.
	 *
	 * @param name the name to be used
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the {@link UITooltip} for this {@link UIComponent}.
	 *
	 * @return the tooltip
	 */
	public UITooltip getTooltip()
	{
		return tooltip;
	}

	/**
	 * Sets the {@link UITooltip} of this {@link UIComponent}.
	 *
	 * @param tooltip the tooltip
	 */
	public void setTooltip(UITooltip tooltip)
	{
		this.tooltip = tooltip;
	}

	/**
	 * Sets the {@link UITooltip} of this {@link UIComponent}.
	 *
	 * @param text the text of the tooltip
	 */
	public void setTooltip(String text)
	{
		if (Strings.isEmpty(text))
			setTooltip((UITooltip) null);
		else
			setTooltip(new UITooltip(text));
	}

	//@Override
	public void setColor(int color)
	{
		this.color = color;
	}

	public int getColor()
	{
		return color;
	}

	/**
	 * Sets the alpha transparency for this {@link UIComponent}.
	 *
	 * @param alpha the new alpha
	 */
	//@Override
	public void setAlpha(int alpha)
	{
		this.alpha = alpha;
	}

	/**
	 * Gets the alpha transparency for this {@link UIComponent}.
	 *
	 * @return the alpha
	 */
	public int getAlpha()
	{
		if (getParent() == null)
			return alpha;

		return Math.min(alpha, parent.getAlpha());
	}

	/**
	 * Sets the background for this {@link UIComponent}.
	 *
	 * @param render the new background
	 */
	public void setBackground(IGuiRenderer render)
	{
		backgroundRenderer = render;
	}

	/**
	 * Sets the foreground for this {@link UIComponent}.
	 *
	 * @param render the new foreground
	 */
	public void setForeground(IGuiRenderer render)
	{
		foregroundRenderer = render;
	}

	public void attachData(Object data)
	{
		this.data = data;
	}

	public Object getData()
	{
		return data;
	}

	// #end getters/setters

	/**
	 * Fires a {@link GuiEvent}.<br>
	 *
	 * @param event event to fire
	 * @return if true, stop the propagation of the event to the source's parent
	 */
	public boolean fireEvent(GuiEvent<?> event)
	{
		return eventHandler.fireEvent(event);
	}

	@Override
	public <T extends GuiEvent<? extends UIComponent>> void register(Class<T> clazz, Predicate<T> handler)
	{
		eventHandler.register(clazz, handler);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void onKeyTyped(Predicate<KeyTypedEvent<UIComponent>> handler)
	{
		register(KeyTypedEvent.class, (Predicate) handler);
	}

	//#region Inputs

	/**
	 * Called from the GUI when mouse is moved and over this component.
	 */
	public void mouseMove()
	{
		if (!isEnabled())
			return;
		if (fireEvent(new MouseMove<>(this)))
			return;
		if (parent != null)
			parent.mouseMove();
	}

	/**
	 * Called from the GUI when a mouse button is pressed
	 *
	 * @param button the button
	 */
	public void mouseDown(MouseButton button)
	{
		if (!isEnabled())
			return;
		if (fireEvent(new MouseDown<>(this, button)))
			return;
		if (parent != null)
			parent.mouseDown(button);
	}

	/**
	 * Called from teh GUI when a mouse button is released
	 *
	 * @param button the button
	 */
	public void mouseUp(MouseButton button)
	{
		if (!isEnabled())
			return;
		if (fireEvent(new MouseUp<>(this, button)))
			return;
		if (parent != null)
			parent.mouseUp(button);
	}

	/**
	 * Called from the GUI when the left mouse button is clicked
	 *
	 * @param button button being clicked
	 */
	public void click(MouseButton button)
	{
		if (!isEnabled())
			return;
		if (fireEvent(button == MouseButton.LEFT ? new MouseLeftClick<>(this) : new MouseRightClick<>(this)))
			return;
		if (parent != null)
			parent.click(button);
	}

	/**
	 * Called from the GUI when a mouse button is double clicked.<br>
	 * Only fires the event if the button is {@link MouseButton#LEFT}.
	 *
	 * @param button the button
	 */
	public void doubleClick(MouseButton button)
	{
		if (!isEnabled())
			return;
		if (button != MouseButton.LEFT)
			return;
		if (fireEvent(new MouseDoubleClick<>(this)))
			return;
		if (parent != null)
			parent.doubleClick(button);
	}

	/**
	 * Called from the GUI when the mouse is dragged with a button pressed.<br>
	 * To get the distance dragged, use {@code MalisisGui.MOUSE_POSITION.moved()}
	 *
	 * @param button the button
	 */
	public void mouseDrag(MouseButton button)
	{
		if (!isEnabled())
			return;
		if (fireEvent(new MouseDrag<>(this, button)))
			return;
		if (parent != null)
			parent.mouseDrag(button);
	}

	/**
	 * Called from the GUI when the mouse scroll wheel is used.
	 *
	 * @param delta the delta
	 */
	public void scrollWheel(int delta)
	{
		if (!isEnabled())
			return;

		//IControlComponents should just register their event
		//		for (IControlComponent c : controlComponents)
		//		{
		//			if (c.onScrollWheel(delta))
		//				return;
		//		}
		//
		//		if(this instanceof IControlComponent)
		//			return;

		if (fireEvent(new ScrollWheel<>(this, delta)))
			return;

		if (parent != null)
			parent.scrollWheel(delta);
	}

	@Override
	public boolean keyTyped(char keyChar, int keyCode)
	{
		if (!isEnabled())
			return false;

		//IControlComponents should just register their event
		//		for (IControlComponent c : controlComponents)
		//		{
		//			if (c.keyTyped(keyChar, keyCode))
		//				return;
		//		}
		//
		//		if(this instanceof IControlComponent)
		//			return;

		if (fireEvent(new KeyTypedEvent<>(this, keyChar, keyCode)))
			return true;

		return parent != null && parent.keyTyped(keyChar, keyCode);
	}

	//#end Inputs

	/**
	 * Checks if supplied coordinates are inside this {@link UIComponent} bounds.
	 *
	 * @param x the x
	 * @param y the y
	 * @return true, if coordinates are inside bounds
	 */
	public boolean isInsideBounds(int x, int y)
	{
		if (!isVisible())
			return false;
		int sx = screenPosition().x();
		int sy = screenPosition().y();
		return x >= sx && x <= sx + size().width() && y >= sy && y <= sy + size().height();
	}

	/**
	 * Gets the {@link UIComponent} at the specified coordinates.<br>
	 * Will return a {@link IControlComponent} if any. Checks if inside bounds, visible and not disabled.
	 *
	 * @param x the x
	 * @param y the y
	 * @return this {@link UIComponent} or null if outside its bounds.
	 */
	public UIComponent getComponentAt(int x, int y)
	{
		//control components take precedence over regular components
		for (IControlComponent c : controlComponents)
		{
			UIComponent component = c.getComponentAt(x, y);
			if (component != null)
				return component;
		}

		return isInsideBounds(x, y) ? this : null;
	}

	/**
	 * Adds a {@link IControlComponent} component to this {@link UIComponent}.
	 *
	 * @param component the component
	 */
	public void addControlComponent(IControlComponent component)
	{
		controlComponents.add(component);
		component.setParent(this);
	}

	/**
	 * Removes the {@link IControlComponent} from this {@link UIComponent}.
	 *
	 * @param component the component
	 */
	public void removeControlComponent(IControlComponent component)
	{
		if (component.getParent() != this)
			return;

		controlComponents.remove(component);
		component.setParent(null);
	}

	/**
	 * Removes all the {@link IControlComponent} from this {@link UIContainer}.
	 */
	public void removeAllControlComponents()
	{
		for (IControlComponent component : controlComponents)
		{
			component.setParent(null);
		}
		controlComponents.clear();
	}

	/**
	 * Called when this {@link UIComponent} is added to screen.
	 */
	public void onAddedToScreen(MalisisGui gui)
	{
		this.gui = gui;
	}

	/**
	 * Draws this {@link UIComponent}. Rendering is surrounded by glPushAttrib(GL_ALL_ATTRIB_BITS) so no state should bleed between
	 * components. Also, a draw() is triggered between background and foreground.
	 *
	 * @param renderer the renderer
	 */
	@Override
	public void render(GuiRenderer renderer)
	{
		if (!isVisible())
			return;

		//		if (getGui().isOverlay())
		//		{
		//			GlStateManager.blendFunc(GL11.GL_CONSTANT_ALPHA, GL11.GL_ONE_MINUS_CONSTANT_ALPHA);
		//			GL14.glBlendColor(1, 1, 1, (float) getAlpha() / 255);
		//		}

		//store last drawn component so that it can be set back after drawing.
		//makes sure components overriding rendering and calling super still have correct
		//relative position in case super renders other components.
		UIComponent oldComponent = renderer.currentComponent;

		//draw background
		if (backgroundRenderer != null)
		{
			renderer.currentComponent = this;
			backgroundRenderer.render(renderer);
		}

		//draw foreground
		if (foregroundRenderer != null)
		{
			ClipArea area = IClipable.intersected(this);
			if (!area.fullClip())
			{
				if (!(this instanceof IControlComponent))
					renderer.startClipping(area);
				renderer.currentComponent = this;
				foregroundRenderer.render(renderer);
				if (!(this instanceof IControlComponent))
					renderer.endClipping(area);

			}
		}

		if (controlComponents.size() != 0)
		{
			controlComponents.forEach(c -> c.render(renderer));
		}

		renderer.currentComponent = oldComponent;
	}

	/**
	 * Gets the property string.
	 *
	 * @return the property string
	 */
	public String getPropertyString()
	{
		String str = position() + "@" + size() + " | Screen=" + screenPosition();
		if (this instanceof IPadded)
		{
			Padding p = Padding.of(this);
			str += " | Padding " + p.left() + "." + p.right() + "." + p.right() + "." + p.right();
		}
		return str;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString()
	{
		return (name == null ? getClass().getSimpleName() : name) + " " + getPropertyString();
	}
}

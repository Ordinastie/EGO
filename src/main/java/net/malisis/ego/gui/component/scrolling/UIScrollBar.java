/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 Ordinastie
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

package net.malisis.ego.gui.component.scrolling;

import static com.google.common.base.Preconditions.*;

import net.malisis.ego.gui.component.MouseButton;
import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.component.content.IContent;
import net.malisis.ego.gui.component.control.IControlComponent;
import net.malisis.ego.gui.component.control.IScrollable;
import net.malisis.ego.gui.element.Padding;
import net.malisis.ego.gui.element.position.Position;
import net.malisis.ego.gui.element.position.Position.IPosition;
import net.malisis.ego.gui.element.size.Size;
import net.malisis.ego.gui.element.size.Size.ISize;
import net.malisis.ego.gui.element.size.Size.ISized;
import net.malisis.ego.gui.event.KeyTypedEvent;
import net.malisis.ego.gui.event.MouseEvent.ScrollWheel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Keyboard;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.IntSupplier;

/**
 * UIScrollBar
 *
 * @author Ordinastie
 */
public abstract class UIScrollBar extends UIComponent implements IControlComponent
{
	public enum Type
	{
		HORIZONTAL, VERTICAL
	}

	private static final Map<IScrollable, UIScrollBar> verticalScrollbars = new WeakHashMap<>();
	private static final Map<IScrollable, UIScrollBar> horizontalScrollbars = new WeakHashMap<>();

	protected IPosition scrollPosition = new ScrollPosition();
	protected ISize scrollSize = Size.of(5, 5);

	/** The type of scrollbar. */
	protected Type type;

	protected float offset;

	public boolean autoHide = false;

	public <T extends UIComponent & IScrollable> UIScrollBar(T parent, Type type)
	{
		this.type = type;
		zIndex = 5;

		parent.addControlComponent(this);
		parent.onScrollWheel(this::scrollWheel);
		parent.onKeyTyped(this::keyTyped);

		if (type == Type.VERTICAL)
			verticalScrollbars.put(parent, this);
		else
			horizontalScrollbars.put(parent, this);

		setPosition(new ScrollbarPosition());
		setSize(new ScrollbarSize());
		updateScrollbar();
	}

	@SuppressWarnings("unchecked")
	public <T extends UIComponent & IScrollable> T parent()
	{
		return (T) getParent();
	}

	/**
	 * Gets the parent as a {@link IScrollable}.
	 *
	 * @return the scrollable
	 */
	protected IScrollable getScrollable()
	{
		return (IScrollable) getParent();
	}

	/**
	 * Scroll thickness.
	 *
	 * @return the int
	 */
	protected int scrollThickness()
	{
		return isHorizontal() ? scrollSize.height() : scrollSize.width();
	}

	/**
	 * Scroll length.
	 *
	 * @return the int
	 */
	protected int scrollLength()
	{
		return isHorizontal() ? scrollSize.width() : scrollSize.height();
	}

	/**
	 * Gets the length of this {@link UIScrollBar} (width if {@link Type#HORIZONTAL}, height if {@link Type#VERTICAL}.
	 *
	 * @return the length
	 */
	public int getLength()
	{
		return isHorizontal() ? size().width() : size().height();
	}

	/**
	 * Checks if this {@link UIScrollBar} is {@link Type#HORIZONTAL}.
	 *
	 * @return true, if is horizontal
	 */
	public boolean isHorizontal()
	{
		return type == Type.HORIZONTAL;
	}

	/**
	 * Sets whether this {@link UIScrollBar} should automatically hide when scrolling is not possible (content size is inferior to
	 * component size).
	 *
	 * @param autoHide the auto hide
	 */
	public void setAutoHide(boolean autoHide)
	{
		this.autoHide = autoHide;
	}

	@Override
	public int getZIndex()
	{
		return getParent() != null ? getParent().getZIndex() + zIndex : 0;
	}

	protected int sizeDiff()
	{
		ISize s = parent().size();
		IPosition cp = parent().contentPosition();
		ISize cs = parent().contentSize();
		Padding pad = Padding.of(parent());

		return Math.min((s.height() - pad.vertical() - cp.y() - cs.height()), 0);
	}

	public void setOffset(float offset)
	{
		this.offset = MathHelper.clamp(offset, 0, 1);
	}

	public void setOffset(int offset)
	{
		if (offset == 0)
			this.offset = 0;
		else
			this.offset = (float) offset / sizeDiff();
	}

	/**
	 * Gets the offset of the parent component of this {@link UIScrollBar}.
	 *
	 * @return the offset
	 */
	public float offset()
	{
		return offset;
	}

	public int positionOffset()
	{
		if (offset == 0)
			return 0;

		int diff = sizeDiff();
		if (diff >= 0)
			offset = 0;
		return Math.round(diff * offset);
	}

	/**
	 * Scroll this {@link UIScrollBar} to the specified offset.
	 *
	 * @param offset the offset
	 */
	public void scrollTo(float offset)
	{
		if (!isEnabled())
			return;

		setOffset(offset);
	}

	/**
	 * Scroll this {@link UIScrollBar} by the specified amount.
	 *
	 * @param amount the amount
	 */
	public void scrollBy(float amount)
	{
		scrollTo(offset() + amount);
	}

	/**
	 * Update this {@link UIScrollBar}, hiding and disabling it if necessary, based on content size.
	 */
	public void updateScrollbar()
	{
		UIComponent parent = getParent();
		IScrollable scrollable = getScrollable();
		int delta = 0;// hasVisibleOtherScrollbar() ? scrollThickness() : 0;
		boolean hide = false;
		float offset = offset();
		if (isHorizontal())
		{
			if (scrollable.contentSize().width() <= parent.size().width() - delta)
				hide = true;
		}
		else
		{
			if (scrollable.contentSize().height() <= parent.size().height() - delta)
				hide = true;
		}

		if (hide == isEnabled() || offset < 0)
			scrollTo(0);
		if (offset > 1)
			scrollTo(1);
		//setEnabled(!hide);
		if (autoHide)
			setVisible(!hide);
	}

	@Override
	public void mouseDown(MouseButton button)
	{
		if (button == MouseButton.LEFT)
			scrollToMouse();
	}

	@Override
	public void click(MouseButton button)
	{
		//do not propagate to parent
	}

	@Override
	public void mouseDrag(MouseButton button)
	{
		if (button == MouseButton.LEFT)
			scrollToMouse();
	}

	private void scrollToMouse()
	{
		int l = getLength() - scrollLength() - 2;
		int pos = isHorizontal() ? mousePosition().x() : mousePosition().y();
		pos -= scrollLength() / 2;
		scrollTo((float) pos / l);
	}

	/**
	 * Event fired by the parent
	 *
	 * @param event ScrollWheel event
	 * @return true if the event should propagate to parent
	 */
	protected boolean scrollWheel(ScrollWheel<UIScrollBar> event)
	{
		//we want to propagate to (grand) parents so they can scroll too if needed
		if ((isHorizontal() != GuiScreen.isShiftKeyDown()) && !isHovered())
			return false;

		scrollBy(-event.delta() * getScrollable().getScrollStep());
		float o = offset();
		//if we can't scroll anymore, propagate to parent
		return (event.delta() <= 0 || o != 0) && (event.delta() >= 0 || o != 1);
	}

	protected boolean keyTyped(KeyTypedEvent<UIScrollBar> event)
	{
		if (event.isKey(Keyboard.KEY_HOME))
		{
			scrollTo(0);
			return true;
		}
		if (event.isKey(Keyboard.KEY_END))
		{
			scrollTo(1);
			return true;
		}

		return false;
	}

	@Override
	public String getPropertyString()
	{
		ISize cs = getScrollable().contentSize();
		return type + " | O=" + offset() + "(" + (isHorizontal() ? cs.width() : cs.height()) + ") | " + super.getPropertyString();
	}

	public static UIScrollBar verticalScrollbar(Object component)
	{
		//noinspection SuspiciousMethodCalls
		return verticalScrollbars.get(component);
	}

	public static UIScrollBar horizontalScrollbar(Object component)
	{
		//noinspection SuspiciousMethodCalls
		return horizontalScrollbars.get(component);
	}

	public static int scrollbarWidth(Object component)
	{
		//noinspection SuspiciousMethodCalls
		UIScrollBar scrollbar = verticalScrollbars.get(component);
		return scrollbar != null && scrollbar.isVisible() ? scrollbar.size().width() : 0;
	}

	public static int scrollbarHeight(Object component)
	{
		//noinspection SuspiciousMethodCalls
		UIScrollBar scrollbar = horizontalScrollbars.get(component);
		return scrollbar != null && scrollbar.isVisible() ? scrollbar.size().height() : 0;
	}

	private class ScrollbarPosition implements IPosition
	{
		@Override
		public int x()
		{
			if (isHorizontal())
				return Padding.of(getParent()).left();
			else
				return getParent().size().width() - size().width() - Padding.of(getParent()).right();
		}

		@Override
		public int y()
		{
			if (isHorizontal())
				return getParent().size().height() - size().height();
			else
				return Padding.of(getParent()).right();
		}

		@Override
		public String toString()
		{
			return x() + "," + y();
		}
	}

	private class ScrollbarSize implements ISize
	{
		@Override
		public int width()
		{
			if (!isHorizontal())
				return scrollThickness() + 2;

			return getParent().innerSize().width();
		}

		@Override
		public int height()
		{
			if (isHorizontal())
				return scrollThickness() + 2;

			return getParent().innerSize().height();
		}

		@Override
		public String toString()
		{
			return width() + "," + height();
		}
	}

	private class ScrollPosition implements IPosition
	{
		@Override
		public int x()
		{
			int l = getLength() - scrollLength() - 1;
			return isHorizontal() ? (int) (UIScrollBar.this.offset() * l) + 1 : 1;
		}

		@Override
		public int y()
		{
			int l = getLength() - scrollLength() - 2;
			return isHorizontal() ? 1 : (int) (UIScrollBar.this.offset() * l) + 1;
		}

		@Override
		public String toString()
		{
			return x() + "," + y();
		}
	}

	public static <T extends ISized & IContent> IPosition scrollingOffset(T owner)
	{
		return Position.of(UIScrollBar.horizontalScrolling(owner), UIScrollBar.verticalScrolling(owner));
	}

	public static <T extends ISized & IContent> IntSupplier verticalScrolling(T owner)
	{
		checkNotNull(owner);
		return () -> {
			UIScrollBar scrollbar = verticalScrollbar(owner);
			if (scrollbar == null)
				return 0;
			return scrollbar.positionOffset();
		};
	}

	public static <T extends ISized & IContent> IntSupplier horizontalScrolling(T owner)
	{
		checkNotNull(owner);
		return () -> {
			UIScrollBar scrollbar = horizontalScrollbar(owner);
			if (scrollbar == null)
				return 0;
			return scrollbar.positionOffset();
		};
	}

}

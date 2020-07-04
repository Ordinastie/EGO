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

package net.malisis.ego.gui.component.container;

import static com.google.common.base.Preconditions.*;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.malisis.ego.font.FontOptions;
import net.malisis.ego.font.FontOptions.FontOptionsBuilder;
import net.malisis.ego.gui.EGOGui;
import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.component.UIComponentBuilder;
import net.malisis.ego.gui.component.content.IContent;
import net.malisis.ego.gui.component.control.ICloseable;
import net.malisis.ego.gui.component.control.IScrollable;
import net.malisis.ego.gui.component.control.Title;
import net.malisis.ego.gui.component.layout.ILayout;
import net.malisis.ego.gui.component.scrolling.UIScrollBar;
import net.malisis.ego.gui.component.scrolling.UIScrollBar.Type;
import net.malisis.ego.gui.element.IClipable;
import net.malisis.ego.gui.element.Padding;
import net.malisis.ego.gui.element.position.Position;
import net.malisis.ego.gui.element.position.Position.IPosition;
import net.malisis.ego.gui.element.size.Size;
import net.malisis.ego.gui.element.size.Size.ISize;
import net.malisis.ego.gui.render.GuiRenderer;
import net.malisis.ego.gui.render.IGuiRenderer;
import net.malisis.ego.gui.render.background.BoxBackground;
import net.malisis.ego.gui.render.background.PanelBackground;
import net.malisis.ego.gui.render.background.WindowBackground;
import net.malisis.ego.gui.text.GuiText;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

/**
 * {@link UIContainer} are the base for components holding other components.<br>
 * Child components are drawn in the foreground.<br>
 * Mouse events received are passed to the child concerned (getComponentAt()).<br>
 * Keyboard event are passed to all the children.
 *
 * @author Ordinastie
 */
public class UIContainer extends UIComponent implements IClipable, IScrollable, ICloseable
{
	protected ContainerContent content = new ContainerContent();

	//IClipable
	/** Determines whether this {@link UIContainer} should clip its contents to its drawn area. */
	protected boolean clipContent = true;

	protected final IPosition offset = UIScrollBar.scrollingOffset(this);

	protected ILayout layout;

	/**
	 * Instantiates a new {@link UIContainer}.
	 */
	public UIContainer()
	{
		setForeground(content);
	}

	@Override
	public ContainerContent content()
	{
		return content;
	}

	// #region getters/setters
	@Override
	public IPosition contentPosition()
	{
		return Position.ZERO;
	}

	@Override
	public IPosition offset()
	{
		return offset;
	}

	public void setLayout(ILayout layout)
	{
		this.layout = layout;
	}

	// #end getters/setters

	public List<UIComponent> components()
	{
		return ImmutableList.copyOf(content.components);
	}

	/**
	 * Gets the {@link UIComponent} matching the specified name.
	 *
	 * @param name the name
	 * @return the component
	 */
	@Override
	public UIComponent getComponent(String name)
	{
		if (StringUtils.isEmpty(name))
			return null;

		UIComponent found;
		if ((found = super.getComponent(name)) != null)
			return found;

		for (UIComponent c : content.components)
		{
			if ((found = c.getComponent(name)) != null)
				return found;
		}

		return null;
	}

	/**
	 * Gets the {@link UIComponent} at the specified coordinates.<br>
	 * Selects the component with the highest z-index from the components overlapping the coordinates.
	 *
	 * @param x the x
	 * @param y the y
	 * @return the child component in this {@link UIContainer}, this {@link UIContainer} if none, or null if outside its bounds.
	 */
	@Override
	public UIComponent getComponentAt(int x, int y)
	{
		if (!isEnabled() || !isVisible())
			return null;

		//super impl will return control components or itself
		//control components take precedence over child components
		UIComponent superComp = super.getComponentAt(x, y);
		if (superComp != null && superComp != this)
			return superComp;

		if (shouldClipContent() && !getClipArea().isInside(x, y))
			return superComp;

		return content.components.stream()
								 .map(c -> c.getComponentAt(x, y))
								 .filter(Objects::nonNull)
								 //.filter(UIComponent::isEnabled)
								 .sorted(Comparator.comparingInt(UIComponent::zIndex))
								 .reduce((a, b) -> b)
								 .orElse(superComp);
	}

	//#region IClipable
	@Override
	public ClipArea getClipArea()
	{
		return shouldClipContent() ? ClipArea.from(this) : IClipable.NOCLIP;
	}

	/**
	 * Sets whether this {@link UIContainer} should clip its contents
	 *
	 * @param clipContent if true, clip contents
	 */
	public void setClipContent(boolean clipContent)
	{
		this.clipContent = clipContent;
	}

	/**
	 * Checks whether this {@link UIContainer} should clip its contents
	 *
	 * @return true, if should clip contents
	 */
	public boolean shouldClipContent()
	{
		return clipContent;
	}

	//#end IClipable

	/**
	 * Adds components to this {@link UIContainer}.
	 *
	 * @param components the components
	 */
	public void add(UIComponent... components)
	{
		Arrays.stream(components)
			  .filter(Objects::nonNull)
			  .forEach(content::add);
	}

	/**
	 * Removes the component from this {@link UIContainer}.
	 *
	 * @param component the component
	 */
	public void remove(UIComponent component)
	{
		if (component == null || component.getParent() != this)
			return;

		content.components.remove(component);
		component.setParent(null);

		if (layout != null)
			layout.remove(component);
	}

	/**
	 * Removes all the components from this {@link UIContainer}. Does not remove control components
	 */
	public void removeAll()
	{
		for (UIComponent component : content.components)
		{
			component.setParent(null);
		}
		content.components.clear();
		if (layout != null)
			layout.clear();
	}

	@Override
	public void onAddedToScreen(EGOGui gui)
	{
		this.gui = gui;
		for (UIComponent component : content.components)
		{
			component.onAddedToScreen(gui);
		}
	}

	@Override
	public void onClose()
	{
		if (getParent() instanceof UIContainer)
			((UIContainer) getParent()).remove(this);
	}

	@Override
	public String getPropertyString()
	{
		return super.getPropertyString() + " | O : " + offset;
	}

	public static UIContainerBuilder builder()
	{
		return new UIContainerBuilder();
	}

	/**
	 * Creates a builder with default values for a centered {@link UIContainer} with a window background and a padding of 5.
	 *
	 * @return the UI parent
	 */
	public static UIContainerBuilder window()
	{
		return builder().window();
	}

	/**
	 * Creates a builder with default values for a {@link UIContainer} with a panel background and a padding of 3.
	 *
	 * @return the UI parent
	 */
	public static UIContainerBuilder panel()
	{
		return builder().panel();
	}

	/**
	 * Creates a builder with default values for a {@link UIContainer} with a box background and a padding of 1.
	 *
	 * @return the UI parent
	 */
	public static UIContainerBuilder box()
	{
		return builder().name("Box")
						.background(BoxBackground::new)
						.padding(1);
	}

	public class ContainerContent implements IContent, IGuiRenderer
	{
		/** List of {@link UIComponent} inside this {@link UIContainer}. */
		protected final List<UIComponent> components = Lists.newArrayList();
		protected ISize size = Size.of(this::updateWidth, this::updateHeight);

		public void add(UIComponent component)
		{
			if (components.contains(component))
				return;

			components.add(component);
			components.sort(Comparator.comparingInt(UIComponent::zIndex));
			component.setParent(getParent());

			if (layout != null)
				layout.add(component);
		}

		@Override
		public UIContainer getParent()
		{
			return UIContainer.this;
		}

		@Nonnull
		@Override
		public IPosition position()
		{
			return Position.ZERO;
		}

		@Nonnull
		@Override
		public ISize size()
		{
			return size;
		}

		private int updateWidth()
		{
			return components.stream()
							 .filter(UIComponent::isVisible)
							 .mapToInt(c -> c.x() + c.width())
							 .max()
							 .orElse(16) - padding().left();

		}

		private int updateHeight()
		{
			return components.stream()
							 .filter(UIComponent::isVisible)
							 .mapToInt(c -> c.y() + c.height())
							 .max()
							 .orElse(16) - padding().top();
		}

		@Override
		public void render(GuiRenderer renderer)
		{
			components.forEach(c -> c.render(renderer));
		}
	}

	public static class UIContainerBuilder extends UIContainerBuilderG<UIContainerBuilder, UIContainer>
	{
		@Override
		public UIContainer build()
		{
			return super.build(new UIContainer());
		}
	}

	public abstract static class UIContainerBuilderG<BUILDER extends UIContainerBuilderG<?, ?>, CONTAINER extends UIContainer>
			extends UIComponentBuilder<BUILDER, CONTAINER>
	{
		protected boolean clipContent = true;
		protected Function<CONTAINER, ILayout> layout = c -> null;
		protected BiFunction<CONTAINER, UIScrollBar.Type, UIScrollBar> vertical = null;
		protected BiFunction<CONTAINER, UIScrollBar.Type, UIScrollBar> horizontal = null;
		protected List<UIComponent> children = Lists.newArrayList();
		protected GuiText.Builder titleBuilder = GuiText.builder();
		protected FontOptionsBuilder titleOptionsBuilder = FontOptions.builder();

		protected UIContainerBuilderG()
		{
			margin(0); //containers don't use default margin
			widthOfContent();
			heightOfContent();
			titleOptionsBuilder.color(0x404040);
		}

		@Override
		@SuppressWarnings("unchecked")
		public BUILDER self()
		{
			return (BUILDER) this;
		}

		public BUILDER title(String text)
		{
			titleBuilder.text(text);
			return self();
		}

		public BUILDER title(Supplier<String> supplier)
		{
			titleBuilder.text(supplier);
			return self();
		}

		public BUILDER window()
		{
			name("Window").background(WindowBackground::new)
						  .middleCenter()
						  .padding(5);
			return self();
		}

		public BUILDER panel()
		{
			name("Panel").background(PanelBackground::new)
						 .padding(3);
			return self();
		}

		public BUILDER clipContent(boolean clip)
		{
			clipContent = clip;
			return self();
		}

		public BUILDER layout(Function<CONTAINER, ILayout> layout)
		{
			this.layout = checkNotNull(layout);
			return self();
		}

		public BUILDER verticalScrollbar(BiFunction<CONTAINER, Type, UIScrollBar> scrollbarFactory)
		{
			vertical = scrollbarFactory;
			return self();
		}

		public BUILDER horizontalScrollbar(BiFunction<CONTAINER, Type, UIScrollBar> scrollbarFactory)
		{
			horizontal = scrollbarFactory;
			return self();
		}

		public BUILDER add(UIComponent component)
		{
			children.add(checkNotNull(component));
			return self();
		}

		@Override
		protected CONTAINER build(CONTAINER container)
		{
			super.build(container);

			container.setClipContent(clipContent);
			container.setLayout(layout.apply(container));
			if (vertical != null)
				vertical.apply(container, Type.VERTICAL);
			if (horizontal != null)
				horizontal.apply(container, Type.HORIZONTAL);

			for (UIComponent c : children)
				container.add(c);

			return container;
		}
	}
}

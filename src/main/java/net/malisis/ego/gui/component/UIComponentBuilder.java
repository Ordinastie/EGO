package net.malisis.ego.gui.component;

import static com.google.common.base.Preconditions.*;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.malisis.ego.gui.component.container.UIContainer;
import net.malisis.ego.gui.component.decoration.UITooltip;
import net.malisis.ego.gui.element.position.IPositionBuilder;
import net.malisis.ego.gui.element.position.Position;
import net.malisis.ego.gui.element.position.Position.IPosition;
import net.malisis.ego.gui.element.position.Positions;
import net.malisis.ego.gui.element.size.ISizeBuilder;
import net.malisis.ego.gui.element.size.Size.ISize;
import net.malisis.ego.gui.event.GuiEvent;
import net.malisis.ego.gui.event.MouseEvent.IMouseEventBuilder;
import net.malisis.ego.gui.render.IGuiRenderer;

import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Predicate;

public abstract class UIComponentBuilder<BUILDER extends UIComponentBuilder<?, ?>, COMPONENT extends UIComponent>
		implements IPositionBuilder<BUILDER, COMPONENT>, ISizeBuilder<BUILDER, COMPONENT>, IMouseEventBuilder<BUILDER, COMPONENT>
{

	private Multimap<Class<?>, Predicate<?>> handlers = HashMultimap.create();
	protected String name;
	protected UIComponent parent;
	protected Function<COMPONENT, IPosition> position = null;
	protected Function<COMPONENT, IntSupplier> x = o -> Positions.leftAligned(o, 0);
	protected Function<COMPONENT, IntSupplier> y = o -> Positions.topAligned(o, 0);
	protected Function<COMPONENT, ISize> size = null;
	protected int zIndex;
	protected int alpha = 255;
	protected UITooltip tooltip;
	protected boolean enabled = true;
	protected boolean visible = true;
	protected Function<COMPONENT, IGuiRenderer> background = null;
	protected Function<COMPONENT, IGuiRenderer> foreground = null;
	protected Object data;

	@Override
	@SuppressWarnings("unchecked")
	public BUILDER self()
	{
		return (BUILDER) this;
	}

	@Override
	public <T extends GuiEvent<? extends UIComponent>> void register(Class<T> clazz, Predicate<T> handler)
	{
		handlers.put(clazz, handler);
	}

	public BUILDER name(String name)
	{
		this.name = name;
		return self();
	}

	public BUILDER parent(UIComponent parent)
	{
		this.parent = parent;
		return self();
	}

	@Override
	public BUILDER position(Function<COMPONENT, IPosition> func)
	{
		position = checkNotNull(func);
		return self();
	}

	@Override
	public BUILDER x(Function<COMPONENT, IntSupplier> x)
	{
		this.x = checkNotNull(x);
		position = null;
		return self();
	}

	@Override
	public BUILDER y(Function<COMPONENT, IntSupplier> y)
	{
		this.y = checkNotNull(y);
		position = null;
		return self();
	}

	@Override
	public BUILDER size(Function<COMPONENT, ISize> func)
	{
		size = checkNotNull(func);
		return self();
	}

	public BUILDER zIndex(int zIndex)
	{
		this.zIndex = zIndex;
		return self();
	}

	public BUILDER alpha(int alpha)
	{
		this.alpha = alpha;
		return self();
	}

	public BUILDER tooltip(String text)
	{
		if (text == null)
			return tooltip((UITooltip) null);
		return tooltip(new UITooltip(text));
	}

	public BUILDER tooltip(UITooltip tooltip)
	{
		this.tooltip = tooltip;
		return self();
	}

	public BUILDER enabled(boolean enabled)
	{
		this.enabled = enabled;
		return self();
	}

	public BUILDER visible(boolean visible)
	{
		this.visible = visible;
		return self();
	}

	public BUILDER background(IGuiRenderer background)
	{
		this.background = c -> background;
		return self();
	}

	public BUILDER background(Function<COMPONENT, IGuiRenderer> background)
	{
		this.background = checkNotNull(background);
		return self();
	}

	public BUILDER foreground(IGuiRenderer foreground)
	{
		this.foreground = c -> foreground;
		return self();
	}

	public BUILDER foreground(Function<COMPONENT, IGuiRenderer> foreground)
	{
		this.foreground = checkNotNull(foreground);
		return self();
	}

	public BUILDER data(Object data)
	{
		this.data = data;
		return self();
	}

	protected COMPONENT build(COMPONENT component)
	{
		checkNotNull(component);

		component.setName(name);
		if (position != null)
			component.setPosition(position.apply(component));
		else
			component.setPosition(Position.of(x.apply(component), y.apply(component)));

		if (size != null)
			component.setSize(size.apply(component));
		component.setEnabled(enabled);
		component.setVisible(visible);
		component.setZIndex(zIndex);
		component.setAlpha(alpha);
		if (tooltip != null)
			component.setTooltip(tooltip);

		//renders
		if (background != null)
			component.setBackground(background.apply(component));
		if (foreground != null)
			component.setForeground(foreground.apply(component));

		//extra data
		if (data != null)
			component.attachData(data);

		//events
		handlers.entries()
				.forEach(e -> registerHandler(component, e));

		if (parent instanceof UIContainer)
			((UIContainer) parent).add(component);
		else if (parent != null)
			component.setParent(parent);

		return component;
	}

	@SuppressWarnings("unchecked")
	private <T extends GuiEvent<?>> void registerHandler(UIComponent component, Entry<Class<?>, Predicate<?>> entry)
	{
		component.register((Class<T>) entry.getKey(), (Predicate<T>) entry.getValue());
	}

	public abstract COMPONENT build();
}

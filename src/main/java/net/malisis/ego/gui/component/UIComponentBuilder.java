package net.malisis.ego.gui.component;

import static com.google.common.base.Preconditions.*;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import net.malisis.ego.gui.EGOGui;
import net.malisis.ego.gui.component.container.UIContainer;
import net.malisis.ego.gui.component.control.IControlComponent;
import net.malisis.ego.gui.component.control.UIMoveHandle;
import net.malisis.ego.gui.component.decoration.UITooltip;
import net.malisis.ego.gui.element.Margin;
import net.malisis.ego.gui.element.Margin.InheritedMargin;
import net.malisis.ego.gui.element.Padding;
import net.malisis.ego.gui.element.position.IPositionBuilder;
import net.malisis.ego.gui.element.position.Position;
import net.malisis.ego.gui.element.position.Position.IPosition;
import net.malisis.ego.gui.element.position.Positions;
import net.malisis.ego.gui.element.size.ISizeBuilder;
import net.malisis.ego.gui.element.size.Size;
import net.malisis.ego.gui.element.size.Size.ISize;
import net.malisis.ego.gui.element.size.Sizes;
import net.malisis.ego.gui.event.GuiEvent;
import net.malisis.ego.gui.event.mouse.IMouseEventBuilder;
import net.malisis.ego.gui.render.IGuiRenderer;

import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Predicate;

public abstract class UIComponentBuilder<BUILDER extends UIComponentBuilder<?, ?>, COMPONENT extends UIComponent>
		implements IPositionBuilder<BUILDER, COMPONENT>, ISizeBuilder<BUILDER, COMPONENT>, IMouseEventBuilder<BUILDER, COMPONENT>
{

	private final Multimap<Class<?>, Predicate<?>> handlers = HashMultimap.create();
	protected String name;
	protected UIComponent parent;
	protected final Set<Function<COMPONENT, IControlComponent>> controlComponents = Sets.newHashSet();
	//pos & size
	protected int px, py;
	protected Function<COMPONENT, IntSupplier> x = o -> Positions.leftAligned(o, 0);
	protected Function<COMPONENT, IntSupplier> y = o -> Positions.topAligned(o, 0);
	protected Function<COMPONENT, IPosition> position = this::buildPosition;
	protected Function<COMPONENT, IntSupplier> width = o -> Sizes.parentWidth(o, 1F, 0);
	protected Function<COMPONENT, IntSupplier> height = o -> Sizes.parentHeight(o, 1F, 0);
	protected Function<COMPONENT, ISize> size = o -> Size.of(width.apply(o), height.apply(o));
	protected int pleft, pright, ptop, pbottom;
	protected Function<COMPONENT, Padding> padding = o -> Padding.of(ptop, pbottom, pleft, pright);
	protected int mleft, mright, mtop, mbottom;
	protected Function<COMPONENT, Margin> margin = o -> Margin.of(mtop, mbottom, mleft, mright);

	protected int zIndex;
	protected int color = 0xFFFFFF;
	protected int alpha = 255;
	protected UITooltip tooltip;
	protected BooleanSupplier enabled = () -> true;
	protected BooleanSupplier visible = () -> true;
	protected Function<COMPONENT, IGuiRenderer> background = null;
	protected Function<COMPONENT, IGuiRenderer> foreground = null;
	protected Object data;

	public UIComponentBuilder()
	{
		Margin margin = EGOGui.defaultMargin();
		mleft = margin.left();
		mright = margin.right();
		mtop = margin.top();
		mbottom = margin.bottom();
	}

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
		return self();
	}

	@Override
	public BUILDER y(Function<COMPONENT, IntSupplier> y)
	{
		this.y = checkNotNull(y);
		return self();
	}

	@Override
	public BUILDER size(Function<COMPONENT, ISize> func)
	{
		size = checkNotNull(func);
		return self();
	}

	@Override
	public BUILDER width(Function<COMPONENT, IntSupplier> width)
	{
		this.width = checkNotNull(width);
		return self();
	}

	@Override
	public BUILDER height(Function<COMPONENT, IntSupplier> height)
	{
		this.height = checkNotNull(height);
		return self();
	}

	public BUILDER paddingTop(int padding)
	{
		ptop = padding;
		return self();
	}

	public BUILDER paddingBottom(int padding)
	{
		pbottom = padding;
		return self();
	}

	public BUILDER paddingLeft(int padding)
	{
		pleft = padding;
		return self();
	}

	public BUILDER paddingRight(int padding)
	{
		pright = padding;
		return self();
	}

	public BUILDER padding(Padding padding)
	{
		return padding(o -> padding);
	}

	public BUILDER padding(int padding)
	{
		ptop = pbottom = pleft = pright = padding;
		return self();
	}

	public BUILDER padding(Function<COMPONENT, Padding> func)
	{
		padding = checkNotNull(func);
		return self();
	}

	public BUILDER marginTop(int margin)
	{
		mtop = margin;
		return self();
	}

	public BUILDER marginBottom(int margin)
	{
		mbottom = margin;
		return self();
	}

	public BUILDER marginLeft(int margin)
	{
		mleft = margin;
		return self();
	}

	public BUILDER marginRight(int margin)
	{
		mright = margin;
		return self();
	}

	public BUILDER margin(Margin margin)
	{
		return margin(o -> margin);
	}

	public BUILDER margin(int margin)
	{
		mtop = mbottom = mleft = mright = margin;
		return self();
	}

	public BUILDER inheritMargin()
	{
		return margin(InheritedMargin::new);
	}

	public BUILDER margin(Function<COMPONENT, Margin> func)
	{
		margin = checkNotNull(func);
		return self();
	}

	public BUILDER zIndex(int zIndex)
	{
		this.zIndex = zIndex;
		return self();
	}

	public BUILDER color(int color)
	{
		this.color = color;
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
		return enabled(() -> enabled);
	}

	public BUILDER enabled(BooleanSupplier supplier)
	{
		enabled = checkNotNull(supplier);
		return self();
	}

	public BUILDER visible(BooleanSupplier supplier)
	{
		visible = checkNotNull(supplier);
		return self();

	}

	public BUILDER visible(boolean visible)
	{
		return visible(() -> visible);
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

	public BUILDER movable()
	{
		return withControl(UIMoveHandle.builder()
									   .build());
	}

	public BUILDER withControl(IControlComponent controlComponent)
	{
		return withControl(c -> controlComponent);
	}

	public BUILDER withControl(Function<COMPONENT, IControlComponent> control)
	{
		controlComponents.add(checkNotNull(control));
		return self();
	}

	public BUILDER data(Object data)
	{
		this.data = data;
		return self();
	}

	protected IPosition buildPosition(COMPONENT component)
	{
		return Position.of(px, py, x.apply(component), y.apply(component), true);
	}

	protected COMPONENT build(COMPONENT component)
	{
		checkNotNull(component);

		component.setName(name);
		component.setPosition(position.apply(component));
		component.setSize(size.apply(component));
		component.setPadding(padding.apply(component));
		component.setMargin(margin.apply(component));
		component.setEnabled(enabled);
		component.setVisible(visible);
		component.setZIndex(zIndex);
		component.setColor(color);
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

		controlComponents.stream()
						 .map(f -> f.apply(component))
						 .forEach(component::addControlComponent);

		return component;
	}

	@SuppressWarnings("unchecked")
	private <T extends GuiEvent<?>> void registerHandler(UIComponent component, Entry<Class<?>, Predicate<?>> entry)
	{
		component.register((Class<T>) entry.getKey(), (Predicate<T>) entry.getValue());
	}

	public abstract COMPONENT build();
}

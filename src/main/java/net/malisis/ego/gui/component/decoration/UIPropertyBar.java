package net.malisis.ego.gui.component.decoration;

import static com.google.common.base.Preconditions.*;

import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.element.position.Position;
import net.malisis.ego.gui.element.position.Positions;
import net.malisis.ego.gui.element.size.Size;
import net.malisis.ego.gui.element.size.Size.ISize;
import net.malisis.ego.gui.render.GuiIcon;
import net.malisis.ego.gui.render.IGuiRenderer;
import net.malisis.ego.gui.render.shape.GuiShape;
import net.malisis.ego.gui.text.GuiText;
import net.malisis.ego.gui.text.UITextComponentBuilder;

import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class UIPropertyBar<T> extends UIComponent
{
	protected GuiIcon icon;
	protected int iconColor;
	protected GuiText text;
	protected ISize iconSize = Size.of(10, 10);
	protected Supplier<T> value;
	protected Function<T, Float> converter;

	protected GuiShape iconShape;

	public UIPropertyBar(UIPropertyBarBuilder<T> builder)
	{
		this.value = builder.value;
		this.converter = builder.converter;
		this.icon = builder.icon;
		this.iconColor = builder.iconColor;
		this.text = builder.buildText(this);

		int x = 0;
		GuiShape iconShape = null;
		if (builder.icon != null)
		{
			iconShape = GuiShape.builder(this)
								.middleLeft()
								.size(builder.iconSize)
								.icon(builder.icon)
								.color(iconColor)
								.build();
			x = iconSize.width() + builder.iconSpacing;
		}

		int barX = x;
		GuiShape barBg = GuiShape.builder(this)
								 .x(x)
								 .width(() -> size().width() - barX)
								 .height(() -> size().height())
								 .color(0x000000)
								 .build();
		GuiShape barFg = GuiShape.builder(this)
								 .x(() -> barX + builder.barBorder)
								 .y(1)
								 .width(() -> {
									 float f = converter.apply(value.get());
									 return (int) ((float) (size().width() - barX - builder.barBorder * 2) * f);
								 })
								 .height(() -> size().height() - builder.barBorder * 2)
								 .color(this::getColor)
								 .build();
		IntSupplier px = Positions.centeredTo(text, barBg, 0);
		IntSupplier py = Positions.middleAlignedTo(text, barBg, 0);
		text.setPosition(Position.of(px, py));

		setBackground(IGuiRenderer.of(iconShape)
								  .and(barBg));
		setForeground(barFg.and(r -> text.render(r)));
	}

	public T value()
	{
		return value.get();
	}

	public GuiIcon icon()
	{
		return icon;
	}

	public static IntUIPropertyBarBuilder builder(IntSupplier supplier)
	{
		return new IntUIPropertyBarBuilder(supplier);
	}

	public static <T> UIPropertyBarBuilder<T> builder(Supplier<T> value, Function<T, Float> converter)
	{
		return new UIPropertyBarBuilder<>(value, checkNotNull(converter));
	}

	public static class IntUIPropertyBarBuilder extends UIPropertyBarBuilder<Integer>
	{
		protected IntSupplier max;

		public IntUIPropertyBarBuilder(IntSupplier value)
		{
			super(value::getAsInt, null);
			text("{VALUE} / {MAX}");
		}

		@Override
		public IntUIPropertyBarBuilder self()
		{
			return this;
		}

		public IntUIPropertyBarBuilder max(IntSupplier max)
		{
			this.max = checkNotNull(max);
			return self();
		}

		public IntUIPropertyBarBuilder max(int max)
		{
			this.max = () -> max;
			return self();
		}

		@Override
		public UIPropertyBar<Integer> build()
		{
			if (max == null)
				throw new IllegalStateException("Max value must be set");
			converter = v -> (float) v / max.getAsInt();
			bind("MAX", max::getAsInt);
			return super.build();
		}
	}

	public static class UIPropertyBarBuilder<T> extends UITextComponentBuilder<UIPropertyBarBuilder<T>, UIPropertyBar<T>>
	{
		protected GuiIcon icon;
		protected int iconColor = 0xFFFFFF;
		protected Supplier<T> value;
		protected ISize iconSize = Size.of(10, 10);//TODO: square size based on UIPropertyBar height ?
		protected Function<T, Float> converter;
		protected int barBorder = 1;
		protected int iconSpacing = 2;

		public UIPropertyBarBuilder(Supplier<T> value, Function<T, Float> converter)
		{
			this.value = checkNotNull(value);
			this.converter = converter;

			parentWidth().height(12);
			text("{VALUE}");
			bind("VALUE", value);
		}

		public UIPropertyBarBuilder<T> icon(GuiIcon icon)
		{
			this.icon = icon;
			return this;
		}

		public UIPropertyBarBuilder<T> iconColor(int color)
		{
			this.iconColor = color;
			return this;
		}

		public UIPropertyBarBuilder<T> iconSize(ISize size)
		{
			this.iconSize = checkNotNull(size);
			return this;
		}

		public UIPropertyBarBuilder<T> border(int border)
		{
			this.barBorder = border;
			return this;
		}

		public UIPropertyBarBuilder<T> iconSpacing(int spacing)
		{
			this.iconSpacing = spacing;
			return this;
		}

		@Override
		public UIPropertyBar<T> build()
		{
			return build(new UIPropertyBar<>(this));
		}
	}

}

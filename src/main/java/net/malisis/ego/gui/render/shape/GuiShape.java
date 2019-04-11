/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Ordinastie
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

package net.malisis.ego.gui.render.shape;

import static com.google.common.base.Preconditions.*;

import com.google.common.collect.Maps;
import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.element.IChild;
import net.malisis.ego.gui.element.IOffset;
import net.malisis.ego.gui.element.position.IPositionBuilder;
import net.malisis.ego.gui.element.position.Position;
import net.malisis.ego.gui.element.position.Position.IPosition;
import net.malisis.ego.gui.element.position.Position.IPositioned;
import net.malisis.ego.gui.element.position.Position.ScreenPosition;
import net.malisis.ego.gui.element.position.Positions;
import net.malisis.ego.gui.element.size.ISizeBuilder;
import net.malisis.ego.gui.element.size.Size;
import net.malisis.ego.gui.element.size.Size.ISize;
import net.malisis.ego.gui.element.size.Size.ISized;
import net.malisis.ego.gui.render.GuiIcon;
import net.malisis.ego.gui.render.GuiRenderer;
import net.malisis.ego.gui.render.IGuiRenderer;
import org.apache.commons.lang3.ObjectUtils;

import java.util.EnumMap;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.function.ToIntBiFunction;

/**
 * @author Ordinastie
 */
public class GuiShape implements IGuiRenderer, IPositioned, ISized, IChild<UIComponent>
{
	public static final int CORNER_SIZE = 5;

	private final UIComponent parent;
	private final IPosition position;
	private final ScreenPosition screenPosition;
	private final IntSupplier zIndex;
	private final ISize size;
	private final ToIntBiFunction<FacePosition, VertexPosition> color;
	private final ToIntBiFunction<FacePosition, VertexPosition> alpha;
	private final Supplier<GuiIcon> icon;
	private final int border;

	private GuiShape(UIComponent parent, Function<GuiShape, IPosition> position, Function<GuiShape, IntSupplier> x, Function<GuiShape, IntSupplier> y, IntSupplier zIndex, Function<GuiShape, ISize> size, ToIntBiFunction<FacePosition, VertexPosition> color, ToIntBiFunction<FacePosition, VertexPosition> alpha, Supplier<GuiIcon> icon, int border, boolean fixed)
	{
		this.parent = parent;
		if (position != null)
			this.position = position.apply(this);
		else
			this.position = Position.of(x.apply(this), y.apply(this));
		screenPosition = new ScreenPosition(this, fixed);
		this.zIndex = zIndex;
		this.size = size.apply(this);
		this.color = color;
		this.alpha = alpha;
		this.icon = icon;
		this.border = border;
	}

	@Override
	public IPosition position()
	{
		return position;
	}

	public int getZIndex()
	{
		return zIndex != null ? zIndex.getAsInt() : 0;
	}

	@Override
	public ISize size()
	{
		return size;
	}

	@Override
	public UIComponent getParent()
	{
		return parent;
	}

	public int getColor(FacePosition facePosition, VertexPosition vertexPosition)
	{
		if (color == null)
			return 0xFFFFFF;
		return color.applyAsInt(facePosition, vertexPosition);
	}

	public int getAlpha(FacePosition facePosition, VertexPosition vertexPosition)
	{
		if (alpha == null)
			return 255;
		return alpha.applyAsInt(facePosition, vertexPosition);
	}

	public GuiIcon getIcon()
	{
		if (icon == null)
			return GuiIcon.NONE;
		return ObjectUtils.firstNonNull(icon.get(), GuiIcon.NONE);
	}

	@Override
	public void render(GuiRenderer renderer)
	{
		render(renderer, screenPosition, size());
	}

	public void renderFor(GuiRenderer renderer, UIComponent t)
	{
		render(renderer, t.screenPosition(), t.size());
	}

	public void render(GuiRenderer renderer, IPosition position, ISize size)
	{
		icon.get()
			.bind(renderer);
		for (FacePosition fp : FacePosition.VALUES)
			for (VertexPosition vp : VertexPosition.VALUES)
				addVertexData(fp, vp, position, size);
	}

	/**
	 * Gets the vertex data for this vertex.
	 *
	 * @param fp the fp
	 * @param vp the position
	 */
	public void addVertexData(FacePosition fp, VertexPosition vp, IPosition position, ISize size)
	{
		GuiIcon icon = this.icon.get();

		float x, y, u, v;
		if (border == 0)
		{
			if (fp != FacePosition.CENTER)
				return;

			x = vp.x(size.width());
			y = vp.y(size.height());
			u = icon.interpolatedU(vp.x());
			v = icon.interpolatedV(vp.y());
			//debug show full texture
			//			u = vp.x();
			//			v = vp.y();

		}
		else
		{
			int width = size.width() - 2 * border;
			int height = size.height() - 2 * border;

			x = fp.x(width, border) + vp.x(fp.width(width, border));
			y = fp.y(height, border) + vp.y(fp.height(height, border));

			u = border > 0 ? interpolatedU(fp, vp, icon) : icon.interpolatedU(x);
			v = border > 0 ? interpolatedV(fp, vp, icon) : icon.interpolatedV(y);
		}

		int color = getColor(fp, vp);
		int r = (color >> 16) & 0xFF;
		int g = (color >> 8) & 0xFF;
		int b = (color) & 0xFF;
		color = (b << 16) + (g << 8) + r + (getAlpha(fp, vp) << 24);

		GuiRenderer.BUFFER.addVertexData(new int[] { Float.floatToRawIntBits(position.x() + x),
													 Float.floatToRawIntBits(position.y() + y),
													 Float.floatToRawIntBits(getZIndex()),
													 Float.floatToRawIntBits(u),
													 Float.floatToRawIntBits(v),
													 color });
	}

	private float interpolatedU(FacePosition fp, VertexPosition vp, GuiIcon icon)
	{
		switch (fp.x() + vp.x())
		{
			case 1:
				return icon.pixelU(border);
			case 2:
				return icon.pixelU(-border);
			case 3:
				return icon.interpolatedU(1);
		}
		return icon.interpolatedU(0);
	}

	private float interpolatedV(FacePosition fp, VertexPosition vp, GuiIcon icon)
	{
		switch (fp.y() + vp.y())
		{
			case 1:
				return icon.pixelV(border);
			case 2:
				return icon.pixelV(-border);
			case 3:
				return icon.interpolatedV(1);
		}
		return icon.interpolatedV(0);
	}

	public static Builder builder()
	{
		return new Builder();
	}

	public static Builder builder(UIComponent component)
	{
		return new Builder().forComponent(component);
	}

	public static class Builder implements IPositionBuilder<Builder, GuiShape>, ISizeBuilder<Builder, GuiShape>
	{
		private UIComponent component;
		private boolean fixed = true;
		private Function<GuiShape, IPosition> position = s -> Position.ZERO;
		private Function<GuiShape, IntSupplier> x = o -> Positions.leftAligned(o, 0);
		private Function<GuiShape, IntSupplier> y = o -> Positions.topAligned(o, 0);
		private Function<GuiShape, ISize> size = Size::relativeTo;
		private IntSupplier zIndex;
		private ToIntBiFunction<FacePosition, VertexPosition> color = (fp, vp) -> 0xFFFFFF;
		private ToIntBiFunction<FacePosition, VertexPosition> alpha = (fp, vp) -> 255;
		private EnumMap<VertexPosition, Integer> colors = Maps.newEnumMap(VertexPosition.class);
		private EnumMap<VertexPosition, Integer> alphas = Maps.newEnumMap(VertexPosition.class);
		private Supplier<GuiIcon> icon = () -> GuiIcon.NONE;
		private int borderColor;
		private int borderAlpha;
		private int borderSize;

		private Builder forComponent(UIComponent component)
		{
			this.component = component;
			size = s -> Size.relativeTo(component);
			color = (fp, vp) -> component.getColor();
			alpha = (fp, vp) -> component.getAlpha();
			zIndex = component::zIndex;
			return this;
		}

		@Override
		public Builder position(Function<GuiShape, IPosition> func)
		{
			position = checkNotNull(func);
			return this;
		}

		@Override
		public Builder x(Function<GuiShape, IntSupplier> x)
		{
			this.x = checkNotNull(x);
			position = null;
			return this;
		}

		@Override
		public Builder y(Function<GuiShape, IntSupplier> y)
		{
			this.y = checkNotNull(y);
			position = null;
			return this;
		}

		/**
		 * Whether the {@link GuiShape} position will be relative to the component {@link IOffset}. If set to true (default), the
		 * GuiShape position will match the component regardless of the scrolling offset.
		 *
		 * @param fixed
		 * @return
		 */
		public Builder fixed(boolean fixed)
		{
			this.fixed = fixed;
			return this;
		}

		public Builder zIndex(int z)
		{
			zIndex = () -> z;
			return this;
		}

		public Builder zIndex(IntSupplier supplier)
		{
			zIndex = checkNotNull(supplier);
			return this;
		}

		@Override
		public Builder size(Function<GuiShape, ISize> func)
		{
			size = checkNotNull(func);
			return this;
		}

		//COLOR
		public Builder color(int c)
		{
			return color((fp, vp) -> c);
		}

		public Builder color(IntSupplier supplier)
		{
			checkNotNull(supplier);
			return color((fp, vp) -> supplier.getAsInt());
		}

		public Builder color(ToIntBiFunction<FacePosition, VertexPosition> func)
		{
			checkNotNull(func);
			color = colorFunction(func);
			return this;
		}

		//per vertex colors
		private void putColor(VertexPosition position, int color)
		{
			colors.put(position, color);
			color((f, v) -> colors.getOrDefault(v, 0xFFFFFF));
		}

		public Builder color(VertexPosition position, int color)
		{
			putColor(position, color);
			return this;
		}

		public Builder topColor(int color)
		{
			putColor(VertexPosition.TOPLEFT, color);
			putColor(VertexPosition.TOPRIGHT, color);
			return this;
		}

		public Builder bottomColor(int color)
		{
			putColor(VertexPosition.BOTTOMLEFT, color);
			putColor(VertexPosition.BOTTOMRIGHT, color);
			return this;
		}

		public Builder leftColor(int color)
		{
			putColor(VertexPosition.TOPLEFT, color);
			putColor(VertexPosition.BOTTOMLEFT, color);
			return this;
		}

		public Builder rightColor(int color)
		{
			putColor(VertexPosition.TOPRIGHT, color);
			putColor(VertexPosition.BOTTOMRIGHT, color);
			return this;
		}

		//ALPHA
		public Builder alpha(int a)
		{
			alpha = (fp, vp) -> a;
			return this;
		}

		public Builder alpha(IntSupplier supplier)
		{
			checkNotNull(supplier);
			alpha = (fp, vp) -> supplier.getAsInt();
			return this;
		}

		public Builder alpha(ToIntBiFunction<FacePosition, VertexPosition> func)
		{
			checkNotNull(func);
			alpha = alphaFunction(func);
			return this;
		}

		//per vertex alpha
		private void putAlpha(VertexPosition position, int alpha)
		{
			alphas.put(position, alpha);
			alpha((f, v) -> alphas.getOrDefault(v, 0xFFFFFF));
		}

		public Builder alphaFor(VertexPosition position, int alpha)
		{
			putAlpha(position, alpha);
			return this;
		}

		public Builder topAlpha(int alpha)
		{
			putAlpha(VertexPosition.TOPLEFT, alpha);
			putAlpha(VertexPosition.TOPRIGHT, alpha);
			return this;
		}

		public Builder bottomAlpha(int alpha)
		{
			putAlpha(VertexPosition.BOTTOMLEFT, alpha);
			putAlpha(VertexPosition.BOTTOMRIGHT, alpha);
			return this;
		}

		public Builder leftAlpha(int alpha)
		{
			putAlpha(VertexPosition.TOPLEFT, alpha);
			putAlpha(VertexPosition.BOTTOMLEFT, alpha);
			return this;
		}

		public Builder rightAlpha(int alpha)
		{
			putAlpha(VertexPosition.TOPRIGHT, alpha);
			putAlpha(VertexPosition.BOTTOMRIGHT, alpha);
			return this;
		}

		//BORDER
		public Builder border(int size)
		{
			borderSize = size;
			return this;
		}

		public Builder border(int size, int color, int alpha)
		{
			borderColor = color;
			borderSize = size;
			borderAlpha = alpha;
			if (icon == null)
				icon(GuiIcon.NONE);

			this.color = colorFunction(this.color);
			this.alpha = alphaFunction(this.alpha);

			return this;
		}

		public Builder border(int size, int color)
		{
			return border(size, color, 255);
		}

		private ToIntBiFunction<FacePosition, VertexPosition> colorFunction(ToIntBiFunction<FacePosition, VertexPosition> color)
		{
			if (borderSize == 0)
				return color;
			return (fp, vp) -> fp != FacePosition.CENTER ? borderColor : color.applyAsInt(fp, vp);
		}

		private ToIntBiFunction<FacePosition, VertexPosition> alphaFunction(ToIntBiFunction<FacePosition, VertexPosition> alpha)
		{
			if (borderSize == 0)
				return alpha;
			return (fp, vp) -> fp != FacePosition.CENTER ? borderAlpha : alpha.applyAsInt(fp, vp);
		}

		public Builder icon(GuiIcon i)
		{
			icon = () -> i;
			return this;
		}

		public Builder icon(Supplier<GuiIcon> supplier)
		{
			icon = checkNotNull(supplier);
			return this;
		}

		public GuiShape build()
		{
			return new GuiShape(component, position, x, y, zIndex, size, color, alpha, icon, borderSize, fixed);
		}
	}
}
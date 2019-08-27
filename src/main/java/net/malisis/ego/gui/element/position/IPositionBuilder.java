/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Ordinastie
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

package net.malisis.ego.gui.element.position;

import static com.google.common.base.Preconditions.*;

import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.component.UIComponentBuilder;
import net.malisis.ego.gui.element.IChild;
import net.malisis.ego.gui.element.position.Position.IPosition;
import net.malisis.ego.gui.element.position.Position.IPositioned;
import net.malisis.ego.gui.element.size.Size.ISized;
import net.malisis.ego.gui.render.shape.GuiShape;
import net.malisis.ego.gui.text.GuiText;

import java.util.function.Function;
import java.util.function.IntSupplier;

/**
 * This interface is a helper for Builders that build objects implementing the {@link Position.IPositioned} interface.
 *
 * @author Ordinastie
 * @see GuiText.Builder
 * @see GuiShape.Builder
 * @see UIComponentBuilder
 */
public interface IPositionBuilder<BUILDER, OWNER extends IPositioned & ISized & IChild<UIComponent>>
{
	public BUILDER position(Function<OWNER, IPosition> func);

	public BUILDER x(Function<OWNER, IntSupplier> func);

	public BUILDER y(Function<OWNER, IntSupplier> func);

	public default BUILDER position(IPosition position)
	{
		checkNotNull(position);
		return position(o -> position);
	}

	public default BUILDER position(int x, int y)
	{
		return position(o -> Position.of(x, y));
	}

	public default BUILDER x(int x)
	{
		return x(o -> () -> x);
	}

	public default BUILDER y(int y)
	{
		return y(o -> () -> y);
	}

	public default BUILDER x(IntSupplier func)
	{
		return x(s -> func);
	}

	public default BUILDER y(IntSupplier func)
	{
		return y(s -> func);
	}

	public default BUILDER topLeft()
	{
		return topLeft(0, 0);
	}

	public default BUILDER topLeft(int leftSpacing, int topSpacing)
	{
		leftAligned(leftSpacing);
		return topAligned(topSpacing);
	}

	public default BUILDER topCenter()
	{
		return topCenter(0, 0);
	}

	public default BUILDER topCenter(int centerOffset, int topSpacing)
	{
		centered(centerOffset);
		return topAligned(topSpacing);
	}

	public default BUILDER topRight()
	{
		return topRight(0, 0);
	}

	public default BUILDER topRight(int rightSpacing, int topSpacing)
	{
		rightAligned(rightSpacing);
		return topAligned(topSpacing);
	}

	public default BUILDER middleLeft()
	{
		return middleLeft(0, 0);
	}

	public default BUILDER middleLeft(int leftSpacing, int middleOffset)
	{
		leftAligned(leftSpacing);
		return middleAligned(middleOffset);
	}

	public default BUILDER middleCenter()
	{
		return middleCenter(0, 0);
	}

	public default BUILDER middleCenter(int centerOffset, int middleOffset)
	{
		centered(centerOffset);
		return middleAligned(middleOffset);
	}

	public default BUILDER middleRight()
	{
		return middleRight(0, 0);
	}

	public default BUILDER middleRight(int rightSpacing, int middleOffset)
	{
		rightAligned(rightSpacing);
		return middleAligned(middleOffset);
	}

	public default BUILDER bottomLeft()
	{
		return bottomLeft(0, 0);
	}

	public default BUILDER bottomLeft(int leftSpacing, int bottomSpacing)
	{
		leftAligned(leftSpacing);
		return bottomAligned(bottomSpacing);
	}

	public default BUILDER bottomCenter()
	{
		return bottomCenter(0, 0);
	}

	public default BUILDER bottomCenter(int centerOffset, int bottomSpacing)
	{
		centered(centerOffset);
		return bottomAligned(bottomSpacing);
	}

	public default BUILDER bottomRight()
	{
		return bottomRight(0, 0);
	}

	public default BUILDER bottomRight(int rightSpacing, int bottomSpacing)
	{
		rightAligned(rightSpacing);
		return bottomAligned(bottomSpacing);
	}

	//X alignment inside parent
	public default BUILDER leftAligned()
	{
		return x(o -> Positions.leftAligned(o, 0));
	}

	public default BUILDER leftAligned(int spacing)
	{
		return x(o -> Positions.leftAligned(o, spacing));
	}

	public default BUILDER centered()
	{
		return x(o -> Positions.centered(o, 0));
	}

	public default BUILDER centered(int offset)
	{
		return x(o -> Positions.centered(o, offset));
	}

	public default BUILDER rightAligned()
	{
		return x(o -> Positions.rightAligned(o, 0));
	}

	public default BUILDER rightAligned(int spacing)
	{
		return x(o -> Positions.rightAligned(o, spacing));
	}

	//Y alignment inside parent
	public default BUILDER topAligned()
	{
		return y(o -> Positions.topAligned(o, 0));
	}

	public default BUILDER topAligned(int spacing)
	{
		return y(o -> Positions.topAligned(o, spacing));
	}

	public default BUILDER middleAligned()
	{
		return y(o -> Positions.middleAligned(o, 0));
	}

	public default BUILDER middleAligned(int offset)
	{
		return y(o -> Positions.middleAligned(o, offset));
	}

	public default BUILDER bottomAligned()
	{
		return y(o -> Positions.bottomAligned(o, 0));
	}

	public default BUILDER bottomAligned(int spacing)
	{
		return y(o -> Positions.bottomAligned(o, spacing));
	}

	//X relative to other
	public default BUILDER leftOf(IPositioned other)
	{
		return x(o -> Positions.leftOf(o, other, 0));
	}

	public default BUILDER leftOf(IPositioned other, int spacing)
	{
		return x(o -> Positions.leftOf(o, other, spacing));
	}

	public default <T extends IPositioned & ISized> BUILDER rightOf(T other)
	{
		return x(o -> Positions.rightOf(other, 0));
	}

	public default <T extends IPositioned & ISized> BUILDER rightOf(T other, int spacing)
	{
		return x(o -> Positions.rightOf(other, spacing));
	}

	//Y relative to other
	public default BUILDER above(IPositioned other)
	{
		return y(o -> Positions.above(o, other, 0));
	}

	public default BUILDER above(IPositioned other, int spacing)
	{
		return y(o -> Positions.above(o, other, spacing));
	}

	public default <T extends IPositioned & ISized> BUILDER below(T other)
	{
		return y(o -> Positions.below(other, 0));
	}

	public default <T extends IPositioned & ISized> BUILDER below(T other, int spacing)
	{
		return y(o -> Positions.below(other, spacing));
	}

	//X alignment relative to other
	public default BUILDER leftAlignedTo(IPositioned other)
	{
		return x(o -> Positions.leftAlignedTo(other, 0));
	}

	public default BUILDER leftAlignedTo(IPositioned other, int offset)
	{
		return x(o -> Positions.leftAlignedTo(other, offset));
	}

	public default <T extends IPositioned & ISized> BUILDER rightAlignedTo(T other)
	{
		return x(o -> Positions.rightAlignedTo(o, other, 0));
	}

	public default <T extends IPositioned & ISized> BUILDER rightAlignedTo(T other, int offset)
	{
		return x(o -> Positions.rightAlignedTo(o, other, offset));
	}

	public default <T extends IPositioned & ISized> BUILDER centeredTo(T other)
	{
		return x(o -> Positions.centeredTo(o, other, 0));
	}

	public default <T extends IPositioned & ISized> BUILDER centeredTo(T other, int offset)
	{
		return x(o -> Positions.centeredTo(o, other, offset));
	}

	//Y alignment relative to other
	public default BUILDER topAlignedTo(IPositioned other)
	{
		return y(o -> Positions.topAlignedTo(other, 0));
	}

	public default BUILDER topAlignedTo(IPositioned other, int offset)
	{
		return y(o -> Positions.topAlignedTo(other, offset));
	}

	public default <T extends IPositioned & ISized> BUILDER bottomAlignedTo(T other)
	{
		return y(o -> Positions.bottomAlignedTo(o, other, 0));
	}

	public default <T extends IPositioned & ISized> BUILDER bottomAlignedTo(T other, int offset)
	{
		return y(o -> Positions.bottomAlignedTo(o, other, offset));
	}

	public default <T extends IPositioned & ISized> BUILDER middleAlignedTo(T other)
	{
		return y(o -> Positions.middleAlignedTo(o, other, 0));
	}

	public default <T extends IPositioned & ISized> BUILDER middleAlignedTo(T other, int offset)
	{
		return y(o -> Positions.middleAlignedTo(o, other, offset));
	}

	//X relative to center
	public default BUILDER leftOfCenter()
	{
		return x(o -> Positions.leftOfCenter(o, 0));
	}

	public default BUILDER leftOfCenter(int offset)
	{
		return x(o -> Positions.leftOfCenter(o, offset));
	}

	public default BUILDER rightOfCenter()
	{
		return x(o -> Positions.rightOfCenter(o, 0));
	}

	public default BUILDER rightOfCenter(int offset)
	{
		return x(o -> Positions.rightOfCenter(o, offset));
	}
}

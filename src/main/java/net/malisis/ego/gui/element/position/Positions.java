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
import net.malisis.ego.gui.element.IChild;
import net.malisis.ego.gui.element.Margin;
import net.malisis.ego.gui.element.Padding;
import net.malisis.ego.gui.element.position.Position.IPosition;
import net.malisis.ego.gui.element.position.Position.IPositioned;
import net.malisis.ego.gui.element.size.Size.ISized;

import java.util.function.IntSupplier;

/**
 * This class contains helper functions for positioning of elements.
 *
 * @author Ordinastie
 */
public class Positions
{
	//position inside parent

	/**
	 * Positions the owner to the left inside its parent.<br>
	 * Respects the parent padding.
	 *
	 * @param spacing the spacing
	 * @return the int supplier
	 */
	public static IntSupplier leftAligned(IChild<?> owner, int spacing)
	{
		return () -> Padding.of(owner.getParent())
							.left() + spacing;
	}

	/**
	 * Positions the owner to the right inside its parent.<br>
	 * Respects the parent padding.
	 *
	 * @param <T> the generic type
	 * @param <U> the generic type
	 * @param spacing the spacing
	 * @return the int supplier
	 */
	public static <T extends ISized & IChild<U>, U extends ISized> IntSupplier rightAligned(T owner, int spacing)
	{

		return () -> {
			U parent = owner.getParent();
			if (parent == null)
				return 0;
			return parent.width() - owner.width() - Padding.of(parent)
														   .right() - spacing;
		};
	}

	/**
	 * Positions the owner in the center of its parent.<br>
	 * Respects the parent padding.
	 *
	 * @param <T> the generic type
	 * @param <U> the generic type
	 * @param offset the offset
	 * @return the int supplier
	 */
	public static <T extends ISized & IChild<U>, U extends ISized> IntSupplier centered(T owner, int offset)
	{
		return () -> {
			U parent = owner.getParent();
			if (parent == null)
				return 0;
			return (parent.width() - Padding.of(parent)
											.horizontal() - owner.width()) / 2 + offset + Padding.of(parent)
																								 .left();
		};
	}

	/**
	 * Positions the owner to the top inside its parent.<br>
	 * Respects the parent padding.
	 *
	 * @param spacing the spacing
	 * @return the int supplier
	 */
	public static IntSupplier topAligned(IChild<?> owner, int spacing)
	{
		return () -> Padding.of(owner.getParent())
							.top() + spacing;
	}

	/**
	 * Positions the owner to the bottom inside its parent.<br>
	 * Respects the parent padding.
	 *
	 * @param <T> the generic type
	 * @param <U> the generic type
	 * @param spacing the spacing
	 * @return the int supplier
	 */
	public static <T extends ISized & IChild<U>, U extends ISized> IntSupplier bottomAligned(T owner, int spacing)
	{

		return () -> {
			U parent = owner.getParent();
			if (owner.getParent() == null)
				return 0;
			return parent.height() - owner.height() - Padding.of(parent)
															 .bottom() - spacing;
		};

	}

	/**
	 * Positions the owner to the bottom inside its parent.<br>
	 * Respects the parent padding.
	 *
	 * @param <T> the generic type
	 * @param <U> the generic type
	 * @param offset the offset
	 * @return the int supplier
	 */
	public static <T extends ISized & IChild<U>, U extends ISized> IntSupplier middleAligned(T owner, int offset)
	{
		return () -> {
			U parent = owner.getParent();
			if (owner.getParent() == null)
				return 0;
			return (int) (Math.ceil(((float) parent.height() - Padding.of(parent)
																	  .vertical() - owner.height()) / 2) + offset + Padding.of(parent)
																														   .top());
		};
	}

	//relative position to other

	/**
	 * Positions the owner to the left of the other.
	 *
	 * @param other the other
	 * @param spacing the spacing
	 * @return the int supplier
	 */
	public static IntSupplier leftOf(ISized owner, IPositioned other, int spacing)
	{
		checkNotNull(other);
		return () -> other.x() - owner.width() - Margin.horizontal(owner, other) - spacing;
	}

	/**
	 * Positions the owner to the right of the other.
	 *
	 * @param <T> the generic type
	 * @param other the other
	 * @param spacing the spacing
	 * @return the int supplier
	 */
	public static <T extends IPositioned & ISized> IntSupplier rightOf(Object owner, T other, int spacing)
	{
		checkNotNull(other);
		return () -> other.x() + other.width() + Margin.horizontal(other, owner) + spacing;
	}

	/**
	 * Positions the owner above the other.
	 *
	 * @param other the other
	 * @param spacing the spacing
	 * @return the int supplier
	 */
	public static IntSupplier above(ISized owner, IPositioned other, int spacing)
	{
		checkNotNull(other);
		return () -> other.y() - owner.height() - Margin.vertical(owner, other) - spacing;
	}

	/**
	 * Positions the owner below the other.
	 *
	 * @param <T> the generic type
	 * @param other the other
	 * @param spacing the spacing
	 * @return the int supplier
	 */
	public static <T extends IPositioned & ISized> IntSupplier below(Object owner, T other, int spacing)
	{
		checkNotNull(other);
		return () -> other.y() + other.height() + Margin.vertical(other, owner) + spacing;

	}

	//alignment relative to another component

	/**
	 * Left aligns the owner to the other.
	 *
	 * @param other the other
	 * @param offset the offset
	 * @return the int supplier
	 */
	public static IntSupplier leftAlignedTo(IPositioned other, int offset)
	{
		checkNotNull(other);
		return () -> other.x() + offset;
	}

	/**
	 * Right aligns the owner to the other.
	 *
	 * @param <T> the generic type
	 * @param other the other
	 * @param offset the offset
	 * @return the int supplier
	 */
	public static <T extends IPositioned & ISized> IntSupplier rightAlignedTo(ISized owner, T other, int offset)
	{
		checkNotNull(other);
		return () -> other.x() + other.width() - owner.width() + offset;
	}

	/**
	 * Centers the owner to the other.
	 *
	 * @param <T> the generic type
	 * @param other the other
	 * @param offset the offset
	 * @return the int supplier
	 */
	public static <T extends IPositioned & ISized> IntSupplier centeredTo(ISized owner, T other, int offset)
	{
		checkNotNull(other);
		return () -> other.x() + (other.width() - owner.width()) / 2 + offset;
	}

	/**
	 * Top aligns the owner to the other.
	 *
	 * @param other the other
	 * @param offset the offset
	 * @return the int supplier
	 */
	public static IntSupplier topAlignedTo(IPositioned other, int offset)
	{
		checkNotNull(other);
		return () -> other.y() + offset;

	}

	/**
	 * Bottom aligns the owner to the other.
	 *
	 * @param <T> the generic type
	 * @param other the other
	 * @param offset the offset
	 * @return the int supplier
	 */
	public static <T extends IPositioned & ISized> IntSupplier bottomAlignedTo(ISized owner, T other, int offset)
	{

		checkNotNull(other);
		return () -> other.y() + other.height() - owner.height() + offset;

	}

	/**
	 * Middle aligns the owner to the other.
	 *
	 * @param <T> the generic type
	 * @param other the other
	 * @param offset the offset
	 * @return the int supplier
	 */
	public static <T extends IPositioned & ISized> IntSupplier middleAlignedTo(ISized owner, T other, int offset)
	{
		checkNotNull(other);
		return () -> (int) (other.y() + Math.ceil(((float) other.height() - owner.height()) / 2) + offset);
	}

	/**
	 * Positions the owner to the left of the center of its parent.<br>
	 * Respects the parent padding.
	 *
	 * @param <T> the generic type
	 * @param <U> the generic type
	 * @param offset the offset
	 * @return the int supplier
	 */
	public static <T extends ISized & IChild<U>, U extends ISized> IntSupplier leftOfCenter(T owner, int offset)
	{
		return () -> {
			U parent = owner.getParent();
			if (parent == null)
				return 0;
			return (parent.width() - Padding.of(parent)
											.horizontal()) / 2 - owner.width() + offset;
		};
	}

	/**
	 * Positions the owner to the right of the center of its parent.<br>
	 * Respects the parent padding.
	 *
	 * @param <T> the generic type
	 * @param <U> the generic type
	 * @param offset the offset
	 * @return the int supplier
	 */
	public static <T extends ISized & IChild<U>, U extends ISized> IntSupplier rightOfCenter(T owner, int offset)
	{
		return () -> {
			U parent = owner.getParent();
			if (parent == null)
				return 0;
			return (parent.width() - Padding.of(parent)
											.horizontal()) / 2 + offset;
		};
	}

	/**
	 * Of.
	 *
	 * @param component the component
	 * @return the i position
	 */
	public static IPosition of(UIComponent component)
	{
		return of(component, 0, 0);
	}

	/**
	 * Of.
	 *
	 * @param component the component
	 * @param xOffset the x offset
	 * @param yOffset the y offset
	 * @return the i position
	 */
	public static IPosition of(UIComponent component, int xOffset, int yOffset)
	{
		return component.screenPosition()
						.offset(xOffset, yOffset);
	}
}

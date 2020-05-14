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

package net.malisis.ego.gui.text;

import net.malisis.ego.font.FontOptions;
import net.malisis.ego.font.EGOFont;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author Ordinastie
 */
public class PredicatedFontOptions extends FontOptions
{
	private final List<Pair<Predicate<Object>, FontOptions>> suppliers;
	private final Object param;

	public PredicatedFontOptions(FontOptionsBuilder builder, List<Pair<Predicate<Object>, FontOptions>> suppliers, Object param)
	{
		super(builder);
		this.suppliers = suppliers;
		this.param = param;
	}

	private <T> T get(Function<FontOptions, T> func, T superValue)
	{
		return suppliers.stream()
						.filter(p -> p.getLeft()
									  .test(param))
						.findFirst()
						.map(Pair::getRight)
						.map(func)
						.orElse(superValue);
	}

	@Override
	public EGOFont getFont()
	{
		return get(FontOptions::getFont, super.getFont());
	}

	/**
	 * Gets the font scale for this {@link FontOptions}.
	 *
	 * @return the font scale
	 */
	@Override
	public float getFontScale()
	{
		return get(FontOptions::getFontScale, super.getFontScale());
	}

	/**
	 * Checks if this {@link FontOptions} is bold.
	 *
	 * @return true, if is bold
	 */
	@Override
	public boolean isBold()
	{
		return get(FontOptions::isBold, super.isBold());
	}

	/**
	 * Checks if this {@link FontOptions} is italic.
	 *
	 * @return true, if is italic
	 */
	@Override
	public boolean isItalic()
	{
		return get(FontOptions::isItalic, super.isItalic());
	}

	/**
	 * Checks if this {@link FontOptions} is underlined.
	 *
	 * @return true, if is underline
	 */
	@Override
	public boolean isUnderline()
	{
		return get(FontOptions::isUnderline, super.isUnderline());
	}

	/**
	 * Checks if this {@link FontOptions} is strikethrough.
	 *
	 * @return true, if is strikethrough
	 */
	@Override
	public boolean isStrikethrough()
	{
		return get(FontOptions::isStrikethrough, super.isStrikethrough());
	}

	/**
	 * Checks if this {@link FontOptions} is obfuscated.
	 *
	 * @return true, if is obfuscated
	 */
	@Override
	public boolean isObfuscated()
	{
		return get(FontOptions::isObfuscated, super.isObfuscated());
	}

	/**
	 * Checks whether draw shadow is enabled for this {@link FontOptions}.
	 *
	 * @return true, if successful
	 */
	@Override
	public boolean hasShadow()
	{
		return get(FontOptions::hasShadow, super.hasShadow());
	}

	/**
	 * Gets the color for this {@link FontOptions}.
	 *
	 * @return the color
	 */
	@Override
	public int getColor()
	{
		return get(FontOptions::getColor, super.getColor());
	}

	@Override
	public boolean isRightAligned()
	{
		return get(FontOptions::isRightAligned, super.isRightAligned());
	}

	@Override
	public FontOptionsBuilder toBuilder()
	{
		FontOptionsBuilder builder = super.toBuilder()
										  .withPredicateParameter(param);
		suppliers.forEach(p -> builder.when(p.getLeft(), p.getRight()));
		return builder;
	}
}

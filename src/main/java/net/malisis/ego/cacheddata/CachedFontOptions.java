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

package net.malisis.ego.cacheddata;

import static com.google.common.base.Preconditions.*;

import net.malisis.ego.font.EGOFont;
import net.malisis.ego.font.FontOptions;

import java.util.function.Supplier;

/**
 * This class allows custom handling for data that needs to be cached and checked for changes.<br>
 * {@link #update()} must be called to refresh the data before calling {@link #hasChanged()}.
 *
 * @author Ordinastie
 */
public class CachedFontOptions implements ICachedData<FontOptions>
{
	/** Supplier to fetch the current {@link FontOptions}. */
	protected Supplier<FontOptions> getter;
	/** FontOptions at the last update. */
	protected FontOptions lastFontOptions;
	/** Current data. */
	protected FontOptions currentFontOptions;

	protected boolean lastBold;
	protected float lastFontScale;
	protected EGOFont lastFont;

	public CachedFontOptions(Supplier<FontOptions> getter)
	{
		this.getter = checkNotNull(getter);
		currentFontOptions = null;
	}

	/**
	 * Gets the current data.
	 *
	 * @return the t
	 */
	@Override
	public FontOptions get()
	{
		return currentFontOptions;
	}

	/**
	 * Updates the current data.
	 */
	@Override
	public void update()
	{

		lastFontOptions = currentFontOptions;
		currentFontOptions = getter.get();
	}

	/**
	 * Checks whether the data has changed since the last update.
	 *
	 * @return true, if data has changed
	 */
	@Override
	public boolean hasChanged()
	{
		boolean changed = lastBold != get().isBold() || lastFontScale != get().getFontScale() || lastFont != get().getFont();

		lastBold = get().isBold();
		lastFontScale = get().getFontScale();
		lastFont = get().getFont();

		return changed;
	}
}

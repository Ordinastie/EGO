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

package net.malisis.ego.font;

import static com.google.common.base.Preconditions.*;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

/**
 * @author Ordinastie
 */
public class FontOptions
{
	public static final FontOptions EMPTY = FontOptions.builder()
													   .build();

	/** Map of TextFormatting **/
	protected static Map<Character, TextFormatting> charFormats = new HashMap<>();
	/** List of ECF colors **/
	protected static int[] colors = new int[32];

	static
	{
		//could reflect to get TextFormatting.formattingCodeMapping instead
		for (TextFormatting ecf : TextFormatting.values())
		{
			charFormats.put(ecf.toString()
							   .charAt(1), ecf);
		}

		//build colors for ECF
		for (int i = 0; i < 16; ++i)
		{
			int j = (i >> 3 & 1) * 85;
			int r = (i >> 2 & 1) * 170 + j;
			int g = (i >> 1 & 1) * 170 + j;
			int b = (i & 1) * 170 + j;

			if (i == 6) //GOLD
				r += 85;

			colors[i] = (r & 255) << 16 | (g & 255) << 8 | b & 255;
		}
	}

	protected final EGOFont font;
	/** Scale for the font **/
	protected final Float fontScale;
	/** Color of the text **/
	protected final Integer color;
	/** Draw with shadow **/
	protected final Boolean shadow;
	/** Use bold font **/
	protected final Boolean bold;
	/** Use italic font **/
	protected final Boolean italic;
	/** Underline the text **/
	protected final Boolean underline;
	/** Strike through the text **/
	protected final Boolean strikethrough;
	/** Obfuscated text */
	protected final Boolean obfuscated;
	/** Space between each character. */
	protected final Integer charSpacing;
	/** Space between each line. */
	protected final Integer lineSpacing;
	/** Right aligned. */
	protected final Boolean rightAligned;
	/* Obfuscated char list */
	protected String obfuscatedCharList = null;

	protected List<FontOptions> predicates = Lists.newArrayList();

	protected FontOptions base;
	protected Predicate<Object> predicate;
	protected Object predicateParam;

	protected FontOptions(FontOptionsBuilder builder, Object predicateParam)
	{
		font = builder.font;
		fontScale = builder.fontScale;
		color = builder.color;
		shadow = builder.shadow;
		bold = builder.bold;
		italic = builder.italic;
		underline = builder.underline;
		strikethrough = builder.strikethrough;
		obfuscated = builder.obfuscated;
		charSpacing = builder.charSpacing;
		lineSpacing = builder.lineSpacing;
		rightAligned = builder.rightAligned;
		obfuscatedCharList = builder.obfuscatedCharList;

		predicate = builder.predicate;
		this.predicateParam = predicateParam;

		builder.predicates.values()
						  .forEach(fob -> predicates.add(new FontOptions(this, fob, predicateParam)));
	}

	public FontOptions(FontOptions fontOptions, FontOptionsBuilder fob, Object predicateParam)
	{
		this(fob, predicateParam);
		this.base = fontOptions;
	}

	private FontOptions get()
	{
		return predicates.stream()
						 .filter(FontOptions::test)
						 .findFirst()
						 .orElse(this);
	}

	private boolean test()
	{
		if (predicate == null)
			return false; //should never happen
		return predicate.test(predicateParam);
	}

	public EGOFont getFont()
	{
		EGOFont font = ObjectUtils.firstNonNull(get().font, this.font, MinecraftFont.INSTANCE);
		return font != null && font.isLoaded() ? font : MinecraftFont.INSTANCE;
	}

	/**
	 * Gets the font scale for this {@link FontOptions}.
	 *
	 * @return the font scale
	 */
	public float getFontScale()
	{
		return ObjectUtils.firstNonNull(get().fontScale, fontScale, 1F);
	}

	/**
	 * Checks if this {@link FontOptions} is bold.
	 *
	 * @return true, if is bold
	 */
	public boolean isBold()
	{
		return ObjectUtils.firstNonNull(get().bold, bold, false);
	}

	/**
	 * Checks if this {@link FontOptions} is italic.
	 *
	 * @return true, if is italic
	 */
	public boolean isItalic()
	{
		return ObjectUtils.firstNonNull(get().italic, italic, false);
	}

	/**
	 * Checks if this {@link FontOptions} is underlined.
	 *
	 * @return true, if is underline
	 */
	public boolean isUnderline()
	{
		return ObjectUtils.firstNonNull(get().underline, underline, false);
	}

	/**
	 * Checks if this {@link FontOptions} is strikethrough.
	 *
	 * @return true, if is strikethrough
	 */
	public boolean isStrikethrough()
	{
		return ObjectUtils.firstNonNull(get().strikethrough, strikethrough, false);
	}

	/**
	 * Checks if this {@link FontOptions} is obfuscated.
	 *
	 * @return true, if is obfuscated
	 */
	public boolean isObfuscated()
	{
		return ObjectUtils.firstNonNull(get().obfuscated, obfuscated, false);
	}

	public String getObfuscatedCharList()
	{
		return ObjectUtils.firstNonNull(get().obfuscatedCharList, obfuscatedCharList);
	}

	/**
	 * Checks whether draw shadow is enabled for this {@link FontOptions}.
	 *
	 * @return true, if successful
	 */
	public boolean hasShadow()
	{
		return ObjectUtils.firstNonNull(get().shadow, shadow, false);
	}

	/**
	 * Gets the color for this {@link FontOptions}.
	 *
	 * @return the color
	 */
	public int getColor()
	{
		return ObjectUtils.firstNonNull(get().color, color, 0);
	}

	/**
	 * Space between each character.
	 *
	 * @return the space
	 */
	public int charSpacing()
	{
		return ObjectUtils.firstNonNull(get().charSpacing, charSpacing, 1);
	}

	/**
	 * Space between each line.
	 *
	 * @return the space
	 */
	public int lineSpacing()
	{
		return ObjectUtils.firstNonNull(get().lineSpacing, lineSpacing, 1);
	}

	/**
	 * Checks whether the text is right aligned.
	 *
	 * @return true, if right aligned
	 */
	public boolean isRightAligned()
	{
		return ObjectUtils.firstNonNull(get().rightAligned, rightAligned, false);
	}

	/**
	 * Gets the shadow color corresponding to the current color.
	 *
	 * @return the shadow color
	 */
	public int getShadowColor()
	{
		int color = getColor(); //make sure we use the right color
		if (color == 0) //black
			return 0x222222;
		if (color == 0xFFAA00) //gold
			return 0x2A2A00;

		int r = (color >> 16) & 255;
		int g = (color >> 8) & 255;
		int b = color & 255;

		r /= 4;
		g /= 4;
		b /= 4;

		return (r & 255) << 16 | (g & 255) << 8 | b & 255;
	}

	/**
	 * Gets the {@link TextFormatting} at the specified position in the text.<br>
	 * Returns null if none is found.
	 *
	 * @param text the text
	 * @param index the index
	 * @return the formatting
	 */
	public static TextFormatting getFormatting(String text, int index)
	{
		if (StringUtils.isEmpty(text) || index < 0 || index > text.length() - 2)
			return null;

		char c = text.charAt(index);
		if (c != '\u00a7')
			return null;
		return charFormats.get(text.charAt(index + 1));
	}

	/**
	 * Gets the {@link TextFormatting TextFormattings} at the specified position in the text.
	 *
	 * @param text the text
	 * @param index the index
	 * @return the formattings
	 */
	public static List<TextFormatting> getFormattings(String text, int index)
	{
		List<TextFormatting> list = Lists.newArrayList();
		TextFormatting format;
		int offset = 0;
		while ((format = getFormatting(text, index + offset)) != null)
		{
			offset += 2;
			list.add(format);
		}
		return list;
	}

	/**
	 * Gets the string corresponding to the passed {@link TextFormatting TextFormattings}.
	 *
	 * @param list the list
	 * @return the string
	 */
	public static String formattingAsText(List<TextFormatting> list)
	{
		StringBuilder builder = new StringBuilder();
		list.forEach(builder::append);
		return builder.toString();
	}

	/**
	 * Checks if there is a {@link TextFormatting} at the specified position in the text.
	 *
	 * @param text the text
	 * @param index the index
	 * @return true, if ECF
	 */
	public static boolean isFormatting(String text, int index)
	{
		return getFormatting(text, index) != null;
	}

	/**
	 * Gets a {@link Pair} separating the {@link TextFormatting} tags at the beginning of a <code>text</code>.
	 *
	 * @param text the text
	 * @return a Pair with the formatting in the left part and the text in the right part.
	 */
	public static Pair<String, String> getStartFormat(String text)
	{
		int offset = 0;
		while (getFormatting(text, offset) != null)
		{
			offset += 2;
		}

		return Pair.of(text.substring(0, offset), text.substring(offset));
	}

	public static Link getLink(String text, int index)
	{
		if (StringUtils.isEmpty(text) || index < 0 || index > text.length() - 2)
			return null;

		if (text.charAt(index) != '[')
			return null;
		int i = text.indexOf(']');
		if (i < 2)
			return null;

		Link link = new Link(index, text.substring(index + 1, i));
		return link.isValid() ? link : null;
	}

	/**
	 * Create a {@link FontOptionsBuilder} with the values from {@code this}.
	 *
	 * @return the font options builder
	 */
	public FontOptionsBuilder toBuilder()
	{
		return new FontOptionsBuilder().from(this);
	}

	/**
	 * Create the {@link FontOptionsBuilder} for a new {@link FontOptions}.
	 *
	 * @return the font options builder
	 */
	public static FontOptionsBuilder builder()
	{
		return new FontOptionsBuilder();
	}

	public static class FontOptionsBuilder
	{
		private Map<Predicate<Object>, FontOptionsBuilder> predicates = Maps.newHashMap();
		private FontOptionsBuilder baseBuilder;
		private Predicate<Object> predicate;
		private Object predicateParameter = null;

		protected EGOFont font;
		/** Scale for the font **/
		protected Float fontScale;
		/** Color of the text **/
		protected Integer color;
		/** Draw with shadow **/
		protected Boolean shadow;
		/** Use bold font **/
		protected Boolean bold;
		/** Use italic font **/
		protected Boolean italic;
		/** Underline the text **/
		protected Boolean underline;
		/** Strike through the text **/
		protected Boolean strikethrough;
		/** Obfuscated text */
		protected Boolean obfuscated;
		/** Space between each character. */
		protected Integer charSpacing;
		/** Space between each line. */
		protected Integer lineSpacing;
		/** Right aligned. */
		protected Boolean rightAligned;
		/* Obfuscated char list */
		protected String obfuscatedCharList = null;

		public FontOptionsBuilder()
		{
			font = MinecraftFont.INSTANCE;
			fontScale = 1F;
			color = 0x000000;
			shadow = false;
			bold = false;
			italic = false;
			underline = false;
			strikethrough = false;
			obfuscated = false;
			charSpacing = 1;
			lineSpacing = 1;
			rightAligned = false;
		}

		public FontOptionsBuilder(FontOptionsBuilder base, Predicate<Object> predicate)
		{
			baseBuilder = base;
			this.predicate = predicate;
			font = null;
			fontScale = null;
			color = null;
			shadow = null;
			bold = null;
			italic = null;
			underline = null;
			strikethrough = null;
			obfuscated = null;
			charSpacing = null;
			lineSpacing = null;
			rightAligned = null;
		}

		/**
		 * @return The base fontOptionBuilder before the predicated suppliers are applied.
		 */
		public FontOptionsBuilder base()
		{
			return baseBuilder != null ? baseBuilder : this;
		}

		public FontOptionsBuilder font(EGOFont font)
		{
			this.font = checkNotNull(font);
			return this;
		}

		public FontOptionsBuilder scale(float scale)
		{
			fontScale = scale;
			return this;
		}

		public FontOptionsBuilder color(int color)
		{
			this.color = color;
			return this;
		}

		public FontOptionsBuilder color(TextFormatting color)
		{
			styles(color);
			return this;
		}

		public FontOptionsBuilder bold()
		{
			return bold(true);
		}

		public FontOptionsBuilder bold(boolean bold)
		{
			this.bold = bold;
			return this;
		}

		public FontOptionsBuilder italic()
		{
			return italic(true);
		}

		public FontOptionsBuilder italic(boolean italic)
		{
			this.italic = italic;
			return this;
		}

		public FontOptionsBuilder underline()
		{
			return underline(true);
		}

		public FontOptionsBuilder underline(boolean underline)
		{
			this.underline = underline;
			return this;
		}

		public FontOptionsBuilder strikethrough()
		{
			return strikethrough(true);
		}

		public FontOptionsBuilder strikethrough(boolean strikethrough)
		{
			this.strikethrough = strikethrough;
			return this;
		}

		public FontOptionsBuilder obfuscated()
		{
			return obfuscated(true);
		}

		public FontOptionsBuilder obfuscated(boolean obfuscated)
		{
			this.obfuscated = obfuscated;
			return this;
		}

		public FontOptionsBuilder obfuscatedCharList(String chars)
		{
			this.obfuscatedCharList = chars;
			return this;
		}

		public FontOptionsBuilder shadow()
		{
			return shadow(true);
		}

		public FontOptionsBuilder shadow(boolean shadow)
		{
			this.shadow = shadow;
			return this;
		}

		public FontOptionsBuilder charSpacing(int spacing)
		{
			charSpacing = spacing;
			return this;
		}

		public FontOptionsBuilder lineSpacing(int spacing)
		{
			lineSpacing = spacing;
			return this;
		}

		public FontOptionsBuilder rightAligned()
		{
			rightAligned = true;
			return this;
		}

		public FontOptionsBuilder leftAligned()
		{
			rightAligned = false;
			return this;
		}

		public FontOptionsBuilder styles(String styles)
		{
			for (TextFormatting format : FontOptions.getFormattings(styles, 0))
			{
				styles(format);
			}
			return this;
		}

		public FontOptionsBuilder styles(TextFormatting... formats)
		{
			for (TextFormatting f : formats)
			{
				if (f.isColor())
					color(FontOptions.colors[f.ordinal()]);
				else
					switch (f)
					{
						case BOLD:
							bold();
							break;
						case UNDERLINE:
							underline();
							break;
						case ITALIC:
							italic();
							break;
						case STRIKETHROUGH:
							strikethrough();
							break;
						case OBFUSCATED:
							obfuscated();
							break;
						default:
					}
			}
			return this;
		}

		public FontOptionsBuilder from(FontOptions options)
		{
			font = options.getFont();
			fontScale = options.getFontScale();
			color = options.getColor();
			shadow = options.hasShadow();
			bold = options.isBold();
			italic = options.isItalic();
			underline = options.isUnderline();
			strikethrough = options.isStrikethrough();
			obfuscated = options.isObfuscated();
			lineSpacing = options.lineSpacing();
			rightAligned = options.isRightAligned();

			return this;
		}

		public FontOptionsBuilder when(BooleanSupplier supplier)
		{
			return when(o -> supplier.getAsBoolean());
		}

		@SuppressWarnings("unchecked")
		public <T> FontOptionsBuilder when(Predicate<T> predicate)
		{
			if (this.predicate != null)
				return baseBuilder.when(predicate);

			return predicates.computeIfAbsent((Predicate<Object>) predicate, p -> new FontOptionsBuilder(this, p));
		}

		public FontOptionsBuilder when(Predicate<Object> predicate, FontOptions predicatedOptions)
		{
			//suppliers.add(Pair.of(predicate, predicatedOptions));
			return this;
		}

		public FontOptionsBuilder withPredicateParameter(Object parameter)
		{
			predicateParameter = parameter;
			return this;
		}

		public FontOptions build(Object predicateParameter)
		{
			if (this.predicate != null)
				return baseBuilder.build(predicateParameter);

			return new FontOptions(this, predicateParameter);
		}

		public FontOptions build()
		{
			return build(predicateParameter);
		}

	}
}

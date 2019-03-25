package net.malisis.ego.gui.text;

import net.malisis.ego.cacheddata.CachedData;
import net.malisis.ego.cacheddata.FixedData;
import net.malisis.ego.cacheddata.ICachedData;
import net.malisis.ego.font.FontOptions;
import net.malisis.ego.font.FontOptions.FontOptionsBuilder;
import net.malisis.ego.font.MalisisFont;
import net.minecraft.util.text.TextFormatting;

import java.util.function.Supplier;

/**
 * Interface that provides the implementing builder default proxy methods to the underlying {@link GuiText} and {@link FontOptions} objects.
 *
 * @param <BUILDER>
 */
public interface ITextBuilder<BUILDER>
{
	public GuiText.Builder getGuiTextBuilder();

	public FontOptionsBuilder getFontOptionsBuilder();

	public BUILDER fontOptions(FontOptions fontOptions);

	@SuppressWarnings("unchecked")
	public default BUILDER self()
	{
		return (BUILDER) this;
	}

	public default BUILDER text(String text)
	{
		getGuiTextBuilder().text(text);
		return self();
	}

	public default BUILDER text(Supplier<String> supplier)
	{
		getGuiTextBuilder().text(supplier);
		return self();
	}

	/**
	 * Binds a fixed value to the specified parameter.
	 *
	 * @param <T> the generic type
	 * @param key the key
	 * @param value the value
	 */
	public default <T> BUILDER bind(String key, T value)
	{
		return bind(key, new FixedData<>(value));
	}

	/**
	 * Binds supplier to the specified parameter.
	 *
	 * @param <T> the generic type
	 * @param key the key
	 * @param supplier the supplier
	 */
	public default <T> BUILDER bind(String key, Supplier<T> supplier)
	{
		return bind(key, new CachedData<>(supplier));
	}

	/**
	 * Binds {@link ICachedData} to the specified parameter.
	 *
	 * @param <T> the generic type
	 * @param key the key
	 * @param data the data
	 */
	public default <T> BUILDER bind(String key, ICachedData<T> data)
	{
		getGuiTextBuilder().bind(key, data);
		return self();
	}

	public default BUILDER multiLine(boolean multiLine)
	{
		getGuiTextBuilder().multiLine(multiLine);
		return self();
	}

	public default BUILDER multiLine()
	{
		getGuiTextBuilder().multiLine();
		return self();
	}

	public default BUILDER translated(boolean translated)
	{
		getGuiTextBuilder().translated(translated);
		return self();
	}

	public default BUILDER translated()
	{
		getGuiTextBuilder().translated();
		return self();
	}

	public default BUILDER literal(boolean literal)
	{
		getGuiTextBuilder().literal(literal);
		return self();
	}

	public default BUILDER literal()
	{
		getGuiTextBuilder().literal();
		return self();
	}

	//FontOptions
	public default BUILDER font(MalisisFont font)
	{
		getFontOptionsBuilder().font(font);
		return self();
	}

	public default BUILDER scale(float scale)
	{
		getFontOptionsBuilder().scale(scale);
		return self();
	}

	public default BUILDER color(int color)
	{
		getFontOptionsBuilder().color(color);
		return self();
	}

	public default BUILDER color(TextFormatting color)
	{
		getFontOptionsBuilder().color(color);
		return self();
	}

	public default BUILDER bold()
	{
		return bold(true);
	}

	public default BUILDER bold(boolean bold)
	{
		getFontOptionsBuilder().bold(bold);
		return self();
	}

	public default BUILDER italic()
	{
		return italic(true);
	}

	public default BUILDER italic(boolean italic)
	{
		getFontOptionsBuilder().italic(italic);
		return self();
	}

	public default BUILDER underline()
	{
		return underline(true);
	}

	public default BUILDER underline(boolean underline)
	{
		getFontOptionsBuilder().underline(underline);
		return self();
	}

	public default BUILDER strikethrough()
	{
		return strikethrough(true);
	}

	public default BUILDER strikethrough(boolean strikethrough)
	{
		getFontOptionsBuilder().strikethrough(strikethrough);
		return self();
	}

	public default BUILDER obfuscated()
	{
		return obfuscated(true);
	}

	public default BUILDER obfuscated(boolean obfuscated)
	{
		getFontOptionsBuilder().obfuscated(obfuscated);
		return self();
	}

	public default BUILDER shadow()
	{
		return shadow(true);
	}

	public default BUILDER shadow(boolean shadow)
	{
		getFontOptionsBuilder().shadow(shadow);
		return self();
	}

	public default BUILDER lineSpacing(int spacing)
	{
		getFontOptionsBuilder().lineSpacing(spacing);
		return self();
	}

	public default BUILDER rightAligned()
	{
		getFontOptionsBuilder().rightAligned();
		return self();
	}

	public default BUILDER leftAligned()
	{
		getFontOptionsBuilder().leftAligned();
		return self();
	}

	public default BUILDER styles(String styles)
	{
		getFontOptionsBuilder().styles(styles);
		return self();
	}

	public default BUILDER styles(TextFormatting... formats)
	{
		getFontOptionsBuilder().styles(formats);
		return self();
	}

}

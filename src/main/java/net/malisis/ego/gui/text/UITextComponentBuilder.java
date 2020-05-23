package net.malisis.ego.gui.text;

import static com.google.common.base.Preconditions.*;

import com.google.common.collect.Maps;
import net.malisis.ego.cacheddata.CachedData;
import net.malisis.ego.cacheddata.FixedData;
import net.malisis.ego.cacheddata.ICachedData;
import net.malisis.ego.font.EGOFont;
import net.malisis.ego.font.FontOptions;
import net.malisis.ego.font.FontOptions.FontOptionsBuilder;
import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.component.UIComponentBuilder;
import net.malisis.ego.gui.text.GuiText.Builder;
import net.minecraft.util.text.TextFormatting;

import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

/**
 * Interface that provides the implementing builder  proxy methods to the underlying {@link GuiText} and {@link FontOptions} objects.
 *
 * @param <BUILDER>
 */
public abstract class UITextComponentBuilder<BUILDER extends UIComponentBuilder<?, ?>, COMPONENT extends UIComponent>
		extends UIComponentBuilder<BUILDER, COMPONENT>
{
	protected GuiText.Builder guiTextBuilder = GuiText.builder();
	protected FontOptionsBuilder fontOptionsBuilder = FontOptions.builder();
	protected Function<COMPONENT, IntSupplier> wrapSize;
	protected Function<COMPONENT, IntSupplier> fitSize;
	protected Map<String, Function<COMPONENT, ICachedData<?>>> parameterFuncs = Maps.newHashMap();

	public Builder tb()
	{
		return guiTextBuilder;
	}

	public FontOptionsBuilder fob()
	{
		return fontOptionsBuilder;
	}

	public BUILDER fontOptions(@Nonnull FontOptions fontOptions)
	{
		fontOptionsBuilder = checkNotNull(fontOptions).toBuilder();
		return self();
	}

	public BUILDER fontOptionsBuilder(@Nonnull FontOptionsBuilder builder)
	{
		fontOptionsBuilder = checkNotNull(builder);
		return self();
	}

	public BUILDER text(String text)
	{
		tb().text(text);
		return self();
	}

	public BUILDER text(Supplier<String> supplier)
	{
		tb().text(supplier);
		return self();
	}

	/**
	 * Binds a fixed value to the specified parameter.
	 *
	 * @param key the key
	 * @param value the value
	 */
	public BUILDER bind(String key, Object value)
	{
		return bindData(key, c -> new FixedData<>(value));
	}

	public BUILDER bind(String key, Supplier<?> supplier)
	{
		return bindData(key, c -> new CachedData<>(supplier));
	}

	public BUILDER bind(String key, Function<COMPONENT, Supplier<?>> func)
	{
		return bindData(key, c -> new CachedData<>(func.apply(c)));
	}

	public BUILDER bindData(String key, ICachedData<?> data)
	{
		return bindData(key, c -> data);
	}

	public BUILDER bindData(String key, Function<COMPONENT, ICachedData<?>> data)
	{
		parameterFuncs.put(key, data);
		return self();
	}

	public BUILDER translated(boolean translated)
	{
		tb().translated(translated);
		return self();
	}

	public BUILDER translated()
	{
		tb().translated();
		return self();
	}

	public BUILDER literal(boolean literal)
	{
		tb().literal(literal);
		return self();
	}

	public BUILDER literal()
	{
		tb().literal();
		return self();
	}

	public BUILDER wrapSize(int size)
	{
		tb().wrapSize(size);
		return self();
	}

	public BUILDER wrapSize(IntSupplier supplier)
	{
		tb().wrapSize(checkNotNull(supplier));
		return self();
	}

	public BUILDER wrapSize(Function<COMPONENT, IntSupplier> func)
	{
		wrapSize = checkNotNull(func);
		return self();
	}

	public BUILDER fitSize(int size)
	{
		tb().fitSize(size);
		return self();
	}

	public BUILDER fitSize(IntSupplier supplier)
	{
		tb().fitSize(checkNotNull(supplier));
		return self();
	}

	public BUILDER fitSize(Function<COMPONENT, IntSupplier> func)
	{
		fitSize = checkNotNull(func);
		return self();
	}

	//FontOptions
	public BUILDER font(EGOFont font)
	{
		fob().font(font);
		return self();
	}

	public BUILDER scale(float scale)
	{
		fob().scale(scale);
		return self();
	}

	public BUILDER textColor(int color)
	{
		fob().color(color);
		return self();
	}

	public BUILDER textColor(TextFormatting color)
	{
		fob().color(color);
		return self();
	}

	public BUILDER bold()
	{
		return bold(true);
	}

	public BUILDER bold(boolean bold)
	{
		fob().bold(bold);
		return self();
	}

	public BUILDER italic()
	{
		return italic(true);
	}

	public BUILDER italic(boolean italic)
	{
		fob().italic(italic);
		return self();
	}

	public BUILDER underline()
	{
		return underline(true);
	}

	public BUILDER underline(boolean underline)
	{
		fob().underline(underline);
		return self();
	}

	public BUILDER strikethrough()
	{
		return strikethrough(true);
	}

	public BUILDER strikethrough(boolean strikethrough)
	{
		fob().strikethrough(strikethrough);
		return self();
	}

	public BUILDER obfuscated()
	{
		return obfuscated(true);
	}

	public BUILDER obfuscated(boolean obfuscated)
	{
		fob().obfuscated(obfuscated);
		return self();
	}

	public BUILDER shadow()
	{
		return shadow(true);
	}

	public BUILDER shadow(boolean shadow)
	{
		fob().shadow(shadow);
		return self();
	}

	public BUILDER lineSpacing(int spacing)
	{
		fob().lineSpacing(spacing);
		return self();
	}

	public BUILDER textRightAligned()
	{
		fob().rightAligned();
		return self();
	}

	public BUILDER TextLeftAligned()
	{
		fob().leftAligned();
		return self();
	}

	public BUILDER styles(String styles)
	{
		fob().styles(styles);
		return self();
	}

	public BUILDER styles(TextFormatting... formats)
	{
		fob().styles(formats);
		return self();
	}

	public BUILDER when(BooleanSupplier supplier)
	{
		return when(o -> supplier.getAsBoolean());
	}

	public BUILDER when(Predicate<COMPONENT> predicate)
	{
		fontOptionsBuilder = fob().when(predicate);
		return self();
	}

	public GuiText buildText(COMPONENT component)
	{
		tb().parent(component)
			.fontOptions(fontOptionsBuilder.build(component));
		parameterFuncs.forEach((s, f) -> tb().bind(s, f.apply(component)));
		if (wrapSize != null)
			tb().wrapSize(wrapSize.apply(component));
		if (fitSize != null)
			tb().fitSize(fitSize.apply(component));

		return guiTextBuilder.build();
	}
}

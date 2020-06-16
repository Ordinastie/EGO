package net.malisis.ego.gui.text;

import static com.google.common.base.Preconditions.*;

import com.google.common.collect.Maps;
import net.malisis.ego.cacheddata.CachedData;
import net.malisis.ego.cacheddata.ICachedData;
import net.malisis.ego.font.FontOptions;
import net.malisis.ego.font.FontOptions.FontOptionsBuilder;
import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.component.UIComponentBuilder;
import net.malisis.ego.gui.text.GuiText.Builder;

import java.util.Map;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

/**
 * Interface that provides the implementing builder  proxy methods to the underlying {@link GuiText} and {@link FontOptions} objects.
 *
 * @param <BUILDER>
 */
public abstract class  UITextComponentBuilder<BUILDER extends UIComponentBuilder<?, ?>, COMPONENT extends UIComponent>
		extends UIComponentBuilder<BUILDER, COMPONENT> implements ITextBuilder<BUILDER, COMPONENT>
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

	@Override
	public void setFontOptionsBuilder(FontOptionsBuilder builder)
	{
		fontOptionsBuilder = checkNotNull(builder);
	}

	public BUILDER fontOptions(@Nonnull FontOptions fontOptions)
	{
		fontOptionsBuilder = checkNotNull(fontOptions).toBuilder();
		return self();
	}

	public BUILDER bind(String key, Function<COMPONENT, Supplier<?>> func)
	{
		return bindData(key, c -> new CachedData<>(func.apply(c)));
	}

	public BUILDER bindData(String key, Function<COMPONENT, ICachedData<?>> data)
	{
		parameterFuncs.put(key, data);
		return self();
	}

	public BUILDER wrapSize(Function<COMPONENT, IntSupplier> func)
	{
		wrapSize = checkNotNull(func);
		return self();
	}

	public BUILDER fitSize(Function<COMPONENT, IntSupplier> func)
	{
		fitSize = checkNotNull(func);
		return self();
	}

	public GuiText buildText(COMPONENT component)
	{
		tb().parent(component);
		parameterFuncs.forEach((s, f) -> tb().bind(s, f.apply(component)));
		if (wrapSize != null)
			tb().wrapSize(wrapSize.apply(component));
		if (fitSize != null)
			tb().fitSize(fitSize.apply(component));

		return ITextBuilder.super.buildText(component);
	}
}

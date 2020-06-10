package net.malisis.ego.gui.text;

import static com.google.common.base.Preconditions.*;

import net.malisis.ego.cacheddata.ICachedData;
import net.malisis.ego.font.FontOptions;
import net.malisis.ego.gui.element.size.Size.ISized;

import java.util.function.IntSupplier;
import java.util.function.Supplier;

/**
 * Interface that provides the implementing builder  proxy methods to the underlying {@link GuiText} and {@link FontOptions} objects.
 *
 * @param <BUILDER>
 */
public interface ITextBuilder<BUILDER, PARENT extends ISized> extends IFontOptionsBuilder<BUILDER, PARENT>
{
	GuiText.Builder tb();

	default BUILDER text(String text)
	{
		tb().text(text);
		return self();
	}

	default BUILDER text(Supplier<String> supplier)
	{
		tb().text(supplier);
		return self();
	}

	default BUILDER bind(String key, Object value)
	{
		tb().bind(key, value);
		return self();
	}

	default BUILDER bind(String key, Supplier<?> supplier)
	{
		tb().bind(key, supplier);
		return self();
	}

	default BUILDER bindData(String key, ICachedData<?> data)
	{
		tb().bind(key, data);
		return self();
	}

	default BUILDER translated(boolean translated)
	{
		tb().translated(translated);
		return self();
	}

	default BUILDER literal(boolean literal)
	{
		tb().literal(literal);
		return self();
	}

	default BUILDER literal()
	{
		tb().literal();
		return self();
	}

	default BUILDER wrapSize(int size)
	{
		tb().wrapSize(size);
		return self();
	}

	default BUILDER wrapSize(IntSupplier supplier)
	{
		tb().wrapSize(checkNotNull(supplier));
		return self();
	}

	default BUILDER fitSize(int size)
	{
		tb().fitSize(size);
		return self();
	}

	default BUILDER fitSize(IntSupplier supplier)
	{
		tb().fitSize(checkNotNull(supplier));
		return self();
	}

	default GuiText buildText(PARENT parent)
	{
		fob().withPredicateParameter(parent);
		FontOptions fo = buildFontOptions();

		tb().parent(parent);
		tb().fontOptions(fo);
		return tb().build();
	}
}


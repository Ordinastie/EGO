package net.malisis.ego.gui.render.background;

import static com.google.common.base.Preconditions.*;

import net.malisis.ego.font.FontOptions;
import net.malisis.ego.font.FontOptions.FontOptionsBuilder;
import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.element.Padding;
import net.malisis.ego.gui.render.GuiIcon;
import net.malisis.ego.gui.render.GuiRenderer;
import net.malisis.ego.gui.render.IGuiRenderer;
import net.malisis.ego.gui.render.shape.GuiShape;
import net.malisis.ego.gui.text.GuiText;
import net.malisis.ego.gui.text.GuiText.Builder;
import net.malisis.ego.gui.text.ITextBuilder;

import java.util.function.Function;
import java.util.function.Supplier;

public class WindowBackground implements IGuiRenderer, Padding
{
	private final GuiShape shape;
	private final GuiText text;

	protected WindowBackground(UIComponent component, GuiText text)
	{
		shape = GuiShape.builder(component)
						.icon(GuiIcon.WINDOW)
						.border(5)
						.build();

		this.text = text;
	}

	@Override
	public int left()
	{
		return 5;
	}

	@Override
	public int right()
	{
		return 5;
	}

	@Override
	public int top()
	{
		return 5 + (text.isEmpty() ? 0 : text.height());
	}

	@Override
	public int bottom()
	{
		return 5;
	}

	@Override
	public void render(GuiRenderer renderer)
	{
		shape.and(text)
			 .render(renderer);
	}

	public static WindowBackground defaultWindow(UIComponent component)
	{
		return builder().build(component);
	}

	public static <T extends UIComponent> WindowBackgroundBuilder<T> builder()
	{
		return new WindowBackgroundBuilder<>();
	}

	public static class WindowBackgroundBuilder<COMPONENT extends UIComponent>
			implements ITextBuilder<WindowBackgroundBuilder<COMPONENT>, UIComponent>
	{
		private final Builder textBuilder = GuiText.builder();
		private FontOptionsBuilder fontOptionsBuilder = FontOptions.builder();
		private Function<COMPONENT, String> title = c -> "";

		private WindowBackgroundBuilder()
		{
			tb().position(5, 5);
			fob().color(0x404040);
		}

		@Override
		public WindowBackgroundBuilder<COMPONENT> self()
		{
			return this;
		}

		@Override
		public Builder tb()
		{
			return textBuilder;
		}

		@Override
		public FontOptionsBuilder fob()
		{
			return fontOptionsBuilder;
		}

		public WindowBackgroundBuilder<COMPONENT> text(Supplier<String> text)
		{
			return title(text);
		}

		public WindowBackgroundBuilder<COMPONENT> title(String title)
		{
			return title(c -> title);
		}

		public WindowBackgroundBuilder<COMPONENT> title(Supplier<String> title)
		{
			return title(c -> title.get());
		}

		public WindowBackgroundBuilder<COMPONENT> title(Function<COMPONENT, String> title)
		{
			this.title = checkNotNull(title);
			return this;
		}

		@Override
		public void setFontOptionsBuilder(FontOptionsBuilder builder)
		{
			fontOptionsBuilder = checkNotNull(builder);
		}

		public WindowBackground build(COMPONENT component)
		{
			tb().parent(component);
			tb().text(() -> title.apply(component));
			fob().withPredicateParameter(component);
			tb().fontOptions(fob().build());
			GuiText text = tb().build();
			return new WindowBackground(component, text);
		}

	}
}



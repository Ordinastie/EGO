package net.malisis.ego.gui.component;

import static com.google.common.base.Preconditions.*;

import com.google.common.collect.Lists;
import net.malisis.ego.font.FontOptions;
import net.malisis.ego.font.FontOptions.FontOptionsBuilder;
import net.malisis.ego.gui.EGOGui;
import net.malisis.ego.gui.component.container.UIContainer;
import net.malisis.ego.gui.component.content.IContent;
import net.malisis.ego.gui.component.interaction.UIButton;
import net.malisis.ego.gui.component.layout.FloatingLayout;
import net.malisis.ego.gui.element.Margin;
import net.malisis.ego.gui.text.GuiText;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class AlertComponent extends UIContainer
{
	private final UIComponent message;
	private final UIContainer buttons;

	public AlertComponent(Function<AlertComponent, IContent> supplier, List<UIButton> buttons)
	{
		this.message = supplier.apply(this)
							   .toComponent();
		this.buttons = buttonContainer(buttons);

		add(this.message);
		add(this.buttons);
	}

	private UIContainer buttonContainer(List<UIButton> buttons)
	{
		UIContainer container = UIContainer.builder()
										   .parent(this)
										   .name("Button container")
										   .margin(10)
										   .layout(FloatingLayout.builder()
																 .autoWrap(false)::build)
										   .centered()
										   .below(message)
										   .build();
		for (UIButton btn : buttons)
			container.add(btn);
		return container;
	}

	private int defaultWidth()
	{
		return message.width() + padding().horizontal();
	}

	private int defaultHeight()
	{
		return message.height() + padding().vertical() + buttons.height() + Margin.vertical(message, buttons);
	}

	public static AlertComponent alert(String message)
	{
		return new Builder().name("Alert")
							.text(message)
							.withButton(ButtonType.OK)
							.build();
	}

	public static AlertComponent alert(IContent content)
	{
		return new Builder().name("Alert")
							.content(content)
							.withButton(ButtonType.OK)
							.build();
	}

	public static AlertComponent error(String message)
	{
		return new Builder().name("Error")
							.text(message)
							.textColor(0x990000)
							.lightShadow()
							.withButton(ButtonType.OK)
							.build();
	}

	public static AlertComponent confirm(String message, BooleanSupplier onOk)
	{
		return new Builder().name("Confirm")
							.text(message)
							.withButton(ButtonType.OK, onOk)
							.withButton(ButtonType.CANCEL)
							.build();
	}

	public static AlertComponent confirm(IContent content, BooleanSupplier onOk)
	{
		return new Builder().name("Confirm")
							.content(content)
							.withButton(ButtonType.OK, onOk)
							.withButton(ButtonType.CANCEL)
							.build();
	}

	public static class Builder extends UIContainerBuilderG<Builder, AlertComponent>
			implements IContentHolderBuilder<Builder, AlertComponent>
	{
		protected GuiText.Builder textBuilder = GuiText.builder();
		protected FontOptionsBuilder fontOptionsBuilderBuilder = FontOptions.builder();
		private Function<AlertComponent, IContent> content = this::buildText;
		private final List<UIButton> buttons = Lists.newArrayList();

		public Builder()
		{
			window();
			padding(10);
			width(a -> a::defaultWidth);
			height(a -> a::defaultHeight);
			textColor(0xFFFFFF);
			shadow();
			//		movable();
		}

		@Override
		public GuiText.Builder tb()
		{
			return textBuilder;
		}

		@Override
		public void setFontOptionsBuilder(FontOptionsBuilder builder)
		{
			fontOptionsBuilderBuilder = checkNotNull(builder);
		}

		@Override
		public FontOptionsBuilder fob()
		{
			return fontOptionsBuilderBuilder;
		}

		@Override
		public Builder content(Function<AlertComponent, IContent> content)
		{
			this.content = checkNotNull(content);
			return this;
		}

		public Builder withButton(ButtonType type)
		{
			return withButton(type.label, () -> true);
		}

		public Builder withButton(ButtonType type, BooleanSupplier onClick)
		{
			return withButton(type.label, onClick);
		}

		public Builder withButton(ButtonType type, Predicate<UIButton> onClick)
		{
			return withButton(type.label, onClick);
		}

		public Builder withButton(String label)
		{
			return withButton(label, () -> true);
		}

		public Builder withButton(String label, BooleanSupplier onclick)
		{
			return withButton(label, btn -> onclick.getAsBoolean());
		}

		public Builder withButton(String label, Predicate<UIButton> onClick)
		{
			Consumer<UIButton> c = button -> {
				if (onClick.test(button))
					EGOGui.closeModal();
			};

			UIButton button = UIButton.builder()
									  .text(label)
									  .onClick(c)
									  .build();

			return withButton(button);
		}

		public Builder withButton(UIButton button)
		{
			buttons.add(button);
			return this;
		} //withButton

		@Override
		public AlertComponent build()
		{
			AlertComponent component = build(new AlertComponent(content, buttons));
			return component;
		}
	}

	public enum ButtonType
	{
		OK("Ok"),
		SAVE("Save"),
		CANCEL("Cancel"),
		CLOSE("Close");

		String label;

		ButtonType(String label)
		{
			this.label = label;
		}

	}
}

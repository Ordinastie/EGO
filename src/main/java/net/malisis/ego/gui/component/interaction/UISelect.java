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

package net.malisis.ego.gui.component.interaction;

import static com.google.common.base.Preconditions.*;

import net.malisis.ego.font.FontOptions;
import net.malisis.ego.gui.MalisisGui;
import net.malisis.ego.gui.component.MouseButton;
import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.component.UIComponentBuilder;
import net.malisis.ego.gui.component.container.UIListContainer;
import net.malisis.ego.gui.component.scrolling.UIScrollBar;
import net.malisis.ego.gui.component.scrolling.UIScrollBar.Type;
import net.malisis.ego.gui.component.scrolling.UISlimScrollbar;
import net.malisis.ego.gui.element.Padding;
import net.malisis.ego.gui.element.position.Position;
import net.malisis.ego.gui.element.size.Size;
import net.malisis.ego.gui.element.size.Size.ISize;
import net.malisis.ego.gui.element.size.Sizes;
import net.malisis.ego.gui.event.ValueChange;
import net.malisis.ego.gui.event.ValueChange.IValueChangeEventRegister;
import net.malisis.ego.gui.render.GuiIcon;
import net.malisis.ego.gui.render.shape.GuiShape;
import net.malisis.ego.gui.text.GuiText;
import org.lwjgl.input.Keyboard;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Predicate;

/**
 * The Class UISelect.
 *
 * @author Ordinastie
 */
public class UISelect<T> extends UIComponent implements IValueChangeEventRegister<UISelect<T>, T>
{

	/** Make the options width match the longest option available. */
	public static int LONGEST_OPTION = -1;
	/** Make the options width match the {@link UISelect} width. */
	public static int SELECT_WIDTH = -2;

	protected int selectedIndex = -1;
	/** Max width of the option parent. */
	protected int maxOptionsWidth = LONGEST_OPTION;
	/** Whether this {@link UISelect} is expanded. */
	protected boolean expanded = false;
	/** Container holding the {@link Option}. */
	protected OptionsContainer optionsContainer = new OptionsContainer();
	/** The string supplier to use for the default UILabel option. */
	protected Function<T, String> stringFunction = Objects::toString;
	/** Predicate for option disability */
	protected Predicate<T> disablePredicate = t -> false;

	protected UIComponent selectedComponent = null;

	/**
	 * Instantiates a new {@link UISelect}.
	 *
	 * @param values the values
	 */
	public UISelect(List<T> values)
	{
		setOptions(values);

		/* Shape used for the background of the select. */
		GuiShape background = GuiShape.builder(this)
									  .icon(this, GuiIcon.SELECT_BG, GuiIcon.SELECT_BG_HOVER, GuiIcon.SELECT_BG_DISABLED)
									  .border(1)
									  .build();
		/* Shape used to draw the arrow. */
		GuiShape arrowShape = GuiShape.builder(this)
									  .middleRight(2, 0)
									  .size(7, 4)
									  .color(() -> (isHovered() || expanded ? 0xBEC8FF : 0xFFFFFF))
									  .icon(GuiIcon.SELECT_ARROW)
									  .build();

		setBackground(background);
		setForeground(r -> {
			arrowShape.render(r);
			if (selectedComponent != null)
				selectedComponent.render(r);
		});
	}

	@Override
	public void onAddedToScreen(MalisisGui gui)
	{
		this.gui = gui;
		gui.addToScreen(optionsContainer);
	}

	//#region Getters/Setters
	private List<T> options()
	{
		return (List<T>) optionsContainer.getElements();
	}

	public void setDisablePredicate(Predicate<T> predicate)
	{
		disablePredicate = checkNotNull(predicate);
	}

	public void setStringFunction(Function<T, String> stringFunction)
	{
		this.stringFunction = checkNotNull(stringFunction);
	}

	public void setComponentFactory(Function<T, UIComponent> factory)
	{
		optionsContainer.setComponentFactory(factory);
	}

	public void setOptionsSize(ISize size)
	{
		optionsContainer.setSize(checkNotNull(size));
	}

	//#end Getters/Setters
	public int optionsMaxWidth()
	{
		return optionsContainer.optionsMaxWidth();
	}

	/**
	 * Set the options for this {@link UISelect}.
	 *
	 * @param elements the elements
	 * @return this {@link UISelect}
	 */
	public UISelect<T> setOptions(Collection<T> elements)
	{
		if (elements == null)
			elements = Collections.emptyList();

		optionsContainer.setElements(elements);
		return this;
	}

	/**
	 * Sets the selected option.<br>
	 * Does not trigger the SelectEvent
	 *
	 * @param option the new selected
	 */
	public void setSelected(T option)
	{
		selectedIndex = options().indexOf(option);
		selectedComponent = optionsContainer.generateComponent(selected());
	}

	/**
	 * Gets the currently selected option.
	 *
	 * @return the selected option
	 */
	public T selected()
	{
		return selectedIndex < 0 || selectedIndex >= options().size() ? null : options().get(selectedIndex);
	}

	/**
	 * Selects the option.
	 *
	 * @param option the option
	 * @return the option value
	 */
	public T select(T option)
	{
		int index = options().indexOf(option);
		if (index == -1 || index == selectedIndex)
			return selected();

		T old = selected();
		if (fireEvent(new ValueChange.Pre<>(this, old, option)))
			return old;

		setSelected(option);
		fireEvent(new ValueChange.Post<>(this, old, option));

		return option;
	}

	private T select(int index)
	{
		if (selectedIndex < 0 || selectedIndex >= options().size())
			index = -1;
		selectedIndex = index;
		return selected();
	}

	/**
	 * Selects the first option of this {@link UISelect}.
	 *
	 * @return the option value
	 */
	public T selectFirst()
	{
		return select(0);
	}

	/**
	 * Selects the last option of this {@link UISelect}.
	 *
	 * @return the option value
	 */
	public T selectLast()
	{
		return select(options().size() - 1);
	}

	/**
	 * Selects the option before the currently selected one.
	 *
	 * @return the option value
	 */
	public T selectPrevious()
	{
		if (selectedIndex <= 0)
			return selectFirst();
		return select(selectedIndex - 1);
	}

	/**
	 * Select the option after the currently selected one.
	 *
	 * @return the t
	 */
	public T selectNext()
	{
		if (selectedIndex >= options().size() - 1)
			return selectLast();
		return select(selectedIndex + 1);
	}

	@Override
	public void click(MouseButton button)
	{
		if (isDisabled())
			return;

		if (!expanded)
			optionsContainer.display();
		else
			optionsContainer.hide();
	}

	@Override
	public void scrollWheel(int delta)
	{
		if (!isFocused())
		{
			super.scrollWheel(delta);
			return;
		}

		if (delta < 0)
			selectNext();
		else
			selectPrevious();
	}

	@Override
	public boolean keyTyped(char keyChar, int keyCode)
	{
		if (!isFocused() && !optionsContainer.isFocused())
			return super.keyTyped(keyChar, keyCode);

		switch (keyCode)
		{
			case Keyboard.KEY_UP:
				selectPrevious();
				break;
			case Keyboard.KEY_DOWN:
				selectNext();
				break;
			case Keyboard.KEY_HOME:
				selectFirst();
				break;
			case Keyboard.KEY_END:
				selectLast();
				break;
			default:
				return super.keyTyped(keyChar, keyCode);
		}
		return true;
	}

	public class OptionsContainer extends UIListContainer<T>
	{
		/** The {@link UIScrollBar} used by this {@link OptionsContainer}. */
		protected UISlimScrollbar scrollbar;
		protected Padding padding = Padding.of(1);

		public OptionsContainer()
		{
			//TODO: place it above if room below is too small
			setPosition(Position.of(() -> UISelect.this.screenPosition()
													   .x(), () -> UISelect.this.screenPosition()
																				.y() + UISelect.this.size()
																									.height()));
			setZIndex(300);
			setBackground(GuiShape.builder(this)
								  .color(UISelect.this::getColor)
								  .icon(GuiIcon.SELECT_BOX)
								  .border(1)
								  .build());
			setComponentFactory(Option::new);

			hide();

			scrollbar = new UISlimScrollbar(this, Type.VERTICAL);
			scrollbar.setFade(false);
			scrollbar.setAutoHide(true);
		}

		protected UIComponent generateComponent(T element)
		{
			if (elementComponentFactory == null || element == null)
				return null;
			UIComponent comp = elementComponentFactory.apply(element);
			comp.setPosition(Position.of(1, 1));
			comp.setParent(UISelect.this);
			return comp;
		}

		public UISelect<T> uiSelect()
		{
			return UISelect.this;
		}

		public int optionsMaxWidth()
		{
			return components().stream()
							   .mapToInt(c -> c.size()
											   .width())
							   .max()
							   .orElse(0);
		}

		private void display()
		{
			scrollbar.updateScrollbar();
			setFocused(true);
			setVisible(true);
			expanded = true;
		}

		private void hide()
		{
			if (isFocused())
				setFocused(false);
			setVisible(false);
			expanded = false;
		}

		//#region IScrollable
		@Override
		public Padding padding()
		{
			return padding;
		}

		//#end IScrollable

		@SuppressWarnings("unchecked")
		public void click()
		{
			UIComponent comp = getComponentAt(MalisisGui.MOUSE_POSITION.x(), MalisisGui.MOUSE_POSITION.y());
			if (comp == null)
				return;

			select((T) comp.getData());
			hide();
			UISelect.this.setFocused(true);
		}

		@Override
		public boolean keyTyped(char keyChar, int keyCode)
		{
			return UISelect.this.keyTyped(keyChar, keyCode);
		}

		@Override
		public void setClipContent(boolean clip)
		{
		}

		@Override
		public boolean shouldClipContent()
		{
			return true;
		}
	}

	public class Option extends UIComponent
	{
		protected T element;
		/** The default {@link FontOptions} to use for this {@link UISelect}. */
		protected FontOptions fontOptions = FontOptions.builder()
													   .color(0xFFFFFF)
													   .shadow()
													   .when(this::isHovered)
													   .color(0xFED89F)
													   .when(this::isSelected)
													   .color(0x9EA8DF)
													   .build();

		protected GuiText text = GuiText.builder()
										.parent(this)
										.text(() -> stringFunction.apply(element))
										.position(1, 1)
										.fontOptions(fontOptions)
										.build();

		protected FontOptions selectedfontOptions = FontOptions.builder()
															   .color(0xFFFFFF)
															   .shadow()
															   .build();

		protected GuiShape background = GuiShape.builder(this)
												.color(0x5E789F)
												.alpha(() -> isHovered() ? 255 : 0)
												.build();

		public Option(T element)
		{
			this.element = element;
			setName(stringFunction.apply(element));
			attachData(element);
			setSize(Size.of(this::width, () -> text.size()
												   .height()));
			setAlpha(301);

			setBackground(background);
			setForeground(r -> {
				text.render(r);
			});
		}

		public int width()
		{
			//TODO: cache ?
			//noinspection unchecked
			int maxTextWidth = UISelect.this.optionsContainer.components()
															 .stream()
															 .mapToInt(c -> ((Option) c).text()
																						.size()
																						.width())
															 .max()
															 .orElse(text.size()
																		 .width());
			return Math.max(maxTextWidth, UISelect.this.size()
													   .width());
		}

		public GuiText text()
		{
			return text;
		}

		public boolean isSelected()
		{
			return Objects.equals(selected(), getData());
		}

		@Override
		public void click(MouseButton button)
		{
			select(element);
			optionsContainer.hide();
			UISelect.this.setFocused(true);
		}
	}

	public static <T> UISelectBuilder<T> builder(List<T> values)
	{
		return new UISelectBuilder<>(values);
	}

	//TODO: allow "NONE" option ?
	//TODO: maxDisplayedOptions
	public static class UISelectBuilder<T> extends UIComponentBuilder<UISelectBuilder<T>, UISelect<T>>
	{
		protected List<T> values;
		protected Function<T, UIComponent> componentFactory; // can't use Option::new because static context
		protected Function<T, String> stringFunction = Objects::toString;
		protected Predicate<T> disablePredicate = t -> false;
		protected Function<UISelect.OptionsContainer, IntSupplier> optionsWidth = oc -> () -> Math.max(oc.uiSelect()
																										 .size()
																										 .width(), oc.optionsMaxWidth());
		protected Function<UISelect.OptionsContainer, IntSupplier> optionsHeight = oc -> Sizes.heightOfContent(oc, 0);
		protected Function<UISelect.OptionsContainer, ISize> optionsSize = oc -> Size.of(optionsWidth.apply(oc), optionsHeight.apply(oc));

		protected T selected;

		public UISelectBuilder(List<T> value)
		{
			this.values = value;
			height(12);
			widthOfElements();
		}

		/**
		 * Sets the width to match the longest available element width.
		 *
		 * @return
		 */
		public UISelectBuilder<T> widthOfElements()
		{
			return width(s -> s::optionsMaxWidth);
		}

		/**
		 * Sets the size of the container holding the elements.
		 *
		 * @return
		 */
		public UISelectBuilder<T> optionsSize(Function<UISelect.OptionsContainer, ISize> size)
		{
			optionsSize = checkNotNull(size);
			return self();
		}

		/**
		 * Sets the factory to create the UIComponent for each element of the UISelect.<br>
		 * Uses {@link UISelect<T>.Option::new} by default
		 *
		 * @param factory
		 * @return
		 */
		public UISelectBuilder<T> withOptions(Function<T, UIComponent> factory)
		{
			componentFactory = checkNotNull(factory);
			return self();
		}

		/**
		 * Sets the function to converts each element to a String.<br>
		 * Uses {@link Objects::toString} by default.
		 *
		 * @param func
		 * @return
		 */
		public UISelectBuilder<T> withLabel(Function<T, String> func)
		{
			stringFunction = checkNotNull(func);
			return self();
		}

		public UISelectBuilder<T> disableElements(Predicate<T> predicate)
		{
			this.disablePredicate = checkNotNull(predicate);
			return self();
		}

		public UISelectBuilder<T> select(T selected)
		{
			this.selected = selected;
			return self();
		}

		@Override
		public UISelect<T> build()
		{
			UISelect<T> component = super.build(new UISelect<>(values));
			if (componentFactory != null)
				component.setComponentFactory(componentFactory);
			component.setStringFunction(stringFunction);
			component.setDisablePredicate(disablePredicate);
			component.setOptionsSize(optionsSize.apply(component.optionsContainer));
			component.setSelected(selected);
			return component;
		}
	}
}

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

package net.malisis.ego.gui.component.container;

import static com.google.common.base.Preconditions.*;

import com.google.common.collect.Maps;
import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.component.decoration.UILabel;
import net.malisis.ego.gui.component.layout.RowLayout;
import net.malisis.ego.gui.event.ValueChange;
import net.malisis.ego.gui.event.ValueChange.IValueChangeBuilder;
import net.malisis.ego.gui.render.GuiRenderer;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * @author Ordinastie
 */
public class UIListContainer<S> extends UIContainer
{
	protected int elementSpacing = 0;
	protected Collection<S> elements = Collections.emptyList();
	protected Map<S, UIComponent> componentElements = Maps.newHashMap();
	protected int lastSize = 0;
	protected BiFunction<UIListContainer<S>, S, UIComponent> elementComponentFactory = (lc, e) -> UILabel.builder()
																										 .text(Objects.toString(e))
																										 .build();
	protected int elementsSize;

	protected boolean selectable = false;
	protected boolean deselectable = false;
	protected S selected;

	public UIListContainer()
	{
	}

	protected void buildElementComponents()
	{
		removeAll();
		componentElements.clear();

		for (S element : elements)
		{
			UIComponent comp = createElementComponent(element);
			comp.attachData(element);
			componentElements.put(element, comp);
			add(comp);
		}
		elementsSize = elements.size();

		if (selectable)
		{
			applyEventToElements();
			if (selectedComponent() == null)
				selected = null;
		}
	}

	private void applyEventToElements()
	{
		componentElements.values()
						 .forEach(c -> c.onLeftClick(e -> {
							 select(c);
							 return true;
						 }));
	}

	protected UIComponent createElementComponent(S element)
	{
		return elementComponentFactory.apply(this, element);
	}

	public void setElements(Collection<S> elements)
	{
		this.elements = elements != null ? elements : Collections.emptyList();
		elementsSize = -1;//ensure rebuilding
	}

	public Collection<S> getElements()
	{
		return elements;
	}

	public UIComponent getElementComponent(S element)
	{
		return componentElements.get(element);
	}

	public void setComponentFactory(BiFunction<UIListContainer<S>, S, UIComponent> factory)
	{
		elementComponentFactory = factory;
	}

	public void setElementSpacing(int elementSpacing)
	{
		setLayout(new RowLayout(this, elementSpacing));
	}

	public void setSelectable(boolean selectable)
	{
		this.selectable = selectable;
	}

	public void setDeselectable(boolean deselectable)
	{
		this.deselectable = deselectable;
	}

	@SuppressWarnings("unchecked")
	public S select(UIComponent component)
	{
		select((S) component.getData());
		return selected();
	}

	public UIComponent setSelected(S element)
	{
		UIComponent comp = getElementComponent(element);
		selected = comp != null ? element : null;
		return comp;
	}

	public UIComponent select(S element)
	{
		//checks the element has a valid component in the list
		UIComponent comp = getElementComponent(element);
		if (comp == null)
			return null;

		S oldValue = selected;
		if (!fireEvent(new ValueChange.Pre<>(this, oldValue, element)))
			selected = element;
		fireEvent(new ValueChange.Post<>(this, oldValue, selected));
		return comp;
	}

	public S selected()
	{
		return selected;
	}

	public UIComponent selectedComponent()
	{
		return getElementComponent(selected);
	}

	public boolean isSelected(UIComponent component)
	{
		return component == selectedComponent();
	}

	public boolean isSelected(S element)
	{
		return element == selected;
	}

	@Override
	public void render(GuiRenderer renderer)
	{
		if (elements.size() != elementsSize)
			buildElementComponents();

		super.render(renderer);
	}

	public static <S> UIListContainerBuilder<S> builder(Collection<S> collection)
	{
		return new UIListContainerBuilder<>(collection);
	}

	public static class UIListContainerBuilder<S> extends UIContainerBuilderG<UIListContainerBuilder<S>, UIListContainer<S>>
			implements IValueChangeBuilder<UIListContainerBuilder<S>, UIListContainer<S>, S>
	{
		private final Collection<S> elements;
		private BiFunction<UIListContainer<S>, S, UIComponent> componentFactory = (lc, e) -> UILabel.builder()
																									.text(Objects.toString(e))
																									.build();
		protected boolean selectable = false;
		protected boolean deselectable = false;
		protected S selected;

		public UIListContainerBuilder(Collection<S> collection)
		{
			elements = collection;
			layout(c -> new RowLayout(c, 0));
		}

		public UIListContainerBuilder<S> selectable()
		{
			return selectable(true);
		}

		public UIListContainerBuilder<S> selectable(boolean selectable)
		{
			this.selectable = selectable;
			return this;
		}

		public UIListContainerBuilder<S> deselectable()
		{
			return deselectable(true);
		}

		public UIListContainerBuilder<S> deselectable(boolean deselectable)
		{
			this.deselectable = deselectable;
			return this;
		}

		public UIListContainerBuilder<S> selected(S selected)
		{
			this.selected = selected;
			return this;
		}

		public UIListContainerBuilder<S> factory(BiFunction<UIListContainer<S>, S, UIComponent> factory)
		{
			this.componentFactory = checkNotNull(factory);
			return this;
		}

		/**
		 * Alias for {@link #onChange(Consumer)}.
		 *
		 * @param consumer action to execute when an element is selected
		 * @return the builder
		 */
		public UIListContainerBuilder<S> onSelect(Consumer<S> consumer)
		{
			return onChange(consumer);
		}

		@Override
		public UIListContainer<S> build()
		{
			UIListContainer<S> list = build(new UIListContainer<>());
			list.setComponentFactory(componentFactory);
			list.setElements(elements);
			list.setSelectable(selectable);
			list.setDeselectable(deselectable);
			list.setSelected(selected);

			return list;
		}
	}

}

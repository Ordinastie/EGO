package net.malisis.ego.gui.event;

import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.component.UIComponentBuilder;

import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class ValueChange<T extends UIComponent, S> extends GuiEvent<T>
{
	/** The old value. */
	protected S oldValue;
	/** The new value. */
	protected S newValue;

	/**
	 * Instantiates a new {@link ValueChange}
	 *
	 * @param component the component
	 * @param oldValue the old value
	 * @param newValue the new value
	 */
	public ValueChange(T component, S oldValue, S newValue)
	{
		super(component);
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	/**
	 * Gets the value being changed for the {@link UIComponent}.
	 *
	 * @return the old value
	 */
	public S getOldValue()
	{
		return oldValue;
	}

	/**
	 * Gets the value being set for the {@link UIComponent}.
	 *
	 * @return the new value
	 */
	public S getNewValue()
	{
		return newValue;
	}

	public static class Pre<T extends UIComponent, S> extends ValueChange<T, S>
	{
		/**
		 * Instantiates a new {@link ValueChange}
		 *
		 * @param component the component
		 * @param oldValue the old value
		 * @param newValue the new value
		 */
		public Pre(T component, S oldValue, S newValue)
		{
			super(component, oldValue, newValue);
		}
	}

	public static class Post<T extends UIComponent, S> extends ValueChange<T, S>
	{
		/**
		 * Instantiates a new {@link ValueChange}
		 *
		 * @param component the component
		 * @param oldValue the old value
		 * @param newValue the new value
		 */
		public Post(T component, S oldValue, S newValue)
		{
			super(component, oldValue, newValue);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public interface IValueChangeEventRegister<T extends UIComponent, S> extends IEventRegister
	{
		public default void onPreChange(Predicate<ValueChange.Pre<T, S>> onChange)
		{
			register(ValueChange.Pre.class, (Predicate) onChange);
		}

		public default void onChange(Predicate<ValueChange.Post<T, S>> onChange)
		{
			register(ValueChange.Post.class, (Predicate) onChange);
		}

		public default void onChange(Consumer<S> onChange)
		{
			register(ValueChange.Post.class, e -> {
				onChange.accept((S) e.getNewValue());
				return true;
			});
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public interface IValueChangeBuilder<BUILDER extends UIComponentBuilder<?, ?>, T extends UIComponent, S> extends IEventRegister
	{
		@SuppressWarnings("unchecked")
		public default BUILDER self()
		{
			return (BUILDER) this;
		}

		public default BUILDER onPreChange(Predicate<ValueChange.Pre<T, S>> onChange)
		{
			register(ValueChange.Pre.class, (Predicate) onChange);
			return self();
		}

		public default BUILDER onPostChange(Predicate<ValueChange.Post<T, S>> onChange)
		{
			register(ValueChange.Post.class, (Predicate) onChange);
			return self();
		}

		public default BUILDER onChange(Consumer<S> onChange)
		{
			register(ValueChange.Post.class, e -> {
				onChange.accept((S) e.getNewValue());
				return true;
			});
			return self();
		}
	}
}
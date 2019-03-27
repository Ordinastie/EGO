package net.malisis.ego.gui.event;

import static net.malisis.ego.gui.component.MouseButton.LEFT;
import static net.malisis.ego.gui.component.MouseButton.RIGHT;
import static net.malisis.ego.gui.component.MouseButton.UNKNOWN;

import com.google.gson.reflect.TypeToken;
import net.malisis.ego.gui.MalisisGui;
import net.malisis.ego.gui.component.MouseButton;
import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.element.position.Position;
import net.malisis.ego.gui.element.position.Position.IPosition;

import java.lang.reflect.Type;
import java.util.function.Predicate;

public class MouseEvent<T extends UIComponent> extends GuiEvent<T>
{
	protected final MouseButton button;
	protected IPosition position;

	public MouseEvent(T source, MouseButton button)
	{
		super(source);
		this.button = button;
		position = Position.fixed(MalisisGui.MOUSE_POSITION);
	}

	/**
	 * @return the mouse button that triggered this event. {@link MouseButton#UNKNOWN} is returned if no button is applicable to this
	 * event.
	 */
	public MouseButton button()
	{
		return button;
	}

	public IPosition position()
	{
		return position;
	}

	//@formatter:off
	public static class MouseMove		<T extends UIComponent> extends MouseEvent<T> { public MouseMove(T source) 			{ super(source, UNKNOWN); }}
	public static class MouseLeftClick	<T extends UIComponent> extends MouseEvent<T> { public MouseLeftClick(T source) 	{ super(source, LEFT); }}
	public static class MouseRightClick	<T extends UIComponent> extends MouseEvent<T> { public MouseRightClick(T source) 	{ super(source, RIGHT); }}
	public static class MouseDoubleClick<T extends UIComponent> extends MouseEvent<T> { public MouseDoubleClick(T source) 	{ super(source, LEFT); }}
	public static class MouseOver		<T extends UIComponent> extends MouseEvent<T> { public MouseOver(T source) 			{ super(source, UNKNOWN); }}
	public static class MouseOut		<T extends UIComponent> extends MouseEvent<T> { public MouseOut(T source) 			{ super(source, UNKNOWN); }}
	public static class MouseDown		<T extends UIComponent> extends MouseEvent<T> { public MouseDown(T source, MouseButton button) 	{ super(source, button); }}
	public static class MouseUp			<T extends UIComponent> extends MouseEvent<T> { public MouseUp(T source, MouseButton button) 	{ super(source, button); }}
	public static class MouseDrag		<T extends UIComponent> extends MouseEvent<T> { public MouseDrag(T source, MouseButton button)  { super(source, button); }}
	//@formatter:on

	public static class ScrollWheel<T extends UIComponent> extends MouseEvent<T>
	{
		protected final int delta;

		public ScrollWheel(T source, int delta)
		{
			super(source, UNKNOWN);
			this.delta = delta;
		}

		public int delta()
		{
			return delta;
		}

		public boolean isUp()
		{
			return delta < 0;
		}

		public boolean isDown()
		{
			return delta > 0;
		}
	}

	public static class Test<T extends UIComponent>
	{
		public Type mouseMoveType = (new TypeToken<MouseMove<T>>() {}).getType();
	}

	public static class P<E, T extends UIComponent>

	{
		public P(Class<E> clazz, Predicate<E> p)
		{

		}
	}

	/**
	 * Interface helper for classes that need to register mouse events
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public interface IMouseEventRegister<T extends UIComponent> extends IEventRegister
	{
		public default void onMouseMove(Predicate<MouseMove<T>> onMouseMove)
		{
			register(MouseMove.class, (Predicate) onMouseMove);
		}

		public default void onLeftClick(Predicate<MouseLeftClick<T>> onClick)
		{
			register(MouseLeftClick.class, (Predicate) onClick);
		}

		public default void onRightClick(Predicate<MouseRightClick<T>> onRightClick)
		{
			register(MouseRightClick.class, (Predicate) onRightClick);
		}

		public default void onDoubleClick(Predicate<MouseDoubleClick<T>> onDoubleClick)
		{
			register(MouseDoubleClick.class, (Predicate) onDoubleClick);
		}

		public default void onMouseOver(Predicate<MouseOver<T>> onMouseOver)
		{
			register(MouseOver.class, (Predicate) onMouseOver);
		}

		public default void onMouseOut(Predicate<MouseOut<T>> onMouseOut)
		{
			register(MouseOut.class, (Predicate) onMouseOut);
		}

		public default void onMouseDown(Predicate<MouseDown<T>> onMouseDown)
		{
			register(MouseDown.class, (Predicate) onMouseDown);
		}

		public default void onMouseUp(Predicate<MouseUp<T>> onMouseUp)
		{
			register(MouseUp.class, (Predicate) onMouseUp);
		}

		public default void onMouseDrag(Predicate<MouseDrag<T>> onMouseDrag)
		{
			register(MouseDrag.class, (Predicate) onMouseDrag);
		}

		public default void onScrollWheel(Predicate<ScrollWheel<T>> onScrollWheel)
		{
			register(ScrollWheel.class, (Predicate) onScrollWheel);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public interface IMouseEventBuilder<BUILDER, COMPONENT extends UIComponent> extends IEventRegister
	{
		@SuppressWarnings("unchecked")
		public default BUILDER self()
		{
			return (BUILDER) this;
		}

		public default BUILDER onMouseMove(Predicate<MouseMove<COMPONENT>> onMouseMove)
		{
			register(MouseMove.class, (Predicate) onMouseMove);
			return self();
		}

		public default BUILDER onLeftClick(Predicate<MouseLeftClick<COMPONENT>> onClick)
		{
			register(MouseLeftClick.class, (Predicate) onClick);
			return self();
		}

		public default BUILDER onRightClick(Predicate<MouseRightClick<COMPONENT>> onRightClick)
		{
			register(MouseRightClick.class, (Predicate) onRightClick);
			return self();
		}

		public default BUILDER onDoubleClick(Predicate<MouseDoubleClick<COMPONENT>> onDoubleClick)
		{
			register(MouseDoubleClick.class, (Predicate) onDoubleClick);
			return self();
		}

		public default BUILDER onMouseOver(Predicate<MouseOver<COMPONENT>> onMouseOver)
		{
			register(MouseOver.class, (Predicate) onMouseOver);
			return self();
		}

		public default BUILDER onMouseOut(Predicate<MouseOut<COMPONENT>> onMouseOut)
		{
			register(MouseOut.class, (Predicate) onMouseOut);
			return self();
		}

		public default BUILDER onMouseDown(Predicate<MouseDown<COMPONENT>> onMouseDown)
		{
			register(MouseDown.class, (Predicate) onMouseDown);
			return self();
		}

		public default BUILDER onMouseUp(Predicate<MouseUp<COMPONENT>> onMouseUp)
		{
			register(MouseUp.class, (Predicate) onMouseUp);
			return self();
		}

		public default BUILDER onMouseDrag(Predicate<MouseDrag<COMPONENT>> onMouseDrag)
		{
			register(MouseDrag.class, (Predicate) onMouseDrag);
			return self();
		}

		public default BUILDER onScrollWheel(Predicate<ScrollWheel<COMPONENT>> onScrollWheel)
		{
			register(ScrollWheel.class, (Predicate) onScrollWheel);
			return self();
		}
	}
}

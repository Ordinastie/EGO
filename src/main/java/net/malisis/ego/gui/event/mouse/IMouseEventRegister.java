package net.malisis.ego.gui.event.mouse;

import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.event.GuiEvent.IEventRegister;
import net.malisis.ego.gui.event.mouse.MouseEvent.MouseDoubleClick;
import net.malisis.ego.gui.event.mouse.MouseEvent.MouseDown;
import net.malisis.ego.gui.event.mouse.MouseEvent.MouseDrag;
import net.malisis.ego.gui.event.mouse.MouseEvent.MouseLeftClick;
import net.malisis.ego.gui.event.mouse.MouseEvent.MouseMove;
import net.malisis.ego.gui.event.mouse.MouseEvent.MouseOut;
import net.malisis.ego.gui.event.mouse.MouseEvent.MouseOver;
import net.malisis.ego.gui.event.mouse.MouseEvent.MouseRightClick;
import net.malisis.ego.gui.event.mouse.MouseEvent.MouseUp;
import net.malisis.ego.gui.event.mouse.MouseEvent.ScrollWheel;

import java.util.function.Predicate;

/**
 * Interface helper for classes that need to register mouse events
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public interface IMouseEventRegister<T extends UIComponent> extends IEventRegister
{
	default void onMouseMove(Predicate<MouseMove<T>> onMouseMove)
	{
		register(MouseMove.class, (Predicate) onMouseMove);
	}

	default void onLeftClick(Predicate<MouseLeftClick<T>> onClick)
	{
		register(MouseLeftClick.class, (Predicate) onClick);
	}

	default void onRightClick(Predicate<MouseRightClick<T>> onRightClick)
	{
		register(MouseRightClick.class, (Predicate) onRightClick);
	}

	default void onDoubleClick(Predicate<MouseDoubleClick<T>> onDoubleClick)
	{
		register(MouseDoubleClick.class, (Predicate) onDoubleClick);
	}

	default void onMouseOver(Predicate<MouseOver<T>> onMouseOver)
	{
		register(MouseOver.class, (Predicate) onMouseOver);
	}

	default void onMouseOut(Predicate<MouseOut<T>> onMouseOut)
	{
		register(MouseOut.class, (Predicate) onMouseOut);
	}

	default void onMouseDown(Predicate<MouseDown<T>> onMouseDown)
	{
		register(MouseDown.class, (Predicate) onMouseDown);
	}

	default void onMouseUp(Predicate<MouseUp<T>> onMouseUp)
	{
		register(MouseUp.class, (Predicate) onMouseUp);
	}

	default void onMouseDrag(Predicate<MouseDrag<T>> onMouseDrag)
	{
		register(MouseDrag.class, (Predicate) onMouseDrag);
	}

	default void onScrollWheel(Predicate<ScrollWheel<T>> onScrollWheel)
	{
		register(ScrollWheel.class, (Predicate) onScrollWheel);
	}
}

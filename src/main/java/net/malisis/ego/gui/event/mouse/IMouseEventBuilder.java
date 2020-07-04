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
 * This interface represents a builder that accepts mouse events to be registered. *
 *
 * @param <BUILDER>
 * @param <COMPONENT>
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public interface IMouseEventBuilder<BUILDER, COMPONENT extends UIComponent> extends IEventRegister
{
	@SuppressWarnings("unchecked")
	default BUILDER self()
	{
		return (BUILDER) this;
	}

	default BUILDER onMouseMove(Predicate<MouseMove<COMPONENT>> onMouseMove)
	{
		register(MouseMove.class, (Predicate) onMouseMove);
		return self();
	}

	default BUILDER onLeftClick(Predicate<MouseLeftClick<COMPONENT>> onClick)
	{
		register(MouseLeftClick.class, (Predicate) onClick);
		return self();
	}

	default BUILDER onRightClick(Predicate<MouseRightClick<COMPONENT>> onRightClick)
	{
		register(MouseRightClick.class, (Predicate) onRightClick);
		return self();
	}

	default BUILDER onDoubleClick(Predicate<MouseDoubleClick<COMPONENT>> onDoubleClick)
	{
		register(MouseDoubleClick.class, (Predicate) onDoubleClick);
		return self();
	}

	default BUILDER onMouseOver(Predicate<MouseOver<COMPONENT>> onMouseOver)
	{
		register(MouseOver.class, (Predicate) onMouseOver);
		return self();
	}

	default BUILDER onMouseOut(Predicate<MouseOut<COMPONENT>> onMouseOut)
	{
		register(MouseOut.class, (Predicate) onMouseOut);
		return self();
	}

	default BUILDER onMouseDown(Predicate<MouseDown<COMPONENT>> onMouseDown)
	{
		register(MouseDown.class, (Predicate) onMouseDown);
		return self();
	}

	default BUILDER onMouseUp(Predicate<MouseUp<COMPONENT>> onMouseUp)
	{
		register(MouseUp.class, (Predicate) onMouseUp);
		return self();
	}

	default BUILDER onMouseDrag(Predicate<MouseDrag<COMPONENT>> onMouseDrag)
	{
		register(MouseDrag.class, (Predicate) onMouseDrag);
		return self();
	}

	default BUILDER onScrollWheel(Predicate<ScrollWheel<COMPONENT>> onScrollWheel)
	{
		register(ScrollWheel.class, (Predicate) onScrollWheel);
		return self();
	}
}

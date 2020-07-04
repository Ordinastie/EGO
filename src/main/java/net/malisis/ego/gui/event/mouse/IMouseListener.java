package net.malisis.ego.gui.event.mouse;

import net.malisis.ego.gui.component.MouseButton;
import net.malisis.ego.gui.component.UIComponent;

/**
 * This interface represents a class that receives mouse events.
 *
 * @see UIComponent
 */
public interface IMouseListener
{
	/**
	 * Called from the GUI when mouse is moved over this listener.
	 */
	void mouseMove();

	/**
	 * Called from the GUI when a mouse button is pressed over this listener
	 *
	 * @param button the pressed button
	 */
	void mouseDown(MouseButton button);

	/**
	 * Called from teh GUI when a mouse button is released over this listener
	 *
	 * @param button the released button
	 */
	void mouseUp(MouseButton button);

	/**
	 * Called from the GUI when this listener is clicked by a mouse button.
	 *
	 * @param button the clicked button
	 */
	void click(MouseButton button);

	/**
	 * Called from the GUI when this listener is double clicked by a mouse button.<br>
	 * Only fires the event if the button is {@link MouseButton#LEFT}.
	 *
	 * @param button the double clicked button
	 */
	void doubleClick(MouseButton button);

	/**
	 * Called from the GUI when this listener is dragged with a button pressed.<br>
	 * To get the distance dragged, use {@code EGOGui.MOUSE_POSITION.moved()}
	 *
	 * @param button the button
	 */
	void mouseDrag(MouseButton button);

	default boolean dragPreventsClick()
	{
		return false;
	}

	/**
	 * Called from the GUI when the mouse scroll wheel is used over this listener.<br>
	 * Also called if this listener is a focused component.
	 *
	 * @param delta the delta
	 */
	void scrollWheel(int delta);
}

package net.malisis.ego.gui.event.mouse;

import static net.malisis.ego.gui.component.MouseButton.LEFT;
import static net.malisis.ego.gui.component.MouseButton.RIGHT;
import static net.malisis.ego.gui.component.MouseButton.UNKNOWN;

import net.malisis.ego.gui.EGOGui;
import net.malisis.ego.gui.component.MouseButton;
import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.element.position.Position;
import net.malisis.ego.gui.element.position.Position.IPosition;
import net.malisis.ego.gui.event.GuiEvent;

public class MouseEvent<T extends UIComponent> extends GuiEvent<T>
{
	protected final MouseButton button;
	protected IPosition position;

	public MouseEvent(T source, MouseButton button)
	{
		super(source);
		this.button = button;
		position = Position.of(EGOGui.MOUSE_POSITION);
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

}

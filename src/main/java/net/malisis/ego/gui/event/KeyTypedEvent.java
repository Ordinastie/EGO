package net.malisis.ego.gui.event;

import net.malisis.ego.gui.component.UIComponent;
import net.minecraft.client.gui.GuiScreen;

public class KeyTypedEvent<T extends UIComponent> extends GuiEvent<T>
{
	private final int keyChar;
	private final int keyCode;

	public KeyTypedEvent(T source, int keyChar, int keyCode)
	{
		super(source);
		this.keyChar = keyChar;
		this.keyCode = keyCode;
	}

	public int getKeyChar()
	{
		return keyChar;
	}

	public int getKeyCode()
	{
		return keyCode;
	}

	public boolean isKey(int key)
	{
		return keyCode == key;
	}

	public boolean hasShiftModifier()
	{
		return GuiScreen.isShiftKeyDown();
	}

	public boolean hasCtrlModifier()
	{
		return GuiScreen.isCtrlKeyDown();
	}

	public boolean hasAltModifier()
	{
		return GuiScreen.isAltKeyDown();
	}

	public boolean isCtrlC()
	{
		return GuiScreen.isKeyComboCtrlC(keyCode);
	}

	public boolean isCtrlX()
	{
		return GuiScreen.isKeyComboCtrlX(keyCode);
	}

	public boolean isCtrlV()
	{
		return GuiScreen.isKeyComboCtrlV(keyCode);
	}

	public boolean isCtrlA()
	{
		return GuiScreen.isKeyComboCtrlA(keyCode);
	}

}

/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Ordinastie
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

package net.malisis.ego.gui.component;

import static net.malisis.ego.gui.element.size.Sizes.heightOfContent;
import static net.malisis.ego.gui.element.size.Sizes.parentWidth;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.malisis.ego.cacheddata.PredicatedData;
import net.malisis.ego.font.FontOptions;
import net.malisis.ego.gui.EGOGui;
import net.malisis.ego.gui.component.content.IContent.IContentHolder;
import net.malisis.ego.gui.element.IClipable;
import net.malisis.ego.gui.element.IClipable.ClipArea;
import net.malisis.ego.gui.element.Margin;
import net.malisis.ego.gui.element.Padding;
import net.malisis.ego.gui.element.Padding.IPadded;
import net.malisis.ego.gui.element.position.Position;
import net.malisis.ego.gui.element.size.Size;
import net.malisis.ego.gui.render.GuiIcon;
import net.malisis.ego.gui.render.GuiRenderer;
import net.malisis.ego.gui.render.IGuiRenderer;
import net.malisis.ego.gui.render.shape.GuiShape;
import net.malisis.ego.gui.text.GuiText;
import net.malisis.ego.gui.text.GuiText.Builder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Keyboard;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

/**
 * @author Ordinastie
 */
public class DebugComponent extends UIComponent implements IPadded, IContentHolder
{
	//remember setting between reconstructs and different guis
	private static boolean isDebug = false;
	private static boolean isTop = true; //can't store the actual position because it's dependant on the instance
	private static float scale = 1;
	private static int alpha = 80;

	private final HashMap<String, Supplier<String>> debugMap = new LinkedHashMap<>();
	private GuiText text;
	private final FontOptions fontOptions = FontOptions.builder()
													   .color(0xFFFFFF)
													   .scale(scale)
													   .shadow()
													   .build();
	private final Padding padding = Padding.of(5, 5);
	private final GuiShape borderShape = GuiShape.builder()
												 .icon(GuiIcon.BORDER)
												 .color(this::hierarchyColor)
												 .zIndex(this::hierarchyZIndex)
												 .border(2)
												 .build();
	private final GuiShape clipableShape = GuiShape.builder()
												   .color(0xFF0000)
												   .alpha(25)
												   .build();

	private final GuiText cachedText = GuiText.builder()
											  .parent(this)
											  .text("FPS: {FPS} ({DRAWCOUNT})\n{POS}Position" + ChatFormatting.RESET
															+ "\n{SIZE}Size\n{TEXT}Text")
											  .bind("FPS", Minecraft::getDebugFPS)
											  .bind("DRAWCOUNT", () -> EGOGui.current() != null ?
																	   EGOGui.current()
																			 .getRenderer().lastDrawCount :
																	   0)
											  .bind("POS", new PredicatedData<>(() -> Position.CACHED, ChatFormatting.DARK_GREEN,
																				ChatFormatting.DARK_RED))
											  .bind("SIZE", new PredicatedData<>(() -> Size.CACHED, ChatFormatting.DARK_GREEN,
																				 ChatFormatting.DARK_RED))
											  .bind("TEXT", new PredicatedData<>(() -> GuiText.CACHED, ChatFormatting.DARK_GREEN,
																				 ChatFormatting.DARK_RED))
											  .translated(false)
											  .fontOptions(FontOptions.builder()
																	  .color(0xFFFFFF)
																	  .scale(scale)
																	  .shadow()
																	  .rightAligned()
																	  .build())
											  .position(Position::topRight)
											  .alpha(255) //do not respect the component alpha
											  .build();

	private int hierarchyColor;
	private int hierarchyZIndex;

	public DebugComponent(EGOGui gui)
	{
		this.gui = gui;

		setAlpha(alpha);
		setZIndex(100);
		setMargin(Margin.NO_MARGIN);

		if (!isTop)
			setPosition(Position.bottomLeft(this));
		setSize(Size.of(parentWidth(this, 1.0F, 0), heightOfContent(this, 0)));

		setBackground(GuiShape.builder(this)
							  .color(0)
							  .alpha(this::getAlpha)
							  .build());
		setForeground(((IGuiRenderer) this::drawHierarchy).and(r -> {
			text.render(r);
			cachedText.render(r);
		}));

		setDefaultDebug();
		setEnabled(isDebug);
	}

	private void setDefaultDebug()
	{
		debugMap.put("Mouse", () -> EGOGui.MOUSE_POSITION + (EGOGui.getHoveredComponent() != null ?
															 " (" + EGOGui.getHoveredComponent()
																		  .mousePosition()
																		  .x() + "," + EGOGui.getHoveredComponent()
																							 .mousePosition()
																							 .y() + ")" :
															 ""));
		debugMap.put("Focus", () -> String.valueOf(EGOGui.getFocusedComponent()));
		debugMap.put("Hover", () -> String.valueOf(EGOGui.getHoveredComponent()));
		debugMap.put("Dragged", () -> String.valueOf(EGOGui.getDraggedComponent()));
		//		if (getGui().inventoryContainer() != null)
		//			debugMap.put("Picked", () -> ItemUtils.toString(getGui().inventoryContainer().getPickedItemStack()));
		updateGuiText();
	}

	private void updateGuiText()
	{
		Builder tb = GuiText.builder()
							.parent(this)
							.translated(false)
							.fontOptions(fontOptions)
							.alpha(255);//do not respect the component alpha

		String str = debugMap.entrySet()
							 .stream()
							 .peek(e -> tb.bind(e.getKey(), e.getValue()))
							 .map(e -> e.getKey() + " : {" + e.getKey() + "}")
							 .collect(Collectors.joining("\n"));
		text = tb.text(str)
				 .build();
	}

	@Override
	public GuiText content()
	{
		return text;
	}

	@Override
	@Nonnull
	public Padding padding()
	{
		return padding;
	}

	@Override
	public boolean isEnabled()
	{
		return super.isEnabled();
	}

	@Override
	public void setEnabled(boolean enabled)
	{
		if (enabled)
			getGui().addToScreen(this);
		else
			getGui().removeFromScreen(this);
		super.setEnabled(enabled);
		isDebug = enabled;
	}

	public void clear()
	{
		debugMap.clear();
		setDefaultDebug();
	}

	public void addDebug(String name, Supplier<String> supplier)
	{
		debugMap.put(name, supplier);
		updateGuiText();
	}

	public void removeDebug(String name)
	{
		debugMap.remove(name);
		updateGuiText();
	}

	public void watch(String watched)
	{
		addDebug("Watched", () -> String.valueOf(EGOGui.current()
													   .findComponent(watched)));
	}

	public void unWatch()
	{
		removeDebug("Watched");
	}

	@Override
	public void scrollWheel(int delta)
	{
		if (!isHovered())
			return;

		if (GuiScreen.isCtrlKeyDown())
		{
			scale += 1 / 3F * delta;
			scale = MathHelper.clamp(scale, 1 / 3F, 1);

			text.setFontOptions(text.getFontOptions()
									.toBuilder()
									.scale(scale)
									.build());
			cachedText.setFontOptions(cachedText.getFontOptions()
												.toBuilder()
												.scale(scale)
												.build());
		}
		else if (GuiScreen.isShiftKeyDown())
		{
			alpha += 25 * delta;
			alpha = MathHelper.clamp(alpha, 0, 255);
			setAlpha(alpha);
		}
	}

	@Override
	public boolean keyTyped(char keyChar, int keyCode)
	{
		if (!GuiScreen.isCtrlKeyDown())
			return false;

		if (keyCode == Keyboard.KEY_D)
		{
			setEnabled(!isEnabled());
			return true;
		}

		if (!isEnabled())
			return false;

		switch (keyCode)
		{
			case Keyboard.KEY_P:
				Position.CACHED = !Position.CACHED;
				break;
			case Keyboard.KEY_S:
				Size.CACHED = !Size.CACHED;
				break;
			case Keyboard.KEY_T:
				GuiText.CACHED = !GuiText.CACHED;
				break;
			case Keyboard.KEY_DOWN:
				isTop = false;
				setPosition(Position.bottomLeft(this));
				break;
			case Keyboard.KEY_UP:
				isTop = true;
				setPosition(Position.topLeft(this));
				break;
			case Keyboard.KEY_O:
				if (getGui().isOverlay() && EGOGui.current() == null)
					getGui().display();
				else if (getGui().isOverlay() && getGui() == EGOGui.current())
					getGui().close();
		}

		return false;

	}

	private int hierarchyColor()
	{
		return hierarchyColor;
	}

	private int hierarchyZIndex()
	{
		return hierarchyZIndex;
	}

	public void drawHierarchy(GuiRenderer renderer)
	{
		UIComponent component = EGOGui.getHoveredComponent();
		if (component == null || !GuiScreen.isAltKeyDown())
			return;

		if (component instanceof IClipable)
		{
			ClipArea area = ((IClipable) component).getClipArea();
			if (!area.noClip() && !area.fullClip())
				clipableShape.render(renderer, Position.of(area.x, area.y), Size.of(area.width(), area.height()));
		}

		int offset = 80;
		hierarchyColor = 0xFF0000;
		hierarchyZIndex = 400;
		while (component != null)
		{
			hierarchyZIndex--;
			borderShape.renderFor(renderer, component);

			int r = (hierarchyColor >> 16) & 0xFF;
			int g = (hierarchyColor >> 8) & 0xFF;
			int b = (hierarchyColor) & 0xFF;

			r -= Math.max(offset, 0);
			g = Math.min(g + offset, 255);
			b = Math.min(b + 2 * offset, 255);

			hierarchyColor = (b << 16) + (g << 8) + r;

			component = component.getParent();
		}
	}

	@Override
	public String toString()
	{
		return "Debug Component";
	}
}

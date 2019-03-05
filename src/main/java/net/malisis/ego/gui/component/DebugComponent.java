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
import net.malisis.ego.gui.MalisisGui;
import net.malisis.ego.gui.component.content.IContentHolder;
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
	private HashMap<String, Supplier<String>> debugMap = new LinkedHashMap<>();
	private GuiText text;
	private FontOptions fontOptions = FontOptions.builder().color(0xFFFFFF).shadow().build();
	private Padding padding = Padding.of(5, 5);
	private GuiShape borderShape = GuiShape.builder()
										   .icon(GuiIcon.BORDER)
										   .color(this::hierarchyColor)
										   .zIndex(this::hierarchyZIndex)
										   .border(2)
										   .build();

	private GuiText cachedText = GuiText.builder()
										.parent(this)
										.text("FPS: {FPS}\n{POS}Position" + ChatFormatting.RESET + "\n{SIZE}Size")
										.bind("FPS", Minecraft::getDebugFPS)
										.bind("POS",
											  new PredicatedData<>(() -> Position.CACHED,
																   ChatFormatting.DARK_GREEN,
																   ChatFormatting.DARK_RED))
										.bind("SIZE",
											  new PredicatedData<>(() -> Size.CACHED, ChatFormatting.DARK_GREEN, ChatFormatting.DARK_RED))
										.translated(false)
										.fontOptions(FontOptions.builder().color(0xFFFFFF).shadow().rightAligned().build())
										.position(Position::topRight)
										.build();

	private int hierarchyColor;
	private int hierarchyZIndex;

	public DebugComponent(MalisisGui gui)
	{
		this.gui = gui;
		enabled = false;

		setAlpha(80);
		//setZIndex(-1);

		setSize(Size.of(parentWidth(this, 1.0F, 0), heightOfContent(this, 0)));

		setBackground(GuiShape.builder(this).color(0).alpha(this::getAlpha).build());
		setForeground(((IGuiRenderer) this::drawHierarchy).and(r -> {
			text.render(r);
			cachedText.render(r);
		}));

		setDefaultDebug();
		setEnabled(MalisisGui.debug);
	}

	private void setDefaultDebug()
	{
		debugMap.put("Mouse",
					 () -> MalisisGui.MOUSE_POSITION + (MalisisGui.getHoveredComponent() != null ?
														" (" + MalisisGui.getHoveredComponent().mousePosition() + ")" :
														""));
		debugMap.put("Focus", () -> String.valueOf(MalisisGui.getFocusedComponent()));
		debugMap.put("Hover", () -> String.valueOf(MalisisGui.getHoveredComponent()));
		debugMap.put("Dragged", () -> String.valueOf(MalisisGui.getDraggedComponent()));
		//		if (getGui().inventoryContainer() != null)
		//			debugMap.put("Picked", () -> ItemUtils.toString(getGui().inventoryContainer().getPickedItemStack()));
		updateGuiText();
	}

	private void updateGuiText()
	{
		Builder tb = GuiText.builder().parent(this).multiLine().translated(false).fontOptions(fontOptions);

		String str = debugMap.entrySet()
							 .stream()
							 .peek(e -> tb.bind(e.getKey(), e.getValue()))
							 .map(e -> e.getKey() + " : {" + e.getKey() + "}")
							 .collect(Collectors.joining("\n"));
		text = tb.text(str).build();
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
		return MalisisGui.debug;
	}

	@Override
	public void setEnabled(boolean enabled)
	{
		if (enabled)
			getGui().addToScreen(this);
		else
			getGui().removeFromScreen(this);
		super.setEnabled(enabled);
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

	@Override
	public boolean onKeyTyped(char keyChar, int keyCode)
	{
		return super.onKeyTyped(keyChar, keyCode);
	}

	@Override
	public boolean onScrollWheel(int delta)
	{
		if (!isHovered())
			return false;

		FontOptions fontOptions = text.getFontOptions();
		if (GuiScreen.isCtrlKeyDown())
		{
			float scale = fontOptions.getFontScale();
			scale += 1 / 3F * delta;
			scale = MathHelper.clamp(scale, 1 / 3F, 1);

			text.setFontOptions(fontOptions.toBuilder().scale(scale).build());
			cachedText.setFontOptions(fontOptions.toBuilder().scale(scale).build());
		}
		else if (GuiScreen.isShiftKeyDown())
		{
			alpha += 25 * delta;
			alpha = MathHelper.clamp(alpha, 0, 255);
		}

		return true;
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
		UIComponent component = MalisisGui.getHoveredComponent();
		if (component == null || !GuiScreen.isAltKeyDown())
			return;

		int offset = 80;
		hierarchyColor = 0xFF0000;
		hierarchyZIndex = 200;
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

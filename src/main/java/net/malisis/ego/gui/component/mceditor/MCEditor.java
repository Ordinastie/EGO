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

package net.malisis.ego.gui.component.mceditor;

import static net.malisis.ego.gui.element.position.Positions.bottomAligned;

import net.malisis.ego.font.EGOFont;
import net.malisis.ego.font.FontOptions;
import net.malisis.ego.font.MinecraftFont;
import net.malisis.ego.gui.EGOGui;
import net.malisis.ego.gui.component.container.UIContainer;
import net.malisis.ego.gui.component.interaction.UICheckBox;
import net.malisis.ego.gui.component.interaction.UISelect;
import net.malisis.ego.gui.component.interaction.UITextField;
import net.malisis.ego.gui.element.position.Position;
import net.malisis.ego.gui.element.size.Size;
import net.malisis.ego.gui.element.size.Size.ISize;
import net.malisis.ego.gui.element.size.Sizes;
import net.minecraft.util.text.TextFormatting;

/**
 * @author Ordinastie
 */
public class MCEditor extends UIContainer
{
	private UITextField tf;
	private EcfSelect sel;
	private UICheckBox cb;

	private EGOFont font = MinecraftFont.INSTANCE;
	private FontOptions fontOptions = FontOptions.builder()
												 .build();

	public MCEditor()
	{
		super();
		tf = new UITextField();
		tf.setPosition(Position.of(0, bottomAligned(tf, 0)));
		tf.setSize(Size.of(Sizes.parentWidth(tf, 1.0F, 0), Sizes.parentHeight(tf, 0.9f, 0)));

		sel = new EcfSelect(this);

		cb = new UICheckBox("Use litteral formatting");
		cb.setPosition(Position.of(85, 0));
		//cb.register(this);

		add(tf, sel, cb);
	}

	public MCEditor(EGOGui gui, ISize size)
	{
		this();
		setSize(size);
	}

	public UITextField getTextfield()
	{
		return tf;
	}

	public UISelect<TextFormatting> getSelect()
	{
		return sel;
	}

	//#region IGuiText
	public EGOFont getFont()
	{
		return font;
	}

	public MCEditor setFont(EGOFont font)
	{
		this.font = font;
		return this;
	}

	public FontOptions getFontOptions()
	{
		return fontOptions;
	}

	public MCEditor setFontOptions(FontOptions fro)
	{
		fontOptions = fro;
		return this;
	}

	//#end IGuiText

}

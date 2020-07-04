/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 PaleoCrafter, Ordinastie
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

package net.malisis.ego.gui.component.interaction;

import static com.google.common.base.Preconditions.*;

import net.malisis.ego.font.FontOptions;
import net.malisis.ego.font.FontOptions.FontOptionsBuilder;
import net.malisis.ego.font.StringWalker;
import net.malisis.ego.gui.EGOGui;
import net.malisis.ego.gui.component.MouseButton;
import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.component.UIComponentBuilder;
import net.malisis.ego.gui.component.content.IContent.IContentHolder;
import net.malisis.ego.gui.element.IClipable;
import net.malisis.ego.gui.element.IOffset;
import net.malisis.ego.gui.element.Padding;
import net.malisis.ego.gui.element.position.Position;
import net.malisis.ego.gui.element.position.Position.IPosition;
import net.malisis.ego.gui.element.size.Size;
import net.malisis.ego.gui.event.ValueChange;
import net.malisis.ego.gui.event.ValueChange.IValueChangeEventRegister;
import net.malisis.ego.gui.render.GuiIcon;
import net.malisis.ego.gui.render.GuiRenderer;
import net.malisis.ego.gui.render.shape.GuiShape;
import net.malisis.ego.gui.text.GuiText;
import net.malisis.ego.gui.text.IFontOptionsBuilder;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * UITextField.
 *
 * @author Ordinastie
 */
public class UITextField extends UIComponent implements IContentHolder, IClipable, IOffset, IValueChangeEventRegister<UITextField, String>
{
	protected final GuiText guiText;
	/** Current text of this {@link UITextField}. */
	protected StringBuilder text = new StringBuilder();

	private int xOffset = 0;
	protected IPosition offset = Position.of(this::xOffset, 0);

	//cursors
	/** Whether currently selecting text. */
	protected boolean selectingText = false;
	/** Current cursor position. */
	protected Cursor cursor = new Cursor();
	/** Current selection cursor position. */
	protected Cursor selectionCursor = new Cursor();
	/** Cursor blink timer. */
	protected long startTimer;

	//interaction
	/** Whether this {@link UITextField} should select the text when release left mouse button. */
	protected boolean selectAllOnRelease = false;
	/** Whether this {@link UITextField} should auto select the text when gaining focus. */
	protected boolean autoSelectOnFocus = false;
	/** Whether this {@link UITextField} is editable. */
	protected boolean editable = true;
	protected Function<String, String> filterFunction = Function.identity();
	/** Checks whether the character input is allowed **/
	protected BiPredicate<UITextField, Character> allowedInput = (tf, c) -> true;
	/** Check whether the text entered is valid. No internal use. */
	protected Predicate<UITextField> validator = tf -> true;

	private boolean isValid = true;

	//options
	/** Cursor color for this {@link UITextField}. */
	protected int cursorColor = 0xD0D0D0;
	/** Selection color for this {@link UITextField}. */
	protected int selectColor = 0x0000FF;

	//drawing
	/** Shape used to draw the cursor of this {@link UITextField}. */
	protected GuiShape cursorShape = GuiShape.builder(this)
											 .position(cursor)
											 .fixed(false)
											 .size(Size.of(1, cursor::height))
											 .color(this::getCursorColor)
											 .icon(GuiIcon.NONE)
											 .build();

	protected GuiShape cursorRenderer = cursorShape;
	private Consumer<UITextField> enterCallback;

	public UITextField()
	{
		guiText = GuiText.builder()
						 .parent(this)
						 .text(this::getText)
						 .translated(false)
						 .literal(true)
						 .position(2, 1)
						 .fontOptions(FontOptions.builder()
												 .color(0xFFFFFF)
												 .shadow()
												 .build())
						 .build();
		setSize(Size.of(100, 12));
		setPadding(Padding.of(1));

		GuiShape background = GuiShape.builder(this)
									  .icon(GuiIcon.forComponent(this, GuiIcon.TEXTFIELD_BG, null, GuiIcon.TEXTFIELD_BG_DISABLED))
									  .border(1)
									  .build();
		setBackground(background);
		setForeground(guiText.and(this::drawCursor)
							 .and(this::drawSelectionBox));
	}

	// #region Getters/Setters

	/**
	 * Sets the text of this {@link UITextField} and place the cursor at the end.
	 *
	 * @param text the new text
	 */
	public void setText(String text)
	{
		if (text == null)
			text = "";
		this.text.setLength(0);
		addText(text);
		//		guiText.setText(text);

		selectingText = false;
		xOffset = 0;
		if (focused)
			cursor.jumpToEnd();
		// fireEvent(new TextChanged(this));
	}

	/**
	 * Gets the text of this {@link UITextField}.
	 *
	 * @return the text
	 */
	public String getText()
	{
		return text.toString();
	}

	public int getTextAsInt()
	{
		return Integer.parseInt(getText());
	}

	public float getTextAsFloat()
	{
		return Float.parseFloat(getText());
	}

	public void setFontOptions(FontOptions fontOptions)
	{
		guiText.setFontOptions(fontOptions);
	}

	@Override
	public GuiText content()
	{
		return guiText;
	}

	/**
	 * Gets the currently selected text.
	 *
	 * @return the text selected.
	 */
	public String getSelectedText()
	{
		if (!selectingText)
			return "";

		int start = Math.min(selectionCursor.index, cursor.index);
		int end = Math.max(selectionCursor.index, cursor.index);

		return text.substring(start, end);
	}

	private int xOffset()
	{
		return xOffset;
	}

	/**
	 * Gets the cursor color.
	 *
	 * @return the cursor color
	 */
	public int getCursorColor()
	{
		return cursorColor;
	}

	/**
	 * Sets the cursor color.
	 *
	 * @param cursorColor the cursor color
	 */
	public void setCursorColor(int cursorColor)
	{
		this.cursorColor = cursorColor;
	}

	/**
	 * Gets the select color.
	 *
	 * @return the select color
	 */
	public int getSelectColor()
	{
		return selectColor;
	}

	/**
	 * Sets the select color.
	 *
	 * @param selectColor the select color
	 */
	public void setSelectColor(int selectColor)
	{
		this.selectColor = selectColor;
	}

	/**
	 * Sets the options.
	 *
	 * @param bgColor the bg color
	 * @param cursorColor the cursor color
	 * @param selectColor the select color
	 * @return the UI text field
	 */
	public UITextField setColors(int bgColor, int cursorColor, int selectColor)
	{
		setColor(bgColor);
		this.cursorColor = cursorColor;
		this.selectColor = selectColor;

		return this;
	}

	/**
	 * Gets the current cursor position.
	 *
	 * @return the position of the cursor.
	 */
	public Cursor getCursorPosition()
	{
		return cursor;
	}

	/**
	 * Gets the selection position.
	 *
	 * @return the selection position
	 */
	public Cursor getSelectionPosition()
	{
		return selectionCursor;
	}

	/**
	 * Sets whether this {@link UITextField} should automatically select its {@link #text} when focused.
	 *
	 * @param auto the auto
	 */
	public void setAutoSelectOnFocus(boolean auto)
	{
		autoSelectOnFocus = auto;
	}

	/**
	 * Checks if is editable.
	 *
	 * @return true, if is editable
	 */
	public boolean isEditable()
	{
		return editable;
	}

	/**
	 * Sets the editable.
	 *
	 * @param editable the editable
	 */
	public void setEditable(boolean editable)
	{
		this.editable = editable;
	}

	/**
	 * Sets the function that applies to all incoming text. Immediately applies filter to current text.
	 *
	 * @param filterFunction the function
	 */
	public void setFilter(Function<String, String> filterFunction)
	{
		this.filterFunction = filterFunction;
		if (filterFunction != null)
			text = new StringBuilder(this.filterFunction.apply(text.toString()));
	}

	/**
	 * Gets the function applied to all incoming text
	 *
	 * @return the filter function
	 */
	public Function<String, String> getFilter()
	{
		return filterFunction;
	}

	public void setAllowedInput(BiPredicate<UITextField, Character> allowedInput)
	{
		this.allowedInput = allowedInput;
	}

	public BiPredicate<UITextField, Character> getAllowedInput()
	{
		return allowedInput;
	}

	public void setValidator(Predicate<UITextField> validator)
	{
		this.validator = validator;
		isValid = validator.test(this);
	}

	public Predicate<UITextField> getValidator()
	{
		return validator;
	}

	public void setEnterCallback(Consumer<UITextField> consumer)
	{
		enterCallback = consumer;
	}

	public Consumer<UITextField> getEnterCallback()
	{
		return enterCallback;
	}

	public void setCursor(GuiShape cursor)
	{
		cursorRenderer = cursor;
	}

	public boolean isValid()
	{
		return isValid;
	}

	public boolean isInvalid()
	{
		return !isValid;
	}

	// #end Getters/Setters
	@Override
	public IPosition offset()
	{
		return offset;
	}

	@Override
	public ClipArea getClipArea()
	{
		return ClipArea.from(this);
	}

	/**
	 * Adds text at current cursor position. If some text is selected, it's deleted first.
	 *
	 * @param str the text
	 */
	public void addText(String str)
	{
		if (selectingText)
			deleteSelectedText();

		if (filterFunction != null)
			str = filterFunction.apply(str);

		StringBuilder oldText = text;
		String oldValue = text.toString();

		int index = cursor.index();
		for (int i = 0; i < str.length(); i++)
		{
			char c = str.charAt(i);
			if (allowedInput.test(this, c))
				text.insert(index++, c);
		}
		String newValue = text.toString();

		if (filterFunction != null)
			newValue = filterFunction.apply(newValue);

		if (fireEvent(new ValueChange.Pre<>(this, oldValue, newValue)))
			return;

		text = new StringBuilder(newValue);
		guiText.setText(newValue);
		cursor.jumpBy(index);

		isValid = validator.test(this);

		fireEvent(new ValueChange.Post<>(this, oldValue, newValue));
	}

	/**
	 * Deletes the text currently selected.
	 */
	public void deleteSelectedText()
	{
		if (!selectingText)
			return;

		int start = Math.min(selectionCursor.index, cursor.index);
		int end = Math.max(selectionCursor.index, cursor.index);

		String oldValue = text.toString();
		String newValue = new StringBuilder(oldValue).delete(start, end)
													 .toString();

		if (fireEvent(new ValueChange.Pre<>(this, oldValue, newValue)))
		{
			selectingText = false;
			return;
		}

		text = new StringBuilder(newValue);
		guiText.setText(newValue);
		selectingText = false;
		cursor.jumpTo(start);

		isValid = validator.test(this);

		fireEvent(new ValueChange.Post<>(this, oldValue, newValue));
	}

	/**
	 * Deletes the specified <b>amount</b> of characters. Negative numbers will delete characters left of the cursor.<br>
	 * If text is already selected, delete that text instead.
	 *
	 * @param amount the amount of characters to delete
	 */
	public void deleteFromCursor(int amount)
	{
		if (!selectingText)
		{
			selectingText = true;
			selectionCursor.from(cursor);
			selectionCursor.jumpBy(amount);
		}
		deleteSelectedText();
	}

	/**
	 * Deletes the text from current cursor position to the next space.
	 *
	 * @param backwards whether to look left for the next space
	 */
	public void deleteWord(boolean backwards)
	{
		if (!selectingText)
		{
			selectingText = true;
			selectionCursor.from(cursor);
			cursor.jumpToNextSpace(backwards);
		}
		deleteSelectedText();
	}

	/**
	 * Select word.
	 */
	public void selectWord()
	{
		selectWord(cursor.index);
	}

	/**
	 * Select word.
	 *
	 * @param position the position
	 */
	public void selectWord(int position)
	{
		if (text.length() == 0)
			return;

		selectingText = true;

		selectionCursor.jumpTo(position);
		selectionCursor.jumpToNextSpace(true);
		if (Character.isWhitespace(text.charAt(selectionCursor.index)))
			selectionCursor.shiftRight();

		cursor.jumpTo(position);
		cursor.jumpToNextSpace(false);
	}

	/**
	 * Called when a cursor is updated.<br>
	 * Offsets the content to make sure the cursor is still visible.
	 */
	protected void onCursorUpdated()
	{
		if (getParent() == null)
			return;

		startTimer = System.currentTimeMillis();
		//- 4 because Padding.of(2) for clipping
		if (text.length() == 0)
			xOffset = 0;
		else if (cursor.x <= -xOffset)
			xOffset = -cursor.x;
		else if (cursor.x >= innerSize().width() - xOffset)
			xOffset = Math.min(innerSize().width() - cursor.x - 4, 0);
		else if (guiText.size()
						.width() <= innerSize().width() - xOffset - 5)
			xOffset = Math.min(innerSize().width() - guiText.size()
															.width() - 4, 0);
	}

	/**
	 * Starts selecting text if Shift key is pressed.<br>
	 * Places selection cursor at the current cursor position.
	 */
	protected void startSelecting()
	{
		if (GuiScreen.isShiftKeyDown())
		{
			if (!selectingText)
				selectionCursor.from(cursor);
			selectingText = true;
		}
		else
			selectingText = false;
	}

	//#region Input

	@Override
	public void focus()
	{
		if (!autoSelectOnFocus)
			return;
		selectingText = true;
		selectionCursor.jumpToStart();
		cursor.jumpToEnd();
	}

	@Override
	public void unfocus()
	{
		selectingText = false;
	}

	@Override
	public void mouseDown(MouseButton button)
	{
		if (button != MouseButton.LEFT)
			return;

		if (GuiScreen.isShiftKeyDown())
		{
			if (!selectingText)
			{
				selectingText = true;
				selectionCursor.from(cursor);
			}
		}
		else
			selectingText = false;

		cursor.fromMouse();
	}

	@Override
	public void mouseUp(MouseButton button)
	{
		//		if (!autoSelectOnFocus || !selectAllOnRelease || button != MouseButton.LEFT)
		//			return;
		//
		//		selectingText = true;
		//		selectionCursor.jumpTo(0);
		//		cursor.jumpTo(text.length());
		//
		//		selectAllOnRelease = false;
	}

	@Override
	public void mouseDrag(MouseButton button)
	{
		if (button != MouseButton.LEFT)
			return;

		if (!selectingText)
		{
			selectingText = true;
			selectionCursor.from(cursor);
		}

		cursor.fromMouse();
		selectAllOnRelease = false;
	}

	@Override
	public boolean keyTyped(char keyChar, int keyCode)
	{
		if (keyCode == Keyboard.KEY_ESCAPE)
		{
			EGOGui.setFocusedComponent(null);
			return true; //we don't want to close the GUI
		}

		if (GuiScreen.isCtrlKeyDown() && !Keyboard.isKeyDown(
				Keyboard.KEY_RMENU)) //for some reason, Alt gr press triggers left control key too
			return handleCtrlKeyDown(keyCode);

		switch (keyCode)
		{
			case Keyboard.KEY_LEFT:
				startSelecting();
				cursor.shiftLeft();
				return true;
			case Keyboard.KEY_RIGHT:
				startSelecting();
				cursor.shiftRight();
				return true;
			case Keyboard.KEY_HOME:
				startSelecting();
				cursor.jumpToLineStart();
				return true;
			case Keyboard.KEY_END:
				startSelecting();
				cursor.jumpToLineEnd();
				return true;
			case Keyboard.KEY_BACK:
				if (isEditable())
					deleteFromCursor(-1);
				return true;
			case Keyboard.KEY_DELETE:
				if (isEditable())
					deleteFromCursor(1);
				return true;
			case Keyboard.KEY_RETURN:
			case Keyboard.KEY_NUMPADENTER:
				if (enterCallback != null)
					enterCallback.accept(this);
				return true;
			//case Keyboard.KEY_TAB:
			//	if (isEditable())
			//		addText("\t");
			//	return true;
			default:
				if ((ChatAllowedCharacters.isAllowedCharacter(keyChar) || keyChar == '\u00a7') && isEditable())
					addText(Character.toString(keyChar));
		}
		return true;
	}

	/**
	 * Handles the key typed while a control key is pressed.
	 *
	 * @param keyCode the key code
	 * @return true, if successful
	 */
	protected boolean handleCtrlKeyDown(int keyCode)
	{
		switch (keyCode)
		{
			case Keyboard.KEY_LEFT:
				startSelecting();
				cursor.jumpToNextSpace(true);
				return true;
			case Keyboard.KEY_RIGHT:
				startSelecting();
				cursor.jumpToNextSpace(false);
				return true;
			case Keyboard.KEY_BACK:
				if (isEditable())
					deleteWord(true);
				return true;
			case Keyboard.KEY_END:
				startSelecting();
				cursor.jumpToEnd();
				return true;
			case Keyboard.KEY_A:
				selectingText = true;
				selectionCursor.jumpToStart();
				cursor.jumpToEnd();
				return true;
			case Keyboard.KEY_C:
				GuiScreen.setClipboardString(getSelectedText());
				return true;
			case Keyboard.KEY_V:
				if (isEditable())
					addText(GuiScreen.getClipboardString());
				return true;
			case Keyboard.KEY_X:
				GuiScreen.setClipboardString(getSelectedText());
				if (isEditable())
					addText("");
				return true;
			default:
				return false;
		}
	}

	/**
	 * Draws the cursor for this {@link UITextField}.
	 *
	 * @param renderer the renderer
	 */
	//#end Input
	public void drawCursor(GuiRenderer renderer)
	{
		if (cursorRenderer == null || !isFocused())
			return;

		long elaspedTime = startTimer - System.currentTimeMillis();
		if ((elaspedTime / 500) % 2 != 0)
			return;

		cursorRenderer.render(renderer);
	}

	/**
	 * Draws the selection box of this {@link UITextField}.
	 *
	 * @param renderer the renderer
	 */
	public void drawSelectionBox(GuiRenderer renderer)
	{
		if (!selectingText || selectionCursor.index == cursor.index)
			return;

		renderer.next();
		GL11.glEnable(GL11.GL_COLOR_LOGIC_OP);
		GL11.glLogicOp(GL11.GL_OR_REVERSE);

		Cursor first = cursor.index < selectionCursor.index ? cursor : selectionCursor;
		Cursor last = cursor == first ? selectionCursor : cursor;

		GuiShape s = GuiShape.builder(this)
							 .position(first)
							 .fixed(false)
							 .size(last.x() - first.x(), cursor.height())
							 .color(selectColor)
							 .build();
		s.render(renderer);

		renderer.next();

		GL11.glDisable(GL11.GL_COLOR_LOGIC_OP);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	//	@Subscribe
	//	public void onResize(SizeChangeEvent<UITextField> event)
	//	{
	//		onCursorUpdated();
	//	}

	/**
	 * Gets the property string.
	 *
	 * @return the property string
	 */
	@Override
	public String getPropertyString()
	{
		return "[" + TextFormatting.DARK_AQUA + guiText + TextFormatting.RESET + "] " + TextFormatting.DARK_PURPLE + cursor
				+ TextFormatting.RESET + " | " + super.getPropertyString();
	}

	//#region CursorPosition

	/**
	 * This class determines a position inside the text.
	 */
	public class Cursor implements IPosition
	{
		/** The text position. */
		protected int index;
		/** The char position in line. */
		protected int charIndex;
		/** The line number. */
		protected int lineIndex;

		/** The last x position. */
		protected int lastX; //used when going up and down lines, if the new line is shorter than the previous one, we want to keep the
		// correct offset for the other ones.
		/** The x position in the text. */
		protected int x;
		/** The y position in the text. */
		protected int y;
		/** The height of the line. */
		protected int height = 10;

		public int index()
		{
			return index;
		}

		/**
		 * Gets the x offset from the left of this {@link Cursor} inside this {@link UITextField}.
		 *
		 * @return the x offset
		 */
		@Override
		public int x()
		{
			return x + 2;
		}

		/**
		 * Gets the x offset from the left of this {@link Cursor} inside this {@link UITextField}.
		 *
		 * @return the y offset
		 */
		@Override
		public int y()
		{
			return y + 1;
		}

		/**
		 * Get the height of the cursor, should match the height of the current line.
		 *
		 * @return the int
		 */
		public int height()
		{
			return height;
		}

		private StringWalker back(StringWalker walker)
		{
			//			if (walker.getChar() == '\n')
			//			{
			//				int i = walker.globalIndex();
			//				walker = guiText.walker();
			//				walker.walkToIndex(i - 1);
			//			}
			return walker;
		}

		/**
		 * Sets this {@link Cursor} data based on walker current state.
		 *
		 * @param walker the walker
		 */
		private void set(StringWalker walker)
		{
			index = MathHelper.clamp(walker.globalIndex() + 1, 0, text.length()); //== globalIndex
			charIndex = walker.charIndex();
			lineIndex = walker.lineIndex();
			x = (int) walker.lineWidth();
			y = (int) walker.y(); //== 0
			height = (int) Math.ceil(walker.lineHeight());
			if (height < 9)
				height = 9;

			//if cursor after a \n, virtually set its position at the beginning of the next line.
			if (walker.getChar() == '\n')
			{
				x = 0;
				y += height;
			}

			onCursorUpdated();
		}

		public void from(Cursor cursor)
		{
			index = cursor.index;
			charIndex = cursor.charIndex;
			lineIndex = cursor.lineIndex;
			height = cursor.height;
			x = cursor.x;
			y = cursor.y;
		}

		/**
		 * Updates this cursor based on mouse position.
		 */
		public void fromMouse()
		{
			StringWalker walker = guiText.walker();
			if (!walker.walkToY(mousePosition().y()))
			{
				jumpToEnd();
				lastX = x;
				return;
			}

			int x = mousePosition().x();
			walker.walkToX(x);
			walker = back(walker);
			int i = walker.globalIndex();
			if (x > walker.x() + walker.width() / 2)
				i++;
			jumpTo(i);
			lastX = x;
		}

		/**
		 * Sets this {@link Cursor} at the specified position in the text.
		 *
		 * @param index the new cursor position
		 */
		public void jumpTo(int index)
		{
			index = MathHelper.clamp(index, 0, text.length());
			StringWalker walker = guiText.walker();
			walker.walkToIndex(index - 1);
			set(walker);
			lastX = x;
			onCursorUpdated();
		}

		/**
		 * Moves this {@link Cursor} by a specified amount.
		 *
		 * @param amount the amount
		 */
		public void jumpBy(int amount)
		{
			jumpTo(index + amount);
		}

		/**
		 * Moves this {@link Cursor} to the beginning of the text.
		 */
		public void jumpToStart()
		{
			jumpTo(0);
		}

		/**
		 * Moves this {@link Cursor} to the end of the text.
		 */
		public void jumpToEnd()
		{
			jumpTo(text.length());
		}

		public void jumpToLineStart()
		{
			jumpTo(index - charIndex - 1);
		}

		public void jumpToLineEnd()
		{
			StringWalker walker = guiText.walker();
			walker.walkToIndex(index);
			walker.walkToEOL();
			walker = back(walker);
			set(walker);
		}

		/**
		 * Moves this {@link Cursor} one step to the left. If the cursor is at the beginning of the line, it is moved to the end of the
		 * previous line without changing the {@link #index}.
		 */
		public void shiftLeft()
		{
			//if (index == 0)
			//	return;

			if (FontOptions.isFormatting(text.toString(), index - 2))
				jumpBy(-2);
			else
				jumpBy(-1);
		}

		/**
		 * Moves this {@link Cursor} one step to the right. If the cursor is at the end of the line, it is moved to the start of the next
		 * line whithout changing the {@link #index}.
		 */
		public void shiftRight()
		{
			if (index == text.length())
				return;

			if (FontOptions.isFormatting(text.toString(), index))
				jumpBy(2);
			else
				jumpBy(1);
		}

		public void jumpLine(boolean backwards)
		{
			if (lineIndex == 0 && backwards)
				return;
			if (lineIndex == guiText.lineCount() - 1 && !backwards)
				return;

			StringWalker walker = guiText.walker();
			walker.walkUntil(w -> w.lineIndex() == lineIndex + (backwards ? -1 : 1));
			walker.walkToX(lastX);
			//if (guiText.lines().get(walker.lineIndex()).text().length() == 1)
			//	walker = back(walker);
			set(walker);
		}

		/**
		 * Moves this {@link Cursor} to the next space position.
		 *
		 * @param backwards backwards whether to look left of the cursor
		 */
		public void jumpToNextSpace(boolean backwards)
		{
			int pos = index;
			int step = backwards ? -1 : 1;

			pos += step;
			while (pos > 0 && pos < text.length() && Character.isLetterOrDigit(text.charAt(pos)))
			{
				pos += step;
			}

			jumpTo(pos);
		}

		@Override
		public String toString()
		{
			return "Index : " + index + " (L" + lineIndex + " C" + charIndex + ") at " + x() + "," + y() + " (offset: " + offset + ")";
		}
	}
	//#end CursorPosition

	public static UITextFieldBuilder builder()
	{
		return new UITextFieldBuilder();
	}

	public static class UITextFieldBuilder extends UIComponentBuilder<UITextFieldBuilder, UITextField>
			implements IFontOptionsBuilder<UITextFieldBuilder, UITextField>
	{
		private String text = "";
		private boolean editable = true;
		protected boolean autoSelectOnFocus = true;

		private GuiShape cursor;
		protected int cursorColor = 0xD0D0D0;
		protected int selectColor = 0x0000FF;

		protected Function<String, String> filterFunction = Function.identity();
		protected BiPredicate<UITextField, Character> allowedInput = (tf, c) -> true;
		protected Consumer<UITextField> enterCallback;
		protected Predicate<UITextField> validator = tf -> true;

		protected FontOptionsBuilder fontOptionsBuilder = FontOptions.builder();

		private UITextFieldBuilder()
		{
			width(100);
			height(12);
			fob().color(0xFFFFFF)
				 .shadow();//default for UITextfields
		}

		@Override
		public FontOptionsBuilder fob()
		{
			return fontOptionsBuilder;
		}

		@Override
		public void setFontOptionsBuilder(FontOptionsBuilder builder)
		{
			fontOptionsBuilder = checkNotNull(builder);
		}

		@Override
		public UITextFieldBuilder when(Predicate<UITextField> predicate)
		{
			fontOptionsBuilder = fob().when(predicate);
			return this;
		}

		public UITextFieldBuilder text(String text)
		{
			this.text = text;
			return this;
		}

		public UITextFieldBuilder editable()
		{
			return editable(true);
		}

		public UITextFieldBuilder editable(boolean editable)
		{
			this.editable = editable;
			return this;
		}

		public UITextFieldBuilder autoSelectOnFocus()
		{
			return autoSelectOnFocus(true);
		}

		public UITextFieldBuilder autoSelectOnFocus(boolean autoSelect)
		{
			autoSelectOnFocus = autoSelect;
			return this;
		}

		public UITextFieldBuilder cursor(GuiShape cursor)
		{
			this.cursor = cursor;
			return this;
		}

		public UITextFieldBuilder cursorColor(int color)
		{
			cursorColor = color;
			return this;
		}

		public UITextFieldBuilder filter(Function<String, String> func)
		{
			filterFunction = func;
			return this;
		}

		public UITextFieldBuilder allowedInput(BiPredicate<UITextField, Character> allowedInput)
		{
			this.allowedInput = checkNotNull(allowedInput);
			return this;
		}

		public UITextFieldBuilder allowedInput(Predicate<Character> allowedInput)
		{
			return allowedInput((tc, c) -> allowedInput.test(c));
		}

		public UITextFieldBuilder validator(Predicate<UITextField> validator)
		{
			this.validator = checkNotNull(validator);
			return this;
		}

		public UITextFieldBuilder onEnter(Runnable callback)
		{
			return onEnter(tf -> callback.run());
		}

		public UITextFieldBuilder onEnter(Consumer<UITextField> callback)
		{
			enterCallback = callback;
			return this;
		}

		@Override
		public UITextField build()
		{
			UITextField tf = build(new UITextField());
			tf.setText(text);
			tf.setEditable(editable);
			tf.setAutoSelectOnFocus(autoSelectOnFocus);

			if (cursor != null)
				tf.setCursor(cursor);
			tf.setCursorColor(cursorColor);
			tf.setFilter(filterFunction);
			tf.setAllowedInput(allowedInput);
			tf.setEnterCallback(enterCallback);
			tf.setValidator(validator);

			fob().withPredicateParameter(tf);

			tf.setFontOptions(buildFontOptions());

			return tf;
		}
	}
}

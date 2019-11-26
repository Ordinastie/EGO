package net.malisis.ego.gui.component.layout;

import static com.google.common.base.Preconditions.*;

import com.google.common.collect.Lists;
import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.component.container.UIContainer;
import net.malisis.ego.gui.element.position.Position;
import net.malisis.ego.gui.element.position.Position.IPosition;
import net.malisis.ego.gui.element.position.Position.IPositioned;
import net.malisis.ego.gui.element.size.Size;
import net.malisis.ego.gui.element.size.Size.ISize;
import net.malisis.ego.gui.element.size.Size.ISized;

import java.util.List;
import java.util.function.IntSupplier;

public class GridLayout implements ILayout
{
	protected UIContainer parent;
	protected int rows;
	protected int columns;
	protected ISize cellSize;
	protected int cellSpacing;
	protected int rowSpacing;

	private Row currentRow;
	private int currentColumn;

	private IntSupplier fitWidth = () -> parent.size()
											   .width() / columns;

	public GridLayout(GridLayoutBuilder builder)
	{
		this.parent = builder.parent;
		this.columns = builder.cols;
		this.rows = builder.rows;
		this.cellSize = builder.cellSize;
		this.cellSpacing = builder.cellSpacing;
		this.rowSpacing = builder.rowSpacing;

		currentRow = new Row(Position.ZERO, Size.of(parent.size()::width, cellSize::height));
	}

	@Override
	public void add(UIComponent component)
	{
		if (rows > 0)
		{
			if (rows * columns <= currentRow.row * currentColumn)
				throw new IllegalStateException(
						String.format("Elements size exceed grid size (%d > %d", currentRow.row * currentColumn, rows * columns));
		}

		currentRow = currentRow.add(component);
		parent.add(component);
	}

	private class Row implements IPositioned, ISized
	{
		private int row;
		private IPosition position;
		private ISize size;
		private List<Cell> cells = Lists.newArrayList();

		private Row(IPosition position, ISize size)
		{
			this.row = currentRow != null ? currentRow.row + 1 : 0;
			this.position = position;
			this.size = size;
		}

		public Row add(UIComponent component)
		{
			if (cells.size() >= columns)
				return new Row(position, size).add(component);

			cells.add(new Cell(row, cells.size(), component));
			return this;
		}

		@Override
		public String toString()
		{
			return "Row " + row + " [" + columns + " cells] (" + position + "@" + size + ")";
		}
	}

	private class Cell implements IPositioned, ISized
	{
		private int row, col;
		private IPosition position;
		private ISize size;
		private UIComponent component;

		private Cell(int row, int col, UIComponent component)
		{
			this.row = row;
			this.col = col;
			component.setPosition(Position.of(col * (cellSize.width() + cellSpacing), row * (cellSize.height() + rowSpacing)));
			this.component = component;
		}

		@Override
		public String toString()
		{
			return "Cell " + row + "." + col + " (" + position + "@" + size + ") " + component;
		}
	}

	public static GridLayoutBuilder builder(UIContainer parent)
	{
		return new GridLayoutBuilder(checkNotNull(parent));
	}

	public static class GridLayoutBuilder
	{
		private UIContainer parent;
		private int cols;
		private int rows = -1;
		private ISize cellSize;
		private int cellSpacing = 1;
		private int rowSpacing = 1;

		public GridLayoutBuilder(UIContainer parent)
		{
			this.parent = parent;
		}

		public GridLayoutBuilder columns(int columns)
		{
			checkArgument(columns > 0, "Columns must be positive");
			cols = columns;
			return this;
		}

		public GridLayoutBuilder rows(int numRows)
		{
			rows = numRows;
			return this;
		}

		public GridLayoutBuilder cellSize(ISize size)
		{
			cellSize = size;
			return this;
		}

		public GridLayoutBuilder cellSpacing(int spacing)
		{
			cellSpacing = spacing;
			return this;
		}

		public GridLayoutBuilder rowSpacing(int spacing)
		{
			rowSpacing = spacing;
			return this;
		}

		public GridLayoutBuilder spacing(int spacing)
		{
			return spacing(spacing, spacing);
		}

		public GridLayoutBuilder spacing(int cellSpacing, int rowSpacing)
		{
			cellSpacing(cellSpacing);
			rowSpacing(rowSpacing);
			return this;
		}

		public GridLayout build()
		{
			return new GridLayout(this);
		}
	}

}

package net.malisis.ego.gui.component.control;

import net.malisis.ego.gui.component.decoration.UILabel;

public class Title extends UILabel implements IControlComponent
{
	protected Title()
	{
	}

	@Override
	public int top()
	{
		return text.isEmpty() ? 0 : text.height() + getParent().paddingTop();
	}

	public static class Builder extends UILabelBuilderG<Builder, Title>
	{
		public Builder()
		{
		}

		@Override
		public Title build()
		{
			return build(new Title());
		}
	}
}

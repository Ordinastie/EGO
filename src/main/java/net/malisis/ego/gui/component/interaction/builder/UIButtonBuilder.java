package net.malisis.ego.gui.component.interaction.builder;

import static com.google.common.base.Preconditions.*;

import net.malisis.ego.gui.component.container.UIContainer;
import net.malisis.ego.gui.component.content.IContent;
import net.malisis.ego.gui.component.decoration.UIImage;
import net.malisis.ego.gui.component.decoration.UITooltip;
import net.malisis.ego.gui.component.interaction.UIButton;
import net.malisis.ego.gui.element.position.Position.IPosition;
import net.malisis.ego.gui.element.size.Size.ISize;
import net.malisis.ego.gui.render.GuiIcon;
import net.malisis.ego.gui.render.GuiTexture;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.Consumer;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public final class UIButtonBuilder
{
	@Nullable
	private String name;
	private IPosition position;
	private ISize size;
	private int zIndex;

	@Nullable
	private IContent content;
	@Nullable
	private String text;
	@Nullable
	private UIContainer parent;
	@Nullable
	private UITooltip tooltip;
	@Nullable
	private Consumer<UIButton> onClickRunnable;

	private boolean enabled = true;
	private boolean visible = true;

	public UIButtonBuilder()
	{
	}

	public UIButtonBuilder text(String text)
	{
		this.text = text;
		return this;
	}

	public UIButtonBuilder image(UIImage image)
	{
		this.content = image;
		return this;
	}

	public UIButtonBuilder icon(GuiIcon icon)
	{
		return image(new UIImage(icon));
	}

	public UIButtonBuilder texture(GuiTexture texture)
	{
		return image(new UIImage(new GuiIcon(texture)));
	}

	public UIButtonBuilder position(IPosition position)
	{
		this.position = checkNotNull(position);
		return this;
	}

	public UIButtonBuilder size(ISize size)
	{
		this.size = checkNotNull(size);
		return this;
	}

	public UIButtonBuilder zIndex(int zIndex)
	{
		this.zIndex = zIndex;
		return this;
	}

	public UIButtonBuilder tooltip(String text)
	{
		return tooltip(new UITooltip(text, 15));
	}

	public UIButtonBuilder tooltip(UITooltip tooltip)
	{
		this.tooltip = tooltip;
		return this;
	}

	public UIButtonBuilder enabled(boolean enabled)
	{
		this.enabled = enabled;
		return this;
	}

	public UIButtonBuilder visible(boolean visible)
	{
		this.visible = visible;
		return this;
	}

	public UIButtonBuilder parent(UIContainer parent)
	{
		this.parent = parent;
		return this;
	}

	public UIButtonBuilder onClick(Consumer<UIButton> onClickRunnable)
	{
		this.onClickRunnable = onClickRunnable;
		return this;
	}

	@SuppressWarnings("unchecked")
	public UIButton build()
	{
		UIButton button = new UIButton();
		button.setPosition(position);
		button.setSize(size);

		if (name != null)
			button.setName(name);

		if (text != null)
		{
			button.setText(text);
		}
		if (content != null)
		{
			button.setContent(content);
		}
		if (tooltip != null)
		{
			button.setTooltip(tooltip);
		}
		if (parent != null)
		{
			parent.add(button);
		}
		if (onClickRunnable != null)
		{
			button.onClick(onClickRunnable);
		}
		button.setEnabled(enabled);
		button.setVisible(visible);
		button.setZIndex(zIndex);

		return button;
	}
}
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

package net.malisis.ego.atlas;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.malisis.ego.EGO;
import net.malisis.ego.command.EGOCommand;
import net.malisis.ego.command.LayeredCommand;
import net.malisis.ego.gui.MalisisGui;
import net.malisis.ego.gui.render.GuiIcon;
import net.malisis.ego.gui.render.GuiTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Ordinastie
 */
public class Atlas implements ITextureObject
{
	public static final ResourceLocation ATLAS_LOCATION = new ResourceLocation(EGO.modid, "textures/atlas.png");
	private static Atlas ATLAS = new Atlas();

	static
	{
		LayeredCommand atlasCommand = new LayeredCommand("atlas");
		atlasCommand.registerCommand("register", Atlas::reloadRegisters);
		atlasCommand.registerCommand("reload", Atlas::reloadAtlas);
		atlasCommand.registerCommand("show", Atlas::showAtlas);
		EGOCommand.registerCommand("atlas", atlasCommand);
	}

	private List<Consumer<Atlas>> registers = Lists.newArrayList();
	private final Map<ResourceLocation, Holder> holders = Maps.newHashMap();
	private int glTextureId = -1;

	public void registerIcons()
	{
		System.out.println("Registering icons");
		holders.clear();

		registers.forEach(r -> r.accept(this));
	}

	public GuiIcon registerIcon(ResourceLocation resourceLocation, ResourceLocation from, int x, int y, int w, int h)
	{
		if (holders.get(resourceLocation) != null)
			return holders.get(resourceLocation)
						  .icon();

		GuiIcon icon = new GuiIcon(resourceLocation);
		Holder holder = new Holder(icon);
		if (from != null)
			holder.subOf(from, x, y, w, h);
		holders.put(resourceLocation, holder);
		return icon;
	}

	@Override
	public void loadTexture(IResourceManager resourceManager)
	{
		deleteGlTexture();

		int maxSize = Minecraft.getGLMaximumTextureSize();
		Stitcher stitcher = new Stitcher(maxSize, maxSize);

		//load texture data into holders
		holders.values()
			   .forEach(h -> h.loadTexture(resourceManager));

		//allocates node positions and expand atlas if necessary
		stitcher.stitch(holders);
		EGO.log.info("Created: {}x{} atlas for GUIs", stitcher.width(), stitcher.height());
		TextureUtil.allocateTexture(this.getGlTextureId(), stitcher.width(), stitcher.height());
		MalisisGui.DEFAULT_TEXTURE = new GuiTexture(ATLAS_LOCATION, stitcher.width(), stitcher.height());

		//upload texture data to the texture
		holders.values()
			   .forEach(h -> h.upload(stitcher.width(), stitcher.height()));
	}

	@Override
	public void setBlurMipmap(boolean blurIn, boolean mipmapIn)
	{

	}

	@Override
	public void restoreLastBlurMipmap()
	{

	}

	public int getGlTextureId()
	{
		if (this.glTextureId == -1)
		{
			this.glTextureId = TextureUtil.glGenTextures();
		}

		return this.glTextureId;
	}

	public void deleteGlTexture()
	{
		if (this.glTextureId != -1)
		{
			TextureUtil.deleteTexture(this.glTextureId);
			this.glTextureId = -1;
		}
	}

	public static List<GuiIcon> registeredIcons()
	{
		return ATLAS.holders.values()
							.stream()
							.map(Holder::icon)
							.collect(Collectors.toList());
	}

	public static GuiIcon register(ResourceLocation resourceLocation)
	{
		return ATLAS.registerIcon(resourceLocation, null, 0, 0, 0, 0);
	}

	public static GuiIcon register(ResourceLocation resourceLocation, ResourceLocation from, int x, int y, int w, int h)
	{
		return ATLAS.registerIcon(resourceLocation, from, x, y, w, h);
	}

	public static void addRegister(Consumer<Atlas> consumer)
	{
		ATLAS.registers.add(consumer);
	}

	public static void init()
	{
		ATLAS.registerIcons();
		Minecraft.getMinecraft()
				 .getTextureManager()
				 .loadTexture(ATLAS_LOCATION, ATLAS);
	}

	public static void reloadRegisters()
	{
		ATLAS.registerIcons();
		reloadAtlas();
	}

	public static void reloadAtlas()
	{
		ATLAS.loadTexture(Minecraft.getMinecraft()
								   .getResourceManager());
	}

	public static void showAtlas()
	{
		new GuiAtlas().display(true);
	}

	@SideOnly(Side.CLIENT)
	public static class Holder implements Comparable<Holder>
	{
		private int[] textureData;
		private final GuiIcon icon;
		private int x;
		private int y;
		private int width;
		private int height;

		private ResourceLocation sub;
		private int subX;
		private int subY;

		public Holder(GuiIcon icon)
		{
			this.icon = icon;
		}

		public void subOf(ResourceLocation sub, int x, int y, int w, int h)
		{
			this.sub = sub;
			this.subX = x;
			this.subY = y;
			this.width = w;
			this.height = h;
		}

		public GuiIcon icon()
		{
			return this.icon;
		}

		public IResource getResource(IResourceManager manager) throws IOException
		{
			return manager.getResource(sub != null ? sub : icon.location());
		}

		public BufferedImage clip(BufferedImage img)
		{
			if (sub == null)
				return img;

			return img.getSubimage(subX, subY, width, height);
		}

		public int x()
		{
			return x;
		}

		public int y()
		{
			return y;
		}

		public int width()
		{
			return width;
		}

		public int height()
		{
			return height;
		}

		public int[] textureData()
		{
			return textureData;
		}

		public void setPosition(int x, int y)
		{
			this.x = x;
			this.y = y;
		}

		public void loadTexture(IResourceManager manager)
		{
			try (IResource res = manager.getResource(sub != null ? sub : icon.location()))
			{
				BufferedImage img = TextureUtil.readBufferedImage(res.getInputStream());
				if (sub == null)
				{
					width = img.getWidth();
					height = img.getHeight();
				}

				textureData = img.getRGB(subX, subY, width, height, null, 0, width);
			}
			catch (IOException e)
			{
				EGO.log.error("Failed to load texture for GUI atlas : {}", icon.location(), e);
			}
		}

		public void upload(int atlasWidth, int atlasHeight)
		{
			if (textureData == null)
				return;
			//upload icon texture data at the correct place on the atlas texture
			int[][] data = new int[1][];
			data[0] = textureData;
			TextureUtil.uploadTextureMipmap(data, width, height, x, y, false, false);

			icon.stitch(MalisisGui.DEFAULT_TEXTURE, (float) x / atlasWidth, (float) y / atlasHeight, (float) (x + width) / atlasWidth,
						(float) (y + height) / atlasHeight);
		}

		public String toString()
		{
			return icon.location() + " - " + width + "x" + height;
		}

		public int compareTo(Holder other)
		{
			if (height() > other.height())
				return -1;
			if (height() < other.height())
				return 1;
			if (width() > other.width())
				return -1;
			if (width() < other.width())
				return 1;

			return Comparator.<ResourceLocation>naturalOrder().compare(icon().location(), other.icon()
																							   .location());
		}
	}
}

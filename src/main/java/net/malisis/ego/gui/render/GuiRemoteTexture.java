/*
 * This file is part of Almura.
 *
 * Copyright (c) AlmuraDev <https://github.com/AlmuraDev/>
 *
 * All Rights Reserved.
 */
package net.malisis.ego.gui.render;

import com.google.common.collect.Maps;
import net.malisis.ego.EGO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@SideOnly(Side.CLIENT)
public class GuiRemoteTexture extends GuiTexture
{
	//TODO: disk cache ?
	private static final Map<String, ResourceLocation> CACHE = Maps.newHashMap();

	private static final String USER_AGENT =
			"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.31 (KHTML, like Gecko) " + "Chrome/26.0.1410.65 Safari/537.31";

	private final String url;

	// TODO: error RL ?
	public GuiRemoteTexture(String url, ResourceLocation loadingRl, int width, int height)
	{
		super(loadingRl, width, height);
		this.url = url;
		ResourceLocation rl = CACHE.get(url);
		if (rl == null)
			CompletableFuture.supplyAsync(this::getStream).thenAccept(this::registerTexture);
		else
			resourceLocation = rl;
	}

	private InputStream getStream()
	{
		HttpURLConnection httpURLConnection = null;

		try
		{
			httpURLConnection = (HttpURLConnection) (new URL(url).openConnection(Minecraft.getMinecraft().getProxy()));
			httpURLConnection.setRequestProperty("User-Agent", USER_AGENT);
			httpURLConnection.connect();

			if (httpURLConnection.getResponseCode() / 100 == 2)
				return httpURLConnection.getInputStream();
		}
		catch (IOException e)
		{
			EGO.log.warn("Couldn't load remote texture :", e);
			if (httpURLConnection != null)
				httpURLConnection.disconnect();
		}

		return null;
	}

	private void registerTexture(InputStream stream)
	{
		if (stream == null)
		{
			EGO.message("Couldn't retrieve texture at %s.");
			return;
		}

		Minecraft.getMinecraft().addScheduledTask(() -> {
			try
			{
				BufferedImage image = TextureUtil.readBufferedImage(stream);
				DynamicTexture dynTex = new DynamicTexture(image);
				width = image.getWidth();
				height = image.getHeight();
				resourceLocation = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation(url, dynTex);
				CACHE.put(url, resourceLocation);
			}
			catch (IOException e)
			{
				EGO.message("Failed to register texture : %s", e.getMessage());
				EGO.log.error("Failed to register texture : {}", e.getMessage(), e);
			}
		});
	}
}

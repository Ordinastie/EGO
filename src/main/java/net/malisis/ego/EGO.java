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

package net.malisis.ego;

import net.malisis.ego.gui.MalisisGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

@Mod(modid = EGO.modid, name = EGO.modname, version = EGO.version, acceptedMinecraftVersions = "[1.12,1.13)")
public class EGO
{
	/** Mod ID. */
	public static final String modid = "ego";
	/** Mod name. */
	public static final String modname = "EGO";
	/** Current version. */
	public static final String version = "${version}";
	/** Url for the mod. */
	public static final String url = "";
	/** Logger for the mod. */
	public static Logger log = LogManager.getLogger(modid);
	/** Whether the mod is currently running in obfuscated environment or not. */
	public static boolean isObfEnv = !(boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

	/**
	 * Post-initialization event
	 *
	 * @param event the event
	 */
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		ClientCommandHandler.instance.registerCommand(new EGOCommand());
		//register this to the EVENT_BUS for onGuiClose()
		MinecraftForge.EVENT_BUS.register(this);
	}

	/**
	 * GuiDemo close event.<br>
	 * Used to cancel the closing of the {@link MalisisGui} when opened from command line.
	 *
	 * @param event the event
	 */
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onGuiClose(GuiOpenEvent event)
	{
		if (!MalisisGui.cancelClose || event.getGui() != null)
			return;

		MalisisGui.cancelClose = false;
		event.setCanceled(true);
	}

	/**
	 * Gets the client world.
	 *
	 * @return the client world
	 */
	@SideOnly(Side.CLIENT)
	public static World world()
	{
		return Minecraft.getMinecraft().world;
	}

	/**
	 * Gets the client player.
	 *
	 * @return the client player
	 */
	@SideOnly(Side.CLIENT)
	public static EntityPlayer player()
	{
		return Minecraft.getMinecraft().player;
	}

	/**
	 * Displays a text in the chat.
	 *
	 * @param text the text
	 */
	public static void message(Object text)
	{
		message(text, (Object) null);
	}

	/**
	 * Displays a text in the chat.<br>
	 * Client side calls will display italic and grey text.<br>
	 * Server side calls will display white text. The text will be sent to all clients connected.
	 *
	 * @param text the text
	 * @param data the data
	 */
	public static void message(Object text, Object... data)
	{
		EntityPlayer player = player();
		if (player == null)
			return;

		String txt = text != null ? text.toString() : "null";
		if (text instanceof Object[])
			txt = Arrays.deepToString((Object[]) text);

		TextComponentString msg = new TextComponentString(I18n.format(txt, data));
		Style cs = new Style();
		cs.setItalic(true);
		cs.setColor(TextFormatting.GRAY);
		msg.setStyle(cs);
		player.sendMessage(msg);
	}
}

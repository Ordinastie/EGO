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

package net.malisis.ego.command;

import com.google.common.collect.Lists;
import net.malisis.ego.EGO;
import net.malisis.ego.GuiDemo;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

/**
 * Commands handler for {@link EGO} mod.
 *
 * @author Ordinastie
 */
public class EGOCommand extends CommandBase
{
	public static final EGOCommand INSTANCE = new EGOCommand();

	private LayeredCommand layeredCommand = new LayeredCommand("EGO", "ego.commands.usage");

	/**
	 * Instantiates the command
	 */
	public EGOCommand()
	{
		layeredCommand.registerCommand("demo", () -> new GuiDemo().display(true));
		layeredCommand.registerCommand("version", () -> EGO.message("ego.commands.modversion", EGO.version));
	}

	@Override
	public String getName()
	{
		return "EGO";
	}

	@Override
	public List<String> getAliases()
	{
		return Lists.newArrayList("ego");
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "ego.commands.usage";
	}

	/**
	 * Processes the command.
	 *
	 * @param sender the sender
	 * @param params the params
	 */
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] params) throws CommandException
	{
		if (params.length == 0)
			throw new WrongUsageException("ego.commands.usage");

		ArrayList<String> args = Lists.newArrayList(params);
		String err = layeredCommand.execute(server, sender, args);
		if (err != null)
			throw new WrongUsageException(err);
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender)
	{
		return true;
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] params, BlockPos pos)
	{
		ArrayList<String> args = Lists.newArrayList(params);
		return layeredCommand.getTabCompletions(server, sender, args, pos);
	}

	@Override
	public boolean isUsernameIndex(String[] astring, int i)
	{
		return false;
	}

	public static void registerCommand(String name, Runnable runnable)
	{
		INSTANCE.layeredCommand.registerCommand(name, runnable);
	}

	public static void registerCommand(LayeredCommand command)
	{
		INSTANCE.layeredCommand.registerCommand(command.name(), command);
	}

	public static void registerCommand(String name, ISubCommand command)
	{
		INSTANCE.layeredCommand.registerCommand(name, command);
	}
}

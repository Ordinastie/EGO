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

import com.google.common.collect.Sets;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Set;

/**
 * Commands handler for {@link EGO} mod.
 *
 * @author Ordinastie
 */
public class EGOCommand extends CommandBase
{
	/** List of parameters available for this {@link EGOCommand}. */
	private Set<String> parameters = Sets.newHashSet();

	/**
	 * Instantiates the command
	 */
	public EGOCommand()
	{
		parameters.add("demo");
		parameters.add("version");
	}

	/**
	 * Gets the command name.
	 *
	 * @return the command name
	 */
	@Override
	public String getName()
	{
		return "EGO";
	}

	/**
	 * Gets the command usage.
	 *
	 * @param sender the sender
	 * @return the command usage
	 */
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

		if (!parameters.contains(params[0]))
			throw new WrongUsageException("ego.commands.usage");

		switch (params[0])
		{
			case "demo":
				new GuiDemo().display(true);
				break;
			case "version":
				EGO.message("ego.commands.modversion", EGO.version);
				break;

			default:
				EGO.message("EGO command unknown.");
				break;
		}

	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender)
	{
		return true;
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] params, BlockPos pos)
	{
		//		if (params.length == 1)
		//			return getListOfStringsMatchingLastWord(params, parameters);
		//		else if (params.length == 2 && params[0].equals("debug"))
		//			return getListOfStringsMatchingLastWord(params, debugs.keySet());
		//		else if (params.length == 2)
		//			return getListOfStringsMatchingLastWord(params, MalisisCore.listModId());
		//		else
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] astring, int i)
	{
		return false;
	}
}

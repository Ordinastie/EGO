package net.malisis.ego.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Map;

public class LayeredCommand implements ISubCommand
{
	private String name;
	private String errorMessage;
	private Map<String, ISubCommand> subcommands = Maps.newHashMap();

	public LayeredCommand(String name, String errorMessage)
	{
		this.name = name;
		this.errorMessage = errorMessage;
	}

	public LayeredCommand(String name)
	{
		this.name = name;
	}

	public String name()
	{
		return name;
	}

	private String errorMessage()
	{
		if (this.errorMessage != null)
			return errorMessage;

		return "Available parameters : " + String.join(", ", subcommands.keySet());
	}

	public void registerCommand(String name, Runnable runnable)
	{
		registerCommand(name, (server, sender, args) -> {
			runnable.run();
			return null;
		});
	}

	public void registerCommand(String name, ISubCommand command)
	{
		subcommands.put(name, command);
	}

	public ISubCommand getCommand(List<String> params)
	{
		if (params.size() == 0)
			return null;
		return subcommands.get(params.remove(0));
	}

	@Override
	public String execute(MinecraftServer server, ICommandSender sender, List<String> args)
	{
		if (args.size() == 0)
			return errorMessage();

		ISubCommand command = getCommand(args);
		if (command == null)
			return errorMessage();

		return command.execute(server, sender, args);
	}

	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, List<String> params, BlockPos pos)
	{
		ISubCommand command = getCommand(params);
		if (command instanceof LayeredCommand)
			return ((LayeredCommand) command).getTabCompletions(server, sender, params, pos);
		else if (command != null)
			return Lists.newArrayList();
		return Lists.newArrayList(subcommands.keySet());
	}

	@Override
	public String toString()
	{
		return name();
	}
}
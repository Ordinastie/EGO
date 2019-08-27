package net.malisis.ego.command;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import java.util.List;

public interface ISubCommand
{
	//return error message
	public String execute(MinecraftServer server, ICommandSender sender, List<String> args);
}

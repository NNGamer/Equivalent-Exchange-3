package com.pahimar.ee3.command;

import com.google.common.base.Joiner;
import com.pahimar.ee3.reference.Messages;
import com.pahimar.ee3.reference.Names;
import net.minecraft.command.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommandEE extends CommandBase {

    private static List<CommandBase> modCommands = new ArrayList<>();
    private static List<String> commands = new ArrayList<>();

    @Override
    public String getCommandName() {
        return Names.Commands.BASE_COMMAND;
    }

    @Override
    public String getCommandUsage(ICommandSender commandSender) {
        return Messages.Commands.BASE_COMMAND_USAGE;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

        boolean found = false;

        if (args.length >= 1) {

            for (CommandBase command : modCommands) {

                if (command.getCommandName().equalsIgnoreCase(args[0]) && command.checkPermission(server, sender)) {
                    found = true;
                    command.execute(server, sender, args);
                }
            }
        }

        if (!found) {
            throw new WrongUsageException("Invalid command. Usage: /ee3 " + Joiner.on(" ").join(commands));
        }
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {

        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, commands);
        }
        else if (args.length >= 2) {
            for (CommandBase command : modCommands) {
                if (command.getCommandName().equalsIgnoreCase(args[0])) {
                    return command.getTabCompletionOptions(server, sender, args, pos);
                }
            }
        }

        return null;
    }

    static {
        modCommands.add(new CommandSetEnergyValue());
        modCommands.add(new CommandPlayerLearnItem());
        modCommands.add(new CommandPlayerForgetEverything());
        modCommands.add(new CommandPlayerForgetItem());
        modCommands.add(new CommandSetItemLearnable());
        modCommands.add(new CommandSetItemNotLearnable());
        modCommands.add(new CommandSetItemRecoverable());
        modCommands.add(new CommandSetItemNotRecoverable());
        modCommands.add(new CommandSyncEnergyValues());
        modCommands.add(new CommandRegenEnergyValues());
        modCommands.add(new CommandRunTest());

        commands.addAll(modCommands.stream().map(ICommand::getCommandName).collect(Collectors.toList()));
    }
}

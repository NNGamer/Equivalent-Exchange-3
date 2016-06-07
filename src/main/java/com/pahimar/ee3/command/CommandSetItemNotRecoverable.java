package com.pahimar.ee3.command;

import com.pahimar.ee3.api.blacklist.BlacklistRegistryProxy;
import com.pahimar.ee3.network.Network;
import com.pahimar.ee3.network.message.MessageSetBlacklistEntry;
import com.pahimar.ee3.reference.Messages;
import com.pahimar.ee3.reference.Names;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.List;

public class CommandSetItemNotRecoverable extends CommandBase {

    @Override
    public String getCommandName() {
        return Names.Commands.SET_ITEM_NOT_RECOVERABLE;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender commandSender) {
        return Messages.Commands.SET_ITEM_NOT_RECOVERABLE_USAGE;
    }

    @Override
    public void execute(MinecraftServer minecraftServer, ICommandSender commandSender, String[] args) throws CommandException {

        if (args.length < 2) {
            throw new WrongUsageException(Messages.Commands.SET_ITEM_NOT_RECOVERABLE_USAGE);
        }
        else {

            Item item = getItemByText(commandSender, args[1]);
            int metaData = args.length >= 3 ? parseInt(args[2]) : 0;

            ItemStack itemStack = new ItemStack(item, 1, metaData);

            if (args.length >= 4) {

                String stringNBTData = getChatComponentFromNthArg(commandSender, args, 3).getUnformattedText();

                try {
                    itemStack.setTagCompound(JsonToNBT.getTagFromJson(stringNBTData));
                }
                catch (Exception exception) {
                    notifyCommandListener(commandSender, this, Messages.Commands.INVALID_NBT_TAG_ERROR, exception.getMessage());
                    return;
                }
            }

            // TODO Check to see if the request runs before telling everyone it did
            BlacklistRegistryProxy.addToBlacklist(itemStack, BlacklistRegistryProxy.Blacklist.EXCHANGE);
            Network.INSTANCE.sendToAll(new MessageSetBlacklistEntry(itemStack, BlacklistRegistryProxy.Blacklist.EXCHANGE));
            notifyCommandListener(commandSender, this, Messages.Commands.SET_ITEM_NOT_RECOVERABLE_SUCCESS, commandSender.getName(), itemStack.getTextComponent());
        }
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer minecraftServer, ICommandSender commandSender, String[] args, @Nullable BlockPos pos) {

        if (args.length == 2) {
            return getListOfStringsMatchingLastWord(args, Item.REGISTRY.getKeys());
        }

        return null;
    }
}

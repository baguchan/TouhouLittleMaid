package com.github.tartaricacid.touhoulittlemaid.command;

import com.github.tartaricacid.touhoulittlemaid.command.subcommand.MaidNumCommand;
import com.github.tartaricacid.touhoulittlemaid.command.subcommand.PackCommand;
import com.github.tartaricacid.touhoulittlemaid.command.subcommand.PowerCommand;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public final class RootCommand {
    private static final String ROOT_NAME = "tlm";

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> root = Commands.literal(ROOT_NAME)
                .requires((source -> source.hasPermission(2)));
        root.then(PackCommand.get());
        root.then(PowerCommand.get());
        root.then(MaidNumCommand.get());
        dispatcher.register(root);
    }
}

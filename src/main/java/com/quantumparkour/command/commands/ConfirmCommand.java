package com.quantumparkour.command.commands;

import com.quantumparkour.command.QuantumCommand;
import com.quantumparkour.util.SafeConfirm;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class ConfirmCommand implements QuantumCommand {
    @Override
    public String getName() {
        return "confirm";
    }

    @Override
    public String getDescription() {
        return "Confirms a requested action";
    }

    @Override
    public String getUsage() {
        return "/confirm <code>";
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            sender.sendRichMessage("<red>Not enough arguments.");
            return;
        }
        if (args.length == 1) {
            if (SafeConfirm.getFromSender(sender).isEmpty()) {
                sender.sendRichMessage("<red>You have no pending confirmations.");
                return;
            }
            SafeConfirm safeConfirm = SafeConfirm.get(sender, args[0]);
            if (safeConfirm == null) {
                sender.sendRichMessage("<red>You have no confirmations with code '" + args[0] + "'.");
            } else {
                safeConfirm.run();
            }
            return;
        }
        sender.sendRichMessage("<red>Too many arguments.");
    }

    @Override
    public List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return SafeConfirm.getFromSender(sender).stream().map(SafeConfirm::getCode).collect(Collectors.toList());
    }
}

package com.quantumparkour.util;

import org.bukkit.command.CommandSender;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SafeConfirm {
    private static final List<SafeConfirm> safeConfirmList = new ArrayList<>();

    public static void create(CommandSender sender, int ticks, Consumer<CommandSender> action) {
        String code;
        do {
            code = generateCode();
        } while (isCodeTaken(code));
        sender.sendRichMessage("<red>Type <gold><hover:show_text:'Click Here'><click:suggest_command:/confirm " + code + ">/confirm " + code + "</click></hover><red> to confirm.");
        safeConfirmList.add(new SafeConfirm(sender, ticks, action, code));
    }

    private static boolean isCodeTaken(String code) {
        return safeConfirmList.stream().anyMatch(safeConfirm -> safeConfirm.code.equals(code));
    }

    public static SafeConfirm get(CommandSender sender, String code) {
        return getFromSender(sender).stream()
                .filter(safeConfirm -> safeConfirm.code.equals(code))
                .findFirst()
                .orElse(null);
    }

    public static List<SafeConfirm> getFromSender(CommandSender sender) {
        return safeConfirmList.stream()
                .filter(safeConfirm -> safeConfirm.sender.equals(sender))
                .collect(Collectors.toList());
    }

    public static void tickAll() {
        ListIterator<SafeConfirm> iterator = safeConfirmList.listIterator();
        while (iterator.hasNext()) {
            SafeConfirm safeConfirm = iterator.next();
            if (safeConfirm.isExpired()) {
                safeConfirm.sender.sendRichMessage("<red>Confirmation <gold>" + safeConfirm.code + "<red> has expired.");
            }
            if (safeConfirm.isExpired() || safeConfirm.executed) {
                iterator.remove();
            }
        }
    }

    private static String generateCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        return random.ints(7, 0, characters.length())
                .mapToObj(i -> String.valueOf(characters.charAt(i)))
                .collect(Collectors.joining());
    }

    private final CommandSender sender;
    private final Consumer<CommandSender> action;
    private final String code;
    private final long expirationTime;
    private boolean executed = false;

    private SafeConfirm(CommandSender sender, int ticks, Consumer<CommandSender> action, String code) {
        this.sender = sender;
        this.action = action;
        this.code = code;
        this.expirationTime = System.currentTimeMillis() + (ticks * 50L);
    }

    private boolean isExpired() {
        return System.currentTimeMillis() >= expirationTime;
    }

    public String getCode() {
        return this.code;
    }

    public void run() {
        this.executed = true;
        this.action.accept(this.sender);
    }
}

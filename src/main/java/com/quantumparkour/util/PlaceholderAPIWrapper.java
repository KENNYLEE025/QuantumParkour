package com.quantumparkour.util;

import org.apache.commons.lang3.function.TriFunction;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class PlaceholderAPIWrapper {
    private static boolean active = false;
    private static BiFunction<OfflinePlayer, String, String> setPlaceholders = (player, text) -> text;
    private static BiFunction<OfflinePlayer, String, String> setBracketPlaceholders = (player, text) -> text;
    private static TriFunction<Player, Player, String, String> setRelationalPlaceholders = (one, two, text) -> text;
    private static Function<String, Boolean> isRegistered = identifier -> false;
    private static Supplier<Set<String>> getRegisteredIdentifiers = Collections::emptySet;
    private static Function<String, Boolean> containsPlaceholders = text -> false;
    private static Function<String, Boolean> containsBracketPlaceholders = text -> false;

    private PlaceholderAPIWrapper() {}

    public static void init() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            setPlaceholders = me.clip.placeholderapi.PlaceholderAPI::setPlaceholders;
            setBracketPlaceholders = me.clip.placeholderapi.PlaceholderAPI::setBracketPlaceholders;
            setRelationalPlaceholders = me.clip.placeholderapi.PlaceholderAPI::setRelationalPlaceholders;
            isRegistered = me.clip.placeholderapi.PlaceholderAPI::isRegistered;
            getRegisteredIdentifiers = me.clip.placeholderapi.PlaceholderAPI::getRegisteredIdentifiers;
            containsPlaceholders = me.clip.placeholderapi.PlaceholderAPI::containsPlaceholders;
            containsBracketPlaceholders = me.clip.placeholderapi.PlaceholderAPI::containsBracketPlaceholders;
            active = true;
        }
    }

    public static boolean isActive() {
        return active;
    }

    public static @NotNull String setPlaceholders(OfflinePlayer player, @NotNull String text) {
        return setPlaceholders.apply(player, text);
    }

    public static @NotNull List<String> setPlaceholders(OfflinePlayer player, @NotNull List<String> text) {
        return text.stream().map(line -> setPlaceholders(player, line)).collect(Collectors.toList());
    }

    public static @NotNull String setPlaceholders(Player player, @NotNull String text) {
        return setPlaceholders((OfflinePlayer) player, text);
    }

    public static @NotNull List<String> setPlaceholders(Player player, @NotNull List<String> text) {
        return text.stream().map(line -> setPlaceholders(player, line)).collect(Collectors.toList());
    }

    public static @NotNull String setBracketPlaceholders(OfflinePlayer player, @NotNull String text) {
        return setBracketPlaceholders.apply(player, text);
    }

    public static @NotNull List<String> setBracketPlaceholders(OfflinePlayer player, @NotNull List<String> text) {
        return text.stream().map(line -> setBracketPlaceholders(player, line)).collect(Collectors.toList());
    }

    public static String setBracketPlaceholders(Player player, String text) {
        return setBracketPlaceholders((OfflinePlayer) player, text);
    }

    public static List<String> setBracketPlaceholders(Player player, List<String> text) {
        return text.stream().map(line -> setBracketPlaceholders(player, line)).collect(Collectors.toList());
    }

    public static String setRelationalPlaceholders(Player one, Player two, String text) {
        return setRelationalPlaceholders.apply(one, two, text);
    }

    public static List<String> setRelationalPlaceholders(Player one, Player two, List<String> text) {
        return text.stream().map(line -> setRelationalPlaceholders(one, two, line)).collect(Collectors.toList());
    }

    public static boolean isRegistered(@NotNull String identifier) {
        return isRegistered.apply(identifier);
    }

    public static @NotNull Set<String> getRegisteredIdentifiers() {
        return getRegisteredIdentifiers.get();
    }

    public static boolean containsPlaceholders(String text) {
        return containsPlaceholders.apply(text);
    }

    public static boolean containsBracketPlaceholders(String text) {
        return containsBracketPlaceholders.apply(text);
    }
}

package com.quantumparkour.util;

import org.bukkit.ChatColor;
import org.bukkit.Color;

public class MessageColorUtils
{
    //------------------------------------------------------------------------------------------------------------
    public static String translate(String message)
    {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    //------------------------------------------------------------------------------------------------------------
    public static Color getColorFromString(String colorName)
    {
        if (colorName == null) return null;

        return switch (colorName.toLowerCase())
        {
            case "black" -> Color.BLACK;
            case "white" -> Color.WHITE;
            case "yellow" -> Color.YELLOW;
            case "navy" -> Color.NAVY;
            case "blue" -> Color.BLUE;
            case "fuchsia" -> Color.FUCHSIA;    // Magenta
            case "aqua" -> Color.AQUA;
            case "olive" -> Color.OLIVE;        // Gold
            case "maroon" -> Color.MAROON;      // Dark Red
            case "green" -> Color.GREEN;
            case "lime" -> Color.LIME;
            case "gray" -> Color.GRAY;
            case "orange" -> Color.ORANGE;
            case "red" -> Color.RED;
            case "silver" -> Color.SILVER;      // Light gray
            case "teal" -> Color.TEAL;
            case "purple" -> Color.PURPLE;
            default -> null;
        };
    }
}

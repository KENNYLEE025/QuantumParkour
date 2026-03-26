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

        switch (colorName.toLowerCase())
        {
            case "black":   return Color.BLACK;
            case "white":   return Color.WHITE;
            case "yellow":  return Color.YELLOW;
            case "navy":    return Color.NAVY;
            case "blue":    return Color.BLUE;
            case "fuchsia": return Color.FUCHSIA;
            case "aqua":    return Color.AQUA;
            case "olive":   return Color.OLIVE;
            case "maroon":  return Color.MAROON;
            case "green":   return Color.GREEN;
            case "lime":    return Color.LIME;
            case "gray":    return Color.GRAY;
            case "orange":  return Color.ORANGE;
            case "red":     return Color.RED;
            case "silver":  return Color.SILVER;
            case "teal":    return Color.TEAL;
            case "purple":  return Color.PURPLE;
            default:        return null;
        }
    }
}

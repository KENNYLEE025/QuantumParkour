package com.quantumparkour.util;

public class DatatypeUtils
{
    //------------------------------------------------------------------------------------------------------------
    public static boolean isInteger(String input)
    {
        try
        {
            Integer.parseInt(input);
        }
        catch (Exception exception)
        {
            return false;
        }
        return true;
    }

    //------------------------------------------------------------------------------------------------------------
    public static boolean isFloat(String input)
    {
        try
        {
            Float.parseFloat(input);
        }
        catch (Exception exception)
        {
            return false;
        }
        return true;
    }

    //------------------------------------------------------------------------------------------------------------
    public static boolean isDouble(String input)
    {
        try
        {
            Double.parseDouble(input);
        }
        catch (Exception exception)
        {
            return false;
        }
        return true;
    }

    //------------------------------------------------------------------------------------------------------------
    public static boolean isLong(String input)
    {
        try
        {
            Long.parseLong(input);
        }
        catch (Exception exception)
        {
            return false;
        }
        return true;
    }

    //------------------------------------------------------------------------------------------------------------
    public static boolean isShort(String input)
    {
        try
        {
            Short.parseShort(input);
        }
        catch (Exception exception)
        {
            return false;
        }
        return true;
    }
}

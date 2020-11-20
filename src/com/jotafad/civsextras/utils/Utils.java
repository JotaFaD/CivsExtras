package com.jotafad.civsextras.utils;

public class Utils
{
    public static int clamp(int x, int min, int max)
    {
        return Math.max(min, Math.min(max, x));
    }

    public static double clamp(double x, double min, double max)
    {
        return Math.max(min, Math.min(max, x));
    }
}

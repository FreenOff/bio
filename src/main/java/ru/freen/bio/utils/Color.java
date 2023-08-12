package ru.freen.bio.utils;

import org.bukkit.ChatColor;

public class Color {

    public static String parser(String text){
        return ChatColor.translateAlternateColorCodes('&',text);
    }
}

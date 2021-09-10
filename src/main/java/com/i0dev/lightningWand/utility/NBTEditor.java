package com.i0dev.lightningWand.utility;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class NBTEditor {

    /*
    Credit given to EmberCM
    Discord: Ember#1404
    GitHub: https://github.com/EmberCM
     */

    public static ItemStack set(ItemStack item, String key, Object value, Class<?> type) {
        try {
            if (item == null) return null;
            Object copy = getOBCClass("inventory.CraftItemStack").getDeclaredMethod("asNMSCopy", ItemStack.class).invoke(null, item);
            boolean has = (boolean) copy.getClass().getDeclaredMethod("hasTag").invoke(copy);
            Object tag = copy.getClass().getDeclaredMethod("getTag").invoke(copy);
            Object cmpd = has ? tag : getNMSClass("NBTTagCompound").getConstructor().newInstance();
            cmpd.getClass().getDeclaredMethod("set" + caps(type.getSimpleName()), String.class, type).invoke(cmpd, key, value);
            copy.getClass().getDeclaredMethod("setTag", getNMSClass("NBTTagCompound")).invoke(copy, cmpd);
            return (ItemStack) getOBCClass("inventory.CraftItemStack").getDeclaredMethod("asBukkitCopy", getNMSClass("ItemStack")).invoke(null, copy);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ItemStack set(ItemStack item, String key, Object value) {
        return set(item, key, value, String.class);
    }

    public static String caps(String string) {
        String[] list = string.split("_");
        StringBuilder s = new StringBuilder();
        Arrays.stream(list).forEach(st -> s.append(st.substring(0, 1).toUpperCase()).append(st.substring(1).toLowerCase()).append("_"));
        return s.toString().substring(0, s.length() - 1);
    }

    public static <T> T get(ItemStack item, String key, Class<T> type) {
        try {
            if (item == null) return type == String.class ? (T) "" : null;
            Object copy = getOBCClass("inventory.CraftItemStack").getDeclaredMethod("asNMSCopy", ItemStack.class).invoke(null, item);
            if (!((boolean) copy.getClass().getDeclaredMethod("hasTag").invoke(copy)))
                return type == String.class ? (T) "" : null;
            Object tag = copy.getClass().getDeclaredMethod("getTag").invoke(copy);
            if (!((boolean) tag.getClass().getDeclaredMethod("hasKey", String.class).invoke(tag, key)))
                return type == String.class ? (T) "" : null;
            return (T) tag.getClass().getMethod("get" + caps(type.getSimpleName()), String.class).invoke(tag, key);
        } catch (Exception e) {
            e.printStackTrace();
            return type == String.class ? (T) "" : null;
        }
    }

    public static Class<?> getOBCClass(String name) {
        try {
            return Class.forName("org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static Class<?> getNMSClass(String name, String def) {
        return getNMSClass(name) != null ? getNMSClass(name) : getNMSClass(def.split("\\.")[0]).getDeclaredClasses()[0];
    }

    public static String get(ItemStack item, String key) {
        return get(item, key, String.class);
    }
}
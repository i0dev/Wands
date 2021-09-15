package com.i0dev.lightningWand;

import com.i0dev.lightningWand.commands.CmdWand;
import com.i0dev.lightningWand.config.GeneralConfig;
import com.i0dev.lightningWand.config.MessageConfig;
import com.i0dev.lightningWand.handlers.WandHandler;
import com.i0dev.lightningWand.managers.MessageManager;
import com.i0dev.lightningWand.managers.WandManager;
import com.i0dev.lightningWand.templates.AbstractCommand;
import com.i0dev.lightningWand.templates.AbstractConfiguration;
import com.i0dev.lightningWand.templates.AbstractListener;
import com.i0dev.lightningWand.templates.AbstractManager;
import com.i0dev.lightningWand.utility.ConfigUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class Heart extends JavaPlugin {

    List<AbstractManager> managers = new ArrayList<>();
    List<AbstractConfiguration> configs = new ArrayList<>();


    boolean usingPapi;
    boolean usingMCoreFactions;

    @Override
    public void onEnable() {

        usingPapi = getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");
        System.out.println("Placeholder api enabled: " + usingPapi);
        Plugin factions = getServer().getPluginManager().getPlugin("Factions");
        usingMCoreFactions = factions != null && factions.getDescription().getVersion().startsWith("2.");

        managers.addAll(Arrays.asList(
                new WandManager(this),
                new WandHandler(this),
                new MessageManager(this),
                new CmdWand(this, "wand")
        ));

        configs.addAll(Arrays.asList(
                new GeneralConfig(this, getDataFolder() + "/General.json"),
                new MessageConfig(this, getDataFolder() + "/Messages.json")
        ));

        reload();
        System.out.println("\u001B[32m" + this.getDescription().getName() + " by: " + this.getDescription().getAuthors().get(0) + " has been enabled.");
    }

    public void reload() {
        // old ~ new
        ArrayList<MessageManager.Pair<AbstractConfiguration, AbstractConfiguration>> toReplace = new ArrayList<>();
        configs.forEach(abstractConfiguration -> toReplace.add(new MessageManager.Pair<>(abstractConfiguration, ConfigUtil.load(abstractConfiguration, this))));
        toReplace.forEach(pairs -> {
            configs.remove(pairs.getKey());
            configs.add(pairs.getValue());
        });

        managers.forEach(abstractManager -> {
            if (abstractManager.isLoaded()) abstractManager.deinitialize();
            if (abstractManager instanceof AbstractListener)
                getServer().getPluginManager().registerEvents((AbstractListener) abstractManager, this);
            else if (abstractManager instanceof AbstractCommand) {
                getCommand(((AbstractCommand) abstractManager).getCommand()).setExecutor(((AbstractCommand) abstractManager));
                getCommand(((AbstractCommand) abstractManager).getCommand()).setTabCompleter(((AbstractCommand) abstractManager));
            }
            abstractManager.initialize();
            abstractManager.setLoaded(true);
        });
    }


    @Override
    public void onDisable() {
        configs.clear();
        managers.forEach(AbstractManager::deinitialize);
        managers.clear();
        Bukkit.getScheduler().cancelTasks(this);
        System.out.println("\u001B[31m" + this.getDescription().getName() + " by: " + this.getDescription().getAuthors().get(0) + " has been disabled.");
    }

    public <T> T getManager(Class<T> clazz) {
        return (T) managers.stream().filter(manager -> manager.getClass().equals(clazz)).findFirst().orElse(null);
    }

    public <T> T getConfig(Class<T> clazz) {
        return (T) configs.stream().filter(config -> config.getClass().equals(clazz)).findFirst().orElse(null);
    }

}

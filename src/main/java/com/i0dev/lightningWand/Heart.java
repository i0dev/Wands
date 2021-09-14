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
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class Heart extends JavaPlugin {

    List<AbstractManager> managers = new ArrayList<>();
    List<AbstractConfiguration> configs = new ArrayList<>();

    @Override
    public void onEnable() {
        System.out.println("\n\n\nwands enabled\n\n");
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
    }

    public void reload() {
        // old --- new
        ArrayList<MessageManager.Pair<AbstractConfiguration, AbstractConfiguration>> toReplace = new ArrayList<>();
        configs.forEach(abstractConfiguration -> toReplace.add(new MessageManager.Pair<>(abstractConfiguration, ConfigUtil.load(abstractConfiguration, this))));
        toReplace.forEach(pairs -> {
            configs.remove(pairs.getKey());
            configs.add(pairs.getValue());
        });

        managers.forEach(abstractManager -> {
            if (abstractManager.isLoaded())
                abstractManager.deinitialize();
            if (abstractManager instanceof AbstractListener) {
                getServer().getPluginManager().registerEvents((AbstractListener) abstractManager, this);
                System.out.println("resgisted event listener: " + abstractManager.getClass().getSimpleName());
            } else if (abstractManager instanceof AbstractCommand) {
                getCommand(((AbstractCommand) abstractManager).getCommand()).setExecutor(((AbstractCommand) abstractManager));
                getCommand(((AbstractCommand) abstractManager).getCommand()).setTabCompleter(((AbstractCommand) abstractManager));
                System.out.println("resgisted cmd: " + abstractManager.getClass().getSimpleName());
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
        System.out.println("\n\n\ndisabled\n\n\n");
    }

    public <T> T getManager(Class<T> clazz) {
        return (T) managers.stream().filter(manager -> manager.getClass().equals(clazz)).findFirst().orElse(null);
    }

    public <T> T getConfig(Class<T> clazz) {
        return (T) configs.stream().filter(config -> config.getClass().equals(clazz)).findFirst().orElse(null);
    }

}

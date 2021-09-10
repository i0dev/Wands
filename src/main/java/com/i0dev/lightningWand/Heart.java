package com.i0dev.lightningWand;

import com.i0dev.lightningWand.config.GeneralConfig;
import com.i0dev.lightningWand.handlers.WandHandler;
import com.i0dev.lightningWand.managers.ConfigManager;
import com.i0dev.lightningWand.managers.WandManager;
import com.i0dev.lightningWand.templates.AbstractCommand;
import com.i0dev.lightningWand.templates.AbstractConfiguration;
import com.i0dev.lightningWand.templates.AbstractListener;
import com.i0dev.lightningWand.templates.AbstractManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Heart extends JavaPlugin {

    List<AbstractManager> managers = new ArrayList<>();
    List<AbstractConfiguration> configs = new ArrayList<>();

    @Override

    public void onEnable() {
        System.out.println("\n\n\nwands enabled\n\n");
        managers.addAll(Arrays.asList(
                new WandManager(this),
                new ConfigManager(this),
                new WandHandler(this)
        ));

        configs.addAll(Arrays.asList(
                new GeneralConfig(this, getDataFolder() + "/General.json")
        ));

        managers.forEach(abstractManager -> {
            if (abstractManager instanceof AbstractListener) {
                getServer().getPluginManager().registerEvents((AbstractListener) abstractManager, this);
                System.out.println("resgisted event listener: " + abstractManager.getClass().getSimpleName());
            } else if (abstractManager instanceof AbstractCommand) {
                getCommand(((AbstractCommand) abstractManager).getCommand()).setExecutor(((AbstractCommand) abstractManager));
                System.out.println("resgisted cmd: " + abstractManager.getClass().getSimpleName());
            }
            abstractManager.initialize();
        });

        ConfigManager cnfg = getManager(ConfigManager.class);
        configs.forEach(cnfg::load);
    }


    @Override
    public void onDisable() {
        configs.clear();
        managers.forEach(AbstractManager::deinitialize);
        managers.clear();
        System.out.println("\n\n\ndisabled\n\n\n");
    }

    public <T> T getManager(Class<T> clazz) {
        return (T) managers.stream().filter(manager -> manager.getClass().equals(clazz)).findFirst().orElse(null);
    }

}

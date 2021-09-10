package com.i0dev.lightningWand.managers;

import com.i0dev.lightningWand.Heart;
import com.i0dev.lightningWand.config.GeneralConfig;
import com.i0dev.lightningWand.objects.Wand;
import com.i0dev.lightningWand.templates.AbstractManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.Tuple;
import org.bukkit.Bukkit;

import java.util.*;

@Getter
public class WandManager extends AbstractManager {
    public WandManager(Heart plugin) {
        super(plugin);
    }

    List<Wand> wands;
    List<CooldownObj> cooldown;

    @Override
    public void initialize() {
        wands = new ArrayList<>();
        cooldown = new ArrayList<>();
        Bukkit.getScheduler().runTaskTimerAsynchronously(getHeart(), clearCoolDown, 20L, 20L);
        wands.addAll(getHeart().getConfig(GeneralConfig.class).getWands());
    }

    @Override
    public void deinitialize() {
        wands.clear();
        cooldown.clear();
    }

    Runnable clearCoolDown = () -> {
        List<CooldownObj> toRemove = new ArrayList<>();
        for (CooldownObj cooldownObj : cooldown) {
            if (cooldownObj.getTimeEnd() < System.currentTimeMillis()) {
                toRemove.add(cooldownObj);
            }
        }
        toRemove.forEach(cooldown::remove);
    };

    @Getter
    @AllArgsConstructor
    public static class CooldownObj {
        Wand wand;
        Heart heart;
        long timeEnd;
        UUID uuid;
    }

}


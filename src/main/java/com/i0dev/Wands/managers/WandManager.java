package com.i0dev.Wands.managers;

import com.i0dev.Wands.Heart;
import com.i0dev.Wands.config.GeneralConfig;
import com.i0dev.Wands.objects.Wand;
import com.i0dev.Wands.templates.AbstractManager;
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

    public Wand getWand(String id) {
        return wands.stream().filter(wand -> wand.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
    }

    @Getter
    @AllArgsConstructor
    public static class CooldownObj {
        Wand wand;
        Heart heart;
        long timeEnd;
        UUID uuid;
    }

}


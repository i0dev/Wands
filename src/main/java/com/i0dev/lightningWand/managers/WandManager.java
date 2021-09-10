package com.i0dev.lightningWand.managers;

import com.i0dev.lightningWand.Heart;
import com.i0dev.lightningWand.objects.Wand;
import com.i0dev.lightningWand.templates.AbstractManager;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
public class WandManager extends AbstractManager {
    public WandManager(Heart plugin) {
        super(plugin);
    }

    List<Wand> wands = new ArrayList<>();

    @Override
    public void initialize() {
    }
}

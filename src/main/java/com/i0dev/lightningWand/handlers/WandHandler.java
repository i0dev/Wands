package com.i0dev.lightningWand.handlers;

import com.i0dev.lightningWand.Heart;
import com.i0dev.lightningWand.managers.WandManager;
import com.i0dev.lightningWand.objects.Wand;
import com.i0dev.lightningWand.templates.AbstractListener;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class WandHandler extends AbstractListener {

    WandManager wandManager;

    public WandHandler(Heart heart) {
        super(heart);
    }

    @Override
    public void initialize() {
        wandManager = getHeart().getManager(WandManager.class);
    }

    @EventHandler
    public void onLightingWand(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (e.getItem() == null || Material.AIR.equals(e.getItem().getType())) return;
        for (Wand wand : wandManager.getWands()) {
            if (!Material.getMaterial(wand.getMaterial()).equals(e.getItem().getType())) continue;
            // if (!NBTEditor.get(e.getItem(), "id").equals(wand.getId())) continue;
            Location loc = e.getClickedBlock().getLocation();

            loc.getWorld().strikeLightning(loc);
            e.getPlayer().sendMessage("kaka baka");

            break;
        }
    }
}

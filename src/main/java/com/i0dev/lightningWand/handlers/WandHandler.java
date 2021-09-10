package com.i0dev.lightningWand.handlers;

import com.i0dev.lightningWand.Heart;
import com.i0dev.lightningWand.managers.WandManager;
import com.i0dev.lightningWand.objects.Wand;
import com.i0dev.lightningWand.templates.AbstractListener;
import com.i0dev.lightningWand.utility.NBTEditor;
import net.minecraft.server.v1_8_R3.ItemStack;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftItem;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
            if (!NBTEditor.get(e.getItem(), "id").equals(wand.getId())) continue;
            List<WandManager.CooldownObj> wandsFiltered = wandManager.getCooldown().stream().filter(cooldownObj -> cooldownObj.getWand().getId().equals(wand.getId())).collect(Collectors.toList());
            List<UUID> uuids = new ArrayList<>();
            wandsFiltered.forEach(cooldownObj -> uuids.add(cooldownObj.getUuid()));
            if (uuids.contains(e.getPlayer().getUniqueId())) {
                WandManager.CooldownObj obj = wandsFiltered.stream().filter(cooldownObj -> cooldownObj.getUuid().equals(e.getPlayer().getUniqueId())).findAny().orElse(null);
                e.getPlayer().sendMessage("u on cooldown! for: " + (obj.getTimeEnd() - System.currentTimeMillis()) / 1000 + "sec");
                continue;
            }
            Location loc = e.getClickedBlock().getLocation();
            loc.getWorld().strikeLightning(loc);
            e.getPlayer().sendMessage("u struck lighting. cool");
            wandManager.getCooldown().add(new WandManager.CooldownObj(wand, getHeart(), System.currentTimeMillis() + (wand.getCooldownSeconds() * 1000), e.getPlayer().getUniqueId()));
            break;
        }
    }
}

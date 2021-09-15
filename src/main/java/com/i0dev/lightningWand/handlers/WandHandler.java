package com.i0dev.lightningWand.handlers;

import com.i0dev.lightningWand.Heart;
import com.i0dev.lightningWand.config.GeneralConfig;
import com.i0dev.lightningWand.config.MessageConfig;
import com.i0dev.lightningWand.hooks.MCoreFactionsHook;
import com.i0dev.lightningWand.managers.MessageManager;
import com.i0dev.lightningWand.managers.WandManager;
import com.i0dev.lightningWand.objects.Wand;
import com.i0dev.lightningWand.templates.AbstractListener;
import com.i0dev.lightningWand.utility.NBTEditor;
import com.i0dev.lightningWand.utility.Utility;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class WandHandler extends AbstractListener {

    WandManager wandManager;
    GeneralConfig cnf;
    MessageManager msgManager;
    MessageConfig msg;

    public WandHandler(Heart heart) {
        super(heart);
    }

    @Override
    public void initialize() {
        wandManager = getHeart().getManager(WandManager.class);
        msgManager = getHeart().getManager(MessageManager.class);
        cnf = getHeart().getConfig(GeneralConfig.class);
        msg = getHeart().getConfig(MessageConfig.class);
    }

    @Override
    public void deinitialize() {
        wandManager = null;
        msgManager = null;
        cnf = null;
        msg = null;
    }

    @EventHandler
    public void onLightingWand(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (e.getItem() == null || Material.AIR.equals(e.getItem().getType())) return;
        perform(e.getPlayer(), e.getClickedBlock().getLocation(), e.getItem());
    }

    @EventHandler
    public void onLightningWandEntity(PlayerInteractEntityEvent e) {
        if (e.getPlayer().getItemInHand() == null || Material.AIR.equals(e.getPlayer().getItemInHand().getType()))
            return;
        perform(e.getPlayer(), e.getRightClicked().getLocation(), e.getPlayer().getItemInHand());
    }

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent e) {
        if (cnf.isAllowHittingPlayersWithWand()) return;
        if (!(e.getDamager() instanceof Player)) return;
        if (!(e.getEntity() instanceof Player)) return;
        Player player = ((Player) e.getDamager());
        ItemStack item = player.getItemInHand();
        if (item == null || Material.AIR.equals(item.getType())) return;
        for (Wand wand : wandManager.getWands()) {
            if (!Material.getMaterial(wand.getMaterial()).equals(item.getType())) continue;
            if (!NBTEditor.get(item, "id").equals(wand.getId())) continue;
            e.setCancelled(true);
            msgManager.msg(player, msg.getCantHitPlayers());
            break;
        }
    }


    public void perform(Player player, Location location, ItemStack item) {
        for (Wand wand : wandManager.getWands()) {
            if (!Material.getMaterial(wand.getMaterial()).equals(item.getType())) continue;
            if (!NBTEditor.get(item, "id").equals(wand.getId())) continue;

            if (getHeart().isUsingMCoreFactions() && cnf.isDenySystemFactionUse()) {
                if (MCoreFactionsHook.isSystemFaction(location)) {
                    msgManager.msg(player, msg.getCantUseInSystemFaction());
                    return;
                }
            }

            List<WandManager.CooldownObj> wandsFiltered = wandManager.getCooldown().stream().filter(cooldownObj -> cooldownObj.getWand().getId().equals(wand.getId())).collect(Collectors.toList());
            List<UUID> uuids = new ArrayList<>();
            if ("0".equalsIgnoreCase(NBTEditor.get(item, "uses"))) {
                msgManager.msg(player, msg.getNoMoreUses());
                return;
            }
            wandsFiltered.forEach(cooldownObj -> uuids.add(cooldownObj.getUuid()));
            if (uuids.contains(player.getUniqueId())) {
                WandManager.CooldownObj obj = wandsFiltered.stream().filter(cooldownObj -> cooldownObj.getUuid().equals(player.getUniqueId())).findAny().orElse(null);
                msgManager.msg(player, msg.getOnCoolDown(), new MessageManager.Pair<>("{sec}", ((obj.getTimeEnd() - System.currentTimeMillis()) / 1000) + ""));
                continue;
            }
            location.getWorld().strikeLightning(location);
            msgManager.msg(player, msg.getYouStruck());
            wandManager.getCooldown().add(new WandManager.CooldownObj(wand, getHeart(), System.currentTimeMillis() + (wand.getCooldownSeconds() * 1000), player.getUniqueId()));
            String uses = NBTEditor.get(item, "uses");
            if (!"-1".equalsIgnoreCase(uses)) {
                long usesL = Long.parseLong(uses);
                item = NBTEditor.set(item, "uses", (usesL - 1) + "");

                List<String> newLore = new ArrayList<>();
                wand.getLore().forEach(s -> {
                    newLore.add(msgManager.pair(s,
                            new MessageManager.Pair<>("{kb}", wand.getKnockback() + ""),
                            new MessageManager.Pair<>("{cooldown}", wand.getCooldownSeconds() + ""),
                            new MessageManager.Pair<>("{uses}", (usesL - 1) + "")
                    ));
                });
                ItemMeta meta = item.getItemMeta();
                meta.setLore(Utility.color(newLore));
                item.setItemMeta(meta);
                player.setItemInHand(item);
            }
            break;
        }
    }

}

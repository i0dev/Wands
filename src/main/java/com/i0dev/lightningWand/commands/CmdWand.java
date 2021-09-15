package com.i0dev.lightningWand.commands;

import com.i0dev.lightningWand.Heart;
import com.i0dev.lightningWand.config.MessageConfig;
import com.i0dev.lightningWand.managers.MessageManager;
import com.i0dev.lightningWand.managers.WandManager;
import com.i0dev.lightningWand.objects.Wand;
import com.i0dev.lightningWand.templates.AbstractCommand;
import com.i0dev.lightningWand.utility.NBTEditor;
import com.i0dev.lightningWand.utility.Utility;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class CmdWand extends AbstractCommand {

    WandManager wandManager;
    MessageConfig msg;
    MessageManager msgManager;

    public CmdWand(Heart heart, String command) {
        super(heart, command);
    }

    @Override
    public void initialize() {
        wandManager = getHeart().getManager(WandManager.class);
        msgManager = getHeart().getManager(MessageManager.class);
        msg = getHeart().getConfig(MessageConfig.class);
    }

    @Override
    public void deinitialize() {
        wandManager = null;
        msgManager = null;
        msg = null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            msgManager.msg(sender, msg.getWandGiveUsage());
            return;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("wands.reload")) {
                msgManager.msg(sender, msg.getNoPermission());
                return;
            }
            getHeart().reload();
            msgManager.msg(sender, msg.getReloadedConfig());
            return;
        }

        if (args[0].equalsIgnoreCase("give")) {
            if (!sender.hasPermission("wands.give")) {
                msgManager.msg(sender, msg.getNoPermission());
                return;
            }

            if (args.length < 3) {
                msgManager.msg(sender, msg.getWandGiveUsage());
                return;
            }

            Player player = msgManager.getPlayer(args[1]);
            if (player == null) {
                msgManager.msg(sender, msg.getCantFindPlayer(), new MessageManager.Pair<>("{player}", args[1]));
                return;
            }
            String type = args[2];
            Wand foundWand = wandManager.getWands().stream().filter(wand -> wand.getId().equals(type)).findAny().orElse(null);
            if (foundWand == null) {
                StringBuilder wandsList = new StringBuilder();
                wandManager.getWands().forEach(wand -> wandsList.append(wand.getId()).append(", "));
                msgManager.msg(sender, msg.getWandNotFound(),
                        new MessageManager.Pair<>("{wand}", type),
                        new MessageManager.Pair<>("{list}", wandsList.substring(0, wandsList.length() - 2)));
                return;
            }
            int amt = 1;
            if (args.length == 4) {
                Integer amt1 = Utility.getInt(args[3]);
                if (amt1 == null) {
                    msgManager.msg(sender, msg.getInvalidNumber(),
                            new MessageManager.Pair<>("{num}", args[3]));
                    return;
                }
                amt = amt1;
            }

            List<String> newLore = new ArrayList<>();
            foundWand.getLore().forEach(s -> {
                newLore.add(msgManager.pair(s,
                        new MessageManager.Pair<>("{kb}", foundWand.getKnockback() + ""),
                        new MessageManager.Pair<>("{cooldown}", foundWand.getCooldownSeconds() + ""),
                        new MessageManager.Pair<>("{uses}", foundWand.getUses() + "")
                ));
            });


            ItemStack wandToGive = Utility.makeItem(Material.getMaterial(foundWand.getMaterial()), amt, foundWand.getData(), foundWand.getDisplayName(), newLore, foundWand.isGlow());
            ItemMeta meta = wandToGive.getItemMeta();
            meta.addEnchant(Enchantment.KNOCKBACK, foundWand.getKnockback(), true);
            wandToGive.setItemMeta(meta);


            wandToGive = NBTEditor.set(wandToGive, "id", foundWand.getId());
            wandToGive = NBTEditor.set(wandToGive, "uses", foundWand.getUses() + "");

            player.getInventory().addItem(wandToGive);
            msgManager.msg(player, msg.getReceivedWand(),
                    new MessageManager.Pair<>("{wand}", foundWand.getDisplayName()),
                    new MessageManager.Pair<>("{player}", sender.getName())
            );

            msgManager.msg(sender, msg.getGaveWand(),
                    new MessageManager.Pair<>("{wand}", foundWand.getDisplayName()),
                    new MessageManager.Pair<>("{player}", player.getName())
            );
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) return Arrays.asList("give", "reload");
        if (args[0].equalsIgnoreCase("give")) {
            if (args.length == 2) return null;
            if (args.length == 3) {
                List<String> ret = new ArrayList<>();
                wandManager.getWands().forEach(wand -> ret.add(wand.getId()));
                return ret;
            }
            if (args.length == 4) return Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
        }
        return new ArrayList<>();
    }
}

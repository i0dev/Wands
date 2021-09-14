package com.i0dev.lightningWand.commands;

import com.i0dev.lightningWand.Heart;
import com.i0dev.lightningWand.config.MessageConfig;
import com.i0dev.lightningWand.managers.MessageManager;
import com.i0dev.lightningWand.managers.WandManager;
import com.i0dev.lightningWand.objects.Wand;
import com.i0dev.lightningWand.templates.AbstractCommand;
import com.i0dev.lightningWand.utility.NBTEditor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
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

        // perm check

        if (args.length == 0) {
            msgManager.msg(sender, msg.getWandGiveUsage());
            return;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            getHeart().reload();
            return;
        }

        if (args[0].equalsIgnoreCase("give")) {
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

            ItemStack wandToGive = new ItemStack(Material.getMaterial(foundWand.getMaterial()));
            ItemMeta meta = wandToGive.getItemMeta();
            meta.setLore(foundWand.getLore());
            meta.setDisplayName(foundWand.getDisplayName());
            wandToGive.setItemMeta(meta);
            wandToGive = NBTEditor.set(wandToGive, "id", foundWand.getId());

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
        System.out.println(Arrays.toString(args));
        if (args.length == 1) return Arrays.asList("give", "reload");
        if (args[0].equalsIgnoreCase("give")) {
            List<String> ret = new ArrayList<>();
            if (args.length == 2) return null;
            if (args.length == 3) {
                wandManager.getWands().forEach(wand -> ret.add(wand.getId()));
                return ret;
            }
            if (args.length == 4) return Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
        }

        return new ArrayList<>();

    }
}

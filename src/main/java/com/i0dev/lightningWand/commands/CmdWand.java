package com.i0dev.lightningWand.commands;

import com.i0dev.lightningWand.Heart;
import com.i0dev.lightningWand.managers.ConfigManager;
import com.i0dev.lightningWand.managers.WandManager;
import com.i0dev.lightningWand.objects.Wand;
import com.i0dev.lightningWand.templates.AbstractCommand;
import com.i0dev.lightningWand.utility.NBTEditor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CmdWand extends AbstractCommand {

    ConfigManager configManager;
    WandManager wandManager;

    public CmdWand(Heart heart, String command) {
        super(heart, command);
    }

    @Override
    public void initialize() {
        configManager = getHeart().getManager(ConfigManager.class);
        wandManager = getHeart().getManager(WandManager.class);
    }

    @Override
    public void deinitialize() {
        configManager = null;
        wandManager = null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        // perm check

        if (args.length == 0) {
            sender.sendMessage("/wand give <type> [user]");
            return;
        }

        if (args[0].equalsIgnoreCase("give")) {
            if (args.length == 1) {
                sender.sendMessage("/wand give <type> [user]");
                return;
            }
            String type = args[1];

            Wand foundWand = wandManager.getWands().stream().filter(wand -> wand.getId().equals(type)).findAny().orElse(null);
            if (foundWand == null) {
                StringBuilder wandsList = new StringBuilder();
                wandManager.getWands().forEach(wand -> wandsList.append(wand.getId() + ", "));

                sender.sendMessage("wand not found, wands: " + wandsList.substring(0, wandsList.length() - 2));
                return;
            }

            ItemStack wandToGive = new ItemStack(Material.getMaterial(foundWand.getMaterial()));
            ItemMeta meta = wandToGive.getItemMeta();
            meta.setLore(foundWand.getLore());
            meta.setDisplayName(foundWand.getDisplayName());
            wandToGive.setItemMeta(meta);
            wandToGive = NBTEditor.set(wandToGive, "id", foundWand.getId());
            ((Player) sender).getInventory().addItem(wandToGive);
            sender.sendMessage("got wand: " + foundWand.getId());
        }

    }
}

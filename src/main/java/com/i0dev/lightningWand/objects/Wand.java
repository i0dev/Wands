package com.i0dev.lightningWand.objects;

import com.i0dev.lightningWand.Heart;
import lombok.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Wand {

    //Cosmetics
    String displayName;
    List<String> lore;

    //Item
    String material;
    short data;
    boolean glow;

    //Abilities
    double knockback;
    long cooldownSeconds;


    //Backend
    String id;
    transient Heart heart;

}

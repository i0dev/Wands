package com.i0dev.lightningWand.config;

import com.i0dev.lightningWand.Heart;
import com.i0dev.lightningWand.objects.Wand;
import com.i0dev.lightningWand.templates.AbstractConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class GeneralConfig extends AbstractConfiguration {

    public GeneralConfig(Heart heart, String path) {
        this.path = path;
        this.heart = heart;
    }

    boolean allowHittingPlayersWithWand = false;
    boolean denySystemFactionUse = true;

    List<Wand> wands = Arrays.asList(
            new Wand("&e&lTier 1 Lightning Wand",
                    Arrays.asList("&7Use this wand to charge creepers, or just", "&7strike lightning on the ground!", "", "&7Cooldown: &c{cooldown}&7 seconds", "&7Knockback Power: &c{kb}", "&7Remaining Uses: &c{uses}"),
                    "BLAZE_ROD",
                    (short) 0,
                    true,
                    1,
                    30,
                    50,
                    "tier1",
                    getHeart()),

            new Wand("&e&lTier 2 Lightning Wand",
                    Arrays.asList("&7Use this wand to charge creepers, or just", "&7strike lightning on the ground!", "", "&7Cooldown: &c{cooldown}&7 seconds", "&7Knockback Power: &c{kb}", "&7Remaining Uses: &c{uses}"),
                    "BLAZE_ROD",
                    (short) 0,
                    true,
                    3,
                    15,
                    100,
                    "tier2",
                    getHeart())


    );
}

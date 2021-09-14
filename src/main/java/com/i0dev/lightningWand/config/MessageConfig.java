package com.i0dev.lightningWand.config;

import com.i0dev.lightningWand.Heart;
import com.i0dev.lightningWand.templates.AbstractConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class MessageConfig extends AbstractConfiguration {

    String wandGiveUsage = "&cUsage: &7/wand give <player> <type> [amount]";
    String wandNotFound = "&cThe wand &f{wand}&c was not found! &7Wands: &f{list}";
    String cantFindPlayer = "&cThe player: &f{player}&c cannot be found!";
    String receivedWand = "&7You have received a {wand} from &c{player}";
    String gaveWand = "&7You have given &c{player}&7: {wand}";

    String reloadedConfig = "&7You have&a reloaded&7 the configuration.";

    public MessageConfig(Heart heart, String path) {
        this.path = path;
        this.heart = heart;
    }
}

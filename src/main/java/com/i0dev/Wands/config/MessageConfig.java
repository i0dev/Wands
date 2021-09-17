package com.i0dev.Wands.config;

import com.i0dev.Wands.Heart;
import com.i0dev.Wands.templates.AbstractConfiguration;
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
    String wandReloadUsage = "&cUsage: &7/wand reload";
    String wandNotFound = "&cThe wand &f{wand}&c was not found! &7Wands: &f{list}";
    String cantFindPlayer = "&cThe player: &f{player}&c cannot be found!";
    String receivedWand = "&7You have received a {wand} from &c{player}";
    String gaveWand = "&7You have given &c{player}&7: {wand}";
    String onCoolDown = "&cYou are on a cool-down for using this wand for another &f{sec}&c seconds.";
    String youStruck = "&7You have&a successfully&7 struck lightning!";
    String noMoreUses = "&cYour wand is broken! It has no more remaining uses.";
    String cantUseInSystemFaction = "&cYou cannot use wands in system factions!";

    String cantHitPlayers = "&cYou cannot hit players with a wand!";

    String invalidNumber = "&cThe number &f{num} &cis invalid! Try again.";
    String reloadedConfig = "&7You have&a reloaded&7 the configuration.";
    String noPermission = "&cYou don not have permission to run that command.";

    public MessageConfig(Heart heart, String path) {
        this.path = path;
        this.heart = heart;
    }
}

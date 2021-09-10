package com.i0dev.lightningWand.templates;

import com.i0dev.lightningWand.Heart;
import lombok.Getter;
import org.bukkit.event.Listener;

@Getter
public abstract class AbstractListener extends AbstractManager implements Listener {
    public AbstractListener(Heart heart) {
        super(heart);
    }
}
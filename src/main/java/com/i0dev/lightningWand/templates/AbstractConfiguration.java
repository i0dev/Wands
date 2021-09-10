package com.i0dev.lightningWand.templates;

import com.i0dev.lightningWand.Heart;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AbstractConfiguration {

    public transient Heart heart = null;
    public transient String path = "";

}

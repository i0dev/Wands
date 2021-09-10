package com.i0dev.lightningWand.templates;


import com.i0dev.lightningWand.Heart;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AbstractManager {

    /*
    Credit given to EmberCM
    Discord: Ember#1404
    GitHub: https://github.com/EmberCM
     */

    public Heart heart;

    public void initialize() {

    }

    public void deinitialize() {

    }
}

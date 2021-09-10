package com.i0dev.lightningWand.managers;

import com.google.gson.*;
import com.i0dev.lightningWand.Heart;
import com.i0dev.lightningWand.config.GeneralConfig;
import com.i0dev.lightningWand.templates.AbstractConfiguration;
import com.i0dev.lightningWand.templates.AbstractManager;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConfigManager extends AbstractManager {

    public ConfigManager(Heart heart) {
        super(heart);
    }


    public String ObjectToJson(AbstractConfiguration object) {
        return new GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().create().toJson(new JsonParser().parse(new Gson().fromJson(new Gson().toJson(object), JsonObject.class).toString()));
    }

    public JsonObject ObjectToJsonObj(Object object) {
        return new Gson().fromJson(new Gson().toJson(object), JsonObject.class);
    }

    public JsonArray ObjectToJsonArr(Object object) {
        return new Gson().fromJson(new Gson().toJson(object), JsonArray.class);
    }

    public Object JsonToObject(JsonElement json, Class<?> clazz) {
        return new Gson().fromJson(new Gson().toJson(json), clazz);
    }

    @SneakyThrows
    public void save(AbstractConfiguration object, String path) {
        System.out.println(ObjectToJson(object));
        Files.write(Paths.get(path), ObjectToJson(object).getBytes());
    }

    public JsonObject getJsonObject(String path) {
        try {
            Reader reader = Files.newBufferedReader(Paths.get(path));
            return new Gson().fromJson(reader, JsonObject.class);
        } catch (Exception e) {
            return null;
        }
    }

    public JsonElement getObjectFromInternalPath(String path, JsonObject json) {
        String[] paths = path.split("\\.");
        if (paths.length == 1)
            return json.get(paths[0]);
        JsonObject finalProduct = new JsonObject();
        for (int i = 0; i < paths.length - 1; i++) {
            if (i == 0) finalProduct = json.get(paths[i]).getAsJsonObject();
            else finalProduct = finalProduct.get(paths[i]).getAsJsonObject();
        }
        return finalProduct.get(paths[paths.length - 1]);
    }

    @SneakyThrows
    public void load(AbstractConfiguration object) {
        String path = object.getPath();
        System.out.println("OBJECT TO SAVE:: " + object);
        JsonObject savedObject = getJsonObject(path);
        if (!getHeart().getDataFolder().exists()) getHeart().getDataFolder().mkdir();
        File file = new File(path);
        if (!file.exists()) {
            file.createNewFile();
        }
        String configString = IOUtils.toString(Files.newBufferedReader(Paths.get(path)));
        AbstractConfiguration config = new Gson().fromJson(savedObject, object.getClass());
        if ("".equals(configString)) {
            save(object, path);
            load(object);
            return;
        }
        if (config == null) throw new IOException("The config file: [" + path + "] is not in valid json format.");
        save(config, path);
        config.setHeart(getHeart());
        config.setPath(path);
        System.out.println("Loaded config: " + object.getClass().getSimpleName() + " from storage.");
    }
}

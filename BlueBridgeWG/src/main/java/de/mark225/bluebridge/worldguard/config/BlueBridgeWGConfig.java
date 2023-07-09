package de.mark225.bluebridge.worldguard.config;

import de.bluecolored.bluemap.api.math.Color;
import de.mark225.bluebridge.core.addon.AddonConfig;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.regex.Pattern;

public class BlueBridgeWGConfig extends AddonConfig {

    private static BlueBridgeWGConfig instance;

    public static BlueBridgeWGConfig getInstance() {
        return instance;
    }

    public BlueBridgeWGConfig(FileConfiguration config) {
        super();
        instance = this;
        init(config);
    }

    public synchronized String htmlPreset() {
        return config.getString("htmlPreset", "$(name)");
    }

    public synchronized boolean defaultExtrude() {
        return config.getBoolean("defaultExtrude", false);
    }

    private static Pattern rgbaRegex = Pattern.compile("[0-9a-f]{8}");

    public synchronized Color unownedDefaultColor() {
        String rgba = config.getString("unownedDefaultColor", "");
        if (rgba == null || !rgbaRegex.matcher(rgba).matches())
            return defaultColor();
        return new Color("#" + rgba);
    }

    public synchronized Color unownedDefaultOutlineColor() {
        String rgb = config.getString("unownedDefaultOutlineColor", "");
        if (rgb == null || !rgbaRegex.matcher(rgb).matches())
            return defaultOutlineColor();
        return new Color("#" + rgb);
    }
}

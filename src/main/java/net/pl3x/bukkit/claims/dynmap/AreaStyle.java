package net.pl3x.bukkit.claims.dynmap;

import org.bukkit.configuration.file.FileConfiguration;

class AreaStyle {
    String strokeColor;
    double strokeOpacity;
    int strokeWeight;
    String fillColor;
    double fillOpacity;
    String label;

    AreaStyle(FileConfiguration config, String path, AreaStyle def) {
        strokeColor = config.getString(path + ".strokeColor", def.strokeColor);
        strokeOpacity = config.getDouble(path + ".strokeOpacity", def.strokeOpacity);
        strokeWeight = config.getInt(path + ".strokeWeight", def.strokeWeight);
        fillColor = config.getString(path + ".fillColor", def.fillColor);
        fillOpacity = config.getDouble(path + ".fillOpacity", def.fillOpacity);
        label = config.getString(path + ".label", null);
    }

    AreaStyle(FileConfiguration config, String path) {
        strokeColor = config.getString(path + ".strokeColor", "#FF0000");
        strokeOpacity = config.getDouble(path + ".strokeOpacity", 0.8);
        strokeWeight = config.getInt(path + ".strokeWeight", 3);
        fillColor = config.getString(path + ".fillColor", "#FF0000");
        fillOpacity = config.getDouble(path + ".fillOpacity", 0.35);
    }
}

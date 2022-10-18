package com.guflimc.brick.nametags.spigot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.guflimc.brick.nametags.common.BrickNametagConfig;
import com.guflimc.brick.nametags.spigot.api.SpigotNametagAPI;
import com.guflimc.brick.nametags.spigot.listeners.JoinQuitListener;
import com.guflimc.brick.scheduler.spigot.api.SpigotScheduler;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

public class SpigotBrickNametags extends JavaPlugin {

    private final static Gson gson = new GsonBuilder().create();

    @Override
    public void onEnable() {
        saveResource("config.json", false);

        BrickNametagConfig config;
        try (
                InputStream is = new FileInputStream(new File(getDataFolder(), "config.json"));
                InputStreamReader isr = new InputStreamReader(is);
        ) {
            config = gson.fromJson(isr, BrickNametagConfig.class);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        SpigotScheduler scheduler = new SpigotScheduler(this, getName());

        BrickSpigotNametagManager nametagManager = new BrickSpigotNametagManager(scheduler);
        SpigotNametagAPI.setNametagManager(nametagManager);

        // default prefix and suffix
        getServer().getPluginManager().registerEvents(new JoinQuitListener(config, nametagManager), this);

        getLogger().info("Enabled " + nameAndVersion() + ".");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabled " + nameAndVersion() + ".");
    }

    private String nameAndVersion() {
        return getName() + " v" + getDescription().getVersion();
    }

}

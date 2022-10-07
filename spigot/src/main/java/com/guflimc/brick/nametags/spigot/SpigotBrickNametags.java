package com.guflimc.brick.nametags.spigot;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.guflimc.brick.nametags.spigot.api.SpigotNametagAPI;
import com.guflimc.brick.scheduler.spigot.api.SpigotScheduler;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

public class SpigotBrickNametags extends JavaPlugin {

    @Override
    public void onEnable() {
        saveResource("config.json", false);
        JsonObject config;
        try (
                InputStream is = new FileInputStream(new File(getDataFolder(), "config.json"));
                InputStreamReader isr = new InputStreamReader(is);
        ) {
            config = JsonParser.parseReader(isr).getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        SpigotScheduler scheduler = new SpigotScheduler(this, getName());

        BrickSpigotNametagManager nametagManager = new BrickSpigotNametagManager(scheduler);
        SpigotNametagAPI.setNametagManager(nametagManager);

        // default prefix and suffix
        JsonElement prefix = config.get("prefix");
        JsonElement suffix = config.get("suffix");
        if ( (prefix != null && !prefix.getAsString().equals(""))
                || (suffix != null && !suffix.getAsString().equals("")) ) {

            getServer().getPluginManager().registerEvents(new Listener() {
                @EventHandler
                public void onJoin(PlayerJoinEvent event) {
                    if ( prefix != null ) {
                        nametagManager.setPrefix(event.getPlayer(), MiniMessage.miniMessage().deserialize(prefix.getAsString()));
                    }
                    if ( suffix != null ) {
                        nametagManager.setSuffix(event.getPlayer(), MiniMessage.miniMessage().deserialize(suffix.getAsString()));
                    }
                }

                @EventHandler
                public void onQuit(PlayerQuitEvent event) {
                    nametagManager.clear(event.getPlayer());
                }
            }, this);
        }

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

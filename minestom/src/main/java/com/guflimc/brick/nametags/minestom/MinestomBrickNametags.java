package com.guflimc.brick.nametags.minestom;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.kyori.adventure.text.minimessage.MiniMessage;
import com.guflimc.brick.nametags.minestom.api.MinestomNametagAPI;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.extensions.Extension;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MinestomBrickNametags extends Extension {

    @Override
    public void initialize() {
        getLogger().info("Enabling " + nameAndVersion() + ".");

        // LOAD CONFIG
        JsonObject config;
        try (
                InputStream is = getResource("config.json");
                InputStreamReader isr = new InputStreamReader(is);
        ) {
            config = JsonParser.parseReader(isr).getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        BrickMinestomNametagManager nametagManager = new BrickMinestomNametagManager();
        MinestomNametagAPI.setNametagManager(nametagManager);

        // default prefix and suffix
        JsonElement prefix = config.get("prefix");
        JsonElement suffix = config.get("suffix");
        if ( (prefix != null && !prefix.getAsString().equals(""))
                || (suffix != null && !suffix.getAsString().equals("")) ) {

            MinecraftServer.getGlobalEventHandler().addListener(PlayerLoginEvent.class, (event) -> {
                if ( prefix != null ) {
                    nametagManager.setPrefix(event.getPlayer(), MiniMessage.miniMessage().deserialize(prefix.getAsString()));
                }
                if ( suffix != null ) {
                    nametagManager.setSuffix(event.getPlayer(), MiniMessage.miniMessage().deserialize(suffix.getAsString()));
                }
            });

            MinecraftServer.getGlobalEventHandler().addListener(PlayerDisconnectEvent.class, (event) -> {
                nametagManager.clear(event.getPlayer());
            });
        }

        getLogger().info("Enabled " + nameAndVersion() + ".");
    }

    @Override
    public void terminate() {
        getLogger().info("Disabled " + nameAndVersion() + ".");
    }

    private String nameAndVersion() {
        return getOrigin().getName() + " v" + getOrigin().getVersion();
    }

}

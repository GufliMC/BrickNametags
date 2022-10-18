package com.guflimc.brick.nametags.minestom;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.guflimc.brick.nametags.common.BrickNametagConfig;
import com.guflimc.brick.nametags.minestom.api.MinestomNametagAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.extensions.Extension;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MinestomBrickNametags extends Extension {

    private final static Gson gson = new GsonBuilder().create();

    private BrickMinestomNametagManager nametagManager;

    private Component prefix;
    private Component suffix;

    @Override
    public void initialize() {
        getLogger().info("Enabling " + nameAndVersion() + ".");

        // LOAD CONFIG
        BrickNametagConfig config;
        try (
                InputStream is = getResource("config.json");
                InputStreamReader isr = new InputStreamReader(is);
        ) {
            config = gson.fromJson(isr, BrickNametagConfig.class);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        BrickMinestomNametagManager nametagManager = new BrickMinestomNametagManager();
        MinestomNametagAPI.setNametagManager(nametagManager);

        // default prefix and suffix
        prefix = config.prefix == null ? null : MiniMessage.miniMessage().deserialize(config.prefix);
        suffix = config.suffix == null ? null : MiniMessage.miniMessage().deserialize(config.suffix);

        MinecraftServer.getGlobalEventHandler().addListener(PlayerLoginEvent.class, (event) -> join(event.getPlayer()));
        MinecraftServer.getGlobalEventHandler().addListener(PlayerDisconnectEvent.class, (event) -> quit(event.getPlayer()));

        //
        MinecraftServer.getConnectionManager().getOnlinePlayers().forEach(this::join);

        getLogger().info("Enabled " + nameAndVersion() + ".");
    }

    @Override
    public void terminate() {
        getLogger().info("Disabled " + nameAndVersion() + ".");
    }

    private String nameAndVersion() {
        return getOrigin().getName() + " v" + getOrigin().getVersion();
    }

    //

    private void join(Player player) {
        if (prefix != null) {
            nametagManager.setPrefix(player, prefix);
        }
        if (suffix != null) {
            nametagManager.setSuffix(player, suffix);
        }
    }

    private void quit(Player player) {
        nametagManager.clear(player);
    }

}

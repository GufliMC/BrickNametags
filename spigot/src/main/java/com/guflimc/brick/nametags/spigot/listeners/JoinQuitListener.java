package com.guflimc.brick.nametags.spigot.listeners;

import com.guflimc.brick.nametags.common.BrickNametagConfig;
import com.guflimc.brick.nametags.spigot.BrickSpigotNametagManager;
import com.guflimc.brick.nametags.spigot.api.SpigotNametagManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitListener implements Listener {

    private final BrickSpigotNametagManager manager;

    private final Component prefix;
    private final Component suffix;

    public JoinQuitListener(BrickNametagConfig config, BrickSpigotNametagManager manager) {
        this.manager = manager;

        prefix = config.prefix == null ? null : MiniMessage.miniMessage().deserialize(config.prefix);
        suffix = config.suffix == null ? null : MiniMessage.miniMessage().deserialize(config.suffix);

        Bukkit.getOnlinePlayers().forEach(this::join);
    }

    private void join(Player player) {
        if (prefix != null) {
            manager.setPrefix(player, prefix);
        }
        if (suffix != null) {
            manager.setSuffix(player, suffix);
        }

        manager.join(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        join(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuitLowest(PlayerQuitEvent event) {
        manager.clear(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuitMonitor(PlayerQuitEvent event) {
        manager.quit(event.getPlayer());
    }

}

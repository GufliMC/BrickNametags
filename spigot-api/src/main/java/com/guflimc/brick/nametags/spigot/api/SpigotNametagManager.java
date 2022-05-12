package com.guflimc.brick.nametags.spigot.api;

import com.guflimc.brick.nametags.api.NametagManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

/**
 * The spigot implementation for {@link NametagManager}.
 */
public interface SpigotNametagManager extends NametagManager<Player> {

    FakeTeam createFakeTeam(String id, Component prefix, Component suffix);

    FakeTeam createFakeTeam(String id, Component prefix, Component suffix, NamedTextColor color);

}
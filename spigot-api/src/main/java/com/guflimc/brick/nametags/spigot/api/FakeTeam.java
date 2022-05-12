package com.guflimc.brick.nametags.spigot.api;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.Set;

public interface FakeTeam {

    // MEMBERS
    void addMember(String player);

    void removeMember(String player);

    Set<String> members();

    // VIEWERS
    void addServerViewers();

    void addViewer(Player player);

    void removeViewer(Player player);

    void removeAllViewers();

    // INFORMATION
    String id();

    Component prefix();

    Component suffix();

    NamedTextColor color();

    void setPrefix(Component prefix);

    void setSuffix(Component suffix);

    void setColor(NamedTextColor color);

}

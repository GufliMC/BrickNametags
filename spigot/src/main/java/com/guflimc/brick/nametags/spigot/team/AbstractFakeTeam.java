package com.guflimc.brick.nametags.spigot.team;

import com.guflimc.brick.nametags.spigot.api.FakeTeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractFakeTeam implements FakeTeam {

    private final Set<String> members = new HashSet<>();
    protected final Set<Player> viewers = new HashSet<>();

    protected final String id;
    protected Component prefix;
    protected Component suffix;
    protected NamedTextColor color;

    public AbstractFakeTeam(String id, Component prefix, Component suffix, NamedTextColor color) {
        this.id = id;
        this.prefix = prefix;
        this.suffix = suffix;
        this.color = color;
    }

    public void addMember(String player) {
        members.add(player);
    }

    public void removeMember(String player) {
        members.remove(player);
    }

    public Set<String> members() {
        return members;
    }

    public void addServerViewers() {
        Bukkit.getOnlinePlayers().forEach(this::addViewer);
    }

    public void addViewer(Player player) {
        if ( viewers.contains(player) ) {
            return;
        }
        viewers.add(player);
    }

    public void removeViewer(Player player) {
        viewers.remove(player);
    }

    public void removeAllViewers() {
        new HashSet<>(viewers).forEach(this::removeViewer);
    }

    public String id() { return id; }

    public Component prefix() {
        return prefix;
    }

    public Component suffix() {
        return suffix;
    }

    public NamedTextColor color() {
        return color;
    }

    public void setPrefix(Component prefix) {
        this.prefix = prefix;
    }

    public void setSuffix(Component suffix) {
        this.suffix = suffix;
    }

    public void setColor(NamedTextColor color) {
        this.color = color;
    }

}
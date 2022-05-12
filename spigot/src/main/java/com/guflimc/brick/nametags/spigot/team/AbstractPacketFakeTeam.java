package com.guflimc.brick.nametags.spigot.team;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class AbstractPacketFakeTeam extends AbstractFakeTeam {

    public AbstractPacketFakeTeam(String id, Component prefix, Component suffix, NamedTextColor color) {
        super(id, prefix, suffix, color);
    }

    @Override
    public void addMember(String player) {
        super.addMember(player);
        sendAddPlayersToTeam(Collections.singletonList(player));
    }

    @Override
    public void removeMember(String player) {
        super.removeMember(player);
        sendRemovePlayersFromTeam(Collections.singletonList(player));
    }

    @Override
    public void addViewer(Player player) {
        super.addViewer(player);
        show(player);
    }

    @Override
    public void removeViewer(Player player) {
        super.removeViewer(player);
        hide(player);
    }

    public void setPrefix(Component prefix) {
        super.setPrefix(prefix);
        sendUpdate();
    }

    public void setSuffix(Component suffix) {
        super.setSuffix(suffix);
        sendUpdate();
    }

    public void setColor(NamedTextColor color) {
        super.setColor(color);
        sendUpdate();
    }

    // PACKETS

    private void hide(Player player) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
        packet.getIntegers().write(0, 1); // 1 = REMOVED
        packet.getStrings().write(0, id);
        send(player, packet);
    }

    private void show(Player receiver) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
        packet.getIntegers().write(0, 0); // 2 = TEAM_CREATED
        packet.getStrings().write(0, id);
        fillValues(packet);
        send(receiver, packet);

        sendAddPlayersToTeam(new ArrayList<>(members()));
    }

    protected void sendAddPlayersToTeam(List<String> players) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
        packet.getIntegers().write(0, 3); // 3 = PLAYERS_REMOVED
        packet.getStrings().write(0, id);
        packet.getSpecificModifier(Collection.class).write(0, players);
        send(packet);
    }

    protected void sendRemovePlayersFromTeam(List<String> players) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
        packet.getIntegers().write(0, 4); // 4 = PLAYERS_REMOVED
        packet.getStrings().write(0, id);
        packet.getSpecificModifier(Collection.class).write(0, players);
        send(packet);
    }

    protected void sendUpdate() {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
        packet.getIntegers().write(0, 2); // 2 = TEAM_UPDATED
        packet.getStrings().write(0, id);
        fillValues(packet);
        send(packet);
    }

    private void send(Player receiver, PacketContainer packet) {
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(receiver, packet);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Cannot send packet.", e);
        }
    }

    private void send(PacketContainer packet) {
        viewers.forEach(player -> send(player, packet));
    }

    protected abstract void fillValues(PacketContainer packetContainer);

}
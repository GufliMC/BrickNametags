package com.guflimc.brick.nametags.minestom;

import com.guflimc.brick.nametags.common.BrickNametag;
import com.guflimc.brick.nametags.common.BrickNametagManager;
import com.guflimc.brick.nametags.minestom.api.MinestomNametagManager;
import com.guflimc.brick.placeholders.minestom.api.MinestomPlaceholderAPI;
import com.guflimc.brick.scheduler.minestom.api.MinestomScheduler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.scoreboard.Team;

import java.util.concurrent.TimeUnit;

public class BrickMinestomNametagManager extends BrickNametagManager<Player> implements MinestomNametagManager {

    private final static PlainTextComponentSerializer PLAIN_TEXT = PlainTextComponentSerializer.plainText();

    private final static String UNIQUEID = "BRNTGS";
    private long COUNTER = 0;

    BrickMinestomNametagManager() {
        super();
        MinestomScheduler.get().asyncRepeating(() -> entities().forEach(this::refresh),
                100, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void refresh(Player player) {
        BrickNametag nametag = nametag(player);
        if (nametag == null) {
            return;
        }

        Component prefix = nametag.prefix();
        if (MinecraftServer.getExtensionManager().hasExtension("brickplaceholders")) {
            prefix = MinestomPlaceholderAPI.get().replace(player, prefix);
        }

        Component suffix = nametag.suffix();
        if (MinecraftServer.getExtensionManager().hasExtension("brickplaceholders")) {
            suffix = MinestomPlaceholderAPI.get().replace(player, suffix);
        }

        String strPrefix = PLAIN_TEXT.serialize(prefix);
        String strSuffix = PLAIN_TEXT.serialize(prefix);

        // If player is already in the team -> ignore
        Team previous = findTeam(player);
        if (previous != null && checkSimilar(previous, strPrefix, strSuffix)) {
            return;
        }

        // Remove from old team
        remove(player);

        // Do not update if the prefix or suffix are empty
        if (strPrefix.equals("") && strSuffix.equals("")) {
            return;
        }

        Team team = findTeam(strPrefix, strSuffix);
        if (team != null) {
            // Team already exists
            team.addMember(player.getUsername());
            team.sendUpdatePacket();
            return;
        }

        // Team doesn't exist
        String name = UNIQUEID + (COUNTER++);
        team = MinecraftServer.getTeamManager().createTeam(name, Component.text(name), prefix, nametag.nameColor(), suffix);
        team.addMember(player.getUsername());
    }

    @Override
    protected void remove(Player player) {
        Team team = findTeam(player);
        if (team == null) {
            return;
        }
        team.removeMember(player.getUsername());
        if (team.getMembers().isEmpty()) {
            MinecraftServer.getTeamManager().deleteTeam(team);
        }
    }

    //

    private boolean checkSimilar(Team team, String prefix, String suffix) {
        return PLAIN_TEXT.serialize(team.getPrefix()).equals(prefix)
                && PLAIN_TEXT.serialize(team.getSuffix()).equals(suffix);
    }

    private Team findTeam(String prefix, String suffix) {
        return MinecraftServer.getTeamManager().getTeams().stream()
                .filter(team -> checkSimilar(team, prefix, suffix))
                .findFirst().orElse(null);
    }

    private Team findTeam(Player player) {
        return MinecraftServer.getTeamManager().getTeams().stream()
                .filter(t -> t.getMembers().contains(player.getUsername()))
                .findFirst().orElse(null);
    }
}

package com.guflimc.brick.nametags.spigot;

import com.guflimc.brick.nametags.common.BrickNametag;
import com.guflimc.brick.nametags.common.BrickNametagManager;
import com.guflimc.brick.nametags.spigot.api.FakeTeam;
import com.guflimc.brick.nametags.spigot.api.SpigotNametagManager;
import com.guflimc.brick.nametags.spigot.team.PacketFakeTeam;
import com.guflimc.brick.placeholders.spigot.api.SpigotPlaceholderAPI;
import com.guflimc.brick.scheduler.spigot.api.SpigotScheduler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class BrickSpigotNametagManager extends BrickNametagManager<Player> implements SpigotNametagManager {

    private final static PlainTextComponentSerializer PLAIN_TEXT = PlainTextComponentSerializer.plainText();

    private final static String UNIQUEID = "BRNTGS";
    private long COUNTER = 0;

    private final Set<FakeTeam> teams = new HashSet<>();

    BrickSpigotNametagManager(SpigotScheduler scheduler) {
        super();
        scheduler.asyncRepeating(() -> entities().forEach(this::refresh),
                100, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void refresh(Player player) {
        BrickNametag nametag = nametag(player);
        if (nametag == null) {
            return;
        }

        Component prefix = nametag.prefix();
        if (Bukkit.getPluginManager().isPluginEnabled("BrickPlaceholders")) {
            prefix = SpigotPlaceholderAPI.get().replace(player, prefix);
        }

        Component suffix = nametag.suffix();
        if (Bukkit.getPluginManager().isPluginEnabled("BrickPlaceholders")) {
            suffix = SpigotPlaceholderAPI.get().replace(player, suffix);
        }

        String strPrefix = PLAIN_TEXT.serialize(prefix);
        String strSuffix = PLAIN_TEXT.serialize(prefix);

        // If player is already in the team -> ignore
        FakeTeam previous = findTeam(player);
        if (previous != null && checkSimilar(previous, strPrefix, strSuffix)) {
            return;
        }

        // Remove from old team
        remove(player);

        // Do not update if the prefix or suffix are empty
        if (strPrefix.equals("") && strSuffix.equals("")) {
            return;
        }

        FakeTeam team = findTeam(strPrefix, strSuffix);
        if (team != null) {
            // Team already exists
            team.addMember(player.getName());
            return;
        }

        // Team doesn't exist
        String name = UNIQUEID + (COUNTER++);
        team = createFakeTeam(name, prefix, suffix, nametag.nameColor());
        team.addMember(player.getName());
        teams.add(team);
    }

    @Override
    protected void remove(Player player) {
        FakeTeam team = findTeam(player);
        if (team == null) {
            return;
        }

        team.removeMember(player.getName());

        // team is empty -> delete
        if (team.members().isEmpty()) {
            team.removeAllViewers();
            teams.remove(team);
        }
    }

    //

    private boolean checkSimilar(FakeTeam team, String prefix, String suffix) {
        return PLAIN_TEXT.serialize(team.prefix()).equals(prefix)
                && PLAIN_TEXT.serialize(team.suffix()).equals(suffix);
    }

    private FakeTeam findTeam(String prefix, String suffix) {
        return teams.stream().filter(t -> checkSimilar(t, prefix, suffix))
                .findFirst().orElse(null);
    }

    private FakeTeam findTeam(Player player) {
        return teams.stream().filter(t -> t.members().contains(player.getName())).findFirst().orElse(null);
    }

    // SPIGOT ONLY STUFF

    @Override
    public FakeTeam createFakeTeam(String id, Component prefix, Component suffix) {
        return createFakeTeam(id, prefix, suffix, NamedTextColor.WHITE);
    }

    @Override
    public FakeTeam createFakeTeam(String id, Component prefix, Component suffix, NamedTextColor color) {
        return new PacketFakeTeam(id, prefix, suffix, color);
    }
}


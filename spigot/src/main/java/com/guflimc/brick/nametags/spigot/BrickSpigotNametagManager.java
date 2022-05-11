package com.guflimc.brick.nametags.spigot;

import com.guflimc.brick.nametags.common.BrickNametag;
import com.guflimc.brick.nametags.common.BrickNametagManager;
import com.guflimc.brick.nametags.spigot.api.SpigotNametagManager;
import com.guflimc.brick.nametags.spigot.team.AbstractFakeTeam;
import com.guflimc.brick.nametags.spigot.team.FakeTeam_v1_17;
import com.guflimc.brick.placeholders.spigot.api.SpigotPlaceholderAPI;
import com.guflimc.brick.scheduler.spigot.api.SpigotScheduler;
import net.kyori.adventure.text.Component;
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

    private final Set<AbstractFakeTeam> teams = new HashSet<>();

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
        AbstractFakeTeam previous = findTeam(player);
        if (previous != null && checkSimilar(previous, strPrefix, strSuffix)) {
            return;
        }

        // Remove from old team
        remove(player);

        // Do not update if the prefix or suffix are empty
        if (strPrefix.equals("") && strSuffix.equals("")) {
            return;
        }

        AbstractFakeTeam team = findTeam(strPrefix, strSuffix);
        if (team != null) {
            // Team already exists
            team.addMember(player.getName());
            return;
        }

        // Team doesn't exist
        String name = UNIQUEID + (COUNTER++);
        team = new FakeTeam_v1_17(name, prefix, suffix, nametag.nameColor());
        team.addMember(player.getName());
    }

    @Override
    protected void remove(Player player) {
        AbstractFakeTeam team = findTeam(player);
        if (team == null) {
            return;
        }

        team.removeMember(player.getName());

        // team is empty -> delete
        if (team.getMembers().isEmpty()) {
            team.clear();
            teams.remove(team);
        }
    }

    //

    private boolean checkSimilar(AbstractFakeTeam team, String prefix, String suffix) {
        return PLAIN_TEXT.serialize(team.prefix()).equals(prefix)
                && PLAIN_TEXT.serialize(team.suffix()).equals(suffix);
    }

    private AbstractFakeTeam findTeam(String prefix, String suffix) {
        return teams.stream().filter(t -> checkSimilar(t, prefix, suffix))
                .findFirst().orElse(null);
    }

    private AbstractFakeTeam findTeam(Player player) {
        return teams.stream().filter(t -> t.getMembers().contains(player.getName())).findFirst().orElse(null);
    }
}

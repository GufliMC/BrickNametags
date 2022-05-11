package com.guflimc.brick.nametags.spigot.team;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.BukkitConverters;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.ChatColor;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class FakeTeam_v1_17 extends AbstractPacketFakeTeam {

    private final static GsonComponentSerializer GSON = GsonComponentSerializer.gson();

    public FakeTeam_v1_17(String id, Component prefix, Component suffix, NamedTextColor color) {
        super(id, prefix, suffix, color);
    }

    @Override
    protected void fillValues(PacketContainer packet) {
        try {
            Class<?> scoreboardClass = Class.forName("net.minecraft.world.scores.Scoreboard");
            Class<?> scoreboardTeamClass = Class.forName("net.minecraft.world.scores.ScoreboardTeam");
            Object scoreboardTeam = scoreboardTeamClass.getConstructor(scoreboardClass, String.class).newInstance(null, id);

            StructureModifier<?> m = new StructureModifier<>(scoreboardTeamClass).withTarget(scoreboardTeam);

            StructureModifier<WrappedChatComponent> wccm = m.withType(
                    MinecraftReflection.getIChatBaseComponentClass(), BukkitConverters.getWrappedChatComponentConverter());

            wccm.write(0, WrappedChatComponent.fromLegacyText(id));
            wccm.write(1, WrappedChatComponent.fromJson(GSON.serialize(prefix)));
            wccm.write(2, WrappedChatComponent.fromJson(GSON.serialize(suffix)));

            ChatColor color = ChatColor.valueOf(this.color.toString().toUpperCase());
            Class<?> enumChatFormatClass = MinecraftReflection.getMinecraftClass("EnumChatFormat");
            m.withType(enumChatFormatClass, new EnumWrappers.EnumConverter<>(enumChatFormatClass, ChatColor.class)).write(0, color);

            Class<?> dataClass = packet.getType().getPacketClass().getDeclaredClasses()[0];
            Object dataObject = dataClass.getConstructor(scoreboardTeamClass).newInstance(scoreboardTeam);
            packet.getModifier().withType(Optional.class).write(0, Optional.of(dataObject));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
package com.guflimc.brick.nametags.common;

import com.guflimc.brick.nametags.api.NametagManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BrickNametagManager<T> implements NametagManager<T> {

    private final Map<T, BrickNametag> nametags = new ConcurrentHashMap<>();

    // API

    @Override
    public final void setNametag(@NotNull T entity, @NotNull Component prefix, @NotNull Component suffix, @NotNull NamedTextColor nameColor) {
        System.out.println("set nametag");
        BrickNametag nametag = new BrickNametag(prefix, suffix, nameColor);
        nametags.put(entity, nametag);
        refresh(entity);
    }

    @Override
    public final void setPrefix(@NotNull T entity, @NotNull Component prefix) {
        BrickNametag previous = nametags.get(entity);
        if (previous != null) {
            setNametag(entity, prefix, previous.suffix(), previous.nameColor());
            return;
        }
        setNametag(entity, prefix, Component.text(""), NamedTextColor.WHITE);
    }

    @Override
    public final void setSuffix(@NotNull T entity, @NotNull Component suffix) {
        BrickNametag previous = nametags.get(entity);
        if (previous != null) {
            setNametag(entity, previous.prefix(), suffix, previous.nameColor());
            return;
        }
        setNametag(entity, Component.text(""), suffix, NamedTextColor.WHITE);
    }

    @Override
    public void setNameColor(@NotNull T entity, @NotNull NamedTextColor nameColor) {
        BrickNametag previous = nametags.get(entity);
        if (previous != null) {
            setNametag(entity, previous.prefix(), previous.suffix(), nameColor);
            return;
        }
        setNametag(entity, Component.text(""), Component.text(""), nameColor);
    }

    @Override
    public final void clear(T entity) {
        nametags.remove(entity);
        remove(entity);
    }

    // INTERNAL

    protected final Collection<T> entities() {
        return Collections.unmodifiableSet(nametags.keySet());
    }

    protected final BrickNametag nametag(T entity) {
        return nametags.get(entity);
    }

    protected abstract void refresh(T entity);

    protected abstract void remove(T entity);

}

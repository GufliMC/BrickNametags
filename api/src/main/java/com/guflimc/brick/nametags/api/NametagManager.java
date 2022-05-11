package com.guflimc.brick.nametags.api;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

public interface NametagManager<T> {

    /**
     * Set the nametag prefix and suffix for the given entity
     * @param entity the entity
     * @param prefix the prefix
     * @param suffix the suffix
     */
    void setNametag(@NotNull T entity, @NotNull Component prefix, @NotNull Component suffix, @NotNull NamedTextColor nameColor);

    /**
     * Set the nametag prefix for the given entity
     * @param entity the entity
     * @param prefix the prefix
     */
    void setPrefix(@NotNull T entity, @NotNull Component prefix);

    /**
     * Set the nametag suffix for the given entity
     * @param entity the entity
     * @param suffix the suffix
     */
    void setSuffix(@NotNull T entity, @NotNull Component suffix);

    /**
     * Set the nametag name color for the given entity
     * @param entity the entity
     * @param nameColor the name color
     */
    void setNameColor(@NotNull T entity, @NotNull NamedTextColor nameColor);

    /**
     * Clear the nametag prefix and suffix for the given entity
     * @param entity the entity
     */
    void clear(T entity);

}
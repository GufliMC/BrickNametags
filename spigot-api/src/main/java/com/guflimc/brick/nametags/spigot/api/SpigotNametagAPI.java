package com.guflimc.brick.nametags.spigot.api;

import org.jetbrains.annotations.ApiStatus;

public class SpigotNametagAPI {

    private static SpigotNametagManager spigotNametagManager;

    @ApiStatus.Internal
    public static void setNametagManager(SpigotNametagManager manager) {
        spigotNametagManager = manager;
    }

    //

    /**
     * Get the registered nametag manager.
     * @return the nametag manager
     */
    public static SpigotNametagManager get() {
        return spigotNametagManager;
    }

}

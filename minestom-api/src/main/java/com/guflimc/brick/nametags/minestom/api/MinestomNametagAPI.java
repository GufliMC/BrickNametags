package com.guflimc.brick.nametags.minestom.api;

import org.jetbrains.annotations.ApiStatus;

public class MinestomNametagAPI {

    private static MinestomNametagManager minestomNametagManager;

    @ApiStatus.Internal
    public static void setNametagManager(MinestomNametagManager manager) {
        minestomNametagManager = manager;
    }

    //

    /**
     * Get the registered nametag manager.
     * @return the nametag manager
     */
    public static MinestomNametagManager get() {
        return minestomNametagManager;
    }

}

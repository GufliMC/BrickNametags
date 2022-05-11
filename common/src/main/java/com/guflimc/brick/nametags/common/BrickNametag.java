package com.guflimc.brick.nametags.common;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public record BrickNametag(Component prefix, Component suffix, NamedTextColor nameColor) {
}

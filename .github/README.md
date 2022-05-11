# BrickNametags

A nametag/tablist extension for [Minestom](https://github.com/Minestom/Minestom).

## Install

Get the [release](https://github.com/GufliMC/BrickNametags/releases)
and place it in the extension folder of your minestom server.

### Dependencies
* [BrickPlaceholders](https://github.com/MinestomBrick/BrickPlaceholders) (soft)

## Config

You can change the settings in the `config.json`.

```json
{
  "prefix": "{rank}",
  "suffix": ""
}
```

## API

### Maven
```
repositories {
    maven { url "https://repo.jorisg.com/snapshots" }
}

dependencies {
    // minestom
    compileOnly 'com.guflimc.brick.nametags:minestom-api:1.0-SNAPSHOT'
    
    // spigot
    compileOnly 'com.guflimc.brick.nametags:spigot-api:1.0-SNAPSHOT'
}
```

### Usage

Check the [javadocs](https://minestombrick.github.io/BrickNametags/)

#### Examples

```java
SpigotNametagAPI.get().setNametag(player, Component.text("hey"), Component.text("oi"));
SpigotNametagAPI.get().clear(player);
```

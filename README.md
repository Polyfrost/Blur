# Blur+ for Ornithe 1.8.9

Blur+ adds configurable blur, gradients, and fade animations behind Minecraft screens.

![Image showing the Minecraft inventory with a blur effect in the background](https://cdn.modrinth.com/data/NK39zBp2/images/213f18fcf3d6c55cad164077d569e2f0339551da.webp)

## Configuration

Use Mod Menu's **Configure** button or edit `config/blur.json`. The file retains Blur+'s current screen, style, and animation options. The 1.8.9 port also stores `radius`, because this version has no vanilla menu-blur slider.

## Build

```sh
./gradlew build
```

The release JAR is written to `build/libs/`.

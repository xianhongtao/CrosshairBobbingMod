# Crosshair Bobbing Mod

A Minecraft mod that makes the crosshair follow the view bobbing.

## Supported versions

This project targets the modern Minecraft versions (1.20.4+), split into two loader
lines inside the `modern/` Gradle workspace:

| Loader | Versions |
|---|---|
| **Fabric** | 1.20.4, 1.20.5, 1.20.6, 1.21, 1.21.1 – 1.21.11, 26.2 |
| **NeoForge** | 1.20.6, 1.21, 1.21.1 – 1.21.11, 26.1.2 |

> The legacy 1.16.2 – 1.20.2 line (Fabric + Forge, ForgeGradle toolchain) is archived
> under [`legacy/`](./legacy) and is no longer maintained.

## Requirements

- JDK 25 (toolchain compiles down to Java 21 bytecode)
- Gradle 9.x (wrapper included)

## Building

The `modern/` directory is its own Gradle build. Run all commands from the repository
root:

```sh
# Build the distribution (single multi-version Fabric jar + per-version NeoForge jars)
./modern/gradlew -p modern buildMod

# Run the client for a specific version (smoke test)
./modern/gradlew -p modern :game:fabric26.2:runClient
```

Artifacts are placed in `modern/dists/` (Fabric single jar + NeoForge per-version jars).

## Formatting

Java sources are formatted with [Spotless](https://github.com/diffplug/spotless)
(google-java-format). JSON resources are kept as hand-maintained templates because
their Groovy placeholders and escaped values must remain byte-for-byte intact. An
`.editorconfig` and `.gitattributes` keep editor and line-ending settings consistent.

```sh
# Apply formatting to all source
./modern/gradlew -p modern spotlessApply

# Verify formatting (CI does this)
./modern/gradlew -p modern spotlessCheck
```

## License

MIT

# Minecraft version compatibility

The project currently has a verified build for Minecraft 1.21.1 (Fabric).

## Target versions

| Minecraft | Status |
| --- | --- |
| 1.21.1 | Existing verified build |
| 1.21.8 | Planned; needs a dedicated compile and test |
| 1.21.9 | Planned; needs API and compile checks |
| 1.21.10 | Planned; needs API and compile checks |
| 1.21.11 | Planned; needs API and compile checks |
| 26.1 | Planned; separate Loom setup and Java 25 required |
| 26.2 | Planned; separate Loom setup and Java 25 required |
| 26.3 | Planned; separate Loom setup and Java 25 required |

## Porting approach

- Keep the working 1.21.1 build intact.
- Port and verify the 1.21.x versions first.
- Keep Minecraft 26.x builds separate because Minecraft 26.1 and newer use the non-obfuscated Fabric Loom plugin ID and Java 25.
- Publish a separate JAR for each version only after its build succeeds.
- A successful compile is not a substitute for an in-game test.

## Current compatibility notes

The current source uses Yarn names and Fabric client APIs. Each target version must be checked against its matching mappings, Fabric Loader, Fabric API, Loom and Java toolchain before being marked supported.

# Streamer Mod

A custom client-side utility mod for **Minecraft Java 1.21.1 + Fabric**.

## Current prototype

- Open the menu with **Right Shift**.
- **Auction Notes**: a temporary reminder field for item, price, or player details.
- **PayGuard Checklist**: reminders to verify trade details before confirming anything in-game.
- **Countdown**: menu placeholder for the upcoming timer feature.

This mod does not place bets, gamble, or automate trades. It is a utility interface only.

## Build

Requires Java 21.

```bash
gradle build
```

The JAR is created in `build/libs/`. GitHub Actions also builds the project after pushes and uploads the JAR as a workflow artifact.

## Compatibility

- Minecraft 1.21.1
- Fabric Loader 0.16+
- Fabric API
- Java 21

## Controls

| Key | Action |
| --- | --- |
| Right Shift | Open Streamer Mod menu |

## Project status

Early prototype (0.1.0). Auction notes are temporary and the countdown is not implemented yet.

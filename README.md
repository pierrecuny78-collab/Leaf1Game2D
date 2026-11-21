# Leaf1Game2D

**Prototype RPG 2D — version 0.1.0**

Leaf1Game2D is an offline-first 2D fantasy RPG prototype made with Godot 4.4.1 and GDScript. The first playable build focuses on exploration, egg discovery, creature hatching, progression and local saves.

## First playable features

- Top-down 2D exploration in a fantasy meadow.
- Mobile touch controls and keyboard controls (WASD / arrow keys).
- Egg discovery and interaction.
- Random creature hatching and collection.
- Player levels, experience and coins.
- A guide NPC and a small pond.
- Local save file stored on the device.
- Android APK build through GitHub Actions.

## Play

Open this folder with Godot 4.4.1 and run the main scene at scenes/main.tscn.

On desktop, move with WASD or arrow keys. On Android, use the direction pad. Approach a golden egg and tap INTERAGIR, then tap ÉCLORE to hatch it. Use SAUVEGARDER to save progress.

## Build Android APK

The GitHub Actions workflow in .github/workflows/build-android.yml builds a debug APK on every push to main, and can also be started manually from the Actions tab.

When the workflow succeeds, download the artifact named **Leaf1Game2D-Android-APK** from its run.

The Android export uses Godot's debug signing configuration. It is intended for testing, not Play Store publication.

## Roadmap

- 0.1.x: polish the first playable build, add sound, quests, inventory and more creatures.
- 0.2.x: combat, skills, equipment and first boss.
- 0.3.x: more biomes, dungeons, egg rarities and creature evolution.
- Later: online co-op and a separate Android live-wallpaper companion.

## Project principles

Offline-first, no subscription, local save by default. Online features will remain optional and will not block offline play.

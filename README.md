# Leaf1Game2D

**RPG 2D pixel art — LeafEngine2D native alpha 0.3.0**

Leaf1Game2D is an offline-first Android RPG prototype powered by the in-house C++17 LeafEngine2D core. Android's Activity/Canvas is the mobile shell; game simulation and pixel-buffer rendering are implemented in C++ and exposed through JNI.

## Alpha 0.3.0

- Native C++17 engine, Android NDK and CMake.
- 480x270 pixel-art scene with crisp nearest-neighbor scaling.
- Original 16x16 pixel-art sprites for the player, Sproutling creature and golden egg.
- Touch movement and bounds checking.
- Egg reward: +100 coins, +35 XP and one collected egg.
- Battle reward: +150 coins, +120 XP; the first creature gains XP and levels up.
- Local progression persistence for coins, XP, player level and egg count.
- Offline prototype; no account or network permission.

## Build and downloads

GitHub Actions runs native C++ tests, builds the Android game APK and the LeafStudio2D editor APK, then packages both APKs with the complete source tree.

Download from the repository's **Actions → Build LeafVibe 2D apps → Artifacts**:
- `Leaf1Game2D-Alpha-0.3.0-APK`
- `LeafStudio2D-Alpha-0.2.0-APK`
- `Leaf1Game2D-Complet-0.3.0` (source ZIP + both APKs)

The APKs are debug builds intended for testing, not Play Store release-signed packages. GitHub Actions artifacts are retained for 30 days.

## Engine architecture

See `docs/LEAFENGINE2D_ARCHITECTURE.md` and `engine/README.md`. SDL2 is not currently used: Android's Activity/Canvas is the mobile shell, while LeafEngine2D owns game logic and pixel rendering.

## Roadmap

Next: animation playback, world collisions, collectible objects with proximity checks, egg inventory and hatching screen, creature collection, battle UI and a first boss. Later: larger explorable maps, crafting, building, kingdom progression, optional multiplayer and a separate live-wallpaper service.

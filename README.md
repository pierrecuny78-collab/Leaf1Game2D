# Leaf1Game2D

**RPG 2D pixel art — LeafEngine2D native alpha 0.1.0**

Leaf1Game2D is a native Android RPG prototype powered by the in-house C++17 LeafEngine2D core. It does not use Godot or Unity. Android's Activity/Canvas is the mobile shell; the game simulation and pixel buffer are implemented in C++ and exposed through JNI.

## Alpha 0.1.0
- Native C++17 game core built with Android NDK and CMake.
- 480x270 pixel-art scene rendered with crisp nearest-neighbor scaling.
- Touch movement, egg reward action (+100 coins, +35 XP), battle reward action (+150 coins, +120 XP).
- Starts with 500 coins and a starter creature in engine state.
- Offline prototype; no account or network permission.

## Build APK
GitHub Actions builds a debug APK on pushes to main. Open Actions and download the artifact **Leaf1Game2D-Alpha-0.1.0-APK**. The APK is for testing and is not Play Store release-signed.

## Engine architecture
See docs/LEAFENGINE2D_ARCHITECTURE.md and engine/README.md. SDL2 is not currently used: the first Android alpha uses the Android native UI shell and JNI, while LeafEngine2D owns the game logic and pixel rendering.

## Roadmap
Next: proper touch D-pad, animated sprite sheets, world collisions, egg inventory/hatching screen, battle UI, local persistence. Later: bosses, kingdom progression, optional multiplayer, and a separate live-wallpaper service.

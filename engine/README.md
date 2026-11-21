# LeafEngine2D — runtime prototype

LeafEngine2D is the shared 2D runtime planned for the Leaf game toolchain. This code is currently staged inside the first game repository; it will be extracted into its own GitHub repository when repository creation is available.

## Current implementation

- C++17 native core.
- Low-resolution pixel buffer with clipped rectangle drawing.
- Initial scene graph: Scene and hierarchical SceneNode objects.
- Node kinds: Node2D, Sprite2D, Rectangle2D and Camera2D (camera behavior is not implemented yet).
- Android NDK/JNI integration for the Leaf1Game2D prototype.

## Scene graph limits in this alpha

The first renderer supports hierarchical position offsets, visibility and colored rectangles. Sprite texture loading, camera transforms, animation, physics, input abstraction and scene-file parsing are not implemented yet. The .leafscene file is currently a design contract, not a parser input.

## Build

The Android prototype uses Android Activity/Canvas as its platform shell and C++ through JNI. SDL2 is not currently integrated. Build configuration is in CMakeLists.txt.

## Architecture direction

The editor (LeafStudio2D), runtime (LeafEngine2D), exporter (LeafBuild2D) and every game project remain separate products. This folder is a temporary development staging area for the runtime API, not the final repository boundary.

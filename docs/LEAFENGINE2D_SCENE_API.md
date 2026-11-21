# LeafEngine2D scene API — 0.1 draft

## Scene model

A Scene owns one root SceneNode. Nodes contain a name, kind, local integer position, optional rectangle dimensions, a color, visibility and ordered children. Child positions are relative to their parent. Invisible nodes suppress their complete subtree.

## Node kinds

| Kind | Current behavior |
|---|---|
| Node2D | Transform/container only |
| Rectangle2D | Draw a filled rectangle |
| Sprite2D | Temporary colored-rectangle placeholder |
| Camera2D | Reserved; no camera transform yet |

## Rendering

The current renderer traverses the tree depth-first and accumulates integer positions. PixelSurface performs clipping at the target bounds. Integer coordinates preserve crisp pixel-art rendering.

## Deliberate limitations

This draft does not yet implement texture resources, rotation, scaling, camera transforms, animation, physics, scene serialization/loading or editor selection. Those will be added as versioned features rather than hidden assumptions.

## Host tests

Configure the engine CMake project with LEAF_BUILD_TESTS=ON on a desktop compiler, then run CTest. Android builds leave this option disabled.

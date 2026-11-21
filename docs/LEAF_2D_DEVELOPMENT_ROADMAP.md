# Leaf 2D development roadmap

## Phase 0 — contracts and project layout
- [x] Define editor, runtime, build tool and game responsibilities.
- [x] Add versioned game manifest to Leaf1Game2D.
- [x] Define per-game content and save isolation.
- [ ] Create the standalone LeafStudio2D repository.
- [ ] Create the standalone LeafEngine2D repository.
- [ ] Create the standalone LeafBuild2D repository.

## Phase 1 — minimum usable runtime
- [ ] Extract the native C++ core from the game repository into LeafEngine2D.
- [ ] Define stable scene, node, resource and input APIs.
- [ ] Load leafgame.project.json.
- [ ] Load a first scene file and texture assets.
- [ ] Implement sprite rendering, transforms, input and collisions.
- [ ] Add runtime tests on Android and desktop.

## Phase 2 — minimum usable editor
- [ ] Project manager: create, open, duplicate and validate a game.
- [ ] Scene editor with node tree and inspector.
- [ ] 2D viewport with selection, move, scale and rotate tools.
- [ ] Sprite and texture import with license/attribution metadata.
- [ ] Pixel-art editor with 64x64 preset and adjustable canvas.
- [ ] Tilemap editor and animation timeline.
- [ ] Save all edits into the game's own repository structure.

## Phase 3 — build pipeline
- [ ] Build tool accepts a local project folder or Git repository URL plus a pinned branch/tag/commit.
- [ ] Validate project manifest and resource references.
- [ ] Build APK using the matching LeafEngine2D version.
- [ ] Publish APK and reproducible build metadata as an artifact.
- [ ] Add release signing with a user-provided keystore kept outside source control.

## Phase 4 — game production
- [ ] Move Leaf1Game2D content into scenes/resources managed by the project format.
- [ ] Implement player controller, world map, collision and camera.
- [ ] Implement inventory, eggs, hatching and creature collection.
- [ ] Implement battle system, bosses and progression.
- [ ] Add optional multiplayer only after the offline game loop is stable.

## Phase 5 — cross-platform maturity
- [ ] Desktop editor builds for Windows, Linux and macOS.
- [ ] Android editor optimized for touch and constrained memory.
- [ ] Desktop game exports and additional mobile targets.
- [ ] Plugin and extension API with permission boundaries.

# Leaf 2D ecosystem integration contract

Status: initial architecture specification, 2026-11-21.

## Product responsibilities

- LeafStudio2D is the cross-platform authoring application (Android + desktop). It creates and edits game projects; it is not itself the game runtime.
- LeafEngine2D is the shared runtime and engine API. A game repository pins a compatible engine API version.
- LeafBuild2D is the independent build/export tool. It validates a game project, resolves the declared engine version, packages the game and produces an Android APK.
- Leaf1Game2D is a game content repository and the first RPG test project. It must remain independently buildable and must not contain the editor.

## Platform strategy

LeafStudio2D targets Android and desktop. The editor UI and project format should be shared where practical. Desktop targets should include Windows, Linux and macOS. Android is a first-class authoring platform, with touch-friendly panels and an optional external keyboard/mouse workflow.

LeafBuild2D should initially build Android APKs. Desktop exports can be added after the Android pipeline is stable.

## Project isolation

Every game repository owns its scenes and levels, scripts and game logic, textures and visual assets, audio, game-specific data and localization, input mappings and settings, and save-data schema and migration rules.

The shared engine is a dependency, not a place to store game content. A game must not read or write another game's saves or assets.

## Project manifest

leafgame.project.json is the first project manifest contract. It identifies the project format, engine API, display settings, entry scene, asset directories and Android application ID.

Future format revisions must increment format_version when compatibility is broken. Tools must reject unsupported major format versions with a readable diagnostic rather than silently corrupting a project.

## Asset rules

- Pixel-art default sprite canvas: 64x64 pixels.
- Non-square and other sprite sizes are supported.
- Conventional 2D artwork is supported alongside pixel art.
- Imported artwork must preserve attribution and license metadata where required.
- Pinterest is a visual-reference source, not a blanket license to copy or redistribute artwork. Imported files must be original, user-owned, public-domain or explicitly licensed for the intended use.

## Engine API boundary

The editor writes project data using documented, versioned formats. It must not generate arbitrary engine-private files without a versioned schema. The runtime loads the project manifest and entry scene. The build tool validates the manifest and packages only the selected game's resources plus the compatible engine runtime.

## Build contract

Input: one game repository at a pinned commit or release tag.

Validation:
1. Parse leafgame.project.json.
2. Check format_version and minimum_engine_api.
3. Verify the entry scene and declared asset directories.
4. Check application ID, Android target settings and supported ABIs.
5. Report missing assets and unsupported features before compiling.

Output: a versioned APK artifact, build log, project commit SHA, engine version and build-tool version. Debug builds are for testing; release builds must be signed with a user-controlled key. Never put signing secrets in the game repository.

## Current implementation status

Leaf1Game2D currently contains a native C++17 prototype and Android Java/JNI shell. It is not yet a complete LeafEngine2D SDK and it does not yet load the project manifest or scene files. The next integration work is to make the runtime consume this contract, then make LeafBuild2D build from the same contract.

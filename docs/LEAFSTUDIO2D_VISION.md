# LeafStudio2D — product vision and feature plan

Status: 0.1.0-alpha code-editor prototype.

## Product direction

LeafStudio2D is a code-first 2D game creation environment for Android and desktop. Its workflow takes inspiration from the productivity concepts of Godot and Roblox Studio (scene tree, inspector, assets, script editor, output console, play/test workflow), while using an original implementation, API, language and visual identity.

## Available in the current prototype

- Android application module.
- Local sample project created in app-private storage.
- Project files: project manifest, scene document and LeafScript source.
- Multi-file switching for the starter files.
- Monospaced code editor.
- Save to local project files.
- Basic structural validation for balanced braces and unterminated quoted strings.
- Console/log panel with clear action.
- New script action.

## Not implemented yet

The current editor does not execute LeafScript, compile games, render an editable scene viewport, import arbitrary files, synchronize GitHub repositories or build APKs. The Validate action is only a lightweight structural check, not a full language parser or compiler. The sample LeafScript syntax is a design draft.

## Planned IDE layout

1. Project/file dock: game folders, scripts, scenes, assets, audio and settings.
2. Script workspace: tabs, search, replace, line numbers, syntax highlighting, code completion, diagnostics and refactoring.
3. Scene tree: hierarchical nodes, groups, ownership and reusable scenes.
4. Inspector: editable properties, signals, metadata and resource references.
5. 2D viewport: pan, zoom, selection, snapping, transform gizmos, grid and guides.
6. Asset browser: import, preview, metadata, texture filtering, sprite sheets and animation clips.
7. Output console: timestamped logs, info/warning/error filters, search, clear, copy, stack traces and runtime messages.
8. Debugger: breakpoints, step controls, call stack, variables, watch expressions and remote device logs.
9. Run controls: play project, play current scene, stop, pause and reload.
10. Build/export dock: Android APK profiles, package ID, version, icon, permissions and signing.
11. Project settings: display, input actions, physics, audio, rendering, localization and save-data settings.
12. Documentation/help: searchable API reference, templates and examples.

## Code-first design

Games are authored in source code. The planned original language is LeafScript, with a readable syntax inspired by common game scripting patterns but not claiming GDScript or Lua compatibility. The language must have a formal grammar, versioned standard library, diagnostics and sandboxed runtime.

Planned API families:
- Node and Scene lifecycle.
- Node2D transforms and hierarchy.
- Sprite, texture and animation resources.
- Input actions for touch, keyboard, mouse and controllers.
- Physics bodies, shapes, ray queries and collision layers.
- Audio players and buses.
- UI controls and signals.
- Timers, tweens and coroutines.
- Files, settings and isolated per-game saves.
- Networking only through explicit project permissions.

## Safety and project isolation

- Each game project has its own root, manifest, assets, scripts and runtime save namespace.
- Scripts do not receive unrestricted filesystem, network or device access by default.
- Network, external file access, microphone, camera and other sensitive capabilities require explicit project permissions.
- Editor actions that overwrite or delete project files require confirmation or recoverable undo.
- Imported assets retain license and attribution metadata.
- A game build records source commit, engine API and build-tool version.

## Cross-platform plan

Android is the first test target for the editor UI. Desktop targets are Windows, Linux and macOS. The project format, language grammar, diagnostics and runtime APIs should remain shared. Desktop UI and Android UI may use different platform shells over shared editor/core modules.

## Milestones

- M0: Android code-editor prototype and console.
- M1: robust project manager, file tree, tabs, search, line numbers and undo/redo.
- M2: LeafScript parser, diagnostics, syntax highlighting and interpreter.
- M3: engine runtime loads and runs code-authored scenes.
- M4: scene tree, inspector, 2D viewport and resource browser.
- M5: debugger, input/physics/audio APIs and test runner.
- M6: Git integration and LeafBuild2D Android APK export.
- M7: desktop editor releases and parity.

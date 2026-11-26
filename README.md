# Leaf1Game2D

**LeafStudio2D — atelier de création 2D (version de travail 0.4.0-alpha)**

Ce dépôt héberge l'application Android LeafStudio2D et le prototype Leaf1Game2D utilisé comme projet/jeu de démonstration. Le moteur natif C++17 LeafEngine2D est encore un moteur en cours d'évolution. LeafStudio2D n'est pas encore un éditeur universel/runtime complet : consulter le cahier maître pour distinguer les fonctions présentes, partielles et à construire.

## État du dépôt — 0.4.0-alpha (travail en cours)

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

Les artefacts sont publiés par GitHub Actions sous **Actions → Build LeafVibe 2D apps → Artifacts** lorsque le workflow réussit :
- `Leaf1Game2D-Alpha-0.4.0-APK`
- `LeafStudio2D-Alpha-0.4.0-APK`
- `Leaf1Game2D-Complet-0.4.0` (ZIP source + deux APK)

Les artefacts sont des APK debug de test. Leur présence ne certifie pas le fonctionnement sur appareil. Ne pas annoncer LeafStudio2D comme terminé tant que les critères du cahier maître ne sont pas passés.

The APKs are debug builds intended for testing, not Play Store release-signed packages. GitHub Actions artifacts are retained for 30 days.

## Engine architecture

See `docs/LEAFENGINE2D_ARCHITECTURE.md` and `engine/README.md`. SDL2 is not currently used: Android's Activity/Canvas is the mobile shell, while LeafEngine2D owns game logic and pixel rendering.

## Documentation LeafStudio2D

- `docs/LEAFSTUDIO2D_MASTER_FEATURE_BACKLOG.md` : cahier maître consolidé, toutes les demandes, priorités, limites et critères de validation.
- `docs/LEAFSTUDIO2D_VISION_UNIVERSELLE_V2.md` : vision produit.
- `docs/LEAFSTUDIO2D_IMPLEMENTATION_PLAN.md` : plan d'intégration et état technique.

## Roadmap

Next: animation playback, world collisions, collectible objects with proximity checks, egg inventory and hatching screen, creature collection, battle UI and a first boss. Later: larger explorable maps, crafting, building, kingdom progression, optional multiplayer and a separate live-wallpaper service.

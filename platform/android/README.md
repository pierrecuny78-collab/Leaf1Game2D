# Android native build

LeafEngine2D cible Android arm64-v8a en premier. Le projet utilise Android NDK + CMake et SDL2 comme couche native d'affichage et d'entrées ; aucune dépendance à Godot n'est requise.

Étapes d'intégration :
1. Ajouter SDL2 compilé pour Android comme dépendance CMake (source ou artefact épinglé).
2. Ajouter le point d'entrée SDL Android et l'Activity SDL.
3. Lier leafengine et SDL2 dans une bibliothèque native partagée.
4. Emballer l'Activity, les assets pixel art et les permissions minimales dans un APK.
5. Construire et signer l'APK de debug dans GitHub Actions.

La première cible de compilation est arm64-v8a, adaptée aux téléphones Android récents. L'APK ne doit être déclaré prêt qu'après installation et test sur appareil réel.

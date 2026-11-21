# LeafEngine2D

Moteur 2D dédié à Leaf1Game2D. Implémentation native C++17, Android via NDK et SDL2, rendu pixel-art basse résolution, logique de jeu indépendante de la plateforme.

## Principes
- boucle fixe de simulation et rendu séparés ;
- rendu basse résolution avec filtrage nearest-neighbor ;
- systèmes découplés : monde, entités, créatures, combats, inventaire, économie, sauvegarde ;
- données de jeu sérialisées et versionnées ;
- commandes tactiles Android et clavier/souris desktop ;
- fonctionnement solo hors ligne en priorité.

Le moteur est développé pour ce jeu, sans embarquer Godot ou Unity. SDL2 fournit uniquement la couche fenêtre/entrée/audio/rendu bas niveau ; les règles RPG et le moteur de scène sont LeafEngine2D.

## Build
Voir `platform/android/README.md`. La compilation APK sera activée après intégration de SDL2 et du SDK/NDK Android dans le pipeline.

# Leaf1Game2D

**RPG 2D de collection et de combats — version 0.3.0**

Leaf1Game2D est un RPG fantasy 2D développé avec Godot 4.4.1. Son orientation évolue vers un jeu de collection de monstres, d'éclosion et de combats tactiques au tour par tour, avec une expérience hors ligne et une sauvegarde locale.

## Direction artistique et progression

- Direction artistique pixel art intégrale, avec rendu basse résolution et agrandissement net par pixels.
- Progression accélérée : 500 pièces au départ, +100 pièces et +35 XP par œuf, +150 pièces et +120 XP par victoire ; les gains d'expérience accordent aussi des pièces bonus.

## Fonctionnalités

- Exploration en vue de dessus d'une prairie, village, étang et zones sauvages.
- Commandes tactiles Android et clavier PC (WASD/ZQSD ou flèches).
- Six œufs à découvrir et faire éclore.
- Sept créatures originales avec éléments, points de vie, attaques et compétences.
- Collection de créatures et équipe de combat pouvant accueillir trois compagnons.
- Combats au tour par tour : attaque, compétence spéciale, changement de compagnon et fuite.
- PV persistants des compagnons pendant l'exploration et les combats.
- Trois Slimes sauvages à affronter, récompenses en pièces et expérience.
- Progression du joueur, quête du guide et sauvegarde locale.
- Export APK Android automatisé par GitHub Actions.

## Jouer

Ouvrir le dossier avec Godot 4.4.1 puis lancer `scenes/main.tscn`.

Sur PC, se déplacer avec WASD/ZQSD ou les flèches. Sur Android, utiliser le pavé directionnel. Approcher un œuf et toucher **INTERAGIR**, puis **ÉCLORE**. Les trois premiers compagnons rejoignent automatiquement l'équipe. Approcher un Slime et toucher **INTERAGIR** pour lancer un combat. Choisir **ATTAQUE**, **COMPÉTENCE**, **CHANGER** ou **FUIR**.

## Construction Android

Le workflow `.github/workflows/build-android.yml` compile l'APK à chaque push sur `main`. Quand la compilation réussit, récupérer l'artefact **Leaf1Game2D-Android-APK** dans GitHub Actions.

L'APK debug est destinée aux tests et n'est pas une version signée pour publication sur le Play Store.

## Feuille de route

- **0.3.x** : équilibrage des combats, gestion manuelle de l'équipe, inventaire et soins.
- **0.3.x** : habitats, nourriture, élevage et reproduction des créatures.
- **0.4.x** : éléments et résistances, compétences avancées, boss et donjons.
- **Versions suivantes** : nouvelles îles, coopération en ligne facultative et compagnon fond d'écran animé Android indépendant.

## Principes

Gratuit, sans abonnement, hors ligne en priorité. Les fonctionnalités connectées resteront facultatives.

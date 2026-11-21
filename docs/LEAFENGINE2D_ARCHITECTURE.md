# LeafEngine2D — Architecture technique initiale

## Objectif
Moteur natif C++17 spécialisé dans le RPG pixel art Leaf1Game2D. SDL2 est limité à la couche plateforme ; toutes les règles et systèmes de jeu appartiennent au moteur Leaf.

## Modules prévus
- Core : boucle fixe 60 Hz, horloge, événements et gestion de scènes.
- Render : surface 480x270, sprites, tilesets, caméra, animations et nearest-neighbor.
- Input : tactile multipoint, manette, clavier et souris.
- World : cartes en tuiles, collisions, objets ramassables, transitions de zones.
- Creatures : base de données, raretés, éléments, statistiques, œufs, éclosion, évolution.
- Battle : combat au tour par tour, compétences, statuts, boss et récompenses.
- Kingdom : bâtiments, habitats, production hors ligne plafonnée, améliorations.
- Economy : pièces, matériaux, récompenses et coûts équilibrés.
- Save : sauvegarde locale versionnée, écriture atomique et migration.
- Audio : musique et effets sonores, volumes réglables.
- Network (phase ultérieure) : protocole client/serveur, comptes facultatifs, matchmaking et synchronisation ; jamais nécessaire au mode solo.

## Règles de conception
- Aucun système ne doit dépendre de l'interface graphique.
- Simulation à pas fixe, rendu indépendant du framerate.
- Toutes les ressources visuelles doivent être du pixel art original.
- Mode hors ligne complet ; réseau désactivé par défaut.
- Aucune monétisation pay-to-win ni attente artificielle.

## État
Socle C++ initial créé. Le port Android SDL2, la persistance disque, le moteur de scènes complet et l'APK restent à intégrer et valider.

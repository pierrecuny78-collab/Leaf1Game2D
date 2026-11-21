# Leaf1Game2D — Conception de la première édition

**Document de conception — édition V1**

## 1. Vision

Leaf1Game2D est un RPG 2D mobile et PC qui réunit cinq boucles complémentaires : explorer, collectionner, bâtir, combattre et survivre. Le jeu possède son univers, ses créatures, ses personnages, ses cartes et son identité graphique. Les jeux cités comme références servent uniquement à étudier des mécaniques de genre ; aucun personnage, visuel, nom, texte, interface ou contenu protégé ne doit être repris.

Priorités : Android en premier, Windows/Linux ensuite ; jeu jouable hors ligne ; sauvegarde locale ; gratuit, sans abonnement ; commandes tactiles et clavier ; progression équilibrée sans achat obligatoire.

## 2. Piliers de gameplay

### A. Exploration et aventure
- Monde en zones reliées : Prairie des Origines, Forêt des Murmures, Côte des Brumes, Monts Cendrés et Ruines Astrales.
- Récolte de bois, pierre, plantes, minerais, nourriture et fragments mystérieux.
- Coffres, œufs cachés, personnages non-joueurs, quêtes principales et missions secondaires.
- Donjons à salles, vagues d'ennemis, mini-boss et boss avec motifs d'attaque lisibles.
- Points de voyage débloqués pour réduire les déplacements répétitifs.

### B. Collection et élevage
- Créatures originales réparties en éléments : Nature, Feu, Eau, Air, Terre, Lumière et Ombre.
- Raretés : commune, inhabituelle, rare, épique, légendaire et mythique.
- Œufs à trouver, acheter avec monnaie gagnée en jeu ou obtenir en récompense ; incubation avec durée de jeu, sans attente obligatoire bloquante.
- Créatures avec statistiques, rôle, passif, deux compétences actives et progression.
- Habitats adaptés aux éléments ; capacité augmentée par amélioration.
- Nourrir, entraîner, faire évoluer et fusionner des doublons selon des recettes explicites.
- Reproduction débloquée plus tard, avec héritage contrôlé des éléments et traits ; aucune créature indispensable ne doit dépendre du hasard seul.

### C. Royaume et gestion
- Île centrale personnalisable avec bâtiments plaçables sur une grille.
- Bâtiments de départ : cœur du royaume, habitat Nature, ferme, scierie, carrière, atelier, infirmerie et tour de garde.
- Production hors ligne plafonnée et calculée au retour du joueur ; aucune perte punitive de ressources lorsque le joueur est absent.
- Améliorations consommant ressources et pièces, avec prévisualisation du coût et du résultat.
- Défense de l'île par tours, pièges et gardiens ; attaques scénarisées d'ennemis contrôlées par le jeu.
- Les bâtiments ont une fonction réelle et un affichage clair de leur état.

### D. Combats tactiques
- Équipe de trois créatures au départ, extension de la réserve et choix manuel des combattants.
- Combat au tour par tour : attaque, compétence, défense, objet, permutation et fuite lorsque permise.
- Ordre d'action fondé sur la vitesse ; énergie ou temps de recharge pour éviter le spam des compétences.
- Tableau élémentaire avec avantages et résistances modérés, jamais de victoire automatique par élément.
- Rôles : attaquant, défenseur, soutien, contrôle et soigneur.
- Effets : brûlure, poison, ralentissement, bouclier, soin et affaiblissement.
- Récompenses : expérience, pièces, matériaux et parfois fragments ou œufs.

### E. Défense, cartes et survie
- Défis de défense en vagues avec placement de tours et compagnons.
- Cartes de compétences et reliques à collectionner, améliorer et équiper ; les cartes sont un système complémentaire, pas un second jeu obligatoire.
- Expéditions de survie dans des zones dangereuses : endurance, nourriture, collecte et fabrication d'équipement.
- La survie reste accessible et ne détruit jamais la progression permanente du royaume ou de la collection.

## 3. Boucle de progression de la V1

1. Le joueur arrive sur l'île et restaure le cœur du royaume.
2. Il explore la prairie, récolte des ressources et trouve ses premiers œufs.
3. Il fait éclore deux ou trois créatures et compose une équipe.
4. Il construit des habitats et des bâtiments de production.
5. Il accomplit des quêtes et remporte des combats pour gagner des matériaux.
6. Il améliore ses créatures et son royaume pour ouvrir la forêt.
7. Il termine un premier donjon et affronte le gardien de zone.
8. Il débloque ensuite de nouveaux biomes et systèmes de progression.

## 4. Contenu cible de la première édition jouable

- 1 île centrale et 3 zones d'aventure.
- 12 à 18 créatures originales réparties sur les éléments de départ.
- 8 bâtiments fonctionnels minimum, dont 3 habitats ou bâtiments de production.
- 3 familles d'ennemis, 3 mini-boss et 1 boss de fin de première région.
- 1 donjon rejouable, 1 défi de défense en vagues et une suite de quêtes.
- Inventaire, fabrication simple, équipement de base et cartes de compétences.
- Sauvegarde locale versionnée et migration depuis les sauvegardes du prototype.
- Interface tactile adaptée aux petits écrans, menus lisibles et commandes PC.

Ces quantités sont des objectifs de contenu pour la V1 complète, pas des fonctionnalités déjà livrées.

## 5. Économie et équité

Monnaies et ressources : pièces, bois, pierre, nourriture, minerais et fragments. Chaque source et chaque dépense doivent être visibles. Les ressources de base se récoltent en jeu ; les récompenses ne doivent pas être exclusivement aléatoires. Les minuteries sont facultatives ou accélérables en jouant. Pas de publicité imposée, abonnement, énergie payante, coffre payant ni mécanisme pay-to-win.

## 6. Interface

- HUD compact : santé, niveau, expérience, pièces et objectif courant.
- Navigation principale : Aventure, Royaume, Créatures, Équipe, Inventaire et Missions.
- Fiches de créatures avec statistiques, élément, rôle, compétences, niveau et habitat.
- Écrans de construction avec coût, prévisualisation et bouton d'annulation.
- Menus tactiles de grande taille ; raccourcis clavier sur PC.
- Paramètres : volume, langue, qualité graphique, taille de l'interface et sauvegarde.

## 7. Architecture technique

Godot 4.4.1 et GDScript. Séparer progressivement les systèmes en modules : World, CreatureDatabase, CreatureRoster, BattleSystem, KingdomSystem, EconomySystem, QuestSystem, InventorySystem, SaveSystem et UI. Les données de créatures, bâtiments, objets et ennemis doivent être définies dans des ressources ou fichiers de données plutôt que dispersées dans l'interface.

La sauvegarde doit inclure un numéro de version, les ressources, les bâtiments, la collection, l'équipe, les créatures, les quêtes, les zones débloquées et les paramètres. Toute migration doit préserver les anciennes parties.

## 8. Phases de réalisation

- **Fondation** : corriger la construction Android, stabiliser l'exploration, les combats et les sauvegardes.
- **V1 - Royaume** : île centrale, inventaire, récolte, bâtiments, habitats et production.
- **V1 - Collection** : gestion manuelle de l'équipe, progression, compétences, raretés et élevage initial.
- **V1 - Aventure** : zones, quêtes, donjon, vagues et boss.
- **V1 - Finition** : graphismes originaux, animations, sons, tutoriel, équilibrage et tests sur Android/PC.

Le multijoueur, les classements en ligne, les guildes, le PvP asynchrone et le fond d'écran animé restent des extensions ultérieures. Le mode solo hors ligne demeure complet.

## 9. Critères d'acceptation

- Le jeu démarre et reste jouable hors ligne sur Android.
- Une sauvegarde peut être fermée puis rechargée sans perte.
- Le joueur peut récolter, construire, gérer son équipe et lancer un combat.
- Les coûts, récompenses, statistiques et effets sont cohérents et visibles.
- Une partie ancienne est migrée sans effacer la collection.
- Les contrôles et panneaux restent utilisables sur un écran de téléphone.
- La compilation APK et les validations du projet passent dans l'intégration continue.

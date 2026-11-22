# LeafStudio2D — Système universel de données et sauvegardes joueur

Chaque projet de jeu créé, importé ou exporté par LeafStudio2D doit embarquer le système de données standard. Le modèle est inspiré des mondes/profils Minecraft : données du monde séparées des données individuelles de chaque joueur, fichiers éditables et réimportables.

## Structure obligatoire dans chaque ZIP de jeu

```
<Jeu>.leafgame.zip
  project.json
  main.py
  scripts/
  scenes/
  assets/
  data/
    world.json
    players/
      player_001.json
    profiles/
      default.json
    saves/
      autosave.json
      backup.json
    settings.json
```

Les fichiers templates fournis dans `templates/game-data/` sont ajoutés lors de la création d'un projet. Si un projet importé ne possède pas encore ces données, LeafStudio2D propose de les initialiser sans écraser les données déjà présentes.

## Séparation des responsabilités

- `world.json`: état global du monde, jour, météo, graine, régions débloquées, boss vaincus et événements.
- `players/<player_id>.json`: niveau, XP, santé, mana, monnaie, inventaire, équipement, compétences, quêtes, position et temps de jeu d'un joueur.
- `profiles/<profile>.json`: emplacement de profil/slot de sauvegarde, nom, paramètres de partie et référence au joueur/monde.
- `saves/autosave.json`: checkpoint cohérent et état runtime sérialisable.
- `saves/backup.json`: copie précédente restaurable.
- `settings.json`: préférences propres à ce jeu.

## Règles de sauvegarde

1. Le ZIP sur carte SD est le format persistant officiel. Aucun fichier de jeu ne doit être conservé comme copie permanente dans le stockage interne.
2. L'éditeur charge le contenu du ZIP sélectionné dans une session de travail temporaire en mémoire.
3. Toute sauvegarde écrit un nouveau ZIP temporaire dans le dossier SD autorisé, valide l'archive, puis remplace l'ancienne archive de manière sûre (ou crée une nouvelle version si le fournisseur SAF ne permet pas le remplacement atomique).
4. Conserver une sauvegarde précédente dans l'archive, avec rotation configurable.
5. Le jeu peut déclencher une sauvegarde automatique selon les paramètres. En mode ZIP-only, l'autosave met à jour l'archive sur SD ; si la carte est absente ou inaccessible, avertir et conserver la session non sauvegardée en mémoire jusqu'à rétablissement.
6. Valider JSON et les types, limiter les tailles, refuser les chemins ZIP absolus ou contenant `..`, empêcher les doublons de chemins et limiter le nombre d'entrées / le volume décompressé.
7. Écrire les fichiers avec UTF-8. Ne jamais exécuter le contenu d'un fichier de données comme du code.
8. Les scripts accèdent aux données via une API contrôlée (`player.get/set`, `world.get/set`, `save()`) et non via un accès libre au système de fichiers.

## Profils multiples

- Créer, renommer, dupliquer, sélectionner, exporter et supprimer des profils.
- Chaque profil référence son propre joueur et son monde.
- Un joueur ne peut pas modifier les données d'un autre profil sans action explicite de l'utilisateur.
- Avant modification manuelle, créer une sauvegarde restaurable.
- Après édition JSON, afficher les erreurs de validation avant d'appliquer les changements au runtime.

## Éditeur de données

Un écran « Données & Profils » doit afficher les fichiers du projet, proposer une vue formulaire simple et une vue JSON texte avancée. L'utilisateur peut modifier niveau, XP, santé, pièces, inventaire, équipements, quêtes, boss et zones, enregistrer puis recharger le profil.

## Téléchargements et imports

Tout ZIP téléchargé/importé doit être inspecté. Si les fichiers standards sont absents, proposer « Ajouter les données joueur Leaf » et créer les modèles manquants. Ne jamais remplacer un fichier de sauvegarde déjà présent. Les nouveaux jeux créés reçoivent ces fichiers dès leur création.

## Critères d'acceptation

- Un nouveau jeu contient tous les fichiers standards.
- Un ZIP téléchargé sans profils peut être initialisé sans écraser les ressources du jeu.
- Deux profils restent indépendants.
- Modifier le niveau dans le JSON puis recharger affiche le nouveau niveau.
- L'autosave survit à la fermeture/réouverture de l'application via l'archive SD.
- Exporter puis importer le ZIP restaure monde, joueurs, profils et progression.
- Si la carte SD est absente, aucune fausse confirmation de sauvegarde n'est affichée.

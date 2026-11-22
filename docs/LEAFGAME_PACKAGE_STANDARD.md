# LeafGame — standard de paquet de jeu

Version du standard : 1.0 (proposition d'architecture)

Chaque jeu LeafStudio2D est un paquet ZIP autonome `.leafgame.zip`. La carte SD choisie via Android Storage Access Framework est l'emplacement persistant unique des projets et des sauvegardes. La mémoire interne ne sert pas de stockage permanent des projets. Une session peut utiliser la mémoire vive pendant l'édition ou l'exécution, mais toute sauvegarde confirmée doit écrire dans le ZIP sur la carte SD.

## Structure recommandée

```
MyGame.leafgame.zip
  leafgame.json                 # manifeste, identifiant et versions
  project.json                  # nom, moteur, scène de départ
  main.py                       # entrée optionnelle du jeu
  scripts/                      # logique de jeu
  scenes/                       # mondes et scènes
  assets/
    sprites/
    tilesets/
    audio/
    fonts/
    ui/
    effects/
  data/
    world.json                  # état du monde
    players/player_001.json     # stats par joueur
    profiles/default.json       # emplacement de sauvegarde
    saves/autosave.json         # checkpoint
    saves/backup.json           # sauvegarde précédente
    settings.json               # réglages du jeu
    inventory.json              # objets et œufs
    quests.json                 # quêtes
    achievements.json            # succès
    analytics.json              # statistiques locales
    mailbox.json                # récompenses/messages
    multiplayer.json            # configuration réseau facultative
  localization/fr.json
  localization/en.json
  mods/                         # extensions optionnelles
  docs/                         # aide du jeu
  LICENSES/                     # licences des ressources
```

## Fonctionnalités du standard

### Progression et données
- Plusieurs profils indépendants, sauvegardes manuelles et automatiques, restauration et export.
- Statistiques du joueur : niveau, XP, santé, mana, monnaie, inventaire, équipement, compétences, quêtes, position, temps de jeu.
- Monde : graine, calendrier, météo, zones, constructions, boss, événements et état des PNJ.
- Objets, œufs, incubation, rareté, éclosion, collection, compagnons et évolution.
- Succès, trophées, objectifs quotidiens, récompenses, courrier et journal d'activité.

### Personnalisation et confort
- Paramètres audio, graphismes pixel-art, commandes tactiles/manette, difficulté, langue et accessibilité.
- Tutoriel, aide intégrée, crédits, licences et informations de version.
- Galerie de captures et replays uniquement si le jeu active ces fonctions.

### Jeu et contenu
- Scènes, cartes, tuiles, collisions, animations, particules, météo, cycle jour/nuit.
- Ennemis, boss, butin, artisanat, recettes, compétences, arbres de talents et économie.
- Donjons, vagues, événements temporaires et génération procédurale si déclarés dans le manifeste.

### Multijoueur optionnel
- Le paquet décrit les capacités et la configuration, mais ne contient jamais de serveur privé ou de secrets.
- Le jeu reste jouable hors ligne si le mode offline est déclaré.
- Comptes, matchmaking, salons, amis, guildes, chat et synchronisation nécessitent un backend séparé et une authentification sûre.
- Les données reçues du réseau sont validées côté serveur ; les récompenses, monnaie et progression compétitives ne doivent pas faire confiance au client.
- Aucun identifiant secret, mot de passe ou jeton d'accès ne doit être stocké dans le ZIP.

### Extensions
- Les mods sont désactivés par défaut, listés dans un manifeste et isolés par permissions.
- Une extension ne peut accéder qu'aux API autorisées par le moteur.
- Les scripts et fichiers importés sont des données non fiables : pas d'exécution arbitraire ni d'accès libre au système de fichiers.

## Compatibilité et validation
- Chaque JSON contient `schema_version`.
- Le manifeste précise `format_version`, `engine_api_min`, `entry_scene`, `capabilities` et `required_files`.
- À l'import, vérifier les chemins, les doublons, le nombre d'entrées, la taille totale décompressée et les JSON.
- Refuser chemins absolus, `..`, liens symboliques et fichiers dépassant les limites configurées.
- Ne jamais écraser une sauvegarde existante lors de l'initialisation d'un ancien jeu.
- Les fonctionnalités optionnelles sont déclarées dans `capabilities`; ne pas afficher comme actives les fonctions qui ne sont pas réellement implémentées dans l'APK.

## Politique ZIP et carte SD
- Le ZIP est le format persistant de projet et de sauvegarde.
- Les changements sont appliqués à une nouvelle archive temporaire sur la carte SD, puis vérifiés avant remplacement de l'ancienne archive.
- Si la carte SD est indisponible, l'application affiche clairement « non sauvegardé » et ne prétend pas avoir enregistré.
- Conserver une archive de secours ou une entrée de backup interne au ZIP selon les paramètres du projet.
- Le sélecteur SAF conserve l'autorisation de dossier via `takePersistableUriPermission`.

## Statut d'implémentation
Ce document définit le format cible. La présence d'un fichier modèle ne signifie pas que l'APK sait déjà l'utiliser. Chaque capacité doit être implémentée dans le runtime, testée et marquée disponible seulement après validation.

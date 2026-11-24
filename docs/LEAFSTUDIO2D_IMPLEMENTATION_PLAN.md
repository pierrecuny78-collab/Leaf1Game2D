# LeafStudio2D — plan d'intégration exécutable

Ce plan distingue les fonctions réellement présentes du cahier des charges cible. Ne pas annoncer une capacité comme disponible tant que son test d'acceptation n'est pas passé sur Android physique.

## État constaté dans le code actuel
- MainActivity crée encore le projet prototype sous `getFilesDir()/LeafProjects/FirstGame` : ce n'est pas conforme au choix « carte SD + ZIP uniquement ».
- `saveAll()` écrit les fichiers dans ce répertoire interne.
- Le bouton Exécuter ouvre GameActivity; le validateur indique lui-même que l'interpréteur LeafScript n'est pas intégré. Un fichier main.py n'est donc pas exécuté.
- Les réglages sont enregistrés dans SharedPreferences internes et certains ne sont pas consommés par les composants correspondants.
- GameRuntimeService existe pour le mode automatique, mais cela ne constitue pas un WallpaperService Android.
- Les modèles JSON et la documentation LeafGame sont des spécifications/templates; leur présence seule ne signifie pas que le runtime les charge.

## Découpage de développement obligatoire

### P0 — persistance carte SD / ZIP
1. Ajouter un écran Bibliothèque de projets et un sélecteur SAF ACTION_OPEN_DOCUMENT_TREE.
2. Persister l'autorisation URI avec takePersistableUriPermission; vérifier les droits au démarrage et signaler carte absente.
3. Importer un fichier .leafgame.zip avec ACTION_OPEN_DOCUMENT.
4. Valider l'archive contre Zip Slip, chemins absolus, entrées dupliquées, limites d'entrées, taille décompressée et compression anormale.
5. Charger les fichiers en session de travail, sans créer de copie persistante interne.
6. Sauvegarder en écrivant une nouvelle archive sur SD, vérifier son intégrité, puis remplacer l'ancienne de manière sûre. Si l'opération échoue, afficher « non sauvegardé ».
7. Gérer les catégories et métadonnées de bibliothèque dans un index exporté sur la SD (pas dans SharedPreferences comme source de vérité).
8. Tester le retrait/réinsertion de la carte et les fournisseurs SAF différents.

### P1 — format LeafGame et données universelles
1. À la création/import, initialiser uniquement les fichiers standards absents : manifeste, world, players, profiles, saves, settings, inventory, quests, achievements, analytics, mailbox.
2. Ne jamais remplacer des données existantes.
3. Ajouter un éditeur JSON avec validation de schéma, aperçu des erreurs, sauvegarde de secours et rechargement du profil.
4. Implémenter une API runtime contrôlée pour player/world/inventory/quest/save.
5. Versionner les schémas et écrire des migrations idempotentes.

### P2 — runtime 2D
1. Définir une grammaire et une API LeafScript documentées; ne pas présenter Python comme exécutable tant qu'un runtime Python réel n'est pas embarqué.
2. Construire un chargeur de scènes, sprites, tilesets, collisions, animations et sons.
3. Ajouter boucle de jeu, événements tactiles, pause/reprise et console d'erreurs.
4. Exécuter les jeux dans un environnement restreint, sans accès arbitraire aux fichiers, au réseau ou aux API Android.

### P3 — fonctions Android
1. Le mode automatique doit transmettre l'identifiant du projet et utiliser les paramètres propres à ce projet.
2. Ajouter un vrai WallpaperService, déclaration BIND_WALLPAPER et écran de sélection du fond d'écran Android. Il doit charger le même paquet actif; ne pas confondre service de simulation et fond d'écran.
3. Notifications, économie hors ligne et autosave doivent respecter les options et l'état de permission Android.

### P4 — fonctions optionnelles
- Multijoueur: backend et protocole dédiés, authentification, autorité serveur pour les récompenses; le mode local reste disponible.
- Mods: API versionnée, liste blanche de permissions et isolation.
- Œufs/compagnons, boss, quêtes, succès, localisation et accessibilité: systèmes runtime, pas uniquement fichiers de données.

## Critères de sortie pour une APK « complète »
- Aucun projet permanent dans le stockage privé interne.
- Créer, importer, modifier, sauvegarder, fermer et réouvrir un ZIP depuis la carte SD sans perte.
- Plusieurs jeux et catégories isolés.
- Éditer un profil joueur et constater les valeurs dans le jeu après rechargement.
- Exécuter une scène réelle avec scripts et ressources du paquet sélectionné.
- Les réglages désactivent réellement les fonctions correspondantes.
- Le fond d'écran est sélectionnable dans Android et anime la scène du jeu choisi.
- Tests de sécurité ZIP, tests d'absence de carte et tests de régression sur appareil Android.

## Politique de publication
Chaque APK publiée doit indiquer le commit source, le numéro de version, les fonctions implémentées, les fonctions expérimentales et les tests passés. Une compilation réussie ne prouve pas que les fonctionnalités sont complètes.


## Mise à jour — éditeur visuel de scènes (23 novembre 2026)

Une première version interactive de l'éditeur visuel a été ajoutée dans `MainActivity`.

Fonctions réellement implémentées dans cette étape :
- ouverture et lecture de `scenes/world.leafscene` ;
- affichage d'une zone de scène 2D 480×270 avec grille ;
- création de nœuds `Rectangle2D` et `Sprite2D` (Sprite2D reste un rectangle de remplacement) ;
- sélection tactile et déplacement des nœuds ;
- suppression du nœud sélectionné ;
- sauvegarde explicite du tableau `root.children` dans le fichier de scène.

Limites actuelles :
- pas encore de chargement de textures depuis `assets/sprites` ;
- pas de redimensionnement, rotation, calques visuels, collisions éditables ni caméra ;
- l'aperçu intégré est un éditeur de données de scène, pas encore le runtime qui exécute le gameplay ;
- les scènes autres que `world.leafscene` ne sont pas encore sélectionnables depuis cet écran.

Validation à réaliser : compilation Android, ouverture de scène existante, ajout/déplacement/suppression, sauvegarde, fermeture/réouverture et contrôle de conservation des autres champs JSON.


## V2 — première tranche de mise en œuvre

- Icône Android LeafStudio2D ajoutée et référencée dans le manifeste.
- Manifeste Android normalisé avec de vrais retours à la ligne.
- Spécification de référence créée : `LEAFSTUDIO2D_VISION_UNIVERSELLE_V2.md`.
- Gestionnaire de projets : création guidée en quatre étapes (genre, nom, description, confirmation).
- À la création, génération d'une arborescence de projet autonome, de trois dossiers de profils, d'un manifeste `project.leaf`, d'un catalogue de ressources JSON vide et d'une scène initiale `world.leafscene`.

Validation restante : compilation Android du dernier commit, test de création depuis l'interface, fermeture/réouverture du projet, vérification de chaque fichier via le stockage SAF et test sur appareil réel.

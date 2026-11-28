# LeafStudio2D — cahier maître des fonctionnalités et plan de complétion

**Branche de travail :** `feature/leafstudio2d-0.4.0-workspace`  
**Périmètre :** LeafStudio2D uniquement. Le développement de Leaf1Game2D comme produit séparé reste en attente de demande explicite.  
**Statut :** cahier de référence consolidé ; les états ci-dessous distinguent code présent, base partielle, spécification et tests à faire.

## 0. Règles de vérité et de livraison

- Une entrée de menu n'est pas une fonctionnalité terminée.
- Un écran de démonstration n'est pas un moteur de jeu.
- La présence d'un JSON ne prouve pas que le runtime le lit ni que le jeu applique ses valeurs.
- Une compilation réussie prouve seulement que le paquet compile ; elle ne prouve ni l'installation sur Galaxy A15, ni le bon fonctionnement sur carte SD, ni la persistance.
- Une fonctionnalité passe à « terminée » seulement après : implémentation, build, test automatisé pertinent, test de persistance après fermeture/réouverture, test sur appareil réel si elle dépend d'Android, et documentation.
- Toute action destructive doit proposer une sauvegarde et demander confirmation.
- Ne jamais écraser les fichiers d'un projet existant pendant une initialisation, un import ou une migration.
- Les fonctions non implémentées doivent être désactivées ou clairement étiquetées « à venir », sans faux succès.

## 1. Vision produit et périmètre

LeafStudio2D est un atelier tactile de création de jeux 2D, pas seulement une bibliothèque d'APK. Il doit permettre de créer un jeu depuis zéro sans APK préexistante, de voir et modifier tous les fichiers du projet, d'en faire un aperçu puis de l'exécuter avec un runtime réellement compatible et enfin de produire un APK autonome lorsque la chaîne de build sera intégrée.

Les projets sont indépendants, portables, utilisables hors ligne et stockés dans l'emplacement choisi par l'utilisateur, avec priorité à la carte SD via Android Storage Access Framework (SAF). La source du jeu est le dossier projet/ZIP ; l'APK est un export exécutable, pas la source éditable.

### Types de jeux à couvrir par modèles extensibles
RPG, action-aventure, plateforme, puzzle, arcade, simulation, gestion, stratégie, tower defense, survie, exploration, jeu narratif, idle/incrémental, cartes/collection et genre personnalisé. Le moteur commun fournit les primitives ; les modèles ajoutent des systèmes et scènes sans créer un moteur distinct pour chaque genre.

## 2. Navigation et ergonomie demandées

### Accueil
- Interface simplifiée, fluide, avec peu de boutons et des espaces de travail regroupés.
- Bibliothèque APK accessible en haut de l'écran.
- Petit bouton carré « Exécuter » en haut de l'espace de travail, avec état clair (aperçu, runtime, APK externe).
- Accès aux projets, bibliothèque, préférences et ressources sans empiler des menus inutiles.
- Conserver une lecture claire des fichiers texte et scripts.

### Création LeafSwipe
Parcours horizontal gauche/droite, étape par étape, inspiré de LeafSwipe, avec retour et progression :
1. genre et modèle ;
2. nom, icône et description libre ;
3. univers, histoire, ambiance, époque et saisons ;
4. direction artistique, graphismes et audio ;
5. gameplay, commandes et règles ;
6. économie, ressources et statistiques ;
7. héros, personnages, ennemis, boss et PNJ ;
8. scènes, cartes, HUD et ScreenGUI ;
9. animations, effets, dialogues et cinématiques ;
10. plugins/événements, progression et récompenses ;
11. trois profils, stockage et plateformes ;
12. récapitulatif, structure du projet et plan de production.

La description doit pouvoir être exportée en cahier des charges, arborescence, liste d'assets, scripts à créer, tâches et tests. L'assistance IA est une aide de conception ; le projet doit rester créable manuellement et hors ligne.

### Vue d'un projet
- Vue simple par défaut et explorateur complet accessible en un geste.
- Actions : ouvrir, explorer, modifier, renommer, dupliquer, archiver, supprimer avec confirmation, sauvegarder, exporter ZIP, importer ZIP, ajouter à une playlist, exécuter/aperçu.
- Métadonnées : icône, nom, genre, description, version, emplacement, date, taille, état de sauvegarde et type (source, ZIP, APK).

## 3. Bibliothèque APK, playlists et stockage

- Bibliothèque APK en haut de l'accueil.
- Import des APK, métadonnées disponibles, icône/nom, version, package, taille et statut installé/importé.
- Playlists de jeux : créer, renommer, supprimer playlist ; ajouter/retirer un jeu ; un jeu peut figurer dans plusieurs playlists.
- Recherche, tri, favoris, catégories et détection des doublons.
- Distinguer projet source, ZIP, APK importée, APK compilée et application installée.
- Lancer une APK installée via Android ; proposer l'installation d'une APK importée avec confirmation système.
- Une APK seule ne fournit pas le code source. Pour modifier le jeu, ouvrir le projet source ou le ZIP correspondant. Ne pas promettre de décompiler une APK en projet fidèle.

### Carte SD / SAF
- Sélectionner un dossier racine avec `ACTION_OPEN_DOCUMENT_TREE`.
- Persister les permissions URI avec `takePersistableUriPermission`.
- Ne jamais coder en dur le chemin/ID de la carte.
- Structure racine suggérée : `Projects/`, `APK/`, `Playlists/`, `ZIP/`, `Saves/`, `Backups/`, `Library/`.
- Afficher carte absente, accès révoqué, stockage plein et erreurs d'écriture.
- Index de bibliothèque exportable et stocké sur l'emplacement choisi.
- Import/export ZIP avec protection Zip Slip, chemins absolus, doublons, limites d'entrées, taille décompressée et taux de compression.
- ZIP réimporté doit restaurer le projet sans perte.

## 4. Arborescence de référence d'un projet

```text
<NomDuJeu>/
  project.leaf
  README.md
  scenes/
    world.leafscene
  scripts/
    main.leaf
    gameplay/
    graphics/
    ui/
    systems/
    ai/
    quests/
    data/
  assets/
    icons/
    characters/
    enemies/
    bosses/
    environments/
    items/
    ui/
    spritesheets/
    audio/
  prefabs/
  animations/
  effects/
  gui/
    starter_gui/
    screens/
    widgets/
  data/
    game_settings.json
    resources.json
    economy.json
    characters.json
    items.json
    enemies.json
    quests.json
    events.json
    summon_tables.json
    profiles/
      profile_1.json
      profile_2.json
      profile_3.json
  saves/
    profile_1/
    profile_2/
    profile_3/
  plugins/
  exports/
  backups/
  logs/
```

Créer les fichiers standards absents uniquement. Conserver les fichiers inconnus et les champs JSON inconnus lors des modifications/migrations.

## 5. Éditeur de code et gestion de fichiers

### Éditeur intégré
- Ouvrir les fichiers texte connus ; conserver chemin, extension et encodage lorsque possible.
- Enregistrer, enregistrer sous, annuler/rétablir, rechercher/remplacer, indentation, coloration selon langage.
- État modifié/non enregistré visible ; sauvegarde de secours avant remplacement.
- JSON : validation réelle avec ligne/colonne et aperçu des erreurs.
- « Enregistrer » écrit le fichier courant, pas une liste ambiguë de scripts.
- « Vérifier » affiche diagnostics réellement effectués : fichiers manquants, JSON invalide, références cassées, assets absents, scripts non supportés. Ne pas prétendre exécuter LeafScript.
- « + Script » crée un fichier dans la catégorie sélectionnée, avec nom et extension valides.
- Ouvrir dans une application externe avec URI temporaire ; retour par action explicite de réimportation, comparaison et sauvegarde de l'original.
- Les formats non compris restent visibles et exportables, jamais réécrits arbitrairement.

### Scripts et automatisation
Catégories : gameplay, graphismes, interface, systèmes/statistiques, IA, quêtes/dialogues, données, événements et plugins.
- LeafScript : grammaire, API, runtime, erreurs et documentation à définir avant de prétendre exécuter le code.
- LeafVisualScript : blocs/nœuds, événements et liens vers scripts, à construire après le runtime minimal.
- Les scripts BAT/Shell sont des fichiers texte et modèles d'automatisation ; aucune exécution automatique. Exécution externe uniquement avec consentement, aperçu de commande, permissions minimales et journal.
- Ne pas embarquer une exécution arbitraire de Python ou de shell par défaut.

## 6. Éditeur de scènes et moteur 2D

### Éditeur de scène
- Multi-scènes, hiérarchie/nœuds, inspecteur de propriétés, duplication et suppression.
- Canvas 2D, grille, zoom/pan, caméra, calques et ordre d'affichage.
- Import et affichage effectif des PNG/JPG/WebP, atlas, spritesheets et audio.
- Transformations : position, dimensions, rotation, pivot, opacité, visibilité.
- Collisions et zones éditables, déclencheurs, liens entre scènes.
- Aperçu sur ratios d'écran différents.
- Sauvegarde non destructive du fichier de scène.

### Runtime requis
- Chargeur de manifeste, scènes, ressources et données.
- Boucle de jeu, rendu, entrées tactiles, pause/reprise, gestion du cycle de vie Android.
- Sprites/tilesets, caméra, collisions, animation, audio et événements.
- API de gameplay sécurisée et versionnée.
- Console d'erreurs et diagnostics.
- Exécuter la scène/le projet réellement sélectionné, jamais un écran de démonstration présenté comme le jeu.
- Runtime isolé : aucun accès arbitraire au réseau, aux fichiers ou aux API Android pour les scripts importés.

### Export APK
- Chaîne de compilation reproductible par projet, identifiant d'application configurable, assets, icône, permissions, version.
- Prévalidation des assets, références, tailles, permissions et erreurs.
- APK debug de test ; signature release séparée et procédure documentée.
- Export ZIP du projet indépendant de l'APK.
- Ne déclarer une cible desktop que si sa toolchain et ses tests sont disponibles.

## 7. ScreenGUI / interface de jeu

Éditeur inspiré de Roblox Studio et de StarterGui/ScreenGui :
- Arborescence d'écrans, panneaux, widgets, boutons, images, textes, barres, listes, fenêtres et popups.
- Placement tactile, déplacement, redimensionnement, alignement, ancrages, marges, ordre Z, visibilité.
- Propriétés, événements/actions et liaison de données.
- Modèles : HUD, inventaire, boutique, dialogues, notifications, pause, sélection de personnage, carte, profil et paramètres.
- Prévisualisation de plusieurs résolutions/orientations.
- Modèles communs disponibles pour chaque projet, mais chaque jeu peut les modifier indépendamment.
- Boutons UI doivent être réellement reliés aux actions du runtime.

## 8. Assets, icônes, LeafWallpaper, LeafNotes, LeafColour

### Assets et icônes
- Icône de l'application LeafStudio2D.
- Icône personnalisable par jeu/projet, import PNG/JPG/WebP, aperçu et variantes.
- Import d'images, spritesheets, audio et documents ; conserver nom/extension d'origine.
- Bibliothèque avec catégories, tags, recherche, favoris, prévisualisation et référence d'utilisation.
- Ne pas écraser les originaux ; doublons signalés.

### Intégrations Leaf*
Prévoir des adaptateurs/importeurs et formats d'échange plutôt que fusionner les applications indépendantes :
- **LeafWallpaper** : importer des fonds, thèmes, scènes ou packs compatibles ; utiliser les assets du projet pour prévisualisation/fond animé lorsque le service est réellement opérationnel.
- **LeafNotes** : ouvrir/créer des notes de conception, cahiers des charges, dialogues, lore, listes de tâches et documentation liées au projet ; stocker une copie portable dans le dossier projet ou utiliser un format d'échange documenté.
- **LeafColour** : réutiliser palettes, thèmes, couleurs, dégradés et préférences visuelles pour l'éditeur et les UI du jeu.
- Autres applications Leaf : intégration par plugin/adaptateur versionné, permissions explicites, import/export documenté. Aucun compte LeafBridge central requis.
Ces intégrations ne doivent pas être déclarées disponibles tant que les formats/API des applications concernées ne sont pas connus et testés.

## 9. Paramètres, données, économie et statistiques

Les préférences doivent être groupées en panneaux avec icônes et catégories, et chaque paramètre doit réellement agir sur le composant concerné. Les contrôles décoratifs sont interdits.

### Fichier game_settings.json
Doit permettre de régler les variables de jeu : numéro d'enveloppe, niveau, santé/max santé, XP, monnaie, énergie, production, progression, multiplicateurs, options de simulation et valeurs personnalisées. Chaque clé a type, valeur par défaut, limites et description.

### Ressources
Familles : pièces, billets, minerais, cristaux, potions, matériaux, expérience, énergie, réputation, fragments, clés, jetons, objets et catégories personnalisées.
Chaque ressource : ID stable, nom, description, icône, catégorie, quantité initiale, rareté, valeur, règles d'obtention/usage et améliorations.
- CRUD : créer, consulter, modifier, dupliquer, supprimer avec confirmation.
- Import/export JSON, validation et contrôle des doublons.
- Les valeurs de l'éditeur doivent être réellement lues par le runtime.

### Préférences
- Apparence et thèmes, palettes LeafColour.
- Paramètres de projet et runtime.
- Autosauvegarde, fréquence et rétention.
- Mode automatique, progression hors ligne et notifications.
- Fond d'écran animé.
- Contrôles, accessibilité et performances.
Chaque préférence affiche si elle est active, nécessite une permission, ou est à venir.

## 10. Trois profils, personnages et sauvegardes

- Exactement trois emplacements de profil par jeu, visibles à l'entrée du jeu sous forme de choix de personnages/cartes.
- Les trois profils sont indépendants : statistiques, monnaie, inventaire, personnages débloqués, héros sélectionné, quêtes, zones, succès et progression.
- Le catalogue de héros/PNJ/boss est commun au jeu ; déblocages et progression sont propres au profil.
- Écran de sélection des personnages façon jeu de rôle, avec portrait, nom, classe, niveau, statistiques et résumé de sauvegarde.
- Fichiers JSON modifiables pour statistiques, numéro d'enveloppe, monnaies et autres valeurs.
- Sauvegarde manuelle et automatique, restauration avec aperçu, export/import, horodatage et validation.
- Écriture atomique, copie de secours et récupération après arrêt.
- Un profil ne doit jamais écraser les deux autres ni les profils d'un autre jeu.

## 11. Personnages, animations, dialogues et effets

### Éditeur de personnages
- Héros, boss, PNJ, ennemis, compagnons.
- Fiche, statistiques, équipement, compétences, états, rareté et progression.
- Import de spritesheets, découpage, animations idle/marche/attaque/dégâts/mort et expressions.
- Timeline, images clés, vitesse, boucle, transitions et prévisualisation.
- Scènes d'introduction/cinématiques et caméra.

### Dialogues et UI/UX
- Arbre de dialogues, portraits, émotions, choix, conditions et conséquences.
- Sous-titres, localisation, événements déclencheurs et séquences.
- Liaison entre dialogue, animation du personnage et actions de scène.

### LeafVisualFX
- Attaques, projectiles, impacts, auras, particules, éléments, météo, lumière et transitions.
- Éditeur séparé avec paramètres, aperçu, sauvegarde en preset et insertion dans scène/compétence.
- Optimisation pour appareils Android modestes.

## 12. Ateliers RPG universels

### LeafCodex
Encyclopédie optionnelle intégrable dans tous les jeux : héros, boss, ennemis, objets, équipements, compétences, lieux, factions et collections.
Fiches avec image, description, histoire/lore, statistiques, faiblesses, récompenses, améliorations et conditions de découverte.

### LeafSummon
- Bannières configurables, personnages/objets invoquables, raretés et taux.
- Index visible des probabilités ; taux affichés identiques aux taux effectivement utilisés par le runtime.
- Historique des invocations, compteur de garantie/pity, règles de bannière et reset explicite.
- Tirage reproductible/testable, journal vérifiable, tests statistiques.
- Outil de simulation de tirages avant activation en jeu.
- Ne pas dissimuler les probabilités ni présenter une simulation comme un tirage réel.

### Économie et progression
- Améliorations de héros, statistiques et items ; coûts, matériaux, limites et historique.
- Inventaire, équipements, crafting, récompenses, quêtes, boss et progression.
- Systèmes activables par projet, pas imposés à tous les genres.

## 13. Plugins, événements et déclencheurs

- Gestionnaire de plugins avec manifeste, version, dépendances, compatibilité, permissions et origine.
- Import en quarantaine, validation, désactivation, rollback et journal.
- Permissions séparées : lire/écrire le projet, supprimer, exécuter, réseau, accès aux autres modules.
- LeafEventSystem : saisons, fêtes, événements de progression, événements par date/heure locale ou événement de jeu.
- Packs d'événement : visuels, sons, scripts, récompenses, dialogues et calendrier.
- Fonctionnement hors ligne via calendrier local ; pas de dépendance obligatoire à un serveur.
- Images générées/importées ou sélectionnées peuvent être liées à des déclencheurs, mais aucun asset externe n'est récupéré sans action explicite.
- Les plugins ne doivent pas pouvoir modifier un autre projet ou exécuter du code sans autorisation.

## 14. Automatisation et fond d'écran animé

Deux fonctions distinctes :
1. **Automatique** : simulation/progression d'un projet, liée à son ID et à son profil actif, avec pause, notification si nécessaire, sauvegarde et paramètres effectifs.
2. **LeafWallpaper** : vrai service Android WallpaperService, déclaré et sélectionnable par le sélecteur Android. Il doit charger la scène, sprites et animations du projet sélectionné.
Android conserve le contrôle du fond d'écran : l'application ne peut pas forcer silencieusement son activation ou sa désactivation. L'arrêt d'une application en arrière-plan ne doit pas corrompre les données ; les opérations persistantes doivent utiliser les mécanismes Android adaptés.

## 15. Architecture, performance et sécurité

- Architecture modulaire : UI, projet/SAF, modèle LeafGame, éditeurs, runtime, export, plugins.
- Chargement progressif, cache borné, libération des bitmaps, résolution d'aperçu adaptée.
- Mesures de mémoire, temps d'ouverture et fluidité sur Galaxy A15 4 Go RAM.
- Opérations longues hors UI thread, progression et annulation.
- Autosave récupérable, logs lisibles et diagnostic.
- Validation de chemins, JSON et ZIP ; prévention path traversal.
- Permissions minimales ; aucun réseau requis pour le cœur hors ligne.
- Pas de promesse d'exécution continue : Android peut suspendre/arrêter l'application.

## 16. Plan de réalisation priorisé et dépendances

### P0 — livraison fiable et données sûres
1. Stabiliser build, manifestes, navigation et état réel des fonctionnalités.
2. SAF SD persistant pour projets, bibliothèque, APK, playlists, ZIP, sauvegardes.
3. Format de projet et migrations sans perte.
4. Import/export ZIP sécurisé, autosave et récupération.
5. Trois profils réellement indépendants.
6. Bibliothèque APK et playlists avec distinction source/APK.
**Sortie P0 :** créer/importer/renommer/supprimer avec sauvegarde, réouvrir sans perte depuis carte SD.

### P1 — création et édition productive
1. Assistant LeafSwipe complet.
2. Explorateur simple/complet, éditeur intégré et ouverture externe avec réimportation.
3. Assets, icônes, catalogue de ressources CRUD et JSON.
4. Éditeur de scènes avec sprites réellement chargés, hiérarchie et inspecteur.
5. ScreenGUI editor.
**Sortie P1 :** créer un petit jeu sans APK et modifier les scènes, assets, UI et données.

### P2 — runtime exécutable
1. Spécifier/implémenter LeafScript ou système de logique contrôlé.
2. Charger manifeste, scènes, ressources et scripts.
3. Boucle, entrées, collisions, animation, audio, événements, pause/reprise.
4. Console, diagnostics, tests et isolation.
**Sortie P2 :** le bouton Exécuter lance le projet courant, pas une démo.

### P3 — création de contenu avancée
1. Éditeur personnages/spritesheets/animations.
2. Dialogues, cinématiques, effets.
3. LeafCodex, LeafSummon, économie et améliorations.
4. Plugins et événements saisonniers.
5. Adaptateurs LeafWallpaper, LeafNotes, LeafColour et autres.
**Sortie P3 :** modules optionnels réutilisables et validés dans au moins un projet exemple.

### P4 — distribution et validation
1. Génération APK autonome, icône, identifiant, permissions et version.
2. ZIP portable et documentation.
3. Tests automatisés et tests réels Galaxy A15/carte SD.
4. Vérification d'installation, lancement, sauvegardes, reprise et performance.
5. Livraison avec commit source, changelog, artefacts et limites connues.

## 17. Matrice de tests d'acceptation

- Créer deux projets sur SD, redémarrer, retrouver chacun et le projet actif.
- Renommer/dupliquer/archiver/supprimer avec confirmation et sauvegarde.
- Importer/exporter ZIP et comparer l'arborescence/empreinte des fichiers.
- Rejeter ZIP avec `../`, chemin absolu, entrées dupliquées et taille excessive.
- Importer PNG/JPG/WebP/MP3/OGG ; vérifier nom, extension et affichage réel.
- Créer/éditer/supprimer une ressource ; fermer/réouvrir et vérifier JSON + runtime.
- Modifier game_settings et vérifier que le runtime applique les statistiques.
- Modifier les trois profils ; prouver qu'aucune valeur ne fuit entre profils/projets.
- Créer un personnage, sélectionner un profil, afficher le bon portrait et la bonne progression.
- Créer une scène avec sprite ; enregistrer, rouvrir et exécuter la même scène.
- Créer un ScreenGUI et vérifier boutons/actions en runtime.
- Tester animations, dialogues, effets et événements déclenchés.
- Vérifier les taux LeafSummon affichés et utilisés avec tests de distribution.
- Activer/désactiver le mode automatique et vérifier ses effets sur le profil choisi.
- Sélectionner LeafWallpaper dans Android et vérifier le rendu de la scène active.
- Révoquer/retirer la carte SD : signaler clairement l'état et éviter toute perte silencieuse.
- Tester arrêt forcé, rotation écran, reprise, stockage presque plein et mémoire faible.
- Installer l'APK sur Galaxy A15 et tester parcours principal tactile hors ligne.

## 18. État constaté au moment de cette consolidation

### Présent dans la branche (à revalider par build et tests)
- Application Android LeafStudio2D, icône et écran d'accueil.
- Gestionnaire de projets et création guidée initiale.
- Import d'icône personnalisée de projet PNG/JPG/WebP ajouté dans la branche.
- Première interface de scène avec création/déplacement/suppression de nœuds de base documentée.
- Éditeur de scripts/fichiers et catégories de travail déjà amorcés.
- Formulaire de création de ressource ajouté ; catalogue JSON.
- Fichiers initiaux de trois profils et paramètres de jeu ajoutés dans la génération de projet.
- Bibliothèque APK distincte et aperçu de jeu de démonstration.
- Workflow CI qui construit les APK du jeu et de l'éditeur et prépare un ZIP source.

### Partiel / non prouvé
- Accès SAF complet et persistance réelle sur carte SD pour toutes les données.
- Playlists persistantes, import/export ZIP complet et sécurisé.
- Trois profils utilisés par un écran de sélection et par le runtime.
- CRUD complet de toutes les données et application effective des paramètres.
- Éditeur de scène avec chargement réel des textures, inspecteur et collisions.
- Wallpaper service chargeant la scène du projet actif.
- Exécution réelle de scripts et gameplay depuis un projet.
- Export d'un projet utilisateur en APK autonome.
- Intégrations concrètes LeafWallpaper, LeafNotes, LeafColour.

### Non implémenté / à construire
- Assistant LeafSwipe complet en 12 étapes.
- ScreenGUI editor complet.
- Runtime LeafScript/LeafVisualScript complet.
- Éditeur complet personnages, animations, effets et cinématiques.
- LeafCodex et LeafSummon complets.
- Système plugins/événements saisonniers sécurisé et opérationnel.
- Chaîne d'export APK par projet et tests sur appareil physique.

Ce statut est un état de dépôt et de documentation, pas une certification de fonctionnement. Chaque ligne doit être mise à jour après inspection du code et tests.

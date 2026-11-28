# LeafStudio2D — Vision universelle V2

Statut : spécification de référence / développement progressif  
Cible initiale : Android (téléphone et tablette), avec priorité à une utilisation tactile et hors ligne.

## 1. Objectif

LeafStudio2D est l'atelier central de création de jeux 2D. Il doit permettre de créer rapidement des genres variés (RPG, plateforme, gestion, stratégie, simulation, aventure, tower defense, survie et projets personnalisés) sans imposer une architecture spécifique à un genre.

Le moteur commun fournit les fondations. Les modèles, composants, plugins et outils spécialisés sont activables projet par projet. Chaque jeu exporté reste autonome.

## 2. Principes d'expérience utilisateur

- Accueil épuré : Projets, Bibliothèque APK, Préférences.
- Action Exécuter accessible dans un petit bouton carré en haut de l'éditeur.
- Création guidée en étapes horizontales avec flèches gauche/droite (inspiration LeafSwipe).
- Éviter les écrans saturés de boutons : privilégier des espaces de travail, panneaux contextuels et icônes cohérentes.
- Conserver l'affichage lisible des fichiers texte et scripts, avec coloration et validation.
- Navigation tactile, sauvegarde explicite et autosauvegarde récupérable.
- Interface réactive : opérations longues en tâche de fond, progression visible et reprise après fermeture.

## 3. Parcours LeafSwipe / LeafGameBlueprint

1. Genre et modèle de départ.
2. Description libre du jeu et objectifs.
3. Univers, histoire, ambiance, époque et saisons.
4. Direction artistique et ressources visuelles.
5. Gameplay et règles.
6. Économie, ressources et statistiques.
7. Personnages, ennemis, boss et PNJ.
8. Interfaces (HUD, menus, ScreenGUI, sélection de personnage).
9. Sons, musique, dialogues et cinématiques.
10. Profils, sauvegardes, plateformes cibles.
11. Récapitulatif du plan de production et génération du squelette de projet.

La description libre doit pouvoir être transformée en cahier des charges, architecture, liste de scènes, scripts à produire, ressources nécessaires, tâches et tests. L'assistance IA est facultative ; le parcours doit rester utilisable hors ligne avec des modèles locaux.

## 4. Espaces de travail

### 4.1 Scene Editor
- Scènes multiples, hiérarchie de nœuds, inspecteur de propriétés.
- Sprites PNG/JPG/WebP, atlas et spritesheets.
- Calques, ordre d'affichage, transformation (position, taille, rotation, pivot).
- Caméra, zones, grille, alignement et aperçu.
- Collisions et formes de collision éditables.
- Prévisualisation du rendu et sauvegarde non destructive.

### 4.2 ScreenGUI Editor
- Arborescence inspirée de StarterGui / ScreenGui.
- HUD, menus, inventaire, boutique, dialogues, notifications, sélection de personnages.
- Placement tactile, ancrages, dimensions, marges, ordre, visibilité et actions.
- Prévisualisation sur plusieurs tailles d'écran.
- Les éléments communs sont des modèles copiables, chaque jeu peut les modifier.

### 4.3 Scripts et logique
- Éditeur texte avec coloration, recherche, indentation, validation et console d'erreurs.
- LeafVisualScript : programmation par nœuds/blocs et événements.
- Liaison explicite entre événements visuels et scripts.
- Débogage, points d'arrêt lorsque le runtime le permettra.
- Les scripts importés ne sont jamais exécutés sans permissions et validation.

### 4.4 Animation et effets
- Timeline et animation image par image.
- Spritesheets, clips, transitions, articulations 2D si supportées.
- Animations des héros, PNJ, boss, expressions et dialogues.
- LeafVisualFX : particules, impacts, auras, projectiles, éléments, lumière et météo.
- Séquences d'introduction et cinématiques.
- Prévisualisation dans un espace dédié puis intégration à la scène.

## 5. Bibliothèques communes

### LeafAssets / LeafIconManager
- Import PNG, JPG/JPEG, WebP et audio pris en charge.
- Icône d'application, icône de projet, couverture, icônes d'objets et personnages.
- Redimensionnement/recadrage et génération des variantes d'icônes.
- Bibliothèque locale, tags, recherche, favoris, attribution et aperçu.
- Ne pas écraser les fichiers sources lors de l'import.

### Ressources et données
Familles extensibles : monnaies, billets, minerais, cristaux, potions, matériaux, expérience, énergie, réputation, compétences, clés, fragments, jetons, collections et ressources personnalisées.

Chaque définition comprend au minimum un identifiant stable, nom, description, icône, type, valeur/quantité initiale, rareté, règles d'obtention et d'utilisation, et éventuelles améliorations.

Formats :
- JSON pour données structurées et statistiques.
- LeafScript pour logique portable du moteur.
- BAT/Shell seulement pour automatisations externes explicitement autorisées ; pas d'exécution aveugle depuis l'éditeur.

### LeafCodex
Encyclopédie intégrable aux jeux : héros, boss, ennemis, objets, équipements, compétences, lieux et collections. Fiches avec images, histoire, statistiques, faiblesses, récompenses et améliorations.

### LeafSummon
Système optionnel d'invocation : bannières, raretés, taux configurables, historique, garantie/pity et animations. Les probabilités affichées doivent être les mêmes que celles appliquées par le moteur ; journal de tirages vérifiable et configuration transparente.

### LeafEventSystem
Événements saisonniers et déclencheurs par date, progression ou événement de jeu. Packs isolés avec assets, scripts, récompenses et manifeste. Prise en charge hors ligne avec calendrier local configurable, sans dépendance obligatoire à un serveur.

## 6. Modèle de projet

Chaque projet reste indépendant et portable. Structure cible :

```text
MyGame/
  project.leaf
  scenes/
  scripts/
  assets/
    characters/
    enemies/
    environments/
    ui/
    icons/
    audio/
  prefabs/
  animations/
  effects/
  data/
    resources.json
    characters.json
    economy.json
  plugins/
  saves/
    profile_1/
    profile_2/
    profile_3/
  exports/
  backup/
```

Le schéma exact sera versionné. Les migrations doivent préserver les données inconnues et sauvegarder avant conversion.

## 7. Personnages, profils et sauvegardes

- Trois profils par jeu, affichés lors de l'entrée dans le jeu.
- Chaque profil possède sa progression, ses statistiques, inventaires, quêtes, monnaie et personnages sélectionnés.
- Le catalogue de personnages et leurs définitions appartiennent au jeu ; la progression/déblocage appartient au profil.
- Sauvegardes automatiques, manuelles, restauration et export/import.
- Autosave atomique, rotation de sauvegardes et récupération après arrêt inattendu.

## 8. Runtime et systèmes de jeu

Fondations communes : scènes, boucle de jeu, entrées tactiles, physique 2D, collisions, caméra, animation, audio, événements, navigation et sauvegarde.

Composants optionnels : personnages, ennemis, IA, quêtes, dialogues, inventaire, équipements, crafting, économie, construction, vagues, stratégie, génération de monde, multijoueur.

Chaque genre utilise des modèles et modules plutôt qu'un moteur séparé.

## 9. Bibliothèque APK et exécution

- Bibliothèque en haut de l'écran, avec icônes et recherche.
- Import APK, métadonnées, image de couverture, favoris et playlists.
- Distinguer les APK installées, importées et les projets sources.
- Une APK externe est lancée par Android ; LeafStudio ne prétend pas pouvoir inspecter son code source.
- Pour les projets natifs Leaf, afficher l'arborescence, fichiers, scripts, assets, ZIP, profils et aperçu.
- Revenir de l'exécution à l'éditeur sans perdre le contexte.

## 10. Performance, stockage et arrière-plan

- Chargement progressif, cache borné, libération des ressources et aperçu à résolution adaptée.
- Stockage de projets volumineux sur carte SD via Storage Access Framework (SAF), sans supposer de chemin absolu.
- Autosave et journal de récupération.
- Travaux longs via mécanismes Android adaptés et notifications si nécessaire.
- Android peut suspendre ou arrêter une application ; ne pas promettre une exécution permanente en arrière-plan.
- Mesurer mémoire, temps de rendu et temps d'ouverture sur appareils modestes.

## 11. Sécurité des plugins et scripts

- Manifeste, version, dépendances et permissions par plugin.
- Permissions minimales : lecture/écriture projet, accès réseau, exécution, accès à d'autres modules.
- Confirmation pour actions sensibles.
- Import en quarantaine, validation des chemins et prévention du path traversal.
- Désactivation/récupération si plugin défaillant.
- Les scripts de build externes ne sont pas exécutés automatiquement.

## 12. Export

- Export ZIP portable du projet.
- Export APK Android avec signature de développement et instructions de signature de publication.
- Cibles Windows/Linux/macOS uniquement lorsque les toolchains et tests correspondants sont disponibles.
- Vérifier les assets manquants, permissions, identifiants, taille et erreurs avant export.
- Ne jamais annoncer une plateforme comme prise en charge sans build et test.

## 13. Plan de réalisation et critères de validation

### Phase A — Fiabilité et identité
- Icône Android, manifestes propres, navigation simplifiée.
- Build reproductible et tests d'installation/lancement.

### Phase B — Création guidée
- Assistant multi-étapes, description, genre, modèle et création du projet.
- Vérifier qu'un projet vierge et chaque modèle génèrent un dossier valide.

### Phase C — Données et ressources
- Import d'images, icônes personnalisées, ressources JSON et éditeur de données.
- Vérifier import, modification, sauvegarde et réouverture.

### Phase D — Interface et scènes
- ScreenGUI, scène, hiérarchie, inspecteur, transformations, spritesheets.
- Vérifier persistance et rendu sur plusieurs dimensions.

### Phase E — Runtime et gameplay
- Boucle d'exécution, entrées, collisions, animations, événements, scripts.
- Tests fonctionnels et stabilité.

### Phase F — RPG et contenu avancé
- Personnages, trois profils, inventaire, LeafCodex, LeafSummon, événements et effets.

### Phase G — Distribution
- Bibliothèque APK, ZIP, APK signée, tests sur appareils et documentation.

## 14. Règle de livraison

Une fonctionnalité est « terminée » seulement si elle est implémentée, compilée, testée, persistante après fermeture/réouverture et documentée. Les maquettes et spécifications ne sont pas présentées comme des fonctions disponibles. Chaque livraison indique clairement les fonctions terminées, partielles et restantes.

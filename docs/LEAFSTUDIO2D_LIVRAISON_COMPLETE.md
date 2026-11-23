# LeafStudio2D + Leaf1Game2D — cible de livraison complète

**Décision produit :** une seule livraison utilisateur consolidée. Les jalons intermédiaires servent au développement, à l'intégration et aux tests ; ils ne constituent pas des versions à installer successivement.

**Dépôt :** Leaf1Game2D  
**Branche de travail :** feature/leafstudio2d-0.4.0-workspace

## 1. Objectif

Faire de LeafStudio2D un atelier de création de jeux 2D RPG sur Android, capable de gérer des projets sur carte SD, de créer et modifier les ressources d'un jeu, de lancer un aperçu/runtime réellement pris en charge, puis de préparer des exports installables. Leaf1Game2D sert de jeu de démonstration et de validation des fonctions de l'éditeur.

Le produit doit rester utilisable hors ligne après installation. Aucune fonction ne doit dépendre d'un abonnement.

## 2. Règle de livraison unique

- Ne pas demander à l'utilisateur d'installer chaque jalon technique.
- Garder une branche de développement intégrée et un historique de commits.
- Produire une livraison candidate uniquement après intégration des ateliers, du runtime, des sauvegardes et des exports.
- La livraison finale comprend : APK LeafStudio2D, APK Leaf1Game2D, ZIP complet des sources/projets, notes de version, guide d'installation et liste des fonctions validées.
- Si une fonction n'est pas implémentée ou testée, la présenter comme indisponible/expérimentale, jamais comme terminée.

## 3. Éditeur visuel 2D

### Scènes et monde
- Création, duplication, renommage et suppression contrôlée de scènes.
- Éditeur de scène avec zone de travail, zoom, déplacement, grille et aimantation.
- Ajout, sélection, déplacement, rotation, redimensionnement et suppression d'objets.
- Hiérarchie des nœuds, groupes, visibilité et verrouillage.
- Calques de fond, terrain, objets, personnages, effets et interface.
- Tilemap, atlas de tuiles, pinceau, gomme, remplissage, sélection rectangulaire et autotuiles.
- Caméra, limites de monde, zones de transition et points d'apparition.
- Collisions simples, formes, couches et masques.
- Annuler/rétablir et sauvegarde de scène sans perte.

### Inspection et aperçu
- Inspecteur de propriétés adapté au type d'objet.
- Aperçu de scène et aperçu de jeu clairement identifiés.
- Console d'erreurs, validation des ressources manquantes et références cassées.
- Le mode aperçu ne doit pas être présenté comme une exécution complète si le runtime ne prend pas en charge une fonctionnalité.

## 4. Ressources et ateliers

- Bibliothèque d'images, sprites, tilesets, polices, sons, musiques et vidéos compatibles.
- Import multi-fichiers, prévisualisation, recherche, tags, catégories et détection de doublons.
- Découpe de spritesheets, animations image par image, durée des frames, boucles et points d'ancrage.
- Effets visuels, particules, transitions et filtres dans la limite du moteur intégré.
- Gestion des fichiers originaux : ne pas convertir ou écraser silencieusement les sources.

### Ateliers RPG
- Héros, personnages jouables, classes, races et statistiques.
- Compétences, arbres de talents, états, buffs/debuffs et progression.
- Armes, armures, accessoires, raretés, améliorations et enchantements.
- Inventaires, coffres, butin, monnaies, magasins et échanges.
- Ressources, récolte, plantes, minerais, matériaux et drops.
- Artisanat, recettes, ateliers et améliorations.
- Compagnons, familiers, montures et recrutement.
- Ennemis, élites, boss, phases, attaques et tables de butin.
- Quêtes, objectifs, dialogues, choix, PNJ et relations.
- Biomes, cartes, donjons, bâtiments, décorations et objets interactifs.
- Bestiaire, codex, succès, journal, carte et encyclopédie.
- Chaque entrée doit pouvoir être créée, modifiée, supprimée avec confirmation, référencée dans une scène et validée.

## 5. Runtime et logique

- Runtime 2D local, documenté et versionné, réellement capable de charger les scènes et ressources du format projet.
- Boucle de jeu, entrées tactiles et clavier, déplacements, animation et caméra.
- Système d'événements, signaux, déclencheurs et interactions.
- Logique visuelle en blocs/nœuds avec connexions validées.
- Système de scripts uniquement si un interpréteur embarqué est livré, testé et documenté.
- Physique 2D, collisions, zones, raycasts et navigation dans la mesure prise en charge.
- Combat RPG, dégâts, résistances, états, IA et comportement de boss.
- Pause, paramètres, audio, sauvegarde et chargement.
- Console, erreurs compréhensibles et outils de diagnostic.
- Aucune action « Exécuter » ne doit simuler une exécution si le runtime ne la réalise pas.

## 6. Données et sauvegardes joueur

Chaque jeu possède exactement trois profils indépendants par défaut.
- Identité du profil, temps de jeu, niveau, XP et progression.
- Statistiques, compétences, équipement, inventaire et monnaie.
- Héros recrutés, compagnons, quêtes, succès et zones débloquées.
- Paramètres du joueur et état du monde nécessaires à la reprise.
- Sauvegardes manuelles, automatiques et sauvegardes de secours.
- Aperçu avant restauration, copie/export et validation de l'intégrité.
- Schémas versionnés avec migrations non destructives.
- Isolation stricte entre les trois profils et entre les différents jeux.

## 7. Stockage, bibliothèque et transfert

- Stockage permanent sur l'emplacement choisi, carte SD privilégiée via Android Storage Access Framework.
- Ne jamais coder en dur le chemin/identifiant du volume amovible.
- Index de bibliothèque exportable, recherche, tri, tags et playlists de jeux.
- Import/export ZIP avec validation des chemins, limites de taille, collisions et fichiers corrompus.
- Préserver les fichiers existants ; demander confirmation avant toute opération destructive.
- Sauvegardes ZIP avant migration ou remplacement.
- Gestion claire de la carte retirée, de l'autorisation révoquée et du manque d'espace.
- Formats de projet documentés et indépendants de l'emplacement physique.

## 8. Export et compatibilité

- APK Android : génération, vérification du manifeste, signature de test et installation avec confirmation Android.
- Export PC : fournir seulement les cibles dont le moteur, les templates et la chaîne de compilation ont été intégrés et validés.
- ZIP source complet avec ressources, scènes, scripts, données, profils de démonstration et documentation.
- Les APK et les projets sources sont des livrables différents.
- Afficher les plateformes prises en charge, les limites et les versions de runtime.

## 9. Expérience Android

- Interface adaptée au tactile et aux écrans de téléphone, avec panneaux repliables.
- Barre d'outils accessible, navigation simple et explorateur avancé.
- Fonctionnement hors ligne ; téléchargement de dépendances uniquement pendant la préparation de compilation si nécessaire.
- Gestion mémoire et prévisualisations adaptées aux appareils Android modestes.
- Éviter de copier les gros projets dans le stockage interne : travailler via les URI SAF lorsque possible.
- Reprise après fermeture et messages d'erreur utiles.

## 10. Validation avant livraison

### Tests automatiques
- Compilation Android des deux applications.
- Tests unitaires du format projet, import/export ZIP, profils, sauvegardes et migrations.
- Tests du runtime sur scènes de référence.
- Vérification des fichiers APK et du ZIP final.
- Analyse des ressources manquantes, références invalides et erreurs de données.

### Tests d'acceptation sur appareil
- Choisir un dossier de carte SD, fermer/réouvrir l'application et retrouver le projet.
- Importer, modifier, sauvegarder, exporter et réimporter un projet.
- Créer une scène, y placer des ressources et lancer le runtime pris en charge.
- Créer deux jeux et vérifier leur isolation.
- Modifier les trois profils d'un jeu et vérifier leur indépendance.
- Retirer/réinsérer la carte SD et tester les autorisations révoquées.
- Vérifier les APK sur Android cible et mesurer les erreurs au démarrage.
- Tester les cas de fichiers volumineux, doublons, archives invalides et espace insuffisant.

## 11. État honnête des fonctions

Le dépôt contient déjà une base Android de workspace, un explorateur/éditeur de fichiers, des ateliers de données RPG, une gestion initiale des profils, l'import/export ZIP et une chaîne CI de compilation. Cela ne constitue pas encore un éditeur visuel complet ni un runtime RPG complet.

La suite du travail doit remplacer les prototypes de données par des outils réellement interactifs, intégrer le format de scène et le runtime, puis valider les exports. La livraison ne sera annoncée comme « complète » qu'après ces validations.

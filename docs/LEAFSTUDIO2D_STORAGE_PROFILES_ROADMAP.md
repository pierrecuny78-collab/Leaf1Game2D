# LeafStudio2D — Stockage, profils et exécution du projet

## Diagnostic de la version actuelle

La version actuellement présente dans le dépôt ne stocke pas les projets sur la carte SD. `MainActivity` initialise `projectDir` avec `getFilesDir()/LeafProjects/FirstGame`. Le projet est donc dans l'espace privé interne de LeafStudio2D.

L'éditeur écrit les fichiers de sa collection `files` dans ce dossier. Le modèle actuel contient notamment `scripts/main.leaf`, `scenes/world.leafscene` et `project.leaf.json`. Le bouton Exécuter ouvre `GameActivity`, qui dessine un aperçu pixel-art de démonstration : il ne lit ni n'interprète encore le script `main.leaf`.

Il n'existe pas encore de gestionnaire de catégories, d'export ZIP, d'import ZIP, de service WallpaperService ni de véritable système de profils de sauvegarde. Les interrupteurs de paramètres sont partiellement décoratifs : plusieurs options ne sont pas consommées par le moteur.

## Objectif produit

Chaque jeu doit être un projet autonome, identifié par un ID stable, stocké dans un dossier choisi par l'utilisateur sur la carte SD. Le projet sélectionné dans la bibliothèque est l'unique source de vérité pour l'éditeur, le runtime, le mode automatique et le fond d'écran.

## Emplacement et autorisation SD

Ne pas coder en dur le chemin de volume amovible (ex. identifiant de carte SD variable). Utiliser Android Storage Access Framework :

1. L'utilisateur choisit « Emplacement des projets ».
2. Ouvrir `ACTION_OPEN_DOCUMENT_TREE`.
3. L'utilisateur choisit ou crée un dossier `LeafStudio2D` sur sa carte SD.
4. Conserver l'URI du dossier et les droits persistants lecture/écriture avec `takePersistableUriPermission`.
5. Créer dans ce dossier `Projects/`, `Exports/` et `Backups/`.
6. Afficher l'emplacement choisi et son état (accessible / carte absente / autorisation à renouveler).

Android peut interdire la sélection de la racine de la carte SD sur certaines versions/fabricants. L'application doit donc demander de sélectionner un sous-dossier autorisé, sans supposer un chemin absolu.

## Format d'un projet

Chaque projet possède un dossier de travail éditable :

```text
LeafStudio2D/
  Projects/
    <project-id>/
      project.leaf.json
      main.py
      scripts/
      scenes/
      assets/
      data/
        profiles/
          default.json
      saves/
      logs/
  Exports/
    <project-name>.leafgame.zip
  Backups/
```

Le ZIP d'export contient le dossier complet du projet, pas seulement le script principal. Il inclut les scènes, ressources, fichiers de données, profils et manifeste. Il exclut les caches, fichiers temporaires, secrets et journaux volumineux. Le ZIP doit pouvoir être réimporté sans perte.

## Catégories et bibliothèque

- Créer, renommer et supprimer des catégories.
- Créer, renommer, dupliquer, archiver et supprimer des projets.
- Affecter un projet à une ou plusieurs catégories.
- Rechercher et trier les projets.
- La suppression d'un projet demande confirmation et propose une sauvegarde ZIP avant suppression.
- La bibliothèque affiche le nom, l'icône, la catégorie, la date de modification et l'emplacement.

## Profils de données éditables

Inspiré du principe des mondes Minecraft, chaque projet expose des profils JSON lisibles et modifiables dans un éditeur de texte intégré. Exemple :

```json
{
  "profile": "default",
  "level": 12,
  "experience": 450,
  "gold": 2500,
  "gems": 40,
  "unlocked_areas": ["village", "forest"],
  "party": []
}
```

L'utilisateur peut modifier les valeurs, enregistrer puis demander « Recharger le profil ». Le moteur valide le JSON, contrôle les plages autorisées et conserve une copie de sauvegarde avant toute modification. Les profils ne doivent jamais être modifiés silencieusement pendant que le jeu les écrit.

## Exécution et sélection active

- La sélection d'un projet définit `activeProjectId`.
- Run, Auto et Wallpaper reçoivent explicitement cet ID et son URI/dossier.
- Le runtime charge le manifeste, la scène d'entrée, les scripts et les ressources de CE projet.
- Un changement de projet arrête proprement le runtime précédent ou demande confirmation.
- Le mode automatique est un choix explicite par projet.
- Le fond d'écran animé doit être un vrai `WallpaperService` déclaré avec `BIND_WALLPAPER`, qui lit le projet actif et dessine sa scène. Un simple lancement d'Activity ou un service de simulation ne crée pas un fond d'écran.
- Les scripts non fiables doivent être interprétés dans un environnement restreint, sans accès arbitraire aux fichiers, au réseau ou aux composants Android.

## Paramètres effectifs

Chaque paramètre doit être lu par le composant concerné et afficher un état réel. Les options prévues :
- démarrage automatique à la sélection ;
- simulation en arrière-plan ;
- progression hors ligne ;
- sauvegarde automatique ;
- notification de simulation ;
- fond d'écran animé ;
- production automatique.

Une option non encore implémentée doit être désactivée et marquée « À venir », jamais présentée comme fonctionnelle.

## Critères d'acceptation

1. Choisir une fois le dossier SD, fermer et rouvrir l'application : l'accès reste valable.
2. Créer deux projets et vérifier qu'ils ont des données et catégories indépendantes.
3. Exporter un projet en ZIP, le supprimer puis le réimporter : les scripts, scènes, ressources et profils sont restaurés.
4. Modifier `data/profiles/default.json`, enregistrer et recharger : le niveau et les ressources affichent les nouvelles valeurs.
5. Sélectionner le projet B : Run, Auto et Wallpaper ne doivent jamais charger les fichiers du projet A.
6. Le fond d'écran n'est déclaré disponible que lorsque le vrai service Android est installé, sélectionnable et capable de dessiner la scène du projet.

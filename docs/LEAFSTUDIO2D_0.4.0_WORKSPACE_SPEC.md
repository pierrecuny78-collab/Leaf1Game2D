# LeafStudio2D 0.4.0 — spécification de l'espace de travail

**Statut :** préparation de l'évolution  
**Branche :** `feature/leafstudio2d-0.4.0-workspace`  
**Version cible :** `0.4.0-alpha`  
**Périmètre :** LeafStudio2D uniquement. Aucun changement du gameplay de Leaf1Game2D n'est autorisé dans cette évolution.

## 1. Décisions validées

- Ouverture d'un projet avec deux niveaux complémentaires : **vue simple** par défaut et **explorateur complet** accessible en un geste.
- Modification du code avec deux possibilités : **éditeur intégré** et **application externe**.
- Les projets, APK, archives ZIP, profils et sauvegardes permanentes résident sur l'emplacement choisi par l'utilisateur, en priorité la carte SD.
- Un jeu possède exactement **trois profils joueur indépendants**.
- Une APK est un livrable exécutable ; elle n'est pas considérée comme un projet source modifiable.

## 2. Principes non négociables

1. Ne jamais écraser ni initialiser à nouveau un fichier de jeu déjà présent.
2. Ne jamais annoncer qu'un script est exécutable si son interpréteur n'est pas réellement intégré.
3. Ne jamais confondre aperçu graphique, lancement d'une APK installée et exécution du projet source.
4. Ne jamais enregistrer un projet permanent dans le stockage privé de l'application.
5. Avant une opération destructive, proposer une sauvegarde ZIP et demander confirmation.
6. Un échec d'écriture sur la carte SD doit être visible : l'état reste « non enregistré ».
7. Les données d'un jeu et celles de ses trois profils restent strictement séparées des autres jeux.

## 3. Architecture de navigation

### Vue simple — ouverture par défaut

La fiche d'un jeu affiche uniquement les actions courantes :

- Ouvrir le projet
- Explorer les fichiers
- Profils (1, 2, 3)
- Modifier
- Ouvrir dans une application externe
- Exécuter / lancer l'APK
- Exporter ZIP
- Sauvegarder

Afficher le nom, la vignette, la version, le type de paquet (source ZIP ou APK), l'emplacement, la date de modification et l'état de sauvegarde.

### Explorateur complet

Arborescence navigable avec dossiers repliables, recherche, tri, taille et type de fichier. Prévisualisation des images, sons, scènes, JSON et scripts lorsque le format est pris en charge.

Arborescence de référence (adaptée au contenu réel du projet ; ne pas créer artificiellement tous les fichiers) :

```text
<Jeu>/
  project.leaf.json
  project.godot                 (si projet Godot)
  main.py                       (si fourni)
  scripts/
  scenes/
  assets/
  audio/
  data/
    profiles/
      profile_1.json
      profile_2.json
      profile_3.json
  saves/
    profile_1/
    profile_2/
    profile_3/
  backups/
  README.md
```

Les formats inconnus restent visibles et peuvent être copiés/exportés ; ils ne doivent pas être modifiés par un éditeur qui ne les comprend pas.

## 4. Stockage carte SD et format ZIP

- Utiliser Android Storage Access Framework (`ACTION_OPEN_DOCUMENT_TREE`) ; ne pas coder en dur l'identifiant du volume SD.
- Enregistrer l'URI du dossier et ses droits persistants de lecture/écriture.
- Créer sous le dossier choisi : `Library/`, `Projects/`, `Playlists/`, `APK/`, `ZIP/`, `Saves/`, `Backups/`.
- Garder l'index de bibliothèque et les playlists sous forme de fichiers exportables sur la carte SD.
- Importer les ZIP via le sélecteur Android, vérifier l'archive avant extraction, puis ouvrir une session de travail.
- Exporter le projet entier dans un nouveau ZIP ; vérifier l'archive avant de proposer de remplacer une version existante.
- Contrôler les chemins absolus, `..`, entrées dupliquées, nombre d'entrées, taille décompressée et taux de compression pour éviter Zip Slip et les archives piégées.
- Si la carte est retirée ou l'autorisation révoquée, afficher l'état et permettre de rétablir l'accès sans perdre silencieusement les données.

## 5. Bibliothèque et playlists

- Une seule fiche de bibliothèque par jeu ; un jeu peut appartenir à plusieurs playlists.
- Ajouter, renommer et supprimer une playlist sans supprimer le jeu.
- Ajouter/retirer un jeu d'une playlist.
- Rechercher et trier par nom, genre, date, version et type (APK/source ZIP).
- Détecter les doublons à partir de l'identifiant de projet et, à défaut, d'une empreinte de fichier.
- Conserver les versions APK originales et les exports compilés séparément.

## 6. Édition intégrée et externe

### Éditeur intégré

- Ouvrir un fichier texte pris en charge depuis l'explorateur.
- Enregistrer, enregistrer sous, annuler/rétablir, rechercher/remplacer.
- Afficher le chemin et l'état « modifié / enregistré ».
- Validation JSON avant sauvegarde des fichiers JSON ; signaler ligne et colonne de l'erreur.
- Sauvegarde de secours du fichier avant modification.
- Ne pas interpréter automatiquement un fichier arbitraire comme du Python, du GDScript ou du LeafScript.

### Application externe

- Action « Ouvrir dans une application externe » avec choix d'une application compatible.
- Transmettre une copie temporaire du fichier avec les droits URI nécessaires.
- Prévoir une action explicite « Réimporter la version modifiée » et comparer avant remplacement : le retour depuis une application externe ne garantit pas que le fichier a été modifié.
- Sauvegarder l'original avant remplacement et afficher les conflits si le fichier source a changé entre-temps.

## 7. Trois profils joueur par jeu

Chaque jeu expose trois emplacements nommés « Profil 1 », « Profil 2 » et « Profil 3 ». Chaque profil possède ses propres fichiers de progression et sauvegardes.

Données possibles selon le schéma du jeu : niveau, XP, statistiques, compétences, santé/mana, monnaie, inventaire, équipement, héros, compagnons, quêtes, boss, succès, zones débloquées, position, temps de jeu et paramètres.

- Sélectionner un profil avant le lancement du jeu.
- Ne jamais partager les fichiers de sauvegarde entre profils.
- Autosauvegardes, sauvegardes manuelles et restaurations séparées par profil.
- Prévisualiser les données avant restauration.
- Créer les fichiers standards absents sans écraser les données existantes.
- Versionner les schémas et migrer les données avec sauvegarde préalable.

## 8. Exécution : distinguer les cas

- **APK importée :** afficher les métadonnées et proposer de lancer l'application si elle est installée. L'installation reste une action Android distincte avec confirmation système.
- **ZIP source :** ouvrir le projet dans l'éditeur. L'exécution n'est disponible que si un runtime compatible est réellement présent.
- **Projet Godot :** afficher les fichiers et permettre l'édition de texte ; ne pas prétendre compiler Godot sans moteur/exporteur intégré.
- **Python / LeafScript :** afficher et éditer les sources ; ne proposer « Exécuter » que lorsque l'interpréteur correspondant et son API sont intégrés et testés.
- **Aperçu :** toujours étiqueter clairement un aperçu de démonstration comme tel.

## 9. Ordre de réalisation

### Étape A — fondations sûres
1. Sélecteur de dossier SD et autorisation persistante.
2. Index de bibliothèque sur SD.
3. Import/export ZIP sécurisé.
4. État de stockage et erreurs explicites.

### Étape B — navigation et édition
1. Vue simple.
2. Explorateur complet.
3. Éditeur texte intégré.
4. Ouverture externe avec réimportation contrôlée.

### Étape C — profils et bibliothèque
1. Trois profils isolés par jeu.
2. Gestion des sauvegardes par profil.
3. Playlists et recherche de bibliothèque.

### Étape D — validation et publication
1. Tests unitaires de ZIP, index, profils et migrations.
2. Tests Android physiques sur carte SD et plusieurs fournisseurs SAF.
3. Tests après fermeture/réouverture et retrait/réinsertion de la carte.
4. Publication APK debug + ZIP complet avec commit source, fonctions réellement disponibles et limites connues.

## 10. Tests d'acceptation obligatoires

- Choisir un dossier sur la carte SD, fermer l'application et retrouver les projets après redémarrage.
- Importer un ZIP, ouvrir sa vue simple, passer à l'explorateur complet et retrouver tous les fichiers.
- Modifier un fichier texte dans l'éditeur intégré, enregistrer, fermer et rouvrir sans perte.
- Ouvrir un fichier dans une application externe, le réimporter, comparer et confirmer le remplacement.
- Créer deux jeux et vérifier l'isolation totale de leurs données.
- Modifier les trois profils d'un même jeu et vérifier que les niveaux, inventaires et sauvegardes restent indépendants.
- Exporter puis réimporter un ZIP et vérifier l'intégrité des scripts, scènes, ressources et profils.
- Tester une archive malveillante, un manque d'espace, une carte retirée et une autorisation révoquée.
- Vérifier qu'une APK non installée n'est pas présentée comme directement exécutable depuis son fichier.
- Vérifier que les fonctions non encore implémentées sont désactivées ou marquées « À venir ».

## 11. Définition de « prêt »

La version 0.4.0-alpha ne doit être publiée comme évolution fonctionnelle que lorsque les tests correspondant aux fonctions annoncées sont passés. Une compilation Android réussie, seule, ne valide pas le stockage SD, l'édition externe, les profils ni le runtime.

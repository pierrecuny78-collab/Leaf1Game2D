# Retour utilisateur — LeafStudio2D

Ce document traduit les observations de l'utilisateur et les captures Android en exigences vérifiables.

## Décisions de conception

1. Un jeu doit être créable dans LeafStudio2D sans APK. Le projet (scènes, scripts, graphismes, données et sauvegardes) est la source de vérité ; l'APK est un export/une installation facultative.
2. Chaque jeu est stocké dans son propre dossier sous `LeafStudio2D/Projects/<NomDuProjet>`.
3. L'utilisateur peut créer, ouvrir, renommer, exporter et supprimer un dossier projet. La suppression doit demander confirmation.
4. Les scripts sont organisés en catégories : gameplay, graphismes, interface, systèmes/statistiques, IA, quêtes/dialogues et données.
5. Les graphismes/audio importés depuis le sélecteur Android doivent conserver leur nom et extension d'origine.
6. Les paramètres globaux du jeu sont persistés dans `game_settings.json`. Les statistiques et données joueur sont séparées par projet et par profil dans `data/profiles/profile_1.json` à `profile_3.json`.
7. L'automatisation de progression et l'affichage du jeu en fond d'écran sont deux fonctions différentes. Le service d'automatisation met à jour des données ; le Live Wallpaper est un service Android sélectionné par l'utilisateur via le sélecteur système.
8. Les contrôles « Enregistrer » et « Vérifier » doivent annoncer exactement ce qu'ils font. La vérification structurelle ne doit pas prétendre exécuter LeafScript.

## Corrections intégrées dans la branche de travail

- Ajout d'un gestionnaire de projets carte SD avec création, sélection, renommage et suppression confirmée.
- Sélection du projet actif persistée au lieu d'ouvrir systématiquement `FirstGame`.
- Ajout de catégories de scripts et de création de fichiers `.leaf` dans chaque catégorie.
- Ajout de `game_settings.json` avec numéro d'enveloppe, niveau, santé, expérience, monnaie, production et options de simulation.
- Correction du traitement du retour du sélecteur d'assets (code de requête 510).
- Import des assets avec conservation du nom et de l'extension.
- Simulation automatique reliée au fichier de configuration et au profil actif, avec mise à jour du temps de jeu et de la monnaie.
- Ajout d'un service Android Live Wallpaper Leaf World et de son entrée dans le sélecteur système.

## Limites à valider / développement restant

- Le fond animé actuel est un décor Leaf World autonome ; il ne charge pas encore les scènes, spritesheets et scripts du projet actif. L'intégration du rendu de chaque projet est une étape ultérieure.
- Android garde le contrôle du choix et de la désactivation d'un fond d'écran animé ; LeafStudio2D ne peut pas forcer silencieusement le changement de fond.
- Le moteur LeafScript n'est pas encore un interpréteur complet. « Vérifier » doit rester un diagnostic de structure/syntaxe disponible, et « Exécuter » doit rester désactivé ou clairement indiqué comme aperçu tant que le runtime ne sait pas charger le projet.
- L'éditeur de scènes visuel avec glisser-déposer, le navigateur de graphismes avec prévisualisation, les contrôles CRUD des ressources et la compilation de projet en APK autonome restent à intégrer.
- Les tests d'acceptation sur un appareil Android et une vraie carte SD sont requis ; une compilation CI seule ne prouve pas l'accès réel à tous les volumes externes.

## Tests d'acceptation requis

- Créer deux projets sur carte SD, redémarrer LeafStudio2D et retrouver le bon projet actif.
- Renommer un projet et vérifier que son contenu est intact ; supprimer un projet seulement après confirmation.
- Créer un script dans chacune des catégories et le retrouver dans l'explorateur.
- Importer PNG/JPG/WEBP/OGG/MP3 et vérifier que le nom et l'extension sont conservés.
- Modifier `game_settings.json` et chacun des trois profils ; vérifier que les projets et profils ne se mélangent pas.
- Activer la simulation automatique, attendre plusieurs cycles, puis vérifier que le profil actif évolue et que les deux autres restent inchangés.
- Ouvrir le sélecteur de fonds d'écran Android, appliquer Leaf World, revenir à l'écran d'accueil et vérifier l'animation ; choisir un autre fond pour désactiver Leaf World.
- Vérifier que les boutons non encore opérationnels sont clairement signalés et ne produisent pas de faux succès.

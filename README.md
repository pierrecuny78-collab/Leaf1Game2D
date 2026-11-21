# Leaf1Game2D

**RPG 2D hors ligne — version 0.1.1**

Leaf1Game2D est un jeu de rôle 2D fantasy développé avec Godot 4.4.1 et GDScript. Le prototype privilégie une expérience jouable hors ligne, avec sauvegarde locale et commandes adaptées au mobile.

## Fonctionnalités de la version 0.1.1

- Exploration en vue de dessus d'une prairie étendue.
- Déplacement tactile Android et clavier PC (ZQSD/WASD ou flèches).
- Village, guide, étang, arbres et œufs cachés.
- Six œufs à découvrir, chacun récupérable une seule fois.
- Éclosion aléatoire et collection de sept créatures possibles.
- Progression : niveaux, expérience, pièces et points de vie.
- Première quête du guide avec récompense.
- Premiers combats contre trois Slimes.
- Écran de collection et sauvegarde locale persistante.
- Export APK Android automatisé par GitHub Actions.

## Jouer

Ouvrir le dossier avec Godot 4.4.1 puis lancer `scenes/main.tscn`.

Sur PC, se déplacer avec WASD/ZQSD ou les flèches. Sur Android, utiliser le pavé directionnel. Approcher un œuf et toucher **INTERAGIR**. Utiliser **ÉCLORE** pour obtenir une créature. Approcher un Slime et toucher **ATTAQUER**. Parler au guide pour recevoir la première quête. Le bouton **COLLECTION** affiche les créatures et l'avancement de la quête.

## Construire l'APK

Le workflow `.github/workflows/build-android.yml` lance la compilation sur chaque push vers `main` et manuellement depuis l'onglet Actions.

Quand le workflow réussit, télécharger l'artefact **Leaf1Game2D-Android-APK** dans la page de l'exécution.

L'APK debug est destiné aux tests et n'est pas une version signée pour publication sur le Play Store.

## Feuille de route

- **0.1.x** : stabilisation, interface, audio, inventaire et équilibrage.
- **0.2.x** : compétences, équipement, boss et nouvelles zones.
- **0.3.x** : raretés des œufs, évolution et davantage de créatures.
- **Versions suivantes** : coopération en ligne facultative et compagnon fond d'écran animé Android indépendant.

## Principes

Gratuit, sans abonnement, hors ligne en priorité. La sauvegarde locale est le mode par défaut. Les services en ligne resteront facultatifs.

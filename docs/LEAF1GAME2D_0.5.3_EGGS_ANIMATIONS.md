# Leaf1Game2D 0.5.3 — Animations et éclosion

## Fonctionnalités intégrées

- Animation d'attente du personnage et des créatures par déplacement pixel-art vertical.
- Œuf animé avec oscillation, scintillement et particules lors d'une éclosion.
- Actions séparées : trouver un œuf (récompense de 100 pièces et 35 XP) puis faire éclore un œuf de l'inventaire.
- Quatre raretés : commune (60 %), rare (25 %), épique (12 %) et légendaire (3 %).
- Garantie épique ou légendaire au dixième œuf éclos.
- Une éclosion consomme exactement un œuf et ajoute une créature à la collection.
- Le compteur d'œufs éclos et la collection sont restaurés localement sur Android.
- Le runtime de test intégré à LeafStudio2D expose les actions trouver/éclore et le nombre de créatures.

## Contrôles de validation

1. Trouver un œuf : l'inventaire augmente de 1, les pièces de 100 et l'XP de 35.
2. Éclore sans œuf : aucune ressource ni créature ne change.
3. Éclore avec un œuf : l'inventaire diminue de 1, le compteur de collection augmente de 1 et l'effet visuel apparaît.
4. Le dixième œuf éclos garantit une rareté épique ou légendaire.
5. Fermer puis relancer le jeu conserve le nombre d'œufs éclos et la progression locale.

Les animations sont actuellement des animations procédurales du moteur natif. Les spritesheets 64×64, animations d'attaque/défense/évolution et interface complète du Pokédex restent des étapes ultérieures.

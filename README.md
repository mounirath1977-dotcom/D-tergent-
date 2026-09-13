# Annonces Mhalma (Alger) 📱📍

Application Android native développée avec **Kotlin** et **Jetpack Compose** pour les petites annonces locales réservées exclusivement aux habitants de la commune de **Mhalma (Alger)**.

## 🌟 Fonctionnalités Principales

- **📍 Géolocalisation Stricte (Mhalma, Alger)** :
  - L'accès et la publication sont conditionnés à la présence physique dans le périmètre de Mhalma (`36.6775° N, 2.8745° E`).
  - L'administrateur peut ajuster le rayon autorisé (en kilomètres).
  - Mode démo/simulation disponible pour les tests hors zone.
- **📸 Petites Annonces avec Photos** :
  - Publication avec titre, description, prix (DZD), contact téléphonique, quartier et photo.
  - Plafond limité à **20 annonces** actives au total sur la plateforme.
- **⏳ Suppression Automatique (7 jours)** :
  - Chaque annonce expire et est purgée automatiquement 7 jours après sa publication.
- **💬 Authentification & Confirmation WhatsApp** :
  - Création de compte avec numéro de téléphone et validation WhatsApp.
  - Gestion des annonces créées par l'utilisateur (suppression de ses propres annonces).
- **🛡️ Accès Administrateur Caché & Sécurisé** :
  - **Déclencheur secret** : Saisir `mounirath@yahoo.fr` dans le champ identifiant/nom fait apparaître le bouton d'accès Administrateur.
  - **Connexion Admin** : Identifiant `dziri-diou`, Mot de passe `212154`.
  - **Tableau de bord Admin** : Configuration du rayon GPS (km), modération et suppression d'annonces, bascule des publicités.
- **📢 Monétisation Publicitaire** :
  - Intégration de bannières publicitaires type Google AdMob (activable/désactivable depuis le panneau d'administration).

## 🛠️ Technologies Utilisées

- **Langage** : Kotlin
- **Interface UI** : Jetpack Compose (Material Design 3)
- **Architecture** : MVVM (Model-View-ViewModel) + StateFlow
- **Base de données Locale** : Room Database (SQLite avec migrations automatiques)
- **Services Android** : Location Services (FusedLocationProviderClient / GPS), Dynamic Permissions, Coroutines & Flow

## 🚀 Compilation & Exécution

### Prérequis
- Android Studio Ladybug ou version plus récente
- JDK 17 ou supérieur
- Android SDK 34 (Android 14)

### Commandes Gradle
```bash
# Compiler l'application en mode Debug
gradle assembleDebug

# Lancer les tests unitaires Robolectric
gradle :app:testDebugUnitTest
```

## 📦 Déploiement sur GitHub

Pour lier et pousser ce projet vers votre dépôt GitHub :

```bash
git branch -M main
git remote add origin https://github.com/mounirath/<nom-du-repo>.git
git push -u origin main
```

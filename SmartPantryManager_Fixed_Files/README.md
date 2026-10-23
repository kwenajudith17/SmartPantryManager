# Smart Pantry Manager

## Application Description

Smart Pantry Manager is a Java-based Android application designed to help users reduce food waste by managing ingredients available at home and suggesting recipes that can be prepared using those ingredients.

The application is intended to help users keep track of pantry items, their quantities and optional expiry dates, and identify recipes they can prepare without purchasing additional ingredients.

## Main Features

- Add, view, edit and delete pantry ingredients.
- Store pantry data locally.
- Browse a collection of recipes.
- Suggest recipes based on available ingredients.
- Display recipe ingredients and preparation instructions.
- Provide basic settings for the user.
- Display feedback when no recipes match the pantry.

## Technology Used

- **Programming language:** Java
- **Platform:** Android
- **Database:** SQLite
- **Version control:** Git and GitHub
- **Build automation:** GitHub Actions

## Database Choice

SQLite was selected because it is a lightweight database that can store information directly on an Android device. It does not require a separate database server and supports persistent local storage.

## Project Structure

- `app/src/main/java/` — Java application source code.
- `app/src/main/res/` — Android resources and styling.
- `app/src/main/AndroidManifest.xml` — Android application configuration.
- `app/build.gradle` — App module build configuration.
- `.github/workflows/` — Automated build workflow configuration.

## Setup Instructions

1. Download or clone this GitHub repository.
2. Open the project in Android Studio.
3. Allow Gradle to synchronise the project and download the required dependencies.
4. Connect an Android device with USB debugging enabled, or start an Android emulator.
5. Build and run the application.
6. Test the pantry management, recipe matching, recipe details and settings features.

The project build must complete successfully before the application can be installed and tested.

## Repository

https://github.com/kwenajudith17/SmartPantryManager


# Smart Pantry Manager

A Java Android application that helps reduce food waste by tracking pantry ingredients and suggesting recipes that can be made **strictly** from what you already have.

## Features
- Add / Edit / Delete pantry items (name, quantity, unit, optional expiry)
- SQLite local database (persists after app close)
- 15+ pre-seeded recipes
- **Strict matching**: a recipe only appears if every required ingredient is present in sufficient quantity
- Simple plural/singular name normalisation
- Suggested Recipes, All Recipes, Settings screens
- Built entirely in Java with programmatic UI

## Database Choice
**SQLite** – lightweight, fully on-device, no network required, matches the module content.

## Project Structure
```
SmartPantryManager-clean/
├── app/
│   ├── build.gradle
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/example/smartpantry/MainActivity.java
│       └── res/
├── build.gradle
├── settings.gradle
├── gradle.properties
└── README.md
```

## How to Build an APK (without Android Studio on your laptop)

### Option A – Use GitHub Actions (recommended if you have no space)
1. Push this cleaned project to your GitHub repository.
2. Go to the **Actions** tab → run the “Android CI” or “Android debug build” workflow.
3. Download the generated `app-debug.apk` artifact.
4. Transfer the APK to your phone (USB, email, Drive, etc.) and install it (enable “Install from unknown sources”).

### Option B – Build on any machine that has the Android SDK
```bash
# From the project root
./gradlew assembleDebug
# APK will be at: app/build/outputs/apk/debug/app-debug.apk
```

### Option C – Install via USB after building
1. On your phone: Settings → About phone → tap Build number 7 times to enable Developer options.
2. Enable **USB debugging**.
3. Connect phone via USB cable.
4. On a computer with `adb`:
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

## Requirements Met
- Java (not Kotlin)
- SQLite with full CRUD + persistence
- Strict recipe matching logic
- Multiple screens (Pantry, Suggested Recipes, All Recipes, Settings)
- Input validation
- Navigation between screens

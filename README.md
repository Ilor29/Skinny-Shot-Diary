# Pendia – GLP-1 Tagebuch

Android-App (APK) für Menschen, die eine GLP-1-Spritze nutzen (Typ-2-Diabetes oder Übergewicht). Deutsch und Englisch, ohne Konto, alle Daten bleiben auf dem Handy.

- [Marktrecherche v1](docs/recherche_v1.md)
- [Konzept v2](docs/konzept_v2.md) (aktuell), [v1](docs/konzept_v1.md)
- [Screenshots](docs/screenshots/)
- [Icon-Entwürfe](docs/icon/)

## APK holen

1. GitHub → **Actions** → „APK bauen“ → letzten Lauf öffnen
2. Unten unter **Artifacts** `pendia-apk` herunterladen und entpacken
3. `pendia-debug.apk` aufs Handy kopieren und öffnen („Installation aus unbekannten Quellen“ erlauben)

Das ist eine Test-APK. Für den Play Store braucht es später eine signierte Release-APK.

## Lokal bauen

```
./gradlew assembleDebug        # APK nach app/build/outputs/apk/debug
./gradlew testDebugUnitTest    # Tests, Screenshots nach app/build/screens
```

Hinweis: Pendia ersetzt keine ärztliche Beratung.

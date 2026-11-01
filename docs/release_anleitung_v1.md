# Pendia – Signierte Version bauen v1

Stand: 01.11.2026 · Version 1

Für den Play Store muss die App mit einem eigenen Schlüssel signiert sein. Den Schlüssel legst du einmal an und hinterlegst ihn als Secret in GitHub. Danach baut GitHub die signierte Version auf Knopfdruck.

**Wichtig:** Verlierst du den Schlüssel oder das Passwort, kannst du keine Updates mehr veröffentlichen. Bewahre die Datei und die Passwörter an zwei sicheren Orten auf, z. B. in einem Passwortmanager. Die Schlüsseldatei gehört **nie** ins Repository.

## 1. Schlüssel anlegen (einmalig, auf deinem Rechner)

Du brauchst Java (z. B. über Android Studio). Im Terminal:

```
keytool -genkeypair -v -keystore pendia-upload.jks -alias pendia -keyalg RSA -keysize 2048 -validity 10000
```

Das Programm fragt nach einem Passwort und nach deinem Namen. Merk dir:
- das Passwort für die Datei (Keystore-Passwort)
- den Alias: `pendia`
- das Passwort für den Schlüssel (darf dasselbe sein)

## 2. Datei in Text umwandeln

macOS / Linux:
```
base64 -i pendia-upload.jks | tr -d '\n' > pendia-upload.txt
```
Windows (PowerShell):
```
[Convert]::ToBase64String([IO.File]::ReadAllBytes("pendia-upload.jks")) > pendia-upload.txt
```

## 3. Secrets in GitHub hinterlegen

GitHub → Repository → **Settings → Secrets and variables → Actions → New repository secret**. Lege vier Secrets an:

| Name | Inhalt |
|---|---|
| `PENDIA_KEYSTORE_BASE64` | der komplette Text aus `pendia-upload.txt` |
| `PENDIA_KEYSTORE_PASSWORD` | Keystore-Passwort |
| `PENDIA_KEY_ALIAS` | `pendia` |
| `PENDIA_KEY_PASSWORD` | Schlüssel-Passwort |

Danach `pendia-upload.txt` löschen.

## 4. Signierte Version bauen

GitHub → **Actions → „Release bauen (signiert)“ → Run workflow**.
Nach ein paar Minuten liegt unter Artifacts `pendia-release` mit:
- `pendia-release.apk` – zum direkten Installieren
- `pendia-release.aab` – zum Hochladen in die Google Play Console

## 5. Neue Version

Vor jeder neuen Version in `app/build.gradle.kts` erhöhen:
- `versionCode` (Zahl, immer +1)
- `versionName` (z. B. `0.2.0`)

## 6. Google Play Console (Hinweise)

- **Play App Signing** einschalten. Dann ist dein Schlüssel nur ein Upload-Schlüssel und kann bei Verlust über Google ersetzt werden.
- **Datensicherheit (Data safety):** Pendia erhebt und teilt keine Daten. Alles bleibt auf dem Gerät, die App hat keine Internet-Berechtigung.
- **Gesundheits-Apps-Deklaration:** Kategorie „Medikamenten- und Behandlungsmanagement“. Kein Medizinprodukt.
- **Datenschutzerklärung:** muss als Webadresse angegeben werden, Entwurf siehe `docs/datenschutz_v1.md`.
- **Zielgruppe:** Erwachsene (18+).
- Vorher: Markenrecherche „Pendia“ (DPMA, EUIPO, USPTO).

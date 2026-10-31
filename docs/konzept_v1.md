# Pendia – Konzept v1

Stand: 31.10.2026 · Version 1

**Store-Name:** Pendia – GLP-1 Tagebuch / Pendia – GLP-1 Diary
**Kurzbeschreibung (Store, max. 80 Zeichen):** Abnehmspritze & Diabetes: Dosis, Stellen, Gewicht & Nebenwirkungen – ohne Konto.
**Plattform:** nur Android (APK), Sprachen Deutsch und Englisch

## Wofür

Ein Tagebuch für alle, die eine GLP-1-Spritze nutzen, egal ob wegen Typ-2-Diabetes oder Übergewicht. Unterstützte Wirkstoffe sind Semaglutid, Tirzepatid und Liraglutid. Für weitere Medikamente gibt es ein freies Feld.

## Grundsätze

1. **Kein Konto, keine Cloud.** Alle Daten bleiben auf dem Handy.
2. **Kostenloser Kern, der kostenlos bleibt.** Kein Abo.
3. **Kein Medizinprodukt.** Die App protokolliert, erinnert und informiert. Sie rechnet keine Dosis aus und empfiehlt keine.
4. **Keine Markennamen im App-Namen und im Store-Text.** In der App stehen sie nur als Auswahl im Tagebuch.
5. **Jede Info mit Quelle und Stand-Datum.**

## Funktionen in Version 1

| Bereich | Inhalt |
|---|---|
| Einrichtung | Medikament auswählen, Pen-Typ, Spritztag, Startdatum der ersten Spritze (auch rückwirkend), optional Diabetes-Modus |
| Spritze eintragen | Datum und Uhrzeit, Dosis (frei oder aus der Liste der zugelassenen Stärken), Injektionsstelle auf der Körperkarte, Notiz |
| Injektionsstellen | Körperkarte mit 8 Feldern, zeigt die zuletzt genutzten Stellen. Rein informativ: „zuletzt hier“ |
| Erinnerungen | Wochentag und Uhrzeit, zweite Erinnerung optional. Funktionieren auch nach Neustart und Zeitumstellung |
| Medikamentenwechsel | mehrere Medikamente mit Verlauf, z. B. erst Ozempic, dann Mounjaro |
| Körper | Gewicht, Bauchumfang, Verlaufskurve |
| Nebenwirkungen | Übelkeit, Verstopfung, Durchfall usw. mit Stärke 1–3 und Notiz |
| Diabetes-Modus | Blutzucker, HbA1c, Feld „Unterzucker gehabt?“, Hinweistext bei Kombination mit Insulin oder Sulfonylharnstoffen |
| Vorrat und Lagerung | Pen geöffnet am …, ungekühlt seit … Zeigt die Grenzwerte aus der Fachinformation an, ohne Empfehlung |
| Wissen | FAQ und Tipps (siehe unten), Notfall-Warnzeichen, Quellenlinks |
| Bericht | PDF für die Arztpraxis: Dosen, Gewicht, Nebenwirkungen, ggf. Blutzucker |
| Sicherung | Export und Import als Datei (JSON), Daten vollständig löschbar |
| Sprache | Deutsch und Englisch, folgt der Systemsprache, umstellbar |

## Bewusst nicht in Version 1

- Dosisrechner oder Hinweis „jetzt erhöhen“
- Rechner für vergessene Dosis (nur der Fachinfo-Text plus „frag deine Ärztin oder deinen Arzt“)
- Cloud-Sync, Konto, Werbung, Tracking
- Kalorienzähler, KI-Coach

## FAQ und Tipps (Version 1)

1. Was tun bei vergessener Dosis? (je Wirkstoff laut Fachinformation)
2. Wie steigert sich die Dosis normalerweise? (Info-Tabelle, keine Empfehlung)
3. Wie lagere ich den Pen, auch auf Reisen?
4. Injektionsstellen wechseln: warum und wie?
5. Übelkeit, Verstopfung, Durchfall: was hilft im Alltag?
6. Genug trinken: warum das wichtig ist
7. Eiweiß und Muskeln: allgemeiner Hinweis, Ernährungsberatung fragen
8. Pille und Mounjaro: was die Behörden sagen
9. Schwangerschaft und Kinderwunsch
10. OP oder Narkose geplant: vorher Bescheid sagen
11. Unterzucker zusammen mit Insulin oder Sulfonylharnstoffen
12. Warnzeichen: wann sofort zum Arzt (Pankreatitis, Gallenblase, Sehstörungen)
13. Fälschungen erkennen: nur in der Apotheke kaufen
14. Wer zahlt? Kasse bei Diabetes, Selbstzahlung bei Adipositas
15. Was die App kann und was nicht (Hinweis „ersetzt keine ärztliche Beratung“)

Noch offen, vor Veröffentlichung prüfen: Alkohol, Absetzen und Jojo-Effekt, aktuelle Preise.

## Technik

- Kotlin, Jetpack Compose, Material 3
- Room (SQLite) lokal, auf Wunsch verschlüsselt
- WorkManager oder AlarmManager für Erinnerungen
- Android 8.0+ (API 26), Ziel aktuelles API-Level
- Android-Cloud-Backup **aus** (`allowBackup=false`), damit keine Gesundheitsdaten unbemerkt hochgeladen werden
- Keine Internet-Berechtigung nötig
- APK-Build per Gradle, später optional über GitHub Actions

## Bildschirme

1. Einrichtung (3 Schritte)
2. Start: nächste Spritze, letzte Stelle, Gewichtstrend
3. Tagebuch (Liste)
4. Neuer Eintrag
5. Verlauf (Kurven)
6. Wissen (FAQ und Tipps)
7. Einstellungen (Sprache, Erinnerungen, Sicherung, Löschen, Datenschutz)

## Gestaltung und Icon

- ruhig, freundlich, medizinisch, aber nicht klinisch kalt
- Farben: Petrol/Türkis als Hauptfarbe, warmes Koralle als Akzent
- Icon: einfacher, abgerundeter Pen mit einem Häkchen oder einem Kalenderblatt. Keine Nadel, keine Hersteller-Farben, keine Pen-Form eines bestimmten Herstellers

## Vor dem Release

- Markenrecherche „Pendia“ (USPTO, EUIPO, DPMA)
- Datenschutzerklärung
- FAQ-Texte fachlich gegenlesen lassen (Ärztin/Arzt oder Apotheke)
- Google Play: Health-Apps-Deklaration, Kategorie Medizin

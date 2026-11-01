# Pendia – Design v1

Stand: 01.11.2026 · Version 1 · gilt ab App-Version 0.2.0

Diese Datei legt fest, wie Pendia aussieht. Neue Bildschirme halten sich daran. Der Code dazu steht in `app/src/main/java/app/pendia/ui/theme/Theme.kt`.

## Grundgefühl

Ruhig, freundlich, medizinisch, aber nicht klinisch kalt. Wenig Farbe, viel Weißraum, eine klare Hauptaktion pro Bildschirm.

## Farben (Salbei)

| Rolle | Hell | Dunkel | Wofür |
|---|---|---|---|
| primary | `#2D6A4F` | `#52B788` | Hauptknöpfe, aktive Navigation, Kurven, ausgewählte Stelle |
| primaryContainer | `#D8F3DC` | `#1B4332` | Karte „Nächste Spritze“ |
| secondary | `#1A759F` | `#34A0A4` | sparsam für Zusatzinfos, nicht für Knöpfe |
| secondaryContainer | `#CBEBD3` | `#1F4A36` | Markierung im Navigationsbalken, gewählte Chips |
| tertiary / Container | `#B4452F` / `#FFDAD2` | `#FFB4A3` / `#7A2A1A` | Warnzeichen-Karte, Hinweis „Wichtig“ |
| Koralle (Akzent) | `#F07B62` | `#F07B62` | zuletzt genutzte Stellen auf der Körperkarte, Tropfen im Icon |
| error | `#BA1A1A` | `#FFB4AB` | Fehler in Formularen, Löschen, Pen abgelaufen |
| background | `#F8F9FA` | `#121212` | Seitenhintergrund |
| surface | `#FFFFFF` | `#1A1C1B` | Karten |
| surfaceContainer | `#EDF2EF` | `#1E211F` | Kacheln, Navigationsbalken |
| onSurface | `#212529` | `#E3E3E3` | Fließtext |
| onSurfaceVariant | `#4F5B55` | `#BFC9C3` | Nebentext, Beschriftungen |
| outlineVariant | `#D3DBD6` | `#3C4440` | Kartenrahmen, Trennlinien |

**Regeln**
- Alle Material-Farbrollen sind gesetzt. Neue Farben kommen nur über das Theme, nie fest im Bildschirm.
- Rot nur für Fehler und Löschen. Warnungen in Koralle (tertiary), damit Rot seine Wirkung behält.
- Koralle sparsam: Körperkarte, Icon, Warnkarte.
- Kontrast mindestens 4,5:1 für Text. Weiß auf `#2D6A4F` liegt bei etwa 6:1.

## Schrift

Material-3-Standard (Roboto), keine eigene Schrift.

| Stil | Einsatz |
|---|---|
| headlineMedium, fett | Hauptaussage („Heute“, „in 3 Tagen“) |
| titleLarge, fett | Werte in Kacheln („95 kg“, „7,5 mg“) |
| titleMedium | Kartentitel |
| labelMedium, GROSSBUCHSTABEN, 70 % Deckkraft | Überschriften über Werten („NÄCHSTE SPRITZE“, „GEWICHT“) |
| bodyLarge / bodyMedium | Text, Listen |
| bodySmall | Hinweise, Quellen |

## Abstände und Formen

- Raster 4 dp. Seitenrand 16 dp, Abstand zwischen Karten 12 dp, Innenabstand Karten 16 dp, Hauptkarte 20 dp.
- Ecken: Material-Standard (Karten 12 dp, Knöpfe rund).
- Hauptknopf 52 dp hoch, volle Breite.
- Tippflächen mindestens 48 dp.

## Bausteine

| Baustein | Aussehen | Wo |
|---|---|---|
| Hauptkarte | primaryContainer, Label in Großbuchstaben, großer Wert, Hauptknopf | Start: nächste Spritze |
| Kachel (StatTile) | surfaceContainer, Label groß geschrieben, Wert fett, Untertext in primary; antippbar | Start: Gewicht, aktuelle Dosis |
| Abschnittskarte (SectionCard) | surface mit dünnem Rahmen, Titel | Letzte Spritze, Pen, Verlauf, Einstellungen |
| Warnkarte | tertiaryContainer mit Warnsymbol | Warnzeichen, Hinweis „Wichtig“ |
| Körperkarte | Silhouette in surfaceVariant, freie Stellen als Ring, gewählte Stelle primary, letzte Stellen Koralle (verblassend) | Spritze eintragen, Start |
| Formular | Zurück oben, Speichern unten volle Breite über der Navigationsleiste | alle Eingaben |

## Texte

- Du-Form, kurze Sätze, Verben auf Knöpfen („Spritze eintragen“, „Daten exportieren“).
- Fehlertexte sagen, was zu tun ist, und geben niemandem die Schuld („Gib eine Zahl ein, z. B. 82,5“).
- Löschen immer mit Rückfrage, die nennt, was gelöscht wird.

## Icon

„p“ mit Pen als Stamm und Tropfen in Koralle, auf `#2D6A4F`. Vektor in `res/drawable/ic_launcher_*.xml`, monochrome Variante für Android-Themen-Icons.

## Änderungen

- v1 (01.11.2026): Wechsel von Petrol (`#0F5E63`) auf Salbei. Kacheln und Label in Großbuchstaben aus dem Mockup übernommen.

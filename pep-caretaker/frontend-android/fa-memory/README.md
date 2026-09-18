# MemoryGamePepper

> Das MemorySpiel für Pepper, ist ein einfaches MemorySpiel **(Spiel-Logik)** für die Senioren im Altersheim, aber mit den speziellen Funktionen.
**Wichtig**: bei den Codes Kommentare hinzufügen und erklären!

> Das Projekt wurde erfolgreich geändert und läuft jetzt jetzt auf Android Studio Bumblebee 2021.1.1 Patch3

### Der ganze Spiel-Ablauf
> *Stand1*: Nachdem Starten des Spiels, kommt man im Start-Menü. Hier ist ein Start-Button zu sehen. Wenn man auf ihn drückt, kommt man in ein anderes Menü.

> *Stand2*: Hier sieht man wie Optionen, Spielen, vlt Einloggen?

> *Stand3*: (Nehmen wir an, man drückt auf Spielen)
>
## 1. Hauptmenü Implementieren
Ziel: Ein einfacher Startpunkt mit Optionen für:
High Scores anzeigen: Zeigt die bisherigen GameScores des Benutzers.
Spielanleitung/Einstellungen: Eine einfache Erklärung des Spiels und ggf. Einstellungen für Lautstärke, Thema etc.
Umsetzung:
Ein LinearLayout oder ConstraintLayout für das Layout.
Buttons für die einzelnen Optionen.
Navigation zur nächsten Ansicht (z. B. Grid-Auswahl oder High-Score-Bildschirm).
## 2. Grid-Auswahl vor dem Spielstart
Ziel:
Benutzer sieht alle verfügbaren Grids (3x2, 4x2, 4x3, 4x4).
Nach der Auswahl wird das Memory-Spiel mit der entsprechenden Grid-Größe gestartet.
Umsetzung:
Ein neuer Bildschirm zeigt die Grids als Auswahl (z. B. mit Buttons oder Kacheln).
Die Grid-Größe wird als Parameter an die MainActivity (oder das Spiel-Setup) übergeben.
Bild-Handling:
Zufällige Auswahl von Benutzerbildern.
Auffüllen mit lokalen Bildern aus dem drawable-Ordner, wenn nicht genügend Bilder vorhanden sind.
Reduzieren auf die benötigte Anzahl, wenn zu viele Bilder verfügbar sind.
## 3. Backend-Integration für Benutzerbilder und Scores
Ziel:
Abrufen der Benutzerdaten (Bilder, Scores) per API.
Bilder als Base64-String oder URL empfangen und für das Spiel vorbereiten.
Umsetzung:
Retrofit für die API-Integration nutzen.
Decodieren von Base64-Strings oder Laden von URLs mit einer Bildbibliothek (z. B. Coil oder Glide).
Ergebnisse aus der API zwischenspeichern, um sie lokal verfügbar zu halten.
## 4. Spielablauf und GameScores
Ziel:
Nach Abschluss eines Spiels werden die Ergebnisse angezeigt:
Aktueller Score.
Bester bisheriger Score.
Optional: Senden eines besseren Scores ans Backend.
Umsetzung:
Ein ResultScreen, der nach Spielende erscheint.
Vergleich des aktuellen Scores mit dem besten Score.
Optionaler API-Call, um den neuen High Score zu speichern.
## 5. Flexibilität und Wartbarkeit sicherstellen
Ziel: Das Projekt soll modular und erweiterbar sein.
Umsetzung:
Saubere Trennung zwischen Logik, UI und Daten (z. B. MVVM-Architektur).
Einfache Anpassung von Grids, Bildern und Backend-APIs.
Testbarkeit der wichtigsten Funktionen.
Priorisierung
Hauptmenü erstellen (inkl. Navigation zu Grid-Auswahl und High Scores).
Grid-Auswahl umsetzen (alle Grids anzeigen, Handling für Bilder einbauen).
Backend-Integration (API für Benutzerbilder und Scores).
Spielablauf abschließen (Ergebnisanzeige mit aktuellem und bestem Score).
Flexibilität prüfen und Feinschliff vornehmen.



## Meileinsteine
- erstmal ein einfaches funktionbares Memory-Spiel programmieren (Ohne ein Anfang-Menü, oder sonst was) => einfach funktionbar. 4x4 Grid für Anfang
- Die Kästchen sollen sich automatisch an jeder Bildschirmgröße anpassen (Länge und Breite)
- Die Karten sollen immer zufällig gelegt werden
- Ein Box, dass zeigt, wie viele Versuche man schon hat und wie viele Paare gefunden und noch zu finden hat.
- Ein Option-Button im Spiel einbauen mit Dropdown => Neustart & Pausieren
- Hintergrundbilder hinzufügen (auch für die Karten)
- Soundeffekte hinzufügen **Karte umdrehen, falsches paar, richtiges paar**
- Animataion hinzufügen bei Drehung einer Karte
- 

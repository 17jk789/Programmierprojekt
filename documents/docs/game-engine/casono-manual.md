# Casono Manual

<!-- vim-markdown-toc GFM -->

* [Start Game](#start-game)
    * [Server starten](#server-starten)
    * [Client starten](#client-starten)
* [UI](#ui)
    * [Lobby UI](#lobby-ui)
        * [Eine Lobby erstellen](#eine-lobby-erstellen)
        * [Einer Lobby beitreten](#einer-lobby-beitreten)
        * [Den Username ändern](#den-username-ändern)
        * [Highscores](#highscores)
    * [Game UI](#game-ui)
    * [Casono Browser](#casono-browser)
* [Casono Rules](#casono-rules)
    * [1. Spielübersicht](#1-spielübersicht)
        * [Grundregeln](#grundregeln)
    * [2. Sitzposition & Dealer-Button](#2-sitzposition--dealer-button)
    * [3. Blinds (Pflichteinsätze)](#3-blinds-pflichteinsätze)
    * [4. Spielablauf im Detail](#4-spielablauf-im-detail)
        * [4.1 Preflop (erste Setzrunde)](#41-preflop-erste-setzrunde)
        * [4.2 Flop (3 Gemeinschaftskarten)](#42-flop-3-gemeinschaftskarten)
        * [4.3 Turn (4. Gemeinschaftskarte)](#43-turn-4-gemeinschaftskarte)
        * [4.4 River (5. Gemeinschaftskarte)](#44-river-5-gemeinschaftskarte)
    * [5. Showdown (Kartenvergleich)](#5-showdown-kartenvergleich)
    * [6. Poker-Handrangfolge](#6-poker-handrangfolge)
    * [7. Wichtige Grundprinzipien](#7-wichtige-grundprinzipien)
        * [Reihenfolge beachten](#reihenfolge-beachten)
        * [Klare Aktionen](#klare-aktionen)
        * [Ein Spieler – eine Hand](#ein-spieler--eine-hand)
        * [Fehlerhafte Einsätze](#fehlerhafte-einsätze)
    * [8. Strategische Einordnung](#8-strategische-einordnung)
* [Casono Rules Easy Description](#casono-rules-easy-description)
    * [Blinds (Small Blind & Big Blind)](#blinds-small-blind--big-blind)
    * [Erste Setzrunde (Preflop)](#erste-setzrunde-preflop)
    * [Beispiel Preflop](#beispiel-preflop)
    * [Flop (3 Gemeinschaftskarten)](#flop-3-gemeinschaftskarten)
    * [Beispiel Flop](#beispiel-flop)
    * [Turn (4. Karte)](#turn-4-karte)
    * [River (5. Karte)](#river-5-karte)
    * [Showdown (Gewinnentscheidung)](#showdown-gewinnentscheidung)
    * [Poker Hand Rankings (Gewichtung)](#poker-hand-rankings-gewichtung)
    * [Fazit](#fazit)

<!-- vim-markdown-toc -->

# Start Game

## Server starten
```bash
./gradlew run --no-configuration-cache --args="server 1234"
```

## Client starten
```bash
./gradlew run --no-configuration-cache --args="client 127.0.0.1:1234 DeinName"
```

# UI

## Lobby UI

### Eine Lobby erstellen

![Create a Lobby](./images/create-a-lobby.png)

### Einer Lobby beitreten

![Join a Lobby](./images/join-a-lobby.png)

### Den Username ändern

![Change Username](./images/change-username.png)

### Highscores

![View Highscores](./images/view-high-scores.png)

## Game UI

![Game UI](./images/game-ui.png)

![Taskbar](./images/taskbar.png)

## Casono Browser

![[casono-browser.png]]

![Casono Browser](./images/casono-browser.png)

## Chat UI
### Globaler Chat

![[global_chat.png]]

### Lobby Chat

![[lobby_chat.png]]

### Whisper Chat

![[whisper_chat.png]]
# Casono Rules

## 1. Spielübersicht

Texas Hold’em ist ein strategisches Kartenspiel für mehrere Spieler. 
Ziel ist es, den Pot (alle gesetzten Chips) zu gewinnen, entweder durch:

- die beste Kartenkombination am Ende der Runde
- oder indem alle anderen Spieler vorher aussteigen (Fold)

### Grundregeln

- Jeder Spieler erhält 2 verdeckte Karten (Hole Cards)
- Es werden 5 Gemeinschaftskarten offen in der Mitte ausgelegt
- Jeder Spieler bildet die beste 5-Karten-Kombination aus:
  - eigenen Karten
  - und Gemeinschaftskarten

- Zu Spielbeginn erhält jeder Spieler ein Startgeld von 20000 Chips ($)

## 2. Sitzposition & Dealer-Button

Der sogenannte Dealer-Button bestimmt die Positionen am Tisch:

- Er zeigt an, wer als „Geber“ (Dealer) fungiert
- Die Positionen rotieren im Uhrzeigersinn nach jeder Runde

Die Position ist entscheidend, da sie bestimmt:

- die Reihenfolge der Aktionen
- wer die Blinds setzen muss

## 3. Blinds (Pflichteinsätze)

Vor jeder Runde werden zwei verpflichtende Einsätze geleistet:

- **Small Blind** (kleiner Blind): 100 Chips – gesetzt vom Spieler links neben dem Dealer
- **Big Blind** (großer Blind): 200 Chips - gesetzt vom Spieler zwei Plätze links vom Dealer

Diese Einsätze sorgen dafür, dass:

- ein Startpot entsteht
- jede Runde aktiv gespielt wird

## 4. Spielablauf im Detail

### 4.1 Preflop (erste Setzrunde)

Nach dem Austeilen der Karten beginnt die erste Setzrunde.

Der Spieler links vom Big Blind eröffnet die Runde.

Jeder Spieler hat folgende Optionen:

- **Fold** – Karten ablegen und aussteigen
- **Call** – Einsatz mitgehen
- **Raise** – Einsatz erhöhen

### 4.2 Flop (3 Gemeinschaftskarten)

- Drei Karten werden offen auf den Tisch gelegt
- Eine neue Setzrunde beginnt
- Die Setzrunde beginnt jetzt immer beim ersten aktiven Spieler links vom Dealer (im Uhrzeigersinn).

Ab diesem Zeitpunkt können alle Spieler ihre Strategie anhand zusätzlicher Informationen anpassen.

### 4.3 Turn (4. Gemeinschaftskarte)

- Die vierte Karte wird aufgedeckt
- Eine weitere Setzrunde folgt

Die Einsätze werden oft höher, da sich stärkere Hände entwickeln.

### 4.4 River (5. Gemeinschaftskarte)

- Die letzte Karte wird aufgedeckt
- Letzte Setzrunde

Dies ist die finale Entscheidungsphase:

- Maximierung des Gewinns
- oder Minimierung von Verlusten

## 5. Showdown (Kartenvergleich)

Wenn nach der letzten Setzrunde mindestens zwei Spieler verbleiben:

- Alle verbleibenden Spieler decken ihre Karten auf
- Die **beste 5-Karten-Kombination gewinnt**

Wichtig:

- Die Karten „sprechen für sich“ – die beste Hand zählt unabhängig von Ansagen

## 6. Poker-Handrangfolge

Die Stärke der Hände ist eindeutig festgelegt (von schwach nach stark):

1. High Card (höchste Einzelkarte)
2. One Pair (ein Paar)
3. Two Pair (zwei Paare)
4. Three of a Kind (Drilling)
5. Straight (Straße)
6. Flush (Farbe)
7. Full House
8. Four of a Kind (Vierling)
9. Straight Flush
10. Royal Flush

Je höher die Kombination, desto stärker die Hand.

## 7. Wichtige Grundprinzipien

### Reihenfolge beachten

Spieler müssen immer der Reihe nach handeln.

### Klare Aktionen

Alle Aktionen müssen eindeutig sein:

- Einsätze klar ansagen oder eindeutig setzen

### Ein Spieler – eine Hand

- Spieler dürfen ihre Karten nicht teilen oder gemeinsam spielen

### Fehlerhafte Einsätze

- Unklare oder falsche Einsätze können korrigiert werden, abhängig von der Spielsituation (nur wenn der Einsatz außerhalb der gültigen Grenzen liegt; zu hohe oder unzulässige Beträge werden blockiert und nicht automatisch korrigiert)

## 8. Strategische Einordnung

Texas Hold’em ist kein reines Glücksspiel. Der Erfolg basiert auf:

- Wahrscheinlichkeiten (Mathematik)
- Einschätzung von Gegnern (Psychologie)
- Positionsspiel und Timing

# Casono Rules Easy Description

Der Pokertisch ist ein unglaublich faszinierender Erlebnisraum, in dem man sehr viel lernen kann: über sich selbst, über andere Menschen und über Fragen wie: Wie treffe ich eigentlich Entscheidungen, wie gehe ich mit Stress und Unsicherheit um und wie gut ich darin bin, mich in andere hineinzuversetzen und Situationen richtig einzuschätzen.

Damit Du in diesem Erlebnisraum starten kannst, ist es – wie bei jedem Spiel – notwendig, zuerst die Grundregeln und den Spielablauf zu verstehen.

Also los geht es:

Wir haben am Tisch **4 Spieler**: Julian, Mathis, Jona und Lars. Jeder Spieler startet mit **20000 Chips ($)**. Jeder bekommt **2 Karten auf die Hand** und es gibt zusätzlich **5 Gemeinschaftskarten**, die später in der Mitte aufgedeckt werden.

![1. image](./images/1-1.svg)

Die Spieler sitzen in folgender Reihenfolge: Julian, Mathis, Jona und Lars. Einer davon hat den Dealer-Button, der bestimmt, wer die Karten austeilt und von wo die Runde beginnt. Dieser Button wandert nach jeder Runde im Uhrzeigersinn weiter und verändert damit die Position ständig.

Regel: *34 Button Placement and Movement 🟢*

## Blinds (Small Blind & Big Blind)

Bevor die Karten verteilt werden, gibt es zwei Pflicht-Einsätze:

Der Small Blind und der Big Blind. Der Big Blind ist immer doppelt so hoch wie der Small Blind.

![2. image](./images/2-1.svg)

Regel: *32 Dead Button 🟡*

Diese Einsätze sorgen dafür, dass sofort ein Pot entsteht und das Spiel überhaupt beginnt, weil jeder schon “im Spiel” ist.

Danach werden die Karten verteilt: zuerst Small Blind, dann Big Blind und dann im Uhrzeigersinn alle anderen Spieler.

## Erste Setzrunde (Preflop)

Die erste Setzrunde beginnt immer bei dem Spieler links vom Big Blind.

Jetzt muss jeder Spieler entscheiden:

* Fold (aussteigen)
* Call (mitgehen)
* Raise (erhöhen)

Regel: *40 Methods of Betting 🟢*
Regel: *41 Methods of Calling 🟢*
Regel: *42 Methods of Raising 🟢*
Regel: *50 Acting in Turn 🟢*

## Beispiel Preflop

Julian schaut seine Karten an und entscheidet sich direkt für einen Raise von **600 Chips**.

![3. image](./images/3-1.svg)

Mathis sieht seine Karten an und merkt, dass sie nicht gut sind, also foldet er und steigt aus.

![4. image](./images/4-1.svg)

Jona ist nun dran und entscheidet sich ebenfalls für einen Call, weil seine Hand spielbar ist.

![5. image](./images/5-1.svg)

Lars schaut seine Karten an, erkennt eine starke Hand und erhöht auf **1200 Chips**.

![6. image](./images/6-1.svg)

Damit verändert sich sofort die Situation: Julian und Jona müssen entscheiden, ob sie diesen Raise bezahlen, selbst erhöhen oder aussteigen.

## Flop (3 Gemeinschaftskarten)

Jetzt werden **3 Gemeinschaftskarten** in die Mitte gelegt. Ab hier verändert sich das Spiel komplett, weil alle Spieler zusätzliche Informationen bekommen.

Die Setzrunde beginnt jetzt immer beim ersten aktiven Spieler links vom Dealer (im Uhrzeigersinn).

![7. image](./images/7-1.svg)

Es beginnt eine neue Setzrunde.

Regel: *49 Accepted Action 🟢*

## Beispiel Flop

Jona setzt **1000 Chips** als Erstes. Lars entscheidet sich mitzugehen (Call), weil seine Karten durch die Gemeinschaftskarten stärker geworden sind.

Julian steigt aus, weil er keine gute Verbindung mehr sieht. Mathis ist bereits raus.

![8. image](./images/8-1.svg)

## Turn (4. Karte)

Jetzt kommt die **4. Gemeinschaftskarte**.

Wieder beginnt eine neue Setzrunde.

Jona setzt diesmal **3000 Chips**. Lars bezahlt erneut (Call), weil seine Hand weiterhin gut spielbar ist.

![9. image](./images/9-1.svg)

Regel: *53 Action Out of Turn 🟡*

## River (5. Karte)

Jetzt wird die letzte Gemeinschaftskarte aufgedeckt.

Dies ist die letzte Entscheidung im Spiel.

Jona setzt **5000 Chips**.

![10. image](./images/10-1.svg)

Lars muss jetzt entscheiden: Fold, Call oder Raise auf 10000 Chips.

Regel: *54 Pot Size Bets 🟡*

## Showdown (Gewinnentscheidung)

Wenn nach der letzten Setzrunde noch zwei Spieler übrig sind, kommt es zum Showdown.

Beide Spieler zeigen ihre Karten offen. Gewonnen hat die **beste 5-Karten-Kombination aus Handkarten und Gemeinschaftskarten**.

![11. image](./images/11-1.svg)

Regel: *12 Cards Speak at Showdown 🟢*
Regel: *16 Face Up for All-Ins 🟢*
Regel: *17 Non All-In Showdowns 🟢*

Wenn Lars den letzten Einsatz bezahlt, werden die Hände verglichen. Wenn er foldet, gewinnt Jona automatisch den gesamten Pot.

## Poker Hand Rankings (Gewichtung)

Die Kartenkombinationen sind klar geordnet – von schwach bis extrem stark:

<img src="./images/12.png" height="600">

Je höher die Kombination, desto stärker die Hand und desto wahrscheinlicher der Gewinn.

Jona: 2. Paar:

![13. image](./images/13-1.svg)

Lars: 1. Paar:

![14. image](./images/14-1.svg)

Da zwei Paare in der Rangfolge über einem einzelnen Paar stehen, gewinnt Jona diese Runde.

## Fazit

Poker ist kein Glücksspiel im klassischen Sinn, sondern ein Spiel aus Strategie, Psychologie und Mathematik. Jede Entscheidung von Julian, Mathis, Jona oder Lars verändert die komplette Dynamik am Tisch. Wer die Regeln versteht, versteht nicht nur Karten, sondern auch Menschen und Entscheidungen unter Druck.

Regel: *67 One Player One Hand 🟢*
Regel: *52 Incorrect Bets 🟡*
Regel: *57 Non-Standard Betting 🟡*

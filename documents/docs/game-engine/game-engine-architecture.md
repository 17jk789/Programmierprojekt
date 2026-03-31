# Casono Game Engine

> This document and the associated architecture represent a pre-alpha version of the Casono Game Engine.

```txt
server/
 └── domain/
     └── game/
         ├── engine/
         │    ├── GameEngine.java
         │    ├── TurnManager.java -> 23: New Hand and New Limits 🟢 | 34: Button Placement and Movement 🟢
         │    ├── ChipRaceManager.java -> 24: Chip Race, Scheduled Color Ups 🟡
         │    └── RoundManager.java -> 23: New Hand and New Limits 🟢 | 34: Button Placement and Movement 🟢
         │
         ├── state/
         │    ├── GameState.java
         │    ├── Pot.java -> 21: Side Pots 🟡
         │    └── TableState.java
         │
         ├── player/
         │    ├── Player.java
         │    └── PlayerStatus.java
         │
         ├── deck/
         │    ├── Deck.java -> For mixing and distributing
         │    └── Card.java -> Definition of card values
         │
         ├── action/
         │    ├── Action.java
         │    ├── ActionType.java
         │    ├── RaiseAction.java
         │    ├── CallAction.java
         │    └── FoldAction.java
         │
         ├── rules/
         │    ├── Rule.java
         │    ├── RuleEngine.java
         │    │
         │    ├── betting/
         │    │    ├── MinimumRaiseRule.java -> 43: Raise Amounts 🟢
         │    │    ├── RaiseReopenRule.java -> 43: Raise Amounts 🟢 | 47: Re-Opening the Bet. 🟡 | 48: Number of Allowable Raises 🟡
         │    │    ├── PotLimitRule.java -> 54: Pot Size and Pot-Limit Bets 🟡
         │    │    ├── AllInRule.java -> 16: Face Up for All-Ins 🟢 | 62: All-In with Chips Found Behind Later 🟡
         │    │    ├── AcceptedActionRule.java -> 49: Accepted Action 🟢
         │    │    ├── BindingDeclarationRule.java -> 51: Binding Declarations / Undercalls in Turn 🟡 | 56: String Bets and Raises 🟡
         │    │    └── MinimumBetRule.java -> 52: Incorrect Bets, Underbets and Underraises 🟡
         │    │
         │    ├── etiquette/
         │    │    └── DisclosureRule.java -> 67: No Disclosure. One Player to a Hand 🟢
         │    │
         │    ├── turn/
         │    │    ├── ActionOrderRule.java -> 50: Acting in Turn 🟢
         │    │    └── OutOfTurnRule.java -> 53: Action Out of Turn (OOT) 🟡
         │    │
         │    └─── showdown/
         │         ├── ShowdownOrderRule.java -> 17: Non All-In Showdowns and Showdown Order 🟢
         │         ├── CardsSpeakRule.java -> 12: Declarations. Cards Speak at Showdown 🟢
         │         └── LiveHandRule.java -> 30: At Your Seat and Live Hands 🟢
         │
         ├── evaluator/
         │    ├── HandEvaluator.java
         │    └── HandRank.java
         │
         └── exception/
              ├── InvalidActionException.java
              └── RuleViolationException.java
```

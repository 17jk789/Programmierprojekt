# Game Engine Control
<!-- vim-markdown-toc GFM -->

* [GameController](#gamecontroller)
* [How the Server interacts with the GameController](#how-the-server-interacts-with-the-gamecontroller)
    * [Starting a Game](#starting-a-game)
    * [Preflop Actions](#preflop-actions)
    * [Flop](#flop)
    * [Turn](#turn)
    * [River](#river)
    * [Showdown](#showdown)
* [Server Outputs](#server-outputs)
    * [Get full game state](#get-full-game-state)
    * [Get community cards](#get-community-cards)
    * [Get player hole cards](#get-player-hole-cards)

<!-- vim-markdown-toc -->

# GameController

The GameController acts as the main entry point for controlling the poker game logic from the server.

The server does not manipulate the game state directly.
Instead, it interacts exclusively with the `GameController`, which internally coordinates:

- the GameEngine
- the GameState
- the RuleEngine
- the RoundManager
- the TurnManager

# How the Server interacts with the GameController

The server calls methods on the GameController to control the game.

## Starting a Game

```java
GameController game = new GameController(engine);

game.addPlayer(PlayerId.of("Julian"), 20000);
game.addPlayer(PlayerId.of("Mathis"), 20000);
game.addPlayer(PlayerId.of("Jona"), 20000);
game.addPlayer(PlayerId.of("Lars"), 20000);

game.startGame();
```

Steps performed:

1. Create a GameController instance.
2. Add players with their starting chip stacks.
3. Start the game.

When `startGame()` is called:

- the deck is prepared 
- hole cards are dealt
- the game phase switches to PREFLOP

## Preflop Actions

During the preflop phase, players perform actions.

```java
game.playerCall(PlayerId.of("Julian"));
game.playerFold(PlayerId.of("Mathis"));
game.playerCall(PlayerId.of("Jona"));
game.playerRaise(PlayerId.of("Lars"), 1200);
```

Supported actions include:

* `playerCall(playerId)`
* `playerFold(playerId)`
* `playerRaise(playerId, amount)`

The GameController passes these actions on to the GameEngine, which validates them using the RuleEngine.

## Flop

Once the preflop betting round is completed, the server can deal the flop.

```java
game.dealFlop();
```

This will:

- draw 3 community cards
- add them to the board
- update the game phase

## Turn

```java
game.dealTurn();
```

This deals the fourth community card.

## River

```java
game.dealRiver();
```

This deals the fifth and final community card and starts the final betting round.

## Showdown

After the final betting round, the server can determine the winner.

```java
String winner = game.showdown();
````

This will:

- evaluate the hands of all remaining players
- determine the best poker hand
- award the pot to the winner
- end the current hand

Example:

```text
Winner: Julian
Pot: 3200
```

During the showdown, each remaining player's hand is evaluated using their:

- two hole cards
- five community cards

The best possible 5-card poker hand wins the pot.

# Server Outputs

The server can query the current game state at any time.

## Get full game state

```java
GameState state = game.getState();
````

Returns the complete game state.

The state includes information such as:

- players
- chip stacks
- pot
- current game phase
- deck state

## Get community cards

```java
List<Card> board = game.getCommunityCards();
```

Returns the community cards currently on the board.

The number of cards depends on the game phase:

- Flop -> 3 cards
- Turn -> 4 cards
- River -> 5 cards

Example:

```text
[NINE of DIAMONDS, TEN of SPADES, JACK of CLUBS]
```

## Get player hole cards

```java
Map<PlayerId, List<Card>> cards = game.getPlayerCards();
```

Returns a mapping of player IDs to their hole cards.

Each player receives two private cards at the start of the hand.

```text
playerId -> [Card, Card]
```

Example:

```text
Mathis -> [NINE of DIAMONDS, TEN of SPADES]
Lars   -> [JACK of SPADES, TWO of HEARTS]
Jona   -> [SEVEN of DIAMONDS, TWO of DIAMONDS]
Julian -> [NINE of HEARTS, FIVE of DIAMONDS]
```

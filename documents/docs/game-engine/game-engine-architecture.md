# Casono Game Engine
<!-- vim-markdown-toc GFM -->

* [server/GameController](#servergamecontroller)
    * [GameController](#gamecontroller)
    * [addPlayer](#addplayer)
    * [startGame](#startgame)
    * [rotateDealer](#rotatedealer)
    * [getDealer](#getdealer)
    * [postBlinds](#postblinds)
    * [dealHoleCards](#dealholecards)
    * [dealFlop](#dealflop)
    * [dealTurn](#dealturn)
    * [dealRiver](#dealriver)
    * [playerFold](#playerfold)
    * [playerCall](#playercall)
    * [playerRaise](#playerraise)
    * [getState](#getstate)
    * [getCommunityCards](#getcommunitycards)
* [server/action/AbstractAction](#serveractionabstractaction)
    * [AbstractAction](#abstractaction)
* [server/action/AllInAction](#serveractionallinaction)
    * [AllInAction](#allinaction)
* [server/action/BetAction](#serveractionbetaction)
    * [BetAction](#betaction)
    * [getAmount](#getamount)
* [server/action/BlindAction](#serveractionblindaction)
    * [BlindAction](#blindaction)
    * [getAmount](#getamount-1)
* [server/action/CallAction](#serveractioncallaction)
    * [CallAction](#callaction)
* [server/action/FoldAction](#serveractionfoldaction)
    * [FoldAction](#foldaction)
* [server/action/RaiseAction](#serveractionraiseaction)
    * [RaiseAction](#raiseaction)
    * [getAmount](#getamount-2)
* [server/deck/Card](#serverdeckcard)
    * [Card](#card)
    * [getSuit](#getsuit)
    * [getRank](#getrank)
* [server/deck/Deck](#serverdeckdeck)
    * [Deck](#deck)
    * [shuffle](#shuffle)
    * [draw](#draw)
    * [setCards](#setcards)
* [server/engine/GameEngine](#serverenginegameengine)
    * [GameEngine](#gameengine)
    * [startNewHand](#startnewhand)
    * [startNewHand](#startnewhand-1)
    * [processAction](#processaction)
    * [handleAction](#handleaction)
    * [getState](#getstate-1)
* [server/engine/RoundManager](#serverengineroundmanager)
    * [startNewHand](#startnewhand-2)
    * [progressIfNeeded](#progressifneeded)
    * [isBettingRoundFinished](#isbettingroundfinished)
    * [advancePhase](#advancephase)
    * [postBlinds](#postblinds-1)
* [server/engine/TurnManager](#serverengineturnmanager)
    * [nextPlayer](#nextplayer)
* [server/evaluator/HandEvaluator](#serverevaluatorhandevaluator)
    * [evaluate](#evaluate)
    * [getRank](#getrank-1)
    * [isStraight](#isstraight)
* [server/evaluator/HandRank](#serverevaluatorhandrank)
    * [HandRank](#handrank)
    * [getType](#gettype)
    * [getKickers](#getkickers)
* [server/player/Player](#serverplayerplayer)
    * [Player](#player)
    * [isAllIn](#isallin)
    * [getId](#getid)
    * [getName](#getname)
    * [getChips](#getchips)
    * [removeChips](#removechips)
    * [addChips](#addchips)
    * [setStatus](#setstatus)
    * [getStatus](#getstatus)
    * [getHand](#gethand)
    * [giveCard](#givecard)
    * [clearHand](#clearhand)
    * [isFolded](#isfolded)
    * [setFolded](#setfolded)
* [server/rules/RuleEngine](#serverrulesruleengine)
    * [RuleEngine](#ruleengine)
    * [validate](#validate)
* [server/rules/RuleViolationException](#serverrulesruleviolationexception)
    * [RuleViolationException](#ruleviolationexception)
* [server/rules/showdown/CardsSpeakRule](#serverrulesshowdowncardsspeakrule)
    * [determineWinner](#determinewinner)
    * [awardPot](#awardpot)
* [server/state/GameState](#serverstategamestate)
    * [getPlayers](#getplayers)
    * [getCurrentPlayer](#getcurrentplayer)
    * [getCurrentPlayerIndex](#getcurrentplayerindex)
    * [isHandActive](#ishandactive)
    * [getPhase](#getphase)
    * [getTableState](#gettablestate)
    * [getPot](#getpot)
    * [getDeck](#getdeck)
    * [getCommunityCards](#getcommunitycards-1)
    * [getHoleCards](#getholecards)
    * [getDealerIndex](#getdealerindex)
    * [getPlayerCount](#getplayercount)
    * [setCurrentPlayerIndex](#setcurrentplayerindex)
    * [setHandActive](#sethandactive)
    * [setPhase](#setphase)
    * [setDealerIndex](#setdealerindex)
    * [setDeck](#setdeck)
    * [addPlayer](#addplayer-1)
    * [getCurrentBet](#getcurrentbet)
    * [setCurrentBet](#setcurrentbet)
    * [resetBets](#resetbets)
    * [addToPot](#addtopot)
    * [isAllowOutOfTurn](#isallowoutofturn)
    * [setAllowOutOfTurn](#setallowoutofturn)
    * [getPlayer](#getplayer)
    * [getCurrentBetCommitment](#getcurrentbetcommitment)
    * [setCurrentBetCommitment](#setcurrentbetcommitment)
    * [giveHoleCards](#giveholecards)
    * [addCommunityCard](#addcommunitycard)
    * [resetCommunityCards](#resetcommunitycards)
    * [nextPlayer](#nextplayer-1)
    * [rotateDealer](#rotatedealer-1)
    * [getDealer](#getdealer-1)
    * [startNewHand](#startnewhand-3)
* [server/state/Pot](#serverstatepot)
    * [add](#add)
    * [getAmount](#getamount-3)
    * [reset](#reset)
* [server/state/TableState](#serverstatetablestate)
    * [getCurrentBet](#getcurrentbet-1)
    * [setCurrentBet](#setcurrentbet-1)
    * [getBigBlind](#getbigblind)
    * [setBigBlind](#setbigblind)
    * [getMinRaise](#getminraise)
    * [setMinRaise](#setminraise)
    * [isBettingOpen](#isbettingopen)
    * [setBettingOpen](#setbettingopen)
    * [canReopenBetting](#canreopenbetting)
    * [setCanReopenBetting](#setcanreopenbetting)
    * [getLastAggressorId](#getlastaggressorid)
    * [setLastAggressorId](#setlastaggressorid)

<!-- vim-markdown-toc -->

## server/GameController

### GameController

- **Description**: GameController is responsible for managing the flow of the poker game. It
- **Parameter (`engine`)**: The GameEngine instance that manages the game state and logic.

### addPlayer

- **Description**: Adds a player to the game with the specified name and initial chip count.
- **Parameter (`name`)**: The name of the player to add.
- **Parameter (`chips`)**: The initial number of chips the player has.

### startGame

- **Description**: Initializes a new hand by preparing the deck, setting the phase to PREFLOP,

### rotateDealer

- **Description**: Rotates the dealer position to the next player in the list.

### getDealer

- **Description**: Returns the player currently acting as dealer.

### postBlinds

- **Description**: Determines the small and big blind players relative to the dealer

### dealHoleCards

- **Description**: Deals hole cards to each player from the deck.

### dealFlop

- **Description**: Draws three cards from the deck and adds them as community cards (flop).

### dealTurn

- **Description**: Deals the turn by drawing one community card from the deck and adding it to

### dealRiver

- **Description**: Deals the river by drawing one community card from the deck and adding it to

### playerFold

- **Description**: Processes a player's fold action by sending a FoldAction to the GameEngine.
- **Parameter (`playerId`)**: The ID of the player who is folding.

### playerCall

- **Description**: Processes a player's call action by sending a CallAction to the GameEngine.
- **Parameter (`playerId`)**: The ID of the player who is calling.

### playerRaise

- **Description**: Processes a player's raise action by sending a RaiseAction to the GameEngine.
- **Parameter (`playerId`)**: The ID of the player who is raising.
  *
- **Parameter (`amount`)**: The amount the player is raising.

### getState

- **Description**: Retrieves the current game state from the GameEngine.

### getCommunityCards

- **Description**: Retrieves the list of community cards currently on the table.

## server/action/AbstractAction

### AbstractAction

- **Description**: AbstractAction serves as a base class for all player actions in the poker
- **Parameter (`playerId`)**: The ID of the player performing the action.

## server/action/AllInAction

### AllInAction

- **Description**: AllInAction represents the action of a player going all-in in a poker game.
- **Parameter (`playerId`)**: The ID of the player performing the all-in action.

## server/action/BetAction

### BetAction

- **Description**: Represents a bet action where a player contributes a fixed amount of chips.
- **Parameter (`playerId`)**: The ID of the player performing the bet action.
- **Parameter (`amount`)**: The amount of chips the player is betting.

### getAmount

- **Description**: Retrieves the amount of chips being bet in this action.

## server/action/BlindAction

### BlindAction

- **Description**: Executes a blind by deducting chips from the player, adding them to the pot,
- **Parameter (`playerId`)**: The ID of the player posting the blind bet.
- **Parameter (`amount`)**: The amount of chips the player is posting as a blind bet.

### getAmount

- **Description**: Retrieves the type of this action, which is BLIND.
- **Parameter (`state`)**: The current game state on which to execute the action.
- **Return**: The ActionType corresponding to this action.

## server/action/CallAction

### CallAction

- **Description**: CallAction represents the action of a player calling in a poker game.
- **Parameter (`playerId`)**: The ID of the player performing the call action.

## server/action/FoldAction

### FoldAction

- **Description**: FoldAction represents the action of a player folding in a poker game. When a
- **Parameter (`playerId`)**: The ID of the player performing the fold action.

## server/action/RaiseAction

### RaiseAction

- **Description**: Constructs a RaiseAction for the specified player ID and raise amount.
- **Parameter (`playerId`)**: The ID of the player performing the raise action.
- **Parameter (`raiseAmount`)**: The amount of chips the player is raising.

### getAmount

- **Description**: Retrieves the amount of chips being raised in this action.

## server/deck/Card

### Card

- **Description**: The Card class represents a single playing card in a standard deck of cards.
- **Parameter (`suit`)**: The suit of the card (Hearts, Diamonds, Clubs, Spades).
- **Parameter (`rank`)**: The rank of the card (2-10, Jack, Queen, King, Ace).

### getSuit

- **Description**: Retrieves the suit of the card.

### getRank

- **Description**: Retrieves the rank of the card.

## server/deck/Deck

### Deck

- **Description**: The Deck class represents a standard deck of playing cards. It provides

### shuffle

- **Description**: Shuffles the deck of cards using the Collections.shuffle method, which

### draw

- **Description**: Draws and removes the last card in the list, which represents the top of the deck.

### setCards

- **Description**: Retrieves the current list of cards in the deck. This method returns a new

## server/engine/GameEngine

### GameEngine

- **Description**: The GameEngine class is responsible for managing the core logic of the poker
- **Parameter (`state`)**: The initial game state to be managed by the engine.
- **Parameter (`ruleEngine`)**: The RuleEngine instance responsible for validating player actions.
- **Parameter (`roundManager`)**: The RoundManager instance responsible for managing the progression of rounds.
- **Parameter (`turnManager`)**: The TurnManager instance responsible for managing player turns.

### startNewHand

- **Description**: Starts a new hand with the provided game state. This method initializes the
- **Parameter (`state`)**: The game state to be used for starting the new hand.

### startNewHand

- **Description**: Starts a new hand using the current game state. This method is a convenience

### processAction

- **Description**: Processes a player action by validating it against the game rules, executing
- **Parameter (`action`)**: The player action to be processed.

### handleAction

- **Description**: Handles a player action by performing the following steps:
- **Parameter (`state`)**: The current game state on which to execute the action.
- **Parameter (`action`)**: The player action to be processed.

### getState

- **Description**: Retrieves the current game state managed by the GameEngine.

## server/engine/RoundManager

### startNewHand

- **Description**: RoundManager is responsible for managing the flow of a poker game round. It
- **Parameter (`state`)**: The game state to be used for starting the new hand.

### progressIfNeeded

- **Description**: Checks if the betting round is finished and advances the game phase if
- **Parameter (`state`)**: The current game state to be evaluated for betting round progression.

### isBettingRoundFinished

- **Description**: Determines if the betting round is finished by checking if all active players
- **Parameter (`state`)**: The current game state to be evaluated for betting round completion.

### advancePhase

- **Description**: Advances the game phase to the next stage (flop, turn, river, or showdown)
- **Parameter (`state`)**: The current game state to be updated with the new phase.

### postBlinds

- **Description**: Handles the posting of blinds at the start of a new hand. This method
- **Parameter (`state`)**: The current game state to be updated with the posted blinds.

## server/engine/TurnManager

### nextPlayer

- **Description**: The TurnManager class is responsible for managing the flow of turns in a
- **Parameter (`state`)**: The current game state that will be updated to reflect the next player's turn.

## server/evaluator/HandEvaluator

### evaluate

- **Description**: The HandEvaluator class provides functionality to evaluate a poker hand and
- **Parameter (`cards`)**: A list of Card objects representing the player's hand and community cards.

### getRank

- **Description**: Helper method to get the rank of a card based on the count of occurrences in
- **Parameter (`map`)**: A map where the key is the card rank and the value is the count of occurrences.
- **Parameter (`count`)**: The specific count to look for (e.g., 2 for pairs, 3 for three of a kind).

### isStraight

- **Description**: Helper method to determine if a list of card ranks forms a straight.
- **Parameter (`ranks`)**: A list of integer ranks representing the cards in the hand.

## server/evaluator/HandRank

### HandRank

- **Description**: HandRank represents the rank of a poker hand, including its type (e.g.,
- **Parameter (`type`)**: The type of the hand (e.g., flush, straight).
- **Parameter (`kickers`)**: A list of integers representing the kickers for tie-breaking.

### getType

- **Description**: Retrieves the type of the hand.

### getKickers

- **Description**: Retrieves the list of kickers for tie-breaking.

## server/player/Player

### Player

- **Description**: The Player class represents a participant in the poker game. It holds
- **Parameter (`id`)**: The unique identifier for the player.
- **Parameter (`chips`)**: The initial number of chips the player has.

### isAllIn

- **Description**: Checks if the player is all-in, meaning they have no chips left to bet.

### getId

- **Description**: Retrieves the unique identifier of the player.

### getName

- **Description**: Returns the display name of the player.

### getChips

- **Description**: Retrieves the current chip count of the player.

### removeChips

- **Description**: Removes a specified amount of chips from the player's total. If the amount
- **Parameter (`amount`)**: The number of chips to remove from the player.

### addChips

- **Description**: Adds a specified amount of chips to the player's total.
- **Parameter (`amount`)**: The number of chips to add to the player.

### setStatus

- **Description**: Sets the player's status to the specified value.
- **Parameter (`status`)**: The new status for the player.

### getStatus

- **Description**: Retrieves the current status of the player.

### getHand

- **Description**: Retrieves the player's current hand of cards.

### giveCard

- **Description**: Adds a card to the player's hand.
- **Parameter (`card`)**: The Card object to be added to the player's hand.

### clearHand

- **Description**: Clears the player's hand of cards, removing all cards from the hand.

### isFolded

- **Description**: Checks if the player has folded in the current round.

### setFolded

- **Description**: Sets the player's folded status to the specified value.
- **Parameter (`folded`)**: The new folded status for the player.

## server/rules/RuleEngine

### RuleEngine

- **Description**: The RuleEngine class is responsible for managing and validating a list of
- **Parameter (`rules`)**: The list of rules to be managed by the RuleEngine.

### validate

- **Description**: Validates the given action against all the rules in the RuleEngine. If any
- **Parameter (`state`)**: The current state of the game.
- **Parameter (`action`)**: The action to be validated against the rules.

## server/rules/RuleViolationException

### RuleViolationException

- **Description**: RuleViolationException is a custom exception that is thrown when a player
- **Parameter (`message`)**: The detail message explaining the reason for the rule violation.

## server/rules/showdown/CardsSpeakRule

### determineWinner

- **Description**: The CardsSpeakRule class implements the Rule interface and defines the logic
- **Parameter (`state`)**: The current state of the game.
- **Parameter (`action`)**: The action to be validated.
- **Parameter (`state`)**: The current state of the game, which includes player information, hole cards, and community cards.

### awardPot

- **Description**: Awards the pot to the winner of the poker hand. It determines the winner(s)
- **Parameter (`state`)**: The current state of the game, which includes player information and pot details.

## server/state/GameState

### getPlayers

- **Description**: The GameState class encapsulates the entire state of a poker game at any

### getCurrentPlayer

- **Description**: Returns the current player whose turn it is to act.

### getCurrentPlayerIndex

- **Description**: Returns the index of the current player in the players list.

### isHandActive

- **Description**: Indicates whether a hand is currently active in the game.

### getPhase

- **Description**: Returns the current phase of the game (e.g., PREFLOP, FLOP, TURN, RIVER).

### getTableState

- **Description**: Returns the current state of the table, including player statuses and

### getPot

- **Description**: Returns the current pot, which contains the total amount of chips bet by

### getDeck

- **Description**: Returns the current deck of cards being used in the game.

### getCommunityCards

- **Description**: Returns the list of community cards currently on the table.

### getHoleCards

- **Description**: Returns the hole cards for a specific player based on their ID.
- **Parameter (`playerId`)**: The ID of the player whose hole cards are being requested.

### getDealerIndex

- **Description**: Returns the index of the dealer in the players list.

### getPlayerCount

- **Description**: Returns a map of player IDs to their current hole cards.
- **Return**: A map where the key is the player ID and the value is a list of Card objects representing the player's hole cards.

### setCurrentPlayerIndex

- **Description**: Sets the index of the current player in the players list.
- **Parameter (`index`)**: An integer representing the index of the current player.

### setHandActive

- **Description**: Sets whether a hand is currently active in the game.
- **Parameter (`handActive`)**: A boolean value indicating whether a hand is active.

### setPhase

- **Description**: Sets the current phase of the game.
- **Parameter (`phase`)**: The GamePhase enum value representing the new phase of the game.

### setDealerIndex

- **Description**: Sets the index of the dealer in the players list.
- **Parameter (`dealerIndex`)**: An integer representing the index of the dealer.

### setDeck

- **Description**: Sets the current deck of cards being used in the game.
- **Parameter (`deck`)**: A Deck object representing the new deck of cards to be used in the game.

### addPlayer

- **Description**: Adds a player to the game with the specified ID and initial chip count. This
- **Parameter (`id`)**: The ID of the player to add.
- **Parameter (`chips`)**: The initial number of chips the player has.

### getCurrentBet

- **Description**: Retrieves the current bet amount for a specific player based on their ID.
- **Parameter (`playerId`)**: The ID of the player whose current bet is being requested.

### setCurrentBet

- **Description**: Sets the current bet amount for a specific player based on their ID. This
- **Parameter (`playerId`)**: The ID of the player whose current bet is being set.
- **Parameter (`amount`)**: The new bet amount to be set for the specified player.

### resetBets

- **Description**: Resets the current bets for all players by clearing the currentBets map. This

### addToPot

- **Description**: Adds a specified amount to the pot. This method updates the total amount in
- **Parameter (`amount`)**: The amount of chips to be added to the pot.

### isAllowOutOfTurn

- **Description**: Returns whether out-of-turn actions are allowed in the game. Out-of-turn

### setAllowOutOfTurn

- **Description**: Sets whether out-of-turn actions are allowed in the game. This method updates
- **Parameter (`allowOutOfTurn`)**: A boolean value indicating whether out-of-turn actions should be allowed in the game.

### getPlayer

- **Description**: Retrieves a player from the game based on their ID. This method searches the
- **Parameter (`id`)**: The ID of the player to retrieve.
- **Return**: The Player object corresponding to the specified ID.

### getCurrentBetCommitment

- **Description**: Retrieves the current bet commitment for a specific player based on their ID.
- **Parameter (`playerId`)**: The ID of the player whose current bet commitment is being requested.

### setCurrentBetCommitment

- **Description**: Sets the current bet commitment for a specific player based on their ID. This
- **Parameter (`playerId`)**: The ID of the player whose current bet commitment is being set.
- **Parameter (`amount`)**: The new bet commitment amount to be set for the specified player.

### giveHoleCards

- **Description**: Gives hole cards to a specific player based on their ID. This method takes
- **Parameter (`playerId`)**: The ID of the player to whom the hole cards are being given.
- **Parameter (`c1`)**: The first Card object representing one of the player's hole cards.
- **Parameter (`c2`)**: The second Card object representing the other hole card for the player.

### addCommunityCard

- **Description**: Adds a community card to the game state. This method takes a Card object
- **Parameter (`card`)**: The Card object representing the community card to be added to the game state.

### resetCommunityCards

- **Description**: Resets the community cards by clearing the list of community cards. This

### nextPlayer

- **Description**: Advances the turn to the next player in the players list. This method updates

### rotateDealer

- **Description**: Rotates the dealer position to the next player in the players list. This

### getDealer

- **Description**: Retrieves the current dealer based on the dealerIndex. This method returns

### startNewHand

- **Description**: Starts a new hand by resetting the game state for the next round of poker.

## server/state/Pot

### add

- **Description**: The Pot class represents the total amount of chips that players have bet in a
- **Parameter (`chips`)**: The number of chips to add to the pot.

### getAmount

- **Description**: Retrieves the current amount of chips in the pot.

### reset

- **Description**: Resets the pot to zero, typically used at the end of a round or when

## server/state/TableState

### getCurrentBet

- **Description**: The TableState class represents the current state of the poker table during a

### setCurrentBet

- **Description**: Sets the current bet amount for the table. This method is typically called
- **Parameter (`currentBet`)**: The new current bet amount to set for the table.

### getBigBlind

- **Description**: Returns the big blind amount. The big blind serves as a baseline

### setBigBlind

- **Description**: Sets the big blind amount for the game. The big blind is a forced bet that
- **Parameter (`bigBlind`)**: The amount to set as the big blind for the game.

### getMinRaise

- **Description**: Retrieves the minimum raise amount for the current betting round. The

### setMinRaise

- **Description**: Sets the minimum raise amount for the current betting round. This method is
- **Parameter (`minRaise`)**: The new minimum raise amount to set for the current betting round.

### isBettingOpen

- **Description**: Checks if betting is currently open at the table. This status indicates

### setBettingOpen

- **Description**: Sets the betting status for the table. This method can be used to open or
- **Parameter (`bettingOpen`)**: The new betting status to set for the table (true for open, false for closed).

### canReopenBetting

- **Description**: Checks if betting can be reopened after being closed. This status allows for

### setCanReopenBetting

- **Description**: Sets whether betting can be reopened after being closed. This method is
- **Parameter (`canReopenBetting`)**: The new status indicating whether betting can be reopened (true or false).

### getLastAggressorId

- **Description**: Retrieves the ID of the last aggressor in the current betting round. The

### setLastAggressorId

- **Description**: Sets the ID of the last aggressor in the current betting round. This method
- **Parameter (`lastAggressorId`)**: The new ID of the last aggressor to set for the current betting round.

# Protocol Document

This document describes the protocol for client-server communication in our application. It defines the structure of requests and responses, the supported commands along with their request parameters, response formats, and possible errors.

# Table of Contents

- [Protocol Document](#protocol-document)
- [Table of Contents](#table-of-contents)
- [General structure of requests](#general-structure-of-requests)
- [Preconditions](#preconditions)
  - [Parsing](#parsing)
    - [Error Response](#error-response)
  - [Command dispatching](#command-dispatching)
    - [Error Response](#error-response-1)
  - [Command parsing](#command-parsing)
    - [Error Response](#error-response-2)
- [Pre-execution checks](#pre-execution-checks)
  - [UserLoggedInCheck](#userloggedincheck)
    - [Error Response](#error-response-3)
- [Commands](#commands)
  - [PING command](#ping-command)
    - [Required pre-execution checks](#required-pre-execution-checks)
    - [Request Parameters](#request-parameters)
    - [Success Response](#success-response)
    - [Example Request](#example-request)
    - [Example Response](#example-response)
  - [CHECK\_USERNAME command](#check_username-command)
    - [Required pre-execution checks](#required-pre-execution-checks-1)
    - [Request Parameters](#request-parameters-1)
    - [Success Response](#success-response-1)
    - [Example Request](#example-request-1)
    - [Example Response](#example-response-1)
  - [LOGIN command](#login-command)
    - [Required pre-execution checks](#required-pre-execution-checks-2)
    - [Request Parameters](#request-parameters-2)
    - [Success Response](#success-response-2)
    - [Error Response](#error-response-4)
    - [Example Request](#example-request-2)
    - [Example Response](#example-response-2)
  - [CHANGE\_USERNAME command](#change_username-command)
    - [Required pre-execution checks](#required-pre-execution-checks-3)
    - [Request Parameters](#request-parameters-3)
    - [Success Response](#success-response-3)
    - [Error Response](#error-response-5)
    - [Example Request](#example-request-3)
    - [Example Response](#example-response-3)
  - [LOGOUT command](#logout-command)
    - [Required pre-execution checks](#required-pre-execution-checks-4)
    - [Request Parameters](#request-parameters-4)
    - [Success Response](#success-response-4)
    - [Error Response](#error-response-6)
    - [Example Request](#example-request-4)
    - [Example Response](#example-response-4)
  - [LIST\_USERS command](#list_users-command)
    - [Required pre-execution checks](#required-pre-execution-checks-5)
    - [Request Parameters](#request-parameters-5)
    - [Success Response](#success-response-5)
    - [Error Response](#error-response-7)
    - [Example Request](#example-request-5)
    - [Example Response](#example-response-5)
  - [GET_LOBBY_LIST command](#get_lobby_list-command)
    - [Required pre-execution checks](#required-pre-execution-checks)
    - [Request Parameters](#request-parameters)
    - [Success Response](#success-response)
    - [Example Request](#example-request)
    - [Example Response](#example-response)
  - [GET_GAME_STATE command](#get_game_state-command)
    - [Required pre-execution checks](#required-pre-execution-checks)
    - [Request Parameters](#request-parameters)
    - [Success Response](#success-response)
    - [Example Request](#example-request)
    - [Example Response](#example-response)
  - [GET_HIGHSCORES command](#get_highscores-command)
    - [Required pre-execution checks](#required-pre-execution-checks)
    - [Request Parameters](#request-parameters)
    - [Success Response](#success-response)
    - [Example Request](#example-request)
    - [Example Response](#example-response)
  - [CLEAR_HIGHSCORES command](#clear_highscores-command)
    - [Required pre-execution checks](#required-pre-execution-checks)
    - [Request Parameters](#request-parameters)
    - [Success Response](#success-response)
    - [Example Request](#example-request)
    - [Example Response](#example-response)
  - [RAISE command](#raise-command)
  - [CALL command](#call-command)
  - [FOLD command](#fold-command)
  - [BET command](#bet-command)
  - [SEND_MESSAGE command](#send_message-command)
    - [Required pre-execution checks](#required-pre-execution-checks)
    - [Request Parameters](#request-parameters)
    - [Success Response](#success-response)
  - [GET_MESSAGE_COUNT command](#get_message_count-command)
    - [Required pre-execution checks](#required-pre-execution-checks)
    - [Request Parameters](#request-parameters)
    - [Success Response](#success-response)
  - [GET_NEXT_MESSAGE command](#get_next_message-command)
    - [Required pre-execution checks](#required-pre-execution-checks)
    - [Request Parameters](#request-parameters)
    - [Success Response](#success-response)
  - [JOIN_LOBBY command](#join_lobby-command)
    - [Required pre-execution checks](#required-pre-execution-checks)
    - [Request Parameters](#request-parameters)
    - [Success Response](#success-response)
  - [START_GAME command](#start_game-command)
  - [GET_LOBBY_STATUS command](#get_lobby_status-command)
    - [Required pre-execution checks](#required-pre-execution-checks)
    - [Request Parameters](#request-parameters)
    - [Success Response](#success-response)

<!-- Please see the comments for copy ‚ n' paste ready examples -->

# General structure of requests

As mentioned before, our protocol is based on POP3.
Each command is represented as a single line of text, starting with the command name followed by parameters. The server responds with a status line indicating success or failure, followed by the body.

Requests can have parameters that provide additional information for the command. Parameters are key-value pairs separated by an equal sign (`=`).

Responses are collections of key-value pairs, containing either a value or another collection, allowing for nested structures.
Each collection is ended with the `END` keyword.

# Preconditions

The serverside pipeline to process incoming requests consists of multiple stages.
Each of these stages can yield an error response if the request does not meet the requirements of that stage.

## Parsing

One of these stages is the parsing. It is responsible for parsing the raw request into a structured format that can be easily processed by the command handlers. It validates the syntax of the request as well.

### Error Response

| Code            | Description                                                                                     |
| :-------------- | :---------------------------------------------------------------------------------------------- |
| `PARSING_ERROR` | The body of the request contains syntax errors (see message field of response for more details) |

## Command dispatching

After the request has been successfully parsed, the next stage is to dispatch the `PrimitiveRequest` to the appropriate `CommandParser`. This is done by the `CommandDispatcherDispatcher`, which uses the command name to determine which parser to use.

### Error Response

| Code              | Description                                                       |
| :---------------- | :---------------------------------------------------------------- |
| `UNKNOWN_COMMAND` | The command is unknown to this server. No parser has been defined |

## Command parsing

Once the `PrimitiveRequest` has been dispatched to the appropriate `CommandParser`, the parser is responsible for parsing the parameters of the request and creating a `Request` that can be executed by the responsible `CommandHandler`.

### Error Response

| Code                | Description                                                                                       |
| :------------------ | :------------------------------------------------------------------------------------------------ |
| `MISSING_PARAMETER` | A required parameter is missing from the request (see message field of response for more details) |

# Pre-execution checks

Pre-execution checks are reusable validation steps that can be registered on command handlers.
They are implemented as `HandlerCheck` instances and are executed by the `CommandHandlerExecutor` before the handler's main logic is invoked.

<!--
## Name of the check
Description of the check, what it does and when it should be used.

### Response if not met
| Code         | Description              |
| :----------- | :----------------------- |
| `ERROR_CODE` | Description of the error |
-->

## UserLoggedInCheck

The `UserLoggedInCheck` is a common pre-execution check that verifies whether the user is logged in (i.e. has a user associated with his session).

### Error Response

| Code                 | Description               |
| :------------------- | :------------------------ |
| `USER_NOT_LOGGED_IN` | The user is not logged in |

# Commands

Commands are the core of our protocol, representing the various actions that clients can request from the server. Each command has a unique name and may require specific parameters in addition to pre-execution checks.
The server processes these commands and responds accordingly.

<!--
## Name of the command
Description of the command, what it does, and when it should be used.

### Required pre-execution checks
- [`Check1`](#check1)

### Request Parameters
| Parameter Name | Type   | Optional                            | Description           |
| :------------- | :----- | :---------------------------------- | :-------------------- |
| `param1`       | `type` | If the parameter is optional or not | Description of param1 |

### Success Response
| Field    | Type                | Description           |
| :------- | :------------------ | :-------------------- |
| `field1` | `type`              | Description of field1 |
| `field2` | `Collection<type2>` |                       |
| `field3` | `Enum<type3>`       | Description of field3 |

| Fields of `type2` | Type   | Description           |
| :---------------- | :----- | :-------------------- |
| `field1`          | `type` | Description of field1 |

| Members of `type` | Description            |
| :---------------- | :--------------------- |
| `MEMBER1`         | Description of member1 |

### Error Response
| Code         | Description              |
| :----------- | :----------------------- |
| `ERROR_CODE` | Description of the error |

### Example Request
```
COMMAND_NAME PARAM1='value1' PARAM2='value2'
```

### Example Response
```
+OK
  KEY1=VALUE1
  FIELDS
    FIELD
      NESTED_KEY=NESTED_VALUE
  END
  KEY2=VALUE2
END
```
-->

## PING command

The `PING` command is a simple command that can be used to check if the server is responsive.

### Required pre-execution checks

None.

### Request Parameters

No parameters.

### Success Response

No response fields.

### Example Request

```
PING
```

### Example Response

```
+OK
END
```

## CHECK_USERNAME command

The `CHECK_USERNAME` command is used to check if a username is already taken by another user. Additional users can still log in with the same username, but their name will be substituted with a suffix.

### Required pre-execution checks

None.

### Request Parameters

| Parameter Name | Type     | Optional | Description                            |
| :------------- | :------- | :------- | :------------------------------------- |
| `USERNAME`     | `String` | no       | The username to check for availability |

### Success Response

| Field    | Type                         | Description                                                             |
| :------- | :--------------------------- | :---------------------------------------------------------------------- |
| `STATUS` | `Enum<UsernameAvailability>` | Member of enum indicating if the username is available or already taken |

| Members of `UsernameAvailability` | Description                |
| :-------------------------------- | :------------------------- |
| `FREE`                            | Username is available      |
| `TAKEN`                           | Username is already in use |

### Example Request

```
CHECK_USERNAME USERNAME='Lars'
```

### Example Response

```
+OK 
  STATUS=FREE
END
```

## LOGIN command

The `LOGIN` command is used to log in a user with a specified username. If the username is already taken by another user, the server will append a suffix to the username to make it unique.

### Required pre-execution checks

None.

### Request Parameters

| Parameter Name | Type     | Optional | Description                          |
| :------------- | :------- | :------- | :----------------------------------- |
| `USERNAME`     | `String` | no       | The username to create the user with |

### Success Response

| Field      | Type                                                                    | Description                                                           |
| :--------- | :---------------------------------------------------------------------- | :-------------------------------------------------------------------- |
| `USERNAME` | `String`                                                                | Username of the newly created user, can differ from the requested one |
| `ID`       | [`UUID`](https://docs.oracle.com/javase/8/docs/api/java/util/UUID.html) | The ID of the created user                                            |

### Error Response

| Code                | Description                                                                    |
| :------------------ | :----------------------------------------------------------------------------- |
| `ALREADY_LOGGED_IN` | The session is already associated with a user, logging in again is prohibited. |

### Example Request

```
LOGIN USERNAME='Lars'
```

### Example Response

```
+OK
  USERNAME='Lars_1234'
  ID=e47a671e-2b2a-42df-bb82-953fe2ebd307
END
```

## CHANGE_USERNAME command

The `CHANGE_USERNAME` command is used to change the username of an already logged-in user. The request is tied to the current session and updates all affected server-side mappings.

### Required pre-execution checks

None.

### Request Parameters

| Parameter Name | Type     | Optional | Description                           |
| :------------- | :------- | :------- | :------------------------------------ |
| `USERNAME`     | `String` | no       | The new username for the active user  |

### Success Response

| Field      | Type                                                                    | Description                             |
| :--------- | :---------------------------------------------------------------------- | :-------------------------------------- |
| `USERNAME` | `String`                                                                | Effective username after rename         |
| `ID`       | [`UUID`](https://docs.oracle.com/javase/8/docs/api/java/util/UUID.html) | The ID of the renamed user              |

### Error Response

| Code                 | Description                                                           |
| :------------------- | :-------------------------------------------------------------------- |
| `USER_NOT_LOGGED_IN` | No active user is associated with this session.                       |
| `INVALID_USERNAME`   | Username contains disallowed characters or is empty.                  |
| `USERNAME_TAKEN`     | Requested username is already used by another user.                   |
| `RENAME_CONFLICT`    | Rename could not be propagated to all current lobby/game structures.  |

### Example Request

```
CHANGE_USERNAME USERNAME='Lars_New'
```

### Example Response

```
+OK
  USERNAME='Lars_New'
  ID=e47a671e-2b2a-42df-bb82-953fe2ebd307
END
```

## LOGOUT command

Description of the command, what it does, and when it should be used.

### Required pre-execution checks

None.

### Request Parameters

No parameters.

### Success Response

No response fields.

### Error Response

| Code                 | Description                                                      |
| :------------------- | :--------------------------------------------------------------- |
| `NO_USER_ASSOCIATED` | The session has no user associated, logging out is not possible. |

### Example Request

```
LOGOUT
```

### Example Response

```
+OK
END
```

## LIST_USERS command

The `LIST_USERS` command is used to retrieve a list of all currently logged-in users.

### Required pre-execution checks

None.

### Request Parameters

No parameters.

### Success Response

| Field   | Type               | Description                              |
| :------ | :----------------- | :--------------------------------------- |
| `USERS` | `Collection<User>` | Collection of all users currently online |

| Fields of `User` | Type                                                                    | Description                                                           |
| :--------------- | :---------------------------------------------------------------------- | :-------------------------------------------------------------------- |
| `USERNAME`       | `String`                                                                | Username of the newly created user, can differ from the requested one |
| `ID`             | [`UUID`](https://docs.oracle.com/javase/8/docs/api/java/util/UUID.html) | The ID of the created user                                            |

### Error Response

None.

### Example Request

```
LIST_USERS
```

### Example Response

```
+OK
  USERS
    USER
      USERNAME=Lars_001
      ID=56765d0f-8cd3-4eec-91b2-7e36265c1a5d
    END
    USER
      USERNAME=Lars_002
      ID=b7bbd9b3-0d49-4c92-8306-b1c8506d2ff0
    END
    USER
      USERNAME=Lars
      ID=982bc78e-547f-495a-a821-433a3603f92c
    END
  END
END
```

## SEND_MESSAGE command

The `SEND_MESSAGE` command is used to transfer the chat message sent by a user to the server.

### Required pre-execution checks

None.

### Request Parameters

| Field    | Type            | Description                                                        |
|:---------|:----------------|:-------------------------------------------------------------------|
| `TYPE`   | `Enum<ChatType` | Member of Enum, indicating with chat type is used                  |
| `GAME`   | `int`           | ID for identifying the Lobby Chat                                  |
| `USER`   | `String`        | Username of the player that sent the message                       |
| `TARGET` | `String`        | Username of to which the message is sent to (only in Whisper Chat) |
| `TIME`   | `String`        | Timestamp, when the message was sent                               |
| `TEXT`   | `String`        | Content of the message                                             |

| Members of `ChatType` | Description                     |
|:----------------------|:--------------------------------|
| `GLOBAL`              | Message is for the global chat  |
| `LOBBY`               | Message is for the lobby chat   |
| `WHISPER`             | Message is for the whisper chat |

### Success Response

No response fields.

### Error Response

None.

### Example Request

```
SEND_MESSAGE TYPE=GLOBAL GAME=1 USER=player1 TARGET=null TIME='10:30' TEXT='Hello World'
```

### Example Response

```
+OK
END
```

## GET_MESSAGE_COUNT command

The `GET_MESSAGE_COUNT` is used to get the current number of messages that are stored in the queue for a client.

### Required pre-execution checks

None.

### Request Parameters

No parameters.

### Success Response

| Field   | Type  | Description                    |
|:--------|:------|:-------------------------------|
| `COUNT` | `int` | The current number of messages |

### Error Response

| Code                 | Description                                                                      |
| :------------------- |:---------------------------------------------------------------------------------|
| `NO_USER_ASSOCIATED` | The session has no user associated, there is no queue of messages for the client |

### Example Request

```
GET_MESSAGE_COUNT
```

### Example Response

```
+OK
COUNT=10
END
```

## GET_NEXT_MESSAGE command

The `GET_NEXT_MESSAGE` command is used to get the next message stored in a queue for the client.

### Required pre-execution checks

None.

### Request Parameters

No parameters.

### Success Response

| Field    | Type            | Description                                                        |
|:---------|:----------------|:-------------------------------------------------------------------|
| `TYPE`   | `Enum<ChatType` | Member of Enum, indicating with chat type is used                  |
| `GAME`   | `int`           | ID for identifying the Lobby Chat                                  |
| `USER`   | `String`        | Username of the player that sent the message                       |
| `TARGET` | `String`        | Username of to which the message is sent to (only in Whisper Chat) |
| `TIME`   | `String`        | Timestamp, when the message was sent                               |
| `TEXT`   | `String`        | Content of the message                                             |

| Members of `ChatType` | Description                     |
|:----------------------|:--------------------------------|
| `GLOBAL`              | Message is for the global chat  |
| `LOBBY`               | Message is for the lobby chat   |
| `WHISPER`             | Message is for the whisper chat |

### Error Response

| Code                 | Description                                                                      |
| :------------------- |:---------------------------------------------------------------------------------|
| `NO_USER_ASSOCIATED` | The session has no user associated, there is no queue of messages for the client |

### Example Request

```
GET_NEXT_MESSAGE
```

### Example Response

```
+OK
TYPE=GLOBAL GAME=-1 USER=player1 TARGET=null TIME=9:30 TEXT="Guten Tag"
END
```

### Example Response (success)

```
+OK
END
```

## JOIN_LOBBY command

The `JOIN_LOBBY` command requests the server to add the currently logged-in user to the lobby with the given identifier. The server resolves the username from the session; clients must only provide the `ID` parameter.

### Required pre-execution checks

- [`UserLoggedInCheck`](#userloggedincheck)

### Request Parameters

| Parameter Name | Type | Optional | Description |
| :------------- | :--- | :------: | :---------- |
| `ID` | `int` | no | Numeric id of the target lobby |

### Implementation notes

- Parser: `JoinLobbyParser` — reads the `ID` parameter and builds `JoinLobbyRequest`.
- Handler: `JoinLobbyHandler` — resolves the username from `UserRegistry` using the session id, attempts to add the user to the lobby via `LobbyManager.addPlayerToLobby(...)` and returns appropriate responses.

### Success Response

No additional response fields. The server replies with a simple `+OK` on success.

### Error Response

| Code | Description |
| :--- | :---------- |
| `USER_NOT_LOGGED_IN` | No user associated with the session (pre-execution check failed) |
| `LOBBY_NOT_FOUND` | The specified lobby id does not exist |
| `LOBBY_FULL_OR_ALREADY_IN` | Lobby is full or the user is already in the lobby |

### Example Request

```
JOIN_LOBBY ID=1
```

### Example Response (success)

```
+OK
END
```

### Example Response (error)

```
-ERR
  CODE=LOBBY_NOT_FOUND
  MSG=Lobby not found
END

## START_GAME command

The `START_GAME` command requests the server to start a new game in the specified lobby. The
server requires an explicit numeric `ID` parameter identifying the target lobby.

### Required pre-execution checks
- [`UserLoggedInCheck`](#userloggedincheck)

### Request Parameters
| Parameter Name | Type | Optional | Description |
| :------------- | :--- | :------: | :---------- |
| `ID` | `int` | no | Numeric id of the target lobby |

### Implementation notes

- Parser: `StartGameParser` — reads the required `ID` parameter and builds `StartGameRequest`.
- Handler: `StartGameHandler` — resolves the lobby by id, checks the requester is a member of the
  lobby, verifies a game is not already running and that enough players are present, initializes
  `GameController` and attaches it to the lobby. On success a structured `START_GAME` success
  response containing `GAME` and `MESSAGE` fields is returned.

### Success Response
| Field    | Type    | Description                       |
| :------- | :------ | :-------------------------------- |
| `GAME`   | `int`   | Numeric id of the started game (uses lobby id) |
| `MESSAGE`| `String`| Human-readable status message     |

### Error Response
| Code             | Description                               |
| :--------------- | :---------------------------------------- |
| `MISSING_PARAMETER` | Required parameter `ID` missing (parser) |
| `LOBBY_NOT_FOUND` | The specified lobby id does not exist     |
| `NOT_IN_LOBBY`    | Requesting user is not a member of the lobby |
| `NOT_ENOUGH_PLAYERS` | Not enough players to start the game   |
| `ALREADY_STARTED` | A game is already running in the lobby   |
| `USER_NOT_LOGGED_IN` | No user associated with the session    |

### Example Request

```

START_GAME ID=1

```

### Example Response (success)

```

+OK
  GAME=1
  MESSAGE=Game started successfully
END

```
```

## GET_LOBBY_LIST command

The `GET_LOBBY_LIST` command requests the server to return a list of currently available lobbies with basic metadata.

### Required pre-execution checks

None.

### Request Parameters

No parameters.

### Implementation notes

- Parser: `GetLobbyListParser` — creates `GetLobbyListRequest` (no parameters).
- Handler: `GetLobbyListHandler` — queries `LobbyManager#getAllLobbies()` and returns `GetLobbyListResponse`.
- Response: `GetLobbyListResponse` — builds a `LOBBIES` collection with repeated `LOBBY` blocks containing `ID`, `NAME`, `PLAYER_COUNT`.

### Success Response

The response contains a `LOBBIES` collection with nested `LOBBY` entries.

Example response structure:

```
+OK
  LOBBIES
    LOBBY
      ID=1
      NAME=Room 1
      PLAYER_COUNT=2
    END
    LOBBY
      ID=2
      NAME=Room 2
      PLAYER_COUNT=3
    END
  END
END
```

### Example Request

```
GET_LOBBY_LIST
```

### Example Response (error)

```
-ERR
  CODE=NO_LOBBIES_AVAILABLE
  MESSAGE=No lobbies available
END
```

## GET_GAME_STATE command

The `GET_GAME_STATE` command returns a snapshot of the current game for a lobby. It includes global fields (`PHASE`, `POT`, `CURRENT_BET`, `DEALER`, `ACTIVE_PLAYER`), repeated `CARD` blocks for community cards and repeated `PLAYER` blocks for per-player information. If the requester is a player in the game, their hole cards are included inside their `PLAYER` block as nested `CARD` blocks.

### Required pre-execution checks

- [`UserLoggedInCheck`](#userloggedincheck)

### Request Parameters

| Parameter Name | Type   | Optional | Description |
| :------------- | :----- | :------: | :---------- |
| `GAME_ID`      | `int`  | yes      | Numeric id of the lobby/game to query. If omitted the server will resolve the lobby by the requesting session's associated user.
| `USERNAME`     | `String` | yes   | Optional username to query the game state for (server will also accept session resolution).

### Implementation notes

- Parser: `GetGameStateParser` — accepts optional `GAME_ID` and `USERNAME` and builds `GetGameStateRequest`.
- Handler: `GetGameStateHandler` — resolves the target lobby by `GAME_ID` when provided, otherwise by `USERNAME` or session user; if a game is running a `GetGameStateResponse` is returned.
- Response: `GetGameStateResponse` — uses repeated `CARD` and `PLAYER` blocks to match the client parser expectations.

### Success Response

Top-level fields and collections (example):

```
+OK
  PHASE=PRE_FLOP
  POT=150
  CURRENT_BET=50
  DEALER=2
  ACTIVE_PLAYER=1
  CARD
    VALUE=K
    SUIT=H
  END
  PLAYER
    NAME=player1
    CHIPS=1000
    BET=50
    STATE=ACTIVE
    CARD
      VALUE=A
      SUIT=S
    END
  END
END
```

### Example Request

```
GET_GAME_STATE GAME_ID=1
```

### Example Response (error)

```
-ERR
  CODE=GAME_NOT_STARTED
  MESSAGE=Game not started
END
```

## BET command

The `BET` command lets the currently logged-in player place a bet in the ongoing game for their lobby.

### Required pre-execution checks

- [`UserLoggedInCheck`](#userloggedincheck)

### Request Parameters

| Parameter Name | Type | Optional | Description |
| :------------- | :--- | :------: | :---------- |
| `GAME_ID` | `int` | yes | Numeric id of the lobby/game to target. If omitted the server resolves the lobby by the requesting session's user. |
| `AMOUNT` | `int` | no | Amount the player wants to bet |

### Implementation notes

- Parser: `PlayerBetParser` — reads `AMOUNT` and optionally `GAME_ID`.
- Handler: `PlayerBetHandler` — validates the session, lobby (by id when provided or else by session), player's turn and balance and forwards to `GameController`.

### Success Response

No additional response fields. Server replies with `+OK` on success.

### Error Response

| Code | Description |
| :--- | :---------- |
| `NOT_YOUR_TURN` | The player attempted to bet when not their turn |
| `INSUFFICIENT_FUNDS` | Player does not have enough chips |
| `INVALID_AMOUNT` | Amount parameter is invalid |
| `GAME_NOT_STARTED` | No game is running in the lobby |
| `NOT_IN_LOBBY` | Requesting user is not a member of the lobby |
| `LOBBY_NOT_FOUND` | The specified `GAME_ID` does not exist |

### Example Request

```
BET GAME_ID=1 AMOUNT=50
```

### Example Response (success)

```
+OK
END
```

### Example Response (error)

```
-ERR
  CODE=INSUFFICIENT_FUNDS
  MSG=Not enough chips
END
```

## RAISE command

The `RAISE` command lets the currently logged-in player increase the current bet in the ongoing game for their lobby.

### Required pre-execution checks

- [`UserLoggedInCheck`](#userloggedincheck)

### Request Parameters

| Parameter Name | Type | Optional | Description |
| :------------- | :--- | :------: | :---------- |
| `GAME_ID` | `int` | yes | Numeric id of the lobby/game to target. If omitted the server resolves the lobby by the requesting session's user. |
| `AMOUNT` | `int` | no | Amount the player wants to raise |

### Implementation notes

- Parser: `PlayerRaiseParser` — reads `AMOUNT` and optionally `GAME_ID`.
- Handler: `PlayerRaiseHandler` — validates the session, lobby (by id when provided or else by session), player's turn and balance and forwards to `GameController`.

### Success Response

No additional response fields. Server replies with `+OK` on success.

### Error Response

| Code | Description |
| :--- | :---------- |
| `NOT_YOUR_TURN` | The player attempted to raise when not their turn |
| `INSUFFICIENT_FUNDS` | Player does not have enough chips |
| `INVALID_AMOUNT` | Amount parameter is invalid |
| `GAME_NOT_STARTED` | No game is running in the lobby |
| `NOT_IN_LOBBY` | Requesting user is not a member of the lobby |
| `LOBBY_NOT_FOUND` | The specified `GAME_ID` does not exist |

### Example Request

```
RAISE GAME_ID=1 AMOUNT=100
```

### Example Response (success)

```
+OK
END
```

### Example Response (error)

```
-ERR
  CODE=INSUFFICIENT_FUNDS
  MSG=Not enough chips
END
```

## CALL command

The `CALL` command lets the currently logged-in player match the current bet (call) in the ongoing game for their lobby.

### Required pre-execution checks

- [`UserLoggedInCheck`](#userloggedincheck)

### Request Parameters

| Parameter Name | Type | Optional | Description |
| :------------- | :--- | :------: | :---------- |
| `GAME_ID` | `int` | yes | Numeric id of the lobby/game to target. If omitted the server resolves the lobby by the requesting session's user. |

### Implementation notes

- Parser: `PlayerCallParser` — accepts optional `GAME_ID`.
- Handler: `PlayerCallHandler` — validates the session, resolves the lobby by id when provided or by session otherwise and forwards to `GameController`.

### Success Response

No additional response fields. Server replies with `+OK` on success.

### Error Response

| Code | Description |
| :--- | :---------- |
| `NOT_YOUR_TURN` | The player attempted to call when not their turn |
| `INSUFFICIENT_FUNDS` | Player does not have enough chips |
| `GAME_NOT_STARTED` | No game is running in the lobby |
| `NOT_IN_LOBBY` | Requesting user is not a member of the lobby |
| `LOBBY_NOT_FOUND` | The specified `GAME_ID` does not exist |

### Example Request

```
CALL GAME_ID=1
```

### Example Response (success)

```
+OK
END
```

### Example Response (error)

```
-ERR
  CODE=NOT_YOUR_TURN
  MSG=It is not your turn
END
```

## FOLD command

The `FOLD` command lets the currently logged-in player leave the current hand (fold) in the ongoing game for their lobby.

### Required pre-execution checks

- [`UserLoggedInCheck`](#userloggedincheck)

### Request Parameters

| Parameter Name | Type | Optional | Description |
| :------------- | :--- | :------: | :---------- |
| `GAME_ID` | `int` | yes | Numeric id of the lobby/game to target. If omitted the server resolves the lobby by the requesting session's user. |

### Implementation notes

- Parser: `PlayerFoldParser` — accepts optional `GAME_ID`.
- Handler: `PlayerFoldHandler` — validates the session, resolves the lobby by id when provided or by session otherwise and forwards to `GameController`.

### Success Response

No additional response fields. Server replies with `+OK` on success.

### Error Response

| Code | Description |
| :--- | :---------- |
| `NOT_YOUR_TURN` | The player attempted to fold when not their turn |
| `GAME_NOT_STARTED` | No game is running in the lobby |
| `NOT_IN_LOBBY` | Requesting user is not a member of the lobby |
| `LOBBY_NOT_FOUND` | The specified `GAME_ID` does not exist |

### Example Request

```
FOLD GAME_ID=1
```

### Example Response (success)

```
+OK
END
```

### Example Response (error)

```
-ERR
  CODE=NOT_YOUR_TURN
  MSG=It is not your turn
END
```

## FOLD command

The `FOLD` command lets the currently logged-in player leave the current hand (fold) in the ongoing game for their lobby.

### Required pre-execution checks

- [`UserLoggedInCheck`](#userloggedincheck)

### Request Parameters

| Parameter Name | Type | Optional | Description |
| :------------- | :--- | :------: | :---------- |
| `GAME_ID` | `int` | yes | Numeric id of the lobby/game to target. If omitted the server resolves the lobby by the requesting session's user. |

### Implementation notes

- Parser: `PlayerFoldParser` — accepts optional `GAME_ID`.
- Handler: `PlayerFoldHandler` — validates the session, resolves the lobby by id when provided or by session otherwise and forwards to `GameController`.

### Success Response

No additional response fields. Server replies with `+OK` on success.

### Error Response

| Code | Description |
| :--- | :---------- |
| `NOT_YOUR_TURN` | The player attempted to fold when not their turn |
| `GAME_NOT_STARTED` | No game is running in the lobby |
| `NOT_IN_LOBBY` | Requesting user is not a member of the lobby |
| `LOBBY_NOT_FOUND` | The specified `GAME_ID` does not exist |

### Example Request

```
FOLD GAME_ID=1
```

### Example Response (success)

```
+OK
END
```

### Example Response (error)

```
-ERR
  CODE=NOT_YOUR_TURN
  MSG=It is not your turn
END
```

## FOLD command

The `FOLD` command lets the currently logged-in player leave the current hand (fold) in the ongoing game for their lobby.

### Required pre-execution checks

- [`UserLoggedInCheck`](#userloggedincheck)

### Request Parameters

| Parameter Name | Type | Optional | Description |
| :------------- | :--- | :------: | :---------- |
| `GAME_ID` | `int` | yes | Numeric id of the lobby/game to target. If omitted the server resolves the lobby by the requesting session's user. |

### Implementation notes

- Parser: `PlayerFoldParser` — accepts optional `GAME_ID`.
- Handler: `PlayerFoldHandler` — validates the session, resolves the lobby by id when provided or by session otherwise and forwards to `GameController`.

### Success Response

No additional response fields. Server replies with `+OK` on success.

### Error Response

| Code | Description |
| :--- | :---------- |
| `NOT_YOUR_TURN` | The player attempted to fold when not their turn |
| `GAME_NOT_STARTED` | No game is running in the lobby |
| `NOT_IN_LOBBY` | Requesting user is not a member of the lobby |
| `LOBBY_NOT_FOUND` | The specified `GAME_ID` does not exist |

### Example Request

```
FOLD GAME_ID=1
```

### Example Response (success)

```
+OK
END
```

### Example Response (error)

```
-ERR
  CODE=NOT_YOUR_TURN
  MSG=It is not your turn
END
```

## FOLD command

The `FOLD` command lets the currently logged-in player leave the current hand (fold) in the ongoing game for their lobby.

### Required pre-execution checks

- [`UserLoggedInCheck`](#userloggedincheck)

### Request Parameters

| Parameter Name | Type | Optional | Description |
| :------------- | :--- | :------: | :---------- |
| `GAME_ID` | `int` | yes | Numeric id of the lobby/game to target. If omitted the server resolves the lobby by the requesting session's user. |

### Implementation notes

- Parser: `PlayerFoldParser` — accepts optional `GAME_ID`.
- Handler: `PlayerFoldHandler` — validates the session, resolves the lobby by id when provided or by session otherwise and forwards to `GameController`.

### Success Response

No additional response fields. Server replies with `+OK` on success.

### Error Response

| Code | Description |
| :--- | :---------- |
| `NOT_YOUR_TURN` | The player attempted to fold when not their turn |
| `GAME_NOT_STARTED` | No game is running in the lobby |
| `NOT_IN_LOBBY` | Requesting user is not a member of the lobby |
| `LOBBY_NOT_FOUND` | The specified `GAME_ID` does not exist |

### Example Request

```
FOLD GAME_ID=1
```

### Example Response (success)

```
+OK
END
```

### Example Response (error)

```
-ERR
  CODE=NOT_YOUR_TURN
  MSG=It is not your turn
END
```

## GET_LOBBY_STATUS command

The `GET_LOBBY_STATUS` command requests the server to return the current state of a lobby, including the list of players and their ready state.

### Required pre-execution checks

None.

### Request Parameters

| Parameter Name | Type | Optional | Description |
| :------------- | :--- | :------: | :---------- |
| `ID` | `int` | no | Numeric id of the target lobby |

### Implementation notes

- Parser: `GetLobbyStatusParser` — reads the `ID` parameter and builds `GetLobbyStatusRequest`.
- Handler: `GetLobbyStatusHandler` — queries `LobbyManager` for the lobby and builds a `GetLobbyStatusResponse` containing player entries.

### Success Response

The response contains a `LOBBY` collection with nested `PLAYERS` and one or more `PLAYER` entries. Each `PLAYER` entry contains `USERNAME` and `READY` fields.

Example response structure:

```
+OK
  LOBBY
    ID=1
    PLAYERS
      PLAYER
        USERNAME=Lars_001
        READY=false
      END
      PLAYER
        USERNAME=Anna
        READY=true
      END
    END
  END
END
```

### Error Response

| Code | Description |
| :--- | :---------- |
| `LOBBY_NOT_FOUND` | The specified lobby id does not exist |

### Example Request

```
GET_LOBBY_STATUS ID=1
```

### Example Response (error)

```
-ERR
  CODE=LOBBY_NOT_FOUND
  MESSAGE=Lobby not found
END
```

## CREATE_LOBBY command

The `CREATE_LOBBY` command requests the server to create a new lobby and return its identifier.

### Required pre-execution checks

None.

### Request Parameters

No parameters.

### Implementation notes

- Parser: CreateLobbyParser — constructs a CreateLobbyRequest from the incoming PrimitiveRequest context (no parameters are read).
- Handler: CreateLobbyHandler — calls LobbyManager.createNewLobby(null).
  - If the returned LobbyId is null, the handler dispatches an ErrorResponse with code `LOBBIES_FULL` and message "Maximum number of 8 lobbies reached".
  - On success, the handler dispatches a CreateLobbyResponse containing the new lobby id.

### Success Response

| Field     | Type | Description |
| :-------- | :--- | :---------- |
| `LOBBY_ID`| `int`| Numeric id of the newly created lobby |

### Error Response

| Code        | Description                                                            |
| :---------- | :--------------------------------------------------------------------- |
| `LOBBIES_FULL` | The server cannot create a new lobby because the maximum number of lobbies has been reached |

### Example Request

```
CREATE_LOBBY
```

### Example Success Response

```
+OK
  LOBBY_ID=1
END
```

### Example Error Response

```
-ERR
  CODE=LOBBIES_FULL
  MESSAGE=Maximum number of 8 lobbies reached
END
```

## GET_HIGHSCORES command

The `GET_HIGHSCORES` command returns the stored highscore list. The server sends a `HIGHSCORES` block containing repeated `HIGHSCORE` fields. Each entry is already formatted as `timestamp | winner name`.

### Required pre-execution checks

None.

### Request Parameters

No parameters.

### Success Response

| Field        | Type                    | Description                              |
| :----------- | :---------------------- | :--------------------------------------- |
| `HIGHSCORES` | `Collection<Highscore>` | Collection of formatted highscore lines. |

| Fields of `Highscore` | Type     | Description                                         |
| :-------------------- | :------- | :-------------------------------------------------- |
| `HIGHSCORE`           | `String` | One formatted entry in `yyyy-MM-dd HH:mm:ss | name` |

### Example Request

```
GET_HIGHSCORES
```

### Example Response

```
+OK
  HIGHSCORES
    HIGHSCORE='2026-04-21 15:19:23 | Jona'
    HIGHSCORE='2026-04-21 16:02:11 | Lars'
  END
END
```

## CLEAR_HIGHSCORES command

The `CLEAR_HIGHSCORES` command deletes all stored highscore entries on the server.

### Required pre-execution checks

None.

### Request Parameters

No parameters.

### Success Response

No response fields.

### Example Request

```
CLEAR_HIGHSCORES
```

### Example Response

```
+OK
END
```

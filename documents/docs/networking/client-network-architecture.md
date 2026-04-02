# Client Nework Architecture
<!-- vim-markdown-toc GFM -->

* [Architecture Overview](#architecture-overview)
    * [network/Card.java](#networkcardjava)
    * [network/GameState.java](#networkgamestatejava)
    * [network/Player.java](#networkplayerjava)
    * [network/ChatClient.java](#networkchatclientjava)
        * [ChatClient(ClientService clientService)](#chatclientclientservice-clientservice)
        * [sendMessage(Message message)](#sendmessagemessage-message)
        * [getMessages()](#getmessages)
    * [network/ClientService.java](#networkclientservicejava)
        * [ClientService(String ip, int port)](#clientservicestring-ip-int-port)
        * [processCommand(String message)](#processcommandstring-message)
        * [sendRequest(Runnable request)](#sendrequestrunnable-request)
        * [getRuntimeException(Exception e)](#getruntimeexceptionexception-e)
        * [closeSocket()](#closesocket)
        * [writeToTransport(String s) throws IOException](#writetotransportstring-s-throws-ioexception)
    * [network/CoreClient.java](#networkcoreclientjava)
        * [CoreClient(ClientService clientservice)](#coreclientclientservice-clientservice)
        * [ping()](#ping)
        * [login(String user)](#loginstring-user)
    * [network/GameClient.java](#networkgameclientjava)
        * [GameClient(ClientService client)](#gameclientclientservice-client)
        * [getGameState()](#getgamestate)
        * [parseGameState(String input)](#parsegamestatestring-input)
        * [Example server response](#example-server-response)
    * [network/LobbyClient.java](#networklobbyclientjava)
        * [LobbyClient(ClientService client)](#lobbyclientclientservice-client)
        * [fetchLobbyStatusString(int lobbyId)](#fetchlobbystatusstringint-lobbyid)
        * [createLobby()](#createlobby)
        * [getLobbyId()](#getlobbyid)
        * [joinLobby(int lobbyId)](#joinlobbyint-lobbyid)

<!-- vim-markdown-toc -->

## Architecture Overview

```text
client/
 ├── game/
 │    ├── Card.java
 │    ├── GameState.java
 │    └── Player.java
 │
 └── network/
      ├── ChatClient.java
      ├── ClientService.java
      ├── CoreClient.java
      ├── GameClient.java
      └── LobbyClient.java
```

### game/Card.java

Represents a playing card with a value and suit.

### game/GameState.java

Represents the current state of the poker game, including the phase, pot size, current bet, dealer position, active player, community cards, and player information.

### game/Player.java

Represents a player in the poker game, including their name, chip count, current bet, state (e.g., `active`, `folded`), and their hole cards.

### network/ChatClient.java

The ChatClient class is responsible for sending messages to the server and retrieving messages from the server. It uses the ClientService to send commands and receive responses from the server.

#### ChatClient(ClientService clientService)

Constructs a ChatClient with the given ClientService for communication.
 
- **Parameter (`clientService`)**: The ClientService instance used to send commands and receive responses from the server.

#### sendMessage(Message message)

Send a Message to the server by converting it to a string format and sending a `SEND_MESSAGE` command with the message content as arguments.
 
- **Parameter (`message`)**: message The Message object to be sent to the server.

#### getMessages()

Retrieve messages from the server by first sending a `GET_MESSAGE_COUNT` command to determine how many messages are available and then sending `GET_NEXT_MESSAGE` commands in a loop to retrieve each message. The retrieved messages are parsed into Message objects and returned as a list.
 
- **Parameter (`A`)**: list of Message objects representing the messages retrieved from the server.

### network/ClientService.java

The ClientService class is responsible for managing the connection to the server, sending commands, and receiving responses. It uses a TcpTransport to
communicate with the server and an ExecutorService to handle asynchronous requests.

#### ClientService(String ip, int port)

Constructs a ClientService with the given server IP and port. It establishes a socket connection to the server and initializes the TcpTransport and ExecutorService for communication.

- **Parameter (`ip`)**: The IP address of the server to connect to.
- **Parameter (`port`)**: The port number of the server to connect to.

#### processCommand(String message)

Sends a command to the server and waits for the response. The command is sent using the TcpTransport, and the response is read in a loop until a valid response is received. The method handles `+OK` and `-ERROR` responses from the server and returns the actual response content.

- **Parameter (`message`)**: The command message to be sent to the server.
- **Return**: The response from the server as a string.

#### sendRequest(Runnable request)

Helper method to send a request to the server using the ExecutorService. It submits the request as a Runnable task and waits for its completion. If the task is interrupted or encounters an execution exception, it throws a
RuntimeException with the appropriate cause.
 
- **Parameter (`request`)**: The Runnable task representing the request to be sent to the server.

#### getRuntimeException(Exception e)

Helper method to extract the cause of an exception and return it as a RuntimeException. If the cause is null, it returns the original exception as a RuntimeException. If the cause is already a RuntimeException, it returns it directly. Otherwise, it wraps the cause in a new RuntimeException and returns it.
 
- **Parameter (`e`)**: The exception from which to extract the cause.
- **Return**: A RuntimeException representing the cause of the original exception.

#### closeSocket()

Closes the socket connection to the server and shuts down the ExecutorService. It also closes the TcpTransport used for communication. If any IOException occurs during this process, it prints the exception to the console.

#### writeToTransport(String s) throws IOException

Helper method to write a command string to the TcpTransport. It generates a unique ID for the command using the idGenerator and sends a RawPacket containing the ID and the command string to the server. If an IOException occurs during this process, it throws a RuntimeException with the cause.
 
- **Parameter (`s`)**: The command string to be sent to the server.
- **Throws IOException**: If an I/O error occurs while writing to the transport.

### network/CoreClient.java

The CoreClient class provides basic functionalities for communicating with the server, such as sending a ping command to check connectivity and logging in with a username. It uses the ClientService to send commands and receive responses from the server.

#### CoreClient(ClientService clientservice)

Constructs a CoreClient with the given ClientService for communication.

- **Parameter (`clientservice`)**: The ClientService instance used to send commands and receive responses from the server.

#### ping()

Sends a `PING` command to the server to check connectivity. The server should respond with a `PONG` message if the connection is successful.

#### login(String user)

Logs in to the server with the given username by sending a `LOGIN` command.

- **Parameter (`user`)**: The username to log in with.

### network/GameClient.java

The GameClient class is responsible for communicating with the server to retrieve the current game state. It sends a command to the server and parses the response into a structured GameState object.

#### GameClient(ClientService client)

Constructs a GameClient with the given ClientService for communication.

- **Parameter (`client`)**: The ClientService instance used to send commands and receive responses from the server.

#### getGameState()

Retrieves the current game state from the server by sending a command and parsing the response.

- **Return**: A GameState object representing the current state of the game.

#### parseGameState(String input)

Parses the raw response from the server into a structured GameState object.

- **Parameter (`input`)**: The raw response string from the server.
- **Return**: A GameState object representing the current state of the game.

#### Example server response

```text
+OK
    PHASE=FLO P
    POT=150
    CURRENT_BET=50
    DEALER=0
    ACTIVE_PLAYER=1
    CARDS
        CARD 
            VALUE=10
            SUIT=H
        CARD 
            VALUE=7
            SUIT=S
        CARD 
            VALUE=A
            SUIT=D
    PLAYERS
        PLAYER
            NAME=Max
            CHIPS=1200
            BET=50
            STATE=ACTIVE
            CARDS
                CARD 
                    VALUE=K
                    SUIT=H
                CARD 
                    VALUE=3
                    SUIT=C
        PLAYER
            NAME=Anna
            CHIPS=800
            BET=0
            STATE=FOLDED
            CARDS
END
```

### network/LobbyClient.java

The LobbyClient class is responsible for communicating with the server to manage game lobbies. It provides methods to create a lobby, join a lobby, and fetch the current status of a lobby by sending appropriate commands to the server and processing the responses.

#### LobbyClient(ClientService client)

Constructs a LobbyClient with the given ClientService for communication.

- **Parameter (`client`)**: The ClientService instance used to send commands and receive responses from the server.

#### fetchLobbyStatusString(int lobbyId)

Fetch the current status of the lobby with the given id from the server.

- **Parameter (`lobbyId`)**: The id of the lobby to fetch the status for.
- **Return**: A string representing the current status of the lobby, as returned by the server.

#### createLobby()

Request the server to create a new lobby and return the id of the newly created lobby.

- **Return**: The id of the newly created lobby, as returned by the server.

#### getLobbyId()

Request the server to return the id of the lobby that the client is currently in.

- **Return**: The id of the lobby that the client is currently in, as returned by the server.

#### joinLobby(int lobbyId)

Request the server to join the lobby with the given id.
 
- **Parameter (`lobbyId`)**: The id of the lobby to join.

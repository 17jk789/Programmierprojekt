# Server-Side Networking — How It All Fits Together

This doc is meant to give a solid mixed-level understanding of how our server-side networking works. No deep dives — just a clear picture of what's happening under the hood when a client connects and sends a command.

> **Note:** This is v1. Annotations and Reflection-based dispatching are on the roadmap but not covered here yet.

> **Disclamer:** This document has been written by Claude. I modified certain parts and verified its contents for correctness.

## Our Protocol at a Glance

Our protocol is inspired by **POP3** - a classic, text-based protocol that communicates over a raw TCP connection. The idea is simple: the client sends a command as a plain-text string, and the server responds with either a success or an error.

A typical exchange looks something like this:

```
Client  →  GET_DELTA SINCE=42
Server  →  +OK
           PLAYER_FOLDED playerId=3
           POT 240
           NEXT_TURN playerId=1
           .
```

Responses start with `+OK` on success or `-ERR` when something goes wrong. Commands are short, uppercase strings - sometimes followed by arguments.

We don't use HTTP, there's no JSON body, no headers. Just a raw socket, a text stream, and a clearly defined set of commands.

## Core Components & Their Roles

Here's a quick rundown of the main building blocks:

### `NetworkManager`
This is the entry point. It binds to a specific port and listens for incoming TCP connections.
Once a client connects, it creates a `Session` adds it to the `SessionManager` and goes back to waiting. Its not the responsibility of the `NetworkManager` to recieve and send data from and to each connected client.

### `Session`
Every client that connects has its own `Session` instance running on its own thread. This component owns the lifecycle of that connection: it reads incoming data, passes it along for processing, and writes responses back.

###  `SessionId`
The `SessionId` is used to uniquely identify each session. Essentially its a wrapper arround the UUID type.

### `SessionManager`
The `SessionManager` stores all currently active sessions. It allows for the retrieval of a specific session by its id.

### `ProtocolParser`
Raw text (encapsulated in a `RawRequest`) coming off the socket isn't immediately useful — the `ProtocolParser` turns it with the help of the `Tokenizer` into a `PrimitiveRequest`.

### `RawRequest`
The `RawRequest` object is created after a message has been recieved.
It holds both a reference to the `Session` that recieved the message and the message itself.

### `PrimitiveRequest`
The `PrimitiveRequest` object is created by the `ProtocolParser` after the content of the `RawRequest` has been tokenized by the `Tokenizer`.
The command is stored in its own attribute and the arguments are accessible as a dictionary.

### `Tokenizer`
The job of the `tokenizer` is it, to take the raw string and turn it into tokens. 
For example, the command `CHAT_LOBBY GAME=12 MESSAGE='All-in?'` will be decoded as `"CHAT_LOBBY", "GAME", "=", "12", "MESSAGE", "=", "All-in?"`.
It is unaware about the meaning.

### `CommandParser` (per command)
Takes in the `PrimitiveRequest` and checks if all required fields are provided with the correct value.
Creates a command specific `Request` object containing command arguments in a structured manner.

### `Request` (per command)
Has fields common along each implementation such as a reference to the `Session` that recieved the request.
Furthermore each implementation has fields unique to each command. For example, the `ChatLobbyRequest` has fields for the game and the message.

### `CommandRouter`
By identifying the type of class of the `Request`, the `CommandRouter` routes the request to the matching `CommandHandler`.

### `CommandHandler` (per command)
Each supported command has its own handler — a small, focused class that contains the logic for that specific command. They have access to the domain, containing inner parts of the game itself.

### `Response` (per response)
The interface has two sub-interfaces for either failed `ErrorResponse` or successfull `SuccessResponse` execution of commands.

Each step has exactly one responsibility. Increasing the ability to test and extend different components later on.

## Concurrency - Handling Multiple Clients

Every incoming connection spawns a new **Thread**. This means multiple clients can be served simultaneously without blocking each other.

As each `Session` runs entirely on its own thread, there's no shared mutable state between sessions, with exception of the `SessionManager` and other key components explained later.

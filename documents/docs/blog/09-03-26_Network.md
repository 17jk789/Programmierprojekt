# Table of Contents
- [Table of Contents](#table-of-contents)
- [Introduction](#introduction)
  - [Formal concepts of networking](#formal-concepts-of-networking)
  - [The Language of the Network](#the-language-of-the-network)


# Basics
## Formal concepts of networking
Whenever you want to access the same data on different devices without carrying it (the data) from device to device, with, let’s say, a thumb drive, you have multiple options for achieving this.

![Peer To Peer Versus Client-Server Architecture](/documents/images/docs/blog/09-03-26_Network/shared_ressouce_possible_approaches.svg)

<!-- I tried to get this embeddet image to show up, but it just wouldnt
<img src="/documents/images/docs/blog/09-03-26_Network/shared_ressouce_possible_approaches.svg" alt="Peer To Peer Versus Client-Server Architecture" width="200"/>
-->

**Peer to Peer** enables users to share content (files, messages, ...).
While it does not rely on a central server, it comes with the big trade-of that the device storing the data you want to access has to be reachable and online.
Today Peer to Peer is still widely used with VoIP and decentralized networks for censorship-resistant transactions formaly known as the blockchain.

The **Client-Server** model evolved into the sophisticated "web-services" we know and use daily.
One central powerful computer (the server) stores data and allows external devices (clients) to access it.

For our course "Programmierprojekt" we are required to use the Client-Server model.

## The Language of the Network
To communicate over the network, we first need to establish a few rules.

If we look at the real world, language also follows certain rules:

* Grammar
* Meaning of Words

In computer science, these rules are known as a "protocol." Everyone must follow these rules, because if not, the program we want to communicate with wouldn’t understand us, or in the worst case, misunderstand us.

For our network, we’ve decided to build upon the Post Office Protocol (POP) version 3. It follows the request-response model, is synchronous, and meets our requirements.

An example of a request and response (for our game) might look like this:

1. Request
   ```text
   GET_DELTA 42
   ```

2. Response
   ```text
   +OK
   PLAYER_FOLDED playerId=3
   POT 240
   NEXT_TURN playerId=1
   WHISPER from=player2 msg="hey"
   CHAT_LOBBY player3="hello"
   .
   ```

The client requests the occurred actions since the 42nd state.

The server responds with all the actions other players have made and the messages the client has received.


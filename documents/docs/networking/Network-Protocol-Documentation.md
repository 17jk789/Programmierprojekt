This Document states the Network-Protocol as it is currently implemented 

## GET_MESSAGE_COUNT
This Command gets the number of messages, currently stored in the queue for the specific client.
(Is used together with GET_NEXT_MESSAGE, to get all messages that are currently in the queue)

Example:

```
GET_MESSAGE_COUNT

1
+OK
```

## GET_NEXT_MESSAGE
This Command gets the next Message that is being stored in the queue for the client.
(Command is being sent the amount of times, the GET_MESSAGE_COUNT Command returns)

Example:

```
GET_NEXT_MESSAGE

TYPE=LOBBY GAME=1 USER=player1 TARGET=null TIME=9:30 TEXT="Guten Tag"
+OK
```

## SEND_MESSAGE
This Command gets sent if the user of that client writes a message to one of the three possible chats

(The Arguments / Parameters of the Command describe all the parameters of the Object "Message" being used internally by both the Client and the Server)

Example:
```
SEND_MESSAGE TYPE=LOBBY GAME=1 USER=player1 TARGET=null TIME=10:30 TEXT="Hallo Welt"

+OK
```

## LOG_IN
This Command is used to create a user on the server and associate that user with a username
Server returns the username, slightly changed if it is already used by someone else, and an ID to identify the client.

Example:

```
LOG_IN USERNAME="Peter"

USERNAME="Peter" ID=<random UUID>
+OK
```

## LOG_OUT
This Command is used to quit the connection between client and server.

Example:

```
LOG_OUT

+OK
```

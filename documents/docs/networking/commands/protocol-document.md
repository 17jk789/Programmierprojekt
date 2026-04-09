# Protocol Document
This document describes the protocol for client-server communication in our application. It defines the structure of requests and responses, the supported commands along with thier request parameters, response formats and possible errors.


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
- [Pre executions checks](#pre-executions-checks)
  - [UserLoggedInCheck](#userloggedincheck)
    - [Error Response](#error-response-3)
- [Commands](#commands)
  - [PING command](#ping-command)
    - [Required pre-execution checks](#required-pre-execution-checks)
    - [Request Parameters](#request-parameters)
    - [Success Response](#success-response)
  - [CHECK\_USERNAME command](#check_username-command)
    - [Required pre-execution checks](#required-pre-execution-checks-1)
    - [Request Parameters](#request-parameters-1)
    - [Success Response](#success-response-1)
  - [LOGIN comamnd](#login-comamnd)
    - [Required pre-execution checks](#required-pre-execution-checks-2)
    - [Request Parameters](#request-parameters-2)
    - [Success Response](#success-response-2)
    - [Error Response](#error-response-4)
  - [LOGOUT command](#logout-command)
    - [Required pre-execution checks](#required-pre-execution-checks-3)
    - [Request Parameters](#request-parameters-3)
    - [Success Response](#success-response-3)
    - [Error Response](#error-response-5)

<!-- Plase see the comments for copy n' paste ready examples -->

# General structure of requests
As mentioned before, our protocol is based on POP3. 
Each command is represented as a single line of text, starting with the command name followed by parameters. The server responds with a status line indicating success or failure, followed by the body.

Requests can have parameters that provide additional information for the command. Parameters are key-value pairs separated by a equal sign (`=`).

Responses are collections of key-value pairs, containing either a value or another collection, allowing for nested structures. 
Each collection is ended with the `END` keyword.



# Preconditions
The serverside pipeline to process incomming requests consists of multiple stages.
Each of these stages can yield an error response if the request does not meet the requirements of that stage.

## Parsing
One of these stages is the parsing. It is responsible for parsing the raw request into a structured format that can be easily processed by the command handlers. It validates the syntax of the request aswell.

### Error Response
| Code            | Discription                                                                                     |
| :-------------- | :---------------------------------------------------------------------------------------------- |
| `PARSING_ERROR` | The body of the request contains syntax errors (see message field of response for more details) |


## Command dispatching
After the request has been successfully parsed, the next stage is to dispatch the `PrimitiveRequest` to the appropriate `CommandParser`. This is done by the `CommandDispatcherDispatcher`, which uses the command name to determine which parser to use.

### Error Response
| Code              | Discription                                                       |
| :---------------- | :---------------------------------------------------------------- |
| `UNKNOWN_COMMAND` | The command is unknown to this server. No parser has been defined |


## Command parsing
Once the `PrimitiveRequest` has been dispatched to the appropriate `CommandParser`, the parser is responsible for parsing the parameters of the request and creating a `Request` that can be executed by the responsible `CommandHandler`.

### Error Response
| Code                | Discription                                                                                       |
| :------------------ | :------------------------------------------------------------------------------------------------ |
| `MISSING_PARAMETER` | A required parameter is missing from the request (see message field of response for more details) |



# Pre executions checks
Pre-execution checks are reusable validation steps that can be registered on command handlers. 
They are implemented as `HandlerCheck` instances and are executed by the `CommandHandlerExecutor` before the handler's main logic is invoked.

<!--
## Name of the check
Description of the check, what it does and when it should be used.

### Response if not met
| Code         | Discription              |
| :----------- | :----------------------- |
| `ERROR_CODE` | Description of the error |
-->

## UserLoggedInCheck
The `UserLoggedInCheck` is a common pre-execution check that verifies whether the user is logged in (i.e. has a user associated with his session).

### Error Response
| Code                 | Discription               |
| :------------------- | :------------------------ |
| `USER_NOT_LOGGED_IN` | The user is not logged in |



# Commands
Commands are the core of our protocol, representing the various actions that clients can request from the server. Each command has a unique name and may require specific parameters in addition to pre-execution checks.
The server processes these commands and responds accordingly.

<!--
## Name of the command
Description of the command, what it does and when it should be used.

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
| Code         | Discription              |
| :----------- | :----------------------- |
| `ERROR_CODE` | Description of the error |
-->

## PING command
The `PING` command is a simple command that can be used to check if the server is responsive.

### Required pre-execution checks
None.

### Request Parameters
No parameters.

### Success Response
No response fields.


## CHECK_USERNAME command
The `CHECK_USERNAME` command is used to check if a username is already taken by another user. Additional users can still login with the same username, but thier name will be subsituted with a suffix.

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


## LOGIN comamnd
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
| Code                | Discription                                                                    |
| :------------------ | :----------------------------------------------------------------------------- |
| `ALREADY_LOGGED_IN` | The session is already associated with a user, logging in again is prohibited. |


## LOGOUT command
Description of the command, what it does and when it should be used.

### Required pre-execution checks
None.

### Request Parameters
No parameters.

### Success Response
No response fields.

### Error Response
| Code               | Discription                                                      |
| :----------------- | :--------------------------------------------------------------- |
| `NO_USER_ASSOCIATED` | The session has no user associated, logging out is not possible. |

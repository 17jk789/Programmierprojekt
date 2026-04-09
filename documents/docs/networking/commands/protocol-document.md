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
- [Commands](#commands)

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

### Response
| Field    | Type                | Description           |
| :------- | :------------------ | :-------------------- |
| `field1` | `type`              | Description of field1 |
| `field2` | `Collection<type2>` |                       |

| Fields of `type2` | Type   | Description           |
| :---------------- | :----- | :-------------------- |
| `field1`          | `type` | Description of field1 |
-->

# Implementing a Command
This guide walks through the full process of adding a new command to the server. By the end you
will have a working command with a Parser, Request, Handler, and Response, all correctly wired
into the server.

For background on how these components interact at a technical level, see [Command Infrastructure](../reference/command-infrastructure.md).

## Before You Start
A command consists of exactly four classes, all placed in the same package:

```
app/commands/<your_command>/
    YourCommandParser.java
    YourCommandRequest.java
    YourCommandHandler.java
    YourCommandResponse.java   ← omit this if OkResponse is sufficient
```

Name the package after the command in `snake_case`, matching the wire-protocol name (e.g. `join_lobby` for the `JOIN_LOBBY` command).
Name the classes in `UpperCamelCase` with the command name as prefix.

## Step 1 — Define the Request

The `Request` subclass is a typed, immutable value object holding everything the handler needs.
Define it first, because both the Parser and the Handler depend on it.

```java
package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.greet;

import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.Request;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;

public class GreetRequest extends Request {
  private final String name;

  public GreetRequest(RequestContext context, String name) {
    super(context);
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
```

**Rules for the Request class:**

- Always pass `context` directly to `super(context)`. Never store it in a separate field.
- Fields must be `private final`. Set them only through the constructor.
- Provide a getter for every field. No setters.
- No logic. The request is data, not behaviour.



## Step 2 — Implement the Parser

The Parser extracts parameters from the `PrimitiveRequest` and constructs the typed Request.

```java
package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.greet;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.CommandParser;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.PrimitiveRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.accessor.RequestParameterAccessor;

public class GreetParser implements CommandParser<GreetRequest> {
    @Override
    public GreetRequest parse(PrimitiveRequest primitiveRequest) {
        RequestParameterAccessor accessor = new RequestParameterAccessor(primitiveRequest.parameters());
        String name = accessor.require("NAME");
        return new GreetRequest(primitiveRequest.context(), name);
    }
}
```

**Rules for the Parser class:**

- Always create a `RequestParameterAccessor` from `primitiveRequest.parameters()`.
- Use `accessor.require(key)` for mandatory parameters. It throws `MissingParameterException`
  automatically — do not write your own null checks.
- Use `accessor.optional(key, defaultValue)` for optional parameters.
- Use the typed overloads (e.g. `accessor.require("COUNT", Integer::parseInt)`) for non-string
  parameters. The resulting `ParameterParseException` is handled by `SessionReader`.
- Always pass `primitiveRequest.context()` as the first argument to the Request constructor.
- No domain logic.

### Choosing between `require` and `optional`

| Use | When |
|-|-|
| `accessor.require(key)` | The command cannot function without this parameter |
| `accessor.require(key, parser)` | Same, but the value must be converted to a specific type |
| `accessor.optional(key, default)` | The parameter has a sensible default when omitted |
| `accessor.optional(key, default, parser)` | Optional + type conversion |



## Step 3 — Implement the Response

If your command returns data, create a dedicated Response class. If it only needs to signal
success, use `OkResponse` directly in the handler and skip this step.

```java
package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.greet;

import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.SuccessResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.builder.ResponseBodyBuilder;

public class GreetResponse extends SuccessResponse {
    public GreetResponse(RequestContext context, String greeting) {
        super(context, new ResponseBodyBuilder()
            .param("GREETING", greeting)
            .build());
    }
}
```

**Rules for the Response class:**

- Extend `SuccessResponse` for successful outcomes, not `Response` directly.
- Build the body inline in the `super(...)` call using `ResponseBodyBuilder`. Do not store
  the builder or body separately.
- Use `ResponseBodyBuilder.block(tag, consumer)` to add nested structures when the response
  carries a list or a complex sub-object.
- Parameter keys must be `UPPER_SNAKE_CASE` to match the wire protocol convention.
- If you need to return an error (e.g. lobby not found), do not throw — dispatch an
  `ErrorResponse` from the handler instead (see Step 4).



## Step 4 — Implement the Handler

The Handler contains the domain logic. It reads from registries, modifies state if needed, and
always dispatches exactly one response.

```java
package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.greet;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.execution.CommandHandler;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.dispatcher.ResponseDispatcher;

public class GreetHandler implements CommandHandler<GreetRequest> {
    public final ResponseDispatcher responseDispatcher;
    
    public GreetHandler(ResponseDispatcher responseDispatcher) {
        this.responseDispatcher = responseDispatcher;
    }

    @Override
    public void execute(GreetRequest request) {
        GreetResponse response = new GreetResponse(request.getContext(), "Hello " + request.getName() + ", nice to meet you");
        responseDispatcher.dispatch(response);
    }
}
```

**Rules for the Handler class:**

- Declare all dependencies as `private final` fields, injected through the constructor.
- Always dispatch exactly one response per execution path. Every branch must end with a
  `responseDispatcher.dispatch(...)` call.
- Use `ErrorResponse` for domain-level failures (e.g. entity not found, precondition not met).
  Do not throw exceptions for expected failure cases.
- Use `request.getContext()` when constructing any Response — never construct a `RequestContext`
  yourself.
- Do not call `responseDispatcher.dispatch(...)` more than once in a single `execute` invocation.



## Step 5 — Register the Command

Open `ServerApp.registerCommands(...)` and add two lines: one to register the parser and one to
register the handler.

```java
private static void registerCommands(
    CommandParserDispatcher parserDispatcher,
    CommandRouter commandRouter,
    ResponseDispatcher responseDispatcher /* add new dependencies here */) {
    // ... existing commands ...

    parserDispatcher.register("GREET", new GreetParser());
    commandRouter.register(GreetRequest.class,
    new GreetHandler(responseDispatcher));
}
```

The string passed to `parserDispatcher.register(...)` must exactly match the command name as
sent by the client on the wire, in `UPPER_SNAKE_CASE`.

> **Important:** Always register both the parser **and** the handler. Registering a parser
> without a handler will result in an `UnknownRequestException` at runtime when the command is
> received — the parser will succeed, but the router will find no handler for the resulting
> request type.



## Common Mistakes

**Forgetting to pass `context` through to the Response.** The `context` is how the response
finds its way back to the right client. Dropping it means the response is dispatched to the
wrong session or causes a NullPointerException.

**Putting domain logic in the Parser.** Parsers run before the request is validated as
meaningful. A parser that calls a registry or modifies state creates hidden coupling between the
parsing and execution phases and makes the parser difficult to test.

**Dispatching a response before an early return.** A common mistake is to dispatch an error
and then fall through to dispatch a success response as well. Always `return` immediately after
dispatching an error.

**Using a raw string for the error code.** Error codes should be `UPPER_SNAKE_CASE` constant
strings that the client can match against programmatically. Avoid spaces or punctuation.

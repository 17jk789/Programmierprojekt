## Protocol / API Change

### Summary
<!-- Brief description of the protocol change -->

### Affected Commands / Responses
<!-- Which commands or response types are being added, changed, or removed? -->

| Command | Response | Type of Change | Description |
|---------|----------|----------------|-------------|
|         |          | New / Changed / Removed | |

### New / Changed Syntax
```
<!-- Before / after or new command syntax here -->
<!-- Example:
BEFORE: LOGIN UNAME=<username> PASS=<password>
AFTER:  LOGIN USERNAME=<username> PASSWORD=<password>
-->
```

### Affected Implementation Layers
<!-- Which components / classes need to be updated? -->
- [ ] `Session`
- [ ] `User` or `UserRegistry`
- [ ] `Tokenizer` / `RequestTokenClassifier`
- [ ] `ProtocolParser`
- [ ] `CommandParserDispatcher` + corresponding `CommandParser`
- [ ] `CommandHandler`
- [ ] `Response` hierarchy
- [ ] `GameEngine` or `GameState`
- [ ] Client-side (create your own issue and create link)

### Motivation
<!-- Why is this protocol change necessary? -->

/label ~protocol

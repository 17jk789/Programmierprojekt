# Contribution Guidelines
This document describes the conventions and workflows everyone must follow to keep the codebase consistent and the collaboration smooth. 
If you notice a violation, speak to the person involved respectfully. 

Since this project is part of a course at the University of Basel, the [Code of Conduct](https://www.unibas.ch/de/Universitaet/Administration-Services/Vizerektorat-People-And-Culture/Persoenliche-Integritaet/Code-of-Conduct.html) applies.


## Table of Contents
- [Contribution Guidelines](#contribution-guidelines)
  - [Table of Contents](#table-of-contents)
  - [Issues \& Tasks](#issues--tasks)
    - [Creating an issue](#creating-an-issue)
    - [During implementation](#during-implementation)
    - [Collaborative work](#collaborative-work)
  - [Git Workflow](#git-workflow)
    - [Creating a branch](#creating-a-branch)
    - [Working on a branch](#working-on-a-branch)
  - [Commit Messages](#commit-messages)
    - [Rules](#rules)
    - [Examples](#examples)
  - [Code Style](#code-style)
    - [Linter](#linter)
    - [Formatter](#formatter)
    - [General guidelines](#general-guidelines)
  - [CI/CD Pipeline](#cicd-pipeline)
    - [Before pushing](#before-pushing)
  - [Merge Requests](#merge-requests)
    - [Opening a MR](#opening-a-mr)
    - [Merging](#merging)
    - [After merging](#after-merging)
  - [Be human](#be-human)


## Issues & Tasks
Every piece of work - whether a new feature, a bug fix, or a refactoring - must be tracked as an
Issue or Task in GitLab **before** any implementation begins.

### Creating an issue
1. Open a new Issue or Task using the **relevant template** provided in the repository.
2. Fill in **all fields** specified by the template thoughtfully and completely. A well-written issue is the single source of truth for the work being done - treat it accordingly.
3. Work through the **checklist** in the template before marking the issue as ready. Do not skip items.

### During implementation
- If you encounter a problem or an unexpected finding while working on an issue, record it as a **comment** on the issue. This keeps the history intact and visible to the whole team.
- **Do not restructurally edit the original description** to incorporate new information. The description reflects the intent at the time the issue was created; comments document what happened along the way.

### Collaborative work
- When multiple people are working on the same issue, **prefer issue comments over private messages** for coordination. This keeps the current status, decisions, and open questions
  centrally visible and searchable.
- Before starting work that overlaps with an existing issue, check its comment thread first to avoid duplicating effort.


## Git Workflow
We use a **feature branch -> main** strategy. The `main` branch is always in a releasable state.

### Creating a branch
We follow the [**Conventional Branch**](https://conventional-branch.github.io/) specification. Branch names follow this pattern:

```
<type>/<short-description>
```

| Type       | When to use                                      |
|------------|--------------------------------------------------|
| `feat`     | New feature or capability                        |
| `fix`      | Bug fix                                          |
| `refactor` | Restructuring without behaviour change           |
| `test`     | Adding or fixing tests                           |
| `ci`       | Pipeline, Gradle, or tooling changes             |
| `docs`     | Documentation only                               |

**Examples:**

```
feat/reconnect-command
fix/session-writer-flush
refactor/user-registry-cleanup
docs/contributing
```

### Working on a branch
Keep branches short-lived. A branch should represent one cohesive unit of work.

It is permissible to commit changes within a feature branch that cause the program to become non-functional, but these should be fixed as soon as possible. In any case, the code that is merged into `main` must be functional.

And most importantly: **Do not commit directly to `main`**.


## Commit Messages
We follow the [**Conventional Commits**](https://www.conventionalcommits.org/) specification. Every commit message must have the form:

```
<type>: <short summary>

[optional body]
```

### Rules
The summary line must be **<= 72 characters**, written in the **imperative mood** (e.g. "add", not "added" or "adds").

### Examples
```
Reat: Add RECONNECT command handler

The handler re-associates an existing User with a new Session after
a connection drop, preserving in-flight state.
```

```
Fix: Flush output stream before closing
```

```
Ci: Tighten Checkstyle failure policy to allow_failure: false
```

```
Refactor: Replace ArrayList with CopyOnWriteArrayList
```


## Code Style
Code formatting is enforced automatically. **Do not submit a MR with formatting violations.**

### Linter
We use **Checkstyle** as our linter with a custom set of rules tailored to our project.

Checkstyle runs on every pipeline. Fix all violations locally before pushing:

```bash
./gradlew checkstyleMain checkstyleTest
```


### Formatter
We use **Spotless** with the **[Google](https://google.github.io/styleguide/javaguide.html) / [AOSP Java style](https://source.android.com/docs/core/architecture/hidl/code-style?hl=en)**:

- **Indentation:** 4 spaces (no tabs)
- **Line length:** 100 characters
- No decorative blank lines directly after opening braces `{`
- Blank lines are reserved for separating logical sections within a block

Run the formatter before committing:

```bash
./gradlew spotlessApply
```

Check without applying:

```bash
./gradlew spotlessCheck
```

### General guidelines
- Add **JavaDoc** docstrings to classes, interfaces, records and methods.
- Exercise **clean architecture**
- Prefer **stateless components**
- Use **`record` types** for immutable data carriers.
- Log with **Log4J 2** (`log4j-api`). Use the appropriate level (`DEBUG` for pipeline internals, `INFO` for lifecycle events, `WARN`/`ERROR` for recoverable/unrecoverable problems).


## CI/CD Pipeline
The pipeline runs automatically on every push. It has four stages:

```
lint > report > build > test
```

| Stage    | Jobs                                       |
|----------|--------------------------------------------|
| `lint`   | Spotless check, Checkstyle                 |
| `report` | Code Quality JSON conversion (only for mr) |
| `build`  | `./gradlew assemble`                       |
| `test`   | `./gradlew test` + JUnit result reporting  |

### Before pushing
Run the full check suite locally to avoid a broken pipeline:

```bash
./gradlew spotlessCheck checkstyleMain checkstyleTest build test
```

A red pipeline blocks merging. Fix failures before creating your merge request.


## Merge Requests

### Opening a MR
- Target branch is always **`main`**.
- Fill in the MR description: what changed and why. Link the relevant issue (with 'Closing #x') if one exists.

### Merging
A MR can be merged when **all main CI pipeline stages are green** (lint, build, test).

No explicit peer approval is required, but leaving a note or question in the MR thread for non-trivial changes is encouraged.
If you spot a problem in someone else's open MR, comment - **do not push directly to their branch** and try to fix the issue yourself.

### After merging
Delete the feature branch after the MR is merged. GitLab can do this automatically via the
"Delete source branch" checkbox in the MR.


## Be human
We are all human.
We all forget things or make mistakes sometimes.

It’s important that we look out for one another and **work together as a team**.

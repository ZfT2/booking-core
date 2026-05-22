# Contributing to booking-core

Thank you for your interest in contributing to **booking-core**.

The project is currently alpha software. Contributions are welcome for bug
reports, API feedback, documentation improvements, and pull requests.

## Reporting Issues

Before opening a new issue, please check whether it already exists or has been
fixed in the current development version.

For bug reports, include:

- operating system
- Java version
- booking-core version or commit
- steps to reproduce the problem
- relevant log output

## Development Setup

You need Java 17 or newer, Maven, and Git.

```powershell
git clone https://github.com/ZfT2/booking-core.git
cd booking-core
mvn clean verify
```

Use `mvn clean install` when another local Maven project should consume the
current snapshot.

## Code Style

- Follow standard Java naming conventions.
- Keep methods focused and readable.
- Discuss new dependencies before adding them.

The project uses Log4j2. Prefer structured logging over `System.out.println`.

```java
log.info("Processing account {}", accountId);
```

## Commit Guidelines

Write clear commit messages. Conventional Commit prefixes are helpful for
release notes, for example:

```text
feat: add a shared booking helper
fix(process): handle missing booking purpose
docs: explain release tags
```

## Pull Requests

1. Fork the repository.
2. Create a feature branch.
3. Commit the change.
4. Push the branch.
5. Open a pull request.

By contributing, you agree that your contribution is licensed under the GNU
General Public License v3.0. See `LICENSE`.

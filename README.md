# booking-core

> Alpha status: the shared API and processing logic are still evolving.

![Build](https://github.com/ZfT2/booking-core/actions/workflows/release.yml/badge.svg)
![Release](https://img.shields.io/github/v/release/ZfT2/booking-core)
![Java](https://img.shields.io/badge/Java-17-blue)
![License](https://img.shields.io/badge/License-GPLv3-blue)

`booking-core` contains reusable booking contracts and processing helpers shared
between banking and import projects.

## Scope

- `Account` and `Booking` interfaces for account and booking DTOs
- common booking type and rebooking processing logic
- property-file loading helpers used by the shared processors
- exceptions for missing import configuration

The processor classes expect the consuming application to provide the import
property files they use under `properties/import`.

## Requirements

- Java 17 or newer
- Maven for local builds

## Build

```powershell
mvn clean verify
```

Install the library into the local Maven repository when another local project
should consume the current snapshot:

```powershell
mvn clean install
```

## Maven Coordinates

```xml
<dependency>
  <groupId>de.zft2</groupId>
  <artifactId>booking-core</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency>
```

Released artifacts are published to GitHub Packages for this repository.

## Release Management

The GitHub Actions release workflow is triggered by tags beginning with `v`.

```powershell
git tag v0.0.1+patch
git push origin v0.0.1+patch
```

Supported release tags are:

- `v1.2.3+patch` to release `1.2.4`
- `v1.2.3+minor` to release `1.3.0`
- `v1.2.3+major` to release `2.0.0`
- `v1.2.3` to release exactly `1.2.3`

The workflow builds the JAR, creates the GitHub release, deploys to GitHub
Packages, and commits the next patch snapshot version to the repository default
branch.

For cleanup of a failed release, start with plan or dry-run mode:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\cleanup-github-release.ps1 `
  -Version 0.0.2 `
  -Tags "v0.0.1+patch,v0.0.2" `
  -Mode Plan
```

## Project Layout

```text
booking-core
|-- .github/workflows/release.yml
|-- scripts/cleanup-github-release.ps1
|-- src/main/java
|-- CHANGELOG.md
|-- CONTRIBUTING.md
|-- LICENSE
`-- pom.xml
```

## License

This project is licensed under the GNU General Public License v3.0. See
`LICENSE`.

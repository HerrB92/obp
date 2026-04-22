# Open Beacon Package (obp)

Open Beacon Package (obp) is a Java-based research codebase for ingesting and processing telemetry from OpenBeacon RFID/BLE hardware readers and tags.  
It was originally developed as part of an academic tracking system and is now modernized to build with a current Maven multi-module setup.

## Project Status

- `OpenBeaconPackage` is a legacy archive and is no longer the active runtime path.
- Active runtime components are:
  - `OpenBeaconPackageService` (packet listener and decoding service)
  - `OpenBeaconTracker` (tracking logic, persistence, JSON export)
- `OpenBeaconTestLab` is a lightweight playground for listener and tracking experiments.

## Modules

- `OpenBeaconPackageService`
  - Listens on UDP for OpenBeacon packets.
  - Decodes sightings and forwards parsed events to listeners.
  - Provides reusable base functionality for tracker/testlab modules.
- `OpenBeaconTracker`
  - Persists configuration and runtime data with Hibernate.
  - Computes positions based on sightings and spot-tag relationships.
  - Exports status data as JSON for the web frontend in `OpenBeaconTracker/web`.
- `OpenBeaconTestLab`
  - Small standalone environment for experimenting with listener and tracking behavior.

## Technology Stack (Modernized)

- Java 17
- Maven multi-module build
- Hibernate 5.6.x
- MySQL Connector/J 8.x
- Log4j 2.23.x
- Jackson Core 2.17.x
- Joda-Time 2.12.x
- JUnit 4.13.x

## Build

This repository now uses a parent Maven project in the repository root.

```bash
mvn test
```

Because the codebase uses legacy source layout (`src` and `test` folders instead of `src/main/java` and `src/test/java`), module POMs explicitly map these directories.

## Runtime Notes

- Tracker database configuration is defined in `OpenBeaconTracker/src/hibernate.cfg.xml`.
- The tracker still expects a local MySQL database named `obp` unless reconfigured.
- JSON export targets in `OpenBeaconTracker` are currently hardcoded to `/var/www/html/json/...`.
- Database credentials can be provided via environment variables (preferred) or a local `.env` file:
  - `OBP_DB_URL`
  - `OBP_DB_USERNAME`
  - `OBP_DB_PASSWORD`
- Use `.env.example` as a template and create a local `.env` file (ignored by git).

## OpenBeacon Hardware Context

This project targets historical OpenBeacon hardware generations used around the original thesis timeframe.  
The current OpenBeacon website describes newer hardware and project context, which differs from the setup used here.

- OpenBeacon project background: [openbeacon.org/about](https://www.openbeacon.org/about.html)

## Encoding

Project text/source files in active modules are UTF-8 encoded.
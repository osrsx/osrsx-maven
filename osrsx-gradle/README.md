# osrsx-gradle — the `io.osrsx.plugin` Gradle plugin

The convention Gradle plugin an **external** osrsx-plugin project applies. It wires the plugin build
against the published `io.osrsx:osrsx-api` SDK, stamps the plugin jar with the metadata the host reads,
and provides the install/run dev-loop tasks. This is Phase 1 of the plugin SDK
(`docs/PLUGIN_SDK_ROADMAP.md`).

You do **not** need a checkout of the osrsx client to build a plugin — only this plugin and the
published SDK artifact.

## Applying it

In your plugin project's `build.gradle`:

```gradle
plugins {
    id 'org.jetbrains.kotlin.jvm' version '2.1.0'   // or `java` — plugins may be Kotlin or Java
    id 'io.osrsx.plugin' version '0.1.0'
}

osrsxPlugin {
    id = 'my-woodcutter'          // required — stable plugin identifier
    name = 'My Woodcutter'        // optional — defaults to id
    // version    — optional, defaults to project.version
    // apiVersion — optional, defaults to the osrsx-api version this Gradle plugin was built against
}
```

Applying the plugin automatically:
- applies the `java-library` base,
- adds `mavenLocal()` + `mavenCentral()` repositories,
- adds `compileOnly "io.osrsx:osrsx-api:<apiVersion>"` — you compile against the SDK; the running
  client provides it at runtime (so it is **not** bundled into your jar),
- stamps the jar manifest (below),
- registers `installPlugin` and `osrsxRun`.

## Tasks

| Task | What it does |
|------|--------------|
| `installPlugin` | Copies the built plugin jar into `~/.osrsx/plugins/` (where the host's `PluginManager` scans on startup). |
| `osrsxRun` | Runs `installPlugin`, then prints how to launch the host (`./gradlew :osrsx-core:runClient`). **Stub:** launching the full host from an external project is out of scope for Phase 1 — Phase 2's directory watcher turns `gradle -t installPlugin` into a live hot-reload loop. |

Dev loop today: `./gradlew installPlugin` then start the host. Once Phase 2 lands:
`./gradlew -t installPlugin` rebuilds+reinstalls on every save and the host hot-reloads.

## Manifest contract (consumed by the host `PluginManager` — Phase 2/4)

The plugin jar's `META-INF/MANIFEST.MF` carries these **main attributes** (see
`io.osrsx.gradle.PluginManifest`):

```
Osrsx-Plugin-Id:       my-woodcutter     # stable identifier
Osrsx-Plugin-Name:     My Woodcutter     # human display name
Osrsx-Plugin-Version:  1.2.0             # the plugin's own version
Osrsx-Api-Version:     0.1.0             # the io.osrsx:osrsx-api version it compiled against
```

The host reads these via `JarFile.getManifest().getMainAttributes()`. `Osrsx-Api-Version` is the field
a Phase 4 compat check gates on. Today's `PluginManager` still discovers plugins by scanning for
`@PluginDescriptor` classes and does **not** yet read this manifest — wiring that read is Phase 2's job;
this module only guarantees the fields are present and stable.

## Publishing (local)

This module is published with `maven-publish` + `java-gradle-plugin`:

```bash
./gradlew :osrsx-gradle:publishToMavenLocal
```

That puts both the plugin artifact (`io.osrsx:osrsx-gradle`) and its plugin marker
(`io.osrsx.plugin:io.osrsx.plugin.gradle.plugin`) in `~/.m2`, so a consumer project resolves
`id 'io.osrsx.plugin'` via `mavenLocal()` in its `settings.gradle` `pluginManagement { repositories { mavenLocal() } }`.
The SDK itself is published from the `:osrsx-api` module (`./gradlew :osrsx-api:publishToMavenLocal`).

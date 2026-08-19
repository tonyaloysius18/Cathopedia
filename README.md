# Cathopedia

A cross-linked Catholic encyclopedia — Saints, Popes, Apostles, Churches, Marian
Apparitions, Eucharistic Miracles and Feasts, offline-first on Android and iOS.
This is the **Phase 0 (Foundation) skeleton** from the project roadmap: schema,
navigation shell and base design system, not yet the content-populated app.

## Structure

- `shared/` — Kotlin Multiplatform module. Everything but the two platform
  entry points lives here: the SQLDelight schema (`src/commonMain/sqldelight`),
  domain models and repository (`src/commonMain/kotlin/.../model`, `.../data`),
  and the Compose Multiplatform UI (`.../ui`, `App.kt`). `androidMain`/`iosMain`
  hold only the platform SQLite driver.
- `androidApp/` — thin Android application module (`MainActivity` + manifest).
- `iosApp/` — Xcode project. `iosApp/iosApp/iOSApp.swift` hosts the shared
  Compose UI via `ComposeUIViewController`.

## Content model

Seven entity types, each with a language-independent metadata table and a
`*Text` table keyed by `(entityId, language)` — English-only content for now,
French arrives in the Phase 3 polish pass. Cross-links between any two
entities live in `EntityRelation`; full-text search is FTS5-backed
(`ContentSearch`), filterable by content type. See `shared/src/commonMain/sqldelight/com/ynotlabs/cathopedia/db/`.

A handful of hand-written English entries (`SeedData.kt`) seed the database on
first launch so the app is browsable and searchable immediately — this stands
in for the real Phase 2 content pipeline (a Git repo of YAML/JSON compiled
into the bundled SQLite file), which isn't built yet.

## Running it

### Android — Android Studio

1. Open this folder (`Cathopedia/`) directly in Android Studio.
2. Let Gradle sync finish (first sync compiles the SQLDelight schema into
   generated Kotlin — this is normal and only happens once per schema change).
3. Run the `androidApp` configuration on an emulator or device (minSdk 26).

### iOS — Xcode

1. `open iosApp/iosApp.xcodeproj` (or open it from within Android Studio via
   the Kotlin Multiplatform plugin's iOS run configuration, if installed).
2. Build/run the `iosApp` scheme. The first build invokes
   `./gradlew :shared:embedAndSignAppleFrameworkForXcode` to compile the
   Kotlin framework — this also happens automatically as an Xcode build phase.
3. Automatic code signing is already configured against the same Apple
   Developer team as this machine's other projects; Xcode will pick it up.

### From the command line

```bash
./gradlew :androidApp:assembleDebug
./gradlew :shared:embedAndSignAppleFrameworkForXcode
```

## What's not here yet

Per the roadmap, Phase 0 is schema + skeleton only:

- **Licensing** — AELF/USCCB outreach hasn't started; Scripture and daily
  readings are Horizon 2, gated on those replies.
- **Content pipeline** — `SeedData.kt` is a placeholder for the Phase 2 Git
  repo of YAML/JSON compiled into SQLite.
- **Related-content UI polish** — the detail screen shows related items as
  plain chips; the Phase 3 pass gives this real design treatment.
- **French** — `language` is threaded through the schema and repository from
  day one, but only `"en"` content exists right now.

# DSA Master

A free, offline-first Android app for learning **Data Structures & Algorithms** — structured lessons, flashcards with **SM-2 spaced repetition**, topic quizzes, curated coding problems, streaks and a review heatmap. No ads, no in-app purchases, no data collection.

Built as a portfolio-quality project with Kotlin, Jetpack Compose, Material 3, Room, Hilt and Clean-Architecture-inspired layering.

## Features

- **18 DSA topics** (arrays → dynamic programming, tries, bit manipulation), each with an intro, real-world analogy, lessons with Kotlin code, complexity tables and common mistakes
- **Flashcards** — a built-in "DSA Essentials" deck plus custom decks and cards (create / edit / delete)
- **SM-2 spaced repetition** — Again / Hard / Good / Easy grading, ease-factor tracking, due-date scheduling, unit-tested
- **Daily review queue** with progress bar and session summary
- **Quizzes** — multiple-choice, true/false and code-output questions with explanations, retry and shuffled order
- **Coding problems grouped by pattern** (Hashing, Stack, Binary Search, Heap / Top-K, DP, Bit Manipulation, ...) — each problem teaches *recognition first*: statement signals ("How do I recognize the pattern?"), then a step-by-step walkthrough of choosing the algorithm, then hints, and only then a reference solution
- **Coding problems** — statement, examples, constraints, progressive hints, full Kotlin solution, complexity analysis, notes, bookmark, mark-as-solved
- **Search** across topics, lessons, flashcards and problems
- **Progress** — current/longest streak, quiz accuracy, mastered cards, 7-day chart, 12-week GitHub-style heatmap
- **Settings** — name, daily goal, light/dark/system theme, dynamic color, daily reminder (WorkManager + notification permission handling), reset progress
- **100% offline**, seeded on first launch from a Kotlin seed provider

## Tech stack

Kotlin · Jetpack Compose · Material 3 · MVVM · Room · Hilt · Navigation Compose · DataStore · WorkManager · Coroutines/StateFlow · Gradle Kotlin DSL + version catalog · JUnit + Compose/instrumented tests.

## Architecture

```
app (single module)
├── core/          designsystem, database (Room), datastore, navigation, notifications, seed
├── domain/        Sm2Scheduler, StreakCalculator, repository interfaces
├── data/          repository implementations
├── di/            Hilt modules
└── feature/       onboarding, home, topics, lessons, flashcards, review, quiz,
                   problems, progress, search, settings  (Screen + ViewModel each)
```

Deliberate simplifications (documented trade-offs, easy to evolve later):
- **Single Gradle module** — multi-module adds build complexity with no payoff at this size.
- **Room entities are used as models across layers.** With no remote source there is nothing to map; a `domain/model + mapper` layer would be pure boilerplate here. Pure logic (SM-2, streaks) still lives in `domain/` behind repository interfaces, so swapping the data layer later is contained.
- **Kotlin seed provider** over a pre-populated `.db` asset: content changes are type-checked at compile time and reviewable in version control.
- Dependency versions are pinned to a known-good compatible matrix in `gradle/libs.versions.toml`; bump them there when updating.

## Database schema

`topics 1—N lessons / quizzes / problems` · `decks 1—N flashcards 1—1 review_states` · `quizzes 1—N quiz_questions / quiz_attempts` · `study_days` (one row per active day → streaks & heatmap). Foreign keys cascade; child FK columns are indexed.

## SM-2 in one paragraph

Each card stores `(repetition, intervalDays, easeFactor)`. Grades map to SM-2 quality: Again=0, Hard=3, Good=4, Easy=5. A grade < 3 is a lapse: repetition resets and the card returns tomorrow (ease still drops). On a pass the interval goes 1 day → 6 days → previous × easeFactor. Ease updates by `ef + (0.1 − (5−q)(0.08 + (5−q)·0.02))`, floored at 1.3. A card is "mastered" at a 21-day interval. See `domain/Sm2Scheduler.kt` and `Sm2SchedulerTest.kt`.

## Build & run

Requirements: **Android Studio (Ladybug or newer)** with JDK 17 (bundled) and Android SDK 35.

1. Unzip, then in Android Studio: **File → Open** → select the `DSAMaster` folder.
2. Let Gradle sync finish (first sync downloads dependencies).
3. Pick a device/emulator (API 26+) and press **Run ▶**.

Command line: `./gradlew assembleDebug` (APK lands in `app/build/outputs/apk/debug/`).

## Tests

- Unit tests (SM-2, streaks, lesson parser): `./gradlew test` — or right-click `app/src/test` in Studio → Run.
- Instrumented tests (Room DAO, needs a device/emulator): `./gradlew connectedAndroidTest`.

## Roadmap

Export/import of decks & progress via Storage Access Framework (JSON) · per-card images (Coil) · bookmarked-content hub screen · richer per-topic charts · lesson diagrams · widget for due-card count · localization.

## License

MIT — see [LICENSE](LICENSE).

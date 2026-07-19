# Contributing

1. Fork and clone, open in Android Studio, let Gradle sync.
2. Create a branch: `git checkout -b feature/short-description`.
3. Keep changes focused; follow Kotlin coding conventions; prefer adding seed content in `core/seed/SeedData.kt` for new lessons/cards/problems.
4. Run `./gradlew test` before opening a PR; add tests for logic changes (especially anything touching `Sm2Scheduler` or `StreakCalculator`).
5. Open a PR describing what changed and why.

Content contributions (new lessons, flashcards, problems) are very welcome — they are data-only changes.

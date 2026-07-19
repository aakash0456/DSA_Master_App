# Publishing DSAMaster to Google Play

This guide covers everything needed to take the project from source code to a live
Play Store listing. Policies and console screens change over time, so treat this as
a map rather than gospel — always double-check the current requirements in the
[Play Console Help Center](https://support.google.com/googleplay/android-developer).

---

## 1. One-time setup

1. **Create a Google Play Developer account** at https://play.google.com/console.
   There is a one-time registration fee (25 USD). Identity verification is required.
2. **Note for new personal accounts:** Google has required new individual accounts to
   run a closed test with a minimum number of testers over a minimum period (at one
   point 12 testers for 14 days) before production access is granted. Check the
   current rules in the console — plan for this in your timeline.
3. **Pick a real application ID.** The project ships as `com.example.dsamaster`,
   which Google Play rejects (`com.example` is blocked). Change it before your first
   upload:
   - In `app/build.gradle.kts`, update `namespace` and `applicationId`
     (e.g. `dev.yourname.dsamaster`).
   - The application ID is permanent once published — choose carefully.
4. **Update the privacy policy contact.** Replace the placeholder email in
   `PRIVACY_POLICY.md` and host the policy at a public URL (GitHub Pages works fine).
   A privacy policy URL is mandatory for all apps.

## 2. Prepare a release build

1. **Create an upload keystore** (once, keep it safe and backed up):
   ```
   keytool -genkeypair -v -keystore upload-keystore.jks -alias upload \
     -keyalg RSA -keysize 2048 -validity 9125
   ```
2. **Build a signed App Bundle** in Android Studio:
   *Build → Generate Signed App Bundle / APK → Android App Bundle*, select the
   keystore, choose the `release` variant. Output lands in
   `app/build/outputs/bundle/release/app-release.aab`.
3. **Enroll in Play App Signing** when prompted during the first upload. Google holds
   the app signing key; your keystore becomes the upload key. This is now the default
   and strongly recommended.
4. Release checklist before building:
   - [ ] `versionCode` incremented (every upload needs a higher one)
   - [ ] `versionName` updated and CHANGELOG entry added
   - [ ] `minifyEnabled` / R8 verified: install the release build on a device and
         smoke-test navigation, review flow, quiz flow, and reminders
   - [ ] Application ID is no longer `com.example.*`
   - [ ] Unit tests pass: `./gradlew test`

## 3. Store listing assets

| Asset | Requirement |
|---|---|
| App icon | 512 × 512 px, 32-bit PNG |
| Feature graphic | 1024 × 500 px |
| Phone screenshots | At least 2 (ideally 4–8), 16:9 or 9:16 |
| 7-inch / 10-inch tablet screenshots | Optional but recommended |

Suggested screenshots: Home (streak + due cards), a lesson with a code block, the
flashcard review screen, a quiz question with explanation, the Problems list, and
the Progress heatmap.

### Suggested listing copy

**App name:** DSAMaster — Learn DSA Offline

**Short description (max 80 chars):**
> Master data structures & algorithms with lessons, flashcards & quizzes. Offline.

**Full description (max 4000 chars):**
> DSAMaster is a free, offline-first companion for learning data structures and
> algorithms — whether you're preparing for coding interviews, studying for exams,
> or just building a stronger foundation.
>
> LEARN
> • 18 core topics, from arrays and hash tables to graphs, dynamic programming and
>   bit manipulation
> • Concise lessons with runnable Kotlin examples, complexity tables and
>   common-mistake callouts
> • Real-world analogies that make abstract ideas stick
>
> REMEMBER
> • Built-in flashcard deck covering every topic, plus your own custom decks
> • Spaced repetition (SM-2) schedules each card exactly when you're about to
>   forget it
> • Daily review goal and reminder notifications keep you consistent
>
> PRACTICE
> • Topic quizzes with instant feedback and explanations
> • Classic coding problems (Two Sum, Kadane's algorithm, Reverse Linked List and
>   more) with progressive hints, full Kotlin solutions and complexity analysis
> • Personal notes, bookmarks and solved tracking per problem
>
> TRACK
> • Study streaks, weekly activity chart and a 12-week heatmap
> • Quiz accuracy and card mastery statistics
>
> PRIVATE BY DESIGN
> • Works fully offline — no account, no ads, no analytics, no data collection
> • Everything stays on your device
>
> DSAMaster is open source under the MIT license.

**Category:** Education
**Tags:** algorithms, data structures, interview prep, computer science, flashcards

## 4. Console configuration

Work through every item under *Policy → App content*:

1. **Privacy policy** — paste the hosted URL.
2. **Data safety form** — declare that the app collects and shares **no data**.
   All content is stored locally; the app makes no network calls.
3. **Ads** — declare "No, my app does not contain ads."
4. **Content rating questionnaire** — an education app with no user-generated
   content, violence, or gambling; expect an "Everyone" rating.
5. **Target audience** — 13+ is the simplest choice; targeting children adds
   Families policy requirements.
6. **App access** — "All functionality is available without special access"
   (no login exists).
7. **Government apps / Financial features / Health** — answer "No" to all.

## 5. Rollout

1. **Internal testing first.** Create an internal testing release, upload the AAB,
   and install via the opt-in link on a real device. Verify the signed build behaves
   like the debug build.
2. **Closed testing** if required for your account type (see §1.2), or as a beta
   with friends.
3. **Production release.** Add release notes (reuse the CHANGELOG entry), review the
   pre-launch report the console generates, then roll out — staged rollout (e.g. 20%)
   is a good habit even for small apps.
4. **Review times** vary from a few hours to about a week, and are typically longer
   for a developer account's first app.

## 6. After launch

- Bump `versionCode`/`versionName` for every update; upload to internal testing
  before promoting to production.
- Watch *Quality → Android vitals* for crashes and ANRs.
- Keep `targetSdk` current — Google enforces a minimum target API level each year
  for both new apps and updates.

# ICEL Missal texts (local draft)

`mass_responses.en.json` holds the dialogues and the people's parts of the Order of Mass
in the 2010 English translation of the Roman Missal. That translation is ICEL's copyright.
ICEL has said an app needs its written permission. It will give that permission free of
charge, but only after the Conférence des évêques de France approves.

Until the permission arrives:

- **The JSON is git-ignored.** This repository is public, so committing the file would
  publish ICEL's text. It exists only on this Mac, so back it up.
- `:shared:syncIcelDraft` copies it into the app's resources **only for debug builds**.
  For any release build (a Gradle task containing "Release", or Xcode `CONFIGURATION=Release`)
  it removes the copy instead.
- At runtime the app also shows the texts only in debug builds (`IcelTexts.enabled`).

The same file is the full draft that ICEL asked to proofread.

When the letter of permission arrives:

1. Apply ICEL's proofreading corrections.
2. Set `IcelTexts.PERMISSION_GRANTED = true` in `liturgical/OrderOfMass.kt`.
3. Decide with ICEL whether the source file may sit in the public repo. If not, keep it
   out and copy it in for release builds.

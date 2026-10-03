# tools/translate

Machine translation of Cathopedia's English content with Claude. Read the
docstring at the top of `translate.py` for usage.

- `state/<lang>.json` — hash of the English each machine translation was made
  from. Delete an entry to force that string to be retranslated. Strings with
  no entry were written by a person and are never touched.
- `state/ui.<lang>.json` — the UI-chrome translations; `Strings<XX>.kt` and
  `VestmentStrings<XX>.kt` are generated from it (`--stubs` regenerates
  without calling the API). Fix a wording here, then run `--stubs`.
- `glossary/common.md`, `glossary/<lang>.md` — appended to the system prompt.
  Add a term here when reviewers correct the same word twice, then delete the
  affected `state` entries and re-run.
- Prayers are never machine-translated: they must be sourced from published
  texts (content/README.md).

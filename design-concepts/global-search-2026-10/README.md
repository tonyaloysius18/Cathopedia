# Global search verification

Search now uses destinations for encyclopedia entries, published topic articles and sections,
prayers, and the Rosary, Stations, Sacred Vestments and Order of Mass guides. It indexes body
text and localized facts as well as titles. Church addresses are searchable too.

- Common input variants: `turnible` → Thurible; `cannon law` → Canon Law.
- Unknown words receive a correction only when a unique close title word exists.
- Prefix matching remains available while typing; punctuation is treated as literal input.
- Topics and Prayers have their own result filters; cards open their matching screens.
- The selected language is preferred, with English and Latin prayer matches available.
- Content upgrades replace the derived index atomically and preserve prayer favourites/history.

Automated verification: `SearchQueryTest` (3 tests) and `GlobalSearchTest` (4 real SQLite tests).
The latter covers text inside articles, diagram references, specialized screen text, prayer
bodies and descriptions, Latin, language fallback, guides, addresses, localized facts,
spelling/punctuation, stale index removal and retained prayer user state.

Native Android result/navigation checks are recorded in `implementation-checks.json`.
The screenshots show the actual app, not generated concepts.

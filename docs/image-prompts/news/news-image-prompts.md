# Catholic News — image generation brief

Art for the cards in the Catholic News feed (`files/news/news.json`). Until
an item has art, its card draws its theme in code (WYD: red sun and gold
cross on night blue; papal: crossed keys on gold-umber), so these are an
upgrade, not a blocker.

**Do not use press photos, news-agency images or official logos.** They are
copyrighted or trademarked (Vatican Media, AP, Getty, the WYD Seoul logo).
Every image here is our own illustration.

## Rules for every news image

- Landscape **16:9, 1600×900**, full-bleed (no transparency).
- The **bottom third stays dark and quiet**: the title, date and summary sit
  on it. Keep the subject in the upper-right two thirds.
- Classical sacred-illustration finish, warm light, matching the app's other
  art. **No text, no letters, no logos, no watermark, no signature.**
- No recognisable likeness of a living person; figures are small, distant
  or seen from behind.
- Convert to WebP (q82) and save as
  `shared/src/commonMain/composeResources/files/news/images/<item-id>.webp`,
  then set `"image": "images/<item-id>.webp"` on the item. An image added
  later on `main` is fetched by older installs too.

**Negative prompt (all):** `text, letters, words, logo, watermark, signature,
photograph, press photo, close-up face, portrait, cartoon, flat vector, low detail`

---

## `wyd-seoul-2027.webp` — World Youth Day Seoul 2027

Dawn over Seoul: the city skyline with Namsan Tower and the brick Gothic
spire of Myeongdong Cathedral, under a deep night-blue sky warming to a red
rising sun. In the middle distance, a crowd of young pilgrims seen from
behind, carrying a tall plain wooden cross and many small coloured flags
(no readable national flags). Gold light on the cross. Joyful, hopeful,
“Take courage, I have overcome the world”. The lower third falls into dark
blue shadow over the crowd.

## `pope-leo-discharged-2026-10.webp` — Pope Leo XIV returns to the Vatican

Saint Peter's Square on a clear Sunday at midday, seen from the square
looking up at the Apostolic Palace. One upper window is open with a small
red-and-gold tapestry hanging from the sill; a tiny white-robed figure stands
at it, too distant to have a face. Pilgrims below, seen from behind, some
with rosaries raised. Soft golden autumn light, calm and grateful rather
than dramatic: no hospital, no medical imagery. The lower third falls into
the shadow of the colonnade.

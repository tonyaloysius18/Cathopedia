package com.ynotlabs.cathopedia.ui.theme

import androidx.compose.ui.graphics.Color

// Exact tokens from "Cathopedia — Colour & Image System". Two authentic
// references, not one theme and its inverse: dark is a church at night,
// light is an illuminated manuscript on parchment — not dark-mode-inverted-to-white.

// The brand gold — a true metallic gold rather than the old brass/ochre tone.
// Every other gold below is derived from this one hue (~46°) so dark, light,
// and category accents all read as the same metal at different depths.
val MetallicGold = Color(0xFFD4AF37)

// Dark — "the sanctuary"
val DarkBackground = Color(0xFF0B1A15)
val DarkSurface = Color(0xFF12241D)
val DarkRaised = Color(0xFF1A2E26)
val DarkText = Color(0xFFEFE7D6)
val DarkTextMuted = Color(0xFF9C9683)
// The floating pill bar needs to read as a distinct object sitting above the
// page, not a same-toned panel — DarkSurface is only ~1.6x DarkBackground's
// lightness, too close once it's a thin floating shape instead of a full-bleed
// screen. These sit a clear step further up the same green, with the active
// bubble a step further again for a visible bg < pill < bubble hierarchy.
val DarkPillSurface = Color(0xFF1F3D32)
val DarkPillBubble = Color(0xFF2C5645)
// MetallicGold sits at 8.5:1 contrast on this background as-is — no darkening needed.
val DarkGold = MetallicGold
// Lightened for non-text ornament only (rules, dividers, icon fills) — same hue, +10% lightness.
val DarkGoldBright = Color(0xFFDDBF5F)
val DarkLine = Color(0x1FEFE7D6) // rgba(239,231,214,.12)

// Light — "the manuscript"
val LightBackground = Color(0xFFF4EDDD)
val LightSurface = Color(0xFFFBF6EA)
val LightText = Color(0xFF1E1A14)
val LightTextMuted = Color(0xFF5C5445)
// MetallicGold on parchment lands ~1.8:1 — fails. Same hue/saturation, darkened
// to ~28% lightness gets to 5.2:1, which is what light mode uses for any text.
val LightGoldText = Color(0xFF766019)
val LightGreenDeep = Color(0xFF1F4034)
// Same reasoning as the dark pair above: LightSurface is only a few points
// off LightBackground, not enough separation for a floating shape. A deeper,
// slightly more saturated parchment for the pill, with the active bubble
// popping lighter than both — the reverse relationship of dark mode, but the
// same rule: the bubble is always the lightest, most prominent element.
val LightPillSurface = Color(0xFFECE0C4)
val LightPillBubble = Color(0xFFFFFDF7)

// Entity-category accent colours (both modes) — the chip/card colour-coding
// that makes the relation graph's shape readable at a glance.
val CategoryPeopleGreen = Color(0xFF3F5A3D) // saints, popes, apostles
val CategoryPlacesGold = Color(0xFFB59326) // churches — MetallicGold's hue at mid lightness
val CategoryEventsViolet = Color(0xFF5B3560) // apparitions, miracles
val CategoryFeastMustard = Color(0xFF7C6220) // feast days — deliberately a distinct, muddier mustard, not gold

// Liturgical accent palette. Only Ordinary Time's green is used before the
// calendar engine exists (v1.0) — see LiturgicalAccent.kt.
val LiturgicalOrdinary = Color(0xFF3F5A3D)
val LiturgicalAdventLent = Color(0xFF5B3560)
val LiturgicalChristmasEaster = CategoryPlacesGold
val LiturgicalPentecostMartyrs = Color(0xFF8C2E2E)
val LiturgicalGaudeteLaetare = Color(0xFFA85A72)

val AlertRed = Color(0xFF8C2E2E)

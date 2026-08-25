package com.ynotlabs.cathopedia.data

object PreferenceKeys {
    const val ONBOARDING_COMPLETE = "onboarding_complete"
    const val LANGUAGE = "language"
    const val THEME_MODE = "theme_mode"
    const val NOTIFICATIONS_ENABLED = "notifications_enabled"
    /** Prayer reading font scale, applies to every prayer/Rosary text screen. */
    const val PRAYER_FONT_SCALE = "prayer_font_scale"

    /** Rosary guided-mode session, so an interrupted Rosary can offer to resume. */
    const val ROSARY_MYSTERY_SET = "rosary_mystery_set"
    const val ROSARY_CURRENT_INDEX = "rosary_current_index"
    const val ROSARY_SESSION_STARTED_AT = "rosary_session_started_at"
    const val ROSARY_ORGANIC_SHAPE = "rosary_organic_shape"
    const val ROSARY_CAROUSEL_ON_LEFT = "rosary_carousel_on_left"
}

package com.ynotlabs.cathopedia.ui

import androidx.compose.ui.graphics.vector.ImageVector
import com.ynotlabs.cathopedia.model.PrayerCategory
import com.ynotlabs.cathopedia.resources.*
import org.jetbrains.compose.resources.DrawableResource

/**
 * Shared icon resolution logic for prayer categories.
 */
sealed class CategoryIcon {
    data class Vector(val imageVector: ImageVector) : CategoryIcon()
    data class Resource(val res: DrawableResource) : CategoryIcon()
}

fun getCategoryIcon(category: PrayerCategory): CategoryIcon = when (category) {
    PrayerCategory.EVERYDAY -> CategoryIcon.Resource(Res.drawable.prayer_category_everyday)
    PrayerCategory.MARIAN -> CategoryIcon.Resource(Res.drawable.prayer_category_marian)
    PrayerCategory.HOLY_SPIRIT -> CategoryIcon.Resource(Res.drawable.prayer_category_holy_spirit)
    PrayerCategory.EUCHARISTIC -> CategoryIcon.Resource(Res.drawable.prayer_category_eucharistic)
    PrayerCategory.SAINTS -> CategoryIcon.Resource(Res.drawable.prayer_category_saints)
    PrayerCategory.PENITENTIAL -> CategoryIcon.Resource(Res.drawable.prayer_category_penitential)
    PrayerCategory.SEQUENCES -> CategoryIcon.Resource(Res.drawable.prayer_category_sequences)
    PrayerCategory.OCCASIONAL -> CategoryIcon.Resource(Res.drawable.prayer_category_occasional)
}

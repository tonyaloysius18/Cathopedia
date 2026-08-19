package com.ynotlabs.cathopedia.ui

import androidx.compose.ui.graphics.Color
import com.ynotlabs.cathopedia.model.ContentCategory
import com.ynotlabs.cathopedia.model.ContentType
import com.ynotlabs.cathopedia.ui.theme.CategoryEventsViolet
import com.ynotlabs.cathopedia.ui.theme.CategoryFeastMustard
import com.ynotlabs.cathopedia.ui.theme.CategoryPeopleGreen
import com.ynotlabs.cathopedia.ui.theme.CategoryPlacesGold

fun ContentType.displayName(): String = when (this) {
    ContentType.SAINT -> "Saints"
    ContentType.POPE -> "Popes"
    ContentType.APOSTLE -> "Apostles"
    ContentType.CHURCH -> "Churches & Shrines"
    ContentType.APPARITION -> "Marian Apparitions"
    ContentType.MIRACLE -> "Eucharistic Miracles"
    ContentType.FEAST -> "Liturgical Feasts"
}

fun ContentType.singularLabel(): String = when (this) {
    ContentType.SAINT -> "Saint"
    ContentType.POPE -> "Pope"
    ContentType.APOSTLE -> "Apostle"
    ContentType.CHURCH -> "Church"
    ContentType.APPARITION -> "Apparition"
    ContentType.MIRACLE -> "Eucharistic Miracle"
    ContentType.FEAST -> "Liturgical Feast"
}

/** The colour-coding that makes the relation graph's shape readable at a glance. */
fun ContentCategory.accentColor(): Color = when (this) {
    ContentCategory.PEOPLE -> CategoryPeopleGreen
    ContentCategory.PLACES -> CategoryPlacesGold
    ContentCategory.EVENTS -> CategoryEventsViolet
    ContentCategory.FEASTS -> CategoryFeastMustard
}

fun ContentType.accentColor(): Color = ContentCategory.of(this).accentColor()

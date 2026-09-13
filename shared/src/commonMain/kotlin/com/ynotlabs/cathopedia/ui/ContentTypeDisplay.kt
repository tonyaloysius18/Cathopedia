package com.ynotlabs.cathopedia.ui

import androidx.compose.ui.graphics.Color
import com.ynotlabs.cathopedia.i18n.Strings
import com.ynotlabs.cathopedia.model.ContentCategory
import com.ynotlabs.cathopedia.model.ContentType
import com.ynotlabs.cathopedia.ui.theme.CategoryEventsViolet
import com.ynotlabs.cathopedia.ui.theme.CategoryFeastMustard
import com.ynotlabs.cathopedia.ui.theme.CategoryPeopleGreen
import com.ynotlabs.cathopedia.ui.theme.CategoryPlacesGold

fun ContentType.displayName(s: Strings): String = when (this) {
    ContentType.SAINT -> s.typeSaintsPlural
    ContentType.POPE -> s.typePopesPlural
    ContentType.APOSTLE -> s.typeApostlesPlural
    ContentType.CHURCH -> s.typeChurchesPlural
    ContentType.APPARITION -> s.typeApparitionsPlural
    ContentType.MIRACLE -> s.typeMiraclesPlural
    ContentType.FEAST -> s.typeFeastsPlural
    ContentType.DOCUMENT -> s.typeDocumentsPlural
}

fun ContentType.singularLabel(s: Strings): String = when (this) {
    ContentType.SAINT -> s.typeSaintSingular
    ContentType.POPE -> s.typePopeSingular
    ContentType.APOSTLE -> s.typeApostleSingular
    ContentType.CHURCH -> s.typeChurchSingular
    ContentType.APPARITION -> s.typeApparitionSingular
    ContentType.MIRACLE -> s.typeMiracleSingular
    ContentType.FEAST -> s.typeFeastSingular
    ContentType.DOCUMENT -> s.typeDocumentSingular
}

fun ContentCategory.label(s: Strings): String = when (this) {
    ContentCategory.PEOPLE -> s.categoryPeople
    ContentCategory.PLACES -> s.categoryPlaces
    ContentCategory.EVENTS -> s.categoryEvents
    ContentCategory.FEASTS -> s.categoryFeasts
    ContentCategory.DOCUMENTS -> s.categoryDocuments
}

/** The colour-coding that makes the relation graph's shape readable at a glance. */
fun ContentCategory.accentColor(): Color = when (this) {
    ContentCategory.PEOPLE -> CategoryPeopleGreen
    ContentCategory.PLACES -> CategoryPlacesGold
    ContentCategory.EVENTS -> CategoryEventsViolet
    ContentCategory.FEASTS -> CategoryFeastMustard
    ContentCategory.DOCUMENTS -> CategoryPlacesGold
}

fun ContentType.accentColor(): Color = ContentCategory.of(this).accentColor()

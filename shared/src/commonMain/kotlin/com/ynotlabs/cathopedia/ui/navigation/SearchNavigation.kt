package com.ynotlabs.cathopedia.ui.navigation

import com.ynotlabs.cathopedia.model.SearchTarget

fun SearchTarget.destination(): Destination = when (this) {
    is SearchTarget.Entity -> Destination.EntityDetail(type, id)
    is SearchTarget.Prayer -> Destination.PrayerDetail(id)
    is SearchTarget.Hub -> Destination.Hub(id)
    is SearchTarget.Article -> Destination.HubArticle(hubId, id)
    is SearchTarget.Section -> when (id) {
        "cat.sacraments" -> Destination.SacramentsScreen
        "mass.procession" -> Destination.ProcessionScreen
        else -> Destination.HubSection(hubId, id)
    }
    is SearchTarget.Guide -> when (id) {
        "rosary" -> Destination.RosaryScreen
        "stations" -> Destination.StationsScreen
        "mass" -> Destination.CelebrateMass
        else -> Destination.Vestments
    }
}

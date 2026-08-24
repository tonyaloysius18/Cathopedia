package com.ynotlabs.cathopedia.model

/** Domain read model for a row in `topic_hub` — see HubContent.sq. */
data class HubSummary(
    val id: String,
    val slug: String,
    val titleKey: String,
    val subtitleKey: String?,
    val icon: String?,
    val heroAsset: String?,
    val accentColor: String?,
    val sortOrder: Int,
)

/** Domain read model for a row in `hub_section` — see HubContent.sq. */
data class HubSectionSummary(
    val id: String,
    val sortOrder: Int,
    val status: String,
    val layout: String,
    val titleKey: String,
    val summaryKey: String?,
    val icon: String?,
    val accentColor: String?,
)

data class HubDetail(
    val summary: HubSummary,
    val introKey: String?,
    val sections: List<HubSectionSummary>,
)

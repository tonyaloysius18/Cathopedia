package com.ynotlabs.cathopedia.model

import com.ynotlabs.cathopedia.content.model.Block
import com.ynotlabs.cathopedia.content.model.EntityRef
import com.ynotlabs.cathopedia.content.model.Fact
import com.ynotlabs.cathopedia.content.model.HotspotShape
import com.ynotlabs.cathopedia.content.model.Source
import com.ynotlabs.cathopedia.content.model.Step
import com.ynotlabs.cathopedia.content.model.TimelineEvent

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
    val factSheetId: String?,
    val stepperId: String?,
    val timelineId: String?,
    val diagramId: String?,
)

data class HubDetail(
    val summary: HubSummary,
    val introKey: String?,
    val sections: List<HubSectionSummary>,
)

data class HubArticleSummary(
    val id: String,
    val sortOrder: Int,
    val titleKey: String,
    val leadKey: String?,
    val readingTimeMinutes: Int?,
)

data class HubArticleDetail(
    val id: String,
    val titleKey: String,
    val leadKey: String?,
    val blocks: List<Block>,
    val related: List<EntityRef>,
    val sources: List<Source>,
)

data class HubDiagramDetail(
    val id: String,
    val titleKey: String?,
    val captionKey: String?,
    val asset: String,
    val aspectRatio: Float,
    val minZoom: Float,
    val maxZoom: Float,
    val hotspots: List<HubHotspotDetail>,
)

data class HubHotspotDetail(
    val id: String,
    val labelKey: String,
    val blurbKey: String?,
    val order: Int?,
    val shape: HotspotShape,
    val target: EntityRef?,
)

data class HubFactSheetDetail(
    val id: String,
    val titleKey: String,
    val facts: List<Fact>,
)

data class HubStepperDetail(
    val id: String,
    val titleKey: String,
    val introKey: String?,
    val steps: List<Step>,
)

data class HubTimelineDetail(
    val id: String,
    val titleKey: String,
    val events: List<TimelineEvent>,
)

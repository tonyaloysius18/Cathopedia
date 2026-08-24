package com.ynotlabs.cathopedia.content

import kotlinx.serialization.json.Json

/**
 * Shared with [com.ynotlabs.cathopedia.data.ContentLoader] (encodes hub content into the
 * `*_json` columns on seed) and [com.ynotlabs.cathopedia.data.CathopediaRepository] (decodes
 * them back out for the UI) — encode/decode must use identical settings or the sealed
 * Block/HotspotShape hierarchy (see HubContentModels.kt) round-trips wrong.
 */
val hubContentJson = Json {
    ignoreUnknownKeys = true
    classDiscriminator = "type"
    explicitNulls = false
}

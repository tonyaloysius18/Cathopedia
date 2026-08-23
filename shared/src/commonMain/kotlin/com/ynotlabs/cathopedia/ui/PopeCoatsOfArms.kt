package com.ynotlabs.cathopedia.ui

import com.ynotlabs.cathopedia.resources.Res
import com.ynotlabs.cathopedia.resources.coa_innocent_iii
import com.ynotlabs.cathopedia.resources.coa_honorius_iii
import com.ynotlabs.cathopedia.resources.coa_gregory_ix
import com.ynotlabs.cathopedia.resources.coa_celestine_iv
import com.ynotlabs.cathopedia.resources.coa_alexander_iv
import com.ynotlabs.cathopedia.resources.coa_urban_iv
import com.ynotlabs.cathopedia.resources.coa_clement_iv
import com.ynotlabs.cathopedia.resources.coa_gregory_x
import com.ynotlabs.cathopedia.resources.coa_john_xxi
import com.ynotlabs.cathopedia.resources.coa_innocent_v
import com.ynotlabs.cathopedia.resources.coa_adrian_v
import com.ynotlabs.cathopedia.resources.coa_nicholas_iii
import com.ynotlabs.cathopedia.resources.coa_martin_iv
import com.ynotlabs.cathopedia.resources.coa_honorius_iv
import com.ynotlabs.cathopedia.resources.coa_nicholas_iv
import com.ynotlabs.cathopedia.resources.coa_celestine_v
import com.ynotlabs.cathopedia.resources.coa_boniface_viii
import com.ynotlabs.cathopedia.resources.coa_benedict_xi
import com.ynotlabs.cathopedia.resources.coa_clement_v
import com.ynotlabs.cathopedia.resources.coa_john_xxii
import com.ynotlabs.cathopedia.resources.coa_benedict_xii
import com.ynotlabs.cathopedia.resources.coa_clement_vi
import com.ynotlabs.cathopedia.resources.coa_innocent_vi
import com.ynotlabs.cathopedia.resources.coa_urban_v
import com.ynotlabs.cathopedia.resources.coa_gregory_xi
import com.ynotlabs.cathopedia.resources.coa_urban_vi
import com.ynotlabs.cathopedia.resources.coa_boniface_ix
import com.ynotlabs.cathopedia.resources.coa_innocent_vii
import com.ynotlabs.cathopedia.resources.coa_gregory_xii
import com.ynotlabs.cathopedia.resources.coa_martin_v
import com.ynotlabs.cathopedia.resources.coa_eugene_iv
import com.ynotlabs.cathopedia.resources.coa_nicholas_v
import com.ynotlabs.cathopedia.resources.coa_callixtus_iii
import com.ynotlabs.cathopedia.resources.coa_pius_ii
import com.ynotlabs.cathopedia.resources.coa_paul_ii
import com.ynotlabs.cathopedia.resources.coa_sixtus_iv
import com.ynotlabs.cathopedia.resources.coa_innocent_viii
import com.ynotlabs.cathopedia.resources.coa_alexander_vi
import com.ynotlabs.cathopedia.resources.coa_pius_iii
import com.ynotlabs.cathopedia.resources.coa_julius_ii
import com.ynotlabs.cathopedia.resources.coa_leo_x
import com.ynotlabs.cathopedia.resources.coa_adrian_vi
import com.ynotlabs.cathopedia.resources.coa_clement_vii
import com.ynotlabs.cathopedia.resources.coa_paul_iii
import com.ynotlabs.cathopedia.resources.coa_julius_iii
import com.ynotlabs.cathopedia.resources.coa_paul_iv
import com.ynotlabs.cathopedia.resources.coa_marcellus_ii
import com.ynotlabs.cathopedia.resources.coa_pius_iv
import com.ynotlabs.cathopedia.resources.coa_pius_v
import com.ynotlabs.cathopedia.resources.coa_gregory_xiii
import com.ynotlabs.cathopedia.resources.coa_sixtus_v
import com.ynotlabs.cathopedia.resources.coa_gregory_xiv
import com.ynotlabs.cathopedia.resources.coa_urban_vii
import com.ynotlabs.cathopedia.resources.coa_innocent_ix
import com.ynotlabs.cathopedia.resources.coa_clement_viii
import com.ynotlabs.cathopedia.resources.coa_leo_xi
import com.ynotlabs.cathopedia.resources.coa_paul_v
import com.ynotlabs.cathopedia.resources.coa_gregory_xv
import com.ynotlabs.cathopedia.resources.coa_urban_viii
import com.ynotlabs.cathopedia.resources.coa_innocent_x
import com.ynotlabs.cathopedia.resources.coa_alexander_vii
import com.ynotlabs.cathopedia.resources.coa_clement_ix
import com.ynotlabs.cathopedia.resources.coa_clement_x
import com.ynotlabs.cathopedia.resources.coa_innocent_xi
import com.ynotlabs.cathopedia.resources.coa_alexander_viii
import com.ynotlabs.cathopedia.resources.coa_innocent_xii
import com.ynotlabs.cathopedia.resources.coa_clement_xi
import com.ynotlabs.cathopedia.resources.coa_innocent_xiii
import com.ynotlabs.cathopedia.resources.coa_benedict_xiii
import com.ynotlabs.cathopedia.resources.coa_clement_xii
import com.ynotlabs.cathopedia.resources.coa_benedict_xiv
import com.ynotlabs.cathopedia.resources.coa_clement_xiii
import com.ynotlabs.cathopedia.resources.coa_clement_xiv
import com.ynotlabs.cathopedia.resources.coa_pius_vi
import com.ynotlabs.cathopedia.resources.coa_pius_vii
import com.ynotlabs.cathopedia.resources.coa_leo_xii
import com.ynotlabs.cathopedia.resources.coa_pius_viii
import com.ynotlabs.cathopedia.resources.coa_gregory_xvi
import com.ynotlabs.cathopedia.resources.coa_pius_ix
import com.ynotlabs.cathopedia.resources.coa_leo_xiii
import com.ynotlabs.cathopedia.resources.coa_pius_x
import com.ynotlabs.cathopedia.resources.coa_benedict_xv
import com.ynotlabs.cathopedia.resources.coa_pius_xi
import com.ynotlabs.cathopedia.resources.coa_pius_xii
import com.ynotlabs.cathopedia.resources.coa_john_xxiii
import com.ynotlabs.cathopedia.resources.coa_paul_vi
import com.ynotlabs.cathopedia.resources.coa_john_paul_i
import com.ynotlabs.cathopedia.resources.coa_john_paul_ii
import com.ynotlabs.cathopedia.resources.coa_benedict_xvi
import com.ynotlabs.cathopedia.resources.coa_francis
import com.ynotlabs.cathopedia.resources.coa_leo_xiv
import org.jetbrains.compose.resources.DrawableResource

/**
 * Coat-of-arms art for the pope detail screen, keyed by content/popes/<id>.json
 * id. Separate from [Portraits] for the same reason as [PrayerPortraits]: this
 * isn't a 1:1 mapping over every pope - personal papal heraldry only began
 * around Innocent III (1198), so popes before that have no entry here at all,
 * and [forPope] returning null is the honest "no coat of arms exists" case,
 * not a missing-asset bug. See content/popes/coat-of-arms-sources.json for the
 * citation backing each entry.
 *
 * Sourced from each pope's English Wikipedia infobox `coat_of_arms` field,
 * cross-checked against the Wikimedia Commons file title (or, for popes from
 * the same family - e.g. the Piccolomini, Della Rovere, Medici, Savelli or
 * Conti popes - against the shared family-arms file Wikipedia itself reuses
 * across those entries). One pope in scope, Innocent IV, has no coat_of_arms
 * value on English Wikipedia at all and is intentionally left out of this map
 * rather than guessed.
 *
 * Each entry needs its own explicit `import com.ynotlabs.cathopedia.resources.*`
 * line above (see Portraits.kt's own note on this) - Compose Resources codegen
 * makes the symbol available, but referencing it via `Res.drawable.x` without
 * the import is an "unresolved reference" despite the generated accessor
 * existing.
 */
object PopeCoatsOfArms {
    private val coatsOfArms: Map<String, DrawableResource> = mapOf(
        "innocent-iii" to Res.drawable.coa_innocent_iii,
        "honorius-iii" to Res.drawable.coa_honorius_iii,
        "gregory-ix" to Res.drawable.coa_gregory_ix,
        "celestine-iv" to Res.drawable.coa_celestine_iv,
        "alexander-iv" to Res.drawable.coa_alexander_iv,
        "urban-iv" to Res.drawable.coa_urban_iv,
        "clement-iv" to Res.drawable.coa_clement_iv,
        "gregory-x" to Res.drawable.coa_gregory_x,
        "john-xxi" to Res.drawable.coa_john_xxi,
        "innocent-v" to Res.drawable.coa_innocent_v,
        "adrian-v" to Res.drawable.coa_adrian_v,
        "nicholas-iii" to Res.drawable.coa_nicholas_iii,
        "martin-iv" to Res.drawable.coa_martin_iv,
        "honorius-iv" to Res.drawable.coa_honorius_iv,
        "nicholas-iv" to Res.drawable.coa_nicholas_iv,
        "celestine-v" to Res.drawable.coa_celestine_v,
        "boniface-viii" to Res.drawable.coa_boniface_viii,
        "benedict-xi" to Res.drawable.coa_benedict_xi,
        "clement-v" to Res.drawable.coa_clement_v,
        "john-xxii" to Res.drawable.coa_john_xxii,
        "benedict-xii" to Res.drawable.coa_benedict_xii,
        "clement-vi" to Res.drawable.coa_clement_vi,
        "innocent-vi" to Res.drawable.coa_innocent_vi,
        "urban-v" to Res.drawable.coa_urban_v,
        "gregory-xi" to Res.drawable.coa_gregory_xi,
        "urban-vi" to Res.drawable.coa_urban_vi,
        "boniface-ix" to Res.drawable.coa_boniface_ix,
        "innocent-vii" to Res.drawable.coa_innocent_vii,
        "gregory-xii" to Res.drawable.coa_gregory_xii,
        "martin-v" to Res.drawable.coa_martin_v,
        "eugene-iv" to Res.drawable.coa_eugene_iv,
        "nicholas-v" to Res.drawable.coa_nicholas_v,
        "callixtus-iii" to Res.drawable.coa_callixtus_iii,
        "pius-ii" to Res.drawable.coa_pius_ii,
        "paul-ii" to Res.drawable.coa_paul_ii,
        "sixtus-iv" to Res.drawable.coa_sixtus_iv,
        "innocent-viii" to Res.drawable.coa_innocent_viii,
        "alexander-vi" to Res.drawable.coa_alexander_vi,
        "pius-iii" to Res.drawable.coa_pius_iii,
        "julius-ii" to Res.drawable.coa_julius_ii,
        "leo-x" to Res.drawable.coa_leo_x,
        "adrian-vi" to Res.drawable.coa_adrian_vi,
        "clement-vii" to Res.drawable.coa_clement_vii,
        "paul-iii" to Res.drawable.coa_paul_iii,
        "julius-iii" to Res.drawable.coa_julius_iii,
        "paul-iv" to Res.drawable.coa_paul_iv,
        "marcellus-ii" to Res.drawable.coa_marcellus_ii,
        "pius-iv" to Res.drawable.coa_pius_iv,
        "pius-v" to Res.drawable.coa_pius_v,
        "gregory-xiii" to Res.drawable.coa_gregory_xiii,
        "sixtus-v" to Res.drawable.coa_sixtus_v,
        "gregory-xiv" to Res.drawable.coa_gregory_xiv,
        "urban-vii" to Res.drawable.coa_urban_vii,
        "innocent-ix" to Res.drawable.coa_innocent_ix,
        "clement-viii" to Res.drawable.coa_clement_viii,
        "leo-xi" to Res.drawable.coa_leo_xi,
        "paul-v" to Res.drawable.coa_paul_v,
        "gregory-xv" to Res.drawable.coa_gregory_xv,
        "urban-viii" to Res.drawable.coa_urban_viii,
        "innocent-x" to Res.drawable.coa_innocent_x,
        "alexander-vii" to Res.drawable.coa_alexander_vii,
        "clement-ix" to Res.drawable.coa_clement_ix,
        "clement-x" to Res.drawable.coa_clement_x,
        "innocent-xi" to Res.drawable.coa_innocent_xi,
        "alexander-viii" to Res.drawable.coa_alexander_viii,
        "innocent-xii" to Res.drawable.coa_innocent_xii,
        "clement-xi" to Res.drawable.coa_clement_xi,
        "innocent-xiii" to Res.drawable.coa_innocent_xiii,
        "benedict-xiii" to Res.drawable.coa_benedict_xiii,
        "clement-xii" to Res.drawable.coa_clement_xii,
        "benedict-xiv" to Res.drawable.coa_benedict_xiv,
        "clement-xiii" to Res.drawable.coa_clement_xiii,
        "clement-xiv" to Res.drawable.coa_clement_xiv,
        "pius-vi" to Res.drawable.coa_pius_vi,
        "pius-vii" to Res.drawable.coa_pius_vii,
        "leo-xii" to Res.drawable.coa_leo_xii,
        "pius-viii" to Res.drawable.coa_pius_viii,
        "gregory-xvi" to Res.drawable.coa_gregory_xvi,
        "pius-ix" to Res.drawable.coa_pius_ix,
        "leo-xiii" to Res.drawable.coa_leo_xiii,
        "pius-x" to Res.drawable.coa_pius_x,
        "benedict-xv" to Res.drawable.coa_benedict_xv,
        "pius-xi" to Res.drawable.coa_pius_xi,
        "pius-xii" to Res.drawable.coa_pius_xii,
        "john-xxiii" to Res.drawable.coa_john_xxiii,
        "paul-vi" to Res.drawable.coa_paul_vi,
        "john-paul-i" to Res.drawable.coa_john_paul_i,
        "john-paul-ii" to Res.drawable.coa_john_paul_ii,
        "benedict-xvi" to Res.drawable.coa_benedict_xvi,
        "francis" to Res.drawable.coa_francis,
        "leo-xiv" to Res.drawable.coa_leo_xiv,
    )

    fun forPope(id: String): DrawableResource? = coatsOfArms[id]
}

package com.ynotlabs.cathopedia.ui

import com.ynotlabs.cathopedia.resources.Res
import com.ynotlabs.cathopedia.resources.prayer_full_act_of_charity
import com.ynotlabs.cathopedia.resources.prayer_full_act_of_contrition
import com.ynotlabs.cathopedia.resources.prayer_full_act_of_faith
import com.ynotlabs.cathopedia.resources.prayer_full_act_of_hope
import com.ynotlabs.cathopedia.resources.prayer_full_adoro_te_devote
import com.ynotlabs.cathopedia.resources.prayer_full_after_confession
import com.ynotlabs.cathopedia.resources.prayer_full_angelus
import com.ynotlabs.cathopedia.resources.prayer_full_anima_christi
import com.ynotlabs.cathopedia.resources.prayer_full_apostles_creed
import com.ynotlabs.cathopedia.resources.prayer_full_aquinas_before_study
import com.ynotlabs.cathopedia.resources.prayer_full_before_confession
import com.ynotlabs.cathopedia.resources.prayer_full_before_exams
import com.ynotlabs.cathopedia.resources.prayer_full_confiteor
import com.ynotlabs.cathopedia.resources.prayer_full_de_profundis
import com.ynotlabs.cathopedia.resources.prayer_full_divine_mercy_chaplet
import com.ynotlabs.cathopedia.resources.prayer_full_divine_praises
import com.ynotlabs.cathopedia.resources.prayer_full_eternal_rest
import com.ynotlabs.cathopedia.resources.prayer_full_evening_prayer
import com.ynotlabs.cathopedia.resources.prayer_full_examination_of_conscience
import com.ynotlabs.cathopedia.resources.prayer_full_fatima_decade_prayer
import com.ynotlabs.cathopedia.resources.prayer_full_for_a_happy_death
import com.ynotlabs.cathopedia.resources.prayer_full_for_peace
import com.ynotlabs.cathopedia.resources.prayer_full_for_the_pope
import com.ynotlabs.cathopedia.resources.prayer_full_for_the_sick
import com.ynotlabs.cathopedia.resources.prayer_full_for_travel
import com.ynotlabs.cathopedia.resources.prayer_full_for_vocations
import com.ynotlabs.cathopedia.resources.prayer_full_glory_be
import com.ynotlabs.cathopedia.resources.prayer_full_grace_after_meals
import com.ynotlabs.cathopedia.resources.prayer_full_grace_before_meals
import com.ynotlabs.cathopedia.resources.prayer_full_guardian_angel
import com.ynotlabs.cathopedia.resources.prayer_full_hail_mary
import com.ynotlabs.cathopedia.resources.prayer_full_holy_rosary
import com.ynotlabs.cathopedia.resources.prayer_full_in_time_of_anxiety
import com.ynotlabs.cathopedia.resources.prayer_full_late_have_i_loved_you
import com.ynotlabs.cathopedia.resources.prayer_full_litany_of_loreto
import com.ynotlabs.cathopedia.resources.prayer_full_magnificat
import com.ynotlabs.cathopedia.resources.prayer_full_memorare
import com.ynotlabs.cathopedia.resources.prayer_full_montfort_consecration
import com.ynotlabs.cathopedia.resources.prayer_full_morning_offering
import com.ynotlabs.cathopedia.resources.prayer_full_nicene_creed
import com.ynotlabs.cathopedia.resources.prayer_full_novena_to_the_holy_spirit
import com.ynotlabs.cathopedia.resources.prayer_full_o_salutaris_hostia
import com.ynotlabs.cathopedia.resources.prayer_full_our_father
import com.ynotlabs.cathopedia.resources.prayer_full_peace_prayer_of_st_francis
import com.ynotlabs.cathopedia.resources.prayer_full_prayer_for_the_seven_gifts
import com.ynotlabs.cathopedia.resources.prayer_full_radiating_christ
import com.ynotlabs.cathopedia.resources.prayer_full_regina_caeli
import com.ynotlabs.cathopedia.resources.prayer_full_rosary_closing_prayer
import com.ynotlabs.cathopedia.resources.prayer_full_salve_regina
import com.ynotlabs.cathopedia.resources.prayer_full_seven_sorrows
import com.ynotlabs.cathopedia.resources.prayer_full_sign_of_the_cross
import com.ynotlabs.cathopedia.resources.prayer_full_spiritual_communion
import com.ynotlabs.cathopedia.resources.prayer_full_st_andrew_christmas_novena
import com.ynotlabs.cathopedia.resources.prayer_full_st_gertrude
import com.ynotlabs.cathopedia.resources.prayer_full_st_michael
import com.ynotlabs.cathopedia.resources.prayer_full_st_patricks_breastplate
import com.ynotlabs.cathopedia.resources.prayer_full_stations_of_the_cross
import com.ynotlabs.cathopedia.resources.prayer_full_sub_tuum_praesidium
import com.ynotlabs.cathopedia.resources.prayer_full_suscipe
import com.ynotlabs.cathopedia.resources.prayer_full_tantum_ergo
import com.ynotlabs.cathopedia.resources.prayer_full_thanksgiving_after_communion
import com.ynotlabs.cathopedia.resources.prayer_full_to_you_o_blessed_joseph
import com.ynotlabs.cathopedia.resources.prayer_full_veni_creator
import com.ynotlabs.cathopedia.resources.prayer_full_veni_sancte_spiritus
import org.jetbrains.compose.resources.DrawableResource

/**
 * Background art for the prayer reading screen, keyed by content/prayers/<id>.json
 * id. Separate from [Portraits] because prayers aren't a [com.ynotlabs.cathopedia.model.ContentType]
 * — they're keyed by [com.ynotlabs.cathopedia.model.PrayerCategory] instead, so they
 * don't fit that registry's `forEntity`/`fullForEntity` shape.
 *
 * Each entry needs its own explicit `import com.ynotlabs.cathopedia.resources.*` line
 * above (see Portraits.kt's own note on this) — Compose Resources codegen makes the
 * symbol available, but referencing it via `Res.drawable.x` without the import is an
 * "unresolved reference" despite the generated accessor existing.
 */
object PrayerPortraits {
    private val backgrounds: Map<String, DrawableResource> = mapOf(
        "act-of-charity" to Res.drawable.prayer_full_act_of_charity,
        "act-of-contrition" to Res.drawable.prayer_full_act_of_contrition,
        "act-of-faith" to Res.drawable.prayer_full_act_of_faith,
        "act-of-hope" to Res.drawable.prayer_full_act_of_hope,
        "adoro-te-devote" to Res.drawable.prayer_full_adoro_te_devote,
        "after-confession" to Res.drawable.prayer_full_after_confession,
        "angelus" to Res.drawable.prayer_full_angelus,
        "anima-christi" to Res.drawable.prayer_full_anima_christi,
        "apostles-creed" to Res.drawable.prayer_full_apostles_creed,
        "aquinas-before-study" to Res.drawable.prayer_full_aquinas_before_study,
        "before-confession" to Res.drawable.prayer_full_before_confession,
        "before-exams" to Res.drawable.prayer_full_before_exams,
        "confiteor" to Res.drawable.prayer_full_confiteor,
        "de-profundis" to Res.drawable.prayer_full_de_profundis,
        "divine-mercy-chaplet" to Res.drawable.prayer_full_divine_mercy_chaplet,
        "divine-praises" to Res.drawable.prayer_full_divine_praises,
        "eternal-rest" to Res.drawable.prayer_full_eternal_rest,
        "evening-prayer" to Res.drawable.prayer_full_evening_prayer,
        "examination-of-conscience" to Res.drawable.prayer_full_examination_of_conscience,
        "fatima-decade-prayer" to Res.drawable.prayer_full_fatima_decade_prayer,
        "for-a-happy-death" to Res.drawable.prayer_full_for_a_happy_death,
        "for-peace" to Res.drawable.prayer_full_for_peace,
        "for-the-pope" to Res.drawable.prayer_full_for_the_pope,
        "for-the-sick" to Res.drawable.prayer_full_for_the_sick,
        "for-travel" to Res.drawable.prayer_full_for_travel,
        "for-vocations" to Res.drawable.prayer_full_for_vocations,
        "glory-be" to Res.drawable.prayer_full_glory_be,
        "grace-after-meals" to Res.drawable.prayer_full_grace_after_meals,
        "grace-before-meals" to Res.drawable.prayer_full_grace_before_meals,
        "guardian-angel" to Res.drawable.prayer_full_guardian_angel,
        "hail-mary" to Res.drawable.prayer_full_hail_mary,
        "holy-rosary" to Res.drawable.prayer_full_holy_rosary,
        "in-time-of-anxiety" to Res.drawable.prayer_full_in_time_of_anxiety,
        "late-have-i-loved-you" to Res.drawable.prayer_full_late_have_i_loved_you,
        "litany-of-loreto" to Res.drawable.prayer_full_litany_of_loreto,
        "magnificat" to Res.drawable.prayer_full_magnificat,
        "memorare" to Res.drawable.prayer_full_memorare,
        "montfort-consecration" to Res.drawable.prayer_full_montfort_consecration,
        "morning-offering" to Res.drawable.prayer_full_morning_offering,
        "nicene-creed" to Res.drawable.prayer_full_nicene_creed,
        "novena-to-the-holy-spirit" to Res.drawable.prayer_full_novena_to_the_holy_spirit,
        "o-salutaris-hostia" to Res.drawable.prayer_full_o_salutaris_hostia,
        "our-father" to Res.drawable.prayer_full_our_father,
        "peace-prayer-of-st-francis" to Res.drawable.prayer_full_peace_prayer_of_st_francis,
        "prayer-for-the-seven-gifts" to Res.drawable.prayer_full_prayer_for_the_seven_gifts,
        "radiating-christ" to Res.drawable.prayer_full_radiating_christ,
        "regina-caeli" to Res.drawable.prayer_full_regina_caeli,
        "rosary-closing-prayer" to Res.drawable.prayer_full_rosary_closing_prayer,
        "salve-regina" to Res.drawable.prayer_full_salve_regina,
        "seven-sorrows" to Res.drawable.prayer_full_seven_sorrows,
        "sign-of-the-cross" to Res.drawable.prayer_full_sign_of_the_cross,
        "spiritual-communion" to Res.drawable.prayer_full_spiritual_communion,
        "st-andrew-christmas-novena" to Res.drawable.prayer_full_st_andrew_christmas_novena,
        "st-gertrude" to Res.drawable.prayer_full_st_gertrude,
        "st-michael" to Res.drawable.prayer_full_st_michael,
        "st-patricks-breastplate" to Res.drawable.prayer_full_st_patricks_breastplate,
        "stations-of-the-cross" to Res.drawable.prayer_full_stations_of_the_cross,
        "sub-tuum-praesidium" to Res.drawable.prayer_full_sub_tuum_praesidium,
        "suscipe" to Res.drawable.prayer_full_suscipe,
        "tantum-ergo" to Res.drawable.prayer_full_tantum_ergo,
        "thanksgiving-after-communion" to Res.drawable.prayer_full_thanksgiving_after_communion,
        "to-you-o-blessed-joseph" to Res.drawable.prayer_full_to_you_o_blessed_joseph,
        "veni-creator" to Res.drawable.prayer_full_veni_creator,
        "veni-sancte-spiritus" to Res.drawable.prayer_full_veni_sancte_spiritus,
    )

    fun forPrayer(id: String): DrawableResource? = backgrounds[id]
}

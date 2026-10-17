package com.ynotlabs.cathopedia.liturgical

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MassPosturesTest {
    @Test fun congregationPostureDoesNotFollowTheMinisterRole() {
        // A reader stands to proclaim; the congregation sits. The priest stands
        // at the preparation of the gifts while the congregation remains seated.
        for (part in listOf("first_reading", "psalm", "second_reading", "homily", "preparation")) {
            assertEquals(MassPosture.SIT, massPostureCues(part, OrdoProfile.GENERAL, "Weekday").single().posture)
        }
        assertEquals(MassPosture.STAND, massPostureCues("gospel", OrdoProfile.GENERAL, "Weekday").single().posture)
    }

    @Test fun kneelingCueExplainsTheScopeForTheSelectedCalendar() {
        val general = massPostureCues("eucharistic_prayer", OrdoProfile.GENERAL, "Weekday").single()
        val us = massPostureCues("eucharistic_prayer", OrdoProfile.US, "Weekday").single()
        assertEquals(MassPosture.KNEEL, general.posture)
        assertEquals("screen.mass.postures.at_consecration", general.detailKey)
        assertEquals("screen.mass.postures.local_custom", us.detailKey)
    }

    @Test fun incarnationGestureChangesOnChristmasAndAnnunciation() {
        for (celebration in listOf("Easter", "Christmas2")) {
            assertEquals(MassPosture.BOW, massPostureCues("creed", OrdoProfile.GENERAL, celebration).last().posture)
        }
        for (celebration in listOf("Christmas", "Annunciation")) {
            val cues = massPostureCues("creed", OrdoProfile.GENERAL, celebration)
            assertEquals(MassPosture.STAND, cues.first().posture)
            assertEquals(MassPosture.GENUFLECT, cues.last().posture)
            assertEquals("screen.mass.postures.at_incarnation", cues.last().detailKey)
        }
    }

    @Test fun variableOrUnknownPartsDoNotReceiveAnUnqualifiedPosture() {
        for (part in listOf("communion", "sequence", "unknown")) {
            assertTrue(massPostureCues(part, OrdoProfile.GENERAL, "Weekday").isEmpty())
        }
    }
}

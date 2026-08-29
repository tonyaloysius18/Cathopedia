package com.ynotlabs.cathopedia.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.i18n.Strings
import com.ynotlabs.cathopedia.ui.components.CathopediaBackButton
import com.ynotlabs.cathopedia.resources.Res
import com.ynotlabs.cathopedia.resources.arch_bishop
import com.ynotlabs.cathopedia.resources.bishop
import com.ynotlabs.cathopedia.resources.cardinal
import com.ynotlabs.cathopedia.resources.deacon
import com.ynotlabs.cathopedia.resources.pope
import com.ynotlabs.cathopedia.resources.priest
import com.ynotlabs.cathopedia.resources.alb_bishop
import com.ynotlabs.cathopedia.resources.alb_deacon
import com.ynotlabs.cathopedia.resources.alb_priest
import com.ynotlabs.cathopedia.resources.amice_deacon
import com.ynotlabs.cathopedia.resources.amice_priest
import com.ynotlabs.cathopedia.resources.archiepiscopal_cross
import com.ynotlabs.cathopedia.resources.biretta_cardinal
import com.ynotlabs.cathopedia.resources.biretta_priest
import com.ynotlabs.cathopedia.resources.cardinal_ring
import com.ynotlabs.cathopedia.resources.vestment_cardinal
import com.ynotlabs.cathopedia.resources.chasuble_archbishop
import com.ynotlabs.cathopedia.resources.chasuble_bishop
import com.ynotlabs.cathopedia.resources.chasuble_pope
import com.ynotlabs.cathopedia.resources.chasuble_priest
import com.ynotlabs.cathopedia.resources.cincture_deacon
import com.ynotlabs.cathopedia.resources.cincture_priest
import com.ynotlabs.cathopedia.resources.crozier_archbishop
import com.ynotlabs.cathopedia.resources.crozier_bishop
import com.ynotlabs.cathopedia.resources.dalmatic_deacon
import com.ynotlabs.cathopedia.resources.episcopal_ring_bishop
import com.ynotlabs.cathopedia.resources.ferula_pope
import com.ynotlabs.cathopedia.resources.fishermans_ring
import com.ynotlabs.cathopedia.resources.mitre_archbishop
import com.ynotlabs.cathopedia.resources.mitre_bishop
import com.ynotlabs.cathopedia.resources.mitre_pope
import com.ynotlabs.cathopedia.resources.mozzetta_cardinal
import com.ynotlabs.cathopedia.resources.pallium_archbishop
import com.ynotlabs.cathopedia.resources.papal_cassock
import com.ynotlabs.cathopedia.resources.papal_pallium
import com.ynotlabs.cathopedia.resources.papal_shoes
import com.ynotlabs.cathopedia.resources.pectoral_cross_bishop
import com.ynotlabs.cathopedia.resources.scarlet_cassock_cardinal
import com.ynotlabs.cathopedia.resources.stole_deacon
import com.ynotlabs.cathopedia.resources.stole_priest
import com.ynotlabs.cathopedia.resources.zucchetto_bishop
import com.ynotlabs.cathopedia.resources.zucchetto_cardinal
import com.ynotlabs.cathopedia.resources.zucchetto_priest
import kotlin.math.abs
import kotlin.math.min
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

private const val MINISTER_CARD_WIDTH_DP = 210
private const val MINISTER_CARD_HEIGHT_DP = 315
private const val MINISTER_SIDE_PADDING_DP = 96
private const val MINISTER_MAX_ROTATION_DEG = 38f
private const val MINISTER_MAX_SCALE_DROP = 0.24f
private const val MINISTER_MAX_ALPHA_DROP = 0.55f
private const val MINISTER_MAX_SHADE_ALPHA = 0.6f

private val VestBg = Color(0xFF061A13)
private val VestSurface = Color(0xFF0C271E)
private val VestSurfaceRaised = Color(0xFF123127)
private val VestGold = Color(0xFFD8B24C)
private val VestGoldSoft = Color(0xFF9D8858)
private val VestCream = Color(0xFFF4ECDD)
private val VestMuted = Color(0xFFB4AD98)

data class Vestment(
    val name: String,
    val purpose: String,
    val significance: String,
    val detail: String,
    val colorNote: String? = null,
    val image: DrawableResource? = null,
    val sourceName: String = name,
)

data class Minister(
    val id: String,
    val name: String,
    val title: String,
    val description: String,
    val image: DrawableResource,
    val vestments: List<Vestment>,
)

data class LiturgicalColor(
    val name: String,
    val swatch: Color,
    val occasion: String,
    val meaning: String,
    val needsBorder: Boolean = false,
)

private val ministers = listOf(
    Minister(
        id = "deacon",
        name = "Deacon",
        title = "Minister of the Word and the Altar",
        description = "Ordained to service, the deacon proclaims the Gospel, assists the priest at the altar, and distributes Holy Communion.",
        image = Res.drawable.deacon,
        vestments = listOf(
            Vestment(
                name = "Amice",
                purpose = "A rectangular linen cloth tied around the neck and shoulders before the alb is put on.",
                significance = "Recalls the \"helmet of salvation\" from Ephesians 6:17, guarding the mind against distraction during the liturgy.",
                detail = "Largely optional today, since most modern albs already have a built-in collar that covers ordinary clothing.",
                colorNote = "White linen",
            ),
            Vestment(
                name = "Alb",
                purpose = "A long, ankle-length white tunic worn as the base garment beneath all other vestments.",
                significance = "Symbolizes the purity of soul required to approach the altar, and recalls the white garment received at Baptism.",
                detail = "Derived from the everyday Roman tunic; every ordained and instituted minister at the altar wears one.",
                colorNote = "Always white",
            ),
            Vestment(
                name = "Cincture",
                purpose = "A cord or rope belt tied around the waist over the alb to gather in its fullness.",
                significance = "A symbol of chastity and readiness to serve, echoing the biblical image of \"girding one's loins\" for labor.",
                detail = "Often finished with tassels; its color can match the day's liturgical color in more elaborate sets.",
            ),
            Vestment(
                name = "Stole",
                purpose = "A long, narrow band of cloth that is the deacon's own distinguishing insignia of ordained ministry.",
                significance = "Represents the \"yoke of Christ\" taken up in service; for a deacon it marks his office as servant, not celebrant.",
                detail = "Worn diagonally, crossing from the left shoulder to the right hip and fastened near the waist — unlike a priest, who wears it straight down both shoulders.",
                colorNote = "Follows the liturgical color of the day",
            ),
            Vestment(
                name = "Dalmatic",
                purpose = "A wide-sleeved outer tunic worn over the alb and stole at Mass and other solemn celebrations.",
                significance = "Represents joy, justice, and salvation; it visually marks the deacon's proper share in the sacred ministry.",
                detail = "Named for the Roman province of Dalmatia, where the garment originated; it may be simplified or omitted at less solemn Masses.",
                colorNote = "Follows the liturgical color of the day",
            ),
        ),
    ),

    Minister(
        id = "priest",
        name = "Priest",
        title = "Celebrant of the Holy Eucharist",
        description = "Configured to Christ the High Priest, the priest offers the Sacrifice of the Mass and administers the sacraments in the person of Christ.",
        image = Res.drawable.priest,
        vestments = listOf(
            Vestment(
                name = "Amice",
                purpose = "A linen cloth tied around the neck and shoulders as the first vestment put on for Mass.",
                significance = "Recalls the \"helmet of salvation,\" a reminder to keep the mind fixed on the sacred action about to be offered.",
                detail = "Increasingly optional, since modern albs are usually designed to cover street clothing on their own.",
                colorNote = "White linen",
            ),
            Vestment(
                name = "Alb",
                purpose = "The long white tunic that forms the base of every Mass vestment, reaching to the ankles.",
                significance = "Represents the purity of soul with which the priest approaches the altar of sacrifice.",
                detail = "Recalls the white robe given at Baptism, when the Christian first put on Christ.",
                colorNote = "Always white",
            ),
            Vestment(
                name = "Cincture",
                purpose = "A rope-like cord tied around the waist to hold the alb close to the body.",
                significance = "A sign of priestly chastity and interior discipline in service of the altar.",
                detail = "In more solemn sets its color may echo the vestments of the day.",
            ),
            Vestment(
                name = "Stole",
                purpose = "The proper sign of priestly office, worn hanging straight down from both shoulders.",
                significance = "Represents the authority received at ordination to celebrate the sacraments in the person of Christ.",
                detail = "Crossed over the chest in some older forms of the Roman Rite; always worn straight in the current Ordinary Form.",
                colorNote = "Follows the liturgical color of the day",
            ),
            Vestment(
                name = "Biretta",
                purpose = "A stiff, square cap with three ridges, or \"horns,\" on top, traditionally worn to and from the altar and in choir.",
                significance = "An outward sign of the clerical state; for a priest it is worn plain, without the colored pom of higher rank.",
                detail = "Sometimes finished with a small pom at the center — black for an ordinary priest, other colors marking certain academic or honorary distinctions. Never worn during Mass itself.",
                colorNote = "Black",
            ),
            Vestment(
                name = "Zucchetto",
                purpose = "A small, close-fitting skullcap worn over the crown of the head as part of choir and everyday attire.",
                significance = "Its color marks rank at a glance across the hierarchy: white for the Pope, red for cardinals, violet for bishops, black for priests.",
                detail = "Removed during the Eucharistic Prayer and at other moments of deepest reverence, much like the biretta and mitre.",
                colorNote = "Black",
            ),
            Vestment(
                name = "Chasuble",
                purpose = "The outer, poncho-like garment worn over everything else as the principal vestment for Mass.",
                significance = "Represents charity, which \"covers a multitude of sins,\" and the gentle yoke of Christ that the priest willingly bears.",
                detail = "Descends from the paenula, a hooded traveling cloak common in the late Roman Empire, gradually reserved to sacred use.",
                colorNote = "Follows the liturgical color of the day",
            ),
        ),
    ),

    Minister(
        id = "bishop",
        name = "Bishop",
        title = "Successor of the Apostles",
        description = "Bearer of the fullness of Holy Orders, the bishop teaches, sanctifies, and governs a diocese as chief shepherd and high priest of his flock.",
        image = Res.drawable.bishop,
        vestments = listOf(
            Vestment(
                name = "Alb, Cincture & Stole",
                purpose = "The same foundational garments worn by every priest: the white tunic, its cord belt, and the stole of ordained office.",
                significance = "As a priest still, the bishop offers Mass wearing the same base vestments, now joined to the fullness of Holy Orders.",
                detail = "Beneath his pontificals the bishop typically wears a rochet, a fine linen garment trimmed with lace, in place of a simple alb.",
                colorNote = "Follows the liturgical color of the day",
            ),
            Vestment(
                name = "Pectoral Cross",
                purpose = "A cross worn on a chain or cord over the chest, resting near the heart.",
                significance = "A daily reminder for the bishop to carry the cross of Christ close to his heart in his pastoral ministry.",
                detail = "Often received at episcopal ordination and worn even outside of Mass as part of choir dress.",
            ),
            Vestment(
                name = "Episcopal Ring",
                purpose = "A ring worn on the right hand, presented at the rite of episcopal ordination.",
                significance = "A sign of fidelity and the bishop's spousal bond of care and love for his particular diocese.",
                detail = "Traditionally kissed as a sign of respect for the office, though the custom varies by region.",
            ),
            Vestment(
                name = "Zucchetto",
                purpose = "A small skullcap worn over the crown of the head as part of choir dress and everyday attire.",
                significance = "Marks the bishop's place in the Church's hierarchy of color, worn in violet rather than the priest's black or the cardinal's scarlet.",
                detail = "Removed, like the mitre, during the Eucharistic Prayer and other moments of deepest reverence at Mass.",
                colorNote = "Violet (amaranth)",
            ),
            Vestment(
                name = "Mitre",
                purpose = "The tall, peaked ceremonial headdress worn during solemn parts of the liturgy.",
                significance = "Its two peaks represent knowledge of the Old and New Testaments, signifying the bishop's authority to teach.",
                detail = "Removed during the Eucharistic Prayer and periods of prayer, and worn again for blessings and processions.",
                colorNote = "White or gold, often matching the vestments",
            ),
            Vestment(
                name = "Crozier",
                purpose = "A shepherd's crook carried in the left hand during processions and solemn blessings.",
                significance = "Represents the bishop's role as shepherd of his flock, with authority to guide, gather, and correct.",
                detail = "The curved head recalls a shepherd drawing sheep close, while the point once symbolized correction of the wayward.",
            ),
            Vestment(
                name = "Chasuble",
                purpose = "The outer Mass vestment worn by the bishop as principal celebrant, over a matching dalmatic and tunicle in the solemn pontifical form.",
                significance = "Signifies charity and the yoke of Christ, now borne by one who holds the fullness of the priesthood.",
                detail = "Often paired with a matching cope for processions before and after Mass in very solemn celebrations.",
                colorNote = "Follows the liturgical color of the day",
            ),
        ),
    ),

    Minister(
        id = "archbishop",
        name = "Archbishop",
        title = "Metropolitan of an Ecclesiastical Province",
        description = "A bishop entrusted with a metropolitan see, exercising oversight of the dioceses within his ecclesiastical province in communion with the Pope.",
        image = Res.drawable.arch_bishop,
        vestments = listOf(
            Vestment(
                name = "Pallium",
                purpose = "A narrow circular woolen band worn around the shoulders, resting over the chasuble, marked with six black crosses.",
                significance = "Recalls the lost sheep carried on the Good Shepherd's shoulders, and signifies the archbishop's communion with and authority received from the See of Rome.",
                detail = "Woven from the wool of two lambs blessed at the Basilica of St. Agnes in Rome on January 21 each year, and worn only within the archbishop's own province.",
                colorNote = "White wool with black crosses",
            ),
            Vestment(
                name = "Archiepiscopal Cross",
                purpose = "A cross with two horizontal bars, carried immediately before the archbishop in solemn processions.",
                significance = "A visible sign of his metropolitan jurisdiction over the province, distinct from an ordinary bishop's authority.",
                detail = "Used only within the archbishop's own territory, never outside it.",
            ),
            Vestment(
                name = "Zucchetto",
                purpose = "A small skullcap worn over the crown of the head as part of choir dress and everyday attire.",
                significance = "Worn in the same violet as a bishop's; it is the pallium, not the zucchetto, that visibly marks his metropolitan office.",
                detail = "Removed, like the mitre, during the Eucharistic Prayer and other moments of deepest reverence at Mass.",
                colorNote = "Violet (amaranth)",
            ),
            Vestment(
                name = "Mitre",
                purpose = "The peaked ceremonial headdress worn for solemn portions of the liturgy, as with any bishop.",
                significance = "Represents mastery of Scripture and the fullness of teaching authority now exercised over a wider province.",
                detail = "A plainer \"simple\" mitre is used on penitential days; the ornamented \"precious\" mitre is reserved for greater solemnities.",
                colorNote = "White or gold, often matching the vestments",
            ),
            Vestment(
                name = "Crozier",
                purpose = "The pastoral staff carried as a shepherd's crook during processions and blessings.",
                significance = "Signifies pastoral care extended not just to one diocese but to the archbishop's entire ecclesiastical province.",
                detail = "Carried with the crook turned outward, toward the people, when the bishop is within his own territory.",
            ),
            Vestment(
                name = "Chasuble",
                purpose = "The principal outer Mass vestment, worn beneath the pallium when celebrating the Eucharist.",
                significance = "Represents charity and the yoke of Christ carried by the chief shepherd of the province.",
                detail = "The pallium is worn directly over it, visible resting on the chest, shoulders, and back.",
                colorNote = "Follows the liturgical color of the day",
            ),
        ),
    ),

    Minister(
        id = "cardinal",
        name = "Cardinal",
        title = "Prince of the Church",
        description = "A senior prelate named by the Pope to advise the Holy See and, if under eighty, to take part in electing his successor.",
        image = Res.drawable.cardinal,
        vestments = listOf(
            Vestment(
                name = "Scarlet Cassock",
                purpose = "The long, buttoned garment worn as everyday and choir attire, piped and buttoned in scarlet.",
                significance = "The color symbolizes a cardinal's willingness to shed his blood in defense of the faith, in imitation of the martyrs and Apostles.",
                detail = "Its shade, cardinal red, is reserved to this rank in the Church's hierarchy of color.",
                colorNote = "Scarlet red",
            ),
            Vestment(
                name = "Zucchetto",
                purpose = "A small, close-fitting skullcap worn as part of choir dress.",
                significance = "Its color marks rank at a glance across the hierarchy: white for the Pope, red for cardinals, violet for bishops, black for priests.",
                detail = "Removed during the Eucharistic Prayer, much like the mitre, as a sign of reverence.",
                colorNote = "Scarlet red",
            ),
            Vestment(
                name = "Biretta",
                purpose = "A stiff, square cap with three or four ridges, traditionally worn to and from the sanctuary.",
                significance = "A further outward mark of the cardinal's rank and dignity within the college of cardinals.",
                detail = "Never worn during the Mass itself, only in procession or choir.",
                colorNote = "Scarlet red",
            ),
            Vestment(
                name = "Mozzetta",
                purpose = "A short, elbow-length cape worn over the cassock as part of formal choir dress.",
                significance = "A visible sign of jurisdiction and office, distinct from the simple cassock alone.",
                detail = "Also worn, in white, by the Pope, and in other colors by bishops and abbots according to their rank.",
                colorNote = "Scarlet red",
            ),
            Vestment(
                name = "Cardinal's Ring",
                purpose = "A ring presented by the Pope at the consistory in which a new cardinal is created.",
                significance = "A sign of the cardinal's close bond of fidelity and service to the Holy See.",
                detail = "Often bears the coat of arms of the reigning Pope who bestowed it.",
            ),
            Vestment(
                name = "Pontifical Mass Vestments",
                purpose = "The alb, stole, and chasuble worn by a cardinal, most of whom are also bishops, when celebrating Mass.",
                significance = "At the altar the cardinal wears the same charity-signifying chasuble as any other bishop or priest.",
                detail = "The scarlet of choir dress is not used for the Mass chasuble itself, which instead follows the liturgical color of the day.",
                colorNote = "Follows the liturgical color of the day",
            ),
        ),
    ),

    Minister(
        id = "pope",
        name = "Pope",
        title = "Bishop of Rome, Successor of St. Peter",
        description = "The Vicar of Christ and visible head of the universal Church on earth, entrusted with the pastoral care of all the faithful.",
        image = Res.drawable.pope,
        vestments = listOf(
            Vestment(
                name = "Papal Cassock (Simar)",
                purpose = "The simple white cassock worn as the Pope's everyday attire, distinct from the color worn by any other rank.",
                significance = "White signifies purity and the unique, universal character of the papal ministry as chief shepherd of the whole Church.",
                detail = "Tradition holds the custom began with the Dominican Pope St. Pius V, who kept the white habit of his religious order after his election.",
                colorNote = "Always white",
            ),
            Vestment(
                name = "Papal Red Shoes",
                purpose = "Red shoes traditionally worn by the Pope as part of his everyday and ceremonial dress.",
                significance = "Red recalls the blood of the martyrs and of Christ's own Passion, a reminder that the papal ministry is one of self-sacrificing service even unto death.",
                detail = "A centuries-old custom of the papacy, sometimes called the \"shoes of the fisherman\"; not every modern Pope has worn them consistently, but they remain a recognized element of traditional papal dress.",
                colorNote = "Red",
            ),
            Vestment(
                name = "Papal Pallium",
                purpose = "A woolen band worn over the chasuble at Mass, longer than the version given to archbishops, with pendants at front and back.",
                significance = "Signifies the fullness of the pastoral office as chief shepherd over the entire universal Church.",
                detail = "Received by a new Pope near the beginning of his pontificate as a sign of his ministry of unity.",
                colorNote = "White wool with black crosses",
            ),
            Vestment(
                name = "Fisherman's Ring",
                purpose = "A ring bearing an image of St. Peter fishing from a boat, historically used to seal papal documents.",
                significance = "Recalls St. Peter the fisherman and Christ's call to the Apostles to become \"fishers of men.\"",
                detail = "Ceremonially destroyed at the end of a pontificate so it cannot be used to forge documents in the Pope's name.",
            ),
            Vestment(
                name = "Mitre",
                purpose = "The peaked liturgical headdress worn by the Pope for solemn portions of the Mass, as with any bishop.",
                significance = "Signifies the Pope's supreme teaching authority, the Magisterium, exercised for the whole Church.",
                detail = "Simpler forms are used on ordinary occasions; more ornate mitres appear at the greatest solemnities.",
                colorNote = "White or gold, often matching the vestments",
            ),
            Vestment(
                name = "Ferula",
                purpose = "A tall staff topped with a crucifix, carried by the Pope in place of an ordinary crozier.",
                significance = "Represents Christ himself shepherding the universal Church through the ministry of Peter's successor.",
                detail = "Its use was revived in the modern era; earlier Popes more often processed without a staff at all.",
            ),
            Vestment(
                name = "Chasuble",
                purpose = "The outer Mass vestment worn by the Pope as celebrant, in the color proper to the day's liturgy.",
                significance = "Unites the Pope, as chief celebrant of the universal Church, with every priest offering Mass at every altar in the same charity of Christ.",
                detail = "Worn beneath the pallium, which rests visibly over it at the shoulders and chest.",
                colorNote = "Follows the liturgical color of the day",
            ),
        ),
    ),
)

private val liturgicalColors = listOf(
    LiturgicalColor(
        name = "White",
        swatch = Color(0xFFF5F0E6),
        occasion = "Christmas, Easter, feasts of Our Lord, Mary, and the angels, and feasts of saints who were not martyrs.",
        meaning = "Purity, innocence, and glory — the joy of the Resurrection and the holiness of God.",
        needsBorder = true,
    ),
    LiturgicalColor(
        name = "Red",
        swatch = Color(0xFFB3221D),
        occasion = "Palm Sunday, Good Friday, Pentecost, and feasts of martyrs, Apostles, and Evangelists.",
        meaning = "The blood of martyrdom and the fire of the Holy Spirit poured out on the Church.",
    ),
    LiturgicalColor(
        name = "Green",
        swatch = Color(0xFF1F7A4D),
        occasion = "Ordinary Time, the long season of growth between the great feasts of the liturgical year.",
        meaning = "Hope, and the steady growth of the Christian life and virtue over time.",
    ),
    LiturgicalColor(
        name = "Violet (Purple)",
        swatch = Color(0xFF5B2A86),
        occasion = "Advent and Lent, as well as funerals, All Souls' Day, and other penitential rites.",
        meaning = "Penance, preparation, and quiet mourning as the Church awaits the coming of Christ.",
    ),
    LiturgicalColor(
        name = "Rose (Pink)",
        swatch = Color(0xFFE293B3),
        occasion = "Gaudete Sunday (the third Sunday of Advent) and Laetare Sunday (the fourth Sunday of Lent).",
        meaning = "A brief note of joyful anticipation, lightening the penitential violet at the midpoint of the season.",
    ),
    LiturgicalColor(
        name = "Gold",
        swatch = Color(0xFFD4AF37),
        occasion = "May substitute for white, red, or green on the most solemn feasts, such as Christmas and Easter.",
        meaning = "The triumph, majesty, and radiant glory of God on the Church's greatest days.",
    ),
    LiturgicalColor(
        name = "Blue",
        swatch = Color(0xFF1F4E9C),
        occasion = "Not a universal Roman color; permitted by special indult in places such as Spain and Portugal for Marian feasts.",
        meaning = "Honors the purity and heavenly queenship of the Blessed Virgin Mary.",
    ),
    LiturgicalColor(
        name = "Black",
        swatch = Color(0xFF17140F),
        occasion = "Traditionally worn for Requiem Masses, All Souls' Day, and funerals.",
        meaning = "Mourning and the sober reality of death, held in hope of the Resurrection.",
        needsBorder = true,
    ),
)

private fun Strings.vestmentText(source: String): String = vestmentTranslations[source] ?: source

private fun Minister.localized(s: Strings): Minister = copy(
    name = s.vestmentText(name),
    title = s.vestmentText(title),
    description = s.vestmentText(description),
    vestments = vestments.map { it.localized(s) },
)

private fun Vestment.localized(s: Strings): Vestment = copy(
    name = s.vestmentText(name),
    purpose = s.vestmentText(purpose),
    significance = s.vestmentText(significance),
    detail = s.vestmentText(detail),
    colorNote = colorNote?.let(s::vestmentText),
)

private fun LiturgicalColor.localized(s: Strings): LiturgicalColor = copy(
    name = s.vestmentText(name),
    occasion = s.vestmentText(occasion),
    meaning = s.vestmentText(meaning),
)

@Composable
fun VestmentsScreen(
    onBackClick: () -> Unit = {},
) {
    val s = LocalStrings.current
    val localizedMinisters = remember(s) { ministers.map { it.localized(s) } }
    val localizedLiturgicalColors = remember(s) { liturgicalColors.map { it.localized(s) } }
    val listState = rememberLazyListState()
    val density = LocalDensity.current
    val cardWidthPx = with(density) { MINISTER_CARD_WIDTH_DP.dp.toPx() }
    val scope = rememberCoroutineScope()

    var selectedIndex by remember { mutableStateOf(1) }
    var headerHeightPx by remember { mutableIntStateOf(0) }
    val headerHeightDp = with(density) { headerHeightPx.toDp() }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo }
            .collect { layoutInfo ->
                if (layoutInfo.visibleItemsInfo.isEmpty()) return@collect
                val viewportCenter = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
                val closest = layoutInfo.visibleItemsInfo.minByOrNull {
                    abs((it.offset + it.size / 2) - viewportCenter)
                }
                closest?.let { selectedIndex = it.index }
            }
    }

    val selected = localizedMinisters[selectedIndex]

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VestBg),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = headerHeightDp + 16.dp),
            contentPadding = PaddingValues(
                start = 18.dp,
                end = 18.dp,
                bottom = 124.dp,
            ),
        ) {
            item {
                MinisterIntro(minister = selected)

                Spacer(Modifier.height(22.dp))

                VestmentsSectionHeader(
                    title = s.vestmentsRegaliaSection,
                    count = selected.vestments.size,
                )

                Spacer(Modifier.height(10.dp))
            }

            items(selected.vestments, key = { "${selected.id}-${it.name}" }) { vestment ->
                VestmentCard(vestment = vestment.copy(image = vestmentImage(vestment.sourceName, selected.id)))
                Spacer(Modifier.height(10.dp))
            }

            item {
                Spacer(Modifier.height(12.dp))

                VestmentsSectionHeader(
                    title = s.vestmentsColorsSection,
                    count = localizedLiturgicalColors.size,
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = s.vestmentsColorsDescription,
                    color = VestMuted,
                    fontSize = 12.5.sp,
                    lineHeight = 17.sp,
                )

                Spacer(Modifier.height(12.dp))

                LiturgicalColorGrid(colors = localizedLiturgicalColors)
            }
        }

        VestmentsHeaderCard(
            ministers = localizedMinisters,
            listState = listState,
            cardWidthPx = cardWidthPx,
            currentIndex = selectedIndex,
            onBackClick = onBackClick,
            onCardClick = { index ->
                scope.launch { listState.animateScrollToItem(index) }
            },
            modifier = Modifier.onGloballyPositioned {
                headerHeightPx = it.size.height
            }
        )
    }
}

@Composable
private fun VestmentsHeaderCard(
    ministers: List<Minister>,
    listState: LazyListState,
    cardWidthPx: Float,
    currentIndex: Int,
    onBackClick: () -> Unit,
    onCardClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val s = LocalStrings.current
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 14.dp,
                shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
                clip = false
            ),
        color = VestSurface,
        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
        border = BorderStroke(1.dp, VestGold.copy(alpha = 0.35f)),
    ) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 18.dp, top = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CathopediaBackButton(
                    onClick = onBackClick,
                    contentDescription = s.back,
                )

                Spacer(Modifier.width(10.dp))

                Column {
                    Text(
                        text = s.vestmentsTitle,
                        color = VestCream,
                        fontFamily = FontFamily.Serif,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.SemiBold,
                    )

                    Text(
                        text = s.vestmentsSubtitle,
                        color = VestGoldSoft,
                        fontSize = 14.sp,
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            LazyRow(
                state = listState,
                flingBehavior = rememberSnapFlingBehavior(listState),
                contentPadding = PaddingValues(horizontal = MINISTER_SIDE_PADDING_DP.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MINISTER_CARD_HEIGHT_DP.dp),
            ) {
                itemsIndexed(ministers, key = { _, minister -> minister.id }) { index, minister ->
                    MinisterFigureCard(
                        minister = minister,
                        index = index,
                        listState = listState,
                        cardWidthPx = cardWidthPx,
                        isSelected = index == currentIndex,
                        onClick = { onCardClick(index) },
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            MinisterDots(count = ministers.size, currentIndex = currentIndex)
        }
    }
}

@Composable
private fun MinisterFigureCard(
    minister: Minister,
    index: Int,
    listState: LazyListState,
    cardWidthPx: Float,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(22.dp)

    fun carouselNormalized(): Float? {
        val layoutInfo = listState.layoutInfo
        val itemInfo = layoutInfo.visibleItemsInfo.firstOrNull { it.index == index } ?: return null
        val viewportCenter = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2f
        val itemCenter = itemInfo.offset + itemInfo.size / 2f
        return ((itemCenter - viewportCenter) / cardWidthPx).coerceIn(-1.6f, 1.6f)
    }

    Box(
        modifier = Modifier
            .width(MINISTER_CARD_WIDTH_DP.dp)
            .fillMaxSize()
            .graphicsLayer {
                cameraDistance = 16f * density
                val normalized = carouselNormalized()
                if (normalized != null) {
                    rotationY = normalized * MINISTER_MAX_ROTATION_DEG
                    val scale = 1f - min(abs(normalized), 1f) * MINISTER_MAX_SCALE_DROP
                    scaleX = scale
                    scaleY = scale
                    alpha = 1f - min(abs(normalized), 1f) * MINISTER_MAX_ALPHA_DROP
                    translationX = -normalized * cardWidthPx * 0.18f
                }
            }
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(VestSurfaceRaised, VestSurface),
                ),
            )
            .border(
                width = if (isSelected) 1.6.dp else 1.dp,
                color = if (isSelected) VestGold else VestGold.copy(alpha = 0.35f),
                shape = shape,
            )
            .clickable(onClick = onClick)
            .drawWithContent {
                drawContent()

                val normalized = carouselNormalized() ?: return@drawWithContent
                val shade = min(abs(normalized), 1f) * MINISTER_MAX_SHADE_ALPHA
                if (shade <= 0f) return@drawWithContent

                // Simulates a curved, cylindrical surface
                val gradient = if (normalized >= 0f) {
                    Brush.horizontalGradient(
                        0f to Color.Transparent,
                        1f to Color.Black.copy(alpha = shade),
                    )
                } else {
                    Brush.horizontalGradient(
                        0f to Color.Black.copy(alpha = shade),
                        1f to Color.Transparent,
                    )
                }
                drawRect(brush = gradient)
            },
    ) {
        Image(
            painter = painterResource(minister.image),
            contentDescription = minister.name,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 10.dp, top = 10.dp, end = 15.dp),
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.62f to Color.Transparent,
                        1f to VestSurface.copy(alpha = 0.96f),
                    ),
                ),
        )
    }
}

@Composable
private fun MinisterDots(count: Int, currentIndex: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        repeat(count) { i ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .size(if (i == currentIndex) 8.dp else 6.dp)
                    .clip(CircleShape)
                    .background(if (i == currentIndex) VestGold else VestGold.copy(alpha = 0.22f)),
            )
        }
    }
}

@Composable
private fun MinisterIntro(minister: Minister) {
    Column {
        Text(
            text = minister.name,
            color = VestGold,
            fontFamily = FontFamily.Serif,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(Modifier.height(2.dp))

        Text(
            text = minister.title,
            color = VestCream,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = minister.description,
            color = VestMuted,
            fontSize = 13.sp,
            lineHeight = 18.sp,
        )
    }
}

@Composable
private fun VestmentsSectionHeader(
    title: String,
    count: Int,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(20.dp)
                .background(VestGold, RoundedCornerShape(2.dp)),
        )

        Spacer(Modifier.width(10.dp))

        Text(
            text = title.uppercase(),
            color = VestGold,
            fontSize = 12.sp,
            letterSpacing = 1.1.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.width(8.dp))

        Text(
            text = "• $count",
            color = VestGoldSoft,
            fontSize = 12.sp,
        )
    }
}

@Composable
private fun vestmentImage(name: String, ministerId: String): DrawableResource? = when (ministerId) {
    "deacon" -> when (name) {
        "Amice" -> Res.drawable.amice_deacon
        "Alb" -> Res.drawable.alb_deacon
        "Cincture" -> Res.drawable.cincture_deacon
        "Stole" -> Res.drawable.stole_deacon
        "Dalmatic" -> Res.drawable.dalmatic_deacon
        else -> null
    }
    "priest" -> when (name) {
        "Amice" -> Res.drawable.amice_priest
        "Alb" -> Res.drawable.alb_priest
        "Cincture" -> Res.drawable.cincture_priest
        "Stole" -> Res.drawable.stole_priest
        "Biretta" -> Res.drawable.biretta_priest
        "Zucchetto" -> Res.drawable.zucchetto_priest
        "Chasuble" -> Res.drawable.chasuble_priest
        else -> null
    }
    "bishop" -> when (name) {
        "Alb, Cincture & Stole" -> Res.drawable.alb_bishop
        "Pectoral Cross" -> Res.drawable.pectoral_cross_bishop
        "Episcopal Ring" -> Res.drawable.episcopal_ring_bishop
        "Zucchetto" -> Res.drawable.zucchetto_bishop
        "Mitre" -> Res.drawable.mitre_bishop
        "Crozier" -> Res.drawable.crozier_bishop
        "Chasuble" -> Res.drawable.chasuble_bishop
        else -> null
    }
    "archbishop" -> when (name) {
        "Pallium" -> Res.drawable.pallium_archbishop
        "Archiepiscopal Cross" -> Res.drawable.archiepiscopal_cross
        "Zucchetto" -> Res.drawable.zucchetto_bishop
        "Mitre" -> Res.drawable.mitre_archbishop
        "Crozier" -> Res.drawable.crozier_archbishop
        "Chasuble" -> Res.drawable.chasuble_archbishop
        else -> null
    }
    "cardinal" -> when (name) {
        "Scarlet Cassock" -> Res.drawable.scarlet_cassock_cardinal
        "Zucchetto" -> Res.drawable.zucchetto_cardinal
        "Biretta" -> Res.drawable.biretta_cardinal
        "Mozzetta" -> Res.drawable.mozzetta_cardinal
        "Cardinal's Ring" -> Res.drawable.cardinal_ring
        "Pontifical Mass Vestments" -> Res.drawable.vestment_cardinal
        else -> null
    }
    "pope" -> when (name) {
        "Papal Cassock (Simar)" -> Res.drawable.papal_cassock
        "Papal Red Shoes" -> Res.drawable.papal_shoes
        "Papal Pallium" -> Res.drawable.papal_pallium
        "Fisherman's Ring" -> Res.drawable.fishermans_ring
        "Mitre" -> Res.drawable.mitre_pope
        "Ferula" -> Res.drawable.ferula_pope
        "Chasuble" -> Res.drawable.chasuble_pope
        else -> null
    }
    else -> null
}

@Composable
private fun VestmentCard(vestment: Vestment) {
    val s = LocalStrings.current
    val shape = RoundedCornerShape(20.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(VestSurfaceRaised, VestSurface),
                ),
            )
            .border(1.dp, VestGold.copy(alpha = 0.35f), shape)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = vestment.name,
                    color = VestCream,
                    fontFamily = FontFamily.Serif,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f).padding(end = 65.dp),
                )
            }

            vestment.colorNote?.let { note ->
                Spacer(Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(VestGold.copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = note,
                        color = VestGold,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            VestmentDetailRow(label = s.vestmentsPurposeLabel, body = vestment.purpose)

            Spacer(Modifier.height(10.dp))

            VestmentDetailRow(label = s.vestmentsSignificanceLabel, body = vestment.significance)

            Spacer(Modifier.height(10.dp))

            VestmentDetailRow(label = s.vestmentsGoodToKnowLabel, body = vestment.detail)
        }


        vestment.image?.let { image ->
            Image(
                painter = painterResource(image),
                contentDescription = vestment.name,
                modifier = Modifier
                    .size(95.dp)
                    .align(Alignment.TopEnd)
                    .padding(top = 10.dp, end = 10.dp),
                contentScale = ContentScale.Fit,
            )
        }
    }
}

@Composable
private fun VestmentDetailRow(label: String, body: String) {
    Column {
        Text(
            text = label.uppercase(),
            color = VestGoldSoft,
            fontSize = 10.sp,
            letterSpacing = 0.8.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.height(3.dp))

        Text(
            text = body,
            color = VestMuted,
            fontSize = 13.sp,
            lineHeight = 18.sp,
        )
    }
}

@Composable
private fun LiturgicalColorGrid(colors: List<LiturgicalColor>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        colors.chunked(2).forEach { rowColors ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                rowColors.forEach { color ->
                    LiturgicalColorCard(
                        color = color,
                        modifier = Modifier.weight(1f),
                    )
                }

                if (rowColors.size == 1) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun LiturgicalColorCard(
    color: LiturgicalColor,
    modifier: Modifier = Modifier,
) {
    val s = LocalStrings.current
    val shape = RoundedCornerShape(18.dp)

    Column(
        modifier = modifier
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(VestSurfaceRaised, VestSurface),
                ),
            )
            .border(1.dp, VestGold.copy(alpha = 0.35f), shape)
            .padding(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(color.swatch)
                    .then(
                        if (color.needsBorder) {
                            Modifier.border(1.dp, VestGold.copy(alpha = 0.5f), CircleShape)
                        } else {
                            Modifier
                        },
                    ),
            )

            Spacer(Modifier.width(10.dp))

            Text(
                text = color.name,
                color = VestCream,
                fontFamily = FontFamily.Serif,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }

        Spacer(Modifier.height(10.dp))

        Text(
            text = s.vestmentsWornForLabel.uppercase(),
            color = VestGoldSoft,
            fontSize = 9.5.sp,
            letterSpacing = 0.7.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.height(2.dp))

        Text(
            text = color.occasion,
            color = VestMuted,
            fontSize = 12.sp,
            lineHeight = 16.sp,
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = s.vestmentsSignificanceLabel.uppercase(),
            color = VestGoldSoft,
            fontSize = 9.5.sp,
            letterSpacing = 0.7.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.height(2.dp))

        Text(
            text = color.meaning,
            color = VestMuted,
            fontSize = 12.sp,
            lineHeight = 16.sp,
        )
    }
}

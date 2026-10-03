package com.ynotlabs.cathopedia.sacraments

/**
 * One of the seven sacraments of the Catholic Church. Titles and the short
 * group label are standard catechetical labels (like a category name, not
 * quoted magisterial text), so they ship directly as plain Kotlin strings.
 * The sign/grace/description fields are plain-language summaries drawn from
 * the Catechism of the Catholic Church (CCC 1210–1666), mirroring the
 * Stations model in [com.ynotlabs.cathopedia.stations.Station].
 */
data class Sacrament(val number: Int, val id: String) {
    /** Field keys live under this prefix in content/strings/screens.<lang>.json. */
    val keyPrefix: String get() = "screen.sacraments.$id"
}

object SacramentsData {
    val sacraments: List<Sacrament> = listOf(
        Sacrament(1, "baptism"),
        Sacrament(2, "eucharist"),
        Sacrament(3, "confirmation"),
        Sacrament(4, "penance"),
        Sacrament(5, "matrimony"),
        Sacrament(6, "holy_orders"),
        Sacrament(7, "anointing_of_the_sick"),
    )
}

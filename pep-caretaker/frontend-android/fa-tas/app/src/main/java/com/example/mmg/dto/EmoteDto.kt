package com.example.mmg.dto

import com.example.mmg.R

data class EmoteDto(
    val id: String,
    val name : String,
    val duration: Int,
    val path: Int
)


fun getEmotes(): List<EmoteDto> {
    return listOf(
        // Hurra
        EmoteDto(id = "1", name = "hurra_5", duration = 5, path = R.raw.hurra_5),
        EmoteDto(id = "2", name = "hurra_10", duration = 10, path = R.raw.hurra_10),
        EmoteDto(id = "3", name = "hurra_15", duration = 15, path = R.raw.hurra_15),

        // Essen
        EmoteDto(id = "4", name = "essen_5", duration = 5, path = R.raw.essen_05),
        EmoteDto(id = "5", name = "essen_10", duration = 10, path = R.raw.essen_10),
        EmoteDto(id = "6", name = "essen_15", duration = 15, path = R.raw.essen_15),

        // Gehen
        EmoteDto(id = "7", name = "gehen_5", duration = 5, path = R.raw.gehen_5),
        EmoteDto(id = "8", name = "gehen_10", duration = 10, path = R.raw.gehen_10),
        EmoteDto(id = "9", name = "gehen_15", duration = 15, path = R.raw.gehen_15),

        // Hand heben
        EmoteDto(id = "10", name = "hand_heben_5", duration = 5, path = R.raw.hand_heben_5),
        EmoteDto(id = "11", name = "hand_heben_10", duration = 10, path = R.raw.hand_heben_10),
        EmoteDto(id = "12", name = "hand_heben_15", duration = 15, path = R.raw.hand_heben_15),

        // Highfive links
        EmoteDto(id = "13", name = "highfive_links_5", duration = 5, path = R.raw.highfive_links_5),
        EmoteDto(id = "14", name = "highfive_links_10", duration = 10, path = R.raw.highfive_links_10),
        EmoteDto(id = "15", name = "highfive_links_15", duration = 15, path = R.raw.highfive_links_15),

        // Highfive rechts
        EmoteDto(id = "16", name = "highfive_rechts_5", duration = 5, path = R.raw.highfive_rechts_5),
        EmoteDto(id = "17", name = "highfive_rechts_10", duration = 10, path = R.raw.highfive_rechts_10),
        EmoteDto(id = "18", name = "highfive_rechts_15", duration = 15, path = R.raw.highfive_rechts_15),

        // Klatschen
        EmoteDto(id = "19", name = "klatschen_5", duration = 5, path = R.raw.klatschen_5),
        EmoteDto(id = "20", name = "klatschen_10", duration = 10, path = R.raw.klatschen_10),
        EmoteDto(id = "21", name = "klatschen_15", duration = 15, path = R.raw.klatschen_15),

        // Strecken
        EmoteDto(id = "22", name = "strecken_5", duration = 5, path = R.raw.strecken_5),
        EmoteDto(id = "23", name = "strecken_10", duration = 10, path = R.raw.strecken_10),
        EmoteDto(id = "24", name = "strecken_15", duration = 15, path = R.raw.strecken_15),

        // Umher sehen
        EmoteDto(id = "25", name = "umher_sehen_5", duration = 5, path = R.raw.umher_sehen_5),
        EmoteDto(id = "26", name = "umher_sehen_10", duration = 10, path = R.raw.umher_sehen_10),
        EmoteDto(id = "27", name = "umher_sehen_15", duration = 15, path = R.raw.umher_sehen_15),

        // Winken
        EmoteDto(id = "28", name = "winken_5", duration = 5, path = R.raw.winken_5),
        EmoteDto(id = "29", name = "winken_10", duration = 10, path = R.raw.winken_10),
        EmoteDto(id = "30", name = "winken_15", duration = 15, path = R.raw.winken_15)
    )
}
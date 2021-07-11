package com.example.myapplication

import java.io.Serializable
import java.time.LocalDate

val COLOR_NUM_TO_COLOR_ADDRESS = mapOf<Int, Int>(
        0 to R.drawable.ic_round_0_light_green,
        1 to R.drawable.ic_round_1_red,
        2 to R.drawable.ic_round_2_orange,
        3 to R.drawable.ic_round_3_indigo,
        4 to R.drawable.ic_round_4_pink,
        5 to R.drawable.ic_round_5_gold,
        6 to R.drawable.ic_round_6_teal,
        7 to R.drawable.ic_round_7_turquoise,
        8 to R.drawable.ic_round_8_purple,
        9 to R.drawable.ic_round_9_light_blue,
        10 to R.drawable.ic_round_10_blue_gray,
        11 to R.drawable.ic_round_11_blue,
        12 to R.drawable.ic_round_12_red,
        13 to R.drawable.ic_round_13_dark_green,
        14 to R.drawable.ic_round_14_dark_purple
)


var SUBJ_GUID_TO_COLOR_DICT = mutableMapOf<String, Int>()

var SUBJ_GUID_TO_SUBJ_NAME = mutableMapOf<String, String>()

data class TableEnteryItem(val imageResource: Int, var Subject_name: String, var Task_text: String,
                           var Date_to_do: String, var Percent_of_done: String,
                           val Percent_of_done_text_color: Int,
                           val ImViewNearPercent: Int = R.drawable.ic_near_pcent_pen,
                           val GUID: String, val UpdatedTime: String,
                           val __dateForOrderBy: String?): Serializable {
}


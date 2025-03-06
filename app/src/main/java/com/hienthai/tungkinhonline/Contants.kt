package com.hienthai.tungkinhonline

import androidx.annotation.StringRes

enum class Rank(
    val point: Int,
    @StringRes
    val text: Int,
) {
    DONG(100, R.string.text_rank_bronze),
    BAC(1000, R.string.text_rank_silver),
    VANG(5000, R.string.text_rank_gold),
    BACH_KIM(10000, R.string.text_rank_platinum),
    KIM_CUONG(20000, R.string.text_rank_diamond),
    TINH_ANH(30000, R.string.text_rank_veteran),
    CAO_THU(50000, R.string.text_rank_master),
    DAI_CAO_THU(70000, R.string.text_rank_fighter),
    THACH_DAU(100000, R.string.text_rank_gage),
    CHIEN_TUONG(500000, R.string.text_rank_war_general),
    TINH_LINH_ANH_SANG(1000000, R.string.text_rank_light_of_veda);

    companion object {
        fun getRank(point: Long): Int {
            return values().findLast { point >= it.point }?.text ?: R.string.text_rank_bronze
        }
    }
}
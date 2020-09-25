package com.yz.addwatermarkdemo

/**
 *
 * @author RenBing
 * @date 2020/9/25 0025
 */

data class WatermarkData(
    val imgHeight: Int,
    val imgUrl: String,
    val imgWidth: Int,
    val logoInfo: List<LogoInfo>
)

data class LogoInfo(
    val color: String,
    val logoHeight: Int,
    val logoUrl: String,
    val logoWidth: Int,
    val opacity: Int,
    val rotate: Int,
    val startX: Int,
    val startY: Int
)
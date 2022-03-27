package com.rebecca.rs2.npc


data class Npc(
    var id: Int,
    var name: String,
    var modelIds: IntArray? = null,
    var size: Int,
    var standAnimation: Int,
    var walkAnimation: Int,
    var originalColors: IntArray? = null,
    var replacementColors: IntArray? = null,
    var headModelIds: IntArray? = null,
    var scaleXY: Int,
    var scaleZ: Int,
    var ambient: Int,
    var contrast: Int,
) {
    var primaryAnimation: Int = 0
    var scale: Float = 0.0f
    var normalised: Float = 0.0f
    var scaled: Boolean = false
}
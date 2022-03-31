package com.rebecca.rs2.npc

import com.rebecca.factory.ModelFactory
import com.rebecca.rs2.model.impl.ModelService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
data class Npc(
    var id: Int?,
    var name: String?,
    var modelIds: IntArray? = null,
    var size: Int?,
    var standAnimation: Int?,
    var walkAnimation: Int?,
    var originalColors: IntArray? = null,
    var replacementColors: IntArray? = null,
    var headModelIds: IntArray? = null,
    var scaleXY: Int?,
    var scaleZ: Int?,
    var ambient: Int?,
    var contrast: Int?,

) {
    var primaryAnimation: Int = 0
    var scale: Float = 0.0f
    var normalised: Float = 0.0f
    var scaled: Boolean = false
}

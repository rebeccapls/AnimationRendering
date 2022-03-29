package com.rebecca.rs2.npc

import com.rebecca.rs2.model.impl.ModelService
import jagex.model.Model
import org.springframework.beans.factory.annotation.Autowired

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
    @Autowired var models: ModelService? = null
) {

    var primaryAnimation: Int = 0
    var scale: Float = 0.0f
    var normalised: Float = 0.0f
    var scaled: Boolean = false

    fun getModel(primary: Int, secondary: Int, labels: IntArray?): Model {
        val model: Model
        val combined = arrayOfNulls<Model>(modelIds!!.size)
        for (i in modelIds!!.indices) {
            combined[i] = models?.getModel(modelIds!![i])
        }
        model = if (combined.size == 1) {
            combined[0]!!
        } else {
            Model(combined, combined.size)
        }
        if (originalColors != null) {
            for (index in originalColors!!.indices) model.recolor(
                originalColors!![index],
                replacementColors!![index]
            )
        }
        model.applySkins()
        model.applyLighting(64 + ambient, 850 + contrast, -30, -50, -30, true)
        if (secondary != -1 && primary != -1) {
           // model.animate(primary, secondary, labels)
        } else if (primary != -1) {
           // model.animate(primary)
        }
        if (scaleXY != 128 || scaleZ != 128) {
            model.scale(scaleXY, scaleZ, scaleXY)
        }
        model.calculateBoundaries()
        if(!scaled) {
            scaled = true
           //normalised = ModelHelper.normalise()
        }
        model.scale(normalised, normalised, normalised)
        model.calculateBoundaries()
        return model
    }
}
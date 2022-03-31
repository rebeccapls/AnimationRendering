package com.rebecca.tasks

import com.rebecca.factory.ModelFactory
import com.rebecca.util.`object`.Camera
import com.rebecca.util.animationToGif
import com.rebecca.util.clearViewportBuffer
import com.rebecca.util.prepareViewport
import com.rebecca.util.toImage
import com.rebecca.rs2.anim.impl.AnimService
import com.rebecca.rs2.model.impl.ModelService
import com.rebecca.rs2.npc.Npc
import com.rebecca.rs2.npc.impl.NpcService
import com.rebecca.util.`object`.npcData
import jagex.graphic.Draw2D
import jagex.model.Model
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.awt.image.BufferedImage

@Service
@Component
class DrawNpcTask(var width: Int = 400, var height: Int = 540, var camera: Camera = Camera(), val npcs: NpcService, val anims: AnimService, val modelFactory: ModelFactory, val models: ModelService) {

    lateinit var primaryAnimations: IntArray
    lateinit var secondaryAnimations: IntArray
    lateinit var delays: Array<String?>
    lateinit var images: Array<BufferedImage?>
    var labels: IntArray? = null;

    fun draw() {
        val time = System.nanoTime()
        println("Starting npc drawing")
        (0..npcs.getCount()).toList().parallelStream().forEachOrdered { id ->
            println("Setting up npc $id")
            val npc = npcs.getNpc(id)
            npcData.get(id)?.toList()?.forEach { anim ->
                anims.getAnimation(anim).also { curr ->
                    curr.let {
                        primaryAnimations = it.primaryAnimations!!
                        secondaryAnimations = it.secondaryAnimations!!
                        labels = it.labels
                    }
                    images = arrayOfNulls(curr.primaryAnimations!!.size)
                    delays = arrayOfNulls(curr.primaryAnimations.size)
                    val viewport = prepareViewport(width, height)

                    (images.indices).toList().forEach { index ->
                        Draw2D.setTransparentBackground(viewport.width, viewport.height)
                        npc.getModel(primaryAnimations[index], secondaryAnimations[index], labels)?.draw(camera)
                        images[index] = viewport.toImage()
                        delays[index] = curr.delays?.get(index).toString()
                        clearViewportBuffer()
                    }
                    println("Creating gif for npc $id with anim $anim")
                    animationToGif(images, delays, "$id", anim)
                }
            }
        }
        println("Took ${((System.nanoTime() - time)) / 1000000}ms to complete.")
    }

    fun Npc.getModel(primary: Int, secondary: Int, labels: IntArray?): Model? {
        var model = modelFactory.newModel()

        val combined = arrayOfNulls<Model>(modelIds!!.size)
        for (i in modelIds!!.indices) {
            combined[i] = models.getModel(modelIds!![i])
        }
        model = if (combined.size == 1) {
            combined[0]!!
        } else {
            modelFactory.newModel(combined, combined.size)
        }
        if (originalColors != null) {
            for (index in originalColors!!.indices) model.recolor(
                originalColors!![index],
                replacementColors!![index]
            )
        }
        model.applySkins()
        model.applyLighting(64 + ambient!!, 850 + contrast!!, -30, -50, -30, true)
        if (secondary != -1 && primary != -1) {
            model.animate(primary, secondary, labels)
        } else if (primary != -1) {
            model.animate(primary, -1, labels)
        }
        if (scaleXY != 128 || scaleZ != 128) {
            model.scale(scaleXY!!, scaleZ!!, scaleXY!!)
        }
        model.calculateBoundaries()
        if (!scaled) {
            scaled = true
            normalised = model.normalise()
        }
        model.scale(normalised, normalised, normalised)
        model.calculateBoundaries()
        return model
    }

}
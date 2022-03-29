package com.rebecca.helper

import com.rebecca.helper.`object`.Camera
import com.rebecca.rs2.anim.impl.AnimService
import com.rebecca.rs2.npc.impl.NpcService
import jagex.graphic.Draw2D
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.awt.image.BufferedImage

@Service
@Component
class NpcDrawingHelper(var width: Int = 400, var height: Int = 540, var camera: Camera = Camera(), val npcs: NpcService, val anims: AnimService) {

    lateinit var primaryAnimations: IntArray;
    lateinit var secondaryAnimations: IntArray;
    lateinit var delays: Array<String?>;
    lateinit var images: Array<BufferedImage?>
    var labels: IntArray? = null;

    fun draw() {
        val time = System.nanoTime()
        (0..npcs.getCount()).forEach { id ->
            val npc = npcs.getNpc(id).also { it.primaryAnimation = it.standAnimation }
            val anim = anims.getAnimation(npc.primaryAnimation).also {
                it.let {
                    primaryAnimations = it.primaryAnimations!!
                    secondaryAnimations = it.secondaryAnimations!!
                    labels = it.labels
                }
            }
            images = arrayOfNulls(anim.primaryAnimations!!.size)
            delays = arrayOfNulls(anim.primaryAnimations.size)
            val viewport = prepareViewport(width, height)

            (images.indices).forEach { index ->
                Draw2D.setTransparentBackground(viewport.width, viewport.height)
                npc.getModel(primaryAnimations[index], secondaryAnimations[index], labels).draw(camera)
                images[index] = viewport.toImage()
                delays[index] = "15"
                clearViewportBuffer()
            }
            animationToGif(images, delays, "$id")
        }
        println("Took ${((System.nanoTime() - time)) / 1000000}ms to complete.")
    }
}
package com.rebecca.helper

import com.rebecca.helper.`object`.Camera
import com.rebecca.rs2.anim.Animation
import com.rebecca.rs2.anim.AnimationFrames
import com.rebecca.rs2.anim.FrameBase
import com.rebecca.rs2.anim.impl.AnimService
import com.rebecca.rs2.model.helper.ModelHelper
import com.rebecca.rs2.model.impl.ModelService
import com.rebecca.rs2.npc.impl.NpcService
import jagex.model.Model
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import reactor.kotlin.core.publisher.toFlux
import java.awt.image.BufferedImage
import java.io.FileWriter
import java.util.*
import java.util.stream.IntStream
import kotlin.math.min
import kotlin.streams.asSequence
import kotlin.streams.asStream

@Service
class NpcDrawingHelper(var width: Int = 400, var height: Int = 540, var camera: Camera = Camera()) : ModelHelper() {

    @Autowired
    lateinit var npcs: NpcService

    @Autowired
    lateinit var models: ModelService

    @Autowired
    lateinit var anims: AnimService

    @Autowired
    lateinit var skeleton: AnimationFrames

    lateinit var primaryAnimations: IntArray;
    lateinit var secondaryAnimations: IntArray;
    var labels: IntArray? = null;
    lateinit var delays: Array<String?>;
    lateinit var images: Array<BufferedImage?>

    val frames: Sequence<Int> = sequence {
        for (i in skeleton.frames.indices) {
            yield(i)
        }
    }

    val animations: Sequence<Int> = sequence {
        for (i in 0 until anims.getCount()) { // is getCount inclusive if inclusive means u have to use until then yes
            yield(i)
        }
    }

    @EventListener(ContextRefreshedEvent::class)
    fun skeleton() {
        val time = System.nanoTime()
        println("Starting skeleton()")
        // hello
        val map = frames.associateWith { key ->
            animations.filter { anims.getAnimation(it).primaryAnimations?.contains(key) == true }   //okay
        }

        // so this is frame id : sequence<anim
        val frameIdToAnimListMap = mutableMapOf<Int, MutableList<Int>>()
        map.values.forEach { v ->
            val x = v.map { anims.getAnimation(it).primaryAnimations?.toSet() ?: emptySet() }
                .flatten() // well this was painful.
            for (frameId in x) {
                val existingAnims = frameIdToAnimListMap.getOrPut(frameId) { mutabl     eListOf() }
               2 existingAnims.addAll(v)
            }
        }

        frameIdToAnimListMap.values.forEach { v ->

        }
        // w/e who needs formatting.
        (0 until npcs.getCount()).toList().parallelStream().forEach { id ->
            val _npc /*val primaries: IntArray = anims.getAnimation(_npc.standAnimation).primaryAnimations!!
            val primaryBase: Array<FrameBase> = primaries.map { skeleton.lookup(it)?.getBase()!! }.toTypedArray()
            val linked: MutableSet<Int> = mutableSetOf()
            (0 until anims.getCount()).forEach { _anim ->
                anims.getAnimation(_anim).primaryAnimations!!.forEach {
                    val anim_base: Array<IntArray?>? = skeleton.lookup(it)?.getBase()?.groups
                    if(anim_base!!.any(primaryBase[0].groups::contains)) {
                        linked.add(_anim)
                    }
                }
            }*/
            //okay cool beans, i was just combining all the anims to iterate over again lol
            // lol alright, i think i got a good solution assuming i understand how it works in the first place. lets see..
            // alright lemme think this through now. wrote on a notepad what i needed to do here.
            /*(0 until anims.getCount()).forEach { com ->
                val combined: Array<IntArray?> = linked.mapTo {  skeleton.lookup(anims.getAnimation(it).primaryAnimations!![0])?.getBase()!!.groups }.distinct()
                linked.clear()
                println(Arrays.toString(combined))
                anims.getAnimation(com).primaryAnimations!!.forEach {
                    val anim_base: Array<IntArray?>? = skeleton.lookup(it)?.getBase()!!.groups
                    if(anim_base!!.any(combined!!::contains)) {
                        linked.add(com)
                    }
                }
            }*/
            val firstFrame = anims.getAnimation(_npc.walkAnimation).primaryAnimations?.firstOrNull()
            val linkedAnims = frameIdToAnimListMap[firstFrame] ?: emptyList()
            FileWriter("anims.txt", true).use {
                it.appendLine("[${_npc.id},${
                    Regex("[^A-Za-z0-9 ]").replace(_npc.name.lowercase(), "").replace(" ", "_")
                }]")
                it.appendLine("walkanim=${_npc.walkAnimation}")
                it.appendLine("standanim=${_npc.standAnimation}")
                it.appendLine("models=[${_npc.modelIds?.joinToString(",")}]")
                it.appendLine("anims=[${linkedAnims.distinct().sorted().joinToString(",")}]\n")
                it.close()
            }

            println(((System.nanoTime() - time)) / 1000000)
        }

/* @EventListener(ContextRefreshedEvent::class)
 fun draw() {
     print("Starting drawing.")
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
         delays = arrayOfNulls(anim.primaryAnimations!!.size)
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
 }*/

        fun com.rebecca.rs2.npc.Npc.getModel(primary: Int, secondary: Int, labels: IntArray?): Model {
            val model: Model
            val combined = arrayOfNulls<Model>(modelIds!!.size)
            for (i in modelIds!!.indices) {
                combined[i] = models.getModel(modelIds!![i])
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
                model.animate(primary, secondary, labels)
            } else if (primary != -1) {
                model.animate(primary)
            }
            if (scaleXY != 128 || scaleZ != 128) {
                model.scale(scaleXY, scaleZ, scaleXY)
            }
            /*model.calculateBoundaries()
        if(!scaled) {
            scaled = true
            normalised = normalise()
        }
        model.scale(normalised, normalised, normalised)*/
            model.calculateBoundaries()
            return model
        }

        fun Model.normalise(): Float {
            val x = 320 / (maxBoundX - minBoundX).toFloat()
            val y = 320 / (maxBoundY - minBoundY).toFloat()
            val z = 320 / (maxBoundZ - minBoundZ).toFloat()
            return min(y, min(x, z))
        }
    }
}





